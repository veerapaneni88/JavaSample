package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CINV68S Mar
 * 16, 2018- 2:03:13 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface FacilityInvSumService {

	/**
	 * Method Name: getFacilityInvSumReport Method Description: Populates form
	 * cfiv1300, which outputs the Facility Investigation Summary form.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getFacilityInvSumReport(FacilityInvSumReq facilityInvSumReq);

}
