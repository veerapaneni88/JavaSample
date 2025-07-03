package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EmergencyAssistInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInsUpdDelOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * inserts or updates emergency assistance details Aug 9, 2017- 4:14:01 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface EmergencyAssistInsUpdDelDao {

	/**
	 * Method Name: cuEmergencyAssistanceDtls Method Description: This method
	 * inserts or updates emergency assistance details
	 * 
	 * @param pInputDataRec
	 * @return EmergencyAssistInsUpdDelOutDto @
	 */
	public EmergencyAssistInsUpdDelOutDto cuEmergencyAssistanceDtls(EmergencyAssistInsUpdDelInDto pInputDataRec);
}
