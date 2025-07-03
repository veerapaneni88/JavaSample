package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.pcsphistoryform.dto.CareDetailDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareNarrativeInfoDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewLegacyServiceDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceAuthDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.NameDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;

import static us.tx.state.dfps.service.common.FormConstants.CONTACT_TYPE_C24H;
import static us.tx.state.dfps.service.common.FormConstants.CONTACT_TYPE_CFTF;
import static us.tx.state.dfps.service.common.FormConstants.CONTACT_TYPE_CMST;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION
 * APS Case Review (Legacy)
 *
 * @author CHITLA
 * Feb, 2022© Texas Department of Family and Protective Services
 */
@Repository
public class APSCaseReviewLegacyPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {
        ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto = (ApsCaseReviewLegacyServiceDto) parentDtoObj;
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        ApsCaseReviewDto apsCaseReviewDto = null;
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkGroupList = new ArrayList<>();
        List<BlobDataDto> blobDataList = new ArrayList<>();

        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getApsCaseReviewDto())) {
            apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDto();
        } else {
            return preFillData;
        }
        /****PAGE HEADER */
        populatePageHeader(apsCaseReviewDto, apsCaseReviewServiceDto.getStageValueBeanDto(), formDataGroupList, bookmarkGroupList);

        /****INTAKE STAGE SUMMARY */
        populateIntakeStageSummary(apsCaseReviewDto, formDataGroupList, bookmarkGroupList);

        /****INVESTIGATION STAGE SUMMARY */
        populateInvestigationStageSummary(apsCaseReviewDto, apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList);
        populateInvestigationContacts(apsCaseReviewServiceDto, formDataGroupList);

        /****SERVICE DELIVERY STAGE */
        List<StageValueBeanDto> stageValueBeanList = apsCaseReviewServiceDto.getSvcDeliveryClosureCheckList();
        if (!TypeConvUtil.isNullOrEmpty(stageValueBeanList) && stageValueBeanList.size() > 0) {
            populateServiceDeliveryStageInfo(stageValueBeanList.get(0), bookmarkGroupList);
            populateServiceDeliveryClosureChecklist(stageValueBeanList.get(0), bookmarkGroupList);
        }

        /****INTAKE DETAILS */
        blobDataList.add(createBlobData(BookmarkConstants.CALL_NARR_BLOB, BookmarkConstants.INCOMING_NARRATIVE_VIEW, String.valueOf(apsCaseReviewDto.getPriorStageId())));

        /****ALLEGATION DETAIL */
        populateAllegationDetails(apsCaseReviewServiceDto.getAllegationDtoList(), formDataGroupList);

        /****PRINCIPAL INFORMATION */
        populatePrincipalInformation(apsCaseReviewServiceDto, formDataGroupList);

        /****REPORTER INFORMATION */
        populateReporterInformation(apsCaseReviewServiceDto, formDataGroupList);

        /****COLLATERAL INFORMATION */
        populateCollateralInformation(apsCaseReviewServiceDto, formDataGroupList);

        populateCareNarrative(apsCaseReviewServiceDto, formDataGroupList);

        /****INVESTIGATION CONCLUSION CHECKLIST */
        populateInvConclusionCheckList(apsCaseReviewServiceDto, formDataGroupList, bookmarkGroupList);

        /****INVESTIGATION CONCLUSION NARRATIVE */
        populateInvestigationConclusionNarrative(apsCaseReviewServiceDto, formDataGroupList);

        populateOutcomeMatrixSummary(apsCaseReviewServiceDto, formDataGroupList);
        populateCareFormSummary(apsCaseReviewServiceDto, formDataGroupList);

        populateOutcomeMatrixNarrative(apsCaseReviewServiceDto, formDataGroupList);

        populateECS(apsCaseReviewServiceDto, formDataGroupList);
        populateDonatedCommunityServices(apsCaseReviewServiceDto, formDataGroupList);

        populateContactInformation(apsCaseReviewServiceDto, formDataGroupList);

        preFillData.setBookmarkDtoList(bookmarkGroupList);
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBlobDataDtoList(blobDataList);
        return preFillData;
    }

    private void populateContactInformation(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getContactInfoList())
                && apsCaseReviewServiceDto.getContactInfoList().size() > 0) {

            for (ApsCaseReviewContactDto contactDto : apsCaseReviewServiceDto.getContactInfoList()) {
                FormDataGroupDto tmpltContact = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT, BookmarkConstants.EMPTY_STRING);
                List<FormDataGroupDto> tmpltContactDtolist = new ArrayList<>();

                List<BookmarkDto> bookmarkContactInfoGroupList = new ArrayList<>();

                bookmarkContactInfoGroupList.add(createBookmark(BookmarkConstants.CONTACT_CONTACT_ID, contactDto.getIdEvent()));
                bookmarkContactInfoGroupList.add(createBookmark(BookmarkConstants.CONTACT_DATE_OCCURRED, FormattingHelper.formatDate(contactDto.getDtContactOccurred())));
                bookmarkContactInfoGroupList.add(createBookmark(BookmarkConstants.CONTACT_TIME_OCCURRED, FormattingHelper.formatTime(contactDto.getDtContactOccurred())));
                bookmarkContactInfoGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_TYPE, contactDto.getCdContactType(), CodesConstant.CCNTCTYP));
                bookmarkContactInfoGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CONTACT_LOCATION, contactDto.getCdContactLocation(), CodesConstant.CCNCTLOC));

                if (!TypeConvUtil.isNullOrEmpty(contactDto.getPersonsContacted()) && contactDto.getPersonsContacted().size() > 0) {
                    for (NameDto nameDto : contactDto.getPersonsContacted()) {
                        FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_NAME, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> bookmarkPersonGroupList = new ArrayList<>();
                        bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_NAME_FIRST, nameDto.getNmNameFirst()));
                        bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_NAME_MIDDLE, nameDto.getNmNameMiddle()));
                        bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_NAME_LAST, nameDto.getNmNameLast()));
                        if (StringUtil.isValid(contactDto.getNameSuffix())) {
                            tmpltContactDtolist.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA, BookmarkConstants.EMPTY_STRING));
                            bookmarkPersonGroupList.add(createBookmark(BookmarkConstants.CONTACT_NAME_SUFFIX, nameDto.getCdNameSuffix()));
                        }
                        personGroup.setBookmarkDtoList(bookmarkPersonGroupList);
                        tmpltContactDtolist.add(personGroup);
                    }
                }

                if (null != contactDto.getIndAttempted() && contactDto.getIndAttempted().equals("Y")) {
                    tmpltContactDtolist.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ATTEMPTED, BookmarkConstants.EMPTY_STRING));
                }

                FormDataGroupDto narrativeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_NARRATIVE,
                        FormConstants.EMPTY_STRING);
                List<BlobDataDto> blobDataDtoList = new ArrayList<>();
                BlobDataDto blobDataDto = createBlobData(BookmarkConstants.CONTACT_NARRATIVE,
                        BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
                blobDataDtoList.add(blobDataDto);
                narrativeGroup.setBlobDataDtoList(blobDataDtoList);
                tmpltContactDtolist.add(narrativeGroup);

                tmpltContact.setBookmarkDtoList(bookmarkContactInfoGroupList);
                tmpltContact.setFormDataGroupList(tmpltContactDtolist);
                formDataGroupList.add(tmpltContact);
            }
        }
    }

    private void populateCareFormSummary(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        List<FormDataGroupDto> careFormGroupList = new ArrayList<>();
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getCareNarrativeInfoDto())) {
            for (CareNarrativeInfoDto careNarrativeInfoDto : apsCaseReviewServiceDto.getCareNarrativeInfoDto()) {
                FormDataGroupDto careFormGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_FORM, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareFormGroupList = new ArrayList<>();

                bookmarkCareFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CARE_PRB_CATEGORY, careNarrativeInfoDto.getCareCdCareCategory(), CodesConstant.CCARECAT));
                bookmarkCareFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CARE_PRB_SUB_CATEGORY, careNarrativeInfoDto.getCareCdCareFactor(), CodesConstant.CCAREFAC));
                bookmarkCareFormGroupList.add(createBookmark(BookmarkConstants.CARE_PRB_TS_UPDATE, FormattingHelper.formatDate(careNarrativeInfoDto.getCareDtLastUpdate())));
                bookmarkCareFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CARE_ACT_CATEGORY, careNarrativeInfoDto.getCareCdApsOutcomeAction(), CodesConstant.CACTITP2));
                bookmarkCareFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CARE_ACT_SUB_CATEGORY, careNarrativeInfoDto.getCareCdApsOutcomeActnCateg(), CodesConstant.CACTCTG2));
                bookmarkCareFormGroupList.add(createBookmark(BookmarkConstants.CARE_ACT_UPDATE, FormattingHelper.formatDate(careNarrativeInfoDto.getCareDtApsOutcomeAction())));
                bookmarkCareFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CARE_OUT_SUB_CATEGORY, careNarrativeInfoDto.getCareCdApsOutcomeResult(), CodesConstant.COUTCTYP));
                bookmarkCareFormGroupList.add(createBookmark(BookmarkConstants.CARE_OUT_UPDATE, FormattingHelper.formatDate(careNarrativeInfoDto.getCareDtApsOutcomeRecord())));

                careFormGroup.setBookmarkDtoList(bookmarkCareFormGroupList);
                careFormGroupList.add(careFormGroup);
            }
        }
        formDataGroupList.addAll(careFormGroupList);
    }

    private void populateOutcomeMatrixSummary(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        List<FormDataGroupDto> outcomeMatrixSummaryGroupList = new ArrayList<>();
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getCareDetailDto())) {
            for (CareDetailDto careDetailDto : apsCaseReviewServiceDto.getCareDetailDto()) {

                FormDataGroupDto outcomeMatrixSummaryGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_OUTCOME_MATRIX, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkOutcomeMatrixGroupList = new ArrayList<>();

                bookmarkOutcomeMatrixGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.OM_PRB_CATEGORY, careDetailDto.getCdApsCltFactorCateg(), CodesConstant.CPROCATG));
                bookmarkOutcomeMatrixGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.OM_PRB_SUB_CATEGORY, careDetailDto.getCdApsClientFactor(), CodesConstant.CPROBTYP));
                if (null != careDetailDto.getDtLastUpdate()) {
                    bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_PRB_LAST_UPDATE, FormattingHelper.formatDate(careDetailDto.getDtLastUpdate())));
                }
                bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_PRB_CMNTS, careDetailDto.getTxtApsCltFactorCmnts()));
                bookmarkOutcomeMatrixGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.OM_ACT_CATEGORY, careDetailDto.getCdApsOutcomeActnCateg(), CodesConstant.CACTCATG));
                bookmarkOutcomeMatrixGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.OM_ACT_SUB_CATEGORY, careDetailDto.getCdApsOutcomeAction(), CodesConstant.CACTITYP));
                if (null != careDetailDto.getDtApsOutcomeAction()) {
                    bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_ACT_LAST_UPDATE, FormattingHelper.formatDate(careDetailDto.getDtApsOutcomeAction())));
                }
                bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_ACT_CMNTS, careDetailDto.getTxtApsOutcomeAction()));
                bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_OUT_SUB_CATEGORY, careDetailDto.getTxtApsOutcomeResult()));
                if (null != careDetailDto.getDtApsOutcomeRecord()) {
                    bookmarkOutcomeMatrixGroupList.add(createBookmark(BookmarkConstants.OM_OUT_LAST_UPDATE, FormattingHelper.formatDate(careDetailDto.getDtApsOutcomeRecord())));
                }
                bookmarkOutcomeMatrixGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.OM_OUT_CMNTS, careDetailDto.getTxtApsOutcomeResult(), CodesConstant.COUTCTYP));

                outcomeMatrixSummaryGroup.setBookmarkDtoList(bookmarkOutcomeMatrixGroupList);
                outcomeMatrixSummaryGroupList.add(outcomeMatrixSummaryGroup);
            }
        }
        formDataGroupList.addAll(outcomeMatrixSummaryGroupList);
    }

    private void populateCareNarrative(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getCareEvents())) {
            for (Long eventId : apsCaseReviewServiceDto.getCareEvents()) {
                FormDataGroupDto careNarrative = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_NARR, BookmarkConstants.EMPTY_STRING);
                List<BlobDataDto> blobDataDtoList = new ArrayList<>();
                blobDataDtoList.add(createBlobData(BookmarkConstants.CARE_NARR,
                        BookmarkConstants.CARE_NARRATIVE, String.valueOf(eventId)));
                careNarrative.setBlobDataDtoList(blobDataDtoList);
                formDataGroupList.add(careNarrative);
            }
        }
    }

    private void populateOutcomeMatrixNarrative(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getOutcomeMatrixEvents())) {
            for (Long eventId : apsCaseReviewServiceDto.getOutcomeMatrixEvents()) {
                FormDataGroupDto careNarrative = createFormDataGroup(FormGroupsConstants.TMPLAT_OUTCOME_NARR, BookmarkConstants.EMPTY_STRING);
                List<BlobDataDto> blobDataDtoList = new ArrayList<>();
                blobDataDtoList.add(createBlobData(BookmarkConstants.OUTCOME_NARR,
                        BookmarkConstants.OUTCOME_MATRIX_NARRATIVE, String.valueOf(eventId)));
                careNarrative.setBlobDataDtoList(blobDataDtoList);
                formDataGroupList.add(careNarrative);
            }
        }
    }

    private void populateServiceDeliveryStageInfo(StageValueBeanDto stageValueBeanDto, List<BookmarkDto> bookmarkGroupList) {
        List<BookmarkDto> bookmarkServiceDeliveryGroupList = new ArrayList<>();
        if (null != stageValueBeanDto.getDtStageStart()) {
            bookmarkServiceDeliveryGroupList.add(createBookmark(BookmarkConstants.DT_SVC_START_START, FormattingHelper.formatDate(stageValueBeanDto.getDtStageStart())));
        }
        bookmarkServiceDeliveryGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_CLOSURE_REASON, stageValueBeanDto.getCdStageReasonClosed(), CodesConstant.CCLSRTYP));
        if (null != stageValueBeanDto.getDtSvcDelvDecision()) {
            bookmarkServiceDeliveryGroupList.add(createBookmark(BookmarkConstants.STAGE_DECISION_DATE, FormattingHelper.formatDate(stageValueBeanDto.getDtSvcDelvDecision())));
        }
        bookmarkGroupList.addAll(bookmarkServiceDeliveryGroupList);
    }

    private void populateServiceDeliveryClosureChecklist(StageValueBeanDto stageValueBeanDto, List<BookmarkDto> bookmarkGroupList) {
        List<BookmarkDto> bookmarkChkGroupList = new ArrayList<>();
        bookmarkChkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_ECS, stageValueBeanDto.getIndEcs(), CodesConstant.CAREDESG));
        bookmarkChkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.IND_ECS_VER, stageValueBeanDto.getIndEcsVer(), CodesConstant.CAREDESG));
        bookmarkChkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CLIENT_ADVISED, stageValueBeanDto.getCdClientAdvised(), CodesConstant.CCLNTNA));
        if (null != stageValueBeanDto.getDtClientAdvised()) {
            bookmarkChkGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED_SVC_DATE, FormattingHelper.formatDate(stageValueBeanDto.getDtClientAdvised())));
        }
        bookmarkChkGroupList.add(createBookmark(BookmarkConstants.STAGE_CLOSURE_COMMENTS, stageValueBeanDto.getStageClosureCmnts()));
        bookmarkGroupList.addAll(bookmarkChkGroupList);
    }

    private void populatePageHeader(ApsCaseReviewDto apsCaseReviewDto, StageValueBeanDto stageValueBeanDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        List<FormDataGroupDto> formDataPageGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkPageGroupList = new ArrayList<>();

        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, apsCaseReviewDto.getCaseNumber()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NAME, stageValueBeanDto.getCaseName()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_LAST, apsCaseReviewDto.getPrimaryLastNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_FIRST, apsCaseReviewDto.getPrimaryFirstNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_WORKER_NAME_MIDDLE, apsCaseReviewDto.getPrimaryMiddleNm()));
        if (StringUtil.isValid(apsCaseReviewDto.getPrimarySuffix())) {
            formDataPageGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA, BookmarkConstants.EMPTY_STRING));
            bookmarkPageGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_WORKER_NAME_SUFFIX, apsCaseReviewDto.getPrimarySuffix(), CodesConstant.CSUFFIX2));
        }
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_LAST, apsCaseReviewDto.getSupervisorLastNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_FIRST, apsCaseReviewDto.getSupervisorfirstNm()));
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_MIDDLE, apsCaseReviewDto.getSupervisorMiddleNm()));
        if (StringUtil.isValid(apsCaseReviewDto.getSupervisorSuffix())) {
            formDataPageGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2, BookmarkConstants.EMPTY_STRING));
            bookmarkPageGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_SUPERVISOR_NAME_SUFFIX, apsCaseReviewDto.getSupervisorSuffix(), CodesConstant.CSUFFIX2));
        }
        bookmarkPageGroupList.add(createBookmark(BookmarkConstants.SUM_CASE_CLOSED, FormattingHelper.formatDate(stageValueBeanDto.getDtCaseClosed())));

        formDataGroupList.addAll(formDataPageGroupList);
        bookmarkGroupList.addAll(bookmarkPageGroupList);
    }

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
            for (String detFactor : apsCaseReviewDto.getCdIncmgDeterm()) {
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


    private void populateInvestigationStageSummary(ApsCaseReviewDto apsCaseReviewDto, ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        List<FormDataGroupDto> formDataInvestigationGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkInvestigationGroupList = new ArrayList<>();

        if (null != apsCaseReviewDto.getDtApsInvstBegun()) {
            bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN, FormattingHelper.formatDate(apsCaseReviewDto.getDtApsInvstBegun())));
        }
        if (null != apsCaseReviewDto.getDtApsInvstCltAssmt()) {
            bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_CLIENT_ASSMT, FormattingHelper.formatDate(apsCaseReviewDto.getDtApsInvstCltAssmt())));
        }
        if (null != apsCaseReviewDto.getDtDtApsInvstCmplt()) {
            bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_INVEST_COMPLETED, FormattingHelper.formatDate(apsCaseReviewDto.getDtDtApsInvstCmplt())));
        }
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_OVERALL_DISP, apsCaseReviewDto.getSzCdApsInvstOvrallDisp(), CodesConstant.CAPSALDP));
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_CLOSURE_REASON, apsCaseReviewServiceDto.getStageValueBeanDto().getCdStageReasonClosed(), CodesConstant.CAINVCLS));
        if (null != apsCaseReviewServiceDto.getStageValueBeanDto().getDtStageClose()) {
            bookmarkInvestigationGroupList.add(createBookmark(BookmarkConstants.SUM_DT_CLOSURE, FormattingHelper.formatDate(apsCaseReviewServiceDto.getStageValueBeanDto().getDtStageClose())));
        }
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_24_HOURS, apsCaseReviewServiceDto.getStageValueBeanDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY));
        bookmarkInvestigationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SUM_FINAL_PRIORITY, apsCaseReviewDto.getSzCdApsInvstFinalPrty(), CodesConstant.CPRIORTY));

        formDataGroupList.addAll(formDataInvestigationGroupList);
        bookmarkGroupList.addAll(bookmarkInvestigationGroupList);
    }

    private void populateInvestigationContacts(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {

        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getContactsList())) {

            List<FormDataGroupDto> initialContactGroupList = new ArrayList<>();
            List<FormDataGroupDto> f2fContactGroupList = new ArrayList<>();
            List<FormDataGroupDto> monthlyContactGroupList = new ArrayList<>();

            for (ApsCaseReviewContactDto contactDto : apsCaseReviewServiceDto.getContactsList()) {
                if (CONTACT_TYPE_C24H.equals(contactDto.getCdContactType())) {
                    FormDataGroupDto initialContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_24_CONTACT, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkInitialContactGroupList = new ArrayList<>();

                    bookmarkInitialContactGroupList.add(createBookmark(BookmarkConstants.DT_ACT_24_CONTACT, FormattingHelper.formatDate(contactDto.getDtEventOccurred())));
                    if (null != contactDto.getDtContactOccurred()) {
                        bookmarkInitialContactGroupList.add(createBookmark(BookmarkConstants.DT_24_CONTACT, FormattingHelper.formatDate(contactDto.getDtContactOccurred())));
                        bookmarkInitialContactGroupList.add(createBookmark(BookmarkConstants.TM_24_CONTACT, FormattingHelper.formatTime(contactDto.getDtContactOccurred())));
                    }
                    initialContactGroup.setBookmarkDtoList(bookmarkInitialContactGroupList);
                    initialContactGroupList.add(initialContactGroup);
                }
            }
            formDataGroupList.addAll(initialContactGroupList);

            for (ApsCaseReviewContactDto contactDto : apsCaseReviewServiceDto.getContactsList()) {
                if (CONTACT_TYPE_CFTF.equals(contactDto.getCdContactType())) {

                    FormDataGroupDto f2fContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FTF, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkF2fContactGroupList = new ArrayList<>();

                    bookmarkF2fContactGroupList.add(createBookmark(BookmarkConstants.DT_ACT_FTF_CONTACT, FormattingHelper.formatDate(contactDto.getDtEventOccurred())));
                    if (null != contactDto.getDtContactOccurred()) {
                        bookmarkF2fContactGroupList.add(createBookmark(BookmarkConstants.DT_FTF_CONTACT, FormattingHelper.formatDate(contactDto.getDtContactOccurred())));
                        bookmarkF2fContactGroupList.add(createBookmark(BookmarkConstants.TM_FTF_CONTACT, FormattingHelper.formatTime(contactDto.getDtContactOccurred())));
                    }
                    f2fContactGroup.setBookmarkDtoList(bookmarkF2fContactGroupList);
                    f2fContactGroupList.add(f2fContactGroup);
                }
            }
            formDataGroupList.addAll(f2fContactGroupList);

            for (ApsCaseReviewContactDto contactDto : apsCaseReviewServiceDto.getContactsList()) {
                if (CONTACT_TYPE_CMST.equals(contactDto.getCdContactType())) {
                    FormDataGroupDto monthlyContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MS_CONTACT, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkMonthlyContactGroupList = new ArrayList<>();

                    bookmarkMonthlyContactGroupList.add(createBookmark(BookmarkConstants.DT_ACT_MS_CONTACT, FormattingHelper.formatDate(contactDto.getDtEventOccurred())));
                    if (null != contactDto.getDtContactOccurred()) {
                        bookmarkMonthlyContactGroupList.add(createBookmark(BookmarkConstants.DT_MS_CONTACT, FormattingHelper.formatDate(contactDto.getDtContactOccurred())));
                        bookmarkMonthlyContactGroupList.add(createBookmark(BookmarkConstants.TM_MS_CONTACT, FormattingHelper.formatTime(contactDto.getDtContactOccurred())));
                    }
                    monthlyContactGroup.setBookmarkDtoList(bookmarkMonthlyContactGroupList);
                    monthlyContactGroupList.add(monthlyContactGroup);
                }
            }
            formDataGroupList.addAll(monthlyContactGroupList);
        }
    }


    /****ALLEGATION DETAIL  INFORMATION */
    private void populateAllegationDetails(List<AllegationDto> allegationDtoList, List<FormDataGroupDto> formDataGroupList) {
        List<FormDataGroupDto> formDataAllegationGroupList = new ArrayList<>();
        if (!allegationDtoList.isEmpty()) {
            for (AllegationDto allegationValueBean : allegationDtoList) {
                FormDataGroupDto allegationGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAllegationGroupList = new ArrayList<>();

                bookmarkAllegationGroupList.add(createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM, allegationValueBean.getNmVictim()));
                bookmarkAllegationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG, allegationValueBean.getCdAllegType(), CodesConstant.CABALTYP));
                bookmarkAllegationGroupList.add(createBookmark(BookmarkConstants.ALLEG_DTL_AP, allegationValueBean.getNmAllegedPerpetrator()));
                bookmarkAllegationGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP, allegationValueBean.getCdAllegDisposition(), CodesConstant.CDISPSTN));

                allegationGroup.setBookmarkDtoList(bookmarkAllegationGroupList);
                formDataAllegationGroupList.add(allegationGroup);
            }
        }
        formDataGroupList.addAll(formDataAllegationGroupList);
    }

    /****PRINCIPAL INFORMATION */
    private void populatePrincipalInformation(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {

        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getPrincipalDtoList())) {
            List<ApsStagePersonDto> principalInfoList = apsCaseReviewServiceDto.getPrincipalDtoList();
            for (ApsStagePersonDto apsStagePersonDto : principalInfoList) {
                FormDataGroupDto principalGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkPrincipalGroupList = new ArrayList<>();

                bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, apsStagePersonDto.getSzNmNameFirst()));
                bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, apsStagePersonDto.getSzNmNameMiddle()));
                bookmarkPrincipalGroupList.add(createBookmark(BookmarkConstants.VICTIM_NAME_LAST, apsStagePersonDto.getSzNmNameLast()));

                if (TypeConvUtil.isValid(apsStagePersonDto.getSzCdNameSuffix())) {
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
                    org.exolab.castor.types.Date birthDate = DateHelper.toCastorDateSafe(FormattingHelper.formatDate(apsStagePersonDto.getDtDtPersonBirth()));
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

    private void populateReporterInformation(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!TypeConvUtil.isNullOrEmpty(apsCaseReviewServiceDto.getReporterDtoList())) {

            List<ApsStagePersonDto> reporterInfoList = apsCaseReviewServiceDto.getReporterDtoList();
            for (ApsStagePersonDto apsStagePersonDto : reporterInfoList) {
                FormDataGroupDto reporterGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkReporterGroupList = new ArrayList<>();

                bookmarkReporterGroupList.add(createBookmark(BookmarkConstants.RPT_NAME_FIRST, apsStagePersonDto.getSzNmNameFirst()));
                bookmarkReporterGroupList.add(createBookmark(BookmarkConstants.RPT_NAME_MIDDLE, apsStagePersonDto.getSzNmNameMiddle()));
                bookmarkReporterGroupList.add(createBookmark(BookmarkConstants.RPT_NAME_LAST, apsStagePersonDto.getSzNmNameLast()));

                if (TypeConvUtil.isValid(apsStagePersonDto.getSzCdNameSuffix())) {
                    FormDataGroupDto suffixCommaGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_D, BookmarkConstants.EMPTY_STRING);
                    List<FormDataGroupDto> formDataSuffixCommaGroupList = new ArrayList<>();
                    formDataSuffixCommaGroupList.add(suffixCommaGroup);
                    reporterGroup.setFormDataGroupList(formDataSuffixCommaGroupList);
                    bookmarkReporterGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.RPT_NAME_SUFFIX, apsStagePersonDto.getSzCdNameSuffix(), CodesConstant.CSUFFIX2));
                }
                bookmarkReporterGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.RPT_RELTNSP, apsStagePersonDto.getSzCdStagePersRelInt(), CodesConstant.CRPTRINT));
                getPersonAddrPhone(bookmarkReporterGroupList, apsStagePersonDto);

                reporterGroup.setBookmarkDtoList(bookmarkReporterGroupList);
                formDataGroupList.add(reporterGroup);
            }
        }


    }

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
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE, apsStagePersonDto.getSzCdPhoneType(), CodesConstant.CPHNTYP));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NUMBER, FormattingHelper.formatPhone(apsStagePersonDto.getNbrPhone())));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION, apsStagePersonDto.getNbrPhoneExtension()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.PHONE_NOTES, apsStagePersonDto.getSzTxtPhoneComments()));
    }

    private void populateCollateralInformation(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!apsCaseReviewServiceDto.getCollateralDtoList().isEmpty()) {
            List<ApsStagePersonDto> collateralDtoList = apsCaseReviewServiceDto.getCollateralDtoList();
            for (ApsStagePersonDto apsStagePersonDto : collateralDtoList) {
                FormDataGroupDto collateralGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERAL, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCollateralGroupList = new ArrayList<>();

                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_FIRST, apsStagePersonDto.getSzNmNameFirst()));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_MIDDLE, apsStagePersonDto.getSzNmNameMiddle()));
                bookmarkCollateralGroupList.add(createBookmark(BookmarkConstants.COL_NAME_LAST, apsStagePersonDto.getSzNmNameLast()));

                if (TypeConvUtil.isValid(apsStagePersonDto.getSzCdNameSuffix())) {
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

    /**
     * Method Name: populateInvestigationConclusionNarrative Method Description: Populates the INVESTIGATION CONCLUSION CHECKLIST
     */
    private void populateInvConclusionCheckList(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkGroupList) {
        ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDto();

        ApsInvstDetail apsInvstDetail = apsCaseReviewServiceDto.getApsInvstDetailList().isEmpty() ? apsCaseReviewServiceDto.getApsInvstDetailList().get(0) : new ApsInvstDetail();

        String closureType = apsCaseReviewDto.getCdClosureType();
        String cdInterpreter = apsCaseReviewDto.getCdInterpreter();

        // Client advised of case closure
        if (CodesConstant.CAPSINVC_CAC.equals(closureType)) {
            FormDataGroupDto cacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CAC, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkCacGroupList = new ArrayList<>();
            bookmarkCacGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED_INV_DATE, FormattingHelper.formatDate(apsCaseReviewDto.getDtDtClientAdvised())));
            cacGroup.setBookmarkDtoList(bookmarkCacGroupList);
            formDataGroupList.add(cacGroup);

        } else if (CodesConstant.CAPSINVC_CNC.equals(closureType)) {
            FormDataGroupDto cncGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNC, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cncGroup);

        }
        // Client participation in service planning
        if (CodesConstant.CAPSINVC_CSP.equals(apsInvstDetail.getCdClosureType())) {

            FormDataGroupDto cspGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CSP, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cspGroup);

        } else if (CodesConstant.CAPSINVC_CNS.equals(apsInvstDetail.getCdClosureType())) {
            FormDataGroupDto cnsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNS, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(cnsGroup);
        }
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.EXT_DOC_IND, apsCaseReviewDto.getBIndExtDoc(), CodesConstant.CAREDESG));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.LEGAL_ACTION_IND, apsCaseReviewDto.getBIndLegalAction(), CodesConstant.CAREDESG));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.FAM_VIOLENCE_IND, apsCaseReviewDto.getBIndFamViolence(), CodesConstant.CAREDESG));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ECS_IND, apsCaseReviewDto.getBIndECS(), CodesConstant.CAREDESG));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CLIENT_IND, apsCaseReviewDto.getBIndClient(), CodesConstant.CAREDESG));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.TXT_CLIENT_OTHER, apsCaseReviewDto.getSzTxtClientOther(), CodesConstant.CAREDESG));

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

    private void populateInvestigationConclusionNarrative(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        ApsCaseReviewDto apsCaseReviewDto = apsCaseReviewServiceDto.getApsCaseReviewDto();
        List<Long> eventIdList = apsCaseReviewDto.getEventIdList();

        for (Long eventId : eventIdList) {
            FormDataGroupDto invCnclsnNarrative = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONCL_NARR, BookmarkConstants.EMPTY_STRING);
            List<BlobDataDto> blobDataDtoList = new ArrayList<>();
            blobDataDtoList.add(createBlobData(BookmarkConstants.INV_CONCL_NARR, BookmarkConstants.APS_INV_NARR, String.valueOf(eventId)));
            invCnclsnNarrative.setBlobDataDtoList(blobDataDtoList);
            formDataGroupList.add(invCnclsnNarrative);
        }
    }

    private void populateECS(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        List<ServiceAuthDto> svcAuthList = apsCaseReviewServiceDto.getSvcAuthEventInfoList();
        if (!TypeConvUtil.isNullOrEmpty(svcAuthList)) {
            for (ServiceAuthDto svcAuthDB : svcAuthList) {
                FormDataGroupDto svcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SERVICE, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSvcGroupList = new ArrayList<>();

                if (null != svcAuthDB.getServiceAuthService()) {
                    bookmarkSvcGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SERVICE, svcAuthDB.getServiceAuthService(), CodesConstant.CSVCCODE));
                }
                if (null != svcAuthDB.getDtApproval()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.APRV_DATE, FormattingHelper.formatDate(svcAuthDB.getDtApproval())));
                }

                if (null != svcAuthDB.getDtServiveAuthDtlBegin()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.BEG_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlBegin())));
                }

                if (null != svcAuthDB.getDtServiveAuthDtlEnd()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.END_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlEnd())));
                }

                if (null != svcAuthDB.getDtServiveAuthDtlTerm()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.TERM_DT, FormattingHelper.formatDate(svcAuthDB.getDtServiveAuthDtlTerm())));
                }
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.AMOUNT, svcAuthDB.getServiceAuthAmount()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.RATE, svcAuthDB.getUnitRate()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.UNITS, svcAuthDB.getUnitReq()));

                if (null != svcAuthDB.getSvcAuthDtlAuthType() && !BookmarkConstants.EMPTY_STRING.equals(svcAuthDB.getSvcAuthDtlAuthType())) {
                    bookmarkSvcGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.AUTH_TYPE, svcAuthDB.getSvcAuthDtlAuthType(), CodesConstant.CSVATYPE));
                }
                svcGroup.setBookmarkDtoList(bookmarkSvcGroupList);
                formDataGroupList.add(svcGroup);
            }
        }
    }

    private void populateDonatedCommunityServices(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        List<ApsCaseReviewServiceAuthDto> donatedCommunityServicesList = apsCaseReviewServiceDto.getDonatedCommunityServicesList();
        if (!TypeConvUtil.isNullOrEmpty(donatedCommunityServicesList)) {
            for (ApsCaseReviewServiceAuthDto serviceAuthDto : donatedCommunityServicesList) {
                FormDataGroupDto svcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DSC_SERVICE, BookmarkConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSvcGroupList = new ArrayList<>();

                if (null != serviceAuthDto.getServiceAuthDateEffective()) {
                    bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.DATE, FormattingHelper.formatDate(serviceAuthDto.getServiceAuthDateEffective())));
                }
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.RSRC_ID, serviceAuthDto.getIdResource()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.RSRC_NAME, serviceAuthDto.getResourceName()));
                bookmarkSvcGroupList.add(createBookmark(BookmarkConstants.EST_VALUE, serviceAuthDto.getServiceAuthAmount()));
                bookmarkSvcGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.SERVICE, serviceAuthDto.getServiceAuthService(), CodesConstant.CSVCCODE));
                svcGroup.setBookmarkDtoList(bookmarkSvcGroupList);
                formDataGroupList.add(svcGroup);
            }
        }
    }

}
