package us.tx.state.dfps.service.workload.service;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.RetrieveOnCallDetailDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The purpose
 * of this class is to retrieve details from EMP_ON_CALL_LINK table using
 * ulIdOnCall Aug 4, 2017- 2:38:10 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface RetrieveOnCallDetailService {

	/**
	 * 
	 * Method Name: callRetrieveOnCallDetailService Method Description: This
	 * service invokes callRetrieveOnCallDetailService method and retrieves
	 * OnCall details.
	 * 
	 * @param retrieveOnCallDetailiDto
	 * @return List<RetrieveOnCallDetailoDto>
	 */
	public List<RetrieveOnCallDetailDto> callRetrieveOnCallDetailService(
			RetrieveOnCallDetailDto retrieveOnCallDetailiDto);
}
