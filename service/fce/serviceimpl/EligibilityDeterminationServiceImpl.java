package us.tx.state.dfps.service.fce.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.domiciledeprivation.dto.PrinciplesDto;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.fce.dao.DomicileDeprivationDao;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.EligibilityDeterminationFceDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.fce.service.EligibilityDeterminationService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This Class
 * is used for EligibilityDeterminationServiceImpl Nov 9, 2017- 2:50:41 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class EligibilityDeterminationServiceImpl implements EligibilityDeterminationService {

	@Autowired
	private UpdateToDoDao updateToDoDao;

	@Autowired
	private DomicileDeprivationDao domicileDeprivationDao;

	@Autowired
	private FceService fceService;

	@Autowired
	private FceDao fceDao;

	@Autowired
	private EventService eventService;
	
	@Autowired
	private EventUtil eventUtil;

	/**
	 * Method Name: fetchEligDetermination Method Description:reads the
	 * EligibilityDetermination details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return eligibilityDeterminationFceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EligibilityDeterminationFceDto fetchEligDetermination(Long idStage, Long idEvent, Long idLastUpdatePerson) {

		FceContextDto fceContextDto = fceService.initializeFceApplication(idStage, idEvent, idLastUpdatePerson);

		EligibilityDeterminationFceDto eligibilityDeterminationFceDto = new EligibilityDeterminationFceDto();

		eligibilityDeterminationFceDto.setFceEligibilityDto(fceContextDto.getFceEligibilityDto());

		eligibilityDeterminationFceDto.setIdEvent(fceContextDto.getIdEvent());
		EventDto eventDto = eventService.getEvent(fceContextDto.getIdEvent());
		eligibilityDeterminationFceDto.setDtEventCreated(eventDto.getDtEventCreated());
		eligibilityDeterminationFceDto.setCdEventStatus(fceContextDto.getCdEventStatus());

		Long idFceEligibility = fceContextDto.getIdFceEligibility();
		if (!isStepparentInHomeOfRemoval(idFceEligibility, idStage)) {
			eligibilityDeterminationFceDto.getFceEligibilityDto().setAmtStepparentAlimony(ServiceConstants.DoubleZero);
			eligibilityDeterminationFceDto.getFceEligibilityDto()
					.setAmtStepparentOutsidePmnt(ServiceConstants.DoubleZero);
		}

		List<FceReasonNotEligibleDto> reasonsNotEligibleList = fceDao.findReasonsNotEligible(idFceEligibility);

		eligibilityDeterminationFceDto.setReasonsNotEligible(reasonsNotEligibleList);

		return eligibilityDeterminationFceDto;
	}

	/**
	 * Method Name: isStepparentInHomeOfRemoval Method Description: checks
	 * StepparentInHomeOfRemoval or not
	 * 
	 * @param idFceEligibility
	 * @param idStage
	 * @return Boolean @
	 */
	private Boolean isStepparentInHomeOfRemoval(Long idFceEligibility, Long idStage) {
		List<PrinciplesDto> principlesDtoList = domicileDeprivationDao.findPrinciples(idFceEligibility);
		if (!ObjectUtils.isEmpty(principlesDtoList) && principlesDtoList.size() > 0) {
			for (PrinciplesDto principlesDto : principlesDtoList) {
				if ((ServiceConstants.Y.equals(principlesDto.getIndPersonHmRemoval()))
						&& (ServiceConstants.CRELPRN2_ST.equals(principlesDto.getCdRelInt()))) {
					return ServiceConstants.TRUEVAL;
				}
			}
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: confirmEligibility Method Description: Confirms the
	 * Eligibility
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes confirmEligibility(Long idStage, Long idEvent, Long idLastUpdatePerson) {
		/**
		 * This is implemented to prevent the user from getting confused which
		 * reviews are tied to which application and which review/application is
		 * currently used to assign system-derived eligibility on eligibility
		 * summary. This also keeps the algorithm for copying eligibility
		 * simple. The last approved application or completed review says what
		 * the new eligibility summary's system-derived eligibility is.
		 */
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		int incompleteReviewEvents = fceDao.countIncompleteEvents(ServiceConstants.FCE_REVIEW_TASK_CODE, idStage);

		if (incompleteReviewEvents > ServiceConstants.ZERO_VAL) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_CLOSE_OPEN_FCE_REVIEWS);
			commonHelperRes.setErrorDto(errorDto);
			return commonHelperRes;
		}

		eventUtil.changeEventStatus(idEvent, ServiceConstants.COMPLETE_EVENT, ServiceConstants.APPROVED_EVENT);

		FceApplicationDto fceApplicationDto = fceDao.findApplicationByApplicationEvent(idEvent);
		/**
		 * To update the completion date of the fceApplication
		 */
		fceDao.updateFceApplication(fceApplicationDto);
		Long oldIdFceEligibility = fceApplicationDto.getIdFceEligibility();
		Long idEligibilityEvent = fceService.createEvent(ServiceConstants.FCE_ELIGIBILITY_EVENT_TYPE,
				idLastUpdatePerson, idStage, null);
		FceEligibilityDto lastFceEligibilityDto = fceDao.getFceEligibility(oldIdFceEligibility);
		lastFceEligibilityDto = fceDao.copyEligibility(lastFceEligibilityDto, idLastUpdatePerson, true);
		lastFceEligibilityDto.setIdEligibilityEvent(idEligibilityEvent);
		fceDao.updateFceEligiblility(lastFceEligibilityDto);
		updateToDoDao.completeTodoEvent(idEvent);
		commonHelperRes.setIdEvent(idEligibilityEvent);
		return commonHelperRes;
	}

	/**
	 * Method Name: save Method Description: saves the eligibility determination
	 * details.
	 * 
	 * @param eligibilityDeterminationFceDto
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes saveEligibility(EligibilityDeterminationFceDto eligibilityDeterminationFceDto) {
		FceEligibilityDto fceEligibilityDto = eligibilityDeterminationFceDto.getFceEligibilityDto();
		fceDao.verifyOpenStage(fceEligibilityDto.getIdStage());
		CommonHelperRes commonHelperRes = fceDao.saveEligibility(fceEligibilityDto, false);
		return commonHelperRes;
	}

	/**
	 * Method Name: hasDOBChangedForCertPers Method Description:Checks if
	 * certified persons DOB has changed
	 * 
	 * @param idFceEligibility
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean hasDOBChangedForCertPers(Long idFceEligibility) {
		boolean isChanged = fceDao.hasDOBChangedForCertPers(idFceEligibility);
		return isChanged;
	}

	/**
	 * Method Name: determineEligibility Method Description: Determines the
	 * Eligibility
	 * 
	 * @param eligibilityDeterminationFceDto
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes determineEligibility(EligibilityDeterminationFceDto eligibilityDeterminationFceDto) {
		FceEligibilityDto fceEligibilityDto = eligibilityDeterminationFceDto.getFceEligibilityDto();
		CommonHelperRes commonHelperRes = fceDao.saveEligibility(fceEligibilityDto, false);
		if (ObjectUtils.isEmpty(commonHelperRes.getErrorDto())) {
			Long idFceEligibility = eligibilityDeterminationFceDto.getFceEligibilityDto().getIdFceEligibility();
			fceDao.deleteFceReasonsNotEligible(idFceEligibility);
			fceDao.updateFceReasonNotEligibles(idFceEligibility);
		}
		return commonHelperRes;
	}

	/**
	 * Commented code will be used for adaption assistance.
	 */
	/*  *//**
			 * Method Name: getEligDeterminationInfo Method Description: This
			 * method returns Adoption Assistance Determination information
			 * including - Event
			 * 
			 * @param idStage
			 * @param idAppEvent
			 * @return AaeApplAndDetermDBDto @
			 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public AaeApplAndDetermDBDto
	 * getEligDeterminationInfo(Long idStage, Long idAppEvent) { LOG.
	 * debug("Entering method getEligDeterminationInfo in EligibilityDeterminationService"
	 * );
	 * 
	 * AaeApplAndDetermDBDto aaeApplAndDetermDBDto = new
	 * AaeApplAndDetermDBDto(); try { aaeApplAndDetermDBDto =
	 * fetchEligDeterminationByEvent(idStage, idAppEvent); } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); } LOG.
	 * debug("Exiting method getEligDeterminationInfo in EligibilityDeterminationService"
	 * ); return aaeApplAndDetermDBDto; }
	 * 
	 *//**
		 * Method Name: fetchEligDeterminationByEvent Method Description:
		 * 
		 * @param idStage
		 * @param idAppEvent
		 * @return AaeApplAndDetermDBDto @
		 */
	/*
	 * private AaeApplAndDetermDBDto fetchEligDeterminationByEvent(Long idStage,
	 * Long idAppEvent) { AaeApplAndDetermDBDto aaeApplAndDetermDBDto = new
	 * AaeApplAndDetermDBDto(); try { aaeApplAndDetermDBDto =
	 * applicationBackgroundService.fetchApplicationDetails(idStage, idAppEvent,
	 * false); EligibilityDeterminationDto eligibilityDeterminationDto =
	 * aaeEligDeterminationDao .selectAdptAsstEligDetermFromEvent(idAppEvent);
	 * 
	 * if (eligibilityDeterminationDto.getIdInitialDetermPerson() !=
	 * ServiceConstants.ZERO_VAL) { Person person =
	 * personDao.getPerson(eligibilityDeterminationDto.getIdInitialDetermPerson(
	 * )); eligibilityDeterminationDto.setNmInitialDetermPerson(person.
	 * getNmPersonFull()); } if
	 * (eligibilityDeterminationDto.getIdFinalDetermPerson() !=
	 * ServiceConstants.ZERO_VAL) { Person person =
	 * personDao.getPerson(eligibilityDeterminationDto.getIdFinalDetermPerson())
	 * ;
	 * eligibilityDeterminationDto.setNmFinalDetermPerson(person.getNmPersonFull
	 * ()); } if (eligibilityDeterminationDto.getIdDetermSibPerson() !=
	 * ServiceConstants.ZERO_VAL) { PersonValueDto siblingPersonInfo =
	 * personDao.fetchStagePersonLinkInfo(idStage,
	 * eligibilityDeterminationDto.getIdDetermSibPerson());
	 * eligibilityDeterminationDto.setNameDetermSibPersonFull(siblingPersonInfo.
	 * getFullName());
	 * eligibilityDeterminationDto.setCdStageDetermSibPersonRelInt(
	 * siblingPersonInfo.getRoleInStageCode());
	 * eligibilityDeterminationDto.setNbrAgeDetermSibPerson(new
	 * Long(siblingPersonInfo.getAge())); } if
	 * (!ServiceConstants.CEVTSTAT_APRV.equals(aaeApplAndDetermDBDto.
	 * getEventValueDto().getCdEventStatus()) && !ServiceConstants.CEVTSTAT_COMP
	 * .equals(aaeApplAndDetermDBDto.getEventValueDto().getCdEventStatus())) {
	 * Date dtPlcmtDate =
	 * fetchAdoptivePlacementStartDate(aaeApplAndDetermDBDto.getIdPerson()); if
	 * (!TypeConvUtil.isNullOrEmpty(dtPlcmtDate)) {
	 * eligibilityDeterminationDto.setDtAdptPlcmtStart(dtPlcmtDate); } if
	 * (!TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDBDto.getDtOfBirth())) { if
	 * (isChildApplicableAgeFiscalYr(aaeApplAndDetermDBDto.getDtOfBirth())) {
	 * eligibilityDeterminationDto.setIndApplicableAge(ServiceConstants.Y); }
	 * else {
	 * eligibilityDeterminationDto.setIndApplicableAge(ServiceConstants.N); } }
	 * 
	 * if (!TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDBDto.getDtOfBirth())) {
	 * eligibilityDeterminationDto.setNbrChildFfyAge(
	 * DateUtils.getAgeAtCurrentFiscalYear(aaeApplAndDetermDBDto.getDtOfBirth())
	 * ); } } if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.
	 * getDtAdptAsstAgreement()) &&
	 * !TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDBDto.getDtOfBirth())) {
	 * eligibilityDeterminationDto.setNbrChildAgreementAge(new Long(
	 * DateUtils.getAge(aaeApplAndDetermDBDto.getDtOfBirth(),
	 * eligibilityDeterminationDto.getDtAdptAsstAgreement()))); }
	 * aaeApplAndDetermDBDto.setEligibilityDeterminationDto(
	 * eligibilityDeterminationDto); } catch (DataNotFoundException e) {
	 * LOG.error(e.getMessage()); } return aaeApplAndDetermDBDto; }
	 *//**
		 * Method Name: updateEventAndCreateEligDeterm Method Description: This
		 * method update the Event to PEND and will create a new Adoption
		 * Assistance Eligibility Determination This is called when the
		 * Application is submitted to Eligibility Specialist
		 * 
		 * @param idEvent
		 * @param idLastUpdatePerson
		 * @return Long @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public Long
	 * updateEventAndCreateEligDeterm(Long idEvent, Long idLastUpdatePerson) {
	 * LOG.
	 * debug("Entering method updateEventAndCreateEligDeterm in EligibilityDeterminationService"
	 * );
	 * 
	 * try { ApplicationBackgroundDto applicationBackgroundDto =
	 * aaeApplBackgroundDao.selectAdptAsstEligAppFromEvent(idEvent);
	 * EventValueDto eventValueDto =
	 * eventUtilityService.fetchEventInfo(idEvent);
	 * 
	 * eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PEND);
	 * eventValueDto.setEventDescr(ServiceConstants.APPLICATION_PEND_EVENT_DESC)
	 * ; eventValueDto.setIdPerson(idLastUpdatePerson);
	 * applicationBackgroundService.callPostEvent(eventValueDto,
	 * ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.ZERO_VAL,
	 * applicationBackgroundDto.getIdAdptEligApplication()); Person personInfo =
	 * personDao.getPerson(applicationBackgroundDto.getIdPerson());
	 * 
	 * EligibilityDeterminationDto eligibilityDeterminationDto = new
	 * EligibilityDeterminationDto();
	 * eligibilityDeterminationDto.setIdAdptEligApplication(
	 * applicationBackgroundDto.getIdAdptEligApplication());
	 * eligibilityDeterminationDto.setNbrChildAgreementAge(ServiceConstants.
	 * ZERO_VAL);
	 * eligibilityDeterminationDto.setIdLastUpdatePerson(idLastUpdatePerson);
	 * eligibilityDeterminationDto.setIdCreatedPerson(idLastUpdatePerson);
	 * eligibilityDeterminationDto.setNbrChildFfyAge(DateUtils.
	 * getAgeAtCurrentFiscalYear(personInfo.getDtPersonBirth())); if
	 * (isChildApplicableAgeFiscalYr(personInfo.getDtPersonBirth())) {
	 * eligibilityDeterminationDto.setIndApplicableAge(ServiceConstants.Y); }
	 * else {
	 * eligibilityDeterminationDto.setIndApplicableAge(ServiceConstants.N); }
	 * createEligDetermination(eligibilityDeterminationDto);
	 * applicationBackgroundDto.setDtApplSubmitted(new Date());
	 * applicationBackgroundDto.setIdLastUpdatePerson(idLastUpdatePerson);
	 * aaeApplBackgroundDao.updateAdptAsstEligApplication(
	 * applicationBackgroundDto); } catch (DataNotFoundException e) {
	 * LOG.error(e.getMessage()); }
	 * 
	 * LOG.
	 * debug("Exiting method updateEventAndCreateEligDeterm in EligibilityDeterminationService"
	 * ); return idLastUpdatePerson; }
	 * 
	 *//**
		 * Method Name: isChildApplicableAgeFiscalYr Method Description:
		 * 
		 * @param dtOfBirth
		 * @return Boolean
		 */
	/*
	 * private Boolean isChildApplicableAgeFiscalYr(Date dtOfBirth) { Long
	 * nbrAgeAtCurrentFiscalYr = DateUtils.getAgeAtCurrentFiscalYear(dtOfBirth);
	 * Boolean isChildApplicable = ServiceConstants.FALSEVAL; Long
	 * currentFiscalYr = DateUtils.getCurrentFiscalYr(); if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_TEN && nbrAgeAtCurrentFiscalYr >=
	 * ServiceConstants.SIXTEEN) isChildApplicable = ServiceConstants.TRUEVAL;
	 * else if (currentFiscalYr == ServiceConstants.TWO_THOUSAND_ELEVAN &&
	 * nbrAgeAtCurrentFiscalYr >= ServiceConstants.FOURTEEN) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_TWELVE && nbrAgeAtCurrentFiscalYr >=
	 * ServiceConstants.TWELVE_MONTH_YEAR) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_THIRTEEN && nbrAgeAtCurrentFiscalYr >=
	 * ServiceConstants.TEN_VALUE_VAL) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_FOURTEEN && nbrAgeAtCurrentFiscalYr >=
	 * (long) ServiceConstants.NUM_EIGHT) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_FIFTEEN && nbrAgeAtCurrentFiscalYr >=
	 * (long) ServiceConstants.NUM_SIX) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr ==
	 * ServiceConstants.TWO_THOUSAND_SIXTEEN && nbrAgeAtCurrentFiscalYr >=
	 * ServiceConstants.FOUR_VAL) isChildApplicable = ServiceConstants.TRUEVAL;
	 * else if (currentFiscalYr == ServiceConstants.TWO_THOUSAND_SEVENTEEN &&
	 * nbrAgeAtCurrentFiscalYr >= ServiceConstants.TWO_VAL) isChildApplicable =
	 * ServiceConstants.TRUEVAL; else if (currentFiscalYr >
	 * ServiceConstants.TWO_THOUSAND_SEVENTEEN) isChildApplicable =
	 * ServiceConstants.TRUEVAL; return isChildApplicable; }
	 * 
	 *//**
		 * Method Name: createEligDetermination Method Description:
		 * 
		 * @param determDto
		 * @return Long
		 */
	/*
	 * private Long createEligDetermination(EligibilityDeterminationDto
	 * determDto) { Long idEligibilityDetermination = ServiceConstants.ZERO_VAL;
	 * try { idEligibilityDetermination =
	 * aaeEligDeterminationDao.insertAdptAsstEligDeterm(determDto); } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); } return
	 * idEligibilityDetermination; }
	 * 
	 *//**
		 * Method Name: saveEligDeterminationInfo Method Description:This method
		 * update Adoption Assistance Eligibility Determination and the event.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public Long
	 * saveEligDeterminationInfo(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
	 * LOG.
	 * debug("Entering method saveEligDeterminationInfo in EligibilityDeterminationService"
	 * ); Long updateResult = ServiceConstants.ZERO_VAL; try {
	 * EligibilityDeterminationDto eligibilityDeterminationDto =
	 * aaeApplAndDetermDB.getEligibilityDeterminationDto(); if
	 * (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.
	 * getDtAdptAsstAgreement()) &&
	 * !TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDB.getDtOfBirth())) {
	 * eligibilityDeterminationDto.setNbrChildAgreementAge(new Long(
	 * DateUtils.getAge(aaeApplAndDetermDB.getDtOfBirth(),
	 * eligibilityDeterminationDto.getDtAdptAsstAgreement()))); }
	 * saveEligDeterminationValueBean(eligibilityDeterminationDto);
	 * 
	 * EventValueDto eventValueDto = aaeApplAndDetermDB.getEventValueDto(); if
	 * (!TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDB.getIdLastUpdatedByPerson(
	 * ))) {
	 * eventValueDto.setIdPerson(aaeApplAndDetermDB.getIdLastUpdatedByPerson());
	 * }
	 * 
	 * ApplicationBackgroundDto applicationBackgroundDto =
	 * aaeApplAndDetermDB.getApplicationBackgroundDto(); if
	 * (isValid(applicationBackgroundDto.getCdWithdrawRsn())) {
	 * 
	 * applicationBackgroundDto.setIdLastUpdatePerson(aaeApplAndDetermDB.
	 * getIdLastUpdatedByPerson()); updateResult =
	 * aaeApplBackgroundDao.updateAdptAsstEligApplication(
	 * applicationBackgroundDto);
	 * applicationBackgroundService.withdrawFromAAEProcess(eventValueDto,
	 * ServiceConstants.REQ_FUNC_CD_UPDATE); completeEligDetermAssignedTodo(new
	 * Long(eventValueDto.getIdEvent())); } else {
	 * applicationBackgroundService.callPostEvent(eventValueDto,
	 * ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.ZERO_VAL,
	 * applicationBackgroundDto.getIdAdptEligApplication()); } } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); }
	 * 
	 * LOG.
	 * debug("Exiting method saveEligDeterminationInfo in EligibilityDeterminationService"
	 * ); return updateResult; }
	 * 
	 *//**
		 * Method Name: isValid Method Description:Checks to see if a given
		 * string is valid. This includes checking that the string is not null
		 * or empty.
		 * 
		 * @param value
		 * @return Boolean
		 */
	/*
	 * private Boolean isValid(String value) { if
	 * (TypeConvUtil.isNullOrEmpty(value)) { return ServiceConstants.FALSEVAL; }
	 * String trimmedString = value.trim(); return (trimmedString.length() >
	 * ServiceConstants.Zero); }
	 * 
	 *//**
		 * Method Name: saveEligDetermAndCompTodo Method Description:This method
		 * saves Adoption Assistance Determination and completes Adoption
		 * Assistance Eligibility Determination Todo as complete.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public Long
	 * saveEligDetermAndCompTodo(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
	 * LOG.
	 * debug("Entering method saveEligDetermAndCompTodo in EligibilityDeterminationService"
	 * ); Long updateResult = ServiceConstants.ZERO_VAL; updateResult =
	 * saveEligDeterminationInfo(aaeApplAndDetermDBDto);
	 * completeEligDetermAssignedTodo(new
	 * Long(aaeApplAndDetermDBDto.getEventValueDto().getIdEvent()));
	 * 
	 * LOG.
	 * debug("Exiting method saveEligDetermAndCompTodo in EligibilityDeterminationService"
	 * ); return updateResult; }
	 * 
	 *//**
		 * Method Name: saveEligDeterminationValueBean Method Description:This
		 * method saves AAE Determination value bean.
		 * 
		 * @param eligibilityDeterminationDto
		 * @return Long @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public Long
	 * saveEligDeterminationValueBean(EligibilityDeterminationDto
	 * eligibilityDeterminationDto) {
	 * 
	 * LOG.
	 * debug("Entering method saveEligDeterminationDto in EligibilityDeterminationService"
	 * ); Long idEligibilityDetermination = ServiceConstants.ZERO_VAL; try {
	 * idEligibilityDetermination = aaeEligDeterminationDao
	 * .updateAdptAsstEligDeterm(eligibilityDeterminationDto); } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); }
	 * 
	 * LOG.
	 * debug("Exiting method saveEligDeterminationDto in EligibilityDeterminationService"
	 * ); return idEligibilityDetermination; }
	 * 
	 *//**
		 * Method Name: determinePrelimDetermin Method Description:This method
		 * determines Preliminary Determination and the saves Adoption
		 * Assistance Eligibility Determination
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public Long
	 * determinePrelimDetermin(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
	 * LOG.
	 * debug("Entering method determinePrelimDetermin in EligibilityDeterminationService"
	 * ); Long updateResult = ServiceConstants.ZERO_VAL;
	 * EligibilityDeterminationDto eligibilityDeterminationDto =
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto();
	 * ApplicationBackgroundDto applicationBackgroundDto =
	 * aaeApplAndDetermDBDto.getApplicationBackgroundDto();
	 * AaeEligDetermMessgDto aaeEligQualMessgDto = applicationBackgroundService
	 * .getDetermQualMessages(aaeApplAndDetermDBDto);
	 * eligibilityDeterminationDto.setIdLastUpdatePerson(aaeApplAndDetermDBDto.
	 * getIdLastUpdatedByPerson());
	 * eligibilityDeterminationDto.setIdInitialDetermPerson(
	 * aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());
	 * eligibilityDeterminationDto.setDtInitialDeterm(new Date());
	 * 
	 * AaeEligDetermMessgDto aaeEligDetermMessgDto =
	 * calculatePrelimEligDeterm(aaeApplAndDetermDBDto, aaeEligQualMessgDto);
	 * 
	 * eligibilityDeterminationDto.setCdInitialDeterm(aaeEligDetermMessgDto.
	 * getCdDetermType());
	 * 
	 * eligibilityDeterminationDto.setIndSpecialNeedOutcm(toYorN(AaeUtils.
	 * specialNeedsExists(applicationBackgroundDto)));
	 * 
	 * eligibilityDeterminationDto.setIndCtznshpOutcm(toYorN(AaeUtils.
	 * doesChildsMeetCitizenshipReq(aaeApplAndDetermDBDto)));
	 * 
	 * eligibilityDeterminationDto.setIndRsnbleEffortOutcm(toYorN(AaeUtils.
	 * reasonableEffortsTaken(applicationBackgroundDto)));
	 * 
	 * eligibilityDeterminationDto.setIndAddtIVEReqOutcm(
	 * toYorN(AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligQualMessgDto)));
	 * 
	 * eligibilityDeterminationDto.setIndDfpsMngngCvsOutcm(toYorN(AaeUtils.
	 * isInCVS(applicationBackgroundDto)));
	 * 
	 * eligibilityDeterminationDto.setIndPlcmntReqMetOutcm(null);
	 * eligibilityDeterminationDto.setIndAdptAgreementOutcm(null); updateResult
	 * = saveEligDeterminationValueBean(eligibilityDeterminationDto);
	 * 
	 * LOG.
	 * debug("Exiting method determinePrelimDetermin in EligibilityDeterminationService"
	 * ); return updateResult; }
	 * 
	 *//**
		 * Method Name: toYorN Method Description:
		 * 
		 * @param bool
		 * @return String
		 */
	/*
	 * private String toYorN(Boolean bool) { if (bool == null) { return
	 * ServiceConstants.EMPTY_STRING; } return Boolean.TRUE.equals(bool) ?
	 * ServiceConstants.Y : ServiceConstants.N; }
	 * 
	 *//**
		 * Method Name: calculatePrelimEligDeterm Method Description:This method
		 * returns preliminary eligibility determination value in form of
		 * AaeEligDetermMessgDto
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @param aaeEligDetermMessgDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public AaeEligDetermMessgDto
	 * calculatePrelimEligDeterm(AaeApplAndDetermDBDto aaeApplAndDetermDBDto,
	 * AaeEligDetermMessgDto aaeEligDetermMessgDto) { LOG.
	 * debug("Entering method calculatePrelimEligDeterm in EligibilityDeterminationService"
	 * );
	 * 
	 * AaeEligDetermMessgDto eligDetermMessgDto = new AaeEligDetermMessgDto();
	 * 
	 * EligibilityDeterminationDto determDto =
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto(); if
	 * (ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID ==
	 * aaeEligDetermMessgDto.getTypeOfElig()) { if
	 * (ServiceConstants.Y.equals(determDto.getIndApplicableChildOutcm())) { if
	 * (AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligDetermMessgDto)) {
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_010); } else
	 * { eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_020); } }
	 * else { if (AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligDetermMessgDto)) {
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_010); } else
	 * { eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_020); } }
	 * } else if (ServiceConstants.ELIG_TYPE_IVE_PAID ==
	 * aaeEligDetermMessgDto.getTypeOfElig()) { if
	 * (ServiceConstants.Y.equals(determDto.getIndApplicableChildOutcm())) { if
	 * (AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligDetermMessgDto)) {
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_010); } else
	 * { eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_040); } }
	 * else { if (AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligDetermMessgDto)) {
	 * 
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_010); } else
	 * {
	 * 
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_040); } } }
	 * else if (ServiceConstants.ELIG_TYPE_NON_RECUR ==
	 * aaeEligDetermMessgDto.getTypeOfElig()) {
	 * 
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_010); } else
	 * if (ServiceConstants.ELIG_TYPE_STATE_PAID ==
	 * aaeEligDetermMessgDto.getTypeOfElig()) {
	 * 
	 * eligDetermMessgDto.setCdDetermType(ServiceConstants.CELIGIBI_020); }
	 * 
	 * eligDetermMessgDto.setTypeOfElig(aaeEligDetermMessgDto.getTypeOfElig());
	 * 
	 * LOG.
	 * debug("Exiting method calculatePrelimEligDeterm in EligibilityDeterminationService"
	 * ); return eligDetermMessgDto; }
	 *//**
		 * Method Name: determineFinalDetermin Method Description:This method
		 * determines Final Determination and the saves AAE Determination and
		 * returns the determination message Dto
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public AaeEligDetermMessgDto
	 * determineFinalDetermin(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
	 * LOG.
	 * debug("Entering method determineFinalDetermin in EligibilityDeterminationService"
	 * );
	 * 
	 * AaeEligDetermMessgDto aaeEligDetermMessgDto = new
	 * AaeEligDetermMessgDto();
	 * 
	 * EligibilityDeterminationDto eligibilityDeterminationDto =
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto();
	 * ApplicationBackgroundDto applicationBackgroundDto =
	 * aaeApplAndDetermDBDto.getApplicationBackgroundDto(); Date
	 * dtAdptConsummation = null; if
	 * (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto)) {
	 * dtAdptConsummation =
	 * findLatestAdopConsummationDt(aaeApplAndDetermDBDto.getIdPerson(),
	 * eligibilityDeterminationDto.getDtAdptPlcmtStart()); }
	 * eligibilityDeterminationDto.setDtAdoptionConsummated(dtAdptConsummation);
	 * Boolean agrmntOnOrBeforeConsummation =
	 * isAdptAgrmntSignedBeforeConsumation(eligibilityDeterminationDto,
	 * dtAdptConsummation);
	 * eligibilityDeterminationDto.setIdLastUpdatePerson(aaeApplAndDetermDBDto.
	 * getIdLastUpdatedByPerson());
	 * eligibilityDeterminationDto.setIdFinalDetermPerson(aaeApplAndDetermDBDto.
	 * getIdLastUpdatedByPerson()); aaeEligDetermMessgDto =
	 * calculateFinalEligDeterm(aaeApplAndDetermDBDto); if
	 * (aaeEligDetermMessgDto.getChildFinalDetermDisqualified()) {
	 * eligibilityDeterminationDto.setIndAsstDisqualified(ServiceConstants.Y); }
	 * else {
	 * eligibilityDeterminationDto.setIndAsstDisqualified(ServiceConstants.N); }
	 * eligibilityDeterminationDto.setCdFinalDeterm(aaeEligDetermMessgDto.
	 * getCdDetermType());
	 * eligibilityDeterminationDto.setIndSpecialNeedOutcm(toYorN(AaeUtils.
	 * specialNeedsExists(applicationBackgroundDto)));
	 * eligibilityDeterminationDto.setIndCtznshpOutcm(toYorN(AaeUtils.
	 * doesChildsMeetCitizenshipReq(aaeApplAndDetermDBDto)));
	 * eligibilityDeterminationDto.setIndRsnbleEffortOutcm(toYorN(AaeUtils.
	 * reasonableEffortsTaken(applicationBackgroundDto)));
	 * eligibilityDeterminationDto.setIndAddtIVEReqOutcm(
	 * toYorN(AaeUtils.doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligDetermMessgDto)));
	 * eligibilityDeterminationDto.setIndDfpsMngngCvsOutcm(toYorN(AaeUtils.
	 * isInCVS(applicationBackgroundDto))); if
	 * (DateUtils.isNull(eligibilityDeterminationDto.getDtAdptPlcmtStart())) {
	 * eligibilityDeterminationDto.setIndPlcmntReqMetOutcm(ServiceConstants.N);
	 * } else {
	 * eligibilityDeterminationDto.setIndPlcmntReqMetOutcm(ServiceConstants.Y);
	 * } eligibilityDeterminationDto.setIndAdptAgreementOutcm(toYorN(
	 * agrmntOnOrBeforeConsummation));
	 * eligibilityDeterminationDto.setIdAdoPlcmtEvent(
	 * fetchIdEventOflatestAdoptivePlacement(aaeApplAndDetermDBDto.getIdPerson()
	 * )); saveEligDeterminationValueBean(eligibilityDeterminationDto);
	 * EventValueDto eventDto = aaeApplAndDetermDBDto.getEventValueDto();
	 * eventDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
	 * eventDto.setEventDescr(ServiceConstants.APPLICATION_COMP_EVENT_DESC); if
	 * (TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDBDto.
	 * getIdLastUpdatedByPerson())) {
	 * eventDto.setIdPerson(aaeApplAndDetermDBDto.getIdLastUpdatedByPerson()); }
	 * 
	 * applicationBackgroundService.callPostEvent(eventDto,
	 * ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.ZERO_VAL,
	 * applicationBackgroundDto.getIdAdptEligApplication());
	 * 
	 * LOG.
	 * debug("Exiting method determineFinalDetermin in EligibilityDeterminationService"
	 * ); return aaeEligDetermMessgDto; }
	 *//**
		 * Method Name: calculateFinalEligDeterm Method Description:This method
		 * returns final eligibility determination value in form of
		 * AaeEligDetermMessgValueBean.This will perform the
		 * calculatePrelimEligDeterm eligibility determination and based in that
		 * will perform the final eligibility determination.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public AaeEligDetermMessgDto
	 * calculateFinalEligDeterm(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
	 * LOG.
	 * debug("Entering method calculateFinalEligDeterm in EligibilityDeterminationService"
	 * );
	 * 
	 * AaeEligDetermMessgDto aaeEligDetermMessgDto = new
	 * AaeEligDetermMessgDto(); List<Long> eligDetermMessageList = new
	 * ArrayList<>();
	 * 
	 * if (ServiceConstants.Y.equals(aaeApplAndDetermDBDto.
	 * getEligibilityDeterminationDto().getIndChildQualify())) {
	 * 
	 * EligibilityDeterminationDto determDto =
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto();
	 * 
	 * Date dtAdptConsummation = null; if
	 * (!TypeConvUtil.isNullOrEmpty(determDto)) { dtAdptConsummation =
	 * findLatestAdopConsummationDt(aaeApplAndDetermDBDto.getIdPerson(),
	 * determDto.getDtAdptPlcmtStart()); } Boolean agrmntOnOrBeforeConsummation
	 * = isAdptAgrmntSignedBeforeConsumation(determDto, dtAdptConsummation);
	 * AaeEligDetermMessgDto aaeEligQualMessgDto = applicationBackgroundService
	 * .getDetermQualMessages(aaeApplAndDetermDBDto); aaeEligDetermMessgDto =
	 * calculatePrelimEligDeterm(aaeApplAndDetermDBDto, aaeEligQualMessgDto); if
	 * (ServiceConstants.ELIG_TYPE_NON_RECUR ==
	 * aaeEligQualMessgDto.getTypeOfElig()) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_NR); } else
	 * if (ServiceConstants.CELIGIBI_010.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) { if (agrmntOnOrBeforeConsummation) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_IVE_NR_MTHLY)
	 * ; } else {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_IVE_NR_MTHLY)
	 * ; eligDetermMessageList.add(ServiceConstants.
	 * MSG_AA_CHLD_DISQL_ADPT_SIGN_LATER); if (ServiceConstants.Y
	 * .equals(aaeApplAndDetermDBDto.getApplicationBackgroundDto().
	 * getIndFairHearing())) { eligDetermMessageList.add(ServiceConstants.
	 * MSG_PCA_AA_FAIRHEARING_WORKSHEET); }
	 * aaeEligDetermMessgDto.setChildFinalDetermDisqualified(true); } } else if
	 * (ServiceConstants.CELIGIBI_020.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) { if (agrmntOnOrBeforeConsummation) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_IVE_MTHLY
	 * );
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_ST_NR_MTHLY);
	 * } else {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_ST_NR_MTHLY);
	 * eligDetermMessageList.add(ServiceConstants.
	 * MSG_AA_CHLD_DISQL_ADPT_SIGN_LATER); if (ServiceConstants.Y
	 * .equals(aaeApplAndDetermDBDto.getApplicationBackgroundDto().
	 * getIndFairHearing())) { eligDetermMessageList.add(ServiceConstants.
	 * MSG_PCA_AA_FAIRHEARING_WORKSHEET); }
	 * aaeEligDetermMessgDto.setChildFinalDetermDisqualified(true); } } else if
	 * (ServiceConstants.CELIGIBI_040.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_IVE_MTHLY
	 * ); if
	 * (AaeUtils.isCVSInLCPA(aaeApplAndDetermDBDto.getApplicationBackgroundDto()
	 * ) && AaeUtils .doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligQualMessgDto) == false) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_ST_MTHLY)
	 * ; } } } else {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT); }
	 * aaeEligDetermMessgDto.setEligMessages(eligDetermMessageList);
	 * 
	 * LOG.
	 * debug("Exiting method calculateFinalEligDeterm in EligibilityDeterminationService"
	 * ); return aaeEligDetermMessgDto; }
	 * 
	 *//**
		 * Method Name: isAdptAgrmntSignedBeforeConsumation Method
		 * Description:This method returns if the Adoption Assist Agreement was
		 * signed before consummation date
		 * 
		 * @param determDto
		 * @param dtAdptConsummation
		 * @return Boolean
		 */
	/*
	 * private Boolean
	 * isAdptAgrmntSignedBeforeConsumation(EligibilityDeterminationDto
	 * determDto, Date dtAdptConsummation) {
	 * 
	 * Boolean agrmntOnOrBeforeConsummation = ServiceConstants.TRUEVAL; if
	 * (!DateUtils.isNull(dtAdptConsummation) &&
	 * !DateUtils.isNull(determDto.getDtAdptAsstAgreement())) { if
	 * (DateUtils.isAfter(determDto.getDtAdptAsstAgreement(),
	 * dtAdptConsummation)) { agrmntOnOrBeforeConsummation =
	 * ServiceConstants.FALSEVAL; } } else if
	 * (DateUtils.isNull(dtAdptConsummation) &&
	 * DateUtils.isNull(determDto.getDtAdptAsstAgreement())) {
	 * agrmntOnOrBeforeConsummation = ServiceConstants.FALSEVAL; } return
	 * agrmntOnOrBeforeConsummation; }
	 * 
	 *//**
		 * Method Name: fetchFinalEligDetermOutcome Method Description:This
		 * method returns the Final Eligibility determination Messages in form
		 * of AaeEligDetermMessgValueBean. Is used for display only.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public AaeEligDetermMessgDto
	 * fetchFinalEligDetermOutcome(AaeApplAndDetermDBDto aaeApplAndDetermDBDto)
	 * { LOG.
	 * debug("Entering method fetchFinalEligDetermOutcome in EligibilityDeterminationService"
	 * );
	 * 
	 * AaeEligDetermMessgDto aaeEligDetermMessgDto = new
	 * AaeEligDetermMessgDto(); List<Long> eligDetermMessageList = new
	 * ArrayList<>();
	 * 
	 * if (ServiceConstants.Y.equals(aaeApplAndDetermDBDto.
	 * getEligibilityDeterminationDto().getIndChildQualify())) {
	 * EligibilityDeterminationDto determDto =
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto();
	 * 
	 * Date dtAdptConsummation = determDto.getDtAdoptionConsummated(); Boolean
	 * agrmntOnOrBeforeConsummation =
	 * isAdptAgrmntSignedBeforeConsumation(determDto, dtAdptConsummation);
	 * AaeEligDetermMessgDto aaeEligQualMessgDto = applicationBackgroundService
	 * .getDetermQualMessages(aaeApplAndDetermDBDto);
	 * aaeEligDetermMessgDto.setTypeOfElig(aaeEligQualMessgDto.getTypeOfElig());
	 * if (isValid(determDto.getCdFinalDeterm())) {
	 * aaeEligDetermMessgDto.setCdDetermType(determDto.getCdFinalDeterm()); } if
	 * (ServiceConstants.ELIG_TYPE_NON_RECUR ==
	 * aaeEligQualMessgDto.getTypeOfElig()) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_NR); } else
	 * if (ServiceConstants.CELIGIBI_010.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) { if (agrmntOnOrBeforeConsummation) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_IVE_NR_MTHLY)
	 * ; } else {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_IVE_NR_MTHLY)
	 * ; eligDetermMessageList.add(ServiceConstants.
	 * MSG_AA_CHLD_DISQL_ADPT_SIGN_LATER);
	 * 
	 * if (ServiceConstants.Y
	 * .equals(aaeApplAndDetermDBDto.getApplicationBackgroundDto().
	 * getIndFairHearing())) { eligDetermMessageList.add(ServiceConstants.
	 * MSG_PCA_AA_FAIRHEARING_WORKSHEET); }
	 * 
	 * aaeEligDetermMessgDto.setChildFinalDetermDisqualified(true); } } else if
	 * (ServiceConstants.CELIGIBI_020.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) { if (agrmntOnOrBeforeConsummation) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_IVE_MTHLY
	 * );
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_ST_NR_MTHLY);
	 * } else {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_ELIG_ST_NR_MTHLY);
	 * eligDetermMessageList.add(ServiceConstants.
	 * MSG_AA_CHLD_DISQL_ADPT_SIGN_LATER);
	 * 
	 * if (ServiceConstants.Y
	 * .equals(aaeApplAndDetermDBDto.getApplicationBackgroundDto().
	 * getIndFairHearing())) { eligDetermMessageList.add(ServiceConstants.
	 * MSG_PCA_AA_FAIRHEARING_WORKSHEET); }
	 * 
	 * aaeEligDetermMessgDto.setChildFinalDetermDisqualified(true); } } else if
	 * (ServiceConstants.CELIGIBI_040.equals(aaeEligDetermMessgDto.
	 * getCdDetermType())) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_IVE_MTHLY
	 * );
	 * 
	 * if
	 * (AaeUtils.isCVSInLCPA(aaeApplAndDetermDBDto.getApplicationBackgroundDto()
	 * ) && AaeUtils .doesChildMeetsAdditionalIVEReq(aaeApplAndDetermDBDto,
	 * aaeEligQualMessgDto) == false) {
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_ELIG_ST_MTHLY)
	 * ; } } } else {
	 * 
	 * eligDetermMessageList.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT); }
	 * aaeEligDetermMessgDto.setEligMessages(eligDetermMessageList);
	 * 
	 * LOG.
	 * debug("Exiting method fetchFinalEligDetermOutcome in EligibilityDeterminationService"
	 * ); return aaeEligDetermMessgDto; }
	 * 
	 *//**
		 * Method Name: findLatestAdopConsummationDt Method Description:
		 * 
		 * @param idChildPerson
		 * @param dtAdptPlcmtStart
		 * @return Date
		 */
	/*
	 * private Date findLatestAdopConsummationDt(Long idChildPerson, Date
	 * dtAdptPlcmtStart) { Date dtAdptConsummated = null; if (null !=
	 * dtAdptPlcmtStart) {
	 * 
	 * try { List<LegalStatusDto> legalStatusList =
	 * legalStatusInfoDao.fetchLegalStatusListForChild(idChildPerson); for
	 * (LegalStatusDto legalStatusDto : legalStatusList) { if
	 * (!TypeConvUtil.isNullOrEmpty(legalStatusDto.getCdLegalStatStatus()) &&
	 * ServiceConstants.CLEGSTAT_090.equals(legalStatusDto.getCdLegalStatStatus(
	 * ))) { dtAdptConsummated = legalStatusDto.getDtLegalStatStatusDt(); break;
	 * } }
	 * 
	 * if ((!TypeConvUtil.isNullOrEmpty(dtAdptConsummated)) &&
	 * (DateUtils.isBefore(dtAdptConsummated, dtAdptPlcmtStart))) {
	 * dtAdptConsummated = null; } } catch (DataNotFoundException e) {
	 * LOG.error(e.getMessage()); } } return dtAdptConsummated; }
	 * 
	 *//**
		 * Method Name: fetchResourceOflatestAdoptivePlacement Method
		 * Description:
		 * 
		 * @param idPerson
		 * @return Date
		 */
	/*
	 * private Long fetchResourceOflatestAdoptivePlacement(Long idPerson) {
	 * 
	 * Long idResource = ServiceConstants.ZERO_VAL; try {
	 * List<PlacementValueDto> latestPlacementList =
	 * placementDao.findActivePlacements(idPerson); idResource =
	 * ServiceConstants.ZERO_VAL; for (PlacementValueDto placementValueDto :
	 * latestPlacementList) { if
	 * (!TypeConvUtil.isNullOrEmpty((placementValueDto)) &&
	 * !TypeConvUtil.isNullOrEmpty(placementValueDto.getCdPlcmtLivArr()) &&
	 * (ServiceConstants.CLAPRSFA_GT.equals(placementValueDto.getCdPlcmtLivArr()
	 * ) ||
	 * ServiceConstants.CPLCMT_71.equals(placementValueDto.getCdPlcmtLivArr())))
	 * { idResource = placementValueDto.getIdRsrcFacil(); break; } } } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); } return
	 * idResource; }
	 * 
	 * private Date fetchAdoptivePlacementStartDate(Long idPerson) {
	 * 
	 * Date dtPlcmtStart = null; try { List<PlacementValueDto>
	 * latestPlacementList = placementDao.findActivePlacements(idPerson);
	 * dtPlcmtStart = new Date(); for (PlacementValueDto placementValueDto :
	 * latestPlacementList) { if
	 * (!TypeConvUtil.isNullOrEmpty((placementValueDto)) &&
	 * !TypeConvUtil.isNullOrEmpty(placementValueDto.getCdPlcmtLivArr()) &&
	 * (ServiceConstants.CLAPRSFA_GT.equals(placementValueDto.getCdPlcmtLivArr()
	 * ) ||
	 * ServiceConstants.CPLCMT_71.equals(placementValueDto.getCdPlcmtLivArr())))
	 * { dtPlcmtStart = placementValueDto.getDtPlcmtStart(); break; } else {
	 * dtPlcmtStart = null; } } } catch (DataNotFoundException e) {
	 * LOG.error(e.getMessage()); } return dtPlcmtStart; }
	 * 
	 *//**
		 * Method Name: fetchIdEventOflatestAdoptivePlacement Method
		 * Description:Fetches the idPlacementEvent of the latest adoptive
		 * placement if it exists
		 * 
		 * @param idPerson
		 * @return Date
		 */
	/*
	 * private Long fetchIdEventOflatestAdoptivePlacement(Long idPerson) { Long
	 * idPlacementEvent = ServiceConstants.ZERO_VAL; try {
	 * List<PlacementValueDto> latestPlacementList =
	 * placementDao.findActivePlacements(idPerson); idPlacementEvent =
	 * ServiceConstants.ZERO_VAL; for (PlacementValueDto placementValueDto :
	 * latestPlacementList) { if (!TypeConvUtil.isNullOrEmpty(placementValueDto)
	 * && !TypeConvUtil.isNullOrEmpty(placementValueDto.getIdPlcmtEvent()) &&
	 * (ServiceConstants.CLAPRSFA_GT.equals(placementValueDto.getCdPlcmtLivArr()
	 * ) ||
	 * ServiceConstants.CPLCMT_71.equals(placementValueDto.getCdPlcmtLivArr())))
	 * { idPlacementEvent = placementValueDto.getIdPlcmtEvent(); break; } } }
	 * catch (DataNotFoundException e) { LOG.error(e.getMessage()); } return
	 * idPlacementEvent; }
	 *//**
		 * Method Name: validateSiblAppl Method Description:This method
		 * determines if the sibling selected is an applicable child or not.
		 * 
		 * @param idSiblingApplPerson
		 * @param idStage
		 * @return List<Long> @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public List<Long>
	 * validateSiblAppl(Long idSiblingApplPerson, Long idStage) { LOG.
	 * debug("Entering method validateSiblAppl in EligibilityDeterminationService"
	 * );
	 * 
	 * List<Long> messageList = new ArrayList<>(); try {
	 * 
	 * Long idAdptEligApplication =
	 * aaeApplBackgroundDao.selectLatestAdptAsstEligAppl(idSiblingApplPerson);
	 * if (idAdptEligApplication != ServiceConstants.Zero_Value) {
	 * 
	 * EligibilityDeterminationDto lastEligDetermDto = aaeEligDeterminationDao
	 * .selectAdptAsstEligDetermForAppl(idAdptEligApplication); if
	 * (ServiceConstants.N.equals(lastEligDetermDto.getIndApplicableChildOutcm()
	 * )) { messageList.add(ServiceConstants.MSG_AA_SIBL_CHLD_NOT_APPLC_IVE); }
	 * } } catch (DataNotFoundException e) { LOG.error(e.getMessage()); }
	 * 
	 * LOG.
	 * debug("Exiting method validateSiblAppl in EligibilityDeterminationService"
	 * ); return messageList; }
	 * 
	 *//**
		 * Method Name: validateFinalEligDetem Method Description:This method
		 * checks for validation errors. 1. Checks if the Date of consummation
		 * exists 2. Active ADO placement exists 3. Sibling is placed in same
		 * resource as the child
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return List<Long> @
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public List<Long>
	 * validateFinalEligDetem(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
	 * LOG.
	 * debug("Entering method validateFinalEligDetem in EligibilityDeterminationService"
	 * );
	 * 
	 * List<Long> messageList = new ArrayList<>();
	 * 
	 * AaeEligDetermMessgDto aaeEligQualMessgDto = applicationBackgroundService
	 * .getDetermQualMessages(aaeApplAndDetermDBDto); AaeEligDetermMessgDto
	 * eligDetermDto = calculatePrelimEligDeterm(aaeApplAndDetermDBDto,
	 * aaeEligQualMessgDto); if
	 * (ServiceConstants.CELIGIBI_040.equals(eligDetermDto.getCdDetermType()) ==
	 * ServiceConstants.FALSEVAL) { Long idResourceOfChildPlacement =
	 * fetchResourceOflatestAdoptivePlacement(
	 * aaeApplAndDetermDBDto.getIdPerson()); if (idResourceOfChildPlacement ==
	 * ServiceConstants.ZERO_VAL) {
	 * messageList.add(ServiceConstants.MSG_NO_ACTV_PLCMT_EXISTS); } else { if
	 * (aaeApplAndDetermDBDto.getEligibilityDeterminationDto().
	 * getIdDetermSibPerson() != 0 && ServiceConstants.Y
	 * .equals(aaeApplAndDetermDBDto.getEligibilityDeterminationDto().
	 * getIndWithSibling())) { Long idResourceOfSibChildPlacement =
	 * fetchResourceOflatestAdoptivePlacement(
	 * aaeApplAndDetermDBDto.getEligibilityDeterminationDto().
	 * getIdDetermSibPerson()); if (idResourceOfChildPlacement !=
	 * idResourceOfSibChildPlacement) {
	 * messageList.add(ServiceConstants.MSG_SIB_AND_CHLD_NT_IN_SAME_RES); } } }
	 * }
	 * 
	 * LOG.
	 * debug("Exiting method validateFinalEligDetem in EligibilityDeterminationService"
	 * ); return messageList; }
	 * 
	 *//**
		 * Method Name: completeEligDetermAssignedTodo Method Description:This
		 * method completes Adoption Assistance Eligibility Determination Todo
		 * if any
		 * 
		 * @param idEvent
		 * @return Long @
		 *//*
		 * @Override
		 * 
		 * @Transactional(readOnly = false, isolation =
		 * Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED) public
		 * Long completeEligDetermAssignedTodo(Long idEvent) { LOG.
		 * debug("Entering method completeEligDetermAssignedTodo in EligibilityDeterminationService"
		 * ); Long updateResult = ServiceConstants.ZERO_VAL; try { updateResult
		 * = ServiceConstants.ZERO_VAL; UpdateToDoDto updateToDoDto = new
		 * UpdateToDoDto(); updateToDoDto.setIdEvent(idEvent); updateResult =
		 * updateToDoDao.completeTodo(updateToDoDto); } catch
		 * (DataNotFoundException e) { LOG.error(e.getMessage()); }
		 * 
		 * LOG.
		 * debug("Exiting method completeEligDetermAssignedTodo in EligibilityDeterminationService"
		 * ); return updateResult; }
		 */
}
