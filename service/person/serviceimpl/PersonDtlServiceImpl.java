package us.tx.state.dfps.service.person.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import freemarker.core.ParseException;
import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonDtl;
import us.tx.state.dfps.common.domain.PersonPotentialDup;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dto.NameDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.admin.service.EmployeeService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.CatCharReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryNarrReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.DuplicatePersonsReq;
import us.tx.state.dfps.service.common.request.GetPersCharsDtlReq;
import us.tx.state.dfps.service.common.request.IsPersonReq;
import us.tx.state.dfps.service.common.request.NameHistoryDetailReq;
import us.tx.state.dfps.service.common.request.PersonCharacteristicsReq;
import us.tx.state.dfps.service.common.request.PersonCharsReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.request.PersonMergeSplitReq;
import us.tx.state.dfps.service.common.request.PotentialDupReq;
import us.tx.state.dfps.service.common.request.PrsnSrchListpInitReq;
import us.tx.state.dfps.service.common.request.SaveNameHistoryDtlReq;
import us.tx.state.dfps.service.common.request.UpdtPersPotentialDupReq;
import us.tx.state.dfps.service.common.request.UpdtPersonDtlReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.common.response.CatCharRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.DuplicatePersonsRes;
import us.tx.state.dfps.service.common.response.GetPersCharsDtlRes;
import us.tx.state.dfps.service.common.response.GetPersonCharsRes;
import us.tx.state.dfps.service.common.response.IsPersonRes;
import us.tx.state.dfps.service.common.response.NameHistoryDetailRes;
import us.tx.state.dfps.service.common.response.PersonCharacteristicsRes;
import us.tx.state.dfps.service.common.response.PersonCharsRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.response.PersonExtRes;
import us.tx.state.dfps.service.common.response.PotentialDupRes;
import us.tx.state.dfps.service.common.response.PrsnSrchListpInitRes;
import us.tx.state.dfps.service.common.response.SaveNameHistoryRes;
import us.tx.state.dfps.service.common.response.UpdtPersPotentialDupRes;
import us.tx.state.dfps.service.common.response.UpdtPersonDtlRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.extperson.dto.ExtPersonDto;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipDao;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ExtendedPersonListPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.AllegationDao;
import us.tx.state.dfps.service.person.dao.ChildPlanDao;
import us.tx.state.dfps.service.person.dao.CvsFaHomeDao;
import us.tx.state.dfps.service.person.dao.EducationDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.FinancialAcctDao;
import us.tx.state.dfps.service.person.dao.IncomeResourceDao;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PPTParticipantDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonCharDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonDtlDao;
import us.tx.state.dfps.service.person.dao.PersonHomeRemovalDao;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dao.PersonRaceEthnicityDao;
import us.tx.state.dfps.service.person.dao.PotentialDupDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.CatCharDto;
import us.tx.state.dfps.service.person.dto.ChildPlanDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.MergeSplitVldMsgDto;
import us.tx.state.dfps.service.person.dto.PCSPPersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.person.dto.PersCharsDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonDupCountDto;
import us.tx.state.dfps.service.person.dto.PersonEmailValueDto;
import us.tx.state.dfps.service.person.dto.PersonExtDto;
import us.tx.state.dfps.service.person.dto.PersonHomeRemovalDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;
import us.tx.state.dfps.service.person.dto.PersonSearchDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;
import us.tx.state.dfps.service.person.dto.SystemAccessDto;
import us.tx.state.dfps.service.person.service.MedicaidUpdateService;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.personmergesplit.dto.CaseValueDto;
import us.tx.state.dfps.service.personmergesplit.service.PersonMergeSplitService;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.web.person.bean.CvsFaHomeValueBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonDetailServiceImpl Apr 30, 2018- 5:43:05 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonDtlServiceImpl implements PersonDtlService {

	@Autowired
	PersonDao personDao;

	private static final Logger log = Logger.getLogger(PersonDtlServiceImpl.class);

	@Autowired
	PersonDtlDao personDtlDao;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	MedicaidUpdateService medicaidUpdateService;

	@Autowired
	CommonService commonService;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	NameDao nameDao;

	@Autowired
	CvsFaHomeDao cvsFaHomeDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	PersonIdDao personIdDao;

	@Autowired
	FinancialAcctDao financialAcctDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthDao;

	@Autowired
	DayCareRequestDao dayCareRequestDao;

	@Autowired
	MedicalConsenterDao mcDao;

	@Autowired
	FTRelationshipDao relDao;

	@Autowired
	WorkLoadDao workloadDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	PPTParticipantDao pptDao;

	@Autowired
	ChildPlanDao childPlanDao;

	@Autowired
	PersonHomeRemovalDao hmRmvlDao;

	@Autowired
	AllegationDao allgnDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	FceEligibilityDao fceEligDao;

	@Autowired
	CharacteristicsDao characDao;

	@Autowired
	PotentialDupDao potentialDupDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	PersonCharDao personCharDao;

	@Autowired
	FTRelationshipDao ftRelationshipDao;

	@Autowired
	PersonRaceEthnicityDao personRaceEthnicityDao;

	@Autowired
	IncomeResourceDao incomeResourceDao;

	@Autowired
	EducationDao educationDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	ExtendedPersonListPrefillData extendedPersonListPrefillData;

	@Autowired
	PersonMergeSplitService personMergeSplitService;
	
	@Autowired
	private SexualVictimizationHistoryDao sexualVictimizationHistoryDao;
	/**
	 * Method Description: This method is used to call personDao method with
	 * input as person_id or stage_id and stage pers role. Tuxedo Service Name:
	 * CCFC38S
	 * 
	 * @param personDtlReq
	 * @return PersonDtlRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public PersonDtlRes getPersonDtl(PersonDtlReq personDtlReq) {
		List<PersonDtlDto> personDtl = null;
		PersonDtlDto personDtls = new PersonDtlDto();
		if (!TypeConvUtil.isNullOrEmpty(personDtlReq.getIdStage())
				&& !TypeConvUtil.isNullOrEmpty(personDtlReq.getCdStagePersRole())) {
			personDtl = personDao.getPersonDtlByIdStage(personDtlReq.getIdStage(), personDtlReq.getCdStagePersRole());
		} else {
			personDtls = personDao.searchPersonDtlById(personDtlReq.getIdPerson());
		}
		PersonDtlRes personDtlOut = new PersonDtlRes();
		personDtlOut.setPersonDtl(personDtl);
		personDtlOut.setPersonDtlDtoList(personDtls);
		log.info("TransactionId :" + personDtlReq.getTransactionId());
		return personDtlOut;
	}

	/**
	 * Method Description: This method is used to call personDao method with
	 * input as person_id to update all column in PersonDtl table. Tuxedo
	 * Service Name: CCFC38S
	 * 
	 * @param updatePersonDto
	 * @return UpdtPersonDtlRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public UpdtPersonDtlRes updatePersonDtl(UpdtPersonDtlReq updatePersonDtlReq) {
		PersonDtl personDtl = new PersonDtl();
		UpdtPersonDtlRes updatePersonDtlRes = new UpdtPersonDtlRes();
		if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq)) {
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getUlIdPerson()))
				personDtl.setIdPerson(updatePersonDtlReq.getUlIdPerson());
			personDtl.setDtLastUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getLdAmtPersonAnnualIncome()))
				personDtl.setAmtPersonAnnualIncome(updatePersonDtlReq.getLdAmtPersonAnnualIncome());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonBirthCity()))
				personDtl.setCdPersonBirthCity(updatePersonDtlReq.getSzCdPersonBirthCity());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonBirthCountry()))
				personDtl.setCdPersonBirthCountry(updatePersonDtlReq.getSzCdPersonBirthCountry());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonBirthCounty()))
				personDtl.setCdPersonBirthCounty(updatePersonDtlReq.getSzCdPersonBirthCounty());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonBirthState()))
				personDtl.setCdPersonBirthState(updatePersonDtlReq.getSzCdPersonBirthState());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonCitizenship()))
				personDtl.setCdPersonCitizenship(updatePersonDtlReq.getSzCdPersonCitizenship());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonEyeColor()))
				personDtl.setCdPersonEyeColor(updatePersonDtlReq.getSzCdPersonEyeColor());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonFaHomeRole()))
				personDtl.setCdPersonFaHomeRole(updatePersonDtlReq.getSzCdPersonFaHomeRole());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonHairColor()))
				personDtl.setCdPersonHairColor(updatePersonDtlReq.getSzCdPersonHairColor());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzCdPersonHighestEduc()))
				personDtl.setCdPersonHighestEduc(updatePersonDtlReq.getSzCdPersonHighestEduc());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getcIndPersonNoUsBrn()))
				personDtl.setIndPersonNoUsBrn(updatePersonDtlReq.getcIndPersonNoUsBrn());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzNmPersonLastEmployer()))
				personDtl.setNmPersonLastEmployer(updatePersonDtlReq.getSzNmPersonLastEmployer());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getSzNmPersonMaidenName()))
				personDtl.setNmPersonMaidenName(updatePersonDtlReq.getSzNmPersonMaidenName());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getsQtyPersonHeightFeet()))
				personDtl.setQtyPersonHeightFeet(updatePersonDtlReq.getsQtyPersonHeightFeet());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getsQtyPersonHeightInches()))
				personDtl.setQtyPersonHeightInches(updatePersonDtlReq.getsQtyPersonHeightInches());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getlQtyPersonWeight()))
				personDtl.setQtyPersonWeight(updatePersonDtlReq.getlQtyPersonWeight());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getcCdRemovalMothrMarrd()))
				personDtl.setCdRemovalMothrMarrd(updatePersonDtlReq.getcCdRemovalMothrMarrd());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getCdEverAdopted()))
				personDtl.setCdEverAdopted(updatePersonDtlReq.getCdEverAdopted());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getDtMostRecentAdoption()))
				personDtl.setDtMostRecentAdoption(updatePersonDtlReq.getDtMostRecentAdoption());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getCdAgencyAdoption()))
				personDtl.setCdAgencyAdoption(updatePersonDtlReq.getCdAgencyAdoption());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getCdEverAdoptInternatl()))
				personDtl.setCdEverAdoptInternatl(updatePersonDtlReq.getCdEverAdoptInternatl());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getIndAdoptDateUnknown()))
				personDtl.setIndAdoptDateUnknown(updatePersonDtlReq.getIndAdoptDateUnknown());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getCdDocType()))
				personDtl.setCdDocType(updatePersonDtlReq.getCdDocType());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getDtUsEntry()))
				personDtl.setDtUsEntry(updatePersonDtlReq.getDtUsEntry());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getTxtAlienDocIdentifier()))
				personDtl.setTxtAlienDocIdentifier(updatePersonDtlReq.getTxtAlienDocIdentifier());
			if (!TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getDtAlienStatusExpiration()))
				personDtl.setDtAlienStatusExpiration(updatePersonDtlReq.getDtAlienStatusExpiration());
			personDao.updatePersonDtl(personDtl, updatePersonDtlReq.getReqFuncCd());
		}
		log.info("TransactionId :" + updatePersonDtlReq.getTransactionId());
		return updatePersonDtlRes;
	}

	/**
	 * Method Description: getCharDtls
	 * 
	 * @param GetPersCharsDtlReq
	 * @return GetPersCharsDtlRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public GetPersCharsDtlRes getCharDtls(GetPersCharsDtlReq persCharsDtlReq) {
		List<PersCharsDto> PersCharsList;
		PersCharsList = personDao.getCharDtls(persCharsDtlReq);
		GetPersCharsDtlRes getPersCharsDtlRes = new GetPersCharsDtlRes();
		getPersCharsDtlRes.setPersCharsList(PersCharsList);
		log.info("TransactionId :" + persCharsDtlReq.getTransactionId());
		return getPersCharsDtlRes;
	}

	/**
	 * Method Description: savePersPotentialDupInfo
	 * 
	 * @param UpdtPersPotentialDupReq
	 * @return UpdtPersPotentialDupRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public UpdtPersPotentialDupRes savePersPotentialDupInfo(UpdtPersPotentialDupReq updtPersPotentialDupReq) {
		PersonPotentialDup personPotentialDup = new PersonPotentialDup();
		PersonPotentialDupDto persPotentialDupDto = updtPersPotentialDupReq.getPersPotentialDupDto();
		Person personPotDup = new Person();
		personPotDup.setIdPerson(persPotentialDupDto.getIdDupPerson());
		personPotentialDup.setPersonByIdDupPerson(personPotDup);
		Person dupPrsnFound = new Person();
		dupPrsnFound.setIdPerson(persPotentialDupDto.getIdPerson());
		personPotentialDup.setPersonByIdPerson(dupPrsnFound);
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getIdPersonPotentialDup())) {
			personPotentialDup.setIdPersonPotentialDup(persPotentialDupDto.getIdPersonPotentialDup());
			updtPersPotentialDupReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		} else
			updtPersPotentialDupReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getIdStaff()))
			personPotentialDup.setIdWrkrPerson(persPotentialDupDto.getIdStaff());
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getCdReasonNotMerged()))
			personPotentialDup.setCdRsnNotMerged(persPotentialDupDto.getCdReasonNotMerged());
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getInvalid()))
			personPotentialDup.setIndInvalid(persPotentialDupDto.getInvalid());
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getMerged()))
			personPotentialDup.setIndMerged(persPotentialDupDto.getMerged());
		//artf231253 : Potential Dup created in Legacy and P2 code expect
		// end date to be GENERIC_END_DATE
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getDtEnd()))
			personPotentialDup.setDtEnd(persPotentialDupDto.getDtEnd());
		else
			personPotentialDup.setDtEnd(ServiceConstants.GENERIC_END_DATE);
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getComments()))
			personPotentialDup.setTxtComments(persPotentialDupDto.getComments());
		if (!TypeConvUtil.isNullOrEmpty(persPotentialDupDto.getDtCreated()))
			personPotentialDup.setDtCreated(persPotentialDupDto.getDtCreated());
		else
			personPotentialDup.setDtCreated(new Date());
		personPotentialDup.setDtLastUpdate(new Date());
		potentialDupDao.savePersPotentialDupInfo(personPotentialDup, updtPersPotentialDupReq.getReqFuncCd());
		UpdtPersPotentialDupRes updtPersPotentialDupRes = new UpdtPersPotentialDupRes();
		updtPersPotentialDupRes.setIdPersonPotentialDup(persPotentialDupDto.getIdPersonPotentialDup());
		return updtPersPotentialDupRes;
	}

	/**
	 * Method Description: This method is used to call PersonDtlDao method to
	 * retrieve the extended person list with ID_PERSON as input.
	 * 
	 * @param PersonDtlReq
	 * @ @return personExtDtl
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonExtRes getExtPersonList(PersonDtlReq personExtReq) {
		List<PersonExtDto> personExtDtl = null;
		PersonExtRes personExtRes = new PersonExtRes();
		personExtDtl = personDao.getPersonExtList(personExtReq);
		personExtRes.setPersonExtDto(personExtDtl);
		return personExtRes;
	}

	/**
	 * Method Description:This method will return the date of birth of the
	 * personID
	 * 
	 * @param personid
	 * @return Date in String form @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonDtlRes getDob(PersonDtlReq personDtlReq) {
		PersonDtlRes personDtlRes = new PersonDtlRes();
		personDtlRes.setPersonDob(personDtlDao.getDob(personDtlReq.getIdPerson()));
		return personDtlRes;
	}

	/**
	 * 
	 * Method Description: This Service calls a DAM to update Name information
	 ** about a person on the Name Table and a DAM to update NM PERSON FULL on
	 * the Person Table. Tuxedo Service Name:CINV25S
	 * 
	 * @param nameHistoryDetailReq
	 * @return nameHistoryDetailRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NameHistoryDetailRes getNameHistoryDtl(NameHistoryDetailReq nameHistoryDetailReq) {
		NameHistoryDetailRes nameHistoryDetailRes = new NameHistoryDetailRes();
		nameHistoryDetailReq.setSysIndIntake(
				(nameHistoryDetailReq.getSysIndIntake().equalsIgnoreCase(ServiceConstants.STRING_IND_Y))
						? (ServiceConstants.IND_TRUE) : (ServiceConstants.IND_FALSE));
		nameHistoryDetailRes.setNameHistoryDtlDto(personDao.getNameHistoryDetail(nameHistoryDetailReq));
		nameHistoryDetailRes.setDtWCDDtSystemDate(new Date());
		log.info("TransactionId :" + nameHistoryDetailReq.getTransactionId());
		return nameHistoryDetailRes;
	}

	/**
	 * 
	 * Method Description: This Service calls a DAM to update Name information
	 ** about a person on the Name Table and a DAM to update NM PERSON FULL on
	 * the Person Table. Tuxedo Service Name:CINV26S Tuxedo DAM Name :
	 * 
	 * @param updatenameHistoryDtlReq
	 * @return saveNameHistoryRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveNameHistoryRes updateNameHistoryDtl(SaveNameHistoryDtlReq updatenameHistoryDtlReq) {
		SaveNameHistoryRes saveNameHistoryRes = new SaveNameHistoryRes();
		Boolean checkEventStage = Boolean.TRUE;
		String message = "";
		if (ServiceConstants.STRING_IND_N.equalsIgnoreCase(updatenameHistoryDtlReq.getSysIndGeneric())) {
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setCdTask(updatenameHistoryDtlReq.getCdTask());
			inCheckStageEventStatusDto.setIdStage(updatenameHistoryDtlReq.getIdStage());
			inCheckStageEventStatusDto.setCdReqFunction(updatenameHistoryDtlReq.getReqFuncCd());
			checkEventStage = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		}
		Long idPerson = updatenameHistoryDtlReq.getNameHistoryDtlDto().get(0).getIdPerson();
		if (checkEventStage) {
			message = personDao.audName(updatenameHistoryDtlReq);
			if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(updatenameHistoryDtlReq.getSysIndUpdateFullName())) {
				message = personDao.audPerson(updatenameHistoryDtlReq);
				String nmCase = updatenameHistoryDtlReq.getPersonFull().get(1);
				String nmPersonFull = updatenameHistoryDtlReq.getPersonFull().get(0);
				employeeService.updateCaseName(nmCase, idPerson, nmPersonFull);
				employeeService.updateStageName(nmCase, idPerson, nmPersonFull);
				String appendNmPersonFull = commonService.appendEtAlToName(nmPersonFull);
				String appenedNmCase = commonService.appendEtAlToName(nmCase);
				employeeService.updateCaseName(appenedNmCase, idPerson, appendNmPersonFull);
				employeeService.updateStageName(appenedNmCase, idPerson, appendNmPersonFull);
			}
		}
		message = ServiceConstants.SUCCESS;
		if (ServiceConstants.STRING_IND_N
				.equalsIgnoreCase(updatenameHistoryDtlReq.getNameHistoryDtlDto().get(0).getIndNameInvalid())
				&& ServiceConstants.STRING_IND_Y
						.equalsIgnoreCase(updatenameHistoryDtlReq.getNameHistoryDtlDto().get(0).getIndNamePrimary())) {
			Long idEvent = personDao.getAdoptionSubsidyIdEvent(idPerson);
			if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
				MedicaidUpdateDto medicaidUpdateDto = new MedicaidUpdateDto();
				if (null != updatenameHistoryDtlReq.getIdStage()) {
					medicaidUpdateDto.setIdMedUpdStage(updatenameHistoryDtlReq.getIdStage());
				}
				if (null != idPerson) {
					medicaidUpdateDto.setIdMedUpdPerson(idPerson);
				}
				if (null != idEvent) {
					medicaidUpdateDto.setIdMedUpdRecord(idEvent);
				}
				medicaidUpdateDto.setCdMedUpdType(ServiceConstants.ADOP_SUB_TYPE);
				medicaidUpdateDto.setCdMedUpdTransType(ServiceConstants.SUSTAIN);
				medicaidUpdateService.editMedicaidUpdate(medicaidUpdateDto, updatenameHistoryDtlReq.getReqFuncCd());
			}
		}
		message = ServiceConstants.SUCCESS;
		saveNameHistoryRes.setMessage(message);
		log.info("TransactionId :" + updatenameHistoryDtlReq.getTransactionId());
		return saveNameHistoryRes;
	}

	/**
	 * 
	 * Method Description:This service is responsible for adding or updating
	 * information from the Person Characteristics window. Tuxedo Service
	 * Name:CINV34S
	 * 
	 * @param personCharReq
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonCharacteristicsRes savePersonChar(PersonCharacteristicsReq personCharReq) {
		PersonCharacteristicsRes personCharRes = new PersonCharacteristicsRes();
		Boolean checkEventStage = Boolean.FALSE;
		String message = ServiceConstants.FAIL;
		if (ServiceConstants.STRING_IND_N.equalsIgnoreCase(personCharReq.getIndGeneric())) {
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setCdTask(personCharReq.getCdTask());
			inCheckStageEventStatusDto.setIdStage(personCharReq.getIdStage());
			inCheckStageEventStatusDto.setCdReqFunction(personCharReq.getReqFuncCd());
			checkEventStage = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		}
		if (checkEventStage) {
			if (personCharReq.getListRowsQty() > 0) {
				for (CharacteristicsDto r : personCharReq.getPersonCharacteristicsDto()) {
					r.setIdPerson(personCharReq.getIdPerson());
					List<Long> idChar = Arrays.asList(r.getIdCharacteristics());
					closeOpenStageService.characteristicsAUD(r, personCharReq.getNbrPersonAge(), r.getCdScrDataAction(),
							idChar);
					message = ServiceConstants.SUCCESS;
				}
			} else {
				personCharRes.setMessage(message);
			}
			personDao.saveAfcarsDatesAndStatus(personCharReq);
			message = ServiceConstants.SUCCESS;
			PersonDto personDto = new PersonDto();
			personDto.setDtLastUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonChar())) {
				personDto.setCdPersonChar(personCharReq.getCdPersonChar());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonDeath())) {
				personDto.setCdPersonDeath(personCharReq.getCdPersonDeath());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonEthnicGroup())) {
				personDto.setCdPersonEthnicGroup(personCharReq.getCdPersonEthnicGroup());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonLanguage())) {
				personDto.setCdPersonLanguage(personCharReq.getCdPersonLanguage());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonLivArr())) {
				personDto.setCdPersonLivArr(personCharReq.getCdPersonLivArr());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonMaritalStatus())) {
				personDto.setCdPersonMaritalStatus(personCharReq.getCdPersonMaritalStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonReligion())) {
				personDto.setCdPersonReligion(personCharReq.getCdPersonReligion());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonSex())) {
				personDto.setCdPersonSex(personCharReq.getCdPersonSex());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdPersonStatus())) {
				personDto.setCdPersonStatus(personCharReq.getCdPersonStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getIndPersonDobApprox())) {
				personDto.setIndPersonDobApprox(personCharReq.getIndPersonDobApprox());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getiIndEducationPortfolio())) {
				personDto.setIndEducationPortfolio(personCharReq.getiIndEducationPortfolio());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getTxtOccupation())) {
				personDto.setPersonOccupation(personCharReq.getTxtOccupation());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getNmPersonFull())) {
				personDto.setNmPersonFull(personCharReq.getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getDtPersonBirth())) {
				personDto.setDtPersonBirth(personCharReq.getDtPersonBirth());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getDtPersonDeath())) {
				personDto.setDtPersonDeath(personCharReq.getDtPersonDeath());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getIdPerson())) {
				personDto.setIdPerson(personCharReq.getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getNbrPersonAge())) {
				personDto.setPersonAge(personCharReq.getNbrPersonAge());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdMannerDeath())) {
				personDto.setCdMannerDeath(personCharReq.getCdMannerDeath());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdDeathRsnCps())) {
				personDto.setCdDeathRsnCps(personCharReq.getCdDeathRsnCps());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdDeathCause())) {
				personDto.setCdDeathCause(personCharReq.getCdDeathCause());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdDeathAutpsyRslt())) {
				personDto.setCdDeathAutpsyRslt(personCharReq.getCdDeathAutpsyRslt());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getCdDeathFinding())) {
				personDto.setCdDeathFinding(personCharReq.getCdDeathFinding());
			}
			if (!TypeConvUtil.isNullOrEmpty(personCharReq.getTxtFatalityDetails())) {
				personDto.setFatalityDetails(personCharReq.getTxtFatalityDetails());
			}
			closeOpenStageService.investigationPersonDtlAUD(personDto, null, null, null, personCharReq.getReqFuncCd());
			message = ServiceConstants.SUCCESS;
		}
		personCharRes.setMessage(message);
		return personCharRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal history complete window. Service Name : Criminal History Person
	 * Helper
	 * 
	 * @param phonereq
	 * @return PhoneRes @,
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean isCriminalHistoryComplete(CriminalHistoryReq crimHistoryReq) {
		boolean criminalHistory;
		criminalHistory = personDtlDao.isCriminalHistoryComplete(crimHistoryReq);
		log.info("TransactionId :" + crimHistoryReq.getTransactionId());
		return criminalHistory;
	}

	/**
	 * 
	 * Method Description: updateRecordCheckIndAccptRej
	 * 
	 * @param CriminalHistoryReq
	 * @return String
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String updateRecordCheckIndAccptRej(CriminalHistoryReq crimHistoryReq) {
		String message = "";
		message = personDtlDao.updateRecordCheckIndAccptRej(crimHistoryReq);
		log.info("TransactionId :" + crimHistoryReq.getTransactionId());
		return message;
	}

	/**
	 * 
	 * Method Description: isCrimHistNarrPresentForRecordCheck
	 * 
	 * @param CriminalHistoryReq
	 * @return boolean
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean isCrimHistNarrPresentForRecordCheck(CriminalHistoryReq crimHistoryReq) {
		boolean criminalHistory;
		criminalHistory = personDtlDao.isCrimHistNarrPresentForRecordCheck(crimHistoryReq);
		log.info("TransactionId :" + crimHistoryReq.getTransactionId());
		return criminalHistory;
	}

	/**
	 * 
	 * Method Description: getCriminalHistNarr
	 * 
	 * @param CriminalHistoryNarrReq
	 * @return boolean
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean getCriminalHistNarr(CriminalHistoryNarrReq criminalHistoryNarrReq) {
		boolean crimHistNarrPresent = false;
		crimHistNarrPresent = 0 != personDtlDao.getCriminalHistNarr(criminalHistoryNarrReq) ? true : false;
		return crimHistNarrPresent;
	}

	/**
	 * 
	 * Method Description: getPersonFullName
	 * 
	 * @param PersonDtlReq
	 * @return String
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public String getPersonFullName(PersonDtlReq personDtlReq) {
		String nmPersonFull = "";
		Person personFullName = personDao.getPersonByPersonId(personDtlReq.getIdPerson());
		nmPersonFull = !StringUtils.isEmpty(personFullName.getCdPersonSuffix())
				? personFullName.getNmPersonFull() + " " + personFullName.getCdPersonSuffix()
				: personFullName.getNmPersonFull();
		return nmPersonFull;
	}
	
	/**
	 * 
	 * Method Description: getPersonFullName
	 * Method written for defect 6503 artf81655
	 * this method will return full name based on person id 
	 * @param PersonDtlReq
	 * @return String
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public String getPersonFullName(Long idPerson) {
		String nmPersonFull = "";
		Person personFullName = personDao.getPersonByPersonId(idPerson);
		nmPersonFull = !StringUtils.isEmpty(personFullName.getCdPersonSuffix())
				? personFullName.getNmPersonFull() + " " + personFullName.getCdPersonSuffix()
				: personFullName.getNmPersonFull();
		return nmPersonFull;
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.PersonDtlService#
	 *      hasCurrentPrimaryAddress(us.tx.state.dfps.service.common.request.
	 *      AddressDtlReq)
	 */
	@Override
	public AddressDtlRes hasCurrentPrimaryAddress(AddressDtlReq addressReq) {
		return personDtlDao.hasCurrentPrimaryAddress(addressReq);
	}

	/**
	 * Method Description:: This method retrieves a list of CriminalHistory ID's
	 * that have REJ Action status, for a give Person ID.
	 * 
	 * @param criminalHistoryNarrReq
	 * @return List<Long>
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<Long> getRejectCriminalHistList(CriminalHistoryNarrReq criminalHistoryNarrReq) {
		return personDtlDao.getRejectCriminalHistList(criminalHistoryNarrReq.getIdRecCheckPerson());
	}

	/**
	 * Method Description:: validatePersonMerge
	 * 
	 * @param PersonMergeSplitDto
	 * @return PersonMergeSplitDto
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PersonMergeSplitDto validatePersonMerge(PersonMergeSplitDto personMergeSplitDto) {
		Person forwardPerson = personDao.getPerson(personMergeSplitDto.getIdForwardPerson());
		Person closedPerson = personDao.getPerson(personMergeSplitDto.getIdClosedPerson());
		if (forwardPerson == null || closedPerson == null)
			return personMergeSplitDto;
		personMergeSplitDto.setNmForwardPerson(!StringUtils.isEmpty(forwardPerson.getCdPersonSuffix())
				? forwardPerson.getNmPersonFull() + " " + forwardPerson.getCdPersonSuffix()
				: forwardPerson.getNmPersonFull());
		personMergeSplitDto.setNmClosedPerson(!StringUtils.isEmpty(closedPerson.getCdPersonSuffix())
				? closedPerson.getNmPersonFull() + " " + closedPerson.getCdPersonSuffix()
				:closedPerson.getNmPersonFull());
		// check if closed person is in IR report
		if (personDao.isPersonIRReport(personMergeSplitDto.getIdClosedPerson())) {
			MergeSplitVldMsgDto closedIRValidationMsgDto = new MergeSplitVldMsgDto();
			closedIRValidationMsgDto.setMessageInt(ServiceConstants.MSG_IR_MERGE_CLOSED_PERSON);
			closedIRValidationMsgDto.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			closedIRValidationMsgDto.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(closedIRValidationMsgDto);
		}
		// check if forward person is in IR report
		if (personDao.isPersonIRReport(personMergeSplitDto.getIdForwardPerson())) {
			MergeSplitVldMsgDto forwardIRValidationMsgDto = new MergeSplitVldMsgDto();
			forwardIRValidationMsgDto.setMessageInt(ServiceConstants.MSG_IR_MERGE_CLOSED_PERSON);
			forwardIRValidationMsgDto.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			forwardIRValidationMsgDto.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(forwardIRValidationMsgDto);
		}
		Name forwardName = nameDao.getActivePrimaryName(personMergeSplitDto.getIdForwardPerson());
		// Fetch the primary name for the forward person
		if (forwardName == null) {
			MergeSplitVldMsgDto nameValidationMsgDto = new MergeSplitVldMsgDto();
			nameValidationMsgDto.setMessageInt(ServiceConstants.MSG_NO_MERGE_UNKNOWN_NAME);
			nameValidationMsgDto.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			nameValidationMsgDto.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(nameValidationMsgDto);
		}
		// If greater than 10 years or more, then show error message
		if (forwardPerson.getDtPersonBirth() != null && closedPerson.getDtPersonBirth() != null) {
			int dobDiffMonths = 0;
			if (forwardPerson.getDtPersonBirth().after(closedPerson.getDtPersonBirth()))
				dobDiffMonths = DateUtils.calculatePersonAgeInMonths(closedPerson.getDtPersonBirth(),
						forwardPerson.getDtPersonBirth());
			else
				dobDiffMonths = DateUtils.calculatePersonAgeInMonths(forwardPerson.getDtPersonBirth(),
						closedPerson.getDtPersonBirth());
			if (dobDiffMonths / 12.0 >= 10.0) {
				MergeSplitVldMsgDto dobValidationMsgBean = new MergeSplitVldMsgDto();
				dobValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_AGE);
				dobValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
				dobValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
				personMergeSplitDto.getValidationDataList().add(dobValidationMsgBean);
			} else if (((int) DateUtils.daysDifference(forwardPerson.getDtPersonBirth(),
					closedPerson.getDtPersonBirth())) != 0) {
				// If Dates of Birth for both Person Merge candidates exist but
				// do not match.
				// Display a warning message that the Person Merge candidates
				// each have
				// a Date of Birth yet they do not match
				MergeSplitVldMsgDto dobValidationMsgBean = new MergeSplitVldMsgDto();
				dobValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_DOB);
				dobValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
				dobValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
				personMergeSplitDto.getValidationDataList().add(dobValidationMsgBean);
			}
		}
		// a Date of Death yet they do not match
		if (forwardPerson.getDtPersonDeath() != null && closedPerson.getDtPersonDeath() != null && (int) (DateUtils
				.daysDifference(forwardPerson.getDtPersonDeath(), closedPerson.getDtPersonDeath())) != 0) {
			MergeSplitVldMsgDto dodValidationMsgBean = new MergeSplitVldMsgDto();
			dodValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_DOD);
			dodValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			dodValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(dodValidationMsgBean);
		}
		// a Gender yet they do not match
		if (forwardPerson.getCdPersonSex() != null && closedPerson.getCdPersonSex() != null
				&& !TypeConvUtil.checkForEquality(forwardPerson.getCdPersonSex(), closedPerson.getCdPersonSex())) {
			MergeSplitVldMsgDto genderValidationMsgBean = new MergeSplitVldMsgDto();
			genderValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_SEX);
			genderValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			genderValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(genderValidationMsgBean);
		}
		// Fetch the person_dtl record for both forward and closed persons
		CvsFaHomeValueBean fwdPersDtlValueBean = cvsFaHomeDao
				.displayCvsFaHome(personMergeSplitDto.getIdForwardPerson());
		CvsFaHomeValueBean closedPersDtlValueBean = cvsFaHomeDao
				.displayCvsFaHome(personMergeSplitDto.getIdClosedPerson());
		// a Citizenship Status yet they do not match.
		if (fwdPersDtlValueBean != null && closedPersDtlValueBean != null
				&& fwdPersDtlValueBean.getCdPersonBirthCitizenship() != null
				&& closedPersDtlValueBean.getCdPersonBirthCitizenship() != null
				&& !TypeConvUtil.checkForEquality(fwdPersDtlValueBean.getCdPersonBirthCitizenship(),
						closedPersDtlValueBean.getCdPersonBirthCitizenship())) {
			MergeSplitVldMsgDto citizenshipValidationMsgBean = new MergeSplitVldMsgDto();
			citizenshipValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_CITIZEN);
			citizenshipValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			citizenshipValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(citizenshipValidationMsgBean);
		}
		AddressDto forwardAddressDto = personAddressDao
				.fetchCurrentPrimaryAddress(personMergeSplitDto.getIdForwardPerson());
		AddressDto closedAddressDto = personAddressDao
				.fetchCurrentPrimaryAddress(personMergeSplitDto.getIdClosedPerson());
		// that both candidates have a Primary Address, but they do not match
		if (CodesConstant.CPERSTAT_A.equals(forwardPerson.getCdPersonStatus())
				&& CodesConstant.CPERSTAT_A.equals(closedPerson.getCdPersonStatus()) && forwardAddressDto != null
				&& closedAddressDto != null) {
			if (!TypeConvUtil.checkForEquality(forwardAddressDto.getAddrPersAddrStLn1(),
					closedAddressDto.getAddrPersAddrStLn1())
					|| !TypeConvUtil.checkForEquality(forwardAddressDto.getAddrPersAddrStLn2(),
							closedAddressDto.getAddrPersAddrStLn2())
					|| !TypeConvUtil.checkForEquality(forwardAddressDto.getAddrCity(), closedAddressDto.getAddrCity())
					|| !TypeConvUtil.checkForEquality(forwardAddressDto.getCdAddrCounty(),
							closedAddressDto.getCdAddrCounty())
					|| !TypeConvUtil.checkForEquality(forwardAddressDto.getCdAddrState(),
							closedAddressDto.getCdAddrState())
					|| !TypeConvUtil.checkForEquality(forwardAddressDto.getAddrZip(), closedAddressDto.getAddrZip())) {
				MergeSplitVldMsgDto addrValidationMsgBean = new MergeSplitVldMsgDto();
				addrValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_ADDRESS);
				addrValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
				addrValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
				personMergeSplitDto.getValidationDataList().add(addrValidationMsgBean);
			}
		}
		// Fetch the list of active Identifiers for the person forward and
		// person closed
		List<PersonIdDto> fwdPersIdentifierArr = personIdDao
				.getPersonIdentifiersList(personMergeSplitDto.getIdForwardPerson());
		List<PersonIdDto> closedPersIdentifierArr = personIdDao
				.getPersonIdentifiersList(personMergeSplitDto.getIdClosedPerson());
		PersonIdDto fwdSSN = null;
		PersonIdDto fwdTDHS = null;
		PersonIdDto fwdMedicaid = null;
		PersonIdDto closedSSN = null;
		PersonIdDto closedTDHS = null;
		PersonIdDto closedMedicaid = null;
		for (int i = 0; i < fwdPersIdentifierArr.size(); i++) {
			PersonIdDto identBean = (PersonIdDto) fwdPersIdentifierArr.get(i);
			if (CodesConstant.CNUMTYPE_SSN.equals(identBean.getCdPersonIdType()))
				fwdSSN = identBean;
			else if (CodesConstant.CNUMTYPE_TDHS_CLIENT_NUMBER.equals(identBean.getCdPersonIdType()))
				fwdTDHS = identBean;
			else if (CodesConstant.CNUMTYPE_MEDICAID_NUMBER.equals(identBean.getCdPersonIdType()))
				fwdMedicaid = identBean;
		}
		for (int i = 0; i < closedPersIdentifierArr.size(); i++) {
			PersonIdDto identBean = (PersonIdDto) closedPersIdentifierArr.get(i);
			if (CodesConstant.CNUMTYPE_SSN.equals(identBean.getCdPersonIdType()))
				closedSSN = identBean;
			else if (CodesConstant.CNUMTYPE_TDHS_CLIENT_NUMBER.equals(identBean.getCdPersonIdType()))
				closedTDHS = identBean;
			else if (CodesConstant.CNUMTYPE_MEDICAID_NUMBER.equals(identBean.getCdPersonIdType()))
				closedMedicaid = identBean;
		}
		// warning
		if (fwdSSN != null && closedSSN != null && fwdSSN.getPersonIdNumber() != null && closedSSN.getPersonIdNumber() != null && !fwdSSN.getPersonIdNumber().equals(closedSSN.getPersonIdNumber())) {
			MergeSplitVldMsgDto identValidationMsgBean = new MergeSplitVldMsgDto();
			identValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_SSN);
			identValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			identValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(identValidationMsgBean);
		}
		// a warning
		if (fwdTDHS != null && closedTDHS != null && fwdTDHS.getPersonIdNumber() != null && closedTDHS.getPersonIdNumber() != null
				&& !fwdTDHS.getPersonIdNumber().equals(closedTDHS.getPersonIdNumber())) {
			MergeSplitVldMsgDto identValidationMsgBean = new MergeSplitVldMsgDto();
			identValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_TDHS_NUMBER);
			identValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			identValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(identValidationMsgBean);
		}
		// raise a warning
		if (fwdMedicaid != null && closedMedicaid != null && fwdMedicaid.getPersonIdNumber() != null && closedMedicaid.getPersonIdNumber() != null 
				&& !fwdMedicaid.getPersonIdNumber().equals(closedMedicaid.getPersonIdNumber())) {
			MergeSplitVldMsgDto identValidationMsgBean = new MergeSplitVldMsgDto();
			identValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_MEDICAID);
			identValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			identValidationMsgBean.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(identValidationMsgBean);
		}
		// Fetch the list of Active financial accounts for person forward and
		// person closed.
		ArrayList fwdPersFinAcctArr = (ArrayList) financialAcctDao.fetchChildAccountList(
				personMergeSplitDto.getIdForwardPerson(), CodesConstant.CACTTYPE_CH, CodesConstant.CACTSTAT_A);
		ArrayList closedPersFinAcctArr = (ArrayList) financialAcctDao.fetchChildAccountList(
				personMergeSplitDto.getIdClosedPerson(), CodesConstant.CACTTYPE_CH, CodesConstant.CACTSTAT_A);
		// Check if the accounts of closed person have any transactions which
		// are linked to invoices
		boolean bInvoiceLinkedTrx = financialAcctDao.checkLinkedInvoicesExist(personMergeSplitDto.getIdClosedPerson());
		// invoice
		if (bInvoiceLinkedTrx) {
			MergeSplitVldMsgDto finAcctValidationMsgBean = new MergeSplitVldMsgDto();
			finAcctValidationMsgBean.setMessageInt(ServiceConstants.MSG_FIN_LINKED_TRAN);
			finAcctValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			finAcctValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(finAcctValidationMsgBean);
		}
		// Checking Financial Account.
		if (fwdPersFinAcctArr.size() > 0 && closedPersFinAcctArr.size() > 0) {
			MergeSplitVldMsgDto finAcctValidationMsgBean = new MergeSplitVldMsgDto();
			finAcctValidationMsgBean.setMessageInt(ServiceConstants.MSG_FIN_ACCT_ACTIVE);
			finAcctValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			finAcctValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(finAcctValidationMsgBean);
		}
		// Check if the Person Closed and Person Forward are both listed in a
		// Service Authorization (SERVICE_AUTHORIZATION) for the same -
		// Resource, Service, With dates that overlap,
		ArrayList<SVCAuthDetailDto> svcAuthArr = serviceAuthDao
				.getServiceAuthDtlListForPerson(personMergeSplitDto.getIdClosedPerson());
		ArrayList<ServiceAuthDto> ovrLapSvcAuthArr = new ArrayList<ServiceAuthDto>();
		for (int i = 0; i < svcAuthArr.size(); i++) {
			SVCAuthDetailDto svcAuthBean = (SVCAuthDetailDto) svcAuthArr.get(i);
			ArrayList<ServiceAuthDto> tmp = serviceAuthDao.getOverlappingSvcAuthDtlListForPerson(svcAuthBean,
					personMergeSplitDto.getIdForwardPerson());
			ovrLapSvcAuthArr.addAll(tmp);
		}
		if (ovrLapSvcAuthArr.size() > 0) {
			MergeSplitVldMsgDto svcAuthValidationMsgBean = new MergeSplitVldMsgDto();
			svcAuthValidationMsgBean.setMessageInt(ServiceConstants.MSG_MERGE_SERV_AUTH);
			svcAuthValidationMsgBean.setValidationDataList(ovrLapSvcAuthArr);
			svcAuthValidationMsgBean.setMsgCategory(ServiceConstants.STAGE);
			svcAuthValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(svcAuthValidationMsgBean);
		}
		// This applies even if the DC request is invalid.
		boolean personsAreInSameDcRequest = dayCareRequestDao.arePersonsInSameDCRequest(
				personMergeSplitDto.getIdClosedPerson(), personMergeSplitDto.getIdForwardPerson());
		if (personsAreInSameDcRequest) {
			MergeSplitVldMsgDto sameDaycareRequestValMsgBean = new MergeSplitVldMsgDto();
			sameDaycareRequestValMsgBean.setMessageInt(ServiceConstants.MSG_SAME_DC_REQUEST);
			sameDaycareRequestValMsgBean.setMsgCategory(ServiceConstants.STAGE);
			sameDaycareRequestValMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameDaycareRequestValMsgBean);
		}
		// SIR 1006837: Set error if the two IDs have valid Daycare Requests
		// with overlapping dates.
		DayCareRequestDto person1Bean = (DayCareRequestDto) dayCareRequestDao
				.listDcRequestDatesForPerson(personMergeSplitDto.getIdClosedPerson());
		DayCareRequestDto person2Bean = (DayCareRequestDto) dayCareRequestDao
				.listDcRequestDatesForPerson(personMergeSplitDto.getIdForwardPerson());
		ArrayList<DayCarePersonDto> person1List = (ArrayList) person1Bean.getDayCarePersonDtoList();
		ArrayList<DayCarePersonDto> person2List = (ArrayList) person2Bean.getDayCarePersonDtoList();
		if (0 < person1List.size() && 0 < person2List.size()) {
			Iterator<DayCarePersonDto> i = person1List.iterator();
			outerLoop: while (i.hasNext()) {
				DayCarePersonDto row = (DayCarePersonDto) i.next();
				for (int j = 0; j < person2List.size(); j++) {
					DayCarePersonDto innerRow = (DayCarePersonDto) person2List.get(j);
					if ((innerRow.getDtEnd().after(row.getDtBegin()) || innerRow.getDtEnd().equals(row.getDtBegin()))
							&& (innerRow.getDtBegin().before(row.getDtEnd())
									|| innerRow.getDtBegin().equals(row.getDtEnd()))) {
						MergeSplitVldMsgDto dcOverlapValMsgBean = new MergeSplitVldMsgDto();
						dcOverlapValMsgBean.setMessageInt(ServiceConstants.MSG_OVERLAP_DC_REQUEST);
						dcOverlapValMsgBean.setMsgCategory(ServiceConstants.STAGE);
						dcOverlapValMsgBean.setMsgSeverity(ServiceConstants.ERROR);
						personMergeSplitDto.getValidationDataList().add(dcOverlapValMsgBean);
						break outerLoop;
					}
				}
			}
		}
		// Fetch list of records where closed person is an active medical
		// consenter
		List<MedicalConsenterDto> medConList = mcDao
				.fetchActiveMedConsRecForPerson(personMergeSplitDto.getIdClosedPerson());
		if (medConList.size() > 0) {
			// if the closed person exists as medical consenter in some stages
			// setup a validation error alongwith needed data
			MergeSplitVldMsgDto medConValidationBean = new MergeSplitVldMsgDto();
			medConValidationBean.setMessageInt(ServiceConstants.MSG_MERGE_MED_CONSENTER_CLOSE);
			medConValidationBean.setMsgSeverity(ServiceConstants.ERROR);
			medConValidationBean.setValidationDataList(medConList);
			medConValidationBean.setMsgCategory(ServiceConstants.STAGE);
			personMergeSplitDto.getValidationDataList().add(medConValidationBean);
		}
		// Check if the Person Closed is in a NYTD Population,
		// (NYTD_POPULATION_LIST.ID_PERSON = Person Closed)
		// If so, Check if they are under 25 years old
		// (DateDiff(PERSON.DT_PERSON_BIRTH, System Date) < 25 years)
		boolean isChildInNYTDPopulation = personDao.isYouthInNytdPopulation(personMergeSplitDto.getIdClosedPerson());
		if (isChildInNYTDPopulation && (closedPerson.getDtPersonBirth() != null)
				&& DateUtils.getAge(closedPerson.getDtPersonBirth()) < 25) {
			// setup a validation error alongwith needed data
			MergeSplitVldMsgDto nytdValidationBean = new MergeSplitVldMsgDto();
			nytdValidationBean.setMessageInt(ServiceConstants.MSG_MERGE_NYTD);
			nytdValidationBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			nytdValidationBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(nytdValidationBean);
		}
		// Check whether the Person Closed and Person Forward have an
		// active Family Tree Relationship between themselves
		ArrayList<FTPersonRelationBean> famRelList = relDao.selectRelationshipsWith2Persons(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (famRelList.size() > 0) {
			// if the closed person exists as medical consenter in some stages
			// setup a validation error alongwith needed data
			MergeSplitVldMsgDto famRelValidationBean = new MergeSplitVldMsgDto();
			famRelValidationBean.setMessageInt(ServiceConstants.MSG_MERGE_FAMILY_TREE);
			famRelValidationBean.setValidationDataList(famRelList);
			famRelValidationBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			famRelValidationBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(famRelValidationBean);
		}
		// Check that person closed is not an Employee or Former Employee
		ArrayList<PersonCategoryDto> personCatgList = personDao
				.getPersonCategoryList(personMergeSplitDto.getIdClosedPerson());
		boolean bEmpOrFEM = false;
		for (int i = 0; i < personCatgList.size(); i++) {
			if (CodesConstant.CPSNDTCT_EMP.equals(((PersonCategoryDto) personCatgList.get(i)).getCdPersonCategory())
					|| CodesConstant.CPSNDTCT_FEM
							.equals(((PersonCategoryDto) personCatgList.get(i)).getCdPersonCategory())) {
				bEmpOrFEM = true;
				break;
			}
		}
		if (bEmpOrFEM) {
			MergeSplitVldMsgDto empFemValidationBean = new MergeSplitVldMsgDto();
			empFemValidationBean.setMessageInt(ServiceConstants.MSG_MERGE_EMP);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Closed");
			empFemValidationBean.setMessageDataList(embedData);
			empFemValidationBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			empFemValidationBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(empFemValidationBean);
		}
		// Check if the forward person is in a stage currently checked out to
		// MPS
		ArrayList<StagePersonValueDto> checkedOutStagesFwdPer = workloadDao
				.getCheckedOutStagesForPerson(personMergeSplitDto.getIdForwardPerson());
		if (checkedOutStagesFwdPer.size() > 0) {
			MergeSplitVldMsgDto checkoutValidationMsgBean = new MergeSplitVldMsgDto();
			checkoutValidationMsgBean.setMessageInt(ServiceConstants.MSG_MPS_PERSON_MERGE);
			checkoutValidationMsgBean.setValidationDataList(checkedOutStagesFwdPer);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Forward");
			checkoutValidationMsgBean.setMessageDataList(embedData);
			checkoutValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			checkoutValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(checkoutValidationMsgBean);
		}
		// Check if the Closed person is in a stage currently checked out to MPS
		ArrayList<StagePersonValueDto> checkedOutStagesClosedPer = workloadDao
				.getCheckedOutStagesForPerson(personMergeSplitDto.getIdClosedPerson());
		if (checkedOutStagesClosedPer.size() > 0) {
			MergeSplitVldMsgDto checkoutValidationMsgBean = new MergeSplitVldMsgDto();
			checkoutValidationMsgBean.setMessageInt(ServiceConstants.MSG_MPS_PERSON_MERGE);
			checkoutValidationMsgBean.setValidationDataList(checkedOutStagesClosedPer);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Closed");
			checkoutValidationMsgBean.setMessageDataList(embedData);
			checkoutValidationMsgBean.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
			checkoutValidationMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(checkoutValidationMsgBean);
		}
		// Check if the person closed is in an open stage, and if so, we need to
		// display a warning message that the
		// Person Closed is in open Stage(s) and extreme caution is advised
		ArrayList<StagePersonValueDto> openStagesClosedPer = workloadDao
				.getStagesForPerson(personMergeSplitDto.getIdClosedPerson());
		if (openStagesClosedPer.size() > 0) {
			MergeSplitVldMsgDto opnStgValidationVB = new MergeSplitVldMsgDto();
			opnStgValidationVB.setMessageInt(ServiceConstants.MSG_PERS_CLD_OPN_STAGE);
			opnStgValidationVB.setValidationDataList(openStagesClosedPer);
			opnStgValidationVB.setMsgCategory(ServiceConstants.STAGE);
			opnStgValidationVB.setMsgSeverity(ServiceConstants.WARNING);
			personMergeSplitDto.getValidationDataList().add(opnStgValidationVB);
		}
		// Intake Stage.
		if (openStagesClosedPer.size() > 0) {
			ArrayList<StagePersonValueDto> intArr = new ArrayList();
			for (int i = 0; i < openStagesClosedPer.size(); i++) {
				if (CodesConstant.CSTAGES_INT.equals(((StagePersonValueDto) openStagesClosedPer.get(i)).getCdStage())) {
					intArr.add(openStagesClosedPer.get(i));
				}
			}
			if (intArr.size() > 0) {
				MergeSplitVldMsgDto opnStgValidationVB = new MergeSplitVldMsgDto();
				opnStgValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_INTAKE);
				opnStgValidationVB.setValidationDataList(intArr);
				opnStgValidationVB.setMsgCategory(ServiceConstants.STAGE);
				opnStgValidationVB.setMsgSeverity(ServiceConstants.ERROR);
				personMergeSplitDto.getValidationDataList().add(opnStgValidationVB);
			}
		}
		// Child in an open Stage.
		if (openStagesClosedPer.size() > 0) {
			ArrayList<StagePersonValueDto> pcArr = new ArrayList<StagePersonValueDto>();
			for (int i = 0; i < openStagesClosedPer.size(); i++) {
				if (CodesConstant.CROLES_PC
						.equals(((StagePersonValueDto) openStagesClosedPer.get(i)).getCdStagePersRole())) {
					pcArr.add(openStagesClosedPer.get(i));
				}
			}
			if (pcArr.size() > 0) {
				MergeSplitVldMsgDto opnStgValidationVB = new MergeSplitVldMsgDto();
				opnStgValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_PC);
				opnStgValidationVB.setValidationDataList(pcArr);
				opnStgValidationVB.setMsgCategory(ServiceConstants.STAGE);
				opnStgValidationVB.setMsgSeverity(ServiceConstants.ERROR);
				personMergeSplitDto.getValidationDataList().add(opnStgValidationVB);
			}
		}
		// Check if the Person Closed and Person Forward are both listed in the
		// same Event
		// for the same Open Stage (EVENT_PERSON_LINK.ID_PERSON), and if so,
		// Display an error message that the Person Closed and Person Forward
		// cannot be linked to the same Event within the same open Stage.
		ArrayList personEventArr = eventDao.fetchOpenStageEventsWithPersons(personMergeSplitDto.getIdForwardPerson(),
				personMergeSplitDto.getIdClosedPerson());
		if (personEventArr.size() > 0) {
			MergeSplitVldMsgDto sameEventValidationVB = new MergeSplitVldMsgDto();
			sameEventValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_DUP_EVENT);
			sameEventValidationVB.setValidationDataList(personEventArr);
			sameEventValidationVB.setMsgCategory(ServiceConstants.STAGE);
			sameEventValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameEventValidationVB);
		}
		// Check for each Open Stage listed for the Person Closed if both they
		// and the Person Forward are listed within the same Permanency Planning
		// Meeting.
		ArrayList<PPTParticipantDto> pptArr = pptDao.fetchOpenStagePPTWithPersons(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (pptArr.size() > 0) {
			MergeSplitVldMsgDto samePPTValidationVB = new MergeSplitVldMsgDto();
			samePPTValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_PPT);
			samePPTValidationVB.setValidationDataList(pptArr);
			samePPTValidationVB.setMsgCategory(ServiceConstants.STAGE);
			samePPTValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(samePPTValidationVB);
		}
		// Check for each Open Stage listed for the Person Closed if both they
		// and the Person Forward are listed within the same Child plan
		// (participants)
		ArrayList<ChildPlanDto> childPlanPartArr = childPlanDao.fetchOpenStageChildPlanWithParticipants(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (childPlanPartArr.size() > 0) {
			MergeSplitVldMsgDto sameCPValidationVB = new MergeSplitVldMsgDto();
			sameCPValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_CP_PART);
			sameCPValidationVB.setValidationDataList(childPlanPartArr);
			sameCPValidationVB.setMsgCategory(ServiceConstants.STAGE);
			sameCPValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameCPValidationVB);
		}
		// Check if the Person Closed and Person Forward are both listed as
		// being in the Home
		// at the time of a Conservatorship Removal for the same Open Stage
		ArrayList<PersonHomeRemovalDto> persHmRmvlArr = hmRmvlDao.getOpenStageHmRmvlEventWithPersons(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (persHmRmvlArr.size() > 0) {
			MergeSplitVldMsgDto sameHMValidationVB = new MergeSplitVldMsgDto();
			sameHMValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_PERS_HOME);
			sameHMValidationVB.setValidationDataList(persHmRmvlArr);
			sameHMValidationVB.setMsgCategory(ServiceConstants.STAGE);
			sameHMValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameHMValidationVB);
		}
		// Check for each Open Stage listed for the Person Closed if both they
		// and
		// the Person Forward are listed in the same Allegation
		ArrayList<AllegationDto> allgnArr = allgnDao.getOpenStageAllegationsWithPersons(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (allgnArr.size() > 0) {
			MergeSplitVldMsgDto sameAllgnValidationVB = new MergeSplitVldMsgDto();
			sameAllgnValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_PERS_VC_ALLEGED);
			sameAllgnValidationVB.setValidationDataList(allgnArr);
			sameAllgnValidationVB.setMsgCategory(ServiceConstants.STAGE);
			sameAllgnValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameAllgnValidationVB);
		}
		// Check for each Open Stage listed for the Person Closed if the Person
		// Forward is listed in the stage as the Primary Worker or Secondary
		// worker
		ArrayList<StagePersonValueDto> stageArr = stageDao.getOpenStagesPersonClosedAndForwardAndWorker(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		if (stageArr.size() > 0) {
			MergeSplitVldMsgDto sameStageValidationVB = new MergeSplitVldMsgDto();
			sameStageValidationVB.setMessageInt(ServiceConstants.MSG_CANT_MERGE_TO_WORKER);
			sameStageValidationVB.setValidationDataList(stageArr);
			sameStageValidationVB.setMsgCategory(ServiceConstants.STAGE);
			sameStageValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(sameStageValidationVB);
		}
		// Check whether either the Person Closed or Person Forward is in an
		// Open Stage
		// with any Pending Stage Approvals. If so,
		ArrayList<EventValueDto> stgPendClosureArr = stageDao
				.getPendingStageClosureEventForPerson(personMergeSplitDto.getIdForwardPerson());
		if (stgPendClosureArr.size() > 0) {
			MergeSplitVldMsgDto pendClosureStageValidationVB = new MergeSplitVldMsgDto();
			pendClosureStageValidationVB.setMessageInt(ServiceConstants.MSG_PRSON_IN_PEND_CASE);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Forward");
			pendClosureStageValidationVB.setMessageDataList(embedData);
			pendClosureStageValidationVB.setValidationDataList(stgPendClosureArr);
			pendClosureStageValidationVB.setMsgCategory(ServiceConstants.STAGE);
			pendClosureStageValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(pendClosureStageValidationVB);
		}
		// now do same for closed person
		ArrayList stgPendClosureArr2 = stageDao
				.getPendingStageClosureEventForPerson(personMergeSplitDto.getIdClosedPerson());
		if (stgPendClosureArr2.size() > 0) {
			MergeSplitVldMsgDto pendClosureStageValidationVB = new MergeSplitVldMsgDto();
			pendClosureStageValidationVB.setMessageInt(ServiceConstants.MSG_PRSON_IN_PEND_CASE);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Closed");
			pendClosureStageValidationVB.setMessageDataList(embedData);
			pendClosureStageValidationVB.setValidationDataList(stgPendClosureArr2);
			pendClosureStageValidationVB.setMsgCategory(ServiceConstants.STAGE);
			pendClosureStageValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(pendClosureStageValidationVB);
		}
		// Check whether the Person Closed or the Person Forward is in a Initial
		// Foster
		// Care Eligibility Application for a Stage with a Pending Approval
		// status.
		// If so, display an error message that the Person Closed is in an
		// initial
		// Foster Care Eligibility Application with a Pending Approval status.
		ArrayList<EventValueDto> fceEligList = fceEligDao
				.fetchFCEPendingAppForPerson(personMergeSplitDto.getIdForwardPerson());
		if (fceEligList.size() > 0) {
			MergeSplitVldMsgDto pendFceValidationVB = new MergeSplitVldMsgDto();
			pendFceValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_FCE);
			pendFceValidationVB.setValidationDataList(fceEligList);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Forward");
			pendFceValidationVB.setMessageDataList(embedData);
			pendFceValidationVB.setMsgCategory(ServiceConstants.STAGE);
			pendFceValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(pendFceValidationVB);
		}
		fceEligList = fceEligDao.fetchFCEPendingAppForPerson(personMergeSplitDto.getIdClosedPerson());
		if (fceEligList.size() > 0) {
			MergeSplitVldMsgDto pendFceValidationVB = new MergeSplitVldMsgDto();
			pendFceValidationVB.setMessageInt(ServiceConstants.MSG_MERGE_FCE);
			pendFceValidationVB.setValidationDataList(fceEligList);
			// set the data to be embedded in the message
			ArrayList embedData = new ArrayList();
			embedData.add("Closed");
			pendFceValidationVB.setMessageDataList(embedData);
			pendFceValidationVB.setMsgCategory(ServiceConstants.STAGE);
			pendFceValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(pendFceValidationVB);
		}
		// SIR 1026527 - Validation for PRT.
		MergeSplitVldMsgDto openPRTValidationVB = new MergeSplitVldMsgDto();
		boolean isPersClosedInOpenPrt = personDao.isActivePRT(personMergeSplitDto.getIdClosedPerson());
		// commented Unused Variables.
		/*
		 * boolean isPersFwdInOpenPrt =
		 * personDao.isActivePRT(personMergeSplitDto.getIdForwardPerson());
		 * String idPersFwd =
		 * Long.toString(personMergeSplitDto.getIdForwardPerson());
		 */
		String idPersClosed = Long.toString(personMergeSplitDto.getIdClosedPerson());
		ArrayList embedData = new ArrayList();
		if (isPersClosedInOpenPrt) {
			openPRTValidationVB.setMessageInt(ServiceConstants.MSG_CHILD_IN_OPEN_PRT);
			embedData.add(idPersClosed);
			openPRTValidationVB.setMessageDataList(embedData);
			openPRTValidationVB.setMsgCategory(ServiceConstants.STAGE);
			openPRTValidationVB.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(openPRTValidationVB);
		}
		// end SIR 1026527
		// SIR 10333960/1036055 - Validation for PCSP
		ArrayList pcspPlcmntPer1List = personDao.getPCSPPlcmntForPerson(personMergeSplitDto.getIdForwardPerson());
		ArrayList pcspPlcmntPer2List = personDao.getPCSPPlcmntForPerson(personMergeSplitDto.getIdClosedPerson());
		boolean inSameAsmnt = false;
		ArrayList ovrLapPcspPlcmntList = new ArrayList();
		// Merge 2PIDs with new PCSP table (PCSP_PCMNT)
		if (0 < pcspPlcmntPer1List.size() && 0 < pcspPlcmntPer2List.size()) {
			for (int i = 0; i < pcspPlcmntPer1List.size(); i++) {
				PCSPPersonMergeSplitDto per1Row = (PCSPPersonMergeSplitDto) pcspPlcmntPer1List.get(i);
				for (int j = 0; j < pcspPlcmntPer2List.size(); j++) {
					PCSPPersonMergeSplitDto per2Row = (PCSPPersonMergeSplitDto) pcspPlcmntPer2List.get(j);
					Long idPcspAsmntClosedPID = per1Row.getIdPcspAsmnt();
					Long idPcspAsmntForwardPID = per2Row.getIdPcspAsmnt();
					// constraint error message
					if (idPcspAsmntClosedPID != null && idPcspAsmntForwardPID != null
							&& !idPcspAsmntClosedPID.equals(0L) && !idPcspAsmntForwardPID.equals(0L)
							&& idPcspAsmntClosedPID.equals(idPcspAsmntForwardPID)) {
						inSameAsmnt = true;
						break;
					}
				}
			}
		}
		if (inSameAsmnt) {
			MergeSplitVldMsgDto uniqueConstraintValMsgBean = new MergeSplitVldMsgDto();
			uniqueConstraintValMsgBean.setMessageInt(ServiceConstants.MSG_PCSP_CANDIDATES_IN_OPEN_ASSMNT);
			uniqueConstraintValMsgBean.setValidationDataList(ovrLapPcspPlcmntList);
			uniqueConstraintValMsgBean.setMsgCategory(ServiceConstants.STAGE);
			uniqueConstraintValMsgBean.setMsgSeverity(ServiceConstants.ERROR);
			personMergeSplitDto.getValidationDataList().add(uniqueConstraintValMsgBean);
		}
		
		validateSxvHistory(personMergeSplitDto);
		return personMergeSplitDto;
	}
	
	/**
	 * Method Name: validateSxvHistory
	 *  Method Description: This method Validates if closed & forward person merge can be done
	 *  based on the additional relevant information text boxes character length
	 * 
	 * @param PersonMergeSplitDto
	 * @return void
	 */

	private void validateSxvHistory(PersonMergeSplitDto personMergeSplitDto) {
		
		Predicate<ChildSxVctmztn> childSxVctmztnPredicate = childSxVctmztn -> ObjectUtils.isEmpty(childSxVctmztn);
		Predicate<StringBuilder> strLengthCheck = str -> str.length() > 3999;	
		Predicate<String> strCheck = str -> ObjectUtils.isEmpty(str);
		StringBuilder txtPreviousUnconfirmFinds = null;
		StringBuilder txtSupervisionContactDesc = null;
		
		ChildSxVctmztn closedPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(personMergeSplitDto.getIdClosedPerson());
		ChildSxVctmztn fwdPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(personMergeSplitDto.getIdForwardPerson());
		
		
		if(!childSxVctmztnPredicate.test(fwdPersonSxVctmztn) && !childSxVctmztnPredicate.test(closedPersonSxVctmztn)) {
			
			if(!strCheck.test(fwdPersonSxVctmztn.getTxtPreviousUnconfirmFinds()) && !strCheck.test(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds())) {
				txtPreviousUnconfirmFinds = new StringBuilder();
				txtPreviousUnconfirmFinds.append(fwdPersonSxVctmztn.getTxtPreviousUnconfirmFinds()).append("\n").append(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds());
				if(strLengthCheck.test(txtPreviousUnconfirmFinds)) {
					MergeSplitVldMsgDto svhHistoryMergeValidationError1 = new MergeSplitVldMsgDto();
					svhHistoryMergeValidationError1.setMessageInt(ServiceConstants.PREV_UNCONF_FIND_EXCEEDS_CHAR_LIMIT);
					svhHistoryMergeValidationError1.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
					svhHistoryMergeValidationError1.setMsgSeverity(ServiceConstants.ERROR);
					personMergeSplitDto.getValidationDataList().add(svhHistoryMergeValidationError1);
					} 
				
			}
			
			if(!strCheck.test(fwdPersonSxVctmztn.getTxtSupervisionContactDesc()) && !strCheck.test(closedPersonSxVctmztn.getTxtSupervisionContactDesc())) {
				txtSupervisionContactDesc = new StringBuilder();
				txtSupervisionContactDesc.append(fwdPersonSxVctmztn.getTxtSupervisionContactDesc()).append("\n").append(closedPersonSxVctmztn.getTxtSupervisionContactDesc());
				if(strLengthCheck.test(txtSupervisionContactDesc)) {
					MergeSplitVldMsgDto svhHistoryMergeValidationError2 = new MergeSplitVldMsgDto();
					svhHistoryMergeValidationError2.setMessageInt(ServiceConstants.LIST_OF_SUPVISE_OR_NO_CONT_EXCEEDS_CHAR_LIMIT);
					svhHistoryMergeValidationError2.setMsgCategory(ServiceConstants.DEMOGRAPHIC);
					svhHistoryMergeValidationError2.setMsgSeverity(ServiceConstants.ERROR);
					personMergeSplitDto.getValidationDataList().add(svhHistoryMergeValidationError2);
					} 
				
			}
		}

		
	}

	/**
	 * Method Name: getPersonMergeInfo Method Description: This method returns
	 * Person Merge row based on IdPersonMerge
	 * 
	 * @param idPersonMerge
	 * @param userProfileDto
	 * @return PersonMergeSplitValueDto @
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public PersonMergeSplitDto getPersonMergeInfo(PersonMergeSplitReq personMergeSplitReq) {

		Long idPersonMerge = personMergeSplitReq.getIdPersonMerge();
		PersonMergeSplitDto personMergeSplitDto = new PersonMergeSplitDto();
		personMergeSplitDto = personDao.getPersonMergeInfo(idPersonMerge);

		if (!ObjectUtils.isEmpty(personMergeSplitDto) && !ObjectUtils.isEmpty(personMergeSplitDto.getIdMergeGroup())
				&& personMergeSplitDto.getIdMergeGroup() > ServiceConstants.Zero) {
			List<String> fieldCategoryList = personDao.getPersonMergeUpdateLogList(idPersonMerge);
			HashMap<String, String> hashMap = new HashMap<>();

			for (String fieldCategory : fieldCategoryList) {
				hashMap.put(fieldCategory, ServiceConstants.TRUE);
			}

			ArrayList<String> updCatgItr = lookupDao.getCategoryListingDecode(ServiceConstants.CPMFLDCT);
			for (String string : updCatgItr) {

				if (!hashMap.containsKey(string)) {
					hashMap.put(string, ServiceConstants.FALSE);
				}
			}

			if (!ObjectUtils.isEmpty(personMergeSplitDto))
				personMergeSplitDto.setUpdateLogMap(hashMap);

			ArrayList<MergeSplitVldMsgDto> mergeSplitVldMsgDtoList = (ArrayList<MergeSplitVldMsgDto>) personDao
					.getPersonMergeMessages(idPersonMerge);

			if (!TypeConvUtil.isNullOrEmpty(personMergeSplitDto)) {
				personMergeSplitDto.setValidationDataList(mergeSplitVldMsgDtoList);
			}

			for (MergeSplitVldMsgDto mergeSplitVldMsgDto : mergeSplitVldMsgDtoList) {
				if (!TypeConvUtil.isNullOrEmpty(mergeSplitVldMsgDto.getStep())) {

					if ((mergeSplitVldMsgDto.getStep() == 3)
							&& mergeSplitVldMsgDto.getMessageInt() == ServiceConstants.MSG_POST_MERGE_ALLEG) {
						List<AllegationDto> allegationDtoList = personMergeSplitService
								.getPersonAllegationsUpdatedInMerge(idPersonMerge,
										personMergeSplitDto.getIdForwardPerson(),
										personMergeSplitDto.getIdClosedPerson());

						for (AllegationDto allegationDto : allegationDtoList) {

							Long caseId = allegationDto.getIdCase();
							CaseValueDto caseValueDto = personDao.getForwardCaseInCaseMerge(caseId);
							if (caseValueDto.getIdCase() > 0) {
								allegationDto.setIdFwdCase(caseValueDto.getIdCase());
								allegationDto.setIndSensitiveCase(caseValueDto.getIndCaseSensitive());
							} else
								allegationDto.setIdFwdCase(caseId);
						}

						boolean bSensitiveCaseNoAccess = false;
						if (!personMergeSplitReq.isHasSensitiveCaseAccessRight() && (allegationDtoList != null)) {
							for (AllegationDto allgnRow : allegationDtoList) {
								if (ServiceConstants.Y.equals(allgnRow.getIndSensitiveCase())) {
									bSensitiveCaseNoAccess = true;
									break;
								}
							}
						}
						if (bSensitiveCaseNoAccess) {
							personMergeSplitDto.setSensitiveCaseNoAccess(true);
							allegationDtoList = null;
						}

						mergeSplitVldMsgDto.setAllegationDataList(allegationDtoList);
						break;
					}
				}
			}

			ArrayList<StagePersonValueDto> stagePersonValueDtoList = personDao.getStagesUpdatedInMerge(idPersonMerge);
			personMergeSplitDto.setOpenStgUpdList(stagePersonValueDtoList);

			for (StagePersonValueDto stagePersonValueDto : stagePersonValueDtoList) {

				Long caseId = stagePersonValueDto.getIdCase();
				CaseValueDto caseValueDto = personDao.getForwardCaseInCaseMerge(caseId);
				if (caseValueDto.getIdCase() > 0) {
					stagePersonValueDto.setIdFwdCase(caseValueDto.getIdCase());
					stagePersonValueDto.setIndSensitiveCase(caseValueDto.getIndCaseSensitive());
				} else
					stagePersonValueDto.setIdFwdCase(caseId);
			}

			if (!personMergeSplitReq.isHasSensitiveCaseAccessRight()) {
				for (StagePersonValueDto stagePersonValueDto : stagePersonValueDtoList) {
					if (ServiceConstants.Y.equals(stagePersonValueDto.getIndSensitiveCase())) {
						personMergeSplitDto.setSensitiveCaseNoAccess(true);
						break;
					}
				}
			}

		}

		return personMergeSplitDto;
	}

	
	/**
	 * Method Name: hasRight Method Description: This method returns a boolean
	 * value indicating whether or not the user has the right that was passed
	 * in.
	 * 
	 * @param securityAttribute
	 * @param rights
	 * @return boolean
	 */
	private boolean hasRight(int securityAttribute, List<Long> rights) {
		if (rights.get(securityAttribute) == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method Description: This method will get the duplicate persons meeting
	 * the criteria. Service Name: Duplicate Alert on Person Detail Screen.
	 * 
	 * @param DuplicatePersonsReq
	 * @return DuplicatePersonsRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DuplicatePersonsRes getDuplicates(DuplicatePersonsReq duplicatePersonsReq) {
		DuplicatePersonsRes duplicatePersonsRes = new DuplicatePersonsRes();
		List<PersonIdDto> personIdDtoList = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(duplicatePersonsReq.getIdPerson())) {
			personIdDtoList = personIdDao.getPersonIdAndFullName(duplicatePersonsReq.getIdPerson());
		}
		personIdDtoList.stream().filter(o -> o.getCdPersonIdType().equals(ServiceConstants.DL))
				.forEach(p -> p.setCdPersonIdType(ServiceConstants.CD_PERSON_ID_TYPE_DL));
		duplicatePersonsRes.setPersonIdDtoList(personIdDtoList);
		log.info("TransactionId :" + duplicatePersonsReq.getTransactionId());
		return duplicatePersonsRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes saveIndAbuseNglctDeathInCare(CommonHelperReq personIndAbuse) {
		CommonHelperRes res = new CommonHelperRes();
		if (!TypeConvUtil.isNullOrEmpty(personIndAbuse.getIndicatorAbuse())) {
			res = personDtlDao.saveIndAbuseNglctDeathInCare(personIndAbuse);
		}
		return res;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonHelperRes getIndAbuseNglctDeathInCare(CommonHelperReq person) {
		PersonDto personDto = new PersonDto();
		CommonHelperRes res = new CommonHelperRes();
		personDto = personDao.getPersonById(person.getIdPerson());
		res.setIndAbuseNglctDeathInCare(personDto.getIndAbuseNglctDeathInCare());
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PrsnSrchListpInitRes populateAddtnlInfoIntakeSearch(PrsnSrchListpInitReq personSearchList) {
		List<Long> idForwardPersonMerge = new ArrayList<>();
		PrsnSrchListpInitRes personSearchInfoRes = new PrsnSrchListpInitRes();
		long ulIdForwardPersonMerge = 0L;

		HashMap<Long, PersonSearchDto> mergedPersonMap = new HashMap();
		HashMap<Long, Long> closedPersonMap = new HashMap();

		List<Long> listPersonMerged = new ArrayList();
		List<Long> forwardPersonMerged = new ArrayList();
		ArrayList<PersonSearchDto> removalList = new ArrayList();

		PersonSearchDto mergedPersonRec = null;

		HashMap personMap = new HashMap();

		for (PersonSearchDto forwardPersonMerge : personSearchList.getPersonSearchList()) {
			idForwardPersonMerge = personDao.getForwardPersonInMerge(forwardPersonMerge.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(idForwardPersonMerge) && !ObjectUtils.isEmpty(idForwardPersonMerge)) {
				ulIdForwardPersonMerge = idForwardPersonMerge.get(0);
				if (ulIdForwardPersonMerge == 0) {
					continue;
				} else {
					listPersonMerged.add(ulIdForwardPersonMerge);
					closedPersonMap.put(forwardPersonMerge.getIdPerson(), ulIdForwardPersonMerge);
				}
			}
		}
		if (!ObjectUtils.isEmpty(listPersonMerged)) {

			List<PersonSearchDto> personDetailAll = personDao.getPersonDetailForAllPid(listPersonMerged);

			for (PersonSearchDto personSearchDto : personDetailAll) {
				if (!TypeConvUtil.isNullOrEmpty(personSearchDto.getDtPersonBirth())) {
					int age = calculateAge(personSearchDto.getDtPersonBirth());
					personSearchDto.setPersonAge(Long.valueOf(age));
				}
				mergedPersonMap.put(personSearchDto.getIdPerson(), personSearchDto);
			}

		}

		for (PersonSearchDto forwardPersonMerge : personSearchList.getPersonSearchList()) {
			Long mergedPersonID = forwardPersonMerge.getIdPerson();
			String personFullName = forwardPersonMerge.getPersonFullName();
			if (closedPersonMap.containsKey(forwardPersonMerge.getIdPerson())
					&& mergedPersonMap.containsKey(closedPersonMap.get(forwardPersonMerge.getIdPerson()))) {
				forwardPersonMerge.setIndMerge(ServiceConstants.Y);
				mergedPersonRec = mergedPersonMap.get(closedPersonMap.get(mergedPersonID));
				forwardPersonMerge.setIdForwarded(forwardPersonMerge.getIdPerson());
				forwardPersonMerge.setIdPerson(mergedPersonRec.getIdPerson());
				forwardPersonMerge.setPersonSex(mergedPersonRec.getPersonSex());
				forwardPersonMerge.setDtPersonBirth(mergedPersonRec.getDtPersonBirth());
				forwardPersonMerge.setPersonEthnicGroup(mergedPersonRec.getPersonEthnicGroup());
				forwardPersonMerge.setPersonIdSsn(mergedPersonRec.getPersonIdNumber());
				forwardPersonMerge.setAddrCity(mergedPersonRec.getAddrCity());
				forwardPersonMerge.setAddrPersAddrStLn1(mergedPersonRec.getAddrPersAddrStLn1());
				forwardPersonMerge.setAddrPersAddrStLn2(mergedPersonRec.getAddrPersAddrStLn2());
				forwardPersonMerge.setAddrCounty(mergedPersonRec.getAddrCounty());
				forwardPersonMerge.setAddrZip(mergedPersonRec.getAddrZip());
				forwardPersonMerge.setAddrState(mergedPersonRec.getAddrState());
				forwardPersonMerge.setIncmgPersFullName(mergedPersonRec.getIncmgPersFullName());
				forwardPersonMerge.setFirstName(mergedPersonRec.getFirstName());
				forwardPersonMerge.setMiddleName(mergedPersonRec.getMiddleName());
				forwardPersonMerge.setLastName(mergedPersonRec.getLastName());
				forwardPersonMerge.setPersonFullName(mergedPersonRec.getPersonFullName());
				forwardPersonMerge.setPersonSearchHit(mergedPersonRec.getPersonSearchHit());
				forwardPersonMerge.setIndActiveStatus(mergedPersonRec.getIndActiveStatus());
				if (!TypeConvUtil.isNullOrEmpty(forwardPersonMerge.getDtPersonBirth())) {
					forwardPersonMerge.setPersonAge(mergedPersonRec.getPersonAge());
				}
				forwardPersonMerge.setIndPersonDobApprox(mergedPersonRec.getIndPersonDobApprox());

				if (ServiceConstants.Y.equals(forwardPersonMerge.getIndNameMatch())) {
					forwardPersonMerge.setPersonFullName(personFullName);

				}
			}

			if (ServiceConstants.VARIABLE.equals(forwardPersonMerge.getPersonFullName())
					|| TypeConvUtil.isNullOrEmpty(forwardPersonMerge.getPersonFullName())) {
				PersonDto personDto = personDao.getPersonById(mergedPersonID);
				if (!TypeConvUtil.isNullOrEmpty(personDto)) {
					if (!TypeConvUtil.isNullOrEmpty(personDto.getNmPersonFull())) {
						forwardPersonMerge.setPersonFullName(personDto.getNmPersonFull());
					} else {
						forwardPersonMerge.setPersonFullName((ServiceConstants.COMMA).trim());
					}
				}
			}
			Integer dupRecCnt = personDao.getPersonDupCount(forwardPersonMerge.getIdPerson());
			if (dupRecCnt > 0) {
				forwardPersonMerge.setIndPotentialDup(ServiceConstants.Y);
			} else {
				forwardPersonMerge.setIndPotentialDup(ServiceConstants.N);
			}
			if (personDao.isPersonInfoViewable(forwardPersonMerge.getIdPerson())) {
				forwardPersonMerge.setIndViewPersonInfo(ServiceConstants.Y);
			} else {
				forwardPersonMerge.setIndViewPersonInfo(ServiceConstants.N);
			}

			// identify the duplicates here after the finding the merge forward
			// person
			// forward person would have been already in the returned search
			// list if yes remove the duplicate of the person
			if (!personMap.containsKey(forwardPersonMerge.getIdPerson())) {
				personMap.put(forwardPersonMerge.getIdPerson(), forwardPersonMerge);
				/*
				 * Do not add merged Person
				 */
				if (!CodesConstant.CPERSTAT_M.equalsIgnoreCase(forwardPersonMerge.getIndActiveStatus())) {
					forwardPersonMerged.add(forwardPersonMerge.getIdPerson());
				}
			} else {
				removalList.add(forwardPersonMerge);
			}
		}
		for (PersonSearchDto personSearchDto : removalList) {
			personSearchList.getPersonSearchList().remove(personSearchDto);
		}

		if (ObjectUtils.isEmpty(forwardPersonMerged)) {
			personSearchInfoRes.setPersonSearchList(personSearchList.getPersonSearchList());
			return personSearchInfoRes;
		}
		List<PersonSearchDto> personDetailAll = personDao.getPersonDetailForAllPid(forwardPersonMerged);

		for (PersonSearchDto personSearchDto : personDetailAll) {
			Long id = personSearchDto.getIdPerson();
			personSearchList.getPersonSearchList().stream().forEach(a -> {
				if (a.getIdPerson().equals(id)) {
					a.setPersonEthnicGroup(personSearchDto.getPersonEthnicGroup());
					a.setPersonIdSsn(personSearchDto.getPersonIdNumber());
					a.setPersonIdNumber(personSearchDto.getPersonIdNumber());
					a.setIndPersonDobApprox(personSearchDto.getIndPersonDobApprox());
					a.setDtPersonBirth(personSearchDto.getDtPersonBirth());
					a.setFirstName(personSearchDto.getFirstName());
					a.setMiddleName(personSearchDto.getMiddleName());
					a.setLastName(personSearchDto.getLastName());
					if (ServiceConstants.VARIABLE.equals(a.getPersonFullName())) {
						if (TypeConvUtil.isNullOrEmpty(personSearchDto.getPersonFullName())) {
							a.setPersonFullName(personSearchDto.getPersonFullName());
							a.setIncmgPersFullName(personSearchDto.getIncmgPersFullName());
						} else {
							a.setPersonFullName(ServiceConstants.COMMA.trim());
						}

					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrPersAddrStLn1())) {
						a.setAddrPersAddrStLn1(personSearchDto.getAddrPersAddrStLn1());
					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrPersAddrStLn2())) {
						a.setAddrPersAddrStLn2(personSearchDto.getAddrPersAddrStLn2());
					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrCity())) {
						a.setAddrCity(personSearchDto.getAddrCity());
					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrCounty())) {
						a.setAddrCounty(personSearchDto.getAddrCounty());
					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrZip())) {
						a.setAddrZip(personSearchDto.getAddrZip());
					}
					if (!StringUtils.isEmpty(personSearchDto.getAddrState())) {
						a.setAddrState(personSearchDto.getAddrState());
					}
				}
			});

		}

		List<PersonDupCountDto> personDupList = personDao.getAllPersonDupCount(forwardPersonMerged);
		for (PersonDupCountDto personDupCountDto : personDupList) {
			personSearchList.getPersonSearchList().stream().forEach(a -> {
				Long id = personDupCountDto.getDupIdPerson();
				if (a.getIdPerson().equals(id)) {
					if (personDupCountDto.getCountPerson() > 0) {
						a.setIndPotentialDup(ServiceConstants.Y);
					} else {
						a.setIndPotentialDup(ServiceConstants.N);
					}

				}
			});

		}
		for (PersonSearchDto forwardPersonMerge : personSearchList.getPersonSearchList()) {
			List<PersonSearchDto> FamilyTreeRel = new ArrayList<>();
			FamilyTreeRel = personDao.getFamilyTreeRelIndicator(forwardPersonMerge.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(FamilyTreeRel) && FamilyTreeRel.size() > 0) {
				if (!TypeConvUtil.isNullOrEmpty(FamilyTreeRel.get(0).getIdRelatedPerson())
						|| !TypeConvUtil.isNullOrEmpty(FamilyTreeRel.get(0).getIdPerson())) {
					forwardPersonMerge.setIndFamRelation(ServiceConstants.Y);
				}
			}
		}

		personSearchInfoRes.setPersonSearchList(personSearchList.getPersonSearchList());
		return personSearchInfoRes;
	}

	/**
	 * Method Name: isPersonInfoViewable Method Description: Check if the
	 * Person's Information is Viewable or Not
	 * 
	 * @param isPersonReq
	 * @return IsPersonRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IsPersonRes isPersonInfoViewable(IsPersonReq isPersonReq) {
		IsPersonRes isPersonRes = new IsPersonRes();
		isPersonRes.setIsPerson(personDao.isPersonInfoViewable(isPersonReq.getIdPerson()));
		isPersonRes.setTransactionId(isPersonReq.getTransactionId());
		return isPersonRes;
	}

	/**
	 * Method Name: getPersonDetail Method Description: To get personDetail from
	 * personId
	 * 
	 * @param person
	 * @return PersonDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonDto getPersonDetail(CommonHelperReq person) {
		PersonDto personDto = new PersonDto();
		personDto = personDao.getPersonById(person.getIdPerson());
		return personDto;
	}

	/**
	 * 
	 * Method Name: fetchPersonCharDetails Method Description: Provides the list
	 * of characteristics for a person along with person details
	 * 
	 * @param personCharsReq
	 * @return PersonCharsRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonCharsRes fetchPersonCharDetails(PersonCharsReq personCharsReq) {
		PersonCharsRes personCharsRes = new PersonCharsRes();
		List<us.tx.state.dfps.service.person.dto.CharacteristicsDto> liCharacteristicsDto = new ArrayList<>();
		if (TypeConvUtil.isNullOrEmpty(personCharsReq.getDtEffectiveDate())) {
			PersonDto personDto = personDao.getPersonById(personCharsReq.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(personDto.getCdPersonChar())
					&& personDto.getCdPersonChar().equals(ServiceConstants.CHARACTERISTIC_CHECKED)) {
				liCharacteristicsDto = personDao.fetchPersonCharaceristics(personCharsReq.getIdPerson());

			}
			personCharsRes.setCharacteristicsDto(liCharacteristicsDto);
			personCharsRes.setPersonDto(personDto);
		} else {
			liCharacteristicsDto = personDao.fetchPersonCharacsWithDate(personCharsReq.getIdPerson(),
					personCharsReq.getDtEffectiveDate());
			personCharsRes.setCharacteristicsDto(liCharacteristicsDto);

		}
		return personCharsRes;
	}

	public PersonDtlRes getPersonPca(Long idPerson) {

		Person person = personDao.getPerson(idPerson);
		PersonDtlRes personDtls = new PersonDtlRes();

		if (!TypeConvUtil.isNullOrEmpty(person.getNmPersonFull()))
			personDtls.setNmPersonFull(person.getNmPersonFull());

		return personDtls;

	}

	/**
	 * Method Name: fetchPersonPotentialDuplicates Method Description: Returns
	 * list of person Potential Duplicates.
	 * 
	 * @param PotentialDupReq
	 * @return PotentialDupListRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PotentialDupRes fetchPersonPotentialDuplicates(PotentialDupReq potentialDupListReq) {
		PotentialDupRes potentialDupListRes = new PotentialDupRes();
		List<PersonPotentialDupDto> personPotentialDupList = potentialDupDao
				.getPersonPotentialDupList(potentialDupListReq.getIdPerson());
		potentialDupListRes.setPersonPotentialDupDtos(personPotentialDupList);
		potentialDupListRes.setTransactionId(potentialDupListReq.getTransactionId());
		return potentialDupListRes;
	}

	/**
	 * Method Name: fetchPersonPotentialDuplicate Method Description: Returns
	 * Person Potential Duplicate detail given Person Potential Duplicate ID
	 * 
	 * @param PotentialDupReq
	 * @return PotentialDupListRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PotentialDupRes fetchPersonPotentialDuplicate(PotentialDupReq potentialDupReq) {
		PotentialDupRes potentialDupRes = new PotentialDupRes();
		PersonPotentialDupDto personPotentialDup = potentialDupDao.getPersonPotentialDup(potentialDupReq.getIdPerson());
		//Added [artf133225] ALM ID : 13757 : PD 57954 : Potential Duplicate Identified By
		personPotentialDup.setStaffName(personDao.getPerson(personPotentialDup.getIdStaff()).getNmPersonFull());
		//End [artf133225] ALM ID : 13757
		potentialDupRes.setPersonPotentialDupDto(personPotentialDup);
		potentialDupRes.setTransactionId(potentialDupReq.getTransactionId());
		return potentialDupRes;
	}

	/**
	 * Method Name: fetchActivePersonPotentialDuplicate Method Description:
	 * Retrieve active potential Duplicate and other information related to a
	 * person.
	 * 
	 * @param personActiveDupReq
	 * @return PersonActiveDupRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PotentialDupRes fetchActivePersonPotentialDuplicate(PotentialDupReq personActiveDupReq) {
		PotentialDupRes personActiveDupRes = new PotentialDupRes();
		personActiveDupRes.setPersonPotentialDupDto(potentialDupDao.getActivePersonPotentialDupDetail(
				personActiveDupReq.getIdPerson(), personActiveDupReq.getIdDupPerson()));
		personActiveDupRes.setTransactionId(personActiveDupReq.getTransactionId());
		return personActiveDupRes;
	}

	/**
	 * Method Name: getForwardPersonInMerge Method Description:This method
	 * checks if the person is closed person in a merge
	 * 
	 * @param idClosedPerson
	 * @return Long
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getForwardPersonInMerge(Long idClosedPerson) {
		return personDtlDao.getForwardPersonInMerge(idClosedPerson);

	}

	/**
	 * Method Name: isPersonIRReport Method Description:This method checks if
	 * the person is in IR Report
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPersonIRReport(Long idPerson) {
		return personDao.isPersonIRReport(idPerson);

	}

	/**
	 * 
	 * Method Description:This is the form service for the Extended Person List
	 * form. Tuxedo Service Name: CPER02S
	 * 
	 * @param personDtlReq
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getExtendedPersonDtl(PersonDtlReq personDtlReq) {

		ExtPersonDto extPersonDto = new ExtPersonDto();

		// csec02d, case information
		/* retrieves stage and caps_case table */
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(personDtlReq.getIdStage());

		// CallCLSCGED
		/* retrieves Extended Person List */
		List<PersonExtDto> personExtDtoList = personDao.getPersonExtList(personDtlReq.getIdPerson());

		// CallCCMN44D
		PersonDto personDto = personDao.getPersonById(personDtlReq.getIdPerson());

		extPersonDto.setGenericCaseInfoDto(genericCaseInfoDto);
		extPersonDto.setPersonDto(personDto);
		extPersonDto.setPersonExtDtoList(personExtDtoList);

		return extendedPersonListPrefillData.returnPrefillData(extPersonDto);
	}

	/**
	 * 
	 * Method Name: fetchCatChar Method Description:Provides the list of all
	 * currently valid characteristics for a given category, plus indicators for
	 * AFCARS and diagnosability.
	 * 
	 * @param catCharReq
	 * @return CatCharRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public CatCharRes fetchCatChar(CatCharReq catCharReq) {
		CatCharRes catCharRes = new CatCharRes();

		try {
			List<CatCharDto> liCatCharDto = personDao.fetchCatChar(catCharReq.getCharCodeType());

			if (!TypeConvUtil.isNullOrEmpty(liCatCharDto)) {
				catCharRes.setLiCatCharDto(liCatCharDto);
			}
		} catch (DataNotFoundException de) {
			log.error(de.getMessage());
		}

		return catCharRes;
	}

	/**
	 * Method Name: getPersonCharList Method Description:Get Person
	 * Characteristics for input person id and Category Type
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @return List<PersonCharDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<PersCharsDto> getPersonCharList(long idPerson, String cdCharCategory) {
		return personCharDao.getPersonCharData(idPerson, cdCharCategory);
	}

	/**
	 * Method Name: getPersonCharList Method Description:Retrieve
	 * characteristics for given person ID, in given category from snapshot
	 * table (SS_CHARACTERISTICS)
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersonCharDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<PersCharsDto> getPersonCharList(long idPerson, String cdCharCategory, long idReferenceData,
			String cdActionType, String cdSnapshotType) {
		return personCharDao.getPersonCharData(idPerson, cdCharCategory, idReferenceData, cdActionType, cdSnapshotType);
	}

	/**
	 * 
	 * Method Name: selectAllRelationsOfPerson Method Description:Reads the
	 * person Family Tree relationships from snapshot table (SS_PERSON_RELATION)
	 * 
	 * (For example: This method is used for displaying the Select Forward
	 * person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<FTPersonRelationDto> selectAllRelationsOfPerson(String cdActionType, String cdSnapshotType,
			Long idPerson, Long idReferenceData) {
		return ftRelationshipDao.selectAllDirectRelationships(idPerson, idReferenceData, cdActionType, cdSnapshotType);
	}

	/**
	 * Method Name: selectAllRelationsOfPerson Method Description:Get All Family
	 * Tree RelationShip for person
	 * 
	 * @param idPerson
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<FTPersonRelationDto> selectAllRelationsOfPerson(Long idPerson) {
		List<Integer> idPersonList = new ArrayList<>();
		idPersonList.add(idPerson.intValue());
		return ftRelationshipDao.selectAllDirectRelForPersons(idPersonList);
	}

	/**
	 * Method Name: getPersonPrimaryEmail Method Description:Fetches the primary
	 * email for the person
	 * 
	 * @param idPerson
	 * @return PersonEmailValueDto
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PersonEmailValueDto getPersonPrimaryEmail(Long idPerson) {
		return personDao.getPersonPrimaryEmail(idPerson);
	}

	/**
	 * Method Name: fetchPersonRaceList Method Description: Get Person Race for
	 * input person id.
	 * 
	 * @param idPerson
	 * @return PersonRaceRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ArrayList<PersonRaceDto> fetchPersonRaceList(long idPerson) {
		return personRaceEthnicityDao.getPersonRaceList(idPerson);
	}

	/**
	 * 
	 * Method Name: getPersonPrimaryEmail Method Description: Reads the current
	 * primary email for a person from snapshot table (SS_PERSON_EMAIL) ( For
	 * example: This method is used for displaying the Select Forward person
	 * details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonEmailValueDto getPersonPrimaryEmail(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData) {
		return personDao.getPersonPrimaryEmailFromSnapshot(idPerson, idReferenceData, cdActionType, cdSnapshotType);
	}

	/**
	 * Method Name: getLatestActiveIncmRsrcStartDate Method Description: get
	 * Latest(Date) Active Income & Resource for person id.
	 * 
	 * @param latestActiveReq
	 * @return LatestActiveRes
	 * @throws DataNotFoundException
	 * @throws ParseException
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Date getLatestActiveIncmRsrcStartDate(Long idPerson) {
		return incomeResourceDao.getLatestActiveStartDate(idPerson);
	}

	/**
	 * Method Name: getCurrentEducationHistoryById Method Description: This
	 * method gets current Education for input person id
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	@Override
	public EducationHistoryDto getCurrentEducationHistoryById(Long idPerson) {
		return educationDao.getCurrentEducationHistoryById(idPerson);
	}

	/**
	 * 
	 * Method Name: getCurrentEducationHistory Method Description:Fetches the
	 * person current educational history from snapshot table
	 * (SS_EDUCATIONAL_HISTORY) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EducationHistoryDto getCurrentEducationHistory(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData) {
		return educationDao.getCurrentEducationHistory(idPerson, idReferenceData, cdActionType, cdSnapshotType);
	}

	/**
	 * Method Name: getEducationalNeedListForHist Method Description:Fetches the
	 * Get Education Need for a Education History Record from snapshot table
	 * (SS_EDUCATIONAL_NEED)
	 * 
	 * @param idPerson
	 * @param idEduHist
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<EducationalNeedDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<EducationalNeedDto> getEducationalNeedListForHist(Long idPerson, Long idEduHist, Long idReferenceData,
			String cdActionType, String cdSnapshotType) {
		return educationDao.getEducationalNeedListForHist(idPerson.intValue(), idEduHist.intValue(),
				idReferenceData.intValue(), cdActionType, cdSnapshotType);
	}

	/**
	 * Method Name: getEducationalNeedListForHist Method Description: This
	 * method fetches the education need records for a education history record.
	 * 
	 * @param idEduHist
	 * @return ArrayList<EducationalNeedDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ArrayList<EducationalNeedDto> getEducationalNeedListForHist(Long idEduHist) {

		return educationDao.getEducationalNeedListForHist(idEduHist);
	}

	/**
	 * 
	 * Method Name: getPersonCategoryList Method Description: Get Person
	 * Category List for input person
	 * 
	 * @param personId
	 * @return PersonCategoryDto
	 * 
	 */
	@Override
	public List<PersonCategoryDto> getPersonCategoryList(Long personId) {
		return personDao.getPersonCategoryList(personId);
	}

	/**
	 * Method Name: isPrimaryChildInOpenStage Method Description: Checks if the
	 * person is a Primary Child (PC) in an open stage for list of stages
	 * 
	 * @param idPerson
	 * @param chkStages
	 * @return boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean isPrimaryChildInOpenStage(int idPerson, String[] chkStages) {
		if ((null != chkStages) && (chkStages.length > 0)) {
			Arrays.sort(chkStages);
			ArrayList<StageValueBeanDto> stageArr = stageDao.getStageListForPC(idPerson, true);

			if (stageArr.size() > 0) {
				for (int i = 0; i < stageArr.size(); i++) {
					StageValueBeanDto stageValueDto = (StageValueBeanDto) stageArr.get(i);
					if (null != stageValueDto.getCdStage()) {
						if (Arrays.binarySearch(chkStages, stageValueDto.getCdStage()) >= 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Method Name: fetchActivePrimaryName Method Description:Fetches the active
	 * primary name for a person from NAME table
	 * 
	 * @param idPerson
	 * @return NameDto ;
	 * @throws ParseException
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public NameDto fetchActivePrimaryName(Long idPerson) {
		NameDto nameDto = null;
		Name name = nameDao.getActivePrimaryName(idPerson);
		if (!ObjectUtils.isEmpty(name)) {
			nameDto = new NameDto();
			nameDto.setCdNameSuffix(name.getCdNameSuffix());
			nameDto.setTsLastUpdate(name.getDtLastUpdate());
			nameDto.setDtNameEnd(name.getDtNameEndDate());
			nameDto.setDtNameStart(name.getDtNameStartDate());
			nameDto.setIdName(name.getIdName());
			nameDto.setIndNameEmp(name.getIndNameEmp().toString());
			nameDto.setIndNameInvalid(name.getIndNameInvalid());
			nameDto.setIndNamePrimary(name.getIndNamePrimary());
			nameDto.setNmNameFirst(name.getNmNameFirst());
			nameDto.setNmNameLast(name.getNmNameLast());
			nameDto.setNmNameMiddle(name.getNmNameMiddle());
			nameDto.setIdPerson(name.getPerson().getIdPerson());
		}

		return nameDto;
	}

	/**
	 * 
	 * Method Name: fetchActivePrimaryName Method Description:Reads the active
	 * primary name for the person from snapshot. ( For example: This method is
	 * used for displaying the Select Forward person details in post person
	 * merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NameDto fetchActivePrimaryName(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData) {

		NameDto nameDto = new NameDto();
		nameDto = nameDao.getActivePrimaryName(idPerson, idReferenceData, cdActionType, cdSnapshotType);

		return nameDto;
	}

	/**
	 * 
	 * Method Name: getIncomeAndResourceList Method Description:Fetches the
	 * person income and resources list from snapshot table
	 * (SS_INCOME_AND_RESOURCES) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param activeFlag
	 * @param idPerson
	 * @param sortBy
	 * @param idReferenceData
	 * @param cdSnapshotType
	 * @param cdActionType
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, Boolean activeFlag,
			Long idReferenceData, String cdActionType, String cdSnapshotType) {
		return incomeResourceDao.getIncomeAndResourceList(idPerson, sortBy, activeFlag, idReferenceData, cdActionType,
				cdSnapshotType);
	}

	/**
	 * Method Name: getIncomeAndResourceList Method Description: Get Income and
	 * Resources for input person id.
	 * 
	 * @param incomeResourceReq
	 * @return GetIncomeResourceRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, boolean activeFlag) {
		return incomeResourceDao.getIncomeAndResourceList(idPerson, sortBy, activeFlag);
	}

	/**
	 * 
	 * Method Name: fetchPersonChar Method Description: Retrieve characteristics
	 * for given person ID, in given category, with status indicators and dates.
	 * 
	 * @param personCharsReq
	 * @return GetPersonCharsRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public GetPersonCharsRes fetchPersonChar(PersonCharsReq personCharsReq) {
		GetPersonCharsRes personCharsRes = personDtlDao.fetchPersonChar(personCharsReq);

		return personCharsRes;
	}

	/**
	 * 
	 * Method Name: calculateAge Method Description: This method is to calculate
	 * age
	 * 
	 * @param date
	 * @return age
	 */
	private int calculateAge(Date date) {

		int age = 0;
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();
		dob.setTime(date);
		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		age = year1 - year2;
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		if (month2 > month1) {
			age--;
		} else if (month1 == month2) {
			int day1 = now.get(Calendar.DAY_OF_MONTH);
			int day2 = dob.get(Calendar.DAY_OF_MONTH);
			if (day2 > day1) {
				age--;
			}
		}
		return age;
	}

	/**
	 * Method Name: getStaffLegalName Method Description:This method retrieves
	 * staff Legal Name using id_person
	 * 
	 * @param idPerson
	 * @return nameDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public us.tx.state.dfps.service.contact.dto.NameDto getStaffLegalName(Long idPerson) {
		log.info("Inside method getStaffLegalName in PersonDtlServiceImpl class");
		us.tx.state.dfps.service.contact.dto.NameDto nameDto = personDao.getStaffLegalName(idPerson);
		log.info("Outside method getStaffLegalName in PersonDtlServiceImpl class");
		return nameDto;
	}

	/**
	 * Method Name: getExtPersonRoles Method Description:Get the external person
	 * roles for a Person with External Application access
	 * 
	 * @param idPerson
	 * @return String
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public String getExtPersonRoles(Long idPerson) {
		log.info("Inside method getExtPersonRoles in PersonDtlServiceImpl class");
		String extPersonRoles = personDao.getExtPersonRoles(idPerson);
		log.info("Outside method getExtPersonRoles in PersonDtlServiceImpl class");
		return extPersonRoles;
	}

	/**
	 * Method Name: getSystemAccessList Method Description: Get the application
	 * access list for external user types
	 * 
	 * @param idPerson
	 * @return List<SystemAccessDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<SystemAccessDto> getSystemAccessList(Long idPerson) {
		log.info("Inside method getSystemAccessList in PersonDtlServiceImpl class");
		List<SystemAccessDto> systemAccessValueDtos = personDao.getSystemAccessList(idPerson);
		log.info("Outside method getSystemAccessList in PersonDtlServiceImpl class");
		return systemAccessValueDtos;
	}

	/**
	 * Method Name: getStaffAddress Method Description:This method retrieves
	 * staff mail code address using cd_mail_code
	 * 
	 * @param mailCode
	 * @return String
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public String getStaffAddress(String mailCode) {
		log.info("Inside method getStaffAddress in PersonDtlServiceImpl class");
		PersonValueDto personValueDto = personDao.getStaffAddress(mailCode);
		StringBuilder builder = new StringBuilder();
		if (personValueDto != null && !StringUtils.isEmpty(personValueDto.getStreetLn1())) {
			builder.append(personValueDto.getStreetLn1()).append(", ").append(personValueDto.getCity()).append(", ")
					.append("TX. ").append(personValueDto.getZip());
		} else {
			builder.append("");
		}
		return builder.toString();
	}

	/**
	 * Method Name: saveSystemAccess Method Description: This method saves the
	 * application access for external user types
	 * 
	 * @param systemAccessDto
	 * @param cdReqFunction
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public void saveSystemAccess(SystemAccessDto systemAccessDto, String cdReqFunction) {
		personDao.saveSystemAccess(systemAccessDto, cdReqFunction);
	}

  /**
   * method to get Child Citizenship by IdStage and relationship type
   *
   * @param idStage
   * @param persRelInt
   * @return
   */
  @Override
  public String getPersonCitizenshipByStageIdAndRelType(long idStage, String persRelInt) {
    return personDao.getOldestVictimCitizenshipByStageIdAndRelType(idStage, persRelInt);
  }

  /**
   * method to get Oldest victim person id and person full name by IdStage and relationship
   * type('OV')
   *
   * @param idStage
   * @return PersonDto
   */
  @Override
  public PersonDto getOldestVictimPersonByStageIdAndRelType(long idStage) {
    return personDao.getOldestVictimPersonByStageIdAndRelType(idStage);
  }
}
