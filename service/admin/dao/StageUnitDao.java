package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageUnitInDto;
import us.tx.state.dfps.service.admin.dto.StageUnitOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * fetches stage details Aug 7, 2017- 8:17:28 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface StageUnitDao {

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from Stage table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageUnitOutDto> @
	 */
	public List<StageUnitOutDto> getStageDetails(StageUnitInDto pInputDataRec);

}
