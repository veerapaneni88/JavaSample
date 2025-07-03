package us.tx.state.dfps.service.fostercarereview.daoimpl;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.ApplicationUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.EligibilityUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.common.utils.ReviewUtils;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.service.AgeCitizenshipService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.fostercarereview.dao.FceHelperDao;
import us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao;
import us.tx.state.dfps.service.fostercarereview.dao.FosterCareReviewDao;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 14, 2018- 3:33:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FosterCareReviewDaoImpl implements FosterCareReviewDao {

	private static final Logger log = Logger.getLogger(FosterCareReviewDaoImpl.class);

	@Autowired
	FceHelperDao fceHelperDao;

	@Autowired
	EventUtil eventUtil;
	@Autowired
	CaseUtils caseUtils;

	@Autowired
	PersonUtil personUtil;

	@Autowired
	FceReviewDao fceReviewDao;

	@Autowired
	private EventUtilityService eventUtilityService;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	EligibilityUtil eligibilityUtil;

	@Autowired
	FceDao fceApplicationDao;

	@Autowired
	AgeCitizenshipService ageCitizenshipService;

	@Autowired
	ApplicationUtil applicationUtil;

	@Autowired
	ReviewUtils reviewUtils;

	@Autowired
	EventDao eventDao;

	@Autowired
	FceDao fceDao;

	@Autowired
	FceService fceService;

	/**
	 * Method Name: confirm Method Description: confirm the eligibility and
	 * close the review
	 * 
	 * @param fosterCareReviewReq
	 * @return @
	 */
	@Override
	public CommonHelperRes confirm(FosterCareReviewDto fosterCareReviewDto) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Long idEligibilityEvent = 0L;
		Calendar cal = Calendar.getInstance();
		reviewUtils.saveReview(fosterCareReviewDto);
		Long idFceReview = fosterCareReviewDto.getIdFceReview();
		fceHelperDao.verifyNonZero("idFceReview", idFceReview);

		Long idEvent = fosterCareReviewDto.getIdEvent();
		fceHelperDao.verifyNonZero("idEvent", idEvent);
		FceReview fceReview = fceReviewDao.getById(idFceReview);
		fceReview.setDtReviewComplete(cal.getTime());
		eventUtil.changeEventStatus(idEvent, ServiceConstants.PENDING_EVENT_STATUS,
				ServiceConstants.COMPLETE_EVENT_STATUS);

		long previousIdFceEligibility = fceReview.getFceEligibility().getIdFceEligibility();
		FceEligibilityDto previousFceEligibilityDto = fceEligibilityDao
				.getFceEligibilityDtobyId(previousIdFceEligibility);

		Long idStage = fceReview.getStage().getIdStage();
		previousFceEligibilityDto.setIdStage(idStage);
		Long idLastUpdatePerson = fceReview.getIdLastUpdatePerson();

		idEligibilityEvent = fceService.createEvent(ServiceConstants.FCE_ELIGIBILITY_EVENT_TYPE, idLastUpdatePerson,
				idStage, null);

		FceEligibilityDto fceEligibilityDto = fceDao.copyEligibility(previousFceEligibilityDto, idLastUpdatePerson,
				true);

		fceEligibilityDto.setIdEligibilityEvent(idEligibilityEvent);

		// SIR 19219
		// When user confirms eligibility, if application was CAPS, set to
		// IMP
		// Also, indicate that checklist should be shown.
		Long idApplication = fceEligibilityDto.getIdFceApplication();

		FceApplicationDto fceApplicationDto = fceApplicationDao.getFceApplicationById(idApplication);

		fceApplicationDto.setCdApplication(ServiceConstants.IMP);

		// boolean wasEligibleAtTimeOfApplication =
		FceEligibilityDto fceEligibilityDtoById = eligibilityUtil.findEligibilityByIdFceApplication(idApplication);
		boolean wasEligibleAtTimeOfApplication = false;
		if (!ObjectUtils.isEmpty(fceEligibilityDtoById)) {
			wasEligibleAtTimeOfApplication = ServiceConstants.Y.equals(fceEligibilityDtoById.getIndEligible());
		}

		String indChecklist = ServiceConstants.STRING_IND_Y;
		if (wasEligibleAtTimeOfApplication) {
			indChecklist = ServiceConstants.STRING_IND_N;
		}

		// checklist should only be shown on COMP Reviews if it was
		// completed
		fceReview.setIndShowChecklist(indChecklist);

		// complete any todos for this review
		eventUtil.completeTodosForEventId(idEvent);
		commonHelperRes.setIdEvent(idEligibilityEvent);
		return commonHelperRes;

	}

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idReviewEvent
	 * @return boolean @
	 */
	@Override
	public boolean isFCReviewCreatedOnAfter1stOct2010(Long idReviewEvent) {
		boolean result = false;
		try {
			EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idReviewEvent);
			if (!DateUtils.isBefore(eventValueDto.getDtEventOccurred(), DateUtils.toJavaDateFromInput(
					lookupDao.simpleDecodeSafe(ServiceConstants.CRELDATE, ServiceConstants.CRELDATE_OCT_2010_FCON)))) {
				result = true;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	@Override
	public boolean isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(Long idFceReview) {
		boolean result = false;
		try {
			FceReview fceReview = fceReviewDao.getById(idFceReview);
			EventDto eventValueDto = eventDao.getEventByid(fceReview.getEventByIdEvent().getIdEvent());
			if (!DateUtils.isBefore(eventValueDto.getDtEventOccurred(), DateUtils.toJavaDateFromInput(
					lookupDao.simpleDecodeSafe(ServiceConstants.CRELDATE, ServiceConstants.CRELDATE_OCT_2010_FCON)))) {
				result = true;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	@Override
	public boolean isEntryLevelLegalStatusPresent(FosterCareReviewDto fosterCareReviewDto) {

		if (personUtil.getLegalStatusDate(fosterCareReviewDto.getIdPerson(),
				caseUtils.getStage(fosterCareReviewDto.getIdStage()).getCapsCase().getIdCase(),
				caseUtils.getStage(fosterCareReviewDto.getIdStage()).getCdStageType()) == null) {
			return false;
		} else {
			return true;
		}
	}
}
