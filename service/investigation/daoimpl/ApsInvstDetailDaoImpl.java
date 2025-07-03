package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:56 PM
 */
@Repository
public class ApsInvstDetailDaoImpl implements ApsInvstDetailDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${ApsInvstDetailDaoImpl.getApsInvestigationDetails}")
	private String getApsInvestigationDetailsSql;

	@Value("${ApsInvstDetailDaoImpl.getInvNarrativeEvents}")
	private String getInvNarrativeEventsSql;

	@Value("${ApsInvstDetailDaoImpl.getInvNarrativeEventsByStage}")
	private String getInvNarrativeEventsByStageSql;


	public ApsInvstDetailDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tx.us.dfps.impact.investigation.dao.ApsInvstDetailDao#
	 * getApsInvstDetailbyId(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApsInvstDetail> getApsInvstDetailbyParentId(Long uIdStage) {

		List<ApsInvstDetail> apsInvstDetailList = new ArrayList<>();
		Criteria crApsInvstDetail = sessionFactory.getCurrentSession().createCriteria(ApsInvstDetail.class)
				.add(Restrictions.eq("stage.idStage", uIdStage));
		apsInvstDetailList = (List<ApsInvstDetail>) crApsInvstDetail.list();

		return apsInvstDetailList;
	}

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	@Override
	public void saveApsInvstDetail(ApsInvstDetail apsInvstDetail) {
		sessionFactory.getCurrentSession().save(apsInvstDetail);

	}

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	@Override
	public void updateApsInvstDetail(ApsInvstDetail apsInvstDetail) {
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(apsInvstDetail));

	}

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	@Override
	public void deleteApsInvstDetail(ApsInvstDetail apsInvstDetail) {
		sessionFactory.getCurrentSession().delete(apsInvstDetail);

	}

	@Override
	public ApsCaseReviewDto getApsInvestigationDetails(Long caseId, String stageType) {
		ApsCaseReviewDto apsCaseReviewDto = null;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsInvestigationDetailsSql)
				.addScalar("priorStageId", StandardBasicTypes.LONG)
				.addScalar("invStageId",StandardBasicTypes.LONG)
				.addScalar("dtDtApsInvstCmplt",StandardBasicTypes.DATE)
				.addScalar("dtApsInvstBegun",StandardBasicTypes.DATE)
				.addScalar("dtApsInvstCltAssmt",StandardBasicTypes.DATE)
				.addScalar("szCdApsInvstOvrallDisp", StandardBasicTypes.STRING)
				.addScalar("szCdApsInvstFinalPrty",StandardBasicTypes.STRING)
				.addScalar("dtDtClientAdvised", StandardBasicTypes.DATE)
				.addScalar("bIndExtDoc", StandardBasicTypes.STRING)
				.addScalar("bIndFamViolence", StandardBasicTypes.STRING)
				.addScalar("bIndLegalAction", StandardBasicTypes.STRING)
				.addScalar("bIndClient", StandardBasicTypes.STRING)
				.addScalar("bIndECS", StandardBasicTypes.STRING)
				.addScalar("szTxtClientOther", StandardBasicTypes.STRING)
				.addScalar("szTxtMethodComm", StandardBasicTypes.STRING)
				.addScalar("szTxtTrnsNameRlt", StandardBasicTypes.STRING)
				.addScalar("szTxtAltComm", StandardBasicTypes.STRING)
				.addScalar("dtDtClientAdvised", StandardBasicTypes.DATE)
				.addScalar("cdClosureType", StandardBasicTypes.STRING)
				.addScalar("cdInterpreter", StandardBasicTypes.STRING)
				.setParameter("idCase", caseId)
				.setParameter("stageType", stageType)
				.setResultTransformer(Transformers.aliasToBean(ApsCaseReviewDto.class)));
		apsCaseReviewDto = (ApsCaseReviewDto) sQLQuery1.uniqueResult();
		return apsCaseReviewDto;
	}

	@Override
	public List<Long> getInvNarrativeEvents(Long caseId, Long stageId) {
		List<Long> eventIds = null;
		if (null != stageId && stageId != 0L) {
			eventIds = (List<Long>)((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInvNarrativeEventsByStageSql)
					.setParameter("idCase", caseId)
					.setParameter("idStage", stageId))
					.addScalar("idEvent", StandardBasicTypes.LONG).list();
		} else {
			eventIds = (List<Long>)((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInvNarrativeEventsSql)
					.setParameter("idCase", caseId))
					.addScalar("idEvent", StandardBasicTypes.LONG).list();
		}
		return eventIds;
	}
}