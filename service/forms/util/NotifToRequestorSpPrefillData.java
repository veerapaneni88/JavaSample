package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.NotifToParentDto;
import us.tx.state.dfps.service.workload.dto.NotifToRequestorDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class NotifToRequestorSpPrefillData extends DocumentServiceUtil {



    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        NotifToRequestorDto prefillDto = (NotifToRequestorDto) parentDtoobj;

        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

        //CLSC03D
        bookmarkNonFrmGrpList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE) ,
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
        ));

        //CSEC02D -- sysdate
        if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
            BookmarkDto dtSysDtGenericSysdate = createBookmark(BookmarkConstants.REP_SYSTEM_DATE,
                    DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
            bookmarkNonFrmGrpList.add(dtSysDtGenericSysdate);

        }

        //CSES63D , TMPLAT_ADMIN_REV_FINDINGS ==010, TMPLAT_RELEASE_HEARING_PROCESS <> 010


        if (prefillDto.getAdminReviewDto().getCdAdminRvAppealType().compareTo(FormConstants.SYSCODE2) == 0) {
            FormDataGroupDto tmpAdminRevFinding = createFormDataGroup(FormGroupsConstants.TMPLAT_ADMIN_REV_FINDINGS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpAdminRevFinding);
        }
        if (prefillDto.getAdminReviewDto().getCdAdminRvAppealType().compareTo(FormConstants.SYSCODE2) != 0) {
            FormDataGroupDto tmpAdminRelHearingProcess = createFormDataGroup(FormGroupsConstants.TMPLAT_RELEASE_HEARING_PROCESS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpAdminRelHearingProcess);
        }

        //CLSC65D

        if (!TypeConvUtil.isNullOrEmpty(prefillDto.getStageReviewYesDto())) {
            for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
                FormDataGroupDto priorAllegationGroupData = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
                        FormConstants.EMPTY_STRING);
                priorAllegationGroupData.setBookmarkDtoList(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_DISPOSITION,
                        stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                createBookmarkWithCodesTable(BookmarkConstants.CD_ADMIN_ALLEG_CLSS,
                        stageReviewDto.getCdAdiminAllegClss(), CodesConstant.CFACCLSS),
                createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_TYPE,
                        stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                        stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                        stageReviewDto.getNmNameFirst()),
                createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                        stageReviewDto.getNmNameLast()),
                createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                        stageReviewDto.getNmNameMiddle())));
                formDataGroupList.add(priorAllegationGroupData);
            }
        }
        if (!TypeConvUtil.isNullOrEmpty(prefillDto.getStageReviewNoDto())) {
            for (StageReviewDto stageReviewDto : prefillDto.getStageReviewNoDto()) {
                FormDataGroupDto currentAllegaitonGroupData = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
                        FormConstants.EMPTY_STRING);

                currentAllegaitonGroupData.setBookmarkDtoList(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_DISPOSITION,
                        stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                createBookmarkWithCodesTable(BookmarkConstants.CD_ADMIN_ALLEG_CLSS,
                        stageReviewDto.getCdAdiminAllegClss(), CodesConstant.CFACCLSS),
                createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_TYPE,
                        stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                        stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                        stageReviewDto.getNmNameFirst()),
                createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                        stageReviewDto.getNmNameLast()),
                createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                        stageReviewDto.getNmNameMiddle())));
                formDataGroupList.add(currentAllegaitonGroupData);
            }
        }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return preFillData;
    }
}
