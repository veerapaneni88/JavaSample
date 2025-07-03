package us.tx.state.dfps.service.childplan.controller;

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
import us.tx.state.dfps.service.childplan.service.ChildServicePlanFormService;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation for ChildPlanBean Nov 7, 2017- 9:17:44 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/child")
public class ChildServicePlanFormController {

	@Autowired
	private ChildServicePlanFormService childPlanService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger Log = Logger.getLogger(ChildServicePlanFormController.class);

	/**
	 * Method Name : getChildServicePlans Method Description : The Child's
	 * Service Plan establishes detailed case plans for providing services to
	 * children in substitute care and their families. Service name : CSUB82S
	 * 
	 * @param childPlanReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/servicePlans", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getChildServicePlans(@RequestBody ChildPlanReq childPlanReq) {
		Log.debug("Entering method getChildServicePlans in ChildServicePlanFormController");
		if (TypeConvUtil.isNullOrEmpty(childPlanReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(childPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory ", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(childPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory ", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(childPlanService.getChildServicesPlan(childPlanReq)));
		Log.debug("Exiting method deleteParticipant in ChildPlanController");
		return commonFormRes;
	}

}
