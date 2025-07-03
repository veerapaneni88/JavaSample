package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv33 Aug 5, 2017- 12:00:16 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface StagePersonLinkMergeViewDao {

	/**
	 * 
	 * Method Name: getActiveProgStagePID Method Description: This method will
	 * get data from STAGE ,STAGE_PERSON_LINK and PERSON_MERGE_VIEW tables.
	 * 
	 * @param stagePersonLinkMergeViewInDto
	 * @return List<StagePersonLinkMergeViewOutDto>
	 */
	public List<StagePersonLinkMergeViewOutDto> getActiveProgStagePID(
			StagePersonLinkMergeViewInDto stagePersonLinkMergeViewInDto);
}
