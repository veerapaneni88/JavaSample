package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentApplicationRes;

import org.springframework.stereotype.Component;

@Component
public class KinshipPaymentPrefillData extends DocumentServiceUtil {


    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        KinPaymentApplicationRes kinPaymentApplicationRes = (KinPaymentApplicationRes) parentDtoobj;

        List<BookmarkDto> bookmarkNonFromGroupDtoList = new ArrayList<>();
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_FIRST,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getFirstName()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_MIDDLE,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getMiddleName()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_LAST,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getLastName()));
        bookmarkNonFromGroupDtoList.add(createBookmarkWithCodesTable(BookmarkConstants.CAREGIVER_RELATION_TO_CHILD,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getRelationshipToChild(), CodesConstant.CRELPRN2));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_SSN,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getSSN()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_ST_LN_1,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getAddrLn1()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_ST_CITY,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getCity()));
        bookmarkNonFromGroupDtoList.add(createBookmarkWithCodesTable(BookmarkConstants.CAREGIVER_STATE,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getState(), CodesConstant.CSTATE));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_ZIP,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getZipCode()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CAREGIVER_PH_NO,
                TypeConvUtil.formatPhone(kinPaymentApplicationRes.getKinPaymentCareGiverDto().getPhone())));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.NO_PERSONS,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getNoOfPersons()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.ANNUAL_INCOME,
                kinPaymentApplicationRes.getKinPaymentCareGiverDto().getAnnualIncome()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CHILD_LAST,
                kinPaymentApplicationRes.getKinPaymentChildDto().getLastName()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CHILD_FIRST,
                kinPaymentApplicationRes.getKinPaymentChildDto().getFirstName()));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CHILD_BIRTH,
                DateUtils.stringDt(kinPaymentApplicationRes.getKinPaymentChildDto().getDateOfBirth())));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.PMC_DATE,
                DateUtils.stringDt(kinPaymentApplicationRes.getKinChildLegalOutcomeDate())));
        bookmarkNonFromGroupDtoList.add(createBookmark(BookmarkConstants.CHILD_DATE,
                DateUtils.stringDt(kinPaymentApplicationRes.getKinPaymentChildDto().getPlacementStartDate())));

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(bookmarkNonFromGroupDtoList);
        return preFillData;
    }
}
