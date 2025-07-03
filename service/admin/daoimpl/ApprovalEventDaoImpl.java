package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ApprovalEventDao;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Get all of
 * the ID EVENTS related to the captured ID APPROVAL from the Approval Event
 * Link Table. Aug 8, 2017- 10:25:41 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ApprovalEventDaoImpl implements ApprovalEventDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalEventDaoImpl.getApprovalEventLink}")
	private String getApprovalEventLink;

	private static final Logger log = Logger.getLogger(ApprovalEventDaoImpl.class);

	/**
	 * Description:Gets all of the ID EVENTS related to the captured ID APPROVAL
	 * from the Approval Event Link Table.
	 * 
	 * @param pInputDataRec
	 * @return liCcmn57doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalEventOutputDto> getApprovalEventLink(ApprovalEventLinkDto approvalEventLinkDto) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getApprovalEventLink)
				.addScalar("hostIdEvent", StandardBasicTypes.LONG).addScalar("hostCdTask", StandardBasicTypes.STRING)
				.setParameter("idApproval", approvalEventLinkDto.getUlIdApproval())
				.setResultTransformer(Transformers.aliasToBean(ApprovalEventOutputDto.class));
		List<ApprovalEventOutputDto> approvalEventOutputDtoList = query.list();
		return approvalEventOutputDtoList;
	}
}
