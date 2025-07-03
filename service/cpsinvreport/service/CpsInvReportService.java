package us.tx.state.dfps.service.cpsinvreport.service;

import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * class for CPSInvReport Apr 4, 2018- 4:46:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CpsInvReportService {

	/**
	 * Method Name: getCPSInvReport Method Description:
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getCPSInvReport(FacilityInvSumReq facilityInvSumReq);

}
