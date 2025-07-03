package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.response.UnitSummaryRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN29S Class
 * Description: Unit Summary Page
 */
public interface UnitSummaryService {

	/**
	 * 
	 * Method Description: This method is called, when a user searches for a
	 * unit. The user profile is used to populate the search parameters
	 * therefore no service is called here.
	 * 
	 * @param assignWorkloadReq
	 * @return AssignWorkloadReq @ Tuxedo Service Name: CCMN29S
	 */
	AssignWorkloadReq getUnitSummaryDtls(AssignWorkloadReq assignWorkloadReq);

	/**
	 * 
	 * Method Description: This method Searches the database for program -
	 * region - unit combination that match the specified search criteria.
	 * 
	 * @param assignWorkloadReq
	 * @return UnitSummaryRes
	 * @, DataNotFoundException Tuxedo Service Name: CCMN29S
	 */
	UnitSummaryRes searchUnitSummary(AssignWorkloadReq assignWorkloadReq);
}
