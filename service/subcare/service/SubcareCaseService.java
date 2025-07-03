package us.tx.state.dfps.service.subcare.service;

import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will populate the Subcare Case Reading Tool.CSUB79S May 9, 2018- 9:22:47 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface SubcareCaseService {

	/**
	 * Method Name: getSubcareCase Method Description: Populates form csc40o00,
	 * which Populates the Subcare Case Reading Tool.
	 * 
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getSubcareCase(FacilityInvSumReq facilityInvSumReq);
}
