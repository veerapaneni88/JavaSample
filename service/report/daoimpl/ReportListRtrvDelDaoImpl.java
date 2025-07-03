package us.tx.state.dfps.service.report.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ReportRtrvDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ReportsReq;
import us.tx.state.dfps.service.common.response.ReportsResponse;
import us.tx.state.dfps.service.report.dao.ReportListRtrvDelDao;

@Repository
public class ReportListRtrvDelDaoImpl implements ReportListRtrvDelDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ReportRtrvDaoImpl.SQL_SELECT_STATEMENT}")
	private transient String SQL_SELECT_STATEMENT;
	@Value("${ReportRtrvDaoImpl.SQL_CHECK_REPORT_TYPE}")
	private transient String SQL_CHECK_REPORT_TYPE;
	@Value("${ReportRtrvDaoImpl.SQL_END_WHERE}")
	private transient String SQL_END_WHERE;
	@Value("${Cauda2dDaoImpl.deleteRecordFromReportList}")
	private String deleteRecordFromReportList;
	private static final Logger log = Logger.getLogger(ReportListRtrvDelDaoImpl.class);

	/**
	 * method name: fetchReportListFromCDYN24DAM method Desc: This method
	 * retrieves list of rows from ReportList and Report table based on dynamic
	 * input
	 * 
	 * @param ReportsReq
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ReportsResponse fetchReportList(ReportsReq pInputDataRec) {
		log.debug("Entering method fetchReportListFromCDYN24DAM in ReportListRtrvDelDaoImpl");
		ReportsResponse reportsResponse = new ReportsResponse();
		StringBuilder hostszDynamicSQL = new StringBuilder(SQL_SELECT_STATEMENT);
		if (pInputDataRec.getIndRptRtrvType().equalsIgnoreCase(ServiceConstants.N)) {
			hostszDynamicSQL.append(SQL_CHECK_REPORT_TYPE);
		}
		hostszDynamicSQL.append(SQL_END_WHERE);
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hostszDynamicSQL.toString())
				.addScalar("rptFullName", StandardBasicTypes.STRING)
				.addScalar("rptLstStatus", StandardBasicTypes.STRING)
				.addScalar("dtRptLstGeneration", StandardBasicTypes.DATE)
				.addScalar("dtRptLstRetainage", StandardBasicTypes.DATE)
				.addScalar("rptLstRuntimeName", StandardBasicTypes.STRING)
				.addScalar("idRptList", StandardBasicTypes.LONG).addScalar("nmRptSqrName", StandardBasicTypes.STRING)
				.addScalar("nmRptSqrVer", StandardBasicTypes.STRING).addScalar("rptGenName", StandardBasicTypes.STRING)
				.addScalar("nmRptOrientation", StandardBasicTypes.STRING)
				.addScalar("nmRptTemplateName", StandardBasicTypes.STRING)
				.addScalar("nmRptType", StandardBasicTypes.STRING)
				.addScalar("rptEmailOption", StandardBasicTypes.STRING)
				.addScalar("idRptListContent", StandardBasicTypes.LONG));

		sQLQuery.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson());
		sQLQuery.setResultTransformer(Transformers.aliasToBean(ReportRtrvDto.class));
		List<ReportRtrvDto> reportRtrvDtoList = (List<ReportRtrvDto>) sQLQuery.list();
		if (!ObjectUtils.isEmpty(reportRtrvDtoList)) {
			reportsResponse.setReportRtrvDtoList(reportRtrvDtoList);
		}
		return reportsResponse;

	}

	/**
	 * Method Name: deleteRecordFromReportList
	 * 
	 * @param reportsReq
	 * @return @
	 */

	@Override
	public ReportsResponse deleteRecordFromReportList(ReportsReq reportsReq) {
		ReportsResponse reportRes = new ReportsResponse();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(deleteRecordFromReportList).setParameter("idReport",
				reportsReq.getReportRtrvDto().getIdRptList());
		query.executeUpdate();
		reportRes.getErrorDto().setErrorMsg(ServiceConstants.SUCCESS);
		log.debug("Exiting method cauda2dAUDdam in Cauda2dDaoImpl");
		return reportRes;
	}

}
