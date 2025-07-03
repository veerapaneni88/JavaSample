package us.tx.state.dfps.service.sscc.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: The
 * interfaces to retrieve the SSCCExceptCareDesignationDao Aug 7, 2018- 5:26:34
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface SSCCExceptCareDesignationDao {

	List<SSCCExceptCareDesignationDto> getSSCCExceptCareList(Long idCase, Long idStage);

	List<SSCCExceptCareDesignationDto> getEligibilityPlcmtInfo(Long idStage);

	SSCCExceptCareDesignationDto getExistECDesig(Long idECDesig);

	SSCCExceptCareDesignationDto getSsccRsrcContractInfo(Long idStage, Long idPlcmntEvent);

	SSCCExceptCareDesignationDto saveOrUpdateExceptCareDesig(SSCCExceptCareDesignationDto sSCCExceptCareDesignationDto,
			String action);

	SSCCExceptCareDesignationDto getExcpCareOnSaveAndApprove(Long idECDesig);

	List<SSCCExceptCareDesignationDto> getActiveSsccPlcmt(Long idStage);

	SSCCExceptCareDesignationDto getActiveChildPlcmtRefferal(Long idStage);

	Boolean getUpdateSSCCExceptCareDesigStatus(String cdStatus, Long idExceptCareDesig);

	Boolean getUpdateCdExceptCareStatus(String cdExceptCareStatus, Long idSsccReferral);

	List<ExceptionalCareDto> getExceptCareList(Long idCase, Long idStage);

	SSCCExceptCareDesignationDto getSSCCExceptCareDays(Long idSsccReferral);
}
