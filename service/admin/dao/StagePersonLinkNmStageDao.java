package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clscf9 Aug 10, 2017- 11:04:00 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface StagePersonLinkNmStageDao {

	/**
	 * 
	 * Method Name: verifyPersonDeleted Method Description: Verify the person
	 * has case name Clscf9d
	 * 
	 * @param stagePersonLinkNmStageInDto
	 * @return List<StagePersonLinkNmStageOutDto>
	 */
	public List<StagePersonLinkNmStageOutDto> verifyPersonDeleted(
			StagePersonLinkNmStageInDto stagePersonLinkNmStageInDto);
}
