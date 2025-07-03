package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.KinCareGiverResourceRes;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service for KIN10O00
 */
@Repository
public class KinCareGiverResourceRequestPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        KinCareGiverResourceRes kinCareGiverResourceRes = (KinCareGiverResourceRes) parentDtoobj;
        List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

        /**
         * CSECE1D
         */
        if (null != kinCareGiverResourceRes.getCareGiverContractInfo()) {
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.RESOURCE_ID,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getIdResource()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.PLCMT_START_DATE,
                    DateUtils.stringDt(kinCareGiverResourceRes.getCareGiverContractInfo().getPlacementStartDate())));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.PLCMT_END_DATE,
                    DateUtils.stringDt(kinCareGiverResourceRes.getCareGiverContractInfo().getPlacementEndDate())));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_NAME_FIRST,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getFirstName()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_NAME_MIDDLE,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getMiddleName()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_NAME_LAST,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getLastName()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_STREET,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getStreetLine1()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_CITY,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getCity()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_STATE,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getState()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_ZIP,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getZip()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.KINSHIP_PHONE,
                    TypeConvUtil.formatPhone(kinCareGiverResourceRes.getCareGiverContractInfo().getPhone())));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.VENDOR_ID,
                    kinCareGiverResourceRes.getCareGiverContractInfo().getVendorId()));
        }

        /**
         * CCMN19D
         */
        if (null != kinCareGiverResourceRes.getCaseWorkerNameDto()) {
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASEWORKER_NAME,
                    kinCareGiverResourceRes.getCaseWorkerNameDto().getFullName()));
        }

        /**
         * CSEC01D
         */
        if (null != kinCareGiverResourceRes.getCaseWorkerAddressDto()) {
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASEWORKER_ST,
                    kinCareGiverResourceRes.getCaseWorkerAddressDto().getStreetLine1()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASEWORKER_CITY,
                    kinCareGiverResourceRes.getCaseWorkerAddressDto().getCity()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASEWORKER_ZIP,
                    kinCareGiverResourceRes.getCaseWorkerAddressDto().getZip()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.MAIL_CODE,
                    kinCareGiverResourceRes.getCaseWorkerAddressDto().getMailCode()));
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.OFFICE_PHONE,
                    TypeConvUtil.formatPhone(kinCareGiverResourceRes.getCaseWorkerAddressDto().getPhoneNumber())));
        }

        /**
         * CLSS0DD
         */
        if (null != kinCareGiverResourceRes.getCaseWorkerPhoneDto()) {
            bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CELL_PHONE,
                    TypeConvUtil.formatPhone(kinCareGiverResourceRes.getCaseWorkerPhoneDto().getPhoneNumber())));
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
        return preFillData;
    }
}
