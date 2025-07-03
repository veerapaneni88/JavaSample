package us.tx.state.dfps.service.report.service;

import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportsResponse;

public interface ReportRetrieveService {
	/**
	 * Method Name:retrieveReportListFromCARC06S Method Description: This method
	 * calls the ReportListRtrvDelDao to retrieve ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	public ReportsResponse retrieveReportListFromCARC06S(ReportsReq pInputMsg);

	/**
	 * Method deleteRecordFromReportList(CARC19S) Method Description: This
	 * method calls the ReportListRtrvDelDao to delete record from ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	public ReportsResponse deleteRecordFromReportList(ReportsReq pInputMsg);

}
