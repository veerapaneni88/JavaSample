package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.UserLdapLink;
import us.tx.state.dfps.service.admin.dao.EmpSecClassLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN05S Class
 * Description: Operations for EmpSecClassLink Apr 14, 2017 - 10:23:10 AM
 */
@Repository
public class EmpSecClassLinkDaoImpl implements EmpSecClassLinkDao {
	@Autowired
	MessageSource messageSource;

	@Value("${EmpSecClassLinkDaoImpl.getEmpSecClassLinkByPersonId}")
	private String getEmpSecClassLinkByPersonIdSql;

	@Value("${EmpSecClassLinkDaoImpl.deleteEmpSecClassLinkByPersonId}")
	private String deleteEmpSecClassLinkByPersonIdSql;

	@Value("${EmpSecClassLinkDaoImpl.getEmpSecClassJSecClassLinkByPersonId}")
	private String getEmpSecClassLinkJSecClassByPersonIdSql;

	@Value("${EmpSecClassLinkDaoImpl.deleteExternalUserSecClassLinkByPersonId}")
	private String deleteExternalUserSecClassLinkByPersonIdSql;

	@Autowired
	private SessionFactory sessionFactory;

	public EmpSecClassLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Description:getEmpSecClassLinkByPersonId
	 * 
	 * @param personId
	 * @return @ @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpSecClassLink> getEmpSecClassLinkByPersonId(Long personId) {
		List<EmpSecClassLink> empSecClassLinks = null;

		Query queryEmpSecClassLink = sessionFactory.getCurrentSession().createQuery(getEmpSecClassLinkByPersonIdSql);
		queryEmpSecClassLink.setParameter("idPerson", personId);
		queryEmpSecClassLink.setMaxResults(100);
		empSecClassLinks = queryEmpSecClassLink.list();

		if (TypeConvUtil.isNullOrEmpty(empSecClassLinks)) {
			throw new DataNotFoundException(
					messageSource.getMessage("empSecClassLink.not.found.personId", null, Locale.US));
		}

		return empSecClassLinks;
	}

	/**
	 * 
	 * Method Description:deleteEmpSecClassLink
	 * 
	 * @param escl
	 * @ @
	 */
	@Override
	public void deleteEmpSecClassLink(EmpSecClassLink escl) {
		sessionFactory.getCurrentSession().delete(escl);

	}

	/**
	 * 
	 * @param personId
	 * @ @
	 */
	@Override
	public void deleteEmpSecClassLinkByPersonId(Long personId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteEmpSecClassLinkByPersonIdSql)
				.setParameter("idPerson", personId);
		query.executeUpdate();

	}

	/**
	 * Method Name: getEmployeeSecurityProfile Method
	 * Description:getEmpSecClassLinkByPersonId - Get the Employee Security
	 * Class based on Id.
	 * 
	 * @param personId
	 * @return empSecClassLinkList @ @
	 */
	// CLSCB4D
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpSecClassLink> getEmployeeSecurityProfile(Long personId) {
		List<EmpSecClassLink> empSecClassLinkList = null;

		Query queryEmpSecClassLink = sessionFactory.getCurrentSession().createQuery(getEmpSecClassLinkByPersonIdSql);
		queryEmpSecClassLink.setParameter("idPerson", personId);
		queryEmpSecClassLink.setMaxResults(100);
		empSecClassLinkList = queryEmpSecClassLink.list();

		if (TypeConvUtil.isNullOrEmpty(empSecClassLinkList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("empSecClassLink.not.found.personId", null, Locale.US));
		}

		return empSecClassLinkList;
	}

	/**
	 * Method Name: deleteExternalUserSecClassLinkByPersonId Method
	 * Description:getEmpSecClassLinkByPersonId - delete external user security
	 * class based on userid.
	 * 
	 * @param personId
	 */
	@Override
	public void deleteExternalUserSecClassLinkByPersonId(Long personId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteExternalUserSecClassLinkByPersonIdSql)
				.setParameter("idPerson", personId);
		query.executeUpdate();
	}

	@Override
	public List<String> getEmployeeSecurityProfileByLogonId(String logonId) {
		List<EmpSecClassLink> empSecClassLinkList = null;
		Long idPerson = ServiceConstants.ZERO;
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserLdapLink.class);
		criteria.add(Restrictions.eq("txtIdLdapUser", logonId).ignoreCase());
		List<UserLdapLink> userLdapLink = (List<UserLdapLink>) criteria.list();
		
		if(!CollectionUtils.isEmpty(userLdapLink)) {
			idPerson = userLdapLink.get(0).getPerson().getIdPerson();
		}
		empSecClassLinkList = getEmployeeSecurityProfile(idPerson);

		List<String> empSecClassList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(empSecClassLinkList)) {
			empSecClassLinkList.stream().forEach(
					empSecClass -> empSecClassList.add(empSecClass.getSecurityClass().getCdSecurityClassName()));
		}
		
		return empSecClassList;
	}
}
