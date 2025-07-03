package us.tx.state.dfps.service.investigation.dao;

import us.tx.state.dfps.common.domain.FacilityInvstDtl;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:48 PM
 */
public interface FacilityInvstDtlDao {
	/**
	 * 
	 * Method Description:legacy service name - CINV17D
	 * 
	 * @param uIdStage
	 * @return @
	 */
	FacilityInvstDtl getFacilityInvstDetailbyParentId(Long uIdStage);

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV54D
	 * 
	 * @param facilityInvst
	 * @
	 */
	void facilityInvstDtlSave(FacilityInvstDtl facilityInvstDtl);

}
