package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.web.CodesConstant;
import us.tx.state.dfps.service.admin.dao.AllegationStageDao;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecordsCheckDao;
import us.tx.state.dfps.service.admin.dao.EmergencyAssistDao;
import us.tx.state.dfps.service.admin.dao.EventRiskAssessmentDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.EventStageTypeStatusDao;
import us.tx.state.dfps.service.admin.dao.EventStageTypeTaskDao;
import us.tx.state.dfps.service.admin.dao.LegalActionEventDao;
import us.tx.state.dfps.service.admin.dao.StageCdDao;
import us.tx.state.dfps.service.admin.dao.StageLinkStageDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthDetailNameDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvCnclsnValidationDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclAudiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckOutDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistOutDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentInDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.CpsInvConclValService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.response.CPSInvConclValBeanRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 5:15:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class CpsInvConclValServiceImpl implements CpsInvConclValService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventStageTypeTaskDao eventStageTypeTaskDao;

	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	@Autowired
	StageSituationDao stageSituationDao;

	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	@Autowired
	SvcAuthDetailNameDao svcAuthDetailNameDao;

	@Autowired
	StageCdDao stageCdDao;

	@Autowired
	EmergencyAssistDao emergencyAssistDao;

	@Autowired
	EventRiskAssessmentDao eventRiskAssessmentDao;

	@Autowired
	CpsInvstDetailStageIdDao cpsInvstDetailStageIdDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	AllegationStageDao allegationStageDao;

	@Autowired
	CriminalHistoryRecordsCheckDao criminalHistoryRecordsCheckDao;

	@Autowired
	StageLinkStageDao stageLinkStageDao;

	@Autowired
	LegalActionEventDao legalActionEventDao;

	@Autowired
	EventStageTypeStatusDao eventStageTypeStatusDao;

	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	@Autowired
	EventDao eventDao;

	@Autowired
	PersonDao personDao;
	
	@Autowired
	 PersonListByStageDao personListByStageDao;

  @Autowired LookupService lookupService;

	private static final Logger log = Logger.getLogger(CpsInvConclValServiceImpl.class);

	/**
	 * 
	 * Method Name: callCpsInvConclValService Method Description:This service
	 * performs server side validation for the CPS Investigation Conclusion
	 * window. The edits performed by the service depend on the decode string in
	 * DCD_EDIT_PROCESS. Once all required edits are passed, the service will
	 * set all the to-dos associated with the input ID_EVENT to 'COMPLETE' and
	 * return a list of all the ID_EVENTs associated with the input ID_STAGE.
	 * 
	 * @param CPSInvestigationConclusionReq
	 * @return CPSInvConclValBeanRes @
	 */
	@Override
	public CpsInvConclValoDto cpsInvConclValidationService(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method callCpsInvConclValService in CpsInvConclValServiceImpl");
		CpsInvConclValoDto cpsInvConclValoDto = new CpsInvConclValoDto();
		boolean subsequentSUB = false;
		StageTaskInDto stageEvtStatusDto = new StageTaskInDto();
		List<Long> nbrMessageCode = new ArrayList<Long>();
		List<EventStagePersonLinkInsUpdOutDto> eventsForApproval = new ArrayList<>();
		cpsInvConclValoDto.setUsSysNbrMessageCode(nbrMessageCode);
		stageEvtStatusDto.setReqFuncCd(cpsInvConclValiDto.getReqFuncCd());
		stageEvtStatusDto.setIdStage(cpsInvConclValiDto.getIdStage());
		stageEvtStatusDto.setCdTask(cpsInvConclValiDto.getCdTask());
		/*
		 ** Call CCMN06U -Check Stage Event Status
		 */
		String stageEventStatus = stageEventStatusCommonService.checkStageEventStatus(stageEvtStatusDto);
		if (!StringUtils.isEmpty(stageEventStatus) && ServiceConstants.ARC_SUCCESS.equalsIgnoreCase(stageEventStatus)) {
			/*
			 * Check if all the PRN of below 18 have answered sexual vctimzation question
			 */
		    if (ServiceConstants.CHAR_Y == cpsInvConclValiDto.getDcdEditProcess()
                    .charAt(ServiceConstants.SEXUAL_VCTM_QUEST_EDIT)) {
    		    boolean validationResult = validationForSexualVctmizationQues(cpsInvConclValiDto.getIdStage());
    
    			if(!validationResult) {
    				cpsInvConclValoDto.getUsSysNbrMessageCode()
    				.add(Long.valueOf(ServiceConstants.MSG_COMPLETE_SEX_VIC_QN));
    			}
			
		    }	
			/*
			 ** Call CSVC34D - Fetch Day Care Request pending events for stage
			 */

			String eventStatus = eventDao.getEventStatus(cpsInvConclValiDto.getIdStage(),
					ServiceConstants.DAY_CARE_TYPE, ServiceConstants.STATUS_PENDING);

			if (!ObjectUtils.isEmpty(eventStatus)) {
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_DAY_CARE_REQ_PEND));
			}
			/*
			 ** Call CINV34D - verify only one Oldest Victim exists.
			 */
			List<PersonListDto> personList = personDao.getPersonListByIdStage(cpsInvConclValiDto.getIdStage(),
					ServiceConstants.STAFF_TYPE);

			/*
			 * Check the below conditions for each prinicipal having non blank
			 * first and last name. If person search is required and is not of
			 * type 'R' or 'V', set flag to post warning.
			 */

			if (ServiceConstants.CHAR_Y == cpsInvConclValiDto.getDcdEditProcess()
					.charAt(ServiceConstants.PERS_SEARCH_EDIT)) {
				boolean persSearch = personList.stream().filter(
						person -> !ServiceConstants.PERSON_SEARCH_R.equalsIgnoreCase(person.getIndStagePersSearch())
								&& !ServiceConstants.PERSON_SEARCH_V.equalsIgnoreCase(person.getIndStagePersSearch())
								&& !ServiceConstants.PERSON_COLLATERAL.equalsIgnoreCase(person.getStagePersType())
								&& !(StringUtils.isEmpty(person.getNmPersonFirst())
										&& StringUtils.isEmpty(person.getNmpersonLast())))
						.findAny().isPresent();

				if (persSearch) {
					cpsInvConclValoDto.getUsSysNbrMessageCode()
							.add(Long.valueOf(ServiceConstants.MSG_INV_PERS_SEARCH_REQ));
				}
			}
			/*
			 * Check the below conditions for each prinicipal having non blank
			 * first and last name. Person Characteristics should only be
			 * required for principals (PRNs). The following if-statement
			 * checked that condition.
			 */
			if (ServiceConstants.CHAR_Y == cpsInvConclValiDto.getDcdEditProcess()
					.charAt(ServiceConstants.PERS_CHARACTER_EDIT)) {
				boolean persChar = personList
						.stream().filter(
								person -> (StringUtils.isEmpty(person.getPersonChar())
										|| ServiceConstants.IND_NO.equalsIgnoreCase(person.getPersonChar()))
										&& ServiceConstants.PRINCIPAL.equalsIgnoreCase(person.getStagePersType())
										&& !(StringUtils.isEmpty(person.getNmPersonFirst())
												&& StringUtils.isEmpty(person.getNmpersonLast())))
						.findAny().isPresent();
				if (persChar) {
					cpsInvConclValoDto.getUsSysNbrMessageCode()
							.add(Long.valueOf(ServiceConstants.MSG_INV_PERS_CHAR_REQ));
				}
			}

			/*
			 * Check the below conditions for each prinicipal having non blank
			 * first and last name. Check that all principals, not just victims,
			 * have a DOB. If one does not, set flag to post warning.
			 **
			 */
			if (ServiceConstants.CHAR_Y == cpsInvConclValiDto.getDcdEditProcess()
					.charAt(ServiceConstants.VICTIM_DOB_EDIT)) {
				boolean victimDob = personList
						.stream().filter(
								person -> ObjectUtils.isEmpty(person.getDtPersonBirth())
										&& ServiceConstants.PRINCIPAL.equalsIgnoreCase(person.getStagePersType())
										&& !(StringUtils.isEmpty(person.getNmPersonFirst())
												&& StringUtils.isEmpty(person.getNmpersonLast())))
						.findAny().isPresent();
				if (victimDob) {
					cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.VICTIM_DOB_WARNING));
				}
			}

			// check that only one oldest victim exists. If two or more, set the
			// flag to post warning.
			long countOV = personList.stream().filter(
					person -> ServiceConstants.PERSON_OLDEST_VICTIM.equalsIgnoreCase(person.getStagePersRelInt()))
					.count();
			if (countOV > 1l) {
				cpsInvConclValoDto.getUsSysNbrMessageCode()
						.add(Long.valueOf(ServiceConstants.MSG_INT_ONE_PRINCIPAL_OV));
			}
			/*
			 ** Call CSESA3D - get n entire row from the EVENT table based on the
			 * ID STAGE, CD TASK, and CD EVENT TYPE.
			 */
			if (cpsInvConclValiDto.getDcdEditProcess()
					.substring(ServiceConstants.SVC_REF_CHKLST_EDIT, ServiceConstants.SVC_REF_CHKLST_EDIT + 1)
					.equalsIgnoreCase(ServiceConstants.Y)) {
				List<EventStageTypeTaskOutDto> eventDtls = getEventDtls(cpsInvConclValiDto);
				boolean svcRefChkLst = eventDtls.stream()
						.filter(event -> !ObjectUtils.isEmpty(event.getCdEventStatus())
								&& (event.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_COMPLETE)
										|| (cpsInvConclValiDto.getSysNbrReserved1() && event.getCdEventStatus()
												.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_PENDING))))
						.findAny().isPresent();
				if (!svcRefChkLst) {
					cpsInvConclValoDto.getUsSysNbrMessageCode()
							.add(Long.valueOf(ServiceConstants.MSG_INV_SVC_RFRL_CHKLST_WARNING));
				}
				// cpsInvConclValoDto.getIdEvent().addAll(eventDtls);
			}
			/*
			 * If a safety Eval is required Call
			 * CCMN87D(getStageAndEventDetails) using ID STAGE and CD TASK ==
			 * SAFETY_EVAL_TASK
			 */
			if (ServiceConstants.CHAR_Y == cpsInvConclValiDto.getDcdEditProcess()
					.charAt(ServiceConstants.SAFETY_EVAL_EDIT)) {
				List<EventStagePersonLinkInsUpdOutDto> safetyEvent = getStageAndEventDetails(cpsInvConclValiDto,
						ServiceConstants.SAFETY_EVAL_TASK, ServiceConstants.SAFETY_EVAL_EVENT_TYPE);
				if (!ObjectUtils.isEmpty(safetyEvent)) {
					boolean safetyEvalWarning = safetyEvent.stream()
							.filter(event -> !ServiceConstants.EVENTSTATUS_COMPLETE.equals(event.getCdEventStatus())
									|| (cpsInvConclValiDto.getSysNbrReserved1()
											&& !ServiceConstants.EVENTSTATUS_PENDING.equals(event.getCdEventStatus())))
							.findAny().isPresent();
					if (safetyEvalWarning) {
						cpsInvConclValoDto.getUsSysNbrMessageCode()
								.add(Long.valueOf(ServiceConstants.SAFETY_EVAL_WARNING));
					}
				}
			}
      /*
       ** If a EA Questions are required, check the value of
       ** IND_CPS_INVST_EA_CONCL passed in the Input Message
       */

      /*
        PPM #87002
        1. For Undetermined status of the oldest victim person with any risk level and Stage intake date
        is more than EA release date, then EA eligibility questions will not display. We can skip EA Question validations.
        2. Before release date (3-Apr-25) if status is high or very high, then validations are required for EA questions.
      */
      if (isEAValidationRequired(cpsInvConclValiDto, stageEvtStatusDto)) {
        if (ServiceConstants.Y.equalsIgnoreCase(
                cpsInvConclValiDto
                    .getDcdEditProcess()
                    .substring(
                        ServiceConstants.EA_QUESTIONS_EDIT, ServiceConstants.EA_QUESTIONS_EDIT + 1))
            && StringUtils.isEmpty(cpsInvConclValiDto.getIndCpsInvstEaConcl())
            && (((ServiceConstants.RISK_INDICATED.equalsIgnoreCase(
                            cpsInvConclValiDto.getCdRiskAssmtRiskFind())
                        || ServiceConstants.RISK_CERA.equalsIgnoreCase(
                            cpsInvConclValiDto.getCdRiskAssmtRiskFind()))
                    && ServiceConstants.N.equalsIgnoreCase(cpsInvConclValiDto.getIndSDM()))
                || ((ServiceConstants.YES.equalsIgnoreCase(cpsInvConclValiDto.getIndSDM()))
                    && (ServiceConstants.YES.equalsIgnoreCase(
                        cpsInvConclValiDto.getIndSDMHighRisk()))))) {
          cpsInvConclValoDto
              .getUsSysNbrMessageCode()
              .add(Long.valueOf(ServiceConstants.EA_QUESTIONS_WARNING));
        }
			}

			/*
			 ** Call CSESC2D - To display a warning if the CD_CRIM_HIST_ACTION is
			 * null for a given ID_STAGE from the Criminal History table
			 */
			cpsInvConclValoDto.setIdPerson(getCriminalCheckRecords(cpsInvConclValiDto));
			if (cpsInvConclValoDto.getIdPerson() > 0) {
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.CRML_HIST_CHECK));
			}

			/*
			 ** Call CLSS30D - Before the user can Save and Submit a CPS
			 * Investigation Conclusion with recommendation "Removal/Subcare", a
			 ** Subcare stage must exist that meets one of the following
			 ** criteria: 1) is currently open, or 2) has been closed within the
			 * timespan of the investigation.
			 */
			if (ServiceConstants.Y.equalsIgnoreCase(cpsInvConclValiDto.getDcdEditProcess()
					.substring(ServiceConstants.OPEN_SUBCARE_STAGE, ServiceConstants.OPEN_SUBCARE_STAGE + 1))
					&& !getStageDetails(cpsInvConclValiDto)) { // Defect 10781 - negate the condition (if sub stage is found, allow submission)
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_OPEN_SUBCARE_STAGE));
			}
			/*
			 ** Call CSECA8D - Retrieves a row if Investigation was progressed to
			 * SUB/FSU.
			 */
			subsequentSUB = getPriorStage(cpsInvConclValiDto);
			/*
			 ** Call CLSSB2D - To display a warning if the Question 'Did you
			 * request orders during the investion?' is answered 'Y' but no
			 * legal actions exist, or answered 'N' and legal actions do exist
			 */
			getLegalEventDtls(cpsInvConclValiDto, cpsInvConclValoDto, subsequentSUB);
			// Service Authorization check
			if (ServiceConstants.Y.equalsIgnoreCase(cpsInvConclValiDto.getDcdEditProcess()
					.substring(ServiceConstants.SVC_AUTH_EDIT, ServiceConstants.SVC_AUTH_EDIT + 1))) {
				checkServiceAuth(cpsInvConclValiDto, cpsInvConclValoDto);
			}
			// Consistency Check
			if (ServiceConstants.N.equalsIgnoreCase(cpsInvConclValiDto.getIndSDM())
					&& !ObjectUtils.isEmpty(cpsInvConclValiDto.getCdRiskAssmtRiskFind())
					&& !cpsInvConclValiDto.getCdRiskAssmtRiskFind().equals(ServiceConstants.RISK_CERA)) {
				consistencyCheck(cpsInvConclValiDto, cpsInvConclValoDto);
			}
			/*
			 ** Call CLSC18D - retrieves a list of principals or collaterals in
			 * the stage and data about them using Id Stage from input.
			 */

			List<StagePrincipalDto> personDetails = getPersonDtls(cpsInvConclValiDto, cpsInvConclValoDto);

			if (ServiceConstants.Y.equalsIgnoreCase(cpsInvConclValiDto.getDcdEditProcess()
					.substring(ServiceConstants.RSN_DTH_EDIT, ServiceConstants.RSN_DTH_EDIT + 1))) {
				for (StagePrincipalDto personDetail : personDetails) {
					/*
					 * Call CSES97D - check that allegation severity is fatal if
					 * death code is A/N, but we also want to check that
					 * allegation severity is NOT fatal when death code is NOT
					 * A/N.
					 */
					getAllegationDtls(cpsInvConclValiDto, cpsInvConclValoDto, personDetail.getIdPerson(),
							personDetail.getDtPersonDeath(), personDetail.getCdPersonDeath(),
							personDetail.getCdDeathRsnCps());
					cpsInvConclValoDto.setIdPerson(personDetail.getIdPerson());
				}
			}
			/*
			 ** If no warnings posted, prepare to submit for approval
			 */
			if (cpsInvConclValoDto.getUsSysNbrMessageCode().isEmpty()) {
				/*
				 * Call CINV43D - Set all TODOs associated with event to
				 * COMPLETED
				 */
				updateTodoCompleted(cpsInvConclValiDto);
				/*
				 * Call CCMN87D - Retrieve all the events associated with the
				 * Investigation
				 */
				eventsForApproval = getStageAndEventDetails(cpsInvConclValiDto, ServiceConstants.NO_TASK,
						ServiceConstants.NO_EVENT_TYPE);
				// Exclude the Auth and approval events along with New Events
				// and submit the event list for approval
				eventsForApproval = eventsForApproval.stream()
						.filter(event -> !ServiceConstants.SVC_AUTH_EVENT_TYPE.equals(event.getCdEventType())
								&& !ServiceConstants.EVENT_STATUS_NEW.equals(event.getCdEventStatus())
								&& !ServiceConstants.APPROVAL_EVENT_TYPE.equals(event.getCdEventType()))
						.collect(Collectors.toList());
				cpsInvConclValoDto.setEventsForApproval(eventsForApproval);

			}

		}
		log.debug("Exiting method callCpsInvConclValService in CpsInvConclValServiceImpl");
		return cpsInvConclValoDto;
	}

  private boolean isIntakeDateBeforeEAReleaseDate(java.util.Date intakeDate) {
    String decDecode =
        lookupService.simpleDecodeSafe(
            CodesConstant.CRELDATE, CodesConstant.CRELDATE_APR_2025_IMPACT_EA);
    boolean preSingleStage = false;
    try {
      preSingleStage = DateHelper.isBefore(intakeDate, DateHelper.toJavaDateFromInput(decDecode));
    } catch (Exception e) {
      log.error("isIntakeDateBeforeEAReleaseDate " + e.getMessage());
    }
    return preSingleStage;
  }

  /**
   * Method Description: This method check release date against stage intake date and return boolean
   * value.
   */
  private boolean isEAValidationRequired(
      CpsInvConclValiDto cpsInvConclValiDto, StageTaskInDto stageEvtStatusDto) {
    String oldestVictimCtzStatus =
        personDao.getOldestVictimCitizenshipByStageIdAndRelType(
            stageEvtStatusDto.getIdStage(), ServiceConstants.PERSON_OLDEST_VICTIM);
    boolean isValidationRequired = false;
    if (!StringUtils.isEmpty(oldestVictimCtzStatus)
        && !ServiceConstants.CITIZENSHIP_STATUS_TMR.equalsIgnoreCase(oldestVictimCtzStatus)
        && !isIntakeDateBeforeEAReleaseDate(cpsInvConclValiDto.getDtCPSInvstDtlIntake())) {
      isValidationRequired = true;
    } else if (isIntakeDateBeforeEAReleaseDate(cpsInvConclValiDto.getDtCPSInvstDtlIntake())) {
      isValidationRequired = true;
    }
    return isValidationRequired;
  }

  /**
   * Method Name: validationForSexualVctmizationQues Method Description: this method check if all
   * the PRN less then 18 have answered Sexual Vctmization Question
   *
   * @param Long
   * @return boolean
   */
  private boolean validationForSexualVctmizationQues(Long stageId) {
		int personage = ServiceConstants.Zero_INT;
		Date dtDtSystemDate = new Date();
		if(stageId != null) {
			List<ContactPrincipalsCollateralsDto> listPpl = personListByStageDao.getPRNPersonDetailsForStage(stageId, ServiceConstants.PRINCIPAL);		
			if(!ObjectUtils.isEmpty(listPpl)) {				
				for(ContactPrincipalsCollateralsDto dto:listPpl) {
					ContactPrincipalsCollateralsDto person = dto;
					 personage = DateUtils.calculatePersonsAgeInYears(dto.getDtPersonBirth(),dtDtSystemDate);//person.getPersonAge();
					if( personage  < ServiceConstants.MIN_PG_AGE) {
						boolean answered = personListByStageDao.getIndChildSxVctmzinHistory(person.getIdPerson());
						if(!answered) {
							return false;
						}
					}	
				}
				
			}
			
			
		}

		return true;
	}
	
	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will give list
	 * of Event Type meeting the criteria.
	 * 
	 * @param cpsInvConclValiDto
	 * @return List<EventStageTypeTaskOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStageTypeTaskOutDto> getEventDtls(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCSESA3D in CpsInvConclValServiceImpl");
		EventStageTypeTaskInDto eventStageTypeTaskInDto = new EventStageTypeTaskInDto();
		eventStageTypeTaskInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		eventStageTypeTaskInDto.setCdEventType(ServiceConstants.CHCKLST_EVENT_TYPE);
		eventStageTypeTaskInDto.setCdTask(ServiceConstants.SVC_REF_CKLST_TASK);
		return eventStageTypeTaskDao.getEventDtls(eventStageTypeTaskInDto);
	}

	/**
	 * 
	 * Method Name: getStageAndEventDetails Method Description: This method will
	 * give list of data from EVENT table and Stage table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStageTypeTaskOutDto> @
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStagePersonLinkInsUpdOutDto> getStageAndEventDetails(CpsInvConclValiDto cpsInvConclValiDto,
			String cdTask, String cdEventType) {
		log.debug("Entering method CallCCMN87D in CpsInvConclValServiceImpl");
		EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
		eventStagePersonLinkInsUpdInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		eventStagePersonLinkInsUpdInDto.setCdEventType(cdEventType);
		eventStagePersonLinkInsUpdInDto.setCdTask(cdTask);
		eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		List<EventStagePersonLinkInsUpdOutDto> eventAndStatusDtlsList = eventStagePersonLinkInsUpdDao
				.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
		log.debug("Exiting method CallCCMN87D in CpsInvConclValServiceImpl");
		return eventAndStatusDtlsList;
	}

	/**
	 * 
	 * Method Name: updateTodoCompleted Method Description: This method will
	 * give list of Person Type meeting the criteria.
	 * 
	 * @param pInputMsg
	 * @return List<EventStageTypeTaskOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateTodoCompleted(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCINV43D in CpsInvConclValServiceImpl");
		TodoUpdDtTodoCompletedInDto todoUpdDtTodoCompletedInDto = new TodoUpdDtTodoCompletedInDto();
		todoUpdDtTodoCompletedInDto.setIdEvent(cpsInvConclValiDto.getIdEvent());
		todoUpdDtTodoCompletedDao.updateTODOEvent(todoUpdDtTodoCompletedInDto);
		log.debug("Exiting method CallCINV43D in CpsInvConclValServiceImpl");
	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will give
	 * list of Person Type meeting the criteria.
	 * 
	 * @param CpsInvConclValiDto
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean getStageDetails(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCLSS30D in CpsInvConclValServiceImpl");
		boolean subStageFound = false;
		StageSituationInDto stageSituationInDto = new StageSituationInDto();
		stageSituationInDto.setIdCase(cpsInvConclValiDto.getIdCase());
		List<StageSituationOutDto> stageDetails = null;
		stageDetails = stageSituationDao.getStageDetails(stageSituationInDto);
		//Modified the date compare condition for Warranty defect - 11728
		if (stageDetails.stream()
				.filter(stage -> !ObjectUtils.isEmpty(stage.getCdStage())
						&& stage.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_SUB)
						&& (ObjectUtils.isEmpty(stage.getDtStageClose())
								|| stage.getDtStageClose().compareTo(cpsInvConclValiDto.getDtCPSInvstDtlBegun()) > 0))
				.findAny().isPresent()) {
			subStageFound = true;
		}
		log.debug("Exiting method CallCLSS30D in CpsInvConclValServiceImpl");
		return subStageFound;
	}

	/**
	 * 
	 * Method Name: checkServiceAuth Method Description: perform the Service
	 * Authorization edit
	 * 
	 * @param pInputMsg
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void checkServiceAuth(CpsInvConclValiDto cpsInvConclValiDto, CpsInvConclValoDto cpsInvConclValoDto) {
		log.debug("Entering method checkServiceAuth in CpsInvConclValServiceImpl");
		boolean lastStage = false;
		/*
		 * Call CCMNF6D - retrieves all the open stages associated to the given
		 * ID CASE.
		 * 
		 */
		List<StageCdOutDto> stageCdOutDtoList = getStageDtlsByDate(cpsInvConclValiDto);
		/*
		 ** If we only get one row back, then the current Investigation stage is
		 * the last stage in the case
		 */
		if (stageCdOutDtoList.stream().count() == 1) {
			lastStage = true;
		}
		/*
		 * Call CCMN87D -retrieves event records based on the given search
		 * criteria.
		 */
		List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdList = getStageAndEventDtls(
				cpsInvConclValiDto);
		Date dtCurrentDate = new Date();
		if ((!lastStage)
				|| (cpsInvConclValiDto.getCdStageReasonClosed().equalsIgnoreCase(ServiceConstants.FAM_PRES_CODE))
				|| (cpsInvConclValiDto.getCdStageReasonClosed().equalsIgnoreCase(ServiceConstants.MOD_FAM_PRES_CODE))
				|| (cpsInvConclValiDto.getCdStageReasonClosed().equalsIgnoreCase(ServiceConstants.INTNSV_FAM_PRES_CODE))
				|| (cpsInvConclValiDto.getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.CNTRCTED_FAM_PRES_CODE))
				|| (cpsInvConclValiDto.getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.CNTRCTED_MOD_FAM_PRES_CODE))
				|| (cpsInvConclValiDto.getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.CNTRCTED_INTNSV_FAM_PRES_CODE))) {
			/*
			 ** Check that there are open COMPlete or PENDing Service
			 ** Authorizations.
			 */
			//below code has been updated for defect 2337 and artf55938
			if (eventStagePersonLinkInsUpdList!=null) {
				eventStagePersonLinkInsUpdList.forEach(eventStagePersonLink -> {
						/*
						 * Call CSES24D -Retrieve the IdSvcAuth based on the
						 * IdCase - updated
						 */
						List<SvcAuthEventLinkOutDto> authEventLink = getAuthEventLinkByCase(
								eventStagePersonLink.getIdCase());
						authEventLink.forEach(authEvent -> {
							String eventStatus = eventDao.getEventStatusForStageInINV(authEvent.getIdSvcAuthEvent(),eventStagePersonLink.getIdStage());//method updated for for defect 15439 artf163376
							if(ServiceConstants.EVENTSTATUS_COMPLETE.equalsIgnoreCase(eventStatus) ||
									ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventStatus) ){
								/*
								 * Call CLSS24D - retrieve the SvcAuthDtlTermDate
								 * for each IdSvcAuth returned
								 */
								List<SvcAuthDetailNameOutDto> serviceAuthentications = getServiceAuthentication(
										authEvent.getIdSvcAuth());
								if (serviceAuthentications.stream()
										.filter(serviceAuthentication -> !ObjectUtils
												.isEmpty(serviceAuthentication.getDtSvcAuthDtlTerm())
												&& dtCurrentDate.compareTo(serviceAuthentication.getDtSvcAuthDtlTerm()) < 0)
										.findAny().isPresent()) {
									cpsInvConclValoDto.getUsSysNbrMessageCode()
											.add(Long.valueOf(ServiceConstants.MSG_SVA_OPN_AUTHS));
								}
							}
						});
				});
			}
			/*
			 ** Check that there are open APRV FORMER_DAY_CARE
			 */
			if (eventStagePersonLinkInsUpdList.stream()
					.filter(eventStagePersonLinkIns -> !ObjectUtils.isEmpty(eventStagePersonLinkIns.getCdEventStatus())
							&& (eventStagePersonLinkIns.getCdEventStatus()
									.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE)))
					.findAny().isPresent()) {
				eventStagePersonLinkInsUpdList.forEach(eventStagePersonLink -> {
					if (!ObjectUtils.isEmpty(eventStagePersonLink.getCdEventStatus()) && (eventStagePersonLink
							.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE))) {
						/*
						 * Call CSES24D -Retrieve the IdSvcAuth based on the
						 * IdEvent
						 */
						List<SvcAuthEventLinkOutDto> authEventLink = getAuthEventLink(
								eventStagePersonLink.getIdEvent());
						authEventLink.forEach(authEvent -> {
							/*
							 * Call CLSS24D - retrieve the SvcAuthDtlTermDate
							 * for each IdSvcAuth returned
							 */
							List<SvcAuthDetailNameOutDto> serviceAuthentications = getServiceAuthentication(
									authEvent.getIdSvcAuth());
							if (serviceAuthentications.stream()
									.filter(serviceAuthentication -> !ObjectUtils
											.isEmpty(serviceAuthentication.getDtSvcAuthDtlTerm())
											&& dtCurrentDate.compareTo(serviceAuthentication.getDtSvcAuthDtlTerm()) < 0
											&& serviceAuthentication.getCdSvcAuthDtlSvc()
													.equalsIgnoreCase(ServiceConstants.FORMER_DAY_CARE))
									.findAny().isPresent()) {
								cpsInvConclValoDto.getUsSysNbrMessageCode()
										.add(Long.valueOf(ServiceConstants.MSG_SVA_OPN_AUTHS));
							}
						});
					}
				});
			}
		}else if (lastStage && eventStagePersonLinkInsUpdList!=null) {//below code has been added for defect 2337 and artf55938
			eventStagePersonLinkInsUpdList.forEach(eventStagePersonLink -> {
				List<SvcAuthEventLinkOutDto> authEventLink = getAuthEventLinkByCase(eventStagePersonLink.getIdCase());
				authEventLink.forEach(authEvent -> {
					String eventStatus = eventDao.getEventStatusForStageInINV(authEvent.getIdSvcAuthEvent(),eventStagePersonLink.getIdStage());//method updated for for defect 15439 artf163376
					if(ServiceConstants.EVENTSTATUS_COMPLETE.equalsIgnoreCase(eventStatus) ||
							ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventStatus) || ServiceConstants.EVENTSTATUS_APPROVE.equalsIgnoreCase(eventStatus) ){
						List<SvcAuthDetailNameOutDto> serviceAuthentications = getServiceAuthentication(
								authEvent.getIdSvcAuth());
						if (serviceAuthentications.stream()
								.filter(serviceAuthentication -> !ObjectUtils
										.isEmpty(serviceAuthentication.getDtSvcAuthDtlTerm())
										&& dtCurrentDate.compareTo(serviceAuthentication.getDtSvcAuthDtlTerm()) < 0)
								.findAny().isPresent()) {
							cpsInvConclValoDto.getUsSysNbrMessageCode()
									.add(Long.valueOf(ServiceConstants.MSG_SVA_OPN_AUTHS));
						}
					}
				});
				
			});
		}else if (eventStagePersonLinkInsUpdList.stream().filter(eventStagePersonLinkIns -> !ObjectUtils
				.isEmpty(eventStagePersonLinkIns.getCdEventStatus())
				&& (eventStagePersonLinkIns.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_COMPLETE)
						|| eventStagePersonLinkIns.getCdEventStatus()
								.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_PENDING)
						|| eventStagePersonLinkIns.getCdEventStatus()
								.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE)))
				.findAny().isPresent()) {
			eventStagePersonLinkInsUpdList.forEach(eventStagePersonLink -> {
				if (!ObjectUtils.isEmpty(eventStagePersonLink.getCdEventStatus()) && (eventStagePersonLink
						.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_COMPLETE)
						|| eventStagePersonLink.getCdEventStatus()
								.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_PENDING)
						|| eventStagePersonLink.getCdEventStatus()
								.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE))) {
					/*
					 * Call CSES24D -Retrieve the IdSvcAuth based on the IdEvent
					 */
					List<SvcAuthEventLinkOutDto> authEventLink = getAuthEventLink(eventStagePersonLink.getIdEvent());
					authEventLink.forEach(authEvent -> {
						/*
						 * Call CLSS24D - retrieve the SvcAuthDtlTermDate for
						 * each IdSvcAuth returned
						 */
						List<SvcAuthDetailNameOutDto> serviceAuthentications = getServiceAuthentication(
								authEvent.getIdSvcAuth());
						if (serviceAuthentications.stream()
								.filter(serviceAuthentication -> !ObjectUtils
										.isEmpty(serviceAuthentication.getDtSvcAuthDtlTerm())
										&& dtCurrentDate.compareTo(serviceAuthentication.getDtSvcAuthDtlTerm()) < 0
										&& serviceAuthentication.getCdSvcAuthDtlSvc()
												.equalsIgnoreCase(ServiceConstants.FORMER_DAY_CARE))
								.findAny().isPresent()) {
							cpsInvConclValoDto.getUsSysNbrMessageCode()
									.add(Long.valueOf(ServiceConstants.MSG_SVA_OPN_AUTHS));
						}
					});
				}
			});
		}
		log.debug("Exiting method checkServiceAuth in CpsInvConclValServiceImpl");
	}
	
	/**
	 * this method retrieves row from SVC_AUTH_EVENT_LINK based on caseid
	 * code added for defect 2337
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthEventLinkOutDto> getAuthEventLinkByCase(long idCase) {
		SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
		svcAuthEventLinkInDto.setCaseId(idCase);
		List<SvcAuthEventLinkOutDto> authEventLink = svcAuthEventLinkDao.getAuthEventLinkByCase(svcAuthEventLinkInDto);
		return authEventLink;
	}

	/**
	 * 
	 * Method Name: getStageAndEventDtls Method Description: This method will
	 * give list of data from EVENT and STAGE table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStagePersonLinkInsUpdOutDto> getStageAndEventDtls(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCCMN87DA in CpsInvConclValServiceImpl");
		EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
		eventStagePersonLinkInsUpdInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		eventStagePersonLinkInsUpdInDto.setCdTask(ServiceConstants.TASK);
		eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutList = eventStagePersonLinkInsUpdDao
				.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
		log.debug("Exiting method CallCCMN87DA in CpsInvConclValServiceImpl");
		return eventStagePersonLinkInsUpdOutList;
	}

	/**
	 * 
	 * Method Name: getAuthEventLink Method Description: This method will give
	 * list of data from SVC_AUTH_EVENT_LINK table.
	 * 
	 * @param idSvcAuthEvent
	 * @return List<SvcAuthEventLinkOutDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthEventLinkOutDto> getAuthEventLink(long idSvcAuthEvent) {
		log.debug("Entering method CallCSES24D in CpsInvConclValServiceImpl");
		SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
		svcAuthEventLinkInDto.setIdSvcAuthEvent(idSvcAuthEvent);
		List<SvcAuthEventLinkOutDto> authEventLink = svcAuthEventLinkDao.getAuthEventLink(svcAuthEventLinkInDto);
		log.debug("Exiting method CallCSES24D in CpsInvConclValServiceImpl");
		return authEventLink;
	}

	/**
	 * 
	 * Method Name: getServiceAuthentication Method Description: This method
	 * will give list of data from SVC_AUTH_DTL table.
	 * 
	 * @param idSvcAuth
	 * @return List<SvcAuthDetailNameOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthDetailNameOutDto> getServiceAuthentication(long idSvcAuth) {
		log.debug("Entering method CallCLSS24D in CpsInvConclValServiceImpl");
		SvcAuthDetailNameInDto svcAuthDetailNameInDto = new SvcAuthDetailNameInDto();
		svcAuthDetailNameInDto.setIdSvcAuth(idSvcAuth);
		List<SvcAuthDetailNameOutDto> serviceAuthentication = svcAuthDetailNameDao
				.getServiceAuthentication(svcAuthDetailNameInDto);
		log.debug("Exiting method CallCLSS24D in CpsInvConclValServiceImpl");
		return serviceAuthentication;
	}

	/**
	 * 
	 * Method Name: CCMNF6D getStageDtlsByDate Method Description: This method
	 * will give list of data from Stage table.
	 * 
	 * @param pInputMsg
	 * @return List<StageCdOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageCdOutDto> getStageDtlsByDate(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCCMNF6D in CpsInvConclValServiceImpl");
		StageCdInDto stageCdInDto = new StageCdInDto();
		stageCdInDto.setIdCase(cpsInvConclValiDto.getIdCase());
		List<StageCdOutDto> stageDtlsByDate = stageCdDao.getStageDtlsByDate(stageCdInDto);
		log.debug("Exiting method CallCCMNF6D in CpsInvConclValServiceImpl");
		return stageDtlsByDate;
	}

	/**
	 * 
	 * Method Name: callConsistencyCheck Method Description: This method will
	 * perform consistency check.
	 * 
	 * @param cpsInvConclValiDto
	 * @param CpsInvConclValoDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void consistencyCheck(CpsInvConclValiDto cpsInvConclValiDto, CpsInvConclValoDto cpsInvConclValoDto) {
		log.debug("Entering method CallConsistencyCheck in CpsInvConclValServiceImpl");
		/*
		 * Call CCMNB4D - Retrieve response to the first EA Question
		 */
		List<EventRiskAssessmentOutDto> eventDetails = getEventDetails(cpsInvConclValiDto);
		/*
		 * Call CINV15D - retrieves full rows from the emregency_assist table.
		 */
		if (!ObjectUtils.isEmpty(eventDetails)) {
			List<EmergencyAssistOutDto> emerAssistDtls = getEmerAssistDtls(eventDetails.get(0).getIdEvent());
			/*
			 * Call CINV95D - retrieves bIndCpsInvstSafetyPln from
			 * CPS_INVST_DETAIL.
			 */
			List<CpsInvstDetailStageIdOutDto> investmentDetails = getInvestmentDtls(cpsInvConclValiDto);

			if (emerAssistDtls.stream()
					.filter(emergencyAssistOutDto -> !ObjectUtils.isEmpty(emergencyAssistOutDto.getIndEaResponse())
							&& (ServiceConstants.YES.equalsIgnoreCase(emergencyAssistOutDto.getIndEaResponse())
									&& !ServiceConstants.RISK_INDICATED
											.equalsIgnoreCase(cpsInvConclValiDto.getCdRiskAssmtRiskFind())
									&& investmentDetails.get(0).getIndCpsInvstSafetyPln()
											.equalsIgnoreCase(ServiceConstants.NO))
							|| (ServiceConstants.N.equalsIgnoreCase(emergencyAssistOutDto.getIndEaResponse())
									&& ServiceConstants.RISK_INDICATED.equalsIgnoreCase(
											cpsInvConclValiDto.getCdRiskAssmtRiskFind())
									&& investmentDetails.get(0).getIndCpsInvstSafetyPln()
											.equalsIgnoreCase(ServiceConstants.YES)))
					.findAny().isPresent())

			{
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_EA_NOT_RISK_SAFETY));
			}
		}
		log.debug("Exiting method CallConsistencyCheck in CpsInvConclValServiceImpl");
	}

	/**
	 * 
	 * Method Name: getEmerAssistDtls Method Description: This method will give
	 * list of data from emergency assist table.
	 * 
	 * @param pInputMsg
	 * @param ulIdEvent
	 * @return List<EmergencyAssistOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EmergencyAssistOutDto> getEmerAssistDtls(long idEvent) {
		log.debug("Entering method CallCINV15D in CpsInvConclValServiceImpl");
		EmergencyAssistInDto emergencyAssistInDto = new EmergencyAssistInDto();
		emergencyAssistInDto.setIdEvent(idEvent);
		List<EmergencyAssistOutDto> emergencyAssistOutList = emergencyAssistDao.fetchQues(emergencyAssistInDto);
		log.debug("Exiting method CallCINV15D in CpsInvConclValServiceImpl");
		return emergencyAssistOutList;
	}

	/**
	 * 
	 * Method Name: getEventDetails Method Description: This method will give
	 * list of data from EVENT table.
	 * 
	 * @param pInputMsg
	 * @return List<EventRiskAssessmentOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventRiskAssessmentOutDto> getEventDetails(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCCMNB4D in CpsInvConclValServiceImpl");
		EventRiskAssessmentInDto eventRiskAssessmentInDto = new EventRiskAssessmentInDto();
		eventRiskAssessmentInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		eventRiskAssessmentInDto.setCdTask(ServiceConstants.TASK);
		List<EventRiskAssessmentOutDto> eventDetails = eventRiskAssessmentDao.getEvent(eventRiskAssessmentInDto);
		log.debug("Exiting method CallCCMNB4D in CpsInvConclValServiceImpl");
		return eventDetails;
	}

	/**
	 * 
	 * Method Name: getInvestmentDtls Method Description: This method will give
	 * list of data from Investment Detail table.
	 * 
	 * @param pInputMsg
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CpsInvstDetailStageIdOutDto> getInvestmentDtls(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCINV95D in CpsInvConclValServiceImpl");
		CpsInvstDetailStageIdInDto cpsInvstDetailStageIdInDto = new CpsInvstDetailStageIdInDto();
		cpsInvstDetailStageIdInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		List<CpsInvstDetailStageIdOutDto> invstDtls = cpsInvstDetailStageIdDao.getInvstDtls(cpsInvstDetailStageIdInDto);
		log.debug("Exiting method CallCINV95D in CpsInvConclValServiceImpl");
		return invstDtls;
	}

	/**
	 * 
	 * Method Name: getPersonDtls Method Description: This method will give list
	 * of data from Person table.
	 * 
	 * @param pInputMsg
	 * @return List<StagePersonLinkPersonStgTypeOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePrincipalDto> getPersonDtls(CpsInvConclValiDto cpsInvConclValiDto,
			CpsInvConclValoDto cpsInvConclValoDto) {
		log.debug("Entering method CallCLSC18D in CpsInvConclValServiceImpl");
		// CLSC18D
		List<StagePrincipalDto> stagePrincipalDtoList = stagePersonLinkDao
				.getStagePrincipalByIdStageType(cpsInvConclValiDto.getIdStage(), ServiceConstants.PRINCIPAL);
		//Modified the code to set the date value - Warranty defect 11225
		Date dtCfFlagDate = DateUtils.date(2014, 9, 1);
		// Below will check if marital Status or Ethinic Group for any Principal
		// is NULL and First & Last name for the same principal is not blank
		// than
		// marital Status and Ethinicity is required before stage closure.
		boolean maritalEthinicity = stagePrincipalDtoList.stream()
				.filter(stagePrincipal -> (ObjectUtils.isEmpty(stagePrincipal.getCdPersonMaritalStatus())
						|| ObjectUtils.isEmpty(stagePrincipal.getCdPersonEthnicGroup())
								&& !(ObjectUtils.isEmpty(stagePrincipal.getNmPersonFirst())
										&& ObjectUtils.isEmpty(stagePrincipal.getNmPersonLast()))))
				.findAny().isPresent();
		if (maritalEthinicity) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MARITAL_ETHNICITY_WARNING));
		}

		if (cpsInvConclValiDto.getDcdEditProcess()
				.substring(ServiceConstants.DATE_RSN_DTH_EDIT, ServiceConstants.DATE_RSN_DTH_EDIT + 1).equalsIgnoreCase(
						ServiceConstants.Y)
				&& stagePrincipalDtoList.stream()
						.filter(stagePrincipal -> !ObjectUtils.isEmpty(stagePrincipal.getDtPersonDeath())
								&& 0 < stagePrincipal.getDtPersonDeath().compareTo(dtCfFlagDate) && ObjectUtils.isEmpty(stagePrincipal.getCdDeathRsnCps()))
						.findAny().isPresent()) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_DATE_RSN_DTH_EDIT));
		}else if(cpsInvConclValiDto.getDcdEditProcess()
				.substring(ServiceConstants.DATE_RSN_DTH_EDIT, ServiceConstants.DATE_RSN_DTH_EDIT + 1).equalsIgnoreCase(
						ServiceConstants.Y)
				&& stagePrincipalDtoList.stream()
						.filter(stagePrincipal -> !ObjectUtils.isEmpty(stagePrincipal.getDtPersonDeath()) 
								&& 0 > stagePrincipal.getDtPersonDeath().compareTo(dtCfFlagDate) && ObjectUtils.isEmpty(stagePrincipal.getCdPersonDeath()))
						.findAny().isPresent()){
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_DATE_RSN_DTH_EDIT));
		}else if (cpsInvConclValiDto.getDcdEditProcess()
				.substring(ServiceConstants.DATE_RSN_DTH_EDIT, ServiceConstants.DATE_RSN_DTH_EDIT + 1)
				.equalsIgnoreCase(ServiceConstants.Y)) {

			stagePrincipalDtoList = stagePersonLinkDao.getStagePrincipalByIdStageType(cpsInvConclValiDto.getIdStage(),
					ServiceConstants.COLLATERAL);

			if (stagePrincipalDtoList.stream()
					.filter(stageCollateral -> !ObjectUtils.isEmpty(stageCollateral.getDtPersonDeath())
							&& 0 < stageCollateral.getDtPersonDeath().compareTo(dtCfFlagDate) && ObjectUtils.isEmpty(stageCollateral.getCdDeathRsnCps()))
					.findAny().isPresent()) {
				cpsInvConclValoDto.getUsSysNbrMessageCode()
						.add(Long.valueOf(ServiceConstants.MSG_INV_DATE_RSN_DTH_EDIT));
			}else if(stagePrincipalDtoList.stream()
					.filter(stageCollateral -> !ObjectUtils.isEmpty(stageCollateral.getDtPersonDeath()) 
							&& 0 > stageCollateral.getDtPersonDeath().compareTo(dtCfFlagDate) && ObjectUtils.isEmpty(stageCollateral.getCdPersonDeath()))
					.findAny().isPresent()){
				cpsInvConclValoDto.getUsSysNbrMessageCode()
				.add(Long.valueOf(ServiceConstants.MSG_INV_DATE_RSN_DTH_EDIT));
			}
		}
		log.debug("Exiting method CallCLSC18D in CpsInvConclValServiceImpl");
		return stagePrincipalDtoList;
	}

	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: This method will give
	 * list of data from ALLEGATION table.
	 * 
	 * @param CpsInvConclValiDto
	 * @param idPerson
	 * @param dtDtPersonDeath
	 * @param szCdPersonDeath
	 * @param szCdDeathRsnCps
	 * @return List<AllegationStageOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AllegationStageOutDto> getAllegationDtls(CpsInvConclValiDto cpsInvConclValiDto,
			CpsInvConclValoDto cpsInvConclValoDto, long idPerson, Date dtPersonDeath, String cdPersonDeath,
			String cdDeathRsnCps) {
		log.debug("Entering method CallCSES97D in CpsInvConclValServiceImpl");
		AllegationStageInDto allegationStageInDto = new AllegationStageInDto();
		allegationStageInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		allegationStageInDto.setIdPerson(idPerson);
		List<AllegationStageOutDto> allegationDtls = allegationStageDao.getAllegationDtls(allegationStageInDto);

		if (!allegationDtls.stream()
				.filter(allegationDtl -> !ObjectUtils.isEmpty(allegationDtl.getCdAllegSeverity())
						&& allegationDtl.getCdAllegSeverity().equalsIgnoreCase(ServiceConstants.ALLEG_SEVERITY))
				.findAny().isPresent()
				&& !ObjectUtils.isEmpty(cdDeathRsnCps)
				&& (cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_IN_OPEN_CASE)
						|| cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_IN_PRIOR_CASE)
						|| cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_NO_PRIOR_CASE))) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_RSN_DTH_EDIT));
		} else if (allegationDtls.stream().filter(allegationDtl -> !ObjectUtils.isEmpty(allegationDtl.getIndFatality())
				&& allegationDtl.getIndFatality().equalsIgnoreCase(ServiceConstants.Y)).findAny().isPresent()) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_CPS_RSN_DTH_EDIT));
		} else if (allegationDtls.stream()
				.filter(allegationDtl -> !ObjectUtils.isEmpty(allegationDtl.getCdAllegSeverity())
						&& allegationDtl.getCdAllegSeverity().equalsIgnoreCase(ServiceConstants.ALLEG_SEVERITY))
				.findAny().isPresent() && (!ObjectUtils.isEmpty(cdPersonDeath) && !ObjectUtils.isEmpty(cdDeathRsnCps))
				&& cdPersonDeath.equalsIgnoreCase(ServiceConstants.NOT_AN_RELATED)
				&& cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.NOT_AN_RELATED)) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_RSN_DTH_EDIT));
		}
		if (ObjectUtils.isEmpty(allegationDtls) && (!ObjectUtils.isEmpty(dtPersonDeath)
				&& (!ObjectUtils.isEmpty(cdPersonDeath) && !ObjectUtils.isEmpty(cdDeathRsnCps))
				&& (cdPersonDeath.equalsIgnoreCase(ServiceConstants.AN_IN_OPEN_CASE)
						|| cdPersonDeath.equalsIgnoreCase(ServiceConstants.AN_IN_PRIOR_CASE)
						|| cdPersonDeath.equalsIgnoreCase(ServiceConstants.AN_NO_PRIOR_CASE)
						|| cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_IN_OPEN_CASE)
						|| cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_IN_PRIOR_CASE)
						|| cdDeathRsnCps.equalsIgnoreCase(ServiceConstants.AN_NO_PRIOR_CASE)))) {
			cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_INV_RSN_DTH_EDIT));
		}
		log.debug("Exiting method CallCSES97D in CpsInvConclValServiceImpl");
		return allegationDtls;
	}

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method Description: This method will
	 * give list of data from CRIMINAL_HISTORY table.
	 * 
	 * @param cpsInvConclValiDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getCriminalCheckRecords(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCSESC2D in CpsInvConclValServiceImpl");
		Long idPerson = 0l;
		CriminalHistoryRecordsCheckInDto criminalHistoryRecordsCheckInDto = new CriminalHistoryRecordsCheckInDto();
		criminalHistoryRecordsCheckInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		List<CriminalHistoryRecordsCheckOutDto> criminalCheckRecords = criminalHistoryRecordsCheckDao
				.getCriminalCheckRecords(criminalHistoryRecordsCheckInDto);
		if (!ObjectUtils.isEmpty(criminalCheckRecords)
				&& !ObjectUtils.isEmpty(criminalCheckRecords.get(0).getIdPerson())
				&& criminalCheckRecords.get(0).getIdPerson() > 0) {
			idPerson = criminalCheckRecords.get(0).getIdPerson();
		}
		log.debug("Exiting method CallCSESC2D in CpsInvConclValServiceImpl");
		return idPerson;
	}

	/**
	 * 
	 * Method Name: getPriorStage Method Description: This method will give list
	 * of ID_PRIOR_STAGE.
	 * 
	 * @param cpsInvConclValiDto
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean getPriorStage(CpsInvConclValiDto cpsInvConclValiDto) {
		boolean subsequentSUB = false;
		log.debug("Entering method CallCSECA8D in CpsInvConclValServiceImpl");
		StageLinkStageInDto stageLinkStageInDto = new StageLinkStageInDto();
		stageLinkStageInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		List<StageLinkStageOutDto> priorStage = stageLinkStageDao.getPriorStage(stageLinkStageInDto);

		if (!ObjectUtils.isEmpty(priorStage)) {
			subsequentSUB = true;
		}
		log.debug("Exiting method CallCSECA8D in CpsInvConclValServiceImpl");
		return subsequentSUB;
	}

	/**
	 * 
	 * Method Name: getLegalEventDtls Method Description: This method will give
	 * list of data from LEGAL EVENT.
	 * 
	 * @param pInputMsg
	 * @return List<LegalActionEventOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<LegalActionEventOutDto> getLegalEventDtls(CpsInvConclValiDto cpsInvConclValiDto,
			CpsInvConclValoDto cpsInvConclValoDto, boolean subsequentSUB) {
		log.debug("Entering method CallCLSSB2D in CpsInvConclValServiceImpl");
		boolean orderGranted = false;
		LegalActionEventInDto legalActionEventInDto = new LegalActionEventInDto();
		legalActionEventInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		List<LegalActionEventOutDto> legalEventDtls = legalActionEventDao.getLegalEventDtls(legalActionEventInDto);
		if (!ObjectUtils.isEmpty(legalEventDtls)) {
			if (legalEventDtls.stream().filter(legalEvent -> !ObjectUtils.isEmpty(legalEvent.getCdLegalActAction())
					&& (legalEvent.getCdLegalActAction().equalsIgnoreCase(ServiceConstants.EXPARTE_ORDER)
							|| legalEvent.getCdLegalActAction().equalsIgnoreCase(ServiceConstants.NON_EMERGENCY))
					&& !ObjectUtils.isEmpty(legalEvent.getCdLegalActOutcome())
					&& legalEvent.getCdLegalActOutcome().equalsIgnoreCase(ServiceConstants.CCOR_010)).findAny()
					.isPresent()) {
				orderGranted = true;
			}
			if (!ObjectUtils.isEmpty(cpsInvConclValiDto.getIndReqOrders())
					&& ServiceConstants.YES.equalsIgnoreCase(cpsInvConclValiDto.getIndReqOrders())
					&& ObjectUtils.isEmpty(legalEventDtls)) {
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_ORDER_DURING_INV));
			}
			if (!ObjectUtils.isEmpty(cpsInvConclValiDto.getIndReqOrders())
					&& ServiceConstants.N.equalsIgnoreCase(cpsInvConclValiDto.getIndReqOrders())
					&& !ObjectUtils.isEmpty(legalEventDtls)) {
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_NO_ORDER_DURING_INV));
			}
			if (!orderGranted && subsequentSUB && (cpsInvConclValiDto.getCdStageReasonClosed()
					.equalsIgnoreCase(ServiceConstants.REMOVAL_SUBCARE))) {
				cpsInvConclValoDto.getUsSysNbrMessageCode()
						.add(Long.valueOf(ServiceConstants.MSG_ORDER_NEEDED_FOR_REMOVAL));
			}

		} else {
			if (!ObjectUtils.isEmpty(cpsInvConclValiDto.getIndReqOrders())
					&& cpsInvConclValiDto.getIndReqOrders().equals(ServiceConstants.AR_YES)) {
				cpsInvConclValoDto.getUsSysNbrMessageCode().add(Long.valueOf(ServiceConstants.MSG_ORDER_DURING_INV));
			}
			if (subsequentSUB && !ObjectUtils.isEmpty(cpsInvConclValiDto.getIndReqOrders())
					&& cpsInvConclValiDto.getIndReqOrders().equals(ServiceConstants.REMOVAL_SUBCARE)) {
				cpsInvConclValoDto.getUsSysNbrMessageCode()
						.add(Long.valueOf(ServiceConstants.MSG_ORDER_NEEDED_FOR_REMOVAL));
			}
		}
		log.debug("Exiting method CallCLSSB2D in CpsInvConclValServiceImpl");
		return legalEventDtls;
	}

	/**
	 * 
	 * Method Name: getEventStatus Method Description: This method will give
	 * list of EVENT_STATUS meeting the criteria.
	 * 
	 * @param pInputMsg
	 * @return List<EventStageTypeStatusOutDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStageTypeStatusOutDto> getEventStatus(CpsInvConclValiDto cpsInvConclValiDto) {
		log.debug("Entering method CallCSVC34D in CpsInvConclValServiceImpl");
		EventStageTypeStatusInDto eventStageTypeStatusInDto = new EventStageTypeStatusInDto();
		eventStageTypeStatusInDto.setIdStage(cpsInvConclValiDto.getIdStage());
		eventStageTypeStatusInDto.setCdEventType(ServiceConstants.DAY_CARE_TYPE);
		eventStageTypeStatusInDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_PENDING);
		List<EventStageTypeStatusOutDto> eventStatus = null;
		try {
			eventStatus = eventStageTypeStatusDao.getEventStatus(eventStageTypeStatusInDto);
		} catch (DataNotFoundException e) {
			log.info("No data found in Event Table");
		}
		log.debug("Exiting method CallCSVC34D in CpsInvConclValServiceImpl");
		return eventStatus;
	}

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: fetchCoSleepingData
	 * 
	 * @param idStage
	 * @return CPSInvConclValBeanRes @
	 */
	@Override
	public CPSInvConclValBeanRes fetchCoSleepingData(Long idStage) {
		log.debug("Entering method fetchCoSleepingData in CpsInvConclValServiceImpl");
		CPSInvConclValBeanRes cpsInvConclValBeanRes = new CPSInvConclValBeanRes();
		List<CpsInvCnclsnValidationDto> cpsInvConclValBeanList = allegationStageDao.getCoSleepingData(idStage);
		cpsInvConclValBeanRes.setCpsInvCnclsnValValueDto(cpsInvConclValBeanList);
		log.debug("Exiting method fetchCoSleepingData in CpsInvConclValServiceImpl");
		return cpsInvConclValBeanRes;
	}

	/**
	 * Method Description: This method Returns any prior stage ID for any given
	 * stage ID and a type request. Example. If a INT stage needs be found for a
	 * case thats currently in a FPR stage. Pass FPR stage ID and 'INT' Method
	 * Name: fetchPriorStageInReverseChronologicalOrder
	 * 
	 * @param idStage
	 * @param cdStageType
	 * @return CPSInvConclValBeanRes @
	 */
	@Override
	public CPSInvConclValBeanRes getPriorStageInReverseChronologicalOrder(Long idStage, String cdStageType) {
		log.debug("Entering method fetchPriorStageInReverseChronologicalOrder in CpsInvConclValServiceImpl");
		CPSInvConclValBeanRes cpsInvConclValBeanRes = new CPSInvConclValBeanRes();
		StageDto stageDtoDetails = allegationStageDao.fetchPriorStageInReverseChronologicalOrder(idStage, cdStageType);
		cpsInvConclValBeanRes.setStageDto(stageDtoDetails);
		log.debug("Exiting method fetchPriorStageInReverseChronologicalOrder in CpsInvConclValServiceImpl");
		return cpsInvConclValBeanRes;
	}
	
	
	
	
}
