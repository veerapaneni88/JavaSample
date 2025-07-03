package us.tx.state.dfps.service.contacts.dao;

import java.util.List;

import us.tx.state.dfps.service.contact.dto.ContactFetchDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * methods in ContactGuideBean Sep 6, 2017- 9:42:56 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ContactGuideDao {

	/**
	 * Method Name: saveGuidePlanForPrincipal Method Description: Method to save
	 * the Contact guide narrative into the Contact_guide_narr table.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 * 
	 */
	public ContactGuideDto saveGuidePlanForPrincipal(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: saveGuideTopics Method Description: Method to save the Guide
	 * Topic information for a person contacted.
	 * 
	 * @param contactGuideDto
	 * @param guideTopic
	 * @return long @
	 */
	public long saveGuideTopics(ContactGuideDto contactGuideDto, String guideTopic);

	/**
	 * Method Name: deleteContactGuideTopic Method Description: Method to delete
	 * the Contact Guide Topics
	 * 
	 * @param contactGuideDto
	 * @return long @
	 */
	public long deleteContactGuideTopic(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: deleteContactGuidePlan Method Description: Method to delete
	 * the Contact Guide Narrative
	 * 
	 * @param contactGuideDto
	 * @return long @
	 */
	public long deleteContactGuidePlan(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: updateContactGuidePlan Method Description: Method to update
	 * the Contact Guide Narrative
	 * 
	 * @param contactGuideDto
	 * @return long
	 */
	public long updateContactGuidePlan(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: saveNarrColCargvr Method Description: Method to save
	 * Narrative information for Collateral/Caregiver.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto saveNarrColCargvr(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: saveCaregvrGuideTopics Method Description: Method to save
	 * the Guide Topic information for Caregivers.
	 * 
	 * @param contactGuideDto
	 * @param guideTopic
	 * @return long
	 */
	public long saveCaregvrGuideTopics(ContactGuideDto contactGuideDto, String guideTopic);

	/**
	 * Method Name: fetchGuideTopicDescr Method Description: This method
	 * retrieves the description of Guide Topics.
	 * 
	 * @return ContactDetailDto
	 */
	public List<ContactFetchDto> fetchGuideTopicDescr();

	/**
	 * Method Name: checkifGuideNarrExists Method Description: Method to
	 * indicate if a Contact has Contact Guide Narrative records
	 * 
	 * @param idEvent
	 * @return boolean
	 */
	public boolean checkifGuideNarrExists(long idEvent);

	/**
	 * Method Name: isPrincipalParent Method Description: Identifies if a person
	 * is a parent.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto isPrincipalParent(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: isStageOpen Method Description: Checks if Stage is open
	 * 
	 * @param contactGuideDto
	 * @return boolean
	 */
	public boolean isStageOpen(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: princChildPC Method Description: Method to determine whether
	 * principal was child (Under 18) when the event_person_link was established
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	public ContactGuideDto princChildPC(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: princChildInfoWhenStageClose Method Description: Method to
	 * determine whether principal was child (Under 18) when the
	 * event_person_link was established
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	public ContactGuideDto princChildInfoWhenStageClose(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: princHasSUBStage Method Description: Method to identify if
	 * person has a SUBCARE stage.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	public ContactGuideDto princHasSUBStage(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: isPrincChild Method Description: Method to identify if
	 * person is a Child.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto isPrincChild(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: fetchContactGuidePlan Method Description: Method to fetch
	 * the Contact Guide Narrative for a Principal(Parent/Child) who was
	 * contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto fetchContactGuidePlan(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: fetchGuideTopicsForPerson Method Description: Method to
	 * fetch Guide Topics for person contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto fetchGuideTopicsForPerson(ContactGuideDto contactGuideDto);

	/**
	 * Method Name: fetchGuidePlanNarr Method Description: Method to fetch
	 * Contact Guide Narrative for Caregiver/Collateral contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	public ContactGuideDto fetchGuidePlanNarr(ContactGuideDto contactGuideDto);

}
