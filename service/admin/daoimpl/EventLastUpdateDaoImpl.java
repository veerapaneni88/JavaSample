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

import us.tx.state.dfps.service.admin.dao.EventLastUpdateDao;
import us.tx.state.dfps.service.admin.dto.EventLastUpdatedInDto;
import us.tx.state.dfps.service.admin.dto.EventLastUpdatedoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * EventLastUpdateDaoImpl Sep 8, 2017- 4:06:24 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class EventLastUpdateDaoImpl implements EventLastUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Csys13dDaoImpl.getLastUpdate}")
	private transient String getLastUpdate;

	@Value("${Csys13dDaoImpl.eventId}")
	private transient String eventId;

	private static final char SPACE = ' ';

	private static final Logger log = Logger.getLogger(EventLastUpdateDaoImpl.class);

	/**
	 * 
	 * Method Name:getEventDtls Method Description: This method fetches event
	 * details for given event id
	 *
	 * @param pInputDataRec
	 * @return List<EventLastUpdatedoDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventLastUpdatedoDto> getEventDtls(EventLastUpdatedInDto pInputDataRec) {
		log.debug("Entering method csys13dQUERYdam in Csys13dDaoImpl");

		StringBuilder stB = new StringBuilder();
		stB.append(this.getLastUpdate).append(pInputDataRec.getNmTable()).append(this.eventId);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stB.toString())
				.setResultTransformer(Transformers.aliasToBean(EventLastUpdatedoDto.class)));
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		List<EventLastUpdatedoDto> liCsys13doDto = (List<EventLastUpdatedoDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsys13doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Csys13dDaoImpl.not.found.ulIdEvent", null, Locale.US));
		}
		log.debug("Exiting method csys13dQUERYdam in Csys13dDaoImpl");
		return liCsys13doDto;
	}

	/**
	 * Method Name: getDtLastUpdateForEvent Method Description: This method
	 * fetches event details for given last update date
	 * 
	 * @param eventLastUpdatediDto
	 * @return EventLastUpdatedoDto @
	 */
	public EventLastUpdatedoDto getDtLastUpdateForEvent(EventLastUpdatedInDto csys13di) {

		StringBuilder str = new StringBuilder();
		str.append(this.getLastUpdate);
		str.append(SPACE);
		str.append("CONTACT_NARRATIVE").append(SPACE).append(this.eventId);

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(str.toString())
				.setResultTransformer(Transformers.aliasToBean(EventLastUpdatedoDto.class)));
		sQLQuery.addScalar("tsLastUpdate", StandardBasicTypes.DATE);
		sQLQuery.setParameter("hI_ulIdEvent", csys13di.getIdEvent());
		List<EventLastUpdatedoDto> csys13doDto = (List<EventLastUpdatedoDto>) sQLQuery.list();
		EventLastUpdatedoDto eventLastUpdatedoDto = new EventLastUpdatedoDto();
		if (csys13doDto.size() > ServiceConstants.Zero) {
			eventLastUpdatedoDto = csys13doDto.get(ServiceConstants.Zero);
		}
		return eventLastUpdatedoDto;
	}

}
