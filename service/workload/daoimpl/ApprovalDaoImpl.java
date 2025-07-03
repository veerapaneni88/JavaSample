package us.tx.state.dfps.service.workload.daoimpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Approval;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dto.ApprovalDto;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonSearchDto;

@Repository
public class ApprovalDaoImpl implements ApprovalDao {

	@Value("${Approval.getApprovalDetail}")
	private String getApprovalDetailSql;

	@Value("${ApprovalPersonSearchDaoImpl.searchApprovalPersonSql}")
	String searchApprovalPerson;

	@Value("${ApprovalDaoImpl.checkStageCase}")
	private String checkStageCase;

	@Value("${ApprovalDaoImpl.getPrimaryWorkerIdForStage}")
	private String getPrimaryWorkerIdForStage;

	@Value("${ApprovalDaoImpl.getARStageOverallDisposition}")
	private String getARStageOverallDisposition;

	@Value("${ApprovalDaoImpl.fetchIdEventForIdAprEvent}")
	private String fetchIdEventForIdAprEvent;

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private MobileUtil mobileUtil;

	public ApprovalDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method is used to perform AUD operation based on
	 * approvalDto Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param approvalDto
	 * @return String @
	 */
	public String getApprovalAUD(ServiceReqHeaderDto ServiceReqHeaderDto, ApprovalDto approvalDto, Long idEvent) {
		Approval apprvEntity = new Approval();
		Date date = new Date();
		// Long retApprovalId = 0l;
		String retMsg = "";
		Person personentity = new Person();
		personentity.setIdPerson(approvalDto.getId_Person());
 		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(approvalDto.getId_Person()))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idEvent))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(approvalDto.getApprovalTopic()))
				&& ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
			apprvEntity.setIdApproval(idEvent);
			apprvEntity.setPerson(personentity);
			apprvEntity.setTxtApprovalTopic(approvalDto.getApprovalTopic());
			apprvEntity.setDtLastUpdate(date);
			sessionFactory.getCurrentSession().persist(apprvEntity);
			retMsg = ServiceConstants.SUCCESS;
			// retApprovalId = apprvEntity.getIdApproval();
		} else if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(approvalDto.getId_Person()))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(approvalDto.getIdApproval()))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(approvalDto.getApprovalTopic()))
				&& ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
			apprvEntity.setIdApproval(approvalDto.getIdApproval());
			// approvalEntity.setPerson(approvalDto.getId_Person());
			apprvEntity.setTxtApprovalTopic(approvalDto.getApprovalTopic());
			apprvEntity.setDtLastUpdate(date);
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(apprvEntity));
			retMsg = ServiceConstants.SUCCESS;
			// retApprovalId = apprvEntity.getIdApproval();
		} else if (ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			Query queryDelApprvEvnLink = sessionFactory.getCurrentSession().createQuery(getApprovalDetailSql);
			queryDelApprvEvnLink.setParameter("id_approval", approvalDto.getIdApproval());
			// queryDelApprvEvnLink.setParameter("dtLastUpdate",
			// approvalDto.get);
			apprvEntity = (Approval) queryDelApprvEvnLink.list().get(0);
			sessionFactory.getCurrentSession().delete(apprvEntity);
			retMsg = ServiceConstants.SUCCESS;
			// retApprovalId = apprvEntity.getIdApproval();
		} else {
			throw new DataLayerException(ServiceConstants.NOAUDDOP);
		}
		return retMsg;
	}

	/**
	 * 
	 * Method Description:retrieve information from PERSON and APPROVAL based on
	 * given id approval for DAM-CCMN58D
	 * 
	 * @param idApproval
	 * @return @
	 */
	public ApprovalPersonSearchDto approvalPersonSearchbyId(Long idApproval) {
		ApprovalPersonSearchDto personDtl = null;
		Query queryApproval = sessionFactory.getCurrentSession().createSQLQuery(searchApprovalPerson)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("approvalTopic", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ApprovalPersonSearchDto.class));
		queryApproval.setParameter("idApproval", idApproval);
		personDtl = (ApprovalPersonSearchDto) queryApproval.uniqueResult();
		return personDtl;
	}

	/**
	 * Method Description: Get Stage Id based on the Case ID. Service Name: Kin
	 * Approval Ejb
	 * 
	 * @param idCase
	 * @return Long @
	 */
	public Boolean checkStageCaseID(Long idCase) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(checkStageCase)
				.setParameter("idCase", idCase).setParameter("cdStg", ServiceConstants.CSTAGES_KIN);
		return (!TypeConvUtil.isNullOrEmpty(query.uniqueResult())) ? ServiceConstants.TRUEVAL
				: ServiceConstants.FALSEVAL;
	}

	@Value("${Approver.getApproverStatus}")
	private String getApproverStatusSql;

	@Value("${Approver.getApproverStatusForMPS}")
	private String getApproverStatusSqlForMPS;
	
	/**
	 * Returns the last (using the fact that ID columns increment) approvers
	 * status record for the event. This will always be one of "APRV," "REJT,"
	 * "PEND," OR "INVD." If the event has not been submitted for approval, it
	 * will be null.
	 *
	 * @param ulIdEvent
	 *            The ID_EVENT of the event for which the Approvers status will
	 *            be returned.
	 * @return The approvers status for the particular event.
	 */
	public String getApproversStatus(Long idEvent) {
		String strQuery = getApproverStatusSql;
		if(mobileUtil.isMPSEnvironment()){
			strQuery = getApproverStatusSqlForMPS;
		}
		Query queryApprovalStatus = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
		queryApprovalStatus.setParameter("idEvent", idEvent);
		return (String) queryApprovalStatus.uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.workload.dao.ApprovalDao#
	 * getPrimaryWorkerIdForStage(us.tx.state.dfps.service.common.request.
	 * CommonHelperReq)
	 */
	@Override
	public CommonHelperRes getPrimaryWorkerIdForStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes assignWorkloadReq = new CommonHelperRes();
		assignWorkloadReq = (CommonHelperRes) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPrimaryWorkerIdForStage).setParameter("idStage", commonHelperReq.getIdStage()))
						.addScalar("ulIdPerson", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(CommonHelperRes.class)).uniqueResult();
		return assignWorkloadReq;
	}

	/**
	 * AR - Stage progression from AR - FPR - message not displayed This method
	 * will fetch the overall disposition
	 * 
	 * @param idCase
	 *            the stage identifier
	 * @return String disposition code @
	 */
	@SuppressWarnings("unchecked")
	public String getARStageOverallDisposition(Long idCase, Long idStage) {
		Query queryARStageOverallDisposition = sessionFactory.getCurrentSession()
				.createSQLQuery(getARStageOverallDisposition);
		queryARStageOverallDisposition.setParameter("idCase", idCase);
		queryARStageOverallDisposition.setParameter("idStage", idStage);
		String stageOverallDispAR = "";
		List<String> stageOverallDisp = queryARStageOverallDisposition.list();
		if (!ObjectUtils.isEmpty(stageOverallDisp)) {
			if (null != stageOverallDisp.get(0)) {
				stageOverallDispAR = stageOverallDisp.get(0);
			} else if (stageOverallDisp.size() > 1 && !ObjectUtils.isEmpty(stageOverallDisp.get(1))) {
				stageOverallDispAR = stageOverallDisp.get(1);
			}
		}
		return stageOverallDispAR;
	}

	/**
	 * Returns idEvent for the given Approval Event.
	 * 
	 * @param idAprvlEvent
	 * 
	 * @return idEvent
	 * 
	 * @ InvalidRequestException
	 */
	@SuppressWarnings("unchecked")
	public Long fetchIdEventForIdAprEvent(Long IdApproval) {
		Long eventID = 0l;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(fetchIdEventForIdAprEvent)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idEvent", IdApproval);
		List<Long> approavlEeventIds = (List<Long>) query.list();
		if (CollectionUtils.isNotEmpty(approavlEeventIds)) {
			return approavlEeventIds.get(0);
		}
		return eventID;
	}

	/**
	 * Returns the ulIdPerson of the Primary Worker of the stage. i;e the Person
	 * with role as PRIMARY ("PR").
	 * 
	 * @param ulIdStage
	 * @return
	 */

	public long getPrimaryWorkerIdForStage(Long ulIdStage) {

		long ulIdPersonPrimaryWorker = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class);
		criteria.add(Restrictions.eq("id.idWkldStage", ulIdStage));
		criteria.add(Restrictions.eq("id.cdWkldStagePersRole", "PR"));

		criteria.setProjection(Projections.property("id.idWkldPerson"));

		// There is a Hibernate bug where using projection and uniqueResult causes NPR if no rows are returned. We
		// could work around the bug by using list(), as shown below. The fix is not checked in because it is out of
		// scope for TC 558.
		//List<Long> personIdList = criteria.list();
		//if (personIdList.size() > 0) {
		//	ulIdPersonPrimaryWorker = personIdList.get(0);
		//}
		ulIdPersonPrimaryWorker = (long) criteria.uniqueResult();
		return ulIdPersonPrimaryWorker;
	}

	@Override
	public void saveorUpdate(Approval approval) {
		sessionFactory.getCurrentSession().saveOrUpdate(approval);
	}
}
