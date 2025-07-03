package us.tx.state.dfps.service.person.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.PersonIdService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.AddressMassUpdateReq;
import us.tx.state.dfps.service.common.request.CaseExtendedPersonReq;
import us.tx.state.dfps.service.common.request.CaseFileMgmtReq;
import us.tx.state.dfps.service.common.request.CatCharReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryNarrReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.DuplicatePersonsReq;
import us.tx.state.dfps.service.common.request.EditPersonAddressReq;
import us.tx.state.dfps.service.common.request.EduNeedListReq;
import us.tx.state.dfps.service.common.request.EducationHistoryReq;
import us.tx.state.dfps.service.common.request.EducationNeedListReq;
import us.tx.state.dfps.service.common.request.EmailDetailReq;
import us.tx.state.dfps.service.common.request.EmailNotificationsReq;
import us.tx.state.dfps.service.common.request.EmailReq;
import us.tx.state.dfps.service.common.request.GetForwardPersonInMergeReq;
import us.tx.state.dfps.service.common.request.GetPersCharsDtlReq;
import us.tx.state.dfps.service.common.request.GetPersonPrimaryEmailReq;
import us.tx.state.dfps.service.common.request.GroupUpdateReq;
import us.tx.state.dfps.service.common.request.HomeMembTrainRtrvReq;
import us.tx.state.dfps.service.common.request.IsPersonReq;
import us.tx.state.dfps.service.common.request.NameHistoryDetailReq;
import us.tx.state.dfps.service.common.request.PersonCharacteristicsReq;
import us.tx.state.dfps.service.common.request.PersonCharsReq;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.request.PersonEmailReq;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.request.PersonMergeSplitReq;
import us.tx.state.dfps.service.common.request.PersonPhoneReq;
import us.tx.state.dfps.service.common.request.PersonRaceReq;
import us.tx.state.dfps.service.common.request.PersonStaffReq;
import us.tx.state.dfps.service.common.request.PhoneMassUpdateReq;
import us.tx.state.dfps.service.common.request.PhoneReq;
import us.tx.state.dfps.service.common.request.PotentialDupReq;
import us.tx.state.dfps.service.common.request.PrimaryChildReq;
import us.tx.state.dfps.service.common.request.PrsnSrchListpInitReq;
import us.tx.state.dfps.service.common.request.RecordsCheckDetailReq;
import us.tx.state.dfps.service.common.request.RecordsCheckReq;
import us.tx.state.dfps.service.common.request.RetrvPersonIdentifiersReq;
import us.tx.state.dfps.service.common.request.RtrvSubPersIdsReq;
import us.tx.state.dfps.service.common.request.RtrvUnitIdReq;
import us.tx.state.dfps.service.common.request.SaveNameHistoryDtlReq;
import us.tx.state.dfps.service.common.request.SavePersonIdentifiersReq;
import us.tx.state.dfps.service.common.request.SearchPersonRaceEthnicityReq;
import us.tx.state.dfps.service.common.request.SelectAllRelationsOfPersonReq;
import us.tx.state.dfps.service.common.request.TletsReq;
import us.tx.state.dfps.service.common.request.UpdateSearchPersonIndReq;
import us.tx.state.dfps.service.common.request.UpdtPersPotentialDupReq;
import us.tx.state.dfps.service.common.request.UpdtPersonDtlReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.common.response.AddressMassUpdateRes;
import us.tx.state.dfps.service.common.response.CaseExtendedPersonRes;
import us.tx.state.dfps.service.common.response.CatCharRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryNarrRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryRes;
import us.tx.state.dfps.service.common.response.DuplicatePersonsRes;
import us.tx.state.dfps.service.common.response.EditPersonAddressRes;
import us.tx.state.dfps.service.common.response.EducationHistoryRes;
import us.tx.state.dfps.service.common.response.EducationNeedListRes;
import us.tx.state.dfps.service.common.response.EmailDetailRes;
import us.tx.state.dfps.service.common.response.EmailNotificationsRes;
import us.tx.state.dfps.service.common.response.EmailRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.common.response.StagePersonRes;
import us.tx.state.dfps.service.common.response.GetForwardPersonInMergeRes;
import us.tx.state.dfps.service.common.response.GetPersCharsDtlRes;
import us.tx.state.dfps.service.common.response.GetPersonCharsRes;
import us.tx.state.dfps.service.common.response.GetPersonPrimaryEmailRes;
import us.tx.state.dfps.service.common.response.GroupUpdateRes;
import us.tx.state.dfps.service.common.response.HomeMembTrainRtrvRes;
import us.tx.state.dfps.service.common.response.IsPersonRes;
import us.tx.state.dfps.service.common.response.NameHistoryDetailRes;
import us.tx.state.dfps.service.common.response.PersonCategoryRes;
import us.tx.state.dfps.service.common.response.PersonCharRes;
import us.tx.state.dfps.service.common.response.PersonCharacteristicsRes;
import us.tx.state.dfps.service.common.response.PersonCharsRes;
import us.tx.state.dfps.service.common.response.PersonCitizenshipStatusRes;
import us.tx.state.dfps.service.common.response.PersonDetlsRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.response.PersonEjbRes;
import us.tx.state.dfps.service.common.response.PersonEmailRes;
import us.tx.state.dfps.service.common.response.PersonExtRes;
import us.tx.state.dfps.service.common.response.PersonIRReportRes;
import us.tx.state.dfps.service.common.response.PersonListRes;
import us.tx.state.dfps.service.common.response.PersonMergeSplitRes;
import us.tx.state.dfps.service.common.response.PersonRaceRes;
import us.tx.state.dfps.service.common.response.PersonStaffRes;
import us.tx.state.dfps.service.common.response.PhoneMassUpdateRes;
import us.tx.state.dfps.service.common.response.PhoneRes;
import us.tx.state.dfps.service.common.response.PotentialDupRes;
import us.tx.state.dfps.service.common.response.PrimaryChildRes;
import us.tx.state.dfps.service.common.response.PrsnSrchListpInitRes;
import us.tx.state.dfps.service.common.response.RecordsCheckRes;
import us.tx.state.dfps.service.common.response.RetrvPersonIdentifiersRes;
import us.tx.state.dfps.service.common.response.RtrvSubPersIdsRes;
import us.tx.state.dfps.service.common.response.RtrvUnitIdRes;
import us.tx.state.dfps.service.common.response.SaveNameHistoryRes;
import us.tx.state.dfps.service.common.response.SavePersonIdentifiersRes;
import us.tx.state.dfps.service.common.response.SearchPersonRaceEthnicityRes;
import us.tx.state.dfps.service.common.response.SelectAllRelationsOfPersonRes;
import us.tx.state.dfps.service.common.response.TletsRes;
import us.tx.state.dfps.service.common.response.UpdateSearchPersonIndRes;
import us.tx.state.dfps.service.common.response.UpdtPersPotentialDupRes;
import us.tx.state.dfps.service.common.response.UpdtPersonDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.person.dto.RecordsCheckDto;
import us.tx.state.dfps.service.person.service.CriminalHistoryService;
import us.tx.state.dfps.service.person.service.EducationHistoryService;
import us.tx.state.dfps.service.person.service.EmpSkillsService;
import us.tx.state.dfps.service.person.service.FAIndivTrainingService;
import us.tx.state.dfps.service.person.service.PersonAddressService;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.person.service.PersonEmailService;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.person.service.PersonPhoneService;
import us.tx.state.dfps.service.person.service.PersonRaceEthnicityService;
import us.tx.state.dfps.service.person.service.RecordsCheckService;
import us.tx.state.dfps.service.person.service.RtrvUnitIdService;
import us.tx.state.dfps.service.person.service.TletsService;
import us.tx.state.dfps.service.personmergesplit.utils.SelectForwardPersonDataManager;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonController May 8, 2018- 5:11:21 PM © 2017 Texas Department
 * of Family and Protective Services
 * *******Change History**********
 *   01/25/2021 nairl artf172936 : DEV BR 15.01 Indicator (IMPACT) for Person Who Has Access to CHRI P2
 */
