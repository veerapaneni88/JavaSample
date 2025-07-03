package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dto.SituationDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U Class
 * Description: Situation Dao Class Mar 24, 2017 - 7:15:39 PM
 */

@Repository
public class SituationDaoImpl implements SituationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public SituationDaoImpl() {

	}

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	@Override
	public void saveSituation(Situation situation) {
		sessionFactory.getCurrentSession().persist(situation);

	}

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	@Override
	public void updateSituation(Situation situation) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(situation));

	}

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	@Override
	public void deleteSituation(Situation situation) {
		sessionFactory.getCurrentSession().delete(situation);

	}

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param idSituation
	 * @return @
	 */
	@Override

	public Situation getSituationEntityById(Long idSituation) {

		Situation situation = (Situation) sessionFactory.getCurrentSession().load(Situation.class,
				Long.valueOf(idSituation));
		return situation;
	}

	/**
	 * 
	 * Method Name: insertIntoSituation Method Description:This method inserts
	 * record into SITUATION table.
	 * 
	 * @param situationDto
	 * @return Long @
	 */
	public Long insertIntoSituation(SituationDto situationDto) {

		Situation situation = new Situation();
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(situationDto.getIdCase());
		situation.setCapsCase(capsCase);
		situation.setDtSituationClosed(situationDto.getDtSituationClosed());
		situation.setNbrSitOccurrence(situationDto.getSitOccurrence());
		situation.setCdSitFunctionalArea(situationDto.getCdSitFunctionalArea());
		situation.setCdSitCurrStatus(situationDto.getCdSitCurrStatus());
		situation.setDtSituationOpened(new Date());
		situation.setDtLastUpdate(new Date());

		Long idSituation = (Long) sessionFactory.getCurrentSession().save(situation);
		if (TypeConvUtil.isNullOrEmpty(idSituation)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));

		}

		return idSituation;
	}

	/**
	 * Method Name: closeSituation Method Description: This method closes the
	 * Situation Record
	 * 
	 * @param idSituation
	 * @return Long
	 */
	@Override
	public Long closeSituation(Long idSituation) {

		Long rowCount = 0L;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Situation.class);

		criteria.add(Restrictions.eq("idSituation", idSituation));
		Situation situation = (Situation) criteria.uniqueResult();
		situation.setDtSituationClosed(new Date());
		if (!TypeConvUtil.isNullOrEmpty(situation)) {
			sessionFactory.getCurrentSession().saveOrUpdate(situation);
			rowCount++;
		}

		return rowCount;
	}
}
