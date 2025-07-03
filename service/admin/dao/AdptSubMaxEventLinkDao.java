package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clssc5 Aug 10, 2017- 10:17:31 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface AdptSubMaxEventLinkDao {

	/**
	 * 
	 * Method Name: getMaxEventID Method Description: Fetch latest event id.
	 * 
	 * @param pInputDataRec
	 * @return List<Clssc5doDto>
	 * @, DataNotFoundException
	 */
	public List<AdptSubMaxEventLinkOutDto> getMaxEventID(AdptSubMaxEventLinkInDto pInputDataRec);
}
