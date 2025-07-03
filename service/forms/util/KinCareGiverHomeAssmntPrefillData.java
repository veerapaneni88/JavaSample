package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverHomeInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business - Kinship CareGiver Home Assessment Template (KIN12O00)
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@Repository
public class KinCareGiverHomeAssmntPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        KinCareGiverHomeInfoDto kinCareGiverHomeInfoDto = (KinCareGiverHomeInfoDto) parentDtoobj;

        List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

        /**
         * Populating the non form group data into prefill data. Dam Name :CSEC02D
         */
        if (null != kinCareGiverHomeInfoDto.getCaseInfoDto()) {
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASE_ID,
                    kinCareGiverHomeInfoDto.getCaseInfoDto().getIdCase()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASE_NAME,
                    kinCareGiverHomeInfoDto.getCaseInfoDto().getCaseName()));
        }

        List<FormDataGroupDto> sbGrpFormDataMembers = new ArrayList<FormDataGroupDto>();

        /**
         * Description: TMPLAT_RSRC_ADDRESS_2 Dam Name:CRES0AD
         * BookMark: TMPLAT_RSRC_ADDRESS_2 Condition: None
         */
        FormDataGroupDto careGiverAddressGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDRESS_2,
                FormConstants.EMPTY_STRING);

        if (null != kinCareGiverHomeInfoDto.getCareGiverAddressDto()) {
            List<BookmarkDto> bookmarkAddressList = new ArrayList<BookmarkDto>();
            bookmarkAddressList.add(createBookmark(BookmarkConstants.RSRC_ADDRESS_LINE_1,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getAddressLine1()));
            bookmarkAddressList.add(createBookmark(BookmarkConstants.RSRC_ADDRESS_LINE_2,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getAddressLine2()));
            bookmarkAddressList.add(createBookmark(BookmarkConstants.RSRC_ADDRESS_CITY,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getCity()));
            bookmarkAddressList.add(createBookmarkWithCodesTable(BookmarkConstants.RSRC_COUNTY,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getCounty(), CodesConstant.CCOUNT));
            bookmarkAddressList.add(createBookmark(BookmarkConstants.RSRC_ADDRESS_ZIP,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getZipCode()));
            bookmarkAddressList.add(createBookmark(BookmarkConstants.RSRC_ADDRESS_STATE,
                    kinCareGiverHomeInfoDto.getCareGiverAddressDto().getState()));
                bookmarkAddressList.add(createBookmark(BookmarkConstants.FAMILY_NAME,
                        kinCareGiverHomeInfoDto.getCareGiverAddressDto().getResourceName()));
            careGiverAddressGroupDto.setBookmarkDtoList(bookmarkAddressList);
        }
        sbGrpFormDataMembers.add(careGiverAddressGroupDto);
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(sbGrpFormDataMembers);
        preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
        return preFillData;
    }
}
