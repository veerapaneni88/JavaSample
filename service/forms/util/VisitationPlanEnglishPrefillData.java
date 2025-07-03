package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;

@Component
/**
 * Name:VisitationPlanEnglishPrefillData Description:
 * VisitationPlanEnglishPrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form CSC08O00.(Family Service Plan Parent-Child Contact and Financial Support
 * (Visitation Plan)) Jan 04, 2018 - 04:40:29 PM
 */
public class VisitationPlanEnglishPrefillData extends DocumentServiceUtil {

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
		StageCaseDtlDto stageCaseDtlDto = (StageCaseDtlDto) parentDtoobj;

		if (null == stageCaseDtlDto) {
			stageCaseDtlDto = (new StageCaseDtlDto());
		}
		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate the TITLE_CASE_NAME from CSEC02D
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				stageCaseDtlDto.getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		// Populate the TITLE_CASE_ID from CSEC02D
		BookmarkDto bookmarkTitleCaseId = createBookmark(BookmarkConstants.TITLE_CASE_ID, stageCaseDtlDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseId);

		BookmarkDto bookmarkTitleCaseIdNum = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, stageCaseDtlDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseIdNum);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
