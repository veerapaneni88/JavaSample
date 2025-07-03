package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.AddressMassUpdateReq;
import us.tx.state.dfps.service.common.request.EditPersonAddressReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.common.response.AddressMassUpdateRes;
import us.tx.state.dfps.service.common.response.EditPersonAddressRes;
import us.tx.state.dfps.service.person.dto.EditPersonAddressDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN44S Class
 * Description:Operations for PersonAddress Apr 14, 2017 - 11:16:39 AM
 */
public interface PersonAddressService {

	/**
	 * This does full row adds and updates of the PERSON_ADDRESS table
	 * savePersonAddress Service Name - CCMN44S, DAM Name - CCMNA8D
	 * 
	 * @param personAddressDto
	 * @
	 */
	public Long editPersonAddress(EditPersonAddressDto editpersonAddressDto, String action);

	/**
	 * This does full row adds and updates of the ADDRESS_PERSON_LINK table.
	 * Although UPDATE will not modify column DT_PERS_ADDR_LINK_START.
	 * saveAddressPersonLink ServiceName - CCMN44S, DAM Name - CCMNA9D
	 * 
	 * @param personAddressDto
	 * @param action
	 * @
	 */
	public String editAddressPersonLink(EditPersonAddressDto editpersonAddressDto, String action, Long ulIdPerson);

	/**
	 * This is the AUD service for the Address List/Detail window
	 * 
	 * @param editPersonAddressReq
	 * @return @
	 */
	public EditPersonAddressRes editPersonAddressDetail(EditPersonAddressReq editPersonAddressReq);

	/**
	 * 
	 * Method Description: This method is to get the List of address of a a
	 * Person from Person Address Table. Tuxedo Servive Name:CCMN42S Tuxedo DAM
	 * Name: CCMN96D
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes @
	 */

	public AddressDtlRes getAddressList(AddressDtlReq addressDtlReq);

	/**
	 * MethodName: getAddressListPullback Method Description: This method is to
	 * get the List of address of a all the persons in a stage.
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes
	 */
	public AddressDtlRes getAddressListPullback(AddressDtlReq addressDtlReq);

	/**
	 * Method Description: This method will perform mass update of address for
	 * person list. Service Name: Address Mass Update
	 * 
	 * @param addressMassUpdateReq
	 * @return AddressMassUpdateRes @
	 */
	AddressMassUpdateRes massAddrUpdate(AddressMassUpdateReq addressMassUpdateReq);

	/**
	 * Method Description: This method will get the most recent residential
	 * address of a person.
	 * 
	 * @param addressDtlReq
	 * @return AddressDtlRes
	 */
	public AddressDtlRes getPersonAddressDtls(AddressDtlReq addressDtlReq);

	/**
	 * Method Description: Returns all the active addresses for people attached
	 * to a stage. Primary Address from the Address_Person_Link table.
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes @
	 */
	public AddressDtlRes getActiveAddressForStage(AddressDtlReq addressDtlReq);
}
