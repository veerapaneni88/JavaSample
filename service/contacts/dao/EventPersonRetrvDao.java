package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.EventPersonRetrvInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventPersonRetrvOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventPersonRetrvDao Oct 31, 2017- 3:09:49 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventPersonRetrvDao {

	/**
	 * Method Name: getPersonIdsForStage Method Description:EVENT_PERSON_LINK
	 * retrieval.
	 * 
	 * @param eventPersonRetrvInDto
	 * @return EventPersonRetrvOutDto
	 */
	public EventPersonRetrvOutDto getPersonIdsForStage(EventPersonRetrvInDto eventPersonRetrvInDto);

}
