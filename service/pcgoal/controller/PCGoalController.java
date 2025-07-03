package us.tx.state.dfps.service.pcgoal.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.PCGoalReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pcgoal.service.PCGoalService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * class for CCMN56S to populate form CCMN0100 Mar 5, 2018- 4:37:34 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/pcgoal")
public class PCGoalController {

	@Autowired
	PCGoalService pCGoalService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("PCGoalControllerLog.class");

	/**
	 * 
	 * Method Description: Populates form ccmn0100 Name: CCMN56S
	 * 
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getpcgoal", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPopulateLetter(PCGoalReq pCGoalReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(pCGoalService.getPCGoal(pCGoalReq)));
		return commonFormRes;
	}

}
