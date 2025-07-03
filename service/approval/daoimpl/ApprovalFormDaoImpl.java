package us.tx.state.dfps.service.approval.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.approval.dto.ApprovalSecondaryCommentsDto;
import us.tx.state.dfps.approval.dto.ApproverJobHistoryDto;
import us.tx.state.dfps.service.approval.dao.ApprovalFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ApprovalRejectionPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes calls
 * to database to retrieve data for service Mar 14, 2018- 12:30:48 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ApprovalFormDaoImpl implements ApprovalFormDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalFormDaoImpl.getApprovalData}")
	private String getApprovalDataSql;

	@Value("${ApprovalFormDaoImpl.getApprovalEventLink}")
	private String getApprovalEventLinkSql;

	@Value("${ApprovalFormDaoImpl.getApprover}")
	private String getApproverSql;

	@Value("${ApprovalFormDaoImpl.getApprovalRejection}")
	private String getApprovalRejectionSql;

	@Value("${ApprovalFormDaoImpl.secondaryComments}")
	private String getSecondaryApprovalCommentsSql;

	/**
	 * Method Name: getApprovalData Method Description: Gets approval data (DAM:
	 * CSES04D)
	 * 
	 * @param idEvent
	 * @return ApprovalFormDataDto
	 */
	@Override
	public ApprovalFormDataDto getApprovalData(Long idEvent) {
		ApprovalFormDataDto approvalFormDataDto = (ApprovalFormDataDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprovalDataSql).setParameter("idEvent", idEvent)
				.setParameter("szTrue", ServiceConstants.Y)).addScalar("idApproval", StandardBasicTypes.LONG)
						.addScalar("dtApprovalLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idApprovalPerson", StandardBasicTypes.LONG)
						.addScalar("approvalTopic", StandardBasicTypes.STRING)
						.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
						.addScalar("idName", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("indNameInvalid", StandardBasicTypes.STRING)
						.addScalar("nmFirst", StandardBasicTypes.STRING)
						.addScalar("nmMiddle", StandardBasicTypes.STRING).addScalar("nmLast", StandardBasicTypes.STRING)
						.addScalar("nmSuffix", StandardBasicTypes.STRING)
						.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
						.addScalar("dtNameEndDate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(ApprovalFormDataDto.class)).uniqueResult();
		return approvalFormDataDto;
	}

	/**
	 * Method Name: getApprovalEventLink Method Description: Gets approval and
	 * event data (DAM: CLSS50D)
	 * 
	 * @param idEvent
	 * @return List<EventDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDto> getApprovalEventLink(Long idEvent) {
		List<EventDto> eventList = (List<EventDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprovalEventLinkSql).setParameter("idEvent", idEvent))
						.addScalar("idApprovalEvent", StandardBasicTypes.LONG)
						.addScalar("approvalDateLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("cdEventType", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdEventTask", StandardBasicTypes.STRING)
						.addScalar("eventDescription", StandardBasicTypes.STRING)
						.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
						.addScalar("cdEventStatus", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EventDto.class)).list();
		return eventList;
	}

	/**
	 * Method Name: getApprover Method Description: Gets approver data (DAM:
	 * CLSC02D)
	 * 
	 * @param idEvent
	 * @return List<ApproverJobHistoryDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApproverJobHistoryDto> getApprover(Long idEvent) {
		List<ApproverJobHistoryDto> approverList = (List<ApproverJobHistoryDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getApproverSql).setParameter("idEvent", idEvent))
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("dtApproversRequested", StandardBasicTypes.DATE)
						.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
						.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
						.addScalar("cdJobClass", StandardBasicTypes.STRING)
						.addScalar("decode", StandardBasicTypes.STRING).addScalar("nmFirst", StandardBasicTypes.STRING)
						.addScalar("nmMiddle", StandardBasicTypes.STRING).addScalar("nmLast", StandardBasicTypes.STRING)
						.addScalar("nmSuffix", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ApproverJobHistoryDto.class)).list();
		return approverList;
	}

	/**
	 * Method Name: getApprovalRejection Method Description: Retrieves rejection
	 * data (DAM: CCMNI4D)
	 * 
	 * @param idStage
	 * @return List<ApprovalRejectionPersonDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalRejectionPersonDto> getApprovalRejection(Long idStage) {
		List<ApprovalRejectionPersonDto> rejectionList = (List<ApprovalRejectionPersonDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getApprovalRejectionSql).setParameter("idStage", idStage))
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("idRejector", StandardBasicTypes.LONG)
						.addScalar("dtRejection", StandardBasicTypes.DATE)
						.addScalar("indApsEffort", StandardBasicTypes.STRING)
						.addScalar("indProblems", StandardBasicTypes.STRING)
						.addScalar("indEvidence", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidRptr", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidAp", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidMp", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidCol", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidPhotos", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidDe", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidOth", StandardBasicTypes.STRING)
						.addScalar("indDiscretionary", StandardBasicTypes.STRING)
						.addScalar("approversCmnts", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ApprovalRejectionPersonDto.class)).list();
		return rejectionList;
	}

	/**
	 * Method Name: getSecondaryApprovalComments Method Description: to get
	 * secondary approval comments
	 * 
	 * @param idEvent
	 * @return ApprovalSecondaryCommentsDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ApprovalSecondaryCommentsDto getSecondaryApprovalComments(Long idEvent) {

		ApprovalSecondaryCommentsDto approvalSecondaryComments = (ApprovalSecondaryCommentsDto) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getSecondaryApprovalCommentsSql).setParameter("idEvent", idEvent))
						.addScalar("approverComments", StandardBasicTypes.STRING)
						.addScalar("empEmailAddr", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ApprovalSecondaryCommentsDto.class))
						.uniqueResult();

		return approvalSecondaryComments;
	}

}
