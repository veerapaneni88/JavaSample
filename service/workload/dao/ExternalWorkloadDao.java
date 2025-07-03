/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 13, 2018- 11:15:09 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.RCCPWorkloadDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 13, 2018- 11:15:09 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ExternalWorkloadDao {

	/**
	 * 
	 * Method Name: getExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadDto
	 * @return List<RCCPWorkloadDto>
	 */
	List<RCCPWorkloadDto> getExternalWorkloadDetails(RCCPWorkloadDto rccpWorkloadDto);

	/**
	 * 
	 * Method Name: searchExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadDto
	 * @return List<RCCPWorkloadDto>
	 */
	List<RCCPWorkloadDto> searchExternalWorkloadDetails(RCCPWorkloadDto rccpWorkloadDto);

}
