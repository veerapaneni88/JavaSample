package us.tx.state.dfps.service.medicalconsenter.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EventPersonLinkProcessDao;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dao.FetchToDoDao;
import us.tx.state.dfps.service.admin.dao.TodoCreateDao;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.admin.dto.EventOutputDto;
import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.EventStageDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalConsenterService;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalConsenterServiceImpl Oct 31, 2017- 4:06:54 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class MedicalConsenterServiceImpl implements MedicalConsenterService {

	@Autowired
	private MedicalConsenterDao medicalConsenterDao;

	@Autowired
	private EventProcessDao eventProcessDao;

	@Autowired

	private FetchToDoDao fetchToDoDao;

	@Autowired
	private EventPersonLinkProcessDao eventPersonLinkProcessDao;

	@Autowired
	private TodoCreateDao todoCreateDao;
	
	@Autowired
	private EventDao eventDao;

	private static final Logger log = Logger.getLogger(MedicalConsenterServiceImpl.class);

	/**
	 * Method Name: saveMedicalConsenterDetail Method Description:Saves the
	 * Medical Consenter data to the database
	 * 
	 * @param medConsBean
	 * @param dto
	 * @return MedicalConsenterDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto saveMedicalConsenterDetail(MedicalConsenterDto medConsBean,
			EventInputDto eventInputDto) {
		log.debug("Entering method saveMedicalConsenterDetail in MedicalConsenterService");
		{
			if (TypeConvUtil.isNullOrEmpty(eventInputDto.getArchInputStruct())) {
				eventInputDto.setArchInputStruct(new ServiceInputDto());
				eventInputDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			Long primaryChildId = ServiceConstants.ZERO_VAL;
			if (medConsBean.getIdPerson() <= ServiceConstants.Zero) {
				primaryChildId = medicalConsenterDao.selectPersonIdFromDao(medConsBean);
				medConsBean.setIdPerson(primaryChildId);
			}
			if (!ObjectUtils.isEmpty(medConsBean.getIdEvent()) && medConsBean.getIdEvent() > ServiceConstants.Zero) {
				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getArchInputStruct()) && !eventInputDto
						.getArchInputStruct().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					eventInputDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
			} else {
				Long newEventId = createEvent(eventInputDto);
				// Modified the code to fetch the event details from
				// getEventByid method in EventDao - Warranty defect 11751
				EventDto eventDto = eventDao.getEventByid(newEventId);
				EventLinkInDto eventLinkInDto = new EventLinkInDto();
				eventLinkInDto.setArchInputStruct(new ServiceInputDto());
				eventLinkInDto.setIdEvent(newEventId);
				eventLinkInDto.setIdPerson(medConsBean.getIdPerson());
				eventLinkInDto.getArchInputStruct().setCreqFuncCd(eventInputDto.getArchInputStruct().getCreqFuncCd());				
				eventLinkInDto.setDtLastUpdate(
						(!ObjectUtils.isEmpty(eventDto.getDtLastUpdate())) ? eventDto.getDtLastUpdate() : new Date());
				eventPersonLinkProcessDao.ccmn68dAUDdam(eventLinkInDto);
				medConsBean.setIdEvent(newEventId);
			}
			if (checkIntegrityConstraints(medConsBean)) {
				if (eventInputDto.getArchInputStruct().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					medConsBean = medicalConsenterDao.updateMedicalConsenterDetail(medConsBean);
				} else if (eventInputDto.getArchInputStruct().getCreqFuncCd()
						.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					medConsBean = medicalConsenterDao.addMedicalConsenterDetail(medConsBean);
					String stageType = ServiceConstants.EMPTY_STRING;
					stageType = getStageType(medConsBean.getIdStage());
					if (stageType.equals(ServiceConstants.SUBCARE)
							&& medConsBean.getIndCourtAuth().equals(ServiceConstants.NO)) {
						if (!checkAlertTodoExists(medConsBean.getIdStage(), medConsBean.getIdCase(), "A",
								ServiceConstants.MEDICAL_ALERT)) {
							FetchToDodiDto fetchToDodiDto = new FetchToDodiDto();
							FetchToDoDto fetchToDoDto = new FetchToDoDto();
							fetchToDodiDto.setCdTodoInfo(ServiceConstants.MEDICAL_ALERT);
							fetchToDoDto = fetchAlertTodoInfo(fetchToDodiDto);
							EventStageDiDto eventStageDiDto = new EventStageDiDto();
							if (medicalConsenterDao
									.getPrimaryWorker(medConsBean.getIdStage()) > ServiceConstants.Zero) {
								eventStageDiDto = populateCCMN43DI(eventStageDiDto, fetchToDoDto, medConsBean);
								createAlertTodo(eventStageDiDto);
							}
						}
					}
				}
			}
			log.debug("Exiting method saveMedicalConsenterDetail in MedicalConsenterService");
			return medConsBean;
		}

	}

	/**
	 * Method Name: selectPersonId Method Description:Select personId from case
	 * and stage ids in database
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto selectPersonId(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method selectPersonId in MedicalConsenterService");
		{
			Long primaryChildId = ServiceConstants.ZERO_VAL;
			primaryChildId = medicalConsenterDao.selectPersonIdFromDao(medicalConsenterDto);
			if (primaryChildId > ServiceConstants.Zero) {
				medicalConsenterDto.setIdPerson(primaryChildId);
			}
			log.debug("Exiting method selectPersonId in MedicalConsenterService");
			return medicalConsenterDto;
		}

	}

	/**
	 * Method Name: queryMedicalConsenterList Method Description:Select list of
	 * medical consenter detail from case and stage ids in database.
	 * 
	 * @param MedicalConsenterDto
	 * @return List<MedicalConsenterDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<MedicalConsenterDto> queryMedicalConsenterList(MedicalConsenterDto MedicalConsenterDto) {
		log.debug("Entering method queryMedicalConsenterList in MedicalConsenterService");
		{

			List<MedicalConsenterDto> mcdList = new ArrayList<MedicalConsenterDto>();
			mcdList = medicalConsenterDao.queryMedicalConsenterList(MedicalConsenterDto.getIdCase(),
					MedicalConsenterDto.getIdStage());
			log.debug("Exiting method queryMedicalConsenterRecord in MedicalConsenterService");
			return mcdList;
		}
	}

	/**
	 * Method Name: queryMedicalConsenterRecord Method Description:Select a
	 * medical consenter detail for the given primary id in database.
	 * 
	 * @param idMedCons
	 * @return MedicalConsenterDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto queryMedicalConsenterRecord(Long idMedCons) {
		log.debug("Entering method queryMedicalConsenterRecord in MedicalConsenterService");
		{
			MedicalConsenterDto medicalConsenterDto = new MedicalConsenterDto();
			medicalConsenterDto = medicalConsenterDao.queryMedicalConsenterRecord(idMedCons);
			log.debug("Exiting method queryMedicalConsenterRecord in MedicalConsenterService");
			return medicalConsenterDto;
		}
	}

	/**
	 * Method Name: createEvent Method Description:Create new event for Medical
	 * Consenter
	 * 
	 * @param eventInputDto
	 * @return Long
	 */
	private Long createEvent(EventInputDto eventInputDto) {
		EventOutputDto eventOutputDto = new EventOutputDto();
		eventInputDto.setReqFunctionCd(eventInputDto.getArchInputStruct().getCreqFuncCd());
		eventOutputDto = eventProcessDao.ccmn46dAUDdam(eventInputDto);
		return eventOutputDto.getIdEvent();
	}

	/**
	 * Method Name: checkIntegrityConstraints Method Description:Check to make
	 * sure Stage Id, Person Id and Case Id are not null before saving new
	 * record into database
	 * 
	 * @param medConsBean
	 * @return Boolean
	 */
	private Boolean checkIntegrityConstraints(MedicalConsenterDto medConsBean) {
		Boolean isValid = ServiceConstants.FALSEVAL;
		if ((medConsBean.getIdStage() > ServiceConstants.Zero) && (medConsBean.getIdPerson() > ServiceConstants.Zero)
				&& (medConsBean.getIdCase() > ServiceConstants.Zero)
				&& (medConsBean.getIdMedConsenterPerson() > ServiceConstants.Zero)) {
			isValid = ServiceConstants.TRUEVAL;
		}
		return isValid;
	}

	/**
	 * Method Name: endDateRecordType Method Description:End Date Type of
	 * another record before saving new Medical Consenter
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto endDateRecordType(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method endDateRecordType in MedicalConsenterService");
		{
			Long primaryChildId = ServiceConstants.ZERO_VAL;
			if (medicalConsenterDto.getIdPerson() <= ServiceConstants.Zero) {
				primaryChildId = medicalConsenterDao.selectPersonIdFromDao(medicalConsenterDto);
				medicalConsenterDto.setIdPerson(primaryChildId);
			}
			medicalConsenterDao.updateEndDateRecordType(medicalConsenterDto);
			log.debug("Exiting method endDateRecordType in MedicalConsenterService");
			return medicalConsenterDto;
		}
	}

	/**
	 * Method Name: updateMedicalConsenterEndDate Method Description:Update the
	 * Medical Consenter end date to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto updateMedicalConsenterEndDate(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method updateMedicalConsenterEndDate in MedicalConsenterService");
		{
			Long primaryChildId = ServiceConstants.ZERO_VAL;
			if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdPerson())
					&& medicalConsenterDto.getIdPerson() <= ServiceConstants.Zero) {
				primaryChildId = medicalConsenterDao.selectPersonIdFromDao(medicalConsenterDto);
				medicalConsenterDto.setIdPerson(primaryChildId);
			}
			if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdPerson())
					&& medicalConsenterDto.getIdPerson() > ServiceConstants.Zero) {
				medicalConsenterDao.updateEndDate(medicalConsenterDto);
			}
		}
		log.debug("Exiting method updateMedicalConsenterEndDate in MedicalConsenterService");
		return medicalConsenterDto;
	}

	/**
	 * Method Name: checkDfpsStaff Method Description: Check if a Medical
	 * Consenter is a DFPS Staff
	 * 
	 * @param staffId
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkDfpsStaff(Long staffId) {
		log.debug("Entering method checkDfpsStaff in MedicalConsenterService");
		{

			Boolean isStaff = ServiceConstants.FALSEVAL;

			isStaff = medicalConsenterDao.isDFPSStaff(staffId);

			log.debug("Exiting method checkDfpsStaff in MedicalConsenterService");
			return isStaff;
		}

	}

	/**
	 * Method Name: checkMedicalConsenterStatus Method Description:check Medical
	 * Consenter Status from database.
	 * 
	 * @param medicalConsenterDto
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkMedicalConsenterStatus(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method checkMedicalConsenterStatus in MedicalConsenterService");
		{

			Boolean status = ServiceConstants.FALSEVAL;
			Long primaryChildId = ServiceConstants.ZERO_VAL;
			if (TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdPerson())) {
				primaryChildId = medicalConsenterDao.selectPersonIdFromDao(medicalConsenterDto);
				if (primaryChildId > ServiceConstants.Zero) {
					medicalConsenterDto.setIdPerson(primaryChildId);
				} else if (primaryChildId == ServiceConstants.ZERO_VAL) {
					status = ServiceConstants.TRUEVAL;
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdPerson())) {
				List<MedicalConsenterDto> medConsList = medicalConsenterDao
						.queryMedicalConsenterList(medicalConsenterDto.getIdCase(), medicalConsenterDto.getIdStage());
				if (!TypeConvUtil.isNullOrEmpty(medConsList)) {
					for (MedicalConsenterDto mcdBean : medConsList) {
						if ((TypeConvUtil.isNullOrEmpty(mcdBean.getDtMedConsEnd())
								|| (DateUtils.isAfter(mcdBean.getDtMedConsEnd(), new Date())))) {
							status = ServiceConstants.TRUEVAL;
							break;
						}
					}
				}
			}
			log.debug("Exiting method checkMedicalConsenterStatus in MedicalConsenterService");
			return status;
		}

	}

	/**
	 * Method Name: isPersonMedicalConsenter Method Description:check if the
	 * person is in Medical Consenter table.
	 * 
	 * @param szIdPerson
	 * @param szIdStage
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPersonMedicalConsenter(String szIdPerson, String szIdStage) {
		log.debug("Entering method isPersonMedicalConsenter in MedicalConsenterService");
		{

			Boolean isMedCons = ServiceConstants.FALSEVAL;
			isMedCons = medicalConsenterDao.isPersonMedicalConsenter(szIdPerson, szIdStage);
			log.debug("Exiting method isPersonMedicalConsenter in MedicalConsenterService");
			return isMedCons;
		}

	}

	/**
	 * Method Name: getMedicalConsenterIdForEvent Method Description:Get the
	 * medical consenter id based on the medical consenter creation event id.
	 * 
	 * @param ulIdEvent
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getMedicalConsenterIdForEvent(Long ulIdEvent) {
		log.debug("Entering method getMedicalConsenterIdForEvent in MedicalConsenterService");
		{
			Long ulIdMedCons = ServiceConstants.ZERO_VAL;
			ulIdMedCons = medicalConsenterDao.getMedicalConsenterIdForEvent(ulIdEvent);
			log.debug("Exiting method getMedicalConsenterIdForEvent in MedicalConsenterService");
			return ulIdMedCons;
		}
	}

	/**
	 * Method Name: isPersonMedicalConsenterType Method Description:check if the
	 * person is already Medical Consenter type.
	 * 
	 * @param idPerson
	 * @param IdCase
	 * @param idChild
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long isPersonMedicalConsenterType(Long idPerson, Long idCase, Long idChild) {
		log.debug("Entering method isPersonMedicalConsenterType in MedicalConsenterService");
		{
			Long ulIdEvent = ServiceConstants.ZERO_VAL;
			ulIdEvent = medicalConsenterDao.isPersonMedicalConsenterType(idPerson, idCase, idChild);
			log.debug("Exiting method isPersonMedicalConsenterType in MedicalConsenterService");
			return ulIdEvent;
		}
	}

	/**
	 * Method Name: updateMedicalConsenterRecord Method Description:Update the
	 * Medical Consenter Record.
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateMedicalConsenterRecord(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method updateMedicalConsenterRecord in MedicalConsenterService");
		{
			Long consenterId = ServiceConstants.ZERO_VAL;
			if (medicalConsenterDto.getIdPerson() <= ServiceConstants.Zero) {
				consenterId = medicalConsenterDao.selectPersonIdFromDao(medicalConsenterDto);
				medicalConsenterDto.setIdPerson(consenterId);
			}
			if (medicalConsenterDto.getIdPerson() > ServiceConstants.Zero) {
				medicalConsenterDao.updateEndDateConsenterRecord(medicalConsenterDto);
			}
			if (medicalConsenterDto.getIdEvent() > ServiceConstants.Zero) {
				medicalConsenterDao.updateEndDateConsenterRecord(medicalConsenterDto);
			}
			log.debug("Exiting method updateMedicalConsenterRecord in MedicalConsenterService");
			return medicalConsenterDto.getIdPerson();
		}

	}

	/**
	 * Method Name: getPrimaryChild Method Description:Get Primary Child given
	 * stage id and Case id
	 * 
	 * @param IdCase
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getPrimaryChild(Long idCase, Long idStage) {
		log.debug("Entering method getPrimaryChild in MedicalConsenterService");
		{
			Long idPerson = ServiceConstants.ZERO_VAL;
			MedicalConsenterDto medBean = new MedicalConsenterDto();
			medBean.setIdCase(idCase);
			medBean.setIdStage(idStage);
			idPerson = medicalConsenterDao.selectPersonIdFromDao(medBean);
			log.debug("Exiting method getPrimaryChild in MedicalConsenterService");
			return idPerson;
		}
	}

	/**
	 * Method Name: getStageType Method Description:Method retrieves the type of
	 * input stage.
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getStageType(Long idStage) {
		log.debug("Entering method getStageType in MedicalConsenterService");
		String stageType = ServiceConstants.EMPTY_STRING;
		stageType = medicalConsenterDao.getStageType(idStage);
		return stageType;
	}

	/**
	 * Method Name: getCorrespStage Method Description:If Input stage is SUB/ADO
	 * the method retrieves corresponding ADO/SUB stage id from stage_link
	 * table.
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getCorrespStage(Long idStage) {
		log.debug("Entering method getCorrespStage in MedicalConsenterService");
		MedicalConsenterDto medicalConsenterDto = new MedicalConsenterDto();
		medicalConsenterDto.setIdStage(idStage);
		idStage = medicalConsenterDao.getCorrespStage(medicalConsenterDto);
		log.debug("Exiting method getCorrespStage in MedicalConsenterService");
		return idStage;
	}

	/**
	 * Method Name: personAddrExists Method Description:This implements two
	 * logic 1. Checks if the person in the Medical Censenter have atleast one
	 * Zip code in the associated addresses and 2.if the peron have atleast one
	 * assoicated address.
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean personAddrExists(Long idPerson) {
		log.debug("Entering method personAddrExists in MedicalConsenterService");
		Boolean bAddrOrZipCodeNull = ServiceConstants.FALSEVAL;
		bAddrOrZipCodeNull = medicalConsenterDao.hasPersonAddrZip(idPerson);
		log.debug("Exiting method personAddrExists in MedicalConsenterService");
		return bAddrOrZipCodeNull;
	}

	/**
	 * Method Name: checkAlertTodoExists Method Description:Method to check if
	 * Medical Consenter Alert Exists for stage
	 * 
	 * @param idStage
	 * @param IdCase
	 * @param cdTodoType
	 * @param txtTodoDesc
	 * @return Boolean
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkAlertTodoExists(Long idStage, Long idCase, String cdTodoType, String txtTodoDesc) {
		log.debug("Entering method checkAlertTodoExists in MedicalConsenterService");
		Boolean indAlertExists = ServiceConstants.FALSEVAL;
		indAlertExists = medicalConsenterDao.indToDoExists(idStage, idCase, cdTodoType, txtTodoDesc);
		log.debug("Exiting method checkAlertTodoExists in MedicalConsenterService");
		return indAlertExists;
	}

	/**
	 * Method Name: getPrimaryWorker Method Description:Fetch Primary Worker for
	 * given stage only when stage is active.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getPrimaryWorker(Long idStage) {
		log.debug("Entering method getPrimaryWorker in MedicalConsenterService");
		{
			Long ulIdPrimaryWorker = ServiceConstants.ZERO_VAL;
			ulIdPrimaryWorker = medicalConsenterDao.getPrimaryWorker(idStage);
			log.debug("Exiting method getPrimaryWorker in MedicalConsenterService");
			return ulIdPrimaryWorker;
		}
	}

	/**
	 * Method Name: fetchAlertTodoInfo Method Description: Private method to
	 * call CSES08dDao to fetch Alert Todo Info
	 * 
	 * @param fetchToDodiDto
	 * @return FetchToDoDto
	 */
	private FetchToDoDto fetchAlertTodoInfo(FetchToDodiDto fetchToDodiDto) {

		FetchToDoDto fetchToDoDto = new FetchToDoDto();
		ServiceInputDto ServiceInputDto = new ServiceInputDto();
		fetchToDodiDto.setServiceInputDto((ServiceInputDto) ServiceInputDto);
		List<FetchToDoDto> fetchToDoDtoList = fetchToDoDao.getTodoInfo(fetchToDodiDto);
		fetchToDoDto = fetchToDoDtoList.get(ServiceConstants.Zero);
		return fetchToDoDto;
	}

	/**
	 * Method Name: createAlertTodo Method Description: Private method to call
	 * CCMN43dDao to create Alert ToDo
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto
	 */
	private EventStageDoDto createAlertTodo(EventStageDiDto eventStageDiDto) {

		EventStageDoDto eventStageDoDto = new EventStageDoDto();
		eventStageDoDto = todoCreateDao.audToDo(eventStageDiDto);
		return eventStageDoDto;
	}

	/**
	 * Method Name: populateCCMN43DI Method Description: Private method to
	 * populate the input object of CCMN43dDao
	 * 
	 * @param eventStageDiDto
	 * @param fetchToDoDto
	 * @param medConsBean
	 * @return EventStageDiDto
	 */
	private EventStageDiDto populateCCMN43DI(EventStageDiDto eventStageDiDto, FetchToDoDto fetchToDoDto,
			MedicalConsenterDto medConsBean) {
		ServiceInputDto ServiceInputDto = new ServiceInputDto();
		eventStageDiDto.setServiceInputDto((ServiceInputDto) ServiceInputDto);
		eventStageDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		if (getPrimaryWorker(medConsBean.getIdStage()) > ServiceConstants.Zero) {
			eventStageDiDto.setUlIdTodoPersAssigned(getPrimaryWorker(medConsBean.getIdStage()));
		}
		eventStageDiDto.setUlIdStage(medConsBean.getIdStage());
		eventStageDiDto.setUlIdCase(medConsBean.getIdCase());
		eventStageDiDto.setUlIdTodoPersWorker(medConsBean.getIdUser());
		eventStageDiDto.setSzCdTodoTask(ServiceConstants.EMPTY_STRING);
		eventStageDiDto.setSzCdTodoType(fetchToDoDto.getCdTodoInfoType());
		eventStageDiDto.setSzTxtTodoDesc(fetchToDoDto.getTxtTodoInfoDesc());
		eventStageDiDto.setDtDtTodoDue(new Date());
		eventStageDiDto.setDtDtTaskDue(null);
		eventStageDiDto.setDtDtTodoCreated(DateUtils.toCastorDate(new Date()));
		eventStageDiDto.setUlIdTodoPersCreator(medConsBean.getIdUser());
		eventStageDiDto.setDtDtTodoCompleted(null);
		eventStageDiDto.setUlIdEvent(medConsBean.getIdEvent());
		return eventStageDiDto;
	}

	/**
	 * Method Name: checkPrimBackExists Method Description:Method returns true
	 * if Primary child for the given stage has atleast one Primary and one
	 * Backup MC's that are court authorized.
	 * 
	 * @param idStage
	 * @param idChild
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkPrimBackExists(Long idStage, Long idChild) {
		log.debug("Entering method checkPrimBackExists in MedicalConsenterService");
		Boolean indPrimBackExists = ServiceConstants.FALSEVAL;
		indPrimBackExists = medicalConsenterDao.checkMCPBExist(idStage, idChild);
		log.debug("Exiting method checkPrimBackExists in MedicalConsenterService");
		return indPrimBackExists;
	}

	/**
	 * Method Name: checkMCCourtAuth Method Description:Method returns true if
	 * the Primary Child has atleast one Medical Consenter either in
	 * SUBCARE/ADOPTION stage or in related ADOPTION/SUBCARE stage that is
	 * marked as court authorized(IND_CRT_AUTH is 'Y')
	 * 
	 * @param idStage
	 * @param idChild
	 * @return
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkMCCourtAuth(Long idStage, Long idChild) {
		log.debug("Entering method checkMCCourtAuth in MedicalConsenterService");
		{

			Boolean checkMCCourtAuth = ServiceConstants.FALSEVAL;
			Long ulIdRelatedStage = ServiceConstants.ZERO_VAL;
			if (idStage > ServiceConstants.Zero) {
				MedicalConsenterDto medBean = new MedicalConsenterDto();
				medBean.setIdStage(idStage);
				if (medicalConsenterDao.getCorrespStage(medBean) > ServiceConstants.Zero) {
					ulIdRelatedStage = medicalConsenterDao.getCorrespStage(medBean);
				}
			}
			checkMCCourtAuth = medicalConsenterDao.checkMCCourtAuth(idStage, ulIdRelatedStage, idChild);
			log.debug("Exiting method checkMCCourtAuth in MedicalConsenterService");
			return checkMCCourtAuth;
		}

	}

	/**
	 * Method Name: isActiveMedCons Method Description:isActiveMedCons gets
	 * count of active medical consenters for the person.
	 * 
	 * @param idPerson
	 * @return Boolean
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isActiveMedCons(Long idPerson) {
		log.debug("Entering method isActiveMedCons in MedicalConsenterService");

		Boolean hasActiveMedCons = ServiceConstants.FALSEVAL;
		hasActiveMedCons = medicalConsenterDao.isActiveMedCons(idPerson);
		log.debug("Exiting method isActiveMedCons in MedicalConsenterService");
		return hasActiveMedCons;

	}

	/**
	 * Method Name: updatePersonNameSfx Method Description:Method to get the
	 * Person Name Suffix
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterDto updatePersonNameSfx(MedicalConsenterDto medicalConsenterDto) {
		log.debug("Entering method updatePersonNameSfx in MedicalConsenterService");
		medicalConsenterDao.getPersonNameSfx(medicalConsenterDto);
		log.debug("Exiting method updatePersonNameSfx in MedicalConsenterService");
		return medicalConsenterDto;
	}

	/**
	 * 
	 * Method Name: audMdclConsenterFormLog Method Description: This method is
	 * to save Medical Consenter forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form launched using launch button in
	 * detail page, If Save and Complete button is clicked in Detail page update
	 * the status as 'COMP', If Delete button clicked delete the record.
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes audMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {
		CommonStringRes commonStringRes = new CommonStringRes();
		commonStringRes = medicalConsenterDao.audMdclConsenterFormLog(medicalConsenterFormLogReq);
		return commonStringRes;
	}

	/**
	 * 
	 * Method Name: getMdclConsenterFormLogList Method Description: This method
	 * is to get the list to display forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) associated with the Medical Consenters in the
	 * Medical Consenter List page
	 *
	 * @param medicalConsenterFormLogReq
	 * @return MedicalConsenterRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MedicalConsenterRes getMdclConsenterFormLogList(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes = medicalConsenterDao.getMdclConsenterFormLogList(medicalConsenterFormLogReq);
		return medicalConsenterRes;
	}

	@Override
	public CommonStringRes updateMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		return medicalConsenterDao.updateMdclConsenterFormLog(medicalConsenterFormLogReq);
	}
}
