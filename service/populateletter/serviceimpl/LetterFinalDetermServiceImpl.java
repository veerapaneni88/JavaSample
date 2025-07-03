package us.tx.state.dfps.service.populateletter.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.populateletter.dto.LetterFinalDetermDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LetterFinalDetermPrefillData;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.populateletter.service.LetterFinalDetermService;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * method CINV78S Jul 6, 2018- 2:46:37 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class LetterFinalDetermServiceImpl implements LetterFinalDetermService {

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private LetterFinalDetermPrefillData prefillData;

	/**
	 * Method Name: getLetter Method Description: Makes DAO calls and sends data
	 * to prefill
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getLetter(PopulateFormReq req) {
		// Declare global variables and main DTO
		LetterFinalDetermDto prefillDto = new LetterFinalDetermDto();
		Long idPerson = ServiceConstants.ZERO;
		Long idCase = ServiceConstants.ZERO;

		// CLSC03D
		List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
				ServiceConstants.NAME);
		if (!ObjectUtils.isEmpty(codesTablesList)) {
			prefillDto.setCodesTablesDto(codesTablesList.get(0));
		}

		// CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(req.getIdStage(),
				ServiceConstants.PRIMARY_WORKER);
		if (!ObjectUtils.isEmpty(stagePersonDto)) {
			idPerson = stagePersonDto.getIdTodoPersWorker();
			prefillDto.setStagePersonDto(stagePersonDto);

			// CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		// CSES39D
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(req.getIdStage());
		facilInvDtlDto.setDtFacilInvstBegun(new Date());
		prefillDto.setFacilInvDtlDto(facilInvDtlDto);

		// CSEC18D
		List<CaseInfoDto> caseInfoList = populateLetterDao.getReporterInfoById(req.getIdStage());
		if (!ObjectUtils.isEmpty(caseInfoList)) {
			CaseInfoDto caseInfoDto = caseInfoList.get(0);
			for (CaseInfoDto dto : caseInfoList) {
				if (dto.getIdPerson().equals(req.getIdPerson())) {
					caseInfoDto = dto;
				}
			}
			// don't print report if reporter address doesn't exist
			if (StringUtils.isBlank(caseInfoDto.getAddrPersAddrStLn1())) {
				return new PreFillDataServiceDto();
			} else {
				prefillDto.setCaseInfoDto(caseInfoDto);
			}
		}

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(req.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		}

		// CLSCGCD
		List<MultiAddressDto> multiAddressList = notifToLawEnforcementDao.getMultiAddress(req.getIdStage(), idCase);
		if (!ObjectUtils.isEmpty(multiAddressList)) {
			prefillDto.setMultiAddressDto(multiAddressList.get(0));
		}

		return prefillData.returnPrefillData(prefillDto);
	}

}
