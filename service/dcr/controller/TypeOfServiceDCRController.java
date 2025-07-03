/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 3, 2018- 12:10:17 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.dcr.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dcr.service.TypeOfServiceDCRService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the service implementation of the Type of service DCI page Apr 3, 2018-
 * 12:10:17 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/typeOfService")
public class TypeOfServiceDCRController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	TypeOfServiceDCRService typeOfServiceDCRService;

	private static final Logger log = Logger.getLogger(TypeOfServiceDCRController.class);

	/**
	 * Method Name: getTypeOfServiceDetail Method Description:
	 * DayCareRequestBean This method will provide day care person DTO, day care
	 * Facility DTO, Day care Search List DTO all together will be set in Day
	 * care request Bean
	 * 
	 * @param DayCareRequestReq
	 * @return DayCareRequestRes
	 */
	@RequestMapping(value = "/getTypeOfServiceDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes getTypeOfServiceDetail(@RequestBody DayCareRequestReq dayCareRequestReq) {
		log.debug("Entering method getTypeOfServiceDetail in TypeOfServiceDCRController");
		// Check day care bean
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestBean())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.getDayCareRequestBean.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPerson.mandatory", null, Locale.US));
		}

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setDayCareRequestBean(typeOfServiceDCRService.getTypeOfServiceDetail(dayCareRequestReq));
		return dayCareRequestRes;
	}

	/**
	 * Method Name: deleteTypeOfService Method Description: This method deletes
	 * child day care service type
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	@RequestMapping(value = "/deleteTypeOfService", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceResHeaderDto deleteTypeOfService(@RequestBody DayCareRequestReq dayCareRequestReq) {
		log.debug("Entering method deleteTypeOfService in TypeOfServiceDCRController");

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareRequest())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdDayCareRequest.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPerson.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPersonLastUpdated())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPersonLastUpdated.mandatory", null, Locale.US));
		}

		ServiceResHeaderDto serviceResHeaderDto = new ServiceResHeaderDto();
		typeOfServiceDCRService.deleteTypeOfService(dayCareRequestReq);
		return serviceResHeaderDto;
	}

	/**
	 * Method Name: saveDayCarePersonInfo Method Description: This method
	 * saves(insert/update) Day Care Request Person Information
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	@RequestMapping(value = "/saveTypeofService", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareRequestRes saveTypeofService(@RequestBody DayCareRequestReq dayCareRequestReq){
		log.debug("Entering method saveDayCarePersonInfo in DayCareRequestController");
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getDayCareRequestValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.dayCareRequestValueDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdDayCareRequest())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdDayCareRequest.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPerson.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getIdPersonLastUpdated())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPerson.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestReq.getXmlResponses())) {
			throw new InvalidRequestException(
					messageSource.getMessage("dayCareRequest.IdPerson.mandatory", null, Locale.US));
		}
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		dayCareRequestRes.setTransactionId(dayCareRequestReq.getTransactionId());
		dayCareRequestRes = typeOfServiceDCRService.saveTypeofService(dayCareRequestReq);
		log.info(ServiceConstants.TRANSACTION_ID + dayCareRequestReq.getTransactionId());
		log.debug("Exiting method saveDayCarePersonInfo in DayCareRequestController");
		return dayCareRequestRes;
	}
}
