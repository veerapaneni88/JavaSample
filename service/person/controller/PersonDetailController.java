package us.tx.state.dfps.service.person.controller;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.admin.dto.PersonDetailUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonDtlUpdateDto;
import us.tx.state.dfps.service.admin.service.PersonDetailRetrvlService;
import us.tx.state.dfps.service.admin.service.PersonDetailUpdateService;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.service.PersonDetailService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Person
 * Detail REST service impl Apr 30, 2018- 5:40:51 PM © 2017 Texas Department of
 * Family and Protective Services
 * ****************  Change History *********************
 * 05/24/2021 nairl Artifact artf185349 : Security -Records check tab is displaying for merged DFPS employee with Person in Closed case Legacy IMPACT and IMPACT2.0
 */

@RestController
@RequestMapping("/personDetail")
public class PersonDetailController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDetailService personDetailService;

	@Autowired
	PersonDetailUpdateService personDetailUpdateService;

	@Autowired
	PersonDetailRetrvlService personDetailRetrvlService;

	/**
	 * Method Name: isPersonEmpOrFormerEmp Method Description:Checks if person
	 * is an employee or former employee
	 *
	 * @param commonHelperReq
	 * @return IsPersonEmpOrFormerEmpRes
	 */
	@RequestMapping(value = "/isPersonEmpOrFormerEmp", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IsPersonEmpOrFormerEmpRes isPersonEmpOrFormerEmp(
			@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		IsPersonEmpOrFormerEmpRes isPersonEmpOrFormerEmpRes = new IsPersonEmpOrFormerEmpRes();
		isPersonEmpOrFormerEmpRes.setIsPersonEmpOrFormerEmpRes(
				personDetailService.isPersonEmpOrFormerEmp(commonHelperReq.getIdPerson()));

		return isPersonEmpOrFormerEmpRes;

	}

	/**
	 * Method Name: hasSSN Method Description: Check if the Person has a Non End
	 * Dated SSN
	 *
	 * @param commonHelperReq
	 * @return HasSSNRes
	 */
	@RequestMapping(value = "/hasSSN", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HasSSNRes hasSSN(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		return personDetailService.hasSSN(commonHelperReq);
	}

	/**
	 *
	 * Method Name: getPersonIdAndFullName Method Description: Method to get
	 * Fullname and person Id
	 *
	 * @param personDtlReq
	 * @return PersonFullNameRes
	 */
	@RequestMapping(value = "/getPersonIdAndFullName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonFullNameRes getPersonIdAndFullName(@RequestBody PersonDtlReq personDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(personDtlReq.getSysNbrReserved1())) {
			throw new InvalidRequestException(messageSource.getMessage("sysNbrId.mandatory", null, Locale.US));
		}
		return personDetailService.getPersonIdAndFullName(personDtlReq);
	}

	/**
	 * Method Name: isSSNVerifiedByInterfaceRes Method Description:Check if the
	 * SSN has been Verified by DHS Interface
	 *
	 * @param isSSNVerifiedByInterfaceReq
	 * @return IsSSNVerifiedByInterfaceRes
	 */
	@RequestMapping(value = "/isSSNVerifiedByInterface", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IsSSNVerifiedByInterfaceRes isSSNVerifiedByInterface(
			@RequestBody IsSSNVerifiedByInterfaceReq isSSNVerifiedByInterfaceReq) {
		if (TypeConvUtil.isNullOrEmpty(isSSNVerifiedByInterfaceReq.getNbrPersonId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.NbrPersonId.mandatory", null, Locale.US));
		}
		IsSSNVerifiedByInterfaceRes isSSNVerifiedByInterfaceRes = new IsSSNVerifiedByInterfaceRes();
		isSSNVerifiedByInterfaceRes.setbSSNVerified(
				personDetailService.isSSNVerifiedByInterface(isSSNVerifiedByInterfaceReq.getNbrPersonId()));

		return isSSNVerifiedByInterfaceRes;
	}

	/**
	 * Method Name: fetchPersonCharacDetails Method Description:Provides the
	 * list of characteristics for a person along with person details
	 *
	 * @param personStaffReq
	 * @return PersonStaffRes
	 */
	@RequestMapping(value = "/fetchPersonCharacDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonEthnicityRes fetchPersonCharacDetails(@RequestBody FetchPersonReq fetchPersonReq) {

		if (TypeConvUtil.isNullOrEmpty(fetchPersonReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchPersonReq.dto.mandatory", null, Locale.US));
		}
		PersonEthnicityRes personEthnicityRes = new PersonEthnicityRes();
		personEthnicityRes = personDetailService.fetchPersonCharacDetails(fetchPersonReq.getCharacteristicsDto());

		return personEthnicityRes;
	}

	/**
	 * Method Name: allegationVictimInformation Method Description:Provides the
	 * list of id victim List with the given
	 *
	 * @param allegationVictimReq
	 * @return allegationVictimRes
	 */
	@RequestMapping(value = "/allegationVictimInformation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AllegationVictimRes allegationVictimInformation(@RequestBody AllegationVictimReq allegationVictimReq) {

		if (TypeConvUtil.isNullOrEmpty(allegationVictimReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("allegationVictimReq.dto.mandatory", null, Locale.US));
		}
		AllegationVictimRes allegationVictimRes = new AllegationVictimRes();
		allegationVictimRes = personDetailService.allegationVictimInformation(allegationVictimReq);

		return allegationVictimRes;
	}

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 *
	 * @param personMergeHierarchyReq
	 * @return PersonMergeHierarchyRes
	 */
	@RequestMapping(value = "/getPersonMergeHierarchyList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonMergeHierarchyRes getPersonMergeHierarchyList(
			@RequestBody PersonMergeHierarchyReq personMergeHierarchyReq) {

		if (TypeConvUtil.isNullOrEmpty(personMergeHierarchyReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("PersonMergeSplit.idPerson.mandatory", null, Locale.US));
		}

		PersonMergeHierarchyRes personMergeHierarchyRes = new PersonMergeHierarchyRes();
		personMergeHierarchyRes.setTransactionId(personMergeHierarchyReq.getTransactionId());
		personMergeHierarchyRes.setPersonMergeSplitValueDtoList(
				personDetailService.getPersonMergeHierarchyList(personMergeHierarchyReq.getIdPerson()));
		personMergeHierarchyRes.setTransactionId(personMergeHierarchyReq.getTransactionId());
		return personMergeHierarchyRes;
	}

	/**
	 *
	 * Method Name: getEmployeeTypeDetail Method Description:retrieve employee
	 * type info given person id
	 *
	 * @param fetchPersonReq
	 * @return PersonIdTypeRes
	 */
	@RequestMapping(value = "/getEmployeeTypeDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonIdTypeRes getEmployeeTypeDetail(@RequestBody FetchPersonReq fetchPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(fetchPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PersonIdTypeRes personIdTypeRes = new PersonIdTypeRes();
		personIdTypeRes.setResultTypeMap(personDetailService.getEmployeeTypeDetail(fetchPersonReq.getIdPerson()));
		return personIdTypeRes;
	}
	/* Added to fix defect artf185349 */
	/**
	 *
	 * Method Name: getEmployeeType
	 * Method Description:retrieve employee type info given person id
	 *
	 * @param fetchPersonReq
	 * @return PersonIdTypeRes
	 */
	@RequestMapping(value = "/getEmployeeType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonIdTypeRes getEmployeeType(@RequestBody FetchPersonReq fetchPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(fetchPersonReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PersonIdTypeRes personIdTypeRes = new PersonIdTypeRes();
		personIdTypeRes.setResultTypeMap(personDetailService.getEmployeeTypeWithMerge(fetchPersonReq.getIdPerson()));
		return personIdTypeRes;
	}
	/* End of code changes for artifact artf185349 */

	/**
	 * Method Name: fetchAfcarsData Method Description:Retrieve the row with the
	 * latest end date for the Person ID, from AFCARS_RESPONSE.
	 *
	 * @param afcarsReq
	 * @return AfcarsRes
	 */

	@RequestMapping(value = "/fetchAfcarsData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AfcarsRes fetchAfcarsData(@RequestBody AfcarsReq afcarsReq) {
		if (TypeConvUtil.isNullOrEmpty(afcarsReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AfcarsRes afcarsRes = personDetailService.fetchAfcarsData(afcarsReq);
		return afcarsRes;
	}

	/**
	 * Method Name: savePersonAudit Method Description:This is a method to set
	 * all input parameters(of the stored procedure). executeStoredProc
	 *
	 * @param storeProcParams
	 *
	 * @return LegalActionsRes
	 */
	@RequestMapping(value = "/executeStoredProc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes savePersonAudit(@RequestBody Object[] storeProcParams){
		if (TypeConvUtil.isNullOrEmpty(storeProcParams)) {
			throw new InvalidRequestException(messageSource.getMessage("arrayList cannot be empty", null, Locale.US));
		}

		CommonStringRes commonStringRes = new CommonStringRes();
		commonStringRes = personDetailService.savePersonAudit(Arrays.asList(storeProcParams));

		return commonStringRes;
	}

	/**
	 * Method Name: savePersonAuditReasonDeath Method Description:This is a
	 * method to set all input parameters(of the stored procedure).
	 * executeStoredProcRsnDth
	 *
	 * @param storeProcParams
	 *
	 * @return LegalActionsRes
	 */
	@RequestMapping(value = "/executeStoredProcRsnDth", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes savePersonAuditReasonDeath(@RequestBody Object[] storeProcParams){
		if (TypeConvUtil.isNullOrEmpty(storeProcParams)) {
			throw new InvalidRequestException(messageSource.getMessage("arrayList cannot be empty", null, Locale.US));
		}

		CommonStringRes commonStringRes = new CommonStringRes();
		commonStringRes = personDetailService.savePersonAuditReasonDeath(Arrays.asList(storeProcParams));

		return commonStringRes;
	}

	/**
	 * Method Name: personDetailUpdate Method Description:This Service calls
	 * update a number of tables. A Row is entered or updated in the Person,
	 * relationship, Stage person link, and the To Do. CINV05S
	 *
	 * @param personDtlUpdateDto
	 * @return PersonDetailUpdateRes
	 */
	@RequestMapping(value = "/personDetailUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonDetailUpdateRes personDetailUpdate(@RequestBody PersonDtlUpdateDto personDtlUpdateDto) {
		PersonDetailUpdateRes personDetailUpdateoRes = new PersonDetailUpdateRes();
		if (TypeConvUtil.isNullOrEmpty(personDtlUpdateDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.personDtlUpdateDto.mandatory", null, Locale.US));
		}
		PersonDetailUpdateDto personDetailUpdateDto = personDetailUpdateService.personDetailUpdate(personDtlUpdateDto);
		personDetailUpdateoRes.setPersonDetailUpdateDto(personDetailUpdateDto);
		return personDetailUpdateoRes;
	}

	/**
	 *
	 * Method Name: PersonDetailRetrieve Method Description: This Service
	 * Retrieves all the information for the person detail window. CINV04S
	 *
	 * @param personDetailsReq
	 * @return PersonDetailRetrvloRes
	 */
	@RequestMapping(value = "/personDetailRetrieve", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PersonDetailsRes PersonDetailRetrieve(@RequestBody PersonDetailsReq personDetailsReq) {

		if (TypeConvUtil.isNullOrEmpty(personDetailsReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		PersonDetailsRes personDetailsRes = personDetailRetrvlService.personDetailRetrvl(personDetailsReq);

		return personDetailsRes;
	}

	@RequestMapping(value = "/mpsPersonDetailRetrieve", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IncomingPersonMpsRes mpsPersonDetailRetrieve(@RequestBody PersonDetailsReq personDetailsReq) {

		if (TypeConvUtil.isNullOrEmpty(personDetailsReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		IncomingPersonMpsRes personRes = personDetailRetrvlService.mpsPersonDetail(personDetailsReq);

		return personRes;
	}


	@RequestMapping(value = "/personDetailUpdateForMPS", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public IncomingPersonMpsRes personDetailUpdateForMPS(@RequestBody IncomingPersonMpsDto incomingPersonMpsDto) {
		if (TypeConvUtil.isNullOrEmpty(incomingPersonMpsDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.personDtlUpdateDto.mandatory", null, Locale.US));
		}
		IncomingPersonMpsRes personRes = personDetailUpdateService.personDetailUpdateForMPS(incomingPersonMpsDto);
		return personRes;
	}


	@RequestMapping(value = "/addMpsPersonDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersonIdDto addMpsPersonDetails(@RequestBody CommonHelperReq req){
		PersonIdDto personIdDto = personDetailUpdateService.addMpsPersonDetails(req.getIdPerson());
		return personIdDto;
	}

}
