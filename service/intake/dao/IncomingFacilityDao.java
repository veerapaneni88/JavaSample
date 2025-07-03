package us.tx.state.dfps.service.intake.dao;

import us.tx.state.dfps.common.domain.IncomingFacility;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 29, 2017 - 12:02:08 PM
 */
public interface IncomingFacilityDao {

	/**
	 * Method Description: legacy service name - CINT10D
	 * 
	 * @param facDetailUpdtReq
	 * @return
	 */
	String updtFacilityDetail(IncomingFacility incomingFacility, String operation);

}
