package us.tx.state.dfps.service.intake.controller;

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION 
 * Tuxedo Service Name: 
 * Class Description:
 * Mar 23, 2017 - 1:28:11 PM
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CallEntryRtrvReq;
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.response.IntNarrBlobRes;
import us.tx.state.dfps.service.common.response.RetrvCallEntryRes;
import us.tx.state.dfps.service.common.response.RtrvAllegRes;
import us.tx.state.dfps.service.intake.service.IntakeActionsService;

@RestController
@RequestMapping("/intakeactions")
public class IntakeActionsController {

	@Autowired
	IntakeActionsService intakeActionsService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(IntakeActionsController.class);

	/**
	 * * This service retrieves the information for the Call Entry and Call
	 * Decision windows. It will retrieve information from the INCOMING_DETAIL,
	 * STAGE, AND INCMG_DETERM_FACTORS tables. Method Description: legacy
	 * service name - CINT25S
	 * 
	 * @param callEntryRtrvIn
	 * @return
	 */
	@RequestMapping(value = "/getCallEntrynDecsn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RetrvCallEntryRes getCallEntrynDecsn(@RequestBody CallEntryRtrvReq callEntryRtrvIn) {

		if (callEntryRtrvIn.getUlIdPerson() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (callEntryRtrvIn.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		RetrvCallEntryRes callEntryRtrvOut = new RetrvCallEntryRes();
		callEntryRtrvOut.setTransactionId(callEntryRtrvIn.getTransactionId());
		log.info("TransactionId :" + callEntryRtrvIn.getTransactionId());
		return intakeActionsService.getCallEntrynDecsn(callEntryRtrvIn.getUlIdPerson(), callEntryRtrvIn.getUlIdStage());

	}

	/**
	 * This service method to retrieve blob of incoming narratives for intake
	 * actions Method Description: legacy service name - CINT22S
	 * 
	 * @param intNarrBlobInRec
	 * @return IntNarrBlobOutRec
	 */
	@RequestMapping(value = "/intnarrblob", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IntNarrBlobRes getIntNarrBlob(@RequestBody CommonStageIdReq intNarrBlobInRec) {

		if (intNarrBlobInRec.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		IntNarrBlobRes intNarrBlobRes = new IntNarrBlobRes();
		intNarrBlobRes.setTransactionId(intNarrBlobInRec.getTransactionId());
		log.info("TransactionId :" + intNarrBlobInRec.getTransactionId());
		return intakeActionsService.getIntNarrBlobOutRec(intNarrBlobInRec.getUlIdStage());

	}

	/**
	 * This method is to get list of allegations for the given stage id Method
	 * Description: legacy service name - CINT30S
	 * 
	 * @param allegRtrvRecIn
	 * @return
	 */
	@RequestMapping(value = "/intallegations", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RtrvAllegRes getAllegations(@RequestBody CommonStageIdReq allegRtrvRecIn) {

		if (allegRtrvRecIn.getUlIdStage() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		RtrvAllegRes allegRes = new RtrvAllegRes();
		allegRes.setTransactionId(allegRtrvRecIn.getTransactionId());
		log.info("TransactionId :" + allegRtrvRecIn.getTransactionId());
		return intakeActionsService.getAllegations(allegRtrvRecIn.getUlIdStage());

	}

}
