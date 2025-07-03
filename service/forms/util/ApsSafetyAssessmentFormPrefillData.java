package us.tx.state.dfps.service.forms.util;


import org.apache.commons.lang3.StringUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentCaretakerDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentServiceDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanActionDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.service.LookupService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ApsSafetyAssessmentFormPrefillData extends DocumentServiceUtil {

    Set<String> itemsWithDoubleRows = new HashSet<> (Arrays.asList("SA1AQ6", "SA1BQ2", "SA2Q2", "SA2Q4", "SA2Q6", "DF1", "DF2", "DF3", "DF4", "DF5", "DF6", "DF8", "DF9", "DF10", "DF11", "DF12")); // Items in the assessment which require two rows with a rowspan for proper formatting

    @Autowired
    LookupService lookupService;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

        ApsSafetyAssessmentServiceDto apsSafetyAssessmentServiceDto = (ApsSafetyAssessmentServiceDto) parentDtoObj;
        // Initializing null DTOs
        if (ObjectUtils.isEmpty(apsSafetyAssessmentServiceDto.getGenericCaseInfoDto())) {
            apsSafetyAssessmentServiceDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
        }
        if (ObjectUtils.isEmpty(apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto())) {
            apsSafetyAssessmentServiceDto.setApsSafetyAssessmentDto(new ApsSafetyAssessmentDto());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

        // Setup Lists that will be used throughout the form generation
        List<ApsSafetyAssessmentResponseDto> section1AList = new ArrayList<>();
        List<ApsSafetyAssessmentResponseDto> section1BList = new ArrayList<>();
        List<ApsSafetyAssessmentResponseDto> section1CList = new ArrayList<>();
        List<ApsSafetyAssessmentResponseDto> section2List = new ArrayList<>();
        List<ApsSafetyAssessmentResponseDto> section3List = new ArrayList<>();

        List<ApsSafetyAssessmentResponseDto> questions = apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto().getSafetyResponses();
        getSectionsInformation(section1AList, section1BList, section1CList, section2List, section3List, questions);

        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.NM_CASE, apsSafetyAssessmentServiceDto.getGenericCaseInfoDto().getNmCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);
        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.ID_CASE, apsSafetyAssessmentServiceDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

        if (CodesConstant.CAPSFAT_INIT.equals(apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto().getAssessmentType())) {
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.APS_SA_TITLE));
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.HTML_TITLE, BookmarkConstants.APS_SA_TITLE));
        } else {
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.SA_REASESS_TITLE));
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.HTML_TITLE, BookmarkConstants.SA_REASESS_TITLE));
        }

        createCareTaker(formDataGroupList, apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto(), bookmarkNonGroupList);

        createCaseInitiation(formDataGroupList, apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto(), section1AList, section1BList, section1CList);

        createFaceToFace(formDataGroupList, apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto(), section2List, section3List);

        createImmediateInterventions(formDataGroupList, apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto());

        createSafetyDecision(formDataGroupList, apsSafetyAssessmentServiceDto.getApsSafetyAssessmentDto(), section3List);

        preFillData.setBookmarkDtoList(bookmarkNonGroupList);
        preFillData.setFormDataGroupList(formDataGroupList);

        return preFillData;
    }

    /**
     * Method Name: getSectionsInformation Method Description: Populates sections 1 to 3 with respective data
     */
    public void getSectionsInformation(List<ApsSafetyAssessmentResponseDto> section1AList, List<ApsSafetyAssessmentResponseDto> section1BList, List<ApsSafetyAssessmentResponseDto> section1CList, List<ApsSafetyAssessmentResponseDto> section2List, List<ApsSafetyAssessmentResponseDto> section3List, List<ApsSafetyAssessmentResponseDto> questions) {
        for (ApsSafetyAssessmentResponseDto responseDB: questions) {
            if (BookmarkConstants.SA1A.equals(responseDB.getSectionCode())) {
                section1AList.add(responseDB);
            } else if (BookmarkConstants.SA1B.equals(responseDB.getSectionCode())) {
                section1BList.add(responseDB);
            } else if (BookmarkConstants.SA1C.equals(responseDB.getSectionCode())) {
                section1CList.add(responseDB);
            } else if (BookmarkConstants.SA2.equals(responseDB.getSectionCode())) {
                section2List.add(responseDB);
            } else if (BookmarkConstants.SA3.equals(responseDB.getSectionCode())) {
                section3List.add(responseDB);
            }
        }
    }

    /**
     * Method Name: createCareTaker Method Description: Populates the care taker section
     */
    public void createCareTaker(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB, List<BookmarkDto> bookmarkNonGroupList) {
        // Populate the Caretakers Section
        FormDataGroupDto ctGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CT, BookmarkConstants.EMPTY_STRING);
        List<FormDataGroupDto> formDataCtGroupList = new ArrayList<>();
        if (BookmarkConstants.Y.equals(safetyDB.getIndCaretakerNotApplicable())) {
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.NO_CARETAKER_BOX, BookmarkConstants.X));
        } else {
            bookmarkNonGroupList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CARETAKER_SPACE, BookmarkConstants.UNDERSCORE_SYMBOL));
        }
        for (ApsSafetyAssessmentCaretakerDto caretakerDB: safetyDB.getAvailableCaretakerList()) {
            FormDataGroupDto caretakerGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CARETAKER, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkCaretakerGroupList = new ArrayList<>();
            List<FormDataGroupDto> formGroupCaretakerGroupList = new ArrayList<>();

            boolean caretakerSelected = false;
            if (null != safetyDB.getPrevSelectedCaretakerList() &&  !safetyDB.getPrevSelectedCaretakerList().isEmpty()) {
                caretakerSelected = matchCareTakerInformation(safetyDB.getPrevSelectedCaretakerList(), caretakerDB);
            }
            if (caretakerSelected) {
                bookmarkCaretakerGroupList.add(createBookmark(BookmarkConstants.CARETAKER_BOX, BookmarkConstants.X));
            } else {
                bookmarkCaretakerGroupList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CARETAKER_SPACE, BookmarkConstants.UNDERSCORE_SYMBOL));
            }
            bookmarkCaretakerGroupList.add(createBookmark(BookmarkConstants.NM_PERSON_FULL, caretakerDB.getFullName()));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_TYPE, caretakerDB.getCaretakerType(), CodesConstant.CPRSNTYP));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_ROLE, caretakerDB.getCaretakerRole(), CodesConstant.CINVROLE));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_REL_INT, caretakerDB.getCaretakerRelation(), CodesConstant.CRPTRINT));

            caretakerGroup.setBookmarkDtoList(bookmarkCaretakerGroupList);
            caretakerGroup.setFormDataGroupList(formGroupCaretakerGroupList);
            formDataCtGroupList.add(caretakerGroup);
        }

        ctGroup.setFormDataGroupList(formDataCtGroupList);
        formDataGroupList.add(ctGroup);

    }

    /**
     * Method Name: matchCareTakerInformation Method Description: From a given list  is contact found return true
     */
    private boolean matchCareTakerInformation(List<ApsSafetyAssessmentCaretakerDto> prevSelectedCaretakerList, ApsSafetyAssessmentCaretakerDto caretakerDB) {
        AtomicBoolean ismatched = new AtomicBoolean(false);
        prevSelectedCaretakerList.forEach(apsSafetyAssessmentCaretakerDto -> {
            if (StringUtils.isNotEmpty(caretakerDB.getFullName()) && StringUtils.isNotEmpty(apsSafetyAssessmentCaretakerDto.getFullName())
                    && caretakerDB.getFullName().equals(apsSafetyAssessmentCaretakerDto.getFullName())) {
                ismatched.set(true);
            }
        });
        return ismatched.get();
    }


    /**
     * Method Name: createCaseInitiation Method Description:populates Case Initiation section
     */
    public void createCaseInitiation(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB, List<ApsSafetyAssessmentResponseDto> section1AList, List<ApsSafetyAssessmentResponseDto> section1BList, List<ApsSafetyAssessmentResponseDto> section1CList) {
        if (CodesConstant.CAPSFAT_INIT.equals(safetyDB.getAssessmentType())) {
            ApsSafetyAssessmentContactDto ciContact = getCIContact(safetyDB);
            if (ciContact != null) {
                FormDataGroupDto ciGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CI, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCiGroupList = new ArrayList<>();
                List<FormDataGroupDto> formDataCiGroupList = new ArrayList<>();
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.CI_CONTACTED_BY, ciContact.getContactWorkerFullName()));
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.CI_DATE, FormattingHelper.formatDate(ciContact.getDateContactOccurred())));
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.CI_TIME, ciContact.getTimeContactOccurred()));
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.ASSIGNED_PRIORITY, safetyDB.getInitialStagePriority()));
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.SA_ASSIGNED_PRIORITY, safetyDB.getInitialStagePriority()));

                buildSectionInformation(FormGroupsConstants.TMPLAT_INCREASE_PRIORITY_REASONS, section1AList, true, formDataCiGroupList);
                buildSectionInformation(FormGroupsConstants.TMPLAT_DECREASE_PRIORITY_REASONS, section1BList, true, formDataCiGroupList);
                buildSectionInformation(FormGroupsConstants.TMPLAT_NA_PRIORITY_REASONS, section1CList, false, formDataCiGroupList);

                //SHIELD SIR# 1022496 - Start changes
                //Populate the Immediate Interventions during Case Initiation Checkbox
                FormDataGroupDto interventionsInCIGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INTERVENTIONS_IN_CI, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkInterventionsInCIGrpList = new ArrayList<>();
                if (BookmarkConstants.Y.equals(safetyDB.getIndImmediateIntervention())) {
                    bookmarkInterventionsInCIGrpList.add(createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X));
                } else {
                    formDataCiGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE, BookmarkConstants.EMPTY_STRING));
                }
                interventionsInCIGrp.setBookmarkDtoList(bookmarkInterventionsInCIGrpList);
                formDataCiGroupList.add(interventionsInCIGrp);
                //SHIELD SIR# 1022496 - End

                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.FINAL_PRIORITY, safetyDB.getCurrentStagePriority()));
                bookmarkCiGroupList.add(createBookmark(BookmarkConstants.REASON_PRIORITY_CHANGE, safetyDB.getStagePriorityCmnts()));
                List<BlobDataDto> blobDataDtoList = new ArrayList<>();
                blobDataDtoList.add(createBlobData(BookmarkConstants.CI_NARRATIVE, BookmarkConstants.CONTACT_NARRATIVE, String.valueOf(ciContact.getContactEventId())));

                ciGroup.setBookmarkDtoList(bookmarkCiGroupList);
                ciGroup.setFormDataGroupList(formDataCiGroupList);
                ciGroup.setBlobDataDtoList(blobDataDtoList);
                formDataGroupList.add(ciGroup);
            }
        }
    }

    /**
     * Method Name: buildSectionInformation Method Description:each sections information  population
     */
    public void buildSectionInformation(String tmplatIncreasePriorityReasons, List<ApsSafetyAssessmentResponseDto> sectionList, boolean checkForDoubleRows, List<FormDataGroupDto> formDataCiGroupList) {

        for (ApsSafetyAssessmentResponseDto responseDB: sectionList) {
            FormDataGroupDto reasonsGrp = createFormDataGroup(tmplatIncreasePriorityReasons, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkDecreaseReasonsGroupList = new ArrayList<>();
            if (BookmarkConstants.Y.equals(responseDB.getQuestionResponseCode())) {
                bookmarkDecreaseReasonsGroupList.add(createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X));
            } else {
                formDataCiGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE, BookmarkConstants.EMPTY_STRING));
            }

            if (checkForDoubleRows && itemsWithDoubleRows.contains(responseDB.getQuestionCode())) {
                formDataCiGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN, BookmarkConstants.EMPTY_STRING));
                bookmarkDecreaseReasonsGroupList.add(createBookmark(BookmarkConstants.CHECK_TEXT, responseDB.getQuestionText()));
                formDataCiGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW, BookmarkConstants.EMPTY_STRING));
            } else {
                bookmarkDecreaseReasonsGroupList.add(createBookmark(BookmarkConstants.CHECK_TEXT, responseDB.getQuestionText()));
            }
            reasonsGrp.setBookmarkDtoList(bookmarkDecreaseReasonsGroupList);
            formDataCiGroupList.add(reasonsGrp);
        }

    }


    /**
     * Method Name: createFaceToFace Method Description:population  of face to face section
     */
    public void createFaceToFace(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB, List<ApsSafetyAssessmentResponseDto> section2List, List<ApsSafetyAssessmentResponseDto> section3List) {
        // Start population of the Face to Face Section
        for (ApsSafetyAssessmentContactDto contactDB: safetyDB.getContactList()) {
            if (ServiceConstants.F2F_CONTACT_TYPE.equals(contactDB.getContactType())) {
                FormDataGroupDto f2fContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_CONTACT, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkF2FContactGroupGrpList = new ArrayList<>();
                List<FormDataGroupDto> formDataF2FContactGroupGrpList = new ArrayList<>();
                bookmarkF2FContactGroupGrpList.add(createBookmark(BookmarkConstants.F2F_DATE, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                bookmarkF2FContactGroupGrpList.add(createBookmark(BookmarkConstants.F2F_TIME, contactDB.getTimeContactOccurred()));
                if (BookmarkConstants.Y.equals(contactDB.getIndContactAttempted())) {
                    bookmarkF2FContactGroupGrpList.add(createBookmark(BookmarkConstants.F2F_ATTEMPT_BOX, BookmarkConstants.X));
                } else {
                    formDataF2FContactGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_ATTEMPT_SPACE, BookmarkConstants.EMPTY_STRING));
                }
                f2fContactGroup.setBookmarkDtoList(bookmarkF2FContactGroupGrpList);
                f2fContactGroup.setFormDataGroupList(formDataF2FContactGroupGrpList);
                formDataGroupList.add(f2fContactGroup);
            }
        }

        for (ApsSafetyAssessmentResponseDto responseDB: section2List) {
            FormDataGroupDto section2Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_2, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkF2FSection2GrpList = new ArrayList<>();
            List<FormDataGroupDto> formDataSection2FGroupGrpList = new ArrayList<>();
            if (BookmarkConstants.Y.equals(responseDB.getQuestionResponseCode())) {
                bookmarkF2FSection2GrpList.add(createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X));
            } else {
                formDataSection2FGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE, BookmarkConstants.EMPTY_STRING));
            }

            if (itemsWithDoubleRows.contains(responseDB.getQuestionCode())) {
                formDataSection2FGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN, BookmarkConstants.EMPTY_STRING));
                bookmarkF2FSection2GrpList.add(createBookmark(BookmarkConstants.SECTION_2_FACTOR, responseDB.getQuestionText()));
                formDataSection2FGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW, BookmarkConstants.EMPTY_STRING));
            } else {
                bookmarkF2FSection2GrpList.add(createBookmark(BookmarkConstants.SECTION_2_FACTOR, responseDB.getQuestionText()));
            }
            section2Factor.setBookmarkDtoList(bookmarkF2FSection2GrpList);
            section2Factor.setFormDataGroupList(formDataSection2FGroupGrpList);
            formDataGroupList.add(section2Factor);
        }

        for (ApsSafetyAssessmentResponseDto responseDB: section3List) {
            FormDataGroupDto section3Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_3, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkF2FSection3GrpList = new ArrayList<>();
            List<FormDataGroupDto> formDataSection3FGroupGrpList = new ArrayList<>();
            if (BookmarkConstants.Y.equals(responseDB.getQuestionResponseCode())) {
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.YES_NO, BookmarkConstants.YES));
            } else if (BookmarkConstants.N.equals(responseDB.getQuestionResponseCode())) {
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.YES_NO, BookmarkConstants.NO));
            }

            if (itemsWithDoubleRows.contains(responseDB.getQuestionCode())) {
                formDataSection3FGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN, BookmarkConstants.EMPTY_STRING));
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.QUESTION_NUMBER, String.valueOf(responseDB.getQuestionOrder())));
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.SECTION_3_FACTOR, responseDB.getQuestionText()));
                formDataSection3FGroupGrpList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW, BookmarkConstants.EMPTY_STRING));
            } else {
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.QUESTION_NUMBER, String.valueOf(responseDB.getQuestionOrder())));
                bookmarkF2FSection3GrpList.add(createBookmark(BookmarkConstants.SECTION_3_FACTOR, responseDB.getQuestionText()));
            }
            section3Factor.setBookmarkDtoList(bookmarkF2FSection3GrpList);
            section3Factor.setFormDataGroupList(formDataSection3FGroupGrpList);
            formDataGroupList.add(section3Factor);
        }

        FormDataGroupDto f2fNarrative = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_NARRATIVE, BookmarkConstants.EMPTY_STRING);
        List<BlobDataDto> blobDataDtoList= new ArrayList<>();
        blobDataDtoList.add(createBlobData(BookmarkConstants.F2F_NARRATIVE, BookmarkConstants.APS_SA_NARR, String.valueOf(safetyDB.getEventId())));
        f2fNarrative.setBlobDataDtoList(blobDataDtoList);
        formDataGroupList.add(f2fNarrative);
    }


    /**
     * Method Name: createImmediateInterventions Method Description:population  of immediate interventions
     */
    public void createImmediateInterventions(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB) {
        ApsServicePlanDto servicePlanDB = safetyDB.getServicePlan();
        if ((null != servicePlanDB) && (servicePlanDB.getId() != 0)
                &&
                ((BookmarkConstants.Y.equals(safetyDB.getIndCaseInitiationComplete())) || (CodesConstant.CAPSFAT_REAS.equals(safetyDB.getAssessmentType())))) {
            List<ApsServicePlanSourceDto> sourcesDBList = servicePlanDB.getSavedServiceSources();
            sourcesDBList = sourcesDBList.stream().sorted(Comparator.comparing(ApsServicePlanSourceDto::getSaResponseId,
                    Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());

            FormDataGroupDto iiGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_II, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkIiGroupGrpList = new ArrayList<>();
            List<FormDataGroupDto> formDataIiGroupGrpList = new ArrayList<>();
            bookmarkIiGroupGrpList.add(createBookmark(BookmarkConstants.SOURCE_GROUP_NAME, "Section 4: Immediate Interventions"));

            if ((null != sourcesDBList) && (!sourcesDBList.isEmpty())) {

                for (ApsServicePlanSourceDto sourceDB: sourcesDBList) {
                    FormDataGroupDto sourceGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DANGER_FACTOR, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkSourceGroupGrpList = new ArrayList<>();
                    List<FormDataGroupDto> formDataSourceGroupGrpList = new ArrayList<>();
                    bookmarkSourceGroupGrpList.add(createBookmark(BookmarkConstants.DF_NUMBER, sourceDB.getSourceCode()));
                    bookmarkSourceGroupGrpList.add(createBookmark(BookmarkConstants.DF_TEXT, sourceDB.getSourceText()));

                    List<ApsServicePlanProblemDto> problemDBList = sourceDB.getServiceProblems();
                    if ((null != problemDBList) && (!problemDBList.isEmpty())) {
                        for (ApsServicePlanProblemDto problemDB: problemDBList) {
                            FormDataGroupDto problemGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_II_PROBLEM, BookmarkConstants.EMPTY_STRING);
                            List<BookmarkDto> bookmarkProblemGroupGrpList = new ArrayList<>();
                            List<FormDataGroupDto> formDataProblemGroupGrpList = new ArrayList<>();

                            bookmarkProblemGroupGrpList.add(createBookmark(BookmarkConstants.SA_PROBLEM_DESC, problemDB.getProblemDescription()));
                            bookmarkProblemGroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.SA_PROBLEM_OUTCOME, problemDB.getOutcomeType(), CodesConstant.CPRBOTCM));

                            List<ApsServicePlanActionDto> actionDBList = problemDB.getActions();
                            if ((null != actionDBList) && (!actionDBList.isEmpty())) {
                                for (ApsServicePlanActionDto actionDB: actionDBList) {
                                    FormDataGroupDto actionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_II_ACTION, BookmarkConstants.EMPTY_STRING);
                                    List<BookmarkDto> bookmarkActionGroupGrpList = new ArrayList<>();
                                    bookmarkActionGroupGrpList.add(createBookmark(BookmarkConstants.SA_ACTION_DESC, actionDB.getDescription()));
                                    bookmarkActionGroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.SA_ACTION_CATEGORY, actionDB.getCategoryCode(), CodesConstant.CACTNCAT));
                                    bookmarkActionGroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.SA_ACTION_RESULT, actionDB.getResultsCode(), CodesConstant.CACTNRES));

                                    actionGroup.setBookmarkDtoList(bookmarkActionGroupGrpList);
                                    formDataProblemGroupGrpList.add(actionGroup);
                                }
                            }
                            problemGroup.setBookmarkDtoList(bookmarkProblemGroupGrpList);
                            problemGroup.setFormDataGroupList(formDataProblemGroupGrpList);
                            formDataSourceGroupGrpList.add(problemGroup);
                        }
                    }
                    sourceGroup.setBookmarkDtoList(bookmarkSourceGroupGrpList);
                    sourceGroup.setFormDataGroupList(formDataSourceGroupGrpList);
                    formDataIiGroupGrpList.add(sourceGroup);
                }
                iiGroup.setBookmarkDtoList(bookmarkIiGroupGrpList);
                iiGroup.setFormDataGroupList(formDataIiGroupGrpList);

            } else {
                // If there are no services provide a message
                FormDataGroupDto noSourceGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_SERVICE_SRC, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkNoSourceGroupGrpList = new ArrayList<>();
                bookmarkNoSourceGroupGrpList.add(createBookmark(BookmarkConstants.NO_SOURCE_ADDED_MESSSAGE, lookupService.getMessages().get(Messages.MSG_SA_IMMEDINT_NULL).getTxtMessage()));
                noSourceGroup.setBookmarkDtoList(bookmarkNoSourceGroupGrpList);
                formDataIiGroupGrpList.add(noSourceGroup);
                iiGroup.setBookmarkDtoList(bookmarkIiGroupGrpList);
                iiGroup.setFormDataGroupList(formDataIiGroupGrpList);

            } //SHIELD SIR# 1022155 - End
            formDataGroupList.add(iiGroup);
        }
    }

    /**
     * Method Name: createSafetyDecision Method Description:population  of safety decision
     */
    public void createSafetyDecision(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB, List<ApsSafetyAssessmentResponseDto> section3List) {
        boolean isDFSectionNotComplete = false;
        for (ApsSafetyAssessmentResponseDto dangerFactorResponseDB: section3List) {
            if  (!BookmarkConstants.Y.equals(dangerFactorResponseDB.getQuestionResponseCode())
                    &&
                    !BookmarkConstants.N.equals(dangerFactorResponseDB.getQuestionResponseCode())) {
                isDFSectionNotComplete = true;
                break;
            }
        }
        if (!isDFSectionNotComplete) {
            FormDataGroupDto section5Group = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_5, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkSection5GroupGrpList = new ArrayList<>();
            List<FormDataGroupDto> formDataSection5GroupGrpList = new ArrayList<>();

            if (!CodesConstant.CSAFEDEC_SAFE.equals((safetyDB.getSavedSafetyDecisionCode()))) {
                FormDataGroupDto mitigateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MITIGATE_DF, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkMitigateGroupGrpList = new ArrayList<>();
                if (BookmarkConstants.Y.equals(safetyDB.getIndInterventionsInPlace())) {
                    bookmarkMitigateGroupGrpList.add(createBookmark(BookmarkConstants.YES_NO_MITIGATE_DF, BookmarkConstants.YES));
                } else if (BookmarkConstants.N.equals(safetyDB.getIndInterventionsInPlace())) {
                    bookmarkMitigateGroupGrpList.add(createBookmark(BookmarkConstants.YES_NO_MITIGATE_DF, BookmarkConstants.NO));
                }
                mitigateGroup.setBookmarkDtoList(bookmarkMitigateGroupGrpList);
                formDataSection5GroupGrpList.add(mitigateGroup);
            }

            bookmarkSection5GroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION, safetyDB.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));
            bookmarkSection5GroupGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.SA_SAFETY_DECISION, safetyDB.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));


            if (BookmarkConstants.Y.equals(safetyDB.getIndReferralRequired())) {
                bookmarkSection5GroupGrpList.add(createBookmark(BookmarkConstants.YES_NO_REFERRAL, BookmarkConstants.YES));
            } else if (BookmarkConstants.N.equals(safetyDB.getIndReferralRequired())) {
                bookmarkSection5GroupGrpList.add(createBookmark(BookmarkConstants.YES_NO_REFERRAL, BookmarkConstants.NO));
            }

            // Add the contact standards section
            if (safetyDB.getSavedSafetyDecisionCode() != null && !CodesConstant.CSAFEDEC_SAFE.equals((safetyDB.getSavedSafetyDecisionCode()))) {
                ApsServicePlanDto servicePlanDB = safetyDB.getServicePlan();
                if (servicePlanDB != null && servicePlanDB.getAssmtMonitoringPlan() != null) {
                        FormDataGroupDto csGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_STANDARDS, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> bookmarkCsGroupGrpList = new ArrayList<>();
                        List<FormDataGroupDto> formDataCsGroupGrpList = new ArrayList<>();

                        //artf148428  Add number of face to face and other contacts required
                        if (0 < servicePlanDB.getAssmtMonitoringPlan().getNoOfFaceToFaceContactsRequired()) {
                            // artf148655 after APS Enhancements Project overwrite total with sum of ftf and other numbers
                            bookmarkCsGroupGrpList.add(createBookmark(BookmarkConstants.CONTACT_STANDARD, String.valueOf(servicePlanDB.getAssmtMonitoringPlan().getNumberOfContactsReqd()
                                    + servicePlanDB.getAssmtMonitoringPlan().getNoOfFaceToFaceContactsRequired())));
                            FormDataGroupDto cstdGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CSTD, FormGroupsConstants.TMPLAT_CONTACT_STANDARDS);
                            List<BookmarkDto> bookmarkCstdGroupGrpList = new ArrayList<>();
                            bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CSTD_FTF, String.valueOf(servicePlanDB.getAssmtMonitoringPlan().getNoOfFaceToFaceContactsRequired())));
                            bookmarkCstdGroupGrpList.add(createBookmark(BookmarkConstants.CSTD_OTHER, String.valueOf(servicePlanDB.getAssmtMonitoringPlan().getNumberOfContactsReqd())));
                            cstdGroup.setBookmarkDtoList(bookmarkCstdGroupGrpList);
                            formDataCsGroupGrpList.add(cstdGroup);
                        } else {
                            bookmarkCsGroupGrpList.add(createBookmark(BookmarkConstants.CONTACT_STANDARD, String.valueOf(servicePlanDB.getAssmtMonitoringPlan().getNumberOfContactsReqd())));
                        }
                        bookmarkCsGroupGrpList.add(createBookmark(BookmarkConstants.CONTACT_DESCRIPTION, servicePlanDB.getAssmtMonitoringPlan().getPlanDescription()));

                        csGroup.setBookmarkDtoList(bookmarkCsGroupGrpList);
                        csGroup.setFormDataGroupList(formDataCsGroupGrpList);
                        formDataSection5GroupGrpList.add(csGroup);
                }
            }
            section5Group.setBookmarkDtoList(bookmarkSection5GroupGrpList);
            section5Group.setFormDataGroupList(formDataSection5GroupGrpList);
            formDataGroupList.add(section5Group);
        }
    }

    /**
     * Method Name: getCIContact Method Description:this method will return the case initiation contact
     */
    private ApsSafetyAssessmentContactDto getCIContact(ApsSafetyAssessmentDto safetyDB) {
        ApsSafetyAssessmentContactDto ciContact = null;
        if ((null != safetyDB.getContactList()) && (!safetyDB.getContactList().isEmpty())) {
            for (ApsSafetyAssessmentContactDto contactDB: safetyDB.getContactList()) {
                if (ServiceConstants.CI_CONTACT_TYPE.equals(contactDB.getContactType())) {
                    ciContact = contactDB;
                    break;
                }
            }
        }
        return ciContact;
    }

    /**
     * PD 91895: Caretaker does not display on APS Case Review Form
     * Method helps to extract the safety assessment/Reassessment care taker details and set to forms group to display
     * in Case Review Form
     *
     * @param formDataGroupList - data group for form
     * @param safetyDB - safety assessment DB data
     * @param bookmarkNonGroupList - non bookmark group list
     */
    public  void createCaretakerForCaseReviewForm(List<FormDataGroupDto> formDataGroupList, ApsSafetyAssessmentDto safetyDB, List<BookmarkDto> bookmarkNonGroupList){

        if (BookmarkConstants.Y.equals(safetyDB.getIndCaretakerNotApplicable())) {
            bookmarkNonGroupList.add(createBookmark(BookmarkConstants.NO_CARETAKER_BOX, BookmarkConstants.X));
        } else {
            bookmarkNonGroupList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CARETAKER_SPACE, BookmarkConstants.UNDERSCORE_SYMBOL));
        }

        for (ApsSafetyAssessmentCaretakerDto caretakerDB : safetyDB.getAvailableCaretakerList()) {
            FormDataGroupDto caretakerGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CARETAKER, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkCaretakerGroupList = new ArrayList<>();
            List<FormDataGroupDto> formGroupCaretakerGroupList = new ArrayList<>();
            boolean caretakerSelected = false;
            if (null != safetyDB.getPrevSelectedCaretakerList() && !safetyDB.getPrevSelectedCaretakerList().isEmpty()) {
                caretakerSelected = matchCareTakerInformation(safetyDB.getPrevSelectedCaretakerList(), caretakerDB);
            }
            if (caretakerSelected) {
                bookmarkCaretakerGroupList.add(createBookmark(BookmarkConstants.CARETAKER_BOX, BookmarkConstants.X));
            } else {
                bookmarkCaretakerGroupList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CARETAKER_SPACE, BookmarkConstants.UNDERSCORE_SYMBOL));
            }
            bookmarkCaretakerGroupList.add(createBookmark(BookmarkConstants.NM_PERSON_FULL, caretakerDB.getFullName()));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_TYPE, caretakerDB.getCaretakerType(), CodesConstant.CPRSNTYP));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_ROLE, caretakerDB.getCaretakerRole(), CodesConstant.CINVROLE));
            bookmarkCaretakerGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSON_REL_INT, caretakerDB.getCaretakerRelation(), CodesConstant.CRPTRINT));
            caretakerGroup.setBookmarkDtoList(bookmarkCaretakerGroupList);
            caretakerGroup.setFormDataGroupList(formGroupCaretakerGroupList);
            formDataGroupList.add(caretakerGroup);
        }
    }

}
