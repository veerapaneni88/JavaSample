package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.CaseFileManagement;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S
 * Class Description: This Method extends BaseDao and implements
 * CaseFileManagementDao. This is used to retrieve case file management details
 * from database. Mar 25, 2017 - 6:43:06 PM
 */

@Repository
public class CaseFileManagementDaoImpl implements CaseFileManagementDao {
	@Value("${CaseFileManagementDaoImpl.getCFMgmtList}")
	private String getCFMgmtList;

	@Value("${CaseFileManagementDaoImpl.getSkpTrnInfo}")
	private String getSkpTrnInfo;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public CaseFileManagementDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve a full row from the Case
	 * File Management DAM Name:CSES57D Service: CCFC21S
	 * 
	 * @param ulIdCase
	 * @return CaseFileManagementDto @
	 */
	@Override
	public CaseFileManagementDto getCaseFileDetails(Long ulIdCase) {

		CaseFileManagementDto caseFileDtls = new CaseFileManagementDto();

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CaseFileManagement.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("idCaseFileCase"), "idCaseFileCase")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("addrCaseFileCity"), "addrCaseFileCity")
								.add(Projections.property("addrCaseFileStLn1"), "addrCaseFileStLn1")
								.add(Projections.property("addrCaseFileStLn2"), "addrCaseFileStLn2")
								.add(Projections.property("office.idOffice"), "idOffice")
								.add(Projections.property("unit.idUnit"), "idUnit")
								.add(Projections.property("cdCaseFileOfficeType"), "cdCaseFileOfficeType")
								.add(Projections.property("dtCaseFileArchCompl"), "dtCaseFileArchCompl")
								.add(Projections.property("dtCaseFileArchElig"), "dtCaseFileArchElig")
								.add(Projections.property("nmCaseFileOffice"), "nmCaseFileOffice")
								.add(Projections.property("txtCaseFileLocateInfo"), "caseFileLocateInfo")
								.add(Projections.property("txtCvsAdop"), "cvsAdop")
								.add(Projections.property("txtTrn"), "trn").add(Projections.property("txtSkp"), "skp")
								.add(Projections.property("txtAddSkpTrn"), "addSkpTrn"))
				.add(Restrictions.eq("idCaseFileCase", ulIdCase));

		caseFileDtls = (CaseFileManagementDto) cr
				.setResultTransformer(Transformers.aliasToBean(CaseFileManagementDto.class)).uniqueResult();

		return caseFileDtls;
	}

	@Override
	public long insertRecordsRetention(RecordsRetention retention) {

		return (long) sessionFactory.getCurrentSession().save(retention);
	}

	@Override
	public void updateRecordsRetention(RecordsRetention retention) {

		sessionFactory.getCurrentSession().update(retention);
		return;
	}

	@Override
	public void deleteRecordsRetention(RecordsRetention retention) {

		sessionFactory.getCurrentSession().delete(retention);
		return;
	}

	@Override
	public long insertCaseFileManagement(CaseFileManagement caseFileManagement) {
		return (long) sessionFactory.getCurrentSession().save(caseFileManagement);
	}

	@Override
	public void updateCaseFileManagement(CaseFileManagement caseFileManagement) {
		sessionFactory.getCurrentSession().update(caseFileManagement);
		return;

	}

	@Override
	public void deleteCaseFileManagement(CaseFileManagement caseFileManagement) {
		sessionFactory.getCurrentSession().delete(caseFileManagement);
		return;

	}

	/**
	 * Method Description: This method will retrieve idCaseFileCase and
	 * txtCaseFileLocateInfo from Case File Management table. Service
	 * Name:CFMgmntList
	 * 
	 * @param idCase
	 * @return List<CaseFileManagementDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<CaseFileManagementDto> getCFMgmntList(Long idCase) {
		List<CaseFileManagementDto> cfMgmtList = new ArrayList<>();
		cfMgmtList = (List<CaseFileManagementDto>) sessionFactory.getCurrentSession().createSQLQuery(getCFMgmtList)
				.addScalar("idCaseFileCase", StandardBasicTypes.LONG)
				.addScalar("caseFileLocateInfo", StandardBasicTypes.STRING).setParameter("caseMrgId", idCase)
				.setResultTransformer(Transformers.aliasToBean(CaseFileManagementDto.class)).list();
		return cfMgmtList;
	}

	/**
	 * Method Description: Query case file management and case merge table and
	 * get the case id and skp trn info for From cases merged to current case.
	 * Service Name:CFMgmntList
	 * 
	 * @param idCase
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	public List<CaseFileManagementDto> getSkpTrnInfo(Long idCase) {
		List<CaseFileManagementDto> cFMgmtListMap = null;

		cFMgmtListMap = (List<CaseFileManagementDto>) sessionFactory.getCurrentSession().createSQLQuery(getSkpTrnInfo)
				.addScalar("idCaseFileCase", StandardBasicTypes.LONG).addScalar("addSkpTrn", StandardBasicTypes.STRING)
				.setParameter("caseMergId", idCase)
				.setResultTransformer(Transformers.aliasToBean(CaseFileManagementDto.class)).list();

		return cFMgmtListMap;
	}

	public CaseFileManagement findCaseFileManagementById(Long idCaseFileCase) {
		CaseFileManagement caseFileManagement = null;
		caseFileManagement = (CaseFileManagement) sessionFactory.getCurrentSession()
				.get(CaseFileManagement.class, idCaseFileCase);
		return caseFileManagement;

	}
}