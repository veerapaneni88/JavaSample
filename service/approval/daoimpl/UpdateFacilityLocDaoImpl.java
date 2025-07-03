package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.UpdateFacilityLocReq;
import us.tx.state.dfps.service.approval.dao.UpdateFacilityLocDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 20,
 * 2018- 12:17:55 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class UpdateFacilityLocDaoImpl implements UpdateFacilityLocDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${UpdateFacilityLocDaoImpl.updateFacilityLoc}")
	private String updateFacilityLocsql;

	public static final Logger log = Logger.getLogger(UpdateFacilityLocDaoImpl.class);

	/**
	 * Service Name: updateFacilityLoc Service Description: This service Used to
	 * end date a facility loc row. Dam Name: CAUDB4D Service Name: CCMN35S
	 * 
	 * @param @
	 */
	@SuppressWarnings("unused")
	@Override
	public void updateFacilityLoc(UpdateFacilityLocReq updateFacilityLocReq) {
		log.debug("Entering method updateFacilityLoc in UpdateFacilityLocDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateFacilityLocsql)
				.setLong("idResource", updateFacilityLocReq.getIdResource())
				.setDate("dtFlocEnd", updateFacilityLocReq.getDtFlocEnd()));
		sqlQuery.executeUpdate();

		log.debug("Exiting method updateFacilityLoc in UpdateFacilityLocDaoImpl");
	}
}
