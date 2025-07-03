package us.tx.state.dfps.service.security.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.StaffSecurityRtrvoDto;
import us.tx.state.dfps.service.common.request.StaffSecurityMaintenanceReq;
import us.tx.state.dfps.service.common.response.StaffSecurityMaintenanceRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.security.service.StaffSecurityMaintenaceService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * will have staff security maintenance service methods Sept 25, 2018- 11:15:08
 * AM © 2018 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/staffSecurityMaintenance")
public class StaffSecurityMaintenanceController {

	@Autowired
	StaffSecurityMaintenaceService objStaffSecurityMaintService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-StaffSecurityAudController");

	/**
	 * Method : StaffSecurityRtrv Method Description : This service retrieves
	 * staff security information for a given employee from the Employee Table
	 * if it exists. It also retrieves any temporary assignments of the
	 * employee, which can be up to five assignments.
	 *
	 * @param StaffSecurityMaintenanceReq
	 * @return StaffSecurityMaintenanceRes
	 */
	@RequestMapping(value = "/staffsecurityrtrv", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StaffSecurityMaintenanceRes StaffSecurityRtrv(
			@RequestBody StaffSecurityMaintenanceReq staffSecurityMaintenanceReq) {
		log.debug("Entering method StaffSecurityRtrv in StaffSecurityRtrvController");
		StaffSecurityRtrvoDto objStaffSecurityRtrvoDto;
		try {
			if (ObjectUtils.isEmpty(staffSecurityMaintenanceReq.getIdPerson())) {
				throw new InvalidRequestException(
						messageSource.getMessage("StaffSecurityRtrviDto.UlIdPerson.mandatory", null, Locale.US));
			}
			objStaffSecurityRtrvoDto = objStaffSecurityMaintService.staffSecurityRtrvService(
					staffSecurityMaintenanceReq.getIdPerson(), staffSecurityMaintenanceReq.isExternalUser());
		} catch (DataNotFoundException e) {
			throw new DataNotFoundException(
					messageSource.getMessage("StaffSecurityRtrv.UlIdPerson.data", null, Locale.US));
		}
		StaffSecurityMaintenanceRes respone = new StaffSecurityMaintenanceRes();
		respone.setStaffSecurityRtrvoDto(objStaffSecurityRtrvoDto);
		log.debug("Exiting method StaffSecurityRtrv in StaffSecurityRtrvController");
		return respone;
	}

	/**
	 * 
	 * Method : StaffSecurityAud Method Description : This service adds,
	 * updates, and deletes staff security information and staff temporary
	 * assignments.
	 *
	 * @param StaffSecurityMaintenanceReq
	 * @return StaffSecurityMaintenanceRes
	 */
	@RequestMapping(value = "/staffsecurityaud", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StaffSecurityMaintenanceRes StaffSecurityAud(
			@RequestBody StaffSecurityMaintenanceReq staffSecurityMaintenanceReq) {
		StaffSecurityMaintenanceRes staffSecurityMaintenanceRes = new StaffSecurityMaintenanceRes();
		log.debug("Entering method StaffSecurityAud in StaffSecurityMaintenanceController");
		if (ObjectUtils.isEmpty(staffSecurityMaintenanceReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("StaffSecurityMaintenanceController.IdPerson.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(staffSecurityMaintenanceReq.getDtLastUpdate())) {
			throw new InvalidRequestException(messageSource
					.getMessage("StaffSecurityMaintenanceController.DtLastUpdate.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(staffSecurityMaintenanceReq.isExternalUser())) {
			throw new InvalidRequestException(messageSource
					.getMessage("StaffSecurityMaintenanceController.ExternalUser.mandatory", null, Locale.US));
		}
		staffSecurityMaintenanceRes = objStaffSecurityMaintService.staffSecurityAudService(staffSecurityMaintenanceReq);

		log.debug("Exiting method StaffSecurityAud in StaffSecurityMaintenanceController");
		return staffSecurityMaintenanceRes;
	}

	/**
	 * Method Name: getStaffSecurityDesigneeDtls Method Description: This
	 * service retrieves all designees for a given employee. It performs a full
	 * row retrieval of the EMP_TEMP_ASSIGN table for a given ID PERSON.
	 * 
	 * @param staffSecurityMaintenanceReq
	 * @return StaffSecurityMaintenanceRes
	 */
	@RequestMapping(value = "/getStaffSecurityDesigneeDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StaffSecurityMaintenanceRes getStaffSecurityDesigneeDtls(
			@RequestBody StaffSecurityMaintenanceReq staffSecurityMaintenanceReq) {

		StaffSecurityMaintenanceRes staffSecurityMaintenanceRes = new StaffSecurityMaintenanceRes();

		if (ObjectUtils.isEmpty(staffSecurityMaintenanceReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		staffSecurityMaintenanceRes.setStaffSecurityDesigneeList(
				objStaffSecurityMaintService.getStaffSecurityDesigneeDtls(staffSecurityMaintenanceReq.getIdPerson()));
		return staffSecurityMaintenanceRes;

	}
	
}
