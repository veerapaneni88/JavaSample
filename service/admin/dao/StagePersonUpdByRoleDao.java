package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.StagePersonUpdByRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonUpdByRoleOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 4:19:22 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StagePersonUpdByRoleDao {

	/**
	 * 
	 * Method Name: updateStagePersonDetails Method Description: This method
	 * will update StagePersonLink table.
	 * 
	 * @param pInputDataRec
	 * @return StagePersonUpdByRoleOutDto @
	 */
	public StagePersonUpdByRoleOutDto updateStagePersonDetails(StagePersonUpdByRoleInDto pInputDataRec);
}
