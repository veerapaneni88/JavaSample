package us.tx.state.dfps.service.populateform.serviceimpl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LetterWhenUtcPrefillData;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.LetterWhenUtcDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateform.service.LetterWhenUtcService;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * method CINV57S Jul 5, 2018- 11:41:20 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class LetterWhenUtcServiceImpl implements LetterWhenUtcService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private NameDao nameDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private LetterWhenUtcPrefillData prefillData;

	/**
	 * Method Name: getUtcLetter Method Description: Makes DAO calls and sends
	 * data to prefill
	 * 
	 * @param req
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getUtcLetter(PopulateFormReq req) {
		// Global variables and main dto
		LetterWhenUtcDto prefillDto = new LetterWhenUtcDto();
		Long idPerson = ServiceConstants.ZERO;

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(req.getIdStage());
		// Only print report if recommended action complete
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)
				&& StringUtils.isNotBlank(genericCaseInfoDto.getCdStageReasonClosed())) {
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);

			// CLSC03D
			List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
					ServiceConstants.NAME);
			prefillDto.setCodesTablesList(codesTablesList);

			// CLSC01D
			List<CaseInfoDto> caseInfoList = populateLetterDao.getCaseInfoById(req.getIdStage(),
					ServiceConstants.PRINCIPAL);
			prefillDto.setCaseInfoList(caseInfoList);

			// CCMN40D
			EmpNameDto empNameDto = nameDao.getNameByPersonId(req.getIdPerson());
			prefillDto.setEmpNameDto(empNameDto);

			// CCMN96D
			AddressDtlReq addressDtlReq = new AddressDtlReq();
			addressDtlReq.setUlIdPerson(req.getIdPerson());
			addressDtlReq.setbSysIndIntake(ServiceConstants.ZERO);
			List<AddressDto> addressList = personAddressDao.getAddressList(addressDtlReq);
			if (!ObjectUtils.isEmpty(addressList)) {
				prefillDto.setAddressDto(addressList.get(0));
			}

			// CCMNB9D
			List<StagePersonLinkDto> stagePersonLinkList = stagePersonLinkDao
					.getStagePersonLinkByIdStage(req.getIdStage());
			if (!ObjectUtils.isEmpty(stagePersonLinkList)) {
				prefillDto.setStagePersonLinkList(stagePersonLinkList);
				for (StagePersonLinkDto stagePersonLinkDto : stagePersonLinkList) {
					if (ServiceConstants.PRIMARY_WORKER.equals(stagePersonLinkDto.getCdStagePersRole())
							|| ServiceConstants.HIST_PRIM_WORKER.equals(stagePersonLinkDto.getCdStagePersRole())) {
						idPerson = stagePersonLinkDto.getIdPerson();
						break;
					}
				}

				// CSEC01D
				EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
				if (!ObjectUtils.isEmpty(employeePersPhNameDto) && !((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS)
						.equals(employeePersPhNameDto.getCdPhoneType())
						|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType()))) {

					employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
					employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getNbrMailCodePhoneExt());
				}
				prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
			}

			// CINV81D
			PersonGenderSpanishDto personGenderSpanishDto = populateFormDao.isSpanGender(req.getIdPerson());
			prefillDto.setPersonGenderSpanishDto(personGenderSpanishDto);
		}
		prefillDto.setFormName(req.getFormName());

		return prefillData.returnPrefillData(prefillDto);
	}

}
