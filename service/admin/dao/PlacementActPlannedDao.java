package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses34d Aug 10, 2017- 11:18:31 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PlacementActPlannedDao {

	/**
	 * 
	 * Method Name: getPlacementRecord Method Description: Get placement record
	 * for given person. Dam Name: CSES34D For CCMM35S Service.
	 * 
	 * @param idPerson
	 * @return PlacementActPlannedOutDto
	 * @throws DataNotFoundException
	 */
	public PlacementActPlannedOutDto getPlacementRecord(Long idPerson) throws DataNotFoundException;

	public List<PlacementActPlannedOutDto> getPlacementRecord(PlacementActPlannedInDto pCSES34DInputRec);
}
