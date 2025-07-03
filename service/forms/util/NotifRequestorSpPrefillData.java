package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.*;
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
public class NotifRequestorSpPrefillData extends DocumentServiceUtil {

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
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE),
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)));



        // DAM CSEC35D

        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER2_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_ADDR_STATE,
                prefillDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);

        // DAM CSEC34D

        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ZIP,
                prefillDto.getPersonAddressDto().getAddrPersonAddrZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ZIP,
                prefillDto.getPersonAddressDto().getAddrPersonAddrZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_CITY,
                prefillDto.getPersonAddressDto().getAddrPersonAddrCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ST_1,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_1,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_2,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn2()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_CITY,
                prefillDto.getPersonAddressDto().getAddrPersonAddrCity()));
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

        // group ccf11o03
        if (ServiceConstants.CCOR_010.equalsIgnoreCase(prefillDto.getAdminReviewDto().getCdAdminRvAppealResult())) {
            FormDataGroupDto allegUpheldGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATIONS_UPHELD,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(allegUpheldGroupDto);
        }

        // group ccf11o05
        else {
            FormDataGroupDto allegReversedGroupDto = createFormDataGroup(
                    FormGroupsConstants.TMPLAT_ALLEGATIONS_REVERSED, FormConstants.EMPTY_STRING);
            formDataGroupList.add(allegReversedGroupDto);
        }

        // group ccf11o01
        FormDataGroupDto furtherInfoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FURTHER_INFORMATION,
                FormConstants.EMPTY_STRING);
        formDataGroupList.add(furtherInfoGroupDto);


        // group ccf11o02
        FormDataGroupDto futureContactGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FUTURE_CONTACT,
                FormConstants.EMPTY_STRING);
        formDataGroupList.add(futureContactGroupDto);


        // group ccf11o04
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
            FormDataGroupDto priorAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            priorAllegGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION, stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION, stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY, stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
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
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY, stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                    createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                    createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, stageReviewDto.getNmNameFirst()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_LAST, stageReviewDto.getNmNameLast()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, stageReviewDto.getNmNameMiddle())));
            formDataGroupList.add(currentAllegGroupDto);
        }

        // group ccf11o07
        if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
            FormDataGroupDto user2NameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_NAME_CD,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkUser2NameList = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkUser2Name = createBookmark(FormGroupsConstants.USER2_NAME_CD_SUFFIX,
                    prefillDto.getEmployeePersPhNameDto().getCdNameSuffix());
            bookmarkUser2NameList.add(bookmarkUser2Name);
            user2NameGroupDto.setBookmarkDtoList(bookmarkUser2NameList);
            formDataGroupList.add(user2NameGroupDto);
        }

        // group ccf11o08
        if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
            FormDataGroupDto user2Addr2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_ADDR2,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkUser2Addr2List = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkUser2Addr2 = createBookmark(FormGroupsConstants.USER2_ADDR2_TXT,
                    prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
            bookmarkUser2Addr2List.add(bookmarkUser2Addr2);
            user2Addr2GroupDto.setBookmarkDtoList(bookmarkUser2Addr2List);
            formDataGroupList.add(user2Addr2GroupDto);
        }

        // group ccf11o09
        if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt())) {
            FormDataGroupDto user2ExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_PHONE_EXT,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkUser2ExtList = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkUser2Ext = createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
                    prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt());
            bookmarkUser2ExtList.add(bookmarkUser2Ext);
            user2ExtGroupDto.setBookmarkDtoList(bookmarkUser2ExtList);
            formDataGroupList.add(user2ExtGroupDto);
        }


        // DAM CSEC01D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.NOTIFICATION_DATE,
                DateUtils.stringDt(prefillDto.getEmployeePersPhNameDto().getDtEmpTermination())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_CITY,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR1,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_ZIP,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
                prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE,
                TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_PHONE,
                TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_TITLE,
                prefillDto.getEmployeePersPhNameDto().getTxtEmployeeClass()));

        // DAM CINT21D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.CASE_ID,
                prefillDto.getStageRtrvOutDto().getIdCase()));




        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
        return preFillData;
    }

}
