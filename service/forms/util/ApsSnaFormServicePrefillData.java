package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.apssna.dto.*;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION Class Description:
 * APS Strengths and Needs Reassessment  -- APSSNA.
 * July 07, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsSnaFormServicePrefillData extends DocumentServiceUtil{

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        ApsSnaFormServiceDto apsSnaFormServiceDto = (ApsSnaFormServiceDto) parentDtoobj;
        PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();

        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.ID_CASE,
                apsSnaFormServiceDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.NM_CASE,
                apsSnaFormServiceDto.getGenericCaseInfoDto().getNmStage());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);

        if(!TypeConvUtil.isNullOrEmpty(apsSnaFormServiceDto.getApsSna())) {
            if (CodesConstant.CSDMASMT_INIT.equals(apsSnaFormServiceDto.getApsSna().getCdAssmtType())) {
                BookmarkDto bookmarkTitle = createBookmark(BookmarkConstants.PRINT_TITLE, FormConstants.SNA_TITLE);
                bookmarkNonGroupList.add(bookmarkTitle);
                BookmarkDto bookmarkTitleH1 = createBookmark(BookmarkConstants.HTML_TITLE, FormConstants.SNA_TITLE);
                bookmarkNonGroupList.add(bookmarkTitleH1);
            } else {
                BookmarkDto bookmarkREASESS = createBookmark(BookmarkConstants.PRINT_TITLE, FormConstants.SNA_REASESS_TITLE);
                bookmarkNonGroupList.add(bookmarkREASESS);
                BookmarkDto bookmarkREASESSH1 = createBookmark(BookmarkConstants.HTML_TITLE, FormConstants.SNA_REASESS_TITLE);
                bookmarkNonGroupList.add(bookmarkREASESSH1);
            }
        }
        if (!TypeConvUtil.isNullOrEmpty(apsSnaFormServiceDto.getApsSnaResponsesList())) {
            for (ApsSnaFormResponseDTO apsSnaResponse : apsSnaFormServiceDto.getApsSnaResponsesList()) {
                Long responseCodeAnswer = null;
                String cdSnaDomiin = apsSnaResponse.getCdSNADomain();
                if (!TypeConvUtil.isNullOrEmpty(apsSnaResponse.getApsSNAAnswerLkpId())) {
                    responseCodeAnswer = apsSnaResponse.getApsSNAAnswerLkpId();
                }
                BookmarkDto bookmarkDomainCodeBmk = createBookmark(cdSnaDomiin, cdSnaDomiin);
                bookmarkNonGroupList.add(bookmarkDomainCodeBmk);
                BookmarkDto bookmarkDomainCode = createBookmark(cdSnaDomiin + BookmarkConstants._TEXT, apsSnaResponse.getTxtDomain());
                bookmarkNonGroupList.add(bookmarkDomainCode);
                if (!TypeConvUtil.isNullOrEmpty(apsSnaFormServiceDto.getApsSnaAnswerLookupList())) {
                    List<ApsSnaAnswerDto> apsSnaAnswerLookup = apsSnaFormServiceDto.getApsSnaAnswerLookupList().stream().filter(ans -> ans.getApsSNADomainLkpId() == apsSnaResponse.getApsSNADomainLkpId()).collect(Collectors.toList());
                    for (ApsSnaAnswerDto apsSnaAnswer : apsSnaAnswerLookup) {
                        if (!TypeConvUtil.isNullOrEmpty(apsSnaAnswer.getApsSNAAnswerLkpId()) && !TypeConvUtil.isNullOrEmpty(responseCodeAnswer)) {
                            if (responseCodeAnswer.equals(apsSnaAnswer.getApsSNAAnswerLkpId())) {
                                BookmarkDto ansChecked = createBookmark(apsSnaAnswer.getAnswerCode() + BookmarkConstants._BOX, BookmarkConstants.X);
                                bookmarkNonGroupList.add(ansChecked);
                            } else {
                                FormDataGroupDto ansGroup = createFormDataGroup(BookmarkConstants.TMPLAT_ + apsSnaAnswer.getAnswerCode(), BookmarkConstants.EMPTY_STRING);
                                formDataGroupList.add(ansGroup);

                            }
                        }
                        BookmarkDto ansCode = createBookmark(apsSnaAnswer.getAnswerCode(), apsSnaAnswer.getCodeValue());
                        bookmarkNonGroupList.add(ansCode);
                        BookmarkDto ansText = createBookmark(apsSnaAnswer.getAnswerCode() + BookmarkConstants._TEXT, apsSnaAnswer.getAnswerText());
                        bookmarkNonGroupList.add(ansText);
                    }
                }
                if (apsSnaResponse.getCdSNADomain().equals(BookmarkConstants.CL12) && null != apsSnaResponse.getTxtOtherDescription()) {
                    BookmarkDto bmkCL12 = createBookmark(apsSnaResponse.getCdSNADomain() + BookmarkConstants._DESCRIPTION, apsSnaResponse.getTxtOtherDescription());
                    bookmarkNonGroupList.add(bmkCL12);
                }
                if (apsSnaResponse.getCdSNADomain().equals(BookmarkConstants.PC7) && null != apsSnaResponse.getTxtOtherDescription()) {
                    BookmarkDto bmkPC7 = createBookmark(apsSnaResponse.getCdSNADomain() + BookmarkConstants._DESCRIPTION, apsSnaResponse.getTxtOtherDescription());
                    bookmarkNonGroupList.add(bmkPC7);
                }
            }
        }


        //Map CareTake Info
        populateSNA(apsSnaFormServiceDto, formDataGroupList);
        getCareTaker(apsSnaFormServiceDto, bookmarkNonGroupList,formDataGroupList);
        prefillData.setBookmarkDtoList(bookmarkNonGroupList);
        prefillData.setFormDataGroupList(formDataGroupList);
        return prefillData;
    }

    /**
     * method to get CareTaker info
     * @param apsSnaFormServiceDto
     * @param bookmarkNonGroupList
     * @param formDataGroupList
     */
    private void getCareTaker(ApsSnaFormServiceDto apsSnaFormServiceDto, List<BookmarkDto> bookmarkNonGroupList, List<FormDataGroupDto> formDataGroupList) {
        if(apsSnaFormServiceDto.isPrimaryCaretakerNA()) {
            BookmarkDto bookmarkPrimaryCTNm = createBookmark(BookmarkConstants.NM_PRIMARY_CARETAKER,
                    apsSnaFormServiceDto.getPrimaryCaretakerName());
            bookmarkNonGroupList.add(bookmarkPrimaryCTNm);
        }else{
            FormDataGroupDto ctGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_NA_CARETAKER_SPACE, BookmarkConstants.EMPTY_STRING);
            List<BookmarkDto> naCareTaker = new ArrayList<>();
            BookmarkDto bookmarknaCareTaker = createBookmark(BookmarkConstants.NA_CARETAKER, BookmarkConstants.X);
            naCareTaker.add(bookmarknaCareTaker);
            ctGroup.setBookmarkDtoList(naCareTaker);
            formDataGroupList.add(ctGroup);
        }
    }

    /**
     * Method Name: populateSNA Method Description: Populates the STRENGTHS AND NEEDS ASSESSMENTS
     */
    private void populateSNA(ApsSnaFormServiceDto apsCaseReviewServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!apsCaseReviewServiceDto.getStrengthsAssessedA().isEmpty()) {
            List<ApsSnaResponseDto> strengthsAssessedA = apsCaseReviewServiceDto.getStrengthsAssessedA();
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
                formDataGroupList.add(strengthGroup);
            }
        }

        if (!apsCaseReviewServiceDto.getStrengthsAssessedB().isEmpty()) {
            boolean spChecked = false;
            List<ApsSnaResponseDto> moderateNeedList = apsCaseReviewServiceDto.getStrengthsAssessedB();
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
                    bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.INCLUDE_IN_SP, BookmarkConstants.YES));
                } else {
                    bookmarkModerateGroupList.add(createBookmark(BookmarkConstants.INCLUDE_IN_SP, BookmarkConstants.NO));
                }
                moderateGroup.setBookmarkDtoList(bookmarkModerateGroupList);
                formDataGroupList.add(moderateGroup);
            }
        }

        if (!apsCaseReviewServiceDto.getStrengthsAssessedC().isEmpty()) {
            List<ApsSnaResponseDto> significantNeedList = apsCaseReviewServiceDto.getStrengthsAssessedC();
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
                formDataGroupList.add(needGroup);
            }
        }
    
    }

}
