package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.EmpTempAssign;
import us.tx.state.dfps.service.admin.dao.EmpTempAssignDao;
import us.tx.state.dfps.service.admin.dto.EmpTempAssignDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN05S Class
 * Description:CCMNH2D, Operations for EmpTempAssign Apr 14, 2017 - 10:25:17 AM
 */
@Repository
public class EmpTempAssignDaoImpl implements EmpTempAssignDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EmpTempAssignDaoImpl.getEmpTemAssignByPersonId}")
	private String getEmpTemAssignByPersonIdSql;

	@Value("${EmpTempAssignDaoImpl.deleteEmpTempAssignByPersonId}")
	private String deleteEmpTempAssignByPersonIdSql;

	@Value("${EmpTempAssignDaoImpl.getActiveEmpTemAssignByPersonId}")
	private String getActiveEmpTemAssignByPersonIdSql;

	@Value("${EmpTempAssignDaoImpl.getCountEmpTempAssignByPersonId}")
	private String getCountEmpTempAssignByPersonIdSql;

	private static final Logger log = Logger.getLogger(EmpTempAssignDaoImpl.class);

	public EmpTempAssignDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Description:getEmpTemAssignByPersonId
	 * 
	 * @param personId
	 * @return @ @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpTempAssign> getEmpTemAssignByPersonId(Long personId) {
		List<EmpTempAssign> tempAssigns = null;

		Query queryTempAssign = sessionFactory.getCurrentSession().createQuery(getEmpTemAssignByPersonIdSql);
		queryTempAssign.setParameter("idPerson", personId);
		queryTempAssign.setMaxResults(100);
		tempAssigns = queryTempAssign.list();

		if (TypeConvUtil.isNullOrEmpty(tempAssigns)) {
			throw new DataNotFoundException(
					messageSource.getMessage("EmpTempAssign.not.found.personId", null, Locale.US));
		}

		return tempAssigns;
	}

	/**
	 * 
	 * Method Description:deleteEmpTempAssign
	 * 
	 * @param eta
	 * @ @
	 */

	@Override
	public void deleteEmpTempAssign(EmpTempAssign eta) {
		sessionFactory.getCurrentSession().delete(eta);

	}

	/**
	 * 
	 * @param personId
	 * @ @
	 */

	@Override
	public void deleteEmpTempAssignByPersonId(Long personId) {
		Query query = sessionFactory.getCurrentSession().createQuery(deleteEmpTempAssignByPersonIdSql);
		query.setParameter("idSearch", personId);
		query.executeUpdate();

	}

	/**
	 * Method Name: getActiveEmpTempAssignByPersonId Method Description: This
	 * method gets temporary assignments for the employee - CMSC45D
	 * 
	 * @param personId
	 * @return empTempAssignList @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpTempAssign> getActiveEmpTempAssignByPersonId(Long personId) {
		List<EmpTempAssign> empTempAssignList;

		Query query = sessionFactory.getCurrentSession().createQuery(getActiveEmpTemAssignByPersonIdSql);
		query.setLong("idPerson", personId);
		query.setDate("currentDate", new java.util.Date());
		empTempAssignList = query.list();

		return empTempAssignList;
	}

	@Override
	public Long getCountEmpTempAssignByPersonId(Long idPersonDesignee, Long idPersonEmp) {

		Query query = sessionFactory.getCurrentSession().createQuery(getCountEmpTempAssignByPersonIdSql);
		query.setParameter("idPersonEmp", idPersonEmp);
		query.setParameter("idPersonDesignee", idPersonDesignee);
		return (Long) query.uniqueResult();
	}

	/**
	 * 
	 * Method Name: updateLegalStatus Method Description: CAUD05D - update Legal
	 * Status
	 * 
	 * @param pInputDataRec
	 */
	@Override
	public void updateEmpTempAssign(EmpTempAssignDto empTempAssignDto, String reqFuncCd) {
		log.info("Entering method updateEmpTempAssign in EmpTempAssignDaoImpl");
		switch (reqFuncCd) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			EmpTempAssign empTempAssign = new EmpTempAssign();
			empTempAssign.setDtAssignExpiration(empTempAssignDto.getDtAssignExpiration());
			empTempAssign.setDtLastUpdate(new Date());
			empTempAssign.setIdPersonDesignee(empTempAssignDto.getIdPersonDesignee());
			empTempAssign.setIdPersonEmp(empTempAssignDto.getIdPersonEmp());
			sessionFactory.getCurrentSession().save(empTempAssign);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			EmpTempAssign empTempAssignForUpdate = (EmpTempAssign) sessionFactory.getCurrentSession()
					.createCriteria(EmpTempAssign.class)
					.add(Restrictions.eq("idEmpTempAssign", empTempAssignDto.getIdEmpTempAssign())).uniqueResult();
			if (!ObjectUtils.isEmpty(empTempAssignForUpdate)) {
				empTempAssignForUpdate.setDtAssignExpiration(empTempAssignDto.getDtAssignExpiration());
				empTempAssignForUpdate.setDtLastUpdate(new Date());
				empTempAssignForUpdate.setIdPersonDesignee(empTempAssignDto.getIdPersonDesignee());
				empTempAssignForUpdate.setIdPersonEmp(empTempAssignDto.getIdPersonEmp());
				sessionFactory.getCurrentSession().saveOrUpdate(empTempAssignForUpdate);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			EmpTempAssign empTempAssignForDelete = (EmpTempAssign) sessionFactory.getCurrentSession()
					.createCriteria(EmpTempAssign.class)
					.add(Restrictions.eq("idEmpTempAssign", empTempAssignDto.getIdEmpTempAssign())).uniqueResult();
			if (!ObjectUtils.isEmpty(empTempAssignForDelete)) {
				sessionFactory.getCurrentSession().delete(empTempAssignForDelete);
			}

			break;
		default:
			break;
		}
		log.info("Exiting method updateEmpTempAssign in EmpTempAssignDaoImpl");
	}

}
