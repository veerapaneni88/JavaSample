package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cres07dDao
 * Aug 22, 2017- 12:03:09 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface FacilityLocOrderedDao {

	/**
	 * Method Name: getFacilityLocOrderedOutDtoList Method Description: This
	 * method is used to
	 * 
	 * @param facilityLocOrderedInDto
	 * @ @return List<FacilityLocOrderedOutDto>
	 */
	public List<FacilityLocOrderedOutDto> getFacilityLocOrderedOutDtoList(
			FacilityLocOrderedInDto facilityLocOrderedInDto);
}
