package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.CloseStageCaseOutputDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.StageCdDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnSaveInDto;
import us.tx.state.dfps.service.casepackage.service.RecordsRetentionService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEligibilityDao;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;

@Service
public class CloseStageCaseServiceImpl implements CloseStageCaseService {

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	MedicalConsenterDao medicalConsenterDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	StageWorkloadDao stageWorkloadDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonEligibilityDao personEligibilityDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	AdminReviewDao adminReviewDao;

	@Autowired
	StageCdDao objCcmnf6dDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	TodoDao todoDao;
	
	@Autowired
	RecordsRetentionService recordsRetentionService;


	/**
	 * This shared library function provides the necessary updates required to
	 * close a stage. If a case and a situation are associated with the stage,
	 * and there are no other open stages associated with the case, the
	 * situation and the case are also closed.
	 * 
	 * Service Name : CCMN02U
	 * 
	 * @param closeStageCaseInputDto
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CloseStageCaseOutputDto closeStageCase(CloseStageCaseInputDto closeStageCaseInputDto) {
		CloseStageCaseOutputDto closeStageCaseOutputDto = new CloseStageCaseOutputDto();
		List<StageProgDto> stageProgDtoList = null;
		// StageDto stageDto = null;
		Stage stageEntity = null;
		Date date = new Date();
		StagePersonLinkDto stagePersonLinkDto = null;
		Long ulIdPrimaryChild = ServiceConstants.ZERO_VAL;
		Long ulIdCasePC = ServiceConstants.ZERO_VAL;
		Long ulADOStageId = ServiceConstants.ZERO_VAL;
		Long ulSUBStageId = ServiceConstants.ZERO_VAL;
		boolean bIndMcEndDate = false;
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		EventDto eventDto = new EventDto();
		EventDto newEventDto = new EventDto();
		StagePersonLinkDto stagePersonLinkPRDto = null;
		EmployeeDetailDto employeeDetailDto = null;
		StagePersonLinkDto newStagePersonLinkDto = new StagePersonLinkDto();
		int usPageNbr = ServiceConstants.INITIAL_PAGE;
		List<StagePersonLinkDto> stagePersonLinkDtoList = null;
		List<StagePersonLinkDto> newStagePersonLinkDtoList = null;
		//List<StageDto> stageDtoList = null;
		PersonEligibilityDto personEligibilityDto = null;
		PersonEligibilityDto newPersonEligibilityDto = new PersonEligibilityDto();
		CapsCaseDto capsCaseDto = null;
		boolean bAPS = false;
		boolean bCPS = false;
		boolean bEA_Close = false;
		boolean bEA_Open_Found = false;
		try {
			if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto)) {
				if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStage())) {
					if (!closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_SPC)
							&& !closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_IR)) {
						if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStageProgram())
								&& !TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStageReasonClosed())) {
							// CCMNB8D
							stageProgDtoList = stageProgDao.getStgProgroession(closeStageCaseInputDto.getCdStage(),
									closeStageCaseInputDto.getCdStageProgram(),
									closeStageCaseInputDto.getCdStageReasonClosed());
						}
					}
					// CINT21D
					if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
						stageEntity = stageDao.getStageEntityById(closeStageCaseInputDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(stageEntity)) {
						if (TypeConvUtil.isNullOrEmpty(stageEntity.getDtStageClose())) {
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())
									&& !TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStageReasonClosed())) {
								// CCMND4D
								closeOpenStageService.updateStageCloseRecord(closeStageCaseInputDto.getIdStage(),
										new Date(), closeStageCaseInputDto.getCdStageReasonClosed(),
										ServiceConstants.EMPTY_STRING);
							}
							if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.ADOPTION)
									|| closeStageCaseInputDto.getCdStage().equals(ServiceConstants.SUBCARE)) {
								if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
									// CLSSA5D
									stagePersonLinkDto = stagePersonLinkDao
											.getPrimaryChildIdByIdStage(closeStageCaseInputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto)) {
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
										ulIdPrimaryChild = stagePersonLinkDto.getIdPerson();
									}
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdCase())) {
										ulIdCasePC = stagePersonLinkDto.getIdCase();
									}
								}
								if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.SUBCARE)) {
									if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
										// CLSSA6D
										ulADOStageId = stagePersonLinkDao
												.getIdADOStageByIdSUBStage(closeStageCaseInputDto.getIdStage());
									}
									if (!TypeConvUtil.isNullOrEmpty(ulADOStageId)) {
										bIndMcEndDate = true;
									}
								}
								if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.ADOPTION)) {
									if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
										// CLSSSA7D
										ulSUBStageId = stagePersonLinkDao
												.getIdSUBStageByIdADOStage(closeStageCaseInputDto.getIdStage());
									}
									if (!TypeConvUtil.isNullOrEmpty(ulSUBStageId)) {
										bIndMcEndDate = true;
									}
								}
								if (bIndMcEndDate) {
									if (!TypeConvUtil.isNullOrEmpty(ulIdCasePC)
											&& !TypeConvUtil.isNullOrEmpty(ulIdPrimaryChild)) {
										// CAUDK8D
										medicalConsenterDao.updateMedicalConcenterByAttributes(date, ulIdCasePC,
												ulIdPrimaryChild);
									}
								}
							}
							// CCMN46D
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
							if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_SPC)
									|| closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_IR)) {
								eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
								eventDto.setCdEventType(ServiceConstants.CSTAGES_STG);
							} else {
								if (!TypeConvUtil.isNullOrEmpty(stageProgDtoList)) {
									if (stageProgDtoList.size() > 0) {
										if (!TypeConvUtil
												.isNullOrEmpty(stageProgDtoList.get(0).getCdStageProgStatus())) {
											eventDto.setCdEventStatus(stageProgDtoList.get(0).getCdStageProgStatus());
										}
										if (!TypeConvUtil
												.isNullOrEmpty(stageProgDtoList.get(0).getCdStageProgEventType())) {
											eventDto.setCdEventType((!ObjectUtils
													.isEmpty(stageProgDtoList.get(0).getCdStageProgEventType()))
															? stageProgDtoList.get(0).getCdStageProgEventType()
															: ServiceConstants.EVENT_TYPE_APPRV);
										}
									}
								}
							}
							if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.INV_Stage)) {
								eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
								eventDto.setCdEventType(ServiceConstants.CSTAGES_STG);
							}
							if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_FAD)) {
								eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
								eventDto.setCdEventType(ServiceConstants.CSTAGES_STG);
								eventDto.setEventDescr(ServiceConstants.FAD_EVENT_DESC);
							}
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
								eventDto.setIdStage(closeStageCaseInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdPerson())) {
								eventDto.setIdPerson(closeStageCaseInputDto.getIdPerson());
							}
							eventDto.setDtEventOccurred(date);
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getEventDescr())) {
								eventDto.setEventDescr(closeStageCaseInputDto.getEventDescr());
							}
							if (!closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_SPC)
									&& !closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_IR)
									&& !ServiceConstants.TRUEVAL.equals(closeStageCaseInputDto.getIsCrsrStage())) {
								if (!TypeConvUtil.isNullOrEmpty(stageProgDtoList)) {
									if (stageProgDtoList.size() > 0) {
										if (!TypeConvUtil
												.isNullOrEmpty(stageProgDtoList.get(0).getStageProgEvntDesc())) {
											eventDto.setEventDescr(stageProgDtoList.get(0).getStageProgEvntDesc());
										}
									}
								}
							} else {
								if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_SPC)) {
									eventDto.setEventDescr(ServiceConstants.SPC_EVENT_DESC);
								} else if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_IR)) {
									eventDto.setEventDescr(ServiceConstants.INR_EVENT_DESC);
								}
							}
							// CCMN46D
							newEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
							if (!TypeConvUtil.isNullOrEmpty(newEventDto)) {
								if (!TypeConvUtil.isNullOrEmpty(newEventDto.getIdEvent())) {
									closeStageCaseOutputDto.setIdEvent(newEventDto.getIdEvent());
								}
							}
							// CCMNG2D
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
								stagePersonLinkPRDto = stageWorkloadDao
										.getStagePersonLinkByStageRole(closeStageCaseInputDto.getIdStage());
							}
							// CCMN69D
							if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto)) {
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIdPerson())) {
									employeeDetailDto = employeeDao.getEmployeeById(stagePersonLinkPRDto.getIdPerson());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getCdStagePersRelInt())) {
									newStagePersonLinkDto
											.setCdStagePersRelInt(stagePersonLinkPRDto.getCdStagePersRelInt());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getCdStagePersSearchInd())) {
									newStagePersonLinkDto
											.setCdStagePersSearchInd(stagePersonLinkPRDto.getCdStagePersSearchInd());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getCdStagePersType())) {
									newStagePersonLinkDto.setCdStagePersType(stagePersonLinkPRDto.getCdStagePersType());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getStagePersNotes())) {
									newStagePersonLinkDto.setStagePersNotes(stagePersonLinkPRDto.getStagePersNotes());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIdPerson())) {
									newStagePersonLinkDto.setIdPerson(stagePersonLinkPRDto.getIdPerson());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIdStagePersonLink())) {
									newStagePersonLinkDto
											.setIdStagePersonLink(stagePersonLinkPRDto.getIdStagePersonLink());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIndStagePersInLaw())) {
									newStagePersonLinkDto
											.setIndStagePersInLaw(stagePersonLinkPRDto.getIndStagePersInLaw());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIndStagePersReporter())) {
									newStagePersonLinkDto
											.setIndStagePersReporter(stagePersonLinkPRDto.getIndStagePersReporter());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIndStagePersEmpNew())) {
									newStagePersonLinkDto
											.setIndStagePersEmpNew(stagePersonLinkPRDto.getIndStagePersEmpNew());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getDtStagePersLink())) {
									newStagePersonLinkDto.setDtStagePersLink(stagePersonLinkPRDto.getDtStagePersLink());
								}
								
								if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
									newStagePersonLinkDto.setIdStage(closeStageCaseInputDto.getIdStage());
								}
								newStagePersonLinkDto.setCdStagePersRole(ServiceConstants.HISTORICAL_PRIMARY);
								if (!TypeConvUtil.isNullOrEmpty(stageEntity.getCapsCase())
										&& !TypeConvUtil.isNullOrEmpty(stageEntity.getCapsCase().getIdCase())) {
									newStagePersonLinkDto.setIdCase(stageEntity.getCapsCase().getIdCase());
								}
								// CCMND3D
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
								stagePersonLinkDao.getStagePersonLinkAUD(newStagePersonLinkDto, serviceReqHeaderDto);
							}
							
							// CCMND3D
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
								stagePersonLinkDao
										.deletePRSEStagePersonLinkByIdStage(closeStageCaseInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStageProgram())) {
								if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)
										&& closeStageCaseInputDto.getCdStageProgram()
												.equals(ServiceConstants.CAPS_PROG_APS)) {
									do {
										if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
											// CCMNB9D
											stagePersonLinkDtoList = stagePersonLinkDao
													.getStagePersonLinkByIdStage(closeStageCaseInputDto.getIdStage());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDtoList)) {
											for (StagePersonLinkDto newNewStagePersonLinkDto : stagePersonLinkDtoList) {
												if (!TypeConvUtil
														.isNullOrEmpty(newNewStagePersonLinkDto.getCdStagePersRole())) {
													if (newNewStagePersonLinkDto.getCdStagePersRole()
															.equals(ServiceConstants.PERSON_ROLE_BOTH)
															|| newNewStagePersonLinkDto.getCdStagePersRole()
																	.equals(ServiceConstants.PERSON_ROLE_VICTIM)) {
														newNewStagePersonLinkDto.setCdStagePersRole(
																ServiceConstants.PERSON_ROLE_CLIENT);
														newNewStagePersonLinkDto.setDtLastUpdate(date);
														// CCMND3D
														serviceReqHeaderDto
																.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
														stagePersonLinkDao.getStagePersonLinkAUD(
																newNewStagePersonLinkDto, serviceReqHeaderDto);
													}
												}
											}
										}
										usPageNbr++;
									} while (stagePersonLinkDtoList.size() > usPageNbr
											* ServiceConstants.STAGE_PERSON_LINK_ROW_NUM);
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
								// CCMNB9D
								newStagePersonLinkDtoList = stagePersonLinkDao
										.getStagePersonLinkByIdStage(closeStageCaseInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkDtoList)) {
								for (StagePersonLinkDto newNewstagePersonLinkDto : newStagePersonLinkDtoList) {
									if (!TypeConvUtil.isNullOrEmpty(newNewstagePersonLinkDto.getCdStagePersType())) {
										if (!newNewstagePersonLinkDto.getCdStagePersType()
												.equals(ServiceConstants.STAFF_TYPE)) {
											List<StageDto> stageDtoList = null;
											// CINV33D
											if (!TypeConvUtil.isNullOrEmpty(newNewstagePersonLinkDto.getIdPerson())) {
												stageDtoList = stageDao.getActiveStageByIdPerson(
														newNewstagePersonLinkDto.getIdPerson());
											}
											//[artf130281]-If a person is involved only in one case/stage which is going to be closed,
											// make that person inactive . Before this code gets executed , sometimes stage gets closed .Hence ,Below two checks needed.
											if (	CollectionUtils.isEmpty(stageDtoList)
													||
													(stageDtoList.size()==1 && closeStageCaseInputDto.getIdStage().longValue()
															== stageDtoList.get(0).getIdStage().longValue())) {
												if (!TypeConvUtil
														.isNullOrEmpty(newNewstagePersonLinkDto.getIdPerson())) {
													Person person = new Person();
													person = personDao.getPersonByPersonId(
															newNewstagePersonLinkDto.getIdPerson());
													person.setCdPersonStatus(ServiceConstants.CD_INACTIVE);
													// CAUD74D
													personDao.updatePerson(person);
												}
											} else {
												for (StageDto newStageDto : stageDtoList) {
													if (!TypeConvUtil.isNullOrEmpty(newStageDto.getCdStageProgram())) {
														if (newStageDto.getCdStageProgram()
																.equals(ServiceConstants.CAPS_PROG_APS)
																|| newStageDto.getCdStageProgram()
																		.equals(ServiceConstants.CAPS_PROG_AFC)) {
															bAPS = true;
														}
														if (newStageDto.getCdStageProgram()
																.equals(ServiceConstants.CAPS_PROG_CPS)) {
															bCPS = true;
														}
													}
												}
											}
											if (bAPS && (!bCPS)) {
												bEA_Close = true;
											}
											if (TypeConvUtil.isNullOrEmpty(stageDtoList) || bEA_Close) {
												// CSECA4D
												if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdPerson())) {
													personEligibilityDto = personEligibilityDao
															.getPrincipalStagesByIdPerson(
																	closeStageCaseInputDto.getIdPerson());
												}
												if (!TypeConvUtil.isNullOrEmpty(personEligibilityDto)) {
													if (!TypeConvUtil.isNullOrEmpty(
															personEligibilityDto.getCdPersEligPrgOpen())) {
														if ((bEA_Open_Found)
																&& (!personEligibilityDto.getCdPersEligPrgOpen()
																		.equals(ServiceConstants.STARS))) {
															newPersonEligibilityDto = personEligibilityDto;
															if (personEligibilityDto.getCdPersEligPrgOpen()
																	.equals(ServiceConstants.BOTH)
																	&& TypeConvUtil.isNullOrEmpty(personEligibilityDto
																			.getCdPersEligPrgClosed())) {
																newPersonEligibilityDto
																		.setCdPersEligPrgOpen(ServiceConstants.STARS);
																newPersonEligibilityDto
																		.setCdPersEligPrgClosed(ServiceConstants.CAPS);
																serviceReqHeaderDto
																		.setReqFuncCd(ServiceConstants.BOTH_NULL);
															}
															if (personEligibilityDto.getCdPersEligPrgOpen()
																	.equals(ServiceConstants.BOTH)
																	&& (!TypeConvUtil.isNullOrEmpty(personEligibilityDto
																			.getCdPersEligPrgClosed()))
																	&& personEligibilityDto.getCdPersEligPrgClosed()
																			.equals(ServiceConstants.STARS)) {
																newPersonEligibilityDto
																		.setCdPersEligPrgClosed(ServiceConstants.BOTH);
																newPersonEligibilityDto.setDtPersEligEaDeny(date);
																serviceReqHeaderDto.setReqFuncCd(
																		ServiceConstants.UPDATE_DENY_DATE);
															}
															if (personEligibilityDto.getCdPersEligPrgOpen()
																	.equals(ServiceConstants.CAPS)
																	&& TypeConvUtil.isNullOrEmpty(personEligibilityDto
																			.getCdPersEligPrgClosed())) {
																newPersonEligibilityDto
																		.setCdPersEligPrgClosed(ServiceConstants.CAPS);
																newPersonEligibilityDto.setDtPersEligEaDeny(date);
																serviceReqHeaderDto.setReqFuncCd(
																		ServiceConstants.UPDATE_DENY_DATE);
															}
															if (personEligibilityDto.getCdPersEligPrgOpen()
																	.equals(ServiceConstants.CAPS)
																	&& (!TypeConvUtil.isNullOrEmpty(personEligibilityDto
																			.getCdPersEligPrgClosed()))
																	&& personEligibilityDto.getCdPersEligPrgClosed()
																			.equals(ServiceConstants.STARS)) {
																newPersonEligibilityDto
																		.setCdPersEligPrgOpen(ServiceConstants.BOTH);
																newPersonEligibilityDto
																		.setCdPersEligPrgClosed(ServiceConstants.BOTH);
																newPersonEligibilityDto.setDtPersEligEaDeny(date);
																serviceReqHeaderDto.setReqFuncCd(
																		ServiceConstants.UPDATE_DENY_OPEN_CLOSE);
															}
															// CAUDC9D
															personEligibilityDao.personEligibilityAUD(
																	newPersonEligibilityDto, serviceReqHeaderDto);
														}
													}
												}
											}
										}
									}
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(stageEntity.getCapsCase())
									&& !TypeConvUtil.isNullOrEmpty(stageEntity.getCapsCase().getIdCase())) {
								// CCMNC5D
								capsCaseDto = capsCaseDao.getCaseDetails(stageEntity.getCapsCase().getIdCase());
								if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStage())
										&& !TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getCdStageProgram())) {
									if (closeStageCaseInputDto.getCdStage().equals(ServiceConstants.CSTAGES_ARI)
											&& closeStageCaseInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_CPS)) {
										// CSES63D
										if (!TypeConvUtil.isNullOrEmpty(closeStageCaseInputDto.getIdStage())) {
											adminReviewDao.getAdminReviewById(closeStageCaseInputDto.getIdStage());
										}
									}
								}
								if ((!TypeConvUtil.isNullOrEmpty(capsCaseDto)
										&& TypeConvUtil.isNullOrEmpty(capsCaseDto.getDtCaseClosed()))
										|| closeStageCaseInputDto.getCdStage()
												.equalsIgnoreCase(ServiceConstants.CSTAGES_ARI)) {
									// CCMNF6D retrieve stage ids that are not
									// closed by caseid
									StageCdInDto pCCMNF6DInputRec = new StageCdInDto();
									pCCMNF6DInputRec.setIdCase(capsCaseDto.getIdCase());
									List<StageCdOutDto> stageDtlsByDate = objCcmnf6dDao
											.getStageDtlsByDate(pCCMNF6DInputRec);
									// close situation and case if there are no
									// records
									boolean anyOpenStage = false;
									if (!TypeConvUtil.isNullOrEmpty(stageDtlsByDate)) {
										for (StageCdOutDto stageCdOutDto : stageDtlsByDate) {
											if (!stageCdOutDto.getIdStage().equals(stageEntity.getIdStage())) {
												anyOpenStage = true;
												break;
											}
										}
									}
									if (!anyOpenStage) {
										if (!closeStageCaseInputDto.getCdStage()
												.equalsIgnoreCase(ServiceConstants.CSTAGES_ARI)
												|| (TypeConvUtil.isNullOrEmpty(capsCaseDto.getDtCaseClosed())
														&& closeStageCaseInputDto.getCdStage()
																.equalsIgnoreCase(ServiceConstants.CSTAGES_ARI))) {
											// CINT24D - Read situation Record
											Situation situation = situationDao.getSituationEntityById(
													stageEntity.getSituation().getIdSituation());
											// CINT13D - Update situation record
											situation.setDtSituationClosed(new Date());
											situationDao.updateSituation(situation);
											// CCMNC6D - Close case
											CapsCase capsCase = capsCaseDao
													.getCapsCaseEntityById(capsCaseDto.getIdCase());
											capsCase.setDtCaseClosed(new Date());
											capsCaseDao.updateCapsCase(capsCase);
											// CCMN46D - Post case Closure event
											Event event = new Event();
											event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
											event.setCdEventType(ServiceConstants.EVENT_TYP_CAS);
											event.setTxtEventDescr(ServiceConstants.CASE_CLOSED);
											event.setDtEventOccurred(new Date());
											event.setDtLastUpdate(new Date());
											event.setDtEventCreated(new Date());
											event.setStage(stageDao.getStageEntityById(stageEntity.getIdStage()));
											event.setIdCase(capsCaseDto.getIdCase());
											eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_ADD);
											// CCMND1D - remove all todos
											// related to the case
											todoDao.toDoDeleteByCaseID(capsCaseDto.getIdCase());
											// Added the call for the records
											// retention service. This was
											// missed in the initial
											// implementation. Adding this code
											// as part of defect 10969 raised
											// during 2.0 warranty 
											RecordsRetnSaveInDto recordsRetnSaveInDto = new RecordsRetnSaveInDto();
											recordsRetnSaveInDto.setIdCase(stageEntity.getCapsCase().getIdCase());
											if (closeStageCaseInputDto.getCdStage()
												.equalsIgnoreCase(ServiceConstants.CSTAGES_ARI)) {
												recordsRetnSaveInDto.setIndRuledOutOrAdm(ServiceConstants.YES);
											} else {
												recordsRetnSaveInDto.setIndRuledOutOrAdm(ServiceConstants.NO);
											}
											recordsRetentionService.saveRecordsRetention(recordsRetnSaveInDto);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (HibernateException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return closeStageCaseOutputDto;
	}
}
