package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.common.request.SpecialHandlingReq;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN81S Class
 * Description: This class is use for retrieving Caps Case details Mar 23, 2017
 * - 7:40:51 PM
 */

public interface SpecialHandlingService {
	/**
	 * 
	 * Method Description: This Method will retrieve either all or only the
	 * valid rows for a given person id depending upon whether the invalid SYS
	 * IND VALID ONLY indicator is set. Service Name: CCMN81S
	 * 
	 * @param specialHandlingReq
	 * @return CapsCaseDto @
	 */
	public CapsCaseDto getSpclHndlng(SpecialHandlingReq specialHandlingReq);

}
