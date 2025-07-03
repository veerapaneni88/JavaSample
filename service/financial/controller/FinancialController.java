package us.tx.state.dfps.service.financial.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SSCCReferralReq;
import us.tx.state.dfps.service.common.request.ServiceAuthDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthGetReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationHeaderReq;
import us.tx.state.dfps.service.common.response.SSCCReferralRes;
import us.tx.state.dfps.service.common.response.ServiceAuthDetailRes;
import us.tx.state.dfps.service.common.response.ServiceAuthGetRes;
import us.tx.state.dfps.service.common.response.ServiceAuthorizationDetailRes;
import us.tx.state.dfps.service.common.response.ServiceAuthorizationHeaderRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.service.DayCareRequestService;
import us.tx.state.dfps.service.dcr.service.TypeOfServiceDCRService;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationHeaderService;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationService;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON21S Class
 * Description: Financial Controller will have all operation which are mapped to
 * financial module. Apr2 2, 2017 - 3:18:29 PM
 */

@RestController
@RequestMapping("/financial")
public class FinancialController {
	@Autowired
	ServiceAuthorizationService serviceAuthorizationDetailService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DayCareRequestService dayCareRequestService;
	@Autowired
	ServiceAuthorizationHeaderService serviceAuthorizationHeaderService;
	@Autowired
	TypeOfServiceDCRService typeOfServiceDCRService;

	private static final Logger log = Logger.getLogger(FinancialController.class);

	/**
	 * 
	 * Method Description: This Method will retrieve a list of Service
	 * Authorization Detail records based upon IdSvcAuth from the Service
	 * Authorization Detail window. It will also retrieve NmPersonFull based
	 * upon IdPerson from the Person table. Service Name: CCON21S
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return servAuthRes
	 */

