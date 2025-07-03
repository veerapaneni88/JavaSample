package us.tx.state.dfps.service.stage.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.StageProgressionReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.stage.service.StageService;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 2:53:11 PM ©
 * 2017 Texas Department of Family and Protective Services
 * * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@RestController
@Api(tags = { "placements" })
@RequestMapping("/stage")
public class StageController {

	@Autowired
	private StageService stageUtilityService;
	
	@Autowired
	
	private StageUtilityService SUtilityService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOGSTAGECONTROLLER = Logger.getLogger("ServiceBusiness-StageUtilityControllerLog");

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param stageProgressionReq
	 * @return StageRes
	 */
	@ApiOperation(value = "Get stage details using id", tags = { "placements" })
	@RequestMapping(value = "/retrieveStageInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageRes retrieveStageInfo(@RequestBody StageProgressionReq stageProgressionReq) {
		LOGSTAGECONTROLLER.debug("Entering method retrieveStageInfo in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idStage.mandatory", null, Locale.US));
		}
		StageRes stageRes = new StageRes();
		stageRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOGSTAGECONTROLLER.info("TransactionId :" + stageProgressionReq.getTransactionId());
		stageRes.setStageValueBeanDto(stageUtilityService.retrieveStageInfo((long) stageProgressionReq.getIdStage()));

		LOGSTAGECONTROLLER.debug("Exiting method retrieveStageInfo in StageUtilityController");
		return stageRes;
	}
	
	
	/**
	 * Method Name: updateStageInfo Method Description: This method updates
	 * information from Stage table using idStage.
	 *  artf129782: Licensing Investigation Conclusion
	 * @param stageProgressionReq
	 * @return StageRes
	 */
	@RequestMapping(value = "/updateStageInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageRes updateStageInfo(@RequestBody StageProgressionReq stageProgressionReq) {
		LOGSTAGECONTROLLER.debug("Entering method updateStageInfo in StageController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idStage.mandatory", null, Locale.US));
		}
		StageRes stageRes = new StageRes();
		stageRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOGSTAGECONTROLLER.info("TransactionId :" + stageProgressionReq.getTransactionId());
		SUtilityService.updateStageInfo((long) stageProgressionReq.getIdStage());

		LOGSTAGECONTROLLER.debug("Exiting method updateStageInfo in StageController");
		return stageRes;
	}

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param stageProgressionReq
	 * @return StageRes
	 */
	@RequestMapping(value = "/retrievePriorStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes retrieveStageInfo(@RequestBody CommonHelperReq commonHelperReq) {
		LOGSTAGECONTROLLER.debug("Entering method retrieveStageInfo in StageUtilityController");
		CommonHelperRes stageRes = new CommonHelperRes();

		stageRes.setIdEvent(stageUtilityService.findPriorSubStageId(commonHelperReq.getIdStage()));

		LOGSTAGECONTROLLER.debug("Exiting method retrieveStageInfo in StageUtilityController");
		return stageRes;
	}

	/**
	 *
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/isPreSingleStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Boolean isPreSingleStage(@RequestBody CommonHelperReq commonHelperReq){
		return SUtilityService.checkPreSingleStageByStartDate(commonHelperReq.getDtStageStart());
	}

	@RequestMapping(value = "/isPreSingleStageByCaseId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public boolean isPreSingleStageByCaseId(@RequestBody CommonHelperReq commonHelperReq){
		return SUtilityService.checkPreSingleStageByCaseId(commonHelperReq.getIdCase());
	}
}
