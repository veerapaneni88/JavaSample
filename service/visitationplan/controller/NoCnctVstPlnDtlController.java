package us.tx.state.dfps.service.visitationplan.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.NoCnctVstPlnDtlReq;
import us.tx.state.dfps.service.common.request.VisitationPlanDtlReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.NoCnctVstPlnDtlRes;
import us.tx.state.dfps.service.common.response.VisitationPlanDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.visitationplan.service.NoCnctVstPlnDtlService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: no contact
 * visitation plan detail Service Controller Sep 20, 2018- 11:44:21 AM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/nocnctvstplndtls")
public class NoCnctVstPlnDtlController {
	@Autowired
	MessageSource messageSource;
	@Autowired
	NoCnctVstPlnDtlService noCnctVstPlnDtlService;

	/**
	 * 
	 * Method Name: retrieveNoContactVisitationPlanDtls Method Description: This
	 * method is used to retrieve the No Contact Visitation Plan.
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	@RequestMapping(value = "/rtrvNoCnctVstPlnDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NoCnctVstPlnDtlRes retrieveNoContactVisitationPlanDtls(
			@RequestBody NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(noCnctVstPlnDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = noCnctVstPlnDtlService.retrieveNoContactVisitationPlnDetails(
				noCnctVstPlnDtlReq.getIdStage(), noCnctVstPlnDtlReq.getIdEvent());

		return noCnctVstPlnDtlRes;
	}

	/**
	 * 
	 * Method Name: saveNoContactVisitationPlanDtls Method Description:This
	 * method is used to Save/Update the New/Existing No Contact Visitation
	 * Plan.
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	@RequestMapping(value = "/saveNoCnctVstPlnDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NoCnctVstPlnDtlRes saveNoContactVisitationPlanDtls(
			@RequestBody NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = noCnctVstPlnDtlService
				.saveNoContactVisitationPlnDetails(noCnctVstPlnDtlReq);
		return noCnctVstPlnDtlRes;
	}

	/**
	 * 
	 * Method Name: saveAndSubmitNoContactVisitationPlanDtls Method
	 * Description:This method is used to Save and submit /Update the
	 * New/Existing No Contact Visitation Plan.
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	@RequestMapping(value = "/saveAndSubmitNoCnctVstPlnDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NoCnctVstPlnDtlRes saveAndSubmitNoContactVisitationPlanDtls(
			@RequestBody NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = noCnctVstPlnDtlService
				.saveNoContactVisitationPlnDetails(noCnctVstPlnDtlReq);
		return noCnctVstPlnDtlRes;
	}

	/**
	 * 
	 * Method Name: retrieveVisitationPlanDtl Method Description:This Method is
	 * used to Fetch the Visitation Plan detail if existing.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDtlRes
	 */
	@RequestMapping(value = "/rtrvVisitationPlanDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  VisitationPlanDtlRes retrieveVisitationPlanDtl(
			@RequestBody VisitationPlanDtlReq visitationPlanDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		VisitationPlanDtlRes visitationPlanDtlRes = new VisitationPlanDtlRes();
		visitationPlanDtlRes.setVisitationPlanDetailDto(
				noCnctVstPlnDtlService.retrieveVisitationPlnDetail(visitationPlanDtlReq.getIdStage(),
						visitationPlanDtlReq.getIdEvent(), visitationPlanDtlReq.getIdCase()));
		return visitationPlanDtlRes;
	}

	/**
	 * 
	 * Method Name: saveVisitationPlanDtl Method Description: This method is
	 * used to Save the Visitation Plan.
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return
	 */
	@RequestMapping(value = "/saveVisitationPlnDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  VisitationPlanDtlRes saveVisitationPlanDtl(
			@RequestBody VisitationPlanDtlReq visitationPlanDtlReq) {
		VisitationPlanDtlRes visitationPlanDtlRes = noCnctVstPlnDtlService
				.saveVisitationPlnDetail(visitationPlanDtlReq);
		return visitationPlanDtlRes;
	}

	/**
	 * 
	 * Method Name: retrieveVisitationPlanDtl Method Description:This Method is
	 * used to Fetch the Visitation Plan detail if existing.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDtlRes
	 */
	@RequestMapping(value = "/getVistnPlanFormDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getVistnPlanFormDtl(@RequestBody VisitationPlanDtlReq visitationPlanDtlReq) {

		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(noCnctVstPlnDtlService.retrieveVisitationPlnFormDetail(
				visitationPlanDtlReq.getIdStage(), visitationPlanDtlReq.getIdEvent(), visitationPlanDtlReq.getIdCase(),
				visitationPlanDtlReq.getNmCase())));

		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getNoContactVisitationPlan Method Description:Method used to
	 * retrieve No Contact Visitation Plan Details
	 * 
	 * @param NoCnctVstPlnDtlReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getNoCntctVistnPlanFormDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getNoCntctVistnPlanFormDtl(@RequestBody NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(noCnctVstPlnDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(noCnctVstPlnDtlReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(noCnctVstPlnDtlReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(noCnctVstPlnDtlService.retrieveNoContactVisitationPlnFormDetails(
						noCnctVstPlnDtlReq.getIdStage(), noCnctVstPlnDtlReq.getIdEvent(),
						noCnctVstPlnDtlReq.getIdCase(), noCnctVstPlnDtlReq.getNmCase())));

		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: deleteVisitationPlanDtl Method Description:This method is
	 * used to delete the Visitation Plan / No Contact Visitation Plan.
	 * 
	 * @param visitationPlanDtlReq
	 * @return
	 */
	@RequestMapping(value = "/deleteVisitationPlanDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  VisitationPlanDtlRes deleteVisitationPlanDtl(
			@RequestBody VisitationPlanDtlReq visitationPlanDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		VisitationPlanDtlRes visitationPlanDtlRes = new VisitationPlanDtlRes();
		/*
		 * Calling the delete visitation Plan method of visitation Plan service.
		 */
		visitationPlanDtlRes
				.setResult(noCnctVstPlnDtlService.deleteVisitationPlanDtl(visitationPlanDtlReq.getIdEvent()));
		return visitationPlanDtlRes;
	}

}
