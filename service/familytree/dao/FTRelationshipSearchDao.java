package us.tx.state.dfps.service.familytree.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;

/**
 * 
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description:This Data
 * Access Object contains functions related to Family Tree Feb 12, 2018- 6:10:24
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface FTRelationshipSearchDao {

	/**
	 * Method Name: selectAllDirectRelationships Method Description: This method
	 * fetches All Direct Relationships with the selected Context Person.
	 * 
	 * @param idPerson
	 * @return List<FTPersonRelationDto> @
	 */
	public List<FTPersonRelationDto> selectAllDirectRelationships(long idPerson);

	/**
	 * 
	 * Method Name: selectAllRelationshipsWithinStage Method Description:
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllRelationshipsWithinStage(Long idStage);

	/**
	 * 
	 * Method Name: selectAllRelWithinClosedCaseOrStage Method Description:
	 * 
	 * @param zeroValue
	 * @param idStage
	 * @param dtClosed
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllRelWithinClosedCaseOrStage(Long zeroValue, Long idStage, Date dtClosed);

	/**
	 * 
	 * Method Name: selectPersonRelationshipsWithinStage Method Description:
	 * 
	 * @param idStage
	 * @param idContextPerson
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectPersonRelationshipsWithinStage(Long idStage, Long idContextPerson);

	/**
	 * 
	 * Method Name: selectAllRelationshipsWithinCase Method Description:
	 * 
	 * @param idCase
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllRelationshipsWithinCase(Long idCase);

	/**
	 * 
	 * Method Name: selectPersonRelationshipsWithinCase Method Description:
	 * 
	 * @param idCase
	 * @param idContextPerson
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectPersonRelationshipsWithinCase(Long idCase, Long idContextPerson);

	/**
	 * Method Name: selectCasePersonListRelations Method Description: fetches
	 * PersonRelWithinClosedCaseOrStage details
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idContextPerson
	 * @param dtClosed
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectPersonRelWithinClosedCaseOrStage(Long idCase, Long idStage,
			Long idContextPerson, Date dtClosed);

	/**
	 * Method Name: selectCasePersonListRelations Method Description: This
	 * method fetches All Relationships of persons with in the Case. Persons in
	 * the Relationship could be in any Case or Stage
	 * 
	 * @param idCase
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectCasePersonListRelations(Long idCase);

	/**
	 * Method Name: selectAllDirectRelInClosedCaseOrStage Method
	 * Description:This method fetches All Direct Relationships in a CLOSED Case
	 * or Stage Related Persons can be in other Case/Stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @param dtCaseClosed
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllDirectRelInClosedCaseOrStage(Long idCase, Long idStage,
			Date dtCaseClosed);

	/**
	 * Method Name: selectStagePersonListRelations Method Description: This
	 * method fetches All Relationships of persons with in the stage. Persons in
	 * the Relationship could be in any Case or Stage
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectStagePersonListRelations(Long idStage);

	/**
	 * Method Name: selectAllDirectRelPersonClosedCaseorStage Method
	 * Description:Fetches AllDirectRelPersonClosedCaseorStage
	 * 
	 * @param idContextPerson
	 * @param dtClosed
	 * @return List<FTPersonRelationDto>
	 ** @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllDirectRelPersonClosedCaseorStage(Long idContextPerson, Date dtClosed);

	/**
	 * Method Name: selectRelationsAmongPersons Method Description:Fetches
	 * RelationsAmongPerson List
	 * 
	 * @param relPersonIdList
	 * @return List<FTPersonRelationDto>
	 ** @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectRelationsAmongPersons(List<Long> relPersonIdList);

	/**
	 * Method Name: selectAllDirectRelForPersons Method Description: This method
	 * gets All Direct Relations for Persons.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto>
	 * @throws DataNotFoundException
	 */
	public List<FTPersonRelationDto> selectAllDirectRelForPersons(List<Long> personIdList);

	/**
	 * 
	 * Method Name: insertRelationship Method Description: Insert Person
	 * Relation details to Person_Relation table
	 * 
	 * @param ftPersonRelationDto
	 */
	public void insertRelationship(FTPersonRelationDto ftPersonRelationDto);

	/**
	 * 
	 * Method Name: updateRelationship Method Description: Update Person
	 * Relation details to Person_Relation table
	 * 
	 * @param ftPersonRelationDto
	 */
	public void updateRelationship(FTPersonRelationDto ftPersonRelationDto);

}
