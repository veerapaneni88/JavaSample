package us.tx.state.dfps.service.familytree.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.FamilyTreeRelationsReq;
import us.tx.state.dfps.service.common.response.FamilyTreeRelationsRes;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familyTree.bean.FamilyTreeRelationshipDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelSuggestionDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * interface for FTRelationshipServiceImpl Feb 12, 2018- 4:37:57 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FTRelationshipService {
	/**
	 * 
	 * Method Name: fetchRelationshipInfo Method Description:This method returns
	 * All the information that needs to be displayed on Family Tree
	 * Relationship page including Context Person List, Relationships and
	 * Suggested Relationships.
	 * 
	 * @param familyTreeRelationsipDto
	 * @return FamilyTreeRelationshipDto @
	 */
	public FamilyTreeRelationshipDto fetchRelationshipInfo(FamilyTreeRelationshipDto familyTreeRelationsipDto);

	/**
	 * 
	 * Method Name: generateRelSuggestionsRelint Method Description:This
	 * function suggests the relationships based on Rel/Int values.
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	public List<FTPersonRelationDto> generateRelSuggestionsRelint(Long idStage);

	/**
	 * 
	 * Method Name: genSuggestionsBasedonExistingRel Method Description:This
	 * function suggests the relationships based on existing Relationships.
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	public List<FTPersonRelationDto> genSuggestionsBasedonExistingRel(Long idStage);

	/**
	 * 
	 * Method Name: saveSuggestedRelations Method Description:This method
	 * inserts suggested relationship details into the database.
	 * 
	 * @param familyTreeRelationsipDto
	 * @return String @
	 */
	public String saveSuggestedRelations(FamilyTreeRelationshipDto familyTreeRelationsipDto);

	/**
	 * 
	 * Method Name: selectSensitveCasePersons Method Description:This method
	 * returns the list of persons with sensitive case access.
	 * 
	 * @param personIdList
	 * @return List<Long> @
	 */
	public List<Long> selectSensitveCasePersons(List<Long> personIdList);

	/**
	 * 
	 * Method Name: filterClosedPersonRelations Method Description:This method
	 * removes Closed Person Relationships.
	 * 
	 * @param relations
	 * @return List<FTPersonRelationDto> @
	 */
	public List<FTPersonRelationDto> filterClosedPersonRelations(List<FTPersonRelationDto> fTPersonRelationBeanList);

	/**
	 * Method Name: getRelationshipDetails Method Description:This method will
	 * be called to fetch the data on "Family Tree Relationship Details" page.
	 * It retrieves Context Person List, Associated Person List based on the
	 * Family Tree Context. It also retrieves single Record of Relationship if
	 * the id is passed.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the Family Tree Relationship details.
	 * @return FamilyTreeRelationsRes - This response object will hold the dto
	 *         containing the family tree relationship information.
	 */
	public FamilyTreeRelationsRes getRelationshipDetails(FamilyTreeRelationsReq familyTreeRelationsReq);

	/**
	 * Method Name: getPersonDetails Method Description:This method will be
	 * called to fetch the person details .
	 * 
	 * @param idPerson
	 *            - person id.
	 * @param idStage
	 *            - stage id.
	 * @return PersonValueDto - This response object will hold the dto
	 *         containing the Person detail information.
	 */
	public PersonValueDto getPersonDetails(Long idPerson, Long idStage);

	/**
	 * Method Name: getExistingRelationships Method Description:This method is
	 * used to call the dao implementation to fetch all the relationships
	 * details between two persons which are not ended and invalidated.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto contains the input values such as idPerson,
	 *            idRelatedPerson based on which the relations is retrieved.
	 * @return List<FTPersonRelationBean> - The list of existing relations.
	 */
	public List<FTPersonRelationBean> getExistingRelationships(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: saverelationship Method Description:This method is used to
	 * call the dao implementation to either create a new relationship or update
	 * the existing relation.
	 * 
	 * @param familyTreeRelationshipDto
	 *            - The dto containing the relationship details.
	 * @return Long - The unique id for the created/updated relationship.
	 */
	public Long saverelationship(FamilyTreeRelationshipDto familyTreeRelationshipDto);

	/**
	 * Method Name: isRelIntMatches Method Description:This method is used to
	 * call the dao implementation to get the REL_INT_DEF based on the input
	 * relInt.The method then checks if the record retrieved from the db matches
	 * with the input passed from the front-end with respect to cdType,
	 * cdRelation,cdLineage, and cdSeparation ignoring if the column values from
	 * the db record are null.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto contains the values which will used to fetch the
	 *            person relation relInt.
	 * @return boolean - The boolean value to indicate if the relInt matches
	 *         with the one entered on the screen.
	 */
	public boolean isRelIntMatches(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: getPossibleRelation Method Description:This method is used
	 * to call the dao implementation to fetch the possible relation based on
	 * the relation between 2 person.
	 * 
	 * @param familyTreeRelationsReq
	 *            - The dto with the input values.
	 * @return PersonRelSuggestionDto - The dto with the values if matched from
	 *         the db.
	 */
	public PersonRelSuggestionDto getPossibleRelation(FamilyTreeRelationsReq familyTreeRelationsReq);

	/**
	 * 
	 * Method Name: fetchRelationshipsForGraph Method Description:This method
	 * returns Relationships for Basic/Extended Graphs.
	 * 
	 * @param fTRelationshipDBDto
	 * @return FTRelationshipDBDto @
	 */
	public FamilyTreeRelationshipDto fetchRelationshipsForGraph(FamilyTreeRelationshipDto familyTreeRelationshipDto);

	/**
	 * Method Name: fetchRelationshipsForReport Method Description:This method
	 * is used to fetch the family tree relationship details for generating the
	 * report.This method calls the 'fetchRelationshipsForGraph' method of the
	 * service implementation internally to get the family tree relationship
	 * details and performs business logic before returning the response
	 * information.
	 * 
	 * @param familyTreeRelationshipDto
	 *            - This dto will hold input parameter values for fetching the
	 *            family tree relationship details.
	 * @return FamilyTreeRelationshipDto - This dto will hold the family tree
	 *         relationship details.
	 */
	public FamilyTreeRelationshipDto fetchRelationshipsForReport(FamilyTreeRelationshipDto familyTreeRelationshipDto);

	/**
	 * Method Name: fetchAllDirectPersonRelationShip Method Description:This
	 * method is used to call the dao implementation to fetch all the
	 * relationships details between two persons which are not ended and
	 * invalidated.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto contains the input values such as idPerson,
	 *            idRelatedPerson based on which the relations is retrieved.
	 * @return List<FTPersonRelationBean> - The list of existing relations.
	 */
	public List<FTPersonRelationBean> fetchAllDirectPersonRelationShip(FTPersonRelationBean ftPersonRelationBean);

	/**
	 * Method Name: isStaff Method Description: This methods checks whether the
	 * given person is a staff
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isStaff(Long idPerson);
}
