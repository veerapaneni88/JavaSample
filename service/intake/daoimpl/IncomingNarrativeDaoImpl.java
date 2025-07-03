package us.tx.state.dfps.service.intake.daoimpl;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomingNarrative;
import us.tx.state.dfps.service.common.response.IntNarrBlobRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.intake.dao.IncomingNarrativeDao;

@Repository
public class IncomingNarrativeDaoImpl implements IncomingNarrativeDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public IncomingNarrativeDaoImpl() {

	}

	@Override
	public IntNarrBlobRes getNarrativeById(Long idStage) {
		IntNarrBlobRes incomingNarrative = new IntNarrBlobRes();

		incomingNarrative = (IntNarrBlobRes) sessionFactory.getCurrentSession().createCriteria(IncomingNarrative.class)
				.setProjection(Projections.projectionList().add(Projections.property("narrIncoming"), "narrIncoming"))
				.add(Restrictions.eq("idStage", idStage))
				.setResultTransformer(Transformers.aliasToBean(IntNarrBlobRes.class)).uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(incomingNarrative)) {
			throw new DataNotFoundException("Nerrations not found");
		}

		return incomingNarrative;
	}

}
