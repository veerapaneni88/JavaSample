package us.tx.state.dfps.service.admin.controller;

import java.util.Arrays;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.LegalActionEventService;
import us.tx.state.dfps.service.common.request.LegalActionEventReq;
import us.tx.state.dfps.service.common.response.LegalActionEventRes;
import us.tx.state.dfps.service.common.response.LegalActionsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Service
 * Retrieves all the information from the legal action table. Aug 13, 2017-
 * 9:53:33 AM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/legalActionEvent")
public class LegalActionEventController {

	/** The legal action event service. */
	@Autowired
	LegalActionEventService legalActionEventService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(LegalActionEventController.class);

	/**
	 * Gets the most recent FDTC subtype.
	 *
	 * @param legalActionEventReq
	 *            the legal action event req
	 * @return the most recent FDTC subtype
	 */
	@RequestMapping(value = "/mostRecentFDTCSubtype", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LegalActionEventRes getMostRecentFDTCSubtype(@RequestBody LegalActionEventReq legalActionEventReq) {
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.data.emptyset", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto().getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legalactionevent.personid.mandatory", null, Locale.US));
		}

		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		legalActionEventRes = legalActionEventService.getMostRecentFDTCSubtype(legalActionEventReq);
		log.debug("LegalActionEventController" + legalActionEventRes);
		return legalActionEventRes;
	}

	/**
	 * Gets the open FBSS stage.
	 *
	 * @param legalActionEventReq
	 *            the legal action event req
	 * @return the open FBSS stage
	 */
	@RequestMapping(value = "/openFBSSStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalActionEventRes getOpenFBSSStage(@RequestBody LegalActionEventReq legalActionEventReq) {
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.data.emptyset", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legalactionevent.caseid.mandatory", null, Locale.US));
		}
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		legalActionEventRes = legalActionEventService.getOpenFBSSStage(legalActionEventReq);

		log.debug("LegalActionEventController" + legalActionEventRes);
		return legalActionEventRes;
	}

	/**
	 * Gets the legal action rel fictive kin.
	 *
	 * @param legalActionEventReq
	 *            the legal action event req
	 * @return the legal action rel fictive kin
	 */
	@RequestMapping(value = "/legalActionRelFictiveKin", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LegalActionEventRes getLegalActionRelFictiveKin(@RequestBody LegalActionEventReq legalActionEventReq) {
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.data.emptyset", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legalactionevent.stageid.mandatory", null, Locale.US));
		}
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		legalActionEventRes = legalActionEventService.getLegalActionRelFictiveKin(legalActionEventReq);

		log.debug("LegalActionEventController" + legalActionEventRes);
		return legalActionEventRes;
	}

	/**
	 * This is a method to set all input parameters(of the stored procedure).
	 *
	 * @param storeProcParams
	 * 
	 * @return LegalActionsRes
	 */
	@RequestMapping(value = "/executeStoredProc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalActionsRes executeStoredProc(@RequestBody Object[] storeProcParams) {
		if (TypeConvUtil.isNullOrEmpty(storeProcParams)) {
			throw new InvalidRequestException(messageSource.getMessage("arrayList cannot be empty", null, Locale.US));
		}

		LegalActionsRes legalActionsRes = new LegalActionsRes();
		legalActionsRes = legalActionEventService.executeStoredProc(Arrays.asList(storeProcParams));

		log.debug("LegalActionEventController" + legalActionsRes);
		return legalActionsRes;
	}
	
	/**
	 * 
	 * Gets the latest legal action outcome 
	 * 
	 * @param legalActionEventReq
	 * @return
	 */
	@RequestMapping(value = "/getLatestLegalActionOutcome", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LegalActionEventRes selectLatestLegalActionOutcome(@RequestBody LegalActionEventReq legalActionEventReq) {
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.data.emptyset", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventReq.getLegalActionEventInDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legalactionevent.stageid.mandatory", null, Locale.US));
		}
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		legalActionEventRes.setLegalActionEventOutDto(
				legalActionEventService.selectLatestLegalActionOutcome(legalActionEventReq.getLegalActionEventInDto()));

		log.debug("LegalActionEventController" + legalActionEventRes);
		return legalActionEventRes;
	}
}
