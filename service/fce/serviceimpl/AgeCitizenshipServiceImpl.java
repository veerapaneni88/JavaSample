package us.tx.state.dfps.service.fce.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.AgeCitizenshipDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.service.AgeCitizenshipService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AgeCitizenshipServiceImpl Feb 20, 2018- 3:14:18 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class AgeCitizenshipServiceImpl implements AgeCitizenshipService {

	/** The fce dao. */
	@Autowired
	private FceDao fceDao;

	/** The event dao. */
	@Autowired
	private EventDao eventDao;

	/** The event service. */
	@Autowired
	private EventService eventService;

	/** The fce service. */
	@Autowired
	private FceService fceService;
	
	@Autowired
	private EventUtil eventUtil;

	/**
	 * Method Name: read Method Description:Fetches the Age Citizenship details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return ageCitizenshipDto @
	 */
	@Transactional
	public AgeCitizenshipDto read(long idStage, long idEvent, long idLastUpdatePerson) {

		FceContextDto fceContextDto = fceService.initializeFceApplication(idStage, idEvent, idLastUpdatePerson);
		FceApplicationDto fceApplicationDto = fceContextDto.getFceApplicationDto();
		fceApplicationDto
				.setIndEvaluationConclusion(fceDao.getIndEvaluationConclusion(fceApplicationDto.getIdFceApplication()));
		FceEligibilityDto fceEligibilityDto = fceContextDto.getFceEligibilityDto();

		FcePersonDto fcePersonDto = getFcePersonDB(fceContextDto.getIdPerson(), fceContextDto.getIdFcePerson());

		AgeCitizenshipDto ageCitizenshipDto = new AgeCitizenshipDto();
		ageCitizenshipDto.setFceApplicationDto(fceApplicationDto);
		ageCitizenshipDto.setFceEligibilityDto(fceEligibilityDto);
		ageCitizenshipDto.setFcePersonDto(fcePersonDto);
		ageCitizenshipDto.setCdEventStatus(fceContextDto.getCdEventStatus());
		return ageCitizenshipDto;
	}

	/**
	 * Method Name: save Method Description:Saves the Age Citizenship details
	 * 
	 * @param ageCitizenshipDto
	 * @return ServiceConstants.ONE_VAL @
	 */
	@Transactional
	public String save(AgeCitizenshipDto ageCitizenshipDto) {
		FceApplicationDto fceApplicationDto = ageCitizenshipDto.getFceApplicationDto();
		FceEligibilityDto fceEligibilityDto = ageCitizenshipDto.getFceEligibilityDto();
		fceService.verifyCanSave(fceEligibilityDto.getIdStage(), fceEligibilityDto.getIdLastUpdatePerson());
		fceDao.updateFcEligibilityAndApp(fceApplicationDto, fceEligibilityDto);
		Long idEvent = fceApplicationDto.getIdEvent();
		EventDto eventDto = eventDao.getEventByid(idEvent);
		String eventType = eventDto.getCdEventStatus();
		if (ServiceConstants.EVENTSTATUS_NEW.equals(eventType)) {
			eventUtil.changeEventStatus(idEvent, ServiceConstants.EVENTSTATUS_NEW, ServiceConstants.EVENTSTATUS_PROCESS);
		} else if (ServiceConstants.EVENTSTATUS_COMPLETE.equals(eventType)) {
			eventUtil.changeEventStatus(idEvent, ServiceConstants.EVENTSTATUS_COMPLETE, ServiceConstants.EVENTSTATUS_PENDING);
		}
		String eventStatus = eventDao.getEventStatus(idEvent);
		return eventStatus;
	}

	/**
	 * Gets the cd event status.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the cd event status @ the service exception
	 */
	public String getCdEventStatus(long idEvent) {
		EventDto eventDto = eventDao.getEventByid(idEvent);
		return eventDto.getCdEventStatus();
	}

	/**
	 * Gets the fce person DB.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idFcePerson
	 *            the id fce person
	 * @return the fce person DB @ the service exception
	 */
	public FcePersonDto getFcePersonDB(long idPerson, long idFcePerson) {
		FcePersonDto fcePersonDto = fceDao.findFcePersonByPrimaryKey(idFcePerson);
		return fcePersonDto;
	}

}
