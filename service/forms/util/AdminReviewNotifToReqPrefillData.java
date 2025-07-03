package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.NotifToRequestorDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:*
 * AdminReviewNotifToReqPrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form Notification to Requestor Form.
 */
@Component
public class AdminReviewNotifToReqPrefillData extends DocumentServiceUtil {
    /*
     * Form Name :CCF15O00(Notification to Notification To Requester)
     */

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        NotifToRequestorDto prefillDto = (NotifToRequestorDto) parentDtoobj;

        if (null == prefillDto.getAdminReviewDto()) {
            prefillDto.setAdminReviewDto(new AdminReviewDto());
        }
        if (null == prefillDto.getStageReviewNoDto()) {
            prefillDto.setStageReviewNoDto(new ArrayList<StageReviewDto>());
        }
        if (null == prefillDto.getStageReviewYesDto()) {
            prefillDto.setStageReviewYesDto(new ArrayList<StageReviewDto>());
        }
        if (null == prefillDto.getStageRtrvOutDto()) {
            prefillDto.setStageRtrvOutDto(new StageRtrvOutDto());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        // CSEC02D
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.REP_SYSTEM_DATE, TypeConvUtil.formDateFormat(DateUtils.getCurrentDate())));
        // CLSC03D
        bookmarkNonGroupList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE)
                , createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
        ));
        // CSES63D
        if (CodesConstant.CARVTYPE_010.equalsIgnoreCase(prefillDto.getAdminReviewDto().getCdAdminRvAppealType())) {
            FormDataGroupDto tmpReview1 = createFormDataGroup(FormGroupsConstants.TMPLAT_ADMIN_REV_FINDINGS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpReview1);
        }
        if (!(CodesConstant.CARVTYPE_010.equalsIgnoreCase(prefillDto.getAdminReviewDto().getCdAdminRvAppealType()))) {
            FormDataGroupDto tmpReview2 = createFormDataGroup(FormGroupsConstants.TMPLAT_RELEASE_HEARING_PROCESS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpReview2);
        }
        // CLSC65D Prior Alleg
            for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
                FormDataGroupDto priorAlleg = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
                        FormConstants.EMPTY_STRING);
                priorAlleg.setBookmarkDtoList(Arrays.asList(
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_TYPE,
                                stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_DISPOSITION,
                                stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ADMIN_ALLEG_CLSS,
                                stageReviewDto.getCdAdiminAllegClss(), CodesConstant.CFACCLSS),
                        createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                                stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                        createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                                stageReviewDto.getNmNameFirst()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                                stageReviewDto.getNmNameLast()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                                stageReviewDto.getNmNameMiddle())
                ));
                if (!ObjectUtils.isEmpty(stageReviewDto.getCdNameSuffix())) {
                    priorAlleg.setFormDataGroupList(Arrays.asList(
                            createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
                                    FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS)));
                }
                formDataGroupList.add(priorAlleg);
            }
        // CLSC65D Current Alleg
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewNoDto()) {
                FormDataGroupDto currentAlleg = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
                        FormConstants.EMPTY_STRING);
                currentAlleg.setBookmarkDtoList(Arrays.asList(
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_TYPE,
                                stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_DISPOSITION,
                                stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                        createBookmarkWithCodesTable(BookmarkConstants.CD_ADMIN_ALLEG_CLSS,
                                stageReviewDto.getCdAdiminAllegClss(), CodesConstant.CFACCLSS),
                        createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                                stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                        createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                                stageReviewDto.getNmNameFirst()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                                stageReviewDto.getNmNameLast()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                                stageReviewDto.getNmNameMiddle()),
                        createBookmark(BookmarkConstants.TMPLAT_COMMA,
                                FormConstants.EMPTY_STRING),
                        createBookmark(BookmarkConstants.TMPLAT_COMMA,
                                FormConstants.EMPTY_STRING)
                ));
                if (!ObjectUtils.isEmpty(stageReviewDto.getCdNameSuffix())){
                    currentAlleg.setFormDataGroupList(Arrays.asList(
                            createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
                                    FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS)));
                }
                formDataGroupList.add(currentAlleg);
            }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonGroupList);
        return preFillData;
    }
}
