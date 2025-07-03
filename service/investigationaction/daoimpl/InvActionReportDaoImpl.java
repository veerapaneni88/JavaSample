package us.tx.state.dfps.service.investigationaction.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.riskandsafetyassmt.dto.InvstActionQuestionDto;
import us.tx.state.dfps.service.investigationaction.dao.InvActionReportDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:InvActionReportDaoImpl will implemented all operation defined in
 * InvActionReportDao Interface related InvActionReport module.. Apr 30, 2018-
 * 2:02:51 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class InvActionReportDaoImpl implements InvActionReportDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${InvActionReportDaoImpl.getInvstActionQuestions}")
	private String getInvstActionQuestionsSql;

	/**
	 * 
	 * Method Name: getInvstActionQuestions DAM Name : CINV04D Method
	 * Description: This dam retrieves seven rows from the invst_action_
	 ** question table based upon an id_event.
	 * 
	 * @param idEvent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<InvstActionQuestionDto> getInvstActionQuestions(Long idEvent) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getInvstActionQuestionsSql)
				.addScalar("idInvstActionQuest", StandardBasicTypes.INTEGER)
				.addScalar("cdInvstActionAns", StandardBasicTypes.STRING)
				.addScalar("cdInvstActionQuest", StandardBasicTypes.STRING)
				.addScalar("invstActionCmnts", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(InvstActionQuestionDto.class));
		return query.list();
	}

}
