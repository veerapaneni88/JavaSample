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

import us.tx.state.dfps.service.admin.dao.EventPersonLinkPersonMergeViewDao;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * retrieves all Event types a person is involved in. Aug 5, 2017- 12:10:26 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EventPersonLinkPersonMergeViewDaoImpl implements EventPersonLinkPersonMergeViewDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventPersonLinkPersonMergeViewDaoImpl.getEventTypeForPerson}")
	private String getEventTypeForPerson;

	private static final Logger log = Logger.getLogger(EventPersonLinkPersonMergeViewDaoImpl.class);

	public EventPersonLinkPersonMergeViewDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventTypeForPerson Method Description: This method will
	 * retrieve the event type meeting the criteria. Cinv52d
	 * 
	 * @param eventPersonLinkPersonMergeViewInDto
	 * @return List<EventPersonLinkPersonMergeViewOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventPersonLinkPersonMergeViewOutDto> getEventTypeForPerson(
			EventPersonLinkPersonMergeViewInDto eventPersonLinkPersonMergeViewInDto) {
		log.debug("Entering method getEventTypeForPerson in EventPersonLinkPersonMergeViewDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventTypeForPerson)
				.addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdTask", StandardBasicTypes.STRING) //ALM defect :15347
				.setResultTransformer(Transformers.aliasToBean(EventPersonLinkPersonMergeViewOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", eventPersonLinkPersonMergeViewInDto.getIdPerson());
		List<EventPersonLinkPersonMergeViewOutDto> personLinkPersonMergeViewOutDtos = (List<EventPersonLinkPersonMergeViewOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method getEventTypeForPerson in EventPersonLinkPersonMergeViewDaoImpl");
		return personLinkPersonMergeViewOutDtos;
	}
}
