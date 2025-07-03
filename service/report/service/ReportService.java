package us.tx.state.dfps.service.report.service;

import us.tx.state.dfps.common.exception.ReportsException;
import us.tx.state.dfps.service.common.request.ReportLaunchReq;
import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportLaunchRes;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.reports.dto.ReportDto;

import java.util.List;

public interface ReportService {

	/**
	 * This service will call report generation process
	 * 
	 * @param idReportList
	 * @param status
	 */	
	public ReportLaunchRes launchReport(ReportLaunchReq reportLaunchReq)
			throws ReportsException;

	/**
	 * This service will retry report generation for report in Err status
	 * 
	 * @param idReportList
	 * @param status
	 */	
	public ReportLaunchRes retryLaunchReport(ReportLaunchReq reportLaunchReq);
	
	/**
	 * This service will update report status
	 * 
	 * @param idReportList
	 * @param status
	 */	
	public void updateReportStatus(Long idReportList, String status);
	
	/**
	 * This service will update report's authorization code in Auth_Text column
	 * Modified the code for UAT DEFECT 10642
	 * 
	 * @param reportsReq
	 */	
	public ReportsResponse updateReportListAuthStatus(ReportsReq reportsReq);

	/**
	 * This service returns a list of all on demand reports including parameters to invoke them.
	 * @return a list of all on demand reports including parameters to invoke them.
	 */
	public  List<ReportDto> getAllReportTypeDetails();
}
