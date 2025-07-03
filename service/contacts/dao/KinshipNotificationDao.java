package us.tx.state.dfps.service.contacts.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

public interface KinshipNotificationDao {
	/**
	 * 
	 * Method Name: getPersonNamesDtls Method Description:This method fetches
	 * the names of Person Kinship Notification
	 * 
	 * @param idEvent
	 * @return List<PersonDto>
	 */

	List<PersonDto> getPersonNamesDtls(Long idEvent);

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method fetches the
	 * Address of Kinship Notification
	 * 
	 * @param idEvent
	 * @return List<PersonAddressDto>
	 */
	List<PersonAddressDto> getPersonAddress(Long idEvent);

	/**
	 * 
	 * Method Name: getPersonPhoneDetailList Method Description:This method
	 * fetches the Person Phone Details of Kinship Notification
	 * 
	 * @param idCaseWorker
	 * @return List<PersonPhoneRetDto>
	 */
	List<PersonPhoneRetDto> getPersonPhoneDetailList(Long idCaseWorker);
}