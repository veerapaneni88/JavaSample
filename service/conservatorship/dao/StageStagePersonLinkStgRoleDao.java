package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleInDto;
import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * acts as the interface for Csub84dDaoImpl Aug 12, 2017- 4:27:05 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface StageStagePersonLinkStgRoleDao {

	/**
	 * 
	 * Method Name: getStageID Method Description:This method acts as the
	 * interface for Csub84dDaoImpl
	 * 
	 * @param stageStagePersonLinkStgRoleInDto
	 * @return List<Csub84doDto> @
	 */
	public List<StageStagePersonLinkStgRoleOutDto> getStageID(
			StageStagePersonLinkStgRoleInDto stageStagePersonLinkStgRoleInDto);
}
