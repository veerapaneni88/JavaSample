package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dao.EmergencyAssistDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dto.CpsDispositionDto;
import us.tx.state.dfps.service.admin.dto.CpsEmergencyAssistDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistOutDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeDto;
import us.tx.state.dfps.service.admin.dto.RetreiveEmergencyDto;
import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistoDto;
import us.tx.state.dfps.service.admin.service.RtrvEmergAssistService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service Impl
 * to retrieve information for the Emergency Assistance window. Aug 5,
 * 2017-3:08:13 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class RtrvEmergAssistServiceImpl implements RtrvEmergAssistService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventStagePersonLinkInsUpdDao objCcmn87dDao;

	@Autowired
	EmergencyAssistDao objCinv15dDao;

	@Autowired
	EventIdDao objCcmn45dDao;

	@Autowired
	CpsInvstDetailStageIdDao objCinv95dDao;

	private static final Logger log = Logger.getLogger(RtrvEmergAssistServiceImpl.class);

	/**
	 * 
	 * Method Name: callRtrvEmergAssistService Method Description:This method is
	 * used to fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<RtrvEmergAssistoDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RtrvEmergAssistoDto callRtrvEmergAssistService(RtrvEmergAssistiDto pInputMsg) {
		log.debug("Entering method callRtrvEmergAssistService in RtrvEmergAssistServiceImpl");
		List<EmergencyAssistOutDto> cinv15doDtos = null;
		List<CpsInvstDetailStageIdOutDto> cinv95doDtos = null;
		List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos = null;
		RtrvEmergAssistoDto assistoDtos = new RtrvEmergAssistoDto();
		ccmn87doDtos = getEventAndStageDtls(pInputMsg);
		if (!TypeConvUtil.isNullOrEmpty(ccmn87doDtos) && ccmn87doDtos.size() > 0) {
			assistoDtos = mapUIDEvent(ccmn87doDtos, assistoDtos);
		}
		if (pInputMsg.getIdEvent() != 0) {
			assistoDtos = getEventDetails(pInputMsg, assistoDtos);
			cinv15doDtos = getEmergencyAssistDtls(pInputMsg);
			if (!TypeConvUtil.isNullOrEmpty(cinv15doDtos) && cinv15doDtos.size() > 0) {
				if (cinv15doDtos.size() > 3) {
					assistoDtos = mapEmergencyAssistDetails(cinv15doDtos.subList(0, 3), assistoDtos);
				} else {
					assistoDtos = mapEmergencyAssistDetails(cinv15doDtos, assistoDtos);
				}
			}
		}
		cinv95doDtos = getInvstDtls(pInputMsg);
		if (!TypeConvUtil.isNullOrEmpty(cinv95doDtos) && cinv95doDtos.size() > 0) {
			mapInvstDtls(cinv95doDtos, assistoDtos);
		}
		// Set the Emergency Assistance Event Id from pInputImsg
		assistoDtos.setIdEvent(pInputMsg.getIdEvent());
		log.debug("Exiting method callRtrvEmergAssistService in RtrvEmergAssistServiceImpl");
		return assistoDtos;
	}

	/**
	 * 
	 * Method Name: getEventAndStageDtls Method Description:This method is used
	 * to fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStagePersonLinkInsUpdOutDto> getEventAndStageDtls(RtrvEmergAssistiDto pInputMsg) {
		log.debug("Entering method getEventAndStageDtls in RtrvEmergAssistServiceImpl");
		EventStagePersonLinkInsUpdInDto pCCMN87DInputRec = new EventStagePersonLinkInsUpdInDto();
		pCCMN87DInputRec.setIdStage(pInputMsg.getIdStage());
		// pCCMN87DInputRec.setSzCdEventType(ServiceConstants.CD_EVENT_TYPE_INV_CCL_TYPE);
		List<EventStageTypeDto> rowccmn87diDtoList = pCCMN87DInputRec.getROWCCMN87DI();
		if (rowccmn87diDtoList != null && rowccmn87diDtoList.size() > 0) {
			rowccmn87diDtoList.get(0).setCdEventType(ServiceConstants.CD_EVENT_TYPE_INV_CCL_TYPE);
		} else {
			if (rowccmn87diDtoList == null) {
				rowccmn87diDtoList = new ArrayList();
			}
			EventStageTypeDto rowccmn87diDto = new EventStageTypeDto();
			rowccmn87diDto.setCdEventType(ServiceConstants.CD_EVENT_TYPE_INV_CCL_TYPE);
			rowccmn87diDtoList.add(rowccmn87diDto);
		}
		pCCMN87DInputRec.setROWCCMN87DI(rowccmn87diDtoList);
		// CCMN87DI()
		// setSzCdEventType(ServiceConstants.CD_EVENT_TYPE_INV_CCL_TYPE);
		pCCMN87DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos = objCcmn87dDao.getEventAndStatusDtls(pCCMN87DInputRec);
		log.debug("Exiting method getEventAndStageDtls in RtrvEmergAssistServiceImpl");
		return ccmn87doDtos;
	}

	/**
	 * 
	 * Method Name: mapEmergencyAssistDetails Method Description: This method is
	 * used to fetch the questions based on the events from emergency assist.
	 * 
	 * @param cinv15doDtos
	 * @param assistoDtos
	 * @return List<RtrvEmergAssistoDto>
	 */
	private RtrvEmergAssistoDto mapEmergencyAssistDetails(List<EmergencyAssistOutDto> cinv15doDtos,
			RtrvEmergAssistoDto assistoDtos) {
		List<CpsEmergencyAssistDto> rowcinv11sog00DtoList = new ArrayList<CpsEmergencyAssistDto>();
		for (EmergencyAssistOutDto cinv15doDto : cinv15doDtos) {
			CpsEmergencyAssistDto rowcinv11sog00Dto = new CpsEmergencyAssistDto();
			if (!TypeConvUtil.isNullOrEmpty(cinv15doDto)) {
				rowcinv11sog00Dto.setCdEaQuestion(cinv15doDto.getCdEaQuestion());
				rowcinv11sog00Dto.setIdEmergencyAssist(cinv15doDto.getIdEmergencyAssist());
				rowcinv11sog00Dto.setIndEaResponse(cinv15doDto.getIndEaResponse());
				rowcinv11sog00Dto.setTsLastUpdate(cinv15doDto.getTsLastUpdate());
				rowcinv11sog00DtoList.add(rowcinv11sog00Dto);
			}
		}
		assistoDtos.setRowCinv11sog00Dto(rowcinv11sog00DtoList);
		return assistoDtos;
	}

	/**
	 * 
	 * Method Name: mapUIDEvent Method Description:This method is used to fetch
	 * the questions based on the events from emergency assist.
	 * 
	 * @param ccmn87doDtos
	 * @param assistoDtos
	 * @return List<RtrvEmergAssistoDto> @
	 */
	private RtrvEmergAssistoDto mapUIDEvent(List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos,
			RtrvEmergAssistoDto assistoDtos) {
		if (!ServiceConstants.EVENT_STATUS_PENDING.equalsIgnoreCase(ccmn87doDtos.get(0).getCdEventStatus())) {
			assistoDtos.setIdEvent(ccmn87doDtos.get(0).getIdEvent());
		} else {
			assistoDtos.setIdEvent(0L);
		}
		return assistoDtos;
	}

	/**
	 * 
	 * Method Name: getEmergencyAssistDtls Method Description: This method is
	 * used to fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<EmergencyAssistOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EmergencyAssistOutDto> getEmergencyAssistDtls(RtrvEmergAssistiDto pInputMsg) {
		log.debug("Entering method CallCINV15D in RtrvEmergAssistServiceImpl");
		EmergencyAssistInDto pCINV15DInputRec = new EmergencyAssistInDto();
		pCINV15DInputRec.setIdEvent(pInputMsg.getIdEvent());
		List<EmergencyAssistOutDto> cinv15doDtos = objCinv15dDao.fetchQues(pCINV15DInputRec);
		log.debug("Exiting method CallCINV15D in RtrvEmergAssistServiceImpl");
		return cinv15doDtos;
	}

	/**
	 * 
	 * Method Name: getEventDetails Method Description: This method is used to
	 * fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<EventIdOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RtrvEmergAssistoDto getEventDetails(RtrvEmergAssistiDto pInputMsg, RtrvEmergAssistoDto assistoDtos) {
		log.debug("Entering method CallCCMN45D in RtrvEmergAssistServiceImpl");
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
		List<EventIdOutDto> ccmn45doDtos = objCcmn45dDao.getEventDetailList(pCCMN45DInputRec);
		RetreiveEmergencyDto rowCcmn45doDto = new RetreiveEmergencyDto();
		rowCcmn45doDto.setCdEventType(ccmn45doDtos.get(0).getCdEventType());
		rowCcmn45doDto.setDtEventOccurred(ccmn45doDtos.get(0).getDtEventOccurred());
		rowCcmn45doDto.setIdEvent(ccmn45doDtos.get(0).getIdEvent());
		rowCcmn45doDto.setIdStage(ccmn45doDtos.get(0).getIdStage());
		rowCcmn45doDto.setIdPerson(ccmn45doDtos.get(0).getIdPerson());
		rowCcmn45doDto.setEventDescr(ccmn45doDtos.get(0).getEventDescr());
		rowCcmn45doDto.setCdTask(ccmn45doDtos.get(0).getCdTask());
		rowCcmn45doDto.setCdEventStatus(ccmn45doDtos.get(0).getCdEventStatus());
		rowCcmn45doDto.setTsLastUpdate(ccmn45doDtos.get(0).getTsLastUpdate());
		assistoDtos.setRowCcmn45doDto(rowCcmn45doDto);
		log.debug("Exiting method CallCCMN45D in RtrvEmergAssistServiceImpl");
		return assistoDtos;
	}

	/**
	 * 
	 * Method Name: getInvstDtls Method Description: This method is used to
	 * fetch the questions based on the events from emergency assist.
	 * 
	 * @param pInputMsg
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CpsInvstDetailStageIdOutDto> getInvstDtls(RtrvEmergAssistiDto pInputMsg) {
		log.debug("Entering method CallCINV95D in RtrvEmergAssistServiceImpl");
		CpsInvstDetailStageIdInDto pCINV95DInputRec = new CpsInvstDetailStageIdInDto();
		pCINV95DInputRec.setIdStage(pInputMsg.getIdStage());
		List<CpsInvstDetailStageIdOutDto> cinv95doDtos = objCinv95dDao.getInvstDtls(pCINV95DInputRec);
		log.debug("Exiting method CallCINV95D in RtrvEmergAssistServiceImpl");
		return cinv95doDtos;
	}

	/**
	 * 
	 * Method Name: mapInvstDtls Method Description: This method is used to
	 * fetch the questions based on the events from emergency assist.
	 * 
	 * @param cinv95doDtos
	 * @param assistoDtos
	 * @return List<RtrvEmergAssistoDto>
	 */
	private RtrvEmergAssistoDto mapInvstDtls(List<CpsInvstDetailStageIdOutDto> cinv95doDtos,
			RtrvEmergAssistoDto assistoDtos) {
		CpsDispositionDto rowCinv11sog01Dto = new CpsDispositionDto();
		rowCinv11sog01Dto.setIdEvent(cinv95doDtos.get(0).getIdEvent());
		rowCinv11sog01Dto.setCdCpsInvstDtlFamIncm(cinv95doDtos.get(0).getCdCpsInvstDtlFamIncm());
		rowCinv11sog01Dto.setCdCpsOverallDisptn(cinv95doDtos.get(0).getCdCpsOverallDisptn());
		rowCinv11sog01Dto.setIdStage(cinv95doDtos.get(0).getIdStage());
		rowCinv11sog01Dto.setIndCpsInvstEaConcl(cinv95doDtos.get(0).getIndCpsInvstEaConcl());
		rowCinv11sog01Dto.setIndCpsInvstEaConcl(cinv95doDtos.get(0).getIndCpsInvstSafetyPln());
		rowCinv11sog01Dto.setIndCpsInvstDtlRaNa(cinv95doDtos.get(0).getIndCpsInvstDtlRaNa());
		rowCinv11sog01Dto.setIndCpsInvstAbbrv(cinv95doDtos.get(0).getIndCpsInvstAbbrv());
		rowCinv11sog01Dto.setDtCPSInvstDtlAssigned(cinv95doDtos.get(0).getDtCPSInvstDtlAssigned());
		rowCinv11sog01Dto.setDtCPSInvstDtlBegun(cinv95doDtos.get(0).getDtCPSInvstDtlBegun());
		rowCinv11sog01Dto.setDtCpsInvstDtlComplt(cinv95doDtos.get(0).getDtCpsInvstDtlComplt());
		rowCinv11sog01Dto.setDtCPSInvstDtlIntake(cinv95doDtos.get(0).getDtCPSInvstDtlIntake());
		assistoDtos.setRowCinv11sog01Dto(rowCinv11sog01Dto);
		return assistoDtos;
	}
}
