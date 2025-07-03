/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 18, 2018- 12:28:05 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.SecurityClassDao;
import us.tx.state.dfps.service.admin.dto.SecurityProfileMaintDto;
import us.tx.state.dfps.service.admin.service.SecurityProfileMaintService;
import us.tx.state.dfps.service.common.request.SecurityProfileMaintReq;
import us.tx.state.dfps.service.common.response.SecurityProfileMaintRes;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this is
 * service class for SecurityProfileMaintenanace Sep 18, 2018- 12:28:05 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
public class SecurityProfileMaintServiceImpl implements SecurityProfileMaintService {

	@Autowired
	SecurityClassDao securityClassDao;// Clss12d
	@Autowired
	LookupDao lookupDao;

	/**
	 * 
	 * Method Name: getSecurityProfiles Method Description: to get profiles
	 * 
	 * @return List<SecurityProfileMaintDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SecurityProfileMaintDto> getSecurityProfiles() {
		return securityClassDao.getSecurityProfiles();
	}

	/**
	 * Method Name: securityProfileAUD Method Description:
	 * 
	 * @param securityProfileMaintReq
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SecurityProfileMaintRes securityProfileAUD(SecurityProfileMaintReq securityProfileMaintReq) {
		return securityClassDao.securityProfileAUD(securityProfileMaintReq);
	}

	/**
	 * 
	 * Method Name: getSecurityProfileByName Method Description:get the profile
	 * 
	 * @param nmSecurityClass
	 * @return SecurityProfileMaintDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SecurityProfileMaintDto getSecurityProfileByName(String nmSecurityClass) {

		SecurityProfileMaintDto securityProfileMaintDto = new SecurityProfileMaintDto();

		securityProfileMaintDto = securityClassDao.getSecurityProfileByName(nmSecurityClass);

		return securityProfileMaintDto;
	}

}
