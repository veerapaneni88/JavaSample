package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsDto;
import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * AddressPhoneDetailsDao Jul 6, 2018- 11:43:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonAddressDetailsDao {

	/**
	 * Gets the person address dtls.
	 *
	 * @param personRetrvAddressDetailsInDto
	 *            the person retrv address details in dto
	 * @return the person address dtls
	 */
	public List<PersonAddressDetailsDto> getPersonAddressDtls(PersonAddressDetailsReq personRetrvAddressDetailsInDto);
}
