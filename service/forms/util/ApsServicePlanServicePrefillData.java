package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanActionDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanCompleteDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanMonitoringDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.service.LookupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static us.tx.state.dfps.service.common.BookmarkConstants.ACTION_CATEGORY;
import static us.tx.state.dfps.service.common.BookmarkConstants.ACTION_DESC;
import static us.tx.state.dfps.service.common.BookmarkConstants.ACTION_RESULT;
import static us.tx.state.dfps.service.common.BookmarkConstants.EMPTY_STRING;
import static us.tx.state.dfps.service.common.BookmarkConstants.NO_SERVICES_MESSSAGE;
import static us.tx.state.dfps.service.common.BookmarkConstants.PROBLEM_DESC;
import static us.tx.state.dfps.service.common.BookmarkConstants.PROBLEM_OUTCOME;
import static us.tx.state.dfps.service.common.BookmarkConstants.SERVICE_GROUP;
import static us.tx.state.dfps.service.common.BookmarkConstants.SERVICE_SOURCE;
import static us.tx.state.dfps.service.common.CodesConstant.*;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_ACTION;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_CNCT_STD;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_NO_SERVICES;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_PROBLEM;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_SERVICE_GROUP;
import static us.tx.state.dfps.service.common.FormGroupsConstants.TMPLAT_SERVICE_SOURCE;

@Repository
public class ApsServicePlanServicePrefillData extends DocumentServiceUtil {

    public static final String MSG_SVC_PLAN_ICS_NULL = "No ICS Services have been added.";
    public static final String II_SECTION_LABEL = "Services Related to Immediate Interventions";
    public static final String ALLEGATION_SECTION_LABEL = "Services Related to Valid ANE";
    public static final String ICS_SECTION_LABEL = "ICS Services";
    public static final String SNA_SECTION_LABEL = "SNA Services";
    private static final String SOURCE_DESC_FMT_1 = "%s - %s";
    private static final String SOURCE_DESC_FMT_2 = "%s by %s";

    @Autowired
    LookupService lookupService;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsServicePlanCompleteDto apsServicePlanCompleteDto = (ApsServicePlanCompleteDto) parentDtoobj;

        // Initializing null DTOs
        if (ObjectUtils.isEmpty(apsServicePlanCompleteDto.getGenericCaseInfoDto())) {
            apsServicePlanCompleteDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
        }

        //Principal/Victim prefill data
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

