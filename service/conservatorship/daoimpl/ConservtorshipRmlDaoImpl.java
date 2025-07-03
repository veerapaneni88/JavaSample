package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.PersonEligibility;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dao.ConservtorshipRmlDao;
import us.tx.state.dfps.service.cvs.dto.ConservtorshipRmlDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dao
 * implementation for ConservtorshipRmlDaoImpl Sep 8, 2017- 12:23:38 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ConservtorshipRmlDaoImpl implements ConservtorshipRmlDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ConservtorshipRmlDaoImpl.rlnqushQuestionAnsweredY}")
	private String rlnqushQuestionAnsweredYsql;

	@Value("${ConservtorshipRmlDaoImpl.prsnCharSelected}")
	private String prsnCharSelectedsql;

	@Value("${ConservtorshipRmlDaoImpl.getCnsrvtrRmvlPersonId}")
	private String getCnsrvtrRmvlPersonIdsql;

	private static final Logger log = Logger.getLogger(ConservtorshipRmlDaoImpl.class);

	/**
	 * Method Name: updateDenyDate Method Description:
	 * 
	 * @param idPerson
	 * @return long @
	 */
	public long updateDenyDate(long idPerson) {
		long result = 0;

		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PersonEligibility.class);

		criteria1.add(Restrictions.eq("idPersElig", idPerson));

		criteria1.add(Restrictions.eq("cdPersEligEligType", "CND"));

		criteria1.add(Restrictions.isNull("dtDeny"));

		List<PersonEligibility> personEligibilityList = criteria1.list();

		for (PersonEligibility personEligibility : personEligibilityList) {
			personEligibility.setDtDeny(new Date());
			personEligibility.setCdPersEligPrgClosed(ServiceConstants.CPS);
			sessionFactory.getCurrentSession().saveOrUpdate(personEligibility);
			result++;
		}
		return result;

	}

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return long @
	 */
	public long rlnqushQuestionAnsweredY(long idStage, long idVictim) {
		log.debug("Entering method  rlnqushQuestionAnsweredY in ConservtorshipRmlDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(rlnqushQuestionAnsweredYsql));
		sQLQuery1.setParameter("ulIdStage", idStage);
		sQLQuery1.setParameter("ulIdVictim", idVictim);

		BigDecimal idPersonBig = (BigDecimal) sQLQuery1.uniqueResult();
		long idPerson = (null == idPersonBig ? 0L : idPersonBig.longValue());
		return idPerson;
	}

	/**
	 * Method Name: prsnCharSelected Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return long @
	 */
	public long prsnCharSelected(long idStage, long idVictim) {
		log.debug("Entering the method  prsnCharSelected in ConservtorshipRmlDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(prsnCharSelectedsql));
		sQLQuery1.setLong("ulIdVictim", idVictim);

		BigDecimal idPersonBig = (BigDecimal) sQLQuery1.uniqueResult();
		long idPerson = (null == idPersonBig ? 0L : idPersonBig.longValue());
		return idPerson;
	}

	/**
	 * Method Name: getCnsrvtrRmvlPersonId Method Description:
	 * 
	 * @param idCase
	 * @param idRemovalEvent
	 * @return List<ConservtorshipRmlDto> @
	 */
	public List<ConservtorshipRmlDto> getCnsrvtrRmvlPersonId(long idCase, long idRemovalEvent) {

		List<ConservtorshipRmlDto> conservtorshipRmlDtoList;
		log.debug("Entering this method getCnsrvtrRmvlPersonId in ConservtorshipRmlDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCnsrvtrRmvlPersonIdsql)
				.setResultTransformer(Transformers.aliasToBean(ConservtorshipRmlDto.class)));

		sQLQuery1.addScalar("personId", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dateOfDeath", StandardBasicTypes.DATE);

		sQLQuery1.setParameter("ulIdRemovalEvent", idRemovalEvent);
		sQLQuery1.setParameter("ulIdCase", idCase);

		conservtorshipRmlDtoList = (List<ConservtorshipRmlDto>) sQLQuery1.list();
		return conservtorshipRmlDtoList;
	}

}