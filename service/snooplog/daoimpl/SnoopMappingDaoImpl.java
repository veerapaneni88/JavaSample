package us.tx.state.dfps.service.snooplog.daoimpl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Snooplog;
import us.tx.state.dfps.service.common.request.SnoopLogReq;
import us.tx.state.dfps.service.snooplog.dao.SnoopMappingDao;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Snoop
 * Log Mapping for Tuxedo Services
 * 
 *
 */

@Repository
public class SnoopMappingDaoImpl implements SnoopMappingDao {

	/**
	 * Method Name: SnoopLogMapping Method Description: Snoop Log Mapping for
	 * Tuxedo Services
	 * 
	 * @return Void
	 * 
	 */

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public void SnoopLogMapping(SnoopLogReq snoopLogReq) {

		Session session = sessionFactory.getCurrentSession();
		Snooplog snooplog = new Snooplog(snoopLogReq.getDtAccesed(), snoopLogReq.getDtCreated(),
				snoopLogReq.getEmployeeLogID(), snoopLogReq.getTxtDesc(), snoopLogReq.getTxtParmeters());
		session.save(snooplog);

	}

}
