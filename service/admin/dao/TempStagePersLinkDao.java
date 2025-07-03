package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.TempStagePersLinkInDto;
import us.tx.state.dfps.service.admin.dto.TempStagePersLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses92d Aug 11, 2017- 5:32:44 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface TempStagePersLinkDao {

	/**
	 * 
	 * Method Name: getTempStagePersonLink Method Description: Get temporary
	 * stage person record for given stage id. Cses92d
	 * 
	 * @param tempStagePersLinkInDto
	 * @return List<TempStagePersLinkOutDto>
	 */
	public List<TempStagePersLinkOutDto> getTempStagePersonLink(TempStagePersLinkInDto tempStagePersLinkInDto);
}
