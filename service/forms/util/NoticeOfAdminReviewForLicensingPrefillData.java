package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
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
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class NoticeOfAdminReviewForLicensingPrefillData extends DocumentServiceUtil{
    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        NotifToParentDto notifToParentDto = (NotifToParentDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();


        List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

        //CLSC03D
        bookmarkNonFormGroupList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE)
                , createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
        ));

        //date
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.NOTIFICATION_DATE, TypeConvUtil.formDateFormat(DateUtils.getCurrentDate())));


         //CSES63D // no prefill

        if (!TypeConvUtil.isNullOrEmpty(notifToParentDto.getStageReviewDtolist())) {

                bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ABUSE_NEGLECT_TYPE,
                        notifToParentDto.getStageReviewDtolist().get(0).getCdAdminAllegType(), CodesConstant.CABALTYP));

                bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.CURRENT_DISPOSITION,
                        notifToParentDto.getStageReviewDtolist().get(0).getCdAdminAllegDispostion(), CodesConstant.CDISPSTN));

        }


       // NameDetailDto   //CSEC35D
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER2_NAME_SUFFIX,
                notifToParentDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_FIRST,
                notifToParentDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_LAST,
                notifToParentDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_MIDDLE,
                notifToParentDto.getNameDetailDto().getNmNameMiddle()));

        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ZIP,
                notifToParentDto.getPersonAddressDto().getAddrPersonAddrZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_CITY,
                notifToParentDto.getPersonAddressDto().getAddrPersonAddrCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_1,
                notifToParentDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_2,
                notifToParentDto.getPersonAddressDto().getAddrPersAddrStLn2()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_ADDR_STATE,
                notifToParentDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE));

        //CSEC01D

        if (StringUtils.isNotBlank(notifToParentDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt())) {
            FormDataGroupDto user2ExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_PHONE_EXT,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkUser2ExtList = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkUser2Ext = createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
                    notifToParentDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt());
            bookmarkUser2ExtList.add(bookmarkUser2Ext);
            user2ExtGroupDto.setBookmarkDtoList(bookmarkUser2ExtList);
            formDataGroupList.add(user2ExtGroupDto);
        }

        if (!TypeConvUtil.isNullOrEmpty(notifToParentDto.getEmployeePersPhNameDto())) {
            bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_PHONE,
                    TypeConvUtil.formatPhone(notifToParentDto.getEmployeePersPhNameDto().getNbrPhone())));
            bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER2_NAME_SUFFIX,
                    notifToParentDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
            bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_FIRST,
                    notifToParentDto.getEmployeePersPhNameDto().getNmNameFirst()));
            bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_LAST,
                    notifToParentDto.getEmployeePersPhNameDto().getNmNameLast()));
            bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_MIDDLE,
                    notifToParentDto.getEmployeePersPhNameDto().getNmNameMiddle()));

        }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);

        return preFillData;
    }
}
