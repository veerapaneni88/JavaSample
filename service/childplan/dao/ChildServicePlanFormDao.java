package us.tx.state.dfps.service.childplan.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

public interface ChildServicePlanFormDao {
	/**
	 * 
	 * Method Name: getIdPersonPerChildPlan Method Description:This method
	 * retrieves the id_person from the EVENT table for the worker who completes
	 * a given CHILD PLAN
	 * 
	 * @param idEvent
	 * @return EventDto
	 * @throws DataNotFoundException
	 */
	public EventDto getIdPersonPerChildPlan(Long idEvent);

	/**
	 * 
	 * Method Name: getPersonDetails Method Description:This method retrieves a
	 * row from the person table based on an input of IdPerson. It also links to
	 * the Name table to retreive Name information for that person
	 * 
	 * @param idPerson
	 * @return PersonDto
	 * @throws DataNotFoundException
	 */
	public PersonDto getPersonDetails(Long idPerson);

	/**
	 * 
	 * Method Name: getPersonChildDetails Method Description:This method joins
	 * retrieves valued from Person and Child Plan where PERSON.IdPerson ==
	 * CHILD PLAN.IdPerson and CHILD PLAN.IdChild Plan Event == Host:IdChild
	 * 
	 * @param idChPerson
	 * @return PersonDto
	 * @throws DataNotFoundException
	 */
	public PersonDto getPersonChildDetails(Long idChPerson);

	/**
	 * 
	 * Method Name: getEventDetails Method Description:This method returns the
	 * EVENT Details based on the Event Id
	 * 
	 * @param idEvent
	 * @return EventDto
	 * @throws DataNotFoundException
	 */
	public EventDto getEventDetails(Long idEvent);

	/**
	 * 
	 * Method Name: getChildPlanDetails Method Description:This method displays
	 * the "Date of Last Plan" field on the Child Plan form and populates the
	 * DT_LAST_UPDATE of the previous plan with respect to the most recent plan
	 * in the stage. It populates with the DT_LAST_UPDATE of the previous plan
	 * with respect to the child plan currently being viewed. For this to
	 * happen, the id_event of the child plan currently being viewed needs to be
	 * passed into this method
	 * 
	 * @param idPerson
	 * @param idChildPlanEvent
	 * @return ChildPlanDetailsDto
	 * @throws DataNotFoundException
	 */
	public ChildPlanDetailsDto getChildPlanDetails(Long idPerson, Long idChildPlanEvent);

	/**
	 * Method Name: getChildPlanParticipants Method Description:This method
	 * retrieves all child plan participants based on the ID EVENT.
	 * 
	 * @param idChildPlanEvent
	 * @return List<ChildParticipantRowDODto>
	 * @throws DataNotFoundException
	 */
	public List<ChildParticipantRowDODto> getChildPlanParticipants(Long idChildPlanEvent);

	/**
	 * Method Name:fetchEventDetails Method Description:his method retrieves all
	 * eventdetails based on the ID EVENT
	 * 
	 * @param idEvent
	 * @return List<EventDto>
	 * @throws DataNotFoundException
	 */
	public List<EventDto> fetchEventDetails(Long idEvent);

	/**
	 * Method Name:getConcurrentData Method Description:his method retrieves all
	 * eventdetails based on the ID EVENT
	 * 
	 * @param idEvent
	 * @return List<FetchFullConcurrentDto> @
	 */
	public List<ConcurrentGoalDto> getConcurrentData(Long idEvent);

	/**
	 * Method Name: getResourcePlacementDetail Method Description: CSES28D - Get
	 * Resource and Placement details for person
	 * 
	 * @param idPerson
	 * @return CapsPlacemntDto
	 */
	public CapsPlacemntDto getResourcePlacementDetail(Long idPerson);

	/**
	 * 
	 * Method Name: geChildPlanItems DAM Name : CLSS07D Method Description:This
	 * DAM will retrieve all rows from the contract period table.
	 * 
	 * @param idChildPlanEvent
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ChildPlanItemDto> geChildPlanItems(Long idChildPlanEvent);

}