package us.tx.state.dfps.service.servicedlvryclosure.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.servicedlvryclosure.dao.DlvryClosureEventDao;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureEventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dlvry
 * closure Event Dao class Jun 4, 2018- 5:25:57 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class DlvryClosureEventDaoImpl implements DlvryClosureEventDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${DlvryClosureEventDaoImpl.retrvEventInfo}")
	private String retrvEventInfo;

	private static final Logger log = Logger.getLogger("ServiceBusiness-DlvryClosureEventDaoImpl");

	/**
	 * 
	 * Method Name: retrvEventList Method Description: This method retrieves
	 * Event information from Event table using idStage and event type.
	 * 
	 * @param idStage
	 * @param eventType
	 * @return List<DlvryClosureEventDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DlvryClosureEventDto retrvEvent(long idStage, String eventType) {
		log.debug("Entering method retrvEvent in DlvryClosureEventDaoImpl");
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrvEventInfo)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("lastUpdate", StandardBasicTypes.DATE)
				.setLong("hI_idStage", idStage).setString("hI_cdEventType", eventType)
				.setResultTransformer(Transformers.aliasToBean(DlvryClosureEventDto.class)));
		DlvryClosureEventDto dlvryClosureEvent = null;
		List<DlvryClosureEventDto> dlvryClosureEventList = (List<DlvryClosureEventDto>) sQLQuery.list();

		if (!ObjectUtils.isEmpty(dlvryClosureEventList)) {
			dlvryClosureEvent = dlvryClosureEventList.get(0);
		}

		log.debug("Exiting method retrvEvent in DlvryClosureEventDaoImpl");
		return dlvryClosureEvent;

	}

}
