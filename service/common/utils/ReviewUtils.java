package us.tx.state.dfps.service.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.dto.FceReviewDto;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Oct 25, 2017- 2:52:53 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ReviewUtils {

	@Autowired
	private static MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private FceDao fceApplicationDao;

	@Autowired
	private EligibilityUtil eligibilityUtil;

	@Autowired
	private PlacementDao placementDao;

	@Autowired
	private LookupDao lookupDao;

	@Value("${ReviewUtils.findLegalStatusForChild}")
	private String findLegalStatusForChild;

	@Value("${ReviewUtil.findBlocSql}")
	private String findBlocSql;

	@Value("${ReviewUtil.getHighestRateForBlocSql}")
	private String getHighestRateForBlocSql;

	@Value("${ReviewUtil.findFosterCareRateSql}")
	private String findFosterCareRateSql;

	@Value("${ReviewUtil.findByReviewEventIdSql}")
	private String findByReviewEventIdSql;

	private static final Logger log = Logger.getLogger(ReviewUtils.class);

	public FceReviewDto findReviewForReviewEvent(Long idEvent) {
		log.info("executing findReviewForReviewEvent");
		FceReviewDto fceReviewDto = null;
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			fceReviewDto = findByReviewEventId(idEvent);
		}
		return fceReviewDto;
	}

	private FceReviewDto findByReviewEventId(Long idEvent) {

		log.info("executing findByReviewEventId");

		List<FceReview> fceReviewList = (List<FceReview>) sessionFactory.getCurrentSession()
				.createCriteria(FceReview.class).add(Restrictions.eq("eventByIdEvent.idEvent", idEvent)).list();
		FceReview fceReview = fceReviewList.get(0);
		FceReviewDto fceReviewDto = new FceReviewDto();
		BeanUtils.copyProperties(fceReview, fceReviewDto);
		fceReviewDto.setIdFceReview(fceReview.getIdFceReview());
		if (!ObjectUtils.isEmpty(fceReview.getFceEligibility()))
			fceReviewDto.setIdFceEligibility(fceReview.getFceEligibility().getIdFceEligibility());
		fceReviewDto.setIdStage(fceReview.getStage().getIdStage());
		if (!ObjectUtils.isEmpty(fceReview.getEventByIdCurrentPlacementEvent()))
			fceReviewDto.setIdCurrentPlacementEvent(fceReview.getEventByIdCurrentPlacementEvent().getIdEvent());
		if (!ObjectUtils.isEmpty(fceReview.getPlacementByIdPlacementRateEvent()))
			fceReviewDto.setIdPlacementRateEvent(fceReview.getPlacementByIdPlacementRateEvent().getIdPlcmtEvent());
		if (!ObjectUtils.isEmpty(fceReview.getFceApplication()))
			fceReviewDto.setIdFceApplication(fceReview.getFceApplication().getIdFceApplication());

		return fceReviewDto;
	}

	/**
	 * Method Name: createReview Method Description: to create fce review
	 * 
	 * @param fceReviewDto
	 * @param idReviewEvent
	 * @param fceEligibilityDto
	 * @return long
	 */
	public Long createReview(FceReviewDto fceReviewDto, Long idReviewEvent, FceEligibilityDto fceEligibilityDto) {

		long idFceApplication = fceEligibilityDto.getIdFceApplication();
		long idFceEligibility = fceEligibilityDto.getIdFceEligibility();
		Long idCase = fceEligibilityDto.getIdCase();
		log.info("executing createReview");
		FceReview fceReview = new FceReview();
		Event eventByIdEvent = new Event();
		eventByIdEvent.setIdEvent(idReviewEvent);
		fceReview.setEventByIdEvent(eventByIdEvent);
		Stage stage = new Stage();
		stage.setIdStage(fceReviewDto.getIdStage());
		fceReview.setStage(stage);
		FceApplication fceApplication = new FceApplication();
		fceApplication.setIdFceApplication(idFceApplication);
		fceReview.setFceApplication(fceApplication);
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdFceEligibility(idFceEligibility);
		fceReview.setFceEligibility(fceEligibility);
		fceReview.setIdCase(idCase);
		fceReview.setIdLastUpdatePerson(fceReviewDto.getIdLastUpdatePerson());
		fceReview.setDtLastUpdate(new Date());
		fceReview.setAmtSavings(fceReviewDto.getAmtSavings());
		fceReview.setCdChangeCtznStatus(fceReviewDto.getCdChangeCtznStatus());
		fceReview.setCdLivingConditionCurrent(fceReviewDto.getCdLivingConditionCurrent());
		fceReview.setCdPersonCitizenship(fceReviewDto.getCdPersonCitizenship());
		fceReview.setDtChildEnterHigher(fceReviewDto.getDtChildEnterHigher());
		fceReview.setDtChildCmpltHighSchool(fceReviewDto.getDtChildCmpltHighSchool());
		fceReview.setDtReviewComplete(fceReviewDto.getDtReviewComplete());
		fceReview.setDtRightsTerminated(fceReviewDto.getDtRightsTerminated());
		fceReview.setIndChildAccptdHigher(fceReviewDto.getIndChildAccptdHigher());
		fceReview.setIndChildEnrolled(fceReviewDto.getIndChildEnrolled());
		fceReview.setIndChildCmplt19(fceReviewDto.getIndChildCmplt19());
		fceReview.setIndCmpltSchlMaxAge(fceReviewDto.getIndCmpltSchlMaxAge());
		fceReview.setIndCurrentParentSit(fceReviewDto.getIndCurrentParentSit());
		fceReview.setIndPermanencyHearings(fceReviewDto.getIndPermanencyHearings());
		fceReview.setIndPrmncyHearingsDue(fceReviewDto.getIndPrmncyHearingsDue());
		fceReview.setIndPrmncyHrngs12Month(fceReviewDto.getIndPrmncyHrngs12Month());
		fceReview.setIndRightsTerminated(fceReviewDto.getIndRightsTerminated());
		fceReview.setIndSavingsAcct(fceReviewDto.getIndSavingsAcct());
		fceReview.setIndChildIncomeGtFcPay(fceReviewDto.getIndChildIncomeGtFcPay());
		fceReview.setIndTdprsResponsibility(fceReviewDto.getIndTdprsResponsibility());
		fceReview.setIndNoActivePlacement(fceReviewDto.getIndNoActivePlacement());
		fceReview.setIndNonPrsPaidPlacement(fceReviewDto.getIndNonPrsPaidPlacement());
		fceReview.setIndNoActiveBloc(fceReviewDto.getIndNoActiveBloc());
		fceReview.setIndNoOpenPaidEligibility(fceReviewDto.getIndNoOpenPaidEligibility());
		fceReview.setIndReviewInappropriate(fceReviewDto.getIndReviewInappropriate());
		fceReview.setIndShowChecklist(fceReviewDto.getIndShowChecklist());
		fceReview.setTxtInappropriateComments(fceReviewDto.getTxtInappropriateComments());
		fceReview.setCdRate(fceReviewDto.getCdRate());
		fceReview.setAmtFosterCareRate(fceReviewDto.getAmtFosterCareRate());
		fceReview.setIndChildVoctechTrng(fceReviewDto.getIndChildVoctechTrng());
		fceReview.setDtCmpltVoctechTrng(fceReviewDto.getDtCmpltVoctechTrng());
		fceReview.setIndHighSchool(fceReviewDto.getIndHighSchool());
		fceReview.setIndPostSecondary(fceReviewDto.getIndPostSecondary());
		fceReview.setIndPromoteEmployment(fceReviewDto.getIndPromoteEmployment());
		fceReview.setIndEmployMinHrs(fceReviewDto.getIndEmployMinHrs());
		fceReview.setIndIncapableMedCond(fceReviewDto.getIndIncapableMedCond());
		fceReview.setIndMedCondDocRecvd(fceReviewDto.getIndMedCondDocRecvd());
		fceReview.setDtVolFcAgrmntSign(fceReviewDto.getDtVolFcAgrmntSign());
		fceReview.setCdMedCondVerified(fceReviewDto.getCdMedCondVerified());
		Long idFceReview = (Long) sessionFactory.getCurrentSession().save(fceReview);
		sessionFactory.getCurrentSession().flush();
		return idFceReview;
	}

	public FceReviewDto createReview(long idCase, Long idReviewEvent, long idFceApplication, long idFceEligibility,
			Long idStage) {
		log.info("executing createReview");
		Date now = new Date();

		FceReview fceReview = new FceReview();
		fceReview.setIdCase(idCase);
		Event eventByIdEvent = new Event();
		eventByIdEvent.setIdEvent(idReviewEvent);
		fceReview.setEventByIdEvent(eventByIdEvent);
		FceApplication fceApplication = new FceApplication();
		fceApplication.setIdFceApplication(idFceApplication);
		fceReview.setFceApplication(fceApplication);
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdFceEligibility(idFceEligibility);
		fceReview.setFceEligibility(fceEligibility);
		Stage stage = new Stage();
		stage.setIdStage(idStage);
		fceReview.setStage(stage);
		fceReview.setDtLastUpdate(now);

		Long idFceReview = (Long) sessionFactory.getCurrentSession().save(fceReview);

		FceReviewDto fceReviewDto = new FceReviewDto();
		BeanUtils.copyProperties(fceReview, fceReviewDto);
		fceReviewDto.setIdFceReview(idFceReview);
		EventDto eventDto = new EventDto();
		eventDto.setIdEvent(idReviewEvent);
		fceReviewDto.setEvent(eventDto);
		StageDto stageDto = new StageDto();
		stageDto.setIdStage(idStage);
		fceReviewDto.setStage(stageDto);
		return fceReviewDto;
	}

	public void syncFceReviewStatus(FceEligibilityDto fceEligibilityDto, FceReviewDto fceReviewDto) {

		try {
			String cdPersonCitizenShip = personDao.searchPersonDtlById(fceEligibilityDto.getIdPerson())
					.getCdPersonCitizenship();
			fceReviewDto.setCdPersonCitizenship(cdPersonCitizenShip);

			FcePersonDto fcePerson = new FcePersonDto();
			fcePerson = fceApplicationDao.findFcePerson(fceEligibilityDto.getIdFcePerson());

			PersonDto person = personDao.getPersonById(fceEligibilityDto.getIdPerson());

			fceApplicationDao.updateBirthday(fcePerson, person);
			fceApplicationDao.createIncomeForFcePersons(fceEligibilityDto.getIdFceEligibility(),
					fceEligibilityDto.getIdFcePerson(), fceEligibilityDto.getIdPerson());

			LegalStatusDto legalStatus = findLegalStatusForChild(fceEligibilityDto.getIdCase(),
					fceEligibilityDto.getIdPerson());

			String cdLegalStatus = legalStatus.getCdLegalStatStatus();

			if (cdLegalStatus != null) {

				Date dtLegalStatStatusDt = (Date) legalStatus.getDtLegalStatStatusDt();
				fceReviewDto.setIndRightsTerminated(ServiceConstants.Y);
				fceReviewDto.setDtRightsTerminated(dtLegalStatStatusDt);
			} else {

				fceReviewDto.setIndRightsTerminated(ServiceConstants.Y);
				fceReviewDto.setDtRightsTerminated(null);
			}

			syncFosterCareRate(fceEligibilityDto, fceReviewDto, fceEligibilityDto.getIdPerson(), false);

		} catch (DataNotFoundException e) {
			log.info("exception at syncFceReviewStatus for personDao.searchPersonDtlById" + e.getMessage());
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;

		}

	}

	public void syncFosterCareRate(FceEligibilityDto fceEligibilityDto, FceReviewDto fceReviewDto, Long idPerson,
			boolean isplacement) {

		fceReviewDto.setIndNoActivePlacement(ServiceConstants.N);
		fceReviewDto.setIndNonPrsPaidPlacement(ServiceConstants.N);
		fceReviewDto.setIndNoActiveBloc(ServiceConstants.N);
		fceReviewDto.setIndNoOpenPaidEligibility(ServiceConstants.N);
		fceReviewDto.setIdCurrentPlacementEvent(0L);
		fceReviewDto.setIdPlacementRateEvent(0L);
		fceReviewDto.setCdRate(null);

		fceReviewDto.setAmtFosterCareRate(new Double(ServiceConstants.ZERO_VAL));
		Long idCase = fceReviewDto.getIdCase();
		if (!eligibilityUtil.isLatestLegacyEligibilityOpenPrsPaid(idCase, idPerson)) {

			fceReviewDto.setIndNoOpenPaidEligibility(ServiceConstants.Y);
		}

		String cdBlocChild = findBloc(idPerson);
		fceEligibilityDto.setCdBlocChild(cdBlocChild);

		if (TypeConvUtil.isNullOrEmpty(cdBlocChild)) {
			fceReviewDto.setIndNoActiveBloc(ServiceConstants.Y);
		}

		PlacementValueDto activePlacement = placementDao.findActivePlacement(idPerson);

		if (null == activePlacement) {
			fceReviewDto.setIndNoActivePlacement(ServiceConstants.Y);
			if (isplacement) {
				throw new ServiceLayerException("MSG_CHILD_NO_PLACEMENT",
						(long) ServiceConstants.MSG_CHILD_NO_PLACEMENT_CODE, null);
			}
			return;
		} else {

			if (null != activePlacement.getIdPlcmtEvent()) {
				fceReviewDto.setIdCurrentPlacementEvent(activePlacement.getIdPlcmtEvent());
			}
		}

		String prsPaidPlacement = getPrsPaidPlacementCode(cdBlocChild, activePlacement);

		if (null == prsPaidPlacement) {

			fceReviewDto.setIndNonPrsPaidPlacement(ServiceConstants.Y);
			fceReviewDto.setAmtFosterCareRate(null);
			return;
		}

		if (null == cdBlocChild) {
			Double rate = getHighestRateForBloc(ServiceConstants.CBILPLOC_010);
			fceReviewDto.setAmtFosterCareRate(rate);
			return;
		}

		String cdRsrcFacilType = activePlacement.getCdRsrcFacilType();
		String cdPlcmtLivArr = activePlacement.getCdPlcmtLivArr();

		if (null == cdRsrcFacilType) {
			throw new ServiceLayerException("Messages.MSG_NO_FACILITY_TYPE_RECORDED");
		}

		if (ServiceConstants.CPLCMT_67.equals(cdPlcmtLivArr)) {
			Double rate = getHighestRateForBloc(cdBlocChild);
			fceReviewDto.setAmtFosterCareRate(rate);
			return;
		}

		String codes = getPrsPaidPlacementCode(cdBlocChild, activePlacement);

		Map<String, String> fosterCareRate = findFosterCareRate(codes);
		if (fosterCareRate.isEmpty()) {
			Double rate = getHighestRateForBloc(cdBlocChild);
			fceReviewDto.setAmtFosterCareRate(rate);
			return;
		}

		String code = (String) fosterCareRate.get("codes");
		fceReviewDto.setCdRate(code);

		fceReviewDto.setAmtFosterCareRate(new Double(fosterCareRate.get("unitRate")));
		fceReviewDto.setIdPlacementRateEvent(activePlacement.getIdPlcmtEvent());

	}

	@SuppressWarnings("rawtypes")
	private Map findFosterCareRate(String codes) {

		Map<String, String> map = new HashMap<>();
		List q = (List) sessionFactory.getCurrentSession().createSQLQuery(findFosterCareRateSql)
				.addScalar("codes", StandardBasicTypes.STRING).addScalar("decode", StandardBasicTypes.STRING)
				.addScalar("unitRate", StandardBasicTypes.STRING).setParameter("codes", codes).list();
		Iterator it = q.iterator();
		while (it.hasNext()) {
			Object o[] = (Object[]) it.next();
			map = new HashMap<>();
			map.put("codes", (String) o[0]);
			map.put("decode", (String) o[1]);
			map.put("unitRate", (String) o[2]);
		}

		return map;
	}

	private Double getHighestRateForBloc(String bloc) {

		bloc = getTransalated(bloc);

		String param1 = bloc + "%";
		String param2 = bloc + "67%";

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHighestRateForBlocSql)
				.addScalar("maxRate", StandardBasicTypes.DOUBLE).setParameter("codeLike", param1)
				.setParameter("codeNotLike", param2);
		Double creatorsValue = (Double) query.uniqueResult();
		return creatorsValue;
	}

	private String getPrsPaidPlacementCode(String cdBlocChild, PlacementValueDto activePlacement) {

		String result = null;

		if (null != activePlacement) {
			if (null == activePlacement.getCdRsrcFacilType()) {
				return null;
			}

			String indPlcmtEmerg = activePlacement.getIndPlcmtEmerg();

			if (null == indPlcmtEmerg) {
				indPlcmtEmerg = ServiceConstants.N;
			}

			String cdPlcmntType = activePlacement.getCdPlcmtType();
			if (isPlacementPrsPaid(cdPlcmntType, activePlacement.getCdPlcmtLivArr()) == false) {
				return null;
			}

			result = activePlacement.getCdRsrcFacilType() + indPlcmtEmerg + cdPlcmntType;
		}

		String translatedLoc = getTransalated(cdBlocChild);

		return translatedLoc + result;
	}

	private String getTransalated(String loc) {
		String translatedLoc = "";
		try {
			translatedLoc = lookupDao.simpleDecodeSafe(ServiceConstants.CLOCMAP, loc);
		} catch (Throwable e) {
			translatedLoc = "";
		}

		if (!ObjectUtils.isEmpty(translatedLoc)) {
			return translatedLoc;
		}
		return loc;
	}

	/**
	 * 
	 * @param cdPlcmntType
	 * @param cdPlcmtLivArr
	 * @return
	 */
	private boolean isPlacementPrsPaid(String cdPlcmntType, String cdPlcmtLivArr) {

		if (cdPlcmntType == null || ServiceConstants.CPLMNTYP_010.equals(cdPlcmntType)
				|| ServiceConstants.CPLMNTYP_090.equals(cdPlcmntType)
				|| ServiceConstants.CPLMNTYP_040.equals(cdPlcmntType)) {
			return false;

		}

		if (ServiceConstants.CPLLAFRM_GT.equals(cdPlcmtLivArr) || ServiceConstants.CPLLAFRM_71.equals(cdPlcmtLivArr)) {
			return false;
		}

		return true;
	}

	private String findBloc(Long idPerson) {

		String bloc = null;
		String s = (String) sessionFactory.getCurrentSession().createSQLQuery(findBlocSql)
				.addScalar("cdPlocChild", StandardBasicTypes.STRING)
				.setParameter("cdplocType", ServiceConstants.CPLOCELG_BLOC).setParameter("idPerson", idPerson)
				.uniqueResult();

		if (null != s) {
			bloc = s;
		}
		return bloc;
	}

	/**
	 * Method Name:findLegalStatusForChild Method Description: This method is
	 * used to get the legal status for the given child based on the case id and
	 * person id.
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LegalStatusDto findLegalStatusForChild(Long idCase, Long idPerson) {

		log.info("inside findLegalStatusForChild method");
		List<String> legalStatus = new ArrayList<>(Arrays.asList(ServiceConstants.CLEGSTAT_040,
				ServiceConstants.CLEGSTAT_050, ServiceConstants.CLEGSTAT_070));

		LegalStatusDto legalStatusDto = new LegalStatusDto();

		List<LegalStatusDto> legalStatusDtoList = (List<LegalStatusDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(findLegalStatusForChild).addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTmcDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.CHARACTER)
				.addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdDischargeRsn", StandardBasicTypes.STRING)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).setParameterList("legalStatus", legalStatus)
				.setParameter("idCase", idCase).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDto.class)).list();
		if (!ObjectUtils.isEmpty(legalStatusDtoList)) {
			legalStatusDto = legalStatusDtoList.get(0);
		}

		return legalStatusDto;

	}

	public void updateBirthday(FcePerson fcePerson, Person person) {
		fcePerson.setDtBirth(person.getDtPersonBirth());
		fcePerson.setNbrAge(person.getNbrPersonAge());
		fcePerson.setIndDobApprox(person.getIndPersonDobApprox());
	}

	// Commenting the Unused Method
	/*
	 * private long calculateAge(Person person) {
	 * 
	 * Calendar birth = Calendar.getInstance();
	 * birth.setTime(person.getDtPersonBirth()); Calendar today =
	 * Calendar.getInstance();
	 * 
	 * int yearDifference = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
	 * 
	 * if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
	 * yearDifference--; } else { if (today.get(Calendar.MONTH) ==
	 * birth.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) <
	 * birth.get(Calendar.DAY_OF_MONTH)) { yearDifference--; }
	 * 
	 * }
	 * 
	 * return yearDifference;
	 * 
	 * }
	 */

	public void saveReview(FosterCareReviewDto fosterCareReviewDto) {

		Long idFceReview = fosterCareReviewDto.getIdFceReview();
		if (ObjectUtils.isEmpty(idFceReview) || idFceReview == 0l) {
			return;
		}
		FceReview fceReview = (FceReview) sessionFactory.getCurrentSession().get(FceReview.class, idFceReview);
		if (fceReview == null) {
			return;
		}

		// fceReview = fosterCareReviewDto.getFceReviewDto(fceReview);

		// from ui side properties setters
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtFosterCareRate()))
			fceReview.setAmtFosterCareRate(fosterCareReviewDto.getAmtFosterCareRate());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtSavings()))
			fceReview.setAmtSavings(fosterCareReviewDto.getAmtSavings());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdChangeCtznStatus()))
			fceReview.setCdChangeCtznStatus(fosterCareReviewDto.getCdChangeCtznStatus());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdLivingConditionCurrent()))
			fceReview.setCdLivingConditionCurrent(fosterCareReviewDto.getCdLivingConditionCurrent());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getFceReviewCdPersonCitizenship()))
			fceReview.setCdPersonCitizenship(fosterCareReviewDto.getFceReviewCdPersonCitizenship());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdRate()))
			fceReview.setCdRate(fosterCareReviewDto.getCdRate());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtChildCmpltHighSchool()))
			fceReview.setDtChildCmpltHighSchool(fosterCareReviewDto.getDtChildCmpltHighSchool());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtChildEnterHigher()))
			fceReview.setDtChildEnterHigher(fosterCareReviewDto.getDtChildEnterHigher());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtReviewComplete()))
			fceReview.setDtReviewComplete(fosterCareReviewDto.getDtReviewComplete());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtRightsTerminated()))
			fceReview.setDtRightsTerminated(fosterCareReviewDto.getDtRightsTerminated());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdCase()))
			fceReview.setIdCase(fosterCareReviewDto.getIdCase());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildAccptdHigher()))
			fceReview.setIndChildAccptdHigher(fosterCareReviewDto.getIndChildAccptdHigher());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildCmplt19()))
			fceReview.setIndChildCmplt19(fosterCareReviewDto.getIndChildCmplt19());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndCmpltSchlMaxAge()))
			fceReview.setIndCmpltSchlMaxAge(fosterCareReviewDto.getIndCmpltSchlMaxAge());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildEnrolled()))
			fceReview.setIndChildEnrolled(fosterCareReviewDto.getIndChildEnrolled());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildIncomeGtFcPay()))
			fceReview.setIndChildIncomeGtFcPay(fosterCareReviewDto.getIndChildIncomeGtFcPay());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndCurrentParentSit()))
			fceReview.setIndCurrentParentSit(fosterCareReviewDto.getIndCurrentParentSit());
		if (ObjectUtils.isEmpty(fosterCareReviewDto.getIndNonPrsPaidPlacement()))
			fceReview.setIndNonPrsPaidPlacement(fosterCareReviewDto.getIndNonPrsPaidPlacement());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndNoActiveBloc()))
			fceReview.setIndNoActiveBloc(fosterCareReviewDto.getIndNoActiveBloc());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndNoActivePlacement()))
			fceReview.setIndNoActivePlacement(fosterCareReviewDto.getIndNoActivePlacement());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndNoOpenPaidEligibility()))
			fceReview.setIndNoOpenPaidEligibility(fosterCareReviewDto.getIndNoOpenPaidEligibility());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPermanencyHearings()))
			fceReview.setIndPermanencyHearings(fosterCareReviewDto.getIndPermanencyHearings());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPrmncyHearingsDue()))
			fceReview.setIndPrmncyHearingsDue(fosterCareReviewDto.getIndPrmncyHearingsDue());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPrmncyHrngs12Month()))
			fceReview.setIndPrmncyHrngs12Month(fosterCareReviewDto.getIndPrmncyHrngs12Month());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndReviewInappropriate()))
			fceReview.setIndReviewInappropriate(fosterCareReviewDto.getIndReviewInappropriate());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndRightsTerminated()))
			fceReview.setIndRightsTerminated(fosterCareReviewDto.getIndRightsTerminated());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndSavingsAcct()))
			fceReview.setIndSavingsAcct(fosterCareReviewDto.getIndSavingsAcct());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndShowChecklist()))
			fceReview.setIndShowChecklist(fosterCareReviewDto.getIndShowChecklist());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndTdprsResponsibility()))
			fceReview.setIndTdprsResponsibility(fosterCareReviewDto.getIndTdprsResponsibility());

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getTxtInappropriateComments()))
			fceReview.setTxtInappropriateComments(fosterCareReviewDto.getTxtInappropriateComments());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildVocTechTrng()))
			fceReview.setIndChildVoctechTrng(fosterCareReviewDto.getIndChildVocTechTrng());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndHighSchool()))
			fceReview.setIndHighSchool(fosterCareReviewDto.getIndHighSchool());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPostSecondary()))
			fceReview.setIndPostSecondary(fosterCareReviewDto.getIndPostSecondary());

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPromoteEmployment()))
			fceReview.setIndPromoteEmployment(fosterCareReviewDto.getIndPromoteEmployment());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndEmployMinHrs()))
			fceReview.setIndEmployMinHrs(fosterCareReviewDto.getIndEmployMinHrs());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndIncapableMedCond()))
			fceReview.setIndIncapableMedCond(fosterCareReviewDto.getIndIncapableMedCond());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndMedCondDocRecvd()))
			fceReview.setIndMedCondDocRecvd(fosterCareReviewDto.getIndMedCondDocRecvd());

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdMedCondVerified()))
			fceReview.setCdMedCondVerified(fosterCareReviewDto.getCdMedCondVerified());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getTxtMedCondVerCommnts()))
			fceReview.setTxtMedCondVerCommnts(fosterCareReviewDto.getTxtMedCondVerCommnts());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtVolFcAgrmntSign()))
			fceReview.setDtVolFcAgrmntSign(fosterCareReviewDto.getDtVolFcAgrmntSign());
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndMedCondDocRecvd()))
			fceReview.setIndMedCondDocRecvd(fosterCareReviewDto.getIndMedCondDocRecvd());

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdCurrentPlacementEvent())
				&& fosterCareReviewDto.getIdCurrentPlacementEvent() > 0l) {
			Long placementEventId = fosterCareReviewDto.getIdCurrentPlacementEvent();
			Placement placementByIdCurrentPlacementEvent = (Placement) sessionFactory.getCurrentSession()
					.get(Placement.class, placementEventId);
			fceReview.setPlacementByIdCurrentPlacementEvent(placementByIdCurrentPlacementEvent);
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdEvent()) && fosterCareReviewDto.getIdEvent() > 0l) {
			Long eventId = fosterCareReviewDto.getIdEvent();
			Event eventByIdEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, eventId);
			fceReview.setEventByIdEvent(eventByIdEvent);

		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFceApplication())
				&& fosterCareReviewDto.getIdFceApplication() > 0l) {
			Long fceApplicationId = fosterCareReviewDto.getIdFceApplication();
			FceApplication fceApplication = (FceApplication) sessionFactory.getCurrentSession()
					.get(FceApplication.class, fceApplicationId);
			fceReview.setFceApplication(fceApplication);

		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFceEligibility())
				&& fosterCareReviewDto.getIdFceEligibility() > 0l) {
			Long fceEligibilityId = fosterCareReviewDto.getIdFceEligibility();
			FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession()
					.get(FceEligibility.class, fceEligibilityId);
			fceReview.setFceEligibility(fceEligibility);

		}

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdPlacementRateEvent())
				&& fosterCareReviewDto.getIdPlacementRateEvent() > 0l) {
			Long placementRateEventId = fosterCareReviewDto.getIdPlacementRateEvent();
			Placement placementByIdPlacementRateEvent = (Placement) sessionFactory.getCurrentSession()
					.get(Placement.class, placementRateEventId);
			fceReview.setPlacementByIdPlacementRateEvent(placementByIdPlacementRateEvent);
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdStage()) && fosterCareReviewDto.getIdStage() > 0l) {
			Long stageId = fosterCareReviewDto.getIdStage();
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, stageId);
			fceReview.setStage(stage);
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdLastUpdatePerson())
				&& fosterCareReviewDto.getIdLastUpdatePerson() > 0l) {
			fceReview.setIdLastUpdatePerson(fosterCareReviewDto.getIdLastUpdatePerson());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdPlacementRateEvent())
				&& fosterCareReviewDto.getIdPlacementRateEvent() > 0l) {
			Long idPlacementRateEvent = fosterCareReviewDto.getIdPlacementRateEvent();
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idPlacementRateEvent);
			fceReview.setEventByIdPlacementRateEvent(event);
		}

		sessionFactory.getCurrentSession().saveOrUpdate(fceReview);

	}

	/*
	 * protected static FceReviewHomeLocal getReviewHome() throws Exception {
	 * InitialContext initialContext = new InitialContext();
	 * 
	 * FceReviewHomeLocal fceReviewHomeLocal = (FceReviewHomeLocal)
	 * initialContext.lookup("FceReview");
	 * 
	 * return fceReviewHomeLocal; }
	 */

	public FceReviewDto findReview(long idFceReview) {
		FceReviewDto dto = new FceReviewDto();
		FceReview entity = (FceReview) sessionFactory.getCurrentSession().get(FceReview.class, idFceReview);
		if (entity != null) {
			ConvertUtils.register(new DateConverter(null), Date.class);
			BeanUtils.copyProperties(entity, dto);
			if (!ObjectUtils.isEmpty(entity.getStage())) {
				Stage stage = entity.getStage();
				StageDto stageDto = new StageDto();
				BeanUtils.copyProperties(stage, stageDto);
				dto.setIdStage(stageDto.getIdStage());
				dto.setStage(stageDto);
			}
			if (!ObjectUtils.isEmpty(entity.getEventByIdEvent())) {
				Event event = entity.getEventByIdEvent();
				EventDto eventDto = new EventDto();
				BeanUtils.copyProperties(event, eventDto);
				dto.setEvent(eventDto);
			}
			if (!ObjectUtils.isEmpty(entity.getEventByIdCurrentPlacementEvent()))
				dto.setIdCurrentPlacementEvent(entity.getEventByIdCurrentPlacementEvent().getIdEvent());
			if (!ObjectUtils.isEmpty(entity.getEventByIdPlacementRateEvent()))
				dto.setIdPlacementRateEvent(entity.getEventByIdPlacementRateEvent().getIdEvent());

			if (!ObjectUtils.isEmpty(entity.getPlacementByIdCurrentPlacementEvent())) {
				Placement placement = entity.getPlacementByIdCurrentPlacementEvent();
				PlacementDto placementDto = new PlacementDto();
				BeanUtils.copyProperties(placement, placementDto);
				dto.setPlacementByIdCurrentPlacementEvent(placementDto);
			}
			if (!ObjectUtils.isEmpty(entity.getFceEligibility())) {
				FceEligibility fceEligibility = entity.getFceEligibility();
				FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
				BeanUtils.copyProperties(fceEligibility, fceEligibilityDto);
				dto.setIdFceEligibility(fceEligibilityDto.getIdFceEligibility());
				dto.setFceEligibility(fceEligibilityDto);
			}

			if (!ObjectUtils.isEmpty(entity.getFceApplication())) {
				FceApplication fceApplication = entity.getFceApplication();
				FceApplicationDto fceApplicationDto = new FceApplicationDto();
				BeanUtils.copyProperties(fceApplication, fceApplicationDto);
				dto.setIdFceApplication(fceApplicationDto.getIdFceApplication());
				dto.setFceApplication(fceApplicationDto);
			}
			// dto.setPlacementByIdCurrentPlacementEvent(entity.getPlacementByIdCurrentPlacementEvent());
			// dto.setFceEligibility(entity.getFceEligibility());
			// dto.setFceApplication(entity.getFceApplication());
		} else {
			throw new ServiceLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));

		}

		return dto;
	}
}
