package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dao.CaseWorkloadDao;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN20S Class
 * Description: Operations for Case Apr 14, 2017 - 12:17:44 PM
 */
@Repository
public class CaseWorkloadDaoImpl implements CaseWorkloadDao {

	@Autowired
	MessageSource messageSource;

	@Value("${CaseDaoImpl.searchCaseLinksByPersonId}")
	private String searchCaseLinksByPersonIdSql;

	@Value("${CaseDaoImpl.searchCaseByNameAndPersonId}")
	private String searchCaseByNameAndPersonIdSql;

	@Value("${CaseDaoImpl.updateCasesByUnitId}")
	private String updateCasesByUnitIdSql;

	@Autowired
	private SessionFactory sessionFactory;

	public CaseWorkloadDaoImpl() {

	}

	/**
	 * 
	 * Method Description:searchCaseLinksByPersonId
	 * 
	 * @param personId
	 * @return @
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CaseLink> searchCaseLinksByPersonId(Long personId) {
		List<CaseLink> caseLinks = null;

		Query queryCaseLink = sessionFactory.getCurrentSession().createQuery(searchCaseLinksByPersonIdSql);
		queryCaseLink.setParameter("idSearch", personId);
		queryCaseLink.setMaxResults(100);
		caseLinks = queryCaseLink.list();

		if (TypeConvUtil.isNullOrEmpty(caseLinks)) {
			throw new DataNotFoundException(messageSource.getMessage("case.not.found.attributes", null, Locale.US));
		}

		return caseLinks;
	}

	/**
	 * 
	 * Method Description:searchCaseByNameAndPersonId
	 * 
	 * @param personFull
	 * @param ulIdPerson
	 * @return
	 * @ @ @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CapsCase> searchCaseByNameAndPersonId(String personFull, Long ulIdPerson) {
		List<CapsCase> capsCases = null;
		List<String> roles = new ArrayList<String>();

		Query queryCaseLink = sessionFactory.getCurrentSession().createQuery(searchCaseByNameAndPersonIdSql);
		queryCaseLink.setParameter("personFull", personFull);
		queryCaseLink.setParameter("maxDate", ServiceConstants.GENERIC_END_DATE);
		queryCaseLink.setParameter("program1", ServiceConstants.STAGE_PROGRAM_APS);
		queryCaseLink.setParameter("program2", ServiceConstants.STAGE_PROGRAM_AFC);
		queryCaseLink.setParameter("idPerson", ulIdPerson);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_VC);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_VP);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_DB);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_DV);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_CL);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_NO);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_UK);
		queryCaseLink.setParameterList("roles", roles);
		queryCaseLink.setMaxResults(100);
		capsCases = queryCaseLink.list();

		if (TypeConvUtil.isNullOrEmpty(capsCases)) {
			throw new DataNotFoundException(messageSource.getMessage("case.not.found.attributes", null, Locale.US));
		}

		return capsCases;
	}

	/**
	 * 
	 * Method Description:updateCapsCase
	 * 
	 * @param capsCase
	 * @ @
	 */
	@Override
	public void updateCapsCase(CapsCase capsCase) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(capsCase));

	}

	/**
	 * 
	 * Method Description:searchCasesByUnitId
	 * 
	 * @param unitId
	 * @return @ @
	 */
	@Override
	public void updateCasesByUnitId(Long unitId) {
		String value = null;

		Query queryCaseLink = sessionFactory.getCurrentSession().createQuery(updateCasesByUnitIdSql);
		queryCaseLink.setParameter("unitId", unitId);
		queryCaseLink.setParameter("value", value);
		queryCaseLink.executeUpdate();

	}
}
