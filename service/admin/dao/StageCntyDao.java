package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageCntyInDto;
import us.tx.state.dfps.service.admin.dto.StageCntyOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cses71dDao
 * Aug 11, 2017- 1:38:46 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StageCntyDao {

	/**
	 * 
	 * Method Name: getStageValues Method Description: This method gets data
	 * from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageCntyOutDto> @
	 */
	public List<StageCntyOutDto> getStageValues(StageCntyInDto pInputDataRec);
}