        // CSEC02D
        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.NM_CASE,
                apsServicePlanCompleteDto.getGenericCaseInfoDto().getNmCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);
        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.ID_CASE,
                apsServicePlanCompleteDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);
        preFillData.setBookmarkDtoList(bookmarkNonGroupList);

        populatePrefillFormData(apsServicePlanCompleteDto, formDataGroupList);

        preFillData.setFormDataGroupList(formDataGroupList);
        return preFillData;
    }

    /**
     * This method populates the FormData for Case, Service Plan and Contqct Standards.
     *
     * @param apsServicePlanCompleteDto
     * @param formDataGroupList
     */
    public void populatePrefillFormData(ApsServicePlanCompleteDto apsServicePlanCompleteDto, List<FormDataGroupDto> formDataGroupList) {
        Map<String, List<ApsServicePlanSourceDto>> sourcesMap = apsServicePlanCompleteDto.getSourceDtoMap();
        Set<String> mapKeySet = sourcesMap.keySet();

        // The following large for loop goes through all the types of service groupings and servies and generates the
        // xml to pre-populate the form.
        for (String sourceType : mapKeySet) {
            List<ApsServicePlanSourceDto> serviceSourcesList = sourcesMap.get(sourceType);
            String strLabel = getServiceSourceLabel(sourceType, apsServicePlanCompleteDto.getStageDto().getCdStage());
            // The following if statment hides the ICS section if we are in an INV stage or a SVC-MNT stage b/c
            // ICS services are not applicable.
            if (CSPSRCTP_ALLEG.equals(sourceType) || CSPSRCTP_SA.equals(sourceType) ||
                    (CSPSRCTP_SNA.equals(sourceType) && !(
                            (CSTAGES_INV.equals(apsServicePlanCompleteDto.getStageDto().getCdStage()) && apsServicePlanCompleteDto.isPreSingleStage()) ||
                                    (CSTGTYPE_MNT.equals(apsServicePlanCompleteDto.getStageDto().getCdStageType()) && apsServicePlanCompleteDto.isPreSingleStage())))) {

                // Create the service grouping to add sources, problems and actions to
                FormDataGroupDto serviceGroup = createFormDataGroup(TMPLAT_SERVICE_GROUP, EMPTY_STRING);
                List<BookmarkDto> serviceGroupBMKList = new ArrayList<>();
                BookmarkDto bookmarkServiceGroupNM = createBookmark(SERVICE_GROUP, strLabel);
                serviceGroupBMKList.add(bookmarkServiceGroupNM);
                serviceGroup.setBookmarkDtoList(serviceGroupBMKList);

                if (!serviceSourcesList.isEmpty()) {
                    List<FormDataGroupDto> serviceSourceGroupList = new ArrayList<>();
                    serviceSourcesList.forEach(sourceDto -> {
                        FormDataGroupDto serviceSourceGroup = createFormDataGroup(TMPLAT_SERVICE_SOURCE, EMPTY_STRING);
                        String sourceDesc = getServiceSourceDesc(sourceDto, sourceType);
                        List<BookmarkDto> sourceDescBMKList = new ArrayList<>();
                        BookmarkDto sourceDescBMK = createBookmark(SERVICE_SOURCE, sourceDesc);
                        sourceDescBMKList.add(sourceDescBMK);
                        serviceSourceGroup.setBookmarkDtoList(sourceDescBMKList);

                        // Go through the problems of the source
                        List<ApsServicePlanProblemDto> problemDtoList = sourceDto.getServiceProblems();
                        List<FormDataGroupDto> problemGroupList = new ArrayList<>();
                        problemDtoList.forEach(problemDto -> {
                            FormDataGroupDto problemGroup = createFormDataGroup(TMPLAT_PROBLEM, EMPTY_STRING);
                            List<BookmarkDto> problemBMKList = new ArrayList<>();
                            BookmarkDto problemDescBMK = createBookmark(PROBLEM_DESC, problemDto.getProblemDescription());
                            problemBMKList.add(problemDescBMK);
                            BookmarkDto problemOutcomeBMK = createBookmarkWithCodesTable(PROBLEM_OUTCOME, problemDto.getOutcomeType(), CPRBOTCM);
                            problemBMKList.add(problemOutcomeBMK);
                            problemGroup.setBookmarkDtoList(problemBMKList);

                            // Go through the actions of each problem
                            List<ApsServicePlanActionDto> actionDtoList = problemDto.getActions();
                            List<FormDataGroupDto> actionGroupList = new ArrayList<>();
                            actionDtoList.forEach(actionDto -> actionGroupList.add(getActionGroup(actionDto)));
                            problemGroup.setFormDataGroupList(actionGroupList);
                            problemGroupList.add(problemGroup);
                        });
                        serviceSourceGroup.setFormDataGroupList(problemGroupList);
                        serviceSourceGroupList.add(serviceSourceGroup);
                    });
                    serviceGroup.setFormDataGroupList(serviceSourceGroupList);
                } else {
                    // If there are no services in a grouping provide a message
                    List<FormDataGroupDto> noServiceGroupList = getNoServiceMsg(sourceType, apsServicePlanCompleteDto.isPreSingleStage());
                    serviceGroup.setFormDataGroupList(noServiceGroupList);
                }
                formDataGroupList.add(serviceGroup);
            }
        }

        // Add the contact standards section
        apsServicePlanCompleteDto.getMonitoringPlanList().forEach(rec -> formDataGroupList.add(prepareCstdGroupFormData(rec)));
    }

    /**
     * This method return the FormDataGroupDto for the Action item of a Problem.
     *
     * @param actionDto
     * @return
     */
    private FormDataGroupDto getActionGroup(ApsServicePlanActionDto actionDto) {
        FormDataGroupDto actionGroup = createFormDataGroup(TMPLAT_ACTION, EMPTY_STRING);
        List<BookmarkDto> actionBMKList = new ArrayList<>();
        BookmarkDto actionDescBMK = createBookmark(ACTION_DESC, actionDto.getDescription());
        actionBMKList.add(actionDescBMK);
        BookmarkDto actionCategoryBMK = createBookmarkWithCodesTable(ACTION_CATEGORY, actionDto.getCategoryCode(), CACTNCAT);
        actionBMKList.add(actionCategoryBMK);
        BookmarkDto actionResultBMK = createBookmarkWithCodesTable(ACTION_RESULT, actionDto.getResultsCode(), CACTNRES);
        actionBMKList.add(actionResultBMK);
        actionGroup.setBookmarkDtoList(actionBMKList);

        return actionGroup;
    }

    /**
     * This method prepare the FormDataGroupDto element for Contact Standards Section.
     *
     * @param monitoringDto
     * @return FormDataGroupDto
     */
    private FormDataGroupDto prepareCstdGroupFormData(ApsServicePlanMonitoringDto monitoringDto) {
        FormDataGroupDto cstdGroup = createFormDataGroup(TMPLAT_CNCT_STD, EMPTY_STRING);
        List<BookmarkDto> bookmarkCstdGroupGrpList = new ArrayList<>();
        bookmarkCstdGroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.CNCT_STD_SOURCE, monitoringDto.getMonitoringPlanSourceCode(), CMONPSRC));
        bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CNCT_STD_START, FormattingHelper.formatDate(monitoringDto.getMonitoringPlanStartDate())));
        bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CNCT_STD_END, (monitoringDto.getMonitoringPlanEndDate() == null ? StringUtils.EMPTY : FormattingHelper.formatDate(monitoringDto.getMonitoringPlanEndDate()))));
        long numberOfContactsRequired = Optional.ofNullable(monitoringDto.getNumberOfContactsReqd()).orElse(0L);
        long numberOfFacetoFaceContactsRequired = Optional.ofNullable(monitoringDto.getNoOfFaceToFaceContactsRequired()).orElse(0L);
        bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CNCT_STD_NBR, String.valueOf(numberOfContactsRequired + numberOfFacetoFaceContactsRequired)));
        bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CNCT_STD_FTF, String.valueOf(numberOfFacetoFaceContactsRequired)));
        bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CNCT_STD_OTH, String.valueOf(numberOfContactsRequired)));
        cstdGroup.setBookmarkDtoList(bookmarkCstdGroupGrpList);

        return cstdGroup;
    }

    /**
     * This method return the Source Label based on the input parameters.
     *
     * @param sourceType
     * @param preSingleStage
     * @return
     */
    private String getServiceSourceLabel(String sourceType, String stageCode) {
        if (CSPSRCTP_SA.equals(sourceType)) {
            return II_SECTION_LABEL;
        } else if (CSPSRCTP_ALLEG.equals(sourceType)) {
            return ALLEGATION_SECTION_LABEL;
        } else if (CSPSRCTP_SNA.equals(sourceType)) {
            if (CSTAGES_SVC.equals(stageCode)) {
                return ICS_SECTION_LABEL;
            }
            return SNA_SECTION_LABEL;
        }
        return null;
    }

    /**
     * This method return the Source Description based on the input parameters.
     *
     * @param sourceDto
     * @param sourceType
     * @return
     */
    private String getServiceSourceDesc(ApsServicePlanSourceDto sourceDto, String sourceType) {
        if (CSPSRCTP_ALLEG.equals(sourceType)) {
            return String.format(SOURCE_DESC_FMT_2, lookupService.simpleDecodeSafe(CAPSALLG, sourceDto.getSourceCode()), sourceDto.getAllegPerpetratorName());
        } else {
            return String.format(SOURCE_DESC_FMT_1, sourceDto.getSourceCode(), sourceDto.getSourceText());
        }
    }

    /**
     * This method returns the list of FormDataGroupDto elements in case of No Service in a Group.
     *
     * @param sourceType
     * @param preSingleStage
     * @return
     */
    private List<FormDataGroupDto> getNoServiceMsg(String sourceType, boolean preSingleStage) {
        List<FormDataGroupDto> noServiceGroupList = new ArrayList<>();
        FormDataGroupDto noServiceGroup = createFormDataGroup(TMPLAT_NO_SERVICES, EMPTY_STRING);
        String message = EMPTY_STRING;
        if (CSPSRCTP_SA.equals(sourceType)) {
            message = lookupService.getMessages().get(Messages.MSG_SVC_PLAN_IMMEDINT_NULL).getTxtMessage();
        } else if (CSPSRCTP_ALLEG.equals(sourceType)) {
            message = lookupService.getMessages().get(Messages.MSG_SVC_PLAN_ALLEG_NULL).getTxtMessage();
        } else if (CSPSRCTP_SNA.equals(sourceType)) {
            if (preSingleStage) {
                message = MSG_SVC_PLAN_ICS_NULL;
            } else {
                message = lookupService.getMessages().get(Messages.MSG_SVC_PLAN_ICS_NULL).getTxtMessage();
            }
        }

        List<BookmarkDto> noServiceBMKList = new ArrayList<>();
        BookmarkDto noServiceBMK = createBookmark(NO_SERVICES_MESSSAGE, message);
        noServiceBMKList.add(noServiceBMK);
        noServiceGroup.setBookmarkDtoList(noServiceBMKList);
        noServiceGroupList.add(noServiceGroup);

        return noServiceGroupList;
    }
}