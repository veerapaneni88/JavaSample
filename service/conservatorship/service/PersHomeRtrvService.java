package us.tx.state.dfps.service.conservatorship.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.PersHomeRtrvReq;
import us.tx.state.dfps.service.cvs.dto.PersHomeRtrvoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for PersHomeRtrvService Aug 2, 2017- 8:36:20 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersHomeRtrvService {

	/**
	 * 
	 * Method Name: personHomeRtrv Method Description:This service retrieves all
	 * persons associated with an event from STAGE_PERSON_LINK and a subset of
	 * all persons at home during removal from PERSON_HOME_RMVL table. If the
	 * person is found in both tables, an attribute is set to true, so that the
	 * person will be 'checked' when displayed to the window.
	 * 
	 * @param persHomeRtrvReq
	 * @return List<PersHomeRtrvoDto> @
	 */
	public List<PersHomeRtrvoDto> personHomeRtrv(PersHomeRtrvReq persHomeRtrvReq);
}
