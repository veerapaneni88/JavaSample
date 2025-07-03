package us.tx.state.dfps.service.forms.util;

import us.tx.state.dfps.common.domain.ApsClientFactors;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsIdentifiedProblemsSummaryDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

/**
 * Prefill data for Identified Problems Summary:CFIV0700
 */

@Repository
public class ApsIdentifiedProblemsSummaryPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

        ApsIdentifiedProblemsSummaryDto apsIdentifiedProblemsSummaryDto = (ApsIdentifiedProblemsSummaryDto) parentDtoObj;

        if (ObjectUtils.isEmpty(apsIdentifiedProblemsSummaryDto.getStageCaseDtlDto())) {
            apsIdentifiedProblemsSummaryDto.setStageCaseDtlDto(new StageCaseDtlDto());
        }

        if (ObjectUtils.isEmpty(apsIdentifiedProblemsSummaryDto.getApsClientFactors())) {
            apsIdentifiedProblemsSummaryDto.setApsClientFactors(new ArrayList<>());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();

        // CSEC02D
        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME, apsIdentifiedProblemsSummaryDto.getStageCaseDtlDto().getNmCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);
        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, apsIdentifiedProblemsSummaryDto.getStageCaseDtlDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

        if (!apsIdentifiedProblemsSummaryDto.getApsClientFactors().isEmpty()) {
            populateClientFactors(formDataGroupList, apsIdentifiedProblemsSummaryDto.getApsClientFactors());
        }

        // Populate narrative
        List<BlobDataDto> blobDataList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(apsIdentifiedProblemsSummaryDto.getIdEvent())) {
            BlobDataDto blobNarrative = createBlobData(BookmarkConstants.ADDITIONAL_COMMENTS,
                    BookmarkConstants.APS_CLIENT_ASSMT_NARR,
                    apsIdentifiedProblemsSummaryDto.getIdEvent().intValue());
            blobDataList.add(blobNarrative);
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonGroupList);
        preFillData.setBlobDataDtoList(blobDataList);
        return preFillData;
    }

    private void populateClientFactors(List<FormDataGroupDto> formDataGroupList, List<ApsClientFactors> apsClientFactors) {

        for (ApsClientFactors clientFactor : apsClientFactors) {
            switch (clientFactor.getCdApsClientFactor().charAt(0)) {
                case BookmarkConstants.E:
                    FormDataGroupDto envFacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ENV_FAC, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> envFacBookmarkList = new ArrayList<>();
                    List<FormDataGroupDto> envFacGroupList = new ArrayList<>();
                    envFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.ENV_FAC_FACTOR, clientFactor.getCdApsClientFactor(), CodesConstant.CENVPBST));
                    envFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.ENV_FAC_RESPONSE, clientFactor.getCdApsCltFactorAns(), CodesConstant.CAFCTRS1));

                    if (null != clientFactor.getTxtApsCltFactorCmnts()) {
                        FormDataGroupDto envFacCommentsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ENV_FAC_COMMENT, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> envFacCommentBookmarkList = new ArrayList<>();
                        envFacCommentBookmarkList.add(createBookmark(BookmarkConstants.ENV_FAC_COMMENT, clientFactor.getTxtApsCltFactorCmnts()));
                        envFacCommentsGroup.setBookmarkDtoList(envFacCommentBookmarkList);
                        envFacGroupList.add(envFacCommentsGroup);
                    }
                    envFacGroup.setBookmarkDtoList(envFacBookmarkList);
                    envFacGroup.setFormDataGroupList(envFacGroupList);
                    formDataGroupList.add(envFacGroup);
                    break;
                case BookmarkConstants.F:
                    FormDataGroupDto finFacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_FAC, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> finFacBookmarkList = new ArrayList<>();
                    List<FormDataGroupDto> finFacGroupList = new ArrayList<>();
                    finFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.FIN_FAC_FACTOR, clientFactor.getCdApsClientFactor(), CodesConstant.CFNPRBST));
                    finFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.FIN_FAC_RESPONSE, clientFactor.getCdApsCltFactorAns(), CodesConstant.CAFCTRS1));

                    if (null != clientFactor.getTxtApsCltFactorCmnts()) {
                        FormDataGroupDto envFacCommentsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_FAC_COMMENT, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> envFacCommentBookmarkList = new ArrayList<>();
                        envFacCommentBookmarkList.add(createBookmark(BookmarkConstants.FIN_FAC_COMMENT, clientFactor.getTxtApsCltFactorCmnts()));
                        envFacCommentsGroup.setBookmarkDtoList(envFacCommentBookmarkList);
                        finFacGroupList.add(envFacCommentsGroup);
                    }
                    finFacGroup.setBookmarkDtoList(finFacBookmarkList);
                    finFacGroup.setFormDataGroupList(finFacGroupList);
                    formDataGroupList.add(finFacGroup);
                    break;
                case BookmarkConstants.P:
                    FormDataGroupDto physicalFacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PHY_MED, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> phyFacBookmarkList = new ArrayList<>();
                    List<FormDataGroupDto> phyFacGroupList = new ArrayList<>();
                    phyFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.PHY_MED_FACTOR, clientFactor.getCdApsClientFactor(), CodesConstant.CPHYSCND));
                    phyFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.PHY_MED_RESPONSE, clientFactor.getCdApsCltFactorAns(), CodesConstant.CAFCTRS1));

                    if (null != clientFactor.getTxtApsCltFactorCmnts()) {
                        FormDataGroupDto envFacCommentsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PHY_MED_COMMENT, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> envFacCommentBookmarkList = new ArrayList<>();
                        envFacCommentBookmarkList.add(createBookmark(BookmarkConstants.PHY_MED_COMMENT, clientFactor.getTxtApsCltFactorCmnts()));
                        envFacCommentsGroup.setBookmarkDtoList(envFacCommentBookmarkList);
                        phyFacGroupList.add(envFacCommentsGroup);
                    }
                    physicalFacGroup.setBookmarkDtoList(phyFacBookmarkList);
                    physicalFacGroup.setFormDataGroupList(phyFacGroupList);
                    formDataGroupList.add(physicalFacGroup);
                    break;
                case BookmarkConstants.M:
                    FormDataGroupDto mentalFacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MENTAL, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> mentalFacBookmarkList = new ArrayList<>();
                    List<FormDataGroupDto> mentalFacGroupList = new ArrayList<>();
                    mentalFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.MENTAL_FACTOR, clientFactor.getCdApsClientFactor(), CodesConstant.CMTLSTAT));
                    mentalFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.MENTAL_RESPONSE, clientFactor.getCdApsCltFactorAns(), CodesConstant.CAFCTRS1));

                    if (null != clientFactor.getTxtApsCltFactorCmnts()) {
                        FormDataGroupDto envFacCommentsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MENTAL_COMMENT, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> envFacCommentBookmarkList = new ArrayList<>();
                        envFacCommentBookmarkList.add(createBookmark(BookmarkConstants.MENTAL_COMMENT, clientFactor.getTxtApsCltFactorCmnts()));
                        envFacCommentsGroup.setBookmarkDtoList(envFacCommentBookmarkList);
                        mentalFacGroupList.add(envFacCommentsGroup);
                    }
                    mentalFacGroup.setBookmarkDtoList(mentalFacBookmarkList);
                    mentalFacGroup.setFormDataGroupList(mentalFacGroupList);
                    formDataGroupList.add(mentalFacGroup);
                    break;
                case BookmarkConstants.S:
                    FormDataGroupDto socialFacGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SOCIAL, BookmarkConstants.EMPTY_STRING);
                    List<BookmarkDto> socialFacBookmarkList = new ArrayList<>();
                    List<FormDataGroupDto> socialFacGroupList = new ArrayList<>();
                    socialFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SOCIAL_FACTOR, clientFactor.getCdApsClientFactor(), CodesConstant.CSCLINTR));
                    socialFacBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SOCIAL_RESPONSE, clientFactor.getCdApsCltFactorAns(), CodesConstant.CAFCTRS1));

                    if (null != clientFactor.getTxtApsCltFactorCmnts()) {
                        FormDataGroupDto envFacCommentsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SOCIAL_COMMENT, BookmarkConstants.EMPTY_STRING);
                        List<BookmarkDto> envFacCommentBookmarkList = new ArrayList<>();
                        envFacCommentBookmarkList.add(createBookmark(BookmarkConstants.SOCIAL_COMMENT, clientFactor.getTxtApsCltFactorCmnts()));
                        envFacCommentsGroup.setBookmarkDtoList(envFacCommentBookmarkList);
                        socialFacGroupList.add(envFacCommentsGroup);
                    }
                    socialFacGroup.setBookmarkDtoList(socialFacBookmarkList);
                    socialFacGroup.setFormDataGroupList(socialFacGroupList);
                    formDataGroupList.add(socialFacGroup);
                    break;
                default:

            }


        }


    }
}
