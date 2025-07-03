package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.medicalhistory.dto.MedicalDevHistoryDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description:
 * CommonApplicationPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form csc11o00. Jan 22, 2018 - 04:10:29 PM
 */
@Component
public class MedicalDevHistoryPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the Dao
	 * by passing Dao output Dto, bookmark and form group bookmark Dto as
	 * objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		MedicalDevHistoryDto medicalDevHistoryDto = (MedicalDevHistoryDto) parentDtoobj;
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		StagePersonLinkCaseDto stagePersonLinkCaseDto = medicalDevHistoryDto.getStagePersonLinkCaseDto();
		// Populate nmCase value from DAM CSEC15D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				stagePersonLinkCaseDto.getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate dtPersonBirth value from DAM CSEC15D
		BookmarkDto bookmarkDtpersonBirth = createBookmark(BookmarkConstants.TITLE_CHILD_DOB,
				TypeConvUtil.formDateFormat(stagePersonLinkCaseDto.getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDtpersonBirth);

		// Populate cdNameSuffix value from DAM CSEC15D
		BookmarkDto bookmarkCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				stagePersonLinkCaseDto.getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSuffix);

		// Populate nmNameFirst value from DAM CSEC15D
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				stagePersonLinkCaseDto.getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmNameFirst);

		// Populate nmNameLast value from DAM CSEC15D
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				stagePersonLinkCaseDto.getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkNmNameLast);

		// Populate nmNameMid value from DAM CSEC15D
		BookmarkDto bookmarkNmNameMid = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				stagePersonLinkCaseDto.getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmNameMid);

		// Populate idCase value from DAM CSEC15D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				stagePersonLinkCaseDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
