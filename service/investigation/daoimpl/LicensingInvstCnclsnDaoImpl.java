package us.tx.state.dfps.service.investigation.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstCnclsnDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is used to get informaion from database> Mar 27, 2018- 3:05:39 PM © 2017
 * Texas Department of Family and Protective Services.
 */
@Repository
public class LicensingInvstCnclsnDaoImpl implements LicensingInvstCnclsnDao {

	@Value("${LicensingInvstCnclsnDao.hasContactTypeSql}")
	private String hasContactTypeSql;

	@Value("${LicensingInvstCnclsnDao.isChildSexLaborTrafficking}")
	private String isChildSexLaborTraffickingSql;

	@Value("${LicensingInvstCnclsnDao.isChildSexLaborTraffickingFalse}")
	private String isChildSexLaborTraffickingFalseSql;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Instantiates a new licensing invst dtl dao impl.
	 */
	public LicensingInvstCnclsnDaoImpl() {

	}

	@Override
	public Boolean hasContactTypeToPerson(Long idStage, String cdContactType, Long idPerson) {
		Boolean hasContactType = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasContactTypeSql)
				.addScalar("REC_EXISTS", StandardBasicTypes.INTEGER).setParameter("idStage", idStage)
				.setParameter("cdContactType", cdContactType).setParameter("idPerson", idPerson);
		if (null != query.uniqueResult() && Integer.parseInt(query.uniqueResult().toString()) > 0) {
			hasContactType = Boolean.TRUE;
		}
		return hasContactType;
	}

	/**
	 * Method Name: isChildSexLaborTrafficking Method Description:This method
	 * returns TRUE if the case has answered Child Sex/Labor Trafficking
	 * question in the current stage or there is Allegation of Child Sex/Labor
	 * Trafficking
	 *
	 * @param idStage
	 * @return Boolean
	 *
	 */
	public Boolean isChildSexLaborTrafficking(Long idStage) {
		Boolean isChildSexLaborTraffic = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isChildSexLaborTraffickingSql);

		query.setParameter(ServiceConstants.IDSTAGE, idStage);

		Character eventStatusStr = (Character) query.uniqueResult();
		if (ServiceConstants.CHAR_IND_Y == eventStatusStr) {
			isChildSexLaborTraffic = true;
		}
		if (!isChildSexLaborTraffic) {
			Query queryIf = sessionFactory.getCurrentSession().createSQLQuery(isChildSexLaborTraffickingFalseSql);
			queryIf.setParameter("idInvstStage", idStage);
			eventStatusStr = (Character) queryIf.uniqueResult();
			if (ServiceConstants.CHAR_IND_Y == eventStatusStr) {
				isChildSexLaborTraffic = true;
			}
		}
		return isChildSexLaborTraffic;
	}

}
