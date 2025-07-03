/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 18, 2018- 11:50:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SecurityProfileMaintDto;
import us.tx.state.dfps.service.common.request.SecurityProfileMaintReq;
import us.tx.state.dfps.service.common.response.SecurityProfileMaintRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: this dao
 * Interface for SecurityClass Sep 18, 2018- 11:50:00 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface SecurityClassDao {

	/**
	 * 
	 * Method Name: getSecurityProfiles Method Description:to fetch profiles
	 * 
	 * @return List<SecurityProfileMaintDto>
	 */
	List<SecurityProfileMaintDto> getSecurityProfiles();

	/**
	 * 
	 * Method Name: getSecurityProfileByName Method Description: to fetch
	 * profile by name
	 * 
	 * @param nmSecurityClass
	 * @return SecurityProfileMaintDto
	 */
	SecurityProfileMaintDto getSecurityProfileByName(String nmSecurityClass);

	/**
	 * Method Name: securityProfileAUD Method Description: to save , update ,
	 * delete the profile
	 * 
	 * @param securityProfileMaintReq
	 * @return SecurityProfileMaintRes
	 */
	SecurityProfileMaintRes securityProfileAUD(SecurityProfileMaintReq securityProfileMaintReq);

}
