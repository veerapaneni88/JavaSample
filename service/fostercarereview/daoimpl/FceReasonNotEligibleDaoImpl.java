/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 21, 2017- 3:40:00 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.daoimpl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceReasonNotEligible;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.fostercarereview.dao.FceReasonNotEligibleDao;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 21, 2017- 3:40:00 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Repository
public class FceReasonNotEligibleDaoImpl implements FceReasonNotEligibleDao {

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.dao.FceReasonNotEligibleDao#
	 * findReasonsNotEligible(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FceReasonNotEligibleDto> findReasonsNotEligible(Long idFceEligibility) {

		List<FceReasonNotEligibleDto> FceReasonNotEligibles = (List<FceReasonNotEligibleDto>) sessionFactory
				.getCurrentSession().createCriteria(FceReasonNotEligible.class, "fceReasonNotEligible")
				.addOrder(Order.desc("cdReasonNotEligible"))
				.setProjection(Projections.projectionList()
						.add(Projections.property("cdReasonNotEligible"), "cdReasonNotEligible")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("fceEligibility.idFceEligibility"), "idFceEligibility")
						.add(Projections.property("idFceReasonNotEligible"), "idFceReasonNotEligible"))
				.createAlias("fceReasonNotEligible.fceEligibility", "fceEligibility")
				.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility))
				.setResultTransformer(Transformers.aliasToBean(FceReasonNotEligibleDto.class)).list();

		return FceReasonNotEligibles;
	}

}
