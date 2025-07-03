package us.tx.state.dfps.service.pcgoal.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.pcgoal.dao.PCGoalDao;
import us.tx.state.dfps.service.pcgoal.dto.PglDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:dao
 * implementation class for CCMN56S to populate form CCMN0100 Mar 5, 2018-
 * 12:14:44 PM © 2017 Texas Department of Family and Protective Services
 */

@Repository
public class PCGoalDaoImpl implements PCGoalDao {

	public PCGoalDaoImpl() {

	}

	private static final Logger log = Logger.getLogger("PCGoalDaoImpl.class");

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PCGoalDaoImpl.getPglDetailsDto}")
	private transient String getPglDetailsDtoSql;

	/**
	 * Method Name: getPglDetailsDto Method Description: Retrieves
	 * PERM_GOAL_LOOKUP static table data
	 * 
	 * @return List<PglDetailDto> @
	 */
	@Override
	public List<PglDetailDto> getPglDetailsDto() {

		List<PglDetailDto> pglDetailDtolist = new ArrayList<PglDetailDto>();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPglDetailsDtoSql)
				.addScalar("cdPermGoal", StandardBasicTypes.STRING).addScalar("txtDecode", StandardBasicTypes.STRING)
				.addScalar("txtDefinition", StandardBasicTypes.STRING).addScalar("nbrOrder", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PglDetailDto.class));

		pglDetailDtolist = (List<PglDetailDto>) query.list();

		return pglDetailDtolist;
	}

}
