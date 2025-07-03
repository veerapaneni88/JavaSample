/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This controller is rest service which do AUD operation for common application
 *Feb 08, 2018- 10:59:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.OutputLaunchRtrvReq;
import us.tx.state.dfps.service.common.request.OutputLaunchSaveReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.OutputLaunchRtrvoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.service.OutputLaunchRtrvService;
@Api(tags = { "commonApplication" })
@RestController
@RequestMapping("/OutputLaunchRtrvController")
public class OutputLaunchRtrvController {

	@Autowired
	OutputLaunchRtrvService objOutputLaunchRtrvService;
	@Autowired
	MessageSource messageSource;

	public static final String ulIdStage_STRING = "ulIdStage";
	public static final int ulIdStage_IND = 13;
	public static final String ulIdEvent_STRING = "ulIdEvent";
	public static final int ulIdEvent_IND = 14;
	public static final String szCdTask_STRING = "szCdTask";
	public static final int szCdTask_IND = 15;
	public static final String SUB_OL_EVNT_NARR_CTB = "CEVNTTBL";

	private static final Logger log = Logger.getLogger(OutputLaunchRtrvController.class);

	/**
	 * Method Description: (CSUB59S) This service will receive Id Event and
	 * invoke perform a full-row retrieval of the Event Table.Using CdEventType
	 * retrieved from the query, service will query one of the
	 * Document/Narrative tables to retrieve a time stamp.
	 *
	 * @param outputLaunchRtrvReq
	 * @return OutputLaunchRtrvoRes
	 */
	@ApiOperation(value = "Get common application details", tags = { "commonApplication" })
	@RequestMapping(value = "/outputlaunchrtrvo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OutputLaunchRtrvoRes retreiveOutputLaunch(
			@RequestBody OutputLaunchRtrvReq outputLaunchRtrvReq) {
		OutputLaunchRtrvoRes outputLaunchRetrRes = new OutputLaunchRtrvoRes();
		// check for mandatory task
		if (TypeConvUtil.isNullOrEmpty(outputLaunchRtrvReq.getCdTask())) {
			throw new InvalidRequestException(
					messageSource.getMessage("OutputLaunchRtrvController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(outputLaunchRtrvReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("OutputLaunchRtrvController.IdStage.mandatory", null, Locale.US));
		}
		log.debug("Entering method CSUB59S in OutputLaunchRtrvController");
		outputLaunchRetrRes = objOutputLaunchRtrvService.callOutputLaunchRtrvService(outputLaunchRtrvReq);
		return outputLaunchRetrRes;
	}

	/**
	 * Method Description: (CSUB60S) This service will add or update the EVENT
	 * table using the Post Event function.
	 *
	 * @param outPutLaunchSaveReq
	 * @return OutputLaunchRtrvoRes
	 */
	@ApiOperation(value = "Save common application details", tags = { "commonApplication" })
	@RequestMapping(value = "/saveOutputLaunch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OutputLaunchRtrvoRes saveOutputLaunch(@RequestBody OutputLaunchSaveReq outPutLaunchSaveReq) {
		// check for mandatory task
		if (TypeConvUtil.isNullOrEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdTask())) {
			throw new InvalidRequestException(
					messageSource.getMessage("OutputLaunchRtrvController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("OutputLaunchRtrvController.IdStage.mandatory", null, Locale.US));
		}
		log.debug("Entering method CSUB60S in OutputLaunchRtrvController");
		OutputLaunchRtrvoRes outputLaunchRtrvoRes = new OutputLaunchRtrvoRes();
		outputLaunchRtrvoRes = objOutputLaunchRtrvService.saveOutputLaunch(outPutLaunchSaveReq);
		objOutputLaunchRtrvService.copyNarrativeDocForNewUsing(outputLaunchRtrvoRes.getOutputLaunchRtrvoDto());
		return outputLaunchRtrvoRes;
				
	}

	/**
	 * Method Name:deleteDocument Method Description: Service method to delete
	 * the 'PROC' Document and and the associated events when the Delete button
	 * clicked in Common Application Detail page. Change Request (CR)
	 * P2-81-FORM-Common Application for Placement of Children in Residential
	 * Care.
	 * 
	 * @param outPutLaunchSaveReq
	 * @return CommonStringRes
	 */
	@ApiOperation(value = "delete common application event and narrative", tags = { "commonApplication" })
	@RequestMapping(value = "/deleteDocument", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonStringRes deleteDocument(@RequestBody OutputLaunchSaveReq outPutLaunchSaveReq) {
		// check for mandatory idEvent
		if (TypeConvUtil.isNullOrEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		log.debug("Entering method deleteDocument in OutputLaunchRtrvController");
		return objOutputLaunchRtrvService.deleteDocument(outPutLaunchSaveReq);
	}

}
