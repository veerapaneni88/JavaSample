package us.tx.state.dfps.service.intake.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.service.intake.dao.IncmgDtrmFactorsDao;

@Repository
public class IncmgDtrmFactorsDaoImpl implements IncmgDtrmFactorsDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public IncmgDtrmFactorsDaoImpl() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IncmgDetermFactors> getincmgDetermFactorsById(Long idStage) {
		List<IncmgDetermFactors> incmgDetermFactors = new ArrayList<>();
		incmgDetermFactors = (List<IncmgDetermFactors>) sessionFactory.getCurrentSession()
				.createCriteria(IncmgDetermFactors.class).add(Restrictions.eq("idIncmgDetermStage", idStage)).list();

		return incmgDetermFactors;
	}

}
