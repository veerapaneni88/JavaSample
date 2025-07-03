package us.tx.state.dfps.service.forms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.apsinvsummary.dto.ApsInvSummaryServiceDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Investigation Summary -- CFIV1200.
 * Dec 14, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsInvSummaryServicePrefillData extends DocumentServiceUtil {

    @Autowired
    private CodesDao codesDao;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsInvSummaryServiceDto apsInvSummaryServiceDto = (ApsInvSummaryServiceDto) parentDtoobj;

        // Initializing null DTOs


        //Prncipal/Victim prefill data
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        List<BlobDataDto> blobList = new ArrayList<>();// Non-group bookmarks
        boolean preSingleStage = checkPreSingleStageByStageId(apsInvSummaryServiceDto.getStageDto());
        PreFillDataServiceDto prefillData = new PreFillDataServiceDto();

        if (preSingleStage) {
            BookmarkDto bookmarkTitle = createBookmark(BookmarkConstants.HTML_TITLE, FormConstants.HTML_TITLE);
            bookmarkNonGroupList.add(bookmarkTitle);
            BookmarkDto bookmarkTitleH1 = createBookmark(BookmarkConstants.H1_TITLE, FormConstants.H1_TITLE);
            bookmarkNonGroupList.add(bookmarkTitleH1);
            BookmarkDto bookmarkDtCmpltdLbl = createBookmark(BookmarkConstants.DATE_COMPLETED_LABEL, FormConstants.DATE_COMPLETED_LABEL);
            bookmarkNonGroupList.add(bookmarkDtCmpltdLbl);
            BookmarkDto bookmarkClChkLstLbl = createBookmark(BookmarkConstants.CLOSURE_CHECKLIST_LABEL, FormConstants.CLOSURE_CHECKLIST_LABEL);
            bookmarkNonGroupList.add(bookmarkClChkLstLbl);
            BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.CLIENT_INFORMED_LABEL, FormConstants.CLIENT_INFORMED_LABEL);
            bookmarkNonGroupList.add(bookmarkTitleCaseName);
            BookmarkDto bookmarkSvsPvdLbl = createBookmark(BookmarkConstants.SERVICES_PROVIDED_LABEL, FormConstants.SERVICES_PROVIDED_LABEL);
            bookmarkNonGroupList.add(bookmarkSvsPvdLbl);
            BookmarkDto bookmarkExtrDocsLbl = createBookmark(BookmarkConstants.EXTERNAL_DOCS_LABEL, FormConstants.EXTERNAL_DOCS_LABEL);
            bookmarkNonGroupList.add(bookmarkExtrDocsLbl);
            BookmarkDto bookmarFmlyVolLbl = createBookmark(BookmarkConstants.FAMILY_VIOLENCE_LABEL, FormConstants.FAMILY_VIOLENCE_LABEL);
            bookmarkNonGroupList.add(bookmarFmlyVolLbl);
            BookmarkDto bookmarkFundsLbl = createBookmark(BookmarkConstants.FUNDS_LABEL, FormConstants.FUNDS_LABEL);
            bookmarkNonGroupList.add(bookmarkFundsLbl);
            BookmarkDto bookmarkFundsExpldLbl = createBookmark(BookmarkConstants.FUNDS_EXPLORED_LABEL, FormConstants.FUNDS_EXPLORED_LABEL);
            bookmarkNonGroupList.add(bookmarkFundsExpldLbl);
        } else {
            BookmarkDto bookmarkTitle = createBookmark(BookmarkConstants.HTML_TITLE, FormConstants.HTML_TITLECL);
            bookmarkNonGroupList.add(bookmarkTitle);
            BookmarkDto bookmarkTitleH1 = createBookmark(BookmarkConstants.H1_TITLE, FormConstants.H1_TITLECL);
            bookmarkNonGroupList.add(bookmarkTitleH1);
            BookmarkDto bookmarkDtCmpltdLbl = createBookmark(BookmarkConstants.DATE_COMPLETED_LABEL, FormConstants.DATE_COMPLETED_LABELCL);
            bookmarkNonGroupList.add(bookmarkDtCmpltdLbl);
            BookmarkDto bookmarkClChkLstLbl = createBookmark(BookmarkConstants.CLOSURE_CHECKLIST_LABEL, FormConstants.CLOSURE_CHECKLIST_LABELCL);
            bookmarkNonGroupList.add(bookmarkClChkLstLbl);
            BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.CLIENT_INFORMED_LABEL, FormConstants.CLIENT_INFORMED_LABELCL);
            bookmarkNonGroupList.add(bookmarkTitleCaseName);
            BookmarkDto bookmarkSvsPvdLbl = createBookmark(BookmarkConstants.SERVICES_PROVIDED_LABEL, FormConstants.SERVICES_PROVIDED_LABELCL);
            bookmarkNonGroupList.add(bookmarkSvsPvdLbl);
            BookmarkDto bookmarkExtrDocsLbl = createBookmark(BookmarkConstants.EXTERNAL_DOCS_LABEL, FormConstants.EXTERNAL_DOCS_LABELCL);
            bookmarkNonGroupList.add(bookmarkExtrDocsLbl);
            BookmarkDto bookmarFmlyVolLbl = createBookmark(BookmarkConstants.FAMILY_VIOLENCE_LABEL, FormConstants.FAMILY_VIOLENCE_LABELCL);
            bookmarkNonGroupList.add(bookmarFmlyVolLbl);
            BookmarkDto bookmarkFundsLbl = createBookmark(BookmarkConstants.FUNDS_LABEL, FormConstants.FUNDS_LABELCL);
            bookmarkNonGroupList.add(bookmarkFundsLbl);
            BookmarkDto bookmarkFundsExpldLbl = createBookmark(BookmarkConstants.FUNDS_EXPLORED_LABEL, FormConstants.FUNDS_EXPLORED_LABELCL);
            bookmarkNonGroupList.add(bookmarkFundsExpldLbl);
        }

        // CSEC02D
        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                apsInvSummaryServiceDto.getGenericCaseInfoDto().getNmCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);
        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                apsInvSummaryServiceDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);
        BookmarkDto bookmarkSumPrty24hrs = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_24_HOURS,
                apsInvSummaryServiceDto.getGenericCaseInfoDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
        bookmarkNonGroupList.add(bookmarkSumPrty24hrs);
        BookmarkDto bookmarkSumPrtyIntake = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_INTAKE,
                apsInvSummaryServiceDto.getGenericCaseInfoDto().getCdStageInitialPriority(), CodesConstant.CPRIORTY);
        bookmarkNonGroupList.add(bookmarkSumPrtyIntake);
        BookmarkDto bookmarkSumReccomAction = createBookmarkWithCodesTable(BookmarkConstants.SUM_CLOSURE_REASON,
                apsInvSummaryServiceDto.getGenericCaseInfoDto().getCdStageReasonClosed(), CodesConstant.CAINVCLS);
        bookmarkNonGroupList.add(bookmarkSumReccomAction);

        //Summary Data
        // CSEC01 - Worker
        BookmarkDto bookmarkSumWrkrNameSuffix = createBookmarkWithCodesTable(
                BookmarkConstants.SUM_WORKER_NAME_SUFFIX, apsInvSummaryServiceDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
        bookmarkNonGroupList.add(bookmarkSumWrkrNameSuffix);
        BookmarkDto bookmarkWrkrFullNm = createBookmark(BookmarkConstants.WORKER_NAME,
                getFullName(apsInvSummaryServiceDto.getEmployeePersPhNameDto().getNmNameFirst(),
                        apsInvSummaryServiceDto.getEmployeePersPhNameDto().getNmNameMiddle(), apsInvSummaryServiceDto.getEmployeePersPhNameDto().getNmNameLast(), apsInvSummaryServiceDto.getEmployeePersPhNameDto().getCdNameSuffix()));
        bookmarkNonGroupList.add(bookmarkWrkrFullNm);

        BookmarkDto bookmarkSupFullNm = createBookmark(BookmarkConstants.SUPERVISOR_NAME,
                getFullName(apsInvSummaryServiceDto.getSupNameDetailDto().getNmNameLast(), apsInvSummaryServiceDto.getSupNameDetailDto().getNmNameFirst(),
                        apsInvSummaryServiceDto.getSupNameDetailDto().getNmNameMiddle(), apsInvSummaryServiceDto.getSupNameDetailDto().getCdNameSuffix()));
        bookmarkNonGroupList.add(bookmarkSupFullNm);

        BookmarkDto bookmarkSumOfficeAddrLn1 = createBookmark(BookmarkConstants.OFFICE_LINE1,
                apsInvSummaryServiceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
        bookmarkNonGroupList.add(bookmarkSumOfficeAddrLn1);
        BookmarkDto bookmarkSumOfficeAddrLn2 = createBookmark(BookmarkConstants.OFFICE_LINE2,
                apsInvSummaryServiceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
        bookmarkNonGroupList.add(bookmarkSumOfficeAddrLn2);
        BookmarkDto bookmarkSumOfficeAddrCity = createBookmark(BookmarkConstants.OFFICE_CITY,
                apsInvSummaryServiceDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
        bookmarkNonGroupList.add(bookmarkSumOfficeAddrCity);
        BookmarkDto bookmarkSumOfficeAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.OFFICE_COUNTY,
                apsInvSummaryServiceDto.getEmployeePersPhNameDto().getAddrMailCodeCounty(), CodesConstant.CCOUNT);
        bookmarkNonGroupList.add(bookmarkSumOfficeAddrCounty);
        BookmarkDto bookmarkSumOfficeAddrZip = createBookmark(BookmarkConstants.OFFICE_ZIP,
                apsInvSummaryServiceDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
        bookmarkNonGroupList.add(bookmarkSumOfficeAddrZip);


        //ApsInvestigationDetails 	CINV44D
        if (!TypeConvUtil.isNullOrEmpty(apsInvSummaryServiceDto.getApsInvstDetail())) {
            if (CodesConstant.CAPSINVC_CAC.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType())) {
                FormDataGroupDto formGroupDtoClosureCac = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CAC,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(formGroupDtoClosureCac);
            } else if (CodesConstant.CAPSINVC_CNC.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType())) {
                FormDataGroupDto formGroupDtoClosureCnc = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNC,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(formGroupDtoClosureCnc);
            }

            if (CodesConstant.CAPSINVC_CSP.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType()) ||
                    FormConstants.Y.equals(apsInvSummaryServiceDto.getApsInvstDetail().getIndSvcPlan())) {
                FormDataGroupDto tmpltProvideSrvcGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CSP,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(tmpltProvideSrvcGroupDto);
            } else if (CodesConstant.CAPSINVC_CSP.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType()) ||
                    !FormConstants.Y.equals(apsInvSummaryServiceDto.getApsInvstDetail().getIndSvcPlan())) {
                FormDataGroupDto tmpltProvideSrvcGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_CLOSURE_CNS,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(tmpltProvideSrvcGroupDto);
            }
            if (!preSingleStage && null != apsInvSummaryServiceDto.getApsInvstDetail().getTxtNotPrtcptSvcPln()) {
                FormDataGroupDto tmpltNotPartcpSrvcGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NONPARTCP,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkNotPartcpSrvc = new ArrayList<>();
                BookmarkDto bookmarkDtCmpltdLbl = createBookmark(BookmarkConstants.NONPARTCP_LABEL, FormConstants.NARRATIVE);
                bookmarkNotPartcpSrvc.add(bookmarkDtCmpltdLbl);
                BookmarkDto bookmarkTxtNotSvcPln = createBookmark(BookmarkConstants.NONPARTCP_PLN, apsInvSummaryServiceDto.getApsInvstDetail().getTxtNotPrtcptSvcPln());
                bookmarkNotPartcpSrvc.add(bookmarkTxtNotSvcPln);
                tmpltNotPartcpSrvcGroupDto.setBookmarkDtoList(bookmarkNotPartcpSrvc);
                formDataGroupList.add(tmpltNotPartcpSrvcGroupDto);
            }

            // post single stage TMPLAT_SELFNEGLECT artf192843
            if (!preSingleStage) {
                FormDataGroupDto tmpltSelfNeglectGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SELFNEGLECT,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkSelfNeglectSrvc = new ArrayList<>();
                BookmarkDto bookmarkDtCmpltdLbl = createBookmark(BookmarkConstants.SELFNEGLECT_LABEL, FormConstants.SELFNEGLECT_LABEL_TEXT);
                bookmarkSelfNeglectSrvc.add(bookmarkDtCmpltdLbl);
                BookmarkDto bookmarkAllegfingLbl = createBookmark(BookmarkConstants.SELFNEGLECT_ALLEGFINDING_LABEL, FormConstants.SELFNEGLECT_ALLEGFINDING_LABEL_TEXT);
                bookmarkSelfNeglectSrvc.add(bookmarkAllegfingLbl);
                BookmarkDto bookmarkAllegfndDesc = createBookmark(BookmarkConstants.SELFNEGLECT_ALLEGFINDING_DESC, apsInvSummaryServiceDto.getApsInvstDetail().getTxtAllegFinding());
                bookmarkSelfNeglectSrvc.add(bookmarkAllegfndDesc);
                BookmarkDto bookmarkRootCauseLbl = createBookmark(BookmarkConstants.SELFNEGLECT_ROOTCAUSE_LABEL, FormConstants.SELFNEGLECT_ROOTCAUSE_LABEL_TEXT);
                bookmarkSelfNeglectSrvc.add(bookmarkRootCauseLbl);
                BookmarkDto bookmarkrootCauseDesc = createBookmark(BookmarkConstants.SELFNEGLECT_ROOTCAUSE_DESC, apsInvSummaryServiceDto.getApsInvstDetail().getTxtRootCause());
                bookmarkSelfNeglectSrvc.add(bookmarkrootCauseDesc);
                tmpltSelfNeglectGroupDto.setBookmarkDtoList(bookmarkSelfNeglectSrvc);
                formDataGroupList.add(tmpltSelfNeglectGroupDto);

            }

            // post single stage TMPLAT_CONCLJUSTFN artf192843
            if (!preSingleStage) {
                FormDataGroupDto conclJustfnGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONCLJUSTFN,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkConcluJustFnList = new ArrayList<>();
                BookmarkDto bookmarkConcluJstfnLbl = createBookmark(BookmarkConstants.CONCLJUSTFN_LABEL, FormConstants.CONCLJUSTFN_LABEL_TEXT);
                bookmarkConcluJustFnList.add(bookmarkConcluJstfnLbl);
                BookmarkDto bookmarkConcluJstfnDescAllgLbl = createBookmark(BookmarkConstants.CONCLJUSTFN_DESCALLG_LABEL, FormConstants.CONCLJUSTFN_DESCALLG_LABEL_TEXT);
                bookmarkConcluJustFnList.add(bookmarkConcluJstfnDescAllgLbl);
                BookmarkDto bookmarkDisAllegfndDesc = createBookmark(BookmarkConstants.CONCLJUSTFN_DESCALLG_DESC, apsInvSummaryServiceDto.getApsInvstDetail().getTxtDescAllg());
                bookmarkConcluJustFnList.add(bookmarkDisAllegfndDesc);
                BookmarkDto bookmarkJtfnAnlyEvieLbl = createBookmark(BookmarkConstants.CONCLJUSTFN_ANLYSEVIDENCE_LABEL, FormConstants.CONCLJUSTFN_ANLYSEVIDENCE_LABEL_TEXT);
                bookmarkConcluJustFnList.add(bookmarkJtfnAnlyEvieLbl);
                BookmarkDto bookmarkkJtfnAnlyEvieDesc = createBookmark(BookmarkConstants.CONCLJUSTFN_ANLYSEVIDENCE_DESC, apsInvSummaryServiceDto.getApsInvstDetail().getTxtAnlysEvidance());
                bookmarkConcluJustFnList.add(bookmarkkJtfnAnlyEvieDesc);
                BookmarkDto bookmarkJtfnPrepStmtLbl = createBookmark(BookmarkConstants.CONCLJUSTFN_PREPONDRNCSTMNT_LABEL, FormConstants.CONCLJUSTFN_PREPONDRNCSTMNT_LABEL_TEXT);
                bookmarkConcluJustFnList.add(bookmarkJtfnPrepStmtLbl);
                BookmarkDto bookmarkkJtfnPrepStmtDesc = createBookmark(BookmarkConstants.CONCLJUSTFN_PREPONDRNCSTMNT_DESC, apsInvSummaryServiceDto.getApsInvstDetail().getTxtPrepondrncStmnt());
                bookmarkConcluJustFnList.add(bookmarkkJtfnPrepStmtDesc);
                conclJustfnGroup.setBookmarkDtoList(bookmarkConcluJustFnList);
                formDataGroupList.add(conclJustfnGroup);

            }

            if (!preSingleStage) {
                FormDataGroupDto indPcsSvcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FUNDS_RECEIVED,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkIndPcsSvcList = new ArrayList<>();
                BookmarkDto bookmarkIndPcs = createBookmarkWithCodesTable(BookmarkConstants.PCS_IND,
                        apsInvSummaryServiceDto.getApsInvstDetail().getIndPcsSvcs(), CodesConstant.CINVACAN);
                bookmarkIndPcsSvcList.add(bookmarkIndPcs);
                indPcsSvcGroup.setBookmarkDtoList(bookmarkIndPcsSvcList);
                formDataGroupList.add(indPcsSvcGroup);
            }

            BookmarkDto bookmarkClntInd = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_IND,
                    apsInvSummaryServiceDto.getApsInvstDetail().getIndClient(), CodesConstant.CAREDESG);
            bookmarkNonGroupList.add(bookmarkClntInd);
            if (FormConstants.Y.equals(apsInvSummaryServiceDto.getApsInvstDetail().getIndClient())) {
                FormDataGroupDto othClntGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CLIENT_OTHER,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkOthClntList = new ArrayList<>();
                BookmarkDto bookmarkIndPcs = createBookmark(BookmarkConstants.TXT_CLIENT_OTHER,
                        apsInvSummaryServiceDto.getApsInvstDetail().getTxtClientOther());
                bookmarkOthClntList.add(bookmarkIndPcs);
                othClntGroup.setBookmarkDtoList(bookmarkOthClntList);
                formDataGroupList.add(othClntGroup);
            }

            if (null != apsInvSummaryServiceDto.getApsInvstDetail().getCdInterpreter()) {
                if (CodesConstant.CAPSINVC_CNI.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType())) {
                    FormDataGroupDto interCniGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_INTER_CNI,
                            FormConstants.EMPTY_STRING);
                    List<BookmarkDto> bookmarkMethodCommList = new ArrayList<>();
                    BookmarkDto bookmarkIndPcs = createBookmark(BookmarkConstants.TXT_METHOD_COMM,
                            apsInvSummaryServiceDto.getApsInvstDetail().getTxtMethodComm());
                    bookmarkMethodCommList.add(bookmarkIndPcs);
                    interCniGroup.setBookmarkDtoList(bookmarkMethodCommList);
                    formDataGroupList.add(interCniGroup);
                } else if (CodesConstant.CAPSINVC_CIP.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType())) {
                    FormDataGroupDto interCniGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CD_INTER_CIP,
                            FormConstants.EMPTY_STRING);
                    formDataGroupList.add(interCniGroup);
                }
            }

            BookmarkDto bookmarkEcsInd = createBookmarkWithCodesTable(BookmarkConstants.ECS_IND,
                    apsInvSummaryServiceDto.getApsInvstDetail().getIndEcs(), CodesConstant.CINVACAN);
            bookmarkNonGroupList.add(bookmarkEcsInd);
            BookmarkDto bookmarkExtDocInd = createBookmarkWithCodesTable(BookmarkConstants.EXT_DOC_IND,
                    apsInvSummaryServiceDto.getApsInvstDetail().getIndExtDoc(), CodesConstant.CINVACAN);
            bookmarkNonGroupList.add(bookmarkExtDocInd);
            BookmarkDto bookmarkFmlyVolInd = createBookmarkWithCodesTable(BookmarkConstants.FAM_VIOLENCE_IND,
                    apsInvSummaryServiceDto.getApsInvstDetail().getIndFamViolence(), CodesConstant.CINVACAN);
            bookmarkNonGroupList.add(bookmarkFmlyVolInd);
            BookmarkDto bookmarkLeglActInd = createBookmarkWithCodesTable(BookmarkConstants.LEGAL_ACTION_IND,
                    apsInvSummaryServiceDto.getApsInvstDetail().getIndLegalAction(), CodesConstant.CINVACAN);
            bookmarkNonGroupList.add(bookmarkLeglActInd);
            if (null != apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstBegun()) {
                BookmarkDto bookmarkSumDtInvestBegun = createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN,
                        DateUtils.stringDt(apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstBegun()));
                bookmarkNonGroupList.add(bookmarkSumDtInvestBegun);
            }
            if (null != apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstCltAssmt()) {
                BookmarkDto bookmarkSuDtAsmtCmpltd = createBookmark(BookmarkConstants.SUM_DT_ASSMT_COMPLETED,
                        DateUtils.stringDt(apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstCltAssmt()));
                bookmarkNonGroupList.add(bookmarkSuDtAsmtCmpltd);
            }
            if (null != apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstCmplt()) {
                BookmarkDto bookmarkSuDtInstCmpltd = createBookmark(BookmarkConstants.SUM_DT_INVEST_COMPLETED,
                        DateUtils.stringDt(apsInvSummaryServiceDto.getApsInvstDetail().getDtApsInvstCmplt()));
                bookmarkNonGroupList.add(bookmarkSuDtInstCmpltd);
            }
            BookmarkDto bookmarkSumPrtFnl = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_FINAL,
                    apsInvSummaryServiceDto.getApsInvstDetail().getCdApsInvstFinalPrty(), CodesConstant.CPRIORTY);
            bookmarkNonGroupList.add(bookmarkSumPrtFnl);
            BookmarkDto bookmarkTxtAltComm = createBookmark(BookmarkConstants.TXT_ALT_COMM,
                    apsInvSummaryServiceDto.getApsInvstDetail().getTxtAltComm());
            bookmarkNonGroupList.add(bookmarkTxtAltComm);
            BookmarkDto bookmarkTxtClntOth = createBookmark(BookmarkConstants.TXT_CLIENT_OTHER,
                    apsInvSummaryServiceDto.getApsInvstDetail().getTxtClientOther());
            bookmarkNonGroupList.add(bookmarkTxtClntOth);
            BookmarkDto bookmarkTxtTrnsNameRlt = createBookmark(BookmarkConstants.TXT_TRNS_NAME_RLT,
                    apsInvSummaryServiceDto.getApsInvstDetail().getTxtTrnsNameRlt());
            bookmarkNonGroupList.add(bookmarkTxtTrnsNameRlt);
            BookmarkDto bookmarkSumAllDisp = createBookmarkWithCodesTable(BookmarkConstants.SUM_OVERALL_DISP,
                    apsInvSummaryServiceDto.getApsInvstDetail().getCdApsInvstOvrallDisp(), CodesConstant.CDISPSTN);
            bookmarkNonGroupList.add(bookmarkSumAllDisp);
            BookmarkDto bookmarkApsInvNarr = createBookmarkWithCodesTable(BookmarkConstants.APS_INV_NARRATIVE,
                    Long.toString(apsInvSummaryServiceDto.getApsInvstDetail().getIdEvent()), CodesConstant.APS_INV_NARR);
            bookmarkNonGroupList.add(bookmarkApsInvNarr);

            // client advised date
            FormDataGroupDto tmpltAdvisedGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADVISED,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkClAdviceList = new ArrayList<>();
            if (CodesConstant.CAPSINVC_CAC.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType()) &&
                    null != apsInvSummaryServiceDto.getApsInvstDetail().getDtClientAdvised()) {
                BookmarkDto bookmarkDtClntAdvsdLbl = createBookmark(BookmarkConstants.ADVISED_LABEL,
                        FormConstants.DATE_CLIENT_ADVISED);
                bookmarkClAdviceList.add(bookmarkDtClntAdvsdLbl);
                BookmarkDto bookmarkClntAdvDt = createBookmark(BookmarkConstants.ADVISED_DATE,
                        DateUtils.stringDt(apsInvSummaryServiceDto.getApsInvstDetail().getDtClientAdvised()));
                bookmarkClAdviceList.add(bookmarkClntAdvDt);
            } else if (CodesConstant.CAPSINVC_CNC.equals(apsInvSummaryServiceDto.getApsInvstDetail().getCdClosureType()) &&
                    !preSingleStage) {
                BookmarkDto bookmarkDtClntAdvsdLbl = createBookmark(BookmarkConstants.ADVISED_LABEL,
                        FormConstants.NARRATIVE);
                bookmarkClAdviceList.add(bookmarkDtClntAdvsdLbl);
                BookmarkDto bookmarkClntAdvDt = createBookmark(BookmarkConstants.ADVISED_NOT,
                        apsInvSummaryServiceDto.getApsInvstDetail().getTxtNotAdviCaseClosure());
                bookmarkClAdviceList.add(bookmarkClntAdvDt);
            }

            tmpltAdvisedGroupDto.setBookmarkDtoList(bookmarkClAdviceList);
            formDataGroupList.add(tmpltAdvisedGroupDto);

            BlobDataDto blobArContactsNarr = createBlobData(BookmarkConstants.APS_INV_NARRATIVE,
                    BookmarkConstants.APS_INV_NARR, apsInvSummaryServiceDto.getApsInvstDetail().getIdEvent().toString());
            blobList.add(blobArContactsNarr);
        } else if (null != apsInvSummaryServiceDto.getStageDto().getDtStageStart()) {
            BookmarkDto bookmarkSumDtInvestBegun = createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN,
                    DateUtils.stringDt(apsInvSummaryServiceDto.getStageDto().getDtStageStart()));
            bookmarkNonGroupList.add(bookmarkSumDtInvestBegun);
        }


        if (null != apsInvSummaryServiceDto.getAllegationWithVicList()) {
            for (AllegationWithVicDto allegationDto : apsInvSummaryServiceDto.getAllegationWithVicList()) {
                FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
                        FormConstants.EMPTY_STRING);
                List<FormDataGroupDto> allegationGroupDtoList = new ArrayList<>();
                List<BookmarkDto> bookmarkAllegationList = new ArrayList<>();
                BookmarkDto bookmarkAllegDtlDisp = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
                        allegationDto.getaCdAllegDisposition(), CodesConstant.CDISPSTN);
                bookmarkAllegationList.add(bookmarkAllegDtlDisp);
                BookmarkDto bookmarkAllegDtlAlleg = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
                        allegationDto.getaCdAllegType(), CodesConstant.CABALTYP);
                bookmarkAllegationList.add(bookmarkAllegDtlAlleg);
                BookmarkDto bookmarkAllegDtlFatality = createBookmark(BookmarkConstants.ALLEG_DTL_FATALITY,
                        allegationDto.getaIndFatality());
                bookmarkAllegationList.add(bookmarkAllegDtlFatality);
                BookmarkDto bookmarkAllegDtlAp = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
                        allegationDto.getcNmPersonFull());

                bookmarkAllegationList.add(bookmarkAllegDtlAp);
                BookmarkDto bookmarkAllegDtlVictim = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
                        allegationDto.getB_nmPersonFull());
                bookmarkAllegationList.add(bookmarkAllegDtlVictim);
                // sub group cfiv1013
                if (!ObjectUtils.isEmpty(allegationDto.getbDtPersonDeath())) {
                    FormDataGroupDto algDodGroupDto = createFormDataGroup(
                            FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITH_DOD, FormGroupsConstants.TMPLAT_ALLEGATION);
                    List<BookmarkDto> bookmarkAlgDodList = new ArrayList<>();
                    BookmarkDto bookmarkAlgChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
                            allegationDto.getaIndFatality());
                    bookmarkAlgDodList.add(bookmarkAlgChildFatality);

                    algDodGroupDto.setBookmarkDtoList(bookmarkAlgDodList);
                    allegationGroupDtoList.add(algDodGroupDto);
                }

                // sub group cfiv1014
                else {
                    FormDataGroupDto algNoDodGroupDto = createFormDataGroup(
                            FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITHOUT_DOD,
                            FormGroupsConstants.TMPLAT_ALLEGATION);
                    allegationGroupDtoList.add(algNoDodGroupDto);
                }

                allegationGroupDto.setBookmarkDtoList(bookmarkAllegationList);
                allegationGroupDto.setFormDataGroupList(allegationGroupDtoList);
                formDataGroupList.add(allegationGroupDto);
            }
        }


        //Principal information
        if (apsInvSummaryServiceDto.getCaseInfoPrincipalList().size() > ServiceConstants.Zero) {
            for (CaseInfoDto caseInfoDtoprincipalType : apsInvSummaryServiceDto.getCaseInfoPrincipalList()) {
                FormDataGroupDto tempPrincipalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCaseInfoDtoprincipalList = new ArrayList<>();
                BookmarkDto bookmarkIndPersonDobApprox = createBookmark(BookmarkConstants.PRN_DOBAPPROX,
                        caseInfoDtoprincipalType.getIndPersonDobApprox());
                bookmarkCaseInfoDtoprincipalList.add(bookmarkIndPersonDobApprox);
                BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.PRN_SEX,
                        caseInfoDtoprincipalType.getCdPersonSex(), CodesConstant.CSEX);
                bookmarkCaseInfoDtoprincipalList.add(bookmarkCdPersonSex);
                BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.PRN_DOB,
                        DateUtils.stringDt(caseInfoDtoprincipalType.getDtPersonBirth()));
                bookmarkCaseInfoDtoprincipalList.add(bookmarkDtPersonBirth);
                BookmarkDto bookmarkNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PRN_SUFFIX,
                        caseInfoDtoprincipalType.getCdNameSuffix(), CodesConstant.CSUFFIX2);
                bookmarkCaseInfoDtoprincipalList.add(bookmarkNameSuffix);
                BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.PRN_TYPE,
                        caseInfoDtoprincipalType.getCdStagePersType(), CodesConstant.CPRSNTYP);
                bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersRelInt);
                BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.PRN_ROLE,
                        caseInfoDtoprincipalType.getCdStagePersRole(), CodesConstant.CROLEALL);
                bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersRole);
                BookmarkDto bookmarkCdStagePersType = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELINT,
                        caseInfoDtoprincipalType.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
                bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersType);
                BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.PRN_FIRST,
                        caseInfoDtoprincipalType.getNmNameFirst());
                bookmarkCaseInfoDtoprincipalList.add(bookmarkNameFirst);
                BookmarkDto bookmarkNameLast = createBookmark(BookmarkConstants.PRN_LAST,
                        caseInfoDtoprincipalType.getNmNameLast());
                bookmarkCaseInfoDtoprincipalList.add(bookmarkNameLast);
                BookmarkDto bookmarkNameMiddle = createBookmark(BookmarkConstants.PRN_MIDDLE,
                        caseInfoDtoprincipalType.getNmNameMiddle());
                bookmarkCaseInfoDtoprincipalList.add(bookmarkNameMiddle);

                tempPrincipalFrmDataGrpDto.setBookmarkDtoList(bookmarkCaseInfoDtoprincipalList);
                List<FormDataGroupDto> principalFrmDataGrpFrmDataGrpList = new ArrayList<>();
                if (FormConstants.EMPTY_STRING
                        .equals(caseInfoDtoprincipalType.getCdNameSuffix())) {
                    FormDataGroupDto tempCommaChildFrmDataGrpDtoWorker = createFormDataGroup(
                            FormGroupsConstants.TMPLAT_PRN_COMMA, FormGroupsConstants.TMPLAT_PRN);
                    principalFrmDataGrpFrmDataGrpList.add(tempCommaChildFrmDataGrpDtoWorker);
                }
                tempPrincipalFrmDataGrpDto.setBookmarkDtoList(bookmarkCaseInfoDtoprincipalList);
                tempPrincipalFrmDataGrpDto.setFormDataGroupList(principalFrmDataGrpFrmDataGrpList);
                formDataGroupList.add(tempPrincipalFrmDataGrpDto);
            }
        }

        // Collateral Details
        // set the prefill data for group cfiv1307

    if(!CollectionUtils.isEmpty(apsInvSummaryServiceDto.getCaseInfoCollateralList())){
        for (CaseInfoDto caseInfoDtoCollateralType : apsInvSummaryServiceDto.getCaseInfoCollateralList()) {
            FormDataGroupDto tempCollateralFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkCaseInfoDtoCollateralList = new ArrayList<>();
            BookmarkDto bookmarkIndPersonDobApprox = createBookmark(BookmarkConstants.COL_DOBAPPROX,
                    caseInfoDtoCollateralType.getIndPersonDobApprox());
            bookmarkCaseInfoDtoCollateralList.add(bookmarkIndPersonDobApprox);
            BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.COL_SEX,
                    caseInfoDtoCollateralType.getCdPersonSex(), CodesConstant.CSEX);
            bookmarkCaseInfoDtoCollateralList.add(bookmarkCdPersonSex);
            BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.COL_DOB,
                    DateUtils.stringDt(caseInfoDtoCollateralType.getDtPersonBirth()));
            bookmarkCaseInfoDtoCollateralList.add(bookmarkDtPersonBirth);
            BookmarkDto bookmarkCdNameSuffixc = createBookmarkWithCodesTable(
                    BookmarkConstants.COL_SUFFIX, caseInfoDtoCollateralType.getCdNameSuffix(),
                    CodesConstant.CSUFFIX2);
            bookmarkCaseInfoDtoCollateralList.add(bookmarkCdNameSuffixc);
            BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(
                    BookmarkConstants.COL_RELINT, caseInfoDtoCollateralType.getCdStagePersRelInt(),
                    CodesConstant.CRPTRINT);
            bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersRelInt);
            BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.COL_ROLE,
                    caseInfoDtoCollateralType.getCdStagePersRole(), CodesConstant.CROLEALL);
            bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersRole);
            BookmarkDto bookmarkCdStagePersType = createBookmarkWithCodesTable(BookmarkConstants.COL_TYPE,
                    caseInfoDtoCollateralType.getCdStagePersType(), CodesConstant.CPRSNTYP);
            bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersType);
            BookmarkDto bookmarkNmNameFirstc = createBookmark(BookmarkConstants.COL_FIRST,
                    caseInfoDtoCollateralType.getNmNameFirst());
            bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameFirstc);
            BookmarkDto bookmarkNmNameLastc = createBookmark(BookmarkConstants.COL_LAST,
                    caseInfoDtoCollateralType.getNmNameLast());
            bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameLastc);
            BookmarkDto bookmarkNmNameMiddlec = createBookmark(BookmarkConstants.COL_MIDDLE,
                    caseInfoDtoCollateralType.getNmNameMiddle());
            bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameMiddlec);

            List<FormDataGroupDto> collateralFrmDataGrpFrmDataGrpList = new ArrayList<>();
            if (FormConstants.EMPTY_STRING
                    .equals(caseInfoDtoCollateralType.getCdNameSuffix())) {
                FormDataGroupDto tempCommaChildFrmDataGrpDtoSupv = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
                        FormGroupsConstants.TMPLAT_COL);
                collateralFrmDataGrpFrmDataGrpList.add(tempCommaChildFrmDataGrpDtoSupv);
            }

            tempCollateralFrmDataGrpDto.setFormDataGroupList(collateralFrmDataGrpFrmDataGrpList);
            tempCollateralFrmDataGrpDto.setBookmarkDtoList(bookmarkCaseInfoDtoCollateralList);
            formDataGroupList.add(tempCollateralFrmDataGrpDto);
        }
    }


            prefillData.setBookmarkDtoList(bookmarkNonGroupList);
            prefillData.setFormDataGroupList(formDataGroupList);
            prefillData.setBlobDataDtoList(blobList);
            return prefillData;
        }

    public String getFullName(String lastName, String firstName, String middleName, String suffix) {
        StringBuilder fullName = new StringBuilder();
        if (!ObjectUtils.isEmpty(lastName)) {
            fullName.append(lastName);
            fullName.append(ServiceConstants.COMMA);
        }
        if (!ObjectUtils.isEmpty(firstName)) {
            fullName.append(firstName);
            fullName.append(ServiceConstants.SPACE);
        }
        if (!ObjectUtils.isEmpty(middleName)) {
            fullName.append(middleName);
        }

        if (!ObjectUtils.isEmpty(suffix)) {
            fullName.append(CodesConstant.CSUFFIX2);
        }

        return fullName.toString();

    }

    /**
     * This operation will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
     * If the stage start date is prior to the APS Release Date, this operation will return true, else false.
     * @param stageDto
     * @return
     */
    public boolean checkPreSingleStageByStageId(StageDto stageDto) {
        boolean preSingleStage = false;
        Date defualtDt = codesDao.getAppRelDate(ServiceConstants.CRELDATE_NOV_2020_APS);
        if (!ObjectUtils.isEmpty(stageDto) && !ObjectUtils.isEmpty(stageDto.getDtStageStart())){
                    preSingleStage = DateUtils.isBefore(stageDto.getDtStageStart(),defualtDt);
            }
        return preSingleStage;
    }

}
