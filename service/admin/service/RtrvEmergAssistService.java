package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to fetch the questions based on the events from emergency assist. Aug
 * 5, 2017- 6:18:05 PM © 2017 Texas Department of Family and Protective Services
 */
public interface RtrvEmergAssistService {
	/**
	 * 
	 * Method Name: callRtrvEmergAssistService Method Description:This method is
	 * used to fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<RtrvEmergAssistoDto> @
	 */
	public RtrvEmergAssistoDto callRtrvEmergAssistService(RtrvEmergAssistiDto pInputMsg);

}
