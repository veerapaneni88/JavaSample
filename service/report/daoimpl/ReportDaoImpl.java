package us.tx.state.dfps.service.report.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ReportList;
import us.tx.state.dfps.common.domain.Reports;
import us.tx.state.dfps.common.domain.ReportsId;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.report.dao.ReportDao;
import us.tx.state.dfps.service.reports.dto.ReportDto;
import us.tx.state.dfps.service.reports.dto.ReportListDto;
import us.tx.state.dfps.service.reports.dto.ReportParameterDto;
/**
 * report DAO implementation - IMPACT PHASE 2 MODERNIZATION
 * Class Description: DAO for reports.
 * Sep 11, 2017- 5:33:55 PM
 * @ 2017 Texas Department of Family and Protective Services
 * ****************  Change History *********************
 * ---------- ------- ---------- IPS Initial Development
 * 02/26/2020 fowlej3 artf139227 Reports Test Page - Add getter for report parameters
 */
@Repository
public class ReportDaoImpl implements ReportDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    MessageSource messageSource;

	@Value("${ReportDaoImpl.getReportParameters}")
	private String getReportParameters;

	@Value("${ReportDaoImpl.getReportTypeDetails}")
	private String getReportTypeDetails;

	@Value("${ReportDaoImpl.getRunnableReportDetails}")
	private String getRunnableReportDetails;

	// artf139227 Add getter for report parameters
	@Value("${ReportDaoImpl.getRunnableReportParameters}")
	private String getRunnableReportParameters;

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportParameterDto> getReportParameters(String sqrName, String sqrVer) {

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getReportParameters)
				.addScalar("ulIdRptParameter", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrName", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrVer", StandardBasicTypes.STRING)
				.addScalar("usNbrRptParmSeq", StandardBasicTypes.SHORT)
				.addScalar("ulNbrRptParmLength", StandardBasicTypes.LONG)
				.addScalar("szNmRptParmName", StandardBasicTypes.STRING)
				.addScalar("szTxtRptParmType", StandardBasicTypes.STRING).setString("hI_szNmRptSqrName", sqrName)
				.setString("hI_szNmRptSqrVer", sqrVer)
				.setResultTransformer(Transformers.aliasToBean(ReportParameterDto.class)));

		List<ReportParameterDto> reportParameters = new ArrayList<>();
		reportParameters = (List<ReportParameterDto>) sQLQuery.list();
		
		// If no data retrieved, throw exception
		if(ObjectUtils.isEmpty(reportParameters)) {
			throw new DataNotFoundException(messageSource.getMessage("report.list.nodata", null, Locale.US));
		}

		return reportParameters;
	}

	// artf139227 Add getter for report parameters
	@Override
	public List<ReportParameterDto> getRunnableReportParameters() {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRunnableReportParameters)
				.addScalar("ulIdRptParameter", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrName", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrVer", StandardBasicTypes.STRING)
				.addScalar("usNbrRptParmSeq", StandardBasicTypes.SHORT)
				.addScalar("ulNbrRptParmLength", StandardBasicTypes.LONG)
				.addScalar("szNmRptParmName", StandardBasicTypes.STRING)
				.addScalar("szTxtRptParmType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ReportParameterDto.class)));

		List<ReportParameterDto> reportParameters = new ArrayList<>();
		reportParameters = (List<ReportParameterDto>) sQLQuery.list();

		// If no data retrieved, throw exception
		if(ObjectUtils.isEmpty(reportParameters)) {
			throw new DataNotFoundException(messageSource.getMessage("report.list.nodata", null, Locale.US));
		}

		return reportParameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReportDto getReportTypeDetails(String sqrName, String sqrVer) {

		ReportDto reportDetails = new ReportDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getReportTypeDetails)
				.addScalar("dtScrDtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("szNmRptSqrName", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrVer", StandardBasicTypes.STRING)
				.addScalar("ulNbrRptRetainage", StandardBasicTypes.LONG)
				.addScalar("szNmRptType", StandardBasicTypes.STRING)
				.addScalar("szTxtRptFullName", StandardBasicTypes.STRING)
				.addScalar("szNmRptTemplateName", StandardBasicTypes.STRING)
				.addScalar("szNmRptOrientation", StandardBasicTypes.STRING)
				.addScalar("szTxtRptEmailOption", StandardBasicTypes.STRING).setString("hI_szNmRptSqrName", sqrName)
				.setString("hI_szNmRptSqrVer", sqrVer).setResultTransformer(Transformers.aliasToBean(ReportDto.class)));
		List<ReportDto> reportTypeList = (List<ReportDto>) sQLQuery1.list();

		if (!ObjectUtils.isEmpty(reportTypeList) && reportTypeList.size() > 0) {
			reportDetails = reportTypeList.get(0);
		}
		else {
			// If no data retrieved, throw exception
			throw new DataNotFoundException(messageSource.getMessage("report.list.nodata", null, Locale.US));
		}

		return reportDetails;

	}

	@Override
	public List<ReportDto> getRunnableReportDetails() {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getRunnableReportDetails)
				.addScalar("dtScrDtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("szNmRptSqrName", StandardBasicTypes.STRING)
				.addScalar("szNmRptSqrVer", StandardBasicTypes.STRING)
				.addScalar("ulNbrRptRetainage", StandardBasicTypes.LONG)
				.addScalar("szNmRptType", StandardBasicTypes.STRING)
				.addScalar("szTxtRptFullName", StandardBasicTypes.STRING)
				.addScalar("szNmRptTemplateName", StandardBasicTypes.STRING)
				.addScalar("szNmRptOrientation", StandardBasicTypes.STRING)
				.addScalar("szTxtRptEmailOption", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ReportDto.class)));
		List<ReportDto> reportTypeList = (List<ReportDto>) sQLQuery1.list();

		// If no data retrieved, throw exception
		if(ObjectUtils.isEmpty(reportTypeList)) {
			throw new DataNotFoundException(messageSource.getMessage("report.list.nodata", null, Locale.US));
		}

		return reportTypeList;

	}

	/**
	 * Method Name: saveReportList
	 * Method Description: Saves Report List Entry
	 * @param reportList
	 */
	@Override
	public ReportListDto saveReportList(ReportListDto reportListdto) {
		
		// populate entity with DTO values for save
		ReportList reportList = new ReportList();
		Reports report=new Reports();
		ReportsId reportId =new ReportsId();		
		Person person = new Person();		
		reportList.setTxtRptLstRuntimeName(reportListdto.getSzTxtRptLstRuntimeName());
		reportList.setDtRptLstRetainage(reportListdto.getDtDtRptLstRetainage());
		reportList.setTxtRptGenName(reportListdto.getSzTxtRptGenName());
		reportList.setDtRptLstGeneration(reportListdto.getDtDtRptLstGeneration());
		reportList.setDtLastUpdate(new Date());
		reportList.setTxtRptLstStatus(reportListdto.getSzTxtRptLstStatus());
		reportList.setTxtRptLstParmlist(reportListdto.getSzRptLstParmlist());
		reportId.setNmRptSqrName(reportListdto.getSzNmRptSqrName());
		reportId.setNmRptSqrVer(reportListdto.getSzNmRptSqrVer());
		report.setId(reportId);
		reportList.setReports(report);
		person.setIdPerson(reportListdto.getUlIdPerson());
		reportList.setPerson(person);
		// Save or update entity
		sessionFactory.getCurrentSession().saveOrUpdate(reportList);
		// Set DTO with new idReportList
		reportListdto.setUlIdRptList(reportList.getIdRptList());
		return reportListdto;
	}

	/**
	 * Method Name: getReportList
	 * Method Description: Gets Report List entry by ID
	 * @param reportList
	 */
	@Override
	public ReportList getReportList(Long idReportList) {
		ReportList reportList = (ReportList) sessionFactory.getCurrentSession().get(ReportList.class, idReportList);
		
		// If no data retrieved, throw exception
		if(ObjectUtils.isEmpty(reportList)) {
			throw new DataNotFoundException(messageSource.getMessage("report.list.nodata", null, Locale.US));
		}
		
		return reportList;
	}
	
	/**
	 * Method Name: updateReportList
	 * Method Description: Updates Report List entry
	 * @param reportList
	 */
	@Override
	public void updateReportList(ReportList reportList) {
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(reportList));
	}

	/**
	 * Method Name: deleteReportList
	 * Method Description: Deletes Report List entry
	 * @param reportList
	 */
	@Override
	public void deleteReportList(ReportList reportList) {
		sessionFactory.getCurrentSession().delete(reportList);
	}

}
