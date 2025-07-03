package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CINV81S May
 * 3, 2018- 3:21:51 PM © 2017 Texas Department of Family and Protective Services
 */
public interface FacilityInvRepService {

	/**
	 * Method Name: getFacilityInvReport Method Description: Populates form
	 * cfiv1500, which Populates the APS ICF-MR FACILITY INVESTIGATIVE REPORT
	 ** aka 5-DAY STATUS REPORT.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getFacilityInvReport(FacilityInvSumReq facilityInvSumReq);

}