	@RequestMapping(value = "/getServAuthDetailList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthorizationDetailRes getSerAuthDetail(
			@RequestBody ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {

		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationDetailReq.getIdSvcAuth())) {
			throw new InvalidRequestException(messageSource.getMessage("common.svcAuthId.mandatory", null, Locale.US));
		}
		ServiceAuthorizationDetailRes servAuthRes = new ServiceAuthorizationDetailRes();
		List<ServiceAuthorizationDetailDto> servAuthList = serviceAuthorizationDetailService
				.getSerAuthDetail(serviceAuthorizationDetailReq);
		servAuthRes.setServAuthDtoList(servAuthList);
		servAuthRes.setTransactionId(serviceAuthorizationDetailReq.getTransactionId());
		log.info("TransactionId :" + serviceAuthorizationDetailReq.getTransactionId());
		return servAuthRes;

	}

	/**
	 * Method Name: selectLatestLegalStatus Method Description: This method
	 * fetches Latest Legal Status Record for the given Person and Legal Status
	 * from the database.
	 * 
	 * @param serviceAuthGetReq
	 * @return serviceAuthGetRes
	 */
	@RequestMapping(value = "/selectLatestLegalStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthGetRes selectLatestLegalStatus(@RequestBody ServiceAuthGetReq serviceAuthGetReq) {

		ServiceAuthGetRes serviceAuthGetRes = new ServiceAuthGetRes();
		log.info(ServiceConstants.TRANSACTION_ID + serviceAuthGetReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getCdLegalStatStatus())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.cdLegalStatStatus.mandatory", null, Locale.US));
		}
		serviceAuthGetRes.setLegalStatusValue(serviceAuthorizationDetailService
				.selectLatestLegalStatus(serviceAuthGetReq.getIdPerson(), serviceAuthGetReq.getCdLegalStatStatus()));
		return serviceAuthGetRes;
	}

	/**
	 * Method Name: saveServiceAuthDetail Method Description: This is the Save
	 * Service for Service Authorization Detail. First it will check whether or
	 * not the Person has already been authorized services for the resource
	 * during the specified time period. It will then retrieve Budget
	 * information and validate against the Amount Requested. If the Amount
	 * Requested decreases the current budget to less than 15%, then a To Do is
	 * initiated. If the above passes validation, then the Save Dam is called
	 * for Svc Auth Dtl and Event Person Link tables.
	 * 
	 * @param serviceAuthDtlReq
	 *            - contains ServiceAuthorization Detail and list of person to
	 *            be saved
	 * @return ServiceResHeaderDto - gives Error message or Success message
	 */
	@RequestMapping(value = "/saveServiceAuthDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes saveServiceAuthDetail(@RequestBody ServiceAuthDetailReq serviceAuthDtlReq) {

		if (TypeConvUtil.isNullOrEmpty(serviceAuthDtlReq)) {
			throw new InvalidRequestException(messageSource.getMessage("serviceAuthDtlReq.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthDtlReq.getServiceAuthDtlDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthDtlReq.getServiceAuthDtlDto().mandatory", null, Locale.US));
		}
		ServiceAuthDetailRes serviceAuthDetailRes = serviceAuthorizationDetailService
				.saveServiceAuthDetail(serviceAuthDtlReq);
		log.info("End - saveServiceAuthDetail method - FinancialController class in Service Controller");
		return serviceAuthDetailRes;
	}

	/**
	 * 
	 * Method Name: retrieveServiceAuthDetail Method Description: his retrieval
	 * service will * either populate the Service combo box, Persons Listbox *
	 * and/or Svc Auth Dtl Listbox. If the Window Mode is * Inquire, then a
	 * single row for Svc Auth Dtl Listbox will * be retrieved; if the Window
	 * Mode is Modify, then a single * row for Svc Auth Dtl Listbox will be
	 * retrieved, and a list * of Services will also be retrieved; if the Window
	 * Mode is * New and no detail record exists, then a list of Services * will
	 * be retrieved, a list of Persons will be retrieved and * the Dt Situation
	 * Opened will be retrieved. However, if the * window mode is New and a
	 * detail record does exist, then * a single row for Svc Auth Dtl Listbox, a
	 * list os Service * and Dt Situation Opened will be retrieved.
	 * 
	 * @param serviceAuthDtlReq
	 *            - Contains idStage , idContract to retrieve the data
	 * @return ServiceAuthDetailRes - this gets data to display service
	 *         authorization detail
	 */
	@RequestMapping(value = "/retrieveServiceAuthDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes retrieveServiceAuthDetail(@RequestBody ServiceAuthDetailReq serviceAuthDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthDtlReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthDtlReq.svcAuthCounty.mandatory", null, Locale.US));
		}
		return serviceAuthorizationDetailService.retrieveServiceAuthDetail(serviceAuthDtlReq);
	}

	/**
	 * Method Name: createDayCareRegionalAlert Method Description: Create Alert
	 * for Regional Daycare Coordinator when the Daycare Service Authorization
	 * is Terminated.
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	@RequestMapping(value = "/createDayCareRegionalAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes createDayCareRegionalAlert(@RequestBody DayCareRequestDto dayCareRequestDto) {

		ServiceAuthDetailRes serviceAuthRes = new ServiceAuthDetailRes();
		log.info("Start - retrieveServiceAuthDetail method - FinancialController class in Service Controller");

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idStage", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestDto.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("idEvent", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestDto.getUserId())) {
			throw new InvalidRequestException(messageSource.getMessage("idLogonUser", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestDto.getIdChild())) {
			throw new InvalidRequestException(messageSource.getMessage("idPrimaryClient", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestDto.getIdSvcAuthEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("idSvcAuthEvent", null, Locale.US));
		}
		dayCareRequestService.createDayCareRegionalAlert(dayCareRequestDto);
		log.info("End - retrieveServiceAuthDetail method - FinancialController class in Service Controller");
		return serviceAuthRes;
	}

	/**
	 * Method name: dayCarePersonList Method description: to retrieve the person
	 * list for day care service
	 * 
	 * @param serviceAuthDetailReq
	 * @return
	 */
	@RequestMapping(value = "/dayCarePersonList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes dayCarePersonList(@RequestBody ServiceAuthDetailReq serviceAuthDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthDetailReq)) {
			throw new InvalidRequestException(messageSource.getMessage("serviceAuthDtlReq.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthDetailReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthDtlReq.idEvent.mandatory", null, Locale.US));
		}
		ServiceAuthDetailRes serviceAuthDetailRes = serviceAuthorizationDetailService
				.dayCarePersonList(serviceAuthDetailReq.getDayCareRequestDto(), serviceAuthDetailReq.getIdEvent());
		return serviceAuthDetailRes;
	}

	/**
	 * Method Name: dayCarePersonListForSvcAuthDtlId Method Description:
	 * retrieve the day care person list based on the event id and idSvcAuthDtl
	 * 
	 * @param serviceAuthDetailReq
	 * @return
	 */
	@RequestMapping(value = "/dayCarePersonListForSvcAuthDtlId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes dayCarePersonListForSvcAuthDtlId(
			@RequestBody ServiceAuthDetailReq serviceAuthDetailReq) {
		ServiceAuthDetailRes serviceAuthDetailRes = serviceAuthorizationDetailService.dayCarePersonListForSvcAuthDtlId(
				serviceAuthDetailReq.getDayCareRequestDto(), serviceAuthDetailReq.getIdEvent(),
				serviceAuthDetailReq.getIdSvcAuthDtl());
		return serviceAuthDetailRes;
	}

	/**
	 * MethodName: updateSSCCListDC MethodDescription:This method updates
	 * SSCC_LIST table with IND_SSCC_DAYCARE = 'Y', DT_SSCC_DAYCARE = system
	 * date for Day Care Requests.
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param sSCCRefReq
	 * @return SSCCRefRes
	 * 
	 */

	@RequestMapping(value = "/updateSSCCListDC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes updateSSCCListDC(@RequestBody SSCCReferralReq sSCCRefReq) {

		SSCCReferralRes sSCCRefRes = new SSCCReferralRes();
		sSCCRefRes
				.setIdSSCCReferral(serviceAuthorizationDetailService.updateSSCCListDC(sSCCRefReq.getIdSSCCReferral()));
		return sSCCRefRes;

	}

	/**
	 * Method Name: retrieveServiceAuthHeaderInfo Method description: This
	 * method is used to call the service to retrieve the service Authorization
	 * header Information.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	@RequestMapping(value = "/retrieveServiceAuthHeaderInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthorizationHeaderRes retrieveServiceAuthHeaderInfo(
			@RequestBody ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idStage", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("idCase", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("cdTask", null, Locale.US));
		}
		return serviceAuthorizationHeaderService.retrieveServiceAuthHeaderInfo(serviceAuthorizationHeaderReq);
	}

	/**
	 * Method Name: saveServiceAuthHeaderInfo Method description: This method is
	 * used to call the service to save the service Authorization header
	 * Information.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	@RequestMapping(value = "/saveServiceAuthHeaderInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthorizationHeaderRes saveServiceAuthHeaderInfo(
			@RequestBody ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthorizationHeaderDto", null, Locale.US));
		}
		return serviceAuthorizationHeaderService.saveServiceAuthHeader(serviceAuthorizationHeaderReq);
	}

	/**
	 * Method Name: deleteServiceAuthHeaderInfo Method description: This method
	 * is used to call the service to delete the service Authorization header
	 * Information.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	@RequestMapping(value = "/deleteServiceAuthHeaderInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthorizationHeaderRes deleteServiceAuthHeaderInfo(
			@RequestBody ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthorizationHeaderDto", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("idEvent", null, Locale.US));
		}
		return serviceAuthorizationHeaderService.saveServiceAuthHeader(serviceAuthorizationHeaderReq);
	}

	/**
	 * Method Name: validateContractForResource Method Description: This method
	 * is used to validate whether the contract exists for resource.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	@RequestMapping(value = "/validateContractForResource", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthorizationHeaderRes validateContractForResource(
			@RequestBody ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("serviceAuthorizationHeaderDto", null, Locale.US));
		}
		if (TypeConvUtil
				.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("idResource", null, Locale.US));
		}
		if (TypeConvUtil
				.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getCdSvcAuthCounty())) {
			throw new InvalidRequestException(messageSource.getMessage("cdSvcAuthCounty", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(
				serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getCdSvcAuthService())) {
			throw new InvalidRequestException(messageSource.getMessage("cdSvcAuthService", null, Locale.US));
		}
		if (TypeConvUtil
				.isNullOrEmpty(serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getDtSvcAuthEff())) {
			throw new InvalidRequestException(messageSource.getMessage("dtSvcAuthEff", null, Locale.US));
		}
		return serviceAuthorizationHeaderService.validateContractForResource(serviceAuthorizationHeaderReq);
	}

	/**
	 * Method Name: validateLegalStatusAndLivingArr Method Description: This
	 * method is used to validate the legalstatus and placement information.
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return
	 */
	@RequestMapping(value = "/validateLegalStatusAndLivingArr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes validateLegalStatusAndLivingArr(
			@RequestBody ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorizationDetailReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("idCase", null, Locale.US));
		}
		return serviceAuthorizationDetailService.validateLegalStatusAndLivingArr(
				serviceAuthorizationDetailReq.getIdCase(), serviceAuthorizationDetailReq.getIdPerson(),
				serviceAuthorizationDetailReq.getIdResource());
	}

	/**
	 * Method Name: validateChildInformation Method Description: This method is
	 * used to get and vaidate the child information.
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return
	 */
	@RequestMapping(value = "/validateChildInformation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes validateChildInformation(
			@RequestBody ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		boolean isValid = serviceAuthorizationDetailService.validateChildInformation(serviceAuthorizationDetailReq);
		serviceAuthDetailRes.setValid(isValid);
		return serviceAuthDetailRes;
	}

	/**
	 * 
	 * Method Name: getOverlapRecsForSvcAuth Method Description: for a service
	 * auth detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param serviceAuthDtlReq
	 * @return
	 */
	@RequestMapping(value = "/getOverlapRecsForSvcAuth", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes getOverlapRecsForSvcAuth(@RequestBody ServiceAuthDetailReq serviceAuthDtlReq) {
		ServiceAuthDetailDto serviceAuthDtlDto = serviceAuthDtlReq.getServiceAuthDtlDto();
		DayCarePersonDto dayCarePersonDto = new DayCarePersonDto();
		dayCarePersonDto.setDtBegin(serviceAuthDtlDto.getDtSvcAuthDtlEnd());
		dayCarePersonDto.setDtEnd(serviceAuthDtlDto.getDtSvcAuthDtlBegin());
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		List<DayCarePersonDto> svcAuthDtlList = typeOfServiceDCRService.getOverlapRecsForSvcAuth(dayCarePersonDto,
				serviceAuthDtlDto.getIdPerson());
		serviceAuthDetailRes.setDayCarePersonForValidation(svcAuthDtlList);
		return serviceAuthDetailRes;
	}

	/**
	 * 
	 * Method Name: getLegalEpisodePaymentDate Method Description: Retrieve the
	 * Kinship child ID_SVC_AUTH_DTL from Kinship table
	 * 
	 * @param serviceAuthDtlReq
	 * @return
	 */
	@RequestMapping(value = "/getLegalEpisodePaymentDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceAuthDetailRes getLegalEpisodePaymentDate(@RequestBody ServiceAuthDetailReq serviceAuthDtlReq) {
		ServiceAuthDetailRes serviceAuthDetailRes = serviceAuthorizationDetailService.getLegalEpisodePaymentDate(
				serviceAuthDtlReq.getIdPerson(), serviceAuthDtlReq.getDtSvcAuthEff(), serviceAuthDtlReq.getIdResource(),
				serviceAuthDtlReq.getSvcAuthDtlSvc());
		return serviceAuthDetailRes;
	}
}
