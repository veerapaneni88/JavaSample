package us.tx.state.dfps.service.workload.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Approval;
import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.domain.Approvers;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.workload.dao.ApprovalEventDelDao;

@Repository
public class ApprovalEventDelDaoImpl implements ApprovalEventDelDao {

	@Value("${Approval.getApprovalDetail}")
	private String getApprovalDetlSql;

	@Value("${ApprovalEvnLinkDel.getApprovalEvnLinkDtl}")
	private String getApprovalEvnLinkDtlSql;

	@Value("${TodoDel.getTodoDetail}")
	private String getTodoDetailSql;

	@Value("${EventDel.getEventDetail}")
	private String getEventDetailSql;

	@Value("${ApproversDel.getApproversDetails}")
	private String getApproversDetailSql;

	@Autowired
	private SessionFactory sessionFactory;

	public ApprovalEventDelDaoImpl() {

	}

	/**
	 * 
	 * Method Description: Method is implemented in ApprovalEventDelDaoImpl to
	 * perform delete operations Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param approvalID
	 * @param todoID
	 * @return ServiceResHeaderDto @
	 */
	@SuppressWarnings("unchecked")
	public String getApprovalEventrecordDel(ServiceReqHeaderDto archInputDto, Long approvalID, Long todoID,
			List<Long> eventList) {
		String retMsg = "";
		if (archInputDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			Approval approval_Id = new Approval();
			approval_Id.setIdApproval(approvalID);
			for (long eventID : eventList) {
				Query queryDeleteAppEvnLink = sessionFactory.getCurrentSession().createQuery(getApprovalEvnLinkDtlSql);
				queryDeleteAppEvnLink.setParameter("idapproval", approvalID);
				queryDeleteAppEvnLink.setParameter("idevent", eventID);
				ApprovalEventLink appEventLinkEntity = (ApprovalEventLink) queryDeleteAppEvnLink.uniqueResult();
				if (appEventLinkEntity != null)
					sessionFactory.getCurrentSession().delete(appEventLinkEntity);
			}
			Query queryDeleteApprovers = sessionFactory.getCurrentSession().createQuery(getApproversDetailSql);
			queryDeleteApprovers.setParameter("idapproval", approvalID);
			List<Approvers> approversEntity = (List<Approvers>) queryDeleteApprovers.list();
			for (Approvers approversEnt : approversEntity) {
				if (null != approversEnt)
					sessionFactory.getCurrentSession().delete(approversEnt);
			}
			Query queryDeleteApproval = sessionFactory.getCurrentSession().createQuery(getApprovalDetlSql);
			queryDeleteApproval.setParameter("id_approval", approvalID);
			Approval approvalEntity = (Approval) queryDeleteApproval.uniqueResult();
			if (null != approvalEntity)
				sessionFactory.getCurrentSession().delete(approvalEntity);
			Query queryDeleteTodo = sessionFactory.getCurrentSession().createQuery(getTodoDetailSql);
			queryDeleteTodo.setParameter("idTodo", todoID);
			Todo todoentity = (Todo) queryDeleteTodo.uniqueResult();
			if (todoentity != null)
				sessionFactory.getCurrentSession().delete(todoentity);
			Query queryDeleteEvent = sessionFactory.getCurrentSession().createQuery(getEventDetailSql);
			queryDeleteEvent.setParameter("event_id", approvalID);
			Event evententity = (Event) queryDeleteEvent.uniqueResult();
			if (evententity != null)
				sessionFactory.getCurrentSession().delete(evententity);
			retMsg = ServiceConstants.SUCCESS;
		}
		return retMsg;
	}
}
