/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 18, 2018- 11:26:12 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.security.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.SecurityProfileMaintDto;
import us.tx.state.dfps.service.admin.service.SecurityProfileMaintService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SecurityProfileMaintReq;
import us.tx.state.dfps.service.common.response.SecurityProfileMaintRes;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This Class
 * used for Security Profile Maintenance purpose(CARC12S also CARC13S) Sep 18,
 * 2018- 11:26:12 AM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/securityMaintenance")
public class SecurityProfileMaintenanceController {

	@Autowired
	SecurityProfileMaintService securityProfileMaintService;

	private static final Logger log = Logger.getLogger(SecurityProfileMaintenanceController.class);

	/**
	 * 
	 * Method Name: SecurityMaintRtr Method Description:The method will retrieve
	 * all rows from the SECURITY CLASS table.
	 * 
	 * @param objSecurityMaintRtrviDto
	 * @return SecurityMaintRtrRes
	 * 
	 */
	@RequestMapping(value = "/profiles", headers = { "Accept=application/json" }, method = RequestMethod.GET)

	public SecurityProfileMaintRes getSecurityProfiles() {
		log.debug("Entering method getSecurityProfiles in SecurityProfileMaintenanceController");
		List<SecurityProfileMaintDto> securityProfileMaintDto = securityProfileMaintService.getSecurityProfiles();
		SecurityProfileMaintRes securityMaintRtrRes = new SecurityProfileMaintRes();
		securityMaintRtrRes.setSecurityProfileMaintDtos(securityProfileMaintDto);
		log.debug("Exiting method getSecurityProfiles in SecurityProfileMaintenanceController");
		return securityMaintRtrRes;
	}

	/**
	 * 
	 * Method Name: securityProfileAUD Method Description: this Method for add ,
	 * update , and delete
	 * 
	 * @param securityProfileMaintReq
	 * @return
	 */
	@RequestMapping(value = "/profileaud", headers = { "Accept=application/json" }, method = RequestMethod.POST)

	public SecurityProfileMaintRes securityProfileAUD(@RequestBody SecurityProfileMaintReq securityProfileMaintReq) {
		log.debug("Entering method securityProfileAUD in SecurityProfileMaintenanceController");
		SecurityProfileMaintRes securityMaintRtrRes = new SecurityProfileMaintRes();
		// this is required as Hibernate not allowing the loading the record ,
		// and updating the primary key(Class Name) in single transaction
		if (!ServiceConstants.REQ_FUNC_CD_ADD.equals(securityProfileMaintReq.getReqFuncCd())) {
			SecurityProfileMaintDto securityProfileMaintDto = securityProfileMaintService
					.getSecurityProfileByName(securityProfileMaintReq.getNmSecurityClass());
			if (ObjectUtils.isEmpty(securityProfileMaintDto) || securityProfileMaintReq.getSecurityProfile()
					.getDtLastUpdate().compareTo(securityProfileMaintDto.getDtLastUpdate()) != 0) {
				throw new ServiceLayerException("Time Mismatch error",
						Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), null);
			}
		}
		// this try block added to handle validation, and custom error code for
		// duplicate record , as legacy also depending on the sql errors
		try {
			securityMaintRtrRes = securityProfileMaintService.securityProfileAUD(securityProfileMaintReq);
		} catch (DataIntegrityViolationException ex) {
			ConstraintViolationException ce = (ConstraintViolationException) ex.getCause();
			ce.getSQLException().getErrorCode();
			if (ce.getSQLException().getErrorCode() == 1) {

				throw new ServiceLayerException("dup error", Long.valueOf(ServiceConstants.MSG_DUPLICATE_PROFILE),
						null);
			}

		}

		log.debug("Exiting method securityProfileAUD in SecurityProfileMaintenanceController");
		return securityMaintRtrRes;
	}

}
