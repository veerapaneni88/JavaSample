package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ContactInDto;
import us.tx.state.dfps.service.admin.dto.ContactOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:DAO Interface for fetching date occured details
 *
 * Aug 6, 2017- 3:46:27 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ContactSearchDtContactOccDao {

	/**
	 * 
	 * Method Name: getDateOccured Method Description: This method will gate the
	 * Date Occurred from Contact table.
	 * 
	 * @param pInputDataRec
	 * @return List<ContactOutDto> @
	 */
	public List<ContactOutDto> getDateOccured(ContactInDto pInputDataRec);
}
