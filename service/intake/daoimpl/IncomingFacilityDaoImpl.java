package us.tx.state.dfps.service.intake.daoimpl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomingFacility;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.intake.dao.IncomingFacilityDao;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 29, 2017 - 12:03:06 PM
 */
@Repository
public class IncomingFacilityDaoImpl implements IncomingFacilityDao {

	@Autowired
	private SessionFactory sessionFactory;

	public IncomingFacilityDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.intake.dao.IncomingFacilityDao#getFacilityDetail(
	 * org.tx.us.dfps.impact.request.FacDetailUpdtReq)
	 */
	@Override
	public String updtFacilityDetail(IncomingFacility incomingFacility, String operation) {

		String status = null;

		if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
			sessionFactory.getCurrentSession().persist(incomingFacility);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_UPDATE))
			sessionFactory.getCurrentSession().update(incomingFacility);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_DELETE))
			sessionFactory.getCurrentSession().delete(incomingFacility);
		status = sessionFactory.getCurrentSession().getTransaction().getLocalStatus().toString();

		return status;
	}

}
