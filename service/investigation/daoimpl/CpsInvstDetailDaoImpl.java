package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:56 PM
 */
@Repository
public class CpsInvstDetailDaoImpl implements CpsInvstDetailDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public CpsInvstDetailDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tx.us.dfps.impact.investigation.dao.ApsInvstDetailDao#
	 * getApsInvstDetailbyId(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvstDetail> getCpsInvstDetailbyParentId(Long uIdStage) {

		List<CpsInvstDetail> CpsInvstDetailList = new ArrayList<>();
		Criteria crApsInvstDetail = sessionFactory.getCurrentSession().createCriteria(CpsInvstDetail.class)
				.add(Restrictions.eq("stage.idStage", uIdStage));
		CpsInvstDetailList = (List<CpsInvstDetail>) crApsInvstDetail.list();
		return CpsInvstDetailList;
	}

	@Override
	public Long updtCpsInvstDetail(CpsInvstDetail cpsInvstDetail, String operation) {
		Long idCpsInvstDetail;
		if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
			sessionFactory.getCurrentSession().save(cpsInvstDetail);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_UPDATE))
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(cpsInvstDetail));
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_DELETE))
			sessionFactory.getCurrentSession().delete(cpsInvstDetail);
		idCpsInvstDetail = cpsInvstDetail.getIdEvent();
		return idCpsInvstDetail;
	}

	/**
	 * This DAM will add, update, and delete from the CPS_INVST_DETAIL table
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV12D
	 * 
	 * @param uIdEvent
	 * @return @
	 */
	@Override
	public CpsInvstDetail getCpsInvstDetailbyEventId(Long uIdEvent) {
		CpsInvstDetail cpsInvstDetail = new CpsInvstDetail();
		Criteria crApsInvstDetail = sessionFactory.getCurrentSession().createCriteria(CpsInvstDetail.class)
				.add(Restrictions.eq("idEvent", uIdEvent));
		cpsInvstDetail = (CpsInvstDetail) crApsInvstDetail.uniqueResult();
		return cpsInvstDetail;
	}

	@Override
	public CpsInvstDetail getCpsInvstDetailbyStageId(Stage stage) {
		CpsInvstDetail cpsInvstDetail = new CpsInvstDetail();
		Criteria crCpsInvstDetail = sessionFactory.getCurrentSession().createCriteria(CpsInvstDetail.class)
				.add(Restrictions.eq("stage", stage));
		cpsInvstDetail = (CpsInvstDetail) crCpsInvstDetail.uniqueResult();
		return cpsInvstDetail;
	}

}
