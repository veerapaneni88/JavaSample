package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Service Interface for fetching event details
 *
 * Aug 6, 2017- 8:25:52 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CpsInvConclSelectService {

	/**
	 * 
	 * Method Name: callCpsInvConclSelectService Method Description: This
	 * service is used in the Predisplay callback of window CINV06W - CPS INV
	 * CONCLUSION. It retrieves all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return CpsInvConclSelectoDto @
	 */
	public CpsInvConclSelectoDto callCpsInvConclSelectService(CpsInvConclSelectiDto pInputMsg);
}
