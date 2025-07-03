package us.tx.state.dfps.service.applicationbackground.serviceimpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.EligibilityDeterminationDto;
import us.tx.state.dfps.service.admin.dto.AaeEligDetermMessgDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.adoptionasstnc.AaeApplAndDetermDBDto;
import us.tx.state.dfps.service.adoptionasstnc.ApplicationBackgroundDto;
import us.tx.state.dfps.service.adoptionasstnc.dao.AdoptionAsstncDao;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.applicationbackground.service.ApplicationBackgroundService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.LegalStatusInfoDao;
import us.tx.state.dfps.service.common.request.AaeApplAndDetermReq;
import us.tx.state.dfps.service.common.request.PersonCharsReq;
import us.tx.state.dfps.service.common.response.PersonCharsRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.AaeUtils;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.person.dao.CvsFaHomeDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.recertification.dao.AaeApplBackgroundDao;
import us.tx.state.dfps.service.recertification.dao.AaeEligDeterminationDao;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.web.person.bean.CvsFaHomeValueBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ApplicationBackgroundServiceImpl Nov 6, 2017- 6:21:04 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ApplicationBackgroundServiceImpl implements ApplicationBackgroundService {

	@Autowired
	private PersonDao personDao;

	@Autowired
	private CvsFaHomeDao cvsFaHomeDao;

	@Autowired
	private AaeApplBackgroundDao appAndBgDao;

	@Autowired
	private AaeEligDeterminationDao aaeEligDetermDao;

	@Autowired
	private PersonUtil personUtil;

	@Autowired
	private EventUtilityService eventUtilityService;

	@Autowired
	private LegalStatusInfoDao legalStatusInfoDao;

	@Autowired
	private AdoptionAsstncDao adoptionAsstncDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private PersonDtlService personDtlService;

	@Autowired
	private CaseUtils caseUtils;

	private static final Logger log = Logger.getLogger(ApplicationBackgroundServiceImpl.class);

	public final static Map<String, String> childCharcCPL = new HashMap<>();
	public final static Map<String, String> childCharcCCH = new HashMap<>();
	static {
		// Child Placement Charc
		childCharcCPL.put(ServiceConstants.CPL_03, ServiceConstants.CPL_03);
		childCharcCPL.put(ServiceConstants.CPL_07, ServiceConstants.CPL_07);
		childCharcCPL.put(ServiceConstants.CPL_11, ServiceConstants.CPL_11);
		childCharcCPL.put(ServiceConstants.CPL_13, ServiceConstants.CPL_13);
		childCharcCPL.put(ServiceConstants.CPL_14, ServiceConstants.CPL_14);
		childCharcCPL.put(ServiceConstants.CPL_18, ServiceConstants.CPL_18);
		childCharcCPL.put(ServiceConstants.CPL_21, ServiceConstants.CPL_21);
		childCharcCPL.put(ServiceConstants.CPL_26, ServiceConstants.CPL_26);
		childCharcCPL.put(ServiceConstants.CPL_34, ServiceConstants.CPL_34);
		childCharcCPL.put(ServiceConstants.CPL_36, ServiceConstants.CPL_36);
		childCharcCPL.put(ServiceConstants.CPL_38, ServiceConstants.CPL_38);
		childCharcCPL.put(ServiceConstants.CPL_40, ServiceConstants.CPL_40);
		childCharcCPL.put(ServiceConstants.CPL_42, ServiceConstants.CPL_42);
		childCharcCPL.put(ServiceConstants.CPL_44, ServiceConstants.CPL_44);
		childCharcCPL.put(ServiceConstants.CPL_48, ServiceConstants.CPL_48);
		childCharcCPL.put(ServiceConstants.CPL_52, ServiceConstants.CPL_52);
		childCharcCPL.put(ServiceConstants.CPL_58, ServiceConstants.CPL_58);
		childCharcCPL.put(ServiceConstants.CPL_59, ServiceConstants.CPL_59);
		childCharcCPL.put(ServiceConstants.CPL_60, ServiceConstants.CPL_60);
		childCharcCPL.put(ServiceConstants.CPL_62, ServiceConstants.CPL_62);
		childCharcCPL.put(ServiceConstants.CPL_63, ServiceConstants.CPL_63);
		childCharcCPL.put(ServiceConstants.CPL_64, ServiceConstants.CPL_64);
		childCharcCPL.put(ServiceConstants.CPL_74, ServiceConstants.CPL_74);
		childCharcCPL.put(ServiceConstants.CPL_75, ServiceConstants.CPL_75);
		childCharcCPL.put(ServiceConstants.CPL_80, ServiceConstants.CPL_80);
		childCharcCPL.put(ServiceConstants.CPL_82, ServiceConstants.CPL_82);
		childCharcCPL.put(ServiceConstants.CPL_84, ServiceConstants.CPL_84);
		childCharcCPL.put(ServiceConstants.CPL_86, ServiceConstants.CPL_86);
		childCharcCPL.put(ServiceConstants.CPL_95, ServiceConstants.CPL_95);

		// Child-Investigation Charc
		childCharcCCH.put(ServiceConstants.CCH_07, ServiceConstants.CCH_07);
		childCharcCCH.put(ServiceConstants.CCH_11, ServiceConstants.CCH_11);
		childCharcCCH.put(ServiceConstants.CCH_13, ServiceConstants.CCH_13);
		childCharcCCH.put(ServiceConstants.CCH_14, ServiceConstants.CCH_14);
		childCharcCCH.put(ServiceConstants.CCH_21, ServiceConstants.CCH_21);
		childCharcCCH.put(ServiceConstants.CCH_36, ServiceConstants.CCH_36);
		childCharcCCH.put(ServiceConstants.CCH_40, ServiceConstants.CCH_40);
		childCharcCCH.put(ServiceConstants.CCH_42, ServiceConstants.CCH_42);
		childCharcCCH.put(ServiceConstants.CCH_44, ServiceConstants.CCH_44);
		childCharcCCH.put(ServiceConstants.CCH_52, ServiceConstants.CCH_52);
		childCharcCCH.put(ServiceConstants.CCH_59, ServiceConstants.CCH_59);
		childCharcCCH.put(ServiceConstants.CCH_60, ServiceConstants.CCH_60);
		childCharcCCH.put(ServiceConstants.CCH_63, ServiceConstants.CCH_63);
		childCharcCCH.put(ServiceConstants.CCH_64, ServiceConstants.CCH_64);
		childCharcCCH.put(ServiceConstants.CCH_74, ServiceConstants.CCH_74);
		childCharcCCH.put(ServiceConstants.CCH_75, ServiceConstants.CCH_75);
		childCharcCCH.put(ServiceConstants.CCH_82, ServiceConstants.CCH_82);
		childCharcCCH.put(ServiceConstants.CCH_84, ServiceConstants.CCH_84);
	}
	public final static String TRACE_TAG = "ApplicationBackgroundBean";

	/**
	 * Method Name: fetchApplicationDetails Method Description: This method
	 * retrieves AAE Application & Background Information, Recent Placements and
	 * Child's information including Social Security Number, Date of Birth,
	 * MNedical Number, Age etc..
	 * 
	 * @param idStage
	 * @param idAppEvent
	 * @param isNewString
	 * @return AaeApplAndDetermDBDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AaeApplAndDetermDBDto fetchApplicationDetails(Long idStage, Long idAppEvent, Boolean isNewString) {
		log.debug("Entering method fetchApplicationDetails in ApplicationBackgroundService");
		AaeApplAndDetermDBDto aaeApplAndDetermDBDto = new AaeApplAndDetermDBDto();
		ApplicationBackgroundDto applicationBackgroundDto = new ApplicationBackgroundDto();
		try {
			Long idPrimChildPerson = personUtil.findPrimaryChildForStage(idStage);
			if (TypeConvUtil.isNullOrEmpty(idPrimChildPerson) || idPrimChildPerson == ServiceConstants.ZERO_VAL) {
				throw new ServiceLayerException(ServiceConstants.PRIMARY_CHILD_MANDATORY + idStage);
			}
			Person person = personDao.getPerson(idPrimChildPerson);
			aaeApplAndDetermDBDto.setIdPerson(idPrimChildPerson);
			aaeApplAndDetermDBDto.setNamePersonfull(person.getNmPersonFull());
			aaeApplAndDetermDBDto.setDtOfBirth(person.getDtPersonBirth());
			aaeApplAndDetermDBDto.setNbrSocialSecurity(personUtil.findSsn(idPrimChildPerson));
			aaeApplAndDetermDBDto.setNbrMedicaid(personUtil.findMedicaid(idPrimChildPerson));
			CvsFaHomeValueBean cvsFaHomeDto = cvsFaHomeDao.displayCvsFaHome(idPrimChildPerson);
			aaeApplAndDetermDBDto.setCdPersonBirthCitizenship(cvsFaHomeDto.getCdPersonBirthCitizenship());
			if (idAppEvent != ServiceConstants.ZERO_VAL) {
				applicationBackgroundDto = appAndBgDao.selectAdptAsstEligAppFromEvent(idAppEvent);
				EligibilityDeterminationDto eligibilityDeterminationDto = aaeEligDetermDao
						.selectAdptAsstEligDetermForAppl(applicationBackgroundDto.getIdAdptEligApplication());
				aaeApplAndDetermDBDto.setEligibilityDeterminationDto(eligibilityDeterminationDto);
				EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idAppEvent);
				aaeApplAndDetermDBDto.setEventValueDto(eventValueDto);
				if (applicationBackgroundDto.getIdQualSibPerson() != ServiceConstants.ZERO_VAL) {
					PersonValueDto siblingPersonInfo = personDao.fetchStagePersonLinkInfo(idStage,
							applicationBackgroundDto.getIdQualSibPerson());
					applicationBackgroundDto.setNameQualSibPersonFull(siblingPersonInfo.getFullName());
					applicationBackgroundDto.setCdStageQualSibPersonRelInt(siblingPersonInfo.getRoleInStageCode());
					if (!ObjectUtils.isEmpty(siblingPersonInfo.getAge()))
						applicationBackgroundDto.setNbrAgeQualSibPerson(Long.valueOf(siblingPersonInfo.getAge()));
				}
				if (!ServiceConstants.CEVTSTAT_APRV.equals(eventValueDto.getCdEventStatus())
						&& !ServiceConstants.CEVTSTAT_COMP.equals(eventValueDto.getCdEventStatus())) {
					applicationBackgroundDto.setCdLegalStatStatus(findLatestCdLegalStatusForChild(idPrimChildPerson));
					applicationBackgroundDto
							.setIdLegalStatEvent(findLatestIdEventLegalStatusForChild(idPrimChildPerson));
					applicationBackgroundDto.setDtParentalRtsTermAll(fetchDtParentalRightsTerm(idPrimChildPerson));
					if (!DateUtils.isNull(aaeApplAndDetermDBDto.getDtOfBirth())) {
						applicationBackgroundDto
								.setNbrChildQualifyAge((long) DateUtils.getAge(aaeApplAndDetermDBDto.getDtOfBirth()));
					}
				}
				Long idStageADOForPlacements = idStage;
		
				List<PlacementDto> placementDtoList = adoptionAsstncDao.fetchADOPlacements(idStageADOForPlacements);
				aaeApplAndDetermDBDto.setPlacementInfoList(placementDtoList);
			}

			if (idAppEvent == ServiceConstants.ZERO_VAL || isNewString) {

				if (!DateUtils.isNull(aaeApplAndDetermDBDto.getDtOfBirth())) {
					applicationBackgroundDto
							.setNbrChildQualifyAge((long) DateUtils.getAge(aaeApplAndDetermDBDto.getDtOfBirth()));
				}

				applicationBackgroundDto.setCdLegalStatStatus(findLatestCdLegalStatusForChild(idPrimChildPerson));
				applicationBackgroundDto.setIdLegalStatEvent(findLatestIdEventLegalStatusForChild(idPrimChildPerson));

				applicationBackgroundDto.setDtParentalRtsTermAll(fetchDtParentalRightsTerm(idPrimChildPerson));
			}

			if (applicationBackgroundDto.getNbrChildQualifyAge() < Long.valueOf(ServiceConstants.NUM_SIX)) {
				applicationBackgroundDto.setIndChildSix(ServiceConstants.N);
			} else {
				applicationBackgroundDto.setIndChildSix(ServiceConstants.Y);
			}
			aaeApplAndDetermDBDto.setApplicationBackgroundDto(applicationBackgroundDto);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		log.debug("Exiting method fetchApplicationDetails in ApplicationBackgroundService");
		return aaeApplAndDetermDBDto;
	}

	/**
	 * Method Name: getDetermQualMessages Method Description:This method returns
	 * the Determine qualification message details
	 * 
	 * @param aaeApplAndDetermDBDto
	 * @return AaeEligDetermMessgDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AaeEligDetermMessgDto getDetermQualMessages(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
		log.debug("Entering method getDetermQualMessages in ApplicationBackgroundService");

		EligibilityDeterminationDto eligibilityDeterminationDto = aaeApplAndDetermDBDto
				.getEligibilityDeterminationDto();
		AaeEligDetermMessgDto aaeEligDetermMessgDto = new AaeEligDetermMessgDto();
		List<Long> qualMessage = new ArrayList<>();
		Boolean qualMessageSet = ServiceConstants.FALSEVAL;
		if (ServiceConstants.Y.equals(eligibilityDeterminationDto.getIndChildQualify())) {
			if (ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID == eligibilityDeterminationDto.getNbrEligDetermOutcm()) {
				aaeEligDetermMessgDto.setChildQualified(ServiceConstants.TRUEVAL);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE_OR_STATE);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
				qualMessageSet = ServiceConstants.TRUEVAL;
			} else if (ServiceConstants.ELIG_TYPE_IVE_PAID == eligibilityDeterminationDto.getNbrEligDetermOutcm()) {

				aaeEligDetermMessgDto.setChildQualified(ServiceConstants.TRUEVAL);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
				qualMessageSet = ServiceConstants.TRUEVAL;
			} else if (ServiceConstants.ELIG_TYPE_STATE_PAID == eligibilityDeterminationDto.getNbrEligDetermOutcm()) {
				aaeEligDetermMessgDto.setChildQualified(ServiceConstants.TRUEVAL);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_IVE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_STATE_PAID);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_STATE_PAID);
				qualMessageSet = ServiceConstants.TRUEVAL;
			} else if (ServiceConstants.ELIG_TYPE_NON_RECUR == eligibilityDeterminationDto.getNbrEligDetermOutcm()) {
				aaeEligDetermMessgDto.setChildQualified(ServiceConstants.TRUEVAL);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_ELIG_NR);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NON_RECUR);
				qualMessageSet = ServiceConstants.TRUEVAL;
			}
		} else {
			aaeEligDetermMessgDto.setChildQualified(ServiceConstants.FALSEVAL);
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
			aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			qualMessageSet = ServiceConstants.TRUEVAL;
		}
		aaeEligDetermMessgDto.setEligMessages(qualMessage);
		if (!qualMessageSet) {
			aaeEligDetermMessgDto = AaeUtils.determineQualification(aaeApplAndDetermDBDto);
		}

		log.debug("Exiting method getDetermQualMessages in ApplicationBackgroundService");
		return aaeEligDetermMessgDto;
	}

	/**
	 * Method Name: callPostEvent Method Description:Calls the common
	 * PostEvent() function. PostEvent() calls DAO that AUD the EVENT table and
	 * it's children.
	 * 
	 * @param eventValueDto
	 * @param cReqFuncCd
	 * @param idStagePersonLinkPerson
	 * @param idAdptEligApplication
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long callPostEvent(EventValueDto eventValueDto, String cReqFuncCd, Long idStagePersonLinkPerson,
			Long idAdptEligApplication) {
		log.debug("Entering method callPostEvent in ApplicationBackgroundService");

		Long ulIdEvent = ServiceConstants.ZERO_VAL;
		Long idLinkedEvent = ServiceConstants.ZERO_VAL;
		EventValueDto eventDto = new EventValueDto();
		try {
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(cReqFuncCd)
					&& ServiceConstants.CD_TASK_PAD_AA_APPL.equals(eventValueDto.getCdEventTask())) {
				idLinkedEvent = appAndBgDao.fetchLinkedADOApplEvent(idAdptEligApplication);
				if (idLinkedEvent != ServiceConstants.ZERO_VAL) {
					eventDto = eventUtilityService.fetchEventInfo(idLinkedEvent);
					eventDto.setCdEventStatus(eventValueDto.getCdEventStatus());
					eventDto.setEventDescr(eventValueDto.getEventDescr());
				}
			}

			if (idLinkedEvent != ServiceConstants.ZERO_VAL) {
				postEventService.postEvent(eventDto, ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.ZERO_VAL);
			}
			ulIdEvent = postEventService.postEvent(eventValueDto, cReqFuncCd, idStagePersonLinkPerson);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		log.debug("Exiting method callPostEvent in ApplicationBackgroundService");
		return ulIdEvent;

	}

	/**
	 * Method Name: withdrawFromAAEProcess Method Description:This method is
	 * called to withdrawal the AAE process. Changes the vent status to COMP
	 * 
	 * @param eventDto
	 * @param cReqFuncCd
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long withdrawFromAAEProcess(EventValueDto eventDto, String cReqFuncCd) {
		log.debug("Entering method withdrawFromAAEProcess in ApplicationBackgroundService");

		try {
			eventDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			eventDto.setEventDescr(ServiceConstants.APPLICATION_COMP_WITHDRAW_FROM_AAE_PROCESS_DESC);
			postEventService.postEvent(eventDto, cReqFuncCd, ServiceConstants.ZERO_VAL);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		log.debug("Exiting method withdrawFromAAEProcess in ApplicationBackgroundService");
		return eventDto.getIdEvent();
	}

	/**
	 * Method Name: findLatestCdLegalStatusForChild Method Description:Fetches
	 * the Latest legal Status type of the child
	 * 
	 * @param idChildPerson
	 * @return String
	 */
	private String findLatestCdLegalStatusForChild(Long idChildPerson) {
		String legalStatusCode = ServiceConstants.EMPTY_STRING;
		try {
			List<LegalStatusDto> legalStatusList = legalStatusInfoDao.fetchLegalStatusListForChild(idChildPerson);
			if (!TypeConvUtil.isNullOrEmpty(legalStatusList)) {
				LegalStatusDto legalStatusDto = legalStatusList.get(ServiceConstants.Zero);
				if (!TypeConvUtil.isNullOrEmpty(legalStatusDto.getCdLegalStatStatus())) {
					legalStatusCode = legalStatusDto.getCdLegalStatStatus();
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return legalStatusCode;
	}

	/**
	 * Method Name: findLatestIdEventLegalStatusForChild Method Description:
	 * Fetches the Id Event of the Latest legal Status of the child
	 * 
	 * @param idChildPerson
	 * @return Long
	 */
	private Long findLatestIdEventLegalStatusForChild(Long idChildPerson) {
		Long legalStatusCode = ServiceConstants.ZERO_VAL;
		try {
			List<LegalStatusDto> legalStatusList = legalStatusInfoDao.fetchLegalStatusListForChild(idChildPerson);
			if (!TypeConvUtil.isNullOrEmpty(legalStatusList)) {
				LegalStatusDto legalStatusDto = legalStatusList.get(ServiceConstants.Zero);
				if (!TypeConvUtil.isNullOrEmpty(legalStatusDto.getCdLegalStatStatus())) {
					legalStatusCode = legalStatusDto.getIdLegalStatEvent();
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		return legalStatusCode;
	}

	/**
	 * Method Name: fetchDtParentalRightsTerm Method Description:Fetches the
	 * Date Parental Rights were Terminated
	 * 
	 * @param idChildPerson
	 * @return Date
	 */
	private Date fetchDtParentalRightsTerm(Long idChildPerson) {
		Date dtParentalRightsTerm = null;
		try {
			List<LegalStatusDto> legalStatusList = legalStatusInfoDao.fetchLegalStatusListForChild(idChildPerson);
			if (!TypeConvUtil.isNullOrEmpty(legalStatusList)) {
				LegalStatusDto legalStatusDto = legalStatusList.get(ServiceConstants.Zero);
				if (!TypeConvUtil.isNullOrEmpty(legalStatusDto.getDtLegalStatStatusDt())
						&& !TypeConvUtil.isNullOrEmpty(legalStatusDto.getCdLegalStatStatus())
						&& (ServiceConstants.CLEGSTAT_040.equals(legalStatusDto.getCdLegalStatStatus())
								|| ServiceConstants.CLEGSTAT_050.equals(legalStatusDto.getCdLegalStatStatus())
								|| ServiceConstants.CLEGSTAT_070.equals(legalStatusDto.getCdLegalStatStatus()))) {
					dtParentalRightsTerm = legalStatusDto.getDtLegalStatStatusDt();
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		return dtParentalRightsTerm;
	}

	/**
	 * Method Name: cleanUpInvalidValues Method Description: This method
	 * validates the value and combination of them Null out invalid fields
	 * before save to Application table.
	 * 
	 * @param aaeApplAndDetermDBDto
	 * @return AaeApplAndDetermDBDto
	 */
	private AaeApplAndDetermDBDto cleanUpInvalidValues(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDBDto.getApplicationBackgroundDto();
		applicationBackgroundDto.setIdPerson(aaeApplAndDetermDBDto.getIdPerson());
		applicationBackgroundDto.setIdLastUpdatePerson(aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());

		// These are the valid fields. Since the page is dynamic so make sure
		// that the fields are valid fields. We will perform field wise logic to
		// check what is the value of the current field and what are the
		// other fields valid values at that current field value.

		// if IndMngngCvs is Yes or Null (i;e is not No)
		if (!ServiceConstants.N.equals(applicationBackgroundDto.getIndMngngCvs())) {
			applicationBackgroundDto.setIndLcpaMngngCvs(null);
		}

		// if IndLcpaMngngCvs is not valid ( i;e it is null or empty)
		if (TypeConvUtil.isNullOrEmpty(applicationBackgroundDto.getIndLcpaMngngCvs())) {
			applicationBackgroundDto.setIndOtherMngngCvs(null);
			applicationBackgroundDto.setNmCvsAgency(null);
			applicationBackgroundDto.setTxtCvsAgencyAddr(null);
		} else if (ServiceConstants.Y.equals(applicationBackgroundDto.getIndLcpaMngngCvs())) {
			applicationBackgroundDto.setIndOtherMngngCvs(null);
		} else if (ServiceConstants.N.equals(applicationBackgroundDto.getIndLcpaMngngCvs())
				&& (TypeConvUtil.isNullOrEmpty(applicationBackgroundDto.getIndOtherMngngCvs()))) {
			applicationBackgroundDto.setNmCvsAgency(null);
			applicationBackgroundDto.setTxtCvsAgencyAddr(null);
		}
		// if both of them are N then null the inner fields.
		if (ServiceConstants.N.equals(applicationBackgroundDto.getIndLcpaMngngCvs())
				&& ServiceConstants.N.equals(applicationBackgroundDto.getIndOtherMngngCvs())) {
			applicationBackgroundDto.setNmCvsAgency(null);
			applicationBackgroundDto.setTxtCvsAgencyAddr(null);
		}
		// if the IndWithSibling is Yes then IdQualSibPerson is valid
		if (!ServiceConstants.Y.equals(applicationBackgroundDto.getIndWithSibling())) {
			applicationBackgroundDto.setIdQualSibPerson(0l);
		}
		// if IndHandicapCond is Yes then only CdMedCondDet is valid.
		if (!ServiceConstants.Y.equals(applicationBackgroundDto.getIndHandicapCond())) {
			applicationBackgroundDto.setIndDocMedProfReq(null);
		}
		// if IndSsaMedReq is Yes then only IndDocSsaReq is valid.
		if (!ServiceConstants.Y.equals(applicationBackgroundDto.getIndSsaMedReq())) {
			applicationBackgroundDto.setIndDocSsaReq(null);
		}
		// if both are not checked then the inner fields are not valid
		if (!ServiceConstants.Y.equals(applicationBackgroundDto.getIndCtznshpPerm())
				&& !ServiceConstants.Y.equals(applicationBackgroundDto.getIndCtznshpOthQualAlien())) {
			applicationBackgroundDto.setIndAdoptedByCtzn(null);
			applicationBackgroundDto.setDtChildEnteredUs(null);
			applicationBackgroundDto.setTxtChildEnteredUs(null);
		}
		// if IndAdoptedByCtzn is Y or NULL then inner fields are not valid.
		else if (!ServiceConstants.N.equals(applicationBackgroundDto.getIndAdoptedByCtzn())) {
			applicationBackgroundDto.setDtChildEnteredUs(null);
			applicationBackgroundDto.setTxtChildEnteredUs(null);
		}
		return aaeApplAndDetermDBDto;
	}

	/**
	 * Method Name: validateSubmitApplication Method Description:This method
	 * validates the data before submitting the Application to AAES.
	 * 
	 * @param AaeApplAndDetermReq
	 * @return Long[] - message Ids
	 * @throws RemoteException
	 * @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List validateSubmitApplication(AaeApplAndDetermReq aaeApplAndDetermReq) {
		log.debug("Entering method validateSubmitApplication in ApplicationBackgroundService");
		List errorsList = null;
		errorsList = getApplSubmitOrDetermQualErrorList(aaeApplAndDetermReq.getAaeApplAndDetermDBDto());
		log.debug("Exiting method validateSubmitApplication in ApplicationBackgroundService");
		// integer
		return errorsList;
	}

	/**
	 * Method Name: validateApplForDetermQual Method Description: This method
	 * validates the data before Determining Qualification for the Application
	 * 
	 * @param AaeApplAndDetermReq
	 * @return Long[] - message Ids
	 * @throws RemoteException
	 * @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List validateApplForDetermQual(AaeApplAndDetermReq aaeApplAndDetermReq) {
		log.debug("Entering method validateApplForDetermQual in ApplicationBackgroundService");
		List errorsList = null;
		errorsList = getApplSubmitOrDetermQualErrorList(aaeApplAndDetermReq.getAaeApplAndDetermDBDto());
		log.debug("Exiting method validateApplForDetermQual in ApplicationBackgroundService");
		return errorsList;

	}

	/**
	 * Method Name: getApplSubmitOrDetermQualErrorList Method Description:
	 * returns a list of errors regarding the most recent legal status of a
	 * given child
	 * 
	 * @param aaeApplAndDetermDB
	 * @return List - error code (Long) list of legal status @
	 */
	private List<Long> getApplSubmitOrDetermQualErrorList(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
		List<Long> errorList = new ArrayList<>();
		Long idChildPerson = aaeApplAndDetermDB.getIdPerson();
		// Check for Legal Status Validations.
		String cdLegalStatusLatest = findLatestCdLegalStatusForChild(idChildPerson);
		// all parental rights terminated OR Adoption Consummated.
		if (ServiceConstants.Y.equals(aaeApplAndDetermDB.getApplicationBackgroundDto().getIndFairHearing())) {
			if (!isAllParentalRightsTerminated(idChildPerson)
					&& !ServiceConstants.CLEGSTAT_090.equals(cdLegalStatusLatest)) {
				errorList.add(ServiceConstants.MSG_LEG_STAT_NT_PMC_RTS_TERM_OR_ADPT_CONS);
			}
		} else {
			// SIR 1004193 - Check if all the parental rights are terminated
			if (!isAllParentalRightsTerminated(idChildPerson)) {
				errorList.add(Long.valueOf(ServiceConstants.MSG_LEGAL_STATUS_NOT_PMC_RTS_TERM_ALL));
			}
		}
		// order to submit the application.
		if (!ServiceConstants.Y.equals(aaeApplAndDetermDB.getApplicationBackgroundDto().getIndFairHearing())) {
			// AaeApplBackgroundDao appAndBgDao = new AaeApplBackgroundDao();
			Long idLatestAAEAppl = appAndBgDao.selectLatestAdptAsstEligAppl(idChildPerson,
					aaeApplAndDetermDB.getEventValueDto().getIdEvent());
			if (!(ServiceConstants.LONG_ZERO_VAL.equals(idLatestAAEAppl))) {
				// fetch the corresponding Eligibility Determination record

				EligibilityDeterminationDto eligDetermDto = aaeEligDetermDao
						.selectAdptAsstEligDetermForAppl(idLatestAAEAppl);
				if (eligDetermDto != null && (!TypeConvUtil.isNullOrEmpty((eligDetermDto.getIndAsstDisqualified()))
						&& ServiceConstants.Y.equals(eligDetermDto.getIndAsstDisqualified()))) {
					errorList.add(Long.valueOf(ServiceConstants.MSG_PRIOR_TYPE_CHILD_AA_DISQUALIFIED));
					// The child's prior determination was Child AA
					// Disqualified.
					// You must check the Fair Hearing Checkbox and enter a Fair
					// Hearing
					// Date in order to submit the application.
				}
			}
		}
		// of the application must have at least one Person characteristic
		// selected on the Person Detail page.
		if (ServiceConstants.Y.equals(aaeApplAndDetermDB.getApplicationBackgroundDto().getIndHandicapCond())) {
			PersonCharsRes personCharRes = new PersonCharsRes();
			PersonCharsReq personCharsReq = new PersonCharsReq();
			personCharsReq.setIdPerson(aaeApplAndDetermDB.getIdPerson());
			personCharsReq.setDtEffectiveDate(new Date());
			// Fetch the Characteristics of the Person with effective date as
			// today.
			// Check of the Category is CPL type - Child-Placement.
			// Check if the CD_CHARACTERISTIC matches the given code types.
			personCharRes = personDtlService.fetchPersonCharDetails(personCharsReq);

			boolean childCharMatches = false;
			for (CharacteristicsDto characteristicsDto : personCharRes.getCharacteristicsDto()) {
				String category = characteristicsDto.getCdCharacCategory();
				if (ServiceConstants.CHARSTAT_D.equals(characteristicsDto.getCdStatus())) {
					if (ServiceConstants.CCHRTCAT_CPL.equals(category)) {
						if (childCharcCPL.containsKey(characteristicsDto.getCdCharacCode())) {
							childCharMatches = true;
							break;
						}
					} else if (ServiceConstants.CCHRTCAT_CCH.equals(category)) {
						if (childCharcCCH.containsKey(characteristicsDto.getCdCharacCode())) {
							childCharMatches = true;
							break;
						}
					}
				}
			}
			if (childCharMatches == false) {
				errorList.add(Long.valueOf(ServiceConstants.MSG_AA_SEL_HANDCP_COND_IN_PERS_CHAR));
			}
		}
		return errorList;
	}

	/**
	 * Method Name: determineQualification Method Description: Determines the
	 * qualification of the Application. It will generate the qualification
	 * messages and saves info based on calculations.
	 * 
	 * @param aaeApplAndDetermReq
	 * @return AaeEligDetermMessgDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AaeEligDetermMessgDto determineQualification(AaeApplAndDetermReq aaeApplAndDetermReq) {
		AaeEligDetermMessgDto aaeEligDetermMessgDto = new AaeEligDetermMessgDto();
		AaeApplAndDetermDBDto aaeApplAndDetermDBDto = aaeApplAndDetermReq.getAaeApplAndDetermDBDto();
		aaeEligDetermMessgDto = AaeUtils.determineQualification(aaeApplAndDetermDBDto);
		EligibilityDeterminationDto eligibilityDeterminationDto = aaeApplAndDetermDBDto
				.getEligibilityDeterminationDto();
		eligibilityDeterminationDto = AaeUtils.blankOutDeterminationIndicators(eligibilityDeterminationDto);
		eligibilityDeterminationDto.setNbrEligDetermOutcm(aaeEligDetermMessgDto.getTypeOfElig());
		if (aaeEligDetermMessgDto.getChildQualified()
				&& aaeEligDetermMessgDto.getTypeOfElig() != (ServiceConstants.APRV_NBR_EVENT_STAT).longValue()) {
			eligibilityDeterminationDto.setIndChildQualify(ServiceConstants.Y);
		} else {
			if (aaeEligDetermMessgDto.getTypeOfElig() == (ServiceConstants.APRV_NBR_EVENT_STAT).longValue()) {
				eligibilityDeterminationDto.setIndChildQualify(ServiceConstants.Y);
				eligibilityDeterminationDto.setIdLastUpdatePerson(aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());
				eligibilityDeterminationDto.setIdFinalDetermPerson(aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());
				eligibilityDeterminationDto.setCdFinalDeterm(ServiceConstants.CELIGIBI_010);
			} else {
				eligibilityDeterminationDto.setIndChildQualify(ServiceConstants.N);
			}
			EventValueDto eventValueDto = aaeApplAndDetermDBDto.getEventValueDto();
			eventValueDto.setEventDescr(AaeUtils.createApplEventDescription(ServiceConstants.CEVTSTAT_COMP));
			eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			eventValueDto.setIdPerson(aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());
			callPostEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.ZERO_VAL,
					aaeApplAndDetermDBDto.getApplicationBackgroundDto().getIdAdptEligApplication());
			eligibilityDeterminationDto.setIndSpecialNeedOutcm(StringUtil
					.toYorN(AaeUtils.specialNeedsExists(aaeApplAndDetermDBDto.getApplicationBackgroundDto())));
			eligibilityDeterminationDto
					.setIndCtznshpOutcm(StringUtil.toYorN(AaeUtils.isChildUSCitizen(aaeApplAndDetermDBDto)));
			eligibilityDeterminationDto.setIndRsnbleEffortOutcm(StringUtil
					.toYorN(AaeUtils.reasonableEffortsTaken(aaeApplAndDetermDBDto.getApplicationBackgroundDto())));
		}
		// eligibilityDeterminationService.saveEligDeterminationValueBean(eligibilityDeterminationDto);
		return aaeEligDetermMessgDto;
	}

	/**
	 * Method Name: fetchLatestApplAndBackgroundInfo Method Description: This
	 * method returns Latest AAE Application And Background information for the
	 * idPerson
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return AaeApplAndDetermDBDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AaeApplAndDetermDBDto fetchLatestApplAndBackgroundInfo(Long idPerson, Long idStage) {
		AaeApplAndDetermDBDto aaeApplAndDetermDBDto = new AaeApplAndDetermDBDto();
		long idAdptEligApplicationLatest = appAndBgDao.selectLatestAdptAsstEligAppl(idPerson, idStage);
		if (idAdptEligApplicationLatest != ServiceConstants.ZERO_VAL) {
			aaeApplAndDetermDBDto.setApplicationBackgroundDto(
					appAndBgDao.selectAdptAsstEligApplication(idAdptEligApplicationLatest));
			aaeApplAndDetermDBDto.setEligibilityDeterminationDto(
					aaeEligDetermDao.selectAdptAsstEligDetermForAppl(idAdptEligApplicationLatest));
			Person person = personDao.getPerson(idPerson);
			aaeApplAndDetermDBDto.setIdPerson(idPerson);
			aaeApplAndDetermDBDto.setNamePersonfull(person.getNmPersonFull());
			aaeApplAndDetermDBDto.setDtOfBirth(person.getDtPersonBirth());
			long idAppEvent = appAndBgDao.fetchIdEventForAppl(idAdptEligApplicationLatest);
			EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idAppEvent);
			aaeApplAndDetermDBDto.setEventValueDto(eventValueDto);
		}
		return aaeApplAndDetermDBDto;
	}

	/**
	 * Method Name: isAdptAssistApplUnapproved Method Description: This method
	 * checks if there is a latest existing Adoption Application which is in
	 * PEND or COMP status ( not withdrawn )
	 * 
	 * @param idStage
	 * @return Boolean @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAdptAssistApplUnapproved(Long idStage) {
		boolean isAdptAssistApplUnapproved = ServiceConstants.FALSEVAL;
		long idEventLatestAppl = appAndBgDao.fetchIdEventOfLatestAppl(idStage);
		if (idEventLatestAppl != ServiceConstants.ZERO_VAL) {
			ApplicationBackgroundDto applicationBackgroundDto = appAndBgDao
					.selectAdptAsstEligAppFromEvent(idEventLatestAppl);
			EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idEventLatestAppl);
			if (ServiceConstants.CEVTSTAT_PEND.equals(eventValueDto.getCdEventStatus())
					|| (ServiceConstants.CEVTSTAT_COMP.equals(eventValueDto.getCdEventStatus())
							&& TypeConvUtil.isNullOrEmpty(applicationBackgroundDto.getCdWithdrawRsn()))) {
				isAdptAssistApplUnapproved = true;
			}
		}
		return isAdptAssistApplUnapproved;
	}

	/**
	 * Method Name: createNewAaeApplication Method Description: This method
	 * creates new AAE Application(with event) and Background information
	 * 
	 * @param aaeApplAndDetermDBDto
	 * @return Long @
	 */
	private Long createNewAaeApplication(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDBDto.getApplicationBackgroundDto();
		if (!TypeConvUtil.isNullOrEmpty(aaeApplAndDetermDBDto.getDtOfBirth())) {
			applicationBackgroundDto
					.setNbrChildQualifyAge(DateUtils.getAgeAtCurrentFiscalYear(aaeApplAndDetermDBDto.getDtOfBirth()));
		}
		EventValueDto eventValueDto = aaeApplAndDetermDBDto.getEventValueDto();
		if (!TypeConvUtil.isNullOrEmpty(applicationBackgroundDto.getCdWithdrawRsn())) {
			eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			eventValueDto.setEventDescr(ServiceConstants.APPLICATION_COMP_WITHDRAW_FROM_AAE_PROCESS_DESC);
		} else {
			eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
			eventValueDto.setEventDescr(AaeUtils.createApplEventDescription(ServiceConstants.CEVTSTAT_PROC));
		}
		eventValueDto.setCdEventType(ServiceConstants.CD_EVENT_TYPE_AA_APPL);
		long idNewEvent = callPostEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_ADD,
				aaeApplAndDetermDBDto.getIdPerson(), ServiceConstants.ZERO_VAL);
		long idAdptEligApplication = appAndBgDao.insertAdptAsstEligApplication(applicationBackgroundDto);
		appAndBgDao.insertAdptAsstAppEventLink(idAdptEligApplication, idNewEvent, eventValueDto.getIdCase(),
				aaeApplAndDetermDBDto.getIdLastUpdatedByPerson());

		return idNewEvent;
	}

	private boolean isAllParentalRightsTerminated(Long idChildPerson) {
		// Legal Status CLEGSTAT_050
		boolean isFatherParentalRtsTerm = false;
		// Legal Status CLEGSTAT_070
		boolean isMotherParentalRightsTerm = false;
		// Legal Status CLEGSTAT_040
		boolean isPMCRightsTermAll = false;
		String legalStatusCode = ServiceConstants.EMPTY_STRING;

		List<LegalStatusDto> legalStatusRows = legalStatusInfoDao.fetchLegalStatusListForChild(idChildPerson);
		if (!ObjectUtils.isEmpty(legalStatusRows)) {
			// Check for most recent Legal status.
			// List<LegalStatusDto>
			LegalStatusDto latestlegalStatusRow = (LegalStatusDto) legalStatusRows.get(0);
			if (TypeConvUtil.isNullOrEmpty(latestlegalStatusRow)
					&& TypeConvUtil.isNullOrEmpty(latestlegalStatusRow.getCdLegalStatStatus())) {
				legalStatusCode = (String) latestlegalStatusRow.getCdLegalStatStatus();
				// If most recent is PMC/ Rts Term (All) then return true
				if (ServiceConstants.CLEGSTAT_040.equals(legalStatusCode)) {
					return true;
				}
				// Most recent legal status should be either of the three.
				// If the most recent legal status is not CLEGSTAT_040,
				// CLEGSTAT_050 or CLEGSTAT_070 then parents rights
				// are not terminated, so return false;
				if (!ServiceConstants.CLEGSTAT_040.equals(legalStatusCode)
						&& !ServiceConstants.CLEGSTAT_050.equals(legalStatusCode)
						&& !ServiceConstants.CLEGSTAT_070.equals(legalStatusCode)) {
					return false;
				}
			}
			// Iterate through all legal statuses to find a combination.
			// If Legal status is of type CLEGSTAT_040, CLEGSTAT_050 or
			// CLEGSTAT_070
			// then Iterate for Parental rights term all.
			for (Iterator iterator = legalStatusRows.iterator(); iterator.hasNext();) {
				LegalStatusDto latestlegalStatusRow1 = (LegalStatusDto) iterator.next();
				if (latestlegalStatusRow1 != null && latestlegalStatusRow1.getCdLegalStatStatus() != null) {
					legalStatusCode = (String) latestlegalStatusRow1.getCdLegalStatStatus();
					if (ServiceConstants.CLEGSTAT_040.equals(legalStatusCode)) {
						isPMCRightsTermAll = true;
					} else if (ServiceConstants.CLEGSTAT_050.equals(legalStatusCode)) {
						isFatherParentalRtsTerm = true;
					} else if (ServiceConstants.CLEGSTAT_070.equals(legalStatusCode)) {
						isMotherParentalRightsTerm = true;
					}
				}
			}
			// Atleast two should be true to terminate all parental rights
			if (isPMCRightsTermAll && (isFatherParentalRtsTerm || isMotherParentalRightsTerm)) {
				return true;
			}
			// if Both are true, then it means all parental rights are
			// terminated.
			if (isFatherParentalRtsTerm && isMotherParentalRightsTerm) {
				return true;
			}
		}
		return false;
	}

	// @Override
	// @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	// propagation = Propagation.REQUIRED)
	// public AaeApplAndDetermRes getApplAndBackgroundInfo(AaeApplAndDetermReq
	// aaeApplAndDetermReq) {
	// LOG.debug("Entering method getApplAndBackgroundInfo in
	// ApplicationBackgroundService");
	//
	// AaeApplAndDetermRes aaeApplAndDetermRes = new AaeApplAndDetermRes();
	//
	//
	// AaeApplAndDetermDBDto aaeApplAndDetermDBDto =
	// aaeApplAndDetermReq.getAaeApplAndDetermDBDto();
	// // Get new connection from Connection pool.
	// //Long idStage, Long idAppEvent, Boolean isNewString
	// // Fetch AAE Application and Other Background Information.
	// aaeApplAndDetermRes.setAaeApplAndDetermDBDto(fetchApplicationDetails());
	// //aaeApplAndDetermRes.setAaeApplAndDetermDBDto(
	// fetchApplicationDetails(aaeApplAndDetermReq.getIdStage(),aaeApplAndDetermDBDto.getEventValueDto().getCdEventTask(),aaeApplAndDetermDBDto.getEventValueDto())));
	//
	// // ;
	//
	//
	//
	// LOG.debug("Exiting method getApplAndBackgroundInfo in
	// ApplicationBackgroundService");
	// return aaeApplAndDetermRes;
	// }

	// In process of being converted to fulfill requirements for controller
	// class - Rick
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveApplAndBackgroundInfo(AaeApplAndDetermReq aaeApplAndDetermReq) {
		log.debug("Entering method saveApplAndBackgroundInfo in ApplicationBackgroundService");
		{

			Long idEvent = ServiceConstants.ZERO_VAL;

			// request gets the applamddeterm dto
			// clean up the invalid values
			// get the applicationbackground dto

			AaeApplAndDetermDBDto aaeApplAndDetermDBDto = aaeApplAndDetermReq.getAaeApplAndDetermDBDto();
			aaeApplAndDetermDBDto = cleanUpInvalidValues(aaeApplAndDetermDBDto);
			ApplicationBackgroundDto appDto = aaeApplAndDetermDBDto.getApplicationBackgroundDto();

			if (!appDto.getIdAdptEligApplication().equals(ServiceConstants.Zero.longValue())) {
				// Populate the Event Object
				// EventDto eventDto = aaeApplAndDetermDB.getEventValueBean();
				// as withdrawn.
				EventValueDto eventValueDto = aaeApplAndDetermDBDto.getEventValueDto();
				// if (StringUtils.isValid(appDto.getCdWithdrawRsn())) {
				if (!TypeConvUtil.isNullOrEmpty(appDto.getCdWithdrawRsn())) {
					withdrawFromAAEProcess(eventValueDto, ServiceConstants.REQ_FUNC_CD_UPDATE);

					// Also mark the associated Todo with event as complete.

					// Original two lines below
					// EligibilityDeterminationValueBeanDto
					// determinationValueBeanDto = new
					// EligibilityDeterminationValueBeanDto();
					// determinationValueBeanDto.completeEligDetermAssignedTodo(eventDto.getIdEvent());

					// Replacement with wiring to new RESTful classes
					// Use auto-wiring to instantiate the new
					// eligibilityDeterminationService object
					// eligibilityDeterminationService.completeEligDetermAssignedTodo((long)
					// eventValueDto.getIdEvent());

				} else {
					// Update the status of the event
					callPostEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_UPDATE, 0L,
							appDto.getIdAdptEligApplication());
				}
				// Update the Application info

				appAndBgDao.updateAdptAsstEligApplication(appDto);
				idEvent = eventValueDto.getIdEvent();

				// If the Eligibility Determination record exists then save
				// that also.
				// if
				// (!aaeApplAndDetermDBDto.getEligibilityDeterminationDto().getIdAdptEligDeterm()
				// .equals(ServiceConstants.Zero)) {
				// new EligibilityDeterminationBean()
				// .saveEligDeterminationValueBean(aaeApplAndDetermDBDto.getEligDetermValueBean());
				// }

				// If the Eligibility Determination record exists then save that
				// also.
				if (!aaeApplAndDetermDBDto.getEligibilityDeterminationDto().getIdAdptEligDeterm()
						.equals(ServiceConstants.Zero.longValue())) {
					/*
					 * eligibilityDeterminationService
					 * .saveEligDeterminationValueBean(aaeApplAndDetermDBDto.
					 * getEligibilityDeterminationDto());
					 */
				}

			} else {
				// If it doesn't exist, create new application
				idEvent = createNewAaeApplication(aaeApplAndDetermDBDto);
			}

			log.debug("Exiting method saveApplAndBackgroundInfo in ApplicationBackgroundService");

			return idEvent;
		}
	}

}
