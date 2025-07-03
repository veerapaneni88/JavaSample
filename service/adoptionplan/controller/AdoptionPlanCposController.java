package us.tx.state.dfps.service.adoptionplan.controller;

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
import us.tx.state.dfps.service.adoptionplan.service.AdoptionPlanCposService;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * service call and sends prefill to forms Apr 16, 2018- 1:09:01 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("adoptionplan")
public class AdoptionPlanCposController {

	@Autowired
	private AdoptionPlanCposService adoptionPlanCposService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(AdoptionPlanCposController.class);

	@RequestMapping(value = "/getplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPlan(@RequestBody CommonApplicationReq request) {
		log.debug("Entering method CSUB65S getPlan in AdoptionPlanCposController");
		if (TypeConvUtil.isNullOrEmpty(request)) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(adoptionPlanCposService.getPlan(request)));

		return commonFormRes;
	}

}
