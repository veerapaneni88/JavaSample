package us.tx.state.dfps.service.legal.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dao.FetchStageDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventRowDto;
import us.tx.state.dfps.service.admin.dto.FetchStagediDto;
import us.tx.state.dfps.service.admin.dto.FetchStagedoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.response.PersonListRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legal.dao.FetchLegalActionDao;
import us.tx.state.dfps.service.legal.dao.LegalActionPersonFetchDao;
import us.tx.state.dfps.service.legal.dao.PersonDetailsDao;
import us.tx.state.dfps.service.legal.dao.ToDoEventDao;
import us.tx.state.dfps.service.legal.dto.CaseDbDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionInDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionOutDto;
import us.tx.state.dfps.service.legal.dto.FetchToDoOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonArrDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.legal.dto.PersonInfoDbDto;
import us.tx.state.dfps.service.legal.dto.RetrvInDto;
import us.tx.state.dfps.service.legal.dto.RetrvOutDto;
import us.tx.state.dfps.service.legal.service.LegalActionRetrievalService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Nov 6, 2017- 4:23:20 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class LegalActionRetrievalServiceImpl implements LegalActionRetrievalService {

	@Autowired
	PersonListService personListService;

	@Autowired
	FetchStageDao fetchStageDao;

	@Autowired
	AdminWorkerDao adminWorkerDao;

	@Autowired
	PersonDetailsDao personDetailsDao;

	@Autowired
	ToDoEventDao toDoEventDao;

	@Autowired
	EventFetDao eventFetDao;

	@Autowired
	FetchLegalActionDao fetchLegalActionDao;

	@Autowired
	LegalActionPersonFetchDao legalActionPersonFetchDao;

	@Autowired
	LookupDao lookupDao;

	private static final Logger log = Logger.getLogger(LegalActionRetrievalServiceImpl.class);

	/**
	 * 
	 * Method Name: legalActionOutcomeRtrv Method Description:This is the
	 * retrieval service for the Legal Action/Outcome window.
	 * 
	 * @param retrvInDto
	 * @return RetrvOutDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public RetrvOutDto legalActionOutcomeRtrv(RetrvInDto retrvInDto) {
		log.debug("Entering method legalActionOutcomeRtrv in LegalActionRetrievalService");
		RetrvOutDto retrvOutDto = new RetrvOutDto();
		LegalActionRtrvInDto legalActionRtrvInDto = new LegalActionRtrvInDto();
		LegalActionRtrvOutDto legalActionRtrvOutDto = new LegalActionRtrvOutDto();
		retrvOutDto.setLegalActionRtrvOutDto(legalActionRtrvOutDto);
		retrvOutDto.setLegalActionRtrvInDto(legalActionRtrvInDto);
		retrvOutDto.setSysDtGenericSysdate(new Date());
		FetchStagedoDto fetchStagedoDto = fetchStage(retrvInDto, retrvOutDto);
		if (retrvInDto.getIdEvent() == ServiceConstants.Zero_Value) {
			if ((ServiceConstants.CSTAGES_INV.equals(retrvInDto.getCdStage())
					&& ServiceConstants.CPGRMS_CPS.equals(fetchStagedoDto.getSzCdStageProgram()))
					|| ServiceConstants.CSTAGES_FSU.equals(retrvInDto.getCdStage())
					|| ServiceConstants.CSTAGES_FRE.equals(retrvInDto.getCdStage())
					|| ServiceConstants.CSTAGES_FPR.equals(retrvInDto.getCdStage())) {
			} else if (ServiceConstants.CSTAGES_SUB.equals(retrvInDto.getCdStage())
					|| ServiceConstants.CSTAGES_ADO.equals(retrvInDto.getCdStage())
					|| ServiceConstants.CSTAGES_PAD.equals(retrvInDto.getCdStage())) {
				fetchPrimaryChild(retrvInDto, retrvOutDto);
				fetchPerson(retrvInDto, retrvOutDto);
				fetchLegalActionForChild(retrvInDto, retrvOutDto);
			}
			else if (ServiceConstants.CPGRMS_APS.equals(fetchStagedoDto.getSzCdStageProgram())) {
				fetchPrimaryPerson(retrvInDto, retrvOutDto);
			}
		} else {
			fetchEventPerson(retrvInDto, retrvOutDto);
			if (ServiceConstants.CEVTSTAT_NEW.equals(retrvOutDto.getLegalActionRtrvInDto().getCdEventStatus())) {
				fetchLegalActionInfo(retrvInDto, retrvOutDto);
				fetchPerson(retrvInDto, retrvOutDto);
				fetchScheduledCourtDate(retrvInDto, retrvOutDto);
				if ((ServiceConstants.CSTAGES_INV.equals(retrvInDto.getCdStage())
						&& ServiceConstants.CPGRMS_CPS.equals(fetchStagedoDto.getSzCdStageProgram()))
						|| ServiceConstants.CSTAGES_FSU.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_FRE.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_FPR.equals(retrvInDto.getCdStage())) {
				} else if (ServiceConstants.CSTAGES_SUB.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_ADO.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_PAD.equals(retrvInDto.getCdStage())) {
					fetchPrimaryChild(retrvInDto, retrvOutDto);
					fetchPerson(retrvInDto, retrvOutDto);
					fetchLegalActionForChild(retrvInDto, retrvOutDto);
				}
			} else if (ServiceConstants.NEW_USING.equals(retrvInDto.getSysIndDamCalled())) {
				fetchLegalActionInfo(retrvInDto, retrvOutDto);
				fetchScheduledCourtDate(retrvInDto, retrvOutDto);
				if ((ServiceConstants.CSTAGES_INV.equals(retrvInDto.getCdStage())
						&& ServiceConstants.CPGRMS_CPS.equals(fetchStagedoDto.getSzCdStageProgram()))
						|| ServiceConstants.CSTAGES_FSU.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_FRE.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_FPR.equals(retrvInDto.getCdStage())) {
				} else if (ServiceConstants.CSTAGES_SUB.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_ADO.equals(retrvInDto.getCdStage())
						|| ServiceConstants.CSTAGES_PAD.equals(retrvInDto.getCdStage())) {
					fetchPrimaryChild(retrvInDto, retrvOutDto);
					fetchPerson(retrvInDto, retrvOutDto);
					fetchLegalActionForChild(retrvInDto, retrvOutDto);
				}
			} else {
				fetchLegalActionInfo(retrvInDto, retrvOutDto);
				fetchPerson(retrvInDto, retrvOutDto);
				fetchScheduledCourtDate(retrvInDto, retrvOutDto);
			}
		}
		log.debug("Exiting method legalActionOutcomeRtrv in LegalActionRetrievalService");
		// ADS -Use case 2.6.2.4 - Outcome subtype and time
		fetchScheduledCourtDate(retrvInDto, retrvOutDto);

		return retrvOutDto;
	}

	/**
	 * 
	 * Method Name: fetchStageDBAndPrincipals Method Description:This is the
	 * retrieval for stages and related principals for the case.
	 * 
	 * @param idCase
	 * @return List<StageDBDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<StageDBDto> fetchStageDBAndPrincipals(Long idCase){
		log.debug("Entering method fetchStageDBAndPrincipals in LegalActionRetrievalService");
		List<StageDBDto> stageDBListFromDB = new ArrayList<StageDBDto>();
		List<StageDBDto> stageDBListWithPrinc = new ArrayList<StageDBDto>();
		StageDBDto stageDB = new StageDBDto();
		CaseDbDto caseDB = getCaseSummary(idCase);
		if (!TypeConvUtil.isNullOrEmpty(caseDB)) {
			stageDBListFromDB = caseDB.getStageList();
		}
		for (StageDBDto stageDbDto : stageDBListFromDB) {
			if ((ServiceConstants.CSTAGES_FRE.equals(stageDbDto.getCdStage())
					|| ServiceConstants.CSTAGES_FSU.equals(stageDbDto.getCdStage())
					|| ServiceConstants.CSTAGES_SUB.equals(stageDbDto.getCdStage()))
					&& DateUtils.isNull(stageDB.getDtStageCloses())) {
				ServiceInputDto serviceInputDto = new ServiceInputDto();
				serviceInputDto.setCreqFuncCd(ServiceConstants.CD_MODE_PRINCIPAL);
				serviceInputDto.setUsPageNbr(ServiceConstants.ROWNUM1);
				serviceInputDto.setUlPageSizeNbr(ServiceConstants.PAGE_SIZE_NBR);
				PersonListReq personListReq = new PersonListReq();
				personListReq.setIdCase(stageDbDto.getCaseId());
				personListReq.setIdPerson(stageDbDto.getPersonId());
				personListReq.setIdStage(stageDbDto.getStageId());
				personListReq.setSysCdWinMode(ServiceConstants.CD_MODE_PRINCIPAL);
				personListReq.setStageProgram(ServiceConstants.CPGRMS_CPS);

				PersonListRes personList = personListService.getPersonList(personListReq);

				stageDB.setFilterPersonList(filterPersonList(personList, stageDB.getCdStage()));

				stageDBListWithPrinc.add(stageDB);
			}
		}
		log.debug("Exiting method fetchStageDBAndPrincipals in LegalActionRetrievalService");
		return stageDBListWithPrinc;
	}

	/**
	 * 
	 * Method Name: getCaseSummary Method Description:This is the retrieval of
	 * Case with all stages info.
	 * 
	 * @param idCase
	 * @return CaseDbDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CaseDbDto getCaseSummary(Long idCase) {
		log.debug("Entering method fetchStageDBAndPrincipals in LegalActionRetrievalService");
		CaseDbDto caseDbDto = new CaseDbDto();
		caseDbDto.setCaseId(idCase);
		caseDbDto = fetchLegalActionDao.getCase(caseDbDto);
		List<StageDBDto> stageList = fetchLegalActionDao.getStageList(caseDbDto);
		caseDbDto.setStageList(stageList);
		for (StageDBDto stageDBDto : stageList) {
			PersonInfoDbDto personInfoDbDto = fetchLegalActionDao.getPerson(stageDBDto);
			stageDBDto.setPersonName(personInfoDbDto.getPersonName());
			stageDBDto.setPersonPhone(personInfoDbDto.getPersonPhone());
		}
		log.debug("Exiting method fetchStageDBAndPrincipals in LegalActionRetrievalService");
		return caseDbDto;
	}

	/**
	 * 
	 * Method Name: filterPersonList Method Description:This will filter the
	 * Person list for the Principal for FRE/FSU stage and Primary child for The
	 * SUB stage.
	 * 
	 * @param personList
	 * @param cdStage
	 * @return List<PersonListDto> @
	 */
	private List<PersonListDto> filterPersonList(PersonListRes personList, String cdStage) {
		List<PersonListDto> personListDtoList = new ArrayList<PersonListDto>();
		for (PersonListDto personListDto : personList.getRetrievePersonList()) {
			if (personListDto.getStagePersType().equals(ServiceConstants.CPRSNTYP_PRN)) {
				if (ServiceConstants.CSTAGES_SUB.equals(cdStage)) {
					if (ServiceConstants.CROLES_PC.equals(personListDto.getStagePersRole())) {
						personListDtoList.add(personListDto);
						break;
					}
				} else {
					personListDtoList.add(personListDto);
				}
			}

		}
		return personListDtoList;
	}

	/**
	 * 
	 * Method Name: fetchStage Method Description:Retrieve Stage table info for
	 * the IdStage. DAM: CINT21D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return FetchStagedoDto @
	 */
	private FetchStagedoDto fetchStage(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		FetchStagediDto fetchStagediDto = new FetchStagediDto();
		fetchStagediDto.setUlIdStage(retrvInDto.getIdStage());
		List<FetchStagedoDto> fetchStagedoDtoList = fetchStageDao.getStageDetails(fetchStagediDto);
		FetchStagedoDto fetchStagedoDto = fetchStagedoDtoList.get(ServiceConstants.Zero);
		if (!TypeConvUtil.isNullOrEmpty(fetchStagedoDto.getSzCdStageProgram())) {
			retrvOutDto.setCdStageProgram(fetchStagedoDto.getSzCdStageProgram());
		}
		return fetchStagedoDto;
	}

	/**
	 * 
	 * Method Name: fetchPrimaryChild Method Description:Retrieves Primary Child
	 * Information for the stage DAM: CINV51D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long @
	 */
	private Long fetchPrimaryChild(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
		adminWorkerInpDto.setServiceInputDto((ServiceInputDto) retrvInDto.getServiceInputDto());
		adminWorkerInpDto.setIdStage(retrvInDto.getIdStage());
		adminWorkerInpDto.setCdStagePersRole(ServiceConstants.CROLES_PC);
		AdminWorkerOutpDto adminWorkerOutpDto = adminWorkerDao.getPersonInRole(adminWorkerInpDto);
		retrvOutDto.setIdPerson(adminWorkerOutpDto.getIdTodoPersAssigned());
		retrvOutDto.getLegalActionRtrvOutDto().setIdPerson(adminWorkerOutpDto.getIdTodoPersAssigned());
		return retrvOutDto.getIdPerson();
	}

	/**
	 * 
	 * Method Name: fetchPerson Method Description:Fetch Person Full Name DAM:
	 * CCMN44D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long @
	 */
	private Long fetchPerson(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		PersonDetailsdiDto personDetailsdiDto = new PersonDetailsdiDto();
		personDetailsdiDto.setServiceInputDto(retrvInDto.getServiceInputDto());
		personDetailsdiDto.setIdPerson(retrvOutDto.getLegalActionRtrvOutDto().getIdPerson());
		PersonDetailsdoDto personDetailsdoDto = personDetailsDao.getPersonInformation(personDetailsdiDto);
		if (!TypeConvUtil.isNullOrEmpty(personDetailsdoDto.getNmPersonFull())) {
			retrvOutDto.setNmPersonFull(personDetailsdoDto.getNmPersonFull());
		}
		return retrvOutDto.getIdPerson();
	}

	/**
	 * 
	 * Method Name: fetchLegalActionForChild Method Description:Retrieve all
	 * JPC/TYC related Legal Actions for child.This will set JPC and TYC
	 * indicators, returning LegalActOutcomeDt for JPC/TYC Start Dates for Legal
	 * Action of type Special Order. DAM: CLSCG2D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long @
	 */
	private Long fetchLegalActionForChild(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		LegalActionPersonRtrvInDto legalActionPersonRtrvInDto = new LegalActionPersonRtrvInDto();
		legalActionPersonRtrvInDto.setIdPerson(retrvOutDto.getIdPerson());
		retrvOutDto.getLegalActionRtrvOutDto().setIndJpcLegActOpen(ServiceConstants.N);
		retrvOutDto.getLegalActionRtrvOutDto().setIndTycLegActOpen(ServiceConstants.N);
		LegalActionPersonRtrvOutDto legalActionPersonRtrvOutDto = legalActionPersonFetchDao
				.fetchLegalActionPerson(legalActionPersonRtrvInDto);
		LegalActionPersonArrDto legalActionPersonArrDto = legalActionPersonRtrvOutDto.getLegalActionPersonArrDto();
		for (LegalActionPersonDto legalActionPersonDto : legalActionPersonArrDto.getLegalActionPersonDtoList()) {

			if (ServiceConstants.LEG_ACT_TYP_JPC_STARTS_270.equals(legalActionPersonDto.getCdLegalActActnSubtype())) {
				retrvOutDto.getLegalActionRtrvOutDto().setIndJpcLegActOpen(ServiceConstants.Y);
				retrvOutDto.getLegalActionRtrvOutDto()
						.setDtJpcLegalActOutcomeDate(legalActionPersonDto.getDtLegalActOutcomeDate());
				break;
			} else if (ServiceConstants.LEG_ACT_TYP_JPC_ENDS_280
					.equals(legalActionPersonDto.getCdLegalActActnSubtype())) {
				retrvOutDto.getLegalActionRtrvOutDto().setIndJpcLegActOpen(ServiceConstants.N);
				retrvOutDto.getLegalActionRtrvOutDto()
						.setDtJpcLegalActOutcomeDate(legalActionPersonDto.getDtLegalActOutcomeDate());
				break;
			}
		}
		for (LegalActionPersonDto legalActionPersonDto : legalActionPersonArrDto.getLegalActionPersonDtoList()) {
			if (ServiceConstants.LEG_ACT_TYP_TYC_STARTS_290.equals(legalActionPersonDto.getCdLegalActActnSubtype())) {
				retrvOutDto.getLegalActionRtrvOutDto().setIndTycLegActOpen(ServiceConstants.Y);
				retrvOutDto.getLegalActionRtrvOutDto()
						.setDtTycLegalActOutcomeDate(legalActionPersonDto.getDtLegalActOutcomeDate());
				break;
			} else if (ServiceConstants.LEG_ACT_TYP_TYC_ENDS_300
					.equals(legalActionPersonDto.getCdLegalActActnSubtype())) {
				retrvOutDto.getLegalActionRtrvOutDto().setIndTycLegActOpen(ServiceConstants.N);
				retrvOutDto.getLegalActionRtrvOutDto()
						.setDtTycLegalActOutcomeDate(legalActionPersonDto.getDtLegalActOutcomeDate());
				break;
			}
		}
		return retrvOutDto.getIdPerson();
	}

	/**
	 * 
	 * Method Name: fetchEventPerson Method Description:Fetch Event Table Info
	 * DAM: CCMN45D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long
	 */
	private Long fetchEventPerson(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto){
		FetchEventDto fetchEventDto = new FetchEventDto();
		FetchEventRowDto fetchEventRowDto = new FetchEventRowDto();
		fetchEventDto.setIdEvent(retrvInDto.getIdEvent());
		fetchEventRowDto = eventFetDao.fetchEventDetails(fetchEventDto).getFetchEventRowDto();
		retrvOutDto.getLegalActionRtrvInDto().setCdEventType(fetchEventRowDto.getCdEventType());
		retrvOutDto.getLegalActionRtrvInDto().setTxtEventDescr(fetchEventRowDto.getTxtEventDescr());
		retrvOutDto.getLegalActionRtrvInDto().setCdTask(fetchEventRowDto.getCdTask());
		retrvOutDto.getLegalActionRtrvInDto().setCdEventStatus(fetchEventRowDto.getCdEventStatus());
		retrvOutDto.getLegalActionRtrvInDto().setIdEvent(fetchEventRowDto.getIdEvent());
		retrvOutDto.getLegalActionRtrvInDto().setIdStage(fetchEventRowDto.getIdStage());
		retrvOutDto.getLegalActionRtrvInDto().setIdEventPerson(fetchEventRowDto.getIdPerson());
		retrvOutDto.getLegalActionRtrvInDto().setDtEventOccurred(fetchEventRowDto.getDtEventOccurred());
		retrvOutDto.getLegalActionRtrvInDto()
					.setTsLastUpdate(fetchEventRowDto.getDtLastUpdate());
		return retrvOutDto.getIdPerson();
	}

	/**
	 * 
	 * Method Name: fetchLegalActionInfo Method Description:Fetch the Legal
	 * Action Info for the event.
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long @
	 */
	private Long fetchLegalActionInfo(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		FetchLegalActionInDto fetchLegalActionInDto = new FetchLegalActionInDto();
		fetchLegalActionInDto.setIdLegalActEvent(retrvInDto.getIdEvent());
		FetchLegalActionOutDto fetchLegalActionOutDto = fetchLegalActionDao
				.fetchLegalActionForEvent(fetchLegalActionInDto);
		if (!TypeConvUtil.isNullOrEmpty(fetchLegalActionOutDto)) {
			retrvOutDto.getLegalActionRtrvOutDto().setIdLegalActEvent(fetchLegalActionOutDto.getIdLegalActEvent());
			retrvOutDto.getLegalActionRtrvOutDto().setTsLastUpdate(fetchLegalActionOutDto.getTsLastUpdate());
			retrvOutDto.getLegalActionRtrvOutDto().setIdPerson(fetchLegalActionOutDto.getIdPerson());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setDtLegalActDateFiled(fetchLegalActionOutDto.getDtLegalActDateFiled());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setDtLegalActOutcomeDt(fetchLegalActionOutDto.getDtLegalActOutcomeDate());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setIndLegalActDocsNCase(fetchLegalActionOutDto.getIndLegalActDocsNCase());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setIndLegalActActionTkn(fetchLegalActionOutDto.getIndLegalActActionTkn());
			retrvOutDto.getLegalActionRtrvOutDto().setCdLegalActAction(fetchLegalActionOutDto.getCdLegalActAction());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setCdLegalActActnSubtype(fetchLegalActionOutDto.getCdLegalActActnSubtype());
			retrvOutDto.getLegalActionRtrvOutDto().setCdLegalActOutcome(fetchLegalActionOutDto.getCdLegalActOutcome());
			retrvOutDto.getLegalActionRtrvOutDto().setCdQrtpCourtStatus(fetchLegalActionOutDto.getCdQRTPCourtStatus());
			retrvOutDto.getLegalActionRtrvOutDto()
					.setTxtLegalActComment(fetchLegalActionOutDto.getTxtLegalActComment());
			retrvOutDto.getLegalActionRtrvOutDto().setIndFDTCGraduated(fetchLegalActionOutDto.getIndFDTCGraduated());
			retrvOutDto.getLegalActionRtrvOutDto().setCdFDTCEndReason(fetchLegalActionOutDto.getCdFDTCEndReason());

			retrvOutDto.getLegalActionRtrvOutDto()
					.setCdLegalActOutcomeSub(fetchLegalActionOutDto.getCdLegalActOutSub());

			retrvOutDto.getLegalActionRtrvOutDto().setScheduledTime(fetchLegalActionOutDto.getScheduledTime());

		}
		return retrvOutDto.getIdPerson();
	}

	/**
	 * 
	 * Method Name: fetchScheduledCourtDate Method Description:Fetches the Next
	 * Scheduled Court Date date for the Todos DAM: CCMN42D
	 * 
	 * @param retrvInDto
	 * @param retrvOutDto
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	private Long fetchScheduledCourtDate(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		List<FetchToDoOutDto> searchArrayToDoDtoList = toDoEventDao.fetchToDoListForEvent(retrvInDto.getIdEvent());
		if (!CollectionUtils.isEmpty(searchArrayToDoDtoList)) {
			for (FetchToDoOutDto searchTodoDto : searchArrayToDoDtoList) {
				if (searchTodoDto.getIdTodo() != ServiceConstants.ZERO_VAL && !ObjectUtils.isEmpty(searchTodoDto.getScrTaskDue())) {
					Date extractDate = DateUtils.stringDateAndTimestamp(searchTodoDto.getScrTaskDue());
					retrvOutDto.getLegalActionRtrvOutDto().setDtScheduledCourtDate(extractDate);
					// ADS -Use case 2.6.2.4 - Outcome subtype and time							
					retrvOutDto.getLegalActionRtrvOutDto()
							.setScheduledTime((DateUtils.getTime(extractDate)));
					break;
				}
			}
		}
		return retrvOutDto.getIdPerson();
	}

	private Long fetchPrimaryPerson(RetrvInDto retrvInDto, RetrvOutDto retrvOutDto) {
		AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
		adminWorkerInpDto.setServiceInputDto((ServiceInputDto) retrvInDto.getServiceInputDto());
		adminWorkerInpDto.setIdStage(retrvInDto.getIdStage());
		adminWorkerInpDto.setCdStagePersType(ServiceConstants.PERSON_TYPE_PRN);
		AdminWorkerOutpDto adminWorkerOutpDto = adminWorkerDao.getVictim(adminWorkerInpDto);
		retrvOutDto.setIdPerson(adminWorkerOutpDto.getIdTodoPersAssigned());
		retrvOutDto.getLegalActionRtrvOutDto().setIdPerson(adminWorkerOutpDto.getIdTodoPersAssigned());
		return retrvOutDto.getIdPerson();
	}

}
