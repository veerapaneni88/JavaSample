package us.tx.state.dfps.service.placement.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.request.PlacementValueReq;
import us.tx.state.dfps.service.common.request.SavePlacementDetailReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.HmEligibilityReq;
import us.tx.state.dfps.service.common.response.PlacementRes;
import us.tx.state.dfps.service.common.response.HmEligibilityRes;
import us.tx.state.dfps.service.common.response.SavePlacementDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

import javax.mail.MessagingException;
import java.util.Locale;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the implementation for Placement EJB Bean Oct 9, 2017- 5:30:02 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/placement")
public class PlacementController {

	@Autowired
	private PlacementService placementService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PlacementController.class);

	/**
	 * 
	 * Method Name: checkLegalAction Method Description:This method Checks for
	 * the proper Legal Action
	 * 
	 * @param placeReq
	 * @return placementRes placementRes
	 */
	@RequestMapping(value = "/checkLegalAction", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes checkLegalAction(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setBoolLegalAction(placementService.checkLegalAction(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * 
	 * Method Name: fetchPlacement Method Description:This method retrieves
	 * Placement Details from the database using idPlcmtEvent.
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/fetchPlacement", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes fetchPlacement(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idPlcmtEvent", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementDto(placementService.fetchPlacement(placeReq.getIdPlcmtEvent()));
		return placementRes;
	}

	@RequestMapping(value = "/getPlacementHistory", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getPlacementHistory(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idPerson", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.getPlacementHistory(placeReq.getIdPerson()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: fetchLatestPlacement Method Description:This method
	 * retrieves Latest Placement for the given
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/fetchLatestPlacement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes fetchLatestPlacement(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementDto(placementService.fetchLatestPlacement(placeReq.getIdStage()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: findActivePlacements Method Description:Fetches the most
	 * recent open Active Placement for the idPerson
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/findActivePlacements", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes findActivePlacements(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.findActivePlacements(placeReq.getIdPerson()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: checkPlcmtDateRange Method Description:This method checks if
	 * there is any Placement for the Child in the Range of Placement Start
	 * Date.
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/checkPlcmtDateRange", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes checkPlcmtDateRange(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeReq.getDtPlcmtStart())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.dtPlcmtStart", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();

		placementRes.setPlacementValueDtoList(
				placementService.checkPlcmtDateRange(placeReq.getIdPerson(), placeReq.getDtPlcmtStart()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * 
	 * Method Name: findAllPlacementsForStage Method Description:This method
	 * returns all the placements for the given Stage
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/findAllPlacementsForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes findAllPlacementsForStage(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.findAllPlacementsForStage(placeReq.getIdStage()));
		return placementRes;
	}


	@RequestMapping(value = "/findQRTPPlacementsPeriod", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes findQRTPPlacementsPeriod(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("placeReq.idPerson.mandatory", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.findAllQTRPPlacements(placeReq.getIdPerson()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getContractedSvcLivArr Method Description:This method
	 * returns an active SIL contract service to filter the living arrangement
	 * for SSCC SIL placement
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getContractedSvcLivArr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getContractedSvcLivArr(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.getContractedSvcLivArr(placeReq.getIdResource()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getResourceSvcLivArr Method Description:This method returns
	 * List of SIL resource services to filter the living arrangement for SIL
	 * placement
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getResourceSvcLivArr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getResourceSvcLivArr(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getResourceSvcLivArr(placeReq.getIdResource()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getActiveSilContract Method Description:This method returns
	 * an active SIL
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getActiveSilContract", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getActiveSilContract(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getActiveSilContract(placeReq.getIdResource()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 *
	 * Method Name: getAllContractPeriods Method Description:This method returns
	 * all contract periods
	 *
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getAllContractPeriods", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getAllContractPeriods(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getAllContractPeriods(placeReq.getIdResource()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getCorrespondingPlacement Method Description:This method
	 * returns List corresponding parent placement for the child within a stage
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getCorrespondingPlacement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getCorrespondingPlacement(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.getCorrespondingPlacement(placeReq.getIdStage()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getContractCounty Method Description:This method gets
	 * address county in SIL Contract services
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getContractCounty", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getContractCounty(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setTransactionId(placeReq.getTransactionId());
		placementRes.setPlacementValueDtoList(placementService.getContractCounty(placeReq.getPlacementValueDto()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getAddCntInSilRsrc Method Description:This method gets
	 * address county in SIL resource services
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getAddCntInSilRsrc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getAddCntInSilRsrc(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeReq.getCountyCode())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.SzCountyCode", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeReq.getLivArr())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.LivArr", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getAddCntInSilRsrc(placeReq.getIdResource(),
				placeReq.getCountyCode(), placeReq.getLivArr()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * Method Name: isSSCCPlacement Method Description:This method checks to see
	 * if the placement is a SSCC placement
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/isSSCCPlacement", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes isSSCCPlacement(@RequestBody PlacementReq placeReq) {
		PlacementRes placementRes = new PlacementRes();
		placementRes.setBoolLegalAction(placementService.isSSCCPlacement(placeReq.getIdPlcmtEvent()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}

	/**
	 * 
	 * Method Name: createExceptCareAlert Method Description:This method to
	 * create an alert to do to staff who created Exception Care
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/createExceptCareAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes createExceptCareAlert(@RequestBody PlacementValueReq placeValueReq) {
		log.debug("Entering method createExceptCareAlert in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.SzCdTask", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.UserId", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.SzCdStage", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getNmStage())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.SzNmStage", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setTotalRecCount(placementService.createExceptCareAlert(placeValueReq.getIdEvent(),
				placeValueReq.getCdTask(), placeValueReq.getIdStage(), placeValueReq.getIdUser(),
				placeValueReq.getCdStage(), placeValueReq.getNmStage()));
		placementRes.setTransactionId(placeValueReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeValueReq.getTransactionId());
		log.debug("Exiting method createExceptCareAlert in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: isActSsccCntrctExist Method Description:This method gets
	 * active SSCC contract services for the catchment area
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/isActSsccCntrctExist", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes isActSsccCntrctExist(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.isActSsccCntrctExist(placeReq));
		return placementRes;
	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:This method
	 * gets valid child placement referral information for the stage id (an
	 * active referral here is not base on the status, it's base on the referral
	 * recorded and discharged dates.
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getActiveChildPlcmtReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getActiveChildPlcmtReferral(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.getActiveChildPlcmtReferral(placeReq.getIdStage()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateIndPlcmtSSCC Method Description:This method updates
	 * indicator placement sscc
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/updateIndPlcmtSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateIndPlcmtSSCC(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateIndPlcmtSSCC in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateIndPlcmtSSCC(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateIndPlcmtSSCC in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateIdPlcmtSSCC Method Description:This method updates id
	 * placement sscc
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/updateIdPlcmtSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateIdPlcmtSSCC(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateIdPlcmtSSCC in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateIdPlcmtSSCC(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateIdPlcmtSSCC in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateChildPlanDue Method Description:This method updates id
	 * placement sscc
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/updateChildPlanDue", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateChildPlanDue(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateChildPlanDue in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateChildPlanDue(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateChildPlanDue in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateIndEfcActive Method Description:This method updates
	 * indicator EFC Active
	 * 
	 * @param placeReq
	 * @return placementRes
	 */

	@RequestMapping(value = "/updateIndEfcActive", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateIndEfcActive(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateIndEfcActive in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateIndEfcActive(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateIndEfcActive in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateIndEfc Method Description:This method updates
	 * indicator EFC
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/updateIndEfc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateIndEfc(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateIndEfc in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateIndEfc(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateIndEfc in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: updateIndLinkedPlcmtData Method Description:This method
	 * updates indicator linked placement data
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/updateIndLinkedPlcmtData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes updateIndLinkedPlcmtData(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method updateIndLinkedPlcmtData in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getPlacementValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.PlacementValueDto", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.updateIndLinkedPlcmtData(placeReq.getPlacementValueDto()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method updateIndLinkedPlcmtData in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getIndPlcmtSSCC Method Description:This method gets indicate
	 * placement sscc
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getIndPlcmtSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getIndPlcmtSSCC(@RequestBody PlacementValueReq placeValueReq) {
		log.debug("Entering method getIndPlcmtSSCC in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdReferral())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.IdReferral", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.getIndPlcmtSSCC(placeValueReq.getIdReferral()));
		placementRes.setTransactionId(placeValueReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeValueReq.getTransactionId());
		log.debug("Exiting method getIndPlcmtSSCC in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getActiveSSCCReferral Method Description:This method gets
	 * active sscc referral for stage id
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getActiveSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getActiveSSCCReferral(@RequestBody PlacementValueReq placeValueReq) {
		log.debug("Entering method getActiveSSCCReferral in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.getActiveSSCCReferral(placeValueReq.getIdStage()));
		log.info(ServiceConstants.TRANSACTION_ID + placeValueReq.getTransactionId());
		placementRes.setTransactionId(placeValueReq.getTransactionId());
		log.debug("Exiting method getActiveSSCCReferral in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: childLastestPlcmtSSCC Method Description:This method gets
	 * child latest placement sscc
	 * 
	 * @param placeReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/childLastestPlcmtSSCC", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes childLastestPlcmtSSCC(@RequestBody PlacementReq placeReq) {
		log.debug("Entering method childLastestPlcmtSSCC in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placeReq.getDtPlcmtStart())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idPlcmtEvent", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(
				placementService.childLastestPlcmtSSCC(placeReq.getIdStage(), placeReq.getDtPlcmtStart()));
		placementRes.setTransactionId(placeReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + placeReq.getTransactionId());
		log.debug("Exiting method childLastestPlcmtSSCC in PlacementController");
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets
	 * the latest child plan initiate info within a stage at the time of
	 * approval of an sscc placement
	 * 
	 * @param placeValueReq
	 * @return placementRes 
	 */
	@RequestMapping(value = "/getChildPlanInitiateInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getChildPlanInitiateInfo(@RequestBody PlacementValueReq placeValueReq) {
		log.debug("PlacementController.getChildPlanInitiateInfo()");
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placeValueReq.IdReferral.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.getChildPlanInitiateInfo(placeValueReq.getIdReferral()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getExceptionalCareDaysUsed Method Description:This method
	 * gets the number of exceptional care used in a contract period
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/getExceptionalCareDaysUsed", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getExceptionalCareDaysUsed(@RequestBody PlacementValueReq placeValueReq) {
		if (TypeConvUtil.isNullOrEmpty(placeValueReq.getIdReferral())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.IdReferral", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setNbrECDaysUsed(placementService.getExceptionalCareDaysUsed(placeValueReq.getIdReferral()));
		return placementRes;
	}

	/**
	 * 
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets
	 * the latest child plan initiate info within a stage at the time of
	 * approval of an sscc placement
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/savePlacementDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SavePlacementDtlRes saveOrUpdatePlacementDtl(
			@RequestBody SavePlacementDetailReq savePlacementDtlReq) {
		log.debug("Entering method getChildPlanInitiateInfo in PlacementController");
		if (TypeConvUtil.isNullOrEmpty(savePlacementDtlReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("event.id.mandatory", null, Locale.US));
		}

		SavePlacementDtlRes savePlacementDtlRes = new SavePlacementDtlRes();
		try {
			savePlacementDtlRes = placementService.saveOrUpdatePlacementDtl(savePlacementDtlReq);
		}catch(MessagingException e)
		{
			log.error(e.getMessage());
		}
		savePlacementDtlRes.setTransactionId(savePlacementDtlReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + savePlacementDtlReq.getTransactionId());
		log.debug("Exiting method getChildPlanInitiateInfo in PlacementController");
		return savePlacementDtlRes;
	}

	/**
	 * Method Name: createIcpcRejectToDo Method Description: This Service is
	 * used to create Todo for ICPC Reject
	 * 
	 * @param icpcPlacementReq
	 * @return
	 * 
	 * 
	 */
	@RequestMapping(value = "/createIcpcRejectToDo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes createIcpcRejectToDo(@RequestBody PlacementValueReq icpcPlacementReq) {

		PlacementRes icpcPlacementRes = new PlacementRes();
		if (TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdEvent())
				|| TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getCdTask())
				|| TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdStage())
				|| TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdUser())
				|| TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		placementService.createIcpcRejectToDo(icpcPlacementReq.getIdEvent(), icpcPlacementReq.getCdTask(),
				icpcPlacementReq.getIdStage(), icpcPlacementReq.getIdUser(), icpcPlacementReq.getCdStage());

		icpcPlacementRes.setTotalRecCount(0L);

		return icpcPlacementRes;
	}

	/**
	 * Method Name: alertPlacementReferral Method Description:This method is
	 * called in save method of placement detail an legal status page to create
	 * alert for the primary assigned caseworker to complete the 2077 Referral
	 * within 7 days of when a child Placement is entered and saved.
	 * 
	 * @param alertPlacementLsDto
	 */
	@RequestMapping(value = "/alertPlacementReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  void alertPlacementReferral(@RequestBody AlertPlacementLsDto alertPlacementLsDto) {
		log.debug("Entering method updateIdPlcmtSSCC in PlacementController");
		placementService.alertPlacementReferral(alertPlacementLsDto);
	}

	/**
	 * Method Name: getLatestPlcmntEvent Method Description: This method is to
	 * retrieve the latest placement event.
	 * 
	 * @param eventDto
	 * @return
	 */
	@RequestMapping(value = "/getLatestPlcmntEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventDto getLatestPlcmntEvent(@RequestBody EventDto eventDto) {
		log.debug("Entering method getLatestPlcmntEvent in PlacementController");
		return placementService.getLatestPlcmntEvent(eventDto);
	}

	/**
	 * 
	 * Method Name: getExceptionalCareDaysUsed Method Description:This method
	 * gets the number of exceptional care used in a contract period
	 * 
	 * @param placeValueReq
	 * @return placementRes
	 */
	@RequestMapping(value = "/chckPlcmntEndedOrNot", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes chckPlcmntEndedOrNot(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return placementService.chckPlcmntEndedOrNot(commonHelperReq.getIdEvent());
	}
	
	/**
	 * 
	 * Method Name: getActiveSilContract Method Description:This method returns an active SIL
	 * 
	 * @param placeReq
	 * @return placementRes
	 * @throws InvalidRequestException
	 * @
	 */
	@RequestMapping(value = "/getActiveTepContract", headers = {
	        "Accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody PlacementRes getActiveTepContract(@RequestBody PlacementReq placeReq) {
		if (TypeConvUtil.isNullOrEmpty(placeReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.idResource", null, Locale.US));
		}
		
		if (Boolean.FALSE.equals(placeReq.getTepPlacement()) && Boolean.FALSE.equals(placeReq.getTfcPlacement())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.type", null, Locale.US));
		}
		
		if (TypeConvUtil.isNullOrEmpty(placeReq.getTepPlacement()) && TypeConvUtil.isNullOrEmpty(placeReq.getTfcPlacement())) {
			throw new InvalidRequestException(messageSource.getMessage("placement.type", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDto(placementService.getActiveTepContract(placeReq));
		placementRes.setTransactionId(placeReq.getTransactionId());
		return placementRes;
	}
	
	/**
	 * Method Name: getCountOfActiveTfcPlmnts Method Description: This method returns tfc children count.
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getCountOfActiveTfcPlmnts", headers = {
	        "Accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody CommonCountRes getCountOfActiveTfcPlmnts(@RequestBody PlacementReq placementReq)
	        {
		
		if (TypeConvUtil.isNullOrEmpty(placementReq.getIdResource())) {
			throw new InvalidRequestException(
			        messageSource.getMessage("placement.resourceId.mandatory", null, Locale.US));
		}
		
		CommonCountRes commonCountRes = placementService.getCountOfActiveTfcPlmnts(placementReq);
		
		return commonCountRes;
	}
	
	
	/**
	 * Method Name: getCountOfAllPlacements Method Description: This method returns active placed children count.
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getCountOfAllPlacements", headers = {
	        "Accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody CommonCountRes getCountOfAllPlacements(@RequestBody PlacementReq placementReq)
	        {
		if (TypeConvUtil.isNullOrEmpty(placementReq.getIdResource())) {
			throw new InvalidRequestException(
			        messageSource.getMessage("placement.resourceId.mandatory", null, Locale.US));
		}
		
		CommonCountRes commonCountRes = placementService.getCountOfAllPlacements(placementReq);

		return commonCountRes;
		
      }

	@RequestMapping(value = "/getChildPlcmtReferrals", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getChildPlcmtReferrals(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getChildPlcmtReferrals(placeReq.getIdStage()));
		return placementRes;
	}

	@RequestMapping(value = "/chkValidFPSContractRsrc/{idResource}/{idRsrcSscc}", headers = {
			"Accept=application/json" }, method = RequestMethod.GET)
	public boolean chkValidFPSContractRsrc(@PathVariable(value = "idResource") Long idResource, @PathVariable(value= "idRsrcSscc") Long idRsrcSscc) {
		boolean result;
		if (!ObjectUtils.isEmpty(idResource) && !ObjectUtils.isEmpty(idRsrcSscc)) {
			result = placementService.chkValidFPSContractRsrc(idResource, idRsrcSscc);
		} else {
			throw new InvalidRequestException("idResource and idRsrcSscc are mandatory");
		}
		return result;
	}

	@RequestMapping(value = "/getChildPlacement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getChildPlacement(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getChildPlacement(placeReq.getIdPerson(), placeReq.getIdStage()));
		return placementRes;
	}

	@RequestMapping(value = "/getAddOnSvcPkg", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes getAddOnSvcPkg(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(placeReq.getIdPerson())) {
			throw new ServiceLayerException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonBooleanRes response = new CommonBooleanRes();
		response.setExists(placementService.getAddOnSvcPkg(placeReq.getIdPerson(), placeReq.getIdStage()));
		return response;
	}

	@RequestMapping(value = "/checkPlacementHmEligibility", headers = {
			"Accept=application/json"}, method = RequestMethod.POST)
	public HmEligibilityRes checkPlacementHmEligibility(@RequestBody HmEligibilityReq placeReq) {
		return placementService.checkPlacementHmEligibility(placeReq);
	}

	@RequestMapping(value = "/getParentPlacement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getParentPlacement(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PlacementRes placementRes = new PlacementRes();
		placementRes.setPlacementValueDtoList(placementService.getParentPlacement(placeReq.getIdPerson(), placeReq.getIdStage()));
		return placementRes;
	}

	@RequestMapping(value = "/getCountCPBPlcmntsForYouthParent", headers = {
			"Accept=application/json"}, method = RequestMethod.POST)
	public @ResponseBody CommonCountRes getCountCPBPlcmntsForYouthParent(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdStage())) {
			throw new ServiceLayerException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(placeReq.getIdPerson())) {
			throw new ServiceLayerException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		CommonCountRes commonCountRes = placementService.getCountCPBPlcmntsForYouthParent(placeReq.getIdPerson(), placeReq.getIdStage(), placeReq.getDtPlcmtStart());
		return commonCountRes;
	}

	@RequestMapping(value = "/checkAlocBlocForNonT3cPlcmt", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes checkAlocBlocForNonT3cPlcmt(@RequestBody PlacementReq placeReq) {
		if (ObjectUtils.isEmpty(placeReq.getIdCase())) {
			throw new ServiceLayerException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(placeReq.getDtPlcmtStart())) {
			throw new ServiceLayerException(messageSource.getMessage("common.placement.start.date.mandatory", null, Locale.US));
		}
		CommonBooleanRes response = new CommonBooleanRes();
		response.setExists(placementService.checkAlocBlocForNonT3cPlcmt(placeReq.getIdCase(), placeReq.getDtPlcmtStart()));
		return response;
	}
}
