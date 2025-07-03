package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.response.AssignWorkloadRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN14S Class
 * Description: This class is use for retrieving AssignWorkload List Mar 23,
 * 2017 - 3:19:51 PM
 */

public interface AssignWorkloadService {
	/**
	 * 
	 * Method Description: This Method is used to retrieve Assigned Workload to
	 * worker by giving person_id in request object(AssignWorkloadReq) Tuxedo
	 * Service Name: CCMN14S
	 * 
	 * @param assignWorkloadReq
	 * @throws Exception
	 */
	public AssignWorkloadRes getAssignWorkloadDetails(AssignWorkloadReq assignWorkloadReq);

}
