package us.tx.state.dfps.service.investigation.controller;

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
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.FacilityAbuseInvReportService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Receives
 * input data and returns prefill string from service Apr 30, 2018- 4:42:18 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("facabuse")
public class FacilityAbuseInvReportController {

	@Autowired
	private FacilityAbuseInvReportService facilityAbuseInvReportService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(FacilityAbuseInvReportController.class);

	/**
	 * Method Name: getAbuseReport Method Description: Gets information about
	 * abuse report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 * 
	 */
	@RequestMapping(value = "/getreport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAbuseReport(@RequestBody CommonApplicationReq request) {
		log.debug("Entering method cinv71s getAbuseReport in FacilityAbuseInvReportController");
		if (TypeConvUtil.isNullOrEmpty(request)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(facilityAbuseInvReportService.getAbuseReport(request)));
		//Added for defect 14421 fix. Set the UserLogonId with User Id which is used to populate the SNOOP_LOG table.
		request.setUserLogonId(request.getUserId());
		return commonFormRes;
	}

	
	/**
	 * Method Name: getAbuseReport Method Description: Gets information about
	 * abuse report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 * 
	 */
	@RequestMapping(value = "/getStageIdForDataFix", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getStageIdForDataFix(@RequestBody CommonApplicationReq request) {
		log.debug("Entering method getStageIdForDataFix getAbuseReport in FacilityAbuseInvReportController");
		if (TypeConvUtil.isNullOrEmpty(request)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();	
		commonFormRes.setRowQty(facilityAbuseInvReportService.getStageIdForDataFix(request.getIdStage()).toString());	
		return commonFormRes;
	}
}
