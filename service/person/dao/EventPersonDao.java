package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for EventPerson May 31, 2018- 11:17:17 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface EventPersonDao {

	/**
	 * 
	 * Method Name: getOpenStageEventsForAPerson Method Description: Get open
	 * stage event for person
	 * 
	 * @param idClosedPerson
	 * @return
	 */
	List<EventDto> getOpenStageEventsForAPerson(Long idClosedPerson);

	/**
	 * 
	 * Method Name: insertIntoEventPersonLinks Method Description: This method
	 * batch inserts into Event_person_link table using EventPersonValueBean
	 * list
	 * 
	 * @param spLinkBeans
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */

	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans) throws DataNotFoundException;
}
