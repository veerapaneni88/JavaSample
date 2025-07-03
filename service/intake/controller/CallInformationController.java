package us.tx.state.dfps.service.intake.controller;

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
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.request.FacDetailUpdtReq;
import us.tx.state.dfps.service.common.request.PersListReq;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.common.response.PersListRes;
import us.tx.state.dfps.service.intake.service.CallInfoService;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CallInformationConversation.java Class Description: Mar 23, 2017 - 1:26:19 PM
 */
@RestController
@RequestMapping("/callinfo")
public class CallInformationController {

	@Autowired
	CallInfoService callInfoService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CallInformationController.class);

	/**
	 * this service to Retrieve Call Person List Box info Method Description:
	 * legacy service name - CINT26S
	 * 
	 * @param persListReq
	 * @return PersListRes
	 */
	@RequestMapping(value = "/getpersonList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersListRes getPersonList(@RequestBody PersListReq persListReq) {

		if (persListReq.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (persListReq.getCdIncmgStatus() == null) {
			throw new InvalidRequestException(
					messageSource.getMessage("callInformation.incomingStatue.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + persListReq.getTransactionId());
		return callInfoService.getPersonList(persListReq);
	}

	/**
	 * this service is to get the facility details for call information screen
	 * Method Description:legacy service name - CINT27S
	 * 
	 * @param stageidIn
	 * @return FacilRtrvRes
	 */
	@RequestMapping(value = "/getfacildetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FacilRtrvRes getFacilDetail(@RequestBody CommonStageIdReq stageidIn) {

		if (stageidIn.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + stageidIn.getTransactionId());
		return callInfoService.getFacilDetail(stageidIn);
	}

	/**
	 * This service method to do modifications to facility details window Method
	 * Description: legacy service name - CINT11S
	 * 
	 * @param facDetailUpdtReq
	 * @return ArchOutputStruct
	 */
	@RequestMapping(value = "/updtfacildetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  String updtFacilityDetail(@RequestBody FacDetailUpdtReq facDetailUpdtReq) {

		if (facDetailUpdtReq.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		String status = callInfoService.updtFacilityDetail(facDetailUpdtReq);
		log.info("TransactionId :" + facDetailUpdtReq.getTransactionId());
		return status;
	}

}
