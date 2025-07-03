package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.NotifToParentEngReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 5, 2018- 12:03:06 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface NotifToParentEngService {

	/**
	 * Service Name: CCFC45S Method Description: This service will Retrieve
	 * names and locations of Board Members of TDPRS and the Executive Directory
	 * Name from table CODES_TABLES
	 *
	 * @param notifToParentEngReq
	 * @return notifToParentEngRes @ the service exception
	 */

	public PreFillDataServiceDto getParentReporterNotified(NotifToParentEngReq notifToParentEngReq);

}
