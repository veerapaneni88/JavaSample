package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountInDto;
import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv87dDaoImpl Aug 5, 2017- 5:19:18 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface AllegationFacilAllegCountDao {

	/**
	 * 
	 * Method Name: getAllAllegationsPID Method Description: This method will
	 * get the count of ID_ALLEGATION meeting the criteria.
	 * 
	 * @param allegationFacilAllegCountInDto
	 * @return List<AllegationFacilAllegCountOutDto>
	 */
	public List<AllegationFacilAllegCountOutDto> getAllAllegationsPID(
			AllegationFacilAllegCountInDto allegationFacilAllegCountInDto);
}
