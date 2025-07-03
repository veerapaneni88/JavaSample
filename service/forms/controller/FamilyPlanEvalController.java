/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 7, 2018- 10:37:41 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FamilyPlanEvalReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.service.FamilyPlanEvalService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:(CSVC24S)
 * Service for IMPACT FGDM Family Plan/Evaluation. Mar 7, 2018- 10:37:41 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/famPlanEval")
public class FamilyPlanEvalController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FamilyPlanEvalService familyPlanEvalService;

	private static final Logger log = Logger.getLogger("ServiceBusiness-FamilyPlanEvalController");

	@RequestMapping(value = "/getFamilyPlanEvalForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getFamilyPlanEvalFrom(@RequestBody FamilyPlanEvalReq familyPlanEvalReq) {
		if (TypeConvUtil.isNullOrEmpty(familyPlanEvalReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanEvalReq.getIdSvcPlnEvalEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.svcplnevaleventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanEvalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		/*
		 * FamilyDto familyDto = new FamilyDto(); familyDto =
		 * familyPlanEvalService.getFamilyPlanService(familyPlanEvalReq);
		 */

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(familyPlanEvalService.getFamilyPlanService(familyPlanEvalReq)));
		log.info("TransactionId :" + familyPlanEvalReq.getTransactionId());
		return commonFormRes;
	}
}
