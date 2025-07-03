package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.AdApprovalEventDao;
import us.tx.state.dfps.xmlstructs.inputstructs.ApproverEventDto;

@Repository
public class AdApprovalEventDaoImpl implements AdApprovalEventDao {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: adApprovalEventLink Method Description:This method delete
	 * and insert the record in APPROVAL_EVENT_LINK
	 * 
	 * @param approverEventDto
	 * @return long @
	 */
	@Override
	public long adApprovalEventLink(ApproverEventDto approverEventDto) {
		long deletedrows = 0;

		String cReqFuncCd = approverEventDto.getArchInputStructDto().getCreqFuncCd();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(cReqFuncCd)) {
			ApprovalEventLink approvalEventLink = new ApprovalEventLink();
			approvalEventLink.setIdEvent((long) approverEventDto.getUlIdEvent());
			approvalEventLink.setIdApproval(approverEventDto.getUlIdApproval());

			sessionFactory.getCurrentSession().save(approvalEventLink);

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(cReqFuncCd)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApprovalEventLink.class);
			criteria.add(Restrictions.eq("idEvent", approverEventDto.getUlIdEvent()));
			List<ApprovalEventLink> approvalEventLinkList = (List<ApprovalEventLink>) criteria.list();
			if (!TypeConvUtil.isNullOrEmpty(approvalEventLinkList)) {
				for (ApprovalEventLink approvalEventLink : approvalEventLinkList) {
					sessionFactory.getCurrentSession().delete(approvalEventLink);
				}
				deletedrows = approvalEventLinkList.size();
			}
		}
		return deletedrows;
	}
}
