/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This class is used as rest service controller to control all
 *the request from web client for respective request for External User management 
 *Jul 17, 2018- 3:27:04 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExtUserMgmntReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingHistoryReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingListReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgResourceLinkDelReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExtUserMgmntRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingHistoryRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingListRes;
import us.tx.state.dfps.service.externalorg.service.ExtUserOrgMappingService;

@RestController
@RequestMapping("/externalUserMngmnt")
public class ExternalUserManagementController {
	private static final Logger LOG = Logger.getLogger("ServiceBusiness-ExternalUserManagementController");

	public ExternalUserManagementController() {
	}

	@Autowired
	MessageSource messageSource;

	@Autowired
	ExtUserOrgMappingService extUserOrgMappingService;

	/**
	 * Method Name: executeOrgSearch Method Description: Rest Controller method
	 * to get External user organization role mappings
	 * 
	 * @param searchReq
	 * @return response
	 */
	@RequestMapping(value = "/getExtUserOrgMappingList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserOrgMappingListRes getExtUserOrgMappingList(@RequestBody ExtUserOrgMappingListReq req) {
		if (ObjectUtils.isEmpty(req.getIdPerson())) {
			// If no id Person, Throw invalid req as idPerson is not found.
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		// If idPerson available then get the list
		return extUserOrgMappingService.getExtUserOrgMappingList(req);
	}

	/**
	 * Method Name: getOrganizationResourcesList Method Description:This method
	 * is used to get the resources list for an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "getOrganizationResources", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes getOrganizationResourcesList(@RequestBody ExtUserMgmntReq extUserMgmntReq) {

		// Calling the service implementation to get the external organization
		// resources
		// list
		return extUserOrgMappingService.getOrgResourcesList(extUserMgmntReq.getIdOrgEin(),
				extUserMgmntReq.isIndRCCPUser());

	}

	/**
	 * Method Name: deleteOrganizationResourcesList Method Description:This
	 * method is used to delete the resources from an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "deleteOrganizationResourcesList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes deleteOrganizationResourcesList(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to delete the external
		// organization
		// resources
		return extUserOrgMappingService.deleteOrgResources(extUserMgmntReq);
	}

	/**
	 * Method Name: addOrganizationResource Method Description:This method is
	 * used to save the resources to an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "addOrganizationResource", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes addOrganizationResource(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to add resources to the external
		// organization
		return extUserOrgMappingService.addOrganizationResource(extUserMgmntReq);
	}

	/**
	 * Method Name: saveAssignOrgRoleDtls Method Description:This method is used
	 * to save the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "saveAssignOrgRoleDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes saveAssignOrgRoleDtls(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to save the assign org & role
		// details.
		return extUserOrgMappingService.saveAssignOrgRoleDtls(extUserMgmntReq);
	}

	/**
	 * Method Name: getAssignOrgRoleDtls Method Description:This method is used
	 * to retrieve the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "getAssignOrgRoleDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes getAssignOrgRoleDtls(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to get the assign org & role
		// details.
		return extUserOrgMappingService.getAssignOrgRoleDtls(extUserMgmntReq);
	}

	/**
	 * Method Name: getExtUserOrgMappingHistory Method Description: Rest
	 * Controller method to get External user organization role mapping history
	 * 
	 * @param searchReq
	 * @return response
	 */
	@RequestMapping(value = "/getExtUserOrgMappingHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserOrgMappingHistoryRes getExtUserOrgMappingHistory(
			@RequestBody ExtUserOrgMappingHistoryReq req) {
		if (ObjectUtils.isEmpty(req.getIdExtUserOrgLink())) {
			// If no id Person, Throw invalid req as idPerson is not found.
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrg.idExtUserOrgLink.mandatory", null, Locale.US));
		}

		// If idPerson available then get the list
		return extUserOrgMappingService.getExtUserOrgMappingHistory(req);
	}

	/**
	 * Method Name: deleteExtUserOrgResourceLink. Method Description: Rest
	 * Controller method to delete external user associations . if idPerson
	 * passed in, will delete all associations (Organizations,Resources,Roles).
	 * if idExtUserOrgLink passed in, will delete only associations related to
	 * that organization , resources and roles. if idOrgRoleDtl passed in,will
	 * delete the respective role for the user. if idExtUserRsrcLink passed
	 * in,will delete the respective role for the user.
	 * 
	 * @param searchReq
	 * @return response
	 */
	@RequestMapping(value = "/deleteExtUserOrgResourceLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceResHeaderDto deleteExtUserOrgResourceLink(
			@RequestBody ExtUserOrgResourceLinkDelReq req) {
		if (ObjectUtils.isEmpty(req.getIdPerson()) && ObjectUtils.isEmpty(req.getIdExtUserOrgLink())
				&& ObjectUtils.isEmpty(req.getIdOrgRoleDtl()) && ObjectUtils.isEmpty(req.getIdExtUserRsrcLink())) {
			// If no id Person, Throw invalid req as idPerson is not found.
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrg.idDeletion.mandatory", null, Locale.US));
		}

		// If idPerson available then get the list
		return extUserOrgMappingService.deleteExtUserOrgResourceLink(req);
	}

	/**
	 * Method Name: saveExtUserRsrcLink Method Description: Controller method
	 * for Save External User Resource Link
	 * 
	 * @param req
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "/saveExtUserRsrcLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes saveExtUserRsrcLink(@RequestBody ExtUserMgmntReq req) {
		if (ObjectUtils.isEmpty(req.getExtOrgRoleMappingDto().getOrgsList())) {
			// If the Org List in the Request is empty. Return the error
			// message.
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrg.userRsrcLink.mandatory", null, Locale.US));
		}
		// Save the UserResource Link if correct record is found.
		return extUserOrgMappingService.saveExtUserRsrcLink(req.getExtOrgRoleMappingDto().getOrgsList().get(0),
				req.getIdUser());
	}

	/**
	 * Method Name: getExtUserRsrcDtls Method Description:This method is used to
	 * retrieve the external user resource association details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "/getExtUserRsrcDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes getExtUserRsrcDtls(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to get the external user resource
		// association details.
		return extUserOrgMappingService.getExtUserRsrcDtls(extUserMgmntReq);
	}

	/**
	 * Method Name: deleteExtUserRsrcAssociation Method Description:This method
	 * is used to delete the external user to resource association.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "/deleteExtUserRsrcAssociation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes deleteExtUserRsrcAssociation(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		// Calling the service implementation to delete the external user
		// resource
		// association details.
		return extUserOrgMappingService.deleteExtUserRsrcAssociation(extUserMgmntReq);
	}

	/**
	 * Method Name: setExtrnlUserLoginAgrmntDtl Method Description:This method
	 * is used to update external user login agreement detail
	 * 
	 * @param commonHelperReq
	 * @return commonHelperRes
	 */
	@RequestMapping(value = "/setExtrnlUserLoginAgrmntDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes setExtrnlUserLoginAgrmntDtl(@RequestBody CommonHelperReq commonHelperReq) {
		// Calling the service implementation to update agreement accepted
		// detail
		return extUserOrgMappingService.setExtrnlUserLoginAgrmntDtl(commonHelperReq);
	}

	/**
	 * Method Name: checkExtUserBgCheckClear Method Description:This method is
	 * used to check if the background check for the user is cleared and active.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@RequestMapping(value = "/checkExtUserBgCheckClear", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExtUserMgmntRes checkExtUserBgCheckClear(@RequestBody ExtUserMgmntReq extUserMgmntReq) {
		/*
		 * Calling the service implementation to check if the background check
		 * for the user is cleared and active.
		 */
		return extUserOrgMappingService.checkExtUserBgCheckClear(extUserMgmntReq);
	}

}
