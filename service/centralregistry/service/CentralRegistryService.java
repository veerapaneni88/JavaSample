package us.tx.state.dfps.service.centralregistry.service;

import us.tx.state.dfps.service.common.request.CentralRegistryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * CentralRegistryService will have all operation which are mapped to
 * CentralRegistry module. May 2, 2018- 2:01:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CentralRegistryService {
	/**
	 * 
	 * Method Name: getCentralRegistryInfo Service Name : CCMN51S Method
	 * Description:This form service populates the Central Registry form.
	 * 
	 * @param centralRegistryReq
	 * @return
	 */
	public PreFillDataServiceDto getCentralRegistryInfo(CentralRegistryReq centralRegistryReq);

}
