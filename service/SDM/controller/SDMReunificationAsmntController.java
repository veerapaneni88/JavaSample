/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This is Service Controller method for SDM Reunification Assessment Screen.
 *Jun 12, 2018- 2:11:19 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.SDM.controller;

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
import us.tx.state.dfps.service.SDM.service.SdmReunificationAsmntService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.SdmReunificationAsmntFetchReq;
import us.tx.state.dfps.service.common.request.SdmReunificationAsmntReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntFetchRes;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntSaveRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

@RestController
@RequestMapping("/sdmReunificationAsmnt")
public class SDMReunificationAsmntController {

	public SDMReunificationAsmntController() {
	}

	@Autowired
	private SdmReunificationAsmntService sdmReunificaitonService;

	@Autowired
	MessageSource messageSource;

	public static final Logger log = Logger.getLogger(SDMReunificationAsmntController.class);

	/**
	 * Method Name: fetchSdmReunificationPageData Method Description: Service
	 * Controller method for Fetching the Sdm Reunfication Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 * @return response
	 */
	@RequestMapping(value = "/fetchSdmReunificationAsmnt", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SdmReunificationAsmntFetchRes fetchSdmReunificationPageData(
			@RequestBody SdmReunificationAsmntFetchReq sdmReunificationAsmntReq) {
		log.info("fetchSdmReunificationPageData In SDMReunificationAsmntController : Execution Started.");
		SdmReunificationAsmntFetchRes response = null;
		// Call the service method to get Page Parameters for SDM Reunification
		// Assessment Page.
		response = sdmReunificaitonService.fetchSdmReunificationAsmnt(sdmReunificationAsmntReq);
		log.info("fetchSdmReunificationPageData In SDMReunificationAsmntController : Return response.");
		return response;
	}

	/**
	 * Method Name: fetchSdmReunificationFormData Method Description: Service
	 * Controller method for Fetching the Sdm Reunfication Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 * @return response
	 */
	@RequestMapping(value = "/fetchSdmReunificationAsmntform", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes fetchSdmReunificationFormData(
			@RequestBody SdmReunificationAsmntFetchReq sdmReunificationAsmntReq) {
		log.info("fetchSdmReunificationPageData In SDMReunificationAsmntController : Execution Started.");
		// Call the service method to get Form Parameters for SDM Reunification
		// Assessment Page.
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(sdmReunificaitonService.fetchSdmReunificationAsmntForm(sdmReunificationAsmntReq)));
		log.info("fetchSdmReunificationPageData In SDMReunificationAsmntController : Return response.");
		return commonFormRes;
	}

	/**
	 * Method Name: SdmReunificationAsmntAUD Method Description: Service
	 * Controller method for AUD operation for SDM Reunfication Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 * @return response
	 */
	@RequestMapping(value = "/sdmReunificationAsmntAUD", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SdmReunificationAsmntSaveRes sdmReunificationAsmntAUD(
			@RequestBody SdmReunificationAsmntReq sdmReunificationAsmntReq) {
		log.info("SdmReunificationAsmntAUD In SDMReunificationAsmntController : Execution Started.");
		SdmReunificationAsmntSaveRes response = null;
		String action = !ObjectUtils.isEmpty(sdmReunificationAsmntReq.getReqFuncCd())
				? sdmReunificationAsmntReq.getReqFuncCd() : ServiceConstants.UPDATE;
		if (ObjectUtils.isEmpty(sdmReunificationAsmntReq.getIdEvent())
				|| ServiceConstants.ZERO.equals(sdmReunificationAsmntReq.getIdEvent())) {
			// Set the Request Action as Add
			sdmReunificationAsmntReq.setReqFuncCd(ServiceConstants.ADD);
		} else {
			// Set the Request Action to update/Delete
			switch (action) {
			case ServiceConstants.DELETE:
				// Delete Reunification Assessment
				sdmReunificationAsmntReq.setReqFuncCd(ServiceConstants.DELETE);
				break;
			default:
				// Update Reunification Assessment
				sdmReunificationAsmntReq.setReqFuncCd(ServiceConstants.UPDATE);
				break;
			}
		}
		// Call the service method Insert the SDM Reunification Assessment
		// Record.
		response = sdmReunificaitonService.sdmReunificationAsmntAUD(sdmReunificationAsmntReq);
		log.info("SdmReunificationAsmntAUD In SDMReunificationAsmntController : Return response.");
		return response;
	}

	/**
	 * Method Name: isReunfctnAsmntForHshldAvlble Method Description: Service
	 * method for checking if the household is available in any other InProgress
	 * Assessment for current stage.
	 * 
	 * @param req
	 * @return response
	 */
	@RequestMapping(value = "/isReunfctnAsmntForHshldAvlble", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes isReunfctnAsmntForHshldAvlble(@RequestBody CommonApplicationReq req) {
		log.info("isReunfctnAsmntForHshldAvlble In SDMReunificationAsmntController : Execution Started.");
		CommonBooleanRes response = new CommonBooleanRes();
		if (TypeConvUtil.isNullOrEmpty(req.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(req.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		response.setExists(sdmReunificaitonService.isHouseholdHavePendAsmnt(req.getIdStage(), req.getIdPerson(),
				req.getIdEvent()));
		log.info("isReunfctnAsmntForHshldAvlble In SDMReunificationAsmntController : Return response.");
		return response;
	}

	/**
	 * Method Name: isReunfctnAsmntForParentAvlble Method Description: Service
	 * method for checking if the parent is available as Primary or Secondary
	 * parent in any other InProgress Assessment for current stage.
	 * 
	 * @param req
	 * @return response
	 */
	@RequestMapping(value = "/isReunfctnAsmntForParentAvlble", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes isReunfctnAsmntForParentAvlble(@RequestBody CommonApplicationReq req) {
		log.info("isReunfctnAsmntForParentAvlble In SDMReunificationAsmntController : Execution Started.");
		CommonBooleanRes response = new CommonBooleanRes();
		if (TypeConvUtil.isNullOrEmpty(req.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(req.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		response.setExists(
				sdmReunificaitonService.isParentHavePendAsmnt(req.getIdStage(), req.getIdPerson(), req.getIdEvent()));
		log.info("isReunfctnAsmntForParentAvlble In SDMReunificationAsmntController : Return response.");
		return response;
	}

}
