package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Populates
 * the LICENSING INVESTIGATION REPORT form.> #cinv74s Mar 27, 2018- 3:06:03 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface LicensingInvSumService {

	/**
	 * Method Name: getFacilityInvSumReport Method Description: Populates form
	 * cfiv1100, which Populates the LICENSING INVESTIGATION REPORT form.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getLicensingInvSumReport(PopulateFormReq populateFormReq);

}
