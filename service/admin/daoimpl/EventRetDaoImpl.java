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

import us.tx.state.dfps.service.admin.dao.EventRetDao;
import us.tx.state.dfps.service.admin.dto.EventdiDto;
import us.tx.state.dfps.service.admin.dto.EventdoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV50S Aug
 * 7, 2017- 7:21:17 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EventRetDaoImpl implements EventRetDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Clsc71dDaoImpl.getEventVals}")
	private transient String getEventVals;

	private static final Logger log = Logger.getLogger(EventRetDaoImpl.class);

	/**
	 * Method Description: getEventValues - Method will query the Event table to
	 * fetch the event details for a given event Id
	 * 
	 * @param pInputDataRec
	 * @return liClsc71doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventdoDto> getEventValues(EventdiDto pInputDataRec) {
		log.debug("Entering method clsc71dQUERYdam in Clsc71dDaoImpl");
		Query queryEventVals = sessionFactory.getCurrentSession().createSQLQuery(getEventVals)
				.addScalar("ulIdEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("ulIdStage", StandardBasicTypes.LONG).addScalar("szCdEventType", StandardBasicTypes.STRING)
				.addScalar("ulIdEventPerson", StandardBasicTypes.LONG).addScalar("szCdTask", StandardBasicTypes.STRING)
				.addScalar("szTxtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtDtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("szCdEventStatus", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(EventdoDto.class));

		queryEventVals.setParameter("hI_szCdTask", pInputDataRec.getSzCdTask());
		queryEventVals.setParameter("hI_ulIdStage", pInputDataRec.getUlIdStage());
		List<EventdoDto> liClsc71doDto = (List<EventdoDto>) queryEventVals.list();

		if (TypeConvUtil.isNullOrEmpty(liClsc71doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.attributes", null, Locale.US));
		}

		log.debug("Exiting method clsc71dQUERYdam in Clsc71dDaoImpl");
		return liClsc71doDto;
	}

}
