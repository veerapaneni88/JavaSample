package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Cinv79dDao Aug 5, 2017- 9:02:56 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventPersonLinkMergeViewCountDao {

	/**
	 * 
	 * Method Name: getRecordCount Method Description: This method will give the
	 * count of rows meeting the criteria.
	 * 
	 * @param eventPersonLinkMergeViewCountInDto
	 * @return List<EventPersonLinkMergeViewCountOutDto>
	 */
	public List<EventPersonLinkMergeViewCountOutDto> getRecordCount(
			EventPersonLinkMergeViewCountInDto eventPersonLinkMergeViewCountInDto);
}
