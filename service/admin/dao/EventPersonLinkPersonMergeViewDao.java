package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Cinv52 Aug 5, 2017- 12:17:19 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventPersonLinkPersonMergeViewDao {

	/**
	 * 
	 * Method Name: getEventTypeForPerson Method Description: This method will
	 * retrieve the event type meeting the criteria.
	 * 
	 * @param eventPersonLinkPersonMergeViewInDto
	 * @return List<EventPersonLinkPersonMergeViewOutDto>
	 */
	public List<EventPersonLinkPersonMergeViewOutDto> getEventTypeForPerson(
			EventPersonLinkPersonMergeViewInDto eventPersonLinkPersonMergeViewInDto);
}
