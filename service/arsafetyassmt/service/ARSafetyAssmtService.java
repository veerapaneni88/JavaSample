package us.tx.state.dfps.service.arsafetyassmt.service;

import us.tx.state.dfps.service.common.request.ARClosureSafetyAssmtReq;
import us.tx.state.dfps.service.common.response.ARClosureSafetyAssmtRes;

public interface ARSafetyAssmtService {

	/**
	 * Fetch the ARSafety Assessment data from the database.
	 * 
	 * @param ARSafetyAssmtReq
	 * @return ARSafetyAssmtRes
	 * 
	 */
	public ARClosureSafetyAssmtRes getARSafetyAssmt(ARClosureSafetyAssmtReq arSafetyAssmtReq);

	/**
	 * Method Name: getARSafetyAssmtFactorValueBean Method Description:
	 * Retrieves ARSafetyAssmtFactorValueDto from database
	 * 
	 * @param idFactor
	 * @param idSafetyAssmt
	 * @return ARClosureSafetyAssmtRes
	 * 
	 */
	public ARClosureSafetyAssmtRes getARSafetyAssmtFactor(Integer idFactor, Integer idSafetyAssmt);

}
