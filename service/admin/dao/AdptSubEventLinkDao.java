package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clssc4d Aug 10, 2017- 6:49:23 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface AdptSubEventLinkDao {

	/**
	 * 
	 * Method Name: getAdoptionEventLink Method Description: Fetch event for
	 * given Adoption subsidy
	 * 
	 * @param pInputDataRec
	 * @return List<Clssc4doDto> @
	 */
	public List<AdptSubEventLinkOutDto> getAdoptionEventLink(AdptSubEventLinkInDto pInputDataRec);
}
