package us.tx.state.dfps.service.dcr.controller;

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

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.request.SSCCDayCareRequestReq;
import us.tx.state.dfps.service.common.request.TWCTransmissionReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.common.response.SSCCDayCareRequestRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.service.DayCareRequestService;
import us.tx.state.dfps.service.sscc.service.SSCCRefService;
import us.tx.state.dfps.service.workload.dto.EventIdDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Day Care
 * Request Service Controller Mar 28, 2018- 11:44:21 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@RestController
@RequestMapping("/dayCareRequest")
public class DayCareRequestController {
	@Autowired
	MessageSource messageSource;
	@Autowired
	DayCareRequestService dayCareRequestService;

	@Autowired
	SSCCRefService sSCCRefService;

	private static final Logger log = Logger.getLogger(DayCareRequestController.class);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuthPersonDtl Method Description:
	 * Retrieve the person details related to DayCareRequest for Service Auth
	 * Detail
	 * 
	 * @param dayCareEventDto
	 *            - Day Care Service Authorization event and Id Stage
	 * @return DayCareRequestValueDto - contains List of DayCarePersonDto which
	 *         contains list of person having day care
	 */
	@RequestMapping(value = "/rtrvDayCareReqSvcAuthPrsnDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrieveDayCareRequestSvcAuthPersonDtl(
			@RequestBody EventIdDto dayCareEventDto) {
		if (TypeConvUtil.isNullOrEmpty(dayCareEventDto.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareEventDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCarePersonDtos(dayCareRequestService
				.retrieveDayCareRequestSvcAuthPersonDtl(dayCareEventDto.getIdEvent(), dayCareEventDto.getIdStage()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuth Method Description: Retrieve
	 * the daycarerequest event id from dayCare_svc_auth_link table
	 * 
	 * @param svcAuthEvent
	 *            - Service Authorization event id
	 * @return dayCareEventDto - Day care event Id
	 */
	@RequestMapping(value = "/retrieveDayCareRequestSvcAuth", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes retrieveDayCareRequestSvcAuth(@RequestBody EventIdDto svcAuthEvent) {
		if (TypeConvUtil.isNullOrEmpty(svcAuthEvent.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		EventIdDto dayCareEventDto = dayCareRequestService.retrieveDayCareRequestSvcAuth(svcAuthEvent.getIdEvent());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIdEvent(dayCareEventDto.getIdEvent());
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: saveDayCareRequestDetail Method Description:This method will
	 * insert/update the DAYCARE_REQUEST table
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/saveDayCareRequestDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes saveDayCareRequestDetail(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.dayCareRequestValueDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getSaveType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.SaveType.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		dayCareRequestRes.setIdDayCareReqEvent(dayCareRequestService.saveDayCareRequestDetail(
				dayCareRequestReq.getDayCareRequestValueDto(), dayCareRequestReq.getSaveType()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: saveDayCarePersonInfo Method Description:This method
	 * saves(insert/update) Day Care Request Person Information into the
	 * database.
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/saveDayCarePersonInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes saveDayCarePersonInfo(@RequestBody DayCareRequestReq dayCareRequestReq){
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.dayCareRequestValueDto.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		dayCareRequestRes.setIdDaycarePersonLink(
				dayCareRequestService.saveDayCarePersonInfo(dayCareRequestReq.getDayCareRequestValueDto()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: updateSSCCDayCareRequest Method Description:This method
	 * updates SSCC DayCare Request table.
	 * 
	 * @param ssccDayCareReqReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/updateSSCCDayCareRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes updateSSCCDayCareRequest(
			@RequestBody SSCCDayCareRequestReq ssccDayCareReqReq) {
		if (TypeConvUtil.isNullOrEmpty(ssccDayCareReqReq.getsSCCDayCareRequestDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.SSCCDayCareRequestDto.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(ssccDayCareReqReq.getTransactionId());
		dayCareRequestRes.setIdSSCCDayCareRequest(
				dayCareRequestService.updateSSCCDayCareRequest(ssccDayCareReqReq.getsSCCDayCareRequestDto()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: updateDayCarePersonLink Method Description:This method is to
	 * update Daycare Person Link table
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/updateDayCarePersonLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes updateDayCarePersonLink(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.dayCareRequestValueDto.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		dayCareRequestRes.setTotalRecCount(
				dayCareRequestService.updateDayCarePersonLink(dayCareRequestReq.getDayCareRequestValueDto()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: retrievePersonDayCareDetails Method Description:Retrieve all
	 * valid daycare requests for input person
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/retrievePersonDayCareDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrievePersonDayCareDetails(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.dayCareRequestValueDto.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + dayCareRequestReq.getTransactionId());
		dayCareRequestRes.setDayCareRequestValueDto(
				dayCareRequestService.retrievePersonDayCareDetails(dayCareRequestReq.getDayCareRequestValueDto()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: createDayCareRejectAlert Method Description:this method
	 * returns approvers count
	 * 
	 * @param twcTransmissionReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/createDayCareRejectAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes createDayCareRejectAlert(
			@RequestBody TWCTransmissionReq twcTransmissionReq) {

		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.UserId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getSzCdTask())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.SzCdTask.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getSzCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.SzCdStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getSzNmStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.SzNmStage.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(twcTransmissionReq.getTransactionId());
		dayCareRequestRes.setTotalRecCount(dayCareRequestService.createDayCareRejectAlert(
				twcTransmissionReq.getIdEvent(), twcTransmissionReq.getSzCdTask(), twcTransmissionReq.getIdStage(),
				twcTransmissionReq.getIdUser(), twcTransmissionReq.getSzCdStage(), twcTransmissionReq.getSzNmStage()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: updateApproversStatus Method Description:Updates the
	 * Approvers Status to Invalid.
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/updateApproversStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes updateApproversStatus(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setIdApproval(dayCareRequestService.updateApproversStatus(dayCareRequestReq.getIdEvent()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: updateAppEventStatus Method Description:Updates the APP
	 * event to Invalid.
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/updateAppEventStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes updateAppEventStatus(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		dayCareRequestRes.setIdEvent(dayCareRequestService.updateAppEventStatus(dayCareRequestReq.getIdEvent()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: createDayCareTerminationAlert Method Description:This method
	 * creates Alert for DayCare Coordinator when the Service Authorization is
	 * Terminated
	 * 
	 * @param twcTransmissionReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/createDayCareTerminationAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes createDayCareTerminationAlert(
			@RequestBody TWCTransmissionReq twcTransmissionReq) {
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.UserId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdChild())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdChild.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdSvcAuth())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuth.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(twcTransmissionReq.getTransactionId());
		dayCareRequestRes.setIdperson(dayCareRequestService.createDayCareTerminationAlert(
				twcTransmissionReq.getIdEvent(), twcTransmissionReq.getIdStage(), twcTransmissionReq.getIdUser(),
				twcTransmissionReq.getIdChild(), twcTransmissionReq.getIdSvcAuth()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idEvent.
	 * 
	 * @param dayCareRequestReq
	 * @return ssccDayCareRequestRes
	 */
	@RequestMapping(value = "/retrieveSSCCDayCareRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCDayCareRequestRes retrieveSSCCDayCareRequest(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		SSCCDayCareRequestRes ssccDayCareRequestRes = new SSCCDayCareRequestRes();
		ssccDayCareRequestRes.setsSCCDayCareRequestDto(
				dayCareRequestService.retrieveSSCCDayCareRequest(dayCareRequestReq.getIdEvent()));
		return ssccDayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: deleteDayCareRequest Method Description:If DFPS Worker
	 * deletes DFPS DayCare Request Follow the current process of using DayCare
	 * Request Complex delete.
	 * 
	 * If DFPS Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables except for SSCC_DAYCARE_REQUEST table. set
	 * ID_DAYCARE_REQUEST = 0 in SSCC_DAYCARE_REQUEST table.
	 * 
	 * If SSCC Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables including SSCC_DAYCARE_REQUEST table.
	 * 
	 * @param dayCareRequestReq
	 * @return ssccDayCareRequestRes
	 */
	@RequestMapping(value = "/deleteDayCareRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCDayCareRequestRes deleteDayCareRequest(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIsSSCCWorker())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IsSSCCWorker.mandatory", null, Locale.US));
		}
		SSCCDayCareRequestRes ssccDayCareRequestRes = new SSCCDayCareRequestRes();
		ssccDayCareRequestRes.setsSCCDayCareRequestDto(dayCareRequestService
				.deleteDayCareRequest(dayCareRequestReq.getIdEvent(), dayCareRequestReq.getIsSSCCWorker()));
		return ssccDayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: fetchActiveReferralForChild Method Description:This function
	 * returns Active SSCC Referral for the Child.
	 * 
	 * @param dayCareRequestReq
	 * @return ssccDayCareRequestRes
	 */
	@RequestMapping(value = "/fetchActiveReferralForChild", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCDayCareRequestRes fetchActiveReferralForChild(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		SSCCDayCareRequestRes ssccDayCareRequestRes = new SSCCDayCareRequestRes();
		ssccDayCareRequestRes.setIdActiveReferral(
				dayCareRequestService.fetchActiveReferralForChild(dayCareRequestReq.getIdPerson()));
		return ssccDayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: fetchReferralsForAllPersonsInDaycareRequest Method
	 * Description:This function returns All the Referrals(Active and Inactive)
	 * for the DayCare Request.
	 * 
	 * @param twcTransmissionReq
	 * @return ssccDayCareRequestRes
	 */
	@RequestMapping(value = "/fetchReferralsForAllPersonsInDaycareRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCDayCareRequestRes fetchReferralsForAllPersonsInDaycareRequest(
			@RequestBody TWCTransmissionReq twcTransmissionReq) {
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdDayCareRequest())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdDayCareRequest.mandatory", null, Locale.US));
		}

		SSCCDayCareRequestRes ssccDayCareRequestRes = new SSCCDayCareRequestRes();
		ssccDayCareRequestRes.setTransactionId(twcTransmissionReq.getTransactionId());
		ssccDayCareRequestRes.setSsccRefDtoList(dayCareRequestService
				.fetchReferralsForAllPersonsInDaycareRequest(twcTransmissionReq.getIdDayCareRequest()));
		return ssccDayCareRequestRes;
	}

	/**
	 * Method Name: createTWCTransmissionAlert Method Description:This function
	 * creates Alert to RDCC notifying them that the Day Care Service
	 * Authorization has been approved but not transmitted to TWC.
	 * 
	 * @param twcTransmissionReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/createTWCTransmissionAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes createTWCTransmissionAlert(
			@RequestBody TWCTransmissionReq twcTransmissionReq){
		log.debug("Entering method createTWCTransmissionAlert in DayCareRequestController");
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdSvcAuthEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.UserId.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(twcTransmissionReq.getTransactionId());
		dayCareRequestRes
				.setIdperson(dayCareRequestService.createTWCTransmissionAlert(twcTransmissionReq.getIdSvcAuthEvent(),
						twcTransmissionReq.getIdStage(), twcTransmissionReq.getIdUser()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: createTWCTransmissionFailureAlert Method Description:This
	 * function creates Alert to the RDCC and the CaseWorker notifying them of
	 * the failed transmission to TWC
	 * 
	 * @param connectionDto
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/createTWCTransmissionFailureAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes createTWCTransmissionFailureAlert(
			@RequestBody TWCTransmissionReq twcTransmissionReq) {
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdSvcAuthEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.UserId.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(twcTransmissionReq.getTransactionId());
		dayCareRequestRes.setIdperson(
				dayCareRequestService.createTWCTransmissionFailureAlert(twcTransmissionReq.getIdSvcAuthEvent(),
						twcTransmissionReq.getIdStage(), twcTransmissionReq.getIdUser()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: createTWCTransmissionTerminateAlert Method Description:This
	 * function creates Alert to the User notifying them that the Approved Day
	 * Care Service Authorization has been terminated and Alert should be sent
	 * to the user.
	 * 
	 * @param twcTransmissionReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/createTWCTransmissionTerminateAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes createTWCTransmissionTerminateAlert(
			@RequestBody TWCTransmissionReq twcTransmissionReq){
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdSvcAuthEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(twcTransmissionReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.UserId.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTodoExists(
				dayCareRequestService.createTWCTransmissionTerminateAlert(twcTransmissionReq.getIdSvcAuthEvent(),
						twcTransmissionReq.getIdStage(), twcTransmissionReq.getIdUser()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: retrieveDayCarePersonLink Method Description: This is
	 * Controller for retrieving Day care Person by day care request id
	 * 
	 * @param dayCareRequestReq
	 * @return DayCareRequestRes
	 */
	@RequestMapping(value = "/retrieveDayCarePersonLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrieveDayCarePersonLink(@RequestBody DayCareRequestReq dayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCarePersonDtos(
				dayCareRequestService.retrieveDayCarePersonLink(dayCareRequestReq.getIdDayCareRequest()));
		return dayCareRequestRes;
	}

	/**
	 * Method Name: hasChangedSystemResponses Method Description: This is
	 * controller for has Changed System Responses Service
	 * 
	 * @param dayCareRequestReq
	 * @return DayCareRequestRes
	 */
	@RequestMapping(value = "/hasChangedSystemResponses", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes hasChangedSystemResponses(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareRequest())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setHasChangedSystemResponses(dayCareRequestService
				.hasChangedSystemResponses(dayCareRequestReq.getIdPerson(), dayCareRequestReq.getIdDayCareRequest()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestDetail Method Description:This method
	 * fetch Day Care Request Details based on the eventid
	 * 
	 * @param DayCareRequestReq
	 * @return dayCareRequestRes @
	 */
	@RequestMapping(value = "/retrieveDayCareRequestDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrieveDayCareRequestDetail(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCareRequestValueDto(
				dayCareRequestService.retrieveDayCareRequestDetail(dayCareRequestReq.getIdEvent()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: isDayCareRequestLinkedToServiceAuth Method Description:
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	@RequestMapping(value = "/isDayCareRequestLinkedToServiceAuth", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes isDayCareRequestLinkedToServiceAuth(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
			// change
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setIsDayCareRequestLinkedToServiceAuth(
				dayCareRequestService.isDayCareRequestLinkedToServiceAuth(dayCareRequestReq.getIdEvent()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: generateServiceAuth Method Description: Service Controller
	 * to generate service Auth
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	@RequestMapping(value = "/generateServiceAuth", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes generateServiceAuth(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdSvcAuthEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idUser.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setIdDaycareSvcAuthLink(dayCareRequestService.generateServiceAuth(
				dayCareRequestReq.getIdDayCareEvent(), dayCareRequestReq.getIdUser(),
				dayCareRequestReq.getDayCareRequestValueDto().getEventValueDto()));
		return dayCareRequestRes;
	}

	/**
	 * Method Name: userSSCC Method Description: To find the login user is a
	 * sscc user or not
	 * 
	 * @param dayCareRequestReq
	 * @return commonHelperRes
	 */
	@RequestMapping(value = "/userSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes userSSCC(@RequestBody DayCareRequestReq dayCareRequestReq) {
		Long idUser = dayCareRequestReq.getIdUser();
		if (TypeConvUtil.isNullOrEmpty(idUser)) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idUser.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		boolean ssccUser = sSCCRefService.isUserSSCC(idUser);
		commonHelperRes.setSsccUser(ssccUser);
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: getStaffInformation Method Description: To get staff
	 * information
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/getStaffInformation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes getStaffInformation(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setStaffDto(dayCareRequestService.getStaffInformation(dayCareRequestReq.getIdStage()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: getDayCareService Method Description:To get day care service
	 * 
	 * @param dayCareRequestReq
	 * @return commonHelperRes
	 */
	@RequestMapping(value = "/dayCareService", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getDayCareService(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareRequest())) {

			throw new InvalidRequestException(
					messageSource.getMessage("common.idDayCareRequest.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		boolean otherServices = dayCareRequestService.dayCareService(dayCareRequestReq.getIdDayCareRequest());
		commonHelperRes.setDayCareOtherServices(otherServices);
		return commonHelperRes;
	}

	/***
	 * 
	 * Method Name: populateAddress Method Description: to populate address
	 * 
	 * @param dayCareRequestReq
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/populateAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes populateAddress(@RequestBody DayCareRequestReq dayCareRequestReq) {
		log.debug("Entering method generateServiceAuth in DayCareRequestController");
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCarePersonDtos(dayCareRequestService.populateAddress(dayCareRequestReq.getIdStage()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: searchFacility Method Description:This service will search
	 * day care facility based on input search cr
	 * 
	 * @param dayCareRequestReq
	 * @return DayCareRequestRes
	 */
	@RequestMapping(value = "/searchFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes searchFacility(@RequestBody DayCareRequestReq dayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDaycareSearchList(dayCareRequestService.daycareSearch(dayCareRequestReq.getSearchDto()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: getFacilityById Method Description: This service will
	 * retrieve facility details by facility id
	 * 
	 * @param dayCareRequestReq
	 * @return DayCareRequestRes
	 */
	@RequestMapping(value = "/retrieveFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes getFacilityById(@RequestBody DayCareRequestReq dayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCareSearchListDto(
				dayCareRequestService.getFacilityById(dayCareRequestReq.getSearchDto().getIdFacility()));
		return dayCareRequestRes;
	}

	/**
	 * 
	 * Method Name: getDaycareCodes Method Description: get day care codes
	 * 
	 * @return dayCareRequestRes
	 */
	@RequestMapping(value = "/getDaycareCodes", headers = { "Accept=application/json" }, method = RequestMethod.GET)
	public  DayCareRequestRes getDaycareCodes() {
		log.info("Entering method getDaycareCodes in DayCareRequestController");
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDaycareCodes(dayCareRequestService.getDaycareCodes());
		log.info("Exiting method getDaycareCodes in DayCareRequestController");
		return dayCareRequestRes;
	}

	/**
	 * Method Name: savePersonList Method Description: Controller to Save Person
	 * List
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param personListDto
	 * @param isApprovalMode
	 * @return DayCareRequestDto
	 */
	@RequestMapping(value = "/savePersonList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes savePersonList(@RequestBody DayCareRequestReq dayCareRequestReq) {

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getApprovalMode())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.approvalMode.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getPersonListDto())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.personList.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		DayCareRequestDto dayCareRequestDto = dayCareRequestService.savePersonList(dayCareRequestReq.getIdStage(),
				dayCareRequestReq.getIdEvent(), dayCareRequestReq.getPersonListDto(),
				dayCareRequestReq.getApprovalMode());
		dayCareRequestRes.setDayCareRequestValueDto(dayCareRequestDto);
		return dayCareRequestRes;
	}

	/**
	 * Method Name: deletePerson Method Description:To delete person day care
	 * link
	 * 
	 * @param dayCareRequestReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/deletePerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes deletePerson(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareRequest())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdDayCareRequest.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getApprovalMode())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.approvalMode.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.personId.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Boolean deletePerson = dayCareRequestService.deletePerson(dayCareRequestReq.getIdDayCareRequest(),
				dayCareRequestReq.getIdPerson(), dayCareRequestReq.getIdEvent(), dayCareRequestReq.getApprovalMode());
		commonHelperRes.setDeleteDayCareLinkPerson(deletePerson);
		return commonHelperRes;
	}

	/**
	 * Method Name: validateAndNotify Method Description:To validate and notify
	 * day care request
	 * 
	 * @param dayCareRequestReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/validateNotify", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes validateAndNotify(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.personId.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();

		Long ssccIdRequest = dayCareRequestService.validateAndNotify(dayCareRequestReq.getIdPerson(),
				dayCareRequestReq.getIdEvent());
		commonHelperRes.setIdResourceSSCC(ssccIdRequest);
		return commonHelperRes;
	}

	/**
	 * Method Name: retrieveSvcAuthPersonCount Method description: this
	 * retrieves the count of person list for day care.
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	@RequestMapping(value = "/retrieveSvcAuthPersonCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrieveSvcAuthPersonCount(
			@RequestBody DayCareRequestReq dayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		Long count = dayCareRequestService.retrieveSvcAuthPersonCount(dayCareRequestReq.getIdEvent());
		dayCareRequestRes.setPersonCount(count.intValue());
		return dayCareRequestRes;
	}

	/**
	 * Method Name: retrieveDayCareDetails Method Description:Controller to
	 * retrive the Day Care Request Details
	 * 
	 * @param dayCareRequestReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/retrieveDayCareDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes retrieveDayCareDetails(@RequestBody DayCareRequestReq dayCareRequestReq) {
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(dayCareRequestReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("dcr.personId.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes = dayCareRequestService.retrieveDayCareRequestDetailsForDisplay(
				dayCareRequestReq.getIdStage(), dayCareRequestReq.getIdEvent(), dayCareRequestReq.getIdUser());
		return dayCareRequestRes;
	}

}
