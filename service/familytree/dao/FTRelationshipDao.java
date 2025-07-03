package us.tx.state.dfps.service.familytree.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.request.FamilyTreeRelationsReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelIntDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelSuggestionDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * 
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description:<interface for
 * FreeTreeRelationship> Feb 12, 2018- 6:25:07 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FTRelationshipDao {
	/**
	 * Method Name: selectRelationshipsWith2Persons Method Description: This
	 * method fetches relationship records from PERSON_RELATION table between 2
	 * persons
	 * 
	 * @param idPerson1
	 * @param idPerson2
	 * @return List<FTPersonRelationBean>
	 */
	public ArrayList<FTPersonRelationBean> selectRelationshipsWith2Persons(Long idPerson1, Long idPerson2);

	/**
	 * 
	 * Method Name: selectAllDirectRelationships Method Description:This method
	 * fetches All Direct Relationships with the selected Context Person It
	 * fetches the relationships from snapshot table (SS_PERSON_RELATION) so
	 * that relationships can be displayed as they existed before person merge
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<FTPersonRelationDto>
	 */
	public List<FTPersonRelationDto> selectAllDirectRelationships(Long idPerson, Long idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * Method Name: selectRelationshipHistory Method Description:This method
	 * fetches single row from PERSON_RELATION_HISTORY table using
	 * idPersonRelationHistory
	 * 
	 * @param idPersonRelationHistory
	 * @return FTPersonRelationValueDto
	 * @throws DataNotFoundException
	 */
	public FTPersonRelationBean selectRelationshipHistory(Long idPersonRelationHistory);

	/**
	 * Method Name: selectRelationship Method Description: This method fetches
	 * single row from PERSON_RELATION table using idRelationship
	 * 
	 * @param idPersonRelation-
	 *            The unique identifier to get the record from the
	 *            PERSON_RELATION table.
	 * @return ftPersonRelationBean - The dto contains values from a single row
	 *         from the PERSON_RELATION table for the input passed.
	 */
	public FTPersonRelationBean selectRelationship(Long idPersonRelation);

	/**
	 * Method Name: selectContextPersons Method Description:This method
	 * retrieves all the persons from STAGE_PERSON_LINK table related to a Stage
	 * or a Case.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return List<PersonValueDto>
	 * @throws DataNotFoundException
	 */
	public List<PersonValueDto> selectContextPersons(Long idCase, Long idStage);

	/**
	 * 
	 * Method Name: selectContextPersons Method Description:This method
	 * retrieves all the persons from STAGE_PERSON_LINK table related to a Stage
	 * or a Case when the Case/Stage is Closed.
	 * 
	 * @param idCase
	 * @param idStage
	 * @param dtClosed
	 * @return List<PersonValueDto>
	 * @throws DataNotFoundException
	 */
	public List<PersonValueDto> selectContextPersons(Long idCase, Long idStage, Date dtClosed);

	/**
	 * 
	 * Method Name: selectOnlineParameterValue Method Description:This method
	 * returns the value for the the given Key Name from ONLINE_PARAMETERS
	 * table.
	 * 
	 * @param familyTreeGraph
	 * @return String
	 * @throws DataNotFoundException
	 */
	public String selectOnlineParameterValue(String familyTreeGraph);

	/**
	 * 
	 * Method Name: selectCasePersonList Method Description:This method
	 * retrieves all the person Ids in the Case Person List.
	 * 
	 * @param idCase
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> selectCasePersonList(Long idCase);

	/**
	 * 
	 * Method Name: selectStaffRecordsAmongPersonList Method Description:This
	 * method retrieves list of staff records among persons in the given person
	 * list.
	 * 
	 * @param personIdList
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> selectStaffRecordsAmongPersonList(List<Long> personIdList);

	/**
	 * Method Name: selectStaffRecordsForSensitiveFitlering Method
	 * Description:This method retrieves list of staff records among persons in
	 * the given person list.
	 * 
	 * @param personIdList
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> selectStaffRecordsForSensitiveFitlering(List<Long> personIdList);

	/**
	 * 
	 * Method Name: selectSensitveCasePersons Method Description:This method
	 * returns the list of persons with sensitive case access.
	 * 
	 * @param personIdList
	 * @return List<Long>
	 */
	public List<Long> selectSensitveCasePersons(List<Long> personIdList);

	/**
	 * Method Name: selectStagePersonList Method Description: This method
	 * retrieves all the person Ids in the Stage Person List.
	 * 
	 * @param idStage
	 * @return List<PersonValueDto>
	 */
	public List<PersonValueDto> selectStagePersonList(Long idStage);

	/**
	 * Method Name: selectPCSPAmongPerons Method Description:fetches
	 * PCSPAmongPerons list
	 * 
	 * @param personIdList
	 * @param idCase
	 * @return List<PcspValueDto>
	 * @throws DataNotFoundException
	 *//*
		 * public List<PcspValueDto> selectPCSPAmongPerons(List<Long>
		 * personIdList, Long idCase)
		 */

	/**
	 * Method Name: selectPeopleClosedAtGivenDate Method Description:This method
	 * returns the list of persons that are closed at the time of Case/Stage
	 * Closure.
	 * 
	 * @param personIdList
	 * @param dtClosed
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> selectPeopleClosedAtGivenDate(List<Long> personIdList, Date dtClosed);

	/**
	 * Method Name: selectRelationshipSugMappingData Method Description: This
	 * method loads the Person Relationship Suggestion Def Mapping Data.
	 * 
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectRelationshipSugMappingData();

	/**
	 * Method Name: selectRelintRelMappingData Method Description: This method
	 * loads the Person Relationship / Relint Mapping data.
	 * 
	 * @return Map<String,FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public Map<String, FTPersonRelationDto> selectRelintRelMappingData();

	/**
	 * Method Name: selectPersonListFromStage Method Description:This method
	 * retrieves all the Persons with in the Stage. It uses the same query as
	 * Person List Window.
	 * 
	 * @param idStage
	 * @return List<PersonValueDto> throws DataNotFoundException
	 */
	List<PersonValueDto> selectPersonListFromStage(Long idStage);

	/**
	 * Method Name: selectRelationshipsAmongPersonList Method Description:This
	 * method retrieves all the relationships among persons in the given person
	 * list.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectRelationshipsAmongPersonList(List<Long> personIdList);

	/**
	 * Method Name: selectPersonDetails Method Description: This method
	 * retrieves Person Details for the given Person List
	 * 
	 * @param personIdList
	 * @return Map<Long,PersonValueDto>
	 * @throws DataNotFoundException
	 */
	public Map<Long, PersonValueDto> selectPersonDetails(List<Long> personIdList);

	/**
	 * Method Name: findRelatedOpenCases Method Description:This method finds
	 * all the open cases where the relationship is being used. Meaning where
	 * both idPerson and idRealtedPerson are in Stage Person Link.
	 * 
	 * @param idPerson
	 *            - The context person in the relationship
	 * @param idRelatedPerson
	 *            - The associated person in the relatioship.
	 * @return List<Long> - The list of case ids in which the relationship
	 *         between the primary person and the secondary person exists.
	 */
	public List<Long> findRelatedOpenCases(Long idPerson, Long idRelatedPerson);

	/**
	 * Method Name: fetchAllDirectRelationships Method Description: This method
	 * is used to find all the relationship between two persons which are not
	 * ended or invalidated and excluding the current record. The values are
	 * fetched from the PERSON_RELATION, PERSON tables.
	 * 
	 * @param ftPersonRelationBean
	 * @return List<FTPersonRelationBean>
	 */
	public List<FTPersonRelationBean> fetchAllDirectRelationships(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: insertRelationship Method Description: This method inserts
	 * record into PERSON_RELATION table.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto with the relation details to be inserted in the
	 *            table.
	 * @return Long - The unique id representing the idPersonRelation for the
	 *         newly created person.
	 */
	Long insertRelationship(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: updateRelationship Method Description: This method updates
	 * PERSON_RELATION table using FTPersonRelationValueBean.
	 * 
	 * @param ftPersonRelationBean-The
	 *            dto with the relation details to be updated in the table.
	 * @return Long -The unique id representing the idPersonRelation for the
	 *         updated person.
	 */
	public Long updateRelationship(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: isRelIntMatches Method Description:The method is used to
	 * fetch from the PERSON_RELATION_RELINT_DEF table based on the column
	 * CD_RELINT.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto with the input value for retrieving the records from
	 *            the db.
	 */
	public List<PersonRelIntDto> isRelIntMatches(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: getPossibleRelation Method Description:This method is used
	 * to fetch data from the PERSON_RELATION_SUG_DEF table based on the
	 * CD_NEW_RELATION and CD_EXISTING_RELATION column.
	 * 
	 * @param familyTreeRelationsReq
	 *            - The dto with the input values for the CD_NEW_RELATION and
	 *            CD_EXISTING_RELATION column.
	 * @return PersonRelSuggestionDto - The dto with details of the possible
	 *         relation match for the input parameters.
	 */
	public PersonRelSuggestionDto getPossibleRelation(FamilyTreeRelationsReq familyTreeRelationsReq);

	/**
	 * Method Name: invalidateRelation Method Description:
	 * 
	 * @param ftPersonRelationBean-The
	 *            dto with the relation details to be updated in the table.
	 */
	void invalidateRelation(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: insertRelationship Method Description: This method inserts
	 * record into RELATIONSHIP table.
	 * 
	 * @param ftPersonRelationBean
	 * @return long @
	 */
	public long insertRelationship(FTPersonRelationDto ftPersonRelationBean);

	/**
	 * Method Name: selectRelationsAmongPersons Method Description:Fetches
	 * RelationsAmongPerson List
	 * 
	 * @param relPersonIdList
	 * @return List<FTPersonRelationDto>
	 ** @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectRelationsAmongPersons(List<Integer> relPersonIdList);

	/**
	 * Method Name: selectAllDirectRelForPersons Method Description: This method
	 * gets All Direct Relations for Persons.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllDirectRelForPersons(List<Integer> personIdList);

	List<PcspDto> selectPCSPAmongPerons(List<Long> personIdList, Long idCase);

	/**
	 * Method Name: fetchAllDirectRelationships Method Description: This method
	 * is used to find all the relationship between two persons which are not
	 * ended or invalidated and excluding the current record. The values are
	 * fetched from the PERSON_RELATION, PERSON tables.
	 * 
	 * @param ftPersonRelationBean
	 * @return List<FTPersonRelationBean>
	 */
	public List<FTPersonRelationBean> fetchAllDirectPersonRelationShip(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: isStaff Method Description:query if a given person id, there
	 * is a record in the employee table, if found record, return true else
	 * false
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isStaff(Long idPerson);

}
