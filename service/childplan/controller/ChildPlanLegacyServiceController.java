package us.tx.state.dfps.service.childplan.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;
import us.tx.state.dfps.service.childplan.service.ChildPlanService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildPlanParticipReq;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.response.ChildPlanParticipRes;
import us.tx.state.dfps.service.common.response.ChildPlanRes;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sscc.util.SSCCRefUtil;
import us.tx.state.dfps.service.ssccchildplan.service.SSCCChildPlanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation for ChildPlanBean Nov 7, 2017- 9:17:44 AM © 2017 Texas
 * Department of Family and Protective Services.
 */
@RestController
@RequestMapping("/childPlanLegacy")
public class ChildPlanLegacyServiceController {

	@Autowired
	private ChildPlanService childPlanService;

	@Autowired
	private SSCCChildPlanService ssccChildPlanService;
	
	@Autowired
	SSCCRefUtil ssccRefUtil;

	@Autowired
	private MessageSource messageSource;

	private static final Logger Log = Logger.getLogger(ChildPlanLegacyServiceController.class);

	/**
	 * Method Name: getChildPlan Method Description: Retrieves the child plan
	 * details from the database.
	 *
	 * @param childPlanReq
	 *            the child plan req
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/getChildPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanRes getChildPlan(@RequestBody ChildPlanReq childPlanReq) {
		Log.debug("Entering method getChildPlan in ChildPlanController");
		if (TypeConvUtil.isNullOrEmpty(childPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		ChildPlanRes childPlanRes = new ChildPlanRes();
		childPlanRes = childPlanService.getChildPlan(childPlanReq);
		Log.debug("Exiting method getChildPlan in ChildPlanController");
		return childPlanRes;
	}

	/**
	 * Read referral by PK.
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the SSCC ref dto
	 */
	@RequestMapping(value = "/readReferralByPK", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCRefDto readReferralByPK(@RequestBody Long idSsccReferral) {
		Log.debug("Entering method readReferralByPK in ChildPlanController");
		SSCCRefDto ssccRefDto = ssccRefUtil.readSSCCRefByPK(idSsccReferral);
		Log.debug("Exiting method readReferralByPK in ChildPlanController");
		return ssccRefDto;
	}

	/**
	 * Method Name: getPlanTypeCode Method Description: This method is used to
	 * Query the plan type code for the given child plan.
	 *
	 * @param childPlanBean
	 *            the child plan bean
	 * @return String
	 */
	@RequestMapping(value = "/getPlanTypeCode", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanRes getPlanTypeCode(@RequestBody ChildPlanReq childPlanBean) {
		ChildPlanRes childPlanRes = new ChildPlanRes();
		Log.debug("Entering method getPlanTypeCode in ChildPlanController");
		String cdPlanType = ServiceConstants.NULL_STRING;
		if (ObjectUtils.isEmpty(childPlanBean.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		if (ObjectUtils.isEmpty(childPlanBean.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		cdPlanType = childPlanService.getPlanTypeCode(childPlanBean.getIdCase(), childPlanBean.getIdEvent());
		Log.debug("Exiting method getPlanTypeCode in ChildPlanController");
		childPlanRes.setCdPlanType(cdPlanType);
		return childPlanRes;
	}

	/**
	 * Method Name: checkIfEventIsLegacy Method Description: Queries a row from
	 * the EVENT_PLAN_LINK for the given event id to determine whether or not
	 * the event is a legacy event--one created before the initial launch of
	 * IMPACT.
	 *
	 * @param childPlanReq
	 *            the child plan req
	 * @return Boolean
	 */
	@RequestMapping(value = "/checkIfEventIsLegacy", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  Boolean checkIfEventIsLegacy(@RequestBody ChildPlanReq childPlanReq) {
		Log.debug("Entering method checkIfEventIsLegacy in ChildPlanController");
		Boolean res = ServiceConstants.FALSEVAL;

		if (ObjectUtils.isEmpty(childPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		res = childPlanService.checkIfEventIsLegacy(childPlanReq.getIdEvent());
		Log.debug("Exiting method checkIfEventIsLegacy in ChildPlanController");
		return res;
	}

	/**
	 * Method Name: deleteChildPlan Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 * 
	 * @param ChildPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/deleteChildPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanRes deleteChildPlan(@RequestBody ChildPlanReq childPlanReq) {
		ChildPlanRes childPlanRes = new ChildPlanRes();
		if (ObjectUtils.isEmpty(childPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(childPlanReq.getChildPlanLegacyDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		childPlanService.deleteChildPlan(childPlanReq);
		return childPlanRes;
	}
	/**
	 * Method Name: deleteChildPlanByEvent Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan And the associated records
	 *
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/deleteChildPlanByEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanRes deleteChildPlanByEvent(@RequestBody ChildPlanReq childPlanReq) {
		ChildPlanRes childPlanRes = new ChildPlanRes();
		childPlanService.deleteChildPlanByEvent(childPlanReq);
		return childPlanRes;
	}
	/**
	 * Method Name: saveChildPlanParticip Method Description:This Method is used
	 * to Savw the child plan Participant details.
	 * 
	 * @param ChildPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/saveChildPlanParticip", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanParticipRes saveChildPlanParticip(
			@RequestBody ChildPlanParticipReq childPlanParticipReq) {
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		if (ObjectUtils.isEmpty(childPlanParticipReq.getChildPlanParticipDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		childPlanParticipRes = childPlanService.saveChildPlanParticip(childPlanParticipReq.getChildPlanParticipDto());
		return childPlanParticipRes;
	}

	/**
	 * Method Name: saveChildPlanParticip Method Description:This Method is used
	 * to Savw the child plan Participant details.
	 * 
	 * @param ChildPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/deleteChildPlanParticip", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ChildPlanParticipRes deleteChildPlanParticip(@RequestBody ChildPlanParticipReq childPlanParticipReq) {
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		if (ObjectUtils.isEmpty(childPlanParticipReq.getChildPlanParticipDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (childPlanParticipReq.isIndSscc()) {
			SSCCChildPlanParticipDto ssccChildPlanParticipDto = new SSCCChildPlanParticipDto();
			ssccChildPlanParticipDto
					.setIdSSCCChildPlanParticipant(childPlanParticipReq.getChildPlanParticipDto().getIdChildPlanPart());
			ssccChildPlanService.deleteSSCCParticipant(ssccChildPlanParticipDto);
		} else {
			childPlanService.deleteChildPlanParticip(childPlanParticipReq.getChildPlanParticipDto());
		}
		return childPlanParticipRes;
	}

	/**
	 * Method Name: staffSearchResultInformation Method Description:This Method
	 * is used to Savw the child plan Participant details.
	 * 
	 * @param ChildPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/staffSearchResultInformation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanParticipRes staffSearchResultInformation(
			@RequestBody ChildPlanParticipReq childPlanParticipReq) {
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		if (ObjectUtils.isEmpty(childPlanParticipReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		childPlanParticipRes = childPlanService.staffSearchResultInformation(childPlanParticipReq.getIdStage());
		return childPlanParticipRes;
	}

	/**
	 * Method Name: saveChildPlanParticip Method Description:This Method is used
	 * to Savw the child plan Participant details.
	 * 
	 * @param ChildPlanReq
	 * @return ChildPlanRes
	 */
	@RequestMapping(value = "/fetchChildPlanParticipation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanParticipRes fetchChildPlanParticipation(
			@RequestBody ChildPlanParticipReq childPlanParticipReq) {
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		if (ObjectUtils.isEmpty(childPlanParticipReq.getChildPlanParticipDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		
		if(childPlanParticipReq.isIndSscc()) {
			childPlanParticipRes.setChildPlanParticipDto(childPlanService.fetchSsccChildPlanParticipant(childPlanParticipReq.getChildPlanParticipDto().getIdChildPlanPart()));
		}
		else {
			childPlanParticipRes = childPlanService.fetchChildPlanParticipant(
					childPlanParticipReq.getChildPlanParticipDto().getIdEvent(),
					childPlanParticipReq.getChildPlanParticipDto().getIdChildPlanPart());
		}
		return childPlanParticipRes;
	}

	@RequestMapping(value = "/saveChildPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanRes saveChildPlan(@RequestBody ChildPlanReq childPlanReq) {
		ChildPlanRes childPlanRes = new ChildPlanRes();
		childPlanRes = childPlanService.saveChildPlan(childPlanReq);
		return childPlanRes;
	}

}
