package us.tx.state.dfps.service.maintaindesignee.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.MaintainDesigneeDto;
import us.tx.state.dfps.service.maintaindesignee.dao.MaintainDesigneeDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtDaoImpl Sep 22, 2017- 9:30:45 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class MaintainDesigneeDaoImpl implements MaintainDesigneeDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${MaintainDesigneeDaoImpl.getDesigneeDtls}")
	private String getDesigneeDtlsSql;

	@Autowired
	MaintainDesigneeDao maintainDesigneeDao;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Description: Fetch the Designee Details
	 * 
	 * @param idPerson
	 * @return List<MaintainDesigneeDto>
	 * 
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintainDesigneeDto> getDesigneeDtls(Long idPerson) {

		Query getDesigneeDtlsQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDesigneeDtlsSql)
				.addScalar("idEmpTempAssign", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPersonEmp", StandardBasicTypes.LONG)
				.addScalar("idPersonDesignee", StandardBasicTypes.LONG)
				.addScalar("dtAssignExpiration", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonPccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(MaintainDesigneeDto.class)));
		List<MaintainDesigneeDto> maintainDesigneeDtoList = (List<MaintainDesigneeDto>) getDesigneeDtlsQuery.list();

		return maintainDesigneeDtoList;
	}

}