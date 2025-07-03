
package us.tx.state.dfps.service.investigationaction.service;

import us.tx.state.dfps.service.common.request.InvActionReportReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * InvActionReportService will have all operation which are mapped to
 * InvActionReport module. May 2, 2018- 2:01:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface InvActionReportService {

	/**
	 * 
	 * Method Name: getInvActionInfo Service Name :CINV37S Method
	 * Description:This is the CINV37S form service to build the Investigation
	 * Actions Report
	 * 
	 * @param invActionReportReq
	 * @return
	 */
	public PreFillDataServiceDto getInvActionReportInfo(InvActionReportReq invActionReportReq);

}
