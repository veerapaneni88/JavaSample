package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.admin.dto.PersonDetailUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonDtlUpdateDto;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.person.dto.PersonIdDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 11:53:14 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonDetailUpdateService {

	/**
	 * 
	 * Method Name: callPersonDetailUpdateService Method Description: service
	 * invokes to update person investigation details
	 * 
	 * @param pInputMsg
	 * @return PersonDetailUpdateoDto
	 */
	public PersonDetailUpdateDto personDetailUpdate(PersonDtlUpdateDto personDetailUpdateiDto);

    public IncomingPersonMpsRes personDetailUpdateForMPS(IncomingPersonMpsDto incomingPersonMpsDto);

	public PersonIdDto addMpsPersonDetails(Long idPerson);
}
