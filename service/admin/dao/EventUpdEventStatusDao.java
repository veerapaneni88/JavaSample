package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn62 Aug 8, 2017- 10:40:28 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventUpdEventStatusDao {

	/**
	 * 
	 * Method Name: updateEvent Method Description:This method updates event
	 * status
	 * 
	 * @param pInputDataRec
	 * @return int @
	 */
	public int updateEvent(EventUpdEventStatusInDto pInputDataRec);
}
