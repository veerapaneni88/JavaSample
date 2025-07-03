package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pcgoal.dto.PCGoalDto;
import us.tx.state.dfps.service.pcgoal.dto.PglDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PCGoalPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form CCMN01O00. Mar 5, 2018- 3:24:09 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@Component
public class PCGoalPrefillData extends DocumentServiceUtil {

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

		PCGoalDto pCGoalDto = (PCGoalDto) parentDtoobj;

		if (ObjectUtils.isEmpty(pCGoalDto.getPglDetailDtoList())) {
			pCGoalDto.setPglDetailDtoList(new ArrayList<PglDetailDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// Set prefill data for the form group ccmn01pg	
		
		for (PglDetailDto pglDetailDtoType : pCGoalDto.getPglDetailDtoList()) {		
			List<BookmarkDto> bookmarkPermGoalsDefinedList = new ArrayList<BookmarkDto>();
			FormDataGroupDto permGoalsDefinedFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.PERM_GOALS_DEFINED,
					FormConstants.EMPTY_STRING);
			BookmarkDto bookmarkTxtDecodeDto = createBookmark(BookmarkConstants.PERM_GOAL_DECODE,
					pglDetailDtoType.getTxtDecode());
			BookmarkDto bookmarkTxtDefinitionDto = createBookmark(BookmarkConstants.PERM_GOAL_DEFINITION,
					pglDetailDtoType.getTxtDefinition());
			bookmarkPermGoalsDefinedList.add(bookmarkTxtDecodeDto);
			bookmarkPermGoalsDefinedList.add(bookmarkTxtDefinitionDto);

			permGoalsDefinedFrmDataGrpDto.setBookmarkDtoList(bookmarkPermGoalsDefinedList);
			formDataGroupList.add(permGoalsDefinedFrmDataGrpDto);
		}
		
		/**
		 * Populating the non form group data into prefill data
		 */
			// this code is not required as the template no values need to be shown for this bookmark refer to the code present in legacy formprocessor line 412
	/*	List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate szCdPermGoal value from DAM CCMNP1D

		if (!ObjectUtils.isEmpty(pCGoalDto.getPglDetailDtoList())) {
			BookmarkDto bookmarkCdPermGoal = createBookmark(BookmarkConstants.PERM_GOAL_CODE,
					pCGoalDto.getPglDetailDtoList().get(0).getCdPermGoal());
			bookmarkNonFrmGrpList.add(bookmarkCdPermGoal);
		}*/

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		//preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;
	}

}
