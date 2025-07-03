package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventStageTypeStatusDao;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl to
 * fetch event status> Aug 8, 2017- 4:22:29 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class EventStageTypeStatusDaoImpl implements EventStageTypeStatusDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventStageTypeStatusDaoImpl.getEventStatus}")
	private transient String getEventStatus;

	private static final Logger log = Logger.getLogger(EventStageTypeStatusDaoImpl.class);

	public EventStageTypeStatusDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventStatus Method Description: This method will get
	 * Event Status from Event.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStageTypeStatusOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventStageTypeStatusOutDto> getEventStatus(EventStageTypeStatusInDto pInputDataRec) {
		log.debug("Entering method EventStageTypeStatusQUERYdam in EventStageTypeStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventStatus)
				.setResultTransformer(Transformers.aliasToBean(EventStageTypeStatusOutDto.class)));
		sQLQuery1.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_szCdEventType", pInputDataRec.getCdEventType())
				.setParameter("hI_szCdEventStatus", pInputDataRec.getCdEventStatus());
		List<EventStageTypeStatusOutDto> liCsvc34doDto = new ArrayList<>();
		liCsvc34doDto = (List<EventStageTypeStatusOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsvc34doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("EventStageTypeStatusDaoImpl.not.found.eventstatus", null, Locale.US));
		}
		log.debug("Exiting method EventStageTypeStatusQUERYdam in EventStageTypeStatusDaoImpl");
		return liCsvc34doDto;
	}

	/**
	 * 
	 * Method Name: getEventStatus Method Description: This method will get
	 * Event Status from Event.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStageTypeStatusOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventStageTypeStatusOutDto> getEventStatusForDayCare(EventStageTypeStatusInDto pInputDataRec,
			String eventType, String eventStatus) {
		pInputDataRec.setCdEventType(eventType);
		pInputDataRec.setCdEventStatus(eventStatus);
		log.debug("Entering method EventStageTypeStatusQUERYdam in EventStageTypeStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventStatus)
				.setResultTransformer(Transformers.aliasToBean(EventStageTypeStatusOutDto.class)));
		sQLQuery1.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_szCdEventType", pInputDataRec.getCdEventType())
				.setParameter("hI_szCdEventStatus", pInputDataRec.getCdEventStatus());
		List<EventStageTypeStatusOutDto> liCsvc34doDto = new ArrayList<>();
		liCsvc34doDto = (List<EventStageTypeStatusOutDto>) sQLQuery1.list();
		log.debug("Exiting method EventStageTypeStatusQUERYdam in EventStageTypeStatusDaoImpl");
		return liCsvc34doDto;
	}
}
