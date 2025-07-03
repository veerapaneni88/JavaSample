package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageLinkStageInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for getting Prior Stage from Stage_Link> Aug 8, 2017- 3:51:06 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface StageLinkStageDao {

	/**
	 * 
	 * Method Name: getPriorStage Method Description: This method will get
	 * IdPrior Stage from Stage Person Link and Stage table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageLinkStageOutDto> @
	 */
	public List<StageLinkStageOutDto> getPriorStage(StageLinkStageInDto pInputDataRec);
}
