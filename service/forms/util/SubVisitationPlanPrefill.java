/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 11, 2018- 2:31:15 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * is used to retrieve the information on the subcare Visitation Plan form. May
 * 11, 2018- 2:31:15 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class SubVisitationPlanPrefill extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description:This method is used to
	 * prefill the data from the different Dao by passing Dao output Dtos and
	 * bookmark and form group bookmark Dto as objects as input request
	 * 
	 * @param genericCaseInfoDto
	 * @return
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		GenericCaseInfoDto genericCaseInfoDto = (GenericCaseInfoDto) parentDtoobj;

		/**
		 * Description: Populating the non form group data into prefill data
		 * BookMark: TITLE_CASE_NAME,TITLE_CASE_NAME Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();
		if (null != genericCaseInfoDto.getNmCase()) {
			BookmarkDto bkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
					genericCaseInfoDto.getNmStage());
			bookmarkDtoDefaultDtoList.add(bkTitleCaseName);
		} else {
			BookmarkDto bkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME, ServiceConstants.BLANK);
			bookmarkDtoDefaultDtoList.add(bkTitleCaseName);
		}

		if (null != genericCaseInfoDto.getIdCase()) {
			BookmarkDto bkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_ID,
					genericCaseInfoDto.getIdCase());
			bookmarkDtoDefaultDtoList.add(bkTitleCaseNum);
		} else {
			BookmarkDto bkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_ID, ServiceConstants.BLANK);
			bookmarkDtoDefaultDtoList.add(bkTitleCaseNum);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

		preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		return preFillData;
	}

}
