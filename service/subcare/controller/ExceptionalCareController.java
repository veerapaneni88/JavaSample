package us.tx.state.dfps.service.subcare.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import us.tx.state.dfps.service.common.request.ExceptionalCareReq;
import us.tx.state.dfps.service.common.response.ExceptionalCareRes;
import us.tx.state.dfps.service.subcare.service.ExceptionalCareService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to perform operations related to exceptional care . Feb 15, 2018-
 * 11:03:06 AM © 2017 Texas Department of Family and Protective Services
 */

@RestController
@Api(tags = { "placements" })
@RequestMapping("/exceptionalCare")
public class ExceptionalCareController {

	private static final Logger log = Logger.getLogger(ExceptionalCareController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	ExceptionalCareService exceptionalCareService;

	/**
	 * 
	 * Method Name: getExceptionalCareList
	 * 
	 * Method Description: This method is used to display Exceptional care list
	 * in the placement
	 * 
	 * @param ExceptionalCareReq
	 * @return
	 */
	@ApiOperation(value = "Get exceptional care list", tags = { "placements" })
	@RequestMapping(value = "/getExceptionalCareList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes getExceptionalCareList(@RequestBody ExceptionalCareReq req) {
		if (ObjectUtils.isEmpty(req)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(req.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.displayExceptCareList(req);
		log.info("Exiting after getting List");
		return resp;
	}

	@RequestMapping(value = "/updateSsccExceptionCare", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes updateSsccExceptCare(@RequestBody ExceptionalCareReq req) {
		if (ObjectUtils.isEmpty(req.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.updateSsccExceptCare(req);
		return resp;
	}

	@RequestMapping(value = "/updateSsccECStartDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes updateSsccECStartDate(@RequestBody ExceptionalCareReq req) {
		if (ObjectUtils.isEmpty(req.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.updateSsccECStartDate(req);
		return resp;
	}

	@RequestMapping(value = "/updateSsccECStartEndDates", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes updateSsccECStartEndDates(@RequestBody ExceptionalCareReq req) {
		if (ObjectUtils.isEmpty(req.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.updateSsccECStartEndDates(req);
		return resp;
	}

	/**
	 * Method Name: getPlacementDates Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getPlacementDates", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes getPlacementDates(@RequestBody ExceptionalCareReq req) {

		if (ObjectUtils.isEmpty(req.getIdPlcmtEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.getPlacementDates(req.getIdPlcmtEvent());
		log.info("Exiting after getting placement Dates");
		return resp;
	}

	/**
	 * Method Name: saveExceptionalCare Method Description: Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param req
	 */
	@RequestMapping(value = "/saveExceptionalCare", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ExceptionalCareRes saveExceptionalCare(@RequestBody ExceptionalCareReq req) {

		if (ObjectUtils.isEmpty(req.getExceptionalCareDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(req.getCdSaveType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes res = exceptionalCareService.saveExceptionalCare(req.getExceptionalCareDto(),
				req.getCdSaveType());
		log.info("Exiting after Save");
		return res;
	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:Gets an
	 * active child sscc referral from the SSCC_REFERRAL table
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getActiveChildPlcmtReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes getActiveChildPlcmtReferral(@RequestBody ExceptionalCareReq req) {

		if (ObjectUtils.isEmpty(req.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.getActiveChildPlcmtReferral(req.getIdStage());
		log.info("Exiting after getting an active child");
		return resp;

	}

	/**
	 * Method Name: getExcpCareDays Method Description: Gets numbers of
	 * exceptional care days in a contract period
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getExcpCareDays", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ExceptionalCareRes getExcpCareDays(@RequestBody ExceptionalCareReq req) {

		if (ObjectUtils.isEmpty(req.getExceptionalCareDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		ExceptionalCareRes resp = exceptionalCareService.getExcpCareDays(req.getExceptionalCareDto());
		log.info("Exiting after getting no of exceptional care days");
		return resp;
	}
}
