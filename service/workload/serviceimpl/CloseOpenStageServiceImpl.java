package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.*;
import java.util.stream.Stream;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.AdminReview;
import us.tx.state.dfps.common.domain.AdptAsstAppEventLink;
import us.tx.state.dfps.common.domain.AdptEligApplication;
import us.tx.state.dfps.common.domain.AdptEligRecert;
import us.tx.state.dfps.common.domain.AdptSubEventLink;
import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Characteristics;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPlanLink;
import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.FacilityInvstDtl;
import us.tx.state.dfps.common.domain.LicensingInvstDtl;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonCategory;
import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StageLink;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionOutputDto;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.SituationDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvstDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.ApsInvstDetailDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDto;
import us.tx.state.dfps.service.investigation.dto.FacilityInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.person.dao.PersonCategoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.AdoptionSubsidyDto;
import us.tx.state.dfps.service.workload.dto.AdptAsstAppEventLinkDto;
import us.tx.state.dfps.service.workload.dto.AdptSubEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.EventPlanLinkDto;
import us.tx.state.dfps.service.workload.dto.EventStagePersonDto;
import us.tx.state.dfps.service.workload.dto.GuardianshipDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.ReopenStageDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthEventDto;
import us.tx.state.dfps.service.workload.dto.ServiceAuthorizationDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;
import us.tx.state.dfps.service.workload.dto.StageResourceDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;
import us.tx.state.dfps.service.workload.service.WorkloadService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U
 * Description: This archived library function provides the necessary edits and
 * updates required to close a stage and open a new one. It generates all the
 * required events and to-do's related with the closure of a stage and the
 * opening of a new one. Apr 4, 2017 - 2:06:05 PM
 */

@Service
@Transactional
public class CloseOpenStageServiceImpl implements CloseOpenStageService {

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	IncomingDetailDao incomingDetailDao;

	@Autowired
	StageWorkloadDao stageWorkloadDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	PersonEligibilityDao personEligibilityDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	TodoCommonFunctionService todoCommonFunctionService;

	@Autowired
	ContactDao contactDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	AdminReviewDao adminReviewDao;

	@Autowired
	AllegtnDao allegtnDao;

	@Autowired
	ApsInvstDetailDao apsInvstDetailDao;

	@Autowired
	CpsInvstDetailDao cpsInvstDetailDao;

	@Autowired
	LicensingInvstDtlDao licensingInvstDtoDao;

	@Autowired
	FacilityInvstDtlDao facilityInvstDtlDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	PersonCategoryDao personCategoryDao;

	@Autowired
	PersonIdDao personIdDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	CloseStageCaseService closeStageCaseService;

	@Autowired
	PostEventService postEventService;

	@Autowired
	PlacementActPlannedDao placementActPlannedOutDto;
	
	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	@Autowired
	WorkloadService workloadService;
	
	@Autowired
	private EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;
	
