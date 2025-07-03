package us.tx.state.dfps.service.medicalconsenter.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.MedicalConsenterForNonDFPSEmployeePrefillData;
import us.tx.state.dfps.service.handwriting.dao.HandWritingDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.medicalconsenter.dao.MedicalConsenterRtrvDao;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalConsenterFormService;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.MedicalDto;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * IMPACT PHASE 2 MODERNIZATION Class Description:This service is used to launch
 * the Medical Consenter forms. Oct 30, 2017- 5:13:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class MedicalConsenterFormServiceImpl implements MedicalConsenterFormService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	MedicalConsenterRtrvDao medicalConsenterRtrvDao;

	@Autowired
	MedicalConsenterForNonDFPSEmployeePrefillData medicalConsenterForNonDFPSEmployeeForm;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	HandWritingDao handWritingDao;

	private static final Logger log = Logger.getLogger(MedicalConsenterFormServiceImpl.class);

	/**
	 * Method Description:Method to launch Medical Consenter forms Service name
	 * : CMED01S
	 * 
	 * @param pInputMsg
	 * @return pOutputMsg @
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getMedicalConsentForms(MedicalConsenterDto medicalConsentInDto) {
		log.debug("Entering method getMedicalConsentForms in MedicalConsenterFormServiceImpl");
		MedicalDto medicalDto = new MedicalDto();
		medicalDto.setIdPerson(medicalConsentInDto.getIdPerson());
		medicalDto.setIdEvent(medicalConsentInDto.getIdEvent());

		// CSECD3D
		PersonDto persDto = personIdDtlsDao.getPersonIdRecords(medicalConsentInDto.getIdCase(),
				medicalConsentInDto.getIdStage(), medicalConsentInDto.getIdPerson());
		medicalDto.setPersonDto(persDto);

		// CSECD5D
		LegalStatusOutDto legalStatusOutDto = personIdDtlsDao.getLegalStatusRecords(medicalConsentInDto.getIdPerson(),
				medicalConsentInDto.getIdCase());
		if (!ObjectUtils.isEmpty(legalStatusOutDto) && !ObjectUtils.isEmpty(legalStatusOutDto.getCdLegalStatCnty())) {
			legalStatusOutDto.setCdLegalStatCnty(
					lookupDao.decode(ServiceConstants.CCOUNT, legalStatusOutDto.getCdLegalStatCnty()));
		}
		medicalDto.setLegalStatusOutDto(legalStatusOutDto);

		// CLSS99D
		List<MedicalConsenterDto> medicalList = medicalConsenterRtrvDao.getMedicalConsenterDtls(medicalConsentInDto);
		medicalDto.setMedicalConsenterDtoList(medicalList);

		// CSECD4D
		List<MedicalConsenterDto> medicalConsentList = medicalConsenterRtrvDao
				.getMedicalConsenterRecords(medicalConsentInDto);
		medicalDto.setMedicalConsnterDto(medicalConsentList);

		// Fetch DFPS Case Worker & Supervisor Details - Call CINV51D
		Long idPersonCaseWrkr = workLoadDao.getPersonIdByRole(medicalConsentInDto.getIdStage(),
				ServiceConstants.STAGE_PERS_ROLE_PR);
		if (!ObjectUtils.isEmpty(idPersonCaseWrkr)) {
			// Call CSEC01D
			EmployeePersPhNameDto employeePersCsWrkrDto = employeeDao.searchPersonPhoneName(idPersonCaseWrkr);
			medicalDto.setEmployeePersCsWrkrDto(employeePersCsWrkrDto);
			if (!ObjectUtils.isEmpty(employeePersCsWrkrDto)
					&& !ObjectUtils.isEmpty(employeePersCsWrkrDto.getIdJobPersSupv())) {
				EmployeePersPhNameDto employeePersSuprvsrDto = employeeDao
						.searchPersonPhoneName(employeePersCsWrkrDto.getIdJobPersSupv());
				medicalDto.setEmployeePersSuprvsrDto(employeePersSuprvsrDto);
			}
		}

		// calling prefillData method with medicalDto as I/p parameter
		return medicalConsenterForNonDFPSEmployeeForm.returnPrefillData(medicalDto);

	}
}