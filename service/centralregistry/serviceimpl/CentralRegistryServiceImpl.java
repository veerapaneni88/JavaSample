package us.tx.state.dfps.service.centralregistry.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.CentralRegistryDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.centralregistry.centralregistrydao.CentralRegistryDao;
import us.tx.state.dfps.service.centralregistry.service.CentralRegistryService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CentralRegistryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CentralRegistryFormPrefillData;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CentralRegistryServiceImpl will implemented all operation defined
 * in CentralRegistryService Interface related CentralRegistry module. May 2,
 * 2018- 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CentralRegistryServiceImpl implements CentralRegistryService {

	@Autowired
	PopulateLetterDao populateLetterDao;

	@Autowired
	CentralRegistryDao centralRegistryDao;

	@Autowired
	CentralRegistryFormPrefillData centralRegistryFormPrefillData;

	/**
	 * 
	 * Method Name: getCentralRegistryInfo Service Name : CCMN51S Method
	 * Description:This form service populates the Central Registry form.
	 * 
	 * @param centralRegistryReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getCentralRegistryInfo(CentralRegistryReq centralRegistryReq) {
		// Long idPerson = ServiceConstants.ZERO_VAL;
		CentralRegistryDto centralRegistryDto = new CentralRegistryDto();
		// call DAM CLSC03D
		List<CodesTablesDto> codeTableDtoList = populateLetterDao
				.getPersonInfoByCode(ServiceConstants.BOARD_TITLE_CODESTABLE, ServiceConstants.BOARD_NAME_CODESTABLE);
		// centralRegistryDto.setCodeTableDtoList(codeTableDtoList);

		// call DAM CLSC91D
		List<PersonRoleDto> personRolesForOpenInvList = centralRegistryDao
				.getPersonRolesForOpenInv(centralRegistryReq.getIdPerson());
		// centralRegistryDto.setPersonRolesForOpenInvList(personRolesForOpenInvList);

		// call DAM CLSC9AD
		List<PersonRoleDto> personRolesForOpenARInList = centralRegistryDao
				.getPersonRolesForOpenARIn(centralRegistryReq.getIdPerson());

		// call DAM CLSC92D
		List<PersonRoleDto> spPersonRoleList = centralRegistryDao.getSpPersonRole(centralRegistryReq.getIdPerson());

		// call DAM CLSC93D

		List<PersonRoleDto> victimPersonRolesList = centralRegistryDao
				.getVictimPersonRoles(centralRegistryReq.getIdPerson());

		// call DAM CSES96D to get person information
		PersonDto personDto = centralRegistryDao.getPersonInfo(centralRegistryReq.getIdPerson());

		if (!ObjectUtils.isEmpty(personRolesForOpenInvList)) {
			personDto.setTxtFormX2(ServiceConstants.ROLE_X);
		}

		if (!ObjectUtils.isEmpty(personRolesForOpenARInList)) {
			personDto.setTxtFormAR(ServiceConstants.ROLE_X);
		}

		if (!ObjectUtils.isEmpty(spPersonRoleList)) {
			personDto.setTxtFormX3(ServiceConstants.ROLE_X);

		}

		if (!ObjectUtils.isEmpty(victimPersonRolesList)) {
			personDto.setTxtFormX4(ServiceConstants.ROLE_X);

		}

		if (!ServiceConstants.ROLE_X.equals(personDto.getTxtFormX2())
				&& !ServiceConstants.ROLE_X.equals(personDto.getTxtFormX3())
				&& !ServiceConstants.ROLE_X.equals(personDto.getTxtFormX4())
				&& !ServiceConstants.ROLE_X.equals(personDto.getTxtFormAR())) {
			personDto.setTxtFormX1(ServiceConstants.ROLE_X);

		}

		centralRegistryDto.setCodeTableDtoList(codeTableDtoList);
		centralRegistryDto.setPersonDto(personDto);
		centralRegistryDto.setPersonRolesForOpenARInList(personRolesForOpenARInList);
		centralRegistryDto.setPersonRolesForOpenInvList(personRolesForOpenInvList);
		centralRegistryDto.setSpPersonRoleList(spPersonRoleList);
		centralRegistryDto.setVictimPersonRolesList(victimPersonRolesList);

		return centralRegistryFormPrefillData.returnPrefillData(centralRegistryDto);
	}

}
