package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountInDto;
import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cint60 Aug 10, 2017- 12:26:47 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventStPerLnkEmpMerRletChkCountDao {

	/**
	 * 
	 * Method Name: deletePerson Method Description: Delete person record if
	 * there is no child cascading exists Cint60d
	 * 
	 * @param eventStPerLnkEmpMerRletChkCountInDto
	 * @return EventStPerLnkEmpMerRletChkCountOutDto
	 */
	public EventStPerLnkEmpMerRletChkCountOutDto deletePerson(
			EventStPerLnkEmpMerRletChkCountInDto eventStPerLnkEmpMerRletChkCountInDto);
}
