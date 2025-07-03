/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 11, 2018- 2:11:04 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.subcare.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.subcare.service.SubVisitationPlanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * is used to retrieve the information on the subcare Visitation Plan form. May
 * 11, 2018- 2:11:04 PM © 2017 Texas Department of Family and Protective
 * Services
 */

@RestController
@RequestMapping("/subvisitplan")
public class SubVisitationPlanController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SubVisitationPlanService subVisitationPlanService;

	@RequestMapping(value = "/getsubvisitplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getSubVisitationPlanDetails(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(subVisitationPlanService.getSubVisitPlanDetail(commonHelperReq)));

		return commonFormRes;
	}
}
