package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.afistatement.dto.AFIStatementDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AFIStatementPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form civ39o00 Mar 14, 2018- 10:49:17 AM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component
public class AFIStatementPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		AFIStatementDto afIStatementDto = (AFIStatementDto) parentDtoobj;

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Bookmarks for CSEC02D
		if (!ObjectUtils.isEmpty(afIStatementDto.getGenericCaseInfoDto())) {
			BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.NM_CASE,
					afIStatementDto.getGenericCaseInfoDto().getNmCase());
			bookmarkNonFrmGrpList.add(bookmarkNmCase);

			BookmarkDto bookmarkNmCaseFoot = createBookmark(BookmarkConstants.NM_CASE_FOOT,
					afIStatementDto.getGenericCaseInfoDto().getNmCase());
			bookmarkNonFrmGrpList.add(bookmarkNmCaseFoot);

			BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.ID_CASE,
					afIStatementDto.getGenericCaseInfoDto().getIdCase());
			bookmarkNonFrmGrpList.add(bookmarkIdCase);

			BookmarkDto bookmarkIdCaseFoot = createBookmark(BookmarkConstants.ID_CASE_FOOT,
					afIStatementDto.getGenericCaseInfoDto().getIdCase());
			bookmarkNonFrmGrpList.add(bookmarkIdCaseFoot);
		}

		// Bookmarks for CSYS11D
		if (!ObjectUtils.isEmpty(afIStatementDto.getStageProgramDto()) && !ObjectUtils.isEmpty(afIStatementDto.getStageProgramDto().getDtCntOccr())) {
			BookmarkDto bookmarkDtContactOccured = createBookmark(BookmarkConstants.DT_CONTACT,
					afIStatementDto.getStageProgramDto().getDtCntOccr());
			bookmarkNonFrmGrpList.add(bookmarkDtContactOccured);

			BookmarkDto bookmarkTimeContact = createBookmark(BookmarkConstants.TIME_CONTACT,
					afIStatementDto.getStageProgramDto().getTmScrTmCntct());
			bookmarkNonFrmGrpList.add(bookmarkTimeContact);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;

	}

}
