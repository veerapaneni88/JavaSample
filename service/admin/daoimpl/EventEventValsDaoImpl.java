package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventEventValsDao;
import us.tx.state.dfps.service.admin.dto.EventInDto;
import us.tx.state.dfps.service.admin.dto.EventOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will query the Event table to fetch the event details for a given event Id
 * Aug 7, 2017- 7:21:17 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class EventEventValsDaoImpl implements EventEventValsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventEventValsDaoImpl.getEventVals}")
	private transient String getEventVals;

	private static final Logger log = Logger.getLogger(EventEventValsDaoImpl.class);

	public EventEventValsDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventValues Method Description: This method will get data
	 * from EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventOutDto> getEventValues(EventInDto pInputDataRec) {
		log.debug("Entering method EventEventValsQUERYdam in EventEventValsDaoImpl");
		Query queryEventVals = sessionFactory.getCurrentSession().createSQLQuery(getEventVals)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(EventOutDto.class));
		queryEventVals.setParameter("hI_szCdTask", pInputDataRec.getCdTask());
		queryEventVals.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<EventOutDto> liClsc71doDto = (List<EventOutDto>) queryEventVals.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc71doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method EventEventValsQUERYdam in EventEventValsDaoImpl");
		return liClsc71doDto;
	}
}
