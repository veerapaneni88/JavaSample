package us.tx.state.dfps.service.legacyapplication.controller;

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
import us.tx.state.dfps.service.common.request.LegacyApplicationReq;
import us.tx.state.dfps.service.common.request.LegacySaveReq;
import us.tx.state.dfps.service.common.response.LegacyApplicationRes;
import us.tx.state.dfps.service.common.response.LegacySaveRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legacyapplication.service.LegacyApplicationService;

@RestController
@RequestMapping("/legacyApplication")
public class LegacyApplicationController {

	@Autowired
	private LegacyApplicationService legacyApplicationService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LegacyApplicationController.class);

	/**
	 * Method Name: read Method Description: It will call the read service.
	 * 
	 * @param legacyApplicationReq
	 * @return LegacyApplicationRes
	 */
	@RequestMapping(value = "/read", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegacyApplicationRes read(@RequestBody LegacyApplicationReq legacyApplicationReq) {
		log.debug("Entering method read in LegacyApplicationController");
		LegacyApplicationRes legacyApplicationRes = new LegacyApplicationRes();
		if (TypeConvUtil.isNullOrEmpty(legacyApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legacyApplicationReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.iIdEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legacyApplicationReq.getIdLastUpdatePerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idLastUpdatePerson.mandatory", null, Locale.US));
		}
		log.debug("Exiting method read in LegacyApplicationController");
		legacyApplicationRes.setTransactionId(legacyApplicationReq.getTransactionId());
		legacyApplicationRes.setLegacyApplicationDto(legacyApplicationService.read(legacyApplicationReq.getIdStage(),
				legacyApplicationReq.getIdEvent(), legacyApplicationReq.getIdLastUpdatePerson()));
		return legacyApplicationRes;
	}

	/**
	 * Method Name: save Method Description: It will call the save service
	 * method.
	 * 
	 * @param legacySaveReq
	 * @return LegacySaveRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegacySaveRes save(@RequestBody LegacySaveReq legacySaveReq){
		log.debug("Entering method save in LegacyApplicationController");
		LegacySaveRes legacySaveRes = new LegacySaveRes();
		if (TypeConvUtil.isNullOrEmpty(legacySaveReq.getLegacyApplicationDto())) {
			throw new InvalidRequestException(messageSource.getMessage("YETTOSET", null, Locale.US));
		}
		legacySaveRes.setUpdateResult(legacyApplicationService.save(legacySaveReq.getLegacyApplicationDto()));
		legacySaveRes.setTransactionId(legacySaveReq.getTransactionId());

		legacyApplicationService.save(legacySaveReq.getLegacyApplicationDto());
		log.debug("Exiting method save in LegacyApplicationController");
		return legacySaveRes;
	}
}
