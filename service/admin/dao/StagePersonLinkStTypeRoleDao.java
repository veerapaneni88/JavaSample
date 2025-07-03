package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cint20dDao
 * Aug 6, 2017- 2:59:27 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StagePersonLinkStTypeRoleDao {

	/**
	 * 
	 * Method Name: stagePersonDtls Method Description: This method will get
	 * data from Stage Person Link table.
	 * 
	 * @param pInputDataRec
	 * @return List<StagePersonLinkStTypeRoleOutDto> @
	 */
	public List<StagePersonLinkStTypeRoleOutDto> stagePersonDtls(StagePersonLinkStTypeRoleInDto pInputDataRec)
			throws DataNotFoundException;
}
