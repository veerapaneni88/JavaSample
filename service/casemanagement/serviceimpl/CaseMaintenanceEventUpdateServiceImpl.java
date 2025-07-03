package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceInsertEventDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateEventPersonLinkDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchTaskEventDao;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceEventUpdateService;
import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.GetEventInDto;
import us.tx.state.dfps.service.casepackage.dto.GetEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.PostEventInDto;
import us.tx.state.dfps.service.casepackage.dto.PostEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateEventInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateEventOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceEventUpdateServiceImpl Feb 7, 2018- 5:53:20 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CaseMaintenanceEventUpdateServiceImpl implements CaseMaintenanceEventUpdateService {

	@Autowired
	FetchTaskEventDao fetchTaskEventDao;

	// Ccmn46d
	@Autowired
	CaseMaintenanceInsertEventDao caseMaintenanceInsertEventDao;

	// Ccmn68d
	@Autowired
	CaseMaintenanceUpdateEventPersonLinkDao caseMaintenanceUpdateEventPersonLinkDao;

	private static final Logger log = Logger.getLogger(CaseMaintenanceEventUpdateServiceImpl.class);

	/**
	 * Method Name: postEvent Method Description:update the event
	 * 
	 * @param postEventInDto
	 * @param postEventOutDto
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void postEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto) {
		log.debug("Entering method PostEvent in CaseMaintenanceEventUpdateServiceImpl");
		updateEvent(postEventInDto, postEventOutDto);
		if (postEventOutDto != null) {
			updateEventPersonLink(postEventInDto, postEventOutDto);
			if (postEventOutDto != null) {
				if (ServiceConstants.REQ_FUNC_CD_DELETE != postEventInDto.getReqFuncCd()) {
					fetchEvent(postEventInDto, postEventOutDto);
					if (postEventOutDto != null) {

					}
				}

			}

		}

		log.debug("Exiting method PostEvent in CaseMaintenanceEventUpdateServiceImpl");
	}

	/**
	 * Method Name: fetchEvent Method Description: fetch Event detail DAM:
	 * CCMN45D
	 * 
	 * @param postEventInDto
	 * @param postEventOutDto
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void fetchEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto) {
		log.debug("Entering method fetchEvent in CaseMaintenanceEventUpdateServiceImpl");
		GetEventInDto getEventInDto = new GetEventInDto();
		GetEventOutDto getEventOutDto = new GetEventOutDto();

		if (ServiceConstants.REQ_FUNC_CD_ADD == postEventInDto.getReqFuncCd()) {
			getEventInDto.setIdEvent(postEventOutDto.getIdEvent());
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE == postEventInDto.getReqFuncCd()) {
			getEventInDto.setIdEvent(postEventInDto.getROWCCMN01UIG00().getIdEvent());
		}

		fetchTaskEventDao.fetchTaskEvent(getEventInDto, getEventOutDto);
		if (getEventOutDto != null) {

		}

		log.debug("Exiting method fetchEvent in CaseMaintenanceEventUpdateServiceImpl");
	}

	/**
	 * Method Name:updateEvent Method Description:update event table DAM:
	 * CCMN46D
	 * 
	 * @param postEventInDto
	 * @param postEventOutDto
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto) {
		log.debug("Entering method updateEvent in CaseMaintenanceEventUpdateServiceImpl");
		UpdateEventInDto updateEventInDto = new UpdateEventInDto();
		UpdateEventOutDto updateEventOutDto = new UpdateEventOutDto();

		updateEventInDto.setReqFuncCd(postEventInDto.getReqFuncCd());
		updateEventInDto.setCdEventType(postEventInDto.getROWCCMN01UIG00().getCdEventType());
		updateEventInDto.setIdEvent(postEventInDto.getROWCCMN01UIG00().getIdEvent());
		updateEventInDto.setIdStage(postEventInDto.getROWCCMN01UIG00().getIdStage());
		updateEventInDto.setIdPerson(postEventInDto.getROWCCMN01UIG00().getIdPerson());
		updateEventInDto.setCdTask(postEventInDto.getROWCCMN01UIG00().getCdTask());
		updateEventInDto.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr());
		updateEventInDto.setCdEventStatus(postEventInDto.getROWCCMN01UIG00().getCdEventStatus());
		caseMaintenanceInsertEventDao.updateEvent(updateEventInDto, updateEventOutDto);
		if (updateEventOutDto != null) {
			if (postEventInDto.getReqFuncCd() == ServiceConstants.REQ_FUNC_CD_ADD) {
				postEventOutDto.setIdEvent(updateEventOutDto.getIdEvent());
				postEventInDto.getROWCCMN01UIG00().setIdEvent((int) updateEventOutDto.getIdEvent());
			} else if (postEventInDto.getReqFuncCd() == ServiceConstants.REQ_FUNC_CD_UPDATE) {
				postEventOutDto.setIdEvent(postEventInDto.getROWCCMN01UIG00().getIdEvent());
			}

		}

		log.debug("Exiting method updateEvent in CaseMaintenanceEventUpdateServiceImpl");
	}

	/**
	 * Method Name: updateEventPersonLink Method Description:update
	 * EventPersonLink table DAM: CCMN68D
	 * 
	 * @param postEventInDto
	 * @param postEventOutDto
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateEventPersonLink(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto) {
		log.debug("Entering method updateEventPersonLink in CaseMaintenanceEventUpdateServiceImpl");
		short i = 0;
		EventPersonLinkUpdateInDto eventPersonLinkUpdateInDto = new EventPersonLinkUpdateInDto();
		EventPersonLinkUpdateOutDto eventPersonLinkUpdateOutDto = new EventPersonLinkUpdateOutDto();

		for (i = 0; i < postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos().length; i++) {
			if (!TypeConvUtil.isNullOrEmpty(postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos()[i])
					&& postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos()[i].getIdPerson() != 0) {
				eventPersonLinkUpdateInDto.setUlIdEvent(postEventInDto.getROWCCMN01UIG00().getIdEvent());
				eventPersonLinkUpdateInDto
						.setUlIdPerson(postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos()[i].getIdPerson());
				eventPersonLinkUpdateInDto.setSzCdScrDataAction(
						postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos()[i].getCdScrDataAction());
				eventPersonLinkUpdateInDto
						.setReqFuncCd(postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos()[i].getCdScrDataAction());
				caseMaintenanceUpdateEventPersonLinkDao.updateEventPersonLink(eventPersonLinkUpdateInDto,
						eventPersonLinkUpdateOutDto);
			} else {
				i = (short) postEventInDto.getROWCCMN01UIG00().getRowPersonInDtos().length;
			}

		}

		log.debug("Exiting method updateEventPersonLink in CaseMaintenanceEventUpdateServiceImpl");
	}

	/**
	 * Method Name: eventAUD Method Description:This common function is called
	 * to update the event table and the Event Person link table. Rows can be
	 * added, updated or deleted from the event table, while the Event Person
	 * Link table, you can only add and delete. Service: Ccmn01u
	 * 
	 * @param postEventInDto
	 * @return PostEventOutDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PostEventOutDto eventAUD(PostEventInDto postEventInDto) {
		log.debug("Entering method eventAUD in CaseMaintenanceEventUpdateServiceImpl");
		PostEventOutDto postEventOutDto = new PostEventOutDto();
		postEvent(postEventInDto, postEventOutDto);

		log.debug("Exiting method eventAUD in CaseMaintenanceEventUpdateServiceImpl");
		return postEventOutDto;
	}

}
