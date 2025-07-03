package us.tx.state.dfps.service.person.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.PersonRaceDetailsDao;
import us.tx.state.dfps.service.admin.dao.PersonSelectEthnicityDao;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.admin.service.PersonIdService;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.EducationHistoryReq;
import us.tx.state.dfps.service.common.request.NameHistoryDetailReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.contacts.dao.KinshipNotificationDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PersonComparisonPrefillData;
import us.tx.state.dfps.service.person.dao.EducationHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonComparisonDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.*;
import us.tx.state.dfps.service.person.service.PersonComparisonService;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:service
 * CPER03S for form per03o00, to populate Person Comparison Form May 31, 2018-
 * 10:15:23 AM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonComparisonServiceImpl implements PersonComparisonService {

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private KinshipNotificationDao kinshipNotificationDao;

	@Autowired
	private PersonRaceDetailsDao personRaceDetailsDao;

	@Autowired
	private PersonSelectEthnicityDao personSelectEthnicityDao;

	@Autowired
	private CharacteristicsDao characteristicsDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private EducationHistoryDao educationHistoryDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private PersonComparisonDao personComparisonDao;

	@Autowired
	private PersonComparisonPrefillData personComparisonPrefillData;


	@Autowired
	PersonIdService personIdService;

	public static final String Address = "Address";
	public static final String Phone = "Phone";
	public static final String Race = "Race";
	public static final String Ethnicity = "Ethnicity";
	public static final String Characteristics = "Characteristics";
	public static final String nameHistory = "nameHistory";
	public static final String educationHistory = "educationHistory";
	public static final String mergeInfo = "mergeInfo";
	public static final String personinfo = "personinfo";
	public static final String Email = "Email";

	/**
	 * Method Name: getPersonComparison Method Description: Populates form
	 * per03o00, which Populates the Person Comparison Form.
	 * 
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto getPersonComparison(PersonDtlReq personDtlReq) {

		PersonComparisonDto personComparisonDto = new PersonComparisonDto();

		Long idPerson = personDtlReq.getIdPersonId();
		Long idPersonId = personDtlReq.getIdPerson();
		Long mergeId = personDtlReq.getIdMerge();

		// CallCSEC74D
		PersonDto personDto = childServicePlanFormDao.getPersonDetails(idPerson);
		personComparisonDto.setPersonDto(personDto);

		PersonDto personDto2 = childServicePlanFormDao.getPersonDetails(idPersonId);
		personComparisonDto.setPersonDto2(personDto2);

		//artf257687 -  When person comparison form is launched from Person search screen "PersonSearchCompareFlag" will be true.
		if(ServiceConstants.FALSE.equals(personDtlReq.getPersonSearchCompareFlag())) {
			// artf213439 : reading person details displaying in demographic from snap table.
			PersonBean persForwardValueBean = personDao.getPersonDetails((long) idPerson, (long) mergeId,
					CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			personComparisonDto.setPersForwardValueBean(persForwardValueBean);

			PersonBean persClosedValueBean = personDao.getPersonDetails((long) idPersonId, (long) mergeId,
					CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			personComparisonDto.setPersClosedValueBean(persClosedValueBean);

			personDto.setNbrPersonAge((short) DateUtils.getAge(persForwardValueBean.getDtDateOfBirth()));

			personDto2.setNbrPersonAge((short) DateUtils.getAge(persClosedValueBean.getDtDateOfBirth()));

			ArrayList<PersonIdentifiersDto> persClosedIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService.fetchIdentifiersList(
					(long) idPersonId, true, (long) mergeId, CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			personComparisonDto.setPersClosedIdentifiers(persClosedIdentifiers);

			ArrayList<PersonIdentifiersDto> persForwardIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService.fetchIdentifiersList(
					(long) idPerson, true, (long) mergeId, CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			personComparisonDto.setPersForwardIdentifiers(persForwardIdentifiers);
		}
		// CallCCMN96D
		AddressDtlReq addressDtlReq = new AddressDtlReq();
		addressDtlReq.setUlIdPerson(idPerson);
		List<AddressDto> addressList = personAddressDao.getAddressList(addressDtlReq);

		addressDtlReq.setUlIdPerson(idPersonId);
		List<AddressDto> addressList2 = personAddressDao.getAddressList(addressDtlReq);

		structureForm(addressList, addressList2, Address);
		personComparisonDto.setAddressList(addressList);
		personComparisonDto.setAddressList2(addressList2);

		// CCMNB0D
		List<PersonPhoneRetDto> personPhoneRetDto = kinshipNotificationDao.getPersonPhoneDetailList(idPerson);
		List<PersonPhoneRetDto> personPhoneRetDto2 = kinshipNotificationDao.getPersonPhoneDetailList(idPersonId);
		structureForm(personPhoneRetDto, personPhoneRetDto2, Phone);
		personComparisonDto.setPersonPhoneRetDto(personPhoneRetDto);
		personComparisonDto.setPersonPhoneRetDto2(personPhoneRetDto2);

		/* retrieves CallCLSS79D */
		PersonRaceInDto personRaceInDto = new PersonRaceInDto();
		personRaceInDto.setIdPerson(idPerson);
		List<PersonRaceOutDto> personRaceOutDtoList = personRaceDetailsDao.getRaceDetails(personRaceInDto);
		personRaceInDto.setIdPerson(idPersonId);
		List<PersonRaceOutDto> personRaceOutDtoList2 = personRaceDetailsDao.getRaceDetails(personRaceInDto);
		structureForm(personRaceOutDtoList, personRaceOutDtoList2, Race);
		personComparisonDto.setPersonRaceOutDtoList(personRaceOutDtoList);
		personComparisonDto.setPersonRaceOutDtoList2(personRaceOutDtoList2);

		// CallCLSS80D
		PersonEthnicityInDto personEthnicityInDto = new PersonEthnicityInDto();
		personEthnicityInDto.setIdPerson(idPerson);
		List<PersonEthnicityOutDto> personEthnicityOutDtos = personSelectEthnicityDao
				.getPersonEthnicity(personEthnicityInDto);
		personEthnicityInDto.setIdPerson(idPersonId);
		List<PersonEthnicityOutDto> personEthnicityOutDtos2 = personSelectEthnicityDao
				.getPersonEthnicity(personEthnicityInDto);
		structureForm(personEthnicityOutDtos, personEthnicityOutDtos2, Ethnicity);
		personComparisonDto.setPersonEthnicityOutDtos(personEthnicityOutDtos);
		personComparisonDto.setPersonEthnicityOutDtos2(personEthnicityOutDtos2);

		// CLSS60D
		List<CharacteristicsDto> charDtlList = characteristicsDao.getCharDtls(idPerson, new Date(), new Date());
		List<CharacteristicsDto> charDtlList2 = characteristicsDao.getCharDtls(idPersonId, new Date(), new Date());
		structureForm(charDtlList, charDtlList2, Characteristics);
		personComparisonDto.setCharDtlList(charDtlList);
		personComparisonDto.setCharDtlList2(charDtlList2);

		// CINV31D
		NameHistoryDetailReq nameHistoryDetailReq = new NameHistoryDetailReq();
		nameHistoryDetailReq.setIdPerson(idPerson);
		List<NameHistoryDtlDto> nameHistoryDtlDtoList = personDao.getNameHistoryDetail(nameHistoryDetailReq);
		nameHistoryDetailReq.setIdPerson(idPersonId);
		List<NameHistoryDtlDto> nameHistoryDtlDtoList2 = personDao.getNameHistoryDetail(nameHistoryDetailReq);
		structureForm(nameHistoryDtlDtoList, nameHistoryDtlDtoList2, nameHistory);
		personComparisonDto.setNameHistoryDtlDtoList(nameHistoryDtlDtoList);
		personComparisonDto.setNameHistoryDtlDtoList2(nameHistoryDtlDtoList2);

		// CallCINT17D
		List<PersonIdDto> personIdDtoList = personComparisonDao.getPersIntakeInv(idPerson, false, new Date());
		List<PersonIdDto> personIdDtoList2 = personComparisonDao.getPersIntakeInv(idPersonId, false, new Date());
		structureForm(personIdDtoList, personIdDtoList2, personinfo);
		personComparisonDto.setPersonIdDtoList(personIdDtoList);
		personComparisonDto.setPersonIdDtoList2(personIdDtoList2);

		// CallCLSS58D
		List<PersonIncomeResourceDto> personIncomeResourceDto = personComparisonDao.getPersIncomeResrc(idPerson);
		personComparisonDto.setPersonIncomeResourceDto(personIncomeResourceDto);
		List<PersonIncomeResourceDto> personIncomeResourceDto2 = personComparisonDao.getPersIncomeResrc(idPersonId);
		personComparisonDto.setPersonIncomeResourceDto2(personIncomeResourceDto2);

		// CLSC49D
		EducationHistoryReq educationHistoryReq = new EducationHistoryReq();
		educationHistoryReq.setIdPerson(idPerson);
		List<EducationHistoryDto> educationHistoryDtoList = educationHistoryDao
				.getPersonEducationHistoryList(educationHistoryReq);
		educationHistoryReq.setIdPerson(idPersonId);
		List<EducationHistoryDto> educationHistoryDtoList2 = educationHistoryDao
				.getPersonEducationHistoryList(educationHistoryReq);
		structureForm(educationHistoryDtoList, educationHistoryDtoList2, educationHistory);
		personComparisonDto.setEducationHistoryDtoList(educationHistoryDtoList);
		personComparisonDto.setEducationHistoryDtoList2(educationHistoryDtoList2);

		// CLSC46D
		List<PersonMergeInfoDto> personMergeInfoDtoList = personComparisonDao.getMergeIds(idPerson);
		List<PersonMergeInfoDto> personMergeInfoDtoList2 = personComparisonDao.getMergeIds(idPersonId);
		structureForm(personMergeInfoDtoList, personMergeInfoDtoList2, mergeInfo);
		personComparisonDto.setPersonMergeInfoDtoList(personMergeInfoDtoList);
		personComparisonDto.setPersonMergeInfoDtoList2(personMergeInfoDtoList2);

		// CLSSB3D
		List<PersonEmailDto> personEmailDtoList = personComparisonDao.getPersEmail(idPerson);
		List<PersonEmailDto> personEmailDtoList2 = personComparisonDao.getPersEmail(idPersonId);
		structureForm(personEmailDtoList, personEmailDtoList2, Email);
		personComparisonDto.setPersonEmailDtoList(personEmailDtoList);
		personComparisonDto.setPersonEmailDtoList2(personEmailDtoList2);

		// CLSSB4D
		PersonPotentialDupDto personPotentialDupDto = personComparisonDao.getPersDup(idPerson);
		personComparisonDto.setPersonPotentialDupDto(personPotentialDupDto);
		PersonPotentialDupDto personPotentialDupDto2 = personComparisonDao.getPersDup(idPersonId);
		personComparisonDto.setPersonPotentialDupDto2(personPotentialDupDto2);

		// CCMN69D
		EmployeeDetailDto employeeDetailDto = employeeDao.getEmployeeById(idPerson);
		personComparisonDto.setEmployeeDetailDto(employeeDetailDto);
		EmployeeDetailDto employeeDetailDto2 = employeeDao.getEmployeeById(idPersonId);
		personComparisonDto.setEmployeeDetailDto2(employeeDetailDto2);

		// CSEC67D
		PersonMergeInfoDto personMergeInfoDto = personComparisonDao.getDemInfo(idPerson);
		personComparisonDto.setPersonMergeInfoDto(personMergeInfoDto);
		PersonMergeInfoDto personMergeInfoDto2 = personComparisonDao.getDemInfo(idPersonId);
		personComparisonDto.setPersonMergeInfoDto2(personMergeInfoDto2);

		// CCMN44D
		PersonDto personDto41 = personDao.getPersonById(idPerson);
		personComparisonDto.setPersonDto41(personDto41);
		PersonDto personDto42 = personDao.getPersonById(idPersonId);
		personComparisonDto.setPersonDto42(personDto42);

		// CSES31D
		PersonDtlDto personDtlDto = personDao.searchPersonDtlById(idPerson);
		personComparisonDto.setPersonDtlDto(personDtlDto);
		PersonDtlDto personDtlDto2 = personDao.searchPersonDtlById(idPersonId);
		personComparisonDto.setPersonDtlDto2(personDtlDto2);

		return personComparisonPrefillData.returnPrefillData(personComparisonDto);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void structureForm(List person1List, List person2List, String type) {

		int idperson1AddrCount = ServiceConstants.Zero_INT;
		int idperson2AddrCount = ServiceConstants.Zero_INT;

		if (!ObjectUtils.isEmpty(person1List)) {
			idperson1AddrCount = person1List.size();
		}
		if (!ObjectUtils.isEmpty(person2List)) {
			idperson2AddrCount = person2List.size();
		}

		if (idperson1AddrCount > idperson2AddrCount) {
			int diffCount = idperson1AddrCount - idperson2AddrCount;

			addEmptyDto(person2List, diffCount, type);

		} else {

			int diffCount = idperson2AddrCount - idperson1AddrCount;

			addEmptyDto(person1List, diffCount, type);

		}

	}

	public void addEmptyDto(List<Object> personList, int diffCount, String type) {
		for (int i = 0; i < diffCount; i++) {

			if (type.equals(Address)) {

				AddressDto addressDto = new AddressDto();
				personList.add(addressDto);

			} else if (type.equals(Phone)) {

				PersonPhoneRetDto personPhoneRetDto = new PersonPhoneRetDto();
				personList.add(personPhoneRetDto);

			} else if (type.equals(Race)) {

				PersonRaceOutDto personRaceOutDto = new PersonRaceOutDto();
				personList.add(personRaceOutDto);

			} else if (type.equals(Ethnicity)) {

				PersonEthnicityOutDto personEthnicityOutDto = new PersonEthnicityOutDto();
				personList.add(personEthnicityOutDto);
			} else if (type.equals(Characteristics)) {

				CharacteristicsDto characteristicsDto = new CharacteristicsDto();
				personList.add(characteristicsDto);
			} else if (type.equals(nameHistory)) {

				NameHistoryDtlDto nameHistoryDtlDto = new NameHistoryDtlDto();
				personList.add(nameHistoryDtlDto);
			} else if (type.equals(personinfo)) {

				PersonIdDto personIdDto = new PersonIdDto();
				personList.add(personIdDto);
			} else if (type.equals(educationHistory)) {

				EducationHistoryDto educationHistoryDto = new EducationHistoryDto();
				personList.add(educationHistoryDto);
			} else if (type.equals(mergeInfo)) {

				PersonMergeInfoDto personMergeInfoDto = new PersonMergeInfoDto();
				personList.add(personMergeInfoDto);
			} else if (type.equals(Email)) {

				PersonEmailDto personEmailDto = new PersonEmailDto();
				personList.add(personEmailDto);
			}

		}

	}

}
