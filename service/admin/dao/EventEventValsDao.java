package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventInDto;
import us.tx.state.dfps.service.admin.dto.EventOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Clsc71dDao
 * Aug 11, 2017- 1:38:30 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventEventValsDao {

	/**
	 * 
	 * Method Name: getEventValues Method Description: This method will get data
	 * from EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventOutDto> @
	 */
	public List<EventOutDto> getEventValues(EventInDto pInputDataRec);
}
