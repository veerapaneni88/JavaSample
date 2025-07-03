package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches the Event Details using EventID Aug 5, 2017- 7:12:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FetchEventDaoImpl implements FetchEventDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(FetchEventDaoImpl.class);

	/**
	 * Method Name: fetchEventDao Method Description: Method to fetch Event
	 * Details using EventID.
	 * 
	 * @param FetchEventDto
	 * @return List<FetchEventResultDto>
	 */
	@Override
	public List<FetchEventResultDto> fetchEventDao(FetchEventDto fetchEventDto) {
		log.debug("Entering method getEventDetls in FetchEventDaoImpl");

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, fetchEventDto.getIdEvent());

		List<FetchEventResultDto> fetchEventResultDtos = null;
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn45dDaoImpl.not.found.ulIdEvent", null, Locale.US));
		}
		FetchEventResultDto fetchEventResultDto = new FetchEventResultDto();
		fetchEventResultDto.setIdEvent(event.getIdEvent());
		fetchEventResultDto.setCdTask(event.getCdTask());
		fetchEventResultDto.setDtLastUpdate(event.getDtLastUpdate());
		fetchEventResultDto.setCdEventStatus(event.getCdEventStatus());
		fetchEventResultDto.setCdEventType(event.getCdEventType());
		fetchEventResultDto.setDtDtEventOccurred(event.getDtEventOccurred());
		if (!TypeConvUtil.isNullOrEmpty(event.getStage())) {
			fetchEventResultDto.setIdStage(event.getStage().getIdStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(event.getPerson())) {
			fetchEventResultDto.setIdPerson(event.getPerson().getIdPerson());
		}
		fetchEventResultDto.setTxtEventDescr(event.getTxtEventDescr());
		fetchEventResultDto.setDtDtEventCreated(event.getDtEventCreated());
		fetchEventResultDto.setIdCase(event.getIdCase());
		fetchEventResultDtos = new ArrayList<FetchEventResultDto>();
		fetchEventResultDtos.add(fetchEventResultDto);

		log.debug("Exiting method getEventDetls in FetchEventDaoImpl");

		return fetchEventResultDtos;
	}

}
