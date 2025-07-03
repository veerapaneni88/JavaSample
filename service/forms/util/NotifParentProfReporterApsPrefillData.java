package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import us.tx.state.dfps.common.dto.PersonAddressDto;
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
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component

public class NotifParentProfReporterApsPrefillData extends DocumentServiceUtil {

    /**
     * Method Name: returnPrefillData Method Description: Creates bookmarks and
     * groups for form
     *
     * @param parentDtoobj
     * @return PreFillDataServiceDto
     *
     */
    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        NotifToRequestorDto prefillDto = (NotifToRequestorDto) parentDtoobj;
        if (null == prefillDto.getAdminReviewDto()) {
            prefillDto.setAdminReviewDto(new AdminReviewDto());
        }
        if (null == prefillDto.getEmployeePersPhNameDto()) {
            prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
        }
        if (null == prefillDto.getNameDetailDto()) {
            prefillDto.setNameDetailDto(new NameDetailDto());
        }
        if (null == prefillDto.getPersonAddressDto()) {
            prefillDto.setPersonAddressDto(new PersonAddressDto());
        }
        if (null == prefillDto.getPrimaryWorkerDto()) {
            prefillDto.setPrimaryWorkerDto(new PrimaryWorkerDto());
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

        // orphan bookmarks
        List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

        // Director info

        bookmarkNonFormGroupList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE)
                , createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
        ));



        // DAM CSEC35D

        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));


        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER_NAME_SUFFIX,
                prefillDto.getRequestorNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_FIRST,
                prefillDto.getRequestorNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_LAST,
                prefillDto.getRequestorNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_MIDDLE,
                prefillDto.getRequestorNameDetailDto().getNmNameMiddle()));

        // DAM CSEC34D

        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ZIP,
                prefillDto.getPersonAddressDto().getAddrPersonAddrZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_CITY,
                prefillDto.getPersonAddressDto().getAddrPersonAddrCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ST_1,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ST_2,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn2()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT_ADDR_STATE,
                prefillDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE));

        // CSES63D
        if (prefillDto.getAdminReviewDto().getCdAdminRvAppealType().equalsIgnoreCase(CodesConstant.CARVTYPE_010)) {
            FormDataGroupDto tmpReview1 = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_1,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpReview1);
        }
        if ( ! prefillDto.getAdminReviewDto().getCdAdminRvAppealType().equalsIgnoreCase(CodesConstant.CARVTYPE_010)) {
            FormDataGroupDto tmpReview2 = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_2,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(tmpReview2);
        }

        // group ccf11o04
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
            FormDataGroupDto priorAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            priorAllegGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION, stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION, stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,  stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                    createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                    createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, stageReviewDto.getNmNameFirst()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_LAST, stageReviewDto.getNmNameLast()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, stageReviewDto.getNmNameMiddle())));
            formDataGroupList.add(priorAllegGroupDto);
        }



        // group ccf11o06
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewNoDto()) {
            FormDataGroupDto currentAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            currentAllegGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION, stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION, stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,  stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                    createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                    createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, stageReviewDto.getNmNameFirst()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_LAST, stageReviewDto.getNmNameLast()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, stageReviewDto.getNmNameMiddle())));
            formDataGroupList.add(currentAllegGroupDto);
        }



        // DAM CSEC01D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.NOTIFICATION_DATE,
                DateUtils.stringDt(prefillDto.getEmployeePersPhNameDto().getDtEmpTermination())));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
                prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE,
                TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));



        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
        return preFillData;
    }

}
