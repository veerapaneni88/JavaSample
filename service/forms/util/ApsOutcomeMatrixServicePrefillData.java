package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.pcsphistoryform.dto.CareDetailDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareNarrativeInfoDto;
import us.tx.state.dfps.pcsphistoryform.dto.PcspCaseInfoDto;
import us.tx.state.dfps.service.apsoutcomematrix.dto.ApsOutcomeMatrixServiceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Prefill data class for Outcome Matrix Form/Narrative - civ34o00.
 * Jan 28th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsOutcomeMatrixServicePrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        ApsOutcomeMatrixServiceDto serviceDto = (ApsOutcomeMatrixServiceDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<>();

        for(CareDetailDto careDetailDto : serviceDto.getCareDetailDtoList()) {

            // for TMPLAT_OUTCOME_MATRIX
            FormDataGroupDto tmpltOutcomeMatrix = createFormDataGroup(FormGroupsConstants.TMPLAT_OUTCOME_MATRIX,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> tmpltCareformList = new ArrayList<>();

            BookmarkDto omPrbCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.OM_PRB_CATEGORY, careDetailDto.getCdApsCltFactorCateg(), CodesConstant.CPROCATG);
            tmpltCareformList.add(omPrbCtryBkmark);
            BookmarkDto omPrbSubCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.OM_PRB_SUB_CATEGORY, careDetailDto.getCdApsClientFactor(),CodesConstant.CPROBTYP);
            tmpltCareformList.add(omPrbSubCtryBkmark);
            BookmarkDto omPrbLstUpdBkmark = createBookmark(BookmarkConstants.OM_PRB_LAST_UPDATE, DateUtils.stringDt(careDetailDto.getDtLastUpdate()));
            tmpltCareformList.add(omPrbLstUpdBkmark);
            BookmarkDto omPrbCmmtBkmark = createBookmark(BookmarkConstants.OM_PRB_CMNTS, careDetailDto.getTxtApsCltFactorCmnts());
            tmpltCareformList.add(omPrbCmmtBkmark);
            BookmarkDto omActCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.OM_ACT_CATEGORY, careDetailDto.getCdApsOutcomeActnCateg(),CodesConstant.CACTCATG);
            tmpltCareformList.add(omActCtryBkmark);
            BookmarkDto omActSubCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.OM_ACT_SUB_CATEGORY, careDetailDto.getCdApsOutcomeAction(),CodesConstant.CACTITYP);
            tmpltCareformList.add(omActSubCtryBkmark);
            BookmarkDto omActLstUpdBkmark = createBookmark(BookmarkConstants.OM_ACT_LAST_UPDATE, DateUtils.stringDt(careDetailDto.getDtApsOutcomeAction()));
            tmpltCareformList.add(omActLstUpdBkmark);
            BookmarkDto omActTmUpdBkmark = createBookmark(BookmarkConstants.OM_ACT_CMNTS, careDetailDto.getTxtApsOutcomeAction());
            tmpltCareformList.add(omActTmUpdBkmark);
            BookmarkDto omOutSubCtryBkmark = createBookmark(BookmarkConstants.OM_OUT_SUB_CATEGORY, careDetailDto.getTxtApsOutcomeResult());
            tmpltCareformList.add(omOutSubCtryBkmark);
            BookmarkDto omOutLstUpdBkmark = createBookmark(BookmarkConstants.OM_OUT_LAST_UPDATE, DateUtils.stringDt(careDetailDto.getDtApsOutcomeRecord()));
            tmpltCareformList.add(omOutLstUpdBkmark);
            BookmarkDto omOutCmntsBkmark = createBookmarkWithCodesTable(BookmarkConstants.OM_OUT_CMNTS, careDetailDto.getTxtApsOutcomeResult(),CodesConstant.COUTCTYP);
            tmpltCareformList.add(omOutCmntsBkmark);
            tmpltOutcomeMatrix.setBookmarkDtoList(tmpltCareformList);
            formDataGroupList.add(tmpltOutcomeMatrix);
        }
            // for TMPLAT_CARE_FORM
        for(CareNarrativeInfoDto careNarrativeInfoDto: serviceDto.getCareNarrativeInfoDtoList()) {
            FormDataGroupDto tmpltCareForm = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_FORM,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> tmpltCareformList = new ArrayList<>();
            BookmarkDto carePrbCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.CARE_PRB_CATEGORY, careNarrativeInfoDto.getCareCdCareCategory(), CodesConstant.CCARECAT);
            tmpltCareformList.add(carePrbCtryBkmark);
            BookmarkDto carePrbSubCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.CARE_PRB_SUB_CATEGORY, careNarrativeInfoDto.getCareCdCareFactor(), CodesConstant.CCAREFAC);
            tmpltCareformList.add(carePrbSubCtryBkmark);
            BookmarkDto carePrbLstUpdBkmark = createBookmark(BookmarkConstants.CARE_PRB_TS_UPDATE,DateUtils.stringDt(careNarrativeInfoDto.getCareDtLastUpdate()));
            tmpltCareformList.add(carePrbLstUpdBkmark);
            BookmarkDto careActCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.CARE_ACT_CATEGORY,careNarrativeInfoDto.getCareCdApsOutcomeAction(),CodesConstant.CACTITP2);
            tmpltCareformList.add(careActCtryBkmark);
            BookmarkDto careActSubCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.CARE_ACT_SUB_CATEGORY, careNarrativeInfoDto.getCareCdApsOutcomeActnCateg(),CodesConstant.CACTCTG2);
            tmpltCareformList.add(careActSubCtryBkmark);
            BookmarkDto careActUpdBkmark = createBookmark(BookmarkConstants.CARE_ACT_UPDATE, DateUtils.stringDt(careNarrativeInfoDto.getCareDtApsOutcomeAction()));
            tmpltCareformList.add(careActUpdBkmark);
            BookmarkDto careOutSubCtryBkmark = createBookmarkWithCodesTable(BookmarkConstants.CARE_OUT_SUB_CATEGORY, careNarrativeInfoDto.getCareCdApsOutcomeResult(),CodesConstant.COUTCTYP);
            tmpltCareformList.add(careOutSubCtryBkmark);
            BookmarkDto careOutUpdBkmark = createBookmark(BookmarkConstants.CARE_OUT_UPDATE, DateUtils.stringDt(careNarrativeInfoDto.getCareDtApsOutcomeRecord()));
            tmpltCareformList.add(careOutUpdBkmark);
            tmpltCareForm.setBookmarkDtoList(tmpltCareformList);
            formDataGroupList.add(tmpltCareForm);
        }

        List<PcspCaseInfoDto> pcspCaseInfoDtoList = serviceDto.getPcspCaseInfoDtoList();
        for(PcspCaseInfoDto pcspCaseInfoDto: pcspCaseInfoDtoList) {
            BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, pcspCaseInfoDto.getIdCase());
            bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumber);

            BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_NM_CASE, pcspCaseInfoDto.getNmCase());
            bookmarkNonFrmGrpList.add(bookmarkTitleCaseName);

            BookmarkDto bookmarkWrkrFName = createBookmark(BookmarkConstants.NAME_FIRST, pcspCaseInfoDto.getNmNameFirst());
            bookmarkNonFrmGrpList.add(bookmarkWrkrFName);

            BookmarkDto bookmarkWrkrMName = createBookmark(BookmarkConstants.NAME_MIDDLE, pcspCaseInfoDto.getNmNameMiddle());
            bookmarkNonFrmGrpList.add(bookmarkWrkrMName);

            BookmarkDto bookmarkWrkrLName = createBookmark(BookmarkConstants.NAME_LAST, pcspCaseInfoDto.getNmNameLast());
            bookmarkNonFrmGrpList.add(bookmarkWrkrLName);

            FormDataGroupDto tmpltOmNarrativeBlob = createFormDataGroup(FormGroupsConstants.TMPLAT_OM_NARRATIVE,
                    FormConstants.EMPTY_STRING);
            List<BlobDataDto> blobDataDtoList = new ArrayList<>();
            BlobDataDto blobNarr = createBlobData(BookmarkConstants.OM_NARRATIVE,
                    BookmarkConstants.OUTCOME_MATRIX_NARRATIVE, pcspCaseInfoDto.getIdEvent().toString());
            blobDataDtoList.add(blobNarr);
            tmpltOmNarrativeBlob.setBlobDataDtoList(blobDataDtoList);
            formDataGroupList.add(tmpltOmNarrativeBlob);
        }

        PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
        prefillData.setFormDataGroupList(formDataGroupList);
        prefillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return prefillData;
    }
}
