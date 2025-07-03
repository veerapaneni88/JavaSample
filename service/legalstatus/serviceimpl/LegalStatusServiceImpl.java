package us.tx.state.dfps.service.legalstatus.serviceimpl;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.EmpSecClassLinkDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusRtrviDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusRtrvoDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.ResourcePlacementOutDto;
import us.tx.state.dfps.service.admin.dto.StageEventDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.LegalStatusUpdateReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.util.CodesTableViewLookupUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.legalstatus.service.LegalStatusService;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalConsenterService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation for LegalStatusRtrv Aug 20, 2017- 9:34:03 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class LegalStatusServiceImpl implements LegalStatusService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	WorkloadStgPerLinkSelDao workloadStgPerLinkSelDao;

	@Autowired
	PersonPortfolioDao personPortfolioDao;

	@Autowired
	EventIdDao eventIdDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	LegalStatusDao legalStatusDao;

	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	StageEventStatusCommonService stageEventStatusService;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	EventUpdEventStatusDao eventUpdateDao;

	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	@Autowired
	TodoCreateService todoCreateService;

	@Autowired
	EmpSecClassLinkDao empSecClassLinkDao;

	@Autowired
	EventInsUpdDelDao eventInsUpdDelDao;

	@Autowired
	PlacementService placementService;
	
	@Autowired
	MedicalConsenterDao medicalConsenterDao;
	
	@Autowired
	MedicalConsenterService medicalConsenterService;

	@Autowired
	private CodesTableViewLookupUtils codesTableViewLookupUtils;

	private static final Logger log = Logger.getLogger(LegalStatusServiceImpl.class);
	public static final String CLOSURE_EVENT_TYPE = "CCL";
	private static final Long FCE_ELIG_EVENT_ID = 0L;
	private static final String SUB_060_TODO_INFO_DESC = "Eligibility action needed due to a change in legal status effective ";
	private static final String SUB_065_TODO_INFO_DESC = "Send Dismissal Order for ";
	private static final String SUB_066_TODO_INFO_DESC = " to assigned Eligibility Specialist";
	private static final String PERIOD = ".";
	private static final String CONSERV_STATUS = "C";
	private static final String PRIV_AGENCY_ADPT_HOME = "71";
	private static final String FPS_FA_HOME = "70";
	private static final List<String> LEGAL_STATUS_LIST1 = Arrays.asList(CodesConstant.CLEGSTAT_090,
			CodesConstant.CLEGSTAT_100, CodesConstant.CLEGSTAT_120, CodesConstant.CLEGSTAT_150);
	private static final List<String> LEGAL_STATUS_LIST2 = Arrays.asList(CodesConstant.CLEGSTAT_010,
			CodesConstant.CLEGSTAT_020, CodesConstant.CLEGSTAT_030, CodesConstant.CLEGSTAT_040,
			CodesConstant.CLEGSTAT_050, CodesConstant.CLEGSTAT_060, CodesConstant.CLEGSTAT_070,
			CodesConstant.CLEGSTAT_080, CodesConstant.CLEGSTAT_130);

	/**
	 * 
	 * Method Name: getLegalStatusDetails Method Description: This is the
	 * retrieval service for the Legal Status
	 * 
	 * @param pInputMsg
	 * @return LegalStatusRtrvoDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalStatusRtrvoDto getLegalStatusDetails(LegalStatusRtrviDto pInputMsg) {
		log.info("Entering method getLegalStatusDetails in LegalStatusServiceImpl");
		LegalStatusRtrvoDto objLegalStatusRtrvoDto = new LegalStatusRtrvoDto();
		String retVal = ServiceConstants.FND_SUCCESS;
		LegalStatusInDto legalStatusInDto = new LegalStatusInDto();
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
		EventIdInDto eventIdInDto = new EventIdInDto();
		EventStagePersonLinkInsUpdInDto eventStagePerLinkDto = new EventStagePersonLinkInsUpdInDto();
		objLegalStatusRtrvoDto.setDtGenericSysdate(new Date());
		eventStagePerLinkDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventStagePerLinkDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventStagePerLinkDto.setIdStage(pInputMsg.getIdStage());
		// Get event type list
		List<EventStageTypeDto> eventStageTypeList = eventStagePerLinkDto.getROWCCMN87DI();
		if (CollectionUtils.isNotEmpty(eventStageTypeList)) {
			eventStageTypeList.get(0).setCdEventType(CLOSURE_EVENT_TYPE);
		} else {
			eventStageTypeList = new ArrayList<>();
			EventStageTypeDto eventStageTypeDto = new EventStageTypeDto();
			eventStageTypeDto.setCdEventType(CLOSURE_EVENT_TYPE);
			eventStageTypeList.add(eventStageTypeDto);
		}
		eventStagePerLinkDto.setROWCCMN87DI(eventStageTypeList);
		List<EventStagePersonLinkInsUpdOutDto> eventsStageOutDtoList = null;
		try {
			// get event status detail
			eventsStageOutDtoList = eventStagePersonLinkInsUpdDao.getEventAndStatusDtls(eventStagePerLinkDto);
			if (!TypeConvUtil.isNullOrEmpty(eventsStageOutDtoList)) {
				EventStagePersonLinkInsUpdOutDto eventStageOutDto = eventsStageOutDtoList.get(0);
				if (!TypeConvUtil.isNullOrEmpty(eventStageOutDto.getCdEventStatus())) {
					objLegalStatusRtrvoDto.setCdEventStatus(eventStageOutDto.getCdEventStatus());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventStageOutDto.getIdEvent())) {
					objLegalStatusRtrvoDto.setIdEvent(eventStageOutDto.getIdEvent());
				}
			}
		} catch (DataNotFoundException e) {
			objLegalStatusRtrvoDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_NEW);
		}
		// If input event id is 0 , and the stage is not SUB OR ADP, Get
		// workload list.
		if ((0 == pInputMsg.getIdEvent()) && ((!pInputMsg.getCdStage().equalsIgnoreCase(ServiceConstants.SUB_CARE))
				|| (!pInputMsg.getCdStage().equalsIgnoreCase(ServiceConstants.ADOPTION)))) {
			workloadStgPerLinkSelInDto.setIdStage(pInputMsg.getIdStage());
			workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
			List<WorkloadStgPerLinkSelOutDto> wkldStgPerLinkOutDtoList;
			// get workload list
			wkldStgPerLinkOutDtoList = workloadStgPerLinkSelDao.getWorkLoad(workloadStgPerLinkSelInDto);
			if (CollectionUtils.isNotEmpty(wkldStgPerLinkOutDtoList)) {
				LegalStatusDetailDto legalStatusDetailDto = new LegalStatusDetailDto();
				WorkloadStgPerLinkSelOutDto objCinv51doDto = wkldStgPerLinkOutDtoList.get(0);
				if (!TypeConvUtil.isNullOrEmpty(objCinv51doDto.getIdTodoPersAssigned())) {
					objLegalStatusRtrvoDto.setIdPerson(objCinv51doDto.getIdTodoPersAssigned());
				}
				if (!TypeConvUtil.isNullOrEmpty(objCinv51doDto.getIdTodoPersAssigned())) {
					legalStatusDetailDto.setIdPerson(objCinv51doDto.getIdTodoPersAssigned());
				}
				getPersonName(objLegalStatusRtrvoDto, legalStatusDetailDto.getIdPerson());
				objLegalStatusRtrvoDto.setLegalStatusDetailDto(legalStatusDetailDto);
			}
		} else if (0 != pInputMsg.getIdEvent()) {
			eventIdInDto.setIdEvent(pInputMsg.getIdEvent());
			List<EventIdOutDto> eventIdOutDtoList = null;
			try {
				// event id is not 0, get event list
				eventIdOutDtoList = eventIdDao.getEventDetailList(eventIdInDto);
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDtoList)) {
					EventIdOutDto eventIdOutDto = eventIdOutDtoList.get(0);
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdEventType())) {
						objLegalStatusRtrvoDto.setCdEventType(eventIdOutDto.getCdEventType());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getEventDescr())) {
						objLegalStatusRtrvoDto.setTxtEventDescr(eventIdOutDto.getEventDescr());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdTask())) {
						objLegalStatusRtrvoDto.setCdTask(eventIdOutDto.getCdTask());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdEventStatus())) {
						objLegalStatusRtrvoDto.setCdEventStatus(eventIdOutDto.getCdEventStatus());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdEvent())) {
						objLegalStatusRtrvoDto.setIdEvent(eventIdOutDto.getIdEvent());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdStage())) {
						objLegalStatusRtrvoDto.setIdStage(eventIdOutDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdPerson())) {
						objLegalStatusRtrvoDto.setIdEventPerson(eventIdOutDto.getIdPerson());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getDtEventOccurred())) {
						objLegalStatusRtrvoDto.setDtEventOccurred(eventIdOutDto.getDtEventOccurred());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getTsLastUpdate())) {
						ObjectMapper mapper = new ObjectMapper();
						try {
							objLegalStatusRtrvoDto
									.setLastUpdate(mapper.writeValueAsString(eventIdOutDto.getTsLastUpdate()));
						} catch (JsonProcessingException e) {
							objLegalStatusRtrvoDto.setLastUpdate(eventIdOutDto.getTsLastUpdate().toString());
						}
					}
					retVal = ServiceConstants.FND_SUCCESS;
				}
			} catch (DataNotFoundException e) {
				retVal = ServiceConstants.FND_FAIL;
			}
			// For new event, get workload detail
			if (ServiceConstants.STATUS_NEW.equalsIgnoreCase(objLegalStatusRtrvoDto.getCdEventStatus())
					&& (retVal.equals(ServiceConstants.FND_SUCCESS))) {
				if (ServiceConstants.SUB_CARE.equalsIgnoreCase(pInputMsg.getCdStage())
						|| (ServiceConstants.ADOPTION.equalsIgnoreCase(pInputMsg.getCdStage()))) {
					workloadStgPerLinkSelInDto.setIdStage(pInputMsg.getIdStage());
					workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
					List<WorkloadStgPerLinkSelOutDto> wkldStgPerLinkOutDtoList;
					wkldStgPerLinkOutDtoList = workloadStgPerLinkSelDao.getWorkLoad(workloadStgPerLinkSelInDto);
					if (!TypeConvUtil.isNullOrEmpty(wkldStgPerLinkOutDtoList)) {
						LegalStatusDetailDto legalStatusDetailDto = new LegalStatusDetailDto();
						WorkloadStgPerLinkSelOutDto objCinv51doDto = wkldStgPerLinkOutDtoList.get(0);
						if (!TypeConvUtil.isNullOrEmpty(objCinv51doDto.getIdTodoPersAssigned())) {
							objLegalStatusRtrvoDto.setIdPerson(objCinv51doDto.getIdTodoPersAssigned());
						}
						if (!TypeConvUtil.isNullOrEmpty(objCinv51doDto.getIdTodoPersAssigned())) {
							legalStatusDetailDto.setIdPerson(objCinv51doDto.getIdTodoPersAssigned());
						}
						getPersonName(objLegalStatusRtrvoDto, legalStatusDetailDto.getIdPerson());
						objLegalStatusRtrvoDto.setLegalStatusDetailDto(legalStatusDetailDto);
					}
				}
				// For new using mode
			} else if ((ServiceConstants.WINDOW_MODE_NEW_USING.equals(pInputMsg.getCsysIndDamCalled()))
					&& (retVal.equalsIgnoreCase(ServiceConstants.FND_SUCCESS))) {
				if ((ServiceConstants.SUB_CARE.equalsIgnoreCase(pInputMsg.getCdStage()))
						|| (ServiceConstants.ADOPTION.equals(pInputMsg.getCdStage()))) {
					legalStatusInDto.setIdLegalStatEvent(pInputMsg.getIdEvent());
					List<LegalStatusOutDto> legalStatusOutDtoList = null;
					// get event list
					legalStatusOutDtoList = legalStatusDao.getLegalStatusForEvent(legalStatusInDto);

					// ADS Change - 2.6.2.4 - Legal Status - Get Legal Status
					// Sub Type
					List<LegalStatusOutDto> legalStatusSubTypeDtoList = null;
					legalStatusSubTypeDtoList = legalStatusDao.getLegalStatusSubTypeForEvent(legalStatusInDto);
					if (legalStatusSubTypeDtoList.size() > ServiceConstants.Zero_Value) {
						LegalStatusOutDto legalStatusSubTypeDto = legalStatusSubTypeDtoList.get(ServiceConstants.Zero);
						objLegalStatusRtrvoDto.setCdLegalStatusSubType(legalStatusSubTypeDto.getCdLegalStatusSubType());
					}

					if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDtoList)) {
						LegalStatusOutDto legalStatusOutDto = legalStatusOutDtoList.get(ServiceConstants.Zero);
						LegalStatusDetailDto legalStatusDetailDto = mapLegalStatusDetailDto(legalStatusOutDto);
						workloadStgPerLinkSelInDto.setIdStage(pInputMsg.getIdStage());
						workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
						// Get workload for stage
						List<WorkloadStgPerLinkSelOutDto> wkldStgPerLinkOutDtoList;
						wkldStgPerLinkOutDtoList = workloadStgPerLinkSelDao.getWorkLoad(workloadStgPerLinkSelInDto);
						if (!TypeConvUtil.isNullOrEmpty(wkldStgPerLinkOutDtoList)) {
							if (!TypeConvUtil.isNullOrEmpty(wkldStgPerLinkOutDtoList.get(0).getIdTodoPersAssigned())) {
								objLegalStatusRtrvoDto
										.setIdPerson(wkldStgPerLinkOutDtoList.get(0).getIdTodoPersAssigned());
							}
							if (!TypeConvUtil.isNullOrEmpty(wkldStgPerLinkOutDtoList.get(0).getIdTodoPersAssigned())) {
								legalStatusDetailDto
										.setIdPerson(wkldStgPerLinkOutDtoList.get(0).getIdTodoPersAssigned());
							}
							getPersonName(objLegalStatusRtrvoDto, legalStatusDetailDto.getIdPerson());
						}

						objLegalStatusRtrvoDto.setLegalStatusDetailDto(legalStatusDetailDto);
						// ADS Change - 2.6.2.4 - Legal Status - Setting the
						// IndJmcPrntReltnshpKnsp Checkbox Value
						if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getIndJmcPrntReltnshpKnsp())) {
							if (legalStatusOutDto.getIndJmcPrntReltnshpKnsp().equals(ServiceConstants.YES)) {
								objLegalStatusRtrvoDto.setIndJmcPrntReltnshpKnsp(true);

							}
						}

					}
				} else {
					legalStatusInDto.setIdLegalStatEvent(pInputMsg.getIdEvent());
					List<LegalStatusOutDto> legalStatusOutDtoList = null;
					legalStatusOutDtoList = legalStatusDao.getLegalStatusForEvent(legalStatusInDto);
					LegalStatusOutDto legalStatusOutDto = legalStatusOutDtoList.get(ServiceConstants.Zero);

					// ADS Change - 2.6.2.4 - Legal Status - get Legal Status
					// Sub Type
					List<LegalStatusOutDto> legalStatusSubTypeDtoList = null;
					legalStatusSubTypeDtoList = legalStatusDao.getLegalStatusSubTypeForEvent(legalStatusInDto);
					if (legalStatusSubTypeDtoList.size() > ServiceConstants.Zero_Value) {
						LegalStatusOutDto legalStatusSubTypeDto = legalStatusSubTypeDtoList.get(ServiceConstants.Zero);
						objLegalStatusRtrvoDto.setCdLegalStatusSubType(legalStatusSubTypeDto.getCdLegalStatusSubType());
					}

					if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDtoList)) {

						if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDtoList.get(0).getIdPerson())) {
							objLegalStatusRtrvoDto.setIdPerson(legalStatusOutDtoList.get(0).getIdPerson());
							getPersonName(objLegalStatusRtrvoDto, objLegalStatusRtrvoDto.getIdPerson());
						}
						LegalStatusDetailDto legalStatusDetailDto = mapLegalStatusDetailDto(
								legalStatusOutDtoList.get(0));
						objLegalStatusRtrvoDto.setLegalStatusDetailDto(legalStatusDetailDto);

						// ADS Change - 2.6.2.4 - Legal Status - Setting the
						// IndJmcPrntReltnshpKnsp Checkbox Value
						if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getIndJmcPrntReltnshpKnsp())) {
							if (legalStatusOutDto.getIndJmcPrntReltnshpKnsp().equals(ServiceConstants.YES)) {
								objLegalStatusRtrvoDto.setIndJmcPrntReltnshpKnsp(true);
							}
						}

					}

				}
			} else {
				legalStatusInDto.setIdLegalStatEvent(pInputMsg.getIdEvent());
				List<LegalStatusOutDto> legalStatusOutDtoList = null;
				legalStatusOutDtoList = legalStatusDao.getLegalStatusForEvent(legalStatusInDto);
				LegalStatusOutDto legalStatusOutDto = legalStatusOutDtoList.get(ServiceConstants.Zero);

				// ADS Change - 2.6.2.4 - Legal Status - get Legal Status Sub
				// Type
				List<LegalStatusOutDto> legalStatusSubTypeDtoList = null;
				legalStatusSubTypeDtoList = legalStatusDao.getLegalStatusSubTypeForEvent(legalStatusInDto);
				if (legalStatusSubTypeDtoList.size() > ServiceConstants.Zero_Value) {
					LegalStatusOutDto legalStatusSubTypeDto = legalStatusSubTypeDtoList.get(ServiceConstants.Zero);
					objLegalStatusRtrvoDto.setCdLegalStatusSubType(legalStatusSubTypeDto.getCdLegalStatusSubType());
				}

				if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDtoList)) {
					if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDtoList.get(0).getIdPerson())) {
						objLegalStatusRtrvoDto.setIdPerson(legalStatusOutDtoList.get(0).getIdPerson());
					}
					LegalStatusDetailDto legalStatusDetailDto = mapLegalStatusDetailDto(legalStatusOutDtoList.get(0));
					getPersonName(objLegalStatusRtrvoDto, legalStatusDetailDto.getIdPerson());
					objLegalStatusRtrvoDto.setLegalStatusDetailDto(legalStatusDetailDto);

					// ADS Change - 2.6.2.4 - Legal Status - Setting the
					// IndJmcPrntReltnshpKnsp Checkbox Value
					if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getIndJmcPrntReltnshpKnsp())) {
						if (legalStatusOutDto.getIndJmcPrntReltnshpKnsp().equals(ServiceConstants.YES)) {
							objLegalStatusRtrvoDto.setIndJmcPrntReltnshpKnsp(true);
						}
					}

				}

			}
		}
		log.info("Exiting method getLegalStatusDetails in LegalStatusServiceImpl");
		return objLegalStatusRtrvoDto;
	}

	/**
	 * 
	 * Method Name: mapRowcsub45sog01Dto Method Description: Map
	 * Rowcsub45sog01Dto
	 * 
	 * @param legalStatusOutDto
	 * @return Rowcsub45sog01Dto
	 */
	private LegalStatusDetailDto mapLegalStatusDetailDto(LegalStatusOutDto legalStatusOutDto) {
		LegalStatusDetailDto legalStatusDetailDto = new LegalStatusDetailDto();
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getIdLegalStatEvent())) {
			legalStatusDetailDto.setIdLegalStatEvent(legalStatusOutDto.getIdLegalStatEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getIdPerson())) {
			legalStatusDetailDto.setIdPerson(legalStatusOutDto.getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getDtLegalStatusDt())) {
			legalStatusDetailDto.setDtLegalStatusDt(legalStatusOutDto.getDtLegalStatusDt());
			ObjectMapper mapper = new ObjectMapper();
			try {
				legalStatusDetailDto
						.setStrLegalStatusDt(mapper.writeValueAsString(legalStatusOutDto.getDtLegalStatusDt()));
			} catch (JsonProcessingException e) {
				legalStatusDetailDto.setStrLegalStatusDt(null);
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getTsLastUpdate())) {
			legalStatusDetailDto.setTsLastUpdate(legalStatusOutDto.getTsLastUpdate());

		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getCdLegalStatCnty())) {
			legalStatusDetailDto.setCdLegalStatCnty(legalStatusOutDto.getCdLegalStatCnty());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getCdLegalStatStatus())) {
			legalStatusDetailDto.setCdLegalStatStatus(legalStatusOutDto.getCdLegalStatStatus());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getTxtLegalStatCauseNbr())) {
			legalStatusDetailDto.setTxtLegalStatCauseNbr(legalStatusOutDto.getTxtLegalStatCauseNbr());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getTxtLegalStatCourtNbr())) {
			legalStatusDetailDto.setTxtLegalStatCourtNbr(legalStatusOutDto.getTxtLegalStatCourtNbr());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getCdCourtNbr())) {
			legalStatusDetailDto.setCdCourtNbr(legalStatusOutDto.getCdCourtNbr());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getDtLegalStatTMCDismiss())) {
			legalStatusDetailDto.setDtLegalStatTMCDismiss(legalStatusOutDto.getDtLegalStatTMCDismiss());
		}
		if (!TypeConvUtil.isNullOrEmpty(legalStatusOutDto.getCdLegStatDischargeRsn())) {
			legalStatusDetailDto.setCdLegStatDischargeRsn(legalStatusOutDto.getCdLegStatDischargeRsn());
		}

		return legalStatusDetailDto;
	}

	/**
	 * 
	 * Method Name: getPersonName Method Description: Fetches person name for
	 * personID. CCMN44D
	 * 
	 * @param objLegalStatusRtrvoDto
	 * @param idPerson
	 * @
	 */
	private void getPersonName(LegalStatusRtrvoDto objLegalStatusRtrvoDto, Long idPerson) {
		PersonPortfolioInDto personPortfolioInDto = new PersonPortfolioInDto();
		personPortfolioInDto.setIdPerson(idPerson);
		List<PersonPortfolioOutDto> personPortfolioOutDtoList = null;
		personPortfolioOutDtoList = personPortfolioDao.getPersonRecord(personPortfolioInDto);
		if (!TypeConvUtil.isNullOrEmpty(personPortfolioOutDtoList)
				&& !TypeConvUtil.isNullOrEmpty(personPortfolioOutDtoList.get(0).getNmPersonFull())) {
			objLegalStatusRtrvoDto.setNmPersonFull(personPortfolioOutDtoList.get(0).getNmPersonFull());
		}
	}
	
	private void checkForInvalidateApproval(LegalStatusUpdateReq legalStatusReq) {
		if ((!ObjectUtils.isEmpty(legalStatusReq.getIdEventApproval()))
				&& (legalStatusReq.getIdEventApproval() != 0)) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(legalStatusReq.getIdEventApproval());
			approvalService.callCcmn05uService(approvalCommonInDto);
		}
	}


	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getLegalStatusPersonDetail(Long idStage){
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
		workloadStgPerLinkSelInDto.setIdStage(idStage);
		workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
		return workloadStgPerLinkSelDao.getWorkLoad(workloadStgPerLinkSelInDto).get(0).getIdTodoPersAssigned();
	}

	/**
	 * 
	 * Method Name: updateLegalStatus Method Description: update legal status
	 * 
	 * @param legalStatusReq
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public int updateLegalStatus(LegalStatusUpdateReq legalStatusReq) {
		log.info("Entering method updateLegalStatus in LegalStatusServiceImpl");
		
		checkForInvalidateApproval(legalStatusReq);
		LegalStatusOutDto legalStatusOutDto = legalStatusReq.getLegalStatusOutDto();
		StageEventDto stageEventDto = legalStatusReq.getStageEventDto();
		Long idPerson = legalStatusOutDto.getIdPerson();
		String reqFuncCode = legalStatusReq.getReqFuncCd();
		Long idEvent = stageEventDto.getIdEvent();
		String alertFlag = null;
		if (idEvent == 0L) {
			alertFlag = ServiceConstants.TRUE;
		}
		// Alert for the Primary Assigned caseworker to complete the 2077
		// Referral within 7 days of when a child Placement is entered and saved
		if (!ObjectUtils.isEmpty(alertFlag) && alertFlag.equals(ServiceConstants.TRUE)) {
			AlertPlacementLsDto alertPlacementLsDto = new AlertPlacementLsDto();
			alertPlacementLsDto.setIdEvent(idEvent);
			alertPlacementLsDto.setIdStage(legalStatusReq.getIdStage());
			alertPlacementLsDto.setIdCase(legalStatusOutDto.getIdCase());
			alertPlacementLsDto.setCheckFlag(ServiceConstants.LEGAL_STATUS);
			alertPlacementLsDto.setIdPerson(stageEventDto.getIdPerson());
			if (!ObjectUtils.isEmpty(alertPlacementLsDto))
				placementService.alertPlacementReferral(alertPlacementLsDto);
		}
		PersonDto personDto = personDao.getPersonById(idPerson);
		Date dtPersonBirth = personDto.getDtPersonBirth();
		if (ObjectUtils.isEmpty(dtPersonBirth))
			dtPersonBirth = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtPersonBirth);
		calendar.add(Calendar.YEAR, 18);
		Date eighteenYear = calendar.getTime();
		// CSUB80D checks for duplicate legal statuses
		int legalStatusCount = legalStatusDao.getLegalStatusCount(idPerson, legalStatusOutDto.getDtLegalStatusDt());
		if (((0 != legalStatusCount) && (ServiceConstants.REQ_FUNC_CD_ADD.equals(reqFuncCode)))
				|| ((0 < legalStatusCount) && (ServiceConstants.YES.equals(legalStatusReq.getIndDateModified()))
						&& (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFuncCode)))) {
			return Messages.MSG_DUP_LEG_STAT_DATE;
		}

		if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(reqFuncCode)) {
			// CCMN44D Checks for Person's Date of birth

			if (legalStatusOutDto.getDtLegalStatusDt().after(eighteenYear)) {
				return Messages.MSG_LEG_CSRVT_OF_PER_PROH;
			}
			// CMSC54D checks for consecutive terminating legal statuses
			if (LEGAL_STATUS_LIST1.contains(legalStatusOutDto.getCdLegalStatStatus())) {
				List<String> legalStatusCodeList = legalStatusDao.getLegalStatusCode(idPerson,
						legalStatusOutDto.getIdLegalStatEvent(), legalStatusOutDto.getDtLegalStatusDt());
				if (CollectionUtils.isNotEmpty(legalStatusCodeList)) {
					for (String legalStatusCode : legalStatusCodeList) {
						if (LEGAL_STATUS_LIST1.contains(legalStatusCode)) {
							return Messages.MSG_DUP_TERM_LEG_STAT;
						}
					}
				} else {
					return Messages.MSG_CHD_FRST_TERM_LEG_STAT;
				}
			}

		}
		// CINT21 Dretrieve whether the stage is closed or not regardless of
		// window mode.
		StageDto stageDto = stageDao.getStageById(legalStatusReq.getIdStage());
		// Eff Date must be before or the same as the Stage Closure Date
		if (stageDto != null && stageDto.getDtStageClose() != null
				&& !DateUtils.isSameDay(legalStatusOutDto.getDtLegalStatusDt(),stageDto.getDtStageClose()) && !legalStatusOutDto.getDtLegalStatusDt().before(stageDto.getDtStageClose())) {
			return Messages.MSG_NO_LS_AFTER_STG_CLOSE;
		}
		// CCMN06U Check Stage/Event status
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		stageTaskInDto.setReqFuncCd(reqFuncCode);
		stageTaskInDto.setIdStage(legalStatusReq.getIdStage());
		stageTaskInDto.setCdTask(stageEventDto.getCdTask());
		String ccmn06uService = stageEventStatusService.checkStageEventStatus(stageTaskInDto);
		if (!TypeConvUtil.isNullOrEmpty(ccmn06uService)) {
			switch (ccmn06uService) {
			case ServiceConstants.MSG_SYS_STAGE_CLOSED:
				return Messages.MSG_SYS_STAGE_CLOSED;
			case ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH:
				return Messages.MSG_SYS_EVENT_STS_MSMTCH;
			case ServiceConstants.MSG_SYS_MULT_INST:
				return Messages.MSG_SYS_MULT_INST;
			default:
				break;
			}
		}

		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(reqFuncCode)
				|| ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFuncCode)) {
			if (!legalStatusReq.getSysNbrReserved1()
					&& CodesConstant.CEVTSTAT_PEND.equals(stageEventDto.getCdEventStatus())) {
				// if the closure event is pending then do not call post event
				// invalidate the approval.
				updatePendingEvent(legalStatusOutDto, stageEventDto);
			} else {
				// Post event common function
				// CCMN01U
				PostEventStageStatusInDto postEventStageStatusInDto = populateEventStageStatusInDto(stageEventDto);
				if (ObjectUtils.isEmpty(postEventStageStatusInDto.getIdEventPerson())) {
					postEventStageStatusInDto.setIdEventPerson(legalStatusOutDto.getIdPerson());
				}
				try {
					PostEventStageStatusOutDto postEventStageStatusOutDto = postEventStageStatusService
							.callPostEventStageStatusService(postEventStageStatusInDto);
					if (null != postEventStageStatusOutDto
							&& !ObjectUtils.isEmpty(postEventStageStatusOutDto.getIdEvent())) {
						idEvent = postEventStageStatusOutDto.getIdEvent();
					} else {
						throw new ServiceLayerException(null, Long.valueOf(Messages.MSG_CMN_TMSTAMP_MISMATCH), null);

					}
				} catch (DataNotFoundException e) {
					throw new ServiceLayerException(null, Long.valueOf(Messages.MSG_CMN_TMSTAMP_MISMATCH), null);
				}
			}

			if (!idEvent.equals(0L)) {
				// cinv43d
				TodoUpdDtTodoCompletedInDto dtTodoCompletedInDto = new TodoUpdDtTodoCompletedInDto();
				dtTodoCompletedInDto.setIdEvent(idEvent);
				todoUpdDtTodoCompletedDao.updateTODOEvent(dtTodoCompletedInDto);
				// END CINV43D
			}

			// CAUD05D update Legal Status
			addOrUpdateLegalStatus(legalStatusOutDto, idEvent, stageEventDto, legalStatusReq);

			// Begin common function CSUB40U
			if (0 != legalStatusReq.getIdTodoCfPersCrea()) {
				TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
				MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INFO_SUB026);
				mergeSplitToDoDto.setIdTodoCfStage(legalStatusReq.getIdStage());
				mergeSplitToDoDto.setIdTodoCfEvent(legalStatusReq.getStageEventDto().getIdEvent());
				mergeSplitToDoDto.setDtTodoCfDueFrom(legalStatusOutDto.getDtLegalStatusDt());
				mergeSplitToDoDto.setIdTodoCfPersCrea(legalStatusReq.getIdTodoCfPersCrea());
				todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
				todoCreateService.callCsub40uService(todoCreateInDto);
			}

			if (LEGAL_STATUS_LIST1.contains(legalStatusOutDto.getCdLegalStatStatus())
					&& legalStatusOutDto.getDtLegalStatusDt().before(eighteenYear)) {
				// Call common todo function structure
				callCommonTodoFunction(legalStatusReq, legalStatusOutDto);
			}

			// CAUDB8D update the person guardianship and conservatorship status
			// on the person table.
			if (LEGAL_STATUS_LIST2.contains(legalStatusOutDto.getCdLegalStatStatus())) {
				legalStatusDao.updatePersonGuardCnsrv(legalStatusOutDto.getIdPerson(), CONSERV_STATUS);
			} else {
				legalStatusDao.updatePersonGuardCnsrv(legalStatusOutDto.getIdPerson(), null);
			}
			// update open slots in resource if legal status is adoption
			// consummated for an ADD
			if (ServiceConstants.REQ_FUNC_CD_ADD.equals(legalStatusReq.getReqFuncCd())
					&& CodesConstant.CLEGSTAT_090.equals(legalStatusOutDto.getCdLegalStatStatus())) {
				updateOpenSlots(legalStatusOutDto.getIdPerson(), reqFuncCode);
			}
		} else {
			// Delete
			EventInsUpdDelInDto eventInsUpdDelInDto = new EventInsUpdDelInDto();
			eventInsUpdDelInDto.setDtEventLastUpdate(stageEventDto.getTsLastUpdate());
			eventInsUpdDelInDto.setIdEvent(stageEventDto.getIdEvent());
			Event event = eventInsUpdDelDao.checkEventByLastUpdate(eventInsUpdDelInDto);
			if (event == null)
				return Messages.MSG_CMN_TMSTAMP_MISMATCH;
			deleteLegalStatus(legalStatusOutDto, reqFuncCode, legalStatusReq);
		}
		
		/*
		 * If the current stage is SUB or ADO and there exists a corresponding
		 * ADO or SUB stage for current stage , then getStage returns the stage
		 * id. When otherStageId <= 0 then all active medical Consenters for
		 * Primary child are end dated. This should only happen during add or
		 * update and should not happen during delete
		 */
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(reqFuncCode)
				|| ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFuncCode)) {
			String cdLegalStatus = legalStatusReq.getLegalStatusOutDto().getCdLegalStatStatus();
			boolean updateMedicalConsenter = false;
			if (CodesConstant.CLEGSTAT_090.equalsIgnoreCase(cdLegalStatus)
					|| CodesConstant.CLEGSTAT_100.equalsIgnoreCase(cdLegalStatus)
					|| CodesConstant.CLEGSTAT_120.equalsIgnoreCase(cdLegalStatus)
					|| CodesConstant.CLEGSTAT_150.equalsIgnoreCase(cdLegalStatus)) {
				updateMedicalConsenter = true;
			}
			if (updateMedicalConsenter) {
				String stageType = medicalConsenterDao.getStageType(legalStatusReq.getIdStage());
				if (!TypeConvUtil.isNullOrEmpty(stageType) && (CodesConstant.CSTAGES_SUB.equalsIgnoreCase(stageType)
						|| CodesConstant.CSTAGES_ADO.equalsIgnoreCase(stageType))) {
					MedicalConsenterDto medicalConsenterDto = new MedicalConsenterDto();
					medicalConsenterDto.setIdStage(legalStatusReq.getIdStage());
					Long otherStageID = medicalConsenterDao.getCorrespStage(medicalConsenterDto);
					if (TypeConvUtil.isNullOrEmpty(otherStageID)) {
						medicalConsenterDto.setIdCase(legalStatusReq.getIdCase());
						medicalConsenterDto.setDtMedConsEnd(legalStatusReq.getLegalStatusOutDto().getDtLegalStatusDt());
						Boolean updateStatus = medicalConsenterService.checkMedicalConsenterStatus(medicalConsenterDto);
						if (updateStatus) {
							medicalConsenterService.updateMedicalConsenterEndDate(medicalConsenterDto);
						}
					}
				}
			}
		}
		log.info("Exit method updateLegalStatus in LegalStatusServiceImpl");
		return 0;
	}

	/**
	 * 
	 * Method Name: deleteLegalStatus Method Description: this method is used to
	 * call CAUD05D and CAUD07D to delete legal status .
	 * 
	 * @param legalStatusOutDto
	 * @param reqFuncCode
	 * @param legalStatusReq
	 */
	private void deleteLegalStatus(LegalStatusOutDto legalStatusOutDto, String reqFuncCode,
			LegalStatusUpdateReq legalStatusReq) {

		// eventDao.deleteEventById(legalStatusReq.getStageEventDto().getIdEvent());

		// BEGIN CAUD05D
		LegalStatusDto legalStatusDto = new LegalStatusDto();
		legalStatusDto.setIdLegalStatEvent(legalStatusOutDto.getIdLegalStatEvent());
		legalStatusDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		if(null != legalStatusReq.getUserLogonId()) {
			legalStatusDto.setUserLogonId(legalStatusReq.getUserLogonId());
		}
		legalStatusDao.updateLegalStatus(legalStatusDto);
		// END CAUD05D

		// BEGIN CAUD07D Stored Procedure
		try {
			legalStatusDao.deleteSubcareEvent(legalStatusReq.getStageEventDto().getIdEvent(),
					legalStatusReq.getStageEventDto().getTsLastUpdate());
		} catch (SQLException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}

		// update open slots in resource if legal status is adoption consummated
		// for an delete
		if (CodesConstant.CLEGSTAT_090.equals(legalStatusOutDto.getCdLegalStatStatus())) {
			updateOpenSlots(legalStatusOutDto.getIdPerson(), reqFuncCode);
		}
	}

	/**
	 * 
	 * Method Name: callWhoToSendToDo Method Description:this function checks to
	 * see who to send a todo if there has to be one to send.
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<Long> callWhoToSendToDo(Long idStage, Long idEvent) {
		boolean bValidEligibilityExist = false;
		boolean bLocalEmpIsSpecialist = false;
		List<Long> workList = new ArrayList<>();
		// CSESG3D Begin
		EligibilityByPersonInDto eligibilityByPerson = getEligibilityEventForStage(idStage);
		if (eligibilityByPerson != null
				&& (eligibilityByPerson.getDtEligEnd() == null || eligibilityByPerson.getDtEligEnd().getYear() == 3500)
				&& (CodesConstant.CELIGIBI_010.equals(eligibilityByPerson.getCdEligSelected())
						|| CodesConstant.CELIGIBI_020.equals(eligibilityByPerson.getCdEligSelected())
						|| CodesConstant.CELIGIBI_030.equals(eligibilityByPerson.getCdEligSelected()))) {
			bValidEligibilityExist = true;
		}
		// CSESG3D END

		if (!bValidEligibilityExist) {
			// CSEC86 BEGIN
			List<WorkLoadDto> workloadDtoList = legalStatusDao.getWorkLoadsForStage(idStage);
			if (CollectionUtils.isNotEmpty(workloadDtoList)) {
				// Find if there is a secondary worker and also keep the index
				// of primary worker.
				// if there is no secondary worker we need to send the todo to
				// primary.
				for (WorkLoadDto workloadDto : workloadDtoList) {
					if (ServiceConstants.PERSON_ROLE_PRIMARY.equals(workloadDto.getCdWkldStagePersRole())) {
						workList.add(0, workloadDto.getIdWrldPerson());
					} else if (ServiceConstants.PERSON_ROLE_SECONDARY.equals(workloadDto.getCdWkldStagePersRole())) {
						// if there is a secondary worker, check to see if they
						// are eligibility specialists.
						bLocalEmpIsSpecialist = getEmployeeSecurityProfile(workloadDto.getIdWrldPerson());
						if (bLocalEmpIsSpecialist) {
							// if the worker is eligibility specialist, we need
							// to send a todo to the worker
							workList.add(workloadDto.getIdWrldPerson());
						}
					}
				}
			}
		}
		return workList;
	}

	/**
	 * 
	 * Method Name: getEligibilityEventForStage Method Description: call service
	 * CSESG3D, get eligibility event for stage
	 * 
	 * @param idStage
	 * @return
	 */
	public EligibilityByPersonInDto getEligibilityEventForStage(long idStage) {
		return legalStatusDao.getEligibilityEventForStage(idStage);
	}

	/**
	 * 
	 * Method Name: getPersonNameForStage Method Description: call service
	 * CSESG4D, get person name for stage
	 * 
	 * @param idStage
	 * @return
	 */
	public String getPersonNameForStage(long idStage) {
		List<String> nameList = legalStatusDao.getPersonNameForStage(idStage);
		if (CollectionUtils.isNotEmpty(nameList))
			return nameList.get(0);
		else
			return null;
	}

	/**
	 * 
	 * Method Name: getEmployeeSecurityProfile Method Description: call service
	 * CLSCB4D , check employee security profile
	 * 
	 * @param idPerson
	 * @return
	 */
	public Boolean getEmployeeSecurityProfile(long idPerson) {
		Boolean empHasSecurity = Boolean.FALSE;
		List<EmpSecClassLink> empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(idPerson);
		for (EmpSecClassLink empSecClassLink : empSecClassLinkList) {
			if (empSecClassLink.getSecurityClass().getTxtSecurityClassProfil().charAt(11) == '1') {
				empHasSecurity = Boolean.TRUE;
				break;
			}
		}
		return empHasSecurity;
	}/* CallCLSCB4D */

	/**
	 * 
	 * Method Name: updateOpenSlots Method Description:this sub function will
	 * take the input and output messages and figure out whether or not to
	 * update the open slots in the caps_resource table. This will only be
	 * called if the legal status is adoption consummated on add or delete.
	 * 
	 * @param idPerson
	 * @param reqFuncCode
	 */
	public void updateOpenSlots(Long idPerson, String reqFuncCode) {
		// CSES28D
		List<ResourcePlacementOutDto> rescPlmtOutDtoList = legalStatusDao.getResourcePlacementDetail(idPerson);
		if (CollectionUtils.isNotEmpty(rescPlmtOutDtoList)) {
			ResourcePlacementOutDto rescPlmtOutDto = rescPlmtOutDtoList.get(0);
			// if the facility type is 70 or 71 (FAD Home), then update the
			// number of open slots
			if (FPS_FA_HOME.equals(rescPlmtOutDto.getCdRsrcFacilType())
					|| PRIV_AGENCY_ADPT_HOME.equals(rescPlmtOutDto.getCdRsrcFacilType())) {
				/*
				 * if deleting a legal status, subtract one from open slots. if
				 * adding the legal status, add one to open slots. (this code
				 * logic looks backwards, but it isn't)
				 */
				// Call CMSC16D
				if (ServiceConstants.REQ_FUNC_CD_ADD.equals(reqFuncCode)) {
					legalStatusDao.modifyNbrRscOpenSlots(rescPlmtOutDto.getIdResource(), "1");
				} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(reqFuncCode)) {
					legalStatusDao.modifyNbrRscOpenSlots(rescPlmtOutDto.getIdResource(), "-1");
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: getIndLegalStatMissing Method Description: get
	 * indLegalStatMissing flag
	 * 
	 * @param idReferral
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesignationDto getIndLegalStatMissing(Long idReferral) {
		return legalStatusDao.getIndLegalStatMissing(idReferral);
	}

	/**
	 * 
	 * Method Name: updateSsccIndLegalStatus Method Description:update SSCC_LIST
	 * TABLE , IND_LEGAL_STATUS_MISSING
	 * 
	 * @param indLegalStatusMissing
	 * @param idSsccReferral
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void updateSsccIndLegalStatus(String indLegalStatusMissing, Long idSsccReferral) {
		legalStatusDao.updateSsccIndLegalStatus(indLegalStatusMissing, idSsccReferral);
	}

	/**
	 * 
	 * Method Name: addOrUpdateLegalStatus Method Description: This method is
	 * used to call CAUD05D to add or update legal status detail
	 * 
	 * @param legalStatusOutDto
	 * @param idEvent
	 * @param stageEventDto
	 * @param legalStatusReq
	 */
	private void addOrUpdateLegalStatus(LegalStatusOutDto legalStatusOutDto, Long idEvent, StageEventDto stageEventDto,
											   LegalStatusUpdateReq legalStatusReq) {
		// CAUD05D update Legal Status
		LegalStatusDto legalStatusDto = new LegalStatusDto();
		if (0l == legalStatusOutDto.getIdLegalStatEvent()) {
			legalStatusDto.setIdLegalStatEvent(idEvent.longValue());
			legalStatusDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			legalStatusDto.setIdLegalStatEvent(legalStatusOutDto.getIdLegalStatEvent());
			legalStatusDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		}
		legalStatusDto.setIdPerson(legalStatusOutDto.getIdPerson());
		legalStatusDto.setIdCase(legalStatusOutDto.getIdCase());
		legalStatusDto.setCdLegalStatCnty(legalStatusOutDto.getCdLegalStatCnty());
		legalStatusDto.setCdLegalStatStatus(legalStatusOutDto.getCdLegalStatStatus());
		legalStatusDto.setDtLegalStatStatusDt(legalStatusOutDto.getDtLegalStatusDt());
		legalStatusDto.setDtLegalStatTMCDismiss(legalStatusOutDto.getDtLegalStatTMCDismiss());
		legalStatusDto.setTxtLegalStatCauseNbr(legalStatusOutDto.getTxtLegalStatCauseNbr());
		legalStatusDto.setTxtLegalStatCourtNbr(legalStatusOutDto.getTxtLegalStatCourtNbr());
		legalStatusDto.setCdCourtNbr(legalStatusOutDto.getCdCourtNbr());
		legalStatusDto.setIndCsupSend(legalStatusReq.getIndCsupSend());
		legalStatusDto.setCdLegStatDischargeRsn(legalStatusOutDto.getCdLegStatDischargeRsn());
		legalStatusDto.setIdLastUpdatePerson(stageEventDto.getIdPerson());
		legalStatusDto.setIndJmcPrntReltnshpKnsp(legalStatusReq.getIndJmcPrntReltnshpKnsp());
		legalStatusDto.setCdLegalStatSubType(legalStatusReq.getLegalStatusOutDto().getCdLegalStatusSubType());
		legalStatusDao.updateLegalStatus(legalStatusDto);
		// END CAUD05D
	}

	/**
	 * 
	 * Method Name: populateEventStageStatusInDto Method Description: populate
	 * input object for service CCMN01U
	 * 
	 * @param stageEventDto
	 * @return
	 */
	private PostEventStageStatusInDto populateEventStageStatusInDto(StageEventDto stageEventDto) {
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();
		postEventStageStatusInDto.setDtEventOccurred(stageEventDto.getDtEventOccurred());
		postEventStageStatusInDto.setIdEvent(stageEventDto.getIdEvent());
		postEventStageStatusInDto.setDtEventLastUpdate(stageEventDto.getTsLastUpdate());
		postEventStageStatusInDto.setIdStage(stageEventDto.getIdStage());
		postEventStageStatusInDto.setIdPerson(stageEventDto.getIdPerson());
		postEventStageStatusInDto.setCdTask(stageEventDto.getCdTask());
		postEventStageStatusInDto.setCdEventStatus(stageEventDto.getCdEventStatus());
		postEventStageStatusInDto.setCdEventType(stageEventDto.getCdEventType());
		postEventStageStatusInDto.setEventDescr(stageEventDto.getEventDescr());
		if (stageEventDto.getIdEvent() == 0L)
			postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		else
			postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		return postEventStageStatusInDto;
	}

	/**
	 * 
	 * Method Name: callCommonTodoFunction Method Description:call common todo
	 * function after update or add legal status detail
	 * 
	 * @param legalStatusReq
	 * @param legalStatusOutDto
	 */
	private void callCommonTodoFunction(LegalStatusUpdateReq legalStatusReq, LegalStatusOutDto legalStatusOutDto) {
		// populate common function input structure
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();

		DateFormat df = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMddyyyy);
		String formatDate = df.format(legalStatusOutDto.getDtLegalStatusDt());
		String desc = SUB_060_TODO_INFO_DESC + formatDate + PERIOD;

		// mergesplit
		// CSESG4D BEGIN
		String personName = getPersonNameForStage(legalStatusReq.getStageEventDto().getIdStage());
		// CSESG4D END
		// CallWhoToSendToDo send todo to worker
		List<Long> workerIdList = callWhoToSendToDo(legalStatusReq.getStageEventDto().getIdStage(), FCE_ELIG_EVENT_ID);

		mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INFO_SUB060);
		mergeSplitToDoDto.setTodoCfDesc(desc);
		mergeSplitToDoDto.setIdTodoCfStage(legalStatusReq.getStageEventDto().getIdStage());
		mergeSplitToDoDto.setIdTodoCfEvent(FCE_ELIG_EVENT_ID);
		mergeSplitToDoDto.setDtTodoCfDueFrom(new Date());
		mergeSplitToDoDto.setIdTodoCfPersCrea(legalStatusReq.getStageEventDto().getIdPerson());

		if (CollectionUtils.isNotEmpty(workerIdList)) {
			for (Long workerId : workerIdList) {
				mergeSplitToDoDto.setIdTodoCfPersAssgn(workerId);
				todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
				todoCreateService.callCsub40uService(todoCreateInDto);
			}
			// send alert to primary worker
			String todoDesc = SUB_065_TODO_INFO_DESC + personName + SUB_066_TODO_INFO_DESC + PERIOD;
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INFO_SUB065);
			mergeSplitToDoDto.setTodoCfDesc(todoDesc);

			todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
			todoCreateService.callCsub40uService(todoCreateInDto);
		}
	}

	/**
	 * 
	 * Method Name: updatePendingEvent Method Description: update pending event
	 * detail by calling service CCMN05U and CCMN62D
	 * 
	 * @param legalStatusOutDto
	 * @param stageEventDto
	 */
	private void updatePendingEvent(LegalStatusOutDto legalStatusOutDto, StageEventDto stageEventDto) {
		// CCMN05U
		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		approvalCommonInDto.setIdEvent(legalStatusOutDto.getIdLegalStatEvent());
		approvalService.callCcmn05uService(approvalCommonInDto);

		// Pending event, get event status from Input. Call CCMN62D
		EventUpdEventStatusInDto eventUpdEventStatusInDto = new EventUpdEventStatusInDto();
		eventUpdEventStatusInDto.setCdEventStatus(stageEventDto.getCdEventStatus());
		eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventUpdEventStatusInDto.setIdEvent(legalStatusOutDto.getIdLegalStatEvent());
		eventUpdateDao.updateEvent(eventUpdEventStatusInDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.service.LegalStatusService#
	 * selectLatestLegalActionSubType(us.tx.state.dfps.service.admin.dto.
	 * LegalActionEventInDto)
	 */
	@Override
	public CommonStringRes selectLatestLegalActionSubType(LegalActionEventInDto legalActionEventInDto) {
		return legalStatusDao.selectLatestLegalActionSubType(legalActionEventInDto);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.service.LegalStatusService#
	 * selectLatestLegalStatus(us.tx.state.dfps.service.admin.dto.
	 * LegalActionEventInDto)
	 */
	@Override
	public LegalStatusDetailDto selectLatestLegalStatus(LegalActionEventInDto legalActionEventInDto) {
		return legalStatusDao.selectLatestLegalStatus(legalActionEventInDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.service.LegalStatusService#
	 * getLegalStatusForChild(java.lang.Long, java.lang.Long)
	 */
	// UIDS 2.3.3.5 - Remove a child from home - Income and Expenditures
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int getLegalStatusForChild(Long idPerson, Long idCase) {
		return legalStatusDao.getLegalStatusForChild(idPerson, idCase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.service.LegalStatusService#
	 * getRecentLegalRegionForChild(java.lang.Long)
	 */
	// UIDS 2.3.3.5 - Remove a child from home - To-Do Detail
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getRecentLegalRegionForChild(Long idPerson) {
		return legalStatusDao.getRecentLegalRegionForChild(idPerson);
	}


	//PPM 77834 – FCL CLASS Webservice for Data Exchange
	/**
	 * @param idPerson
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalStatusDetailDto getLatestLegalStatusByPersonId(Long idPerson) {
		LegalStatusDetailDto statusDetailDto = legalStatusDao.getLatestLegalStatusByPersonId(idPerson);
		if(!ObjectUtils.isEmpty(statusDetailDto) && !ObjectUtils.isEmpty(statusDetailDto.getCdCourtNbr())){
			statusDetailDto.setCdCourtNbr(codesTableViewLookupUtils.getDecodeVal("CRTNUMB",statusDetailDto.getCdCourtNbr()));
		}
		return statusDetailDto;
	}

	/**
	 * @param idPerson
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalStatusDetailDto getLatestLegalStatusInfoByPersonId(Long idPerson) {
		LegalStatusDetailDto statusDetailDto = legalStatusDao.getLatestLegalStatusInfoByPersonId(idPerson);
		if(!ObjectUtils.isEmpty(statusDetailDto) && !ObjectUtils.isEmpty(statusDetailDto.getCdCourtNbr())){
			statusDetailDto.setCdCourtNbr(codesTableViewLookupUtils.getDecodeVal("CRTNUMB",statusDetailDto.getCdCourtNbr()));
		}
		return statusDetailDto;
	}

	/**
	 * @param idEvent
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalStatusDetailDto getLatestLegalStatusInfoByEventId(Long idEvent) {
		LegalStatusDetailDto statusDetailDto = legalStatusDao.getLatestLegalStatusInfoByEventId(idEvent);
		if(!ObjectUtils.isEmpty(statusDetailDto) && !ObjectUtils.isEmpty(statusDetailDto.getCdCourtNbr())){
			statusDetailDto.setCdCourtNbr(codesTableViewLookupUtils.getDecodeVal("CRTNUMB",statusDetailDto.getCdCourtNbr()));
		}
		return statusDetailDto;
	}
}
