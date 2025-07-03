package us.tx.state.dfps.service.pca.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PcaApplicationReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.DenialFormPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.DenailLetterDto;
import us.tx.state.dfps.service.pca.dto.PcaEligApplicationDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.pca.dto.ResourcePlcmntDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.pca.service.PcaService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaServiceImpl will implemented all operation defined in
 * PcaService Interface related PCA module. Feb 9, 2018- 2:01:28 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
public class PcaServiceImpl implements PcaService {

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private DenialFormPrefillData denialFormPrefillData;

	/**
	 * Method Description: This method is used to retrieve the information for
	 * ADOPTION ASSISTANCE/PCA DENIAL LETTER by passing IdStage and IdPerson as
	 * input request
	 * 
	 * @param pcaApplicationReq
	 * @return PcaApplicationRes @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getDenailLetter(PcaApplicationReq pcaApplicationReq) {

		DenailLetterDto denailLetterDto = new DenailLetterDto();
		// Long idPerson = ServiceConstants.ZERO_VAL;
		Long idPlcmtChild = ServiceConstants.ZERO_VAL;
		Long idPlcmtAdult = ServiceConstants.ZERO_VAL;
		Long idPcmntEvent = ServiceConstants.ZERO_VAL;
		// Call CSEC02D
		StageCaseDtlDto stageCaseDtlDto = pcaDao.getStageAndCaseDtls(pcaApplicationReq.getIdStage());
		denailLetterDto.setStageCaseDtlDto(stageCaseDtlDto);
		// Call CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(pcaApplicationReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		denailLetterDto.setStagePersonDto(stagePersonDto);
		if (null != pcaApplicationReq.getIdPerson()
				&& !ServiceConstants.ZERO_VAL.equals(pcaApplicationReq.getIdPerson())) {
			// Call CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao
					.searchPersonPhoneName(pcaApplicationReq.getIdPerson());
			denailLetterDto.setEmployeePersPhNameDto(employeePersPhNameDto);
			// Call CCMN60D
			SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(pcaApplicationReq.getIdPerson());
			denailLetterDto.setSupervisorDto(supervisorDto);
		}
		// Call CSEC58D
		List<PersonDtlDto> personDtlList = personDao.getPersonDtlByIdStage(pcaApplicationReq.getIdStage(),
				ServiceConstants.PRIMARY_CHILD);
		denailLetterDto.setPersonDtlList(personDtlList);
		if (!ObjectUtils.isEmpty(personDtlList)) {
			for (PersonDtlDto personDtlDto : personDtlList) {
				idPlcmtChild = personDtlDto.getIdPerson();
			}
		}

		if (!ObjectUtils.isEmpty(idPlcmtChild)) {
			// Call CSES28D
			ResourcePlcmntDto resourcePlcmntDto = pcaDao.getResourcePlcmntDtls(idPlcmtChild);
			if (!ObjectUtils.isEmpty(resourcePlcmntDto)
					&& !ObjectUtils.isEmpty(resourcePlcmntDto.getIdPersonPlcmtAdult())) {
				idPlcmtAdult = resourcePlcmntDto.getIdPersonPlcmtAdult();
			}
			denailLetterDto.setResourcePlcmntDto(resourcePlcmntDto);
			// Call CSECE0D
			List<PcaEligApplicationDto> pcaEligApplicationDtoList = pcaDao.getPlcmntEvent(idPlcmtChild);
			denailLetterDto.setPcaEligApplicationDtoList(pcaEligApplicationDtoList);
			if (null != pcaEligApplicationDtoList && pcaEligApplicationDtoList.size() > ServiceConstants.Zero) {
				for (PcaEligApplicationDto pcaEligApplicationDto : pcaEligApplicationDtoList) {
					if ((!ServiceConstants.CD_STAGE_PCA.equalsIgnoreCase(stageCaseDtlDto.getCdStage())
							|| !ServiceConstants.SUB_CARE.equalsIgnoreCase(stageCaseDtlDto.getCdStage()))
							&& pcaEligApplicationDto.getIdPlcmntEvent() != ServiceConstants.ZERO_VAL) {
						idPcmntEvent = pcaEligApplicationDto.getIdPlcmntEvent();
						if (!ObjectUtils.isEmpty(idPcmntEvent)) {
							// Call CSES37D
							PlacementDtlDto placementDtlDto = pcaDao.getPlcmntDtls(idPcmntEvent);
							denailLetterDto.setPlacementDtlDto(placementDtlDto);
							idPlcmtAdult = placementDtlDto.getIdPersonPlcmtAdult();
						}

					}
				}
			}
			// Call CCMN44D
			PersonDto personDto = personDao.getPersonById(idPlcmtChild);
			denailLetterDto.setPersonDto(personDto);
		}

		// Call CSEC34D
		if (!ObjectUtils.isEmpty(idPlcmtAdult)) {
			PersonAddressDto personAddressDto = disasterPlanDao.getPersonAddress(idPlcmtAdult,
					ServiceConstants.EVAC_ADDRESS_PERS, new Date());
			denailLetterDto.setPersonAddressDto(personAddressDto);
		}

		return denialFormPrefillData.returnPrefillData(denailLetterDto);
	}
}
