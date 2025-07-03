package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageInDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Retrieves the csvc21dQUERYdam Details Aug 23, 2017- 5:01:47 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ServiceDlvryClosureStageDao {

	/**
	 * 
	 * Method Name: retrvDecisionDate Method Description:This Retrieves the
	 * csvc21dQUERYdam Details
	 * 
	 * @param serviceDlvryClosureStageInDto
	 * @return List<CsvcServiceDlvryClosureStageOutDto>
	 * 
	 */
	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDate(
			ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto);

	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDateAps(
			ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto);
}
