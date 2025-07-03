package us.tx.state.dfps.service.contacts.daoimpl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.StagePersonDao;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonRoleResultDto;

@Repository
public class StagePersonDaoImpl implements StagePersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cseca9dDaoImpl.getIdPersonForDifferentRole}")
	private String getIdPersonForDifferentRoleSql;

	@Value("${StagePersonDaoImpl.getCount}")
	private String getCountSql;

	@Value("${StagePersonDaoImpl.getPrtActiveActionPlan}")
	private String getPrtActiveActionPlanSql;

	@Value("${StagePersonDaoImpl.getPrtActiveActionPlanInProc}")
	private String getPrtActiveActionPlanInProcSql;

	/**
	 * 
	 * Method Name: getIdPersonForDifferentRole Method
	 * Description:PersonRoleResultDto
	 * 
	 * @param personRoleDto
	 * @return PersonRoleResultDto @
	 */
	@Override
	public PersonRoleResultDto getIdPersonForDifferentRole(PersonRoleDto personRoleDto) {

		PersonRoleResultDto personRoleResultDto = new PersonRoleResultDto();
		SQLQuery getIdPersonForDifferentRoleSQL = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIdPersonForDifferentRoleSql).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idCase", personRoleDto.getIdCase())
				.setParameter("cdStage", personRoleDto.getCdStage())
				.setParameter("cdStagePersRole", personRoleDto.getCdStagePersRole())
				.setParameter("idStage", personRoleDto.getIdStage())
				.setParameter("persRole", personRoleDto.getCdStagePersRole())
				.setResultTransformer(Transformers.aliasToBean(PersonRoleResultDto.class)));

		List<PersonRoleResultDto> personRoleResultDtoList = (List<PersonRoleResultDto>) getIdPersonForDifferentRoleSQL
				.list();
		if (!CollectionUtils.isEmpty(personRoleResultDtoList)) {
			personRoleResultDto.setIdStage((long) ServiceConstants.ID_STAGE);
			personRoleResultDto.setIdPerson((long) ServiceConstants.ID_PERSON);
		}

		return personRoleResultDto;
	}

	/**
	 * Method Name: getPrimaryClientIdForStage Method Description:This method
	 * returns the primary child in the particular stage.
	 * 
	 * @param idStage
	 *            -current stage id
	 * @return Long - person id of the child
	 */
	@Override
	public Long getPrimaryClientIdForStage(Long idStage) {
		Long idPerson = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CROLES_PC));

		List<StagePersonLink> stagePersonLinkList = criteria.list();
		if (!CollectionUtils.isEmpty(stagePersonLinkList)) {
			idPerson = stagePersonLinkList.get(ServiceConstants.Zero).getIdPerson();
		}
		return idPerson;
	}

	/**
	 * Method Name: isActiveReferral Method Description: This method Returns
	 * true/false if there is an active child referral for stage id
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public boolean isActiveReferral(Long idStage) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountSql)
				.setParameter(ServiceConstants.IDSTAGE, idStage));
		if (null != query.uniqueResult() && ((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * 
	 * Method Name: getPrtActiveActionPlan Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in open status.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public boolean getPrtActiveActionPlan(Long idPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrtActiveActionPlanSql)
				.setParameter("idPerson", idPerson));
		if (null != query.uniqueResult() && ((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: getPrtActionPlanInProcStatus Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in Proc status.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public boolean getPrtActionPlanInProcStatus(Long idPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrtActiveActionPlanInProcSql)
				.setParameter("idPerson", idPerson));
		if (((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}

}
