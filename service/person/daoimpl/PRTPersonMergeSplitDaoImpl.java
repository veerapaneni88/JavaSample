package us.tx.state.dfps.service.person.daoimpl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.person.dao.PRTPersonMergeSplitDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PRTPersonMergeSplitDaoImpl Sep 22, 2017- 10:51:47 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PRTPersonMergeSplitDaoImpl implements PRTPersonMergeSplitDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PRTPersonMergeSplitDaoImpl.isActivePRT}")
	private String isActivePRTSql;

	@Value("${PRTPersonMergeSplitDaoImpl.updatePersonOnPrtActPlan}")
	private String updatePersonOnPrtActPlanSql;

	@Value("${PRTPersonMergeSplitDao.isOpenActionPlan}")
	private String isOpenActionPlanSql;

	@Value("${PRTPersonMergeSplitDao.getClosedActionPlan}")
	private String getClosedActionPlanSql;

	@Value("${PRTPersonMergeSplitDao.getPRTConnection}")
	private String getPRTConnectionSql;

	/**
	 * Method Name: isActivePRT Method Description:Gets all the PRT(Active) for
	 * the Person.
	 * 
	 * @param idFwdPerson
	 * @return boolean
	 */
	@Override
	public boolean isActivePRT(Long idPerson) {
		boolean isActivePRT = false;
		boolean isOpenActplnwFollowup = false;
		boolean isOpenActPln = isOpenActionPlan(idPerson);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isActivePRTSql);
		query.setParameter("idPerson", idPerson);
		BigDecimal countIsActivePRT = (BigDecimal) query.uniqueResult();
		if (countIsActivePRT.intValue() > 0) {
			isOpenActplnwFollowup = true;
		}
		if (isOpenActplnwFollowup || isOpenActPln) {
			isActivePRT = true;
		}
		return isActivePRT;
	}

	/**
	 * Method Name: updatePersonOnPrtActPlan Method Description:Update person Id
	 * on PRT Person Link This functions is being called during person merge to
	 * update forward person Id on a closed person for PRT.
	 * 
	 * @param idFwdPerson
	 * @param idPrtActPln
	 * @param idClosedPerson
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updatePersonOnPrtActPlan(int idFwdPerson, int idPrtActPln, int idClosedPerson) {

		sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnPrtActPlanSql)
				.setParameter("idFrwdPerson", idFwdPerson).setParameter("idPrtActPlan", idPrtActPln)
				.setParameter("idClosedPerson", idClosedPerson).executeUpdate();

	}

	/**
	 * Method Name: isOpenActionPlan Method Description:Gets all the PRT Action
	 * Plan(Active) for the Person.
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	@Override
	public boolean isOpenActionPlan(Long idPerson) {
		boolean isActiveActpln = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isOpenActionPlanSql);
		query.setParameter("idPerson", idPerson);
		BigDecimal countOpenActionPlan = (BigDecimal) query.uniqueResult();
		if (countOpenActionPlan.intValue() > 0) {
			isActiveActpln = true;
		}
		return isActiveActpln;
	}

	/**
	 * 
	 * Method Name: getPRTActPlnForPerson Method Description:Gets all the PRT
	 * Action Plan (closed) in all the OPEN stages for the Person.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public List<Long> getPRTActPlnForPerson(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getClosedActionPlanSql).setParameter("idPerson",
				idPerson);
		List<Long> prtActPlanList = (List<Long>) query.list();
		return prtActPlanList;
	}

	/**
	 * 
	 * Method Name: getPRTConnectionForPerson Method Description: Gets all the
	 * PRT Connection (Active and Inactive).
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public List<Long> getPRTConnectionForPerson(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPRTConnectionSql).setParameter("idPerson",
				idPerson);
		List<Long> prtConnectionList = (List<Long>) query.list();
		return prtConnectionList;

	}
}
