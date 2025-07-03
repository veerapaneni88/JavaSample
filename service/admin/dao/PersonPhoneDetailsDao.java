package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailReq;
import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonPhoneDetailsDao Jul 6, 2018- 11:45:47 AM © 2017 Texas Department of
 * Family and Protective Services.
 */
public interface PersonPhoneDetailsDao {

	/**
	 * Gets the person phone dtls.
	 *
	 * @param personPhoneDetailReq
	 *            the person phone detail req
	 * @return the person phone dtls
	 */
	public List<PersonPhoneDetailsDto> getPersonPhoneDetails(PersonPhoneDetailReq personPhoneDetailReq);
}
