package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.dto.FacilityStageInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:52:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class FacilityStageInfoPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

		FacilityStageInfoDto facilityStageInfoDto = (FacilityStageInfoDto) parentDtoObj;

		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

		bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.HOME_NAME, facilityStageInfoDto.getResourceName()));
		bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.FACILITY_ID, facilityStageInfoDto.getIdResource()));
		bookmarkDtoDefaultDtoList.add(createBookmark(BookmarkConstants.CASE_ID, facilityStageInfoDto.getIdCase()));

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		return preFillData;
	}

}
