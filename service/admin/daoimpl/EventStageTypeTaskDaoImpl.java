package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.EventStageTypeTaskDao;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching Event Details> Aug 4, 2017- 11:50:18 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class EventStageTypeTaskDaoImpl implements EventStageTypeTaskDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventStageTypeTaskDaoImpl.class);

	@Value("${EventStageTypeTaskDaoImpl.getEventDetails}")
	private transient String getEventDetails;

	public EventStageTypeTaskDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will get data
	 * from EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStageTypeTaskOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventStageTypeTaskOutDto> getEventDtls(EventStageTypeTaskInDto pInputDataRec) {
		log.debug("Entering method EventStageTypeTaskQUERYdam in EventStageTypeTaskDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventDetails)
				.setResultTransformer(Transformers.aliasToBean(EventStageTypeTaskOutDto.class)));
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_szCdTask", pInputDataRec.getCdTask())
				.setParameter("hI_szCdEventType", pInputDataRec.getCdEventType());
		List<EventStageTypeTaskOutDto> liCsesa3doDto = new ArrayList<>();
		liCsesa3doDto = (List<EventStageTypeTaskOutDto>) sQLQuery1.list();
		log.debug("Exiting method EventStageTypeTaskQUERYdam in EventStageTypeTaskDaoImpl");
		return liCsesa3doDto;
	}
}
