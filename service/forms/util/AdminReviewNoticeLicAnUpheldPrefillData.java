package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminReviewNoticeLicAnUpheldPrefillData extends DocumentServiceUtil {
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

        List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();
        // Director info
        bookmarkNonFormGroupList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE)
                , createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
        ));
        //CSEC35D
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER2_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER2_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        //CSEC34D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ZIP,
                prefillDto.getPersonAddressDto().getAddrPersonAddrZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_CITY,
                prefillDto.getPersonAddressDto().getAddrPersonAddrCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_1,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_2,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn2()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_ADDR_STATE,
                prefillDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE));
        //CSEC01D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.NOTIFICATION_DATE,
                DateUtils.stringDt(prefillDto.getEmployeePersPhNameDto().getDtEmpTermination())));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
                prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE,
                TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE_EXTENSION,
                TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_ST_1,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_ST_2,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_CITY,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_ADDR_ZIP,
                prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER2_NAME_CD_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_TITLE,
                prefillDto.getEmployeePersPhNameDto().getTxtEmployeeClass()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_PHONE,
                prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
                prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt()));
        //CLSC65D
            bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ABUSE_NEGLECT_TYPE,
                    prefillDto.getStageReviewYesDto().get(0).getCdAdminAllegType(),CodesConstant.CABALTYP));


        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
        return preFillData;
    }
}
