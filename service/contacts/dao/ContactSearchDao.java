package us.tx.state.dfps.service.contacts.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.InrFollowupPendingReq;
import us.tx.state.dfps.service.common.request.StageSearchReq;
import us.tx.state.dfps.service.common.response.StageSearchRes;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchListDto;
import us.tx.state.dfps.service.contact.dto.InrContactFollowUpPendingDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FindContactDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactSearchDao Aug 2, 2018- 6:02:51 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ContactSearchDao {

	/**
	 * 
	 * Method Name: searchContacts Method Description: searchContacts
	 * 
	 * @param personIds
	 * @param indPersonPhonePrimary
	 * @param cdPersonPhoneType
	 * @param cdEventStatus
	 * @param idCase
	 * @param dtScrSearchDateFrom
	 * @param dtScrSearchDateTo
	 * @param idEvent
	 * @param cdContactType
	 * @param cdContactPurpose
	 * @param cdContactMethod
	 * @param cdContactLocation
	 * @param cdContactOthers
	 * @param IdStage
	 * @return List
	 */
	public List<ContactSearchListDto> searchContacts(List<Long> personIds, String indPersonPhonePrimary,
			String cdPersonPhoneType, String cdEventStatus, Long idCase, Date dtScrSearchDateFrom,
			Date dtScrSearchDateTo, Long idEvent, String cdContactType, String cdContactPurpose, String cdContactMethod,
			String cdContactLocation, String cdContactOthers, List<Long> IdStage);

	/**
	 * Method Name: indStructNarrExists Method Description: get ind narrative
	 * exists
	 * 
	 * @param idEvent
	 * @return boolean
	 */
	public boolean indStructNarrExists(Long idEvent);

	/**
	 * Method Name: getPersonDetailsForEvent Method Description: get Person
	 * Details For the given idEvent
	 * 
	 * @param idEvent
	 * @param idPerson
	 * @return List
	 */
	public List<PersonDto> getPersonDetailsForEvent(Long idEvent, Long idPerson);

	/**
	 * Method Name: searchContacts Method Description: This is NOT thread safe
	 * because it uses a field for its bind variables.
	 * 
	 * @param contactSearchDto
	 * @return FindContactDto
	 */
	public FindContactDto searchContacts(ContactSearchDto contactSearchDto);

	/**
	 * Method Name: searchContactsPriorStageAlso Method Description: Tuxedo
	 * Service Name: CSVC22S, DAM Name: CDYN03D
	 * 
	 * @param contactListSearchDto
	 * @return contactDtoList
	 */
	public List<ContactDto> searchContactList(ContactListSearchDto contactListSearchDto);
	
	/**
	 * Tuxedo Service Name: CSVC22S, DAM Name: CDYN03D This is a dynamic DAM
	 * that retrieves rows from the CONTACT table given a Stage ID and dynamic
	 * criteria that could include one or more Types, Locations, Methods,
	 * Purposes, and Others Contacted. The records will be retrieved in the
	 * order of Type and then Date Contact Occurred (earliest first).
	 * 
	 * @param contactListSearchDto
	 * @return contactDtoList
	 */
	public List<ContactDto> searchContactListCPSClosingSummary(ContactListSearchDto contactListSearchDto);

	public Integer getCountOFContactsInStage(Long idStage);

	public List<ContactSearchListDto> searchContactsForAPIPagination(List<Long> personIds, String indPersonPhonePrimary,
																	 String cdPersonPhoneType, String cdEventStatus, Long idCase, Date dtScrSearchDateFrom,
																	 Date dtScrSearchDateTo, Long uiIdEvent, String cdContactType, String cdContactPurpose,
																	 String cdContactMethod, String cdContactLocation, String cdContactOthers, List<Long> stageIds,int offset,int pageSize);

	// CANIRSP-23 For I&R Staff Search
	public StageSearchRes stageSearch(StageSearchReq stageSearchBean);
	// CANIRSP-244 'Create' Alerts for Identified Follow-up Action Item
	public List<InrContactFollowUpPendingDto> getFollowupPending(InrFollowupPendingReq stageSearchReq);
  // CANIRSP-465 SD 79227: Sensitive Case Handling
  public Boolean isCaseSensitive(Long idCase);
  public Boolean hasSensitiveAccess(Long idCaseworker, Long idCase);
}
