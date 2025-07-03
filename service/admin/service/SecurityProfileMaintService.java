/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 18, 2018- 12:23:56 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SecurityProfileMaintDto;
import us.tx.state.dfps.service.common.request.SecurityProfileMaintReq;
import us.tx.state.dfps.service.common.response.SecurityProfileMaintRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Service Interface class Sep 18, 2018- 12:23:56 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SecurityProfileMaintService {

	/**
	 * 
	 * Method Name: getSecurityProfiles Method Description: to get profiles
	 * 
	 * @return List<SecurityProfileMaintDto>
	 */
	List<SecurityProfileMaintDto> getSecurityProfiles();

	/**
	 * Method Name: securityProfileAUD Method Description:
	 * 
	 * @param securityProfileMaintReq
	 * @return
	 */
	SecurityProfileMaintRes securityProfileAUD(SecurityProfileMaintReq securityProfileMaintReq);

	/**
	 * 
	 * Method Name: getSecurityProfileByName Method Description:get the profile
	 * 
	 * @param nmSecurityClass
	 * @return SecurityProfileMaintDto
	 */
	SecurityProfileMaintDto getSecurityProfileByName(String nmSecurityClass);
}
