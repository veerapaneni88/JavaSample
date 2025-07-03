package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateInDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn40 Aug 5, 2017- 12:58:24 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface NamePrimayEndDateDao {

	/**
	 * 
	 * Method Name: getFullName Method Description: This method will get data
	 * from Name table.
	 * 
	 * @param namePrimayEndDateInDto
	 * @return List<NamePrimayEndDateOutDto>
	 */
	public List<NamePrimayEndDateOutDto> getFullName(NamePrimayEndDateInDto namePrimayEndDateInDto);
}
