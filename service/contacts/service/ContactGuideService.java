package us.tx.state.dfps.service.contacts.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.FetchContactGuideReq;
import us.tx.state.dfps.service.contact.dto.ContactDetailDto;
import us.tx.state.dfps.service.contact.dto.ContactFetchDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implements
 * methods in ContactGuideBean Sep 6, 2017- 9:35:44 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ContactGuideService {

	/**
	 * Method Name: updatecontact Method Description: Method to
	 * add/update/delete Contact guide information.
	 * 
	 * @param contactDetailDreq
	 * @return ContactDetailDto
	 */
	public ContactDetailDto updatecontact(ContactDetailDto contactDetailDreq);

	/**
	 * Method Name: deleteGuidePlanInfo Method Description: Method to delete the
	 * Contact guide information when Contact is deleted.
	 * 
	 * @param contactDetailDreq
	 * @return ContactDetailDto @
	 */
	public ContactDetailDto deleteGuidePlanInfo(ContactDetailDto contactDetailDreq);

	/**
	 * Method Name: fetchGuideTopicDescr Method Description: Method retrieves
	 * Guide Topic Description
	 * 
	 * @return ContactDetailDto @
	 */
	public List<ContactFetchDto> fetchGuideTopicDescr();

	/**
	 * Method Name: checkIfGuideNarrExists Method Description: Method to check
	 * if Contact Guide Narrative records exist for a given Contact.
	 * 
	 * @param idEvent
	 * @return boolean @
	 */
	public boolean checkIfGuideNarrExists(long idEvent);

	/**
	 * Method Name: fetchContactGuideList Method Description: Method to retrieve
	 * all the Contact guide information for saved records/ to create a List of
	 * Contact Guide value beans for a new contact.
	 * 
	 * @param FetchContactGuideReq
	 * @return List<ContactGuideDto>
	 */
	public List<ContactGuideDto> fetchContactGuideList(FetchContactGuideReq fetchContactGuideReq);

}
