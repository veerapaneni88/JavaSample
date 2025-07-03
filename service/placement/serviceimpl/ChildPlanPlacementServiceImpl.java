package us.tx.state.dfps.service.placement.serviceimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.CPLRSTLK;
import static us.tx.state.dfps.service.common.ServiceConstants.MEDICAID_ADDRESS_CODE;
import static us.tx.state.dfps.service.common.ServiceConstants.PRIMARY_CHILD;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.ChildPlanPlacementDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildPlanPlacementPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.placement.dao.ChildPlanPlacementDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.ApprovalInfoDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.service.ChildPlanPlacementService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: To
 * implement the operations of ChildPlanPlacementService Jan 27, 2018- 1:34:16
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ChildPlanPlacementServiceImpl implements ChildPlanPlacementService {

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private ChildPlanPlacementDao childPlanPlacementDao;

	@Autowired
	private ChildPlanPlacementPrefillData prefillData;

	/**
	 * Service Name: CSUB24S Method Name: getChildPlanPlacement Method
	 * Description: This service will get forms populated by receiving idEvent
	 * and idStage from controller, then retrieving data from caps_case, stage,
	 * code_type, stage_person_link, person, etc tables to get the forms
	 * populated.
	 *
	 * @param idEvent
	 * @param idStage
	 * @return ChildPlanPlacementDto @ the service exception
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getChildPlanPlacement(Long idEvent, Long idStage) {
		ChildPlanPlacementDto childPlanPlacementDto = new ChildPlanPlacementDto();
		StagePersonLinkCaseDto stagePersonLinkCaseDto = null;
		PersonAddressDto personAddressDto = null;
		PlacementDtlDto placementDtlDto = null;
		NameDetailDto nameDetailDto = null;
		ApprovalInfoDto approvalInfoDto = null;
		String decodeValue = null;

		// Call CSEC15D
		stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(idStage, PRIMARY_CHILD);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto)
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			// Call CSEC34D
			personAddressDto = disasterPlanDao.getPersonAddress(stagePersonLinkCaseDto.getIdPerson(),
					MEDICAID_ADDRESS_CODE, Calendar.getInstance().getTime());
		}
		// Call CSES37D
		placementDtlDto = pcaDao.getPlcmntDtls(idEvent);
		if (!ObjectUtils.isEmpty(placementDtlDto) && !ObjectUtils.isEmpty(placementDtlDto.getIdPersonPlcmtAdult())) {
			// Call CSEC35D /* use IdPlcmtAdult as idPerson of input */
			nameDetailDto = commonApplicationDao.getNameDetails(placementDtlDto.getIdPersonPlcmtAdult());

			// Call CSEC88D
			if (!ObjectUtils.isEmpty(placementDtlDto.getCdPlcmtRemovalRsn())
					&& !ObjectUtils.isEmpty(placementDtlDto.getCdRmvlRsnSubtype())) {
				decodeValue = childPlanPlacementDao.getDecodeValue(CPLRSTLK, placementDtlDto.getCdPlcmtRemovalRsn(),
						placementDtlDto.getCdRmvlRsnSubtype());
			}
		}
		/* retrieves approval information */
		// Call CLSC42D
		approvalInfoDto = childPlanPlacementDao.getApprovalInfo(idEvent);

		childPlanPlacementDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		childPlanPlacementDto.setPersonAddressDto(personAddressDto);
		childPlanPlacementDto.setPlacementDtlDto(placementDtlDto);
		childPlanPlacementDto.setNameDetailDto(nameDetailDto);
		childPlanPlacementDto.setApprovalInfoDto(approvalInfoDto);
		childPlanPlacementDto.setDecodeValue(decodeValue);
		return prefillData.returnPrefillData(childPlanPlacementDto);
	}

}