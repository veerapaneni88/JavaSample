package us.tx.state.dfps.service.fce.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.ZipCountyChkDao;
import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.request.FceReq;
import us.tx.state.dfps.service.common.request.RetrvPersonIdentifiersReq;
import us.tx.state.dfps.service.common.request.SaveFceApplicationReq;
import us.tx.state.dfps.service.common.request.TexasZipCountyValidateReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.GetFceApplicationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.ApplicationReasonsNotEligibleUtil;
import us.tx.state.dfps.service.common.utils.EligibilityUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.common.utils.ReviewUtils;
import us.tx.state.dfps.service.domiciledeprivation.dto.SelectPersonDomicileDto;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceDomicilePersonWelfDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.dto.FceReviewDto;
import us.tx.state.dfps.service.fce.service.DepCareDeductionService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.fostercarereview.dao.FceIncomeDao;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * class has the methods that initialize FCE (Foster Care Eligibility)
 * application, eligibility and review. Mar 15, 2018- 12:06:16 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class FceServiceImpl implements FceService {

	private static final String MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE = "MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE";
	private static final String MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE = "MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE";

	private static final Logger log = Logger.getLogger(FceServiceImpl.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private FceEligibilityDao fceEligibilityDao;

	@Autowired
	private EligibilityUtil eligibilityUtil;

	@Autowired
	private ReviewUtils reviewUtil;

	@Autowired
	private EventUtil eventUtil;

	@Autowired
	private FceDao fceDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private FcePersonDao fcePersonDao;

	@Autowired
	private PlacementDao placementDao;

	@Autowired
	private ApplicationReasonsNotEligibleUtil applicationReasonsNotEligibleUtil;

	@Autowired
	private ZipCountyChkDao zipCountyChkDao;

	@Autowired
	private DepCareDeductionService depCareDeductionService;

	@Autowired
	FceIncomeDao fceIncomeDao;

	@Autowired
	IncomeExpendituresDao incomeExpendituresDao;

	@Autowired
	PersonIdDao personIdDao;
	
	@Autowired
	CodesDao codesDao;

	@Autowired
	ServicePackageDao servicePackageDao;

	private static final String[] ignoreProperties = { "txtProofAgeSentEs", "nbrCourtYear", "indIncomeAssistance",
			"indNotifiedDhsWorker", "nmNotifiedDhsWrkrFirst", "nmNotifiedDhsWrkrMiddle", "nmNotifiedDhsWrkrLast",
			"nbrNotifiedDhsWrkrPhn", "dtNotifiedWorker", "txtNoIncomeExplanation", "txtIncomeDtrmntnComments",
			"indProofAgeSentEs", "dtProofAgeSentEs", "txtProofAgeSentEs", "indProofCitizenshipSentEs",
			"dtProofCitizenshipSentEs", "txtProofCitizenshipSentEs", "indLegalDocsSentEs", "txtLegalDocsSentEs",
			"indChildSupportOrder" };

	@Override
	public Boolean hasOpenFosterCareEligibility(Long idPerson) {
		log.debug("hasOpenFosterCareEligibility - begin");
		return fceEligibilityDao.hasOpenFosterCareEligibility(idPerson);

	}

	@Override
	public Long getOpenFceEventForStage(Long idStage) {
		log.debug("getOpenFceEventForStage - begin");
		return fceEligibilityDao.getOpenFceEventForStage(idStage);
	}

	@Override
	public EligibilityDto fetchLatestEligibility(Long idPerson) {
		log.debug("fetchLatestEligibility - begin");
		EligibilityDto dto = new EligibilityDto();
		Eligibility eligibility = fceEligibilityDao.fetchLatestEligibility(idPerson);
		if (eligibility != null) {
			dto.setIdEligEvent(eligibility.getIdEligEvent());
			dto.setCdEligActual(eligibility.getCdEligActual());
		}
		log.debug("fetchLatestEligibility - end");
		return dto;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public List<EligibilityDto> fetchActiveFceList(Long idPerson) {
		log.debug("fetchActiveFceList - begin");
		List<EligibilityDto> eligibilityDtos = new ArrayList<>();
		List<Eligibility> eligibilityList = fceEligibilityDao.fetchActiveFceList(idPerson);
		for (Eligibility eligibility : eligibilityList) {
			EligibilityDto dto = new EligibilityDto();
			dto.setIdEligEvent(eligibility.getIdEligEvent());
			dto.setIdPerson(eligibility.getPersonByIdPerson().getIdPerson());
			dto.setCdEligSelected(eligibility.getCdEligSelected());
			dto.setDtEligStart(eligibility.getDtEligStart());
			dto.setDtEligEnd(eligibility.getDtEligEnd());
			dto.setIdCase(eligibility.getIdCase());
			dto.setDtLastUpdate(eligibility.getDtLastUpdate());
			eligibilityDtos.add(dto);
		}
		log.debug("fetchLatestEligibility - end");
		return eligibilityDtos;
	}

	/**
	 * This service is to get the Fce application information for App/Background
	 * page display
	 * 
	 * @param fceReq
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public GetFceApplicationRes getFceApplication(FceReq fceReq) {

		FceContextDto fceContextDto = this.initializeFceApplication(fceReq.getIdStage(), fceReq.getIdappevent(),
				fceReq.getIdlastupdatePerson());
		GetFceApplicationRes fceApplicationRes = new GetFceApplicationRes();
		BeanUtils.copyProperties(fceContextDto, fceApplicationRes);
		// adding for create new Using
		fceApplicationRes.setFceApplicationDto(fceContextDto.getFceApplicationDto());
		fceApplicationRes.setFceEligibilityDto(fceContextDto.getFceEligibilityDto());
		Map<Long, FcePersonDto> persons = fcePersonDao
				.getFcePersonDtosbyEligibilityId(fceContextDto.getIdFceEligibility());
		fceApplicationRes.setPersons(persons);
		RetrvPersonIdentifiersReq retrvPersonIdentifiersReq = new RetrvPersonIdentifiersReq();
		retrvPersonIdentifiersReq.setIdPerson(fceApplicationRes.getFceApplicationDto().getIdPerson());
		retrvPersonIdentifiersReq.setIdType(CodesConstant.CNUMTYPE_MEDICAID_NUMBER);
		List<PersonIdentifiersDto> personIdentifiersDtlsList = personIdDao
				.getPersonIdentifierByIdType(retrvPersonIdentifiersReq);
		if (!ObjectUtils.isEmpty(personIdentifiersDtlsList)) {
			PersonIdentifiersDto personIdentifiersDto = personIdentifiersDtlsList.stream().findFirst().orElse(null);
			fceApplicationRes.getFceApplicationDto().setNbrMedicaid(personIdentifiersDto.getPersonIdNumber());
		}
		List<PlacementDto> palcements = placementDao.findRecentPlacements(fceReq.getIdStage());
		fceApplicationRes.setPlacements(palcements);

		return fceApplicationRes;
	}

	/**
	 * 
	 * Method Name: initializeFceApplication Method Description: This service is
	 * used for Initializes FCE Application
	 * 
	 * @param idStage
	 * @param idAppEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto @
	 */
	@Override
	public FceContextDto initializeFceApplication(Long idStage, Long idApplicationEvent, Long idLastUpdatePerson) {

		log.debug("initializing Fce Application - begin");

		Long idFceEligibility = null;
		Long idPerson = null;
		Long idFcePerson = null;
		Long idFceApplication = null;

		FceContextDto fceContext = new FceContextDto();
		FceEligibilityDto fceEligibilityDto = null;
		EventDto eventDto = null;
		String cdEventStatus = null;
		FceApplicationDto fceApplicationDto = null;

		Long idChild = stageDao.findPrimaryChildForStage(idStage);

		eventDto = findOrCreateEvent(ServiceConstants.FCE_APPLICATION_EVENT_TYPE, idApplicationEvent,
				idLastUpdatePerson, idStage, idChild);

		idApplicationEvent = eventDto.getIdEvent();
		Date eventDate = eventDto.getDtEventCreated();
		cdEventStatus = eventDto.getCdEventStatus();
		fceApplicationDto = fceDao.findApplicationByApplicationEvent(idApplicationEvent);

		if (ObjectUtils.isEmpty(fceApplicationDto)) {
			/**
			 * create "semaphore" to prevent multiple fce_application records
			 * from being created; this is possible when an id_event for the
			 * application has been created, but the fce_application records
			 * haven't (double click on event created by Stage progression from
			 * INV to SUB)
			 */
			/*
			 * fceApplicationDao.createSemaphore(idApplicationEvent,
			 * ServiceConstants.FCE_APPLICATION_TABLE,
			 * ServiceConstants.FCE_APPLICATION_COLUMN);
			 */
			fceApplicationDto = createFcApplication(ServiceConstants.CFCEAPRE_A, eventDto.getIdCase(),
					idApplicationEvent, idLastUpdatePerson, idStage, idChild);
		}
		if (!ObjectUtils.isEmpty(fceApplicationDto)) {
			idFceApplication = fceApplicationDto.getIdFceApplication();
			idFceEligibility = fceApplicationDto.getIdFceEligibility();
			// set event Date
			fceApplicationDto.setEventDate(eventDate);

			fceEligibilityDto = fceDao.getFceEligibility(idFceEligibility);
			idFcePerson = fceEligibilityDto.getIdFcePerson();
			idPerson = fceEligibilityDto.getIdPerson();
			/**
			 * depending on eventStatus, sync application/person state with
			 * other sources of data in the database
			 */
			if (CodesConstant.CEVTSTAT_NEW.equals(cdEventStatus)) {
				PersonDto personDto = personDao.getPersonById(idPerson);
				updateAddress(fceApplicationDto, personDto);
				fceApplicationDto.setCdState(ServiceConstants.CSTATE_TX);
			}

			if (!CodesConstant.CEVTSTAT_APRV.equals(cdEventStatus)
					&& !CodesConstant.CEVTSTAT_COMP.equals(cdEventStatus)) {
				syncFceApplicationStatus(fceEligibilityDto);
			}

			/**
			 * if in Complete status and Determine Eligibility is done and
			 * Application has not gone through 2 Step Income Determination
			 */
			if (CodesConstant.CEVTSTAT_COMP.equals(cdEventStatus)) {
				boolean notTwoStep = false;
				if (ObjectUtils.isEmpty(fceEligibilityDto.getNbrStepCalc()))
					notTwoStep = true;
				/**
				 * NBR_STEPS_IN_AFDC_INCOME_DETERMINATION
				 */
				if (!(ObjectUtils.isEmpty(fceEligibilityDto.getNbrStepCalc()))
						&& (ServiceConstants.NBR_STEPS_IN_AFDC_INCOME_DETERMINATION > fceEligibilityDto
								.getNbrStepCalc()))
					notTwoStep = true;

				if ((notTwoStep && (!ObjectUtils.isEmpty(fceEligibilityDto.getIndEligible())))
						|| (fceDao.hasDOBChangedForCertPers(idFceEligibility))) {
					/**
					 * Reset Determine Eligibility
					 */
					fceEligibilityDto.setIndEligible(null);
					/**
					 * Delete any reasons not eligible if any
					 */
					fceDao.deleteFceReasonsNotEligible(fceEligibilityDto.getIdFceEligibility());
				}
			}
			fceContext.setIdEvent(idApplicationEvent);
			fceContext.setIdPerson(idPerson);
			fceContext.setIdFceApplication(idFceApplication);
			fceContext.setIdFcePerson(idFcePerson);
			fceContext.setIdFceEligibility(idFceEligibility);

			fceContext.setCdEventStatus(cdEventStatus);
			fceContext.setFceApplicationDto(fceApplicationDto);
			fceContext.setFceEligibilityDto(fceEligibilityDto);
		}
		log.debug("initializeFceApplication - end");
		return fceContext;
	}

	/**
	 * 
	 * Method Name: initializeFceEligibility Method Description: This service is
	 * used for Initializes FCE Eligibility
	 * 
	 * @param idStage
	 * @param idEligibilityEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto @
	 */
	@Override
	public FceContextDto initializeFceEligibility(Long idStage, Long idEligibilityEvent, Long idLastUpdatePerson) {
		log.debug("initializeFceEligibility - begin");

		FceContextDto fceContext = new FceContextDto();
		EventDto eventDto = null;
		FceEligibilityDto fceEligibilityDto = null;
		EligibilityDto eligibilityDto = null;
		String cdEventStatus = null;

		if (!ObjectUtils.isEmpty(idEligibilityEvent)) {
			fceEligibilityDto = fceDao.findLatestEligibilityForEligibilityEvent(idEligibilityEvent);

		}
		Long idChild = stageDao.findPrimaryChildForStage(idStage);
		/**
		 * create a new eligibility event
		 */
		eventDto = findOrCreateEvent(ServiceConstants.FCE_ELIGIBILITY_EVENT_TYPE, idEligibilityEvent,
				idLastUpdatePerson, idStage, idChild);

		if (ObjectUtils.isEmpty(fceEligibilityDto)) {
			idEligibilityEvent = eventDto.getIdEvent();

			/**
			 * find the last end-dated eligibility for the stage
			 */
			FceEligibilityDto lastFceEligibilityDto = fceDao.findLatestEligibilityForStage(idStage);

			Long idCase = eventDto.getIdCase();

			if (!ObjectUtils.isEmpty(lastFceEligibilityDto)) {
				/**
				 * copy state from old eligibility to new eligibility
				 */
				FceEligibilityDto fceEligibilityLocaldto = fceDao.copyEligibility(lastFceEligibilityDto,
						idLastUpdatePerson, true);
				fceEligibilityDto = fceEligibilityLocaldto;
			} else {
				fceEligibilityDto = fceDao.createFceEligibility(idCase, idEligibilityEvent, idLastUpdatePerson, idStage,
						idChild, false);
			}

			EligibilityDto legacyEligibilityDto = null;

			if (!CodesConstant.CEVTSTAT_NEW.equals(eventDto.getCdEventStatus())) {
				legacyEligibilityDto = fceDao.findLegacyEligibility(idEligibilityEvent);
			}

			if (CodesConstant.CEVTSTAT_NEW.equals(eventDto.getCdEventStatus())) {
				legacyEligibilityDto = fceDao.findLatestLegacyEligibility(eventDto.getIdCase(),
						fceEligibilityDto.getIdPerson());
			}

			setChildSupportFromLegacy(legacyEligibilityDto, fceEligibilityDto);

			fceEligibilityDto.setIdEligibilityEvent(idEligibilityEvent);
			fceDao.updateFceEligiblility(fceEligibilityDto);
		}

		cdEventStatus = eventDto.getCdEventStatus();

		eligibilityDto = fceDao.findLegacyEligibility(idEligibilityEvent);

		if (!CodesConstant.CEVTSTAT_APRV.equals(cdEventStatus)) {
			syncFceEligibilityStatus(fceEligibilityDto);
		} else if (CodesConstant.CEVTSTAT_APRV.equals(cdEventStatus)) {
			eligibilityDto = new EligibilityDto();
			if (null != fceEligibilityDto) {
				EligibilityDto legacyEligibility = fceDao.findLatestLegacyEligibility(eventDto.getIdCase(),
						fceEligibilityDto.getIdPerson());
				BeanUtils.copyProperties(eligibilityDto, legacyEligibility);
				setChildSupportFromLegacy(eligibilityDto, fceEligibilityDto);
			}
		}
		if (null != fceEligibilityDto) {
			Long idFceApplication = fceEligibilityDto.getIdFceApplication();
			Long idFceEligibility = fceEligibilityDto.getIdFceEligibility();
			Long idFcePerson = fceEligibilityDto.getIdFcePerson();
			Long idPerson = fceEligibilityDto.getIdPerson();

			fceContext.setIdEvent(idEligibilityEvent);
			fceContext.setIdPerson(idPerson);
			fceContext.setIdFceApplication(idFceApplication);
			fceContext.setIdFceEligibility(idFceEligibility);
			fceContext.setIdFcePerson(idFcePerson);

			fceContext.setCdEventStatus(cdEventStatus);
			fceContext.setEligibilityDto(eligibilityDto);
			fceContext.setFceEligibilityDto(fceEligibilityDto);
		}
		log.debug("initializeFceEligibility - end");
		return fceContext;
	}

	@Override
	public FceContextDto initializeFceReview(Long idStage, Long idReviewEvent, Long idLastUpdatePerson) {
		log.debug("initializeFceReview - begin");
		verifyNonZero(ServiceConstants.idStage, idStage);
		verifyNonZero(ServiceConstants.idLastUpdatePerson, idLastUpdatePerson);

		FceContextDto fceContext = new FceContextDto();
		FceEligibility fceEligibilityEntity = new FceEligibility();
		FceReview fceReviewEntity = new FceReview();
		EventDto eventDto = null;
		FceEligibilityDto fceEligibilityLocal = null;
		FceReviewDto fceReviewLocalDto = new FceReviewDto();
		boolean isNewEvent = false;
		if (idReviewEvent != null && idReviewEvent != 0l) {
			fceReviewLocalDto = reviewUtil.findReviewForReviewEvent(idReviewEvent);
			eventDto = new EventDto();
			eventDto.setIdEvent(idReviewEvent);
			fceReviewLocalDto.setEvent(eventDto);
			BeanUtils.copyProperties(fceReviewLocalDto, fceReviewEntity);
			Long idFceEligibility = fceReviewLocalDto.getIdFceEligibility();
			fceEligibilityEntity = eligibilityUtil.findEligibility(idFceEligibility);
			fceEligibilityLocal = new FceEligibilityDto();
			BeanUtils.copyProperties(fceEligibilityEntity, fceEligibilityLocal);
			fceEligibilityLocal.setIdFceApplication(fceReviewLocalDto.getIdFceApplication());

		} else {
			isNewEvent = true;
			// create a new review event
			Long idChild = stageDao.findPrimaryChildForStage(idStage);
			// create a new eligibility event
			eventDto = findOrCreateEvent(CodesConstant.CEVNTTYP_FCR, idReviewEvent, idLastUpdatePerson, idStage,
					idChild);
			idReviewEvent = eventDto.getIdEvent();
			// find the last application with an eligibility
			FceEligibilityDto lastFceEligibility = fceDao.findLatestEligibilityForStage(idStage);

			if (!ObjectUtils.isEmpty(lastFceEligibility)) {

				fceEligibilityLocal = fceDao.copyFceReviewEligibility(lastFceEligibility, idLastUpdatePerson, false);
				// BeanUtils.copyProperties(fceEligibilityLocal,fceEligibilityEntity);
				fceEligibilityLocal.setIdFceReview(null);
			} else {
				eventDto = eventService.getEvent(idReviewEvent);
				Long idCase = eventDto.getIdCase();

				fceEligibilityLocal = fceDao.createFceEligibility(idCase, idReviewEvent, idLastUpdatePerson, idStage,
						idChild, true);
				if (!ObjectUtils.isEmpty(fceEligibilityLocal.getErrorDto()) && fceEligibilityLocal.getErrorDto()
						.getErrorCode() == ServiceConstants.MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE_CODE) {
					throw new ServiceLayerException(MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE,
							(long) ServiceConstants.MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE_CODE, null);
				} else if (!ObjectUtils.isEmpty(fceEligibilityLocal.getErrorDto()) && fceEligibilityLocal.getErrorDto()
						.getErrorCode() == ServiceConstants.MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE_CODE) {
					throw new ServiceLayerException(MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE,
							(long) ServiceConstants.MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE_CODE, null);
				}

			}

			Long idFceApplication = fceEligibilityLocal.getIdFceApplication();
			// If application is CAPS application, create new copy
			// before creating review
			FceApplication fceApplicationLocal = fceDao.getFceApplication(idFceApplication);
			if (ServiceConstants.CAPS_FCE.equals(fceApplicationLocal.getCdApplication())) {
				fceApplicationLocal = fceDao.save(fceApplicationLocal);
				idFceApplication = fceApplicationLocal.getIdFceApplication();
			}
			// could have moved it further up, but that would have just
			// muddied the logic
			// This should never happen
			if (ServiceConstants.NEW_TO_SUBCARE.equals(fceApplicationLocal.getCdApplication())) {
				throw new ServiceLayerException(
						"This child does not have a current or legacy Foster Care Application.  Foster Care Review is not available until an Application has been completed for this child.");
			}

			// BeanUtils.copyProperties(fceReviewLocalDto,fceReviewEntity);
			// Do not display the Deprivation of Parental
			// Support section if the responses will not potentially cause
			// a change in the child's eligibility status.
			// The section may matter if the checklist hasn't been filled
			// out yet
			// That's why we exclude CAPS applications from this decision
			if (ServiceConstants.CAPS_FCE.equals(fceApplicationLocal.getCdApplication()) == false) {
				// This code would be problematic if we were creating the
				// application/eligibility object in this transaction.
				FceEligibilityDto applicationFceEligibilityLocalDto = eligibilityUtil
						.findEligibilityByIdFceApplication(idFceApplication);
				FceEligibility applicationFceEligibilityLocal = new FceEligibility();
				BeanUtils.copyProperties(applicationFceEligibilityLocalDto, applicationFceEligibilityLocal);

				boolean ineligibleDueToAnyReasonOtherThanCitizenshipRequirement = eligibilityUtil
						.ineligibleDueToAnyReasonOtherThanCitizenshipRequirement(applicationFceEligibilityLocal);

				// !!! This is beginning to look more and more like I need a
				// real indicator
				if (ineligibleDueToAnyReasonOtherThanCitizenshipRequirement) {
					// FosterCareReviewBean.MAGIC_DONT_SHOW_ELIGIBILITY_CONFIRMATION
					fceEligibilityLocal.setTxtDeterminationComments("MAGIC_DONT_SHOW_ELIGIBILITY_CONFIRMATION");
				}
			}

			// fceReviewEntity.setIdLastUpdatePerson(new
			// Long(idLastUpdatePerson));
			fceReviewLocalDto.setIdLastUpdatePerson(idLastUpdatePerson);

			// EventHelper.FCE_REVIEW_TASK_CODE
			eventUtil.attachEventToTodo(idReviewEvent, idStage, "3440");
		}
		// fceEligibilityLocal = new FceEligibilityDto();
		// FceReviewDto fceReviewDto = new FceReviewDto();
		// BeanUtils.copyProperties(fceEligibilityEntity,fceEligibilityLocal);
		// BeanUtils.copyProperties( fceReviewEntity,fceReviewDto);

		eventDto = eventService.getEvent(idReviewEvent);
		String eventStatus = eventDto.getCdEventStatus();
		// CodesTables.CEVTSTAT_COMPEventHelper.COMPLETE_EVENT

		if (eventStatus.equals(CodesConstant.CEVTSTAT_COMP) == false) {
			fceReviewLocalDto.setIdStage(idStage);
			fceReviewLocalDto.setIdCase(fceEligibilityLocal.getIdCase());
			reviewUtil.syncFceReviewStatus(fceEligibilityLocal, fceReviewLocalDto);
			if (isNewEvent) {
				Long idFceReview = reviewUtil.createReview(fceReviewLocalDto, idReviewEvent, fceEligibilityLocal);
				fceReviewLocalDto.setIdFceReview(idFceReview);
			}
		}
		fceEligibilityLocal.setIdFceReview(fceReviewLocalDto.getIdFceReview());
		Long idFceApplication = fceEligibilityLocal.getIdFceApplication();
		Long idFceEligibility = fceEligibilityLocal.getIdFceEligibility();
		Long idFcePerson = fceEligibilityLocal.getIdFcePerson();
		Long idFceReview = fceReviewLocalDto.getIdFceReview();
		Long idPerson = fceEligibilityLocal.getIdPerson();

		fceContext.setIdEvent(idReviewEvent);
		fceContext.setIdPerson(idPerson);
		fceContext.setIdFceApplication(idFceApplication);
		fceContext.setIdFcePerson(idFcePerson);
		fceContext.setIdFceEligibility(idFceEligibility);
		fceContext.setIdFceReview(idFceReview);
		fceContext.setFceReviewDto(fceReviewLocalDto);
		fceContext.setFceEligibilityDto(fceEligibilityLocal);
		log.debug("initializeFceReview - end");
		return fceContext;
	}

	/**
	 * 
	 * Method Name: getDescription Method Description: This method returns the
	 * eventDescription based on taskCode and eventStatus
	 * 
	 * @param taskCode
	 * @param eventStatus
	 * @return eventDescription @
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getDescription(String taskCode, String eventStatus) {
		HashMap eventMap = null;

		if (ServiceConstants.FCE_APPLICATION_TASK_CODE.equals(taskCode)) {
			HashMap hashMap = new HashMap();
			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_APP);
			hashMap.put(ServiceConstants.PROCESS_EVENT, ServiceConstants.PROCESS_EVENT_APP);
			hashMap.put(ServiceConstants.PENDING_EVENT, ServiceConstants.PENDING_EVENT_APP);
			hashMap.put(ServiceConstants.COMPLETE_EVENT, ServiceConstants.COMPLETE_EVENT_APP);
			hashMap.put(ServiceConstants.APPROVED_EVENT, ServiceConstants.APPROVED_EVENT_APP);

			eventMap = hashMap;
		} else if (taskCode.equals(ServiceConstants.FCE_ELIGIBILITY_TASK_CODE)) {
			HashMap hashMap = new HashMap();
			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_ELI);
			eventMap = hashMap;
		} else if (ServiceConstants.FCE_REVIEW_TASK_CODE.equals(taskCode)) {

			HashMap hashMap = new HashMap();
			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_FC);
			hashMap.put(ServiceConstants.PROCESS_EVENT, ServiceConstants.PROCESS_EVENT_FC);
			hashMap.put(ServiceConstants.PENDING_EVENT, ServiceConstants.PENDING_EVENT_FC);
			hashMap.put(ServiceConstants.COMPLETE_EVENT, ServiceConstants.COMPLETE_EVENT_FC);

			eventMap = hashMap;
		}

		String description = (String) eventMap.get(eventStatus);
		if (ObjectUtils.isEmpty(description)) {
			throw new ServiceLayerException(
					"Unexpected eventStatus '" + eventStatus + "' " + "for taskCode '" + taskCode + "'");

		}
		final int eventDescriptionColumnLimit = 80;
		if (description.length() > eventDescriptionColumnLimit) {
			description = description.substring(0, eventDescriptionColumnLimit);
		}
		return description;
	}

	/**
	 * 
	 * Method Name: syncFceApplicationStatus Method Description: This method is
	 * used for updating the fceEligibility and fceApplication
	 * 
	 * @param fceEligibilityDto
	 * @
	 */
	@Override
	public void syncFceApplicationStatus(FceEligibilityDto fceEligibilityDto) {

		Long idFceApplication = fceEligibilityDto.getIdFceApplication();
		Long idFceEligibility = fceEligibilityDto.getIdFceEligibility();
		Long idFcePerson = fceEligibilityDto.getIdFcePerson();
		Long idPerson = fceEligibilityDto.getIdPerson();
		Long idStage = fceEligibilityDto.getIdStage();

		fceEligibilityDto.setCdPersonCitizenship(fceDao.getCdPersonCitizenship(idPerson));
		FceApplicationDto fceApplicationDto = fceDao.getFceApplicationById(idFceApplication);
		fceApplicationDto.setIndEvaluationConclusion(fceDao.getIndEvaluationConclusion(idFceApplication));

		fceDao.deleteNonPrinciples(idFceEligibility, idStage);
		fceDao.createPrinciples(idFceEligibility, idStage);
		fceDao.createIncomeForFcePersons(idFceEligibility, idFcePerson, idPerson);
	}

	public static String toCharIndicator(Boolean value) {
		if (value == null) {
			return null;
		}
		if (value.booleanValue()) {
			return "Y";
		}
		return "N";
	}

	public static boolean stringToBoolaen(String value) {
		if ("Y".equals(value)) {
			return true;
		}
		return false;
	}

	public void updateAddress(FceApplicationDto fceApplicationDto, PersonDto personDto) {
		fceApplicationDto.setAddrRemovalAddrZip(personDto.getAddrPersonZip());
		fceApplicationDto.setAddrRemovalCity(personDto.getAddrPersonCity());
		fceApplicationDto.setAddrRemovalStLn1(personDto.getAddrPersonStLn1());
		fceApplicationDto.setCdRemovalAddrCounty(personDto.getCdPersonCounty());
		fceApplicationDto.setCdRemovalAddrState(personDto.getCdPersonState());
	}

	public Long createEligibilityEvent(Long idPerson, Long idStage, Long idChild, String eventType) {

		String taskCode = ServiceConstants.FCE_ELIGIBILITY_TASK_CODE;

		/**
		 * check for "NEW" events fce elegibility events
		 */
		Long newEligibilityEvent = fceDao.countNewEvents(taskCode, idStage);
		/**
		 * if "NEW" event found then delete it
		 */
		if (!ObjectUtils.isEmpty(newEligibilityEvent)) {
			fceDao.deleteEvent(newEligibilityEvent);
		}
		int incompleteEligibilityEvents = fceDao.countIncompleteEvents(ServiceConstants.FCE_ELIGIBILITY_TASK_CODE,
				idStage);
		if (incompleteEligibilityEvents > 0) {
			throw new ServiceLayerException("MSG_OPEN_SUMMARY_EVENT", (long) ServiceConstants.MSG_OPEN_SUMMARY_EVENT,
					null);
		}
		String description = getDescription(taskCode, ServiceConstants.NEW_EVENT);

		Long idEvent = createPostEvent(idPerson, idStage, idChild, taskCode, eventType, description);

		return idEvent;
	}

	/**
	 * 
	 * Method Name: findOrCreateEvent Method Description: This method creates
	 * and retrieves the event data
	 * 
	 * @param eventType
	 * @param idEvent
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @return EventDto @
	 */
	@Override
	public EventDto findOrCreateEvent(String eventType, Long idEvent, Long idPerson, Long idStage, Long idChild) {
		EventDto eventDto = null;
		if (!ObjectUtils.isEmpty(idEvent) && idEvent != 0) {
			eventDto = eventDao.getEventByid(idEvent);
			return eventDto;
		}
		idEvent = createEvent(eventType, idPerson, idStage, idChild);
		eventDto = eventDao.getEventByid(idEvent);
		return eventDto;
	}

	/**
	 * 
	 * Method Name: createEvent Method Description: This method is used for
	 * creating an event
	 * 
	 * @param eventType
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @return @
	 */
	@Override
	public Long createEvent(String eventType, Long idPerson, Long idStage, Long idChild) {
		if (ServiceConstants.FCE_APPLICATION_EVENT_TYPE.equals(eventType)) {
			return createApplicationEvent(idPerson, idStage, idChild, eventType);
		}
		if (ServiceConstants.FCE_ELIGIBILITY_EVENT_TYPE.equals(eventType)) {
			return createEligibilityEvent(idPerson, idStage, idChild, eventType);
		}
		if (ServiceConstants.FCE_REVIEW_EVENT_TYPE.equals(eventType)) {
			return createReviewEvent(idPerson, idStage, idChild, eventType);
		}
		throw new ServiceLayerException("unexpected eventType: " + eventType);
	}

	public FceApplicationDto createFcApplication(String cdApplication, Long idCase, Long idEvent,
			Long idLastUpdatePerson, Long idStage, Long idPerson) {

		if (ObjectUtils.isEmpty(idPerson)) {
			throw new ServiceLayerException(ServiceConstants.primaryChild + idStage);
		}

		Long idFceEligibility = fceDao.createFceEligibility(idCase, idLastUpdatePerson, idStage, idPerson);

		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		fceApplicationDto.setCdApplication(cdApplication);
		fceApplicationDto.setIdCase(idCase);
		fceApplicationDto.setIdEvent(idEvent);
		fceApplicationDto.setIdFceEligibility(idFceEligibility);
		fceApplicationDto.setIdLastUpdatePerson(idLastUpdatePerson);
		fceApplicationDto.setIdPerson(idPerson);
		fceApplicationDto.setIdStage(idStage);
		fceApplicationDto.setDtLastUpdate(new Date());
		Long idFceApplication = fceDao.createFceApplication(fceApplicationDto);
		fceApplicationDto.setIdFceApplication(idFceApplication);
		return fceApplicationDto;
	}

	public Long createApplicationEvent(Long idPerson, Long idStage, Long idChild, String eventType) {
		String taskCode = ServiceConstants.FCE_APPLICATION_TASK_CODE;
		String description = getDescription(taskCode, ServiceConstants.NEW_EVENT);
		Long idEvent = createPostEvent(idPerson, idStage, idChild, taskCode, eventType, description);
		return idEvent;
	}

	/**
	 * 
	 * Method Name: createPostEvent Method Description: This method is used for
	 * the creating a new event.
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @param taskCode
	 * @param eventType
	 * @param eventDescription
	 * @return idEvent @
	 */
	@Override
	public Long createPostEvent(Long idPerson, Long idStage, Long idChild, String taskCode, String eventType,
			String eventDescription) {
		if (ObjectUtils.isEmpty(idChild)) {
			idChild = stageDao.findPrimaryChildForStage(idStage);
		}
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		List<PostEventDto> postEventDtoList = new ArrayList<PostEventDto>();
		Date dtCurrent = new Date();

		postEventIPDto.setCdTask(taskCode);
		postEventIPDto.setEventDescr(eventDescription);
		postEventIPDto.setCdEventType(eventType);
		postEventIPDto.setCdEventStatus(ServiceConstants.NEW_EVENT);
		postEventIPDto.setIdPerson(idPerson);
		postEventIPDto.setIdStage(idStage);
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setTsLastUpdate(dtCurrent);
		postEventIPDto.setDtEventOccurred(dtCurrent);

		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setIdPerson(idChild);
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventDtoList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtoList);

		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	public Long createReviewEvent(Long idPerson, Long idStage, Long idChild, String eventType) {

		String taskCode = ServiceConstants.FCE_REVIEW_TASK_CODE;
		String description = getDescription(taskCode, ServiceConstants.NEW_EVENT);
		Long idEvent = createPostEvent(idPerson, idStage, idChild, taskCode, eventType, description);
		return idEvent;

		/*
		 * String taskCode = ServiceConstants.FCE_REVIEW_TASK_CODE; Long
		 * newFceReviewEvent = fceApplicationDao.countNewEvents(taskCode,
		 * idStage);
		 * 
		 * if (!ObjectUtils.isEmpty(newFceReviewEvent)) {
		 * fceApplicationDao.deleteEvent(newFceReviewEvent); } Integer
		 * incompleteFceReviewEvents = fceApplicationDao
		 * .countIncompleteEvents(ServiceConstants.FCE_REVIEW_TASK_CODE,
		 * idStage);
		 * 
		 * if (!ObjectUtils.isEmpty(incompleteFceReviewEvents)) { throw new
		 * ServiceLayerException( ServiceConstants.MSG_OPEN_SUMMARY_EVENT + " "
		 * + ServiceConstants.WARNING_PRIORITY); }
		 * 
		 * String description = getDescription(taskCode,
		 * ServiceConstants.NEW_EVENT);
		 * 
		 * Long idEvent = createPostEvent(idPerson, idStage, idChild, taskCode,
		 * eventType, description);
		 * 
		 * return idEvent;
		 */
	}

	public void setChildSupportFromLegacy(EligibilityDto legacyEligibilityDto, FceEligibilityDto fceEligibilityDto) {

		if (!(ObjectUtils.isEmpty(legacyEligibilityDto))
				&& (isQuestionAnswered(ServiceConstants.CELIGIBI_030, legacyEligibilityDto))) {
			fceEligibilityDto.setIndChildSupportOrdered(ServiceConstants.Y);
		}
	}

	public boolean isQuestionAnswered(String question, EligibilityDto eligibility) {
		return ((question.equals(eligibility.getCdEligCsupQuest1()))
				|| (question.equals(eligibility.getCdEligCsupQuest2()))
				|| (question.equals(eligibility.getCdEligCsupQuest3()))
				|| (question.equals(eligibility.getCdEligCsupQuest4())));
	}

	/**
	 * 
	 * Method Name: syncFceEligibilityStatus Method Description: This method is
	 * used for updating the fceEligibility
	 * 
	 * @param fceEligibilityDto
	 * @
	 */
	@Override
	public void syncFceEligibilityStatus(FceEligibilityDto fceEligibilityDto) {
		Long idPerson = fceEligibilityDto.getIdPerson();
		Long idFcePerson = fceEligibilityDto.getIdFcePerson();
		Long idEligibilityEvent = fceEligibilityDto.getIdEligibilityEvent();
		FcePersonDto fcePersonDto = fceDao.findFcePerson(idFcePerson);

		PersonDto personDto = personDao.getPersonById(idPerson);

		fceDao.updateBirthday(fcePersonDto, personDto);

		EventDto eventDto = eventService.getEvent(idEligibilityEvent);
		String cdEventStatus = eventDto.getCdEventStatus();

		if ((ServiceConstants.NEW_EVENT.equals(cdEventStatus))) {
			String cdBlocChild = personDao.findBloc(idPerson);
			fceEligibilityDto.setCdBlocChild(cdBlocChild);

			// artf264854: BR 4.28 Eligibility Summary Page Updates
			// Fetch latest service package code from person_svc_pkg table, if Billing Service Level is not available and set to FceEligibilityDto.
			if(StringUtils.isEmpty(cdBlocChild)){
				List<ServicePackageDtlDto> servicePackageDtlDto = servicePackageDao.getActiveSelectedServicePackage(fceEligibilityDto.getIdCase(), fceEligibilityDto.getIdStage());
				if(!CollectionUtils.isEmpty(servicePackageDtlDto)) {
					fceEligibilityDto.setCdServicePackageChild(servicePackageDtlDto.get(0).getSvcPkgCd());
				}
			}

			Double amtSsi = fceDao.findSsi(idPerson);
			fceEligibilityDto.setAmtSsi(amtSsi);
		}
	}

	/**
	 * 
	 * Method Name: verifyCanSave Method Description: This method is used for
	 * verify the save
	 * 
	 * @param idStage
	 * @param idLastUpdatePerson
	 * @
	 */
	@Override
	public void verifyCanSave(Long idStage, Long idLastUpdatePerson) {
		verifyNonZero(ServiceConstants.idStage, idStage);
		fceDao.verifyOpenStage(idStage);
		verifyNonZero(ServiceConstants.idLastUpdatePerson, idLastUpdatePerson);
	}

	public Long verifyNonZero(String propertyName, Long value) {
		if (ObjectUtils.isEmpty(value)) {
			String exceptionMessage = ServiceConstants.PROPERTY + ServiceConstants.SPACE + propertyName
					+ ServiceConstants.NONZERO;
			throw new ServiceLayerException(exceptionMessage);
		}
		return value;
	}

	/**
	 * This service is to save the FCE application from App/Background page.
	 * 
	 * @param saveFceApplicationReq
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public ServiceResHeaderDto saveFceApplication(SaveFceApplicationReq saveFceApplicationReq) {
		verifyCanSave(saveFceApplicationReq.getFceApplicationDto().getIdStage(),
				saveFceApplicationReq.getFceApplicationDto().getIdLastUpdatePerson());
		FceEligibility fceEligibility = fceEligibilityDao
				.getFceEligibilitybyId(saveFceApplicationReq.getFceApplicationDto().getIdFceEligibility());
		FceApplication fceApplication = fceEligibility.getFceApplications().stream().filter(
				o -> saveFceApplicationReq.getFceApplicationDto().getIdFceApplication().equals(o.getIdFceApplication()))
				.findAny().orElse(new FceApplication());

		verifyLatestUpdate(saveFceApplicationReq.getFceApplicationDto().getDtLastUpdate(),
				fceApplication.getDtLastUpdate());
		BeanUtils.copyProperties(saveFceApplicationReq.getFceApplicationDto(), fceApplication, ignoreProperties);
		fceApplication.setDtLastUpdate(new Date());
		if (ServiceConstants.Y.equals(saveFceApplicationReq.getFceApplicationDto().getIndValdtd())) {
			fceApplication.setDtValdtd(new Date());
		}
		Set<FcePerson> personList = fceEligibility.getFcePersons();
		Integer numberPeopleLivingInHomeOfRemoval = 0;
		Long numberCertifiedPeople = 0l;
		for (FcePerson p : personList) {

			String indPersonHmRemovalDto = !ObjectUtils.isEmpty(
					saveFceApplicationReq.getPersons().get(p.getPerson().getIdPerson()).getIndPersonHmRemoval())
							? saveFceApplicationReq.getPersons().get(p.getPerson().getIdPerson())
									.getIndPersonHmRemoval()
							: null;
			String indCertifiedGroupDto = !ObjectUtils
					.isEmpty(saveFceApplicationReq.getPersons().get(p.getPerson().getIdPerson()).getIndCertifiedGroup())
							? saveFceApplicationReq.getPersons().get(p.getPerson().getIdPerson()).getIndCertifiedGroup()
							: null;
			if (ServiceConstants.STRING_IND_Y.equals(indPersonHmRemovalDto))
				numberPeopleLivingInHomeOfRemoval++;
			p.setIndPersonHmRemoval(indPersonHmRemovalDto);
			if (ServiceConstants.STRING_IND_Y.equals(indCertifiedGroupDto))
				numberCertifiedPeople++;
			p.setIndCertifiedGroup(indCertifiedGroupDto);
			p.setDtLastUpdate(new Date());
		}
		fceApplication.setNbrLivingAtHome(numberPeopleLivingInHomeOfRemoval);
		Long oldNumberCertifiedPeople = fceEligibility.getNbrCertifiedGroup();
		fceEligibility.setNbrCertifiedGroup(numberCertifiedPeople);
		// if number of certified people changes, it could affect system-derived
		// deprivation
		if (oldNumberCertifiedPeople != numberCertifiedPeople) {
			String oldIndMeetsDpOrNotSystem = fceEligibility.getIndMeetsDpOrNotSystem();
			FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
			BeanUtils.copyProperties(fceEligibility, fceEligibilityDto);

			FceApplicationDto fceApplicationDto = new FceApplicationDto();
			BeanUtils.copyProperties(fceApplication, fceApplicationDto);
			applicationReasonsNotEligibleUtil.calculateSystemDerivedParentalDeprivation(
					saveFceApplicationReq.getFceApplicationDto().getCdLivingMonthRemoval(),
					saveFceApplicationReq.getFceApplicationDto(), fceEligibilityDto);
			String newIndMeetsDpOrNotSystem = fceEligibility.getIndMeetsDpOrNotSystem();

			if (!newIndMeetsDpOrNotSystem.equals(oldIndMeetsDpOrNotSystem)) {
				// clear ES indicator if system-derived deprivation changes
				fceEligibility.setIndMeetsDpOrNotEs(null);
				// if the application started after R2 release date, set ES to match System DD
				Date dtR2Release = codesDao.getAppRelDate(ServiceConstants.R2_REL_CODE);
				if (!ObjectUtils.isEmpty(fceApplicationDto.getEventDate()) && !fceApplicationDto.getEventDate().before(dtR2Release)){
					fceEligibility.setIndMeetsDpOrNotEs(newIndMeetsDpOrNotSystem);
				}
			}
		}
		fceEligibility.setDtLastUpdate(new Date());
		fceEligibilityDao.save(fceEligibility);
		Long idEvent = saveFceApplicationReq.getFceApplicationDto().getIdEvent();
		EventDto eventDto = eventDao.getEventByid(idEvent);
		String eventType = eventDto.getCdEventStatus();
		if (ServiceConstants.EVENTSTATUS_NEW.equals(eventType)) {
			eventUtil.changeEventStatus(idEvent, ServiceConstants.EVENTSTATUS_NEW,
					ServiceConstants.EVENTSTATUS_PROCESS);
		} else if (ServiceConstants.EVENTSTATUS_COMPLETE.equals(eventType)) {
			eventUtil.changeEventStatus(idEvent, ServiceConstants.EVENTSTATUS_COMPLETE,
					ServiceConstants.EVENTSTATUS_PENDING);
		}
		DependentCareReadReq dependentCareReadReq = new DependentCareReadReq();
		dependentCareReadReq.setIdFceApplication(saveFceApplicationReq.getFceApplicationDto().getIdFceApplication());
		dependentCareReadReq.setIdFceEligiblity(saveFceApplicationReq.getFceApplicationDto().getIdFceEligibility());
		dependentCareReadReq.setIdEvent(idEvent);
		try {
			depCareDeductionService.syncDepCareDeductions(dependentCareReadReq);
		} catch (Exception e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return new ServiceResHeaderDto();
	}

	// checking if user updates latest record
	private void verifyLatestUpdate(Date dtLastUpdtDto, Date dtLastUpdtEntity) {

		if (!ObjectUtils.isEmpty(dtLastUpdtDto) && dtLastUpdtDto.compareTo(dtLastUpdtEntity) != 0)
			throw new ServiceLayerException("Time Mismatch error", new Long(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH),
					null);
	}

	/**
	 * This service is to check if the given zip and county is valid for texas
	 * 
	 * @param texasZipCountyValidateReq
	 * @return CommonBooleanRes
	 * @throws InvalidRequestException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public CommonBooleanRes isValidTexasZipAndCounty(TexasZipCountyValidateReq texasZipCountyValidateReq) {

		Boolean status = zipCountyChkDao.isValidTexasZipAndCounty(texasZipCountyValidateReq.getAddrZip(),
				texasZipCountyValidateReq.getCdAddrCounty());
		CommonBooleanRes res = new CommonBooleanRes();
		res.setExists(status);

		return res;
	}

	/**
	 * 
	 * Method Name: newUsingFceReview Method Description: New Using FCE Review
	 * 
	 * @param idStage
	 * @param idReviewEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto
	 */
	@Override
	public FceContextDto newUsingFceReview(Long idStage, Long idReviewEvent, Long idLastUpdatePerson) {
		log.info("newUsingFceReview - begin");
		verifyNonZero(ServiceConstants.idStage, idStage);
		verifyNonZero(ServiceConstants.idLastUpdatePerson, idLastUpdatePerson);

		FceContextDto fceContext = new FceContextDto();
		EventDto eventDto = null;
		FceEligibilityDto fceEligibilityLocal = null;
		FceReviewDto fceReviewLocalDto = null;
		Long idEvent = 0L;
		// Get existing fce review for new using
		fceReviewLocalDto = reviewUtil.findReviewForReviewEvent(idReviewEvent);

		// create a new review event
		Long idChild = stageDao.findPrimaryChildForStage(idStage);
		// create a new eligibility event
		eventDto = findOrCreateEvent(CodesConstant.CEVNTTYP_FCR, idEvent, idLastUpdatePerson, idStage, idChild);
		idReviewEvent = eventDto.getIdEvent();
		// find the last application with an eligibility
		FceEligibilityDto lastFceEligibility = fceDao.findLatestEligibilityForStage(idStage);
		if (!ObjectUtils.isEmpty(lastFceEligibility)) {
			fceEligibilityLocal = fceDao.copyFceReviewEligibility(lastFceEligibility, idLastUpdatePerson, false);
			fceEligibilityLocal.setIndEquity(lastFceEligibility.getIndEquity());
			fceEligibilityLocal.setIndCtznshpAttorneyReview(lastFceEligibility.getIndCtznshpAttorneyReview());
			fceEligibilityLocal.setIndCtznshpBaptismalCrtfct(lastFceEligibility.getIndCtznshpBaptismalCrtfct());
			fceEligibilityLocal.setIndCtznshpBirthCrtfctFor(lastFceEligibility.getIndCtznshpBirthCrtfctFor());
			fceEligibilityLocal.setIndCtznshpBirthCrtfctUs(lastFceEligibility.getIndCtznshpBirthCrtfctUs());
			fceEligibilityLocal.setIndCtznshpChldFound(lastFceEligibility.getIndCtznshpChldFound());
			fceEligibilityLocal.setIndCtznshpCitizenCrtfct(lastFceEligibility.getIndCtznshpCitizenCrtfct());
			fceEligibilityLocal.setIndCtznshpDhsOther(lastFceEligibility.getIndCtznshpDhsOther());
			fceEligibilityLocal.setIndCtznshpDhsUs(lastFceEligibility.getIndCtznshpDhsUs());
			fceEligibilityLocal.setIndCtznshpEvaluation(lastFceEligibility.getIndCtznshpEvaluation());
			fceEligibilityLocal.setIndCtznshpForDocumentation(lastFceEligibility.getIndCtznshpForDocumentation());
			fceEligibilityLocal.setIndCtznshpHospitalCrtfct(lastFceEligibility.getIndCtznshpHospitalCrtfct());
			fceEligibilityLocal.setIndCtznshpNoDocumentation(lastFceEligibility.getIndCtznshpNoDocumentation());
			fceEligibilityLocal.setIndCtznshpNtrlztnCrtfct(lastFceEligibility.getIndCtznshpNtrlztnCrtfct());
			fceEligibilityLocal.setIndCtznshpPassport(lastFceEligibility.getIndCtznshpPassport());
			fceEligibilityLocal.setIndCtznshpResidentCard(lastFceEligibility.getIndCtznshpResidentCard());

			fceEligibilityLocal.setIdFceReview(null);
		} else {
			eventDto = eventService.getEvent(idReviewEvent);
			Long idCase = eventDto.getIdCase();

			fceEligibilityLocal = fceDao.createFceEligibility(idCase, idReviewEvent, idLastUpdatePerson, idStage,
					idChild, true);
			if (!ObjectUtils.isEmpty(fceEligibilityLocal.getErrorDto()) && fceEligibilityLocal.getErrorDto()
					.getErrorCode() == ServiceConstants.MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE_CODE) {
				throw new ServiceLayerException(MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE,
						(long) ServiceConstants.MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE_CODE, null);
			} else if (!ObjectUtils.isEmpty(fceEligibilityLocal.getErrorDto()) && fceEligibilityLocal.getErrorDto()
					.getErrorCode() == ServiceConstants.MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE_CODE) {
				throw new ServiceLayerException(MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE,
						(long) ServiceConstants.MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE_CODE, null);
			}

		}

		Long idFceApplication = fceEligibilityLocal.getIdFceApplication();
		Long idFceEligibility = fceEligibilityLocal.getIdFceEligibility();
		Long idCase = fceEligibilityLocal.getIdCase();

		// If application is CAPS application, create new copy
		// before creating review
		FceApplication fceApplicationLocal = fceDao.getFceApplication(idFceApplication);
		if (ServiceConstants.CAPS_FCE.equals(fceApplicationLocal.getCdApplication())) {
			fceApplicationLocal.setIdFceApplication(0L);
			fceApplicationLocal = fceDao.save(fceApplicationLocal);
			idFceApplication = fceApplicationLocal.getIdFceApplication();
		}
		// could have moved it further up, but that would have just
		// muddied the logic
		// This should never happen
		if (ServiceConstants.NEW_TO_SUBCARE.equals(fceApplicationLocal.getCdApplication())) {
			throw new ServiceLayerException(
					"This child does not have a current or legacy Foster Care Application.  Foster Care Review is not available until an Application has been completed for this child.");
		}

		FceReviewDto newFceReviewLocalDto = reviewUtil.createReview(idCase, idReviewEvent, idFceApplication,
				idFceEligibility, idStage);

		// Do not display the Deprivation of Parental
		// Support section if the responses will not potentially cause
		// a change in the child's eligibility status.
		// The section may matter if the checklist hasn't been filled out yet
		// That's why we exclude CAPS applications from this decision
		if (!ServiceConstants.CAPS_FCE.equals(fceApplicationLocal.getCdApplication())) {
			// This code would be problematic if we were creating the
			// application/eligibility object in this transaction.
			FceEligibilityDto applicationFceEligibilityLocalDto = eligibilityUtil
					.findEligibilityByIdFceApplication(idFceApplication);
			FceEligibility applicationFceEligibilityLocal = new FceEligibility();
			BeanUtils.copyProperties(applicationFceEligibilityLocalDto, applicationFceEligibilityLocal);

			boolean ineligibleDueToAnyReasonOtherThanCitizenshipRequirement = eligibilityUtil
					.ineligibleDueToAnyReasonOtherThanCitizenshipRequirement(applicationFceEligibilityLocal);

			if (ineligibleDueToAnyReasonOtherThanCitizenshipRequirement) {
				fceEligibilityLocal.setTxtDeterminationComments("MAGIC_DONT_SHOW_ELIGIBILITY_CONFIRMATION");
			}
		}
		newFceReviewLocalDto.setIdLastUpdatePerson(idLastUpdatePerson);

		fceEligibilityLocal.setIdFceReview(newFceReviewLocalDto.getIdFceReview());
		// EventHelper.FCE_REVIEW_TASK_CODE
		eventUtil.attachEventToTodo(idReviewEvent, idStage, "3440");

		eventDto = eventService.getEvent(idReviewEvent);
		String eventStatus = eventDto.getCdEventStatus();
		if (!eventStatus.equals(CodesConstant.CEVTSTAT_COMP)) {
			reviewUtil.syncFceReviewStatus(fceEligibilityLocal, fceReviewLocalDto);
		}

		Long idFcePerson = fceEligibilityLocal.getIdFcePerson();
		Long idFceReview = newFceReviewLocalDto.getIdFceReview();
		Long idPerson = fceEligibilityLocal.getIdPerson();
		BeanUtils.copyProperties(fceReviewLocalDto, newFceReviewLocalDto);
		newFceReviewLocalDto.setIdFceReview(idFceReview);
		newFceReviewLocalDto.setIdFceApplication(idFceApplication);
		newFceReviewLocalDto.setIdFceEligibility(idFceEligibility);
		newFceReviewLocalDto.setIdLastUpdatePerson(idLastUpdatePerson);
		newFceReviewLocalDto.setEvent(eventDto);
		newFceReviewLocalDto.setFceEligibility(fceEligibilityLocal);
		fceContext.setIdEvent(idReviewEvent);
		fceContext.setIdPerson(idPerson);
		fceContext.setIdFceApplication(idFceApplication);
		fceContext.setIdFcePerson(idFcePerson);
		fceContext.setIdFceEligibility(idFceEligibility);
		fceContext.setIdFceReview(idFceReview);
		fceContext.setFceReviewDto(newFceReviewLocalDto);
		fceContext.setFceEligibilityDto(fceEligibilityLocal);
		log.info("newUsingFceReview - end");
		return fceContext;
	}

	/**
	 * 
	 * Method Name: createNewApp Method Description: This method called when
	 * newusing is called , the following method will create new app
	 * 
	 * @param fceReq
	 * @return FceApplicationRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public GetFceApplicationRes createNewApp(FceReq fceReq) {
		fceReq.setIdappevent(0l);
		// intialize the Application.
		GetFceApplicationRes fceApplicationRes = getFceApplication(fceReq);
		return fceApplicationRes;
	}

	/**
	 * 
	 * Method Name: copyOldApptoNewApp Method Description: This method called
	 * after calling the createNewApp to copy the original application data to
	 * new application
	 * 
	 * @param fceReq
	 * @return FceApplicationRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public GetFceApplicationRes copyOldApptoNewApp(GetFceApplicationRes fceApplicationRes, FceReq fceReq) {
		// get the new Eligibility as we we are going to save the application
		// using
		FceEligibility fceEligibility = fceEligibilityDao
				.getFceEligibilitybyId(fceApplicationRes.getIdFceEligibility());
		// get the original Application
		GetFceApplicationRes originalFceApplicationRes = getFceApplication(fceReq);
		FceApplicationDto fceApplicationDto = originalFceApplicationRes.getFceApplicationDto();
		FceEligibilityDto fceEligibilityDto = originalFceApplicationRes.getFceEligibilityDto();
		// List of properties to ignore while copying the data
		String[] ignoreProperties = { "idFceApplication", "idEvent", "idFcePerson", "dtLastUpdate",
				"dtApplicationComplete", "idFceEligibility" };
		String[] eligIgnoreProperties = { "idFceApplication", "idFceEligibility", "idFcePerson", "dtLastUpdate",
				"idEligibilityEvent", "idFcePerson", "idFceReview" };
		String[] personIgnoreProperties = { "idFcePerson", "idPerson", "idFceEligibility", "dtLastUpdate" };

		// pull the Application out from eligibility
		FceApplication fceApplication = fceEligibility.getFceApplications().stream().filter(
				o -> fceApplicationRes.getFceApplicationDto().getIdFceApplication().equals(o.getIdFceApplication()))
				.findAny().orElse(new FceApplication());
		// copy orig App to new app
		BeanUtils.copyProperties(fceApplicationDto, fceApplication, ignoreProperties);
		fceApplication.setDtLastUpdate(new Date());
		// copy orig persons to new persons
		Set<FcePerson> personList = fceEligibility.getFcePersons();
		Map<Long, FcePersonDto> origPersons = originalFceApplicationRes.getPersons();

		for (Entry<Long, FcePersonDto> entry : origPersons.entrySet()) {
			FcePerson fcePerson = personList.stream().filter(o -> o.getPerson().getIdPerson().equals(entry.getKey()))
					.findFirst().orElse(null);
			if (!ObjectUtils.isEmpty(fcePerson)) {
				BeanUtils.copyProperties(entry.getValue(), fcePerson, personIgnoreProperties);
				fcePerson.setDtLastUpdate(new Date());
			}

		}
		// copy orig Incomes to new Incomes
		List<FceIncomeDto> origAllIncomes = fceIncomeDao
				.getFceIncomeDtosByIdElig(originalFceApplicationRes.getIdFceEligibility());
		Set<FceIncome> allIncomes = fceEligibility.getFceIncomes();
		for (FceIncome fceIncome : allIncomes) {
			Optional<FceIncomeDto> origfceIncomeDtoOptional = origAllIncomes.stream().filter(o -> (o.getIdPerson()
					.equals(fceIncome.getPerson().getIdPerson()))
					&& ((!ObjectUtils.isEmpty(o.getIdIncRsrc()) && o.getIdIncRsrc().equals(fceIncome.getIdIncRsrc()))
							|| (ObjectUtils.isEmpty(o.getIdIncRsrc()) && ObjectUtils.isEmpty(fceIncome.getIdIncRsrc())))
					&& o.getIndIncomeSource().equals(fceIncome.getIndIncomeSource())
					&& o.getIndResourceSource().equals(fceIncome.getIndResourceSource())).findFirst();

			if (origfceIncomeDtoOptional.isPresent() && !ObjectUtils.isEmpty(origfceIncomeDtoOptional.get())) {
				FceIncomeDto origFceIncome = origfceIncomeDtoOptional.get();
				// can be changed in the page
				if (!TypeConvUtil.isNullOrEmpty(origFceIncome.getIndCountable())) {
					fceIncome.setIndCountable(origFceIncome.getIndCountable());
				}
				// can be changed in the page
				if (!TypeConvUtil.isNullOrEmpty(origFceIncome.getIndEarned())) {
					fceIncome.setIndEarned(origFceIncome.getIndEarned());
				}
				// can be changed in the page
				if (!TypeConvUtil.isNullOrEmpty(origFceIncome.getIndNone())) {
					fceIncome.setIndNone(origFceIncome.getIndNone());
				}
				fceIncome.setDtLastUpdate(new Date());

			}

		}
		// copy orig elig to new elig
		BeanUtils.copyProperties(fceEligibilityDto, fceEligibility, eligIgnoreProperties);
		// set court month , and year
		if (!ObjectUtils.isEmpty(fceApplicationDto.getNbrCourtMonth()))
			fceApplication.setNbrCourtMonth(fceApplicationDto.getNbrCourtMonth().intValue());
		if (!ObjectUtils.isEmpty(fceApplicationDto.getNbrCourtYear()))
			fceApplication.setNbrCourtYear(fceApplicationDto.getNbrCourtYear().intValue());
		fceEligibility.getFceApplications().add(fceApplication);
		fceEligibility.setDtLastUpdate(new Date());
		fceEligibilityDao.save(fceEligibility);

		// Preparing the response after copying the data
		// ApplicationDto copy
		fceApplicationRes.setFceApplicationDto(fceDao.transformFceApplication(fceApplication));
		// elgibility Dto copy
		BeanUtils.copyProperties(fceEligibility, fceApplicationRes.getFceEligibilityDto());
		fceApplicationRes.getFceEligibilityDto().setIdLastUpdatePerson(fceEligibility.getPerson().getIdPerson());
		fceApplicationRes.getFceEligibilityDto().setIdStage(fceEligibility.getStage().getIdStage());
		// FCE persons Copy
		Map<Long, FcePersonDto> personsMap = new HashMap<>();
		personList.stream().forEach(o -> {
			FcePersonDto personDto = new FcePersonDto();
			BeanUtils.copyProperties(o, personDto);
			personDto.setIdPerson(o.getPerson().getIdPerson());
			personDto.setNmPersonFull(o.getPerson().getNmPersonFull());
			personDto.setNbrSocialSecurity(o.getPerson().getNbrPersonIdNumber());
			personDto.setAddrPersonStLn1(o.getPerson().getAddrPersonStLn1());
			personDto.setAddrPersonCity(o.getPerson().getAddrPersonCity());
			personDto.setCdPersonState(o.getPerson().getCdPersonState());
			personDto.setAddrPersonZip(o.getPerson().getAddrPersonZip());
			personsMap.put(o.getPerson().getIdPerson(), personDto);
		});
		fceApplicationRes.setPersons(personsMap);
		// ADS Change - For DOMICILE Save the New Using Record's Person to FCE DOMICILE
		// PERSON WELFARE Table if any persons are selected
		if (ServiceConstants.NEW_USING.equals(fceReq.getPageMode())) {
			// Get the Person list using Old FCE Application ID
			List<FceDomicilePersonWelfDto> fceDomicilePersonWelfDtoList = new ArrayList<>();
			if (!ObjectUtils.isEmpty(originalFceApplicationRes.getFceApplicationDto())
					&& !ObjectUtils.isEmpty(originalFceApplicationRes.getFceApplicationDto().getIdFceApplication()))
				fceDomicilePersonWelfDtoList = fceDao
						.getFceDomicilePersonWelf(originalFceApplicationRes.getFceApplicationDto());
			// If the Person list is not empty
			if (!CollectionUtils.isEmpty(fceDomicilePersonWelfDtoList)) {
				for (FceDomicilePersonWelfDto selectedPersonWelfaredto : fceDomicilePersonWelfDtoList) {
					FceDomicilePersonWelfDto fceWelfaredto = new FceDomicilePersonWelfDto();
					fceWelfaredto.setIdFceApplication(fceApplicationRes.getFceApplicationDto().getIdFceApplication());
					fceWelfaredto.setIdCreatedPerson(fceApplicationRes.getFceApplicationDto().getIdPerson());
					fceWelfaredto.setIdLastUpdatePerson(fceApplicationRes.getFceApplicationDto().getIdLastUpdatePerson());
					fceWelfaredto.setIdPerson(selectedPersonWelfaredto.getIdPerson());
					fceDao.updateFceDomicilePersonWelf(fceWelfaredto);
				}
			}

		}
		return originalFceApplicationRes;
	}

}