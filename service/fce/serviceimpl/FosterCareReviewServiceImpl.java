package us.tx.state.dfps.service.fce.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.FosterCareReviewReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.ApplicationReasonsNotEligibleUtil;
import us.tx.state.dfps.service.common.utils.ApplicationUtil;
import us.tx.state.dfps.service.common.utils.EligibilityUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.common.utils.FceUtil;
import us.tx.state.dfps.service.common.utils.IncomeUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.common.utils.ReviewReasonsNotEligibleUtils;
import us.tx.state.dfps.service.common.utils.ReviewUtils;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.facility.dto.EligibilityDBDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.fce.dto.FceReviewDto;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.fce.service.FosterCareReviewService;
import us.tx.state.dfps.service.fostercarereview.dao.FceReasonNotEligibleDao;
import us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao;
import us.tx.state.dfps.service.fostercarereview.dao.FosterCareReviewDao;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 14, 2018- 2:56:16 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class FosterCareReviewServiceImpl implements FosterCareReviewService {

	/**
	 * 
	 */
	private static final String ID_EVENT = "idEvent";

	@Autowired
	private FosterCareReviewDao fosterCareReviewDao;

	@Autowired
	private PersonUtil personUtil;

	@Autowired
	private ReviewUtils reviewUtils;

	@Autowired
	private EligibilityUtil eligibilityUtil;

	@Autowired
	private ApplicationUtil applicationUtil;
	@Autowired
	private ReviewReasonsNotEligibleUtils reviewReasonsNotEligibleUtils;

	@Autowired
	private ApplicationReasonsNotEligibleUtil applicationReasonsNotEligibleUtil;

	@Autowired
	private FceDao fceApplicationDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private EventUtil eventUtil;

	@Autowired
	private PlacementDao placementDao;

	@Autowired
	private FceReviewDao fceReviewDao;

	@Autowired
	private FceReasonNotEligibleDao fceReasonNotEligibleDao;

	@Autowired
	private IncomeExpendituresDao incomeExpendituresDao;

	@Autowired
	private FceService fceService;

	@Autowired
	private IncomeUtil incomeUtil;

	@Autowired
	private FceUtil fceUtil;

	@Autowired
	private ReviewUtils reviewUtil;

	private static final Logger log = Logger.getLogger(FosterCareReviewServiceImpl.class);

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idEvent
	 * @return boolean
	 * @ @throws
	 *       ParseException
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isFCReviewCreatedOnAfter1stOct2010(Long idEvent) {
		log.debug("Entering method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewService");

		boolean result = fosterCareReviewDao.isFCReviewCreatedOnAfter1stOct2010(idEvent);

		log.debug("Exiting method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewService");
		return result;

	}

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idEvent
	 * @return boolean
	 * @ @throws
	 *       ParseException
	 */
	@Override
	public boolean isFCReviewCreatedOnAfter1stOct2010ForDetermineEligibility(Long idEvent) {
		log.debug("Entering method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewService");

		boolean result = fosterCareReviewDao.isFCReviewCreatedOnAfter1stOct2010(idEvent);

		log.debug("Exiting method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewService");
		return result;

	}

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010ByIdFceReview Method
	 * Description: Return true if the foster care review was created on or
	 * after Oct 1st 2010 FCON change date for Extended foster care.
	 * 
	 * @param idFceReview
	 * @return boolean @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(Long idFceReview) {
		log.debug("Entering method isFCReviewCreatedOnAfter1stOct2010ByIdFceReview in FosterCareReviewService");

		boolean result = fosterCareReviewDao.isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(idFceReview);

		log.debug("Exiting method isFCReviewCreatedOnAfter1stOct2010ByIdFceReview in FosterCareReviewService");
		return result;
	}

	/**
	 * Method Name: isEntryLevelLegalStatusPresent Method Description: Check if
	 * Entry level Legal Status present for the child
	 * 
	 * @param fosterCareReviewDto
	 * @return boolean @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isEntryLevelLegalStatusPresent(FosterCareReviewDto fosterCareReviewDto) {
		log.debug("Entering method isEntryLevelLegalStatusPresent in FosterCareReviewService");

		boolean result = fosterCareReviewDao.isEntryLevelLegalStatusPresent(fosterCareReviewDto);
		log.debug("Exiting method isEntryLevelLegalStatusPresent in FosterCareReviewService");
		return result;

	}

	/**
	 * Method Name: save Method Description: save data in bean
	 * 
	 * @param fosterCareReviewReq
	 * @ @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void save(FosterCareReviewReq fosterCareReviewReq) {
		save(fosterCareReviewReq, true);
	}

	/**
	 * Method Name: save Method Description: save data in bean
	 * 
	 * @param fosterCareReviewReq
	 * @param changeEventStatus
	 * @
	 */
	private void save(FosterCareReviewReq fosterCareReviewReq, boolean changeEventStatus) {

		FosterCareReviewDto fosterCareReviewDto = fosterCareReviewReq.getFosterCareReviewDto();

		fceUtil.verifyCanSave(fosterCareReviewDto);
		long idEvent = fosterCareReviewDto.getIdEvent();
		FceUtil.verifyNonZero(ID_EVENT, idEvent);
		reviewUtils.saveReview(fosterCareReviewDto);
		eligibilityUtil.saveEligibility(fosterCareReviewDto);
		incomeUtil.saveIncome(fosterCareReviewDto.getIncomeForChild());
		incomeUtil.saveIncome(fosterCareReviewDto.getResourcesForChild());
		if (changeEventStatus) {
			eventUtil.changeEventStatus(idEvent, ServiceConstants.NEW_EVENT, ServiceConstants.PROCESS_EVENT);
		}

		log.debug("Exiting method save in FosterCareReviewService");
	}

	/**
	 * Method Name: submit Method Description: check to make sure the
	 * application is complete before creating a to do to give the eligibility
	 * specialist
	 * 
	 * @param fosterCareReviewReq
	 * @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void submit(FosterCareReviewReq fosterCareReviewReq) {
		FosterCareReviewDto fosterCareReviewDto = fosterCareReviewReq.getFosterCareReviewDto();
		save(fosterCareReviewReq, true);
		long idEvent = fosterCareReviewDto.getIdEvent();
		FceUtil.verifyNonZero(ID_EVENT, idEvent);
		eventUtil.completeTodosForEventId(idEvent);

	}

	/**
	 * Method Name: updateSystemDerivedParentalDeprivation Method Description:
	 * This is executed in a separate transaction so we ensure we have all the
	 * latest information when we calculate system-derived parental deprivation
	 * 
	 * @param commonEventIdReq
	 * @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSystemDerivedParentalDeprivation(CommonEventIdReq commonEventIdReq) {
		FceReviewDto fceReviewDto = reviewUtils.findReviewForReviewEvent(commonEventIdReq.getIdEvent());
		FceEligibilityDto fceEligibilityDto = null;
		if (!ObjectUtils.isEmpty(fceReviewDto) && !ObjectUtils.isEmpty(fceReviewDto.getIdFceApplication())
				&& fceReviewDto.getIdFceApplication() > 0) {
			fceEligibilityDto = eligibilityUtil.findEligibilityByIdFceApplication(fceReviewDto.getIdFceApplication());

			boolean ineligible = ApplicationReasonsNotEligibleUtil
					.ineligibleDueToAnyReasonOtherThanCitizenshipRequirement(fceEligibilityDto);

			if (ineligible) {
				fceEligibilityDto
						.setTxtDeterminationComments(ServiceConstants.MAGIC_DONT_SHOW_ELIGIBILITY_CONFIRMATION);
				String indParentalDeprivation = fceEligibilityDto.getIndParentalDeprivation();
				fceEligibilityDto.setIndMeetsDpOrNotSystem(indParentalDeprivation);
				fceEligibilityDto.setIndMeetsDpOrNotEs(indParentalDeprivation);
				fceEligibilityDto.setIndParentalDeprivation(indParentalDeprivation);
			} else {
				if (ServiceConstants.MAGIC_DONT_SHOW_ELIGIBILITY_CONFIRMATION
						.equals(fceEligibilityDto.getTxtDeterminationComments())) {
					// clear MAGIC
					fceEligibilityDto.setTxtDeterminationComments(null);
					fceEligibilityDto.setIndMeetsDpOrNotEs(null);
					fceEligibilityDto.setIndParentalDeprivation(null);
				}

				FceApplicationDto fceApplicationDto = applicationUtil
						.findApplication(fceEligibilityDto.getIdFceApplication());

				String cdLivingMonthRemoval = fceApplicationDto.getCdLivingMonthRemoval();

				if ((cdLivingMonthRemoval != null) && (!ServiceConstants.CFCELIV_B.equals(cdLivingMonthRemoval))
						&& (!ServiceConstants.CFCELIV_O.equals(cdLivingMonthRemoval))) {

					fceEligibilityDto.setIndMeetsDpOrNotSystem(ServiceConstants.STRING_IND_Y);
				} else if (!ObjectUtils.isEmpty(fceReviewDto)
						&& ServiceConstants.Y.equals(fceReviewDto.getIndRightsTerminated())) {

					fceEligibilityDto.setIndMeetsDpOrNotSystem(ServiceConstants.STRING_IND_Y);
				} else {
					String cdLivingCondition = fceReviewDto.getCdLivingConditionCurrent();

					applicationReasonsNotEligibleUtil.calculateSystemDerivedParentalDeprivation(cdLivingCondition,
							fceApplicationDto, fceEligibilityDto);
				}
			}

		}

	}

	/**
	 * Method Name: determineEligibility Method Description: calculate
	 * eligibility/reasons not eligible
	 * 
	 * @param fosterCareReviewReq
	 * @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FosterCareReviewDto determineEligibility(FosterCareReviewDto fosterCareReviewDto) {

		FceUtil.verifyNonZero("idFceEligibility", fosterCareReviewDto.getIdFceEligibility());
		FceUtil.verifyNonZero("idFceReview", fosterCareReviewDto.getIdFceReview());

		FceEligibilityDto fceEligibilityDto = eligibilityUtil
				.findEligibilityFordetermineEligibility(fosterCareReviewDto.getIdFceEligibility());
		FceReviewDto fceReviewDto = reviewUtils.findReview(fosterCareReviewDto.getIdFceReview());

		reviewUtils.syncFceReviewStatus(fceEligibilityDto, fceReviewDto);
		fceApplicationDao.deleteFceReasonsNotEligible(fosterCareReviewDto.getIdFceEligibility());

		FceContextDto fceContext = getFceContext(fosterCareReviewDto);
		fceEligibilityDto = reviewReasonsNotEligibleUtils.calculateReasonsNotEligible(fceContext, fceEligibilityDto,
				fceReviewDto);
		fceContext.setFceEligibilityDto(fceEligibilityDto);
		List<String> reasonsNotEligible = getReasonsNotEligible(fceContext);

		if (isFCReviewCreatedOnAfter1stOct2010ForDetermineEligibility(fosterCareReviewDto.getIdEvent())) {
			String cdEligibilityActual = getExtendedFosterCareEligibility(new ArrayList<>(), fosterCareReviewDto);
			fceEligibilityDto.setCdEligibilityActual(cdEligibilityActual);
		}

		if (reasonsNotEligible.isEmpty()) {
			fceEligibilityDto.setIndEligible(ServiceConstants.STRING_IND_Y);
		} else {
			fceEligibilityDto.setIndEligible(ServiceConstants.STRING_IND_N);
		}
		fosterCareReviewDto.setFceEligibilityDto(fceEligibilityDto);

		fceApplicationDao.createFceReasonsNotEligible(reasonsNotEligible, fosterCareReviewDto.getIdFceEligibility());
		List<FceReasonNotEligibleDto> reasonsNotEligibleList = fceReasonNotEligibleDao
				.findReasonsNotEligible( fosterCareReviewDto.getIdFceEligibility());
		fosterCareReviewDto.setReasonsNotEligible(reasonsNotEligibleList);
		
		fosterCareReviewDto.setFceReviewCdPersonCitizenship(fceReviewDto.getCdPersonCitizenship());
		fosterCareReviewDto.setFceEligibilityCdPersonCitizenship(fceEligibilityDto.getCdPersonCitizenship());
		return fosterCareReviewDto;
		
	}

	public FceContextDto getFceContext(FosterCareReviewDto fosterCareReviewDto) {
		FceContextDto fceContextDto = new FceContextDto();

		Long idEvent = fosterCareReviewDto.getIdEvent();
		FceUtil.verifyNonZero(ID_EVENT, idEvent);
		fceContextDto.setIdEvent(idEvent);

		Long idPerson = fosterCareReviewDto.getIdPerson();
		FceUtil.verifyNonZero("idPerson", idPerson);
		fceContextDto.setIdPerson(idPerson);

		Long idFceApplication = fosterCareReviewDto.getIdFceApplication();
		FceUtil.verifyNonZero("idFceApplication", idFceApplication);
		fceContextDto.setIdFceApplication(idFceApplication);

		Long idFcePerson = fosterCareReviewDto.getIdFcePerson();
		FceUtil.verifyNonZero("idFcePerson", idFcePerson);
		fceContextDto.setIdFcePerson(idFcePerson);

		Long idFceEligibility = fosterCareReviewDto.getIdFceEligibility();
		FceUtil.verifyNonZero("idFceEligibility", idFceEligibility);
		fceContextDto.setIdFceEligibility(idFceEligibility);

		Long idFceReview = fosterCareReviewDto.getIdFceReview();
		FceUtil.verifyNonZero("idFceReview", idFceReview);
		fceContextDto.setIdFceReview(idFceReview);
		return fceContextDto;
	}

	public List<String> getReasonsNotEligible(FceContextDto fceContext) {

		// get the Application's Reasons Not Eligible
		long idFceApplication = fceContext.getIdFceApplication();

		FceApplicationDto fceApplicationDto = fceApplicationDao.getFceApplicationById(idFceApplication);
		//Fix for defect 13695 - Invld error msg ending sys gen'd elig
		FceEligibilityDto applicationFceEligibilityDto = fceApplicationDao
				.getFceEligibility(fceApplicationDto.getIdFceEligibility());
		
		List<String> list = ApplicationReasonsNotEligibleUtil.getReasonsNotEligible(applicationFceEligibilityDto);

		if ((ServiceConstants.CAPS.equals(fceApplicationDto.getCdApplication()))
				&& (ServiceConstants.N.equals(applicationFceEligibilityDto.getIndEligible())) && (list.isEmpty())) {

			throw new ServiceLayerException(
					"Please check at least one reason the child was not eligible at the time of the application on the Non-Title IV-E Checklist.");
		}

		list.remove(CodesConstant.CFCERNE_A02);

		/*
		 * For Modification of extended foster care, the child can be qualified
		 * for IVE up till the age of 21 years under certain conditions. This
		 * while FC reviewing we have to overlook this reason from Application (
		 * CFCERNE_A01 - Childis over 18 ). It will be possible that on
		 * application the child is not eligible for IVE because of CFCERNE_A01
		 * but in review he should be eligible. get the Review's Reasons Not
		 * Eligible
		 */

		FceReviewDto fceReviewDto = reviewUtils.findReview(fceContext.getIdFceReview());
		// We have to get the DataBean so we can compare the age
		// if the person is over >= 18.

		// uncomment the line once read method is developed
		FosterCareReviewDto fosterCareReviewDB = read(fceReviewDto.getIdStage(), fceReviewDto.getEvent().getIdEvent(),
				fceContext.getIdPerson(), Boolean.FALSE);

		// SIR 1002721 - If new FC then proceed with new age related logic
		if (isFCReviewCreatedOnAfter1stOct2010ForDetermineEligibility(fceReviewDto.getEvent().getIdEvent())) {
			getExtendedFosterCareEligibility(list, fosterCareReviewDB);
		}
		// Else old FC review, so continue with Old age related logic
		else {
			// We have to calculate if the child is "too old" for Title
			// IV-E. The child is eligible until the month following their
			// 18th
			// birthday.
			// Depending on the Extended Education section them may be
			// eligible
			// longer.

			// Set them as being eligible if under age 19, otherwise make
			// them
			// ineligible
			Boolean tooOldForIVE = (fosterCareReviewDB.getNbrAgeYears() < 19L) ? Boolean.FALSE : Boolean.TRUE;

			// We need to find out the last day of the month of the
			// child's 18th birthday
			Date afterDate = DateUtils.addToDate(fosterCareReviewDB.getDtBirth(), 18, 0, 0);

			// If it's after their birthmonth on their 18th birthyear and
			// the
			// two
			// questions weren't answered yes, then they aren't Title IV-E
			// eligible.
			if (DateUtils.isAfter(new Date(), afterDate)
					&& (ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndChildEnrolled())
							|| ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndChildCmplt19()))) {
				tooOldForIVE = Boolean.TRUE;
			}
			if (tooOldForIVE == Boolean.TRUE) {
				list.add(CodesConstant.CFCERNE_R01);
			}
		}
		//Fix for defect 13695 - Invld error msg ending sys gen'd elig
		if (ServiceConstants.N.equalsIgnoreCase(fceContext.getFceEligibilityDto().getIndChildQualifiedCitizen())) {
			list.add(CodesConstant.CFCERNE_R02);
		}

		if ((ServiceConstants.Y.equalsIgnoreCase(fceReviewDto.getIndPrmncyHearingsDue()))
				&& (ServiceConstants.N.equalsIgnoreCase(fceReviewDto.getIndPrmncyHrngs12Month()))) {
			list.add(CodesConstant.CFCERNE_R06);
		}
		return list;
	}

	public String getExtendedFosterCareEligibility(List<String> list, FosterCareReviewDto fosterCareReviewDB) {
		String calculatedCdEligibility = ServiceConstants.EMPTY_STRING;
		boolean isChildAtleast20Yrs11Month = false;

		// Since the Logic will change title IVE eligible age from 18 to 21 so
		// if the application has this reason, then remove it.
		list.remove(CodesConstant.CFCERNE_A01);

		if ((fosterCareReviewDB.getNbrAgeYears() == 20L && fosterCareReviewDB.getNbrAgeMonths() >= 11L)
				|| fosterCareReviewDB.getNbrAgeYears() > 20L) {
			isChildAtleast20Yrs11Month = true;
		}

		// If Child is at-least 20 years and 11 months, then only one question
		// is required to be answered
		if (isChildAtleast20Yrs11Month) {
			// if the answer is NO then, not eligible for any assistance.
			// else eligible, but only for State paid assistance
			if (ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndHighSchool())) {
				// Child Not eligible for any assistance
				list.add(CodesConstant.CFCERNE_R08);
				calculatedCdEligibility = CodesConstant.CELIGIBI_040;
			} else {
				// Not IV E eligible. State paid only
				list.add(CodesConstant.CFCERNE_R09);
				calculatedCdEligibility = CodesConstant.CELIGIBI_020;
			}
		}
		// else If Child is under 20 years and 11 months and atleast 18 years
		// then check for all questions.
		else if (fosterCareReviewDB.getNbrAgeYears() >= 18L) {
			calculatedCdEligibility = processFor18to20yrs11mths(list, fosterCareReviewDB);
		} // End else if- Child is under 20 years and 11 months and atleast 18
			// years then check for all questions.

		// if Child is 18 or over then check if the calculatedEligibilityType is
		// not empty
		// if empty then find the recent open Eligibility's actual eligibility
		// type.
		if (fosterCareReviewDB.getNbrAgeYears() >= 18L
				&& ServiceConstants.EMPTY_STRING.equals(calculatedCdEligibility)) {
			// Now find the recent open Eligibility's actual eligibility type.
			EligibilityDBDto eligibilityDB = eligibilityUtil.findLatestLegacyEligibility(fosterCareReviewDB.getIdCase(),
					fosterCareReviewDB.getIdPerson());

			calculatedCdEligibility = eligibilityDB.getCdEligActual();
			// Fix for defect 13358 - 32 Preexisting - INC000004839988 - CPS - Child
			// erroneously listed as not eligible for Title IV-E
			if (!ObjectUtils.isEmpty(calculatedCdEligibility)
					&& !CodesConstant.CELIGIBI_010.equals(calculatedCdEligibility)) {
				// Not IV E eligible.
				list.add(CodesConstant.CFCERNE_R09);
			}
		}
		return calculatedCdEligibility;
	}

	/**
	 * Method Name: processFor18to20yrs11mths Method Description:
	 * 
	 * @param list
	 * @param fosterCareReviewDB
	 * @param calculatedCdEligibility
	 * @return
	 */
	private String processFor18to20yrs11mths(List<String> list, FosterCareReviewDto fosterCareReviewDB) {
		// Set these variables as per the question answered.
		boolean first5QuestAllNoInExtndFC = false;
		boolean allQuestNoInExtndFC = false;
		String calculatedCdEligibility = ServiceConstants.EMPTY_STRING;

		if (ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndHighSchool())
				&& ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndPostSecondary())
				&& ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndPromoteEmployment())
				&& ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndEmployMinHrs())
				&& ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndIncapableMedCond())) {
			first5QuestAllNoInExtndFC = true;
		}

		if (first5QuestAllNoInExtndFC
				&& ServiceConstants.N.equalsIgnoreCase(fosterCareReviewDB.getIndChildAccptdHigher())) {
			allQuestNoInExtndFC = true;
		}

		// If all 6 quest are answered as No, then child is not eligible for any
		// assistance
		if (allQuestNoInExtndFC) {
			// Child Not eligible for any assistance
			list.add(CodesConstant.CFCERNE_R08);
			calculatedCdEligibility = CodesConstant.CELIGIBI_040;
		} else {
			/*
			 * SIR 1006232 - Removing the logic that marks a child Ineligible or
			 * eligible for State Paid only based on the date the VEFCA
			 * agreement was signed. Henceforth, the VEFCA signature date will
			 * not have any significance in eligibility determination.
			 */
			if (ServiceConstants.IND_TRUE.equalsIgnoreCase(fosterCareReviewDB.getIndChildAccptdHigher())
					&& first5QuestAllNoInExtndFC) {
				// Not IV E eligible. State paid only
				list.add(CodesConstant.CFCERNE_R09);
				calculatedCdEligibility = CodesConstant.CELIGIBI_020;

			} // End SIR 1006232
		}
		return calculatedCdEligibility;
	}

	/**
	 * Method Name: closeEligibility Method Description: Prematurely close the
	 * eligibility
	 * 
	 * @param fosterCareReviewReq
	 * @
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void closeEligibility(FosterCareReviewReq fosterCareReviewReq) {

		save(fosterCareReviewReq, false);

		Long idEvent = fosterCareReviewReq.getFosterCareReviewDto().getIdEvent();
		FceUtil.verifyNonZero(ID_EVENT, idEvent);

		Set<String> statuses = new HashSet<>();
		statuses.add(EventUtil.NEW_EVENT);
		statuses.add(EventUtil.PROCESS_EVENT);
		statuses.add(EventUtil.PENDING_EVENT);

		eventUtil.changeEventStatus(idEvent, statuses, EventUtil.COMPLETE_EVENT, null);

		eventUtil.completeTodosForEventId(idEvent);

	}

	/**
	 * Method Name: confirm Method Description: confirm the eligibility and
	 * close the review
	 * 
	 * @param fosterCareReviewReq
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes confirm(FosterCareReviewReq fosterCareReviewReq) {
		return fosterCareReviewDao.confirm(fosterCareReviewReq.getFosterCareReviewDto());
	}

	/**
	 * Method Name: readFosterCareReview Method Description: Read data for
	 * FosterCareReviewBean; sync data with the rest of the system
	 * 
	 * @param fosterCareReviewReq
	 * @return FosterCareReviewDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FosterCareReviewDto read(Long idStage, Long idEvent, Long idLastUpdatePerson, Boolean indNewUsing) {
		FosterCareReviewDto fosterCareReviewDB = null;

		log.debug("Entering method read in FosterCareReviewService");
		FceContextDto fceContext = null;
		if (!indNewUsing) {
			fceContext = fceService.initializeFceReview(idStage, idEvent, idLastUpdatePerson);
			if (fceContext.getIdEvent() != 0l && ObjectUtils.isEmpty(idEvent)) {
				fceContext = fceService.initializeFceReview(idStage, fceContext.getIdEvent(), idLastUpdatePerson);
			}
		} else {
			fceContext = fceService.newUsingFceReview(idStage, idEvent, idLastUpdatePerson);
		}

		FcePersonDto fcePersonDB = fceApplicationDao.findFcePerson(fceContext.getIdFcePerson());
		PersonDto person = personDao.getPersonById(fceContext.getIdPerson());
		BeanUtils.copyProperties(person, fcePersonDB);

		FceReview fceReviewDB = fceReviewDao.getById(fceContext.getIdFceReview());

		FceReviewDto fceReviewDto = new FceReviewDto();
		BeanUtils.copyProperties(fceReviewDB, fceReviewDto);
		EventDto eventDto = new EventDto();
		BeanUtils.copyProperties(fceReviewDB.getEventByIdEvent(), eventDto);
		fceReviewDto.setEvent(eventDto);
		if (!ObjectUtils.isEmpty(fceReviewDB.getEventByIdCurrentPlacementEvent()))
			fceReviewDto.setIdCurrentPlacementEvent(fceReviewDB.getEventByIdCurrentPlacementEvent().getIdEvent());
		StageDto stageDto = new StageDto();
		BeanUtils.copyProperties(fceReviewDB.getStage(), stageDto);
		fceReviewDto.setStage(stageDto);

		// set entity databeans on foster care review
		fosterCareReviewDB = new FosterCareReviewDto();
		setFceEligibility(fceContext, fosterCareReviewDB);
		if (!indNewUsing)
			setFceReview(fceContext, fceReviewDto, fosterCareReviewDB);
		else {
			FceReviewDto compFceReviewDto = reviewUtil.findReviewForReviewEvent(idEvent);
			FceReview compFceReviewDB = fceReviewDao.getById(compFceReviewDto.getIdFceReview());
			BeanUtils.copyProperties(compFceReviewDB, compFceReviewDto);
			setFceReviewForNewUsing(fceContext, fceReviewDto, compFceReviewDto, fosterCareReviewDB);
		}
		setFcePerson(fcePersonDB, fosterCareReviewDB);
		fosterCareReviewDB.setCdEventStatus(eventDao.getEventByid(fceContext.getIdEvent()).getCdEventStatus());
		// copy medicaid/SSN
		long idPerson = fceContext.getIdPerson();
		fosterCareReviewDB.setNbrMedicaid(personUtil.findMedicaid(idPerson));
		fosterCareReviewDB.setNbrSocialSecurity(personUtil.findSsn(idPerson));
		// set employee fields
		PersonValueDto employeeDB = personDao.findPrimaryWorkerForStage(idStage);
		fosterCareReviewDB.setNbrEmployeePersonPhone(employeeDB.getPhone());
		fosterCareReviewDB.setNmEmployeePersonFull(employeeDB.getFullName());
		// set cdLivingMonthRemoval and cdApplication
		FceApplicationDto fceApplicationLocal = fceApplicationDao
				.getFceApplicationById(fceContext.getIdFceApplication());
		fosterCareReviewDB.setCdLivingMonthRemoval(fceApplicationLocal.getCdLivingMonthRemoval());
		fosterCareReviewDB.setCdApplication(fceApplicationLocal.getCdApplication());
		// nbrAgeYears, nbrAgeMonths
		long nbrAgeYears = 0;
		long nbrAgeMonths = 0;
		Date dtBirth = new Date(fosterCareReviewDB.getDtBirth().getTime());
		Date dtReviewComplete = fosterCareReviewDB.getDtReviewComplete();

		Date dtAgeCalculated = Calendar.getInstance().getTime();
		if (dtReviewComplete != null) {
			dtAgeCalculated = dtReviewComplete;
		}
		if (!ObjectUtils.isEmpty(dtBirth)) {
			// Needs to resolve for R2 release
			nbrAgeMonths = DateUtils.getAgeInMonths(dtBirth, dtAgeCalculated);
			nbrAgeYears = nbrAgeMonths / 12;
			nbrAgeMonths = nbrAgeMonths % 12;
		}
		fosterCareReviewDB.setNbrAgeYears(nbrAgeYears);
		fosterCareReviewDB.setNbrAgeMonths(nbrAgeMonths);
		long idFceEligibility = fceContext.getIdFceEligibility();
		// set income lists on foster care review
		List<FceIncomeDto> incomeForChild = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_CHILD);
		fosterCareReviewDB.setIncomeForChild(incomeForChild);
		List<FceIncomeDto> resourcesForChild = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_RESOURCE, ServiceConstants.FCE_CHILD);
		fosterCareReviewDB.setResourcesForChild(resourcesForChild);
		// set reasonsNotEligible
		List<FceReasonNotEligibleDto> reasonsNotEligible = fceReasonNotEligibleDao
				.findReasonsNotEligible(idFceEligibility);
		fosterCareReviewDB.setReasonsNotEligible(reasonsNotEligible);
		// set original application's eligibility status
		long idFceApplication = fceContext.getIdFceApplication();

		String indEligible = ServiceConstants.N;
		FceEligibilityDto fceEligibilityDto = eligibilityUtil.findEligibilityByIdFceApplication(idFceApplication);
		if (fceEligibilityDto.getIndEligible() != null
				&& ServiceConstants.STRING_IND_Y.equals(fceEligibilityDto.getIndEligible())) {
			indEligible = ServiceConstants.Y;
		}

		fosterCareReviewDB.setIndOriginalApplicationEligible(indEligible);
		// The !!! goofy thing in FceBean is a feature now
		// If id_event on fce_application == id_event on fce_review,
		// then the application was a "dummied" application for a CAPS child
		fosterCareReviewDB.setIndReviewForCapsChild(ServiceConstants.N);
		if ((fceApplicationLocal.getIdEvent() != null)
				&& (fceApplicationLocal.getIdEvent().longValue() == fceReviewDB.getEventByIdEvent().getIdEvent())) {
			fosterCareReviewDB.setIndReviewForCapsChild(ServiceConstants.Y);
		}
		// set multiple placement indicator
		List<PlacementDto> placements = placementDao.findActivePlacementsForFosterCare(idPerson);
		if (CollectionUtils.isEmpty(placements) || placements.size() <= 1) {
			fosterCareReviewDB.setIndMultipleActivePlacements(ServiceConstants.N);
		} else {
			fosterCareReviewDB.setIndMultipleActivePlacements(ServiceConstants.Y);
		}
		// Set the placement list to be displayed on the page
		List<PlacementDto> recentPlacements = placementDao.findRecentPlacements(idStage);
		fosterCareReviewDB.setPlacements(recentPlacements);
		fostercareReviewIndDefaultVal(fosterCareReviewDB);
		return fosterCareReviewDB;
	}

	/**
	 * Method Name: setFcePerson Method Description: Method for setting
	 * fce_person db.
	 * 
	 * @param fcePersonDB
	 * @param fosterCareReviewDB
	 */
	private void setFcePerson(FcePersonDto fcePersonDB, FosterCareReviewDto fosterCareReviewDB) {
		fosterCareReviewDB.setFcePersonDto(fcePersonDB);

		fosterCareReviewDB.setIdPerson(fcePersonDB.getIdPerson());
		fosterCareReviewDB.setDtBirth(fcePersonDB.getDtBirth());
		if (!ObjectUtils.isEmpty(fcePersonDB.getNbrPersonAge()))
			fosterCareReviewDB.setNbrAge(fcePersonDB.getNbrPersonAge().longValue());
		fosterCareReviewDB.setNmPersonFull(fcePersonDB.getNmPersonFull());
		fosterCareReviewDB.setIndPersonHmRemoval(fcePersonDB.getIndPersonHmRemoval());
		fosterCareReviewDB.setIndCertifiedGroup(fcePersonDB.getIndCertifiedGroup());
		fosterCareReviewDB.setIdFcePerson(fcePersonDB.getIdFcePerson());
		fosterCareReviewDB.setIdFceEligibility(fcePersonDB.getIdFceEligibility());
		fosterCareReviewDB.setDtLastUpdate(fcePersonDB.getDtLastUpdate());
		fosterCareReviewDB.setCdRelInt(fcePersonDB.getCdRelInt());
		fosterCareReviewDB.setIndDobApprox(fcePersonDB.getIndDobApprox());
	}

	/**
	 * Method Name: setFceEligibility Method Description: Method for setting
	 * fce_eligibility db.
	 * 
	 * @param fceContext
	 * @param fosterCareReviewDB
	 */
	private void setFceEligibility(FceContextDto fceContext, FosterCareReviewDto fosterCareReviewDB) {
		fosterCareReviewDB.setFceEligibilityDto(fceContext.getFceEligibilityDto());
		FceEligibilityDto fceEligibilityDto = fceContext.getFceEligibilityDto();
		fosterCareReviewDB.setNbrCertifiedGroup(fceEligibilityDto.getNbrCertifiedGroup());
		fosterCareReviewDB.setAmtCountableIncome(fceEligibilityDto.getAmtCountableIncome());
		fosterCareReviewDB.setAmtGrossEarnedCrtfdGrp(fceEligibilityDto.getAmtGrossEarnedCrtfdGrp());
		fosterCareReviewDB.setAmtGrossUnearnedCrtfdGrp(fceEligibilityDto.getAmtGrossUnearnedCrtfdGrp());
		fosterCareReviewDB.setAmtIncomeLimit(fceEligibilityDto.getAmtIncomeLimit());
		fosterCareReviewDB.setAmtPweIncome(fceEligibilityDto.getAmtPweIncome());
		fosterCareReviewDB.setAmtSsi(fceEligibilityDto.getAmtSsi());
		fosterCareReviewDB.setAmtStepparentAlimony(fceEligibilityDto.getAmtStepparentAlimony());
		fosterCareReviewDB.setAmtStepparentAllowance(fceEligibilityDto.getAmtStepparentAllowance());
		fosterCareReviewDB.setAmtStepparentAppliedIncome(fceEligibilityDto.getAmtStepparentAppliedIncome());
		fosterCareReviewDB.setAmtStepparentCntblUnearned(fceEligibilityDto.getAmtStepparentCntblUnearned());
		fosterCareReviewDB.setAmtStepparentGrossEarned(fceEligibilityDto.getAmtStepparentGrossEarned());
		fosterCareReviewDB.setAmtStepparentOutsidePmnt(fceEligibilityDto.getAmtStepparentOutsidePmnt());
		fosterCareReviewDB.setAmtStepparentTotalCntbl(fceEligibilityDto.getAmtStepparentTotalCntbl());
		fosterCareReviewDB.setAmtStepparentWorkDeduct(fceEligibilityDto.getAmtStepparentWorkDeduct());
		fosterCareReviewDB.setCdBlocChild(fceEligibilityDto.getCdBlocChild());
		fosterCareReviewDB.setCdEligibilityActual(fceEligibilityDto.getCdEligibilityActual());
		fosterCareReviewDB.setCdEligibilitySelected(fceEligibilityDto.getCdEligibilitySelected());
		fosterCareReviewDB.setCdMedicaidEligibilityType(fceEligibilityDto.getCdMedicaidEligibilityType());
		fosterCareReviewDB.setCdPweIrregularUnder100(fceEligibilityDto.getCdPweIrregularUnder100());
		fosterCareReviewDB.setCdPweSteadyUnder100(fceEligibilityDto.getCdPweSteadyUnder100());
		fosterCareReviewDB.setDtEligDtrmntnStart(fceEligibilityDto.getDtEligDtrmntnStart());
		fosterCareReviewDB.setDtEligibilityEnd(fceEligibilityDto.getDtEligibilityEnd());
		fosterCareReviewDB.setDtRemovalChildOrdered(fceEligibilityDto.getDtRemovalChildOrdered());
		fosterCareReviewDB.setDtReviewDate(fceEligibilityDto.getDtReviewDate());
		fosterCareReviewDB.setDtRsnblEffortPreventRem(fceEligibilityDto.getDtRsnblEffortPreventRem());
		fosterCareReviewDB.setFceEligibilityCdPersonCitizenship(fceEligibilityDto.getCdPersonCitizenship());
		fosterCareReviewDB.setFceEligibilityDtLastUpdate(fceEligibilityDto.getDtLastUpdate());
		fosterCareReviewDB.setIdCase(fceEligibilityDto.getIdCase());
		fosterCareReviewDB.setIdEligibilityEvent(fceEligibilityDto.getIdEligibilityEvent());
		fosterCareReviewDB.setIdFceApplication(fceEligibilityDto.getIdFceApplication());
		if (!ObjectUtils.isEmpty(fceEligibilityDto.getIdFceEligibility())) {
			fosterCareReviewDB.setIdFceEligibility(fceEligibilityDto.getIdFceEligibility());
		}
		fosterCareReviewDB.setIdFcePerson(fceEligibilityDto.getIdFcePerson());
		fosterCareReviewDB.setIdPerson(fceEligibilityDto.getIdPerson());
		fosterCareReviewDB.setIdFceReview(fceEligibilityDto.getIdFceReview());
		fosterCareReviewDB.setIdLastUpdatePerson(fceEligibilityDto.getIdLastUpdatePerson());
		fosterCareReviewDB.setIdStage(fceEligibilityDto.getIdStage());
		fosterCareReviewDB.setIndAbsentAltrntCustody(fceEligibilityDto.getIndAbsentAltrntCustody());
		fosterCareReviewDB.setIndAbsentDeported(fceEligibilityDto.getIndAbsentDeported());
		fosterCareReviewDB.setIndAbsentDeserted(fceEligibilityDto.getIndAbsentDeserted());
		fosterCareReviewDB.setIndAbsentDied(fceEligibilityDto.getIndAbsentDied());
		fosterCareReviewDB.setIndAbsentDivorced(fceEligibilityDto.getIndAbsentDivorced());
		fosterCareReviewDB.setIndAbsentFather(fceEligibilityDto.getIndAbsentFather());
		fosterCareReviewDB.setIndAbsentHospitalized(fceEligibilityDto.getIndAbsentHospitalized());
		fosterCareReviewDB.setIndAbsentIncarcerated(fceEligibilityDto.getIndAbsentIncarcerated());
		fosterCareReviewDB.setIndAbsentMilitaryWork(fceEligibilityDto.getIndAbsentMilitaryWork());
		fosterCareReviewDB.setIndAbsentMother(fceEligibilityDto.getIndAbsentMother());
		fosterCareReviewDB.setIndAbsentNeverCohabitated(fceEligibilityDto.getIndAbsentNeverCohabitated());
		fosterCareReviewDB.setIndAbsentSeparated(fceEligibilityDto.getIndAbsentSeparated());
		fosterCareReviewDB.setIndAbsentWorkRelated(fceEligibilityDto.getIndAbsentWorkRelated());
		fosterCareReviewDB.setIndChildLivingPrnt6Mnths(fceEligibilityDto.getIndChildLivingPrnt6Mnths());
		fosterCareReviewDB.setIndChildQualifiedCitizen(fceEligibilityDto.getIndChildQualifiedCitizen());
		fosterCareReviewDB.setIndChildSupportOrdered(fceEligibilityDto.getIndChildSupportOrdered());
		fosterCareReviewDB.setIndChildUnder18(fceEligibilityDto.getIndChildUnder18());
		fosterCareReviewDB.setIndCtznshpAttorneyReview(fceEligibilityDto.getIndCtznshpAttorneyReview());
		fosterCareReviewDB.setIndCtznshpBaptismalCrtfct(fceEligibilityDto.getIndCtznshpBaptismalCrtfct());
		fosterCareReviewDB.setIndCtznshpBirthCrtfctFor(fceEligibilityDto.getIndCtznshpBirthCrtfctFor());
		fosterCareReviewDB.setIndCtznshpBirthCrtfctUs(fceEligibilityDto.getIndCtznshpBirthCrtfctUs());
		fosterCareReviewDB.setIndCtznshpChldFound(fceEligibilityDto.getIndCtznshpChldFound());
		fosterCareReviewDB.setIndCtznshpCitizenCrtfct(fceEligibilityDto.getIndCtznshpCitizenCrtfct());
		fosterCareReviewDB.setIndCtznshpDhsOther(fceEligibilityDto.getIndCtznshpDhsOther());
		fosterCareReviewDB.setIndCtznshpDhsUs(fceEligibilityDto.getIndCtznshpDhsUs());
		fosterCareReviewDB.setIndCtznshpEvaluation(fceEligibilityDto.getIndCtznshpEvaluation());
		fosterCareReviewDB.setIndCtznshpForDocumentation(fceEligibilityDto.getIndCtznshpForDocumentation());
		fosterCareReviewDB.setIndCtznshpHospitalCrtfct(fceEligibilityDto.getIndCtznshpHospitalCrtfct());
		fosterCareReviewDB.setIndCtznshpNoDocumentation(fceEligibilityDto.getIndCtznshpNoDocumentation());
		fosterCareReviewDB.setIndCtznshpNtrlztnCrtfct(fceEligibilityDto.getIndCtznshpNtrlztnCrtfct());
		fosterCareReviewDB.setIndCtznshpPassport(fceEligibilityDto.getIndCtznshpPassport());
		fosterCareReviewDB.setIndCtznshpResidentCard(fceEligibilityDto.getIndCtznshpResidentCard());
		fosterCareReviewDB.setIndEligibilityCourtMonth(fceEligibilityDto.getIndEligibilityCourtMonth());
		fosterCareReviewDB.setIndEligible(fceEligibilityDto.getIndEligible());
		fosterCareReviewDB.setIndEquity(fceEligibilityDto.getIndEquity());
		fosterCareReviewDB.setIndFatherPwe(fceEligibilityDto.getIndFatherPwe());
		fosterCareReviewDB.setIndHomeIncomeAfdcElgblty(fceEligibilityDto.getIndHomeIncomeAfdcElgblty());
		fosterCareReviewDB.setIndMeetsDpOrNotEs(fceEligibilityDto.getIndMeetsDpOrNotEs());
		fosterCareReviewDB.setIndMeetsDpOrNotSystem(fceEligibilityDto.getIndMeetsDpOrNotSystem());
		fosterCareReviewDB.setIndMotherPwe(fceEligibilityDto.getIndMotherPwe());
		fosterCareReviewDB.setIndNarrativeApproved(fceEligibilityDto.getIndNarrativeApproved());
		fosterCareReviewDB.setIndOtherVerification(fceEligibilityDto.getIndOtherVerification());
		fosterCareReviewDB.setIndParentDisabled(fceEligibilityDto.getIndParentDisabled());
		fosterCareReviewDB.setIndParentalDeprivation(fceEligibilityDto.getIndParentalDeprivation());
		fosterCareReviewDB.setIndPrsManagingCvs(fceEligibilityDto.getIndPrsManagingCvs());
		fosterCareReviewDB.setIndRemovalChildOrdered(fceEligibilityDto.getIndRemovalChildOrdered());
		fosterCareReviewDB.setIndRsdiVerification(fceEligibilityDto.getIndRsdiVerification());
		fosterCareReviewDB.setIndRsnblEffortPrvtRemoval(fceEligibilityDto.getIndRsnblEffortPrvtRemoval());
		fosterCareReviewDB.setIndSsiVerification(fceEligibilityDto.getIndSsiVerification());
		fosterCareReviewDB.setNbrCertifiedGroup(fceEligibilityDto.getNbrCertifiedGroup());
		fosterCareReviewDB.setNbrParentsHome(fceEligibilityDto.getNbrParentsHome());
		fosterCareReviewDB.setTxtDeterminationComments(fceEligibilityDto.getTxtDeterminationComments());
		fosterCareReviewDB.setIndAbsentNeverCohabitated(fceEligibilityDto.getIndAbsentNeverCohabitated());
		fosterCareReviewDB.setNbrStepparentChildren(fceEligibilityDto.getNbrStepparentChildren());
	}

	/**
	 * Method Name: setFceReview Method Description: Method for setting
	 * fce_review db.
	 * 
	 * @param fceContext
	 * @param fceReviewDto
	 * @param fosterCareReviewDB
	 */
	private void setFceReview(FceContextDto fceContext, FceReviewDto fceReviewDto,
			FosterCareReviewDto fosterCareReviewDB) {
		fosterCareReviewDB.setIndShowChecklist(fceReviewDto.getIndShowChecklist());
		fosterCareReviewDB.setFceReviewDto(fceContext.getFceReviewDto());
		fosterCareReviewDB.setAmtFosterCareRate(fceContext.getFceReviewDto().getAmtFosterCareRate());
		fosterCareReviewDB.setAmtSavings(fceReviewDto.getAmtSavings());
		fosterCareReviewDB.setCdChangeCtznStatus(fceReviewDto.getCdChangeCtznStatus());
		fosterCareReviewDB.setCdLivingConditionCurrent(fceReviewDto.getCdLivingConditionCurrent());
		fosterCareReviewDB.setCdRate(fceReviewDto.getCdRate());
		fosterCareReviewDB.setDtChildCmpltHighSchool(fceReviewDto.getDtChildCmpltHighSchool());
		fosterCareReviewDB.setDtChildEnterHigher(fceReviewDto.getDtChildEnterHigher());
		fosterCareReviewDB.setDtReviewComplete(fceReviewDto.getDtReviewComplete());
		fosterCareReviewDB.setDtRightsTerminated(fceReviewDto.getDtRightsTerminated());
		fosterCareReviewDB.setFceReviewDtLastUpdate(fceReviewDto.getDtLastUpdate());
		fosterCareReviewDB.setIdCase(fceReviewDto.getIdCase());
		fosterCareReviewDB.setIdCurrentPlacementEvent(fceReviewDto.getIdCurrentPlacementEvent());
		fosterCareReviewDB.setIdEvent(fceReviewDto.getEvent().getIdEvent());
		fosterCareReviewDB.setIdFceApplication(fceReviewDto.getIdFceApplication());
		fosterCareReviewDB.setIdFceEligibility(fceReviewDto.getIdFceEligibility());
		fosterCareReviewDB.setIdFceReview(fceReviewDto.getIdFceReview());
		fosterCareReviewDB.setIdLastUpdatePerson(fceReviewDto.getIdLastUpdatePerson());
		fosterCareReviewDB.setIdPlacementRateEvent(fceReviewDto.getIdPlacementRateEvent());
		fosterCareReviewDB.setIdStage(fceReviewDto.getStage().getIdStage());
		fosterCareReviewDB.setIndChildAccptdHigher(fceReviewDto.getIndChildAccptdHigher());
		fosterCareReviewDB.setIndChildCmplt19(fceReviewDto.getIndChildCmplt19());
		fosterCareReviewDB.setIndCmpltSchlMaxAge(fceReviewDto.getIndCmpltSchlMaxAge());
		fosterCareReviewDB.setIndChildEnrolled(fceReviewDto.getIndChildEnrolled());
		fosterCareReviewDB.setIndChildIncomeGtFcPay(fceReviewDto.getIndChildIncomeGtFcPay());
		fosterCareReviewDB.setIndCurrentParentSit(fceReviewDto.getIndCurrentParentSit());

		fosterCareReviewDB.setIndPermanencyHearings(fceReviewDto.getIndPermanencyHearings());
		fosterCareReviewDB.setIndPrmncyHearingsDue(fceReviewDto.getIndPrmncyHearingsDue());
		fosterCareReviewDB.setIndPrmncyHrngs12Month(fceReviewDto.getIndPrmncyHrngs12Month());
		fosterCareReviewDB.setIndReviewInappropriate(fceReviewDto.getIndReviewInappropriate());
		fosterCareReviewDB.setIndRightsTerminated(fceReviewDto.getIndRightsTerminated());
		fosterCareReviewDB.setIndSavingsAcct(fceReviewDto.getIndSavingsAcct());
		fosterCareReviewDB.setIndTdprsResponsibility(fceReviewDto.getIndTdprsResponsibility());
		fosterCareReviewDB.setTxtInappropriateComments(fceReviewDto.getTxtInappropriateComments());
		fosterCareReviewDB.setIndShowChecklist(fceReviewDto.getIndShowChecklist());
		fosterCareReviewDB.setIndChildVocTechTrng(fceReviewDto.getIndChildVoctechTrng());
		fosterCareReviewDB.setDtCmpltVocTechTrng(fceReviewDto.getDtCmpltVoctechTrng());
		fosterCareReviewDB.setIndHighSchool(fceReviewDto.getIndHighSchool());
		fosterCareReviewDB.setIndPostSecondary(fceReviewDto.getIndPostSecondary());
		fosterCareReviewDB.setIndPromoteEmployment(fceReviewDto.getIndPromoteEmployment());
		fosterCareReviewDB.setIndEmployMinHrs(fceReviewDto.getIndEmployMinHrs());
		fosterCareReviewDB.setIndIncapableMedCond(fceReviewDto.getIndIncapableMedCond());
		fosterCareReviewDB.setIndMedCondDocRecvd(fceReviewDto.getIndMedCondDocRecvd());
		fosterCareReviewDB.setCdMedCondVerified(fceReviewDto.getCdMedCondVerified());
		fosterCareReviewDB.setTxtMedCondVerCommnts(fceReviewDto.getTxtMedCondVerCommnts());
		fosterCareReviewDB.setDtVolFcAgrmntSign(fceReviewDto.getDtVolFcAgrmntSign());

		fosterCareReviewDB.setFceReviewCdPersonCitizenship(fceContext.getFceReviewDto().getCdPersonCitizenship());
	}

	private void fostercareReviewIndDefaultVal(FosterCareReviewDto fosterCareReviewDB) {

		fosterCareReviewDB.setIndChildCmplt19(ObjectUtils.isEmpty(fosterCareReviewDB.getIndChildCmplt19())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndChildCmplt19());
		fosterCareReviewDB.setIndCmpltSchlMaxAge(ObjectUtils.isEmpty(fosterCareReviewDB.getIndCmpltSchlMaxAge())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndCmpltSchlMaxAge());
		fosterCareReviewDB.setIndCurrentParentSit(ObjectUtils.isEmpty(fosterCareReviewDB.getIndCurrentParentSit())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndCurrentParentSit());
		fosterCareReviewDB.setIndRightsTerminated(ObjectUtils.isEmpty(fosterCareReviewDB.getIndRightsTerminated())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndRightsTerminated());
		fosterCareReviewDB.setIndChildIncomeGtFcPay(ObjectUtils.isEmpty(fosterCareReviewDB.getIndChildIncomeGtFcPay())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndChildIncomeGtFcPay());
		fosterCareReviewDB.setIndTdprsResponsibility(ObjectUtils.isEmpty(fosterCareReviewDB.getIndTdprsResponsibility())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndTdprsResponsibility());
		fosterCareReviewDB.setIndNoActivePlacement(ObjectUtils.isEmpty(fosterCareReviewDB.getIndNoActivePlacement())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndNoActivePlacement());
		fosterCareReviewDB.setIndNonPrsPaidPlacement(ObjectUtils.isEmpty(fosterCareReviewDB.getIndNonPrsPaidPlacement())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndNonPrsPaidPlacement());
		fosterCareReviewDB.setIndNoActiveBloc(ObjectUtils.isEmpty(fosterCareReviewDB.getIndNoActiveBloc())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndNoActiveBloc());
		fosterCareReviewDB
				.setIndNoOpenPaidEligibility(ObjectUtils.isEmpty(fosterCareReviewDB.getIndNoOpenPaidEligibility())
						? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndNoOpenPaidEligibility());
		fosterCareReviewDB.setIndReviewInappropriate(ObjectUtils.isEmpty(fosterCareReviewDB.getIndReviewInappropriate())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndReviewInappropriate());
		fosterCareReviewDB.setIndShowChecklist(ObjectUtils.isEmpty(fosterCareReviewDB.getIndShowChecklist())
				? ServiceConstants.STRING_IND_N : fosterCareReviewDB.getIndShowChecklist());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.service.FosterCareReviewService#
	 * enableFosterGoupMessage()
	 */
	@Override
	public CommonBooleanRes enableFosterGoupMessage() {
		return fceReviewDao.enableFosterGoupMessage();
	}

	/**
	 * Method Name: setFceReviewForNewUsing Method Description: Method for
	 * setting fce_review db.
	 * 
	 * @param fceContext
	 * @param fceReviewDto
	 * @param fosterCareReviewDB
	 */
	private void setFceReviewForNewUsing(FceContextDto fceContext, FceReviewDto fceReviewDto,
			FceReviewDto compFceReviewDto, FosterCareReviewDto fosterCareReviewDB) {
		fosterCareReviewDB.setIndShowChecklist(compFceReviewDto.getIndShowChecklist());
		fosterCareReviewDB.setFceReviewDto(fceContext.getFceReviewDto());
		fosterCareReviewDB.setAmtFosterCareRate(fceContext.getFceReviewDto().getAmtFosterCareRate());
		fosterCareReviewDB.setAmtSavings(fceReviewDto.getAmtSavings());
		fosterCareReviewDB.setCdChangeCtznStatus(compFceReviewDto.getCdChangeCtznStatus());
		fosterCareReviewDB.setCdLivingConditionCurrent(compFceReviewDto.getCdLivingConditionCurrent());
		fosterCareReviewDB.setCdRate(compFceReviewDto.getCdRate());
		fosterCareReviewDB.setDtChildCmpltHighSchool(fceReviewDto.getDtChildCmpltHighSchool());
		fosterCareReviewDB.setDtChildEnterHigher(fceReviewDto.getDtChildEnterHigher());
		fosterCareReviewDB.setDtReviewComplete(fceReviewDto.getDtReviewComplete());
		fosterCareReviewDB.setDtRightsTerminated(fceReviewDto.getDtRightsTerminated());
		fosterCareReviewDB.setFceReviewDtLastUpdate(fceReviewDto.getDtLastUpdate());
		fosterCareReviewDB.setIdCase(fceReviewDto.getIdCase());
		fosterCareReviewDB.setIdCurrentPlacementEvent(fceReviewDto.getIdCurrentPlacementEvent());
		fosterCareReviewDB.setIdEvent(fceReviewDto.getEvent().getIdEvent());
		fosterCareReviewDB.setIdFceApplication(fceReviewDto.getIdFceApplication());
		fosterCareReviewDB.setIdFceEligibility(fceReviewDto.getIdFceEligibility());
		fosterCareReviewDB.setIdFceReview(fceReviewDto.getIdFceReview());
		fosterCareReviewDB.setIdLastUpdatePerson(fceReviewDto.getIdLastUpdatePerson());
		fosterCareReviewDB.setIdPlacementRateEvent(fceReviewDto.getIdPlacementRateEvent());
		fosterCareReviewDB.setIdStage(fceReviewDto.getStage().getIdStage());
		fosterCareReviewDB.setIndChildAccptdHigher(fceReviewDto.getIndChildAccptdHigher());
		fosterCareReviewDB.setIndChildCmplt19(fceReviewDto.getIndChildCmplt19());
		fosterCareReviewDB.setIndCmpltSchlMaxAge(fceReviewDto.getIndCmpltSchlMaxAge());
		fosterCareReviewDB.setIndChildEnrolled(fceReviewDto.getIndChildEnrolled());
		fosterCareReviewDB.setIndChildIncomeGtFcPay(compFceReviewDto.getIndChildIncomeGtFcPay());
		fosterCareReviewDB.setIndCurrentParentSit(fceReviewDto.getIndCurrentParentSit());
		fosterCareReviewDB.setIndNoActiveBloc(compFceReviewDto.getIndNoActiveBloc());
		fosterCareReviewDB.setIndNoActivePlacement(compFceReviewDto.getIndNoActivePlacement());
		fosterCareReviewDB.setIndNoOpenPaidEligibility(compFceReviewDto.getIndNoOpenPaidEligibility());
		fosterCareReviewDB.setIndNonPrsPaidPlacement(compFceReviewDto.getIndNonPrsPaidPlacement());
		fosterCareReviewDB.setIndPermanencyHearings(fceReviewDto.getIndPermanencyHearings());
		fosterCareReviewDB.setIndPrmncyHearingsDue(fceReviewDto.getIndPrmncyHearingsDue());
		fosterCareReviewDB.setIndPrmncyHrngs12Month(fceReviewDto.getIndPrmncyHrngs12Month());
		fosterCareReviewDB.setIndReviewInappropriate(compFceReviewDto.getIndReviewInappropriate());
		fosterCareReviewDB.setIndRightsTerminated(fceReviewDto.getIndRightsTerminated());
		fosterCareReviewDB.setIndSavingsAcct(compFceReviewDto.getIndSavingsAcct());
		fosterCareReviewDB.setIndTdprsResponsibility(compFceReviewDto.getIndTdprsResponsibility());
		fosterCareReviewDB.setTxtInappropriateComments(compFceReviewDto.getTxtInappropriateComments());
		fosterCareReviewDB.setIndChildVocTechTrng(fceReviewDto.getIndChildVoctechTrng());
		fosterCareReviewDB.setDtCmpltVocTechTrng(fceReviewDto.getDtCmpltVoctechTrng());
		fosterCareReviewDB.setIndHighSchool(fceReviewDto.getIndHighSchool());
		fosterCareReviewDB.setIndPostSecondary(fceReviewDto.getIndPostSecondary());
		fosterCareReviewDB.setIndPromoteEmployment(fceReviewDto.getIndPromoteEmployment());
		fosterCareReviewDB.setIndEmployMinHrs(fceReviewDto.getIndEmployMinHrs());
		fosterCareReviewDB.setIndIncapableMedCond(fceReviewDto.getIndIncapableMedCond());
		fosterCareReviewDB.setIndMedCondDocRecvd(fceReviewDto.getIndMedCondDocRecvd());
		fosterCareReviewDB.setCdMedCondVerified(fceReviewDto.getCdMedCondVerified());
		fosterCareReviewDB.setTxtMedCondVerCommnts(fceReviewDto.getTxtMedCondVerCommnts());
		fosterCareReviewDB.setDtVolFcAgrmntSign(fceReviewDto.getDtVolFcAgrmntSign());
		fosterCareReviewDB.setFceReviewCdPersonCitizenship(fceContext.getFceReviewDto().getCdPersonCitizenship());
	}

}
