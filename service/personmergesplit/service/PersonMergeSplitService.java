package us.tx.state.dfps.service.personmergesplit.service;

import java.util.HashMap;
import java.util.List;

import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitReqDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeUpdateLogDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * Level Class for Person Merge Split> May 30, 2018- 10:29:58 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonMergeSplitService {

	/**
	 * 
	 * Method Name: mergePersons Method Description:This function carries out
	 * the person merge. At high level it performs following sequence of steps -
	 * Make before merge snapshots for person forward and person closed - Call
	 * PersonMergeHelper to perform person and stage data updates - Save the
	 * warnings/informations if any thrown as part of validation step - Save the
	 * select forward fields as chosen by user - Save the areas updated by
	 * person merge - Save the after merge snapshots for the person forward
	 * 
	 * @param personMergeSplitDto
	 * @return PersonMergeSplitDto
	 */
	public PersonMergeSplitDto mergePersons(PersonMergeSplitReqDto personMergeSplitDto);

	/**
	 * 
	 * Method Name: splitPRT Method Description:This method is being called
	 * during the Person Split operation to split PRT
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	public void splitPRT(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * 
	 * Method Name: splitPersonMerge Method Description:This function carries
	 * out the person split
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	public long splitPersonMerge(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * 
	 * Method Name: splitPCSP Method Description:This method is being called
	 * during the Person Split operation to split PCSP
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	public long splitPCSP(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * 
	 * Method Name: splitPCSP Method Description:This method is being called
	 * during the Person Split operation to split Legacy PCSP
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	public long splitLegacyPCSP(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * Method Name: getPersonMergeInfo Method Description: This method returns
	 * Person Merge row based on IdPersonMerge
	 * 
	 * @param idPersonMerge
	 * @param userProfileDto
	 * @return PersonMergeSplitValueDto
	 */
	public PersonMergeSplitValueDto getPersonMergeInfo(Long idPersonMerge, boolean hasSensitiveCaseAccessRight);

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param ulIdPerson
	 * @return List<PersonMergeSplitValueDto>
	 */
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long ulIdPerson);

	/**
	 * Method Name: getPersonMergeUpdateLogList Method Description: This method
	 * gets the Person Merge update log (fields affected by a merge)
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto>
	 */
	public List<PersonMergeUpdateLogDto> getPersonMergeUpdateLogList(Long idPersonMerge);

	/**
	 * Method Name: getPersonMergeSelectFieldMap Method Description: This
	 * function fetches the selections made by a user at Select Forward Person
	 * page during a merge.
	 * 
	 * @param idPersonMerge
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getPersonMergeSelectFieldMap(Long idPersonMerge);

	/**
	 * Method Name: getPersonAllegationsUpdatedInMerge Method Description: This
	 * method fetches the allegations modified for forward person in a person
	 * merge.
	 * 
	 * @param idPersonMerge
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return List<PersonMergeUpdateLogDto>
	 */
	public List<AllegationDto> getPersonAllegationsUpdatedInMerge(Long idPersonMerge, Long idForwardPerson,
			Long idClosedPerson);
	
	/**
	 * 
	 * Method Name: splitChildSxVctmztnIncdnt Method Description:This method is being called
	 * during the Person Split operation to split CHILD_SX_VCTMZTN_INCDNT
	 * 
	 * @param personMergeSplitDB
	 * @return void
	 */
	public void splitChildSxVctmztnIncdnt(PersonMergeSplitDto personMergeSplitDB);
	
	

	/**
	 *
	 * Method Name: splitChildSxMutualIncdnt
	 * Method Description:This method is being called during the Person Split operation to split CHILD_SX_MUTUAL_INCDNT
	 *
	 * @param personMergeSplitDB
	 * @return void
	 */
	public void splitChildSxMutualIncdnt(PersonMergeSplitDto personMergeSplitDB);

}
