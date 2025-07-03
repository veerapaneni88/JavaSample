package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageDetailsDiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageDetailsDao Sep 8, 2017- 7:29:02 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface StageDetailsDao {

	/**
	 * Method Name: getStageDtls Method Description: Retrieves the Stage
	 * Details.
	 * 
	 * @param stageDetailsDiDto
	 * @return List<StageDetailsDoDto>
	 */
	public List<StageDetailsDoDto> getStageDtls(StageDetailsDiDto stageDetailsDiDto);

}
