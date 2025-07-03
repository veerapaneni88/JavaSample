package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for getting stage details> Aug 4, 2017- 12:24:37 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageSituationDao {

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageSituationOutDto> @
	 */
	public List<StageSituationOutDto> getStageDetails(StageSituationInDto pInputDataRec);
}
