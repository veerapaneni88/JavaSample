package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dao.FetchEventDetailDao;
import us.tx.state.dfps.service.admin.dto.FetchEventAdminDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This Class Fetches the Event Details using EventID
 * 
 * Aug 5, 2017- 7:05:32 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class FetchEventDetailDaoImpl implements FetchEventDetailDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(FetchEventDetailDaoImpl.class);

	/**
	 * 
	 * Method Name: ccmn45dQUERYdam Method Description: Method to fetch Event
	 * Details using EventID.
	 * 
	 * @param pCCMN45DInputRec
	 * @return List<Ccmn45doDto> @
	 */
	@Override
	public FetchEventDetailDto getEventDetail(FetchEventAdminDto pInputDataRec) {
		log.debug("Entering method getEventDetls in Ccmn45dDaoImpl");

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));

		Event event = (Event) criteria.uniqueResult();

		FetchEventDetailDto ccmn45doDto = new FetchEventDetailDto();
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn45dDaoImpl.not.found.ulIdEvent", null, Locale.US));
		} else {

			ccmn45doDto.setIdEvent(event.getIdEvent());
			ccmn45doDto.setCdTask(event.getCdTask());
			ccmn45doDto.setDtLastUpdate(event.getDtLastUpdate());
			ccmn45doDto.setCdEventStatus(event.getCdEventStatus());
			ccmn45doDto.setCdEventType(event.getCdEventType());
			ccmn45doDto.setDtEventOccurred(event.getDtEventOccurred());
			if (event.getStage() != null) {
				ccmn45doDto.setIdStage(event.getStage().getIdStage());
			}
			if (event.getPerson() != null){
				ccmn45doDto.setIdPerson(event.getPerson().getIdPerson());
			}
			ccmn45doDto.setTxtEventDescr(event.getTxtEventDescr());
			ccmn45doDto.setDtEventCreated(event.getDtEventCreated());

		}
		log.debug("Exiting method ccmn45dQUERYdam in Ccmn45dDaoImpl");
		return ccmn45doDto;
	}

}
