package us.tx.state.dfps.service.servicedlvryclosure.service;

import java.util.List;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageInDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageOutDto;
import us.tx.state.dfps.service.admin.dto.StageClosureRtrvDto;
import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.response.StageClosureRtrvRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for ServiceDlvryClosureStageService. June 30, 2022- 8:50:18 PM ©
 * 2022 Texas Department of Family and Protective Services
 */
public interface ServiceDlvryClosureStageService {
	/**
	 *
	 * Method Name: retrvDecisionDate Method Description:This Retrieves the
	 * csvc21dQUERYdam Details
	 *
	 * @param serviceDlvryClosureStageInDto
	 * @return List<CsvcServiceDlvryClosureStageOutDto>
	 *
	 */
	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDate(ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto);

	PreFillDataServiceDto getClosureFormInformation(CommonApplicationReq request);

	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDateAps(ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto);

}
