/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 21, 2017- 4:42:42 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.common.domain.OnlineParameters;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 21, 2017- 4:42:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FceReviewDaoImpl implements FceReviewDao {

	/**
	 * 
	 */
	private static final String FOSTER_GROUP_HOME_MSG = "FOSTER_GROUP_HOME_MSG";
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public FceReview getById(Long idfceReview) {
		FceReview fceReview = (FceReview) sessionFactory.getCurrentSession().get(FceReview.class, idfceReview);
		if (TypeConvUtil.isNullOrEmpty(fceReview)) {
			throw new DataNotFoundException("FceReview not found");
		}

		return fceReview;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao#
	 * enableFosterGoupMessage()
	 */
	@Override
	public CommonBooleanRes enableFosterGoupMessage() {
		CommonBooleanRes resp = new CommonBooleanRes();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnlineParameters.class)
				.add(Restrictions.eq("txtName", FOSTER_GROUP_HOME_MSG));
		OnlineParameters onlineParameters = (OnlineParameters) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(onlineParameters)) {
			String val = onlineParameters.getTxtValue();
			if (ServiceConstants.Y.equalsIgnoreCase(val) || ServiceConstants.INDICATOR_YES.equalsIgnoreCase(val)) {
				resp.setExists(Boolean.TRUE);
			}
		}

		return resp;
	}
}
