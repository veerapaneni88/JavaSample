package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches the Event Details using EventID Aug 5, 2017- 7:05:32 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventIdDaoImpl implements EventIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	@Value("${EventIdDaoImpl.getEventDetls}")
	private transient String getEventDetls;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventIdDaoImpl.class);

	public EventIdDaoImpl() {
		super();
	}

	/**
	 * Method Name: getEventDetailList Method Description: This Method Fetches
	 * the Event Details using Event Id. ccmn45dQUERYdam
	 * 
	 * @param pInputDataRec
	 * @return List<EventIdOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventIdOutDto> getEventDetailList(EventIdInDto pInputDataRec) {
		log.debug("Entering method EventIdQUERYdam in EventIdDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventDetls)
				.setResultTransformer(Transformers.aliasToBean(EventIdOutDto.class)));
		sQLQuery1.addScalar("cdTask", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdEventStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdEventType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("eventDescr", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		List<EventIdOutDto> liCcmn45doDto = (List<EventIdOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmn45doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn45dDaoImpl.not.found.ulIdEvent", null, Locale.US));
		}
		log.debug("Exiting method EventIdQUERYdam in EventIdDaoImpl");
		return liCcmn45doDto;
	}
}
