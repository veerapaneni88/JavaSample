package us.tx.state.dfps.service.investigation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityInvCnclsnReq;
import us.tx.state.dfps.service.common.response.FacilityInvCnclsnRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.FacilityInvCnclsnService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the REST service requests for the Facility Investigation
 * Conclusion screen and Investigation Conclusion Provider screen. May 24, 2018-
 * 2:58:00 PM © 2017 Texas Department of Family and Protective Services
 */
@RequestMapping("/facilityInvCnclsn")
@RestController
public class FacilityInvCnclsnController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FacilityInvCnclsnService facilityInvCnclsnService;

	private static final Logger log = Logger.getLogger(FacilityInvCnclsnController.class);

	/**
	 * Method Description: This service will retrieve the facility Investigation
	 * Resource Details for the given InvstRsrcLink ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getFacilityInvstRsrcLinkDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getFacilityInvstRsrcLinkDtls(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getFacilityInvstRsrcLinkDtls of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdInvstRsrcLink())) {
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvCnclsn.idInvstRsrcLink.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getFacilityInvstRsrcLinkDtls of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getFacilityInvstRsrcLink(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will retrieve the the facility
	 * associated with the allegation for the given Stage ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getAllegedFacilities", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getAllegedFacilitiesDtls(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getAllegedFacilitiesDtls of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getAllegedFacilitiesDtls of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getAllegedFacilitiesDtls(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will do the CRUD operations for facility
	 * to the link table as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/saveFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getsaveFacility(@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {

		log.debug("Entering method getsaveFacility of FacilityInvCnclsnController class");
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {
			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilResource()))
				throw new InvalidRequestException(
						messageSource.getMessage("facilityInvCnclsn.idFacilResource.mandatory", null, Locale.US));

			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto()))
				throw new InvalidRequestException(messageSource
						.getMessage("facilityInvCnclsn.facilityInvCnclsnDetailDto.mandatory", null, Locale.US));

			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getResourceAddressDto()))
				throw new InvalidRequestException(
						messageSource.getMessage("facilityInvCnclsn.resourceAddressDto.mandatory", null, Locale.US));

		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {

			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto()))
				throw new InvalidRequestException(messageSource
						.getMessage("facilityInvCnclsn.facilityInvCnclsnDetailDto.mandatory", null, Locale.US));

			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getResourceAddressDto()))
				throw new InvalidRequestException(
						messageSource.getMessage("facilityInvCnclsn.resourceAddressDto.mandatory", null, Locale.US));

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {
			if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilRsrcLink()))
				throw new InvalidRequestException(
						messageSource.getMessage("facilityInvCnclsn.idFacilInvstRsrcLink.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getsaveFacility of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getsaveFacility(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will assign and update the facility
	 * Overall Dispo as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/assignAndUpdateFacilOverallDispo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes assignAndUpdateFacilOverallDispo(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method assignAndUpdateFacilOverallDispo of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method assignAndUpdateFacilOverallDispo of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.assignAndUpdateFacilOverallDispo(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will save the facility to the link table
	 * as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getDeleteFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes deleteFacility(@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method deleteFacility of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilRsrcLink())) {
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvCnclsn.idFacilInvstRsrcLink.mandatory", null, Locale.US));
		}

		log.debug("Exiting method deleteFacility of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.deleteFacility(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will will verify any one of allegation
	 * is tied or not. as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getFacilityTiedToAllegation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getFacilityTiedToAllegation(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getFacilityTiedToAllegation of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getFacilityTiedToAllegation of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.facilityTiedToAllegation(facilityInvCnclsnReq);
	}

	/**
	 * Method Description: This service will will verify any one of allegation
	 * is tied or not. as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getNameTiedToFacWithAllegOfCRC", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getNameTiedToFacWithAllegOfCRC(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getNameTiedToFacWithAllegOfCRC of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getNameTiedToFacWithAllegOfCRC of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.nameTiedToFacWithAllegOfCRC(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getInvestigatedFacilityList Method Description:This method
	 * is invoked to retrieve the list of Providers in the Facility
	 * Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            list of providers.
	 * @return facilityInvCnclsnRes - This dto will hold the list of providers
	 *         in the Facility Investigation conclusion .
	 */
	@RequestMapping(value = "/getInvestigatedFacilityList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getInvestigatedFacilityList(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getInvestigatedFacilityList of FacilityInvCnclsnController class");
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		facilityInvCnclsnRes.setProviderList(
				facilityInvCnclsnService.getInvestigatedFacilitiesList(facilityInvCnclsnReq.getIdStage()));
		log.debug("Exiting method getInvestigatedFacilityList of FacilityInvCnclsnController class");
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This service will compare the Person and Facility
	 * Address and return the boolean value
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getcomparePersonAndFacilityAddress", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getcomparePersonAndFacilityAddress(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getcomparePersonAndFacilityAddress of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getcomparePersonAndFacilityAddress of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.comparePersonAndFacilityAddress(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getCurrentStagePriority Method Description:This method is
	 * used to retrieve the current priority of the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            current priority of the stage.
	 * @return FacilityInvCnclsnRes -This dto will hold the list of providers in
	 *         the Facility Investigation conclusion .
	 */
	@RequestMapping(value = "/getCurrentStagePriority", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getCurrentStagePriority(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getCurrentStagePriority of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.debug("Exiting method getCurrentStagePriority of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getCurrentStagePriority(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getApprovalStatusInfo Method Description:This method is used
	 * to get the approval status of the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            approval status info.
	 * @return FacilityInvCnclsnRes - This dto will hold the approval status
	 *         details for the Facility Investigation Conclusion.
	 */
	@RequestMapping(value = "/getApprovalStatusInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getApprovalStatusInfo(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getApprovalStatusInfo of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		log.debug("Exiting method getApprovalStatusInfo of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getApprovalStatusInfo(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getProgramAdmins Method Description:This method is used to
	 * get the Program Admins for the New EMR Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            Program Admin map .
	 * @return FacilityInvCnclsnRes - This dto will hold the Program Admins map
	 *         for the New EMR Investigation conclusion.
	 */
	@RequestMapping(value = "/getProgramAdmins", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getProgramAdmins(@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getProgramAdmins of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getProgramAdminsDtls(facilityInvCnclsnReq);

	}

	/**
	 * Method Description: This service will return the boolean as true if the
	 * linked facility type is Community Provider.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getLinkedFacilCommunityProvider", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getLinkedFacilCommunityProvider(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getLinkedFacilCommunityProvider of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.debug("Exiting method getLinkedFacilCommunityProvider of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getLinkedFacilityCommunityProvider(facilityInvCnclsnReq);
	}

	/**
	 * Method Description: This service will return the boolean as true if there
	 * is one value in the List that equals to 'Y' indicating that there is at
	 * least one person without an active Medicaid identifier
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@RequestMapping(value = "/getMedicaidIdentifierMissing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getMedicaidIdentifierMissing(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getMedicaidIdentifierMissing of FacilityInvCnclsnController class");
		if (TypeConvUtil.isNullOrEmpty(facilityInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		log.debug("Exiting method getMedicaidIdentifierMissing of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getMedicaidIdentifierMissing(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getReportableConductExists Method Description:This method is
	 * used to check if Reportable Conduct exists for the current stage in the
	 * list of Facilities in the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for checking if a
	 *            reportable conduct exists.
	 * @return FacilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         for reportable conduct exists.
	 */
	@RequestMapping(value = "/getReportableConductExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getReportableConductExists(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getReportableConductExists of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getReportableConductExists(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getFacilityInvCnclsn Method Description:This method is used
	 * to get the Facility Investigation conclusion details for the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter values for retrieving
	 *            the Facility Conclusion details.
	 * @return FacilityInvCnclsnRes - This dto will hold the values of Facility
	 *         Conclusion details.
	 */
	@RequestMapping(value = "/getFacilityInvCnclsn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getFacilityInvCnclsn(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getFacilityInvCnclsn of FacilityInvCnclsnController class");
		return facilityInvCnclsnService.getFacilityInvCnclsn(facilityInvCnclsnReq);
	}

	/**
	 * Method Name: getCommunityProviderExists Method Description:This method is
	 * used to check if a community provider exists in the list of Providers in
	 * the Facility Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters to check if a
	 *            community provider exists as part of the Facility
	 *            Investigation conclusion.
	 * @return facilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         whether a community provider exists or not.
	 */
	@RequestMapping(value = "/getCommunityProviderExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes getCommunityProviderExists(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method getCommunityProviderExists of FacilityInvCnclsnController class");
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		boolean communityProviderExists = facilityInvCnclsnService.checkCommunityProviderExists(facilityInvCnclsnReq);
		facilityInvCnclsnRes.setIndCommunityProviderExists(communityProviderExists);
		log.debug("Exiting method getCommunityProviderExists of FacilityInvCnclsnController class");
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: saveFacilityInvCnclsn Method Description:This method is used
	 * to save the Facility Investigation conclusion details.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the Facility Investigation conclusion
	 *            details to be saved.
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save of Facility Conclusion , whether the data was saved or some
	 *         error occurred.
	 */
	@RequestMapping(value = "/saveFacilityInvCnclsn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes saveFacilityInvCnclsn(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method saveFacilityInvCnclsn of FacilityInvCnclsnController class");
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		try {
			facilityInvCnclsnRes = facilityInvCnclsnService.saveFacilityInvCnclsn(facilityInvCnclsnReq);
		} catch (Exception e) {
			List<Integer> errorList = new ArrayList<Integer>();
			errorList.add(Messages.MSG_CMN_TMSTAMP_MISMATCH);
			facilityInvCnclsnRes.setErrorCodesList(errorList);
		}
		log.debug("Exiting method saveFacilityInvCnclsn of FacilityInvCnclsnController class");
		return facilityInvCnclsnRes;

	}

	/**
	 * Method Name: saveAndSubmitFacilityInvCnclsn Method Description:This
	 * method is used to save and submit the facility conclusion .
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the details to saved .
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save and submit of Facility Conclusion , whether the data was
	 *         saved or some error occurred.
	 */
	@RequestMapping(value = "/saveAndSubmitFacilityInvCnclsn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes saveAndSubmitFacilityInvCnclsn(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method saveAndSubmitFacilityInvCnclsn of FacilityInvCnclsnController class");
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		try {
			facilityInvCnclsnRes = facilityInvCnclsnService.saveAndSubmitFacilityInvCnclsn(facilityInvCnclsnReq);
		} catch (Exception e) {
			List<Integer> errorList = new ArrayList<Integer>();
			errorList.add(Messages.MSG_CMN_TMSTAMP_MISMATCH);
			facilityInvCnclsnRes.setErrorCodesList(errorList);
		}
		log.debug("Exiting method saveAndSubmitFacilityInvCnclsn of FacilityInvCnclsnController class");
		return facilityInvCnclsnRes;

	}

	/**
	 * Method Name: saveAndCloseFacilityInvCnclsn Method Description:This method
	 * is used to save the facility conclusion and close the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the details to saved .
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save and submit of Facility Conclusion , whether the data was
	 *         saved or some error occurred.
	 */
	@RequestMapping(value = "/saveAndCloseFacilityInvCnclsn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityInvCnclsnRes saveAndCloseFacilityInvCnclsn(
			@RequestBody FacilityInvCnclsnReq facilityInvCnclsnReq) {
		log.debug("Entering method saveAndCloseFacilityInvCnclsn of FacilityInvCnclsnController class");
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		try {
			facilityInvCnclsnRes = facilityInvCnclsnService.saveAndCloseFacilityInvCnclsn(facilityInvCnclsnReq);
		} catch (Exception e) {
			List<Integer> errorList = new ArrayList<Integer>();
			errorList.add(Messages.MSG_CMN_TMSTAMP_MISMATCH);
			facilityInvCnclsnRes.setErrorCodesList(errorList);
		}
		log.debug("Exiting method saveAndCloseFacilityInvCnclsn of FacilityInvCnclsnController class");
		return facilityInvCnclsnRes;

	}
	
	@RequestMapping(value = "/checkErrorDisplayAbuseForm", headers = {
	"Accept=application/json" }, method = RequestMethod.POST)
public @ResponseBody FacilityInvCnclsnRes checkErrorDisplayAbuseForm(
	@RequestBody FacilityInvCnclsnReq facilityIcnvCnclsnReq) {
		
		return facilityInvCnclsnService.checkErrorDisplayAbuseForm(facilityIcnvCnclsnReq);
	}
}
