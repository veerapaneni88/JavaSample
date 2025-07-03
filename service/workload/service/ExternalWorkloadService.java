/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 10, 2018- 5:14:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.RCCPWorkloadReq;
import us.tx.state.dfps.service.common.response.RCCPWorkloadRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 10, 2018- 5:14:45 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ExternalWorkloadService {

	/**
	 * 
	 * Method Name: getExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	RCCPWorkloadRes getExternalWorkloadDetails(RCCPWorkloadReq rccpWorkloadReq);

	/**
	 * 
	 * Method Name: getStageDetails Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	RCCPWorkloadRes getStageDetails(RCCPWorkloadReq rccpWorkloadReq);

	/**
	 * Method Name: searchWorkload Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	RCCPWorkloadRes searchWorkload(RCCPWorkloadReq rccpWorkloadReq);

}
