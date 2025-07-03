package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.SaveEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * saves emergency assistance details Aug 9, 2017- 1:05:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SaveEmergAssistService {

	/**
	 * 
	 * Method Name: callSaveEmergAssistService Method Description: This method
	 * saves emergency assistance details
	 * 
	 * @param pInputMsg
	 * @return SaveEmergAssistoDto @
	 */
	public SaveEmergAssistoDto callSaveEmergAssistService(SaveEmergAssistiDto pInputMsg);

}
