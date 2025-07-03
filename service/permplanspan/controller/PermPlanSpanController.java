package us.tx.state.dfps.service.permplanspan.controller;

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
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.permplanspan.service.PermPlanSpanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Controller
 * for Permanency Planning serviced Feb 21, 2018- 2:41:21 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/permplanspan")
public class PermPlanSpanController {

	@Autowired
	private PermPlanSpanService permPlanSpanService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PermPlanSpanController.class);

	/**
	 * 
	 * Method Name: getPpmData Method Description: This method used to validate
	 * the incoming request and retrieving the Participant data
	 * 
	 * @param ppmReq
	 * @return
	 */
	@RequestMapping(value = "/getData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPpmData(@RequestBody PpmReq ppmReq) {
		log.debug("Entering method CSUB76S getPpmData in PpmController");

		if (TypeConvUtil.isNullOrEmpty(ppmReq.getIdPptEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.pptEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ppmReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ppmReq.getIdPptPart())) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.ptPart.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(permPlanSpanService.getPermPlan(ppmReq)));

		log.debug("Exiting method CSUB76S getPpmData in PpmController");
		return commonFormRes;
	}
}
