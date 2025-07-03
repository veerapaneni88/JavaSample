package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apsGuardianshipDetails.dto.ApsGuardianshipDetailsServiceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.GuardianshipDto;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsGuardianshipDetails -- CSC04O00.
 * Jan 14, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsGuardianshipDetailsPrefillData extends DocumentServiceUtil {




    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtObj) {
        ApsGuardianshipDetailsServiceDto apsGuardianshipDetailsServiceDto = (ApsGuardianshipDetailsServiceDto) parentDtObj;

        // Initialize null DTOsparentDtObj = {ApsGuardianshipDetailsServiceDto@27361}
        if (ObjectUtils.isEmpty(apsGuardianshipDetailsServiceDto.getGuardianshipDto())) {
            apsGuardianshipDetailsServiceDto.setGuardianshipDto(new GuardianshipDto());
        }
        if (ObjectUtils.isEmpty(apsGuardianshipDetailsServiceDto.getGenericCaseInfoDto())) {
            apsGuardianshipDetailsServiceDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
        }

        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

        // CSEC02D
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NAME, apsGuardianshipDetailsServiceDto.getGenericCaseInfoDto().getNmCase()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, apsGuardianshipDetailsServiceDto.getGenericCaseInfoDto().getIdCase()));

       // CSEC09D
        GuardianshipDto guardianshipDto = apsGuardianshipDetailsServiceDto.getGuardianshipDto();
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_AGED_OUT,guardianshipDto.getIndGuardAgedOut()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CLOSURE_DATE,guardianshipDto.getDtGuardCloseDate()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_DT_LETTER_ISSUED,guardianshipDto.getDtGuardLetterIssued()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_DT_OF_ORDER,guardianshipDto.getDtGuardOrdered()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.ADDR_CITY,guardianshipDto.getAddrGuardCity()));
        bookmarkNonGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,guardianshipDto.getAddrGuardCnty(), CodesConstant.CCOUNT));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.ADDR_STREET1,guardianshipDto.getAddrGuardLn1()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.ADDR_STREET2,guardianshipDto.getAddrGuardLn2()));
        bookmarkNonGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.ADDR_STATE,guardianshipDto.getAddrGuardSt(), CodesConstant.USSTATES));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.ADDR_ZIP,guardianshipDto.getAddrGuardZip()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CLOSURE_REASON,guardianshipDto.getCdGuardCloseReason()));
        bookmarkNonGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.GUARDIAN_GUARSHIP_TYPE,guardianshipDto.getCdGuardGuardianType(), CodesConstant.CGUARTYP));
        bookmarkNonGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.GUARDIAN_TYPE,guardianshipDto.getCdGuardType(),CodesConstant.CGARSHPT));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,guardianshipDto.getGuardPhoneExt()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_PERSON_FULL,guardianshipDto.getNmPersonFull()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_RESOURCE,guardianshipDto.getNmResource()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.GUARDIAN_NAME,guardianshipDto.getSdsGuardName()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.ADDR_COMMENTS,guardianshipDto.getGuardAddrComments()));
        bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CLOSURE_COMMENTS,guardianshipDto.getGuardComments()));
        BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
                TypeConvUtil.formatPhone(guardianshipDto.getGuardPhone()));
        bookmarkNonGroupList.add(phone);

        preFillData.setBookmarkDtoList(bookmarkNonGroupList);
        return preFillData;

    }
}
