package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EmergencyAssistInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 3:51:48 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface EmergencyAssistDao {

	/**
	 * 
	 * Method Name: fetchQues Method Description: This method fetches questions
	 * based on the event from emergency assist.
	 * 
	 * @param pInputDataRec
	 * @return List<EmergencyAssistOutDto> @
	 */
	public List<EmergencyAssistOutDto> fetchQues(EmergencyAssistInDto pInputDataRec);
}
