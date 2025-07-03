package us.tx.state.dfps.service.report.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.report.service.ReportRetrieveService;

@RestController
@RequestMapping("/carc06sController")
public class ReportRetrieveController {

	@Autowired
	ReportRetrieveService reportRetrieveService;

	public static final String ulIdPerson_STRING = "ulIdPerson";
	public static final int ulIdPerson_IND = 13;
	public static final String cSysIndRptRtrvType_STRING = "cSysIndRptRtrvType";
	public static final int cSysIndRptRtrvType_IND = 14;

	private static final Logger log = Logger.getLogger(ReportRetrieveController.class);

	/**
	 * Method Name: retrieveReportListFromCARC06S Method Description: This
	 * method calls the ReportServiceimpl to retrieve ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	@RequestMapping(value = "/retrieveReportListFromCARC06S", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ReportsResponse retrieveReportList(@RequestBody ReportsReq pInputMsg) {
		log.debug("Entering method retrieveReportListFromCARC06S in Carc06sController");
		ReportsResponse reportsRes = reportRetrieveService.retrieveReportListFromCARC06S(pInputMsg);
		log.debug("Exiting method CARC06S in Carc06sController");
		return reportsRes;
	}

	/**
	 * Method Name: deleteRecordFromReportList(CARC19S) Method Description: This
	 * method calls the ReportServiceimpl to delete record from ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	@RequestMapping(value = "/deleteRecordFromReportList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ReportsResponse deleteRecordFromReportList(@RequestBody ReportsReq pInputMsg) {
		log.debug("Entering method deleteRecordFromReportList in ReportRetrieveController");
		ReportsResponse reportsRes = reportRetrieveService.retrieveReportListFromCARC06S(pInputMsg);
		log.debug("Exiting method deleteRecordFromReportList in ReportRetrieveController");
		return reportsRes;
	}
}
