package us.tx.state.dfps.service.workload.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Approval;
import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.workload.dao.ApprovalEventLinkDao;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkEventDto;

@Repository
public class ApprovalEventLinkDaoImpl implements ApprovalEventLinkDao {

	@Value("${ApprovalEventLink.getEventIdAndTask}")
	String getEvent;

	@Autowired
	private SessionFactory sessionFactory;

	public ApprovalEventLinkDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method is used to retrieve approvaleventlink
	 * details based on idEvent. Dam Name: CCMN55D
	 * 
	 * @param idEvent
	 * @return approvalDto @
	 */
	public ApprovalEventLinkDto getApprovalEventLinkID(Long idEvent) {
		ApprovalEventLinkDto approvalEventLinkDto = new ApprovalEventLinkDto();
		approvalEventLinkDto = (ApprovalEventLinkDto) sessionFactory.getCurrentSession()
				.createCriteria(ApprovalEventLink.class)
				.setProjection(Projections.projectionList().add(Projections.property("idApproval"), "idApproval")
						.add(Projections.property("idApprovalEvent"), "idApprovalEvent")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idApproval"), "idApproval")
						.add(Projections.property("idCase"), "idCase").add(Projections.property("idEvent"), "idEvent"))
				.add(Restrictions.eq("idEvent", idEvent))
				.setResultTransformer(Transformers.aliasToBean(ApprovalEventLinkDto.class)).uniqueResult();
		return approvalEventLinkDto;
	}

	/**
	 * 
	 * Method Description: This Method is will Add several records and delete
	 * the records to the APPROVAL_EVENT_LINK table based on the input & request
	 * indicator DAM Name: CCMN91D
	 * 
	 * @param archInputDto
	 * @param approvalEventLinkDto
	 * @return String @
	 */
	public String getApprovalEventLinkAUD(ServiceReqHeaderDto archInputDto, ApprovalEventLinkDto approvalEventLinkDto) {
		String retMsg = "";
		ApprovalEventLink appEventLink = new ApprovalEventLink();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(approvalEventLinkDto.getIdApproval()))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(approvalEventLinkDto.getIdEvent()))
				&& archInputDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
			appEventLink.setDtLastUpdate(date);
			appEventLink.setIdEvent(approvalEventLinkDto.getIdEvent());
			appEventLink.setIdApproval(approvalEventLinkDto.getIdApproval());
			sessionFactory.getCurrentSession().save(appEventLink);
			retMsg = ServiceConstants.SUCCESS;
		} else if (archInputDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			// ApprovalEventLink apprvlEvnLink = (ApprovalEventLink)
			// session.load(ApprovalEventLink.class, new
			// Long(approvalEventLinkDto.getIdEvent()));
			Query queryDelApprvEvnLink = sessionFactory.getCurrentSession()
					.createQuery("from ApprovalEventLink where idEvent = :eventid");
			queryDelApprvEvnLink.setParameter("eventid", approvalEventLinkDto.getIdEvent());
			appEventLink = (ApprovalEventLink) queryDelApprvEvnLink.uniqueResult();
			if (null != appEventLink)
				sessionFactory.getCurrentSession().delete(appEventLink);
			retMsg = ServiceConstants.SUCCESS;
		} else {
			throw new DataLayerException(ServiceConstants.NOAUDDOP);
		}
		return retMsg;
	}

	/**
	 * 
	 * Method Description: This method will retrieve the list of
	 * ApprovalEventLinkEventDto for given approval. DAM Name: CCMN57D
	 * 
	 * @param idApproval
	 * @return List<ApprovalEventLinkEventDto>
	 */
	@SuppressWarnings("unchecked")
	public List<ApprovalEventLinkEventDto> approvalEventLinkSearchByApprovalId(Long idApproval) {
		List<ApprovalEventLinkEventDto> list = null;
		Query queryApproval = sessionFactory.getCurrentSession().createSQLQuery(getEvent)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ApprovalEventLinkEventDto.class));
		queryApproval.setParameter("idApproval", idApproval);
		list = queryApproval.list();
		return list;
	}

	@Override
	public void saveorUpdate(ApprovalEventLink approvalEventLink) {
		sessionFactory.getCurrentSession().saveOrUpdate(approvalEventLink);
	}
}