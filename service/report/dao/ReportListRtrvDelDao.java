package us.tx.state.dfps.service.report.dao;

import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportsResponse;

public interface ReportListRtrvDelDao {
	/**
	 * method name: fetchReportListFromCDYN24DAM Method Description: This method
	 * call CDYN24DAM service to fetch record for report list
	 * 
	 * @param reportsReq
	 * @return @
	 */
	public ReportsResponse fetchReportList(ReportsReq reportsReq);

	/**
	 * method name: deleteRecordFromReportList Method Description: This method
	 * call Cauda2d service to delete record from report list
	 * 
	 * @param reportsReq
	 * @return @
	 */
	public ReportsResponse deleteRecordFromReportList(ReportsReq reportsReq);

}
