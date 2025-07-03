package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv39d Aug 5, 2017- 10:58:18 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface StagePersonLinkRecordDao {

	/**
	 * 
	 * Method Name: getStagePersonLinkRecord Method Description: This method
	 * will get data from Stage Person Link table.
	 * 
	 * @param stagePersonLinkRecordInDto
	 * @return List<StagePersonLinkRecordOutDto>
	 */
	public List<StagePersonLinkRecordOutDto> getStagePersonLinkRecord(
			StagePersonLinkRecordInDto stagePersonLinkRecordInDto);

	/**
	 * Method Name: getStagePersonLinkCount
	 * Method description : gets the stage person link count for a given person id
	 * @param stagePersonLinkRecordInDto
	 * @return
	 */
	public Integer getStagePersonLinkCount(StagePersonLinkRecordInDto stagePersonLinkRecordInDto);
}