	/**
	 * closeOpenStage This archived library function provides the necessary
	 * edits and updates required to close a stage and open a new one. It
	 * generates all the required events and to-do's related with the closure of
	 * a stage and the opening of a new one.
	 * 
	 * Service Name - CCMN03U
	 * 
	 * @param closeOpenStageInputDto
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CloseOpenStageOutputDto closeOpenStage(CloseOpenStageInputDto closeOpenStageInputDto) {
		CloseOpenStageOutputDto closeOpenStageOutputDto = new CloseOpenStageOutputDto();
		List<StageProgDto> stageProgDtoList = null;
		List<StageProgDto> newStageProgDtoList = null;
		StageDto stageDto = null;
		StageDto newNewStageDto = new StageDto();
		Date date = new Date();
		Date nullDate = null;
		Date time = null;
		Calendar todayCalendar = Calendar.getInstance();
		Calendar timeCalendar = Calendar.getInstance();
		todayCalendar.setTime(date);
		StagePersonLinkDto stagePersonLinkPRDto = null;
		StagePersonLinkDto newStagePersonLinkPRDto = null;
		boolean isAutoAdopt = false;
		boolean isNoAOCTodo = false;
		boolean isAgedChecked = false;
		boolean isClosePAD = false;
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		List<StageDto> stageDtoList = null;
		List<StagePersDto> stagePersDtoList = null;
		List<StagePersDto> newStagePersDtoList = null;
		List<StagePersDto> newStagePersDtoList1 = null;
		List<StagePersDto> newStagePersDtoList2 = null;
		List<StagePersDto> stagePersDtoListForAOC = null;
		List<StagePersDto> newNewStagePersDtoList = null;
		List<StagePersDto> newNewStagePersDtoList1 = null;
		List<StagePersDto> newNewStagePersDtoList2 = null;
		List<StagePersonLinkDto> newStagePersonLinkDtoList = null;
		List<StagePersonLinkDto> newStagePersonLinkDtoList1 = null;
		List<StagePersonLinkDto> newStagePersonLinkDtoList2 = null;
		List<StagePersonLinkDto> newStagePersonLinkDtoList3 = new ArrayList<>();
		List<StagePersonLinkDto> newStagePersonLinkDtoList4 = new ArrayList<>();
		List<StagePersonLinkDto> stagePersonLinkDtoList1 = null;
		int usPageNbr = ServiceConstants.INITIAL_PAGE;
		int lAPSCalculatedAge = ServiceConstants.Zero;
		int i = ServiceConstants.Zero;
		Long countSUBStages = ServiceConstants.ZERO_VAL;
		Long countStages = ServiceConstants.ZERO_VAL;
		CapsCaseDto capsCaseDto = null;
		CapsCaseDto newCapsCaseDto = new CapsCaseDto();
		List<StageResourceDto> stageResourceDtoList = null;
		Long idCase = ServiceConstants.ZERO_VAL;
		Long idSituation = ServiceConstants.ZERO_VAL;
		SituationDto newSituationDto = new SituationDto();
		TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
		TodoCommonFunctionInputDto newTodoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
		TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
		TodoCommonFunctionDto newTodoCommonFunctionDto = new TodoCommonFunctionDto();
		TodoCommonFunctionOutputDto todoCommonFunctionOutputDto = null;
		TodoCommonFunctionOutputDto newTodoCommonFunctionOutputDto = null;
		EventDto newNewEventDto = null;
		EventDto newEventDto = null;
		EventDto newNewNewEventDto = null;
		EventDto newNewNewNewEventDto = null;
		EventDto newEventDto2 = null;
		EventDto newEventDto3 = null;
		EventDto newEventDto4 = null;
		EventDto newEventDto5 = null;
		Long ulIdConclusionEvent = ServiceConstants.ZERO_VAL;
		Long ulIdServicePlanEvent = ServiceConstants.ZERO_VAL;
		ContactDto newContactDto = new ContactDto();
		ContactDto newNewContactDto = new ContactDto();
		TodoDto todoDto = new TodoDto();
		AdminReviewDto adminReviewDto = new AdminReviewDto();
		FacilRtrvRes facilRtrvRes = null;
		List<IntakeAllegationDto> intakeAllegationDtoList = new ArrayList<>();
		Date dtDtIncomingCall = null;
		PersonDto personDto = null;
		List<PersonDto> personDtoList = null;
		List<PersonDto> personDtoList1 = null;
		List<PersonDto> personDtoList2 = null;
		List<CharacteristicsDto> charDtlList = null;
		CharacteristicsDto newCharacteristicsDto = new CharacteristicsDto();
		Long idPALWorker = ServiceConstants.ZERO_VAL;
		List<EventStagePersonDto> eventStagePersonDtoList = null;
		List<EventStagePersonDto> newEventStagePersonDtoList = null;
		List<EventStagePersonDto> newEventStagePersonDtoList1 = null;
		List<EventStagePersonDto> newEventStagePersonDtoList2 = null;
		List<EventStagePersonDto> newEventStagePersonDtoList3 = null;
		List<EventStagePersonDto> newEventStagePersonDtoList4 = null;
		GuardianshipDto guardianshipDto = null;
		SVCAuthEventDto svcAuthEventDto = null;
		ServiceAuthorizationDto serviceAuthorizationDto = null;
		List<SVCAuthDetailDto> svcAuthDetailDtoList = null;
		EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
		List<TodoDto> todoDtoList = null;
		AdoptionSubsidyDto adoptionSubsidyDto = new AdoptionSubsidyDto();
		AdptSubEventLinkDto adptSubEventLinkDto = new AdptSubEventLinkDto();
		Long idAdptEligApplication = ServiceConstants.ZERO_VAL;
		Long idAdptEligRecert = ServiceConstants.ZERO_VAL;
		AdptAsstAppEventLinkDto adptAsstAppEventLinkDto = new AdptAsstAppEventLinkDto();
		CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();

		if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto)) {
			if (!ServiceConstants.STRING_IND_Y.equals(closeOpenStageInputDto.getSysIndSStgOpenOnly())) {
				closeOpenStageInputDto.setPageNbr(ServiceConstants.INITIAL_PAGE);
				closeOpenStageInputDto.setPageSizeNbr(ServiceConstants.STAGE_PROG_SIZE);
				closeOpenStageInputDto.setReqFuncCd(ServiceConstants.STAGE_PROG_OLD_STAGE);
				// CCMNB8D
				stageProgDtoList = getStgProgroession(closeOpenStageInputDto);
				if (!TypeConvUtil.isNullOrEmpty(stageProgDtoList)) {
					closeOpenStageOutputDto.setRowQty(Integer.toString(stageProgDtoList.size()));
				}
			}
			// CINT21D
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
				stageDto = stageDao.getStageById(closeOpenStageInputDto.getIdStage());
				if (!ObjectUtils.isEmpty(stageDto) && !StringUtils.isEmpty(stageDto.getDtStageClose())
						&& !ServiceConstants.ADMIN_REVIEW.equals(closeOpenStageInputDto.getCdStageOpen())
						&& !ServiceConstants.FAD_REVIEW.equals(closeOpenStageInputDto.getCdStageOpen())) {
					closeOpenStageOutputDto.setActionResult(ServiceConstants.ARC_ERR_NO_PROC_RC);
					return closeOpenStageOutputDto;
				}
			}
			if (!ServiceConstants.STRING_IND_Y.equals(closeOpenStageInputDto.getSysIndSStgOpenOnly())) {
				closeOpenStageInputDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				// CCMND4D
				this.updateStageCloseRecord(closeOpenStageInputDto.getIdStage(), date,
						closeOpenStageInputDto.getCdStageReasonClosed(), closeOpenStageInputDto.getCdStage());
				// CCMN46D
				EventDto eventDto = new EventDto();
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
					eventDto.setIdStage(closeOpenStageInputDto.getIdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
					eventDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
				}
				eventDto.setDtEventOccurred(date);
				if (!TypeConvUtil.isNullOrEmpty(stageProgDtoList)) {
					for (StageProgDto stageProgDto : stageProgDtoList) {
						if (!TypeConvUtil.isNullOrEmpty(stageProgDto)) {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())
									&& !TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgOpen())) {
								if (closeOpenStageInputDto.getCdStageOpen().equals(stageProgDto.getCdStageProgOpen())) {
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getStageProgEvntDesc())) {
										eventDto.setEventDescr(stageProgDto.getStageProgEvntDesc());
									}
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgStatus())) {
										eventDto.setCdEventStatus(stageProgDto.getCdStageProgStatus());
									}
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgEventType())) {
										eventDto.setCdEventType(stageProgDto.getCdStageProgEventType());
									}
								}
							}
						}
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(eventDto.getCdEventType())) {
					if (eventDto.getCdEventType().equals(ServiceConstants.SERVICE_AUTH_TYPE)) {
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

						if (!TypeConvUtil.isNullOrEmpty(eventDto.getCdEventStatus())) {
							if (eventDto.getCdEventStatus().equals(ServiceConstants.EVENT_STATUS_PEND)) {
								eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
							}
						}
					}
				}
				// CCMN46D
				eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
				// CCMNG2D
				if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
					stagePersonLinkPRDto = stageWorkloadDao
							.getStagePersonLinkByStageRole(closeOpenStageInputDto.getIdStage());
				}
				// CCMND3D
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto)) {
					stagePersonLinkPRDto.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_CLOSED);
					stagePersonLinkPRDto.setIndStagePersEmpNew(ServiceConstants.STR_ZERO_VAL);
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						stagePersonLinkPRDto.setIdStage(closeOpenStageInputDto.getIdStage());
					}
					stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkPRDto, serviceReqHeaderDto);
				}

				// CCMND3D
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
				if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
					stagePersonLinkDao.deletePRSEStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
					if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.CSTAGES_INT)) {
						// CCMNH1D
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
							stagePersonLinkDao.deleteTodoForClosingStage(closeOpenStageInputDto.getIdStage());
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())
					&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
				if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.CSTAGES_FPR)
						&& !ServiceConstants.STRING_IND_Y.equals(closeOpenStageInputDto.getSysIndSStgOpenOnly())
						&& ServiceConstants.CSTAGES_INV.equals(closeOpenStageInputDto.getCdStage())) {

					closeOpenStageOutputDto.setRowQty(createOrUpdatePersonEligibility(closeOpenStageInputDto.getIdStage()));
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
				if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.SUBCARE)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.POST_ADOPT)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADOPTION)) {


					if (!ObjectUtils.isEmpty(stageDto) && !StringUtils.isEmpty(stageDto.getIdCase())) {
						if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							// CCMNF6D
							stageDtoList = stageDao.getOpenStageByIdCase(stageDto.getIdCase());
							if (!TypeConvUtil.isNullOrEmpty(stageDtoList)) {
								for (StageDto newStageDto : stageDtoList) {
									if (!TypeConvUtil.isNullOrEmpty(newStageDto.getCdStage())) {
										if (newStageDto.getCdStage().equals(closeOpenStageInputDto.getCdStageOpen())) {
											if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
												if (closeOpenStageInputDto.getCdStage()
														.equals(ServiceConstants.INVESTIGATION)
														&& closeOpenStageInputDto.getCdStageOpen()
																.equals(ServiceConstants.SERVICE_DELIVERY)) {
													if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())
															&& !TypeConvUtil.isNullOrEmpty(newStageDto.getIdStage())) {
														// CLSC75D
														stagePersDtoList = stageDao.getStagePersByIdStage(
																closeOpenStageInputDto.getIdStage(),
																newStageDto.getIdStage());
													}
													if (!TypeConvUtil.isNullOrEmpty(stagePersDtoList)) {
														for (StagePersDto stagePersDto : stagePersDtoList) {
															// CCMND3D
															StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
															serviceReqHeaderDto
																	.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
															if (!TypeConvUtil.isNullOrEmpty(newStageDto.getIdStage())) {
																stagePersonLinkDto.setIdStage(newStageDto.getIdStage());
															}
															if (!TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getCdStagePersRole())) {
																stagePersonLinkDto.setCdStagePersRole(
																		stagePersDto.getCdStagePersRole());
															}
															if (!TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getCdStagePersType())) {
																stagePersonLinkDto.setCdStagePersType(
																		stagePersDto.getCdStagePersType());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getCdStagePersSearchInd())) {
																stagePersonLinkDto.setCdStagePersSearchInd(
																		stagePersDto.getCdStagePersSearchInd());
															}
															if (!TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getStagePersNotes())) {
																stagePersonLinkDto.setStagePersNotes(
																		stagePersDto.getStagePersNotes());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getIndStagePersInLaw())) {
																stagePersonLinkDto.setIndStagePersInLaw(
																		stagePersDto.getIndStagePersInLaw());
															}
															if (!TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getDtStagePersLink())) {
																stagePersonLinkDto.setDtStagePersLink(
																		stagePersDto.getDtStagePersLink());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getCdStagePersRelInt())) {
																stagePersonLinkDto.setCdStagePersRelInt(
																		stagePersDto.getCdStagePersRelInt());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getIndStagePersReporter())) {
																stagePersonLinkDto.setIndStagePersReporter(
																		stagePersDto.getIndStagePersReporter());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getIndStagePersEmpNew())) {
																stagePersonLinkDto.setIndStagePersEmpNew(
																		stagePersDto.getIndStagePersEmpNew());
															}
															if (!TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getIdPerson())) {
																stagePersonLinkDto
																		.setIdPerson(stagePersDto.getIdPerson());
															}
															if (!TypeConvUtil.isNullOrEmpty(
																	stagePersDto.getIdStagePersonLink())) {
																stagePersonLinkDto.setIdStagePersonLink(
																		stagePersDto.getIdStagePersonLink());
															}
															stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto,
																	serviceReqHeaderDto);
														}
													}
												}
											}
											closeOpenStageOutputDto.setActionResult(ServiceConstants.ARC_SUCCESS);
											return closeOpenStageOutputDto;
										}
									}
								}
							}
						} else {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								// CCMNB9D
								newStagePersonLinkDtoList = stagePersonLinkDao
										.getStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkDtoList)) {
								for (StagePersonLinkDto stagePersonLinkDto : newStagePersonLinkDtoList) {
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
										if (stagePersonLinkDto.getCdStagePersRole()
												.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)) {
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
												// CLSC45D
												newStagePersDtoList = null;
												newStagePersDtoList = stageDao
														.getStagesByIdPerson(stagePersonLinkDto.getIdPerson());
											}
											if (!TypeConvUtil.isNullOrEmpty(newStagePersDtoList)) {
												for (StagePersDto stagePersDto : newStagePersDtoList) {
													if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStage())) {
														if (stagePersDto.getCdStage().equals(ServiceConstants.AGE_OUT)
																|| stagePersDto.getCdStage()
																		.equals(ServiceConstants.SERVICE_DELIVERY)) {
															boolean stageCloseDateCheck = false;
															if (TypeConvUtil
																	.isNullOrEmpty(stagePersDto.getDtStageClose())) {
																stageCloseDateCheck = true;
															}
															if (stagePersDto.getDtStageClose()
																	.equals(ServiceConstants.GENERIC_END_DATE)) {
																stageCloseDateCheck = true;
															}
															if (stageCloseDateCheck) {
																if (!TypeConvUtil
																		.isNullOrEmpty(stagePersonLinkDto.getIdPerson())
																		&& !TypeConvUtil
																				.isNullOrEmpty(stagePersDto.getIdCase())
																		&& !TypeConvUtil.isNullOrEmpty(
																				stagePersDto.getCdStage())) {
																	// CSEC29D
																	newNewStagePersDtoList = null;
																	newNewStagePersDtoList = stageDao
																			.getStagesByAttributes(
																					stagePersonLinkDto.getIdPerson(),
																					ServiceConstants.STAGE_PERS_ROLE_CL,
																					stagePersDto.getIdCase(),
																					stagePersDto.getCdStage());
																}
																if (!TypeConvUtil
																		.isNullOrEmpty(newNewStagePersDtoList)) {
																	closeOpenStageOutputDto.setActionResult(
																			ServiceConstants.ARC_SUCCESS);
																	return closeOpenStageOutputDto;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} else if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)) {
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.SUBCARE)) {
						// CSES21D
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())) {
							countSUBStages = stageDao.getSUBOpenStagesCount(closeOpenStageInputDto.getScrIdPrimChild(),
									ServiceConstants.STAGE_PERS_ROLE_CL);
						}
						if (!TypeConvUtil.isNullOrEmpty(countSUBStages)) {
							closeOpenStageOutputDto.setActionResult(ServiceConstants.ARC_ERR_NO_PROC_RC);
							return closeOpenStageOutputDto;
						}
					} else {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())) {
							if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)) {
								// CSES94D
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.CLOSED_STAGES);
								countStages = stageDao.getOpenStagesCount(closeOpenStageInputDto.getScrIdPrimChild(),
										ServiceConstants.PERSON_ROLE_PRIM_CHILD, ServiceConstants.PREP_ADULT,
										serviceReqHeaderDto);
								if (TypeConvUtil.isNullOrEmpty(countStages)) {
									closeOpenStageOutputDto.setActionResult(ServiceConstants.ARC_ERR_NO_PROC_RC);
									return closeOpenStageOutputDto;
								}
							} else {
								// CLSC45D
								stagePersDtoListForAOC = stageDao
										.getStagesByIdPerson(closeOpenStageInputDto.getScrIdPrimChild());
								if (!TypeConvUtil.isNullOrEmpty(stagePersDtoListForAOC)) {
									for (StagePersDto stagePersDto : stagePersDtoListForAOC) {
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStage())) {
											if (stagePersDto.getCdStage()
													.equals(closeOpenStageInputDto.getCdStageOpen())) {
												boolean stageCloseDateCheck = false;
												if (TypeConvUtil.isNullOrEmpty(stagePersDto.getDtStageClose())) {
													stageCloseDateCheck = true;
												}
												if (ServiceConstants.GENERIC_END_DATE
														.equals(stagePersDto.getDtStageClose())) {
													stageCloseDateCheck = true;
												}
												if (stageCloseDateCheck) {
													if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIdCase())) {
														// CSEC29D
														newNewStagePersDtoList1 = stageDao.getStagesByAttributes(
																closeOpenStageInputDto.getScrIdPrimChild(),
																ServiceConstants.PERSON_ROLE_PRIM_CHILD,
																stagePersDto.getIdCase(),
																closeOpenStageInputDto.getCdStageOpen());
													}
													if (!CollectionUtils.isEmpty(newNewStagePersDtoList1)) {
														closeOpenStageOutputDto
																.setActionResult(ServiceConstants.ARC_SUCCESS);
														
														return closeOpenStageOutputDto;
													}
												}
											}
										}
									}
								}
							}
						} else {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								// CCMNB9D
								newStagePersonLinkDtoList1 = stagePersonLinkDao
										.getStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkDtoList1)) {
								for (StagePersonLinkDto stagePersonLinkDto : newStagePersonLinkDtoList1) {
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
										if (stagePersonLinkDto.getCdStagePersRole()
												.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)) {
											if (closeOpenStageInputDto.getCdStageOpen()
													.equals(ServiceConstants.PREP_ADULT)) {
												if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
													closeOpenStageInputDto
															.setScrIdPrimChild(stagePersonLinkDto.getIdPerson());
												}
												// CSES94D
												serviceReqHeaderDto.setReqFuncCd(ServiceConstants.CLOSED_STAGES);
												countStages = stageDao.getOpenStagesCount(
														closeOpenStageInputDto.getScrIdPrimChild(),
														ServiceConstants.PERSON_ROLE_PRIM_CHILD,
														ServiceConstants.PREP_ADULT, serviceReqHeaderDto);
												if (TypeConvUtil.isNullOrEmpty(countStages)) {
													closeOpenStageOutputDto
															.setActionResult(ServiceConstants.ARC_ERR_NO_PROC_RC);
													return closeOpenStageOutputDto;
												}
											} else {
												// CLSC45D
												newStagePersDtoList1 = stageDao
														.getStagesByIdPerson(stagePersonLinkDto.getIdPerson());
												if (!TypeConvUtil.isNullOrEmpty(newStagePersDtoList1)) {
													for (StagePersDto stagePersDto : newStagePersDtoList1) {
														if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStage())) {
															if (stagePersDto.getCdStage()
																	.equals(closeOpenStageInputDto.getCdStageOpen())) {
																boolean stageCloseDateCheck = false;
																if (TypeConvUtil.isNullOrEmpty(
																		stagePersDto.getDtStageClose())) {
																	stageCloseDateCheck = true;
																}
																if (stagePersDto.getDtStageClose()
																		.equals(ServiceConstants.GENERIC_END_DATE)) {
																	stageCloseDateCheck = true;
																}
																if (stageCloseDateCheck) {
																	// CSEC29D
																	if (!TypeConvUtil.isNullOrEmpty(
																			stagePersonLinkDto.getIdPerson())
																			&& !TypeConvUtil.isNullOrEmpty(
																					stagePersDto.getIdCase())) {
																		newNewStagePersDtoList2 = stageDao
																				.getStagesByAttributes(
																						stagePersonLinkDto
																								.getIdPerson(),
																						ServiceConstants.PERSON_ROLE_PRIM_CHILD,
																						stagePersDto.getIdCase(),
																						closeOpenStageInputDto
																								.getCdStageOpen());
																	}
																	if (!TypeConvUtil
																			.isNullOrEmpty(newNewStagePersDtoList2)) {
																		if (!TypeConvUtil.isNullOrEmpty(
																				stagePersDto.getCdStage())) {
																			if (!stagePersDto.getCdStage().equals(
																					ServiceConstants.ADOPTION)) {
																				closeOpenStageOutputDto.setActionResult(
																						ServiceConstants.ARC_SUCCESS);
																				return closeOpenStageOutputDto;
																			} else if (TypeConvUtil.isNullOrEmpty(
																					closeOpenStageInputDto
																							.getScrIdPrimChild())) {
																				closeOpenStageOutputDto.setActionResult(
																						ServiceConstants.ARC_SUCCESS);
																				return closeOpenStageOutputDto;
																			} else {
																				isAutoAdopt = true;
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADOPTION)
						|| (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADOPTION)
								&& !isAutoAdopt)) {
					closeOpenStageInputDto.setReqFuncCd(ServiceConstants.STAGE_PROG_NEW_STAGE);
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD)) {
						closeOpenStageInputDto.setReqFuncCd(ServiceConstants.STAGE_PROG_OLD_STAGE);
					}
					if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageReasonClosed())) {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage()) &&
								!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW) &&
								!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)) {
							closeOpenStageInputDto.setCdStageReasonClosed(closeOpenStageInputDto.getCdStage());
						}else if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW))
						{
							closeOpenStageInputDto.setCdStageReasonClosed(ServiceConstants.ADMIN_REVIEW);
						}else if(closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)){
							closeOpenStageInputDto.setCdStageReasonClosed(ServiceConstants.FAD_REVIEW);
						}
					}
					// CCMNB8D
					newStageProgDtoList = this.getStgProgroession(closeOpenStageInputDto);
					newNewStageDto.setCdStage(closeOpenStageInputDto.getCdStageOpen());
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
						newNewStageDto.setCdStageClassification(ServiceConstants.ADULT_PROTECTIVE_SERVICES);
					} else {
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageClassification())) {
								newNewStageDto.setCdStageClassification(stageDto.getCdStageClassification());
							}
						}
					}
					if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
						if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageCnty())) {
							newNewStageDto.setCdStageCnty(stageDto.getCdStageCnty());
						}
					}
					if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.SUBCARE)
							&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADOPTION)
							&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)) {
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FRE_PROGRAM)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
									capsCaseDto = capsCaseDao.getCapsCaseByid(stageDto.getIdCase());
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(capsCaseDto)) {
								if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getNmCase())) {
									newNewStageDto.setNmStage(capsCaseDto.getNmCase());
								}
							}
						} else if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)
								&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getNmPersonFull())) {
							newNewStageDto.setNmStage(closeOpenStageInputDto.getNmPersonFull());
						} else {
							if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
									newNewStageDto.setNmStage(stageDto.getNmStage());
								}
							}
						}
					} else if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getNmPersonFull())) {
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
								newNewStageDto.setNmStage(stageDto.getNmStage());
							}
						}
					} else {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getNmPersonFull())) {
							newNewStageDto.setNmStage(closeOpenStageInputDto.getNmPersonFull());
						}
					}
					if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getDtStageStart())) {
						newNewStageDto.setDtStageStart(date);
					} else {
						newNewStageDto.setDtStageStart(closeOpenStageInputDto.getDtStageStart());
					}
					newNewStageDto.setDtStageClose(nullDate);
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)
							|| closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)) {
						newNewStageDto.setDtStageClose(ServiceConstants.GENERIC_END_DATE);
					}
					if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
						if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
							newNewStageDto.setIdCase(stageDto.getIdCase());
						}

						if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdUnit())) {
							newNewStageDto.setIdUnit(stageDto.getIdUnit());
						}

						if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdSituation())) {
							newNewStageDto.setIdSituation(stageDto.getIdSituation());
						}

						if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageCurrPriority())) {
							newNewStageDto.setCdStageCurrPriority(stageDto.getCdStageCurrPriority());
							newNewStageDto.setCdStageInitialPriority(stageDto.getCdStageCurrPriority());
						}

						if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageRegion())) {
							newNewStageDto.setCdStageRegion(stageDto.getCdStageRegion());
						}
					}
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
						newNewStageDto.setCdStageProgram(ServiceConstants.ADULT_PROTECTIVE_SERVICES);
					} else {
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageProgram())) {
								newNewStageDto.setCdStageProgram(stageDto.getCdStageProgram());
							}
						}
					}
					if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.SUBCARE)) {
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
								boolean checkStageType = false;
								if (!ObjectUtils.isEmpty(newStageProgDtoList)) {
									if (!TypeConvUtil.isNullOrEmpty(newStageProgDtoList.get(0))) {
										if (!TypeConvUtil
												.isNullOrEmpty(newStageProgDtoList.get(0).getCdStageProgStageType())) {
											if (!stageDto.getCdStageType().equals(ServiceConstants.CASE_REL_SPEC_REQ)) {
												checkStageType = true;
												newNewStageDto.setCdStageType(
														newStageProgDtoList.get(0).getCdStageProgStageType());
												closeOpenStageOutputDto.setCdStageType(
														newStageProgDtoList.get(0).getCdStageProgStageType());
											}
										}
									}
								}
								if (!checkStageType) {
									newNewStageDto.setCdStageType(stageDto.getCdStageType());
									closeOpenStageOutputDto.setCdStageType(stageDto.getCdStageType());
								}
							}
						}
					} else {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
							if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.CSTAGES_INT)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
									if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
										newNewStageDto.setCdStageType(stageDto.getCdStageType());
										closeOpenStageOutputDto.setCdStageType(stageDto.getCdStageType());
									}
								}
							} else {
								newNewStageDto.setCdStageType(ServiceConstants.STAGE_TYPE_REG);
								closeOpenStageOutputDto.setCdStageType(ServiceConstants.STAGE_TYPE_REG);
							}
						}
					}
					if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)
							|| (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.POST_ADOPT)
									&& !closeOpenStageInputDto.getCdStage().equals(ServiceConstants.CSTAGES_INT))) {
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							newCapsCaseDto.setCdCaseProgram(ServiceConstants.ADULT_PROTECTIVE_SERVICES);
						} else {
							if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageProgram())) {
									newCapsCaseDto.setCdCaseProgram(stageDto.getCdStageProgram());
								}
							}
						}
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageCnty())) {
								newCapsCaseDto.setCdCaseCounty(stageDto.getCdStageCnty());
							}
						}
						newCapsCaseDto.setDtCaseOpened(date);
						newCapsCaseDto.setDtCaseClosed(nullDate);
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
									newCapsCaseDto.setNmCase(stageDto.getNmStage());
								}
							}
						} else {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								// CCMNB9D
								newStagePersonLinkDtoList2 = stagePersonLinkDao
										.getStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkDtoList2)) {
								for (StagePersonLinkDto stagePersonLinkDto : newStagePersonLinkDtoList2) {
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
										if (stagePersonLinkDto.getCdStagePersRole()
												.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)) {
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
												closeOpenStageInputDto
														.setScrIdPrimChild(stagePersonLinkDto.getIdPerson());
											}
										}
									}
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())) {
								// CLSS63D
								stageResourceDtoList = stagePersonLinkDao.getStageResourceForChild(
										closeOpenStageInputDto.getScrIdPrimChild(), ServiceConstants.PLCMT_ACTUAL_TYPE);
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDtoList)) {
								if (!TypeConvUtil.isNullOrEmpty(stageResourceDtoList.get(0))) {
									if (!TypeConvUtil.isNullOrEmpty(stageResourceDtoList.get(0).getNmResource())) {
										newCapsCaseDto.setNmCase(stageResourceDtoList.get(0).getNmResource());
									} else {
										return closeOpenStageOutputDto;
									}
								} else {
									return closeOpenStageOutputDto;
								}
							} else {
								return closeOpenStageOutputDto;
							}
						}
						if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageRegion())) {
								newCapsCaseDto.setCdCaseRegion(stageDto.getCdStageRegion());
							}
						}
						// CCMNB2D
						idCase = this.capsCaseAUD(newCapsCaseDto, ServiceConstants.REQ_FUNC_CD_ADD);
						if (!TypeConvUtil.isNullOrEmpty(idCase)) {
							newSituationDto.setIdCase(idCase);
							newNewStageDto.setIdCase(idCase);
						}
						newSituationDto.setDtSituationOpened(date);
						newSituationDto.setDtSituationClosed(nullDate);
						newSituationDto.setSitOccurrence(ServiceConstants.SITUATION_OCCUR);
						// CINT13D
						idSituation = this.situationAUD(newSituationDto, ServiceConstants.REQ_FUNC_CD_ADD);
						if (!TypeConvUtil.isNullOrEmpty(idSituation)) {
							newNewStageDto.setIdSituation(idSituation);
						}
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							newNewStageDto.setCdStageType(ServiceConstants.STAGE_TYPE_REG);
						}
					}
					// CINT12D
					/**
					 * SUB stage events(FCA,PPT),FSU and Person Removal events
					 * will be created
					 *///untill here appers good
					Long idStage = this.stageAUD(newNewStageDto, ServiceConstants.REQ_FUNC_CD_ADD);
					closeOpenStageOutputDto.setIdStage(idStage);
					if (!ObjectUtils.isEmpty(newStageProgDtoList)) {
						for (StageProgDto stageProgDto : newStageProgDtoList) { 
							newEventDto=new EventDto();
							if (!StringUtils.isEmpty(stageProgDto.getCdStageProgEventType())
									&& !StringUtils.isEmpty(stageProgDto.getCdStageProgRsnClose())) {
								if (ServiceConstants.FRE_PROGRAM.equals(stageProgDto.getCdStageProgRsnClose())
										&& ServiceConstants.PREP_ADULT.equals(stageProgDto.getCdStageProgOpen())
										|| ServiceConstants.PAL_ILS_EVENT_TYPE
												.equals(stageProgDto.getCdStageProgEventType())
												&& ServiceConstants.PREP_ADULT
														.equals(stageProgDto.getCdStageProgStage())) {
									/* do nothing */
								} else if (!ServiceConstants.FAMILY_PRES_TASK.equals(stageProgDto.getCdStageProgTask())) {
									// CCMN46D//FAD should come here
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										newEventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
									}
									newEventDto.setDtEventOccurred(date);
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgTask())) {
										newEventDto.setCdTask(stageProgDto.getCdStageProgTask());
									}
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getStageProgEvntDesc())) {
										newEventDto.setEventDescr(stageProgDto.getStageProgEvntDesc());
									}
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgStatus())) {
										newEventDto.setCdEventStatus(stageProgDto.getCdStageProgStatus());
									}
									if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgEventType())) {
										newEventDto.setCdEventType(stageProgDto.getCdStageProgEventType());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
										newEventDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
									}
									// CCMN46D
									newNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, newEventDto);
								}
							}
							if (!ObjectUtils.isEmpty(stagePersonLinkPRDto)
									&& !StringUtils.isEmpty(stagePersonLinkPRDto.getIdPerson())
									&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)
									&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)) {
								// CCMNG2D
								if (!StringUtils.isEmpty(closeOpenStageInputDto.getIdStage())) {
									newStagePersonLinkPRDto = stageWorkloadDao
											.getStagePersonLinkByStageRole(closeOpenStageInputDto.getIdStage());
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgTodoInfo())
									&& !TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgEventType())
									&& !TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgStage())) {
								if (!(stageProgDto.getCdStageProgEventType().equals(ServiceConstants.PAL_ILS_EVENT_TYPE)
										&& stageProgDto.getCdStageProgStage().equals(ServiceConstants.PREP_ADULT))) {
									todoCommonFunctionDto.setSysCdTodoCf(stageProgDto.getCdStageProgTodoInfo());
									if (stageProgDto.getCdStageProgTodoInfo()
											.equals(ServiceConstants.TODO_INFO_APS002)) {
										todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.TODO_INV_45_DAY);
									}
									if (stageProgDto.getCdStageProgTodoInfo()
											.equals(ServiceConstants.TODO_INFO_APS003)) {
										todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.TODO_SVC_60_DAY);
									}
									if (!ObjectUtils.isEmpty(newStagePersonLinkPRDto)
											&& !StringUtils.isEmpty(newStagePersonLinkPRDto.getIdPerson())) {
										todoCommonFunctionDto
												.setSysIdTodoCfPersCrea(newStagePersonLinkPRDto.getIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
										todoCommonFunctionDto
												.setSysIdTodoCfPersWkr(closeOpenStageInputDto.getIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										todoCommonFunctionDto.setSysIdTodoCfStage(closeOpenStageOutputDto.getIdStage());
									}
									if (!TypeConvUtil.isNullOrEmpty(newNewEventDto)) {
										if (!TypeConvUtil.isNullOrEmpty(newNewEventDto.getIdEvent())) {
											todoCommonFunctionDto.setSysIdTodoCfEvent(newNewEventDto.getIdEvent());
										}
									}
									if (ServiceConstants.TODO_CODE.equals(stageProgDto.getCdStageProgTodoInfo())) {
										todoCommonFunctionDto.setSysTxtTodoCfDesc(stageProgDto.getStageProgEvntDesc());
									}
									if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getDtStageStart())) {
										todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(date);
									} else {
										todoCommonFunctionDto
												.setDtSysDtTodoCfDueFrom(closeOpenStageInputDto.getDtStageStart());
									}
									todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
									// CSUB40U
									todoCommonFunctionOutputDto = todoCommonFunctionService
											.TodoCommonFunction(todoCommonFunctionInputDto);
									if (!ObjectUtils.isEmpty(todoCommonFunctionOutputDto)
											&& !StringUtils.isEmpty(todoCommonFunctionOutputDto.getCdTodoTask())
											&& ServiceConstants.SVC_CD_TASK_CONTACT_APS
													.equalsIgnoreCase(todoCommonFunctionOutputDto.getCdTodoTask())) {
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionOutputDto.getIdEvent())) {
											newContactDto.setIdEvent(todoCommonFunctionOutputDto.getIdEvent());
										}
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionOutputDto.getIdStage())) {
											newContactDto.setIdContactStage(todoCommonFunctionOutputDto.getIdStage());
										}
										newContactDto.setDtCntctMnthlySummBeg(nullDate);
										newContactDto.setDtCntctMnthlySummEnd(nullDate);
										newContactDto.setDtContactOccurred(nullDate);
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
											if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
												newContactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS45);
											} else if (closeOpenStageInputDto.getCdStage()
													.equals(ServiceConstants.INVESTIGATION)) {
												newContactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS60);
											}
										}
										// CSYS07D
										this.contactAUD(newContactDto, ServiceConstants.REQ_FUNC_CD_ADD);
									}
								}
							}
							if (ServiceConstants.CONCLUSION_EVENT.equals(newEventDto.getCdEventType())) {
								if (!ObjectUtils.isEmpty(newNewEventDto)
										&& !StringUtils.isEmpty(newNewEventDto.getIdEvent())) {
									ulIdConclusionEvent = newNewEventDto.getIdEvent();
								}
							} else if (ServiceConstants.EVENT_TYPE_PLN.equals(newEventDto.getCdEventType())
									|| ServiceConstants.CHILD_SER_PLAN.equals(newEventDto.getCdEventType())) {
								if (!ObjectUtils.isEmpty(newNewEventDto)
										&& !StringUtils.isEmpty(newNewEventDto.getIdEvent())) {
									ulIdServicePlanEvent = newNewEventDto.getIdEvent();
								}
							}
							if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getStageProgTodoDesc())
									&& TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgTodoInfo())) {
								// CCMN43D
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getCdStageProgTask())) {
									todoDto.setCdTodoTask(stageProgDto.getCdStageProgTask());
									if (stageProgDto.getCdStageProgTask().equals(ServiceConstants.FAMILY_PRES_TASK)) {
										todoDto.setCdTodoType(ServiceConstants.ALERT_TODO);
									} else {
										todoDto.setCdTodoType(ServiceConstants.TASK_TODO);
									}
								}
								todoDto.setDtTodoCreated(date);
								todoDto.setDtTodoCompleted(nullDate);
								if (!ObjectUtils.isEmpty(stageDto) && !StringUtils.isEmpty(stageDto.getIdCase())) {
									todoDto.setIdTodoCase(stageDto.getIdCase());
								}
								if (!ObjectUtils.isEmpty(newNewEventDto)
										&& !StringUtils.isEmpty(newNewEventDto.getIdEvent())) {
									todoDto.setIdTodoEvent(newNewEventDto.getIdEvent());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
									todoDto.setIdTodoPersAssigned(closeOpenStageInputDto.getIdPerson());
									todoDto.setIdTodoPersWorker(closeOpenStageInputDto.getIdPerson());
								} else if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkPRDto)
										&& !TypeConvUtil.isNullOrEmpty(newStagePersonLinkPRDto.getIdPerson())) {
									todoDto.setIdTodoPersAssigned(newStagePersonLinkPRDto.getIdPerson());
									todoDto.setIdTodoPersWorker(newStagePersonLinkPRDto.getIdPerson());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									todoDto.setIdTodoStage(closeOpenStageOutputDto.getIdStage());
								}
								todoDto.setTodoDesc(stageProgDto.getStageProgTodoDesc());
								if (!TypeConvUtil.isNullOrEmpty(stageProgDto.getStageProgDaysDue())) {
									if (stageProgDto.getStageProgDaysDue() == ServiceConstants.SPECIAL_DATE) {
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
											if (closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_APS)) {
												time = DateUtils.date(todayCalendar.get(Calendar.YEAR),
														todayCalendar.get(Calendar.MONTH) + 2, 20);
											} else if (closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_CPS)) {
												time = DateUtils.date(todayCalendar.get(Calendar.YEAR),
														todayCalendar.get(Calendar.MONTH), 15);
											}
										}
									} else {
										time = DateUtils.date(todayCalendar.get(Calendar.YEAR),
												todayCalendar.get(Calendar.MONTH) - 1,
												todayCalendar.get(Calendar.DAY_OF_MONTH)
														+ stageProgDto.getStageProgDaysDue());
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(time)) {
									timeCalendar.setTime(time);
									todoDto.setDtTodoDue(DateUtils.date(timeCalendar.get(Calendar.YEAR),
											timeCalendar.get(Calendar.MONTH) + 1,
											timeCalendar.get(Calendar.DAY_OF_MONTH)));
									todoDto.setDtTodoTaskDue(DateUtils.date(timeCalendar.get(Calendar.YEAR),
											timeCalendar.get(Calendar.MONTH) + 1,
											timeCalendar.get(Calendar.DAY_OF_MONTH)));
								}
								// CCMN43D
								todoDao.todoAUD(todoDto, serviceReqHeaderDto);
								if (ServiceConstants.CONTACT.equals(stageProgDto.getCdStageProgEventType())) {
									if (!ObjectUtils.isEmpty(newNewEventDto)
											&& !StringUtils.isEmpty(newNewEventDto.getIdEvent())) {
										newNewContactDto.setIdEvent(newNewEventDto.getIdEvent());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										newNewContactDto.setIdContactStage(closeOpenStageOutputDto.getIdStage());
									}
									newNewContactDto.setDtCntctMnthlySummBeg(nullDate);
									newNewContactDto.setDtCntctMnthlySummEnd(nullDate);
									newNewContactDto.setDtContactOccurred(nullDate);
									if (ServiceConstants.CAPS_PROG_APS
											.equals(closeOpenStageInputDto.getCdStageProgram())) {
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
											if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
												newNewContactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS45);
											} else if (closeOpenStageInputDto.getCdStage()
													.equals(ServiceConstants.INVESTIGATION)) {
												newNewContactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS60);
											}
										}
									} else if (ServiceConstants.CAPS_PROG_CPS
											.equals(closeOpenStageInputDto.getCdStageProgram())) {
										newNewContactDto.setCdContactType(ServiceConstants.BMTH);
									}
									this.contactAUD(newNewContactDto, ServiceConstants.REQ_FUNC_CD_ADD);
								}
							}
							if (ServiceConstants.ADMIN_REVIEW.equals(stageProgDto.getCdStageProgEventType())
									|| ServiceConstants.FAD_REVIEW.equals(stageProgDto.getCdStageProgEventType())) {
								// CAUDA3D
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									adminReviewDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!ObjectUtils.isEmpty(newNewEventDto)
										&& !StringUtils.isEmpty(newNewEventDto.getIdEvent())) {
									adminReviewDto.setIdEvent(newNewEventDto.getIdEvent());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())) {
									adminReviewDto.setIdPerson(closeOpenStageInputDto.getScrIdPrimChild());
								}
								if(!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getUserLogonId())) {
									adminReviewDto.setIdCreatedPerson(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));
									adminReviewDto.setIdLastUpdatePerson(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
									adminReviewDto.setIdStageRelated(closeOpenStageInputDto.getIdStage());
								}
								adminReviewDto.setDtAdminRvAppealNotif(nullDate);
								adminReviewDto.setDtAdminRvAppealReview(nullDate);
								adminReviewDto.setDtAdminRvDue(nullDate);
								adminReviewDto.setDtAdminRvEmgcyRel(nullDate);
								adminReviewDto.setDtAdminRvHearing(nullDate);
								adminReviewDto.setDtAdminRvReqAppeal(nullDate);
								adminReviewDto.setDtLastUpdate(date);
								this.adminReviewAUD(adminReviewDto, ServiceConstants.REQ_FUNC_CD_ADD);
							}
						} 
					}
					if (ServiceConstants.INTAKE.equals(closeOpenStageInputDto.getCdStage())) {
						if (ServiceConstants.CAPS_PROG_AFC.equals(closeOpenStageInputDto.getCdStageProgram())
								&& !StringUtils.isEmpty(closeOpenStageInputDto.getIdStage())) {
							// CINT09D
							facilRtrvRes = stageDao.getFacilityDetail(closeOpenStageInputDto.getIdStage());
						}
						usPageNbr = ServiceConstants.INITIAL_PAGE;
						do {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								// CCIN19D
								intakeAllegationDtoList = personDao
										.getIntakeAllegationByStageId(closeOpenStageInputDto.getIdStage());
							}
							for (IntakeAllegationDto intakeAllegationDto : intakeAllegationDtoList) {
								AllegationDetailDto allegationDetailDto = new AllegationDetailDto();
								allegationDetailDto.setCdAllegIncidentStage(ServiceConstants.INTAKE);
								if (!TypeConvUtil.isNullOrEmpty(intakeAllegationDto.getCdIntakeAllegType())) {
									allegationDetailDto.setCdAllegType(intakeAllegationDto.getCdIntakeAllegType());
								}
								if (!TypeConvUtil.isNullOrEmpty(intakeAllegationDto.getPersonByIdVictim())) {
									allegationDetailDto.setIdVictim(intakeAllegationDto.getPersonByIdVictim());
								}
								if (!TypeConvUtil
										.isNullOrEmpty(intakeAllegationDto.getPersonByIdAllegedPerpetrator())) {
									allegationDetailDto.setIdAllegedPerpetrator(
											intakeAllegationDto.getPersonByIdAllegedPerpetrator());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									allegationDetailDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
									if (!closeOpenStageInputDto.getCdStageProgram()
											.equals(ServiceConstants.CAPS_PROG_CPS)
											&& !closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_CCL)
											&& !closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_RCL)) {
										if (!TypeConvUtil.isNullOrEmpty(intakeAllegationDto.getIntakeAllegDuration())) {
											allegationDetailDto
													.setAllegDuration(intakeAllegationDto.getIntakeAllegDuration());
										}
									}
								}
								// CINV07D
								Long idAllegation = this.allegationAUD(allegationDetailDto,
										ServiceConstants.REQ_FUNC_CD_ADD);
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
									if (closeOpenStageInputDto.getCdStageProgram()
											.equals(ServiceConstants.CAPS_PROG_AFC)) {
										FacilAllegDto facilAllegDto = new FacilAllegDto();
										if (!TypeConvUtil.isNullOrEmpty(idAllegation)) {
											facilAllegDto.setIdAllegation(idAllegation);
										}
										if (!ObjectUtils.isEmpty(facilRtrvRes)) {
											if (!TypeConvUtil.isNullOrEmpty(facilRtrvRes.getIndIncmgOnGrnds())) {
												facilAllegDto
														.setIndFacilAllegAbOffGr(facilRtrvRes.getIndIncmgOnGrnds());
											}
											if (!TypeConvUtil.isNullOrEmpty(facilRtrvRes.getIndIncmgFacilAbSupvd())) {
												facilAllegDto
														.setIndFacilAllegSupvd(facilRtrvRes.getIndIncmgFacilAbSupvd());
											}
										}
										// CINVB4D
										this.facilAllegSave(facilAllegDto);
									}
								}
							}
							usPageNbr++;
						} while (intakeAllegationDtoList.size() > usPageNbr
								* ServiceConstants.INTAKE_ALLEGATION_ROW_NUM);
					}
					if (ServiceConstants.INVESTIGATION.equals(closeOpenStageInputDto.getCdStageOpen())
							&& (!ServiceConstants.ADMIN_REVIEW.equals(closeOpenStageInputDto.getCdStageOpen())
									|| !ServiceConstants.FAD_REVIEW.equals(closeOpenStageInputDto.getCdStageOpen()))) {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
							if (closeOpenStageInputDto.getCdStageProgram().equals(ServiceConstants.CAPS_PROG_APS)) {
								ApsInvstDetailDto apsInvstDetailDto = new ApsInvstDetailDto();
								// CINV24D
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									apsInvstDetailDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(ulIdConclusionEvent)) {
									apsInvstDetailDto.setIdEvent(ulIdConclusionEvent);
								}
								apsInvstDetailDto.setDtApsInvstBegun(date);
								apsInvstDetailDto.setDtApsInvstCltAssmt(nullDate);
								apsInvstDetailDto.setDtApsInvstCmplt(nullDate);
								// CINV24D
								this.apsInvestDetailAUD(apsInvstDetailDto, ServiceConstants.REQ_FUNC_CD_ADD,
										ServiceConstants.EMPTY_STRING);
							} else if (closeOpenStageInputDto.getCdStageProgram()
									.equals(ServiceConstants.CAPS_PROG_CPS)) {
								CpsInvstDetailDto cpsInvstDetailDto = new CpsInvstDetailDto();
								// CINV12D
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									cpsInvstDetailDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(ulIdConclusionEvent)) {
									cpsInvstDetailDto.setIdEvent(ulIdConclusionEvent);
								}
								cpsInvstDetailDto.setDtCpsInvstDtlBegun(nullDate);
								cpsInvstDetailDto.setDtCpsInvstDtlAssigned(date);
								cpsInvstDetailDto.setDtCpsInvstDtlComplt(nullDate);
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
									if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
										if (!TypeConvUtil.isNullOrEmpty(stageDto.getDtStageStart())) {
											cpsInvstDetailDto.setDtCpsInvstDtlIntake(stageDto.getDtStageStart());
										}
									} else {
										cpsInvstDetailDto.setDtCpsInvstDtlIntake(nullDate);
									}
								}
								// CINV12D
								this.cpsInvestDetailAUD(cpsInvstDetailDto, ServiceConstants.REQ_FUNC_CD_ADD);
							} else if (closeOpenStageInputDto.getCdStageProgram().equals(ServiceConstants.CAPS_PROG_CCL)
									|| closeOpenStageInputDto.getCdStageProgram()
											.equals(ServiceConstants.CAPS_PROG_RCL)) {
								// CINV53D
								LicensingInvstDtlDto licensingInvstDtlDto = new LicensingInvstDtlDto();
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									licensingInvstDtlDto.setIdLicngInvstStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(ulIdConclusionEvent)) {
									licensingInvstDtlDto.setIdEvent(ulIdConclusionEvent);
								}
								licensingInvstDtlDto.setDtLicngInvstBegun(date);
								licensingInvstDtlDto.setDtLicngInvstAssigned(date);
								licensingInvstDtlDto.setDtLicngInvstComplt(nullDate);
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
									if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
										if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
											if (!TypeConvUtil.isNullOrEmpty(stageDto.getDtStageStart())) {
												licensingInvstDtlDto.setDtLicngInvstIntake(stageDto.getDtStageStart());
											}
										}
									} else {
										licensingInvstDtlDto.setDtLicngInvstIntake(nullDate);
									}
								}
								// CINV53D
								this.licensingInvstDtlAUD(licensingInvstDtlDto, ServiceConstants.REQ_FUNC_CD_ADD);
							} else if (closeOpenStageInputDto.getCdStageProgram()
									.equals(ServiceConstants.CAPS_PROG_AFC)) {
								// CINV54D
								FacilityInvstDtlDto facilityInvstDtlDto = new FacilityInvstDtlDto();
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									facilityInvstDtlDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(ulIdConclusionEvent)) {
									facilityInvstDtlDto.setIdEvent(ulIdConclusionEvent);
								}
								facilityInvstDtlDto.setDtFacilInvstBegun(nullDate);
								facilityInvstDtlDto.setDtFacilInvstIncident(nullDate);
								facilityInvstDtlDto.setDtFacilInvstComplt(nullDate);
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
									if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
											// CSEC54D
											dtDtIncomingCall = incomingDetailDao
													.getDtIncomingCallByIdStage(closeOpenStageInputDto.getIdStage());
										}
										if (!TypeConvUtil.isNullOrEmpty(dtDtIncomingCall)) {
											facilityInvstDtlDto.setDtFacilInvstIntake(dtDtIncomingCall);
										}
									} else {
										facilityInvstDtlDto.setDtFacilInvstIntake(nullDate);
									}
								}
								// CINV54D
								this.facilityInvstDtlAUD(facilityInvstDtlDto, ServiceConstants.REQ_FUNC_CD_ADD);
							}
						}
					}
					if (!StringUtils.isEmpty(closeOpenStageInputDto.getCdStageOpen())
							&& (Stream
									.of(ServiceConstants.SUBCARE, ServiceConstants.FPR_PROGRAM,
											ServiceConstants.FSU_PROGRAM, ServiceConstants.FRE_PROGRAM)
									.anyMatch(closeOpenStageInputDto.getCdStageOpen()::equals))
							&& !TypeConvUtil.isNullOrEmpty(ulIdServicePlanEvent)) {
						// CAUDE8D
						EventPlanLinkDto eventPlanLinkDto = new EventPlanLinkDto();
						eventPlanLinkDto.setIdEvent(ulIdServicePlanEvent);
						eventPlanLinkDto.setIndImpactCreated(ServiceConstants.STRING_IND_Y);
						this.eventPlanLinkAUD(eventPlanLinkDto, ServiceConstants.REQ_FUNC_CD_ADD);
					}
					if (!ServiceConstants.STRING_IND_Y.equals(closeOpenStageInputDto.getSysIndSStgOpenOnly())) {
						usPageNbr = ServiceConstants.INITIAL_PAGE;
						do {
							// CCMNB9D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								newStagePersonLinkDtoList3 = stagePersonLinkDao
										.getStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
							}
							for (StagePersonLinkDto stagePersonLinkDto : newStagePersonLinkDtoList3) {
								StagePersonLinkDto newStagePersonLinkDto = new StagePersonLinkDto();
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRelInt())) {
									newStagePersonLinkDto
											.setCdStagePersRelInt(stagePersonLinkDto.getCdStagePersRelInt());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
										&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())
										&& !TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
									if ((closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)
											&& closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.CAPS_PROG_APS))
											&& (stagePersonLinkDto.getCdStagePersRole()
													.equals(ServiceConstants.PERSON_ROLE_BOTH)
													|| stagePersonLinkDto.getCdStagePersRole()
															.equals(ServiceConstants.PERSON_ROLE_VICTIM))) {
										newStagePersonLinkDto.setCdStagePersRole(ServiceConstants.PERSON_ROLE_CLIENT);
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
											newStagePersonLinkDto
													.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
										}
									} else {
										if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.AGE_OUT)
										/*
										 * && stagePersonLinkDto.equals(
										 * ServiceConstants.
										 * PERSON_ROLE_PRIM_CHILD)
										 */) {
											newStagePersonLinkDto
													.setCdStagePersRole(ServiceConstants.PERSON_ROLE_CLIENT);
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
												newStagePersonLinkDto
														.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
											}
										} else if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.AGE_OUT)
												&& (stagePersonLinkDto.getCdStagePersRole()
														.equals(ServiceConstants.PERSON_ROLE_BOTH)
														|| stagePersonLinkDto.getCdStagePersRole()
																.equals(ServiceConstants.PERSON_ROLE_VICTIM)
														|| stagePersonLinkDto.getCdStagePersRole()
																.equals(ServiceConstants.PERSON_ROLE_PEPR))) {
											newStagePersonLinkDto.setCdStagePersRole(ServiceConstants.PERSON_ROLE_NONE);
											newStagePersonLinkDto
													.setCdStagePersType(ServiceConstants.PERSON_COLLATERAL);
										} else if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())
												&& !TypeConvUtil
														.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())
												&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())
												&& stagePersonLinkDto.getIdPerson()
														.equals(closeOpenStageInputDto.getScrIdPrimChild())
												&& (closeOpenStageInputDto.getCdStageOpen()
														.equals(ServiceConstants.SUBCARE)
														|| closeOpenStageInputDto.getCdStageOpen()
																.equals(ServiceConstants.ADOPTION)
														|| closeOpenStageInputDto.getCdStageOpen()
																.equals(ServiceConstants.POST_ADOPT)
														|| closeOpenStageInputDto.getCdStageOpen()
																.equals(ServiceConstants.PCA_STAGE)
														|| closeOpenStageInputDto.getCdStageOpen()
																.equals(ServiceConstants.PREP_ADULT))) {
											newStagePersonLinkDto
													.setCdStagePersRole(ServiceConstants.PERSON_ROLE_PRIM_CHILD);
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
												newStagePersonLinkDto
														.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
											}
										} else {
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
												newStagePersonLinkDto
														.setCdStagePersRole(stagePersonLinkDto.getCdStagePersRole());
											}
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
												newStagePersonLinkDto
														.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
											}
										}
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersSearchInd())) {
									newStagePersonLinkDto
											.setCdStagePersSearchInd(stagePersonLinkDto.getCdStagePersSearchInd());
								}
								newStagePersonLinkDto.setDtStagePersLink(date);
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
									newStagePersonLinkDto.setIdPerson(stagePersonLinkDto.getIdPerson());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									newStagePersonLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIndStagePersInLaw())) {
									newStagePersonLinkDto
											.setIndStagePersInLaw(stagePersonLinkDto.getIndStagePersInLaw());
								}
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIndStagePersReporter())) {
									newStagePersonLinkDto
											.setIndStagePersReporter(stagePersonLinkDto.getIndStagePersReporter());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
									if (closeOpenStageInputDto.getCdStageProgram()
											.equals(ServiceConstants.ADULT_PROTECTIVE_SERVICES)
											|| closeOpenStageInputDto.getCdStageProgram()
													.equals(ServiceConstants.ADULT_FACILITY)) {
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())
												&& !TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())
												&& !TypeConvUtil
														.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
											if (stagePersonLinkDto.getCdStagePersType()
													.equals(ServiceConstants.PERSON_ROLE_PRINCIPAL)
													&& (stagePersonLinkDto.getCdStagePersRole()
															.equals(ServiceConstants.PERSON_ROLE_VICTIM)
															|| stagePersonLinkDto.getCdStagePersRole()
																	.equals(ServiceConstants.PERSON_ROLE_BOTH))
													&& closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.INVESTIGATION)) {
												// CCMN44D
												if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
													personDto = personDao
															.getPersonById(stagePersonLinkDto.getIdPerson());
												}
												if (!TypeConvUtil.isNullOrEmpty(personDto)) {
													if (!TypeConvUtil.isNullOrEmpty(personDto.getDtPersonBirth())) {
														lAPSCalculatedAge = DateUtils.calculatePersonAge(personDto);
													} else {
														if (!TypeConvUtil.isNullOrEmpty(personDto.getPersonAge())) {
															lAPSCalculatedAge = personDto.getPersonAge();
														}
													}
												}
												if (lAPSCalculatedAge >= ServiceConstants.AGED_PERSON_AGE) {
													// CLSS60D
													if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
														charDtlList = characteristicsDao.getCharDtls(
																stagePersonLinkDto.getIdPerson(), date, date);
													}
													if (!TypeConvUtil.isNullOrEmpty(charDtlList)) {
														for (CharacteristicsDto characteristicsDto : charDtlList) {
															if (!isAgedChecked) {
																if (!TypeConvUtil.isNullOrEmpty(
																		characteristicsDto.getCdCharacteristic())
																		&& !TypeConvUtil
																				.isNullOrEmpty(characteristicsDto
																						.getCdCharCategory())) {
																	if (characteristicsDto.getCdCharacteristic()
																			.equals(ServiceConstants.AGED_CHARACTERISTIC)
																			&& characteristicsDto.getCdCharCategory()
																					.equals(ServiceConstants.APS_CHARACTERISTIC)) {
																		isAgedChecked = true;
																	}
																}
															}
														}
													}
													if (!isAgedChecked) {
														if (!TypeConvUtil
																.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
															newCharacteristicsDto
																	.setIdPerson(stagePersonLinkDto.getIdPerson());
														}
														newCharacteristicsDto
																.setCdCharCategory(ServiceConstants.APS_CHARACTERISTIC);
														newCharacteristicsDto.setCdCharacteristic(
																ServiceConstants.AGED_CHARACTERISTIC);
														newCharacteristicsDto.setDtCharStart(date);
														newCharacteristicsDto
																.setDtCharEnd(ServiceConstants.GENERIC_END_DATE);
														// CINV48D
														this.characteristicsAUD(newCharacteristicsDto,
																ServiceConstants.Zero, ServiceConstants.REQ_FUNC_CD_ADD,
																null);
														if (!TypeConvUtil
																.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
															personDto.setIdPerson(stagePersonLinkDto.getIdPerson());
														}
														personDto
																.setCdPersonChar(ServiceConstants.CHARACT_NA_NOT_CHECK);
														// CINV41D
														this.investigationPersonDtlAUD(personDto, null, null, null,
																ServiceConstants.WINDOW_MODE_PERSON);
													}
												}
											}
										}
									}
								}
								if (!ServiceConstants.PRIMARY_ROLE_STAGE_CLOSED
										.equals(stagePersonLinkDto.getCdStagePersRole())
										&& !ServiceConstants.PRIMARY_ROLE
												.equals(stagePersonLinkDto.getCdStagePersRole())) {
									if (!ServiceConstants.POST_ADOPT.equals(closeOpenStageInputDto.getCdStageOpen())) {
										// Production Defect#12702 - Set NMStage to true only StrNMStage has value
										// Defect#14155 setting true only if value is one.
										
										if (!ObjectUtils.isEmpty(stagePersonLinkDto.getStrIndNmStage()) 
												&& stagePersonLinkDto.getStrIndNmStage().trim().equals(ServiceConstants.STR_ONE_VAL)
												&& !stagePersonLinkDto.getCdStagePersType()
														.equals(ServiceConstants.STAFF_TYPE)) {
											newStagePersonLinkDto.setIndNmStage(Boolean.TRUE);
										} else {
											newStagePersonLinkDto.setIndNmStage(Boolean.FALSE);
										}

										// CCMND3D
										serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
										stagePersonLinkDao.getStagePersonLinkAUD(newStagePersonLinkDto,
												serviceReqHeaderDto);
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
												&& !TypeConvUtil
														.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())
												&& !TypeConvUtil
														.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
											if (closeOpenStageInputDto.getCdStage()
													.equals(ServiceConstants.INVESTIGATION)
													&& closeOpenStageInputDto.getCdStageProgram()
															.equals(ServiceConstants.CAPS_PROG_APS)
													&& (stagePersonLinkDto.getCdStagePersRole()
															.equals(ServiceConstants.PERSON_ROLE_BOTH)
															|| stagePersonLinkDto.getCdStagePersRole()
																	.equals(ServiceConstants.PERSON_ROLE_VICTIM))) {
												stagePersonLinkDto
														.setCdStagePersRole(ServiceConstants.PERSON_ROLE_CLIENT);
												if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
													stagePersonLinkDto.setIdStage(closeOpenStageInputDto.getIdStage());
												}
												// Production Defect#12702 - Set NMStage to true only StrNMStage has value
												// Defect#14155 setting true only if value is one.
												if (!ObjectUtils.isEmpty(stagePersonLinkDto.getStrIndNmStage())
														&& stagePersonLinkDto.getStrIndNmStage().trim().equals(ServiceConstants.STR_ONE_VAL)
														&& !stagePersonLinkDto.getCdStagePersType()
																.equals(ServiceConstants.STAFF_TYPE)) {
													stagePersonLinkDto.setIndNmStage(Boolean.TRUE);
												} else {
													stagePersonLinkDto.setIndNmStage(Boolean.FALSE);
												}

												// CCMND3D
												serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
												stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto,
														serviceReqHeaderDto);
											}
										}
									} else if (stagePersonLinkDto.getCdStagePersRole()
											.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)
											|| (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
													&& closeOpenStageInputDto.getCdStage()
															.equals(ServiceConstants.INTAKE)
													&& !TypeConvUtil
															.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())
													&& !stagePersonLinkDto.getCdStagePersType()
															.equals(ServiceConstants.STAFF_TYPE))) {
										// CCMND3D
										serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
										stagePersonLinkDao.getStagePersonLinkAUD(newStagePersonLinkDto,
												serviceReqHeaderDto);
									}
								}
							}
							usPageNbr++;
						} while (newStagePersonLinkDtoList3.size() > usPageNbr
								* ServiceConstants.STAGE_PERSON_LINK_ROW_NUM);
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
								&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
							if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)
									&& closeOpenStageInputDto.getCdStageOpen()
											.equals(ServiceConstants.SERVICE_DELIVERY)) {
								// CLSC75D
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())
										&& !TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									newStagePersDtoList2 = stageDao.getStagePersByIdStage(
											closeOpenStageInputDto.getIdStage(), closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(newStagePersDtoList2)) {
									for (StagePersDto stagePersDto : newStagePersDtoList2) {
										StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
											stagePersonLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStagePersRole())) {
											stagePersonLinkDto.setCdStagePersRole(stagePersDto.getCdStagePersRole());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStagePersType())) {
											stagePersonLinkDto.setCdStagePersType(stagePersDto.getCdStagePersType());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStagePersSearchInd())) {
											stagePersonLinkDto
													.setCdStagePersSearchInd(stagePersDto.getCdStagePersSearchInd());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getStagePersNotes())) {
											stagePersonLinkDto.setStagePersNotes(stagePersDto.getStagePersNotes());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIndStagePersInLaw())) {
											stagePersonLinkDto
													.setIndStagePersInLaw(stagePersDto.getIndStagePersInLaw());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getDtStagePersLink())) {
											stagePersonLinkDto.setDtStagePersLink(stagePersDto.getDtStagePersLink());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getCdStagePersRelInt())) {
											stagePersonLinkDto
													.setCdStagePersRelInt(stagePersDto.getCdStagePersRelInt());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIdStage())) {
											stagePersonLinkDto.setIdStage(stagePersDto.getIdStage());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIndStagePersReporter())) {
											stagePersonLinkDto
													.setIndStagePersReporter(stagePersDto.getIndStagePersReporter());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIndStagePersEmpNew())) {
											stagePersonLinkDto
													.setIndStagePersEmpNew(stagePersDto.getIndStagePersEmpNew());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIdPerson())) {
											stagePersonLinkDto.setIdPerson(stagePersDto.getIdPerson());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersDto.getIdStagePersonLink())) {
											stagePersonLinkDto
													.setIdStagePersonLink(stagePersDto.getIdStagePersonLink());
										}
										// CCMND3D
										serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
										stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto,
												serviceReqHeaderDto);
									}
								}
							}
						}
					} else {
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
							if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)) {
								usPageNbr = ServiceConstants.INITIAL_PAGE;
								do {
									// CCMNB9D
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
										newStagePersonLinkDtoList4 = stagePersonLinkDao
												.getStagePersonLinkByIdStage(closeOpenStageInputDto.getIdStage());
									}
									for (StagePersonLinkDto stagePersonLinkDto : newStagePersonLinkDtoList4) {
										StagePersonLinkDto newStagePersonLinkDto = new StagePersonLinkDto();
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
											newStagePersonLinkDto
													.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())
												&& !TypeConvUtil
														.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())) {
											if (stagePersonLinkDto.getIdPerson()
													.equals(closeOpenStageInputDto.getScrIdPrimChild())
													&& (closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.SUBCARE)
															|| closeOpenStageInputDto.getCdStageOpen()
																	.equals(ServiceConstants.ADOPTION)
															|| closeOpenStageInputDto.getCdStageOpen()
																	.equals(ServiceConstants.POST_ADOPT)
															|| closeOpenStageInputDto.getCdStageOpen()
																	.equals(ServiceConstants.PREP_ADULT))) {
												newStagePersonLinkDto
														.setCdStagePersRole(ServiceConstants.PERSON_ROLE_PRIM_CHILD);
											} else {
												if (!TypeConvUtil
														.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRole())) {
													if (closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.AGE_OUT)
															&& stagePersonLinkDto.getCdStagePersRole()
																	.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)) {
														newStagePersonLinkDto.setCdStagePersRole(
																ServiceConstants.PERSON_ROLE_CLIENT);
													} else if (closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.AGE_OUT)
															&& (stagePersonLinkDto.getCdStagePersRole()
																	.equals(ServiceConstants.PERSON_ROLE_BOTH)
																	|| stagePersonLinkDto.getCdStagePersRole()
																			.equals(ServiceConstants.PERSON_ROLE_VICTIM)
																	|| stagePersonLinkDto.getCdStagePersRole()
																			.equals(ServiceConstants.PERSON_ROLE_NONE)
																	|| stagePersonLinkDto.getCdStagePersRole().equals(
																			ServiceConstants.PERSON_ROLE_PEPR))) {
														newStagePersonLinkDto
																.setCdStagePersRole(ServiceConstants.PERSON_ROLE_NONE);
														newStagePersonLinkDto
																.setCdStagePersType(ServiceConstants.PERSON_COLLATERAL);
													} else if (stagePersonLinkDto.getCdStagePersRole()
															.equals(ServiceConstants.PERSON_ROLE_PRIM_CHILD)
															&& (closeOpenStageInputDto.getCdStageOpen()
																	.equals(ServiceConstants.FPR_PROGRAM)
																	|| closeOpenStageInputDto.getCdStageOpen()
																			.equals(ServiceConstants.FRE_PROGRAM))) {
														newStagePersonLinkDto
																.setCdStagePersRole(ServiceConstants.PERSON_ROLE_BOTH);
													} else if (closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.SUBCARE)
															|| closeOpenStageInputDto.getCdStageOpen()
																	.equals(ServiceConstants.FSU_PROGRAM)) {
														newStagePersonLinkDto
																.setCdStagePersRole(ServiceConstants.PERSON_ROLE_NONE);
													} else {
														newStagePersonLinkDto.setCdStagePersRole(
																stagePersonLinkDto.getCdStagePersRole());
													}
												}
											}
										}
										newStagePersonLinkDto.setDtStagePersLink(date);
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
											newStagePersonLinkDto.setIdPerson(stagePersonLinkDto.getIdPerson());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersRelInt())) {
											newStagePersonLinkDto
													.setCdStagePersRelInt(stagePersonLinkDto.getCdStagePersRelInt());
										}
										if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
											newStagePersonLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersSearchInd())) {
											newStagePersonLinkDto.setCdStagePersSearchInd(
													stagePersonLinkDto.getCdStagePersSearchInd());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIndStagePersInLaw())) {
											newStagePersonLinkDto
													.setIndStagePersInLaw(stagePersonLinkDto.getIndStagePersInLaw());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIndStagePersReporter())) {
											newStagePersonLinkDto.setIndStagePersReporter(
													stagePersonLinkDto.getIndStagePersReporter());
										}
										if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getCdStagePersType())) {
											if (!stagePersonLinkDto.getCdStagePersType()
													.equals(ServiceConstants.STAFF_TYPE)
													&& !closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.ADMIN_REVIEW)) {
												// CCMND3D
												serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
												stagePersonLinkDao.getStagePersonLinkAUD(newStagePersonLinkDto,
														serviceReqHeaderDto);
											}
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())
													&& !TypeConvUtil
															.isNullOrEmpty(closeOpenStageInputDto.getScrIdPrimChild())
													&& stagePersonLinkDto.getIdPerson()
															.equals(closeOpenStageInputDto.getScrIdPrimChild())
													&& closeOpenStageInputDto.getCdStageOpen()
															.equals(ServiceConstants.ADMIN_REVIEW)) {
												// CCMND3D
												serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
												stagePersonLinkDao.getStagePersonLinkAUD(newStagePersonLinkDto,
														serviceReqHeaderDto);
											}
										}
									}
									usPageNbr++;
								} while (newStagePersonLinkDtoList4.size() > usPageNbr
										* ServiceConstants.STAGE_PERSON_LINK_ROW_NUM);
							}
						}
					}
					StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
					stagePersonLinkDto.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
					stagePersonLinkDto.setCdStagePersType(ServiceConstants.STAFF_TYPE);
					stagePersonLinkDto.setIndStagePersEmpNew(ServiceConstants.EMP_NEW);
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
								stagePersonLinkDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
							}
						} else if( ! closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)){
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
								stagePersonLinkDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
							} else {
								if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto)
										|| !TypeConvUtil.isNullOrEmpty(newStagePersonLinkPRDto)) {
									if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkPRDto.getIdPerson())) {
										stagePersonLinkDto.setIdPerson(stagePersonLinkPRDto.getIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(newStagePersonLinkPRDto.getIdPerson())) {
										stagePersonLinkDto.setIdPerson(newStagePersonLinkPRDto.getIdPerson());
									}
								}
							}
						}
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)) {
							// CSEC66D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								idPALWorker = stageDao.getIdPersonForPALWorker(closeOpenStageOutputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(idPALWorker)) {
								stagePersonLinkDto.setIdPerson(idPALWorker);
							}
						}
					}
					stagePersonLinkDto.setDtStagePersLink(date);
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
						stagePersonLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
					} else {
						stagePersonLinkDto.setIdStage(idStage);
					}
					stagePersonLinkDto.setDtLastUpdate(date);
					// CCMND3D
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					if(stagePersonLinkDto.getIdPerson()==null &&( ServiceConstants.ADMIN_REVIEW.
							equalsIgnoreCase(closeOpenStageInputDto.getCdStageOpen()) || ServiceConstants.FAD_REVIEW.
							equalsIgnoreCase(closeOpenStageInputDto.getCdStageOpen()))) {
						stagePersonLinkDto.setIdPerson(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));
					}
					stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);

					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
						if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)) {
							i = ServiceConstants.Zero;
							while (!isNoAOCTodo) {
								if (i == 0) {
									newTodoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.AOC_CLIENT_ASSES_ALERT);
									i++;
									isNoAOCTodo = true;
								} else {
									newTodoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.AOC_QUARTERLY_REVIEW);
									// CCMN46D
									EventDto eventDto = new EventDto();
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
									}
									eventDto.setDtEventOccurred(date);
									eventDto.setCdTask(ServiceConstants.APS_CONTACT_TASK);
									eventDto.setEventDescr(ServiceConstants.QUART_EVENT_DESC);
									eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_NEW);
									eventDto.setCdEventType(ServiceConstants.CONTRACT);
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
										eventDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
									}else{
										eventDto.setIdPerson(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));

									}
									// CCMN46D
									newNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
									isNoAOCTodo = true;
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
									newTodoCommonFunctionDto
											.setSysIdTodoCfPersCrea(closeOpenStageInputDto.getIdPerson());
								}
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									newTodoCommonFunctionDto.setSysIdTodoCfStage(closeOpenStageOutputDto.getIdStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(newNewNewEventDto)) {
									if (!TypeConvUtil.isNullOrEmpty(newNewNewEventDto.getIdEvent())) {
										newTodoCommonFunctionDto.setSysIdTodoCfEvent(newNewNewEventDto.getIdEvent());
									}
								}
								if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getDtStageStart())) {
									newTodoCommonFunctionDto.setDtSysDtTodoCfDueFrom(date);
								} else {
									newTodoCommonFunctionDto
											.setDtSysDtTodoCfDueFrom(closeOpenStageInputDto.getDtStageStart());
								}
								newTodoCommonFunctionInputDto.setTodoCommonFunctionDto(newTodoCommonFunctionDto);
								// CSUB40U
								newTodoCommonFunctionOutputDto = todoCommonFunctionService
										.TodoCommonFunction(newTodoCommonFunctionInputDto);
								if (!TypeConvUtil.isNullOrEmpty(newTodoCommonFunctionOutputDto)) {
									if (!TypeConvUtil.isNullOrEmpty(newTodoCommonFunctionOutputDto.getCdTodoTask())) {
										if (newTodoCommonFunctionOutputDto.getCdTodoTask()
												.equals(ServiceConstants.APS_CONTACT_TASK)) {
											ContactDto contactDto = new ContactDto();
											if (!TypeConvUtil
													.isNullOrEmpty(newTodoCommonFunctionOutputDto.getIdEvent())) {
												contactDto.setIdEvent(newTodoCommonFunctionOutputDto.getIdEvent());
											}
											if (!TypeConvUtil
													.isNullOrEmpty(newTodoCommonFunctionOutputDto.getIdStage())) {
												contactDto
														.setIdContactStage(newTodoCommonFunctionOutputDto.getIdStage());
											}
											contactDto.setDtCntctMnthlySummBeg(nullDate);
											contactDto.setDtCntctMnthlySummEnd(nullDate);
											contactDto.setDtContactOccurred(nullDate);
											if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
												if (closeOpenStageInputDto.getCdStage()
														.equals(ServiceConstants.INTAKE)) {
													contactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS45);
													newTodoCommonFunctionDto
															.setSysCdTodoCf(ServiceConstants.TODO_INV_45_DAY);
												} else if (closeOpenStageInputDto.getCdStage()
														.equals(ServiceConstants.INVESTIGATION)) {
													contactDto.setCdContactType(ServiceConstants.CONTACT_TYPE_CS60);
													newTodoCommonFunctionDto
															.setSysCdTodoCf(ServiceConstants.TODO_SVC_60_DAY);
												}
											}
											// CSYS07D
											this.contactAUD(contactDto, ServiceConstants.REQ_FUNC_CD_ADD);
										}
									}
								}
							}
						} else if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)) {
							EventDto eventDto = new EventDto();
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
							}
							eventDto.setDtEventOccurred(date);
							eventDto.setCdTask(ServiceConstants.PAL_ILS_TASK);
							eventDto.setEventDescr(ServiceConstants.PAL_ILS_DESCRIPTION);
							eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_NEW);
							eventDto.setCdEventType(ServiceConstants.PAL_ILS_EVENT_TYPE);
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
								eventDto.setIdPerson(closeOpenStageInputDto.getIdPerson());
							}
							// CCMN46D
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
							newNewNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
							if (!TypeConvUtil.isNullOrEmpty(idPALWorker)) {
								newTodoCommonFunctionDto.setSysIdTodoCfPersCrea(idPALWorker);
							}
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								newTodoCommonFunctionDto.setSysIdTodoCfStage(closeOpenStageOutputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newNewNewNewEventDto)) {
								if (!TypeConvUtil.isNullOrEmpty(newNewNewNewEventDto.getIdEvent())) {
									newTodoCommonFunctionDto.setSysIdTodoCfEvent(newNewNewNewEventDto.getIdEvent());
								}
							}
							newTodoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.PAL_ILS_TODO);
							if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getDtStageStart())) {
								newTodoCommonFunctionDto.setDtSysDtTodoCfDueFrom(date);
							} else {
								newTodoCommonFunctionDto
										.setDtSysDtTodoCfDueFrom(closeOpenStageInputDto.getDtStageStart());
							}
							newTodoCommonFunctionInputDto.setTodoCommonFunctionDto(newTodoCommonFunctionDto);
							// CSUB40U
							newTodoCommonFunctionOutputDto = todoCommonFunctionService
									.TodoCommonFunction(newTodoCommonFunctionInputDto);
						}
					}
					// CCMNC1D
					StageLinkDto stageLinkDto = new StageLinkDto();
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						stageLinkDto.setIdPriorStage(closeOpenStageInputDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
						stageLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
					}
					this.stageLinkSave(stageLinkDto);
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
							&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())
							&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
						if ((closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)
								|| closeOpenStageInputDto.getCdStage().equals(ServiceConstants.AGE_OUT))
								&& closeOpenStageInputDto.getCdStageProgram().equals(ServiceConstants.CAPS_PROG_APS)
								&& closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)) {
							// CCMN87D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								String cdTask = ServiceConstants.EMPTY_STRING;
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
									if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)) {
										cdTask = ServiceConstants.INV_OUTCOME_MATRIX_TASK;
									} else {
										cdTask = ServiceConstants.AOC_OUTCOME_MATRIX_TASK;
									}
								} else {
									cdTask = ServiceConstants.AOC_OUTCOME_MATRIX_TASK;
								}
								// CCMN87D
								eventStagePersonDtoList = stageDao.getEventStagePersonListByAttributes(
										closeOpenStageInputDto.getIdStage(), cdTask, ServiceConstants.EMPTY_STRING);
							}
						}
					}
					if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDtoList)) {
						if (eventStagePersonDtoList.size() == 1) {
							// CCMN46D
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							EventDto eventDto = new EventDto();
							if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDtoList.get(0).getIdEvent())) {
								// CCMN45D
								eventDto = eventDao.getEventByid(eventStagePersonDtoList.get(0).getIdEvent());
							}
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDtoList.get(0).getDtEventOccurred())) {
								eventDto.setDtEventOccurred(eventStagePersonDtoList.get(0).getDtEventOccurred());
							}
							eventDto.setCdTask(ServiceConstants.SVC_OUTCOME_MATRIX_TASK);
							if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDtoList.get(0).getEventDescr())) {
								eventDto.setEventDescr(eventStagePersonDtoList.get(0).getEventDescr());
							}
							eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_PROCESS);
							if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDtoList.get(0).getCdEventType())) {
								eventDto.setCdEventType(eventStagePersonDtoList.get(0).getCdEventType());
							}
							// CCMN46D
							newNewNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
						}
					}
					// CCMN87D
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						String cdTask = ServiceConstants.EMPTY_STRING;
						if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
							if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.AGE_OUT)) {
								cdTask = ServiceConstants.AOC_GUARDIANSHIP_CD_TASK;
							} else {
								cdTask = ServiceConstants.GUARDIANSHIP_TASK;
							}
						} else {
							cdTask = ServiceConstants.GUARDIANSHIP_TASK;
						}
						// CCMN87D
						newEventStagePersonDtoList = stageDao.getEventStagePersonListByAttributes(
								closeOpenStageInputDto.getIdStage(), cdTask, ServiceConstants.EMPTY_STRING);
					}
					if (!TypeConvUtil.isNullOrEmpty(newEventStagePersonDtoList)) {
						if (newEventStagePersonDtoList.size() > 0) {
							for (EventStagePersonDto eventStagePersonDto : newEventStagePersonDtoList) {
								if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventStatus())) {
									if (!eventStagePersonDto.getCdEventStatus()
											.equals(ServiceConstants.EVENT_STATUS_NEW)) {
										// CSEC09D
										if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
											guardianshipDto = eventDao
													.getGuardianshipDtoByIdEvent(eventStagePersonDto.getIdEvent());
										}
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(guardianshipDto)) {
									if (!TypeConvUtil.isNullOrEmpty(guardianshipDto.getCdGuardGuardianType())
											&& !TypeConvUtil.isNullOrEmpty(guardianshipDto.getDtGuardLetterIssued())) {
										if (TypeConvUtil.isNullOrEmpty(guardianshipDto.getDtGuardCloseDate())
												&& (guardianshipDto.getCdGuardGuardianType()
														.equals(ServiceConstants.ADULT_PROTECTIVE_SERVICES)
														|| guardianshipDto.getCdGuardGuardianType()
																.equals(ServiceConstants.CONTRACTED))) {
											// CCMN46D
											serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
											EventDto eventDto = new EventDto();
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
												// CCMN45D
												eventDto = eventDao.getEventByid(eventStagePersonDto.getIdEvent());
											}
											if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
												eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
											}
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getDtEventOccurred())) {
												eventDto.setDtEventOccurred(eventStagePersonDto.getDtEventOccurred());
											}
											eventDto.setCdTask(ServiceConstants.UPDATE_GUARDIANSHIP_CD_TASK);
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getEventDescr())) {
												eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
											}
											eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_PROCESS);
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
												eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
											}
											// CCMN46D
											newNewNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
										} else {
											if (TypeConvUtil.isNullOrEmpty(guardianshipDto.getDtGuardCloseDate())) {
												// CCMN46D
												serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
												EventDto eventDto = new EventDto();
												if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
													// CCMN45D
													eventDto = eventDao.getEventByid(eventStagePersonDto.getIdEvent());
												}
												if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
													eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
												}
												if (!TypeConvUtil
														.isNullOrEmpty(eventStagePersonDto.getDtEventOccurred())) {
													eventDto.setDtEventOccurred(
															eventStagePersonDto.getDtEventOccurred());
												}
												eventDto.setCdTask(ServiceConstants.UPDATE_GUARDIANSHIP_CD_TASK);
												if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getEventDescr())) {
													eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
												}
												eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_PROCESS);
												if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
													eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
												}
												// CCMN46D
												newNewNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto,
														eventDto);
											}
										}
									} else {
										if (TypeConvUtil.isNullOrEmpty(guardianshipDto.getDtGuardCloseDate())) {
											// CCMN46D
											serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
											EventDto eventDto = new EventDto();
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
												// CCMN45D
												eventDto = eventDao.getEventByid(eventStagePersonDto.getIdEvent());
											}
											if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
												eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
											}
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getDtEventOccurred())) {
												eventDto.setDtEventOccurred(eventStagePersonDto.getDtEventOccurred());
											}
											eventDto.setCdTask(ServiceConstants.UPDATE_GUARDIANSHIP_CD_TASK);
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getEventDescr())) {
												eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
											}
											eventDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_PROCESS);
											if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
												eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
											}
											// CCMN46D
											newNewNewNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
										}
									}
								}
							}
						}
					}
				}
				if (!closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD_REVIEW)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.SUBCARE)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.PREP_ADULT)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.AGE_OUT)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.POST_ADOPT)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.FAD)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADOPTION)) {
					String cdTask = ServiceConstants.EMPTY_STRING;
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
							&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())) {
						if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INVESTIGATION)
								&& closeOpenStageInputDto.getCdStageProgram().equals(ServiceConstants.CAPS_PROG_APS)) {
							cdTask = ServiceConstants.APS_SVC_AUTH_TASK;
						} else if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.AGE_OUT)
								&& closeOpenStageInputDto.getCdStage().equals(ServiceConstants.CAPS_PROG_APS)) {
							cdTask = ServiceConstants.AOC_SVC_AUTH_TASK;
						}
					} /*else { Defect 11670 _ do not pass in task code
						cdTask = ServiceConstants.AOC_SVC_AUTH_TASK;
					}*/
					// CCMN87D
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						newEventStagePersonDtoList1 = stageDao.getEventStagePersonListByAttributes(
								closeOpenStageInputDto.getIdStage(), cdTask, ServiceConstants.SERVICE_AUTH_TYPE);
					}
					if (!TypeConvUtil.isNullOrEmpty(newEventStagePersonDtoList1)) {
						if (newEventStagePersonDtoList1.size() > 0) {
							for (EventStagePersonDto eventStagePersonDto : newEventStagePersonDtoList1) {
								if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
									if (eventStagePersonDto.getCdEventType()
											.equals(ServiceConstants.SERVICE_AUTH_TYPE)) {
										// CSEC24D
										if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
											// CSEC24D
											svcAuthEventDto = eventDao.getSVCAuthEventDtoByIdSVCAuthEvent(
													eventStagePersonDto.getIdEvent());
										}
										if (!TypeConvUtil.isNullOrEmpty(svcAuthEventDto)) {
											if (!TypeConvUtil.isNullOrEmpty(svcAuthEventDto.getIdSVCAuth())) {
												// CSEC23D
												serviceAuthorizationDto = serviceAuthorizationDao
														.getServiceAuthorizationById(svcAuthEventDto.getIdSVCAuth());
											}
										}
										if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto)) {
											if (!TypeConvUtil
													.isNullOrEmpty(serviceAuthorizationDto.getIndSvcAuthComplete())) {
												if (serviceAuthorizationDto.getIndSvcAuthComplete()
														.equals(ServiceConstants.STRING_IND_Y)) {
													if (!TypeConvUtil.isNullOrEmpty(svcAuthEventDto)) {
														if (!TypeConvUtil
																.isNullOrEmpty(svcAuthEventDto.getIdSVCAuth())) {
															// CLSS24D
															svcAuthDetailDtoList = serviceAuthorizationDao
																	.getSVCAuthDetailDtoById(
																			svcAuthEventDto.getIdSVCAuth());
														}
													}
													if (!TypeConvUtil.isNullOrEmpty(svcAuthDetailDtoList)) {
														for (SVCAuthDetailDto svcAuthDetailDto : svcAuthDetailDtoList) {
															if (!TypeConvUtil.isNullOrEmpty(
																	svcAuthDetailDto.getDtSvcAuthDtlTerm())) {
																if (svcAuthDetailDto.getDtSvcAuthDtlTerm()
																		.after(date)) {
																	if (!TypeConvUtil
																			.isNullOrEmpty(closeOpenStageInputDto
																					.getCdStageProgram())) {
																		if (closeOpenStageInputDto.getCdStageProgram()
																				.equals(ServiceConstants.CAPS_PROG_APS)) {
																			// CCMN46D
																			EventDto eventDto = new EventDto();
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getCdEventStatus())) {
																				eventDto.setCdEventStatus(
																						eventStagePersonDto
																								.getCdEventStatus());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getDtEventOccurred())) {
																				eventDto.setDtEventOccurred(
																						eventStagePersonDto
																								.getDtEventOccurred());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getEventDescr())) {
																				eventDto.setEventDescr(
																						eventStagePersonDto
																								.getEventDescr());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getCdEventType())) {
																				eventDto.setCdEventType(
																						eventStagePersonDto
																								.getCdEventType());
																			}
																			if (!TypeConvUtil.isNullOrEmpty(
																					closeOpenStageOutputDto
																							.getIdStage())) {
																				eventDto.setIdStage(
																						closeOpenStageOutputDto
																								.getIdStage());
																			}
																			if (closeOpenStageInputDto.getCdStageOpen()
																					.equals(ServiceConstants.SUBCARE)) {
																				eventDto.setCdTask(
																						ServiceConstants.SUBCARE_SVC_AUTH_TASK);
																			} else if (closeOpenStageInputDto
																					.getCdStageOpen()
																					.equals(ServiceConstants.FSU_PROGRAM)) {
																				eventDto.setCdTask(
																						ServiceConstants.FSU_SVC_AUTH_TASK);
																			} else {
																				eventDto.setCdTask(
																						ServiceConstants.UPDATE_SERVICE_AUTH_TASK);
																			}
																			// CCMN46D
																			if (!TypeConvUtil.isNullOrEmpty(
																					closeOpenStageInputDto
																							.getReqFuncCd())) {
																				serviceReqHeaderDto.setReqFuncCd(
																						closeOpenStageInputDto
																								.getReqFuncCd());
																				newEventDto2 = eventDao.eventAUDFunc(
																						serviceReqHeaderDto, eventDto);
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(newEventDto2)) {
																				if (!TypeConvUtil.isNullOrEmpty(
																						newEventDto2.getIdEvent())
																						&& !TypeConvUtil.isNullOrEmpty(
																								svcAuthDetailDto
																										.getIdSvcAuth())) {
																					// CAUD34D
																					this.svcAuthEventLinkSave(
																							newEventDto2.getIdEvent(),
																							svcAuthDetailDto
																									.getIdSvcAuth());
																				}
																			}
																			if (!TypeConvUtil.isNullOrEmpty(
																					eventStagePersonDto.getIdEvent())) {
																				// CCMND2D
																				personDtoList = eventDao
																						.getPersonFromEventPlanLinkByIdEvent(
																								eventStagePersonDto
																										.getIdEvent());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(personDtoList)) {
																				for (PersonDto newPersonDto : personDtoList) {
																					eventPersonLinkDto.setReqFuncCd(
																							ServiceConstants.REQ_FUNC_CD_ADD);
																					if (!TypeConvUtil
																							.isNullOrEmpty(newPersonDto
																									.getIdPerson())) {
																						eventPersonLinkDto.setIdPerson(
																								newPersonDto
																										.getIdPerson());
																					}
																					if (!TypeConvUtil.isNullOrEmpty(
																							newEventDto2)) {
																						if (!TypeConvUtil.isNullOrEmpty(
																								newEventDto2
																										.getIdEvent())) {
																							eventPersonLinkDto
																									.setIdEvent(
																											newEventDto2
																													.getIdEvent());
																						}
																					}
																					// CCMN68D
																					eventPersonLinkDao
																							.getEventPersonLinkAUD(
																									eventPersonLinkDto);
																				}
																			}
																			break;
																		} else if (!TypeConvUtil.isNullOrEmpty(
																				eventStagePersonDto.getCdEventStatus())
																				&& eventStagePersonDto
																						.getCdEventStatus()
																						.equals(ServiceConstants.EVENTSTATUS_APPROVE)) {
																			// CCMN46D
																			EventDto eventDto = new EventDto();
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getCdEventStatus())) {
																				eventDto.setCdEventStatus(
																						eventStagePersonDto
																								.getCdEventStatus());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getDtEventOccurred())) {
																				eventDto.setDtEventOccurred(
																						eventStagePersonDto
																								.getDtEventOccurred());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getEventDescr())) {
																				eventDto.setEventDescr(
																						eventStagePersonDto
																								.getEventDescr());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(eventStagePersonDto
																							.getCdEventType())) {
																				eventDto.setCdEventType(
																						eventStagePersonDto
																								.getCdEventType());
																			}
																			if (!TypeConvUtil.isNullOrEmpty(
																					closeOpenStageOutputDto
																							.getIdStage())) {
																				eventDto.setIdStage(
																						closeOpenStageOutputDto
																								.getIdStage());
																			}
																			if (closeOpenStageInputDto.getCdStageOpen()
																					.equals(ServiceConstants.ADOPTION)) {
																				eventDto.setCdTask(
																						ServiceConstants.ADO_SVC_AUTH_TASK);
																			} else if (closeOpenStageInputDto
																					.getCdStageOpen()
																					.equals(ServiceConstants.FSU_PROGRAM)) {
																				eventDto.setCdTask(
																						ServiceConstants.FSU_SVC_AUTH_TASK);
																			} else if (closeOpenStageInputDto
																					.getCdStageOpen()
																					.equals(ServiceConstants.FPR_PROGRAM)) {
																				eventDto.setCdTask(
																						ServiceConstants.FPR_SVC_AUTH_TASK);
																			} else if (closeOpenStageInputDto
																					.getCdStageOpen()
																					.equals(ServiceConstants.FRE_PROGRAM)) {
																				eventDto.setCdTask(
																						ServiceConstants.FRE_SVC_AUTH_TASK);
																			} else {
																				eventDto.setCdTask(
																						ServiceConstants.UPDATE_SERVICE_AUTH_TASK);
																			}
																			// CCMN46D
																			if (!TypeConvUtil.isNullOrEmpty(
																					closeOpenStageInputDto
																							.getReqFuncCd())) {
																				serviceReqHeaderDto.setReqFuncCd(
																						ServiceConstants.REQ_FUNC_CD_ADD);
																				newEventDto3 = eventDao.eventAUDFunc(
																						serviceReqHeaderDto, eventDto);
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(newEventDto3)) {
																				if (!TypeConvUtil.isNullOrEmpty(
																						newEventDto3.getIdEvent())
																						&& !TypeConvUtil.isNullOrEmpty(
																								svcAuthDetailDto
																										.getIdSvcAuth())) {
																					// CAUD34D
																					this.svcAuthEventLinkSave(
																							newEventDto3.getIdEvent(),
																							svcAuthDetailDto
																									.getIdSvcAuth());
																				}
																			}
																			if (!TypeConvUtil.isNullOrEmpty(
																					eventStagePersonDto.getIdEvent())) {
																				// CCMND2D
																				personDtoList1 = eventDao
																						.getPersonFromEventPlanLinkByIdEvent(
																								eventStagePersonDto
																										.getIdEvent());
																			}
																			if (!TypeConvUtil
																					.isNullOrEmpty(personDtoList1)) {
																				for (PersonDto newPersonDto : personDtoList1) {
																					eventPersonLinkDto.setReqFuncCd(
																							ServiceConstants.REQ_FUNC_CD_ADD);
																					if (!TypeConvUtil
																							.isNullOrEmpty(newPersonDto
																									.getIdPerson())) {
																						eventPersonLinkDto.setIdPerson(
																								newPersonDto
																										.getIdPerson());
																					}

																					if (!TypeConvUtil.isNullOrEmpty(
																							newEventDto3)) {
																						if (!TypeConvUtil.isNullOrEmpty(
																								newEventDto3
																										.getIdEvent())) {
																							eventPersonLinkDto
																									.setIdEvent(
																											newEventDto3
																													.getIdEvent());
																						}
																					}
																					// CCMN68D
																					eventPersonLinkDao
																							.getEventPersonLinkAUD(
																									eventPersonLinkDto);
																				}
																			}
																			break;
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())) {
				if (closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)) {
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						// CCMNE4D
						personDtoList2 = stageDao.getPersonFromPRNStageByIdStage(closeOpenStageInputDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(personDtoList2)) {
						for (PersonDto newPersonDto : personDtoList2) {
							if (!TypeConvUtil.isNullOrEmpty(newPersonDto.getIdPerson())) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
									if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
										usPageNbr = ServiceConstants.INITIAL_PAGE;
										do {
											// CCMNE5D
											stagePersonLinkDtoList1 = stageDao.getStageByCasePersonId(
													newPersonDto.getIdPerson(), stageDto.getIdCase());
											if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDtoList1)) {
												for (StagePersonLinkDto stagePersonLinkDto : stagePersonLinkDtoList1) {
													TodoDto newTodoDto = new TodoDto();
													if (!TypeConvUtil.isNullOrEmpty(newPersonDto.getNmPersonFull())
															&& !TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
														newTodoDto.setTodoDesc(newPersonDto.getNmPersonFull()
																+ ServiceConstants.TODO_DESC_INFO
																+ stageDto.getNmStage());
													}
													newTodoDto.setCdTodoType(ServiceConstants.ALERT_TODO);
													if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdPerson())) {
														newTodoDto.setIdTodoPersAssigned(
																stagePersonLinkDto.getIdPerson());
														newTodoDto
																.setIdTodoPersWorker(stagePersonLinkDto.getIdPerson());
													}
													newTodoDto.setDtTodoCreated(date);
													newTodoDto.setDtTodoDue(date);
													if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdCase())) {
														newTodoDto.setIdTodoCase(stagePersonLinkDto.getIdCase());
													}
													if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto.getIdStage())) {
														newTodoDto.setIdTodoStage(stagePersonLinkDto.getIdStage());
													}
													newTodoDto.setDtTodoCompleted(date);
													newTodoDto.setDtTodoDue(nullDate);
													// CCMN43D
													serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
													todoDao.todoAUD(newTodoDto, serviceReqHeaderDto);
												}
											}
											usPageNbr++;
										} while (stagePersonLinkDtoList1.size() > usPageNbr
												* ServiceConstants.STAGE_PERSON_ROW_NUM);
									}
								}
							}
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageProgram())
					&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
				if (closeOpenStageInputDto.getCdStageProgram().equals(ServiceConstants.CAPS_PROG_APS)
						&& !closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.ADMIN_REVIEW)) {
					if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
						// CCMNH0D
						todoDtoList = todoDao.getTodoByStageId(closeOpenStageInputDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(todoDtoList)) {
						for (TodoDto newTodoDto : todoDtoList) {
							EventDto eventDto = new EventDto();
							ContactDto contactDto = new ContactDto();
							TodoDto newNewtodoDto = new TodoDto();
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								contactDto.setIdContactStage(closeOpenStageOutputDto.getIdStage());
								newNewtodoDto.setIdTodoStage(closeOpenStageOutputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(newTodoDto.getIdTodoEvent())) {
								eventDto.setIdEvent(newTodoDto.getIdTodoEvent());
								contactDto.setIdEvent(newTodoDto.getIdTodoEvent());
							}
							if (!TypeConvUtil.isNullOrEmpty(newTodoDto.getIdTodo())) {
								newNewtodoDto.setIdTodo(newTodoDto.getIdTodo());
							}
							if (closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.INVESTIGATION)) {
								eventDto.setCdTask(ServiceConstants.APS_INT_TO_INV_TASK);
								newNewtodoDto.setCdTodoTask(ServiceConstants.APS_INT_TO_INV_TASK);
							} else if (closeOpenStageInputDto.getCdStageOpen()
									.equals(ServiceConstants.SERVICE_DELIVERY)) {
								eventDto.setCdTask(ServiceConstants.APS_INV_TO_SVC_TASK);
								newNewtodoDto.setCdTodoTask(ServiceConstants.APS_INV_TO_SVC_TASK);
							}
							// CCMNG7D
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
							// CCMNG8D
							this.contactAUD(contactDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
							// CCMNG9D
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							todoDao.todoAUD(newNewtodoDto, serviceReqHeaderDto);
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStage())
					&& !TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getCdStageOpen())) {
				if (!closeOpenStageInputDto.getCdStage().equals(ServiceConstants.INTAKE)
						&& closeOpenStageInputDto.getCdStageOpen().equals(ServiceConstants.POST_ADOPT)) {
					if (!TypeConvUtil.isNullOrEmpty(stageResourceDtoList)) {
						for (StageResourceDto stageResourceDto : stageResourceDtoList) {
							// CCMND3D
							StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersRelInt())) {
								stagePersonLinkDto.setCdStagePersRelInt(stageResourceDto.getCdStagePersRelInt());
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersRole())) {
								stagePersonLinkDto.setCdStagePersRole(stageResourceDto.getCdStagePersRole());
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersType())) {
								stagePersonLinkDto.setCdStagePersType(stageResourceDto.getCdStagePersType());
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersSearchInd())) {
								stagePersonLinkDto.setCdStagePersSearchInd(stageResourceDto.getCdStagePersSearchInd());
							}
							stagePersonLinkDto.setDtStagePersLink(date);
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getIdPerson())) {
								stagePersonLinkDto.setIdPerson(stageResourceDto.getIdPerson());
							}
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
								stagePersonLinkDto.setIdStage(closeOpenStageOutputDto.getIdStage());
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getIndStagePersInLaw())) {
								stagePersonLinkDto.setIndStagePersInLaw(stageResourceDto.getIndStagePersInLaw());
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getIndStagePersReporter())) {
								stagePersonLinkDto.setIndStagePersReporter(stageResourceDto.getIndStagePersReporter());
							}
							if (TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersType())) {
								stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkPRDto, serviceReqHeaderDto);
							}
							if (!TypeConvUtil.isNullOrEmpty(stageResourceDto.getCdStagePersType())
									&& !stageResourceDto.getCdStagePersType().equals(ServiceConstants.STAFF_PERSON)) {
								stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkPRDto, serviceReqHeaderDto);
							}
							// CCMN87D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								newEventStagePersonDtoList2 = stageDao.getEventStagePersonListByAttributes(
										closeOpenStageInputDto.getIdStage(), ServiceConstants.ADOPTION_SUBSIDY_TASK,
										ServiceConstants.SUBSIDY_TYPE);
							}
							if (!TypeConvUtil.isNullOrEmpty(newEventStagePersonDtoList2)) {
								for (EventStagePersonDto evetStagePersonDto : newEventStagePersonDtoList2) {
									if (!TypeConvUtil.isNullOrEmpty(evetStagePersonDto.getCdEventStatus())) {
										if (evetStagePersonDto.getCdEventStatus()
												.equals(ServiceConstants.EVENT_STATUS_PROCESS)) {
											// CSES64D
											if (!TypeConvUtil.isNullOrEmpty(evetStagePersonDto.getIdEvent())) {
												adoptionSubsidyDto = eventDao
														.getAdoptionSubsidyByIdEvent(evetStagePersonDto.getIdEvent());
											}
											EventDto eventDto = new EventDto();
											if (!TypeConvUtil.isNullOrEmpty(evetStagePersonDto.getCdEventStatus())) {
												eventDto.setCdEventStatus(evetStagePersonDto.getCdEventStatus());
											}
											eventDto.setDtEventOccurred(date);
											if (!TypeConvUtil.isNullOrEmpty(evetStagePersonDto.getEventDescr())) {
												eventDto.setEventDescr(evetStagePersonDto.getEventDescr());
											}
											if (!TypeConvUtil.isNullOrEmpty(evetStagePersonDto.getCdEventType())) {
												eventDto.setCdEventType(evetStagePersonDto.getCdEventType());
											}
											if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
												eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
											}
											eventDto.setCdTask(ServiceConstants.PAD_SUBSIDY_TASK);
											// CCMN46D
											serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
											newNewEventDto = null;
											newNewEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
											if (!TypeConvUtil.isNullOrEmpty(newNewEventDto)) {
												if (!TypeConvUtil.isNullOrEmpty(newNewEventDto.getIdEvent())) {
													adptSubEventLinkDto.setIdEvent(newNewEventDto.getIdEvent());
												}
											}
											if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidyDto)) {
												if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidyDto.getIdAdptSub())) {
													adptSubEventLinkDto
															.setIdAdoptionSubsidy(adoptionSubsidyDto.getIdAdptSub());
												}
											}
											// CAUDB2D
											this.adptSubEventLinkEdit(adptSubEventLinkDto,
													ServiceConstants.REQ_FUNC_CD_ADD);
											isClosePAD = true;
										}
									}
								}
							} else {
								isClosePAD = false;
							}
							// CCMN87D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								newEventStagePersonDtoList3 = stageDao.getEventStagePersonListByAttributes(
										closeOpenStageInputDto.getIdStage(), ServiceConstants.ADO_ADPT_ASSIST_APPL_TASK,
										ServiceConstants.ADPT_ASSIST_APPL_EVT_TYPE);
							}
							if (!TypeConvUtil.isNullOrEmpty(newEventStagePersonDtoList3)) {
								for (EventStagePersonDto eventStagePersonDto : newEventStagePersonDtoList3) {
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
										// CSESF8D
										idAdptEligApplication = eventDao
												.getidAdptEligApplicationByIdEvent(eventStagePersonDto.getIdEvent());
									}
									// CCMN46D
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									EventDto eventDto = new EventDto();
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventStatus())) {
										eventDto.setCdEventStatus(eventStagePersonDto.getCdEventStatus());
									}
									eventDto.setDtEventOccurred(date);
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getEventDescr())) {
										eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
									}
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
										eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
									}
									eventDto.setCdTask(ServiceConstants.PAD_ADPT_ASSIST_APPL_TASK);
									newEventDto4 = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
									// CAUDM2D
									if (!TypeConvUtil.isNullOrEmpty(newEventDto4)) {
										if (!TypeConvUtil.isNullOrEmpty(newEventDto4.getIdEvent())) {
											adptAsstAppEventLinkDto.setIdEvent(newEventDto4.getIdEvent());
										}
									}
									if (!TypeConvUtil.isNullOrEmpty(idAdptEligApplication)) {
										adptAsstAppEventLinkDto.setIdAdptEligApplication(idAdptEligApplication);
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
										adptAsstAppEventLinkDto
												.setIdCreatedPerson(closeOpenStageInputDto.getIdPerson());
										adptAsstAppEventLinkDto
												.setIdLastUpdatePerson(closeOpenStageInputDto.getIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(idCase)) {
										adptAsstAppEventLinkDto.setIdCapsCase(idCase);
									}
									this.adptAsstAppEventLinkEdit(adptAsstAppEventLinkDto,
											ServiceConstants.REQ_FUNC_CD_ADD);
								}
							}
							// CCMN87D
							if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
								newEventStagePersonDtoList4 = stageDao.getEventStagePersonListByAttributes(
										closeOpenStageInputDto.getIdStage(),
										ServiceConstants.ADO_ADPT_ASSIST_RECERT_TASK,
										ServiceConstants.ADPT_ASSIST_RECERT_EVT_TYPE);
							}
							if (!TypeConvUtil.isNullOrEmpty(newEventStagePersonDtoList4)) {
								for (EventStagePersonDto eventStagePersonDto : newEventStagePersonDtoList4) {
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getIdEvent())) {
										// CSESF9D
										idAdptEligRecert = eventDao
												.getidAdptEligRecertByIdEvent(eventStagePersonDto.getIdEvent());
									}
									// CCMN46D
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									EventDto eventDto = new EventDto();
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventStatus())) {
										eventDto.setCdEventStatus(eventStagePersonDto.getCdEventStatus());
									}
									eventDto.setDtEventOccurred(date);
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getEventDescr())) {
										eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
									}
									if (!TypeConvUtil.isNullOrEmpty(eventStagePersonDto.getCdEventType())) {
										eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
										eventDto.setIdStage(closeOpenStageOutputDto.getIdStage());
									}
									eventDto.setCdTask(ServiceConstants.PAD_ADPT_ASSIST_RECERT_TASK);
									newEventDto5 = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
									// CAUDM2D
									if (!TypeConvUtil.isNullOrEmpty(newEventDto5)) {
										if (!TypeConvUtil.isNullOrEmpty(newEventDto5.getIdEvent())) {
											adptAsstAppEventLinkDto.setIdEvent(newEventDto5.getIdEvent());
										}
									}
									if (!TypeConvUtil.isNullOrEmpty(idAdptEligRecert)) {
										adptAsstAppEventLinkDto.setIdAdptEligRecert(idAdptEligRecert);
									}
									if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdPerson())) {
										adptAsstAppEventLinkDto
												.setIdCreatedPerson(closeOpenStageInputDto.getIdPerson());
										adptAsstAppEventLinkDto
												.setIdLastUpdatePerson(closeOpenStageInputDto.getIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(idCase)) {
										adptAsstAppEventLinkDto.setIdCapsCase(idCase);
									}
									this.adptAsstAppEventLinkEdit(adptAsstAppEventLinkDto,
											ServiceConstants.REQ_FUNC_CD_ADD);
								}
							}
							if (!isClosePAD) {
								closeStageCaseInputDto.setCdStage(ServiceConstants.POST_ADOPT);
								closeStageCaseInputDto.setCdStageProgram(ServiceConstants.CAPS_PROG_CPS);
								closeStageCaseInputDto.setCdStageReasonClosed(ServiceConstants.POST_ADOPT);
								if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto.getIdStage())) {
									closeStageCaseInputDto.setIdStage(closeOpenStageOutputDto.getIdStage());
								}
								closeStageCaseService.closeStageCase(closeStageCaseInputDto);
							}
						}
					}
				}
			}
		}
		return closeOpenStageOutputDto;
	}

	/**
	 * This DAM will receives CD STAGE, CD STAGE PROGRAM, and CD STAGE REASON
	 * CLOSED from the Service and retrieves entire row(s) from the STAGE_PROG
	 * table. SIR#2417-KDB
	 * 
	 * Service Name - CCMN03U, DAM Name - CCMNB8D
	 * 
	 * @param closeOpenStageInputDto
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<StageProgDto> getStgProgroession(CloseOpenStageInputDto closeOpenStageInputDto) {

		List<StageProgDto> stageProgDtoList = null;
		String cdStageReasonClosed = ServiceConstants.EMPTY_STRING;
		String cdStage = ServiceConstants.EMPTY_STRING;
		if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto)) {
			cdStageReasonClosed = closeOpenStageInputDto.getCdStageReasonClosed();
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getReqFuncCd())) {
				if (closeOpenStageInputDto.getReqFuncCd().equals(ServiceConstants.STAGE_PROG_NEW_STAGE)) {
					cdStage = closeOpenStageInputDto.getCdStageOpen();
				} else if (closeOpenStageInputDto.getReqFuncCd().equals(ServiceConstants.STAGE_PROG_OLD_STAGE)) {
					cdStage = closeOpenStageInputDto.getCdStage();
				}
				if (!TypeConvUtil.isNullOrEmpty(cdStage)) {
					if (cdStage.equals(ServiceConstants.POST_ADOPT)
							&& closeOpenStageInputDto.getReqFuncCd().equals(ServiceConstants.STAGE_PROG_NEW_STAGE)) {
						cdStageReasonClosed = ServiceConstants.ADOPTION;

					}
				}
			}

			if (!TypeConvUtil.isNullOrEmpty(cdStage)) {

				stageProgDtoList = stageProgDao.getStgProgroession(cdStage, closeOpenStageInputDto.getCdStageProgram(),
						cdStageReasonClosed);

				// artf151569 - Updates the Stage Prog List for CPS (A-R & INV) when the Approval created the FPR Release date
				if (!ObjectUtils.isEmpty(closeOpenStageInputDto.getIdApproval()) && ServiceConstants.CD_STAGE_CPS
						.equals(closeOpenStageInputDto.getCdStageProgram()) && Arrays.asList(
								ServiceConstants.CSTAGES_AR, ServiceConstants.CSTAGES_INV).contains(cdStage)
						&& ServiceConstants.FPR_STAGE_PROG_TABLE.containsColumn(
								closeOpenStageInputDto.getCdStageReasonClosed())) {

					workloadService.hasAppEventCreatedBeforeFBSSReferral(closeOpenStageInputDto.getIdApproval(),
							stageProgDtoList);
				}

			}
		}
		return stageProgDtoList;
	}

	/**
	 * If the required function is UPDATE: This DAM performs an update of a
	 * record in the STAGE table. If the ID STAGE in the record equals the ID
	 * STAGE value passed in the Input Message, then the DT STAGE CLOSE for the
	 * selected record is updated to the current system's date
	 * 
	 * Service Name - CCMN03U, CCMN88S, DAM Name - CCMND4D
	 * 
	 * @param reClass
	 * @param idStage
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateStageCloseRecord(Long idStage, Date dtClose, String reasonClosed, String cdStage) {

		if (!TypeConvUtil.isNullOrEmpty(idStage)) {
			stageWorkloadDao.updateStageCloseByStageId(idStage, dtClose, reasonClosed);

			if (!TypeConvUtil.isNullOrEmpty(cdStage)) {
				if (!cdStage.equals(ServiceConstants.STAGE_TYPE_INTAKE)) {
					if (!TypeConvUtil.isNullOrEmpty(reasonClosed)) {
						if (!reasonClosed.equals(ServiceConstants.CD_INT_CLOSED_AND_RECLASS)) {
							incomingDetailDao.updateIncomingDetailReClassByIdStage(ServiceConstants.STRING_IND_Y,
									idStage);
						} else {
							incomingDetailDao.updateIncomingDetailReClassByIdStage(ServiceConstants.STRING_IND_N,
									idStage);
						}
					}
				} else if (!cdStage.equals(ServiceConstants.STAGE_TYPE_INVESTIGATION)
						&& !reasonClosed.equals(ServiceConstants.CD_INV_CLOSED_AND_RECLASS)) {
					incomingDetailDao.updateIncomingDetailReClassByIdStageFromStageLink(ServiceConstants.STRING_IND_Y,
							idStage);
				}
			}
		}
	}

	/**
	 * This dam was written to add, update, and delete from the STAGE table.
	 * 
	 * Service Name - CCMN03U, DAM Name - CINT12T
	 * 
	 * @param stageDto
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long stageAUD(StageDto stageDto, String action) {

		Stage stage = new Stage();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
						stage = stageDao.getStageEntityById(stageDto.getIdStage());
					}
				}
				stage.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
					stage.setCdStageType(stageDto.getCdStageType());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
					CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(stageDto.getIdCase());
					stage.setCapsCase(capsCase);
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getDtStageClose())) {
					stage.setDtStageClose(stageDto.getDtStageClose());
				}
				if (stageDto.isIndStageReOpenInv()) {
					stage.setDtStageClose(null);
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageClassification())) {
					stage.setCdStageClassification(stageDto.getCdStageClassification());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageCurrPriority())) {
					stage.setCdStageCurrPriority(stageDto.getCdStageCurrPriority());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageInitialPriority())) {
					stage.setCdStageInitialPriority(stageDto.getCdStageInitialPriority());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageRsnPriorityChgd())) {
					stage.setCdStageRsnPriorityChgd(stageDto.getCdStageRsnPriorityChgd());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageReasonClosed())) {
					stage.setCdStageReasonClosed(stageDto.getCdStageReasonClosed());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIndStageClose())) {
					stage.setIndStageClose(stageDto.getIndStageClose());
				} else {
					stage.setIndStageClose(ServiceConstants.N);
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getStagePriorityCmnts())) {
					stage.setTxtStagePriorityCmnts(stageDto.getStagePriorityCmnts());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageCnty())) {
					stage.setCdStageCnty(stageDto.getCdStageCnty());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
					stage.setNmStage(stageDto.getNmStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageRegion())) {
					stage.setCdStageRegion(stageDto.getCdStageRegion());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getDtStageStart())) {
					stage.setDtStageStart(stageDto.getDtStageStart());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdSituation())) {
					Situation situation = situationDao.getSituationEntityById(stageDto.getIdSituation());
					stage.setSituation(situation);
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageProgram())) {
					stage.setCdStageProgram(stageDto.getCdStageProgram());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStage())) {
					stage.setCdStage(stageDto.getCdStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getStageClosureCmnts())) {
					stage.setTxtStageClosureCmnts(stageDto.getStageClosureCmnts());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdUnit())) {
					stage.setIdUnit(stageDto.getIdUnit());
				}

				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIndAssignStage())) {
					stage.setIndAssignStage(stageDto.getIndAssignStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getTxtIntAsgnmntNote())) {
					stage.setTxtIntAsgnmntNote(stageDto.getTxtIntAsgnmntNote());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getIndRevwdReadyAsgn())) {
					stage.setIndRevwdReadyAsgn(stageDto.getIndRevwdReadyAsgn());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageReopenRsn())) {
					stage.setCdStageReopenRsn(stageDto.getCdStageReopenRsn());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getTxtStageReopenRsnCmnt())) {
					if (!TypeConvUtil.isNullOrEmpty(stage.getTxtStageReopenRsnCmnt())) {
						StringBuilder txtStageReopenRsnCmntDescr = new StringBuilder();
						txtStageReopenRsnCmntDescr
								.append(stage.getTxtStageReopenRsnCmnt() + " " + stageDto.getTxtStageReopenRsnCmnt());
						stage.setTxtStageReopenRsnCmnt(txtStageReopenRsnCmntDescr.toString());
					} else {
						stage.setTxtStageReopenRsnCmnt(stageDto.getTxtStageReopenRsnCmnt());
					}
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					stageDao.saveStage(stage);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					stageDao.updateStage(stage);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					stageDao.deleteStage(stage);
				}

			}
		}
		return stage.getIdStage();
	}

	/**
	 * Adds, updates or deletes one row from the CAPS_CASE table for a given
	 * ID_CASE.
	 * 
	 * Service Name : CCMN03U, DAM Name : CCMNB2D
	 * 
	 * @param capsCaseDto
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long capsCaseAUD(CapsCaseDto capsCaseDto, String action) {

		Long idCase = ServiceConstants.ZERO_VAL;
		CapsCase capsCase = new CapsCase();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(capsCaseDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIdCase())) {
						capsCase = capsCaseDao.getCapsCaseEntityById(capsCaseDto.getIdCase());
					}
				}
				capsCase.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCdCaseProgram())) {
					capsCase.setCdCaseProgram(capsCaseDto.getCdCaseProgram());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCdCaseCounty())) {
					capsCase.setCdCaseCounty(capsCaseDto.getCdCaseCounty());
				}
				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCdCaseSpecialHandling())) {
					capsCase.setCdCaseSpecialHandling(capsCaseDto.getCdCaseSpecialHandling());
				}
				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndCaseWorkerSafety())) {
					capsCase.setIndCaseWorkerSafety(capsCaseDto.getIndCaseWorkerSafety());
				}
				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCaseWorkerSafety())) {
					capsCase.setTxtCaseWorkerSafety(capsCaseDto.getCaseWorkerSafety());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCaseSensitiveCmnts())) {
					capsCase.setTxtCaseSensitiveCmnts(capsCaseDto.getCaseSensitiveCmnts());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndCaseSensitive())) {
					capsCase.setIndCaseSensitive(capsCaseDto.getIndCaseSensitive());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndCaseArchived())) {
					capsCase.setIndCaseArchived(capsCaseDto.getIndCaseArchived());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getDtCaseClosed())) {
					capsCase.setDtCaseClosed(capsCaseDto.getDtCaseClosed());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCdCaseRegion())) {
					capsCase.setCdCaseRegion(capsCaseDto.getCdCaseRegion());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getDtCaseOpened())) {
					capsCase.setDtCaseOpened(capsCaseDto.getDtCaseOpened());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getNmCase())) {
					capsCase.setNmCase(capsCaseDto.getNmCase());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndCaseSuspMeth())) {
					capsCase.setIndCaseSuspMeth(capsCaseDto.getIndCaseSuspMeth());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getCaseSuspMeth())) {
					capsCase.setTxtCaseSuspMeth(capsCaseDto.getCaseSuspMeth());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getSpecHandling())) {
					capsCase.setTxtSpecHandling(capsCaseDto.getSpecHandling());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndCaseAlert())) {
					capsCase.setIndCaseAlert(capsCaseDto.getIndCaseAlert());
				}

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getIndSafetyCheckList())) {
					capsCase.setIndSafetyCheckList(capsCaseDto.getIndSafetyCheckList());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					capsCaseDao.saveCapsCase(capsCase);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					capsCaseDao.updateCapsCase(capsCase);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					capsCaseDao.deleteCapsCase(capsCase);
				}
				idCase = capsCase.getIdCase();

			}
		}
		return idCase;
	}

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situationDto
	 * @param action
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long situationAUD(SituationDto situationDto, String action) {
		Long idSituation = ServiceConstants.ZERO_VAL;
		Situation situation = new Situation();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(situationDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(situationDto.getIdSituation())) {
						situation = situationDao.getSituationEntityById(situationDto.getIdSituation());
					}
				}
				situation.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(situationDto.getIdCase())) {
					CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(situationDto.getIdCase());
					situation.setCapsCase(capsCase);
				}

				if (!TypeConvUtil.isNullOrEmpty(situationDto.getDtSituationClosed())) {
					situation.setDtSituationClosed(situationDto.getDtSituationClosed());
				}
				if (!TypeConvUtil.isNullOrEmpty(situationDto.getDtSituationOpened())) {
					situation.setDtSituationOpened(situationDto.getDtSituationOpened());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					situationDao.saveSituation(situation);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					situationDao.updateSituation(situation);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					situationDao.deleteSituation(situation);
				}
				idSituation = situation.getIdSituation();

			}
		}
		return idSituation;
	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contactDto
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void contactAUD(ContactDto contactDto, String action) {
		Contact contact = new Contact();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(contactDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(contactDto.getIdEvent())) {
						contact = contactDao.getContactEntityById(contactDto.getIdEvent());
					}
				}
				contact.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIdEvent())) {
					contact.setIdEvent(contactDto.getIdEvent());
					Event event = eventDao.getEventById(contactDto.getIdEvent());
					contact.setEvent(event);
				}
				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIdContactStage())) {
					Stage stage = stageDao.getStageEntityById(contactDto.getIdContactStage());
					contact.setStage(stage);
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIdContactWorker())) {
					Person person = personDao.getPersonByPersonId(contactDto.getIdContactWorker());
					contact.setPerson(person);
				}
				if (!TypeConvUtil.isNullOrEmpty(contactDto.getDtCntctMnthlySummBeg())) {
					contact.setDtCntctMnthlySummBeg(contactDto.getDtCntctMnthlySummBeg());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getDtCntctMnthlySummEnd())) {
					contact.setDtCntctMnthlySummEnd(contactDto.getDtCntctMnthlySummEnd());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdContactLocation())) {
					contact.setCdContactLocation(contactDto.getCdContactLocation());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdContactMethod())) {
					contact.setCdContactMethod(contactDto.getCdContactMethod());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdContactOthers())) {
					contact.setCdContactOthers(contactDto.getCdContactOthers());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdContactPurpose())) {
					contact.setCdContactPurpose(contactDto.getCdContactPurpose());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdContactType())) {
					contact.setCdContactType(contactDto.getCdContactType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getDtContactOccurred())) {
					contact.setDtContactOccurred(contactDto.getDtContactOccurred());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndContactAttempted())) {
					contact.setIndContactAttempted(contactDto.getIndContactAttempted());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIdLastEmpUpdate())) {
					contact.setIdLastEmpUpdate(contactDto.getIdLastEmpUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getDtLastEmpUpdate())) {
					contact.setDtLastEmpUpdate(contactDto.getDtLastEmpUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndEmergency())) {
					contact.setIndEmergency(contactDto.getIndEmergency());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdRsnScrout())) {
					contact.setCdRsnScrout(contactDto.getCdRsnScrout());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndRecCons())) {
					contact.setIndRecCons(contactDto.getIndRecCons());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getKinCaregiver())) {
					contact.setTxtKinCaregiver(contactDto.getKinCaregiver());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdRsnAmtne())) {
					contact.setCdRsnAmtne(contactDto.getCdRsnAmtne());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getAmtNeeded())) {
					contact.setAmtNeeded(contactDto.getAmtNeeded());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndSiblingVisit())) {
					contact.setIndSiblingVisit(contactDto.getIndSiblingVisit());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdChildSafety())) {
					contact.setCdChildSafety(contactDto.getCdChildSafety());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdPendLegalAction())) {
					contact.setCdPendLegalAction(contactDto.getCdPendLegalAction());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndPrincipalInterview())) {
					contact.setIndPrincipalInterview(contactDto.getIndPrincipalInterview());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdProfCollateral())) {
					contact.setCdProfCollateral(contactDto.getCdProfCollateral());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getCdAdministrative())) {
					contact.setCdAdministrative(contactDto.getCdAdministrative());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getTxtComments())) {
					contact.setTxtComments(contactDto.getTxtComments());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndAnnounced())) {
					contact.setIndAnnounced(contactDto.getIndAnnounced());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndSafPlanComp())) {
					contact.setIndSafPlanComp(contactDto.getIndSafPlanComp());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndFamPlanComp())) {
					contact.setIndFamPlanComp(contactDto.getIndFamPlanComp());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getIndSafConResolv())) {
					contact.setIndSafConResolv(contactDto.getIndSafConResolv());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getEstContactHours())) {
					contact.setEstContactHours(contactDto.getEstContactHours());
				}

				if (!TypeConvUtil.isNullOrEmpty(contactDto.getEstContactMins())) {
					contact.setEstContactMins(contactDto.getEstContactMins());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					contactDao.saveContact(contact);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					contactDao.updateContact(contact);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					contactDao.deleteContact(contact);
				}

			}
		}
	}

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReviewDto
	 * @param action
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void adminReviewAUD(AdminReviewDto adminReviewDto, String action) {
		AdminReview adminReview = new AdminReview();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdStage())) {
						adminReview = adminReviewDao.getAdminReviewById(adminReviewDto.getIdStage());
					}
				}
				adminReview.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdStage())) {
					adminReview.setIdStage(adminReviewDto.getIdStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdEvent())) {
					//Event event = eventDao.getEventEntityById(adminReviewDto.getIdEvent());
					//adminReview.setEvent(event);
					adminReview.setIdEvent(adminReviewDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdPerson())) {
					Person person = personDao.getPersonByPersonId(adminReviewDto.getIdPerson());
					adminReview.setPerson(person);
				}
				adminReview.setIdCreatedPerson(adminReviewDto.getIdCreatedPerson());
				adminReview.setIdLastUpdatePerson(adminReviewDto.getIdLastUpdatePerson());

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdStage())) {
					adminReview.setIdStage(adminReviewDto.getIdStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdStageRelated())) {
					adminReview.setIdStageRelated(adminReviewDto.getIdStageRelated());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getCdAdminRvAppealResult())) {
					adminReview.setCdAdminRvAppealResult(adminReviewDto.getCdAdminRvAppealResult());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getCdAdminRvAppealType())) {
					adminReview.setCdAdminRvAppealType(adminReviewDto.getCdAdminRvAppealType());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getCdAdminRvAuth())) {
					adminReview.setCdAdminRvAuth(adminReviewDto.getCdAdminRvAuth());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getCdAdminRvStatus())) {
					adminReview.setCdAdminRvStatus(adminReviewDto.getCdAdminRvStatus());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvAppealNotif())) {
					adminReview.setDtAdminRvAppealNotif(adminReviewDto.getDtAdminRvAppealNotif());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvAppealReview())) {
					adminReview.setDtAdminRvAppealReview(adminReviewDto.getDtAdminRvAppealReview());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvDue())) {
					adminReview.setDtAdminRvDue(adminReviewDto.getDtAdminRvDue());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvEmgcyRel())) {
					adminReview.setDtAdminRvEmgcyRel(adminReviewDto.getDtAdminRvEmgcyRel());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvHearing())) {
					adminReview.setDtAdminRvHearing(adminReviewDto.getDtAdminRvHearing());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getDtAdminRvReqAppeal())) {
					adminReview.setDtAdminRvReqAppeal(adminReviewDto.getDtAdminRvReqAppeal());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIndAdminRvEmgcyRel())) {
					adminReview.setIndAdminRvEmgcyRel(adminReviewDto.getIndAdminRvEmgcyRel());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getCdAdminRvReqBy())) {
					adminReview.setCdAdminRvReqBy(adminReviewDto.getCdAdminRvReqBy());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getNmAdminRvReqBy())) {
					adminReview.setNmAdminRvReqBy(adminReviewDto.getNmAdminRvReqBy());
				}

				if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIndChgRoleSp())) {
					adminReview.setIndChgRoleSp(adminReviewDto.getIndChgRoleSp());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					adminReviewDao.saveAdminReview(adminReview);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					adminReviewDao.updateAdminReview(adminReview);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					adminReviewDao.deleteAdminReview(adminReview);
				}

			}
		}
	}

	/**
	 * This DAM adds, updates, or deletes a full row in the ALLEGATION table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINV07D
	 * 
	 * @param AllegationDetailDto
	 * @param action
	 * @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long allegationAUD(AllegationDetailDto allegationDetailDto, String action) {

		Allegation allegation = new Allegation();
		Date date = new Date();

		Long idAllegation = ServiceConstants.ZERO_VAL;
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIdAllegation())) {
						allegation = allegtnDao.getAllegationById(allegationDetailDto.getIdAllegation());
					}
				}
				allegation.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getCdAllegDisposition())) {
					allegation.setCdAllegDisposition(allegationDetailDto.getCdAllegDisposition());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getCdAllegIncidentStage())) {
					allegation.setCdAllegIncidentStage(allegationDetailDto.getCdAllegIncidentStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getCdAllegSeverity())) {
					allegation.setCdAllegSeverity(allegationDetailDto.getCdAllegSeverity());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getCdAllegType())) {
					allegation.setCdAllegType(allegationDetailDto.getCdAllegType());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getAllegDuration())) {
					allegation.setTxtAllegDuration(allegationDetailDto.getAllegDuration());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getDisptnSeverity())) {
					allegation.setTxtDispstnSeverity(allegationDetailDto.getDisptnSeverity());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIdAllegedPerpetrator())) {
					Person person = personDao.getPersonByPersonId(allegationDetailDto.getIdAllegedPerpetrator());
					allegation.setPersonByIdAllegedPerpetrator(person);
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIdStage())) {
					Stage stage = stageDao.getStageEntityById(allegationDetailDto.getIdStage());
					allegation.setStage(stage);
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIdVictim())) {
					Person person = personDao.getPersonByPersonId(allegationDetailDto.getIdVictim());
					allegation.setPersonByIdVictim(person);

				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndFacilAllegCancelHist())) {
					allegation.setIndAllegCancelHist(allegationDetailDto.getIndFacilAllegCancelHist());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndCoSlpgChildDth())) {
					allegation.setIndCoSlpgChildDth(allegationDetailDto.getIndCoSlpgChildDth());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndCoSlpgSubstance())) {
					allegation.setIndCoSlpgSubstance(allegationDetailDto.getIndCoSlpgSubstance());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndFatalAlleg())) {
					allegation.setIndFatality(allegationDetailDto.getIndFatalAlleg());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndNearFatal())) {
					allegation.setIndNearFatal(allegationDetailDto.getIndNearFatal());
				}

				if (!TypeConvUtil.isNullOrEmpty(allegationDetailDto.getIndRelinquishCstdy())) {
					allegation.setIndRelinquishCstdy(allegationDetailDto.getIndRelinquishCstdy());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					allegtnDao.updateAllegation(allegation, ServiceConstants.REQ_FUNC_CD_ADD, false);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					allegtnDao.updateAllegation(allegation, ServiceConstants.REQ_FUNC_CD_UPDATE, false);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					allegtnDao.updateAllegation(allegation, ServiceConstants.REQ_FUNC_CD_DELETE, false);
				}
				idAllegation = allegation.getIdAllegation();

			}
		}
		return idAllegation;
	}

	/**
	 * This DAM is used by the CloseOpenStage common function (CCMN03U) to add a
	 * dummy row to the FACIL_ALLEG table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINVB4D
	 * 
	 * @param facilAllegDto
	 * @
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void facilAllegSave(FacilAllegDto facilAllegDto) {

		FacilAlleg facilAlleg = new FacilAlleg();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(facilAllegDto)) {

			facilAlleg.setDtLastUpdate(date);

			if (!TypeConvUtil.isNullOrEmpty(facilAllegDto.getIdAllegation())) {
				facilAlleg.setIdAllegation(facilAllegDto.getIdAllegation());
			}

			if (!TypeConvUtil.isNullOrEmpty(facilAllegDto.getIndFacilAllegAbOffGr())) {
				facilAlleg.setIndFacilAllegAbOffGr(facilAllegDto.getIndFacilAllegAbOffGr());
			}

			if (!TypeConvUtil.isNullOrEmpty(facilAllegDto.getIndFacilAllegSupvd())) {
				facilAlleg.setIndFacilAllegSupvd(facilAllegDto.getIndFacilAllegSupvd());
			}

			allegtnDao.saveFacilAlleg(facilAlleg);
		}
	}

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvestDetailDto
	 * @
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void apsInvestDetailAUD(ApsInvstDetailDto apsInvstDetailDto, String action, String indAPSInvCnclsn) {

		ApsInvstDetail apsInvstDetail = new ApsInvstDetail();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIdStage())) {
						apsInvstDetail = apsInvstDetailDao.getApsInvstDetailbyParentId(apsInvstDetailDto.getIdStage())
								.get(0);
					}
				}
				apsInvstDetail.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIdStage())) {
					apsInvstDetail.setStage(stageDao.getStageEntityById(apsInvstDetailDto.getIdStage()));
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIdEvent())) {
					apsInvstDetail.setIdEvent(apsInvstDetailDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getDtApsInvstBegun())) {
					apsInvstDetail.setDtApsInvstBegun(apsInvstDetailDto.getDtApsInvstBegun());
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getCdApsInvstFinalPrty())) {
					apsInvstDetail.setCdApsInvstFinalPrty(apsInvstDetailDto.getCdApsInvstFinalPrty());
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getDtApsInvstCmplt())) {
					apsInvstDetail.setDtApsInvstCmplt(apsInvstDetailDto.getDtApsInvstCmplt());
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getCdApsInvstOvrallDisp())) {
					apsInvstDetail.setCdApsInvstOvrallDisp(apsInvstDetailDto.getCdApsInvstOvrallDisp());
				}

				if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getDtApsInvstCltAssmt())) {
					apsInvstDetail.setDtApsInvstCltAssmt(apsInvstDetailDto.getDtApsInvstCltAssmt());
				}

				if (!TypeConvUtil.isNullOrEmpty(indAPSInvCnclsn)) {

					if (indAPSInvCnclsn.equals(ServiceConstants.STRING_IND_Y)) {
						if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getCdClosureType())) {
								apsInvstDetail.setCdClosureType(apsInvstDetailDto.getCdClosureType());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIndExtDoc())) {
								apsInvstDetail.setIndExtDoc(apsInvstDetailDto.getIndExtDoc());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIndLegalAction())) {
								apsInvstDetail.setIndLegalAction(apsInvstDetailDto.getIndLegalAction());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIndFamViolence())) {
								apsInvstDetail.setIndFamViolence(apsInvstDetailDto.getIndFamViolence());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIndEcs())) {
								apsInvstDetail.setIndEcs(apsInvstDetailDto.getIndEcs());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIndClient())) {
								apsInvstDetail.setIndClient(apsInvstDetailDto.getIndClient());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getClientOther())) {
								apsInvstDetail.setTxtClientOther(apsInvstDetailDto.getClientOther());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getCdInterpreter())) {
								apsInvstDetail.setCdInterpreter(apsInvstDetailDto.getCdInterpreter());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getMethodComm())) {
								apsInvstDetail.setTxtMethodComm(apsInvstDetailDto.getMethodComm());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getTrnsNameRlt())) {
								apsInvstDetail.setTxtTrnsNameRlt(apsInvstDetailDto.getTrnsNameRlt());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getAltComm())) {
								apsInvstDetail.setTxtAltComm(apsInvstDetailDto.getAltComm());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getDtClientAdvised())) {
								apsInvstDetail.setDtClientAdvised(apsInvstDetailDto.getDtClientAdvised());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIdProgramAdminPerson())) {
								apsInvstDetail.setIdProgramAdminPerson(apsInvstDetailDto.getIdProgramAdminPerson());
							}

							if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailDto.getIdEntity())) {
								apsInvstDetail.setIdEntity(apsInvstDetailDto.getIdEntity());
							}
						}
					}
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					apsInvstDetailDao.saveApsInvstDetail(apsInvstDetail);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					apsInvstDetailDao.updateApsInvstDetail(apsInvstDetail);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					apsInvstDetailDao.deleteApsInvstDetail(apsInvstDetail);
				}

			}
		}
	}

	/**
	 * This DAM will add, update, and delete from the CPS_INVST_DETAIL table
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV12D
	 * 
	 * @param apsInvstDetailDto
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void cpsInvestDetailAUD(CpsInvstDetailDto cpsInvstDetailDto, String action) {

		CpsInvstDetail cpsInvstDetail = new CpsInvstDetail();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIdEvent())) {
						cpsInvstDetail = cpsInvstDetailDao.getCpsInvstDetailbyEventId(cpsInvstDetailDto.getIdEvent());
					}
				}
				cpsInvstDetail.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIdEvent())) {
					cpsInvstDetail.setIdEvent(cpsInvstDetailDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIdStage())) {
					cpsInvstDetail.setIdEvent(cpsInvstDetailDto.getIdStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getDtCpsInvstDtlComplt())) {
					cpsInvstDetail.setDtCpsInvstDtlComplt(cpsInvstDetailDto.getDtCpsInvstDtlComplt());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getDtCpsInvstDtlBegun())) {
					cpsInvstDetail.setDtCpsInvstDtlBegun(cpsInvstDetailDto.getDtCpsInvstDtlBegun());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndCpsInvstSafetyPln())) {
					cpsInvstDetail.setIndCpsInvstSafetyPln(cpsInvstDetailDto.getIndCpsInvstSafetyPln());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getDtCpsInvstDtlAssigned())) {
					cpsInvstDetail.setDtCpsInvstDtlAssigned(cpsInvstDetailDto.getDtCpsInvstDtlAssigned());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getDtCpsInvstDtlIntake())) {
					cpsInvstDetail.setDtCpsInvstDtlIntake(cpsInvstDetailDto.getDtCpsInvstDtlIntake());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdCpsInvstDtlFamIncm())) {
					cpsInvstDetail.setCdCpsInvstDtlFamIncm(cpsInvstDetailDto.getCdCpsInvstDtlFamIncm());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndCpsInvstDtlEaConcl())) {
					cpsInvstDetail.setIndCpsInvstDtlEaConcl(cpsInvstDetailDto.getIndCpsInvstDtlEaConcl());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndCpsInvstDtlRaNa())) {
					cpsInvstDetail.setIndCpsInvstDtlRaNa(cpsInvstDetailDto.getIndCpsInvstDtlRaNa());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdCpsOverallDisptn())) {
					cpsInvstDetail.setCdCpsInvstDtlOvrllDisptn(cpsInvstDetailDto.getCdCpsOverallDisptn());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndCpsInvstDtlAbbrv())) {
					cpsInvstDetail.setIndCpsInvstDtlAbbrv(cpsInvstDetailDto.getIndCpsInvstDtlAbbrv());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndCpsLeJntCntct())) {
					cpsInvstDetail.setIndCpsLeJntCntct(cpsInvstDetailDto.getIndCpsLeJntCntct());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdReasonNoJntCntct())) {
					cpsInvstDetail.setCdReasonNoJntCntct(cpsInvstDetailDto.getCdReasonNoJntCntct());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdReasonNoJntCntct())) {
					cpsInvstDetail.setTxtReasonNoJntCntct(cpsInvstDetailDto.getCdReasonNoJntCntct());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndVictimTaped())) {
					cpsInvstDetail.setIndVictimTaped(cpsInvstDetailDto.getIndVictimTaped());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdVictimTaped())) {
					cpsInvstDetail.setCdVictimTaped(cpsInvstDetailDto.getCdVictimTaped());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdVictimTaped())) {
					cpsInvstDetail.setTxtVictimTaped(cpsInvstDetailDto.getCdVictimTaped());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndMeth())) {
					cpsInvstDetail.setIndMeth(cpsInvstDetailDto.getIndMeth());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndVictimPhoto())) {
					cpsInvstDetail.setIndVictimPhoto(cpsInvstDetailDto.getIndVictimPhoto());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getCdVictimNoPhotoRsn())) {
					cpsInvstDetail.setCdVictimNoPhotoRsn(cpsInvstDetailDto.getCdVictimNoPhotoRsn());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getVictimPhoto())) {
					cpsInvstDetail.setTxtVictimPhoto(cpsInvstDetailDto.getVictimPhoto());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndParentGivenGuide())) {
					cpsInvstDetail.setIndParentGivenGuide(cpsInvstDetailDto.getIndParentGivenGuide());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndParentNotify24h())) {
					cpsInvstDetail.setIndParentGivenGuide(cpsInvstDetailDto.getIndParentGivenGuide());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndMultPersFound())) {
					cpsInvstDetail.setIndMultPersFound(cpsInvstDetailDto.getIndMultPersFound());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndMultPersMerged())) {
					cpsInvstDetail.setIndMultPersMerged(cpsInvstDetailDto.getIndMultPersMerged());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndFtmOffered())) {
					cpsInvstDetail.setIndFtmOffered(cpsInvstDetailDto.getIndFtmOffered());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndFtmOccurred())) {
					cpsInvstDetail.setIndFtmOccurred(cpsInvstDetailDto.getIndFtmOccurred());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndReqOrders())) {
					cpsInvstDetail.setIndReqOrders(cpsInvstDetailDto.getIndReqOrders());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getRsnOvrllDisptn())) {
					cpsInvstDetail.setTxtRsnOvrllDisptn(cpsInvstDetailDto.getRsnOvrllDisptn());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getRsnOpenServices())) {
					cpsInvstDetail.setTxtRsnOpenServices(cpsInvstDetailDto.getRsnOpenServices());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getRsnInvClosed())) {
					cpsInvstDetail.setTxtRsnInvClosed(cpsInvstDetailDto.getRsnInvClosed());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getAbsentParent())) {
					cpsInvstDetail.setTxtAbsentParent(cpsInvstDetailDto.getAbsentParent());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndAbsentParent())) {
					cpsInvstDetail.setIndAbsentParent(cpsInvstDetailDto.getIndAbsentParent());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndChildSexTraffic())) {
					cpsInvstDetail.setIndChildSexTraffic(cpsInvstDetailDto.getIndChildSexTraffic());
				}

				if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailDto.getIndChildLaborTraffic())) {
					cpsInvstDetail.setIndChildLaborTraffic(cpsInvstDetailDto.getIndChildLaborTraffic());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					cpsInvstDetailDao.updtCpsInvstDetail(cpsInvstDetail, ServiceConstants.REQ_FUNC_CD_ADD);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					cpsInvstDetailDao.updtCpsInvstDetail(cpsInvstDetail, ServiceConstants.REQ_FUNC_CD_UPDATE);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					cpsInvstDetailDao.updtCpsInvstDetail(cpsInvstDetail, ServiceConstants.REQ_FUNC_CD_DELETE);
				}

			}
		}
	}

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 * 
	 * @param licensingInvstDtlDto
	 * @param action
	 * @
	 */
	@Override
	public void licensingInvstDtlAUD(LicensingInvstDtlDto licensingInvstDtlDto, String action) {

		LicensingInvstDtl licensingInvstDtl = new LicensingInvstDtl();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdLicngInvstStage())) {
						licensingInvstDtl = licensingInvstDtoDao
								.getLicensingInvstDtlDaobyParentId(licensingInvstDtlDto.getIdLicngInvstStage()).get(0);
					}
				}
				licensingInvstDtl.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdLicngInvstStage())) {
					licensingInvstDtl
							.setStage(stageDao.getStageEntityById(licensingInvstDtlDto.getIdLicngInvstStage()));
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdEvent())) {
					licensingInvstDtl.setIdEvent(licensingInvstDtlDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getCdLicngInvstOvrallDisp())) {
					licensingInvstDtl.setCdLicngInvstOvrallDisp(licensingInvstDtlDto.getCdLicngInvstOvrallDisp());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getDtLicngInvstIntake())) {
					licensingInvstDtl.setDtLicngInvstIntake(licensingInvstDtlDto.getDtLicngInvstIntake());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getDtLicngInvstBegun())) {
					licensingInvstDtl.setDtLicngInvstBegun(licensingInvstDtlDto.getDtLicngInvstBegun());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getDtLicngInvstComplt())) {
					licensingInvstDtl.setDtLicngInvstComplt(licensingInvstDtlDto.getDtLicngInvstComplt());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getDtLicngInvstAssigned())) {
					licensingInvstDtl.setDtLicngInvstAssigned(licensingInvstDtlDto.getDtLicngInvstAssigned());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getTxtLicngInvstNoncomp())) {
					licensingInvstDtl.setTxtLicngInvstNoncomp(licensingInvstDtlDto.getTxtLicngInvstNoncomp());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getCdLicngInvstCoractn())) {
					licensingInvstDtl.setCdLicngInvstCoractn(licensingInvstDtlDto.getCdLicngInvstCoractn());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getCdIncidentLoc())) {
					licensingInvstDtl.setCdIncidentLoc(licensingInvstDtlDto.getCdIncidentLoc());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdResource())) {
					licensingInvstDtl.setIdResource(licensingInvstDtlDto.getIdResource());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNmResource())) {
					licensingInvstDtl.setNmResource(licensingInvstDtlDto.getNmResource());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
					licensingInvstDtl.setNbrAcclaim(licensingInvstDtlDto.getNbrAcclaim());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getCdRsrcFacilType())) {
					licensingInvstDtl.setCdRsrcFacilType(licensingInvstDtlDto.getCdRsrcFacilType());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getTxtComments())) {
					licensingInvstDtl.setTxtComments(licensingInvstDtlDto.getTxtComments());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrPhone())) {
					licensingInvstDtl.setNbrPhone(licensingInvstDtlDto.getNbrPhone());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrPhoneExt())) {
					licensingInvstDtl.setNbrPhoneExt(licensingInvstDtlDto.getNbrPhoneExt());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAttn())) {
					licensingInvstDtl.setAddrAttn(licensingInvstDtlDto.getAddrAttn());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrStLn1())) {
					licensingInvstDtl.setAddrStLn1(licensingInvstDtlDto.getAddrStLn1());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrStLn2())) {
					licensingInvstDtl.setAddrStLn2(licensingInvstDtlDto.getAddrStLn2());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrCity())) {
					licensingInvstDtl.setAddrCity(licensingInvstDtlDto.getAddrCity());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrCounty())) {
					licensingInvstDtl.setAddrCounty(licensingInvstDtlDto.getAddrCounty());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrState())) {
					licensingInvstDtl.setAddrState(licensingInvstDtlDto.getAddrState());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrZip())) {
					licensingInvstDtl.setAddrZip(licensingInvstDtlDto.getAddrZip());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdAffilResource())) {
					licensingInvstDtl.setIdAffilResource(licensingInvstDtlDto.getIdAffilResource());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNmAffilResource())) {
					licensingInvstDtl.setNmAffilResource(licensingInvstDtlDto.getNmAffilResource());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getTxtAffilComments())) {
					licensingInvstDtl.setTxtAffilComments(licensingInvstDtlDto.getTxtAffilComments());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAffilPhone())) {
					licensingInvstDtl.setNbrAffilPhone(licensingInvstDtlDto.getNbrAffilPhone());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAffilPhoneExt())) {
					licensingInvstDtl.setNbrAffilPhoneExt(licensingInvstDtlDto.getNbrAffilPhoneExt());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilAttn())) {
					licensingInvstDtl.setAddrAffilAttn(licensingInvstDtlDto.getAddrAffilAttn());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilStLn1())) {
					licensingInvstDtl.setAddrAffilStLn1(licensingInvstDtlDto.getAddrAffilStLn1());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilStLn2())) {
					licensingInvstDtl.setAddrAffilStLn2(licensingInvstDtlDto.getAddrAffilStLn2());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilCity())) {
					licensingInvstDtl.setAddrAffilCity(licensingInvstDtlDto.getAddrAffilCity());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilCounty())) {
					licensingInvstDtl.setAddrAffilCounty(licensingInvstDtlDto.getAddrAffilCounty());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilState())) {
					licensingInvstDtl.setAddrAffilState(licensingInvstDtlDto.getAddrAffilState());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getAddrAffilZip())) {
					licensingInvstDtl.setAddrAffilZip(licensingInvstDtlDto.getAddrAffilZip());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdClassFclty())) {
					licensingInvstDtl.setIdClassFclty(licensingInvstDtlDto.getIdClassFclty());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIdClassAffilFclty())) {
					licensingInvstDtl.setIdClassAffilFclty(licensingInvstDtlDto.getIdClassAffilFclty());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
					licensingInvstDtl.setNbrAcclaim(licensingInvstDtlDto.getNbrAcclaim());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAgency())) {
					licensingInvstDtl.setNbrAgency(licensingInvstDtlDto.getNbrAgency());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrBranch())) {
					licensingInvstDtl.setNbrBranch(licensingInvstDtlDto.getNbrBranch());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAffilAgency())) {
					licensingInvstDtl.setNbrAffilAgency(licensingInvstDtlDto.getNbrAffilAgency());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getNbrAffilBranch())) {
					licensingInvstDtl.setNbrAffilBranch(licensingInvstDtlDto.getNbrAffilBranch());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getCdAffilFacilType())) {
					licensingInvstDtl.setCdAffilFacilType(licensingInvstDtlDto.getCdAffilFacilType());
				}

				if (!TypeConvUtil.isNullOrEmpty(licensingInvstDtlDto.getIndRestraint())) {
					licensingInvstDtl.setIndRestraint(licensingInvstDtlDto.getIndRestraint());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					licensingInvstDtoDao.saveLicensingInvstDtl(licensingInvstDtl);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					licensingInvstDtoDao.saveLicensingInvstDtl(licensingInvstDtl);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					licensingInvstDtoDao.licensingInvstDtlDelete(licensingInvstDtl);
				}

			}
		}
	}

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV54D
	 * 
	 * @param facilityInvstDtlDto
	 * @param action
	 * @
	 */
	@Override
	public void facilityInvstDtlAUD(FacilityInvstDtlDto facilityInvstDtlDto, String action) {

		FacilityInvstDtl facilityInvstDtl = new FacilityInvstDtl();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto)) {

				facilityInvstDtl.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getIdEvent())) {
					facilityInvstDtl.setIdEvent(facilityInvstDtlDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getIdStage())) {
					facilityInvstDtl.setStage(stageDao.getStageEntityById(facilityInvstDtlDto.getIdStage()));
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getCapsResourceByIdFacilResource())) {
					facilityInvstDtl.setCapsResourceByIdFacilResource(capsResourceDao
							.getCapsResourceById(facilityInvstDtlDto.getCapsResourceByIdFacilResource()));
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getCapsResourceByIdAffilResource())) {
					facilityInvstDtl.setCapsResourceByIdAffilResource(capsResourceDao
							.getCapsResourceById(facilityInvstDtlDto.getCapsResourceByIdAffilResource()));
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getCdFacilInvstOvrallDis())) {
					facilityInvstDtl.setCdFacilInvstOvrallDis(facilityInvstDtlDto.getCdFacilInvstOvrallDis());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffAttn())) {
					facilityInvstDtl.setAddrFacilInvstAffAttn(facilityInvstDtlDto.getAddrFacilInvstAffAttn());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffCity())) {
					facilityInvstDtl.setAddrFacilInvstAffCity(facilityInvstDtlDto.getAddrFacilInvstAffCity());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffCnty())) {
					facilityInvstDtl.setAddrFacilInvstAffCnty(facilityInvstDtlDto.getAddrFacilInvstAffCnty());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffSt())) {
					facilityInvstDtl.setAddrFacilInvstAffSt(facilityInvstDtlDto.getAddrFacilInvstAffSt());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffStr1())) {
					facilityInvstDtl.setAddrFacilInvstAffStr1(facilityInvstDtlDto.getAddrFacilInvstAffStr1());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffStr2())) {
					facilityInvstDtl.setAddrFacilInvstAffStr2(facilityInvstDtlDto.getAddrFacilInvstAffStr2());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAffZip())) {
					facilityInvstDtl.setAddrFacilInvstAffZip(facilityInvstDtlDto.getAddrFacilInvstAffZip());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstAttn())) {
					facilityInvstDtl.setAddrFacilInvstAttn(facilityInvstDtlDto.getAddrFacilInvstAttn());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstCity())) {
					facilityInvstDtl.setAddrFacilInvstCity(facilityInvstDtlDto.getAddrFacilInvstCity());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstCnty())) {
					facilityInvstDtl.setAddrFacilInvstCnty(facilityInvstDtlDto.getAddrFacilInvstCnty());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstState())) {
					facilityInvstDtl.setAddrFacilInvstState(facilityInvstDtlDto.getAddrFacilInvstState());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstStr1())) {
					facilityInvstDtl.setAddrFacilInvstStr1(facilityInvstDtlDto.getAddrFacilInvstStr1());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstStr2())) {
					facilityInvstDtl.setAddrFacilInvstStr2(facilityInvstDtlDto.getAddrFacilInvstStr2());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getAddrFacilInvstZip())) {
					facilityInvstDtl.setAddrFacilInvstZip(facilityInvstDtlDto.getAddrFacilInvstZip());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getDtFacilInvstIntake())) {
					facilityInvstDtl.setDtFacilInvstIntake(facilityInvstDtlDto.getDtFacilInvstIntake());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getDtFacilInvstIncident())) {
					facilityInvstDtl.setDtFacilInvstIncident(facilityInvstDtlDto.getDtFacilInvstIncident());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getDtFacilInvstBegun())) {
					facilityInvstDtl.setDtFacilInvstBegun(facilityInvstDtlDto.getDtFacilInvstBegun());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getDtFacilInvstComplt())) {
					facilityInvstDtl.setDtFacilInvstComplt(facilityInvstDtlDto.getDtFacilInvstComplt());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstAffilPhn())) {
					facilityInvstDtl.setNbrFacilInvstAffilPhn(facilityInvstDtlDto.getFacilInvstAffilPhn());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstPhone())) {
					facilityInvstDtl.setNbrFacilInvstPhone(facilityInvstDtlDto.getFacilInvstPhone());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstAffilExt())) {
					facilityInvstDtl.setNbrFacilInvstAffilExt(facilityInvstDtlDto.getFacilInvstAffilExt());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstExtension())) {
					facilityInvstDtl.setNbrFacilInvstExtension(facilityInvstDtlDto.getFacilInvstExtension());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstAffilCmnt())) {
					facilityInvstDtl.setTxtFacilInvstAffilCmnt(facilityInvstDtlDto.getFacilInvstAffilCmnt());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getFacilInvstComments())) {
					facilityInvstDtl.setTxtFacilInvstComments(facilityInvstDtlDto.getFacilInvstComments());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getNmFacilInvstAff())) {
					facilityInvstDtl.setNmFacilInvstAff(facilityInvstDtlDto.getNmFacilInvstAff());
				}

				if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtlDto.getNmFacilInvstFacility())) {
					facilityInvstDtl.setNmFacilInvstFacility(facilityInvstDtlDto.getNmFacilInvstFacility());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					facilityInvstDtlDao.facilityInvstDtlSave(facilityInvstDtl);
				}

			}
		}
	}

	/**
	 * AUD DAM for the EVENT_PLAN_LINK table. Currently only performs INSERTs.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDE8D
	 * 
	 * @param eventPlanLinkDto
	 * @param action
	 * @
	 */
	@Override
	public void eventPlanLinkAUD(EventPlanLinkDto eventPlanLinkDto, String action) {

		EventPlanLink eventPlanLink = new EventPlanLink();
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(eventPlanLinkDto)) {

				eventPlanLink.setDtLastUpdate(date);

				if (!TypeConvUtil.isNullOrEmpty(eventPlanLinkDto.getIdEvent())) {
					eventPlanLink.setEvent(eventDao.getEventEntityById(eventPlanLinkDto.getIdEvent()));
				}

				if (!TypeConvUtil.isNullOrEmpty(eventPlanLinkDto.getIndImpactCreated())) {
					eventPlanLink.setIndImpactCreated(eventPlanLinkDto.getIndImpactCreated());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					eventDao.eventPlanLinkSave(eventPlanLink, eventPlanLinkDto.getIdEvent());
				}

			}
		}
	}

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristicsDto
	 * @param lNbrPersonAge
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void characteristicsAUD(CharacteristicsDto characteristicsDto, int lNbrPersonAge, String action,
			List<Long> idCharacteristics) {
		// List<Long> idCharacteristics = null;

		Characteristics characteristics = new Characteristics();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (action.equals(ServiceConstants.WINDOW_MODE_PERSON)) {
				if (!TypeConvUtil.isNullOrEmpty(characteristicsDto)) {
					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getIdPerson())) {

						idCharacteristics = characteristicsDao
								.getCharacteristicsIdsByPersonId(characteristicsDto.getIdPerson());
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(idCharacteristics)) {
					if (!TypeConvUtil.isNullOrEmpty(lNbrPersonAge)) {
						if (lNbrPersonAge < ServiceConstants.AGED_PERSON_AGE) {
							action = ServiceConstants.REQ_FUNC_CD_UPDATE;
						} else {
							action = ServiceConstants.REQ_FUNC_CD_NO_ACTION;
						}
					}
				} else {
					if (!TypeConvUtil.isNullOrEmpty(lNbrPersonAge)) {
						if (lNbrPersonAge >= ServiceConstants.AGED_PERSON_AGE) {
							action = ServiceConstants.REQ_FUNC_CD_ADD;
						} else {
							action = ServiceConstants.REQ_FUNC_CD_NO_ACTION;
						}
					}
				}

			}
			if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				if (!TypeConvUtil.isNullOrEmpty(characteristicsDto)) {
					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getIdPerson())) {
						characteristics.setIdPerson(characteristicsDto.getIdPerson());
					}
					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getIdPerson())) {
						characteristics.setDtLastUpdate(characteristicsDto.getDtLastUpdate());
					}
					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getCdCharCategory())) {
						characteristics.setCdCharCategory(characteristicsDto.getCdCharCategory());
					}

					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getCdCharacteristic())) {
						characteristics.setCdCharacteristic(characteristicsDto.getCdCharacteristic());
					}

					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getDtCharStart())) {
						Date dtCharStart = characteristicsDto.getDtCharStart();
						Calendar cal = Calendar.getInstance();
						cal.setTime(dtCharStart);
						// cal.add(Calendar.DATE, 1);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						dtCharStart = cal.getTime();
						characteristics.setDtCharStart(dtCharStart);
					}

					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getDtCharEnd())) {
						Date dtCharEnd = characteristicsDto.getDtCharEnd();
						Calendar cal = Calendar.getInstance();
						cal.setTime(dtCharEnd);
						// cal.add(Calendar.DATE, 1);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						dtCharEnd = cal.getTime();
						characteristics.setDtCharEnd(dtCharEnd);
					}

					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getCdStatus())) {
						characteristics.setCdStatus(characteristicsDto.getCdStatus());
					}

					if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getIndAfcars())) {
						characteristics.setIndAfcars(characteristicsDto.getIndAfcars());
					}

				}
				characteristicsDao.characteristicsSave(characteristics);
			}

			if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {

				if (!TypeConvUtil.isNullOrEmpty(idCharacteristics)) {
					for (Long idChar : idCharacteristics) {
						characteristics = characteristicsDao.getCharacteristicsById(idChar);

						if (!TypeConvUtil.isNullOrEmpty(characteristicsDto)) {
							if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getDtCharStart())) {
								Date dtCharStart = characteristicsDto.getDtCharStart();
								Calendar cal = Calendar.getInstance();
								cal.setTime(dtCharStart);
								// cal.add(Calendar.DATE, 1);
								cal.set(Calendar.HOUR_OF_DAY, 0);
								cal.set(Calendar.MINUTE, 0);
								cal.set(Calendar.SECOND, 0);
								dtCharStart = cal.getTime();
								characteristics.setDtCharStart(dtCharStart);
							}

							if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getDtCharEnd())) {
								Date dtCharEnd = characteristicsDto.getDtCharEnd();
								Calendar cal = Calendar.getInstance();
								cal.setTime(dtCharEnd);
								// cal.add(Calendar.DATE, 1);
								cal.set(Calendar.HOUR_OF_DAY, 0);
								cal.set(Calendar.MINUTE, 0);
								cal.set(Calendar.SECOND, 0);
								dtCharEnd = cal.getTime();
								characteristics.setDtCharEnd(dtCharEnd);
							}

							if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getCdStatus())) {
								characteristics.setCdStatus(characteristicsDto.getCdStatus());
							}

							if (!TypeConvUtil.isNullOrEmpty(characteristicsDto.getIndAfcars())) {
								characteristics.setIndAfcars(characteristicsDto.getIndAfcars());
							}
						}

						characteristicsDao.characteristicsUpdate(characteristics);
					}
				}

			}

		}
	}

	/**
	 * This DAM will update all tables associated with the Investigation Person
	 * Detail window. It evolved with the functionality of the window, but in
	 * essence just updates those fields needed for Person Detail. Case ADD:
	 * Always Add to PERSON table, CATEGORY STAGE PERSON LINK Table. If a person
	 * has a a full name of Unknown and is a Principle in the Case, concatenate
	 * the ID PERSON to the end (eg, Unknown 1119). Do not update name table in
	 * this case. Case UPDATE: Always update PERSON and STAGE PERSON LINK
	 * tables. Case DELETE: Always Delete from STAGE PERSON LINK, Update the
	 * Status on the PERSON table. Case LOWER:(A person is being related to the
	 * Stage.) Add to the STAGE_PERSON LINK table. Update the CATEGORY table
	 * only if the Client Sends a category. Case PERSON: A full row update of
	 * the PERSON TABLE.
	 * 
	 * WARNING - This DAM contain non-GENDAM-generated code. Care should be
	 * taken when regenerating.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV41D
	 *
	 * @param personDto
	 * @param stagePersonDto
	 * @param personIdDto
	 * @param personCategoryDto
	 * @param action
	 * @
	 */
	@Override
	public void investigationPersonDtlAUD(PersonDto personDto, StagePersonLinkDto stagePersonLinkDto,
			PersonIdDto personIdDto, PersonCategoryDto personCategoryDto, String action) {

		Long idPerson = ServiceConstants.ZERO_VAL;
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		PersonCategory personCategory = new PersonCategory();
		PersonId personId = new PersonId();
		Date date = new Date();

		List<PersonCategory> personCategoryList = null;
		if (!TypeConvUtil.isNullOrEmpty(action)) {

			if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				if (!TypeConvUtil.isNullOrEmpty(personDto)) {
					idPerson = this.personAUD(personDto, ServiceConstants.REQ_FUNC_CD_ADD);
				}
				if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDto)) {
					if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
						stagePersonLinkDto.setIdPerson(idPerson);
					}
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
				}
				if (!TypeConvUtil.isNullOrEmpty(personCategoryDto)) {
					if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
						personCategory.setIdPerson(idPerson);
					}
					if (!TypeConvUtil.isNullOrEmpty(personCategoryDto.getCdPersonCategory())) {
						personCategory.setCdPersonCategory(personCategoryDto.getCdPersonCategory());
					}
					personCategoryDao.savePersonCategory(personCategory);
				}
				if (!TypeConvUtil.isNullOrEmpty(personIdDto)) {
					if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
						personId.setPerson(personDao.getPersonByPersonId(idPerson));
					}
					personId.setDtPersonIdStart(date);
					personId.setCdPersonIdType(ServiceConstants.PERSON_ID_TYPE_SSN);
					if (!TypeConvUtil.isNullOrEmpty(personIdDto.getPersonIdNumber())) {
						personId.setNbrPersonIdNumber(personIdDto.getPersonIdNumber());
					}
					personIdDao.savePersonId(personId);
				}
			} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				if (!TypeConvUtil.isNullOrEmpty(personDto)) {
					this.personAUD(personDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (stagePersonLinkDto != null) {
					stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
				}
			} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
				if (!TypeConvUtil.isNullOrEmpty(personDto)) {
					this.personAUD(personDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
				stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
			} else if (action.equals(ServiceConstants.WINDOW_MODE_LOWER)) {
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
				this.personAUD(personDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (!TypeConvUtil.isNullOrEmpty(personCategoryDto)) {
					if (!TypeConvUtil.isNullOrEmpty(personCategoryDto.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(personCategoryDto.getCdPersonCategory())) {
						personCategoryList = personCategoryDao.getPersonCategoryByPersonIdAndCategory(
								personCategoryDto.getIdPerson(), personCategoryDto.getCdPersonCategory());
					}
				}
				if (TypeConvUtil.isNullOrEmpty(personCategoryList)) {
					if (!TypeConvUtil.isNullOrEmpty(personCategoryDto)) {
						if (!TypeConvUtil.isNullOrEmpty(personCategoryDto.getIdPerson())) {
							personCategory.setIdPerson(personCategoryDto.getIdPerson());
						}
						if (!TypeConvUtil.isNullOrEmpty(personCategoryDto.getCdPersonCategory())) {
							personCategory.setCdPersonCategory(personCategoryDto.getCdPersonCategory());
						}
						personCategoryDao.savePersonCategory(personCategory);
					}
				}
			} else if (action.equals(ServiceConstants.WINDOW_MODE_PERSON)) {
				this.personAUD(personDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}

		}
	}

	/**
	 * This DAM will update all tables associated with the Investigation Person
	 * Detail window. It evolved with the functionality of the window, but in
	 * essence just updates those fields needed for Person Detail. Case ADD:
	 * Always Add to PERSON table, CATEGORY STAGE PERSON LINK Table. If a person
	 * has a a full name of Unknown and is a Principle in the Case, concatenate
	 * the ID PERSON to the end (eg, Unknown 1119). Do not update name table in
	 * this case. Case UPDATE: Always update PERSON and STAGE PERSON LINK
	 * tables. Case DELETE: Always Delete from STAGE PERSON LINK, Update the
	 * Status on the PERSON table. Case LOWER:(A person is being related to the
	 * Stage.) Add to the STAGE_PERSON LINK table. Update the CATEGORY table
	 * only if the Client Sends a category. Case PERSON: A full row update of
	 * the PERSON TABLE.
	 * 
	 * WARNING - This DAM contain non-GENDAM-generated code. Care should be
	 * taken when regenerating.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV41D
	 * 
	 * @param personDto
	 * @param action
	 * @return @
	 */
	@Override
	public Long personAUD(PersonDto personDto, String action) {

		Long idPerson = ServiceConstants.ZERO_VAL;
		Person person = new Person();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(personDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
						|| action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(personDto.getIdPerson())) {
						person = personDao.getPersonByPersonId(personDto.getIdPerson());
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getPersonAge())) {
					person.setNbrPersonAge(personDto.getPersonAge().shortValue());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getDtPersonDeath())) {
					person.setDtPersonDeath(personDto.getDtPersonDeath());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getDtPersonBirth())) {
					person.setDtPersonBirth(personDto.getDtPersonBirth());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonStatus())) {
					person.setCdPersonStatus(personDto.getCdPersonStatus());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonDeath())) {
					person.setCdPersonDeath(personDto.getCdPersonDeath());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdDeathRsnCps())) {
					person.setCdDeathRsnCps(personDto.getCdDeathRsnCps());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdMannerDeath())) {
					person.setCdMannerDeath(personDto.getCdMannerDeath());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdDeathCause())) {
					person.setCdDeathCause(personDto.getCdDeathCause());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdDeathAutpsyRslt())) {
					person.setCdDeathAutpsyRslt(personDto.getCdDeathAutpsyRslt());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdDeathFinding())) {
					person.setCdDeathFinding(personDto.getCdDeathFinding());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getFatalityDetails())) {
					person.setTxtFatalityDetails(personDto.getFatalityDetails());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonMaritalStatus())) {
					person.setCdPersonMaritalStatus(personDto.getCdPersonMaritalStatus());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonLanguage())) {
					person.setCdPersonLanguage(personDto.getCdPersonLanguage());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonSex())) {
					person.setCdPersonSex(personDto.getCdPersonSex());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getNmPersonFull())) {
					person.setNmPersonFull(personDto.getNmPersonFull());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonEthnicGroup())) {
					person.setCdPersonEthnicGroup(personDto.getCdPersonEthnicGroup());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonReligion())) {
					person.setCdPersonReligion(personDto.getCdPersonReligion());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonChar())) {
					person.setCdPersonChar(personDto.getCdPersonChar());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getIndPersonDobApprox())) {
					person.setIndPersonDobApprox(personDto.getIndPersonDobApprox());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonLivArr())) {
					person.setCdPersonLivArr(personDto.getCdPersonLivArr());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdOccupation())) {
					person.setTxtPersonOccupation(personDto.getCdOccupation());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdOccupation())) {
					person.setCdOccupation(personDto.getCdOccupation());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdDisasterRlf())) {
					person.setCdDisasterRlf(personDto.getCdDisasterRlf());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getIndEducationPortfolio())) {
					person.setIndEducationPortfolio(personDto.getIndEducationPortfolio());
				}
				if (!TypeConvUtil.isNullOrEmpty(personDto.getCdTribeEligible())) {
					person.setCdTribeEligible(personDto.getCdTribeEligible());
				}

				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					personDao.savePerson(person);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					personDao.updatePerson(person);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					personDao.deletePerson(person);
				}
				if (!TypeConvUtil.isNullOrEmpty(person.getIdPerson())) {
					idPerson = person.getIdPerson();
				}

			}

		}
		return idPerson;
	}

	/**
	 * This DAM ADDs a full row in the STAGE_LINK table. It does not perform any
	 * UPDATE or DELETE functionality.
	 * 
	 * Service Name : CCMN03U, CCMNC1D
	 * 
	 * @param stageLink
	 * @
	 */
	@Override
	public void stageLinkSave(StageLinkDto stageLinkDto) {
		StageLink stageLink = new StageLink();
		if (!TypeConvUtil.isNullOrEmpty(stageLinkDto)) {
			if (!TypeConvUtil.isNullOrEmpty(stageLinkDto.getIdStage())) {
				stageLink.setIdStage(stageLinkDto.getIdStage());
			}
			if (!TypeConvUtil.isNullOrEmpty(stageLinkDto.getIdPriorStage())) {
				stageLink.setIdPriorStage(stageLinkDto.getIdPriorStage());
			}
			stageDao.stageLinkSave(stageLink);
		}
	}

	/**
	 * The DAM will insert a new SVC_AUTH_ID for a particular event.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUD34D
	 * 
	 * @param svcAuthEventLink
	 * @
	 */
	@Override
	public void svcAuthEventLinkSave(Long idSvcAuthEvent, Long idSvcAuth) {
		SvcAuthEventLink svcAuthEventLink = new SvcAuthEventLink();
		if (!TypeConvUtil.isNullOrEmpty(idSvcAuthEvent)) {
			svcAuthEventLink.setIdSvcAuthEvent(idSvcAuthEvent);
		}
		if (!TypeConvUtil.isNullOrEmpty(idSvcAuth)) {
			svcAuthEventLink
					.setServiceAuthorization(serviceAuthorizationDao.getServiceAuthorizationEntityById(idSvcAuth));
		}
		svcAuthEventLink.setDtLastUpdate(new Date());
		serviceAuthorizationDao.svcAuthEventLinkSave(svcAuthEventLink);
	}

	/**
	 * This DAM will add and delete a row from the ADPT_SUB_EVENT_LINK table.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDB2D
	 * 
	 * @param adptSubEventLinkEditDto
	 * @param action
	 * @
	 */
	@Override
	public void adptSubEventLinkEdit(AdptSubEventLinkDto adptSubEventLinkDto, String action) {
		AdptSubEventLink adptSubEventLink = new AdptSubEventLink();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(adptSubEventLinkDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					if (!TypeConvUtil.isNullOrEmpty(adptSubEventLinkDto.getIdEvent())) {
						adptSubEventLink.setEvent(eventDao.getEventEntityById(adptSubEventLinkDto.getIdEvent()));
					}
					if (!TypeConvUtil.isNullOrEmpty(adptSubEventLinkDto.getIdAdoptionSubsidy())) {
						adptSubEventLink.setAdoptionSubsidy(
								eventDao.getAdoptionSubsidyEntityByIdEvent(adptSubEventLinkDto.getIdAdoptionSubsidy()));
					}
					eventDao.adptSubEventLinkSave(adptSubEventLink);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(adptSubEventLinkDto.getIdEvent())) {
						eventDao.deleteAdptSubEventLinkByIdEvent(adptSubEventLinkDto.getIdEvent());
					}
				}
			}
		}
	}

	/**
	 * This DAM will add and delete a row from the ADPT_ASST_APP_EVENT_LINK
	 * table.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDM2D
	 * 
	 * @param adptAsstAppEventLinkDto
	 * @param action
	 * @
	 */
	@Override
	public void adptAsstAppEventLinkEdit(AdptAsstAppEventLinkDto adptAsstAppEventLinkDto, String action) {
		AdptAsstAppEventLink adptAsstAppEventLink = new AdptAsstAppEventLink();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdAdptEligApplication())) {
						AdptEligApplication adptEligApplication = new AdptEligApplication();
						adptEligApplication
								.setIdAdptEligApplication(adptAsstAppEventLinkDto.getIdAdptEligApplication());
						adptAsstAppEventLink.setAdptEligApplication(adptEligApplication);
					}
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdAdptEligRecert())) {
						AdptEligRecert adptEligRecert = new AdptEligRecert();
						adptEligRecert.setIdAdptEligRecert(adptAsstAppEventLinkDto.getIdAdptEligRecert());
						adptAsstAppEventLink.setAdptEligRecert(adptEligRecert);
					}
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdEvent())) {
						adptAsstAppEventLink
								.setEvent(eventDao.getEventEntityById(adptAsstAppEventLinkDto.getIdEvent()));
					}
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdCapsCase())) {
						adptAsstAppEventLink.setCapsCase(
								capsCaseDao.getCapsCaseEntityById(adptAsstAppEventLinkDto.getIdCapsCase()));
					}
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdCreatedPerson())) {
						adptAsstAppEventLink.setIdCreatedPerson(adptAsstAppEventLinkDto.getIdCreatedPerson());
					}
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdLastUpdatePerson())) {
						adptAsstAppEventLink.setIdLastUpdatePerson(adptAsstAppEventLinkDto.getIdLastUpdatePerson());
					}
					eventDao.AdptAsstAppEventLinkSave(adptAsstAppEventLink);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
					if (!TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDto.getIdEvent())) {
						eventDao.deleteApdtAsstAppEventLinkByIdEvent(adptAsstAppEventLinkDto.getIdEvent());
					}
				}
			}
		}
	}
	@Override
	public void reopenClosedStage(ReopenStageDto reopenStageDto) {
		Long idStage = reopenStageDto.getIdStage();
		Long idCase = reopenStageDto.getIdCase();
		Long idPerson = reopenStageDto.getIdPerson();
		
		Stage stage = stageDao.getStageEntityById(idStage);
		
		final Long idPrimaryChild;
		
		stage.setDtStageClose(null);
		stage.setCdStageReasonClosed(null);
		stage.setTxtStageClosureCmnts(null);
		stageDao.updateStage(stage);
		
		PostEventIPDto postEventInput = new PostEventIPDto();

		postEventInput.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
		postEventInput.setCdEventType(ServiceConstants.EVENT_TYPE_ROP);
		
		String cdStage = stage.getCdStage();
		
		if(ServiceConstants.CSTAGES_SUB.equals(cdStage)) {
			postEventInput.setEventDescr(ServiceConstants.SUB_EVENT_TXT_DESCRIPTION);
		}
		else if(ServiceConstants.CSTAGES_FSU.equals(cdStage)) {
			postEventInput.setEventDescr(ServiceConstants.FSU_EVENT_TXT_DESCRIPTION);
		}
		else if(ServiceConstants.CSTAGES_FRE.equals(cdStage)) {
			postEventInput.setEventDescr(ServiceConstants.FRE_EVENT_TXT_DESCRIPTION);
		}
		else {
			postEventInput.setEventDescr(ServiceConstants.PAD_EVENT_TXT_DESCRIPTION);
		}
		postEventInput.setIdStage(idStage);
		postEventInput.setDtEventOccurred(DateUtils.getDateWithoutTime(new Date()));
		postEventInput.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

		List<PostEventDto> postEventDtos = new ArrayList<>();
		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventDtos.add(postEventDto);

		postEventInput.setPostEventDto(postEventDtos);

		postEventService.checkPostEventStatus(postEventInput, serviceReqHeaderDto);
		
		if(ServiceConstants.POST_ADOPT.equals(cdStage)) {
			/*CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(reopenStageDto.getIdCase());
			capsCase.setDtCaseClosed(null);
			capsCaseDao.updateCapsCase(capsCase);*/
			
			//skipping PAD - not in scope
		}
		if(ServiceConstants.SUBCARE.equals(cdStage)) {
			
			idPrimaryChild = stagePersonLinkDao.getPersonIdByRole(idStage, CodesConstant.CROLES_PC);
			
			//clss30d
			List<StageDto> stageDtolist = stageDao.getStageEntityByCaseId(idCase, StageDao.ORDERBYCLAUSE.BYSITUATIONSTARTDT);
			
			if(!CollectionUtils.isEmpty(stageDtolist)) {
				//find SUB stage for Primary Child
				stageDtolist.stream().forEach(stageDto -> {
					if (ServiceConstants.SUBCARE.equals(stageDto.getCdStage())
							&& ObjectUtils.isEmpty(stageDto.getDtStageClose())
							&& !stageDto.getIdStage().equals(idStage)) {
						Long idPrimaryChildOpenStage = stagePersonLinkDao.getPersonIdByRole(stageDto.getIdStage(), CodesConstant.CROLES_PC);
						if(!ObjectUtils.isEmpty(idPrimaryChildOpenStage) && idPrimaryChild.equals(idPrimaryChildOpenStage)) {
							throw new ServiceException(ServiceConstants.MSG_ROP_SUB_OPEN);
						}
					}
				});
			}
			
			List<StagePersDto> stagePersDtoListAdo = stageDao.getStagesByAttributes(idPrimaryChild,
					CodesConstant.CROLES_PC, idCase, ServiceConstants.ADOPTION);
			
			long idAdoptionStage = 0;
			long idPrimWorker = 0;
			long idSubWorker = 0;
			long idSubLink = 0;
			boolean indDeleteHP = false;
			
			if(!CollectionUtils.isEmpty(stagePersDtoListAdo)) {
				idAdoptionStage = stagePersDtoListAdo.get(0).getIdStage();
				
				//ccmnb9d
				List<StagePersonLinkDto> stagePersonLinkDtoList = stagePersonLinkDao
						.getStagePersonLinkByIdStage(idAdoptionStage);
				
				if(!CollectionUtils.isEmpty(stagePersonLinkDtoList)) {
					Optional<StagePersonLinkDto> op = stagePersonLinkDtoList.stream().filter(
							x-> ServiceConstants.STAFF.equals(x.getCdStagePersType())
							&& ServiceConstants.PRIMARY_WORKER.equals(x.getCdStagePersRole())).findFirst();
					if(op.isPresent()) {
						idPrimWorker = op.get().getIdPerson();
					}
				}
				stagePersonLinkDtoList = stagePersonLinkDao.getStagePersonLinkByIdStage(idStage);
				
				if(!CollectionUtils.isEmpty(stagePersonLinkDtoList)) {
					Optional<StagePersonLinkDto> op = stagePersonLinkDtoList.stream().filter(
							x-> ServiceConstants.STAFF.equals(x.getCdStagePersType())).findFirst();
					if(op.isPresent()) {
						idSubWorker = op.get().getIdPerson();
						idSubLink = op.get().getIdStagePersonLink();
					}
				}
				StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
				serviceReqHeaderDto = new ServiceReqHeaderDto();
				
				if(idSubWorker == idPrimWorker) {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					stagePersonLinkDto.setIdStagePersonLink(idSubLink);
				}
				else {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					indDeleteHP = true;
				}
				stagePersonLinkDto.setCdStagePersRole(ServiceConstants.PRIMARY_WORKER);
				stagePersonLinkDto.setCdStagePersType(ServiceConstants.STAFF);
				stagePersonLinkDto.setIndStagePersEmpNew(ServiceConstants.IND_STGPER_EMPNEW);
				stagePersonLinkDto.setIdStage(idStage);
				stagePersonLinkDto.setIdPerson(idPrimWorker);
				stagePersonLinkDto.setDtStagePersLink(DateUtils.getDateWithoutTime(new Date()));
				
				//ccmnd3d
				stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
				
				if(indDeleteHP) {
					Optional<StagePersonLinkDto> op = stagePersonLinkDtoList.stream().filter(
							x-> ServiceConstants.HIST_PRIM_WORKER.equals(x.getCdStagePersRole())).findFirst();
					if(op.isPresent()) {
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
						stagePersonLinkDto.setIdStagePersonLink(op.get().getIdStagePersonLink());
						stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
					}
				}
			}//end ADO stage
			PlacementActPlannedOutDto placementRecord = placementActPlannedOutDto.getPlacementRecord(idPrimaryChild);
			
			if (!(!ObjectUtils.isEmpty(placementRecord)
					&& (!ObjectUtils.isEmpty(placementRecord.getDtPlcmtEnd())
							&& ServiceConstants.MAX_DATE.compareTo(placementRecord.getDtPlcmtEnd()) == 0)
					&& (ServiceConstants.PLACEMENT_RSNS.contains(placementRecord.getCdPlcmtRemovalRsn())))) {
				throw new ServiceException(ServiceConstants.MSG_ROP_NO_DISRUPT);
			}
			List<EventStagePersonDto> svcAuthList = stageDao.getEventStagePersonListByAttributes(
					idAdoptionStage, ServiceConstants.ADO_SVC_AUTH_TASK, ServiceConstants.SVC_AUTH_EVENT_TYPE);
			
			if (!CollectionUtils.isEmpty(svcAuthList)) {
				svcAuthList.stream().forEach(e -> {

					if (ServiceConstants.STATUS_APPROVED.equals(e.getCdEventStatus())
							&& ServiceConstants.SERVICE_AUTH_TYPE.equals(e.getCdEventType())) {
						
						SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
						svcAuthEventLinkInDto.setIdSvcAuthEvent(e.getIdEvent());
						//cses24d
						SvcAuthEventLinkOutDto svcAuthEventLink = svcAuthEventLinkDao
								.getAuthEventLink(svcAuthEventLinkInDto).get(0);
						//CLSC73 servExist = false
						if (svcAuthEventLinkDao.checkSvcAuthExists(svcAuthEventLink.getIdSvcAuth()) == 0) {
							ServiceAuthorizationDto servAuth = serviceAuthorizationDao.getServiceAuthorizationById(svcAuthEventLink.getIdSvcAuth());
							
							if (ServiceConstants.YES.equals(servAuth.getIndSvcAuthComplete())) {
								//clss24d
								List<SVCAuthDetailDto> svcAuthDetails = serviceAuthorizationDao.getSVCAuthDetailDtoById(servAuth.getIdSvcAuth());
								
								if (!CollectionUtils.isEmpty(svcAuthDetails)) {
									
									svcAuthDetails.stream().forEach(s -> {
										if (DateUtils.isAfter(s.getDtSvcAuthDtlTerm(),DateUtils.getDateWithoutTime(new Date()))) {
											// CCMN46D 
											Event event = new Event();
											event.setCdEventType(ServiceConstants.SERVICE_AUTH_TYPE);
											event.setDtEventOccurred(e.getDtEventOccurred());
											event.setStage(stageDao.getStageEntityById(idStage));
											event.setTxtEventDescr(e.getEventDescr());
											event.setCdEventStatus(e.getCdEventStatus());
											event.setCdTask(e.getCdTask());
											event.setDtLastUpdate(new Date());
											event.setDtEventCreated(new Date());
											Long idEvent = eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_ADD);
											
											//caud34d
											svcAuthEventLinkSave(idEvent, s.getIdSvcAuth());
											
											List<PersonDto> personDtoList = eventDao.getPersonFromEventPlanLinkByIdEvent(idEvent);
											
											if(!CollectionUtils.isEmpty(personDtoList)) {
												personDtoList.stream().forEach(p->{
													EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
													eventPersonLinkDto.setIdEvent(idEvent);
													eventPersonLinkDto.setIdPerson(p.getIdPerson());
													//ccmn68d
													eventPersonLinkDao.getEventPersonLinkAUD(eventPersonLinkDto);
												});
											}
											
										}
									});
								}
							}
							
						}
					}
				});
			}
			TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
			TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
			todoCommonFunctionInputDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			
			todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.TODO_CODE);
			todoCommonFunctionDto.setSysIdTodoCfPersCrea(idPrimWorker);
			todoCommonFunctionDto.setSysIdTodoCfStage(idStage);
			todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(DateUtils.getDateWithoutTime(new Date()));
			
			
			todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
			// CSUB40U
			todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
			
			todoCommonFunctionInputDto.getTodoCommonFunctionDto().setSysCdTodoCf(ServiceConstants.SUB_TODO_2);
			todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
			
			todoCommonFunctionInputDto.getTodoCommonFunctionDto().setSysCdTodoCf(ServiceConstants.SUB_TODO_3);
			todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
			
			
		}//end SUB
		
		if(ServiceConstants.CSTAGES_FSU.equals(cdStage) || ServiceConstants.CSTAGES_FRE.equals(cdStage)) {
			long idSubLink = 0;
			
			List<StagePersonLinkDto> stagePersonLinkDtoList = stagePersonLinkDao.getStagePersonLinkByIdStage(idStage);
			
			if(!CollectionUtils.isEmpty(stagePersonLinkDtoList)) {
				Optional<StagePersonLinkDto> op = stagePersonLinkDtoList.stream().filter(
						x-> ServiceConstants.STAFF.equals(x.getCdStagePersType())).findFirst();
				if(op.isPresent()) {
					idSubLink = op.get().getIdStagePersonLink();
				}
			}
			
			StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
			serviceReqHeaderDto = new ServiceReqHeaderDto();

			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			stagePersonLinkDto.setIdStagePersonLink(idSubLink);
			stagePersonLinkDto.setCdStagePersRole(ServiceConstants.PRIMARY_WORKER);
			stagePersonLinkDto.setCdStagePersType(ServiceConstants.STAFF);
			stagePersonLinkDto.setIndStagePersEmpNew(ServiceConstants.IND_STGPER_EMPNEW);
			stagePersonLinkDto.setIdStage(idStage);
			stagePersonLinkDto.setIdPerson(idPerson);
			stagePersonLinkDto.setDtStagePersLink(DateUtils.getDateWithoutTime(new Date()));

			//ccmnd3d
			stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDto);
		} // end FSU FRE
		
		EventStagePersonLinkInsUpdInDto eventStagePersonLinkInDto = new EventStagePersonLinkInsUpdInDto();
		eventStagePersonLinkInDto.setIdStage(idStage);
		eventStagePersonLinkInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		if (ServiceConstants.SUBCARE.equals(cdStage)) {
			eventStagePersonLinkInDto.setCdTask(ServiceConstants.SUB_CLOSURE);
		} else if (ServiceConstants.CSTAGES_FSU.equals(cdStage)) {
			eventStagePersonLinkInDto.setCdTask(ServiceConstants.FSU_CLOSURE);
		} else if (ServiceConstants.CSTAGES_FRE.equals(cdStage)) {
			eventStagePersonLinkInDto.setCdTask(ServiceConstants.FRE_CLOSURE);
		} else {
			eventStagePersonLinkInDto.setCdTask(ServiceConstants.PAD_CLOSURE);
		}

		List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkList = eventStagePersonLinkInsUpdDao
				.getEventAndStatusDtls(eventStagePersonLinkInDto);
		
		if(!CollectionUtils.isEmpty(eventStagePersonLinkList)) {
			eventStagePersonLinkList.stream().forEach(e->{
				eventDao.deleteEventById(e.getIdEvent());
			});
		}
	}

	/**
	 * Method Name: createOrUpdatePersonEligibility
	 * Method Description: This method is used to Create or Update the Person Eligibility for INV Stage
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public String createOrUpdatePersonEligibility(Long idStage) {

		String rowQty = null;
		String indCPSInvsDtlEaConcl = null;
		List<StagePrincipalDto> stagePrincipalDtoList = null;
		List<PersonEligibilityDto> personEligibilityDtoList = null;
		boolean isEAFound;
		boolean isEATwelveMnth = false;
		Date date = new Date();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

		if (!TypeConvUtil.isNullOrEmpty(idStage)) {
			indCPSInvsDtlEaConcl = stageDao.getIndCPSInvsDtlEaConclByStageId(idStage);
		}

		if (ServiceConstants.Y.equalsIgnoreCase(indCPSInvsDtlEaConcl)) {

			if (!TypeConvUtil.isNullOrEmpty(idStage)) {
				// CLSC18D
				stagePrincipalDtoList = stagePersonLinkDao.getStagePrincipalByIdStageType(idStage,
						ServiceConstants.PRINCIPAL);
			}
			if (!TypeConvUtil.isNullOrEmpty(stagePrincipalDtoList)) {
				rowQty = Integer.toString(stagePrincipalDtoList.size());
				for (StagePrincipalDto stagePrincipalDto : stagePrincipalDtoList) {
					if (!TypeConvUtil.isNullOrEmpty(stagePrincipalDto.getIdPerson())) {
						// CSECA1D
						personEligibilityDtoList = personEligibilityDao
								.getPersonEligibilityByIdPerson(stagePrincipalDto.getIdPerson());
					}
					isEAFound = !ObjectUtils.isEmpty(personEligibilityDtoList);
					if (isEAFound) {
						for (PersonEligibilityDto personEligibilityDto : personEligibilityDtoList) {
							if (!TypeConvUtil.isNullOrEmpty(personEligibilityDto.getDtPersEligEnd())
									&& !TypeConvUtil
									.isNullOrEmpty(personEligibilityDto.getDtPersEligEaDeny())) {
								Calendar personEligEaDenyDate = Calendar.getInstance();
								personEligEaDenyDate.setTime(personEligibilityDto.getDtPersEligEaDeny());
								if (personEligibilityDto.getDtPersEligEnd().after(date)
										&& personEligEaDenyDate
										.get(Calendar.YEAR) == ServiceConstants.NO_END_DATE) {
									// CAUDC9D
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.EA_UPDATE);
									if (personEligibilityDto.getCdPersEligPrgStart()
											.equals(ServiceConstants.PERSELIG_PRG_START_S)
											&& personEligibilityDto.getCdPersEligPrgOpen()
											.equals(ServiceConstants.PERSELIG_PRG_OPEN_S)) {
										personEligibilityDto
												.setCdPersEligPrgOpen(ServiceConstants.PERSELIG_PRG_OPEN_B);
									} else {
										personEligibilityDto
												.setCdPersEligPrgOpen(ServiceConstants.PERSELIG_PRG_OPEN_C);
									}
									personEligibilityDao.personEligibilityAUD(personEligibilityDto,
											serviceReqHeaderDto);
								} else {
									if (!TypeConvUtil
											.isNullOrEmpty(personEligibilityDto.getDtPersEligStart())) {
										isEATwelveMnth = false;
										// CSEC85D
										Calendar personEligEndDate = Calendar.getInstance();
										personEligEndDate
												.setTime(personEligibilityDto.getDtPersEligStart());
										if (personEligEndDate.get(Calendar.YEAR)
												% 4 == ServiceConstants.Zero) {
											personEligEndDate.add(Calendar.DATE,
													ServiceConstants.LEAP_YEAR_DAYS);
										} else {
											personEligEndDate.add(Calendar.DATE,
													ServiceConstants.NORMAL_YEAR_DAYS);
										}
										if (personEligEndDate.getTime().before(date)) {
											isEATwelveMnth = true;
											break;
										}
									}
								}
							}
						}
					}
					if (!isEAFound || isEATwelveMnth) {
						if (!TypeConvUtil.isNullOrEmpty(idStage)) {
							// CSECA3D
							Date dtSvnAuthDtlBegin = personEligibilityDao.getSvcAuthDtlBeginByIdStage(idStage);
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.EA_ADD);
							PersonEligibilityDto newPersonEligibilityDto = new PersonEligibilityDto();
							if (!TypeConvUtil.isNullOrEmpty(stagePrincipalDto.getIdPerson())) {
								newPersonEligibilityDto
										.setIdPersEligPerson(stagePrincipalDto.getIdPerson());
							}
							if (!TypeConvUtil.isNullOrEmpty(dtSvnAuthDtlBegin)) {
								newPersonEligibilityDto.setDtPersEligStart(dtSvnAuthDtlBegin);
							}
							personEligibilityDao.personEligibilityAUD(newPersonEligibilityDto,
									serviceReqHeaderDto);
						}
					}
				}
			} else {
				rowQty = ServiceConstants.STR_ZERO_VAL;
			}
		}

		return rowQty;
	}
	
}
