package us.tx.state.dfps.service.adoptionasstnc.serviceimpl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.adoptionasstnc.AdoptionAsstncDto;
import us.tx.state.dfps.service.adoptionasstnc.dao.AdoptionAsstncDao;
import us.tx.state.dfps.service.adoptionasstnc.service.AdoptionAsstncService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AdoptionAsstncService Implementation Oct 30, 2017- 2:03:09 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class AdoptionAsstncServiceImpl implements AdoptionAsstncService {

	@Autowired
	private AdoptionAsstncDao adoptionAsstncDao;
	@Autowired
	private LookupDao lookupDao;
	@Autowired
	private PlacementDao placementDao;
	@Autowired
	private StageDao stageDao;
	@Autowired
	private EventUtilityService eventUtilityService;
	@Autowired
	private AdminWorkerDao adminWorkerDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-AdoptionAsstncServiceLog");

	/**
	 * Method Name: getNonRecurringAdoptionAsstncCeiling Method
	 * Description:Determines the maximum one-time payment amount for a
	 * non-recurring adoption assistance payment.
	 * 
	 * @return Double
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Double getNonRecurringAdoptionAsstncCeiling() {
		LOG.debug("Entering method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		LOG.debug("Exiting method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		return ServiceConstants.NONRECURRING_ADOPTION_ASSTNC_MAX;
	}

	/**
	 * Method Name: getRecurringAdoptionAsstncCeiling Method Description:
	 * Determines the monthly adoption assistance ceiling using the person id of
	 * the child being placed into adoption and the "effective" adoption
	 * assistance start date, which is either the start date of the adoption
	 * assistance record being added/updated or the start date of the earliest
	 * adoption assistance record for the person/resource combination, if others
	 * exist.
	 * 
	 * @param adoptionAsstncDto
	 * @return Double
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Double getRecurringAdoptionAsstncCeiling(AdoptionAsstncDto adoptionAsstncDto) {
		LOG.debug("Entering method getRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		Double adoptionAsstncCeiling = ServiceConstants.DoubleZero;

		AdoptionAsstncDto earliestAdoptionAsstncRecord = adoptionAsstncDao
				.queryEarliestAdoptionAsstncRecord(adoptionAsstncDto.getPersonId(), adoptionAsstncDto.getPayeeId());

		Date adoptionPlacementDate = adoptionPlacementDate(adoptionAsstncDto.getPersonId());

		Date startDateToUseForCalculations = adoptionAsstncDto.getDateStart();
		if (!TypeConvUtil.isNullOrEmpty(earliestAdoptionAsstncRecord)
				&& earliestAdoptionAsstncRecord.getAdoptionAsstncId() != adoptionAsstncDto.getAdoptionAsstncId()
				&& !TypeConvUtil.isNullOrEmpty(earliestAdoptionAsstncRecord.getDateStart())
				&& DateUtils.isBefore(earliestAdoptionAsstncRecord.getDateStart(), startDateToUseForCalculations)) {
			startDateToUseForCalculations = earliestAdoptionAsstncRecord.getDateStart();
		}
		if (DateUtils.isBefore(startDateToUseForCalculations, callDateUtils(ServiceConstants.MAY_01_2002))) {
			adoptionAsstncCeiling = ServiceConstants.NO_CEILING_MAX;
		} else if (DateUtils.isAfter(startDateToUseForCalculations, callDateUtils(ServiceConstants.APRIL_30_2002))
				&& DateUtils.isBefore(startDateToUseForCalculations, callDateUtils(ServiceConstants.SEPT_01_2003))) {
			adoptionAsstncCeiling = ServiceConstants.RECURRING_ADOPTION_ASSTNC_MAX_OLD;
		} else if (DateUtils.isAfter(startDateToUseForCalculations, callDateUtils(ServiceConstants.AUG_31_2003))) {
			String currentAloc = getAlocWithGreatestStartDate(adoptionAsstncDto.getPersonId());
			if ((DateUtils.isNull(adoptionPlacementDate) || adoptionPlacementDate.toString().equals("3500-12-31"))
					|| DateUtils.isBefore(adoptionPlacementDate, callDateUtils(ServiceConstants.ENHANCED_AA_DATE))) {
				if (isValid(currentAloc) && (ServiceConstants.PERSON_LOC_BASIC_1.equals(currentAloc)
						|| ServiceConstants.PERSON_LOC_BASIC_2.equals(currentAloc)
						|| ServiceConstants.PERSON_LOC_BASIC_3.equals(currentAloc))) {
					adoptionAsstncCeiling = ServiceConstants.RECURRING_ADOPTION_ASSTNC_BASIC_MAX_NEW;
				} else {
					adoptionAsstncCeiling = ServiceConstants.RECURRING_ADOPTION_ASSTNC_MODERATE_MAX_NEW;
				}
			}

		}

		LOG.debug("Exiting method getRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		return adoptionAsstncCeiling;
	}

	/**
	 * Method Name: callDateUtils Method Description:Converta input string to
	 * JavaDate
	 * 
	 * @param dateString
	 * @return Date
	 */
	private Date callDateUtils(String dateString) {
		Date javaDate = null;
		if (TypeConvUtil.isNullOrEmpty(dateString)) {
			return javaDate;
		}
		try {
			javaDate = DateUtils.toJavaDateFromInput(dateString);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return javaDate;
	}

	/**
	 * Method Name: isValid Method Description:Checks whether
	 * 
	 * @param value
	 * @return Boolean
	 */
	private Boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > ServiceConstants.Zero);
	}

	/**
	 * Method Name: getValidationErrors Method Description:Validates the
	 * specified adoption assistance amount based upon the adoption assistance
	 * type and the "effective" adoption assistance start date, which is either
	 * the start date of the adoption assistance record being added/updated or
	 * the start date of the earliest adoption assistance record for the
	 * person/resource combination, if others exist.
	 * 
	 * @param adoptionAsstncDto
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getValidationErrors(AdoptionAsstncDto adoptionAsstncDto) {
		LOG.debug("Entering method getValidationErrors in AdoptionAsstncService");
		Double adoptionAsstncCeiling;
		String errorMessage = "";

		if (ServiceConstants.CSUBTYPE_17.equals(adoptionAsstncDto.getAdoptionAsstncTypeCode())) {
			adoptionAsstncCeiling = getNonRecurringAdoptionAsstncCeiling();
			if (adoptionAsstncDto.getAdoptionAsstncAmount() > adoptionAsstncCeiling) {
				return lookupDao.getMessageByNumber(ServiceConstants.MSG_NONRECURR_SUB_AMT);
			}
		} else {

			AdoptionAsstncDto earliestAdoptionAsstncRecord = adoptionAsstncDao
					.queryEarliestAdoptionAsstncRecord(adoptionAsstncDto.getPersonId(), adoptionAsstncDto.getPayeeId());
			Date adoptionPlacementDate = adoptionPlacementDate(adoptionAsstncDto.getPersonId());
			Date startDateToUseForValidation = adoptionAsstncDto.getDateStart();
			if (!TypeConvUtil.isNullOrEmpty(earliestAdoptionAsstncRecord)
					&& earliestAdoptionAsstncRecord.getAdoptionAsstncId() != adoptionAsstncDto.getAdoptionAsstncId()
					&& !TypeConvUtil.isNullOrEmpty(earliestAdoptionAsstncRecord.getDateStart())
					&& DateUtils.isBefore(earliestAdoptionAsstncRecord.getDateStart(), startDateToUseForValidation)) {
				startDateToUseForValidation = earliestAdoptionAsstncRecord.getDateStart();
			}
			if (DateUtils.isAfter(startDateToUseForValidation, callDateUtils(ServiceConstants.AUG_31_2003))
					&& TypeConvUtil.isNullOrEmpty(getAlocWithGreatestStartDate(adoptionAsstncDto.getPersonId()))) {
				return lookupDao.getMessageByNumber(ServiceConstants.MSG_ADOPTION_ASSTNC_ALOC_REQ);
			} else {
				adoptionAsstncCeiling = getRecurringAdoptionAsstncCeiling(adoptionAsstncDto);
				if (DateUtils.isAfter(startDateToUseForValidation, callDateUtils(ServiceConstants.APRIL_30_2002))
						&& DateUtils.isBefore(startDateToUseForValidation, callDateUtils(ServiceConstants.SEPT_01_2003))
						&& adoptionAsstncDto.getAdoptionAsstncAmount() > adoptionAsstncCeiling) {
					return lookupDao.getMessageByNumber(ServiceConstants.MSG_RECURR_SUBAMT);
				} else if (DateUtils.isAfter(startDateToUseForValidation, callDateUtils(ServiceConstants.AUG_31_2003))
						&& adoptionAsstncDto.getAdoptionAsstncAmount() > adoptionAsstncCeiling) {
					if ((DateUtils.isNull(adoptionPlacementDate)
							|| adoptionPlacementDate.toString().equals("3500-12-31"))
							|| DateUtils.isBefore(adoptionPlacementDate,
									callDateUtils(ServiceConstants.ENHANCED_AA_DATE))) {
						errorMessage = lookupDao.getMessageByNumber(ServiceConstants.MSG_RECURR_SUBAMT_2);
						// errorMessage = lookupDao.add(errorMessage,
						// adoptionAsstncCeiling, 2);
					}
					return errorMessage;
				}
			}

		}

		LOG.debug("Exiting method getValidationErrors in AdoptionAsstncService");
		return errorMessage;
	}

	/**
	 * Method Name: getAlocWithGreatestStartDate Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) record with the greatest start date
	 * for the given person id.
	 * 
	 * @param personId
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getAlocWithGreatestStartDate(Long personId) {
		LOG.debug("Entering method getAlocWithGreatestStartDate in AdoptionAsstncService");
		String alocWithGreatestStartDate = null;

		alocWithGreatestStartDate = adoptionAsstncDao.queryAlocWithGreatestStartDate(personId);

		LOG.debug("Exiting method getAlocWithGreatestStartDate in AdoptionAsstncService");
		return alocWithGreatestStartDate;
	}

	/**
	 * Method Name: getPlacementWithGreatestStartDate Method
	 * Description:Retrieves the Placement record with the greatest start date
	 * for the given person id.
	 * 
	 * @param personId
	 * @param resourceId
	 * @return Date
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getPlacementWithGreatestStartDate(Long personId, Long resourceId) {
		String livingArrangement = null;
		PlacementDto placementDto = adoptionAsstncDao.queryPlacementWithGreatestStartDate(personId, resourceId);
		if (!ObjectUtils.isEmpty(placementDto) && !ObjectUtils.isEmpty(placementDto.getCdPlcmtLivArr())) {
			livingArrangement = placementDto.getCdPlcmtLivArr();
		}
		return livingArrangement;
	}

	/**
	 * Method Name: adoptionPlacementDate Method Description:Retrieves the most
	 * recent ADO Placement start date for the given person id
	 * 
	 * @param personId
	 * @return Date
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Date adoptionPlacementDate(Long personId) {
		LOG.debug("Entering method adoptionPlacementDate in AdoptionAsstncService");

		Date adoptionPlacementDate = null;

		adoptionPlacementDate = adoptionAsstncDao.adoptionPlacementDate(personId);

		LOG.debug("Exiting method adoptionPlacementDate in AdoptionAsstncService");
		return adoptionPlacementDate;
	}

	/**
	 * Method Name: fetchLatestAdptAsstncRecord Method Description:Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param idPerson
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long fetchLatestAdptAsstncRecord(Long idPerson) {
		LOG.debug("Entering method fetchLatestAdptAsstncRecord in AdoptionAsstncService");
		Long idAdptSub = ServiceConstants.ZERO_VAL;

		idAdptSub = adoptionAsstncDao.fetchLatestOpenAdptAsstncRecord(idPerson);

		LOG.debug("Exiting method fetchLatestAdptAsstncRecord in AdoptionAsstncService");
		return idAdptSub;
	}

	/**
	 * Method Name: fetchAdptAsstncDetail Method Description:Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AdoptionAsstncDto fetchAdptAsstncDetail(Long idAdptSub) {
		LOG.debug("Entering method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		AdoptionAsstncDto adoptionAsstncValueBean = null;

		adoptionAsstncValueBean = adoptionAsstncDao.fetchAdptAsstncRecord(idAdptSub);

		LOG.debug("Exiting method getRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		return adoptionAsstncValueBean;
	}

	/**
	 * Method Name: fetchAllAdptAsstncRecord Method Description:Fetches the all
	 * the adoption assistance record for the given person id if one exists.
	 * 
	 * @param personId
	 * @return List<AdoptionAsstncDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AdoptionAsstncDto> fetchAllAdptAsstncRecord(Long personId) {
		LOG.debug("Entering method fetchAllAdptAsstncRecord in AdoptionAsstncService");
		List<AdoptionAsstncDto> adoptionAsstncList = null;

		adoptionAsstncList = adoptionAsstncDao.fetchAllAdptAsstncRecord(personId);

		LOG.debug("Exiting method fetchAllAdptAsstncRecord in AdoptionAsstncService");
		return adoptionAsstncList;
	}

	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method
	 * Description:Determines if the the Adoption Subsidy is created Pre/Post
	 * Aug 22 2010 rollout.
	 * 
	 * @param idAdptSub
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAdptAsstncCreatedPostAugRollout(Long idAdptSub) {
		LOG.debug("Entering method isAdptAsstncCreatedPostAugRollout in AdoptionAsstncService");

		Boolean isCreatedPostRollOut = false;

		isCreatedPostRollOut = adoptionAsstncDao.isAdptAsstncCreatedPostAugRollout(idAdptSub);

		LOG.debug("Exiting method isAdptAsstncCreatedPostAugRollout in AdoptionAsstncService");
		return isCreatedPostRollOut;
	}

	/**
	 * Method Name: getRecentAdoptPlcmStartDate Method Description:Fetches the
	 * Adoptive Placement Start date for Resource and Child Combination if one
	 * exists.
	 * 
	 * @param personId
	 * @param idResource
	 * @return Date
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Date getRecentAdoptPlcmStartDate(Long personId, Long idResource) {
		LOG.debug("Entering method getRecentAdoptPlcmStartDate in AdoptionAsstncService");
		Date dtAdoptPlcmntStart = null;

		List<AdoptionAsstncDto> adptSubsidyList = adoptionAsstncDao.getAdoptAssistForRsrcAndChild(personId, idResource);

		if (TypeConvUtil.isNullOrEmpty(adptSubsidyList)) {
			dtAdoptPlcmntStart = fetchAdoptivePlacementStartDate(personId);
		}

		LOG.debug("Exiting method getRecentAdoptPlcmStartDate in AdoptionAsstncService");
		return dtAdoptPlcmntStart;
	}

	/**
	 * Method Name: fetchAdoptivePlacementStartDate Method Description:Fetches
	 * the start date adoptive placement if it exists
	 * 
	 * @param idPerson
	 * @return Date
	 */
	private Date fetchAdoptivePlacementStartDate(Long idPerson) {
		Date dtPlcmtStart = null;

		List<PlacementValueDto> latestPlacement = placementDao.findActivePlacements(idPerson);

		for (PlacementValueDto latestPlacementRow : latestPlacement) {

			if (latestPlacementRow != null && latestPlacementRow.getCdPlcmtLivArr() != null
					&& (ServiceConstants.CLAPRSFA_GT.equals(latestPlacementRow.getCdPlcmtLivArr())
							|| ServiceConstants.CPLCMT_71.equals(latestPlacementRow.getCdPlcmtLivArr()))) {
				dtPlcmtStart = latestPlacementRow.getDtPlcmtStart();
				break;
			}
		}

		return dtPlcmtStart;
	}

	/**
	 * Method Name: findEligibilityOrPrimayWorkerForStage Method
	 * Description:This function return if Eligibility Specialist that is
	 * assigned to stage. If None assigned returns Primary worker for Stage.
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long findEligibilityOrPrimayWorkerForStage(Long idStage) {
		LOG.debug("Entering method findEligibilityOrPrimayWorkerForStage in AdoptionAsstncService");
		Long idWorker = ServiceConstants.ZERO_VAL;

		String[] eligWorkerProfiles = { "72" };
		List<StagePersonLinkDto> fcWorkers = stageDao.findWorkersForStage(idStage, eligWorkerProfiles);

		if (!TypeConvUtil.isNullOrEmpty(fcWorkers)) {
			idWorker = fcWorkers.get(0).getIdPerson();
		} else {
			AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
			adminWorkerInpDto.setIdStage(idStage);
			adminWorkerInpDto.setCdStagePersRole(ServiceConstants.CROLEALL_PR);

			AdminWorkerOutpDto cinv51do = adminWorkerDao.getPersonInRole(adminWorkerInpDto);
			idWorker = cinv51do.getIdTodoPersAssigned();

		}

		LOG.debug("Exiting method findEligibilityOrPrimayWorkerForStage in AdoptionAsstncService");
		return idWorker;
	}

	/**
	 * Method Name: getAlocOnAdptAssistAgrmntSignDt Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) on the day when Adoption Assist
	 * Agreement was Signed
	 * 
	 * @param personId
	 * @param dtAdptAsstAgreement
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getAlocOnAdptAssistAgrmntSignDt(Long personId, Date dtAdptAsstAgreement) {
		LOG.debug("Entering method getAlocOnAdptAssistAgrmntSignDt in AdoptionAsstncService");
		String cdPLOCChild = null;

		cdPLOCChild = adoptionAsstncDao.getAlocOnAdptAssistAgrmntSignDt(personId, dtAdptAsstAgreement);

		LOG.debug("Exiting method getAlocOnAdptAssistAgrmntSignDt in AdoptionAsstncService");
		return cdPLOCChild;
	}

	/**
	 * Method Name: isAdptSubsidyCreatedOnAfterAppl Method Description:Returns
	 * true if the Subsidy was created on or after the Adopt Assistance
	 * Application was created
	 * 
	 * @param idEventSubsidy
	 * @param idEventApplication
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAdptSubsidyCreatedOnAfterAppl(Long idEventSubsidy, Long idEventApplication) {
		LOG.debug("Entering method isAdptSubsidyCreatedOnAfterAppl in AdoptionAsstncService");
		Boolean result = false;

		EventValueDto eventValueBeanSub = eventUtilityService.fetchEventInfo(idEventSubsidy);
		EventValueDto eventValueBeanAppl = eventUtilityService.fetchEventInfo(idEventApplication);

		if (DateUtils.isBefore(eventValueBeanSub.getDtEventOccurred(),
				eventValueBeanAppl.getDtEventOccurred()) == false) {
			result = true;
		}

		LOG.debug("Exiting method isAdptSubsidyCreatedOnAfterAppl in AdoptionAsstncService");
		return result;
	}

	/**
	 * Method Name: isAdptSubsidyEnded Method Description:Returns false if any
	 * of the adoption subsidies(for that stage) is not ended.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAdptSubsidyEnded(Long idStage) {
		LOG.debug("Entering method isAdptSubsidyEnded in AdoptionAsstncService");
		Boolean isAdptSubsidyEnded = true;

		List<Long> adoptionAsst = adoptionAsstncDao.getAdoptAssistForStage(idStage);
		for (Long idEvent : adoptionAsst) {
			AdoptionAsstncDto adoptionAsstncDto = adoptionAsstncDao.getAdoptAssistRsnClosure(idEvent);
			if (TypeConvUtil.isNullOrEmpty(adoptionAsstncDto.getClosureReasonCode())) {
				isAdptSubsidyEnded = false;
				break;
			}

		}

		LOG.debug("Exiting method isAdptSubsidyEnded in AdoptionAsstncService");
		return isAdptSubsidyEnded;
	}

	/**
	 * Method Name: getAdptPlcmtInfo Method Description:Retrieves the Adoptive
	 * Placement informations
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AdoptionAsstncDto getAdptPlcmtInfo(Long idAdptSub) {
		LOG.debug("Entering method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		AdoptionAsstncDto adoptionAsstncValueBean = null;

		adoptionAsstncValueBean = adoptionAsstncDao.getAdptPlcmtInfo(idAdptSub);

		LOG.debug("Exiting method getRecurringAdoptionAsstncCeiling in AdoptionAsstncService");
		return adoptionAsstncValueBean;
	}

	/**
	 * Method Name: fetchActiveAdpList Method Description:Get Person Active ADP
	 * Eligibilities for input person id
	 * 
	 * @param idPerson
	 * @return List<AdoptionAsstncDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AdoptionAsstncDto> fetchActiveAdpList(Long idPerson) {
		LOG.debug("Entering method fetchActiveAdpList in AdoptionAsstncService");
		List<AdoptionAsstncDto> activeAdpArr = null;

		activeAdpArr = adoptionAsstncDao.fetchActiveAdpForPerson(idPerson);

		LOG.debug("Exiting method fetchActiveAdpList in AdoptionAsstncService");
		return activeAdpArr;
	}

}
