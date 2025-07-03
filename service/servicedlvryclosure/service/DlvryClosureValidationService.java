package us.tx.state.dfps.service.servicedlvryclosure.service;

import us.tx.state.dfps.service.common.response.DlvryClosurevalidationRes;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * class for csvc16s tuxedo service converted to rest for service closure
 * validation May 14, 2018- 9:44:17 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface DlvryClosureValidationService {

	/**
	 * Method Name: callDlvryClosureValidation Method Description: method for
	 * csvc16s tuxedo service validations for cps program and FRS stage
	 * 
	 * @param dlvryClosureValidationDto
	 */
	public DlvryClosurevalidationRes callDlvryClosureValidation(DlvryClosureValidationDto pInputMsg);

}
