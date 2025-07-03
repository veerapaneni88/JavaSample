package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventApprovalEventLinkDao;
import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Get all of
 * the ID EVENTS related to the captured ID APPROVAL from the Approval Event
 * Link Table. Aug 8, 2017- 10:25:41 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class EventApprovalEventLinkDaoImpl implements EventApprovalEventLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventApprovalEventLinkDaoImpl.getApprovalEventLink}")
	private String getApprovalEventLink;

	private static final Logger log = Logger.getLogger(EventApprovalEventLinkDaoImpl.class);

	public EventApprovalEventLinkDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<Ccmn57doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventApprovalEventLinkOutDto> getApprovalEventLink(EventApprovalEventLinkInDto pInputDataRec) {
		log.debug("Entering method EventApprovalEventLinkQUERYdam in EventApprovalEventLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApprovalEventLink)
				.addScalar("hostIdEvent", StandardBasicTypes.LONG).addScalar("hostCdTask")
				.setParameter("hostIdApproval", pInputDataRec.getIdApproval())
				.setResultTransformer(Transformers.aliasToBean(EventApprovalEventLinkOutDto.class)));
		List<EventApprovalEventLinkOutDto> liCcmn57doDto = (List<EventApprovalEventLinkOutDto>) sQLQuery1.list();
		log.debug("Exiting method EventApprovalEventLinkQUERYdam in EventApprovalEventLinkDaoImpl");
		return liCcmn57doDto;
	}
}
