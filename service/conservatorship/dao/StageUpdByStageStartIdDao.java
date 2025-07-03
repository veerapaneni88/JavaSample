package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.StageUpdByStageStartIdInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Interface Updates Stage Details Aug 14, 2017- 10:20:00 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageUpdByStageStartIdDao {

	public int setStgDetails(StageUpdByStageStartIdInDto stageUpdByStageStartIdInDto);

	/**
	 * This method updates the stage
	 * 
	 * @param cinvc4di
	 * @return @
	 */
	public long updateStage(StageUpdByStageStartIdInDto stageUpdByStageStartIdInDto);
}
