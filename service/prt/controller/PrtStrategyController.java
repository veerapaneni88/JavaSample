package us.tx.state.dfps.service.prt.controller;

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
import us.tx.state.dfps.service.common.request.BasePRTSessionReq;
import us.tx.state.dfps.service.common.response.BasePRTSessionRes;
import us.tx.state.dfps.service.common.response.PrtStrategyRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.prt.service.PrtStrategyService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionController for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/PRTStrategyController")
public class PrtStrategyController {

	@Autowired
	private PrtStrategyService prtStrategyService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-BasePRTSessionControllerLog");

	/**
	 * method name: fetchPrtStrategy description: This method fetches all
	 * strategies from action plan/followup
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/fetchPrtStrategy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrtStrategyRes fetchPrtStrate(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method saveChildPlanGoals in BasePRTSessionController");
		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();
		prtStrategyRes = prtStrategyService.fetchPRTStrategy(basePRTSessionReq.getPrtStrategyValueDto());
		return prtStrategyRes;
	}

	/**
	 * method name: fetchPrtStrategyList description: This method updates Latest
	 * Child Plan Goals into PRT Tables.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/fetchPrtStrategyList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PrtStrategyRes fetchPrtStrategyList(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method saveChildPlanGoals in BasePRTSessionController");
		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();
		prtStrategyRes = prtStrategyService.fetchPRTStrategy(basePRTSessionReq.getPrtStrategyValueDto());
		return prtStrategyRes;
	}

	/**
	 * method name: insertPRTTask description: This method saves PRT Task.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/insertPRTTask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  BasePRTSessionRes insertPRTTask(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method insertPRTTask in BasePRTSessionController");
		BasePRTSessionRes basePRTSessionRes = new BasePRTSessionRes();
		if (TypeConvUtil.isNullOrEmpty(basePRTSessionReq.getPrtTaskValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("PRT.PRTTask.message", null, Locale.US));
		}
		basePRTSessionRes.setTransactionId(basePRTSessionReq.getTransactionId());
		LOG.info("TransactionId :" + basePRTSessionReq.getTransactionId());
		basePRTSessionRes.setIdPrtStrategy(prtStrategyService.insertPRTTask(basePRTSessionReq.getPrtStrategyValueDto().getPrtTaskValueDtoList()));
		return basePRTSessionRes;
	}

	/**
	 * method name: deletePRTTask description: This method deletes PRT Task
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/deletePRTTask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  BasePRTSessionRes deletePRTTask(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method deletePRTTask in BasePRTSessionController");
		BasePRTSessionRes basePRTSessionRes = new BasePRTSessionRes();
		if (TypeConvUtil.isNullOrEmpty(basePRTSessionReq.getIdPrtTask())) {
			throw new InvalidRequestException(messageSource.getMessage("PRT.IdPrt.message", null, Locale.US));
		}
		basePRTSessionRes.setTransactionId(basePRTSessionReq.getTransactionId());
		LOG.info("TransactionId :" + basePRTSessionReq.getTransactionId());
		basePRTSessionRes.setTotalRecCount(prtStrategyService.deletePRTTask(basePRTSessionReq.getIdPrtTask()));
		return basePRTSessionRes;
	}

	/**
	 * method name: insertPRTStrategy description: This method saves new PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/insertPRTStrategy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  BasePRTSessionRes insertPRTStrategy(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method insertPRTStrategy in BasePRTSessionController");
		BasePRTSessionRes basePRTSessionRes = new BasePRTSessionRes();
		if (TypeConvUtil.isNullOrEmpty(basePRTSessionReq.getPrtStrategyValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("PRT.Strategy.message", null, Locale.US));
		}
		basePRTSessionRes.setTransactionId(basePRTSessionReq.getTransactionId());
		LOG.info("TransactionId :" + basePRTSessionReq.getTransactionId());
		basePRTSessionRes
				.setIdPrtStrategy(prtStrategyService.insertPRTStrategy(basePRTSessionReq.getPrtStrategyValueDto()));
		return basePRTSessionRes;
	}

	/**
	 * method name: fetchChildren description: This method fetches children
	 * associated with the PRT
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * 
	 */
	@RequestMapping(value = "/fetchChildren", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrtStrategyRes fetchChildren(@RequestBody BasePRTSessionReq basePRTSessionReq){
		LOG.debug("Entering method saveChildPlanGoals in BasePRTSessionController");
		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();
		prtStrategyRes = prtStrategyService.fetchInitialChildList(basePRTSessionReq.getPrtStrategyValueDto());
		return prtStrategyRes;
	}

}
