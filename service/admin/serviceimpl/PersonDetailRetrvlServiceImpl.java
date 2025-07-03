package us.tx.state.dfps.service.admin.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.admin.dao.AllegationFacilAllegCountDao;
import us.tx.state.dfps.service.admin.dao.AllegationSelIdStageDao;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailSelDisptnDao;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkMergeViewCountDao;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkPersonMergeViewDao;
import us.tx.state.dfps.service.admin.dao.InitChildDthNarrViewCountDao;
import us.tx.state.dfps.service.admin.dao.NamePrimayEndDateDao;
import us.tx.state.dfps.service.admin.dao.PersonCategoriesPidDao;
import us.tx.state.dfps.service.admin.dao.PersonHomeRemovalIdDao;
import us.tx.state.dfps.service.admin.dao.PersonIdTypeDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.PersonRaceDetailsDao;
import us.tx.state.dfps.service.admin.dao.PersonSelectEthnicityDao;
import us.tx.state.dfps.service.admin.dao.PlacementEventDao;
import us.tx.state.dfps.service.admin.dao.ReferralPersonLinkDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkMergeViewDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountInDto;
import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountOutDto;
import us.tx.state.dfps.service.admin.dto.AllegationInDto;
import us.tx.state.dfps.service.admin.dto.AllegationOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailOutDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountOutDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkPersonMergeViewOutDto;
import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountInDto;
import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountOutDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateInDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryOutDto;
import us.tx.state.dfps.service.admin.dto.PersonDetailRetrvloDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityInDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityOutDto;
import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalInDto;
import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalOutDto;
import us.tx.state.dfps.service.admin.dto.PersonIdTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonIdTypeOutDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementEventInDto;
import us.tx.state.dfps.service.admin.dto.PlacementEventOutDto;
import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.admin.service.PersonDetailRetrvlService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.common.response.PersonDetailsRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.person.dto.PersonDtlEventDto;
import us.tx.state.dfps.service.person.dto.PersonDtlStageDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Service
 * Retrieves all the information for the person detail window. Aug 4, 2017-
 * 9:21:24 AM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonDetailRetrvlServiceImpl implements PersonDetailRetrvlService {

	@Autowired
	MessageSource messageSource;

	// Cinv39dD
	@Autowired
	StagePersonLinkRecordDao stagePersonLinkRecordDao;

	// Ccmn44dD
	@Autowired
	PersonPortfolioDao personPortfolioDao;

	// Csesf7d
	@Autowired
	PersonIdTypeDao personIdTypeDao;

	// Clss79d
	@Autowired
	PersonRaceDetailsDao personRaceDetailsDao;

	// Clss80d
	@Autowired
	PersonSelectEthnicityDao personSelectEthnicityDao;

	// Cinv29d
	@Autowired
	PersonCategoriesPidDao personCategoriesPidDao;

	// Cinv33d
	@Autowired
	StagePersonLinkMergeViewDao stagePersonLinkMergeViewDao;

	// Ccmn40d
	@Autowired
	NamePrimayEndDateDao namePrimayEndDateDao;

	// Cinv87d
	@Autowired
	AllegationFacilAllegCountDao allegationFacilAllegCountDao;

	// Cses0ad
	@Autowired
	CpsInvstDetailSelDisptnDao cpsInvstDetailSelDisptnDao;

	// Cses46d
	@Autowired
	PersonHomeRemovalIdDao personHomeRemovalIdDao;

	// Cses47d
	@Autowired
	ReferralPersonLinkDao referralPersonLinkDao;

	// Cinv79d
	@Autowired
	EventPersonLinkMergeViewCountDao eventPersonLinkMergeViewCountDao;

	// Cinv52d
	@Autowired
	EventPersonLinkPersonMergeViewDao eventPersonLinkPersonMergeViewDao;

	// Cinvb5d
	@Autowired
	AllegationSelIdStageDao allegationSelIdStageDao;

	// Cdyn25d
	@Autowired
	InitChildDthNarrViewCountDao initChildDthNarrViewCountDao;

	// Cinve9d
	@Autowired
	PlacementEventDao placementEventDao;

	@Autowired
	MobileUtil mobileUtil;

	private static final Logger log = Logger.getLogger(PersonDetailRetrvlServiceImpl.class);
	@Autowired
	private PersonDetailDao persondetaildao;

	/**
	 *
	 * Method Name: callPersonDetailRetrvlService Method Description: This
	 * Service Retrieves all the information for the person detail window.
	 *
	 * @param personDetailsReq
	 * @return PersonDetailRetrvloDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonDetailsRes personDetailRetrvl(PersonDetailsReq personDetailsReq) {
		log.debug("Entering method personDetailRetrvl in PersonDetailRetrvlServiceImpl");
		// Declaration
		PersonDetailRetrvloDto personDetailRetrvloDto = new PersonDetailRetrvloDto();
		EventPersonLinkMergeViewCountInDto eventPersonLinkMergeViewCountInDto = new EventPersonLinkMergeViewCountInDto();
		StagePersonLinkRecordInDto stagePersonLinkRecordInDto = new StagePersonLinkRecordInDto();
		PersonPortfolioInDto personPortfolioInDto = new PersonPortfolioInDto();
		PersonIdTypeInDto personIdTypeInDto = new PersonIdTypeInDto();
		PersonRaceInDto personRaceInDto = new PersonRaceInDto();
		List<String> personRace = new ArrayList<>();
		PersonEthnicityInDto personEthnicityInDto = new PersonEthnicityInDto();
		List<PersonEthnicityDto> personEthnicity = new ArrayList<>();
		PersonCategoryInDto personCategoryInDto = new PersonCategoryInDto();
		List<String> cdCategoryCategory = new ArrayList<>();
		StagePersonLinkMergeViewInDto stagePersonLinkMergeViewInDto = new StagePersonLinkMergeViewInDto();
		List<PersonDtlStageDto> personDtlStageDto = new ArrayList<>();
		EventPersonLinkPersonMergeViewInDto eventPersonLinkPersonMergeViewInDto = new EventPersonLinkPersonMergeViewInDto();
		List<PersonDtlEventDto> personDtlEventDtos = new ArrayList<>();
		NamePrimayEndDateInDto namePrimayEndDateInDto = new NamePrimayEndDateInDto();
		PersonHomeRemovalInDto personHomeRemovalInDto = new PersonHomeRemovalInDto();
		ReferralPersonLinkInDto referralPersonLinkInDto = new ReferralPersonLinkInDto();
		AllegationFacilAllegCountInDto allegationFacilAllegCountInDto = new AllegationFacilAllegCountInDto();
		CpsInvstDetailInDto cpsInvstDetailInDto = new CpsInvstDetailInDto();
		InitChildDthNarrViewCountInDto initChildDthNarrViewCountInDto = new InitChildDthNarrViewCountInDto();
		AllegationInDto allegationInDto = new AllegationInDto();
		PlacementEventInDto placementEventInDto = new PlacementEventInDto();

		/**
		 * This Retrieves an indicator that the person is involved in an active
		 * event.
		 */
		// CallCINV79D(pInputMsg,pOutputMsg);
		eventPersonLinkMergeViewCountInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<EventPersonLinkMergeViewCountOutDto> personLinkMergeViewCountOutDtos = eventPersonLinkMergeViewCountDao
				.getRecordCount(eventPersonLinkMergeViewCountInDto);
		if (!TypeConvUtil.isNullOrEmpty(personLinkMergeViewCountOutDtos) && personLinkMergeViewCountOutDtos.size() > 0
				&& personLinkMergeViewCountOutDtos.get(0).getSysNbrNumberOfRows() > 0) {
			personDetailRetrvloDto.setIndActiveEvent(ServiceConstants.STRING_IND_Y);
		} else {
			personDetailRetrvloDto.setIndActiveEvent(ServiceConstants.STRING_IND_N);
		}
		/**
		 * This Dao retrieves a full row of the Stage Person Link Table using Id
		 * Stage and Id Person
		 */
		// CallCINV39D(pInputMsg,pOutputMsg);
		stagePersonLinkRecordInDto.setIdStage(personDetailsReq.getIdStage());
		stagePersonLinkRecordInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<StagePersonLinkRecordOutDto> stagePersonLinkRecordOutDtos = stagePersonLinkRecordDao
				.getStagePersonLinkRecord(stagePersonLinkRecordInDto);
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkRecordOutDtos) && stagePersonLinkRecordOutDtos.size() > 0) {
			StagePersonLinkRecordOutDto stagePersonLinkRecordOutDto = stagePersonLinkRecordOutDtos.get(0);
			personDetailRetrvloDto.setCdStagePersType(stagePersonLinkRecordOutDto.getCdStagePersType());
			personDetailRetrvloDto.setCdStagePersRole(stagePersonLinkRecordOutDto.getCdStagePersRole());
			personDetailRetrvloDto.setIndStagePersReporter(stagePersonLinkRecordOutDto.getIndStagePersReporter());
			personDetailRetrvloDto.setIndStagePersInLaw(stagePersonLinkRecordOutDto.getIndStagePersInLaw());
			personDetailRetrvloDto.setIndCaringAdult(stagePersonLinkRecordOutDto.getIndCaringAdult());
			personDetailRetrvloDto.setIndNytdDesgContact(stagePersonLinkRecordOutDto.getIndNytdDesgContact());
			personDetailRetrvloDto.setIndNytdPrimary(stagePersonLinkRecordOutDto.getIndNytdPrimary());
			personDetailRetrvloDto.setCdStagePersRelInt(stagePersonLinkRecordOutDto.getCdStagePersRelInt());
			personDetailRetrvloDto.setIdStagePerson(stagePersonLinkRecordOutDto.getIdStagePerson());
			personDetailRetrvloDto.setIndCdStagePersSearch(stagePersonLinkRecordOutDto.getIndCdStagePersSearch());
			personDetailRetrvloDto.setTsLastUpdate(stagePersonLinkRecordOutDto.getTsLastUpdate());
			personDetailRetrvloDto.setDtLastUpdate(stagePersonLinkRecordOutDto.getDtLasteUpdate());
		}
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkRecordOutDtos) && stagePersonLinkRecordOutDtos.size() > 0
				&& (!(ServiceConstants.WINDOW_MODE_LOWER.equals(personDetailsReq.getCdWinMode())
				&& !(ServiceConstants.WINDOW_MODE_INQUIRE.equals(personDetailsReq.getCdWinMode()))
				&& !(ServiceConstants.WINDOW_MODE_MNTN_PERSON.equals(personDetailsReq.getCdWinMode()))))) {
			personDetailRetrvloDto.setSysIndInStage(ServiceConstants.STRING_IND_N);
		} else {
			if ((ServiceConstants.WINDOW_MODE_LOWER.equals(personDetailsReq.getCdWinMode()))
					|| (ServiceConstants.WINDOW_MODE_INQUIRE.equals(personDetailsReq.getCdWinMode()))
					|| (ServiceConstants.WINDOW_MODE_MNTN_PERSON.equals(personDetailsReq.getCdWinMode()))) {
				if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkRecordOutDtos)
						&& stagePersonLinkRecordOutDtos.size() > 0) {
					personDetailRetrvloDto.setSysIndInStage(ServiceConstants.STRING_IND_Y);
				} else {
					personDetailRetrvloDto.setSysIndInStage(ServiceConstants.STRING_IND_N);
				}
			}
		}
		/**
		 * This Retrieves a full row from the person table
		 */
		// CallCCMN44D(pInputMsg,pOutputMsg);
		personPortfolioInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<PersonPortfolioOutDto> personPortfolioOutDtos = personPortfolioDao.getPersonRecord(personPortfolioInDto);
		if (!TypeConvUtil.isNullOrEmpty(personPortfolioOutDtos) && personPortfolioOutDtos.size() > 0) {
			PersonPortfolioOutDto personPortfolioOutDto = personPortfolioOutDtos.get(0);
			personDetailRetrvloDto.setIndPersonDobApprox(personPortfolioOutDto.getIndPersonDobApprox());
			personDetailRetrvloDto.setIndEducationPortfolio(personPortfolioOutDto.getIndEducationPortfolio());
			personDetailRetrvloDto.setCdPersonLivArr(personPortfolioOutDto.getCdPersonLivArr());
			personDetailRetrvloDto.setCdPersGuardCnsrv(personPortfolioOutDto.getCdPersGuardCnsrv());
			personDetailRetrvloDto.setCdPersonDeath(personPortfolioOutDto.getCdPersonDeath());
			personDetailRetrvloDto.setCdMannerDeath(personPortfolioOutDto.getCdMannerDeath());
			personDetailRetrvloDto.setCdDeathRsnCps(personPortfolioOutDto.getCdDeathRsnCps());
			personDetailRetrvloDto.setCdDeathCause(personPortfolioOutDto.getCdDeathCause());
			personDetailRetrvloDto.setCdDeathAutpsyRslt(personPortfolioOutDto.getCdDeathAutpsyRslt());
			personDetailRetrvloDto.setCdDeathFinding(personPortfolioOutDto.getCdDeathFinding());
			personDetailRetrvloDto.setFatalityDetails(personPortfolioOutDto.getFatalityDetails());
			personDetailRetrvloDto.setCdPersonEthnicGroup(personPortfolioOutDto.getCdPersonEthnicGroup());
			personDetailRetrvloDto.setCdTribeEligible(personPortfolioOutDto.getCdTribeEligible());
			personDetailRetrvloDto.setCdPersonLanguage(personPortfolioOutDto.getCdPersonLanguage());
			personDetailRetrvloDto.setCdPersonMaritalStatus(personPortfolioOutDto.getCdPersonMaritalStatus());
			personDetailRetrvloDto.setCdPersonReligion(personPortfolioOutDto.getCdPersonReligion());
			personDetailRetrvloDto.setPersonSex(personPortfolioOutDto.getPersonSex());
			personDetailRetrvloDto.setCdDisasterRlf(personPortfolioOutDto.getCdDisasterRlf());
			personDetailRetrvloDto.setCdPersonStatus(personPortfolioOutDto.getCdPersonStatus());
			personDetailRetrvloDto.setDtPersonBirth(personPortfolioOutDto.getDtPersonBirth());
			personDetailRetrvloDto.setDtPersonDeath(personPortfolioOutDto.getDtPersonDeath());
			if (!TypeConvUtil.isNullOrEmpty(personPortfolioOutDto.getDtPersonBirth())) {
				personDetailRetrvloDto.setNbrPersonAge(DateUtils.getAge(personPortfolioOutDto.getDtPersonBirth()));
			}
			personDetailRetrvloDto.setNmPersonFull(personPortfolioOutDto.getNmPersonFull());
			personDetailRetrvloDto.setNmNameFirst(personPortfolioOutDto.getNmPersonFirst());
			personDetailRetrvloDto.setNmNameLast(personPortfolioOutDto.getNmPersonLast());
			personDetailRetrvloDto.setNmNameMiddle(personPortfolioOutDto.getNmPersonMiddle());
			personDetailRetrvloDto.setCdNameSuffix(personPortfolioOutDto.getCdNmPersonSuffix());
			personDetailRetrvloDto.setOccupation(personPortfolioOutDto.getOccupation());
			personDetailRetrvloDto.setCdOccupation(personPortfolioOutDto.getCdOccupation());
			personDetailRetrvloDto.setCdPersonChar(personPortfolioOutDto.getCdPersonChar());
			personDetailRetrvloDto.setIndAbuseNglctDeathInCare(personPortfolioOutDto.getIndAbuseNglctDeathInCare());
			personDetailRetrvloDto.setTsSysTsLastUpdate2(personPortfolioOutDto.getTsSysTsLastUpdate2());
			personDetailRetrvloDto.setDtLastUpdate2(personPortfolioOutDto.getDtLastUpdate2());
		}
		/**
		 * This Retrieves SSN row from the person_id table if one exists
		 */
		// CallCSESF7D(pInputMsg,pOutputMsg);
		personIdTypeInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<PersonIdTypeOutDto> personIdTypeOutDtos = personIdTypeDao.getSSNforPersonID(personIdTypeInDto);
		if (!TypeConvUtil.isNullOrEmpty(personIdTypeOutDtos) && personIdTypeOutDtos.size() > 0) {
			personDetailRetrvloDto.setNbrPersonIdNumber(personIdTypeOutDtos.get(0).getNbrPersonIdNumber());
			personDetailRetrvloDto.setCdSsnSource(personIdTypeOutDtos.get(0).getCdSsnSource());
			personDetailRetrvloDto.setCdSsnVerifMeth(personIdTypeOutDtos.get(0).getCdSsnVerifMeth());
		}
		/**
		 * This Retrieves Person Race information for the person
		 */
		// CallCLSS79D(pInputMsg,pOutputMsg);
		personRaceInDto.setIdPerson(personDetailsReq.getIdPerson());
		if(!mobileUtil.isMPSEnvironment()) {
			List<PersonRaceOutDto> personRaceOutDtos = personRaceDetailsDao.getRaceDetails(personRaceInDto);
			if (!TypeConvUtil.isNullOrEmpty(personRaceOutDtos) && personRaceOutDtos.size() > 0) {
				for (PersonRaceOutDto personRaceOutDto : personRaceOutDtos) {
					// objCinv04sog03Dto.setSzCdPersonRace(objClss79doDto.getCdPersonRace());
					personRace.add(personRaceOutDto.getCdPersonRace());
				}
			}
			personDetailRetrvloDto.setCdPersonRace(personRace);
		}
		// pOutputMsg.setCINV04SOG03(personRace);
		/**
		 * This Retrieves Person Ethnicity Information on the Person
		 */
		// CallCLSS80D(pInputMsg,pOutputMsg);
		personEthnicityInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<PersonEthnicityOutDto> personEthnicityOutDtos = personSelectEthnicityDao
				.getPersonEthnicity(personEthnicityInDto);
		if (!TypeConvUtil.isNullOrEmpty(personEthnicityOutDtos) && personEthnicityOutDtos.size() > 0) {
			for (PersonEthnicityOutDto personEthnicityOutDto : personEthnicityOutDtos) {
				PersonEthnicityDto personEthnicityDto = new PersonEthnicityDto();
				personEthnicityDto.setCdPersonEthnicity(personEthnicityOutDto.getCdPersonEthnicity());
				personEthnicity.add(personEthnicityDto);
			}
		}
		personDetailRetrvloDto.setPersonEthnicityDtoList(personEthnicity);
		/**
		 * This retrieves all the category information for a person. This is a
		 * list using id person.
		 */
		// CallCINV29D(pInputMsg,pOutputMsg);
		personCategoryInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<PersonCategoryOutDto> personCategoryOutDtos = personCategoriesPidDao
				.getAllCaegoriesForPID(personCategoryInDto);
		if (!TypeConvUtil.isNullOrEmpty(personCategoryOutDtos) && personCategoryOutDtos.size() > 0) {
			for (PersonCategoryOutDto personCategoryOutDto : personCategoryOutDtos) {
				// Cinv04sogooDto objCinv04sogooDto = new Cinv04sogooDto();
				// objCinv04sogooDto.setCdCategoryCategory(objCinv29doDto.getCdCategoryCategory());
				cdCategoryCategory.add(personCategoryOutDto.getCdCategoryCategory());
			}
		}
		personDetailRetrvloDto.setCdCategoryCategoryList(cdCategoryCategory);
		/**
		 * This joins the Stage Person Link and the stage Table. to determine
		 * all of the active programs and stages that a that a person is
		 * involved in
		 */
		// CallCINV33D(pInputMsg,pOutputMsg);
		stagePersonLinkMergeViewInDto.setIdPerson(personDetailsReq.getIdPerson());
		stagePersonLinkMergeViewInDto.setCdStageProgram(personDetailsReq.getCdStageProgram());
		List<StagePersonLinkMergeViewOutDto> stagePersonLinkMergeViewOutDtos = stagePersonLinkMergeViewDao
				.getActiveProgStagePID(stagePersonLinkMergeViewInDto);
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkMergeViewOutDtos)
				&& stagePersonLinkMergeViewOutDtos.size() > 0) {
			for (StagePersonLinkMergeViewOutDto stagePersonLinkMergeViewOutDto : stagePersonLinkMergeViewOutDtos) {
				PersonDtlStageDto personDtlStage = new PersonDtlStageDto();
				personDtlStage.setIdCase(stagePersonLinkMergeViewOutDto.getIdCase());
				personDtlStage.setIdStage(stagePersonLinkMergeViewOutDto.getIdStage());
				personDtlStage.setCdStageProgram(stagePersonLinkMergeViewOutDto.getCdStageProgram());
				personDtlStageDto.add(personDtlStage);
			}
		}
		personDetailRetrvloDto.setPersonDtlStageDto(personDtlStageDto);
		// pOutputMsg.setCINV04SG01(liCinv04sg01Dto);
		/**
		 * This retrieves all Event types a person is involved in.
		 */
		// CallCINV52D(pInputMsg,pOutputMsg);
		eventPersonLinkPersonMergeViewInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<EventPersonLinkPersonMergeViewOutDto> personMergeViewOutDtos = eventPersonLinkPersonMergeViewDao
				.getEventTypeForPerson(eventPersonLinkPersonMergeViewInDto);
		if (!TypeConvUtil.isNullOrEmpty(personMergeViewOutDtos) && personMergeViewOutDtos.size() > 0) {
			for (EventPersonLinkPersonMergeViewOutDto eventPersonLinkPersonMergeViewOutDto : personMergeViewOutDtos) {
				// Cinv04sg02Dto objCinv04sg02Dto = new Cinv04sg02Dto();
				PersonDtlEventDto personDtlEvent = new PersonDtlEventDto();
				personDtlEvent.setCdEventType(eventPersonLinkPersonMergeViewOutDto.getCdEventType());
				personDtlEvent.setCdEventStatus(eventPersonLinkPersonMergeViewOutDto.getCdEventStatus());
				personDtlEvent.setIdStage(eventPersonLinkPersonMergeViewOutDto.getIdStage());
				personDtlEvent.setCdTask(eventPersonLinkPersonMergeViewOutDto.getCdTask()); //ALM defect :15347 - set cdTask to personDtlEvent
				personDtlEventDtos.add(personDtlEvent);
			}
		}
		personDetailRetrvloDto.setPersonDtlEventDto(personDtlEventDtos);
		// pOutputMsg.setCINV04SG02(liCinv04sg02Dto);
		/**
		 * This Retrieves a full row from the name person table
		 */
		// CallCCMN40D(pInputMsg,pOutputMsg);
		namePrimayEndDateInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<NamePrimayEndDateOutDto> namePrimayEndDateOutDtos = namePrimayEndDateDao
				.getFullName(namePrimayEndDateInDto);
		if (!TypeConvUtil.isNullOrEmpty(namePrimayEndDateOutDtos) && namePrimayEndDateOutDtos.size() > 0) {
			for (NamePrimayEndDateOutDto namePrimayEndDateOutDto : namePrimayEndDateOutDtos) {
				personDetailRetrvloDto.setNmNameFirst(namePrimayEndDateOutDto.getNmNameFirst());
				personDetailRetrvloDto.setNmNameLast(namePrimayEndDateOutDto.getNmNameLast());
				personDetailRetrvloDto.setNmNameMiddle(namePrimayEndDateOutDto.getNmNameMiddle());
				personDetailRetrvloDto.setCdNameSuffix(namePrimayEndDateOutDto.getCdNameSuffix());
			}
		}
		/**
		 * This Retrieves a full row from the Person Home Removal table.
		 */
		// CallCSES46D(pInputMsg,pOutputMsg);
		if (!mobileUtil.isMPSEnvironment()) {
			personHomeRemovalInDto.setIdPersHmRemoval(personDetailsReq.getIdPerson());
			personHomeRemovalInDto.setIdCase(personDetailsReq.getIdCase());
			List<PersonHomeRemovalOutDto> personHomeRemovalOutDtos = personHomeRemovalIdDao
					.getPersonHomeRemoval(personHomeRemovalInDto);
			if (!TypeConvUtil.isNullOrEmpty(personHomeRemovalOutDtos) && personHomeRemovalOutDtos.size() > 0) {
				personDetailRetrvloDto.setSysIndHomeRemovePers(ServiceConstants.STRING_IND_Y);
			} else {
				personDetailRetrvloDto.setSysIndHomeRemovePers(ServiceConstants.STRING_IND_N);
			}
		}
		/**
		 * This Retrieves a full row from the Person Referral Link table.
		 */
		// CallCSES47D(pInputMsg,pOutputMsg);//
		referralPersonLinkInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<ReferralPersonLinkOutDto> referralPersonLinkOutDtos = referralPersonLinkDao
				.getPersonReferralLink(referralPersonLinkInDto);
		if (!TypeConvUtil.isNullOrEmpty(referralPersonLinkOutDtos) && referralPersonLinkOutDtos.size() > 0) {
			personDetailRetrvloDto.setSysIndPersReferPresent(ServiceConstants.STRING_IND_Y);
		} else {
			personDetailRetrvloDto.setSysIndPersReferPresent(ServiceConstants.STRING_IND_N);
		}
		/**
		 * This Retrieves all allegations where the cinv04si id_person is equal
		 * to id_victim in the facility allegation.
		 */
		// CallCINV87D(pInputMsg,pOutputMsg);//
		allegationFacilAllegCountInDto.setIdPerson(personDetailsReq.getIdPerson());
		List<AllegationFacilAllegCountOutDto> allegationFacilAllegCountOutDtos = allegationFacilAllegCountDao
				.getAllAllegationsPID(allegationFacilAllegCountInDto);
		if (!TypeConvUtil.isNullOrEmpty(allegationFacilAllegCountOutDtos)
				&& allegationFacilAllegCountOutDtos.size() > 0) {
			if (allegationFacilAllegCountOutDtos.get(0).getFatalFacilAllegCount() > 0) {
				personDetailRetrvloDto.setIndFatalFacilAlleg(ServiceConstants.POSITIVE_ONE);
			} else {
				personDetailRetrvloDto.setIndFatalFacilAlleg(ServiceConstants.STR_ZERO_VAL);
			}
		} else {
			personDetailRetrvloDto.setIndFatalFacilAlleg(ServiceConstants.STR_ZERO_VAL);
		}
		/**
		 * This Dao will retrieve the overall disposition for the CPS
		 * Investigation.
		 */
		// CallCSES0AD(pInputMsg,pOutputMsg);
		cpsInvstDetailInDto.setIdStage(personDetailsReq.getIdStage());
		List<CpsInvstDetailOutDto> cpsInvstDetailOutDtos = cpsInvstDetailSelDisptnDao.retCPSInvest(cpsInvstDetailInDto);
		if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailOutDtos) && cpsInvstDetailOutDtos.size() > 0) {
			personDetailRetrvloDto
					.setCdCpsInvstDtlOvrllDisptn(cpsInvstDetailOutDtos.get(0).getCdCpsInvstDtlOvrllDisptn());
		}
		if (!mobileUtil.isMPSEnvironment()) {
			initChildDthNarrViewCountInDto.setIdEvent(personDetailsReq.getIdPerson());
			initChildDthNarrViewCountInDto.setSysTxtTablename(ServiceConstants.INITIAL_DEATH_TABLE);
			List<String> tempInd = new ArrayList<>();
			List<InitChildDthNarrViewCountOutDto> initChildDthNarrViewCountOutDtos = initChildDthNarrViewCountDao
					.getEventRelatedRecords(initChildDthNarrViewCountInDto);
			if (!TypeConvUtil.isNullOrEmpty(initChildDthNarrViewCountOutDtos)
					&& initChildDthNarrViewCountOutDtos.size() > 0) {
				if (initChildDthNarrViewCountOutDtos.get(0).getSysNbrUlongKey() > 0) {
					tempInd.add(ServiceConstants.STRING_IND_Y);
				} else {
					tempInd.add(ServiceConstants.STRING_IND_N);
				}
			}
			initChildDthNarrViewCountInDto.setIdEvent(personDetailsReq.getIdPerson());
			initChildDthNarrViewCountInDto.setSysTxtTablename(ServiceConstants.COMMITTEE_DEATH_TABLE);
			initChildDthNarrViewCountOutDtos = initChildDthNarrViewCountDao
					.getEventRelatedRecords(initChildDthNarrViewCountInDto);
			if (!TypeConvUtil.isNullOrEmpty(initChildDthNarrViewCountOutDtos)
					&& initChildDthNarrViewCountOutDtos.size() > 0) {
				if (initChildDthNarrViewCountOutDtos.get(0).getSysNbrUlongKey() > 0) {
					tempInd.add(ServiceConstants.STRING_IND_Y);
					// pOutputMsg.setBIndBLOBExistsInDatabase("Y");
				} else {
					tempInd.add(ServiceConstants.STRING_IND_N);
					// pOutputMsg.setBIndBLOBExistsInDatabase("N");
				}
			}
			personDetailRetrvloDto.setbIndBLOBExistsInDatabase(tempInd);
			personDetailRetrvloDto.setDtWCDDtSystemDate(new Date());
		}
		/**
		 * This determines whether a given person is in any allegations for a
		 * given stage.
		 */
		// CallCINVB5D(pInputMsg,pOutputMsg);
		allegationInDto.setIdPerson(personDetailsReq.getIdPerson());
		allegationInDto.setIdStage(personDetailsReq.getIdStage());
		List<AllegationOutDto> allegationOutDtos = allegationSelIdStageDao.havAllegationsSID(allegationInDto);
		if (!TypeConvUtil.isNullOrEmpty(allegationOutDtos) && allegationOutDtos.size() > 0) {
			personDetailRetrvloDto.setScrIndDupAlleg(ServiceConstants.STRING_IND_Y);
		} else {
			personDetailRetrvloDto.setScrIndDupAlleg(ServiceConstants.STRING_IND_N);
			//[artf251998] Defect: 156746 - Stop deletion of PRN in AR
			List<Long> intakeAllegations = allegationSelIdStageDao.intakeAllegationsSID(allegationInDto);
			if (!CollectionUtils.isEmpty(intakeAllegations)) {
				personDetailRetrvloDto.setScrIndDupAlleg(ServiceConstants.STRING_IND_Y);
			}
		}
		/**
		 * This determines whether a given person is in pl for a given stage.
		 */
		// CallPlcmtAdult(pInputMsg,pOutputMsg);//
		placementEventInDto.setIdPerson(personDetailsReq.getIdPerson());
		placementEventInDto.setIdStage(personDetailsReq.getIdStage());
		List<PlacementEventOutDto> placementEventOutDtos = placementEventDao.hasPLforStage(placementEventInDto);
		if (!TypeConvUtil.isNullOrEmpty(placementEventOutDtos) && placementEventOutDtos.size() > 0) {
			personDetailRetrvloDto.setScrIndPlcmtAdult(ServiceConstants.STRING_IND_Y);
		} else {
			personDetailRetrvloDto.setScrIndPlcmtAdult(ServiceConstants.STRING_IND_N);
		}

		log.debug("Exiting method personDetailRetrvl in PersonDetailRetrvlServiceImpl");
		PersonDetailsRes res = this.mapPersonDetail(personDetailRetrvloDto);
		//Below code is to check whether person is in NYTD youth survey or not.
		if(!mobileUtil.isMPSEnvironment() && !ObjectUtils.isEmpty(personDetailsReq.getIdStage()) && 0 < personDetailsReq.getIdStage()){
			res.setIsYouthInSurvey(personIdTypeDao.verifyYouthInNYDTSurvey(personDetailsReq.getIdStage()));
		}
		//Defect 16866: Keep the status active if the person is involved in other stages while deleting
		res.setRowQtySize(stagePersonLinkRecordDao.getStagePersonLinkCount(stagePersonLinkRecordInDto));
		return res;
	}

	/**
	 *
	 * Method Name: mapPersonDetail Method Description: mapping person detail
	 * values to response object
	 *
	 * @param personDetailRtl
	 * @return PersonDetailsRes
	 */
	protected PersonDetailsRes mapPersonDetail(PersonDetailRetrvloDto personDetailRtl) {
		PersonDetailsRes res = new PersonDetailsRes();
		PersonDto personDto = new PersonDto();
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdCategoryCategoryList())) {
			res.setCdCategoryCategory(personDetailRtl.getCdCategoryCategoryList());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndCdStagePersSearch())) {
			personDto.setCdStagePersSearchInd(personDetailRtl.getIndCdStagePersSearch());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonChar())) {
			personDto.setCdPersonChar(personDetailRtl.getCdPersonChar());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersGuardCnsrv())) {
			personDto.setCdPersGuardCnsrv(personDetailRtl.getCdPersGuardCnsrv());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndStagePersReporter())) {
			personDto.setIndStagePersReporter(personDetailRtl.getIndStagePersReporter());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIdPerson())) {
			personDto.setIdPerson(personDetailRtl.getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndActiveEvent())) {
			personDto.setIndActiveEvent(personDetailRtl.getIndActiveEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdCategoryCategory())) {
			personDto.setCdCategoryCategory(personDetailRtl.getCdCategoryCategory());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonDeath())) {
			personDto.setCdPersonDeath(personDetailRtl.getCdPersonDeath());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdNameSuffix())) {
			personDto.setCdPersonSuffix(personDetailRtl.getCdNameSuffix());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonEthnicGroup())) {
			personDto.setCdPersonEthnicGroup(personDetailRtl.getCdPersonEthnicGroup());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonIdType())) {
			personDto.setCdPersonIdType(personDetailRtl.getCdPersonIdType());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getSysIndHomeRemovePers())) {
			personDto.setSysIndHomeRemovePers(personDetailRtl.getSysIndHomeRemovePers());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getSysIndPersReferPresent())) {
			personDto.setSysIndPersReferPresent(personDetailRtl.getSysIndPersReferPresent());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonLanguage())) {
			personDto.setCdPersonLanguage(personDetailRtl.getCdPersonLanguage());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonMaritalStatus())) {
			personDto.setCdPersonMaritalStatus(personDetailRtl.getCdPersonMaritalStatus());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonRelationship())) {
			personDto.setCdPersonRelationship(personDetailRtl.getCdPersonRelationship());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonReligion())) {
			personDto.setCdPersonReligion(personDetailRtl.getCdPersonReligion());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getPersonSex())) {
			personDto.setCdPersonSex(personDetailRtl.getPersonSex());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdDisasterRlf())) {
			personDto.setCdDisasterRlf(personDetailRtl.getCdDisasterRlf());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonStatus())) {
			personDto.setCdPersonStatus(personDetailRtl.getCdPersonStatus());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonLivArr())) {
			personDto.setCdPersonLivArr(personDetailRtl.getCdPersonLivArr());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtPersonDeath())) {
			personDto.setDtPersonDeath(personDetailRtl.getDtPersonDeath());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtPersonBirth())) {
			personDto.setDtPersonBirth(personDetailRtl.getDtPersonBirth());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndPersonDobApprox())) {
			personDto.setIndPersonDobApprox(personDetailRtl.getIndPersonDobApprox());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getSysIndInStage())) {
			personDto.setSysIndInStage(personDetailRtl.getSysIndInStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIdStagePerson())) {
			personDto.setIdStagePerson(personDetailRtl.getIdStagePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNbrPersonAge())) {
			personDto.setPersonAge(personDetailRtl.getNbrPersonAge());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNmNameFirst())) {
			personDto.setNmPersonFirst(personDetailRtl.getNmNameFirst());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNmNameMiddle())) {
			personDto.setNmPersonMiddle(personDetailRtl.getNmNameMiddle());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNmNameLast())) {
			personDto.setNmPersonLast(personDetailRtl.getNmNameLast());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNmPersonFull())) {
			personDto.setNmPersonFull(personDetailRtl.getNmPersonFull());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdStagePersRelInt())) {
			personDto.setCdStagePersRelInt(personDetailRtl.getCdStagePersRelInt());
			res.setCdStagePersRelInt(personDetailRtl.getCdStagePersRelInt());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdStagePersRole())) {
			personDto.setCdStagePersRole(personDetailRtl.getCdStagePersRole());
			res.setCdStagePersRole(personDetailRtl.getCdStagePersRole());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdStagePersType())) {
			personDto.setCdStagePersType(personDetailRtl.getCdStagePersType());
			res.setCdStagePersType(personDetailRtl.getCdStagePersType());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndStagePersInLaw())) {
			personDto.setIndStagePersInLaw(personDetailRtl.getIndStagePersInLaw());
			res.setIndStagePersInLaw(personDetailRtl.getIndStagePersInLaw());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndMoreData())) {
			personDto.setIndMoreData(personDetailRtl.getIndMoreData());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtLastUpdate())) {
			personDto.setDtLastUpdate(personDetailRtl.getDtLastUpdate());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtLastUpdate2())) {
			personDto.setDtLastUpdate2(personDetailRtl.getDtLastUpdate2());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getTsLastUpdate())) {
			personDto.setTsLastUpdate(personDetailRtl.getTsLastUpdate());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtLastUpdate2())) {
			personDto.setTsSysTsLastUpdate2(personDetailRtl.getDtLastUpdate2());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getOccupation())) {
			personDto.setPersonOccupation(personDetailRtl.getOccupation());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdOccupation())) {
			personDto.setCdOccupation(personDetailRtl.getCdOccupation());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdMannerDeath())) {
			personDto.setCdMannerDeath(personDetailRtl.getCdMannerDeath());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdDeathRsnCps())) {
			personDto.setCdDeathRsnCps(personDetailRtl.getCdDeathRsnCps());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdDeathCause())) {
			personDto.setCdDeathCause(personDetailRtl.getCdDeathCause());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdDeathAutpsyRslt())) {
			personDto.setCdDeathAutpsyRslt(personDetailRtl.getCdDeathAutpsyRslt());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdDeathFinding())) {
			personDto.setCdDeathFinding(personDetailRtl.getCdDeathFinding());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getFatalityDetails())) {
			personDto.setFatalityDetails(personDetailRtl.getFatalityDetails());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getDtWCDDtSystemDate())) {
			personDto.setDtWCDDtSystemDate(personDetailRtl.getDtWCDDtSystemDate());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getScrIndDupAlleg())) {
			personDto.setScrIndDupAlleg(personDetailRtl.getScrIndDupAlleg());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getbIndBLOBExistsInDatabase())) {
			personDto.setbIndBLOBExistsInDatabase(personDetailRtl.getbIndBLOBExistsInDatabase());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndEducationPortfolio())) {
			personDto.setIndEducationPortfolio(personDetailRtl.getIndEducationPortfolio());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndCaringAdult())) {
			personDto.setIndCaringAdult(personDetailRtl.getIndCaringAdult());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getScrIndPlcmtAdult())) {
			personDto.setScrIndPlcmtAdult(personDetailRtl.getScrIndPlcmtAdult());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndFatalFacilAlleg())) {
			personDto.setIndFatalFacilAlleg(personDetailRtl.getIndFatalFacilAlleg());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdCpsInvstDtlOvrllDisptn())) {
			personDto.setCdCpsInvstDtlOvrllDisptn(personDetailRtl.getCdCpsInvstDtlOvrllDisptn());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getNbrPersonIdNumber())) {
			personDto.setPersonIdNumber(personDetailRtl.getNbrPersonIdNumber());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdSsnSource())) {
			personDto.setCdSsnSource(personDetailRtl.getCdSsnSource());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdSsnVerifMeth())) {
			personDto.setCdSsnVerifMeth(personDetailRtl.getCdSsnVerifMeth());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndNytdDesgContact())) {
			personDto.setIndNytdDesgContact(personDetailRtl.getIndNytdDesgContact());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndNytdPrimary())) {
			personDto.setIndNytdPrimary(personDetailRtl.getIndNytdPrimary());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getIndAbuseNglctDeathInCare())) {
			personDto.setIndAbuseNglctDeathInCare(personDetailRtl.getIndAbuseNglctDeathInCare());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdTribeEligible())) {
			personDto.setCdTribeEligible(personDetailRtl.getCdTribeEligible());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDto)) {
			res.setPersonDto(personDto);
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdPersonRace())) {
			res.setCdPersonRace(personDetailRtl.getCdPersonRace());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getPersonEthnicityDtoList())) {
			res.setPersonEthnicityDtoList(personDetailRtl.getPersonEthnicityDtoList());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getPersonDtlStageDto())) {
			res.setPersonDtlStageDto(personDetailRtl.getPersonDtlStageDto());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getPersonDtlEventDto())) {
			res.setPersonDtlEventDto(personDetailRtl.getPersonDtlEventDto());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdCategoryCategory())) {
			res.setCdCategoryCategory(personDetailRtl.getCdCategoryCategoryList());
		}
		if (!TypeConvUtil.isNullOrEmpty(personDetailRtl.getCdTribeEligible())) {
			res.setCdTribeEligible(personDetailRtl.getCdTribeEligible());
		}

		return res;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IncomingPersonMpsRes mpsPersonDetail(PersonDetailsReq personDetailsReq) {
		log.debug("Entering method mpsPersonDetail in PersonDetailRetrvlService");
		IncomingPersonMpsRes personMpsRes = new IncomingPersonMpsRes();
		try {
			IncomingPersonMpsDto incomingPersonMpsDto =persondetaildao.getMPSPersonDetails(personDetailsReq.getIdPerson());
			personMpsRes.setIncomingPersonMpsDto(incomingPersonMpsDto);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return personMpsRes;
	}
}
