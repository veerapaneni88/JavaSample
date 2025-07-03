package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clsc72 Aug 10, 2017- 11:38:55 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface StageStagePersonLinkDao {

	/**
	 * 
	 * Method Name: retrieveStagePersonLinkPID Method Description: It retrieves
	 * StagePersonLink record for provided person id.
	 * 
	 * @param pInputDataRec
	 * @return List<Clsc72doDto> @
	 */
	public List<StageStagePersonLinkOutDto> retrieveStagePersonLinkPID(StageStagePersonLinkInDto pInputDataRec);

	/**
	 * Method Name: getPrimaryWorkerIdForStage
	 * Method Description: Retrieve the primary (or historical primary) worker ID for a stage.
	 *
	 * @param ulIdStage the stage to search
	 * @return personId of the person who is the primary or historical primary for the stage.
	 */
	public long getPrimaryWorkerIdForStage(Long ulIdStage);
}
