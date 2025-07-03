
package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.PCSPPlcmntDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PCSPPlcmntDaoImpl Sep 21, 2017- 4:19:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PCSPPlcmntDaoImpl implements PCSPPlcmntDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PCSPPlcmntDaoImpl.hasPlcmntForStage}")
	private String hasPlcmntForStageSql;

	@Value("${PCSPPlcmntDaoImpl.isPlcmntWithCrgvrAtCls}")
	private String isPlcmntWithCrgvrAtClsSql;

	@Value("${PCSPPlcmntDaoImpl.hasPlcmntForCase}")
	private String hasPlcmntForCaseSql;

	@Value("${PCSPPlcmntDaoImpl.getPlacementIdForEventSql}")
	private String getPlacementIdForEventSql;

	/**
	 * Method Name: hasPlcmntForStage Method Description:This method checks if
	 * there any pcsp placements exists for the stage
	 * 
	 * @param idStage
	 * @return boolean
	 * @throws DataNotFoundException
	 */
	@Override
	public boolean hasPlcmntForStage(Long idStage) throws DataNotFoundException {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasPlcmntForStageSql);

		query.setParameter(ServiceConstants.IDSTAGE, idStage);
		Character result = (Character) query.uniqueResult();

		if (result.equals(ServiceConstants.CHAR_Y)) {
			return true;
		}

		return false;
	}

	/**
	 * Method Name: isPlcmntWithCrgvrAtCls Method Description:This method checks
	 * if there any pcsp placements with closed stage as the input stage having
	 * reason Child Remains in PCSP at Case Closure-No Legal Custody(060)
	 * 
	 * @param idStage
	 * @return boolean
	 * @throws DataNotFoundException
	 */
	@Override
	public boolean isPlcmntWithCrgvrAtCls(Long idStage) throws DataNotFoundException {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isPlcmntWithCrgvrAtClsSql);

		query.setParameter(ServiceConstants.idStage, idStage);

		Character eventStatus = (Character) query.uniqueResult();
		if (eventStatus.equals(ServiceConstants.CHAR_Y)) {
			return true;
		}

		return false;
	}

	/**
	 * Method Name: hasPlacementForCase Method Description: Method added for
	 * Phase II for checking open placement for Case when Investigation is
	 * submitted with household.
	 * 
	 * @param idCase
	 * @return boolean
	 */
	@Override
	public boolean hasPlacementForCase(Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasPlcmntForCaseSql);
		query.setParameter(ServiceConstants.IDCASE, idCase);
		Character result = (Character) query.uniqueResult();
		if (result.equals(ServiceConstants.CHAR_Y)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param eventId
	 * @return
	 */
	@Override
	public String getPlacementIdForEvent(Long eventId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementIdForEventSql);
		query.setParameter("idEvent", eventId);
        String plcmntId = query.uniqueResult().toString();
		return plcmntId;
	}

}
