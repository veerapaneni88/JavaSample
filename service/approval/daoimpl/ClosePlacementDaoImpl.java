package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.ClosePlacementReq;
import us.tx.state.dfps.service.approval.dao.ClosePlacementDao;
import us.tx.state.dfps.service.common.util.DateUtils;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 11:50:32 AM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class ClosePlacementDaoImpl implements ClosePlacementDao {

	@Value("${ClosePlacementDaoImpl.closePlacement}")
	private String closePlacementsql;

	@Autowired
	private SessionFactory sessionFactory;

	public static final Logger log = Logger.getLogger(ApprovalStatusUpdateCapsResourceDaoImpl.class);

	/**
	 * Method Name: closePlacement Method Description : Close open DA/DD
	 * placement from PLACEMENT given ID_PLCMT_EVENT. Dam Name: CSUB87D Service
	 * Name: CCMN35S
	 * 
	 * @param ClosePlacementReq
	 * @return
	 */
	@Override
	public void closePlacement(ClosePlacementReq closePlacementReq) {
		log.debug("Entering method closePlacement in ClosePlacementDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(closePlacementsql)
				.setLong("idPlcmtEvent", closePlacementReq.getIdPlcmtEvent())
				.setDate("scrDtCurrentDate", DateUtils.getDateWithoutTime(closePlacementReq.getScrDtCurrentDate()))
				.setString("cdPlcmtRemovalRsn", closePlacementReq.getCdPlcmtRemovalRsn())
				.setLong("idLastUpdatePerson", closePlacementReq.getIdLastUpdatePerson()));

		sqlQuery.executeUpdate();

		log.debug("Exiting method closePlacement in ClosePlacementDaoImpl");
	}

}
