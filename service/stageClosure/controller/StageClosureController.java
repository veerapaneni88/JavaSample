package us.tx.state.dfps.service.stageClosure.controller;

import java.util.HashMap;
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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.StageClosureReq;
import us.tx.state.dfps.service.common.response.StageClosureRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.stageClosure.service.StageClosureService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageClosureController class will have all operation which are
 * related to StageClosureBean. Sep 6, 2017- 6:18:45 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@RestController
@RequestMapping("/stageClosureController")
public class StageClosureController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageClosureService stageClosureService;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-StageClosureControllerLog");

	/**
	 * This method is used to get PersonId In FDTC.
	 * 
	 * @param StageClosureReq
	 * @return StageClosureRes
	 */
	@RequestMapping(value = "/getPersonIdInFDTC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRes getPersonIdInFDTC(@RequestBody StageClosureReq stageClosureReq) {
		if (TypeConvUtil.isNullOrEmpty(stageClosureReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("StageClosure.getPersonIdInFDTC.mandatory", null, Locale.US));
		}
		HashMap<Long, String> resultHashMap = stageClosureService.getPersonIdInFDTC(stageClosureReq.getIdCase());
		StageClosureRes stageClosureRes = new StageClosureRes();
		stageClosureRes.setResultHashMap(resultHashMap);
		LOG.info(ServiceConstants.TRANSACTION_ID + stageClosureReq.getTransactionId());
		return stageClosureRes;

	}

	/**
	 * This method is used to get Most Recent FDTC Subtype method.
	 * 
	 * @param StageClosureReq
	 * @return StageClosureRes
	 */

	@RequestMapping(value = "/getMostRecentFDTCSubtype", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRes getMostRecentFDTCSubtype(@RequestBody StageClosureReq stageClosureReq) {

		if (TypeConvUtil.isNullOrEmpty(stageClosureReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageclosure.getMostRecentFDTCSubtype.mandatory", null, Locale.US));
		}
		StageClosureRes stageClosureRes = new StageClosureRes();
		stageClosureRes.setHashmp(stageClosureService.getMostRecentFDTCSubtype(stageClosureReq.getIdPerson()));
		LOG.info(ServiceConstants.TRANSACTION_ID + stageClosureReq.getTransactionId());
		return stageClosureRes;
	}

	/**
	 * Method Name: getRunAwayStatus Method Description: This method is to check
	 * runAway status
	 * 
	 * @param stageClosureReq
	 * @return
	 */
	@RequestMapping(value = "/getRunAwayStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRes getRunAwayStatus(@RequestBody StageClosureReq stageClosureReq) {
		if (TypeConvUtil.isNullOrEmpty(stageClosureReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageclosure.getMostRecentFDTCSubtype.mandatory", null, Locale.US));
		}
		StageClosureRes stageClosureRes = new StageClosureRes();
		boolean indRunAway = stageClosureService.getRunAwayStatus(stageClosureReq.getIdPerson());
		stageClosureRes.setIndRunAway(indRunAway);
		LOG.info(ServiceConstants.TRANSACTION_ID + stageClosureReq.getTransactionId());
		return stageClosureRes;
	}

}
