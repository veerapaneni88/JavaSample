package us.tx.state.dfps.service.guardianshipdtl.controller;

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
import us.tx.state.dfps.service.common.request.GuardianshipDtlReq;
import us.tx.state.dfps.service.common.response.GuardianshipDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.guardianshipdtl.service.GuardianshipDtlService;

@RestController
@RequestMapping("/guardianshipDtlController")
public class GuardianshipDtlController {

	@Autowired
	private GuardianshipDtlService guardianshipDtlService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-GuardianshipDtlControllerLog");

	/**
	 * 
	 * Method Name: isDADFinalOutcomeDocumented Method Description:Method checks
	 * if Final Outcome has been documented on Guardian Detail page for Guardian
	 * Type = DAD
	 * 
	 * @param guardianshipDtlReq
	 * @return
	 */
	@RequestMapping(value = "/isDADFinalOutcomeDocumented", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  GuardianshipDtlRes isDADFinalOutcomeDocumented(
			@RequestBody GuardianshipDtlReq guardianshipDtlReq) {
		LOG.debug("Entering method isDADFinalOutcomeDocumented in GuardianshipDtlController");
		if (TypeConvUtil.isNullOrEmpty(guardianshipDtlReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("GuardianshipDtlController.isDADFinalOutcomeDocumented.idCase", null, Locale.US));
		}
		GuardianshipDtlRes guardianshipDtlRes = new GuardianshipDtlRes();
		guardianshipDtlRes
				.setResults(guardianshipDtlService.isDADFinalOutcomeDocumented(guardianshipDtlReq.getIdCase()));
		LOG.debug("Exiting method isDADFinalOutcomeDocumented in GuardianshipDtlController");
		return null;
	}

}
