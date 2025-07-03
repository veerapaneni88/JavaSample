package us.tx.state.dfps.service.investigation.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FacilityInvstDtl;
import us.tx.state.dfps.service.investigation.dao.FacilityInvstDtlDao;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:56 PM
 */
@Repository
public class FacilityInvstDtlDaoImpl implements FacilityInvstDtlDao {

	@Autowired
	private SessionFactory sessionFactory;

	public FacilityInvstDtlDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tx.us.dfps.impact.investigation.dao.ApsInvstDetailDao#
	 * getApsInvstDetailbyId(java.lang.Long)
	 */
	@Override
	public FacilityInvstDtl getFacilityInvstDetailbyParentId(Long uIdStage) {

		FacilityInvstDtl facilityInvstDtl = new FacilityInvstDtl();
		Criteria crApsInvstDetail = sessionFactory.getCurrentSession().createCriteria(FacilityInvstDtl.class)
				.add(Restrictions.eq("stage.idStage", uIdStage));
		facilityInvstDtl = (FacilityInvstDtl) crApsInvstDetail.uniqueResult();
		return facilityInvstDtl;
	}

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV54D
	 * 
	 * @param facilityInvst
	 * @
	 */
	@Override
	public void facilityInvstDtlSave(FacilityInvstDtl facilityInvstDtl) {
		sessionFactory.getCurrentSession().save(facilityInvstDtl);

	}

}
