package us.tx.state.dfps.service.person.serviceimpl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AfcarsReq;
import us.tx.state.dfps.service.common.request.AllegationVictimReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.person.dto.PersonBean;
import us.tx.state.dfps.service.person.dto.PersonFullNameDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.service.PersonDetailService;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonDetailServiceImpl Apr 30, 2018- 5:43:05 PM © 2017 Texas
 * Department of Family and Protective Services
 * ****************  Change History *********************
 * 05/24/2021 nairl Artifact artf185349 : Security -Records check tab is displaying for merged DFPS employee with Person in Closed case Legacy IMPACT and IMPACT2.0
 */
@Service
@Transactional
public class PersonDetailServiceImpl implements PersonDetailService {

	@Autowired
	PersonDetailDao personDetailDao;

	@Autowired
	CharacteristicsDao characDao;

	/**
	 * Method Name: isPersonEmpOrFormerEmp Method Description: Checks if person
	 * is an employee or former employee
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPersonEmpOrFormerEmp(Long idPerson) {

		List<PersonBean> personBeans = personDetailDao.isPersonEmpOrFormerEmp(idPerson);
		for (PersonBean personBean : personBeans) {
			if (ServiceConstants.CPSNDTCT_EMP.equals(personBean.getPersonCategoryCode())
					|| ServiceConstants.CPSNDTCT_FEM.equals(personBean.getPersonCategoryCode())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Method Name: hasSSN Method Description: Check if the Person has a Non End
	 * Dated SSN
	 * 
	 * @param commonHelperReq
	 * @return HasSSNRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public HasSSNRes hasSSN(CommonHelperReq commonHelperReq) {

		HasSSNRes hasSSNRes = new HasSSNRes();
		hasSSNRes.setHasSSN(personDetailDao.hasSSN(commonHelperReq.getIdPerson()));
		return hasSSNRes;
	}

	/**
	 * 
	 * Method Name: getPersonIdAndFullName Method Description: Method to get
	 * Fullname and person Id
	 * 
	 * @param personDtlReq
	 * @return PersonFullNameRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonFullNameRes getPersonIdAndFullName(PersonDtlReq personDtlReq) {

		PersonFullNameRes personFullNameRes = new PersonFullNameRes();
		List<PersonFullNameDto> liPersonFullNameDto = personDetailDao
				.getPersonIdAndFullName(personDtlReq.getNbrPersonId(), personDtlReq.getIdPerson());
		if (!TypeConvUtil.isNullOrEmpty(liPersonFullNameDto)) {
			personFullNameRes.setLiPersonFullNameDto(liPersonFullNameDto);
		}
		return personFullNameRes;
	}

	/**
	 * Method Name: isSSNVerifiedByInterface Method Description:Check if the SSN
	 * has been Verified by DHS Interface
	 * 
	 * @param szNbrPersonIdNbr
	 * @return Boolean
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isSSNVerifiedByInterface(String szNbrPersonIdNbr) {

		Boolean bSSNVerified = false;

		bSSNVerified = personDetailDao.isSSNVerifiedByInterface(szNbrPersonIdNbr);

		return bSSNVerified;

	}

	/**
	 * Method Name: fetchPersonCharacDetails Method Description:Provides the
	 * list of characteristics for a person along with person details
	 * 
	 * @param idPerson
	 * @return HashMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public PersonEthnicityRes fetchPersonCharacDetails(CharacteristicsDto characteristicsDto) {
		PersonEthnicityRes personEthnicityRes = new PersonEthnicityRes();
		if (null == characteristicsDto.getDtEffective()) {

			PersonValueDto personValueDto = new PersonValueDto();
			Person person = personDetailDao.getPersonDetails(characteristicsDto.getIdPerson());
			populatePersonValueBean(person, personValueDto);
			List characList = new ArrayList();
			if (null != personValueDto.getPersonCharacteristicsCode()
					&& personValueDto.getPersonCharacteristicsCode().equals(ServiceConstants.CHARACTERISTIC_CHECKED)) {
				characList = characDao.getCharacteristicDetails(characteristicsDto.getIdPerson());
			}
			personEthnicityRes.setCharacteristicsDtos(characList);
			personEthnicityRes.setPersonValueDto(personValueDto);
		} else {
			List characList = characDao.getCharacteristicDetailsDate(characteristicsDto.getIdPerson(),
					characteristicsDto.getDtEffective());
			personEthnicityRes.setCharacteristicsDtos(characList);
		}

		return personEthnicityRes;
	}


	/**
	 * Method Name: allegationVictimInformation Method Description:Provides the
	 * list of Allegation Victim detail with the given stage id
	 *
	 * @param idStage
	 * @return AlligationVictimRes
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public AllegationVictimRes allegationVictimInformation(AllegationVictimReq allegationVictimReq) {
		AllegationVictimRes allegationVictimRes = new AllegationVictimRes();
		if (null != allegationVictimReq.getId_allegation_stage()) {
			List<String> idVictimList = personDetailDao.getAllegationVictimList(allegationVictimReq);
			allegationVictimRes.setIdVictim(idVictimList);
		}
		return allegationVictimRes;
	}


	/**
	 * 
	 * Method Name: populatePersonValueBean Method Description: This method is
	 * used for populating the PersonValueDto from Person
	 * 
	 * @param person
	 * @param personValueBean
	 */
	private void populatePersonValueBean(Person person, PersonValueDto personValueBean) {
		if (!ObjectUtils.isEmpty(person.getIdPerson())) {
			personValueBean.setPersonId(person.getIdPerson());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonDeath())) {
			personValueBean.setReasonForDeathCode(person.getCdPersonDeath());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonEthnicGroup())) {
			personValueBean.setEthnicGroupCode(person.getCdPersonEthnicGroup());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonLanguage())) {
			personValueBean.setPrimaryLanguageCode(person.getCdPersonLanguage());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonMaritalStatus())) {
			personValueBean.setMaritalStatusCode(person.getCdPersonMaritalStatus());
		}
		if (!ObjectUtils.isEmpty(person.getCdMannerDeath())) {
			personValueBean.setMannerOfDeathCode(person.getCdMannerDeath());
		}
		if (!ObjectUtils.isEmpty(person.getCdDeathRsnCps())) {
			personValueBean.setDeathReasonCpsCode(person.getCdDeathRsnCps());
		}
		if (!ObjectUtils.isEmpty(person.getCdDeathCause())) {
			personValueBean.setDeathCauseCode(person.getCdDeathCause());
		}
		if (!ObjectUtils.isEmpty(person.getCdDeathAutpsyRslt())) {
			personValueBean.setDeathAutopsyResultCode(person.getCdDeathAutpsyRslt());
		}
		if (!ObjectUtils.isEmpty(person.getCdDeathFinding())) {
			personValueBean.setDeathFindingCode(person.getCdDeathFinding());
		}
		if (!ObjectUtils.isEmpty(person.getTxtFatalityDetails())) {
			personValueBean.setFatalityDetails(person.getTxtFatalityDetails());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonReligion())) {
			personValueBean.setReligionCode(person.getCdPersonReligion());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonSex())) {
			personValueBean.setSex(person.getCdPersonSex());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonStatus())) {
			personValueBean.setActiveInactiveMergedStatusCode(person.getCdPersonStatus());
		}
		if (!ObjectUtils.isEmpty(person.getDtPersonBirth())) {
			personValueBean.setDateOfBirth(person.getDtPersonBirth());
		}
		if (!ObjectUtils.isEmpty(person.getDtPersonDeath())) {
			personValueBean.setDateOfDeath(person.getDtPersonDeath());
		}
		if (!ObjectUtils.isEmpty(person.getNbrPersonAge())) {
			personValueBean.setAge((int) person.getNbrPersonAge());
		}
		if (!ObjectUtils.isEmpty(person.getNmPersonFull())) {
			personValueBean.setFullName(person.getNmPersonFull());
		}
		if (!ObjectUtils.isEmpty(person.getTxtPersonOccupation())) {
			personValueBean.setOccupation(person.getTxtPersonOccupation());
		}
		if (!ObjectUtils.isEmpty(person.getIndPersCancelHist())) {
			if (person.getIndPersCancelHist().equals(ServiceConstants.Y))
				personValueBean.setIndCancelHist(new Boolean(true));
			else
				personValueBean.setIndCancelHist(new Boolean(false));
		}
		if (!ObjectUtils.isEmpty(person.getCdPersGuardCnsrv())) {
			personValueBean.setGuardianshipConservatorshipCode(person.getCdPersGuardCnsrv());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonStatus())) {
			personValueBean.setActiveInactiveMergedStatusCode(person.getCdPersonStatus());
		}
		if (!ObjectUtils.isEmpty(person.getDtLastUpdate())) {
			personValueBean.setPersonTableDateLastUpdate(person.getDtLastUpdate());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonLivArr())) {
			personValueBean.setLivingArrangementCode(person.getCdPersonLivArr());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonChar())) {
			personValueBean.setPersonCharacteristicsCode(person.getCdPersonChar());
		}
		if (!ObjectUtils.isEmpty(person.getIndPersonDobApprox())) {
			if (person.getIndPersonDobApprox().equals(ServiceConstants.Y))
				personValueBean.setApproxDateOfBirth(ServiceConstants.TRUE);
			else
				personValueBean.setApproxDateOfBirth(ServiceConstants.FALSE);
		}
		if (!ObjectUtils.isEmpty(person.getCdDisasterRlf())) {
			personValueBean.setDisasterRelief(person.getCdDisasterRlf());
		}
		if (!ObjectUtils.isEmpty(person.getIndEducationPortfolio())) {
			if (person.getIndEducationPortfolio().equals(ServiceConstants.Y))
				personValueBean.setIndEducationPortfolio(new Boolean(Boolean.TRUE));
			else
				personValueBean.setIndEducationPortfolio(new Boolean(Boolean.FALSE));
		}
		if (!ObjectUtils.isEmpty(person.getCdTribeEligible())) {
			personValueBean.setCdTribeEligible(person.getCdTribeEligible());
		}

		if (!ObjectUtils.isEmpty(person.getCdPersonCounty())) {
			personValueBean.setCounty(person.getCdPersonCounty());
		}

		personValueBean.setPhone(person.getNbrPersonPhone());

		if (!ObjectUtils.isEmpty(person.getNmPersonFirst())) {
			personValueBean.setFirstName(person.getNmPersonFirst());
		}
		if (!ObjectUtils.isEmpty(person.getNmPersonLast())) {
			personValueBean.setLastName(person.getNmPersonLast());
		}
		if (!ObjectUtils.isEmpty(person.getNmPersonMiddle())) {
			personValueBean.setMiddleName(person.getNmPersonMiddle());
		}
		if (!ObjectUtils.isEmpty(person.getCdOccupation())) {
			personValueBean.setCdOccupation(person.getCdOccupation());
		}
		if (!ObjectUtils.isEmpty(person.getCdPersonSuffix())) {
			personValueBean.setNameSuffixCode(person.getCdPersonSuffix());
		}
		if (!ObjectUtils.isEmpty(person.getIndIrRpt())) {
			if (person.getIndIrRpt().equals(ServiceConstants.Y))
				personValueBean.setIndIRrpt(new Boolean(ServiceConstants.TRUE));
			else
				personValueBean.setIndIRrpt(new Boolean(ServiceConstants.FALSE));
		}
	}

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param idPerson
	 * @return List<PersonMergeSplitValueDto>
	 */
	@Override
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long idPerson) {
		List<PersonMergeSplitValueDto> personMergeSplitValueDtoList = new ArrayList<>();
		Long fwdPersonId = personDetailDao.getForwardPersonInMerge(idPerson);
		if (Long.compare(fwdPersonId, ServiceConstants.ZERO_VAL) == 0)
			fwdPersonId = idPerson;
		Boolean mergeListLegacy = personDetailDao.checkIfMergeListLegacy(fwdPersonId);
		personMergeSplitValueDtoList = personDetailDao.getPersonMergeHierarchyList(fwdPersonId, mergeListLegacy);
		return personMergeSplitValueDtoList;
	}

	/**
	 * Method Name: getEmployeeTypeDetail Method Description:retrieve employee
	 * type info given person id
	 * 
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getEmployeeTypeDetail(Long idPerson) {
		Map typeMap = new HashMap<>();
		typeMap = personDetailDao.getEmployeeTypeDetail(idPerson);

		return typeMap;
	}
/* Added to fix defect artf185349 */
	/**
	 * Method Name: getEmployeeTypeWithMerge
	 * Method Description:retrieve employee type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getEmployeeTypeWithMerge(Long idPerson) {
		Map typeMap = new HashMap<>();
		typeMap = personDetailDao.getEmployeeTypeWithMerge(idPerson);

		return typeMap;
	}
/* End of code changes for artifact artf185349 */

	/**
	 * Method Name: fetchAfcarsData Method Description:Retrieve the row with the
	 * latest end date for the Person ID, from AFCARS_RESPONSE.
	 * 
	 * @param afcarsReq
	 * @return AfcarsRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public AfcarsRes fetchAfcarsData(AfcarsReq afcarsReq) {
		AfcarsRes afcarsRes = new AfcarsRes();
		afcarsRes.setAfcarsDto(personDetailDao.getAfcarsData(afcarsReq.getIdPerson()));
		return afcarsRes;
	}

	/**
	 * Method Name: savePersonAudit Method Description:This is a method to set
	 * all input parameters(of the stored procedure).
	 * 
	 * @param arrayList
	 * @return CommonStringRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CommonStringRes savePersonAudit(List<Object> arrayList){
		return personDetailDao.savePersonAudit(arrayList);
	}

	/**
	 * Method Name: savePersonAuditReasonDeath Method Description:This is a
	 * method to set all input parameters(of the stored procedure).
	 * 
	 * @param arrayList
	 * @return CommonStringRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CommonStringRes savePersonAuditReasonDeath(List<Object> arrayList){
		return personDetailDao.savePersonAuditReasonDeath(arrayList);
	}
}
