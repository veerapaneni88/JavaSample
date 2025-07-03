package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareCategoryDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFactorDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFormsDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFormsNarrativeServiceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ApsCareFormsNarrativeServicePrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        ApsCareFormsNarrativeServiceDto serviceDto = (ApsCareFormsNarrativeServiceDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<>();

        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                serviceDto.getCapsCaseDto().getIdCase());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumber);

        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_NM_CASE,
                serviceDto.getCapsCaseDto().getNmCase());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseName);

        BookmarkDto bookmarkTitleWrkrName = createBookmark(BookmarkConstants.TITLE_WORKER_NAME,
                serviceDto.getPerson().getNmPersonFull());
        bookmarkNonFrmGrpList.add(bookmarkTitleWrkrName);

        FormDataGroupDto tmpltResYesGroupData = createFormDataGroup(FormGroupsConstants.TMPLAT_LIFE_RESPONSE_YES,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkTempltResYesList = new ArrayList<>();
        setTmpltResYesGroupDataList(serviceDto, bookmarkTempltResYesList);
        tmpltResYesGroupData.setBookmarkDtoList(bookmarkTempltResYesList);
        formDataGroupList.add(tmpltResYesGroupData);

        // Prefill data for TMPLAT_CARE_DOMAIN_L
        for (ApsCareFormsDto apsCareFormsDto : serviceDto.getApsCareFormsDtos()) {

            //Template Care Domian Living Conditions Data
            setTemplateCareDomianLData(formDataGroupList, apsCareFormsDto);

            // Template Care Domian Physical/Medical Status Data
            setTemplateCareDomianPData(formDataGroupList, apsCareFormsDto);

            // Template Care Domian Mental Status Data
            setTemplateCareDomianMData(formDataGroupList, apsCareFormsDto);

            // Template Care Domian Financial Status Data
            setTemplateCareDomianFData(formDataGroupList, apsCareFormsDto);

            // Template Care Domian Social Interaction and Support Data
            setTemplateCareDomainSData(formDataGroupList, apsCareFormsDto);

        }


        List<BlobDataDto> blobDataDtoList = new ArrayList<>();
        BlobDataDto blobF2fNarr = createBlobData(BookmarkConstants.NARRATIVE,
                BookmarkConstants.CARE_NARRATIVE, serviceDto.getIdEvent().toString());
        blobDataDtoList.add(blobF2fNarr);

        PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
        prefillData.setBlobDataDtoList(blobDataDtoList);
        prefillData.setFormDataGroupList(formDataGroupList);
        prefillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return prefillData;
    }

    /**
     * method to set tmpltResYesGroupDataList
     * @param serviceDto
     * @param bookmarkTempltResYesList
     */
    private void setTmpltResYesGroupDataList(ApsCareFormsNarrativeServiceDto serviceDto, List<BookmarkDto> bookmarkTempltResYesList) {
        if (!TypeConvUtil.isNullOrEmpty(serviceDto.getCare())) {
            if (BookmarkConstants.Y.equals(serviceDto.getCare().getCdLifeThreatResponse())) {
                BookmarkDto bookmarkCdResYesLbl = createBookmark(BookmarkConstants.CD_RESPONSE_YES, BookmarkConstants.YES);
                bookmarkTempltResYesList.add(bookmarkCdResYesLbl);
                BookmarkDto bookmarkCdResNoLbl = createBookmark(BookmarkConstants.CD_RESPONSE_NO, BookmarkConstants.NO);
                bookmarkTempltResYesList.add(bookmarkCdResNoLbl);
            } else {
                BookmarkDto bookmarkCdResYesLbl = createBookmark(BookmarkConstants.CD_RESPONSE_YES, BookmarkConstants.NO);
                bookmarkTempltResYesList.add(bookmarkCdResYesLbl);
                BookmarkDto bookmarkCdResNoLbl = createBookmark(BookmarkConstants.CD_RESPONSE_NO, BookmarkConstants.YES);
                bookmarkTempltResYesList.add(bookmarkCdResNoLbl);
            }

            BookmarkDto bookmarkDesLfTrtCmmt = createBookmark(BookmarkConstants.DESC_LIFE_THREAT_COMMENT, serviceDto.getCare().getDescLifeThreatComment());
            bookmarkTempltResYesList.add(bookmarkDesLfTrtCmmt);
            BookmarkDto bookmarkDesLfTrtActns = createBookmark(BookmarkConstants.DESC_LIFE_THREAT_ACTIONS, serviceDto.getCare().getDescLifeThreatActions());
            bookmarkTempltResYesList.add(bookmarkDesLfTrtActns);
            BookmarkDto bookmarkCdResNoLbl = createBookmark(BookmarkConstants.CD_RESPONSE_NO, BookmarkConstants.YES);
            bookmarkTempltResYesList.add(bookmarkCdResNoLbl);
        }
    }

    /**
     * method Template Care Domian Living Conditions Data
     * @param formDataGroupList
     * @param apsCareFormsDto
     */
    private void setTemplateCareDomianLData(List<FormDataGroupDto> formDataGroupList, ApsCareFormsDto apsCareFormsDto) {
        if (apsCareFormsDto.getCdCareDomain().equalsIgnoreCase(CodesConstant.CARE_DOMAIN_L)) {

            FormDataGroupDto tmpltCareDomainLDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_DOMAIN_L,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainTmpltList = new ArrayList<>();

            // bookmarks for TMPLAT_CARE_DOMAIN_L
            List<BookmarkDto> bookmarkCareDomainLList = new ArrayList<>();
            if (!TypeConvUtil.isNullOrEmpty(apsCareFormsDto.getCdAllegationFocus())) {
                BookmarkDto bookmarkDomainL = createBookmark(BookmarkConstants.TXT_DOMAIN_L, apsCareFormsDto.getTxtDomain());
                bookmarkCareDomainLList.add(bookmarkDomainL);
                BookmarkDto bookmarkDomainFcsL = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEGATION_FOCUS_L, apsCareFormsDto.getCdAllegationFocus(), CodesConstant.CAREDESG);
                bookmarkCareDomainLList.add(bookmarkDomainFcsL);
            }
            if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainLList)) {
                tmpltCareDomainLDto.setBookmarkDtoList(bookmarkCareDomainLList);
            }
            // end of bookmarks for TMPLAT_CARE_DOMAIN_L

            // form template for TMPLAT_A2_A1_L
            FormDataGroupDto tmpltCareDomainA2A1Dto = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_A1_L,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainA2GroupList = new ArrayList<>();

            for (ApsCareCategoryDto apsCareCategoryDto : apsCareFormsDto.getApsCareCategoryDtoList()) {
                FormDataGroupDto tmpltA2L = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_L,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareDomainList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDto.getCdReasonBelieve())) {
                    BookmarkDto bookmarkTxtCtryCmmtL = createBookmark(BookmarkConstants.TXT_CATEGORY_L, apsCareCategoryDto.getTxtCategory());
                    bookmarkCareDomainList.add(bookmarkTxtCtryCmmtL);
                    BookmarkDto bookmarkRsnBlfL = createBookmarkWithCodesTable(BookmarkConstants.CD_REASON_BELIEVE_L, apsCareCategoryDto.getCdReasonBelieve(), CodesConstant.CAREDESG);
                    bookmarkCareDomainList.add(bookmarkRsnBlfL);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainList)) {
                    tmpltA2L.setBookmarkDtoList(bookmarkCareDomainList);
                    formDataCareDomainA2GroupList.add(tmpltA2L);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainA2GroupList)) {
                tmpltCareDomainA2A1Dto.setFormDataGroupList(formDataCareDomainA2GroupList);
                formDataCareDomainTmpltList.add(tmpltCareDomainA2A1Dto);
            }
            // end  form template for TMPLAT_A2_A1_L

            // form template for TMPLAT_DETAILED_DISP_L
            FormDataGroupDto tmpltDtlDispL = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_DISP_L,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainDtlAssmtList = new ArrayList<>();

            for (ApsCareFactorDto apsCareFactorDto : apsCareFormsDto.getApsCareFactorDtoList()) {
                FormDataGroupDto tmpltDtlAssmtL = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_ASSESSMENT_L,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDtlAssmtLList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDto.getCdCareFactorResponse())) {
                    BookmarkDto bookmarkTxtFctrL = createBookmark(BookmarkConstants.TXT_FACTOR_L, apsCareFactorDto.getTxtFactor());
                    bookmarkDtlAssmtLList.add(bookmarkTxtFctrL);
                    BookmarkDto bookmarkCrFctrResL = createBookmarkWithCodesTable(BookmarkConstants.CD_CARE_FACTOR_RESPONSE_L, apsCareFactorDto.getCdCareFactorResponse(), CodesConstant.CAREPROB);
                    bookmarkDtlAssmtLList.add(bookmarkCrFctrResL);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkDtlAssmtLList)) {
                    tmpltDtlAssmtL.setBookmarkDtoList(bookmarkDtlAssmtLList);
                    formDataCareDomainDtlAssmtList.add(tmpltDtlAssmtL);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainDtlAssmtList)) {
                tmpltDtlDispL.setFormDataGroupList(formDataCareDomainDtlAssmtList);
                formDataCareDomainTmpltList.add(tmpltDtlDispL);
            }
            // end form template for TMPLAT_DETAILED_DISP_L
            tmpltCareDomainLDto.setFormDataGroupList(formDataCareDomainTmpltList);
            formDataGroupList.add(tmpltCareDomainLDto);
        }
    }

    /**
     * method Template Care Domian Physical/Medical Status Data
     * @param formDataGroupList
     * @param apsCareFormsDto
     */
    private void setTemplateCareDomianPData(List<FormDataGroupDto> formDataGroupList, ApsCareFormsDto apsCareFormsDto) {
        if (apsCareFormsDto.getCdCareDomain().equalsIgnoreCase(CodesConstant.CARE_DOMAIN_P)) {

            FormDataGroupDto tmpltCareDomainPDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_DOMAIN_P,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainTmpltList = new ArrayList<>();

            // bookmarks for TMPLAT_CARE_DOMAIN_P
            List<BookmarkDto> bookmarkCareDomainPList = new ArrayList<>();
            if (!TypeConvUtil.isNullOrEmpty(apsCareFormsDto.getCdAllegationFocus())) {
                BookmarkDto bookmarkDomainP = createBookmark(BookmarkConstants.TXT_DOMAIN_P, apsCareFormsDto.getTxtDomain());
                bookmarkCareDomainPList.add(bookmarkDomainP);
                BookmarkDto bookmarkDomainFcsP = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEGATION_FOCUS_P, apsCareFormsDto.getCdAllegationFocus(), CodesConstant.CAREDESG);
                bookmarkCareDomainPList.add(bookmarkDomainFcsP);
            }
            if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainPList)) {
                tmpltCareDomainPDto.setBookmarkDtoList(bookmarkCareDomainPList);
            }
            // end of bookmarks for TMPLAT_CARE_DOMAIN_P

            // form template for TMPLAT_A2_A1_P
            FormDataGroupDto tmpltCareDomainA2A1Dto = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_A1_P,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainA2GroupList = new ArrayList<>();

            for (ApsCareCategoryDto apsCareCategoryDto : apsCareFormsDto.getApsCareCategoryDtoList()) {
                FormDataGroupDto tmpltA2P = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_P,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareDomainList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDto.getCdReasonBelieve())) {
                    BookmarkDto bookmarkTxtCtryCmmtP = createBookmark(BookmarkConstants.TXT_CATEGORY_P, apsCareCategoryDto.getTxtCategory());
                    bookmarkCareDomainList.add(bookmarkTxtCtryCmmtP);
                    BookmarkDto bookmarkRsnBlfP = createBookmarkWithCodesTable(BookmarkConstants.CD_REASON_BELIEVE_P, apsCareCategoryDto.getCdReasonBelieve(), CodesConstant.CAREDESG);
                    bookmarkCareDomainList.add(bookmarkRsnBlfP);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainList)) {
                    tmpltA2P.setBookmarkDtoList(bookmarkCareDomainList);
                    formDataCareDomainA2GroupList.add(tmpltA2P);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainA2GroupList)) {
                tmpltCareDomainA2A1Dto.setFormDataGroupList(formDataCareDomainA2GroupList);
                formDataCareDomainTmpltList.add(tmpltCareDomainA2A1Dto);
            }
            // end  form template for TMPLAT_A2_A1_P

            // form template for TMPLAT_DETAILED_DISP_P
            FormDataGroupDto tmpltDtlDispP = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_DISP_P,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainDtlAssmtList = new ArrayList<>();

            for (ApsCareFactorDto apsCareFactorDto : apsCareFormsDto.getApsCareFactorDtoList()) {
                FormDataGroupDto tmpltDtlAssmtP = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_ASSESSMENT_P,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDtlAssmtPList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDto.getCdCareFactorResponse())) {
                    BookmarkDto bookmarkTxtFctrP = createBookmark(BookmarkConstants.TXT_FACTOR_P, apsCareFactorDto.getTxtFactor());
                    bookmarkDtlAssmtPList.add(bookmarkTxtFctrP);
                    BookmarkDto bookmarkCrFctrResP = createBookmarkWithCodesTable(BookmarkConstants.CD_CARE_FACTOR_RESPONSE_P, apsCareFactorDto.getCdCareFactorResponse(), CodesConstant.CAREPROB);
                    bookmarkDtlAssmtPList.add(bookmarkCrFctrResP);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkDtlAssmtPList)) {
                    tmpltDtlAssmtP.setBookmarkDtoList(bookmarkDtlAssmtPList);
                    formDataCareDomainDtlAssmtList.add(tmpltDtlAssmtP);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainDtlAssmtList)) {
                tmpltDtlDispP.setFormDataGroupList(formDataCareDomainDtlAssmtList);
                formDataCareDomainTmpltList.add(tmpltDtlDispP);
            }
            // end form template for TMPLAT_DETAILED_DISP_P
            tmpltCareDomainPDto.setFormDataGroupList(formDataCareDomainTmpltList);
            formDataGroupList.add(tmpltCareDomainPDto);
        }
    }

    /**
     * method Template Care Domian Mental Status Data
     * @param formDataGroupList
     * @param apsCareFormsDto
     */
    private void setTemplateCareDomianMData(List<FormDataGroupDto> formDataGroupList, ApsCareFormsDto apsCareFormsDto) {
        if (apsCareFormsDto.getCdCareDomain().equalsIgnoreCase(CodesConstant.CARE_DOMAIN_M)) {

            FormDataGroupDto tmpltCareDomainMDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_DOMAIN_M,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainTmpltList = new ArrayList<>();

            // bookmarks for TMPLAT_CARE_DOMAIN_M
            List<BookmarkDto> bookmarkCareDomainMList = new ArrayList<>();
            if (!TypeConvUtil.isNullOrEmpty(apsCareFormsDto.getCdAllegationFocus())) {
                BookmarkDto bookmarkDomainM = createBookmark(BookmarkConstants.TXT_DOMAIN_M, apsCareFormsDto.getTxtDomain());
                bookmarkCareDomainMList.add(bookmarkDomainM);
                BookmarkDto bookmarkDomainFcsM = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEGATION_FOCUS_M, apsCareFormsDto.getCdAllegationFocus(), CodesConstant.CAREDESG);
                bookmarkCareDomainMList.add(bookmarkDomainFcsM);
            }
            if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainMList)) {
                tmpltCareDomainMDto.setBookmarkDtoList(bookmarkCareDomainMList);
            }
            // end of bookmarks for TMPLAT_CARE_DOMAIN_M

            // form template for TMPLAT_A2_A1_M
            FormDataGroupDto tmpltCareDomainA2A1Dto = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_A1_M,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainA2GroupList = new ArrayList<>();

            for (ApsCareCategoryDto apsCareCategoryDto : apsCareFormsDto.getApsCareCategoryDtoList()) {
                FormDataGroupDto tmpltA2M = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_M,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareDomainList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDto.getCdReasonBelieve())) {
                    BookmarkDto bookmarkTxtCtryCmmtM = createBookmark(BookmarkConstants.TXT_CATEGORY_M, apsCareCategoryDto.getTxtCategory());
                    bookmarkCareDomainList.add(bookmarkTxtCtryCmmtM);
                    BookmarkDto bookmarkRsnBlfM = createBookmarkWithCodesTable(BookmarkConstants.CD_REASON_BELIEVE_M, apsCareCategoryDto.getCdReasonBelieve(), CodesConstant.CAREDESG);
                    bookmarkCareDomainList.add(bookmarkRsnBlfM);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainList)) {
                    tmpltA2M.setBookmarkDtoList(bookmarkCareDomainList);
                    formDataCareDomainA2GroupList.add(tmpltA2M);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainA2GroupList)) {
                tmpltCareDomainA2A1Dto.setFormDataGroupList(formDataCareDomainA2GroupList);
                formDataCareDomainTmpltList.add(tmpltCareDomainA2A1Dto);
            }
            // end  form template for TMPLAT_A2_A1_M

            // form template for TMPLAT_DETAILED_DISP_M
            FormDataGroupDto tmpltDtlDispM = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_DISP_M,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainDtlAssmtList = new ArrayList<>();

            for (ApsCareFactorDto apsCareFactorDto : apsCareFormsDto.getApsCareFactorDtoList()) {
                FormDataGroupDto tmpltDtlAssmtM = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_ASSESSMENT_M,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDtlAssmtMList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDto.getCdCareFactorResponse())) {
                    BookmarkDto bookmarkTxtFctrM = createBookmark(BookmarkConstants.TXT_FACTOR_M, apsCareFactorDto.getTxtFactor());
                    bookmarkDtlAssmtMList.add(bookmarkTxtFctrM);
                    BookmarkDto bookmarkCrFctrResM = createBookmarkWithCodesTable(BookmarkConstants.CD_CARE_FACTOR_RESPONSE_M, apsCareFactorDto.getCdCareFactorResponse(), CodesConstant.CAREPROB);
                    bookmarkDtlAssmtMList.add(bookmarkCrFctrResM);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkDtlAssmtMList)) {
                    tmpltDtlAssmtM.setBookmarkDtoList(bookmarkDtlAssmtMList);
                    formDataCareDomainDtlAssmtList.add(tmpltDtlAssmtM);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainDtlAssmtList)) {
                tmpltDtlDispM.setFormDataGroupList(formDataCareDomainDtlAssmtList);
                formDataCareDomainTmpltList.add(tmpltDtlDispM);
            }
            // end form template for TMPLAT_DETAILED_DISP_M
            tmpltCareDomainMDto.setFormDataGroupList(formDataCareDomainTmpltList);
            formDataGroupList.add(tmpltCareDomainMDto);
        }
    }

    /**
     * method Template Care Domian Financial Status Data
     * @param formDataGroupList
     * @param apsCareFormsDto
     */
    private void setTemplateCareDomianFData(List<FormDataGroupDto> formDataGroupList, ApsCareFormsDto apsCareFormsDto) {
        if (apsCareFormsDto.getCdCareDomain().equalsIgnoreCase(CodesConstant.CARE_DOMAIN_F)) {

            FormDataGroupDto tmpltCareDomainFDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_DOMAIN_F,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainTmpltList = new ArrayList<>();

            // bookmarks for TMPLAT_CARE_DOMAIN_F
            List<BookmarkDto> bookmarkCareDomainFList = new ArrayList<>();
            if (!TypeConvUtil.isNullOrEmpty(apsCareFormsDto.getCdAllegationFocus())) {
                BookmarkDto bookmarkDomainF = createBookmark(BookmarkConstants.TXT_DOMAIN_F, apsCareFormsDto.getTxtDomain());
                bookmarkCareDomainFList.add(bookmarkDomainF);
                BookmarkDto bookmarkDomainFcsF = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEGATION_FOCUS_F, apsCareFormsDto.getCdAllegationFocus(), CodesConstant.CAREDESG);
                bookmarkCareDomainFList.add(bookmarkDomainFcsF);
            }
            if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainFList)) {
                tmpltCareDomainFDto.setBookmarkDtoList(bookmarkCareDomainFList);
            }
            // end of bookmarks for TMPLAT_CARE_DOMAIN_F

            // form template for TMPLAT_A2_A1_F
            FormDataGroupDto tmpltCareDomainA2A1Dto = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_A1_F,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainA2GroupList = new ArrayList<>();

            for (ApsCareCategoryDto apsCareCategoryDto : apsCareFormsDto.getApsCareCategoryDtoList()) {
                FormDataGroupDto tmpltA2F = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_F,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareDomainList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDto.getCdReasonBelieve())) {
                    BookmarkDto bookmarkTxtCtryCmmtF = createBookmark(BookmarkConstants.TXT_CATEGORY_F, apsCareCategoryDto.getTxtCategory());
                    bookmarkCareDomainList.add(bookmarkTxtCtryCmmtF);
                    BookmarkDto bookmarkRsnBlfF = createBookmarkWithCodesTable(BookmarkConstants.CD_REASON_BELIEVE_F, apsCareCategoryDto.getCdReasonBelieve(), CodesConstant.CAREDESG);
                    bookmarkCareDomainList.add(bookmarkRsnBlfF);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainList)) {
                    tmpltA2F.setBookmarkDtoList(bookmarkCareDomainList);
                    formDataCareDomainA2GroupList.add(tmpltA2F);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainA2GroupList)) {
                tmpltCareDomainA2A1Dto.setFormDataGroupList(formDataCareDomainA2GroupList);
                formDataCareDomainTmpltList.add(tmpltCareDomainA2A1Dto);
            }
            // end  form template for TMPLAT_A2_A1_F

            // form template for TMPLAT_DETAILED_DISP_F
            FormDataGroupDto tmpltDtlDispF = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_DISP_F,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainDtlAssmtList = new ArrayList<>();

            for (ApsCareFactorDto apsCareFactorDto : apsCareFormsDto.getApsCareFactorDtoList()) {
                FormDataGroupDto tmpltDtlAssmtF = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_ASSESSMENT_F,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDtlAssmtFList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDto.getCdCareFactorResponse())) {
                    BookmarkDto bookmarkTxtFctrF = createBookmark(BookmarkConstants.TXT_FACTOR_F, apsCareFactorDto.getTxtFactor());
                    bookmarkDtlAssmtFList.add(bookmarkTxtFctrF);
                    BookmarkDto bookmarkCrFctrResF = createBookmarkWithCodesTable(BookmarkConstants.CD_CARE_FACTOR_RESPONSE_F, apsCareFactorDto.getCdCareFactorResponse(), CodesConstant.CAREPROB);
                    bookmarkDtlAssmtFList.add(bookmarkCrFctrResF);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkDtlAssmtFList)) {
                    tmpltDtlAssmtF.setBookmarkDtoList(bookmarkDtlAssmtFList);
                    formDataCareDomainDtlAssmtList.add(tmpltDtlAssmtF);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainDtlAssmtList)) {
                tmpltDtlDispF.setFormDataGroupList(formDataCareDomainDtlAssmtList);
                formDataCareDomainTmpltList.add(tmpltDtlDispF);
            }
            // end form template for TMPLAT_DETAILED_DISP_F
            tmpltCareDomainFDto.setFormDataGroupList(formDataCareDomainTmpltList);
            formDataGroupList.add(tmpltCareDomainFDto);
        }
    }

    /**
     * method Template Care Domian Social Interaction and Support Data
     * @param formDataGroupList
     * @param apsCareFormsDto
     */
    private void setTemplateCareDomainSData(List<FormDataGroupDto> formDataGroupList, ApsCareFormsDto apsCareFormsDto) {
        if (apsCareFormsDto.getCdCareDomain().equalsIgnoreCase(CodesConstant.CARE_DOMAIN_S)) {
            FormDataGroupDto tmpltCareDomainSDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_DOMAIN_S,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainTmpltList = new ArrayList<>();

            // bookmarks for TMPLAT_CARE_DOMAIN_S
            List<BookmarkDto> bookmarkCareDomainSList = new ArrayList<>();
            if (!TypeConvUtil.isNullOrEmpty(apsCareFormsDto.getCdAllegationFocus())) {
                BookmarkDto bookmarkDomainS = createBookmark(BookmarkConstants.TXT_DOMAIN_S, apsCareFormsDto.getTxtDomain());
                bookmarkCareDomainSList.add(bookmarkDomainS);
                BookmarkDto bookmarkDomainFcsS = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEGATION_FOCUS_S, apsCareFormsDto.getCdAllegationFocus(), CodesConstant.CAREDESG);
                bookmarkCareDomainSList.add(bookmarkDomainFcsS);
            }
            if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainSList)) {
                tmpltCareDomainSDto.setBookmarkDtoList(bookmarkCareDomainSList);
            }
            // end of bookmarks for TMPLAT_CARE_DOMAIN_S

            // form template for TMPLAT_A2_A1_S
            FormDataGroupDto tmpltCareDomainA2A1Dto = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_A1_S,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainA2GroupList = new ArrayList<>();

            for (ApsCareCategoryDto apsCareCategoryDto : apsCareFormsDto.getApsCareCategoryDtoList()) {
                FormDataGroupDto tmpltA2S = createFormDataGroup(FormGroupsConstants.TMPLAT_A2_S,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkCareDomainList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDto.getCdReasonBelieve())) {
                    BookmarkDto bookmarkTxtCtryCmmtS = createBookmark(BookmarkConstants.TXT_CATEGORY_S, apsCareCategoryDto.getTxtCategory());
                    bookmarkCareDomainList.add(bookmarkTxtCtryCmmtS);
                    BookmarkDto bookmarkRsnBlfS = createBookmarkWithCodesTable(BookmarkConstants.CD_REASON_BELIEVE_S, apsCareCategoryDto.getCdReasonBelieve(), CodesConstant.CAREDESG);
                    bookmarkCareDomainList.add(bookmarkRsnBlfS);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkCareDomainList)) {
                    tmpltA2S.setBookmarkDtoList(bookmarkCareDomainList);
                    formDataCareDomainA2GroupList.add(tmpltA2S);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainA2GroupList)) {
                tmpltCareDomainA2A1Dto.setFormDataGroupList(formDataCareDomainA2GroupList);
                formDataCareDomainTmpltList.add(tmpltCareDomainA2A1Dto);
            }
            // end  form template for TMPLAT_A2_A1_S

            // form template for TMPLAT_DETAILED_DISP_S
            FormDataGroupDto tmpltDtlDispS = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_DISP_S,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> formDataCareDomainDtlAssmtList = new ArrayList<>();

            for (ApsCareFactorDto apsCareFactorDto : apsCareFormsDto.getApsCareFactorDtoList()) {
                FormDataGroupDto tmpltDtlAssmtS = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAILED_ASSESSMENT_S,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkDtlAssmtSList = new ArrayList<>();
                if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDto.getCdCareFactorResponse())) {
                    BookmarkDto bookmarkTxtFctrS = createBookmark(BookmarkConstants.TXT_FACTOR_S, apsCareFactorDto.getTxtFactor());
                    bookmarkDtlAssmtSList.add(bookmarkTxtFctrS);
                    BookmarkDto bookmarkCrFctrResS = createBookmarkWithCodesTable(BookmarkConstants.CD_CARE_FACTOR_RESPONSE_S, apsCareFactorDto.getCdCareFactorResponse(), CodesConstant.CAREPROB);
                    bookmarkDtlAssmtSList.add(bookmarkCrFctrResS);
                }
                if (!TypeConvUtil.isNullOrEmpty(bookmarkDtlAssmtSList)) {
                    tmpltDtlAssmtS.setBookmarkDtoList(bookmarkDtlAssmtSList);
                    formDataCareDomainDtlAssmtList.add(tmpltDtlAssmtS);
                }
            }
            if (!TypeConvUtil.isNullOrEmpty(formDataCareDomainDtlAssmtList)) {
                tmpltDtlDispS.setFormDataGroupList(formDataCareDomainDtlAssmtList);
                formDataCareDomainTmpltList.add(tmpltDtlDispS);
            }
            // end form template for TMPLAT_DETAILED_DISP_S
            tmpltCareDomainSDto.setFormDataGroupList(formDataCareDomainTmpltList);
            formDataGroupList.add(tmpltCareDomainSDto);
        }
    }
}
