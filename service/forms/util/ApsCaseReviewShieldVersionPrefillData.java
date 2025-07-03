package us.tx.state.dfps.service.forms.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactNamesDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanCompleteDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apssna.dto.ApsSnaResponseDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps case review shield version -- apscr.
 * Jan 21, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsCaseReviewShieldVersionPrefillData extends DocumentServiceUtil {

    @Autowired
    LookupService lookupService;

    @Autowired
    ApsSafetyAssessmentFormPrefillData apsSafetyAssessmentFormPrefillData;

    @Autowired
    ApsRoraReportPrefillData apsRoraReportPrefillData;

    @Autowired
    ApsServicePlanServicePrefillData apsServicePlanServicePrefillData;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {
        ApsCaseReviewServiceDto apsCaseReviewServiceDto = (ApsCaseReviewServiceDto) parentDtoObj;
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        ApsCaseReviewDto apsCaseReviewDto = null;
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkGroupList = new ArrayList<>();
        List<BlobDataDto> blobDataList = new ArrayList<>();
        boolean preSingleStage;

        if (!ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsCaseReviewDtoList())) {
            apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDtoList().get(0);
            preSingleStage = checkPreSingleStage(apsCaseReviewDto.getDtApsInvstBegun());
        } else {
            return preFillData;
        }

        // Initializing null DTOs
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsCaseReviewDtoList())) {
            apsCaseReviewServiceDto.setApsCaseReviewDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsCaseReviewContactNamesDtoList())) {
            apsCaseReviewServiceDto.setApsCaseReviewContactNamesDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsRoraDtoList())) {
            apsCaseReviewServiceDto.setApsRoraDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsSaContactList())) {
            apsCaseReviewServiceDto.setApsSaContactList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsSnaDtoList())) {
            apsCaseReviewServiceDto.setApsSnaDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getSafetyDbList())) {
            apsCaseReviewServiceDto.setSafetyDbList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getSafetyEventList())) {
            apsCaseReviewServiceDto.setSafetyEventList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getStageValueBeanDto())) {
            apsCaseReviewServiceDto.setStageValueBeanDto(new StageValueBeanDto());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getAllegationDtoList())) {
            apsCaseReviewServiceDto.setAllegationDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getPrincipalDtoList())) {
            apsCaseReviewServiceDto.setPrincipalDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getCollateralDtoList())) {
            apsCaseReviewServiceDto.setCollateralDtoList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getApsInvstDetailList())) {
            apsCaseReviewServiceDto.setApsInvstDetailList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getSvcDeliveryClosureCheckList())) {
            apsCaseReviewServiceDto.setSvcDeliveryClosureCheckList(new ArrayList<>());
        }
        if (ObjectUtils.isEmpty(apsCaseReviewServiceDto.getSvcAuthEventInfoList())) {
            apsCaseReviewServiceDto.setSvcAuthEventInfoList(new ArrayList<>());
        }

        /****PAGE HEADER */
        populatePageHeader(apsCaseReviewDto, apsCaseReviewServiceDto.getStageValueBeanDto(), formDataGroupList, bookmarkGroupList);

        /****INTAKE STAGE SUMMARY: */
        populateIntakeStageSummary(apsCaseReviewDto, formDataGroupList, bookmarkGroupList);

        /****INVESTIGATION STAGE SUMMARY: */
        populateInvestigationStageSummary(apsCaseReviewDto, apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList);

        if (preSingleStage) { // artf204307
            populateServiceDeliveryStageSummary(apsCaseReviewServiceDto, formDataGroupList);
        }

        /****INTAKE DETAILS */
        blobDataList.add(createBlobData(BookmarkConstants.CALL_NARR_BLOB, BookmarkConstants.INCOMING_NARRATIVE_VIEW, String.valueOf(apsCaseReviewDto.getPriorStageId())));

        /****ALLEGATION DETAIL */
        populateAllegationDetails(apsCaseReviewServiceDto.getAllegationDtoList(), formDataGroupList);

        /****PRINCIPAL INFORMATION */
        populatePrincipalInformation(apsCaseReviewServiceDto, formDataGroupList);

        /****COLLATERAL INFORMATION */
        populateCollateralInformation(apsCaseReviewServiceDto, formDataGroupList);

        /****SUMMARY OF INVESTIGATION CONTACTS */
        populateSummaryInvestigationContacts(apsCaseReviewServiceDto, formDataGroupList);

        /****SAFETY ASSESSMENTS */
        populateSafetyAssessments(apsCaseReviewServiceDto, formDataGroupList);


        /****RISK OF RECIDIVISM ASSESSMENT */
        populateRoraDetails(apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList);


        /****INVESTIGATION CONCLUSION CHECKLIST */
        populateInvConclusionCheckList(apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList,preSingleStage);

        /****INVESTIGATION CONCLUSION NARRATIVE */
        populateInvestigationConclusionNarrative(apsCaseReviewServiceDto, formDataGroupList,preSingleStage);

        /****STRENGTHS AND NEEDS ASSESSMENT */
         populateSNA(apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList, preSingleStage);


        /****SERVICE PLAN SUMMARY */
        populateServicePlan(apsCaseReviewServiceDto, formDataGroupList);


        /****SUMMARY OF SERVICE DELIVERY CONTACTS */
        populateSummaryServiceDeliveryContacts(apsCaseReviewServiceDto, formDataGroupList,preSingleStage);


        /****PURCHASED CLIENT SERVICES */
        populatePCS(apsCaseReviewServiceDto, formDataGroupList);

        /****SERVICE DELIVERY CLOSURE CHECKLIST  */
        populateSvcDlvryChklist(apsCaseReviewServiceDto, formDataGroupList,preSingleStage);


        preFillData.setBookmarkDtoList(bookmarkGroupList);
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBlobDataDtoList(blobDataList);
        return preFillData;
    }


    /**
     * Method Name: populateServicePlan Method Description: Populates the SERVICE PLAN SUMMARY
     */
    private void populateServicePlan(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDtoList().get(0);
        ApsServicePlanCompleteDto apsSPCompleteDB = apsCaseReviewDto.getApsServicePlanCompleteDto();
        if (null != apsSPCompleteDB) {
            apsServicePlanServicePrefillData.populatePrefillFormData(apsSPCompleteDB,formDataGroupList);
        }
    }

    /**
     * Method Name: populatePCS Method Description: Populates the PURCHASED CLIENT SERVICES
     */
    private void populatePCS(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        List<ServiceAuthDto> svcAuthList = apsCaseReviewServiceDto.getSvcAuthEventInfoList();
        if (null != svcAuthList && !svcAuthList.isEmpty()) {
            for (ServiceAuthDto svcAuthDB : svcAuthList) {
                FormDataGroupDto svcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SERVICE, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSvcGroupList = new ArrayList<>();
                List<FormDataGroupDto> formSvcGroupList = new ArrayList<>();
                if (null != svcAuthDB.getDtApproval()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.APRV_DATE, FormattingHelper.formatDate(svcAuthDB.getDtApproval())));
                } else {
                    FormDataGroupDto space10 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_10, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space10);
                }
                if (null != svcAuthDB.getDtServiveAuthDtlBegin()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.BEG_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlBegin())));
                } else {
                    FormDataGroupDto space6 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_6, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space6);
                }
                if (null != svcAuthDB.getDtServiveAuthDtlEnd()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.END_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlEnd())));
                } else {
                    FormDataGroupDto space8 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_8, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space8);
                }
                if (null != svcAuthDB.getDtServiveAuthDtlTerm()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.TERM_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlTerm())));
                } else {
                    FormDataGroupDto space7 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_7, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space7);
                }
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.AMOUNT, svcAuthDB.getServiceAuthAmount()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.RATE, svcAuthDB.getUnitRate()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.UNITS, svcAuthDB.getUnitReq()));

                if (null != svcAuthDB.getSvcAuthDtlAuthType() && !BookmarkConstants.EMPTY_STRING.equals(svcAuthDB.getSvcAuthDtlAuthType())) {
                    bookmarkSvcGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.AUTH_TYPE, svcAuthDB.getSvcAuthDtlAuthType(), CodesConstant.CSVATYPE));
                } else {
                    FormDataGroupDto space9 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_9, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space9);
                }
                if (null != svcAuthDB.getServiceAuthService()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.SERVICE, svcAuthDB.getServiceAuthService()));
                } else {
                    FormDataGroupDto space5 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_5, BookmarkConstants.EMPTY_STRING);
                    formSvcGroupList.add(space5);
                }
                svcGroup.setBookmarkDtoList(bookmarkSvcGroupList);
                svcGroup.setFormDataGroupList(formSvcGroupList);

                formDataGroupList.add(svcGroup);
            }
        }
    }

    /**
     * Method Name: populateSvcDlvryChklist Method Description: Populates the CASE CLOSURE CHECKLIST
     */
    private void populateSvcDlvryChklist(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList,boolean preSingleStage) {
        List<StageValueBeanDto> stagBeanList = apsCaseReviewServiceDto.getSvcDeliveryClosureCheckList();
        if (null != stagBeanList && !stagBeanList.isEmpty() && preSingleStage) {
            for (StageValueBeanDto stage : stagBeanList) {
                if (null != stage.getDtStageClose()) {
                    FormDataGroupDto chkListGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CLOSURE_CHECKLIST, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkChkGroupList = new ArrayList<>();
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.STG_TYPE, stage.getCdStageType()));
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.STAGE_ECS, stage.getIndEcs()));
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.IND_ECS_VER, stage.getIndEcsVer()));
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED, stage.getCdClientAdvised()));
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED_SVC_DATE, FormattingHelper.formatDate(stage.getDtClientAdvised())));
                    bookmarkChkGroupList.add(createBookmark(BookmarkConstants.STAGE_CLOSURE_COMMENTS, stage.getStageClosureCmnts()));
                    chkListGroup.setBookmarkDtoList(bookmarkChkGroupList);
                    formDataGroupList.add(chkListGroup);
                }
            }
        }
    }

    /**
     * Method Name: populateSummaryServiceDeliveryContacts Method Description: Populates the SUMMARY OF SERVICE DELIVERY CONTACTS
     */
    private void populateSummaryServiceDeliveryContacts(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList,boolean preSingleStage) {
        // Get all the contacts/assmts created in SVC stage
        if (preSingleStage) { // artf204307
            List<ApsSafetyAssessmentContactDto>  svcContactsList = getContactsSummary(CodesConstant.CSTAGES_SVC, apsCaseReviewServiceDto.getApsSaContactList());
            FormDataGroupDto svcGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACTSLABEL, BookmarkConstants.EMPTY_STRING); // artf204307
            if (null != svcContactsList && !svcContactsList.isEmpty()) {
                formDataGroupList.add(svcGrp);
                svcContactsList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getDateContactOccurred));
                List<Long> sortedContactEvents =  new ArrayList<>();
                for (ApsSafetyAssessmentContactDto db : svcContactsList) {
                    sortedContactEvents.add(db.getContactEventId());
                }
                populateSvcContactDetails(formDataGroupList, sortedContactEvents, apsCaseReviewServiceDto);
            }
        }
    }


    /**
     * Method Name: populateSvcContactDetails Method Description: Populates the contact details SVC  stage
     */
    private void populateSvcContactDetails(List<FormDataGroupDto> formDataGroupList, List<Long> sortedContactEvents, ApsCaseReviewServiceDto apsCaseReviewServiceDto) {
        StageValueBeanDto stageDB = apsCaseReviewServiceDto.getStageValueBeanDto();
        for (Long id : sortedContactEvents) {
            FormDataGroupDto svcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT, BookmarkConstants.EMPTY_STRING);
            List<FormDataGroupDto> formSvcGroupList = new ArrayList<>();
            long apsSAEventId = getApsSaEventId(id, apsCaseReviewServiceDto.getApsSaContactList());
            ApsSafetyAssessmentDto safetyDB = null;
            ApsSafetyAssessmentContactDto saContactDb = getContactDetail(id, apsCaseReviewServiceDto.getApsSaContactList());
            List<ApsSafetyAssessmentDto> svcSafetyDbList = getSafetyAssessmentEventsByStageCase(apsCaseReviewServiceDto.getSafetyDbList(), CodesConstant.CSTAGES_SVC);
            if (null != svcSafetyDbList && !svcSafetyDbList.isEmpty()) {
                for (ApsSafetyAssessmentDto saContact : svcSafetyDbList) {
                    if (apsSAEventId == saContact.getEventId()) {
                        safetyDB = saContact;
                        break;
                    }
                }
            }
            // populate contacts created from safety Assessment
            if (apsSAEventId != 0) {
                FormDataGroupDto saTypeGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_SA_TYPE, FormGroupsConstants.TMPLAT_SVC_CONTACT);
                List<BookmarkDto> bookmarkSaGroupList = new ArrayList<>();
                List<FormDataGroupDto> formSaGroupList = new ArrayList<>();

                bookmarkSaGroupList.add(createBookmark(BookmarkConstants.TYPE, BookmarkConstants.SAFETY_REAS));

                // artf140531 add the merge stage indicator where getIdFromStage is set to SVC stage
                if (saContactDb.getIdStage() != stageDB.getIdFromStage()) {
                    FormDataGroupDto mrgSaGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_SACONTACT, FormGroupsConstants.TMPLAT_SVC_SA_TYPE);
                    List<BookmarkDto> bookmarkMrgSaGroupList = new ArrayList<>();
                    bookmarkMrgSaGroupList.add(createBookmark(BookmarkConstants.SVC_SACONTACT_MRGSTAGE, saContactDb.getIdStage()));
                    mrgSaGrp.setBookmarkDtoList(bookmarkMrgSaGroupList);
                    formSaGroupList.add(mrgSaGrp);
                }
                saTypeGrp.setBookmarkDtoList(bookmarkSaGroupList);
                saTypeGrp.setFormDataGroupList(formSaGroupList);
                formSvcGroupList.add(saTypeGrp);


                if (BookmarkConstants.Y.equals(saContactDb.getIndContactAttempted())) {
                    FormDataGroupDto atmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ATTMPT_SVC_DTL, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkAtmptGroupList = new ArrayList<>();
                    bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_DATE, FormattingHelper.formatDate(saContactDb.getDateContactOccurred())));
                    bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_TIME, saContactDb.getTimeContactOccurred()));
                    atmptGroup.setBookmarkDtoList(bookmarkAtmptGroupList);
                    formSvcGroupList.add(atmptGroup);
                } else {
                    FormDataGroupDto actualGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_SVC_DTL, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkActualGroupList = new ArrayList<>();
                    bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_DATE, FormattingHelper.formatDate(saContactDb.getDateContactOccurred())));
                    bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_TIME, saContactDb.getTimeContactOccurred()));
                    actualGroup.setBookmarkDtoList(bookmarkActualGroupList);
                    formSvcGroupList.add(actualGroup);
                }
                // artf149297 since ftf sa contacts append in a single narrative, only print narrative once(with the last contact artf150820)
                if (saContactDb.getContactEventId() == getFirstContactEventBySA(apsSAEventId, apsCaseReviewServiceDto.getApsSaContactList())) {
                    FormDataGroupDto narrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_SVC_NARR, BookmarkConstants.EMPTY_STRING);
                    List<BlobDataDto> blobNarrGroupList = new ArrayList<>();
                    blobNarrGroupList.add(createBlobData(BookmarkConstants.F2F_SVC_NARR, BookmarkConstants.APS_SA_NARR, String.valueOf(apsSAEventId)));
                    narrGroup.setBlobDataDtoList(blobNarrGroupList);
                    formSvcGroupList.add(narrGroup);
                }

                FormDataGroupDto safeDecGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_SAFETY_DECISION, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSafeDecGroupList = new ArrayList<>();
                bookmarkSafeDecGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION, safetyDB.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));
                safeDecGroup.setBookmarkDtoList(bookmarkSafeDecGroupList);
                formSvcGroupList.add(safeDecGroup);

                svcGroup.setFormDataGroupList(formSvcGroupList);
                formDataGroupList.add(svcGroup);

            } else if (apsSAEventId == 0) {  // populate contacts created from contact faceplate
                ApsSafetyAssessmentContactDto contactDB = getContactDetail(id, apsCaseReviewServiceDto.getApsSaContactList());

                FormDataGroupDto ctTypeGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_TYPE, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCtTypeGroupList = new ArrayList<>();
                List<FormDataGroupDto> formCtTypeGroupList = new ArrayList<>();
                if(CodesConstant.CCNTCTYP_CSVC.equals(contactDB.getCodeContactType())){
                    bookmarkCtTypeGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_TYPE, contactDB.getCodeContactType(), CodesConstant.CCNTCTYP));
                }else {
                    bookmarkCtTypeGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_TYPE, contactDB.getContactType(), CodesConstant.CCNTCTYP));
                }

                // artf140531 add the merge stage indicator
                if (contactDB.getIdStage() != stageDB.getIdFromStage()) {
                    FormDataGroupDto mrgSvcCtGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVCCONTACT, FormGroupsConstants.TMPLAT_SVC_CONTACT_TYPE);
                    List<BookmarkDto> bookmarkMrgSvcGroupList = new ArrayList<>();
                    bookmarkMrgSvcGroupList.add(createBookmark(BookmarkConstants.SVCCONTACT_MRGSTAGE, contactDB.getIdStage()));
                    mrgSvcCtGrp.setBookmarkDtoList(bookmarkMrgSvcGroupList);
                    formCtTypeGroupList.add(mrgSvcCtGrp);
                }
                ctTypeGrp.setBookmarkDtoList(bookmarkCtTypeGroupList);
                ctTypeGrp.setFormDataGroupList(formCtTypeGroupList);
                formSvcGroupList.add(ctTypeGrp);

                FormDataGroupDto ctDateGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_DT, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCtDateGroupList = new ArrayList<>();
                if (null != contactDB.getIndContactAttempted()) {
                    if (BookmarkConstants.Y.equals(contactDB.getIndContactAttempted())) {
                        bookmarkCtDateGroupList.add(createBookmark(BookmarkConstants.ATMPT_ACTUAL, BookmarkConstants.ATTEMPTED));
                    } else {
                        bookmarkCtDateGroupList.add(createBookmark(BookmarkConstants.ATMPT_ACTUAL, BookmarkConstants.ACTUAL));
                    }
                }
                bookmarkCtDateGroupList.add(createBookmark(BookmarkConstants.CONTACT_DATE_OCCURRED, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                bookmarkCtDateGroupList.add(createBookmark(BookmarkConstants.CONTACT_TIME_OCCURRED, contactDB.getTimeContactOccurred()));
                ctDateGrp.setBookmarkDtoList(bookmarkCtDateGroupList);
                formSvcGroupList.add(ctDateGrp);

                if (null != contactDB.getContactMethodType()) {
                    FormDataGroupDto methGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_METHOD, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkMethGroupList = new ArrayList<>();
                    bookmarkMethGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_METHOD, contactDB.getContactMethodType(), CodesConstant.CCNTMETH));
                    methGroup.setBookmarkDtoList(bookmarkMethGroupList);
                    formSvcGroupList.add(methGroup);
                }

                if (null != contactDB.getLocation()) {
                    FormDataGroupDto locGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_LOC, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkLocGroupList = new ArrayList<>();
                    bookmarkLocGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_LOCATION, contactDB.getLocation(), CodesConstant.CCNCTLOC));
                    locGroup.setBookmarkDtoList(bookmarkLocGroupList);
                    formSvcGroupList.add(locGroup);
                }

                FormDataGroupDto wrkrGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CT_WORKER, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkWrkrGroupList = new ArrayList<>();
                bookmarkWrkrGroupList.add(createBookmark(BookmarkConstants.CONTACT_WORKER, contactDB.getContactWorkerFullName()));
                wrkrGrp.setBookmarkDtoList(bookmarkWrkrGroupList);
                formSvcGroupList.add(wrkrGrp);

                List<String> personsList = getPersonsContacted(id, apsCaseReviewServiceDto.getApsCaseReviewContactNamesDtoList());
                if (null != personsList && !personsList.isEmpty()) { // artf192342
                    FormDataGroupDto nameGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_NAME, FormGroupsConstants.TMPLAT_SVC_CONTACT);
                    List<FormDataGroupDto> formNameGroupList = new ArrayList<>();
                    for (String personContacted : personsList) {
                        FormDataGroupDto personGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_PERSON_NAME, FormGroupsConstants.TMPLAT_SVC_CONTACT_NAME);
                        List<BookmarkDto> bookmarkPersonGroupList = new ArrayList<>();
                        bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_NAME_FULL, personContacted));
                        personGrp.setBookmarkDtoList(bookmarkPersonGroupList);
                        formNameGroupList.add(personGrp);
                    }
                    nameGrp.setFormDataGroupList(formNameGroupList);
                    formSvcGroupList.add(nameGrp);
                }

                FormDataGroupDto narrGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_NARR, BookmarkConstants.EMPTY_STRING);
                List<BlobDataDto> blobNarrGroupList = new ArrayList<>();
                blobNarrGroupList.add(createBlobData(BookmarkConstants.CONTACT_NARRATIVE_SVC, BookmarkConstants.CONTACT_NARRATIVE, String.valueOf(id)));
                narrGrp.setBlobDataDtoList(blobNarrGroupList);
                formSvcGroupList.add(narrGrp);

                svcGroup.setFormDataGroupList(formSvcGroupList);
                formDataGroupList.add(svcGroup);
            }

        }
    }

    /**
     * Method Name: populateServiceDeliveryStageSummary Method Description: Populates the SERVICE DELIVERY STAGE SUMMARY
     */
    private void populateServiceDeliveryStageSummary(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        FormDataGroupDto svcDlvryGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_DLVRY, BookmarkConstants.EMPTY_STRING); // artf204307 populate the preSingleStage svc summary
        List<FormDataGroupDto> formSvcDlvryGroupList = new ArrayList<>();

        // Get Service Delivery Stage Types
        List<StageValueBeanDto> stageValueBeanList = apsCaseReviewServiceDto.getSvcDeliveryClosureCheckList();
        if (null != stageValueBeanList && !stageValueBeanList.isEmpty()) {
            for (StageValueBeanDto stage : stageValueBeanList) {
                FormDataGroupDto svcTypesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_DLVRY_STG_TYPES, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarksvcTypesGroupList = new ArrayList<>();
                bookmarksvcTypesGroupList.add(createBookmark(BookmarkConstants.SVC_STAGE_TYPE, stage.getCdStageType()));
                bookmarksvcTypesGroupList.add(createBookmark(BookmarkConstants.DT_OPEN, FormattingHelper.formatDate(stage.getDtStageStart())));
                if (null != stage.getDtStageClose()) {
                    bookmarksvcTypesGroupList.add(createBookmark(BookmarkConstants.DT_CLOSE, FormattingHelper.formatDate(stage.getDtStageClose())));
                } else {
                    FormDataGroupDto space1 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_1, BookmarkConstants.EMPTY_STRING);
                    formSvcDlvryGroupList.add(space1);
                }
                if (null != stage.getCdStageReasonClosed() && !BookmarkConstants.EMPTY_STRING.equals(stage.getCdStageReasonClosed())) {
                    bookmarksvcTypesGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_CLOSURE_REASON, stage.getCdStageReasonClosed(), CodesConstant.CAPSCLSV));
                } else {
                    FormDataGroupDto space2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_2, BookmarkConstants.EMPTY_STRING);
                    formSvcDlvryGroupList.add(space2);
                }
                if (null != stage.getDtSvcDelvDecision()) {
                    bookmarksvcTypesGroupList.add(createBookmark(BookmarkConstants.STAGE_DECISION_DATE, FormattingHelper.formatDate(stage.getDtSvcDelvDecision())));
                } else {
                    FormDataGroupDto space3 = createFormDataGroup(FormGroupsConstants.TMPLAT_SPACE_3, BookmarkConstants.EMPTY_STRING);
                    formSvcDlvryGroupList.add(space3);
                }
                svcTypesGroup.setBookmarkDtoList(bookmarksvcTypesGroupList);
                formSvcDlvryGroupList.add(svcTypesGroup);
            }
        }

        // safety Assmt details
        setSvcStageSafetyAssessments(formSvcDlvryGroupList, apsCaseReviewServiceDto);

        //Get Strengths and Needs Assessment Completed Date
        List<ApsStrengthsAndNeedsAssessmentDto> sNADBList = apsCaseReviewServiceDto.getApsSnaDtoList();
        if (null != sNADBList && !sNADBList.isEmpty()) {
            for (ApsStrengthsAndNeedsAssessmentDto sNADB : sNADBList) {
                if (null != sNADB.getDateSNAComplete()) {
                    FormDataGroupDto sNADateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SNA_CMPLT_DATE, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarksNADateGroupList = new ArrayList<>();
                    if (CodesConstant.CAPSFAT_INIT.equals(sNADB.getAssessmentTypeCd())) {
                        bookmarksNADateGroupList.add(createBookmark(BookmarkConstants.SNA_COMP, BookmarkConstants.SNA_COMP_HEADER));
                    } else {
                        bookmarksNADateGroupList.add(createBookmark(BookmarkConstants.SNA_COMP, BookmarkConstants.SNA_REAS_COMP_HEADER));
                    }
                    bookmarksNADateGroupList.add(createBookmark(BookmarkConstants.SNA_CMPLT_DATE, FormattingHelper.formatDate(sNADB.getDateSNAComplete())));
                    sNADateGroup.setBookmarkDtoList(bookmarksNADateGroupList);
                    formSvcDlvryGroupList.add(sNADateGroup);
                }
            }
        }
        // Service Contact Dates
        List<ApsSafetyAssessmentContactDto> apsSaContactList = apsCaseReviewServiceDto.getApsSaContactList();
        if (null != apsSaContactList && !apsSaContactList.isEmpty()) {
            for (ApsSafetyAssessmentContactDto contactDB : apsSaContactList) {
                if (0 == contactDB.getIdApsSaEvent() && CodesConstant.CCNTCTYP_CSVC.equals(contactDB.getCodeContactType())) {
                    FormDataGroupDto svcContactsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_CONTACT_DATE, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarksvcContactsGroupList = new ArrayList<>();
                    bookmarksvcContactsGroupList.add(createBookmark(BookmarkConstants.DT_SERVICE_CONTACT_ENTERED, FormattingHelper.formatDate(contactDB.getDateContactEntered())));
                    bookmarksvcContactsGroupList.add(createBookmark(BookmarkConstants.ACTUAL_DT_SERVICE_CONTACT, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                    bookmarksvcContactsGroupList.add(createBookmark(BookmarkConstants.ACTUAL_TIME_SERVICE_CONTACT, contactDB.getTimeContactOccurred()));
                    svcContactsGroup.setBookmarkDtoList(bookmarksvcContactsGroupList);
                    formSvcDlvryGroupList.add(svcContactsGroup);
                }
            }
        }
        svcDlvryGroup.setFormDataGroupList(formSvcDlvryGroupList);
        formDataGroupList.add(svcDlvryGroup);
    }

    private void setSvcStageSafetyAssessments(List<FormDataGroupDto> formSvcDlvryGroupList, ApsCaseReviewServiceDto apsCaseReviewServiceDto) {
        List<ApsSafetyAssessmentDto> svcSafetyEventList = getSafetyAssessmentEventsByStageCase(apsCaseReviewServiceDto.getSafetyEventList(), CodesConstant.CSTAGES_SVC);
        List<ApsSafetyAssessmentDto> svcSafetyDbList = getSafetyAssessmentEventsByStageCase(apsCaseReviewServiceDto.getSafetyDbList(), CodesConstant.CSTAGES_SVC);
        if (null != svcSafetyEventList && !svcSafetyEventList.isEmpty()) {
            setSvcStageSafetyAssmts(formSvcDlvryGroupList, svcSafetyDbList);
        }
    }

    /**
     * Method Name: setSvcStageSafetyAssmts Method Description: Populates safety assessmenst  SVC  stage
     */
    private void setSvcStageSafetyAssmts(List<FormDataGroupDto> formSvcDlvryGroupList, List<ApsSafetyAssessmentDto> safetyDbList) {
        SimpleDateFormat tmFormatter = new SimpleDateFormat("hh:mm a");
        List<ApsSafetyAssessmentContactDto> contactDBList = new ArrayList<>();
        if (null != safetyDbList && !safetyDbList.isEmpty()) {
            for (ApsSafetyAssessmentDto safetyDb : safetyDbList) {
                List<ApsSafetyAssessmentContactDto> contactList = safetyDb.getContactList();
                if (null != contactList && !contactList.isEmpty()) {
                    for (ApsSafetyAssessmentContactDto contactDb : contactList) {
                        contactDBList.add(contactDb);
                    }
                }
                contactDBList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getDateContactOccurred));

                FormDataGroupDto svcDtlSAGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SVC_SAFETY_REAS, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSvcDtlGroupList = new ArrayList<>();
                List<FormDataGroupDto> formSvcDtlGroupList = new ArrayList<>();
                for (ApsSafetyAssessmentContactDto contactDB : contactDBList) {
                    if (safetyDb.getEventId() == contactDB.getIdApsSaEvent()) {
                        if (BookmarkConstants.Y.equals(contactDB.getIndContactAttempted())) {
                            FormDataGroupDto atmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_ATTMPT_F2F_SVC, BookmarkConstants.EMPTY_STRING);
                            List<BookmarkDto> bookmarkAtmptGroupList = new ArrayList<>();
                            bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_DATE, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                            bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_TIME, contactDB.getTimeContactOccurred()));
                            atmptGroup.setBookmarkDtoList(bookmarkAtmptGroupList);
                            formSvcDtlGroupList.add(atmptGroup);
                        } else {
                            FormDataGroupDto actualGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_ACTUAL_F2F_SVC, BookmarkConstants.EMPTY_STRING);
                            List<BookmarkDto> bookmarkActualGroupList = new ArrayList<>();
                            bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_DATE, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                            bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_TIME, contactDB.getTimeContactOccurred()));
                            actualGroup.setBookmarkDtoList(bookmarkActualGroupList);
                            formSvcDtlGroupList.add(actualGroup);
                        }
                    }
                }
                bookmarkSvcDtlGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION, safetyDb.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));

                if (null != safetyDb.getDateAssessmentCompleted()) {
                    FormDataGroupDto dtGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_CMPLT_SVC, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkDtGroupList = new ArrayList<>();
                    bookmarkDtGroupList.add(createBookmark(BookmarkConstants.SUM_SAFETY_ASSMT_CMPLT_DATE, FormattingHelper.formatDate(safetyDb.getDateAssessmentCompleted())));
                    bookmarkDtGroupList.add(createBookmark(BookmarkConstants.SAFETY_ASSMT_CMPLT_TIME, tmFormatter.format(safetyDb.getDateAssessmentCompleted().getTime())));
                    dtGrp.setBookmarkDtoList(bookmarkDtGroupList);
                    formSvcDtlGroupList.add(dtGrp);
                }
                svcDtlSAGroup.setBookmarkDtoList(bookmarkSvcDtlGroupList);
                svcDtlSAGroup.setFormDataGroupList(formSvcDtlGroupList);
                formSvcDlvryGroupList.add(svcDtlSAGroup);
            }
        }
    }

    /**
     * Method Name: populateSNA Method Description: Populates the STRENGTHS AND NEEDS ASSESSMENTS
     */
    private void populateSNA(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList, boolean preSingleStage) {
        List<ApsStrengthsAndNeedsAssessmentDto> apsSnaDtoList = apsCaseReviewServiceDto.getApsSnaDtoList();
        StageValueBeanDto stageDB = apsCaseReviewServiceDto.getStageValueBeanDto();
        Map<String, String> labels = preSingleStage ? getPreSingleStageMap() : getPostSingleStageMap();
        bookmarkGroupList.add(createBookmark(BookmarkConstants.SNA_SECTIONLABEL, labels.get(BookmarkConstants.SNA_SECTIONLABEL)));

        if (!apsSnaDtoList.isEmpty()) {
            for (ApsStrengthsAndNeedsAssessmentDto apsSnaDto: apsSnaDtoList) {

                FormDataGroupDto snaGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SNA, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSnaGroupList = new ArrayList<>();
                List<FormDataGroupDto> formSnaGroupList = new ArrayList<>();
                if (CodesConstant.CAPSFAT_INIT.equals(apsSnaDto.getAssessmentTypeCd())) {
                    bookmarkSnaGroupList.add(createBookmark(BookmarkConstants.SNA_HEADER, BookmarkConstants.STRENGTHS_AND_NEEDS_ASSESSMENT));
                } else {
                    bookmarkSnaGroupList.add(createBookmark(BookmarkConstants.SNA_HEADER, BookmarkConstants.STRENGTHS_AND_NEEDS_REASSESSMENT));
                }

                if(!TypeConvUtil.isNullOrEmpty(stageDB.getIdFromStage())) {
                    // artf134824 add the merge stage indicator.  artf192714 add for INV !preSingleStage.
                    if ((apsSnaDto.getStageId() != stageDB.getIdFromStage() && preSingleStage)
                            || (!preSingleStage && apsSnaDto.getStageId() != stageDB.getIdStage())) {
                        FormDataGroupDto mrgSnaGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SNA_MERGE, FormGroupsConstants.TMPLAT_SNA);
                        List<BookmarkDto> bookmarkMrgSnaGroupList = new ArrayList<>();
                        bookmarkMrgSnaGroupList.add(createBookmark(BookmarkConstants.SNA_MERGE_STAGE, apsSnaDto.getStageId()));
                        mrgSnaGrp.setBookmarkDtoList(bookmarkMrgSnaGroupList);
                        formSnaGroupList.add(mrgSnaGrp);
                    }
                }
                bookmarkSnaGroupList.add(createBookmark(BookmarkConstants.SNA_COMP, BookmarkConstants.SNA_COMPLETEDDATE_LABEL));
                bookmarkSnaGroupList.add(createBookmark(BookmarkConstants.SNA_CMPLT_DATE, FormattingHelper.formatDate(apsSnaDto.getDateSNAComplete())));

                List<ApsSnaResponseDto>  strengthsAssessedA = apsSnaDto.getStrengthsAssessedA();
                for (ApsSnaResponseDto responseDB : strengthsAssessedA) {
                    FormDataGroupDto strengthGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_AREA_STRENGTH, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkStrengthGroupList = new ArrayList<>();
                    bookmarkStrengthGroupList.add(createBookmark(BookmarkConstants.DOMAIN_CODE, responseDB.getDomainCode()));
                    if (BookmarkConstants.CL12.equals(responseDB.getDomainCode()) || BookmarkConstants.PC7.equals(responseDB.getDomainCode())) {
                        if (null != responseDB.getDescription()) {
                            bookmarkStrengthGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getDescription()));
                        }
                    } else {
                        bookmarkStrengthGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getAnswerText()));
                    }
                    strengthGroup.setBookmarkDtoList(bookmarkStrengthGroupList);
                    formSnaGroupList.add(strengthGroup);
                }

                boolean spChecked = false;
                List<ApsSnaResponseDto> moderateNeedList = apsSnaDto.getNeedsAssessedB();
                for (ApsSnaResponseDto responseDB : moderateNeedList) {
                    spChecked = responseDB.isIndIncludeServicePlan(); // artf141512
                    FormDataGroupDto moderateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MODERATE_NEED, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkModerateGroupList = new ArrayList<>();
                    bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.DOMAIN_CODE, responseDB.getDomainCode()));
                    if (BookmarkConstants.CL12.equals(responseDB.getDomainCode()) || BookmarkConstants.PC7.equals(responseDB.getDomainCode())) {
                        if (null != responseDB.getDescription()) {
                            bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getDescription()));
                        }
                    } else {
                        bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getAnswerText()));
                    }
                    // artf141512 include Service Plan Indicator
                    if (spChecked) {
                        bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.MODERATE_INCLUDE, BookmarkConstants.YES));
                    } else {
                        bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.MODERATE_INCLUDE, BookmarkConstants.NO));
                    }
                    moderateGroup.setBookmarkDtoList(bookmarkModerateGroupList);
                    formSnaGroupList.add(moderateGroup);
                }

                List<ApsSnaResponseDto> significantNeedList = apsSnaDto.getNeedsAssessedC();
                for (ApsSnaResponseDto responseDB : significantNeedList) {
                    FormDataGroupDto needGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SIGNIFICANT_NEED, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkNeedGroupList = new ArrayList<>();
                    bookmarkNeedGroupList.add(createBookmark(BookmarkConstants.DOMAIN_CODE, responseDB.getDomainCode()));
                    if (BookmarkConstants.CL12.equals(responseDB.getDomainCode()) || BookmarkConstants.PC7.equals(responseDB.getDomainCode())) {
                        if (null != responseDB.getDescription()) {
                            bookmarkNeedGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getDescription()));
                        }
                    } else {
                        bookmarkNeedGroupList.add(createBookmark(BookmarkConstants.ANSWER, responseDB.getAnswerText()));
                    }
                    needGroup.setBookmarkDtoList(bookmarkNeedGroupList);
                    formSnaGroupList.add(needGroup);
                }
                snaGroup.setBookmarkDtoList(bookmarkSnaGroupList);
                snaGroup.setFormDataGroupList(formSnaGroupList);
                formDataGroupList.add(snaGroup);
            }
        }
    }

    /**
     * Method Name: populateInvestigationConclusionNarrative Method Description: Populates the Investigation conclusion data
     */
    private void populateInvestigationConclusionNarrative(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList,boolean preSingleStage) {
        // artf192714 added boolean preSingleStage control of narrative display
        ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDtoList().get(0);
        if (preSingleStage) {
            List<Long> eventIdList = apsCaseReviewDto.getEventIdList();
            // id_events are used in the form to return multiple INV Conclusion narratives.  Multiple narratives can exist in merged  cases.
            for (Long eventId : eventIdList) {
                FormDataGroupDto invCnclsnNarrative = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONCL_NARR, BookmarkConstants.EMPTY_STRING);
                List<BlobDataDto> blobDataDtoList = new ArrayList<>();
                blobDataDtoList.add(createBlobData(BookmarkConstants.INV_CONCL_NARR, BookmarkConstants.APS_INV_NARR, String.valueOf(eventId)));
                invCnclsnNarrative.setBlobDataDtoList(blobDataDtoList);
                formDataGroupList.add(invCnclsnNarrative);
            }
        }
    }


    /**
     * get pre map.
     */
    private Map<String, String> getPreSingleStageMap() {
        Map<String, String> map = new HashMap<>();
        map.put(BookmarkConstants.H1_TITLE, "INVESTIGATION CONCLUSION CHECKLIST");
        map.put(BookmarkConstants.DATE_COMPLETED_LABEL, "Investigation Completed:");
        map.put(BookmarkConstants.CLOSURE_CHECKLIST_LABEL, "Stage Closure Checklist");
        map.put(BookmarkConstants.CLIENT_INFORMED_LABEL, "If closed in investigation stage:");
        map.put(BookmarkConstants.SERVICES_PROVIDED_LABEL, "If progressing to service delivery:");
        map.put(BookmarkConstants.EXTERNAL_DOCS_LABEL, "External documents have been added to the external documentation list.");
        map.put(BookmarkConstants.FAMILY_VIOLENCE_LABEL, "If family violence is validated:");
        map.put(BookmarkConstants.FUNDS_LABEL, "If ECS funds are used:");
        map.put(BookmarkConstants.FUNDS_EXPLORED_LABEL, "Alternate resources explored if ECS funds are to be used");
        map.put(BookmarkConstants.SNA_SECTIONLABEL, "SERVICE DELIVERY DETAILS:");  // artf204307
        return map;
    }

    /**
     * get post map.
     */
    private Map<String, String> getPostSingleStageMap() {
        Map<String, String> map = new HashMap<>();
        map.put(BookmarkConstants.H1_TITLE, "CASE CLOSURE CHECKLIST");
        map.put(BookmarkConstants.DATE_COMPLETED_LABEL, "Case Completed:"); // FR 711.01
        map.put(BookmarkConstants.CLOSURE_CHECKLIST_LABEL, "Case Closure Checklist"); // FR 711.01.01
        map.put(BookmarkConstants.CLIENT_INFORMED_LABEL, "Was alleged victim/client informed of case closure?"); // FR 711.01.02
        map.put(BookmarkConstants.SERVICES_PROVIDED_LABEL, "If services were provided:"); // FR 711.01.05
        map.put(BookmarkConstants.EXTERNAL_DOCS_LABEL, "External documents have been added to the case."); // FR 711.01.08
        map.put(BookmarkConstants.FAMILY_VIOLENCE_LABEL, "If family violence is present:"); // FR 711.01.09
        map.put(BookmarkConstants.FUNDS_LABEL, "If PCS funds are used:"); // FR 711.01.11
        map.put(BookmarkConstants.FUNDS_EXPLORED_LABEL, "Alternate resources were explored"); //FR 711.01.12
        map.put(BookmarkConstants.SNA_SECTIONLABEL, "INVESTIGATION DETAILS:");  // artf204307
        return map;
    }

    /**
     * Method Name: populateInvestigationConclusionNarrative Method Description: Populates the INVESTIGATION CONCLUSION CHECKLIST
     */
    private void populateInvConclusionCheckList(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList,boolean preSingleStage) {
        ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDtoList().get(0);
        Map<String, String> labels = preSingleStage ? getPreSingleStageMap() : getPostSingleStageMap();
        ApsInvstDetail apsInvstDetail = !apsCaseReviewServiceDto.getApsInvstDetailList().isEmpty() ? apsCaseReviewServiceDto.getApsInvstDetailList().get(0) : new ApsInvstDetail();

        String closureType = apsCaseReviewDto.getCdClosureType();
        String cdInterpreter = apsCaseReviewDto.getCdInterpreter();

        bookmarkGroupList.add(createBookmark(BookmarkConstants.H1_TITLE, labels.get(BookmarkConstants.H1_TITLE)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.CLOSURE_CHECKLIST_LABEL, labels.get(BookmarkConstants.CLOSURE_CHECKLIST_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.CLIENT_INFORMED_LABEL, labels.get(BookmarkConstants.CLIENT_INFORMED_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.SERVICES_PROVIDED_LABEL, labels.get(BookmarkConstants.SERVICES_PROVIDED_LABEL)));
        // Client advised of case closure
        if (CodesConstant.CAPSINVC_CAC.equals(closureType)) {
            FormDataGroupDto cacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CAC, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkCacGroupList = new ArrayList<>();
            bookmarkCacGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED_INV_DATE, apsCaseReviewDto.getDtDtClientAdvised()));
            cacGroup.setBookmarkDtoList(bookmarkCacGroupList);
            formDataGroupList.add(cacGroup);

        } else if (CodesConstant.CAPSINVC_CNC.equals(closureType)) {
            FormDataGroupDto cncGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNC, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cncGroup);
            if (!preSingleStage) {
                FormDataGroupDto advisedGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ADVISED, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAdvisedGroupList = new ArrayList<>();
                bookmarkAdvisedGroupList.add(createBookmark(BookmarkConstants.ADVISED_LABEL, BookmarkConstants.NARRATIVE));
                bookmarkAdvisedGroupList.add(createBookmark(BookmarkConstants.ADVISED_NOT, apsInvstDetail.getTxtNotAdviCaseClosure()));
                advisedGroup.setBookmarkDtoList(bookmarkAdvisedGroupList);
                formDataGroupList.add(advisedGroup);
            }
        }
        // Client participation in service planning
        if (CodesConstant.CAPSINVC_CSP.equals(apsInvstDetail.getCdClosureType()) // pre single stage
                || BookmarkConstants.Y.equals(apsInvstDetail.getIndSvcPlan())) { // post single stage
            FormDataGroupDto cspGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CSP, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cspGroup);

        } else if (CodesConstant.CAPSINVC_CNS.equals(apsInvstDetail.getCdClosureType()) // pre single stage
                || (apsInvstDetail.getIndSvcPlan() == null || BookmarkConstants.N.equals(apsInvstDetail.getIndSvcPlan()))) { // post single stage
            FormDataGroupDto cnsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNS, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cnsGroup);

            if (!preSingleStage) {
                FormDataGroupDto nonpartcpGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_NONPARTCP, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkNonPartCpGroupList = new ArrayList<>();
                bookmarkNonPartCpGroupList.add(createBookmark(BookmarkConstants.NONPARTCP_LABEL, BookmarkConstants.NARRATIVE));
                bookmarkNonPartCpGroupList.add(createBookmark(BookmarkConstants.NONPARTCP_PLN, apsInvstDetail.getTxtNotPrtcptSvcPln()));
                nonpartcpGroup.setBookmarkDtoList(bookmarkNonPartCpGroupList);
                formDataGroupList.add(nonpartcpGroup);
            }
        }
        // post single stage TMPLAT_SELFNEGLECT artf192843
        if (!preSingleStage) {
            FormDataGroupDto selfNeglectGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SELFNEGLECT, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkSelfNeglectGroupList = new ArrayList<>();
            bookmarkSelfNeglectGroupList.add(createBookmark(BookmarkConstants.SELFNEGLECT_LABEL, BookmarkConstants.SELFNEGLECT_LBL_TEXT));
            bookmarkSelfNeglectGroupList.add(createBookmark(BookmarkConstants.SELFNEGLECT_ALLEGFINDING_LABEL, BookmarkConstants.SELFNEGLECT_ALLEGFINDING_LABEL_TEXT));
            bookmarkSelfNeglectGroupList.add(createBookmark(BookmarkConstants.SELFNEGLECT_ALLEGFINDING_DESC, apsInvstDetail.getTxtAllegFinding()));
            bookmarkSelfNeglectGroupList.add(createBookmark(BookmarkConstants.SELFNEGLECT_ROOTCAUSE_LABEL, BookmarkConstants.SELFNEGLECT_ROOTCAUSE_LABEL_TEXT));
            bookmarkSelfNeglectGroupList.add(createBookmark(BookmarkConstants.SELFNEGLECT_ROOTCAUSE_DESC, apsInvstDetail.getTxtRootCause()));
            selfNeglectGroup.setBookmarkDtoList(bookmarkSelfNeglectGroupList);
            formDataGroupList.add(selfNeglectGroup);
        }
        // post single stage TMPLAT_CONCLJUSTFN artf192843
        if (!preSingleStage) {
            FormDataGroupDto conclJustfnGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONCLJUSTFN, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkConclJustfnGroupList = new ArrayList<>();
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_LABEL, BookmarkConstants.CONCLJUSTFN_LABEL_TEXT));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_DESCALLG_LABEL, BookmarkConstants.CONCLJUSTFN_DESCALLG_LABEL_TEXT));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_DESCALLG_DESC, apsInvstDetail.getTxtDescAllg()));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_ANLYSEVIDENCE_LABEL, BookmarkConstants.CONCLJUSTFN_ANLYSEVIDENCE_LABEL_TEXT));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_ANLYSEVIDENCE_DESC, apsInvstDetail.getTxtAnlysEvidance()));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_PREPONDRNCSTMNT_LABEL, BookmarkConstants.CONCLJUSTFN_PREPONDRNCSTMNT_LABEL_TEXT));
            bookmarkConclJustfnGroupList.add(createBookmark(BookmarkConstants.CONCLJUSTFN_PREPONDRNCSTMNT_DESC, apsInvstDetail.getTxtPrepondrncStmnt()));
            conclJustfnGroup.setBookmarkDtoList(bookmarkConclJustfnGroupList);
            formDataGroupList.add(conclJustfnGroup);
        }
        // pre/post EXTERNAL_DOCS_LABEL
        bookmarkGroupList.add(createBookmark(BookmarkConstants.EXTERNAL_DOCS_LABEL, labels.get(BookmarkConstants.EXTERNAL_DOCS_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.EXT_DOC_IND, apsCaseReviewDto.getBIndExtDoc()));

        bookmarkGroupList.add(createBookmark(BookmarkConstants.LEGAL_ACTION_IND, apsCaseReviewDto.getBIndLegalAction()));

        // pre/post FAMILY_VIOLENCE_LABEL
        bookmarkGroupList.add(createBookmark(BookmarkConstants.FAMILY_VIOLENCE_LABEL, labels.get(BookmarkConstants.FAMILY_VIOLENCE_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.FAM_VIOLENCE_IND, apsCaseReviewDto.getBIndFamViolence()));

        // pre/post FUNDS_LABEL
        bookmarkGroupList.add(createBookmark(BookmarkConstants.FUNDS_LABEL, labels.get(BookmarkConstants.FUNDS_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.FUNDS_EXPLORED_LABEL, labels.get(BookmarkConstants.FUNDS_EXPLORED_LABEL)));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ECS_IND, apsCaseReviewDto.getBIndECS()));
        if (!preSingleStage) {
            FormDataGroupDto fundsRecdGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FUNDS_RECEIVED, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkFundsRecdGroupList = new ArrayList<>();
            bookmarkFundsRecdGroupList.add(createBookmark(BookmarkConstants.PCS_IND, apsInvstDetail.getIndPcsSvcs() == null ? 'N' : apsInvstDetail.getIndPcsSvcs()));
            fundsRecdGroup.setBookmarkDtoList(bookmarkFundsRecdGroupList);
            formDataGroupList.add(fundsRecdGroup);
        }

        bookmarkGroupList.add(createBookmark(BookmarkConstants.CLIENT_IND, apsCaseReviewDto.getBIndClient()));
        if (BookmarkConstants.Y.equals(apsCaseReviewDto.getBIndClient())) {
            FormDataGroupDto clientOtherGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CLIENT_OTHER, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkClientOtherGroupList = new ArrayList<>();
            bookmarkClientOtherGroupList.add(createBookmark(BookmarkConstants.TXT_CLIENT_OTHER, apsCaseReviewDto.getSzTxtClientOther()));
            clientOtherGroup.setBookmarkDtoList(bookmarkClientOtherGroupList);
            formDataGroupList.add(clientOtherGroup);
        }

        if (null != cdInterpreter) {
            if (cdInterpreter.equals(CodesConstant.CAPSINVC_CNI)) {
                FormDataGroupDto cniGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_INTER_CNI, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCniGroupList = new ArrayList<>();
                bookmarkCniGroupList.add(createBookmark(BookmarkConstants.TXT_METHOD_COMM, apsCaseReviewDto.getSzTxtMethodComm()));
                cniGroup.setBookmarkDtoList(bookmarkCniGroupList);
                formDataGroupList.add(cniGroup);
            } else if (cdInterpreter.equals(CodesConstant.CAPSINVC_CIP)) {
                FormDataGroupDto cipGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_INTER_CIP, BookmarkConstants.EMPTY_STRING);
                formDataGroupList.add(cipGroup);
            }
        }
        bookmarkGroupList.add(createBookmark(BookmarkConstants.TXT_TRNS_NAME_RLT, apsCaseReviewDto.getSzTxtTrnsNameRlt()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.TXT_ALT_COMM, apsCaseReviewDto.getSzTxtAltComm()));
    }


    /**
     * Method Name: populateRoraDetails Method Description: Populates the RISK OF RECIDIVISM ASSESSMENT
     */
    private void populateRoraDetails(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        if (!apsCaseReviewServiceDto.getApsRoraDtoList().isEmpty()) {
            List<ApsRoraDto> apsRoraDtoList = apsCaseReviewServiceDto.getApsRoraDtoList();
            ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDtoList().get(0);
                for (ApsRoraDto roraDB : apsRoraDtoList) {
                    if (apsCaseReviewDto.getInvStageId() == roraDB.getStageId()) {
                        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORED_RISK_LEVEL_RORA, roraDB.getScoredRiskLevelCode(), CodesConstant.CANRSKVL));
                        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.FINAL_RISK_LEVEL_RORA, roraDB.getFinalRiskLevelCode(), CodesConstant.CANRSKVL));
                        bookmarkGroupList.add(createBookmark(BookmarkConstants.RSN_OVERRIDE_RORA, roraDB.getReasonForOverrideText()));
                        bookmarkGroupList.add(createBookmark(BookmarkConstants.DT_RORA_CMPLT, FormattingHelper.formatDate(roraDB.getDtRoraComplete())));
                        if (roraDB.getProblemCount() == 0L) {
                            bookmarkGroupList.add(createBookmark(BookmarkConstants.MAND_OVERRIDE_RORA, BookmarkConstants.MAND_OVERRIDE_NA));
                        } else {
                            bookmarkGroupList.add(createBookmark(BookmarkConstants.MAND_OVERRIDE_RORA, BookmarkConstants.MAND_OVERRIDE_APPLICABLE));
                        }
                        break;
                    }
                }

            for (ApsRoraDto apsRoraDto : apsRoraDtoList) {
                FormDataGroupDto roraGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RORA, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkRoraGroupList = new ArrayList<>();
                List<FormDataGroupDto> formDataRoraGroupList = new ArrayList<>();
                if (apsCaseReviewDto.getInvStageId() != apsRoraDto.getStageId()) {
                    FormDataGroupDto roraMergeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RORA_MERGE, FormGroupsConstants.TMPLAT_RORA);
                    List<BookmarkDto> bookmarkRoraMergeGroupList = new ArrayList<>();
                    bookmarkRoraMergeGroupList.add(createBookmark(BookmarkConstants.RORA_MERGE_IDSTAGE, apsRoraDto.getStageId()));
                    roraMergeGroup.setBookmarkDtoList(bookmarkRoraMergeGroupList);
                    formDataRoraGroupList.add(roraMergeGroup);
                }
                bookmarkRoraGroupList.add(createBookmark(BookmarkConstants.RORA_CMPLT_DATE, FormattingHelper.formatDate(apsRoraDto.getDtRoraComplete())));
                apsRoraReportPrefillData.preFillApsRoraData(apsRoraDto, formDataRoraGroupList);
                roraGroup.setFormDataGroupList(formDataRoraGroupList);
                roraGroup.setBookmarkDtoList(bookmarkRoraGroupList);
                formDataGroupList.add(roraGroup);
            }
        }
    }

    /**
     * Method Name: populateSafetyAssessments Method Description: Populates the SAFETY ASSESSMENTS
     */
    private void populateSafetyAssessments(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
       if (!apsCaseReviewServiceDto.getSafetyDbList().isEmpty()) {
           StageValueBeanDto stageDB = apsCaseReviewServiceDto.getStageValueBeanDto();
           for (ApsSafetyAssessmentDto apsSafetyAssessmentDto : apsCaseReviewServiceDto.getSafetyDbList()) {
               FormDataGroupDto safetyAssessmentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_ASSMT, BookmarkConstants.EMPTY_STRING);
               List<FormDataGroupDto> formDataSAGroupList = new ArrayList<>();
               List<BookmarkDto> bookmarkDataSAGroupList = new ArrayList<>();
               List<ApsSafetyAssessmentResponseDto> section1AList = new ArrayList<>();
               List<ApsSafetyAssessmentResponseDto> section1BList = new ArrayList<>();
               List<ApsSafetyAssessmentResponseDto> section1CList = new ArrayList<>();
               List<ApsSafetyAssessmentResponseDto> section2List = new ArrayList<>();
               List<ApsSafetyAssessmentResponseDto> section3List = new ArrayList<>();

               List<ApsSafetyAssessmentResponseDto> questions = apsSafetyAssessmentDto.getSafetyResponses();
               apsSafetyAssessmentFormPrefillData.getSectionsInformation(section1AList, section1BList, section1CList, section2List, section3List, questions);

               if (CodesConstant.CAPSFAT_INIT.equals(apsSafetyAssessmentDto.getAssessmentType())) {
                   bookmarkDataSAGroupList.add(createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.APS_SA_TITLE));
               } else {
                   bookmarkDataSAGroupList.add(createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.SA_REASESS_TITLE));
               }
               long idStage = Optional.ofNullable(stageDB.getIdStage()).orElse(0L);
               long idFromStage = Optional.ofNullable(stageDB.getIdFromStage()).orElse(0L);
               if (!(apsSafetyAssessmentDto.getStageId() == idStage || apsSafetyAssessmentDto.getStageId() == idFromStage)) {
                   FormDataGroupDto mrgStageGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_ASSMT_MRG, FormGroupsConstants.TMPLAT_SAFETY_ASSMT);
                   List<BookmarkDto> bookmarkDataMrgStageGroupList = new ArrayList<>();
                   bookmarkDataMrgStageGroupList.add(createBookmark(BookmarkConstants.SAFETY_ASSMT_MRG, new StringBuilder().append(BookmarkConstants.MERGED).append(apsSafetyAssessmentDto.getStageCode()).append(FormGroupsConstants.STAGE_ID).append(apsSafetyAssessmentDto.getStageId())));
                   mrgStageGroup.setBookmarkDtoList(bookmarkDataMrgStageGroupList);
                   formDataSAGroupList.add(mrgStageGroup);
               }
               bookmarkDataSAGroupList.add(createBookmark(BookmarkConstants.SA_CMPLT_DATE, FormattingHelper.formatDate(apsSafetyAssessmentDto.getDateAssessmentCompleted())));

              //PD 91895: Caretaker does not display on APS Case Review Form
               apsSafetyAssessmentFormPrefillData.createCaretakerForCaseReviewForm(formDataSAGroupList, apsSafetyAssessmentDto, bookmarkDataSAGroupList);
               apsSafetyAssessmentFormPrefillData.createCaseInitiation(formDataSAGroupList, apsSafetyAssessmentDto, section1AList, section1BList, section1CList);
               apsSafetyAssessmentFormPrefillData.createFaceToFace(formDataSAGroupList, apsSafetyAssessmentDto, section2List, section3List);
               apsSafetyAssessmentFormPrefillData.createImmediateInterventions(formDataSAGroupList, apsSafetyAssessmentDto);
               apsSafetyAssessmentFormPrefillData.createSafetyDecision(formDataSAGroupList, apsSafetyAssessmentDto, section3List);
               safetyAssessmentGroup.setBookmarkDtoList(bookmarkDataSAGroupList);
               safetyAssessmentGroup.setFormDataGroupList(formDataSAGroupList);
               formDataGroupList.add(safetyAssessmentGroup);
           }
       }
    }


    /**
     * Method Name: populateSummaryInvestigationContacts Method Description: Populates the SUMMARY OF INVESTIGATION CONTACTS
     */
    private void populateSummaryInvestigationContacts(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        // Get all the contacts created in INV stage
        List<ApsSafetyAssessmentContactDto> contactDBList = getContactsSummary(CodesConstant.CSTAGES_INV, apsCaseReviewServiceDto.getApsSaContactList());
        // Add the merged intakes to contacts with dtIncomingCall as dtContactOccured
        List<ApsSafetyAssessmentContactDto> mergedIntakes = getMergedIntakesAsContacts(apsCaseReviewServiceDto);
        contactDBList.addAll(mergedIntakes);
        contactDBList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getDateContactOccurred));

        List<Long> sortedContactEvents =  new ArrayList<>();
        for (ApsSafetyAssessmentContactDto db : contactDBList) {
            sortedContactEvents.add(db.getContactEventId());
        }

        populateInvContactDetails(apsCaseReviewServiceDto, formDataGroupList, sortedContactEvents, mergedIntakes);
    }


    /**
     * Method Name: populateInvContactDetails Method Description: Populates the contact details INV stage
     */
    private void populateInvContactDetails(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<Long> sortedContactEvents, List<ApsSafetyAssessmentContactDto> mergedIntakes) {
            for (Long id : sortedContactEvents) {
               FormDataGroupDto invGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT, BookmarkConstants.EMPTY_STRING);
               List<FormDataGroupDto> formDataInvGroupList = new ArrayList<>();
               ApsSafetyAssessmentDto safetyDB = null;
               ApsSafetyAssessmentContactDto saContactDb = null;
               long apsSAEventId = getApsSaEventId(id, apsCaseReviewServiceDto.getApsSaContactList());
               List<ApsSafetyAssessmentDto> invSafetyDbList = getSafetyAssessmentEventsByStageCase(apsCaseReviewServiceDto.getSafetyDbList(), CodesConstant.CSTAGES_INV);
               StageValueBeanDto stageDB = apsCaseReviewServiceDto.getStageValueBeanDto();

               if (apsSAEventId != 0L) {
                    saContactDb = getContactDetail(id, apsCaseReviewServiceDto.getApsSaContactList());
                    if (null != invSafetyDbList && !invSafetyDbList.isEmpty()) {
                        for (ApsSafetyAssessmentDto saContact : invSafetyDbList) {
                            if (apsSAEventId == saContact.getEventId()) {
                                safetyDB = saContact;
                                break;
                            }
                        }
                    }
                }
                // artf135380 populate the merged intake bookmarks
                long mergeIntakeId = 0L;
                ApsSafetyAssessmentContactDto mergeIntake = null;
                if (null != mergedIntakes && !mergedIntakes.isEmpty()) {
                    for (ApsSafetyAssessmentContactDto mergedIntake : mergedIntakes) {
                        if (id == mergedIntake.getContactEventId()) {
                            mergeIntakeId = id;
                            mergeIntake = mergedIntake;
                            break;
                        }
                    }
                }
                if (apsSAEventId != 0) { // populate contacts created from safety Assessment
                    FormDataGroupDto saTypeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_TYPE, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkSaGroupList = new ArrayList<>();
                    List<FormDataGroupDto> formDataSaGroupList = new ArrayList<>();
                    if (safetyDB.getAssessmentType().equals(CodesConstant.CAPSFAT_INIT)) {
                        bookmarkSaGroupList.add(createBookmark(BookmarkConstants.SACONTACT_TYPE, BookmarkConstants.SAFETY_ASSMT));
                        bookmarkSaGroupList.add(createBookmark(BookmarkConstants.TYPE, BookmarkConstants.SAFETY_ASSMT));
                    } else {
                        bookmarkSaGroupList.add(createBookmark(BookmarkConstants.SACONTACT_TYPE, BookmarkConstants.SAFETY_REAS));
                        bookmarkSaGroupList.add(createBookmark(BookmarkConstants.TYPE, BookmarkConstants.SAFETY_ASSMT));
                    }
                    if (saContactDb.getIdStage() != stageDB.getIdStage()) {
                        FormDataGroupDto mrgSaGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SACONTACT, FormGroupsConstants.TMPLAT_SA_TYPE);
                        List<BookmarkDto> bookmarkMrgSaGroupList = new ArrayList<>();
                        bookmarkMrgSaGroupList.add(createBookmark(BookmarkConstants.SACONTACT_STAGE, saContactDb.getIdStage()));
                        mrgSaGrp.setBookmarkDtoList(bookmarkMrgSaGroupList);
                        formDataSaGroupList.add(mrgSaGrp);
                    }
                    saTypeGroup.setBookmarkDtoList(bookmarkSaGroupList);
                    saTypeGroup.setFormDataGroupList(formDataSaGroupList);
                    formDataInvGroupList.add(saTypeGroup);

                    FormDataGroupDto safDecGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_DECISION, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkSafDecGroupList = new ArrayList<>();
                    bookmarkSafDecGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION, safetyDB.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));
                    safDecGroup.setBookmarkDtoList(bookmarkSafDecGroupList);
                    formDataInvGroupList.add(safDecGroup);

                    if (CodesConstant.CASE_INITIATION_CONTACT.equals(saContactDb.getCodeContactType()) || CodesConstant.CASE_INITIATION_CONTACT.equals(saContactDb.getContactType())) {
                        FormDataGroupDto ciGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CI_INV_DTL, FormGroupsConstants.TMPLAT_INV_CONTACT);
                        List<BookmarkDto> bookmarkCiGroupList = new ArrayList<>();
                        bookmarkCiGroupList.add(createBookmark(BookmarkConstants.SUM_DATE_CI, FormattingHelper.formatDate(saContactDb.getDateContactOccurred())));
                        bookmarkCiGroupList.add(createBookmark(BookmarkConstants.SUM_TIME_CI, saContactDb.getTimeContactOccurred()));
                        ciGroup.setBookmarkDtoList(bookmarkCiGroupList);
                        formDataInvGroupList.add(ciGroup);
                    } else {
                        if (BookmarkConstants.Y.equals(saContactDb.getIndContactAttempted())) {
                            FormDataGroupDto atmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ATTMPT_INV_DTL, FormGroupsConstants.TMPLAT_INV_CONTACT);
                            List<BookmarkDto> bookmarkAtmptGroupList = new ArrayList<>();
                            bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_DATE, FormattingHelper.formatDate(saContactDb.getDateContactOccurred())));
                            bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_TIME, saContactDb.getTimeContactOccurred()));
                            atmptGroup.setBookmarkDtoList(bookmarkAtmptGroupList);
                            formDataInvGroupList.add(atmptGroup);
                        } else {
                            FormDataGroupDto actualGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_INV_DTL, FormGroupsConstants.TMPLAT_INV_CONTACT);
                            List<BookmarkDto> bookmarkActualGroupList = new ArrayList<>();
                            bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_DATE, FormattingHelper.formatDate(saContactDb.getDateContactOccurred())));
                            bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_TIME, saContactDb.getTimeContactOccurred()));
                            actualGroup.setBookmarkDtoList(bookmarkActualGroupList);
                            formDataInvGroupList.add(actualGroup);
                        }
                    }
                    boolean ciFound = false; // artf150820
                    if (CodesConstant.CASE_INITIATION_CONTACT.equals(saContactDb.getCodeContactType()) || CodesConstant.CASE_INITIATION_CONTACT.equals(saContactDb.getContactType())) {
                        FormDataGroupDto ciNarr = createFormDataGroup(FormGroupsConstants.TMPLAT_CI_NARR, BookmarkConstants.EMPTY_STRING);
                        List<BlobDataDto> blobDataCiNarrGroupList = new ArrayList<>();
                        blobDataCiNarrGroupList.add(createBlobData(BookmarkConstants.CI_NARR, BookmarkConstants.CONTACT_NARRATIVE, String.valueOf(saContactDb.getContactEventId())));
                        ciNarr.setBlobDataDtoList(blobDataCiNarrGroupList);
                        formDataInvGroupList.add(ciNarr);
                        ciFound = true;
                    }
                    // artf149261 since ftf sa contacts append in a single APS_SA_NARR narrative, only print narrative once(with the last contact artf150820)
                    if (!ciFound && saContactDb.getContactEventId() == getFirstContactEventBySA(apsSAEventId, apsCaseReviewServiceDto.getApsSaContactList())) {
                        FormDataGroupDto actualNarr = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_INV_NARR, FormGroupsConstants.TMPLAT_INV_CONTACT);
                        List<BlobDataDto> blobActualNarrGroupList = new ArrayList<>();
                        blobActualNarrGroupList.add(createBlobData(BookmarkConstants.F2F_INV_NARR, BookmarkConstants.APS_SA_NARR, String.valueOf(apsSAEventId)));
                        actualNarr.setBlobDataDtoList(blobActualNarrGroupList);
                        formDataInvGroupList.add(actualNarr);
                    }
                } else if (apsSAEventId == 0 && 0 < mergeIntakeId) { // artf135380 populate intakes from merged investigations as contacts
                    FormDataGroupDto miTypeGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGEDINTAKE, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkMiTypeGroupList = new ArrayList<>();
                    List<BlobDataDto> blobDataMiTypeGroupList = new ArrayList<>();
                    bookmarkMiTypeGroupList.add(createBookmark(BookmarkConstants.MERGEDINTAKE_TYPE, mergeIntake.getContactType()));
                    bookmarkMiTypeGroupList.add(createBookmark(BookmarkConstants.MERGEDINTAKE_STAGE, mergeIntakeId));
                    bookmarkMiTypeGroupList.add(createBookmark(BookmarkConstants.MERGEDINTAKE_DTOCCURRED, FormattingHelper.formatDate(mergeIntake.getDateContactOccurred())));
                    bookmarkMiTypeGroupList.add(createBookmark(BookmarkConstants.MERGEDINTAKE_TMOCCURRED, mergeIntake.getTimeContactOccurred()));
                    blobDataMiTypeGroupList.add(createBlobData(BookmarkConstants.MERGEDINTAKE_NARR, BookmarkConstants.INCOMING_NARRATIVE_VIEW, String.valueOf(mergeIntakeId)));
                    miTypeGrp.setBookmarkDtoList(bookmarkMiTypeGroupList);
                    miTypeGrp.setBlobDataDtoList(blobDataMiTypeGroupList);
                    formDataInvGroupList.add(miTypeGrp);
                } else {  // populate contacts created from contact faceplate
                    ApsSafetyAssessmentContactDto contactDB = getContactDetail(id, apsCaseReviewServiceDto.getApsSaContactList());

                    FormDataGroupDto ctTypeGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_TYPE, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkCtTypeGroupList = new ArrayList<>();
                    List<FormDataGroupDto> formDataCtTypeGroupList = new ArrayList<>();
                    bookmarkCtTypeGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.INVCONTACT_TYPE, contactDB.getCodeContactType(), CodesConstant.CCNTCTYP));
                    if (contactDB.getIdStage() != stageDB.getIdStage()) {
                        FormDataGroupDto mrgCtGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INVCONTACT, FormGroupsConstants.TMPLAT_INV_CONTACT_TYPE);
                        List<BookmarkDto> bookmarkMrgCtGroupList = new ArrayList<>();
                        bookmarkMrgCtGroupList.add(createBookmark(BookmarkConstants.INVCONTACT_STAGE, contactDB.getIdStage()));
                        mrgCtGrp.setBookmarkDtoList(bookmarkMrgCtGroupList);
                        formDataCtTypeGroupList.add(mrgCtGrp);
                    }
                    ctTypeGrp.setBookmarkDtoList(bookmarkCtTypeGroupList);
                    ctTypeGrp.setFormDataGroupList(formDataCtTypeGroupList);
                    formDataInvGroupList.add(ctTypeGrp);

                    FormDataGroupDto ctDate = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_DATE, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkCtGroupList = new ArrayList<>();
                    if (null != contactDB.getIndContactAttempted()) {
                        if (BookmarkConstants.Y.equals(contactDB.getIndContactAttempted())) {
                            bookmarkCtGroupList.add(createBookmark(BookmarkConstants.ATMPT_ACTUAL, BookmarkConstants.ATTEMPTED));
                        } else {
                            bookmarkCtGroupList.add(createBookmark(BookmarkConstants.ATMPT_ACTUAL, BookmarkConstants.ACTUAL));
                        }
                    }
                    bookmarkCtGroupList.add(createBookmark(BookmarkConstants.CONTACT_DATE_OCCURRED, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                    bookmarkCtGroupList.add(createBookmark(BookmarkConstants.CONTACT_TIME_OCCURRED, contactDB.getTimeContactOccurred()));
                    ctDate.setBookmarkDtoList(bookmarkCtGroupList);
                    formDataInvGroupList.add(ctDate);

                    // SHIELD SIR# 1022562 - Add Method of contact to APS Case Review page
                    if (null != contactDB.getContactMethodType()) {
                        FormDataGroupDto methGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_METHOD, FormGroupsConstants.TMPLAT_INV_CONTACT);
                        List<BookmarkDto> bookmarkMethGroupList = new ArrayList<>();
                        bookmarkMethGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_METHOD, contactDB.getContactMethodType(), CodesConstant.CCNTMETH));
                        methGroup.setBookmarkDtoList(bookmarkMethGroupList);
                        formDataInvGroupList.add(methGroup);
                    }
                    // End SIR# 1022562
                    if (null != contactDB.getLocation()) {
                        FormDataGroupDto locGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_LOC, FormGroupsConstants.TMPLAT_INV_CONTACT);
                        List<BookmarkDto> bookmarkLocGroupList = new ArrayList<>();
                        bookmarkLocGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_LOCATION, contactDB.getLocation(), CodesConstant.CCNCTLOC));
                        locGroup.setBookmarkDtoList(bookmarkLocGroupList);
                        formDataInvGroupList.add(locGroup);
                    }

                    FormDataGroupDto workerGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CT_WORKER, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BookmarkDto> bookmarkWorkerGroupList = new ArrayList<>();
                    bookmarkWorkerGroupList.add(createBookmark(BookmarkConstants.CONTACT_WORKER, contactDB.getContactWorkerFullName()));
                    workerGrp.setBookmarkDtoList(bookmarkWorkerGroupList);
                    formDataInvGroupList.add(workerGrp);

                    List<String> personsList = getPersonsContacted(id, apsCaseReviewServiceDto.getApsCaseReviewContactNamesDtoList());
                    if (null != personsList && !personsList.isEmpty()) { // artf192342
                        FormDataGroupDto nameGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_NAME, FormGroupsConstants.TMPLAT_INV_CONTACT);
                        List<FormDataGroupDto> formDataNameGroupList = new ArrayList<>();
                        for (String personContacted : personsList) {
                            FormDataGroupDto personGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_PERSON_NAME, FormGroupsConstants.TMPLAT_INV_CONTACT_NAME);
                            List<BookmarkDto> bookmarkPersonGroupList = new ArrayList<>();
                            bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_INV_NAME_FULL, personContacted));
                            personGrp.setBookmarkDtoList(bookmarkPersonGroupList);
                            formDataNameGroupList.add(personGrp);
                        }
                        nameGrp.setFormDataGroupList(formDataNameGroupList);
                        formDataInvGroupList.add(nameGrp);
                    }
                    FormDataGroupDto narrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONTACT_NARR, FormGroupsConstants.TMPLAT_INV_CONTACT);
                    List<BlobDataDto> blobDataNarrGroupList = new ArrayList<>();
                    blobDataNarrGroupList.add(createBlobData(BookmarkConstants.CONTACT_NARRATIVE_INV, BookmarkConstants.CONTACT_NARRATIVE, String.valueOf(id)));
                    narrGroup.setBlobDataDtoList(blobDataNarrGroupList);
                    formDataInvGroupList.add(narrGroup);

                }
                invGroup.setFormDataGroupList(formDataInvGroupList);
                formDataGroupList.add(invGroup);
            }
    }


    /**
     * Get all the contact names for contact/event_person_link .
     * @param id
     * @return contactNames
     */
    List<String> getPersonsContacted(long id, List<ApsCaseReviewContactNamesDto> contactNames) {
        List<String> contactEventPersonList = new ArrayList<>();
        if (null != contactNames && !contactNames.isEmpty()) {
            for (ApsCaseReviewContactNamesDto name : contactNames) {
                if (name.getEventId() == id) {
                    contactEventPersonList.add(name.getFullName());
                }
            }
        }
        return contactEventPersonList;
    }


    /**
     * check for earliest contact for aps sa event. artf150820 APS Enhancements: make it latest
     * so that if there are multiple sa ftf contacts the narrative will print only once with the last
     * @param id
     * @param apsSaContactList
     */
    private long getFirstContactEventBySA(long id, List<ApsSafetyAssessmentContactDto> apsSaContactList) {
        List<ApsSafetyAssessmentContactDto> saContactList = new ArrayList<>();
        if (null != apsSaContactList && !apsSaContactList.isEmpty()) {
            for (ApsSafetyAssessmentContactDto contactDb : apsSaContactList) {
                if (0 < contactDb.getIdApsSaEvent() && id == contactDb.getIdApsSaEvent()) {
                    saContactList.add(contactDb);
                }
            }
        }
        // artf149261 if there are ftf contacts sort and return the earliest
        long lastContactEventBySA = 0L;
        if (1 == saContactList.size()) {
            lastContactEventBySA = saContactList.get(0).getContactEventId();
        } else if (1 < saContactList.size()) {
            List<ApsSafetyAssessmentContactDto> contactList = new ArrayList<>();
            for (ApsSafetyAssessmentContactDto contact : saContactList) {
                if (!CodesConstant.CASE_INITIATION_CONTACT.equals(contact.getContactType())) {
                    contactList.add(contact);
                }
            }
            if (1 == contactList.size()) {
                lastContactEventBySA = contactList.get(0).getContactEventId();
            } else if (!contactList.isEmpty()) {
                // Sort the contacts on dtContactOccurred
                contactList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getDateContactOccurred).reversed());
                lastContactEventBySA = contactList.get(0).getContactEventId();
            }
        }
        return lastContactEventBySA;
    }


    /**
     * Method Name: getContactDetail Method Description:  returns safety assessment contact based on event
     */
    private ApsSafetyAssessmentContactDto getContactDetail(Long event, List<ApsSafetyAssessmentContactDto> apsSaContactList) {
        ApsSafetyAssessmentContactDto contactDB = new ApsSafetyAssessmentContactDto();
        if (null != apsSaContactList && !apsSaContactList.isEmpty()) {
            for (ApsSafetyAssessmentContactDto contact : apsSaContactList) {
                if (event == contact.getContactEventId()) {
                    contactDB = contact;
                    break;
                }
            }
        }
        return contactDB;
    }

    /**
     * Method Name: getApsSaEventId Method Description:  returns safety assessment event id from contact list
     */
    private long getApsSaEventId(long id, List<ApsSafetyAssessmentContactDto> apsSaContactList) {
        long apsSaEventId = 0;
        if (null != apsSaContactList && !apsSaContactList.isEmpty()) {
            for (ApsSafetyAssessmentContactDto contactDb : apsSaContactList) {
                if (id == contactDb.getContactEventId() && contactDb.getIdApsSaEvent() != 0L) {
                    apsSaEventId = contactDb.getIdApsSaEvent();
                    break;
                }
            }
        }
        return apsSaEventId;
    }

    /**
     * Method Name: getMergedIntakesAsContacts Method Description: populates the contacts from merged
     */
    private  List<ApsSafetyAssessmentContactDto> getMergedIntakesAsContacts(ApsCaseReviewServiceDto apsCaseReviewServiceDto) {
       List<ApsCaseReviewDto>   apsCaseReviewDtoList = apsCaseReviewServiceDto.getApsCaseReviewDtoList();
       List<ApsSafetyAssessmentContactDto> mergedIntakeContacts = new ArrayList<>();

       for (ApsCaseReviewDto apsCaseReviewDto : apsCaseReviewDtoList) {
           if (apsCaseReviewDto.getIdStage() != apsCaseReviewDtoList.get(0).getIdStage()) {
               ApsSafetyAssessmentContactDto contact = new ApsSafetyAssessmentContactDto();
               contact.setContactType(BookmarkConstants.MERGEDINTAKE);
               contact.setContactEventId(apsCaseReviewDto.getIdStage());
               contact.setDateContactOccurred(apsCaseReviewDto.getDtDtIncomingCall());
               contact.setTimeContactOccurred(FormattingHelper.formatTime(apsCaseReviewDto.getDtDtIncomingCall()));
               contact.setTsContactOccurred(contact.getDateContactOccurred(), contact.getTimeContactOccurred());
               mergedIntakeContacts.add(contact);
           }
       }
       return mergedIntakeContacts;
   }

    /**
     * Method Name: getContactsSummary Method Description:  filters the contacts based on stage type
     */
    private List<ApsSafetyAssessmentContactDto> getContactsSummary(String cdStage, List<ApsSafetyAssessmentContactDto> apsSaContactList) {

        List<ApsSafetyAssessmentContactDto> list = new ArrayList<>();
        return apsSaContactList
                .stream()
                .filter(contact -> cdStage.equals(contact.getCdStage()))
                .sorted(Comparator.comparing(ApsSafetyAssessmentContactDto :: getIdStage).reversed())
                .collect(Collectors.toList());
    }

    /****COLLATERAL INFORMATION */
    private void populateCollateralInformation(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!apsCaseReviewServiceDto.getCollateralDtoList().isEmpty()) {
            List<ApsStagePersonDto> collateralDtoList = apsCaseReviewServiceDto.getCollateralDtoList();
            for (ApsStagePersonDto apsStagePersonDto : collateralDtoList) {
                FormDataGroupDto collateralGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERAL, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCollateralGroupList = new ArrayList<>();

                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_FIRST, apsStagePersonDto.getSzNmNameFirst()));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_MIDDLE, apsStagePersonDto.getSzNmNameMiddle()));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_LAST, apsStagePersonDto.getSzNmNameLast()));

                if (isValid(apsStagePersonDto.getSzCdNameSuffix())) {
                    FormDataGroupDto suffixCommaGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_E, BookmarkConstants.EMPTY_STRING);
                    List<FormDataGroupDto> formDataSuffixCommaGroupList = new ArrayList<>();
                    formDataSuffixCommaGroupList.add(suffixCommaGroup);
                    collateralGroup.setFormDataGroupList(formDataSuffixCommaGroupList);
                    bookmarkCollateralGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.COL_NAME_SUFFIX, apsStagePersonDto.getSzCdNameSuffix(), CodesConstant.CSUFFIX2));
                }
                bookmarkCollateralGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.COL_RELTNSP, apsStagePersonDto.getSzCdStagePersRelInt(), CodesConstant.CRPTRINT));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.IND_REPORTER, apsStagePersonDto.getIndStagePersReporter()));
                bookmarkCollateralGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.COL_SEX, apsStagePersonDto.getCdPersonSex(), CodesConstant.CSEX));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NOTES, apsStagePersonDto.getSzTxtStagePersNotes()));

                getPersonAddrPhone(bookmarkCollateralGroupList, apsStagePersonDto);

                collateralGroup.setBookmarkDtoList(bookmarkCollateralGroupList);
                formDataGroupList.add(collateralGroup);
            }
        }
    }

    /****PRINCIPAL INFORMATION */
    private void populatePrincipalInformation(ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {

       if (!apsCaseReviewServiceDto.getPrincipalDtoList().isEmpty()) {
           List<ApsStagePersonDto> principalInfoList = apsCaseReviewServiceDto.getPrincipalDtoList();
           for (ApsStagePersonDto apsStagePersonDto : principalInfoList) {
               FormDataGroupDto principalGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM, BookmarkConstants.EMPTY_STRING);
               List<BookmarkDto> bookmarkPrincipalGroupList = new ArrayList<>();

               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, apsStagePersonDto.getSzNmNameFirst()));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, apsStagePersonDto.getSzNmNameMiddle()));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_LAST, apsStagePersonDto.getSzNmNameLast()));

               if (isValid(apsStagePersonDto.getSzCdNameSuffix())) {
                   FormDataGroupDto suffixCommaGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_PRN, BookmarkConstants.EMPTY_STRING);
                   List<FormDataGroupDto> formDataSuffixCommaGroupList = new ArrayList<>();
                   formDataSuffixCommaGroupList.add(suffixCommaGroup);
                   principalGroup.setFormDataGroupList(formDataSuffixCommaGroupList);
                   bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, apsStagePersonDto.getSzCdNameSuffix(), CodesConstant.CSUFFIX2));

               }
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RELTNSP, apsStagePersonDto.getSzCdStagePersRelInt(), CodesConstant.CRPTRINT));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ROLE, apsStagePersonDto.getSzCdStagePersRole(), CodesConstant.CROLEALL));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.IND_REPORTER, apsStagePersonDto.getIndStagePersReporter()));

               if (null != apsStagePersonDto.getDtDtPersonBirth()) {
                   org.exolab.castor.types.Date birthDate =  DateHelper.toCastorDateSafe(FormattingHelper.formatDate(apsStagePersonDto.getDtDtPersonBirth()));
                   bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_AGE, DateHelper.getAge(birthDate)));
               }

               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_SSN, apsStagePersonDto.getSzNbrPersonIdNumber()));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_LANGUAGE, apsStagePersonDto.getSzCdPersonLanguage(), CodesConstant.CLANG));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_DOB_APPROX, apsStagePersonDto.getIndPersonDobApprox()));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_BIRTH, FormattingHelper.formatDate(apsStagePersonDto.getDtDtPersonBirth())));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_DOD, FormattingHelper.formatDate(apsStagePersonDto.getDtDtPersonDeath())));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ETHNCTY, apsStagePersonDto.getSzCdPersonEthnicGroup(), CodesConstant.CETHNIC));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_SEX, apsStagePersonDto.getCdPersonSex(), CodesConstant.CSEX));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RSN, apsStagePersonDto.getSzCdPersonDeath(), CodesConstant.CRSNDTHA));
               bookmarkPrincipalGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.VICTIM_MARITAL, apsStagePersonDto.getSzCdPersonMaritalStatus(), CodesConstant.CMARSTAT));
               bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NOTES, apsStagePersonDto.getSzTxtStagePersNotes()));

               getPersonAddrPhone(bookmarkPrincipalGroupList, apsStagePersonDto);

               principalGroup.setBookmarkDtoList(bookmarkPrincipalGroupList);
               formDataGroupList.add(principalGroup);
           }
       }
    }

    /**
     * Method Name: getPersonAddrPhone Method Description:  populates address and phone information for principal and collateral
     */
    private void getPersonAddrPhone(List<BookmarkDto> bookmarkGroupList, ApsStagePersonDto apsStagePersonDto) {
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE, apsStagePersonDto.getSzCdPersAddrLinkType(), CodesConstant.CADDRTYP));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_LN_1, apsStagePersonDto.getSzAddrPersAddrStLn1()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_ATTN, apsStagePersonDto.getSzAddrPersAddrAttn()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_LN_2, apsStagePersonDto.getSzAddrPersAddrStLn2()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_CITY, apsStagePersonDto.getSzAddrCity()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_STATE, apsStagePersonDto.getSzCdAddrState()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_ZIP, apsStagePersonDto.getAddrZip()));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY, apsStagePersonDto.getSzCdAddrCounty(), CodesConstant.CCOUNT));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ADDR_NOTES, apsStagePersonDto.getSzTxtPersAddrCmnts()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_TYPE, apsStagePersonDto.getSzCdPhoneType()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NUMBER, FormattingHelper.formatPhone(apsStagePersonDto.getNbrPhone())));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION, apsStagePersonDto.getNbrPhoneExtension()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NOTES, apsStagePersonDto.getSzTxtPhoneComments()));
    }

    /****ALLEGATION DETAIL  INFORMATION */
    private void populateAllegationDetails(List<AllegationDto> allegationDtoList, List<FormDataGroupDto> formDataGroupList) {
        List<FormDataGroupDto> formDataAllegationGroupList = new ArrayList<>();
            if (!allegationDtoList.isEmpty()) {
                for (AllegationDto allegationValueBean : allegationDtoList) {
                    FormDataGroupDto allegationGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkAllegationGroupList = new ArrayList<>();

                    bookmarkAllegationGroupList.add(createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM, allegationValueBean.getNmVictim()));
                    bookmarkAllegationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG, allegationValueBean.getCdAllegType(), CodesConstant.CAPSALLG));
                    bookmarkAllegationGroupList.add(createBookmark(BookmarkConstants.ALLEG_DTL_AP, allegationValueBean.getNmAllegedPerpetrator()));
                    bookmarkAllegationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP, allegationValueBean.getCdAllegDisposition(), CodesConstant.CDISPSTN));

                    allegationGroup.setBookmarkDtoList(bookmarkAllegationGroupList);
                    formDataAllegationGroupList.add(allegationGroup);
                }
            }
        formDataGroupList.addAll(formDataAllegationGroupList);
    }

    /**
     * Method Name: checkPreSingleStage Method Description:  checks the stage if it is pre SINGLE
     */
    private boolean checkPreSingleStage(java.util.Date stageStartDate) {
        String decDecode = lookupService.simpleDecodeSafe(CodesConstant.CRELDATE, CodesConstant.CRELDATE_NOV_2020_APS);
        boolean preSingleStage = false;
        try {
            preSingleStage = DateHelper.isBefore(stageStartDate, DateHelper.toJavaDateFromInput(decDecode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preSingleStage;
    }

    /**
     * Method Name: populateInvestigationStageSummary Method Description:  populates the investigation stage summary section
     */
    private void populateInvestigationStageSummary(ApsCaseReviewDto apsCaseReviewDto, ApsCaseReviewServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        List<FormDataGroupDto> formDataInvestigationGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkInvestigationGroupList = new ArrayList<>();
        List<ApsSafetyAssessmentDto> invSafetyDbList = getSafetyAssessmentEventsByStageCase(apsCaseReviewServiceDto.getSafetyDbList(), CodesConstant.CSTAGES_INV);

        apsCaseReviewServiceDto.getApsRoraDtoList().forEach(apsRoraDto -> {
            if (apsCaseReviewDto.getInvStageId() == apsRoraDto.getStageId()) {
                bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_RORA_CMPLT_DATE, FormattingHelper.formatDate(apsRoraDto.getDtRoraComplete())));
                bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_FINAL_RISK_LEVEL, apsRoraDto.getFinalRiskLevelCode(), CodesConstant.CANRSKVL));
            }
        });
        bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN, FormattingHelper.formatDate(apsCaseReviewDto.getDtApsInvstBegun())));
        bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_INVEST_COMPLETED, FormattingHelper.formatDate(apsCaseReviewDto.getDtDtApsInvstCmplt())));
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_OVERALL_DISP, apsCaseReviewDto.getSzCdApsInvstOvrallDisp(), CodesConstant.CAPSALDP));
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_CLOSURE_REASON, apsCaseReviewServiceDto.getStageValueBeanDto().getCdStageReasonClosed(), CodesConstant.CAINVCLS));
        bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_DECISION, FormattingHelper.formatDate(apsCaseReviewServiceDto.getStageValueBeanDto().getDtStageClose())));

        invSafetyDbList.forEach(apsSafetyAssessmentDto -> formDataInvestigationGroupList.add(setInvStageSafetyAssessments(apsSafetyAssessmentDto)));
        formDataInvestigationGroupList.addAll(setSafetyContactDates(apsCaseReviewServiceDto.getApsSaContactList()));

        formDataGroupList.addAll(formDataInvestigationGroupList);
        bookmarkGroupList.addAll(bookmarkInvestigationGroupList);
    }

    /**
     * Method Name: setSafetyContactDates Method Description:  saty contacts dates will be populated
     */
    private  List<FormDataGroupDto> setSafetyContactDates(List<ApsSafetyAssessmentContactDto> apsSaContactList) {
        List<FormDataGroupDto> safetyContactGroupList = new ArrayList<>();
            if (!apsSaContactList.isEmpty()) {
                apsSaContactList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getTsContactOccurred).reversed());
                for (ApsSafetyAssessmentContactDto contactDB : apsSaContactList) {
                    if (0 == contactDB.getIdApsSaEvent() && CodesConstant.CCNTCTYP_CSAF.equals(contactDB.getCodeContactType())) {
                        FormDataGroupDto safetyContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_CONTACT_DATE, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> bookmarkSafetyContactGroupList = new ArrayList<>();
                        bookmarkSafetyContactGroupList.add(createBookmark(BookmarkConstants.DT_SAFETY_CONTACT_ENTERED, FormattingHelper.formatDate(contactDB.getDateContactEntered())));
                        bookmarkSafetyContactGroupList.add(createBookmark(BookmarkConstants.ACTUAL_DT_SAFETY_CONTACT, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                        bookmarkSafetyContactGroupList.add(createBookmark(BookmarkConstants.ACTUAL_TIME_SAFETY_CONTACT, contactDB.getTimeContactOccurred()));
                        safetyContactGroup.setBookmarkDtoList(bookmarkSafetyContactGroupList);
                        safetyContactGroupList.add(safetyContactGroup);
                    }
                }
            }
            return safetyContactGroupList;
    }

    /**
     * Method Name: setInvStageSafetyAssessments Method Description:  populate the safety assement info INV  stage
     */
    private FormDataGroupDto setInvStageSafetyAssessments(ApsSafetyAssessmentDto safetyDB) {
        SimpleDateFormat tmFormatter = new SimpleDateFormat("hh:mm a");
        FormDataGroupDto initSAGroup = null;
        if (null != safetyDB) {
            List<BookmarkDto> bookmarkInvStageSAGroupList = new ArrayList<>();
            List<FormDataGroupDto> formDataInvStageSAGroupList = new ArrayList<>();

                initSAGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_SAFETY_INIT, BookmarkConstants.EMPTY_STRING);

                // populate Initial safety Assessment header and priority
                if (safetyDB.getAssessmentType().equals(CodesConstant.CAPSFAT_INIT)) {
                    bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.SA_HEADER, BookmarkConstants.SFTY_ASSMT));
                    FormDataGroupDto prtyGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIORITY, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkPrtyGroupList = new ArrayList<>();
                    bookmarkPrtyGroupList.add(createBookmark(BookmarkConstants.ASSIGNED_PRIORITY, safetyDB.getInitialStagePriority()));
                    bookmarkPrtyGroupList.add(createBookmark(BookmarkConstants.SUM_FINAL_PRIORITY, safetyDB.getCurrentStagePriority()));
                    prtyGrp.setBookmarkDtoList(bookmarkPrtyGroupList);
                    formDataInvStageSAGroupList.add(prtyGrp);
                    bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.SA_COMP_HEADER, BookmarkConstants.SA_COMP));
                } else {
                    bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.SA_HEADER, BookmarkConstants.SFTY_REAS));
                    bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.SA_COMP_HEADER, BookmarkConstants.SA_REAS_COMP));
                }

                if (null != safetyDB.getDateAssessmentCompleted()) {
                    FormDataGroupDto dtGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_SA_CMPLT_INV, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkDtGroupList = new ArrayList<>();
                    bookmarkDtGroupList.add(createBookmark(BookmarkConstants.SUM_SAFETY_ASSMT_CMPLT_DATE, FormattingHelper.formatDate(safetyDB.getDateAssessmentCompleted())));
                    bookmarkDtGroupList.add(createBookmark(BookmarkConstants.SAFETY_ASSMT_CMPLT_TIME, tmFormatter.format(safetyDB.getDateAssessmentCompleted().getTime())));
                    dtGrp.setBookmarkDtoList(bookmarkDtGroupList);
                    formDataInvStageSAGroupList.add(dtGrp);
                }
               List<ApsSafetyAssessmentContactDto> contactDBList = safetyDB.getContactList();
               if (!contactDBList.isEmpty()) {
                   contactDBList.sort(Comparator.comparing(ApsSafetyAssessmentContactDto::getTsContactOccurred));
                   for (ApsSafetyAssessmentContactDto contactDB : contactDBList) {
                       if (safetyDB.getEventId() == contactDB.getIdApsSaEvent()) {
                           if (CodesConstant.CASE_INITIATION_CONTACT.equals(contactDB.getContactType())) {
                               FormDataGroupDto ciGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CI_INV, BookmarkConstants.EMPTY_STRING);
                               List<BookmarkDto> bookmarkCiGroupList = new ArrayList<>();
                               bookmarkCiGroupList.add(createBookmark(BookmarkConstants.SUM_DATE_CI, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                               bookmarkCiGroupList.add(createBookmark(BookmarkConstants.SUM_TIME_CI, contactDB.getTimeContactOccurred()));
                               ciGroup.setBookmarkDtoList(bookmarkCiGroupList);
                               formDataInvStageSAGroupList.add(ciGroup);
                           } else {
                               if (BookmarkConstants.Y.equals(contactDB.getIndContactAttempted())) {
                                   FormDataGroupDto atmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_ATTMPT_F2F_INV, BookmarkConstants.EMPTY_STRING);
                                   List<BookmarkDto> bookmarkAtmptGroupList = new ArrayList<>();
                                   bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_DATE, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                                   bookmarkAtmptGroupList.add(createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_TIME, contactDB.getTimeContactOccurred()));
                                   atmptGroup.setBookmarkDtoList(bookmarkAtmptGroupList);
                                   formDataInvStageSAGroupList.add(atmptGroup);
                               } else {
                                   FormDataGroupDto actualGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_ACTUAL_F2F_INV, BookmarkConstants.EMPTY_STRING);
                                   List<BookmarkDto> bookmarkActualGroupList = new ArrayList<>();
                                   bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_DATE, FormattingHelper.formatDate(contactDB.getDateContactOccurred())));
                                   bookmarkActualGroupList.add(createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_TIME, contactDB.getTimeContactOccurred()));
                                   actualGroup.setBookmarkDtoList(bookmarkActualGroupList);
                                   formDataInvStageSAGroupList.add(actualGroup);
                               }
                           }
                       }
                   }
               }
            bookmarkInvStageSAGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION, safetyDB.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC));

            // artf148655 artf148428 nbr is sum of ftf and other
            ApsServicePlanDto servicePlanDB = safetyDB.getServicePlan();
            if ((servicePlanDB != null) && (servicePlanDB.getAssmtMonitoringPlan() != null)) {
                    if (servicePlanDB.getAssmtMonitoringPlan().isBeforeFtfColumn()) {
                        bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.NBR_REQ_SAFETY_CONTACTS, String.valueOf(servicePlanDB.getAssmtMonitoringPlan().getNumberOfContactsReqd())));
                    } else {
                        // artf150680 display all the contact standards details
                        bookmarkInvStageSAGroupList.add(createBookmark(BookmarkConstants.NBR_REQ_SAFETY_CONTACTS, String.valueOf(servicePlanDB
                                .getAssmtMonitoringPlan().getNumberOfContactsReqd()
                                + servicePlanDB.getAssmtMonitoringPlan().getNoOfFaceToFaceContactsRequired())));
                        FormDataGroupDto cstandGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_STAND, FormGroupsConstants.TMPLAT_INV_SAFETY_INIT);
                        List<BookmarkDto> bookmarkCStandGroupList = new ArrayList<>();
                        bookmarkCStandGroupList.add(createBookmark(BookmarkConstants.STAND_FTF, servicePlanDB.getAssmtMonitoringPlan().getNoOfFaceToFaceContactsRequired()));
                        bookmarkCStandGroupList.add(createBookmark(BookmarkConstants.STAND_OTHER, servicePlanDB.getAssmtMonitoringPlan().getNumberOfContactsReqd()));
                        cstandGroup.setBookmarkDtoList(bookmarkCStandGroupList);
                        formDataInvStageSAGroupList.add(cstandGroup);
                    }
                }
                initSAGroup.setBookmarkDtoList(bookmarkInvStageSAGroupList);
                initSAGroup.setFormDataGroupList(formDataInvStageSAGroupList);

            }
          return initSAGroup;
    }



    /**
     * Method Name: getSafetyAssessmentEventsByStageCase Method Description: filters the safety events by stage type
     */
    private List<ApsSafetyAssessmentDto> getSafetyAssessmentEventsByStageCase(List<ApsSafetyAssessmentDto> safetyEventList, String cdStage) {
        return safetyEventList.stream()
                .filter(apsSafetyAssessmentDto -> cdStage.equals(apsSafetyAssessmentDto.getStageCode()))
                .collect(Collectors.toList());
    }


    /**
     * Method Name: populatePageHeader Method Description: Populates the case level header information
     */
    private void populatePageHeader(ApsCaseReviewDto apsCaseReviewDto, StageValueBeanDto stageValueBeanDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        List<FormDataGroupDto> formDataPageGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkPageGroupList = new ArrayList<>();

        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, apsCaseReviewDto.getCaseNumber()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NAME, stageValueBeanDto.getCaseName()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_LAST, apsCaseReviewDto.getPrimaryLastNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_FIRST, apsCaseReviewDto.getPrimaryFirstNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_MIDDLE, apsCaseReviewDto.getPrimaryMiddleNm()));
        if (isValid(apsCaseReviewDto.getPrimarySuffix())) {
            formDataPageGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA, BookmarkConstants.EMPTY_STRING));
            bookmarkPageGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_WORKER_NAME_SUFFIX, apsCaseReviewDto.getPrimarySuffix(), CodesConstant.CSUFFIX2));
        }
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_LAST, apsCaseReviewDto.getSupervisorLastNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_FIRST, apsCaseReviewDto.getSupervisorfirstNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_MIDDLE, apsCaseReviewDto.getSupervisorMiddleNm()));
        if (isValid(apsCaseReviewDto.getSupervisorSuffix())) {
            formDataPageGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2, BookmarkConstants.EMPTY_STRING));
            bookmarkPageGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_SUPERVISOR_NAME_SUFFIX, apsCaseReviewDto.getSupervisorSuffix(), CodesConstant.CSUFFIX2));
        }
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_CASE_CLOSED, FormattingHelper.formatDate(apsCaseReviewDto.getDtCaseClosed())));

        formDataGroupList.addAll(formDataPageGroupList);
        bookmarkGroupList.addAll(bookmarkPageGroupList);
    }

    /**
     * Method Name: populateIntakeStageSummary Method Description: Populates the Intake Stage summary
     */
    private void populateIntakeStageSummary(ApsCaseReviewDto apsCaseReviewDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        List<FormDataGroupDto> formDataIntakeGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkIntakeGroupList = new ArrayList<>();

        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_INTAKE_NUM, apsCaseReviewDto.getIdStage()));
        bookmarkIntakeGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIM_ALLEG, apsCaseReviewDto.getSzCdIncmgAllegType(), CodesConstant.CAPSALLG));
        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_PRIORITY_DETERM, apsCaseReviewDto.getSzCdStageCurrPriority()));
        bookmarkIntakeGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_SPCL_HANDLING, apsCaseReviewDto.getSzCdIncmgSpecHandling(), CodesConstant.CSPECHND));
        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_SAFETY_ISSUES, apsCaseReviewDto.getBIndIncmgWorkerSafety()));
        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_SENSITIVE_ISSUE, apsCaseReviewDto.getBIndIncmgSensitive()));
        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_DATE_RPTED, FormattingHelper.formatDate(apsCaseReviewDto.getDtDtIncomingCall())));
        bookmarkIntakeGroupList.add(createBookmark(BookmarkConstants.SUM_TIME_RPTED, apsCaseReviewDto.getTmTmIncmgCall()));

        if (null != apsCaseReviewDto.getCdIncmgDeterm() && !apsCaseReviewDto.getCdIncmgDeterm().isEmpty()) {
            for (String detFactor:apsCaseReviewDto.getCdIncmgDeterm()) {
                FormDataGroupDto detFactorGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DET_FACTOR, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDetFactorGroupList = new ArrayList<>();
                bookmarkDetFactorGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.DETERM_FACTR, detFactor, CodesConstant.CADETFCT));
                detFactorGroup.setBookmarkDtoList(bookmarkDetFactorGroupList);
                formDataIntakeGroupList.add(detFactorGroup);
            }
        }
        bookmarkGroupList.addAll(bookmarkIntakeGroupList);
        formDataGroupList.addAll(formDataIntakeGroupList);
    }

    private boolean isValid(String str) {
        return (null != str && 0 < str.length());
    }
}
