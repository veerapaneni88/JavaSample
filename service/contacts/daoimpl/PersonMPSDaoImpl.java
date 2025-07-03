/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 7, 2018- 4:54:55 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.service.contacts.dao.PersonMPSDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 7, 2018- 4:54:55 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PersonMPSDaoImpl implements PersonMPSDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonMPSDaoImpl.getNbrMPSPersStage}")
	private String getNbrMPSPersStageSql;

	/**
	 * Method Name: getNbrMPSPersStage Method Description: This method number of
	 * the MPS person related with the given stage
	 * 
	 * @param idStage
	 * @param stagePerRel
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean getNbrMPSPersStage(Long idStage, String stagePersRelType) {
		boolean isPersStageReltd = false;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingPersonMps.class);
		criteria.add(Restrictions.and(Restrictions.eq("idStage", idStage),
				Restrictions.eq("indStagePersRelated", stagePersRelType)));
		List<?> criteriaList = criteria.list();
		if (CollectionUtils.isNotEmpty(criteriaList)) {
			isPersStageReltd = true;
		}
		return isPersStageReltd;
	}

}
