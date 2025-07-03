package us.tx.state.dfps.service.familyplan.controller;

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
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyplan.service.FamilyServicePlanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Sends input
 * parameters to service and returns prefill data May 2, 2018- 4:30:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("familyplan")
public class FamilyServicePlanController {

	@Autowired
	private FamilyServicePlanService familyServicePlanService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(FamilyServicePlanController.class);

	/**
	 * Method Name: getServicePlan Method Description: Makes dao calls and
	 * returns prefill string for Family Service Plan
	 * 
	 * @param req
	 * @return
	 * 
	 */
	@RequestMapping(value = "/getserviceplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getServicePlan(@RequestBody CommonApplicationReq request) {
		log.debug("Entering method csvc08s getServicePlan in FacilityAbuseInvReportController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(familyServicePlanService.getServicePlan(request)));

		return commonFormRes;
	}

}
