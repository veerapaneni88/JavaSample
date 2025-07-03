package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Demote the
 * status of all previously captured approval related events to Complete Aug
 * 8,2017- 10:39:00 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EventUpdEventStatusDaoImpl implements EventUpdEventStatusDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventUpdEventStatusDaoImpl.updateEvent}")
	private String updateEvent;

	private static final Logger log = Logger.getLogger(EventUpdEventStatusDaoImpl.class);

	public EventUpdEventStatusDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updateEvent Method Description:This method updates event
	 * status
	 * 
	 * @param pInputDataRec
	 * @return int @
	 */
	@Override
	public int updateEvent(EventUpdEventStatusInDto pInputDataRec) {
		log.debug("Entering method EventUpdEventStatusQUERYdam in EventUpdEventStatusDaoImpl");
		int rowCount = ServiceConstants.Zero;
		switch (pInputDataRec.getCdReqFunction()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
			Event event = (Event) criteria.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdEventStatus())) {
				event.setCdEventStatus(pInputDataRec.getCdEventStatus());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(event);
			break;
		}
		log.debug("Exiting method EventUpdEventStatusQUERYdam in EventUpdEventStatusDaoImpl");
		return rowCount;
	}
}
