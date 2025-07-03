package us.tx.state.dfps.service.report.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.exception.ReportsException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ReportLaunchReq;
import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportLaunchRes;
import us.tx.state.dfps.service.common.response.ReportTypeDetailRes;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.report.jms.QueueUtil;
import us.tx.state.dfps.service.report.service.ReportService;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.Locale;

/**
 * report service controller - IMPACT PHASE 2 MODERNIZATION
 * Class Description: REST controller for report related services.
 * Sep 11, 2017- 5:33:55 PM
 * @ 2017 Texas Department of Family and Protective Services
 * ****************  Change History *********************
 * ---------- ------- ---------- IPS Initial Development
 * 02/26/2020 fowlej3 artf139227 Reports Test Page - Add getter for report parameters
 */
@RestController
@RequestMapping("/reportsvc")
public class ReportServiceController {

	private static final Logger log = Logger.getLogger("ServiceBusiness-ReportServiceControllerLog");
	private final String REPORT_ERROR = "Reports: Unable to process.";
	private final String QUEUE_ERROR = "Reports: Error Sending Message to Queue-%s. ";
	private final String CHECK_REPORTS = "Please check Reports tab for any record with Error status.";

	@Autowired
	MessageSource messageSource;

	@Autowired
	ReportService reportService;

	@Autowired
	QueueUtil queueUtil;

	/**
	 * This Controller calls service to insert report to report list and sends JMS
	 * message
	 *
	 * @param reportLaunchReq
	 * @return ReportLaunchRes
	 */
	@RequestMapping(value = "/launchReport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ReportLaunchRes launchReport(@RequestBody ReportLaunchReq reportLaunchReq){
		log.debug("Entering method launchReport in ReportServiceController");
		ReportLaunchRes launchRes = new ReportLaunchRes();
		ErrorDto errorDto = null;
		launchRes.setErrorDto(errorDto);
		if (TypeConvUtil.isNullOrEmpty(reportLaunchReq.getNmRptSqrName())) {
			errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.SSM_COMPLETE_REQUIRED);
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			launchRes.setErrorDto(errorDto);
			throw new InvalidRequestException(
					messageSource.getMessage("report.square.name.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(reportLaunchReq.getNmRptSqrVer())) {
			errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.SSM_COMPLETE_REQUIRED);
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			launchRes.setErrorDto(errorDto);
			throw new InvalidRequestException(
					messageSource.getMessage("report.square.version.mandatory", null, Locale.US));
		}
		boolean indReportsError = false;
		try {
			// Call service to Insert Report List and Add message to Queue
			launchRes = reportService.launchReport(reportLaunchReq);
		} catch (ReportsException exception) {
			indReportsError = true;
			log.error(String.format(REPORT_ERROR, ServiceConstants.ERR_OCC_PLS_TRY_AGAIN));
		}

		if (!ObjectUtils.isEmpty(launchRes.getErrorDto())) {
			indReportsError = true;
		}

		// Write message to Queue if there is no error in insert
		if (!indReportsError) {
			String messages=ServiceConstants.EMPTY_STR;
			try {
				messages = queueUtil.sendMessage(String.valueOf(launchRes.getReportListDto().getUlIdRptList()));
			} catch (ReportsException | JMSException | NamingException e) {
				log.error(String.format(QUEUE_ERROR, ServiceConstants.ERR_OCC_PLS_TRY_AGAIN));
				log.error(e.getMessage());
				indReportsError = true;
			}

			// Check if message returned by queue process has error
			if (!messages.equalsIgnoreCase(ServiceConstants.SUCCESS)) {
				// Queuing message was not successful
				// Log the error and throw exception to caller
				log.error(String.format(QUEUE_ERROR, ServiceConstants.ERR_OCC_PLS_TRY_AGAIN));
				ErrorDto queueError = new ErrorDto();
				queueError.setErrorCode(ServiceConstants.SSM_COMPLETE_REQUIRED);
				queueError.setErrorMsg(String.format(QUEUE_ERROR, CHECK_REPORTS));
				launchRes.setErrorDto(queueError);
				//update report list status to err
				reportService.updateReportStatus(launchRes.getReportListDto().getUlIdRptList(), ServiceConstants.REPORT_STATUS_ERR);
			}
		}

		log.debug("Exiting method launchReport in ReportServiceController");
		return launchRes;
	}

	/**
	 * This Controller calls service to retry failed report launch by sending JMS
	 * message
	 *
	 * @param reportLaunchReq
	 * @return ReportLaunchRes
	 */
	@RequestMapping(value = "/retryLaunchReport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ReportLaunchRes retryLaunchReport(@RequestBody ReportLaunchReq reportLaunchReq){
		log.debug("Entering method retryLaunchReport in ReportServiceController");
		ReportLaunchRes launchRes = new ReportLaunchRes();
		ErrorDto errorDto = null;
		launchRes.setErrorDto(errorDto);
		if (TypeConvUtil.isNullOrEmpty(reportLaunchReq.getIdRetryReport())) {
			errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_REPORT_PARM_INVALID);
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			launchRes.setErrorDto(errorDto);
			throw new InvalidRequestException(messageSource.getMessage("report.square.id.mandatory", null, Locale.US));
		}
		// Call service to update Report List and Add message to Queue
		launchRes = reportService.retryLaunchReport(reportLaunchReq);

		log.debug("Exiting method retryLaunchReport in ReportServiceController");
		return launchRes;
	}

	/**
	 * This Controller calls service to update report list to indicate if the report is authorized
	 * for view
	 *
	 * @param reportLaunchReq
	 * @return ReportLaunchRes
	 */
	@RequestMapping(value = "/updateReportListForAuth", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ReportsResponse updateReportListForAuth(@RequestBody ReportsReq reportsReq){
		log.debug("Entering method updateReportListForAuth in ReportServiceController");
		ReportsResponse updateRes = new ReportsResponse();
		ErrorDto errorDto = null;
		updateRes.setErrorDto(errorDto);
		if (TypeConvUtil.isNullOrEmpty(reportsReq.getIdReportList())) {
			errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_REPORT_PARM_INVALID);
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			updateRes.setErrorDto(errorDto);
			throw new InvalidRequestException(messageSource.getMessage("report.square.id.mandatory", null, Locale.US));
		}

		// Modified the code to the update the authorization token - UAT defect
		// 10642.Call service to update Report List Auth token
		updateRes = reportService.updateReportListAuthStatus(reportsReq);

		log.debug("Exiting method updateReportListForAuth in ReportServiceController");
		return updateRes;
	}

	// artf139227 Add getter for report parameters
	@RequestMapping(value = "/getAllReportTypeDetails", headers = { "Accept=application/json" }, method = RequestMethod.GET)
	public ReportTypeDetailRes getAllReportTypeDetails(){
		log.debug("Entering method getAllReportTypeDetails in ReportServiceController");
		ReportTypeDetailRes returnWrapper = new ReportTypeDetailRes();
		returnWrapper.setReportDetails(reportService.getAllReportTypeDetails());
		log.debug("Exiting method getAllReportTypeDetails in ReportServiceController");
		return returnWrapper;
	}

}
