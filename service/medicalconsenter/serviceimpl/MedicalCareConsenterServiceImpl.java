package us.tx.state.dfps.service.medicalconsenter.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.MedCnsntrFormLog;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.MedicalCareConsenterReq;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.MedicalCareConsenterPrefillData;
import us.tx.state.dfps.service.medcareconsenter.dto.MedicalCareConsenterDto;
import us.tx.state.dfps.service.medcareconsenter.dto.PersonPhoneMedCareDto;
import us.tx.state.dfps.service.medicalconsenter.dao.MedicalConsenterRtrvDao;
import us.tx.state.dfps.service.medicalconsenter.dao.NamePersonPhoneDao;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalCareConsenterService;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterFormLogDto;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalCareConsenterServiceImpl to get the information for the
 * Medical Care consenter Form. Feb 9, 2018- 1:43:18 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class MedicalCareConsenterServiceImpl implements MedicalCareConsenterService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageDao stageDao;

	@Autowired
	NamePersonPhoneDao namePersonPhoneDao;

	@Autowired
	PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	MedicalConsenterRtrvDao medicalConsenterRtrvDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	MedicalCareConsenterPrefillData medicalCareConsenterPrefillData;

	@Autowired
	MedicalConsenterDao medicalConsenterDao;

	private static final Logger log = Logger.getLogger(MedicalCareConsenterServiceImpl.class);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * Medical Consenter forms. CMED03S service is converted to this Service.
	 * 
	 * @param MedicalCareConsenterReq
	 * @return MedicalCareConsenterDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto callMedicalCareConsenterService(MedicalCareConsenterReq medCareConsenterReq) {
		log.info("Entering the Method callMedicalCareConsenterService");
		MedicalCareConsenterDto medicalCareConsenterDto = new MedicalCareConsenterDto();

		// CSECD3D
		PersonDto persDto = personIdDtlsDao.getPersonIdRecords(medCareConsenterReq.getIdCase(),
				medCareConsenterReq.getIdStage(), medCareConsenterReq.getIdPerson());
		medicalCareConsenterDto.setPersonDto(persDto);

		// CSECD5D
		LegalStatusOutDto legalStatusOutDto = personIdDtlsDao.getLegalStatusRecords(medCareConsenterReq.getIdPerson(),
				medCareConsenterReq.getIdCase());
		medicalCareConsenterDto.setLegalStatusOutDto(legalStatusOutDto);

		MedicalConsenterDto medConsent = new MedicalConsenterDto();
		medConsent.setIdPerson(medCareConsenterReq.getIdPerson());
		medConsent.setIdCase(medCareConsenterReq.getIdCase());
		// CLSS99D
		List<MedicalConsenterDto> medicalConsenterDtlsList = medicalConsenterRtrvDao
				.getMedicalConsenterDtls(medConsent);
		medicalCareConsenterDto.setMedicalConsenterDtlsList(medicalConsenterDtlsList);

		// CSECD4D
		medConsent.setIdMedConsenter(medCareConsenterReq.getIdMedCons());
		List<MedicalConsenterDto> medicalConsenterRecsList = medicalConsenterRtrvDao
				.getMedicalConsenterRecords(medConsent);
		medicalCareConsenterDto.setMedicalConsenterRecsList(medicalConsenterRecsList);

		// Call CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(medCareConsenterReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		medicalCareConsenterDto.setStagePersonDto(stagePersonDto);
		// Call CSEC01D EmployeePersPhNameDto
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
		medicalCareConsenterDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		// Call CSES29D
		PersonPhoneMedCareDto personPhoneMedCareDto = namePersonPhoneDao
				.getPersonPhoneRecords(stagePersonDto.getIdTodoPersWorker());

		medicalCareConsenterDto.setPersonPhoneMedCareDto(personPhoneMedCareDto);

		return medicalCareConsenterPrefillData.returnPrefillData(medicalCareConsenterDto);
	}

	@Override
	public MedicalConsenterRes getMedCnsntrFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		MedCnsntrFormLog medCnsntrFormLog = new MedCnsntrFormLog();
		List<MedicalConsenterFormLogDto> medCnsntrFormLogList = new ArrayList<MedicalConsenterFormLogDto>();
		medCnsntrFormLog = medicalConsenterDao.getMedCnsntrFormLog(medicalConsenterFormLogReq);

		if (!ObjectUtils.isEmpty(medCnsntrFormLog)) {
			MedicalConsenterFormLogDto medCnsntrFormLogDto = new MedicalConsenterFormLogDto();
			medCnsntrFormLogDto.setCdFormStatus(medCnsntrFormLog.getCdFormStatus());
			medCnsntrFormLogList.add(medCnsntrFormLogDto);
			medicalConsenterRes.setMedicalConsenterFormLogDtos(medCnsntrFormLogList);
		}

		return medicalConsenterRes;

	}

}