@Api(tags = { "identity" })
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	PersonListService personListService;

	@Autowired
	RtrvUnitIdService rtrvUnitIdService;

	@Autowired
	CriminalHistoryService criminalHistoryService;

	@Autowired
	EmpSkillsService empSkillsService;

	@Autowired
	PersonRaceEthnicityService personRaceEthnicityService;

	@Autowired
	PersonPhoneService personPhoneService;

	@Autowired
	PersonIdService personIdService;

	@Autowired
	PersonAddressService personAddressService;

	@Autowired
	TletsService tletsService;

	@Autowired
	PersonEmailService personEmailService;

	@Autowired
	RecordsCheckService recordsCheckService;

	@Autowired
	EducationHistoryService educationHistoryService;

	@Autowired
	FAIndivTrainingService faIndivTrainingService;

	@Autowired
	SelectForwardPersonDataManager selectForwardPersonDataManager;

	private static final Logger log = Logger.getLogger(PersonController.class);

	private static final String CHARCODETYPE_MANDATORY = "charcodetype.mandatory";

	/**
	 * 
	 * Method Description:This method calls to get person details in Kinship
	 * Home Assessment, Placement and Event search. This service will retrieve
	 * all columns for an IdPerson from the Person Dtl table. This Method is to
	 * get response through service layer by giving person_id or Stage_id and
	 * Stage Person Role in request object(PersonDtlReq) Service Name: CCFC37S
	 * 
	 * @param personDtlReq
	 * @return personDtlRes
	 */
	// CCFC37S
	@RequestMapping(value = "/persondtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonDtlRes getPersonDtl(@RequestBody PersonDtlReq personDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(personDtlReq.getIdPerson())
				&& TypeConvUtil.isNullOrEmpty(personDtlReq.getIdStage())
				&& TypeConvUtil.isNullOrEmpty(personDtlReq.getCdStagePersRole())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		PersonDtlRes personDtlRes = new PersonDtlRes();
		personDtlRes = personDtlService.getPersonDtl(personDtlReq);
		return personDtlRes;
	}

	/**
	 * 
	 * Method Description:This method calls to get person details in Kinship
	 * Home Assessment, Placement and Event search. This service will retrieve
	 * all columns for an IdPerson from the Person Dtl table. This Method is to
	 * get response through service layer by giving person_id in request
	 * object(PersonDtlReq) Service Name: CCFC37S
	 * 
	 * @param personDtlReq
	 * @return personDtlRes
	 */
	// CCFC37S
	@RequestMapping(value = "/persondtlById", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonDtlRes persondtlById(@RequestBody PersonDtlReq personDtlReq) {
		PersonDtlRes personDtlRes = new PersonDtlRes();
		personDtlRes = personDtlService.getPersonDtl(personDtlReq);
		return personDtlRes;
	}

	/**
	 * 
	 * Method Description: This Method is to update all column in PersonDtl
	 * through service layer by giving person_id in request
	 * object(UpdatePersonDto) Service Name: CCFC38S
	 * 
	 * @param personDtlDto
	 * @return updatePersonDtlRes
	 */
	// CCFC38S
	@RequestMapping(value = "/updatePersonDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public UpdtPersonDtlRes updatePersonDtl(@RequestBody UpdtPersonDtlReq updatePersonDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(updatePersonDtlReq.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		UpdtPersonDtlRes updatePersonDtlRes = new UpdtPersonDtlRes();
		updatePersonDtlRes = personDtlService.updatePersonDtl(updatePersonDtlReq);
		return updatePersonDtlRes;
	}

	/**
	 * 
	 * Method Description: Retrieves information for the Person List Box from
	 * the Person and Stage_Person_Link tables based upon either a particular
	 * Stage or a particular Case. Service Name: CINV01S
	 * 
	 * @param retrievePersonListReq
	 * @return personListRes
	 */
	// CINV01S
	@RequestMapping(value = "/personlist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonListRes getPersonList(@RequestBody PersonListReq retrievePersonListReq) {
		if (StringUtils.isBlank(retrievePersonListReq.getStageProgram()) && null == (retrievePersonListReq.getIdStage())
				&& null == (retrievePersonListReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		PersonListRes personListRes = new PersonListRes();
		personListRes = personListService.getPersonList(retrievePersonListReq);
		return personListRes;
	}

	/**
	 * 
	 * Method Description: Retrieves information for the Person List Box from
	 * the Person and Stage_Person_Link tables based upon either a particular
	 * Stage or a particular Case. Service Name: CINV01S
	 * 
	 * @param retrievePersonListReq
	 * @return personListRes
	 */
	// modified CINV01S required for Child Plan Detail page
	@RequestMapping(value = "/personlists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonListRes getPersonLists(@RequestBody PersonListReq retrievePersonListReq) {
		if (StringUtils.isBlank(retrievePersonListReq.getStageProgram()) && null == (retrievePersonListReq.getIdStage())
				&& null == (retrievePersonListReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		PersonListRes personListRes = new PersonListRes();
		personListRes = personListService.getPersonLists(retrievePersonListReq);
		if (personListRes.getRetrievePersonList().size() <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}
		return personListRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve ID UNIT for a Parent Unit,
	 * given CD UNIT PROGRAM, CD UNIT REGION, and NBR UNIT. Service Name:
	 * CCMN47S
	 * 
	 * @param rtrvUnitIdReq
	 * @return rtrvUnitIdRes
	 */
	@RequestMapping(value = "/getUnitId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RtrvUnitIdRes getUnitId(@RequestBody RtrvUnitIdReq rtrvUnitIdReq) {
		if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getCdUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("common.unitRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getCdUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.unitProgram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
		}
		RtrvUnitIdRes rtrvUnitIdRes = new RtrvUnitIdRes();
		rtrvUnitIdRes = rtrvUnitIdService.getUnitId(rtrvUnitIdReq);
		return rtrvUnitIdRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve a sub-set of the
	 * ID_PERSON's passed into this service who have the SKILL(S) which are
	 * passed into this method Service Name: CCMN48S
	 * 
	 * @param rtrvSubPersIdsReq
	 * @return rtrvSubsetPersIdsRes
	 */
	@RequestMapping(value = "/getPersonIds", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RtrvSubPersIdsRes getPersonIds(@RequestBody RtrvSubPersIdsReq rtrvSubPersIdsReq) {
		if ((rtrvSubPersIdsReq.getUlIdPerson().size() <= 0)) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.personIdsList.mandatory", null, Locale.US));
		}
		if ((rtrvSubPersIdsReq.getSzCdEmpSkill().size() <= 0)) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.empSkillsList.mandatory", null, Locale.US));
		}
		RtrvSubPersIdsRes rtrvSubsetPersIdsRes = new RtrvSubPersIdsRes();
		List<Long> persIds = empSkillsService.getPersonIds(rtrvSubPersIdsReq);
		rtrvSubsetPersIdsRes.setUlIdPerson(persIds);
		return rtrvSubsetPersIdsRes;
	}

	/**
	 * 
	 * Method Description:searchPersonRaceEthnicity @param
	 * searchPersonRaceEthnicityReq @return
	 */
	// CCMN95S
	@RequestMapping(value = "/searchPersonRaceEthnicity", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SearchPersonRaceEthnicityRes searchPersonRaceEthnicity(
			@RequestBody SearchPersonRaceEthnicityReq searchPersonRaceEthnicityReq) {
		if (TypeConvUtil.isNullOrEmpty(searchPersonRaceEthnicityReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personRaceEthnicityService.searchPersonRaceEthnicity(searchPersonRaceEthnicityReq);
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * List/Detail window. Service Name : CCMN46S
	 * 
	 * @param phonereq
	 * @return PhoneRes
	 */
	@RequestMapping(value = "/getpersonphone", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes getPhoneDtls(@RequestBody PhoneReq phonereq) {
		if (null == phonereq.getIdPerson()) {
			throw new InvalidRequestException("Person Id can not be null");
		}
		PhoneRes phoneRes = new PhoneRes();
		phoneRes = personPhoneService.getPersonPhoneDetailList(phonereq);
		return phoneRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the Person Identifiers for
	 * a person List/Detail window. Service Name : CINT19S
	 * 
	 * @param retrvPersonIdentifiersReq
	 * @return RetrvPersonIdentifiersRes
	 */
	@RequestMapping(value = "/getpersonidentifiers", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RetrvPersonIdentifiersRes getPersonIdentifers(
			@RequestBody RetrvPersonIdentifiersReq retrvPersonIdentifiersReq) {
		if (null == retrvPersonIdentifiersReq.getIdPerson()) {
			throw new InvalidRequestException("Person Id can not be null");
		}
		if (!retrvPersonIdentifiersReq.getIndIntake().equals(ServiceConstants.STRING_IND_Y)
				&& !retrvPersonIdentifiersReq.getIndIntake().equals(ServiceConstants.STRING_IND_N)) {
			throw new InvalidRequestException("The Intake Indicator Should be 'Y' or 'N'");
		}
		if (retrvPersonIdentifiersReq.getIndIntake().equals(ServiceConstants.STRING_IND_Y)
				&& null == retrvPersonIdentifiersReq.getTsQuery()) {
			throw new InvalidRequestException(
					"The Date should be provided for Intake Stage when the Intake indicator is Y");
		}
		RetrvPersonIdentifiersRes retrvPersonIdentifiersRes = new RetrvPersonIdentifiersRes();
		retrvPersonIdentifiersRes = personIdService.getPersonIdentifiersDetailList(retrvPersonIdentifiersReq);
		return retrvPersonIdentifiersRes;
	}

	/**
	 * 
	 * Method Description: This Method will Add/Update/Delete the Person
	 * Identifiers for a person. Service Name : CINT23S
	 * 
	 * @param savePersonIdentifiersReq
	 * @return SavePersonIdentifiersRes
	 */
	@RequestMapping(value = "/savepersonidentifiers", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SavePersonIdentifiersRes savePersonIdentifers(
			@RequestBody SavePersonIdentifiersReq savePersonIdentifiersReq) {
		for (PersonIdentifiersDto personIdentifiersDto : savePersonIdentifiersReq.getSavePersonIdentifiersList()) {
			if (null == personIdentifiersDto.getPersonIdType()) {
				throw new InvalidRequestException("Person Id Type cannot be null");
			} else if (null == personIdentifiersDto.getPersonIdNumber()) {
				throw new InvalidRequestException("Person Id Number cannot be null");
			}
		}
		SavePersonIdentifiersRes savePersonIdentifiersRes = new SavePersonIdentifiersRes();
		savePersonIdentifiersRes = personIdService.savePersonIdentifiersDetail(savePersonIdentifiersReq);
		if (savePersonIdentifiersRes.getEnteredSSNExistsInd().equals(ServiceConstants.ARCHITECTURE_CONS_Y)) {
			throw new InvalidRequestException("Entered SSN Details for the Person already exists");
		}
		return savePersonIdentifiersRes;
	}

	/**
	 * 
	 * Method Description: get the placement event ID if the person has current
	 * AA medicaid; otherwise get a zero. Used as a yes/no, and if yes the event
	 * ID is used
	 * 
	 * @param ulIdPerson
	 *            - Person ID
	 * @return Long - Placement ID for the passed person ID
	 */
	@RequestMapping(value = "/getPlacementIdIfPersonHasAaMedicaid", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long getPlacementIdIfPersonHasAaMedicaid(@RequestBody CommonHelperReq commonHelperReq) {
		if (null == commonHelperReq.getIdPerson()) {
			throw new InvalidRequestException("Person Id cannot be null");
		}
		return personIdService.getPlacementIdIfPersonHasAaMedicaid(commonHelperReq.getIdPerson());
	}

	/**
	 * 
	 * Method Description: Updates the person identifier end date of the person
	 * 
	 * @param savePersonIdentifiersReq
	 * @return SavePersonIdentifiersRes
	 */
	@RequestMapping(value = "/updateIdType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ServiceResHeaderDto updateIdType(@RequestBody SavePersonIdentifiersReq savePersonIdentifiersReq) {
		for (PersonIdentifiersDto personIdentifiersDto : savePersonIdentifiersReq.getSavePersonIdentifiersList()) {
			if (null == personIdentifiersDto.getPersonIdType()) {
				throw new InvalidRequestException("Person Id Type cannot be null");
			} else if (null == personIdentifiersDto.getIdPerson()) {
				throw new InvalidRequestException("Person Id cannot be null");
			}
		}
		return personIdService.updateIdType(savePersonIdentifiersReq);
	}

	/**
	 * This service will update only the person search indicator on the stage
	 * person link table. ServiceName - CINV50S
	 * 
	 * @param updateSearchPersonIndReq
	 * @return
	 */
	@RequestMapping(value = "/updateSearchPersonInd", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public UpdateSearchPersonIndRes personSearchIndUpdate(
			@RequestBody UpdateSearchPersonIndReq updateSearchPersonIndReq) {
		if (TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.ReqFuncCd.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getIdStagePerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personListService.personSearchIndUpdate(updateSearchPersonIndReq);
	}

	/**
	 * Method Description: This Method will retrieve character details for
	 * person details screen
	 * 
	 * @param persCharsDtlReq
	 * @return
	 */
	@RequestMapping(value = "/getcharDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public GetPersCharsDtlRes getCharDtls(@RequestBody GetPersCharsDtlReq persCharsDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(persCharsDtlReq.getUidPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.getCharDtls(persCharsDtlReq);
	}

	/**
	 * Method Description: This Method is to update person potential duplicate
	 * record
	 * 
	 * @param persCharsDtlReq
	 * @return
	 */
	@RequestMapping(value = "/savePotentialdupinfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public UpdtPersPotentialDupRes savePersPotentialDupInfo(
			@RequestBody UpdtPersPotentialDupReq updtPersPotentialDupReq) {
		if (TypeConvUtil.isNullOrEmpty(updtPersPotentialDupReq.getPersPotentialDupDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(updtPersPotentialDupReq.getPersPotentialDupDto().getIdDupPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.savePersPotentialDupInfo(updtPersPotentialDupReq);
	}

	/**
	 * This is the AUD service for the Address List/Detail window. Service Name
	 * - CCMN44S
	 * 
	 * @param persCharsDtlReq
	 * @return
	 */
	@RequestMapping(value = "/editPersonAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EditPersonAddressRes editPersonAddress(@RequestBody EditPersonAddressReq editPersonAddressReq) {
		if (TypeConvUtil.isNullOrEmpty(editPersonAddressReq.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.ReqFuncCd.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(editPersonAddressReq.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personAddressService.editPersonAddressDetail(editPersonAddressReq);
	}

	/**
	 ** Method Description: This Method will retrieve information for populating
	 * Texas Law Enforcement Telecommunications System (TLETS) List window.
	 * Service Name : TLETS List
	 * 
	 * @param TletsReq
	 * @return TletsRes
	 * 
	 */
	@RequestMapping(value = "/gettletsList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TletsRes getTletsList(@RequestBody TletsReq tletsReq) {
		return tletsService.getTletsList(tletsReq);
	}

	/**
	 ** Method Description: This Method will retrieve information for populating
	 * Texas Law Enforcement Telecommunications System (TLETS) List window.
	 * Service Name : TLETS List
	 * 
	 * @param TletsReq
	 * @return TletsRes
	 * 
	 */
	@RequestMapping(value = "/gettletscheckDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TletsRes getTletsDetails(@RequestBody TletsReq tletsReq) {
		// Check for Mandatory input parameters
		if (TypeConvUtil.isNullOrEmpty(tletsReq.getTletsCheckId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("tletsCheck.tletscheckid.mandatory", null, Locale.US));
		}
		return tletsService.getTletsCheckDtl(tletsReq);
	}

	/**
	 ** Method Description: This Method will retrieve information for populating
	 * Texas Law Enforcement Telecommunications System (TLETS) List window.
	 * Service Name : TLETS Check
	 * 
	 * @param TletsReq
	 * @return TletsRes
	 * 
	 */
	@RequestMapping(value = "/savetlets", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TletsRes saveTletsDetails(@RequestBody TletsReq tletsReq) {
		// Check for Mandatory input parameters
		if (TypeConvUtil.isNullOrEmpty(tletsReq.getcReqFuncCd())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.reqIndicator.mandatory", null, Locale.US));
		}
		return tletsService.audTletsDetails(tletsReq);
	}

	/**
	 ** Method Description: This Method will retrieve information for populating
	 * the Extended Person List/Detail window. Service Name : Extended Person
	 * List
	 * 
	 * @param personDtlReq
	 * @return personExtRes
	 * 
	 */
	@RequestMapping(value = "/extpersondtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonExtRes getExtPersonList(@RequestBody PersonDtlReq personDtlReq) {
		// Check for Mandatory input parameters
		if (null == personDtlReq.getIdPerson()) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.getExtPersonList(personDtlReq);
	}

	/**
	 * This Service will retrieve the list of citizenship status for the person
	 * within the stage. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes
	 */
	@RequestMapping(value = "/getPersonCitizenship", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PersonListRes getPersonCitizenshipDtls(@RequestBody PersonListReq personListReq) {
		if (TypeConvUtil.isNullOrEmpty(personListReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("ejbservice.personlist.idstage.mandatory", null, Locale.US));
		return personListService.fetchPersonCitizenshipDtls(personListReq);
	}

	/**
	 * This Service will retrieve the case person list for the person
	 * information. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes
	 */
	@RequestMapping(value = "/getCasePersonList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonListRes getCasePersonList(@RequestBody PersonListReq personListReq) {
		return personListService.fetchCasePersonList(personListReq);
	}

	/**
	 * Method Name:getExtendedPersonList Method Description:This method returns
	 * list of extended case persons list related to a single person
	 * 
	 * @param caseExtendedPersonReq
	 * @return caseExtendedPersonRes
	 */
	@RequestMapping(value = "/getExtendedPersonList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)

	public  CaseExtendedPersonRes getExtendedPersonList(
			@RequestBody CaseExtendedPersonReq caseExtendedPersonReq) {
		CaseExtendedPersonRes caseExtendedPersonRes = new CaseExtendedPersonRes();
		if (TypeConvUtil.isNullOrEmpty(caseExtendedPersonReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("ejbservice.personlist.idperson.mandatory", null, Locale.US));
		caseExtendedPersonRes = personListService.fetchExtendedPersonList(caseExtendedPersonReq);
		return caseExtendedPersonRes;
	}

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@RequestMapping(value = "/getPersonAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDetlsRes getPersonAddress(@RequestBody PersonDetailsReq personDetailsReq) {
		PersonDetlsRes personDetlsRes = new PersonDetlsRes();
		if (TypeConvUtil.isNullOrEmpty(personDetailsReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personlist.idperson.mandatory", null, Locale.US));
		}
		PersonDto personAddr = personListService.fetchPersonAddress(personDetailsReq.getIdPerson());
		personDetlsRes.setPersonDto(personAddr);
		return personDetlsRes;
	}

	/**
	 * This Service will retrieve the date of Birth for a person Fetch DOB of a
	 * person from DB
	 * 
	 * @param ulIdPerson
	 * @return PersonDtlRes
	 */
	@RequestMapping(value = "/getDob", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonDtlRes getDob(@RequestBody PersonDtlReq personDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(personDtlReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.getDob(personDtlReq);
	}

	/**
	 * 
	 * Method Description: This Service calls one DAM to retrieve Name
	 * information about person off the Name Table based on ID PERSON. Also, set
	 * the input record field SYS IND VALID ONLY to FALSE if you want to
	 * retrieve only valid names, and TRUE otherwise. Tuxedo Service Name:
	 * CINV25S
	 * 
	 * @param nameHistoryDetailReq
	 * @return nameHistoryDetailRes
	 */
	@RequestMapping(value = "/namehistorydetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public NameHistoryDetailRes getNameHistoryDtl(@RequestBody NameHistoryDetailReq nameHistoryDetailReq) {
		NameHistoryDetailRes nameHistoryDetailRes = new NameHistoryDetailRes();
		if (TypeConvUtil.isNullOrEmpty(nameHistoryDetailReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		nameHistoryDetailRes = personDtlService.getNameHistoryDtl(nameHistoryDetailReq);
		return nameHistoryDetailRes;
	}

	/**
	 * 
	 * MethodName: getAddressList Method Description: This method is to get the
	 * List of address of a a Person from Person Address Table.
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes
	 */
	@RequestMapping(value = "/getaddresslist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AddressDtlRes getAddressList(@RequestBody AddressDtlReq addressDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(addressDtlReq.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personAddressService.getAddressList(addressDtlReq);
	}

	/**
	 * 
	 * Method Description: This Service calls a DAM to update Name information
	 * about a person on the Name Table and a DAM to update NM PERSON FULL on
	 * the Person Table. Tuxedo Service Name:CINV26S
	 * 
	 * @param updatenameHistoryDtlReq
	 * @return saveNameHistoryRes
	 */
	@RequestMapping(value = "/updatenamehistorydetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SaveNameHistoryRes updateNameHistoryDtl(@RequestBody SaveNameHistoryDtlReq updatenameHistoryDtlReq) {
		SaveNameHistoryRes saveNameHistoryRes = new SaveNameHistoryRes();
		//Commenting out this check for Defect 13762. When we navigate to PersonDetail from Person Search, the stage id will not be set.
		/*if (TypeConvUtil.isNullOrEmpty(updatenameHistoryDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}*/
		saveNameHistoryRes = personDtlService.updateNameHistoryDtl(updatenameHistoryDtlReq);
		if (TypeConvUtil.isNullOrEmpty(saveNameHistoryRes.getMessage())) {
			throw new ServiceLayerException(messageSource.getMessage("person.savenameHistory.data", null, Locale.US));
		}
		return saveNameHistoryRes;
	}

	/**
	 * 
	 * Method Description:This service is responsible for adding or updating
	 * information from the Person Characteristics window. Tuxedo Service
	 * Name:CINV34S
	 * 
	 * @param personCharReq
	 */
	@RequestMapping(value = "/personchar", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonCharacteristicsRes savePersonChar(@RequestBody PersonCharacteristicsReq personCharReq) {
		PersonCharacteristicsRes personCharRes = new PersonCharacteristicsRes();

		personCharRes = personDtlService.savePersonChar(personCharReq);
		if (TypeConvUtil.isNullOrEmpty(personCharRes.getMessage())) {
			throw new ServiceLayerException(messageSource.getMessage("person.personchar.data", null, Locale.US));
		}
		return personCharRes;
	}

	/**
	 * 
	 * MethodName: getAddressListPullback Method Description: This method is to
	 * get the List of address of a a all the persons in a stage.
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes
	 */
	@RequestMapping(value = "/getaddresslistpullback", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public AddressDtlRes getAddressListPullback(@RequestBody AddressDtlReq addressDtlReq) {
		/*
		 * if (TypeConvUtil.isNullOrEmpty(addressDtlReq.getUlIdStage())) { throw
		 * new InvalidRequestException(messageSource.getMessage(
		 * "common.stageid.mandatory", null, Locale.US)); }
		 */
		return personAddressService.getAddressListPullback(addressDtlReq);
	}

	/**
	 ** Method Description: This Method will perform Add and Update of
	 * PersonPhone table. Tuxedo Service Name : CCMN31S Dam: CCMN95D
	 * 
	 * @param PersonPhoneReq
	 * @return PersonPhoneRes
	 * 
	 */
	@RequestMapping(value = "/savepersonphone", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes savePersonPhoneDtls(@RequestBody PersonPhoneReq personPhoneReq) {
		// Check for Mandatory input parameters
		if (TypeConvUtil.isNullOrEmpty(personPhoneReq.getReqFuncCd())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.reqIndicator.mandatory", null, Locale.US));
		}
		return personPhoneService.savePersonPhoneDtls(personPhoneReq);
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * pullback window. Service Name : PhoneList Pullback EJB
	 * 
	 * @param phonereq
	 * @return PhoneRes
	 * 
	 */
	@RequestMapping(value = "/getpersonphonepullback", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes getPhonepullbackDtls(@RequestBody PhoneReq phonereq) {
		return personPhoneService.getPersonPhonePullback(phonereq);
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal History window. Service Name : (isCriminalHistoryComplete)
	 * 
	 * @param crimHistoryReq
	 * @return PhoneRes
	 * 
	 */
	@RequestMapping(value = "/isCrimHistoryComplete", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryNarrRes isCriminalHistoryComplete(@RequestBody CriminalHistoryReq crimHistoryReq) {
		CriminalHistoryNarrRes res = new CriminalHistoryNarrRes();
		boolean isCriminalHistoryComplete = personDtlService.isCriminalHistoryComplete(crimHistoryReq);
		res.setCrimHistNarrPresent(isCriminalHistoryComplete);
		return res;
	}

	/**
	 * 
	 * Method Description: This Method will update for the criminal History
	 * Accp/Rej window. Service Name : (updateRecordCheckIndAccptRej)
	 * 
	 * @param phonereq
	 * @return PhoneRes
	 * 
	 */
	@RequestMapping(value = "/recCheckIndAccRej", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes updateRecordCheckIndAccptRej(@RequestBody CriminalHistoryReq crimHistoryReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(crimHistoryReq.getIdRecCheck())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		commonHelperRes.setUpdateRecordCheckIndAccptRej(personDtlService.updateRecordCheckIndAccptRej(crimHistoryReq));
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal History window. Service Name :
	 * (isCrimHistNarrPresentForRecordCheck)
	 * 
	 * @param crimHistoryReq
	 * @return PhoneRes
	 */
	@RequestMapping(value = "/isCrimHistoryNarr", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryNarrRes isCrimHistNarrPresentForRecordCheck(@RequestBody CriminalHistoryReq crimHistoryReq) {
		CriminalHistoryNarrRes res = new CriminalHistoryNarrRes();
		boolean isCriminalHistoryComplete = personDtlService.isCrimHistNarrPresentForRecordCheck(crimHistoryReq);
		res.setCrimHistNarrPresent(isCriminalHistoryComplete);
		return res;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal History window. Service Name :
	 * (isCrimHistNarrPresentForRecordCheck)
	 * 
	 * @param crimHistoryReq
	 * @return PhoneRes
	 * 
	 */
	@RequestMapping(value = "/getCriminalHistNarr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryNarrRes getCriminalHistNarr(@RequestBody CriminalHistoryNarrReq criminalHistoryNarrReq) {
		CriminalHistoryNarrRes criminalHistoryNarrRes = new CriminalHistoryNarrRes();
		boolean crimHistNarrPresent = personDtlService.getCriminalHistNarr(criminalHistoryNarrReq);
		criminalHistoryNarrRes.setCrimHistNarrPresent(crimHistNarrPresent);
		return criminalHistoryNarrRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal History window. Service Name :
	 * (isCrimHistNarrPresentForRecordCheck)
	 * 
	 * @param crimHistoryReq
	 * @return PhoneRes
	 * 
	 */
	@RequestMapping(value = "/getPersonFullName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonDtlRes getPersonFullName(@RequestBody PersonDtlReq personDtlReq) {
		PersonDtlRes personDtlRes = new PersonDtlRes();
		String nmPersonFull = personDtlService.getPersonFullName(personDtlReq);
		personDtlRes.setNmPersonFull(nmPersonFull);
		return personDtlRes;
	}

	/**
	 * Method Name: getPersonPhoneNumber Method Description:
	 * 
	 * @param phoneReq
	 * @return PhoneRes
	 */
	@RequestMapping(value = "/getPersonPhoneNumber", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes getPersonFPPhoneNumber(@RequestBody PhoneReq phoneReq) {
		return personPhoneService.getPersonPhoneNumber(phoneReq);
	}

	/**
	 * Method Name: getPersonFPEmailAddress Method Description:This method is
	 * used to fetch the email address of type FP for a person
	 * 
	 * @param emailReq
	 * @return
	 */
	@RequestMapping(value = "/getPersonEmailAddress", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailRes getPersonFPEmailAddress(@RequestBody EmailReq emailReq) {
		return personEmailService.getPersonEmailAddress(emailReq);
	}

	@RequestMapping(value = "/getDtCreatedOfPaperRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getDtCreatedOfPaperRecord(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getDtCreatedOfPaperRecord(request);
	}

	@RequestMapping(value = "/getMostRecentCASAFPSIdRecCheck", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getMostRecentCASAFPSIdRecCheck(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getMostRecentCASAFPSIdRecCheck(request);
	}

	@RequestMapping(value = "/getPersonCasaProvisioned", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes isPersonCasaProvisioned(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.isPersonCasaProvisioned(request);
	}

	@RequestMapping(value = "/getRecordsCheckDocuments", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes retrieveRecordsCheckDocument(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.retrieveRecordsCheckDocument(request);
	}

	@RequestMapping(value = "/getRecordsCheckNotifications", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes retrieveRecordsCheckNotifications(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.retrieveRecordsCheckNotifications(request);
	}

	@RequestMapping(value = "/getRecordsCheckDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getRecordsCheckDetail(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getRecordsCheckDetail(request);
	}

	@RequestMapping(value = "/getABCSCheck", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes isABCSCheck(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.isABCSCheck(request);
	}

	@RequestMapping(value = "/getCasaFpsCheck", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes isCasaFpsCheck(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.isCasaFpsCheck(request);
	}

	@RequestMapping(value = "/getAbcsContractID", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getAbcsContractID(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getAbcsContractID(request);
	}

	/**
	 * Added to implement artf172936
	 * Method Description : This method is used to get the access data from ABCS database.
	 * If either IND_ACCESS_CRIMINAL_HISTORY or IND_ACCESS_IMPACT fields of ABCS FBI background check request process
	 * is set as 'Yes' the indicator chriAccess is set as true
	 * @param request
	 * @return RecordsCheckRes
	 */

	@RequestMapping(value = "/getAbcsAccessData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getAbcsAccessData(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getAbcsAccessData(request);
	}

	@RequestMapping(value = "/callDPSWSNameSearchProcedure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void callDPSWSNameSearchProcedure(@RequestBody RecordsCheckReq request) {
		recordsCheckService.callDPSWSNameSearchProcedure(request);
	}

	@RequestMapping(value = "/getPersonNameList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getPersonNameList(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getPersonNameList(request);
	}

	@RequestMapping(value = "/getNameValid", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes isNameValid(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.isNameValid(request);
	}

	@RequestMapping(value = "/hasPendingFingerprintCheck", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes hasPendingFingerprintCheck(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.hasPendingFingerprintCheck(request);
	}

	@RequestMapping(value = "/fetchEmailList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmailRes fetchEmailList(@RequestBody EmailReq request) {
		return personEmailService.fetchEmailList(request);
	}

	/**
	 * Method Name: getEmailList Method Description: this method will return
	 * list of email address for a giving person id
	 * 
	 * @param request
	 * @return PersonEmailRes
	 */
	@RequestMapping(value = "/getEmailList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonEmailRes getEmailList(@RequestBody PersonEmailReq request){
		return personEmailService.getEmailList(request);
	}

	@RequestMapping(value = "/updatePersonPhone", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes updatePersonPhone(@RequestBody PersonPhoneReq request) {
		PhoneRes phoneRes = new PhoneRes();
		personPhoneService.updatePersonPhone(request);
		return phoneRes;
	}

	@RequestMapping(value = "/updatePersonEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmailDetailRes updatePersonEmail(@RequestBody EmailDetailReq request) {
		EmailDetailRes emailRes = new EmailDetailRes();
		personEmailService.updatePersonEmail(request);
		return emailRes;
	}

	/**
	 * Method Name: updateEmail Method Description: this method will update
	 * email address
	 * 
	 * @param request
	 */
	@RequestMapping(value = "/updateEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmailDetailRes updateEmail(@RequestBody EmailDetailReq request){
		EmailDetailRes emailRes = new EmailDetailRes();
		personEmailService.updateEmail(request);
		return emailRes;
	}

	@RequestMapping(value = "/getScorContractNbr", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getScorContractNbr(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getScorContractNbr(request);
	}

	/**
	 * Method Name: getPersonFPPhoneList Method Description:
	 * 
	 * @param phonereq
	 * @return phoneRes
	 */
	@RequestMapping(value = "/getpersonfpphonelist", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PhoneRes getPersonFPPhoneList(@RequestBody PhoneReq phonereq) {
		if (null == phonereq.getIdPerson()) {
			throw new InvalidRequestException("Person Id can not be null");
		}
		PhoneRes phoneRes = new PhoneRes();
		phoneRes = personPhoneService.getPersonFPPhoneDetailList(phonereq);
		return phoneRes;
	}

	@RequestMapping(value = "/hasCurrentPrimaryAddress", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public AddressDtlRes hasCurrentPrimaryAddress(@RequestBody AddressDtlReq addressReq) {
		AddressDtlRes addressResponse = new AddressDtlRes();
		addressResponse = personDtlService.hasCurrentPrimaryAddress(addressReq);
		return addressResponse;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve all rows from the Education
	 * History Table for a given ID_PERSON. Service Name: CCFC17S
	 * 
	 * @param educationHistoryReq
	 * @return educationHistoryRes
	 */
	@RequestMapping(value = "/getPersonEducationHistoryList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EducationHistoryRes getPersonEducationHistoryList(@RequestBody EducationHistoryReq educationHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(educationHistoryReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return educationHistoryService.getPersonEducationHistoryList(educationHistoryReq);
	}

	/**
	 *
	 * Method Description: This Method will retrieve all rows from the training
	 * History Table for a given ID_PERSON. Service Name:
	 *
	 * @param homeMembTrainRtrvReq
	 * @return HomeMembTrainRtrvRes
	 */
	@RequestMapping(value = "/getPersonTrainingHistoryList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public HomeMembTrainRtrvRes getPersonTrainingHistoryList(@RequestBody HomeMembTrainRtrvReq homeMembTrainRtrviDto) {
		if (TypeConvUtil.isNullOrEmpty(homeMembTrainRtrviDto.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return faIndivTrainingService.getFAHomeMemberTrainingList(homeMembTrainRtrviDto);
	
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the
	 * criminal History window. Service Name :
	 * (isCrimHistNarrPresentForRecordCheck)
	 * 
	 * @param crimHistoryReq
	 * @return PhoneRes
	 */
	@RequestMapping(value = "/getRejectCriminalHistList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryNarrRes getRejectCrimHistListForPerson(
			@RequestBody CriminalHistoryNarrReq criminalHistoryNarrReq) {
		CriminalHistoryNarrRes criminalHistoryNarrRes = new CriminalHistoryNarrRes();
		List<Long> criminalHistId = personDtlService.getRejectCriminalHistList(criminalHistoryNarrReq);
		criminalHistoryNarrRes.setCrimHistId(criminalHistId);
		return criminalHistoryNarrRes;
	}

	@RequestMapping(value = "/validatePersonMerge", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PersonMergeSplitRes validatePersonMerge(@RequestBody PersonMergeSplitReq personMergeSplitReq){
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writeValueAsString(personMergeSplitReq);
			log.info("Method : splitPersonMerge - request :" + json);
		} catch (JsonProcessingException e) {
		}
		PersonMergeSplitRes personMergeSplitRes = new PersonMergeSplitRes();
		PersonMergeSplitDto personMergeSplitDto = personDtlService
				.validatePersonMerge(personMergeSplitReq.getPersonMergeSplitDto());
		personMergeSplitRes.setPersonMergeSplitDto(personMergeSplitDto);
		return personMergeSplitRes;
	}

	@RequestMapping(value = "/getPersonMergeInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonMergeSplitRes getPersonMergeInfo(@RequestBody PersonMergeSplitReq personMergeSplitReq) {
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writeValueAsString(personMergeSplitReq);
			log.info("Method : splitPersonMerge - request :" + json);
		} catch (JsonProcessingException e) {
		}
		PersonMergeSplitRes personMergeSplitRes = new PersonMergeSplitRes();
		PersonMergeSplitDto personMergeSplitDto = personDtlService.getPersonMergeInfo(personMergeSplitReq);
		personMergeSplitRes.setPersonMergeSplitDto(personMergeSplitDto);
		return personMergeSplitRes;
	}

	/**
	 * 
	 * @param retrieveGroupUpdateListReq
	 * @return groupUpdateListRes
	 */
	// GroupUpdateList
	@RequestMapping(value = "/groupUpdatelist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public GroupUpdateRes getGroupUpdateList(@RequestBody PersonListReq retrieveGroupUpdateListReq) {
		if (null == (retrieveGroupUpdateListReq.getIdStage()) && null == (retrieveGroupUpdateListReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		GroupUpdateRes groupUpdateListRes = new GroupUpdateRes();
		groupUpdateListRes = personListService.getGroupUpdateList(retrieveGroupUpdateListReq);

		return groupUpdateListRes;
	}

	/**
	 * 
	 * @param saveGroupUpdateReq
	 * @return groupUpdateListRes
	 */
	// saveGroupUpdate
	@RequestMapping(value = "/saveGroupUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public GroupUpdateRes saveGroupUpdate(@RequestBody GroupUpdateReq saveGroupUpdateReq) {
		if (null == (saveGroupUpdateReq.getGroupUpdateDtoList())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		GroupUpdateRes groupUpdateListRes = new GroupUpdateRes();
		groupUpdateListRes = personListService.saveGroupUpdate(saveGroupUpdateReq);
		if (null == groupUpdateListRes.getMessage()) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}
		return groupUpdateListRes;
	}

	/**
	 * Method Description: This method will perform address mass update.
	 * 
	 * @param addressMassUpdateReq
	 * @return AddressMassUpdateRes
	 */
	@RequestMapping(value = "/addressMassUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AddressMassUpdateRes updateAddress(@RequestBody AddressMassUpdateReq addressMassUpdateReq) {
		AddressMassUpdateRes addressMassUpdateRes = new AddressMassUpdateRes();

		if (TypeConvUtil.isNullOrEmpty(addressMassUpdateReq.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.ReqFuncCd.mandatory", null, Locale.US));
		}
		if (addressMassUpdateReq.getPersonListDtoList().size() == 0) {
			throw new InvalidRequestException(messageSource.getMessage("person.personList.mandatory", null, Locale.US));
		}
		addressMassUpdateRes = personAddressService.massAddrUpdate(addressMassUpdateReq);
		return addressMassUpdateRes;
	}

	/**
	 * Method Description: This method will perform phone mass update.
	 * 
	 * @param phoneMassUpdateReq
	 * @return PhoneMassUpdateRes
	 */
	@RequestMapping(value = "/phoneMassUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PhoneMassUpdateRes massUpdatePhone(@RequestBody PhoneMassUpdateReq phoneMassUpdateReq) {
		PhoneMassUpdateRes phoneMassUpdateRes = new PhoneMassUpdateRes();

		if (phoneMassUpdateReq.getPersonPhoneRetDtoList().size() == 0) {
			throw new InvalidRequestException(messageSource.getMessage("person.personList.mandatory", null, Locale.US));
		}
		phoneMassUpdateRes = personPhoneService.phoneMassUpdate(phoneMassUpdateReq);
		return phoneMassUpdateRes;
	}

	/**
	 * Method Description:This method is used to get the Current residential
	 * address for a person based on person Id
	 * 
	 * @param phonereq
	 * @return AddressDtlRes
	 *
	 */
	@RequestMapping(value = "/homeAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AddressDtlRes getPersonAddressDtls(@RequestBody AddressDtlReq addressDtlReq) {
		if (null == addressDtlReq || TypeConvUtil.isNullOrEmpty(addressDtlReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.personId.mandatory", null, Locale.US));
		}
		AddressDtlRes addressDtlRes = personAddressService.getPersonAddressDtls(addressDtlReq);
		return addressDtlRes;
	}

	/**
	 * Method Description:This method is used to get the Current residential
	 * address for a person based on person Id
	 * 
	 * @param phonereq
	 * @return
	 */
	@RequestMapping(value = "/activeAddressForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public AddressDtlRes getActiveAddressForStage(@RequestBody AddressDtlReq addressDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(addressDtlReq.getUlIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("person.stageid.mandatory", null, Locale.US));
		}
		AddressDtlRes addressDtlRes = personAddressService.getActiveAddressForStage(addressDtlReq);
		return addressDtlRes;
	}

	/**
	 * Method Description: This method will get the duplicate persons meeting
	 * the criteria. Service Name: Duplicate Alert on Person Detail Screen.
	 * 
	 * @param DuplicatePersonsReq
	 * @return DuplicatePersonsRes
	 */
	@RequestMapping(value = "/duplicatePersons", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public DuplicatePersonsRes getDuplicatePerson(@RequestBody DuplicatePersonsReq duplicatePersonsReq) {
		DuplicatePersonsRes duplicatePersonsRes = new DuplicatePersonsRes();
		if (TypeConvUtil.isNullOrEmpty(duplicatePersonsReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		duplicatePersonsRes = personDtlService.getDuplicates(duplicatePersonsReq);
		return duplicatePersonsRes;
	}

	@RequestMapping(value = "/indAbuseNglctDeathInCare", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes saveIndAbuseNglctDeathInCare(@RequestBody CommonHelperReq personIndAbuse) {
		CommonHelperRes saveIndPersonAbuse = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(personIndAbuse.getIdPerson())
				&& TypeConvUtil.isNullOrEmpty(personIndAbuse.getIndicatorAbuse())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personIndAbuse.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		saveIndPersonAbuse = personDtlService.saveIndAbuseNglctDeathInCare(personIndAbuse);
		return saveIndPersonAbuse;
	}

	@RequestMapping(value = "/getIndAbuseNglctDeathInCare", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getIndAbuseNglctDeathInCare(@RequestBody CommonHelperReq person) {
		CommonHelperRes getIndPersonAbuse = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(person.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		getIndPersonAbuse = personDtlService.getIndAbuseNglctDeathInCare(person);
		return getIndPersonAbuse;
	}

	@ApiOperation(value = "Additional information for phonetic search", tags = { "personsearch" })
	@RequestMapping(value = "/infoIntakeSearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PrsnSrchListpInitRes populateAddtnlInfoIntakeSearch(@RequestBody PrsnSrchListpInitReq personSearchList) {
		if (TypeConvUtil.isNullOrEmpty(personSearchList)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		PrsnSrchListpInitRes personSearchInfoIntake = new PrsnSrchListpInitRes();
		personSearchInfoIntake = personDtlService.populateAddtnlInfoIntakeSearch(personSearchList);
		return personSearchInfoIntake;
	}

	@RequestMapping(value = "/saveCriminalHistoryAndNarrative", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryRes saveCriminalHistoryAndNarrative(@RequestBody CriminalHistoryReq crimialHistoryRequest){
		if (TypeConvUtil.isNullOrEmpty(crimialHistoryRequest.getCriminalHistoryValueBean())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CriminalHistoryRes response = new CriminalHistoryRes();
		response = criminalHistoryService.saveCriminalHistoryNarrative(
				crimialHistoryRequest.getCriminalHistoryValueBean(), crimialHistoryRequest.getResults());
		return response;
	}

	@RequestMapping(value = "/generateFBIEligibleExhireEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generateFBIEligibleExhireEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generateFBIEligibleExhireEmail(request);
	}


	@RequestMapping(value = "/generateEligibleEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generateEligibleEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generateEligibleEmail(request);
	}

	@RequestMapping(value = "/generatePCSEligibleEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generatePCSEligibleEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generatePCSEligibleEmail(request);
	}

	@RequestMapping(value = "/generatePCSIneligibleEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generatePCSInnEligibleEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generatePCSInEligibleEmail(request);
	}

	@RequestMapping(value = "/generateIneligibleEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generateIneligibleEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generateIneligibleEmail(request);
	}

	@RequestMapping(value = "/generatePSClearanceEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmailNotificationsRes generatePSClearanceEmail(@RequestBody EmailNotificationsReq request) {
		return recordsCheckService.generatePSClearanceEmail(request);
	}

	@RequestMapping(value = "/hasEmailSent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes hasEmailSent(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.hasEmailSent(request);
	}

	@RequestMapping(value = "/getRecordDocumentTsLastUpdate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getRecordDocumentTsLastUpdate(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getRecordDocumentTsLastUpdate(request);
	}

	@RequestMapping(value = "/deleteDocumentPdbRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes deleteDocumentPdbRecord(@RequestBody RecordsCheckDetailReq request) {
		RecordsCheckRes recordsCheckRes = new RecordsCheckRes();
		try {
			recordsCheckService.deleteDocumentPdbRecord(request);
		} catch (Exception e) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.ordinal());
			errorDto.setErrorMsg(e.getMessage());
			recordsCheckRes.setErrorDto(errorDto);
		}
		return recordsCheckRes;
	}

	/**
	 * Method Name: isPersonInfoViewable Method Description: Check if the
	 * Person's Information is Viewable or Not
	 * 
	 * @param isPersonReq
	 * @return IsPersonRes
	 */
	@RequestMapping(value = "/isPersonInfoViewable", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IsPersonRes isPersonInfoViewable(@RequestBody IsPersonReq isPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(isPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		return personDtlService.isPersonInfoViewable(isPersonReq);
	}

	/**
	 * Method Name: isPersonInfoViewable Method Description: Check if the
	 * Person's Information is Viewable or Not
	 * 
	 * @param isPersonReq
	 * @return IsPersonRes
	 */
	@ApiOperation(value = "Get person details", tags = { "identity" })
	@RequestMapping(value = "/getPersonDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDto getPersonDetails(@RequestBody CommonHelperReq personReq) {
		if (TypeConvUtil.isNullOrEmpty(personReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		return personDtlService.getPersonDetail(personReq);
	}

	/**
	 * Method Name: getPersonPca Method Description: Get the name of the person
	 * 
	 * @param PersonDtlRes
	 * @return PersonReq
	 */
	@RequestMapping(value = "/getPcaPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDtlRes getPersonPca(@RequestBody CommonHelperReq personReq) {
		if (TypeConvUtil.isNullOrEmpty(personReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		return personDtlService.getPersonPca(personReq.getIdPerson());
	}

	/**
	 * Method Name: fetchPersonPotentialDuplicates Method Description: Retrieves
	 * list of potential Duplicate and other information related to a person.
	 * 
	 * @param personDupReq
	 * @return PersonDupRes
	 */
	@RequestMapping(value = "/fetchPersonPotentialDuplicates", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PotentialDupRes fetchPersonPotentialDuplicates(
			@RequestBody PotentialDupReq potentialDupListReq) {
		if (TypeConvUtil.isNullOrEmpty(potentialDupListReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		return personDtlService.fetchPersonPotentialDuplicates(potentialDupListReq);
	}

	/**
	 * Method Name: fetchPersonPotentialDuplicate Method Description:information
	 * related to a person given Person Potential Duplicate ID
	 * 
	 * @param personDupReq
	 * @return PersonDupRes
	 */
	@RequestMapping(value = "/fetchPersonPotentialDuplicate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PotentialDupRes fetchPersonPotentialDuplicate(@RequestBody PotentialDupReq potentialDupReq) {
		if (TypeConvUtil.isNullOrEmpty(potentialDupReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		return personDtlService.fetchPersonPotentialDuplicate(potentialDupReq);
	}

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isAssmntPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IsPersonRes isAssmntPerson(@RequestBody IsPersonReq isPersonReq) {
		log.debug("Entering method isAssmntPerson in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(isPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		IsPersonRes isPersonRes = new IsPersonRes();
		isPersonRes.setResult(personIdService.isAssmntPerson(isPersonReq.getIdPerson(), isPersonReq.getStageId()));
		isPersonRes.setTransactionId(isPersonReq.getTransactionId());
		log.debug("Exiting method isAssmntPerson in PersonUtilityController");
		return isPersonRes;
	}

	/**
	 * Method Name: fetchActivePersonPotentialDuplicate Method Description:
	 * Retrieve active potential Duplicate and other information related to a
	 * person.
	 * 
	 * @param personActiveDupReq
	 * @return PersonActiveDupRes
	 */
	@RequestMapping(value = "/fetchActivePersonPotentialDuplicate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PotentialDupRes fetchActivePersonPotentialDuplicate(
			@RequestBody PotentialDupReq personActiveDupReq) {
		if (TypeConvUtil.isNullOrEmpty(personActiveDupReq.getIdPerson())
				&& TypeConvUtil.isNullOrEmpty(personActiveDupReq.getIdDupPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.idPerson.idDupPerson.mandatory", null, Locale.US));
		}
		return personDtlService.fetchActivePersonPotentialDuplicate(personActiveDupReq);
	}

	/**
	 * Method Name: getForwardPersonInMergeRes Method Description:This method
	 * checks if the person is closed person in a merge
	 * 
	 * @param getForwardPersonInMergeReq
	 * @return GetForwardPersonInMergeRes
	 */
	@RequestMapping(value = "/getForwardPersonInMerge", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  GetForwardPersonInMergeRes getForwardPersonInMerge(
			@RequestBody GetForwardPersonInMergeReq getForwardPersonInMergeReq) {
		if (TypeConvUtil.isNullOrEmpty(getForwardPersonInMergeReq.getIdClosedPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.idClosedPerson.mandatory", null, Locale.US));
		}
		GetForwardPersonInMergeRes getForwardPersonInMergeRes = new GetForwardPersonInMergeRes();
		getForwardPersonInMergeRes.setIdFwdPerson(
				personDtlService.getForwardPersonInMerge(getForwardPersonInMergeReq.getIdClosedPerson()));
		return getForwardPersonInMergeRes;
	}

	/**
	 * 
	 * Method Name: isPersonIRReport Method Description:This method checks if
	 * the person is in IR Report
	 * 
	 * @param PotentialDupReq
	 * @return PersonIRReportRes
	 */
	@RequestMapping(value = "/isPersonIRReport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonIRReportRes isPersonIRReport(@RequestBody PotentialDupReq personIRReportReq) {

		if (TypeConvUtil.isNullOrEmpty(personIRReportReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		PersonIRReportRes personIRReportRes = new PersonIRReportRes();
		personIRReportRes.setExists(personDtlService.isPersonIRReport(personIRReportReq.getIdPerson()));
		return personIRReportRes;
	}

	/**
	 * Method Name: isPersonIRReport Method Description:This Service checks if
	 * all persons have an open placement with same adult or resource.
	 * 
	 * @param CommonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/isPersPlcmtWithSameCareGiver", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isPersPlcmtWithSameCareGiver(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getPersonIds())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsPersPlcmtWithSameCareGiver(
				personListService.isPersPlcmtWithSameCareGiver(commonHelperReq.getPersonIds()));
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: getDayCarePersonIdList Method Description: this method
	 * returns Person Ids for the given Day Care Request.
	 * 
	 * @param CommonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getDayCarePersonIdList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getDayCarePersonIdList(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setPersonIds(personListService.getDayCarePersonIdList(commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: isPersAtSameAddr Method Description:This service checks if
	 * all persons reside at the same location address
	 * 
	 * @param CommonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/isPersAtSameAddr", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isPersAtSameAddr(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getPersonIds())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsPersAtSameAddr(personListService.isPersAtSameAddr(commonHelperReq.getPersonIds()));
		return commonHelperRes;
	}

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param isPersonReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isPlcmntPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IsPersonRes isPlcmntPerson(@RequestBody IsPersonReq isPersonReq) {
		log.debug("Entering method isPlcmntPerson in personController");
		if (TypeConvUtil.isNullOrEmpty(isPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		IsPersonRes isPersonRes = new IsPersonRes();
		isPersonRes.setResult(personIdService.isPlcmntPerson(isPersonReq.getIdCase(), isPersonReq.getIdPerson(),
				(isPersonReq.getCdStage())));
		isPersonRes.setTransactionId(isPersonReq.getTransactionId());
		log.debug("Exiting method isPlcmntPerson in PersonController");
		return isPersonRes;
	}

	/**
	 * Method Description: This Method will retrieve the Person Identifiers for
	 * a person List/Detail window.
	 * 
	 * @param retrvPersonIdentifiersReq
	 * @return RetrvPersonIdentifiersRes
	 */
	@RequestMapping(value = "/getpersonidentifierbyidtype", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RetrvPersonIdentifiersRes getPersonIdentifierByIdType(
			@RequestBody RetrvPersonIdentifiersReq retrvPersonIdentifiersReq) {
		if (!ObjectUtils.isEmpty(retrvPersonIdentifiersReq)
				&& ObjectUtils.isEmpty(retrvPersonIdentifiersReq.getIdPerson())) {
			throw new InvalidRequestException("Person Id can not be null");
		}
		if (!ObjectUtils.isEmpty(retrvPersonIdentifiersReq)
				&& ObjectUtils.isEmpty(retrvPersonIdentifiersReq.getIdType())) {
			throw new InvalidRequestException("The person id type can not be null");
		}
		RetrvPersonIdentifiersRes retrvPersonIdentifiersRes = new RetrvPersonIdentifiersRes();
		try {
			retrvPersonIdentifiersRes = personIdService.getPersonIdentifierByIdType(retrvPersonIdentifiersReq);
		} catch (Exception ex) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(ex.toString());
			serviceLayerException.initCause(ex);
			throw serviceLayerException;
		}
		return retrvPersonIdentifiersRes;
	}

	/**
	 * Method Name: getPersonCharList Method Description:Get Person
	 * Characteristics for input person id and Category Type
	 * 
	 * @param personCharsReq
	 * @return PersonCharRes
	 */
	@RequestMapping(value = "/getPersonCharListById", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonCharRes getPersonCharList(@RequestBody PersonCharsReq personCharsReq) {

		if (TypeConvUtil.isNullOrEmpty(personCharsReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personCharsReq.getCdCharCategory())) {
			throw new InvalidRequestException(
					messageSource.getMessage("Person.cdCharCategory.mandatory", null, Locale.US));
		}

		PersonCharRes personCharRes = new PersonCharRes();
		personCharRes.setPersonCharDtos(
				personDtlService.getPersonCharList(personCharsReq.getIdPerson(), personCharsReq.getCdCharCategory()));
		return personCharRes;
	}

	/**
	 * Method Name: getPersonCharList Method Description:Retrieve
	 * characteristics for given person ID, in given category from snapshot
	 * table (SS_CHARACTERISTICS)
	 * 
	 * @param eduNeedListReq
	 * @return PersonCharRes
	 * 
	 */
	@RequestMapping(value = "/getPersonCharList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonCharRes getPersonCharList(@RequestBody EduNeedListReq personCharListReq) {

		if (TypeConvUtil.isNullOrEmpty(personCharListReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personCharListReq.getCdCharCategory())) {
			throw new InvalidRequestException(
					messageSource.getMessage("Person.cdCharCategory.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personCharListReq.getIdReferenceData())) {
			throw new InvalidRequestException(
					messageSource.getMessage("Person.idReferenceData.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personCharListReq.getCdActionType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("Person.cdActionType.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personCharListReq.getCdSnapshotType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("Person.cdSnapshotType.mandatory", null, Locale.US));
		}

		PersonCharRes personCharRes = new PersonCharRes();
		personCharRes.setPersonCharDtos(personDtlService.getPersonCharList(personCharListReq.getIdPerson(),
				personCharListReq.getCdCharCategory(), personCharListReq.getIdReferenceData(),
				personCharListReq.getCdActionType(), personCharListReq.getCdSnapshotType()));
		return personCharRes;
	}

	/**
	 * Method Name: getPersonPrimaryEmailRes Method Description:Fetches the
	 * primary email for the person
	 * 
	 * @param getPersonPrimaryEmailReq
	 * @return GetPersonPrimaryEmailRes
	 * 
	 */
	@RequestMapping(value = "/getPersonPrimaryEmailRes", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  GetPersonPrimaryEmailRes getPersonPrimaryEmailRes(
			@RequestBody GetPersonPrimaryEmailReq getPersonPrimaryEmailReq) {
		if (TypeConvUtil.isNullOrEmpty(getPersonPrimaryEmailReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		GetPersonPrimaryEmailRes getPersonPrimaryEmailRes = new GetPersonPrimaryEmailRes();
		getPersonPrimaryEmailRes
				.setPersonEmailValueDto(personDtlService.getPersonPrimaryEmail(getPersonPrimaryEmailReq.getIdPerson()));

		return getPersonPrimaryEmailRes;
	}

	/**
	 * 
	 * Method Name: getPersonPrimaryEmail Method Description:GET PERSON BY EMAIL
	 * 
	 * @param personStaffReq
	 * @return
	 * 
	 * 
	 */
	@RequestMapping(value = "/getPersonPrimaryEmail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonStaffRes getPersonPrimaryEmail(@RequestBody PersonStaffReq personStaffReq) {

		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdActionType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdActionType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdSnapshotType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdSnapshotType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdPerson", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdReferenceData())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdReferenceData", null, Locale.US));
		}
		PersonStaffRes personStaffRes = new PersonStaffRes();
		personStaffRes.setPersonEmailValueDto(personDtlService.getPersonPrimaryEmail(personStaffReq.getCdActionType(),
				personStaffReq.getCdSnapshotType(), personStaffReq.getIdPerson(), personStaffReq.getIdReferenceData()));
		return personStaffRes;
	}

	/**
	 * Method Name: selectAllRelationsOfPersonRes Method Description:Get All
	 * Family Tree RelationShip for person
	 * 
	 * @param selectAllRelationsOfPersonReq
	 * @return SelectAllRelationsOfPersonRes
	 */
	@RequestMapping(value = "/selectAllRelationsOfPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SelectAllRelationsOfPersonRes selectAllRelationsOfPersonRes(
			@RequestBody SelectAllRelationsOfPersonReq selectAllRelationsOfPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(selectAllRelationsOfPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		SelectAllRelationsOfPersonRes selectAllRelationsOfPersonRes = new SelectAllRelationsOfPersonRes();
		selectAllRelationsOfPersonRes.setFtPersonRelationDtoList(
				personDtlService.selectAllRelationsOfPerson(selectAllRelationsOfPersonReq.getIdPerson()));

		return selectAllRelationsOfPersonRes;
	}

	/**
	 * 
	 * Method Name: selectAllRelationsOfPerson Method Description:this method
	 * select all relations of person.
	 * 
	 * @param personStaffReq
	 * @return
	 * 
	 * 
	 */
	@RequestMapping(value = "/selectAllRelationsPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonStaffRes selectAllRelationsOfPerson(@RequestBody PersonStaffReq personStaffReq) {

		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdActionType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdActionType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdSnapshotType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdSnapshotType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdPerson", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdReferenceData())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdReferenceData", null, Locale.US));
		}

		PersonStaffRes personStaffRes = new PersonStaffRes();
		personStaffRes.setfTPersonRelationDtoList(personDtlService.selectAllRelationsOfPerson(
				personStaffReq.getCdActionType(), personStaffReq.getCdSnapshotType(), personStaffReq.getIdPerson(),
				personStaffReq.getIdReferenceData()));
		return personStaffRes;
	}

	/**
	 * Method Name: getPersonPrimaryActivePhone Method Description: This method
	 * gets person primary phone for input person id
	 * 
	 * @param personPhoneReq
	 * @return phoneRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/getPersonPrimaryActivePhone", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PhoneRes getPersonPrimaryActivePhone(@RequestBody PersonPhoneReq personPhoneReq) {
		if (TypeConvUtil.isNullOrEmpty(personPhoneReq.getUlIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonPhoneReq.UlIdPerson", null, Locale.US));
		}
		PhoneRes phoneRes = new PhoneRes();
		PersonPhoneRetDto phoneDto = personPhoneService.getPersonPrimaryActivePhone(personPhoneReq.getUlIdPerson());
		phoneRes.setPhoneDto(phoneDto);
		return phoneRes;
	}

	/**
	 * Method Name: fetchPersonRaceList Method Description: Get Person Race for
	 * input person id
	 * 
	 * @param personRaceReq
	 * @return PersonRaceRes
	 */
	@RequestMapping(value = "/fetchPersonRaceList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonRaceRes fetchPersonRaceList(@RequestBody PersonRaceReq personRaceReq) {
		if (TypeConvUtil.isNullOrEmpty(personRaceReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		PersonRaceRes personRaceRes = new PersonRaceRes();
		personRaceRes.setPersonRaceDtoList(personDtlService.fetchPersonRaceList(personRaceReq.getIdPerson()));
		return personRaceRes;
	}

	/**
	 * 
	 * Method Name: getPersonPrimaryActivePhone Method Description:Reads the
	 * current primary phone for a person from snapshot table (SS_PERSON_PHONE)
	 * ( For example: This method is used for displaying the Select Forward
	 * person details in post person merge page)
	 * 
	 * @param personStaffReq
	 * @return
	 * 
	 */
	@RequestMapping(value = "/getPersonPrimaryActivePhone1", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PhoneRes getPersonPrimaryActivePhone1(@RequestBody PersonStaffReq personStaffReq) {

		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdActionType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdActionType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdSnapshotType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdSnapshotType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdPerson", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdReferenceData())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdReferenceData", null, Locale.US));
		}
		PhoneRes personPrimaryRes = new PhoneRes();
		personPrimaryRes.setPhoneDto(personPhoneService.getPersonPrimaryActivePhone(personStaffReq.getCdActionType(),
				personStaffReq.getCdSnapshotType(), personStaffReq.getIdPerson(), personStaffReq.getIdReferenceData()));
		return personPrimaryRes;
	}

	/**
	 * Method Name: getCurrentEducationHistoryById Method Description: This
	 * method gets current Education for input person id
	 * 
	 * @param personStaffReq
	 * @return personStaffRes
	 * 
	 */
	@RequestMapping(value = "/getCurrentEducationHistoryById", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonStaffRes getCurrentEducationHistoryById(@RequestBody PersonStaffReq personStaffReq){
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdPerson", null, Locale.US));
		}
		PersonStaffRes personStaffRes = new PersonStaffRes();
		personStaffRes
				.setEducationHistoryDto(personDtlService.getCurrentEducationHistoryById(personStaffReq.getIdPerson()));

		return personStaffRes;
	}

	/**
	 * 
	 * Method Name: getCurrentEducationHistory Method
	 * Description:getCurrentEducationHistory
	 * 
	 * @param personStaffReq
	 * @return
	 * 
	 * 
	 */
	@RequestMapping(value = "/getCurrentEducationHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonStaffRes getCurrentEducationHistory(@RequestBody PersonStaffReq personStaffReq){

		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdActionType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdActionType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getCdSnapshotType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.CdSnapshotType", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdPerson", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personStaffReq.getIdReferenceData())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.PersonStaffReq.IdReferenceData", null, Locale.US));
		}
		PersonStaffRes personStaffRes = new PersonStaffRes();
		personStaffRes.setEducationHistoryDto(personDtlService.getCurrentEducationHistory(
				personStaffReq.getCdActionType(), personStaffReq.getCdSnapshotType(), personStaffReq.getIdPerson(),
				personStaffReq.getIdReferenceData()));
		return personStaffRes;
	}

	/**
	 * Method Name: getEducationalNeedListForHist Method Description: This
	 * method fetches the education need records for a education history record
	 * 
	 * @param educationNeedListReq
	 * @return EducationNeedListRes
	 * 
	 */
	@RequestMapping(value = "/getEducationalNeedListForHist", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EducationNeedListRes getEducationalNeedListForHist(
			@RequestBody EducationNeedListReq educationNeedListReq) {
		if (TypeConvUtil.isNullOrEmpty(educationNeedListReq.getIdEduHist())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idEduHist.mandatory", null, Locale.US));
		}
		EducationNeedListRes educationNeedListRes = new EducationNeedListRes();
		educationNeedListRes.setEducationalNeedDtoList(
				personDtlService.getEducationalNeedListForHist(educationNeedListReq.getIdEduHist()));
		educationNeedListRes.setTransactionId(educationNeedListReq.getTransactionId());
		return educationNeedListRes;
	}

	/**
	 * 
	 * Method Name: getPersonCategoryList Method Description: Get Person
	 * Category List for input person
	 * 
	 * @param personDtlReq
	 * @return PersonCategoryRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/getPersonCategoryList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonCategoryRes getPersonCategoryList(@RequestBody PersonDtlReq personDtlReq){

		PersonCategoryRes personCategoryRes = new PersonCategoryRes();
		if (TypeConvUtil.isNullOrEmpty(personDtlReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		personCategoryRes.setPersonCategoryDto(personDtlService.getPersonCategoryList(personDtlReq.getIdPerson()));
		return personCategoryRes;

	}

	/**
	 * Method Name: isPrimaryChildInOpenStage Method Description: Checks if the
	 * person is a Primary Child (PC) in an open stage
	 * 
	 * @param primaryChildReq
	 * @return PrimaryChildRes
	 */
	@RequestMapping(value = "/isPrimaryChildInOpenStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PrimaryChildRes isPrimaryChildInOpenStage(@RequestBody PrimaryChildReq primaryChildReq){
		if (TypeConvUtil.isNullOrEmpty(primaryChildReq.getIdPerson())
				&& TypeConvUtil.isNullOrEmpty(primaryChildReq.getChkStages())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.idPerson.ChkStages.mandatory", null, Locale.US));
		}
		PrimaryChildRes primaryChildRes = new PrimaryChildRes();
		primaryChildRes.setStatus(personDtlService.isPrimaryChildInOpenStage(primaryChildReq.getIdPerson().intValue(),
				primaryChildReq.getChkStages()));
		primaryChildRes.setTransactionId(primaryChildReq.getTransactionId());
		return primaryChildRes;
	}

	/**
	 * 
	 * Method Description:This is the form service for the Extended Person List
	 * form. Tuxedo Service Name: CPER02S
	 * 
	 * @param personDtlReq
	 * @return personDtlRes
	 */
	// CPER02S
	@RequestMapping(value = "/personextenddtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPersonExtendDtl(@RequestBody PersonDtlReq personDtlReq) {
		if (ObjectUtils.isEmpty(personDtlReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personDtlReq.UlIdPerson.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(personDtlReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personDtlReq.UlIdStage.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(personDtlService.getExtendedPersonDtl(personDtlReq)));
		return commonFormRes;
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
	@RequestMapping(value = "/fetchCatChar", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CatCharRes fetchCatChar(@RequestBody CatCharReq catCharReq) {
		if (TypeConvUtil.isNullOrEmpty(catCharReq.getCharCodeType())) {
			throw new InvalidRequestException(messageSource.getMessage(CHARCODETYPE_MANDATORY, null, Locale.US));
		}
		return personDtlService.fetchCatChar(catCharReq);
	}

	/**
	 * 
	 * Method Name: fetchPersonCharDetails Method Description: Provides the list
	 * of characteristics for a person along with person details
	 * 
	 * @param personCharsReq
	 * @return PersonCharsRes
	 */
	@RequestMapping(value = "/fetchPersonCharDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonCharsRes fetchPersonCharDetails(@RequestBody PersonCharsReq personCharsReq) {
		if (TypeConvUtil.isNullOrEmpty(personCharsReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.fetchPersonCharDetails(personCharsReq);
	}

	/**
	 * Method Name: fetchAfcarsData Method Description:Retrieve the row with the
	 * latest end date for the Person ID, from AFCARS_RESPONSE.
	 * 
	 * @param afcarsValueBean
	 * @return AfcarsDto 
	 */

	@RequestMapping(value = "/mergeSplit/getFieldsToDisplay", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonMergeSplitRes getFieldsToDisplay(@RequestBody PersonMergeSplitReq personMergeSplitReq) {
		SelectForwardPersonValueBean selectForwardPersonValueBean = null;
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writeValueAsString(personMergeSplitReq);
			log.info("Method : MergePersons - request :" + json);
		} catch (JsonProcessingException e) {
		}
		if (ServiceConstants.ZERO_VAL.longValue() != personMergeSplitReq.getIdPersonMerge()) {
			Date dtMergeDate;
			try {
				dtMergeDate = mapper.readValue(personMergeSplitReq.getDtPersonMergeStr(), Date.class);
			} catch (Exception e) {
				dtMergeDate = null;
			}
			selectForwardPersonValueBean = selectForwardPersonDataManager.getFieldsToDisplay(
					(int) personMergeSplitReq.getIdForwardPerson(), (int) personMergeSplitReq.getIdClosedPerson(),
					(int) personMergeSplitReq.getIdPersonMerge(), dtMergeDate);
		} else
			selectForwardPersonValueBean = selectForwardPersonDataManager.getFieldsToDisplay(
					(int) personMergeSplitReq.getIdForwardPerson(), (int) personMergeSplitReq.getIdClosedPerson());
		PersonMergeSplitRes res = new PersonMergeSplitRes();
		res.setSelectForwardPersonValueBean(selectForwardPersonValueBean);
		return res;
	}

	/**
	 * 
	 * Method Name: fetchPersonChar Method Description: Retrieve characteristics
	 * for given person ID, in given category, with status indicators and dates.
	 * 
	 * @param personCharsReq
	 * @return GetPersonCharsRes
	 */
	@RequestMapping(value = "/fetchPersonChar", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  GetPersonCharsRes fetchPersonChar(@RequestBody PersonCharsReq personCharsReq) {
		if (TypeConvUtil.isNullOrEmpty(personCharsReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return personDtlService.fetchPersonChar(personCharsReq);
	}

	/**
	 * Method Name: getStaffLegalName Method Description:This method retrieves
	 * staff Legal Name using id_person
	 * 
	 * @param personDtlReq
	 * @return PersonValueRes
	 */
	@RequestMapping(value = "/getStaffLegalName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDtlRes getStaffLegalName(@RequestBody PersonDtlReq personDtlReq) {
		log.info("In method getStaffLegalName in PersonController Class");
		PersonDtlRes personDtlRes = new PersonDtlRes();
		personDtlRes.setNameDto(personDtlService.getStaffLegalName(personDtlReq.getIdPerson()));
		log.info("Out method getStaffLegalName in PersonController Class");
		return personDtlRes;
	}

	/**
	 * Method Name: getExtPersonRoles Method Description:Get the external person
	 * roles for a Person with External Application access
	 * 
	 * @param fetchPersonReq
	 * @return ExtPersonRolesRes
	 */
	@RequestMapping(value = "/getExtPersonRoles", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDtlRes getExtPersonRoles(@RequestBody PersonDtlReq personDtlReq) {
		log.info("In method getExtPersonRoles in PersonController Class");
		PersonDtlRes extPersonRolesRes = new PersonDtlRes();
		extPersonRolesRes.setExtPersonRoles(personDtlService.getExtPersonRoles(personDtlReq.getIdPerson()));
		log.info("Out method getExtPersonRoles in PersonController Class");
		return extPersonRolesRes;
	}

	/**
	 * Method Name: getSystemAccessList Method Description:Get the application
	 * access list for external user types
	 * 
	 * @param PersonDtlReq
	 * @return PersonEjbRes
	 */
	@RequestMapping(value = "/getSystemAccessList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonEjbRes getSystemAccessList(@RequestBody PersonDtlReq fetchPersonReq) {
		log.info("In method getSystemAccessList in PersonController Class");
		PersonEjbRes systemAccessRes = new PersonEjbRes();
		systemAccessRes.setSystemAccessList(personDtlService.getSystemAccessList(fetchPersonReq.getIdPerson()));
		return systemAccessRes;
	}

	/**
	 * Method Name: getStaffAddress Method Description:This method retrieves
	 * staff mail code address using cd_mail_code
	 * 
	 * @param mailCodeReq
	 * @return PersonEjbRes
	 */
	@RequestMapping(value = "/getStaffAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonEjbRes getStaffAddress(@RequestBody CaseFileMgmtReq mailCodeReq) {
		log.info("In method getStaffAddress in PersonController Class");

		PersonEjbRes personEjbRes = new PersonEjbRes();
		personEjbRes.setStaffAddress(personDtlService.getStaffAddress(mailCodeReq.getAddrMailCode()));
		return personEjbRes;
	}

	/**
	 * Method Name: getSystemAccessList Method Description: This method saves
	 * the application access for external user types
	 * 
	 * @param systemAccessReq
	 * @return systemAccessRes
	 */
	@RequestMapping(value = "/saveSystemAccess", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonEjbRes saveSystemAccess(@RequestBody PersonDtlReq systemAccessReq) {

		log.info("In method saveSystemAccess in PersonController Class");

		PersonEjbRes systemAccessRes = new PersonEjbRes();
		personDtlService.saveSystemAccess(systemAccessReq.getSystemAccessDto(), systemAccessReq.getReqFuncCd());
		return systemAccessRes;
	}

	/**
	 * 
	 * Method Name: getPersonLists Method Description:This Method is used to
	 * fetch the Person List for a given idSatge
	 * 
	 * @param retrievePersonListReq
	 * @return
	 */
	@RequestMapping(value = "/getPersonListByStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)

	public  PersonListRes getPersonListByStage(@RequestBody PersonListReq retrievePersonListReq) {
		if (ObjectUtils.isEmpty(retrievePersonListReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (!ObjectUtils.isEmpty(retrievePersonListReq.getCallFromVisitationPage())
				&& retrievePersonListReq.getCallFromVisitationPage()
				&& ObjectUtils.isEmpty(retrievePersonListReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		PersonListRes personListRes = new PersonListRes();
		personListRes = personListService.getPersonListByStage(retrievePersonListReq);
		if (personListRes.getRetrievePersonList().size() <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}
		return personListRes;
	}

	@RequestMapping(value = "/hasOriginatingFPCheck", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes hasOriginatingFPCheck(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.hasOriginatingFPCheck(request);
	}

	@RequestMapping(value = "/getSidOriginalFingerprint", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getSidOriginalFingerprint(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.getSidOriginalFingerprint(request);
	}

	@RequestMapping(value = "/getABCSCheckRapBack", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes isABCSCheckRapBack(@RequestBody RecordsCheckReq request) {
		return recordsCheckService.isABCSCheckRapBack(request);
	}

	@RequestMapping(value = "/getNewHireCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getNewHireCount(@RequestBody RecordsCheckDto recordsCheckDto) {
		return recordsCheckService.getNewHireCount(recordsCheckDto.getIdRecCheck());
	}

	@RequestMapping(value = "/getCdDetermination", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckRes getCdDetermination(@RequestBody RecordsCheckDto recordsCheckDto) {
		return recordsCheckService.getCdDetermination(recordsCheckDto.getIdRecCheck());
	}

	@RequestMapping(value = "/getStagePersonLink", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StagePersonRes getStagePersonLink(@RequestBody PersonListReq personListReq) {
		log.info("In method getStagePersonLink in PersonController Class");
		StagePersonRes stagePersonRes = new StagePersonRes();
		stagePersonRes.setStagePersonValueDto(personListService.selectStagePersonLink(personListReq.getIdPerson(),personListReq.getIdStage()));
		log.info("Out method getStagePersonLink in PersonController Class");
		return stagePersonRes;
	}
	@RequestMapping(value = "/getAdminReviewOpenStagesByPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IDListRes getAdminReviewOpenStagesByPerson(@RequestBody PersonListReq personListReq) {

	return personListService.getAdminReviewOpenStagesByPerson(personListReq.getIdPerson(),personListReq.getIdStage());

	}
	@RequestMapping(value = "/getAdminReviewOpenExists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IDListRes getAdminReviewOpenExists(@RequestBody PersonListReq personListReq) {

		return personListService.getAdminReviewOpenExists(personListReq.getIdStage());

	}

	@RequestMapping(value = "/getAdminReviewDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AdminReviewDto getAdminReviewDetails(@RequestBody CommonHelperReq commonHelperReq) {
		return personListService.getAdminReviewDetails(commonHelperReq.getIdStage());
	}

  /**
   * Method Description: This Method will retrieve ID UNIT for a Parent Unit, given CD UNIT PROGRAM,
   * CD UNIT REGION, and NBR UNIT. Service Name: CCMN47S
   *
   * @param rtrvUnitIdReq
   * @return rtrvUnitIdRes
   */
  @RequestMapping(
      value = "/getUnitCandidateId",
      headers = {"Accept=application/json"},
      method = RequestMethod.POST)
  public RtrvUnitIdRes getUnitCandidateId(@RequestBody RtrvUnitIdReq rtrvUnitIdReq) {
    if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getCdUnitRegion())) {
      throw new InvalidRequestException(
          messageSource.getMessage("common.unitRegion.mandatory", null, Locale.US));
    }
    if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getCdUnitProgram())) {
      throw new InvalidRequestException(
          messageSource.getMessage("common.unitProgram.mandatory", null, Locale.US));
    }
    if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getNbrUnit())) {
      throw new InvalidRequestException(
          messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
    }
    if (TypeConvUtil.isNullOrEmpty(rtrvUnitIdReq.getIndExtern())) {
      throw new InvalidRequestException(
          messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
    }
    return rtrvUnitIdService.getUnitCandidateId(rtrvUnitIdReq);
  }

  @RequestMapping(
      value = "/getPersonCitizenshipbyIdStageAndRelType",
      headers = {"Accept=application/json"},
      method = RequestMethod.POST)
  public PersonCitizenshipStatusRes getPersonCitizenshipByStageIdAndRelType(
      @RequestBody CommonHelperReq commonHelperReq) {
		PersonCitizenshipStatusRes personCitizenshipStatusRes = new PersonCitizenshipStatusRes();
    personCitizenshipStatusRes.setCdPersonCitizenship(
        personDtlService.getPersonCitizenshipByStageIdAndRelType(
            commonHelperReq.getIdStage(), commonHelperReq.getPersRelInt()));
		return personCitizenshipStatusRes;
	}

  @RequestMapping(
      value = "/getOldestVictimPersonByStageId",
      headers = {"Accept=application/json"},
      method = RequestMethod.POST)
  public PersonCitizenshipStatusRes getOldestVictimPersonByStageIdAndRelType(
      @RequestBody CommonHelperReq commonHelperReq) {
    PersonCitizenshipStatusRes personCitizenshipStatusRes = new PersonCitizenshipStatusRes();
    PersonDto personDto =
        personDtlService.getOldestVictimPersonByStageIdAndRelType(commonHelperReq.getIdStage());
    if (!ObjectUtils.isEmpty(personDto)) {
      personCitizenshipStatusRes.setIdPerson(personDto.getIdPerson());
      personCitizenshipStatusRes.setNmPersonFull(personDto.getNmPersonFull());
    }
    return personCitizenshipStatusRes;
  }
}
