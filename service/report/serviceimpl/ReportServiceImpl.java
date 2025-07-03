package us.tx.state.dfps.service.report.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.ReportList;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ReportLaunchReq;
import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportLaunchRes;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.report.dao.ReportDao;
import us.tx.state.dfps.service.report.jms.QueueUtil;
import us.tx.state.dfps.service.report.service.ReportService;
import us.tx.state.dfps.service.reports.dto.ReportDto;
import us.tx.state.dfps.service.reports.dto.ReportListDto;
import us.tx.state.dfps.service.reports.dto.ReportParameterDto;
import us.tx.state.dfps.common.exception.ReportsException;

/**
 * report service implementation - IMPACT PHASE 2
 * Class Description: THis is the service that handles reports.
 * Feb 26, 2020- 12:48:11 PM
 * 2017 Texas Department of Family and Protective Services
 * ****************  Change History *********************
 * ---------- ------- ---------- IPS Initial Development
 * 02/26/2020  fowlej3 artf139227 Reports Test Page - Add getter for report parameters
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

	private static final Logger log = Logger.getLogger(ReportServiceImpl.class);

	@Autowired
	ReportDao reportDao;

	@Autowired
	QueueUtil queueUtil;

	// Constants
	private final String PARAM_INVALID = "Reports: Error Generating Report: Invalid Parameter.";
	private final String PARAM_INVALID_COUNT = "Parameters count did not match - Invalid Parameter";
	private final String PARAM_INVALID_LENGTH = "Parameters length exceeded - Invalid Parameter";
	private final String QUEUE_ERROR = "Reports: Error Sending Message to Queue-%s";

	/**
	 * This service will insert report to report list and sends JMS message
	 * 
	 * @param reportLaunchReq
	 * @return ReportLaunchRes
	 * @throws ServiceException,
	 *             ReportsException
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReportLaunchRes launchReport(ReportLaunchReq reportLaunchReq) throws ReportsException {
		log.debug("Entering method launchReport in Report Service");
		boolean invalidParamCheck;
		ReportDto reportTypeDetail = new ReportDto();
		ReportLaunchRes reportLaunchRes = new ReportLaunchRes();
		Long ulRptMaxAge;

		invalidParamCheck = checkParameters(reportLaunchReq);

		if (!invalidParamCheck) {
			// fetch the details of the report type from Reports table
			reportTypeDetail = reportDao.getReportTypeDetails(reportLaunchReq.getNmRptSqrName(),
					reportLaunchReq.getNmRptSqrVer());

			ulRptMaxAge = reportTypeDetail.getUlNbrRptRetainage();
			ReportListDto reportListDto = new ReportListDto();

			// Populate DTO to save
			if (!ObjectUtils.isEmpty(ulRptMaxAge)) {
				Date currentDate = new Date();
				Calendar calendar = Calendar.getInstance();
				String message = reportLaunchReq.getTxtEmailMessage();
				calendar.setTime(currentDate);
				calendar.add(Calendar.DATE, ulRptMaxAge.intValue());
				Date dtRetainage = calendar.getTime();
				reportListDto.setUlIdRptList(0L);
				reportListDto.setSzNmRptSqrName(reportLaunchReq.getNmRptSqrName());
				reportListDto.setSzNmRptSqrVer(reportLaunchReq.getNmRptSqrVer());
				reportListDto.setUlIdPerson(reportLaunchReq.getUidPerson());
				reportListDto.setUlRptLstCfpStamp(reportLaunchReq.getUlRptLstCfpStamp());
				reportListDto.setSzRptLstParmlist(reportLaunchReq.getTxtRptParmList());

				// Max length for setSzTxtRptLstRuntimeName is 48. If provided description has
				// length over 48, truncate the message
				if (!StringUtils.isEmpty(message) && message.length() > ServiceConstants.MAX_RPT_DESC_LENGTH) {
					message = message.substring(0, ServiceConstants.MAX_RPT_DESC_LENGTH);
				}
				reportListDto.setSzTxtRptLstRuntimeName(message);
				reportListDto.setSzTxtRptLstStatus(ServiceConstants.REPORT_STATUS_PEND);
				reportListDto.setDtDtRptLstGeneration(currentDate);
				reportListDto.setDtDtRptLstRetainage(dtRetainage);

				try {
					// Insert to Report List
					reportListDto = reportDao.saveReportList(reportListDto);
				}
				// If Exception occurs during insert, throw custom exception which will be
				// catched in controller.
				// This is required since any exception during process should skip send message
				// to queue and report error to web layer
				catch (Exception exception) {
					throw new ReportsException(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
				}

			}
			reportLaunchRes.setResult(ServiceConstants.REPORT_SUCCESS);
			reportLaunchRes.setReportListDto(reportListDto);
		}
		// Parameters are insufficient - Log Error and throw exception to caller
		else {
			log.error(PARAM_INVALID);
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_REPORT_PARM_INVALID);
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			reportLaunchRes.setErrorDto(errorDto);
		}

		return reportLaunchRes;
	}

	/**
	 * This method will insert validate the parameter list passed as part of report
	 * launch is valid
	 * 
	 * @param reportLaunchReq
	 * @return boolean
	 */
	private boolean checkParameters(ReportLaunchReq reportLaunchReq) {
		log.debug("Entering method generateTxtParameterList in Report Service");

		boolean bInvalidSqrParm = false;

		String txtInputParams = reportLaunchReq.getTxtRptParmList();

		// Split param string frm report list to extract only actual parameters
		// separated by ^
		// Don't have to consider anything follows ^^
		if (txtInputParams.contains(ServiceConstants.DOUBLE_CARET)) {
			String[] paramTxtParts = StringUtils.split(txtInputParams, ServiceConstants.DOUBLE_CARET);
			txtInputParams = paramTxtParts[0];
		}

		StringTokenizer inputParamToken = new StringTokenizer(txtInputParams, ServiceConstants.SINGLE_CARET);
		int actualCount = inputParamToken.countTokens();
		// Get configured parameters from DB to validate
		List<ReportParameterDto> configuredParams = reportDao.getReportParameters(reportLaunchReq.getNmRptSqrName(),
				reportLaunchReq.getNmRptSqrVer());

		// Compare parameters passed from report launch against params configured in
		// report_parameter table
		if (!ObjectUtils.isEmpty(configuredParams)) {
			// Count of parameters should match
			// Exit report processing when the param counts are not equal
			if (configuredParams.size() != actualCount) {
				//bInvalidSqrParm = true;
				log.debug(PARAM_INVALID_COUNT);
				return bInvalidSqrParm;
			}

			// Validate actual param length with param length in configuration
			for (ReportParameterDto currentParam : configuredParams) {
				String token = inputParamToken.nextToken();
				// Actual length of param should not exceed configured length
				// Exit report processing when the actual param length exceeds configured param
				// length
				if (token.length() > currentParam.getUlNbrRptParmLength()) {
					bInvalidSqrParm = true;
					log.debug(PARAM_INVALID_LENGTH);
				}
			}
		}

		return bInvalidSqrParm;
	}

	/**
	 * This service will update report status to pend and sends JMS message
	 * 
	 * @param reportLaunchReq
	 * @return ReportLaunchRes
	 * @throws NamingException
	 * @throws JMSException
	 * @throws ServiceException,
	 *             JMSException, NamingException, ReportsException
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReportLaunchRes retryLaunchReport(ReportLaunchReq reportLaunchReq) {
		log.debug("Entering method launchReport in Report Service");
		new ReportDto();
		ReportLaunchRes reportLaunchRes = new ReportLaunchRes();
		String messages = ServiceConstants.EMPTY_STRING;

		// Update Report List
		ReportList reportList = reportDao.getReportList(reportLaunchReq.getIdRetryReport());
		reportList.setTxtRptLstStatus(ServiceConstants.REPORT_STATUS_PEND);
		reportDao.updateReportList(reportList);
		// Write message to Queue
		try {
			messages = queueUtil.sendMessage(String.valueOf(reportLaunchReq.getIdRetryReport()));
		}catch (JMSException e) {
			throw new ServiceLayerException(e.getMessage());
		} catch (NamingException e) {
			throw new ServiceLayerException(e.getMessage());
		}

		// Check if message returned by insert method has error
		if (!messages.equalsIgnoreCase(ServiceConstants.SUCCESS)) {
			// Queuing message was not successful
			// Log the error
			log.error(String.format(QUEUE_ERROR, ServiceConstants.ERR_OCC_PLS_TRY_AGAIN));
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
			reportLaunchRes.setErrorDto(errorDto);
			throw new ReportsException(ServiceConstants.ERR_OCC_PLS_TRY_AGAIN);
		}

		reportLaunchRes.setResult(ServiceConstants.REPORT_SUCCESS);
		return reportLaunchRes;
	}
	
	/**
	 * This service will update report status
	 * 
	 * @param idReportList
	 * @param status
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateReportStatus(Long idReportList, String status) {
		ReportList report = reportDao.getReportList(idReportList);
		report.setTxtRptLstStatus(status);
		reportDao.updateReportList(report);
	}
	
	/**
	 * This service will update report's ind_auth column
	 * 
	 * @param idReportList
	 * @param authStatus
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReportsResponse updateReportListAuthStatus(ReportsReq reportsReq) {
		// Modified the code to the update the authorization token in DB - UAT
		// defect 10642.
		ReportsResponse reportsResponse = new ReportsResponse();
		boolean indUpdateSuccess = false;
		ReportList report = reportDao.getReportList(reportsReq.getIdReportList());
		// Generate token
		String authToken = UUID.randomUUID().toString();
		
		if(report.getPerson().getIdPerson().equals(reportsReq.getIdPerson())) {			
			report.setAuthToken(authToken);
			reportDao.updateReportList(report);
			indUpdateSuccess = true;
		}
		
		reportsResponse.setUpdateSuccess(indUpdateSuccess);
		reportsResponse.setAuthToken(authToken);
		
		return reportsResponse;		
	}

	// artf139227 - Reports Test Page - Add getter for report list with report parameters
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  List<ReportDto> getAllReportTypeDetails() {
		log.debug("Entering method getAllReportTypeDetails in ReportServiceImpl");
		List<ReportDto> reportResponse = reportDao.getRunnableReportDetails();

		// build temporary map to help with lookup.
		HashMap<String, ReportDto> temporary = new HashMap<>();
		reportResponse.stream().forEach(currentReport -> temporary.put(currentReport.getSzNmRptSqrName()+currentReport.getSzNmRptSqrVer(), currentReport));

		// map the parameters to their reports
		List<ReportParameterDto> reportParamResponse = reportDao.getRunnableReportParameters();
		for (ReportParameterDto currParam : reportParamResponse) {
			ReportDto currReport = temporary.get(currParam.getSzNmRptSqrName()+currParam.getSzNmRptSqrVer());
			currReport.getParameterMap().put(Short.valueOf(currParam.getUsNbrRptParmSeq()).intValue(), currParam);
		}

		return reportResponse;

	}
}
