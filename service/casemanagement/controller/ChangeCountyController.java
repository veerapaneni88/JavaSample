package us.tx.state.dfps.service.casemanagement.controller;

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
import us.tx.state.dfps.service.casemanagement.service.ChangeCountyService;
import us.tx.state.dfps.service.common.request.ChangeCountyReq;
import us.tx.state.dfps.service.common.response.ChangeCountyRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:Controller Class for updating County
 *
 * Aug 17, 2017- 12:41:52 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/caseCountyMaintenance")
public class ChangeCountyController {

	@Autowired
	ChangeCountyService changeCountyService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ChangeCountyController.class);

	/**
	 * 
	 * Method Name: changeCounty Method Description: Method for updating Stage
	 * and Caps Resource County
	 * 
	 * @param changeCountyReq
	 * @return changeCountyRes
	 */
	@RequestMapping(value = "/changeCaseCounty", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ChangeCountyRes changeCounty(@RequestBody ChangeCountyReq changeCountyReq) {
		log.debug("Entering method changeCounty in ChangeCountyController");
		ChangeCountyRes changeCountyRes = new ChangeCountyRes();
		if (TypeConvUtil.isNullOrEmpty(changeCountyReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ChangeCounty.ulIdStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(changeCountyReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ChangeCounty.ulIdCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(changeCountyReq.getCdStageCnty())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ChangeCounty.county.mandatory", null, Locale.US));
		}
		changeCountyRes = changeCountyService.changeCountyService(changeCountyReq);
		log.debug("Exiting method changeCounty in ChangeCountyController");
		return changeCountyRes;
	}
}
