package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PlacementEventInDto;
import us.tx.state.dfps.service.admin.dto.PlacementEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Cinve9 Aug 6, 2017- 10:24:39 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PlacementEventDao {

	/**
	 * 
	 * Method Name: hasPLforStage Method Description: This method will give data
	 * from PLACEMENT and EVENT table.
	 * 
	 * @param placementEventInDto
	 * @return List<PlacementEventOutDto>
	 */
	public List<PlacementEventOutDto> hasPLforStage(PlacementEventInDto placementEventInDto);
}
