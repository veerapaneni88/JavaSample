package us.tx.state.dfps.service.report.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.report.dao.ReportListRtrvDelDao;
import us.tx.state.dfps.service.report.service.ReportRetrieveService;

@Service
@Transactional
public class ReportRetrieveServiceImpl implements ReportRetrieveService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ReportListRtrvDelDao repListRtrvDelDao;

	public static final String ulIdPerson_STRING = "ulIdPerson";
	public static final int ulIdPerson_IND = 13;
	public static final String cSysIndRptRtrvType_STRING = "cSysIndRptRtrvType";
	public static final int cSysIndRptRtrvType_IND = 14;

	private static final Logger log = Logger.getLogger(ReportRetrieveServiceImpl.class);

	/**
	 * Method Name:retrieveReportListFromCARC06S Method Description: This method
	 * calls the ReportListRtrvDelDao to retrieve ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ReportsResponse retrieveReportListFromCARC06S(ReportsReq pInputMsg) {
		log.debug("Entering method retrieveReportListFromCARC06S in ReportServiceImpl");
		ReportsResponse reportResponse = new ReportsResponse();
		reportResponse = repListRtrvDelDao.fetchReportList(pInputMsg);
		return reportResponse;

	}

	/**
	 * Method Name: deleteRecordFromReportList Method Description: This method
	 * calls the ReportListRtrvDelDao to delete a record from ReportList
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ReportsResponse deleteRecordFromReportList(ReportsReq pInputMsg) {
		log.debug("Entering method deleteRecordFromReportList in ReportServiceImpl");
		ReportsResponse reportResponse = new ReportsResponse();
		reportResponse = repListRtrvDelDao.deleteRecordFromReportList(pInputMsg);
		return reportResponse;
	}

}
