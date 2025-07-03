/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 20, 2017- 12:09:01 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.AddressValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Sep 20, 2017- 12:09:01 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public interface AddressDao {

	/**
	 * Method Name: isRmRsAddressExist Method Description: This method checks if
	 * any open address of following address types exists Residence-Mailing,
	 * Residence
	 * 
	 * @param personId
	 * @return Boolean
	 */
	public Boolean isRmRsAddressExist(long personId);

	/**
	 * Method Name: fetchCurrentPrimaryAddress Method Description: This method
	 * fetches the current primary address for a person
	 * 
	 * @param idForwardPerson
	 * @return AddressValueDto
	 * @throws DataNotFoundException
	 */
	public AddressValueDto fetchCurrentPrimaryAddress(Long idForwardPerson);

	/**
	 * 
	 * Method Name: fetchCurrentPrimaryAddress Method Description: This method
	 * fetches the current primary address for a person from snapshot tables
	 * (SS_ADDRESS_PERSON_LINK, SS_PERSON_ADDRESS) This is used for showing
	 * Select Person Forward data in Post person merge page.
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return
	 */
	public AddressValueDto fetchCurrentPrimaryAddress(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType);
	
	
	/**
	 * Method Name: fetchCurrentPrimaryAddressList Method Description: This method
	 * fetches the current primary address for a person
	 * 
	 * @param idForwardPerson
	 * @return AddressValueDto
	 * @throws DataNotFoundException
	 */
	// Added a new Method for Fetching AddressList - Warranty Defect 10792
	public List<AddressValueDto> fetchCurrentPrimaryAddressList(Long idForwardPerson);

}
