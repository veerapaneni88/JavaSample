package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageAssignInDto;
import us.tx.state.dfps.service.admin.dto.StageAssignOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for StageAssignDaoImpl Aug 7, 2017- 6:56:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface StageAssignDao {

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageAssignOutDto> @
	 */
	public List<StageAssignOutDto> getStageDetails(StageAssignInDto pInputDataRec);
}
