package us.tx.state.dfps.service.placement.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * controller has the operations which are related to placementInfo form Jan 27,
 * 2018- 1:11:07 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("placementform")
public class ChildPlanPlacementController {

	private static final Logger log = Logger.getLogger(ChildPlanPlacementController.class);

	/**
	 * 
	 * Method Name: getChildPlanPlacement Method Description: To retrieve the
	 * Child's Plan placement header information CSUB24S
	 * 
	 * @param commonApplicationReq
	 * @return childPlanPlacementDto
	 */
	/*
	 * @RequestMapping(value = "/getChildPlanPlacement", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * 
	 *  public CommonFormRes getChildPlanPlacement(@RequestBody
	 * CommonApplicationReq commonApplicationReq) { log.
	 * debug("Entering method getChildPlanPlacement in ChildPlanPlacementController"
	 * );
	 * 
	 * if (ObjectUtils.isEmpty(commonApplicationReq.getIdEvent())) { throw new
	 * InvalidRequestException(messageSource.getMessage(
	 * "common.eventid.mandatory", null, Locale.US)); } if
	 * (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) { throw new
	 * InvalidRequestException(messageSource.getMessage(
	 * "common.stageid.mandatory", null, Locale.US)); } CommonFormRes
	 * commonFormRes = new CommonFormRes(); commonFormRes
	 * .setPreFillData(TypeConvUtil.getXMLFormat(childPlanPlacementService.
	 * getChildPlanPlacement(commonApplicationReq.getIdEvent(),
	 * commonApplicationReq.getIdStage())));
	 * 
	 * log.
	 * debug("Exiting method getChildPlanPlacement in ChildPlanPlacementController"
	 * ); return commonFormRes; }
	 */

}
