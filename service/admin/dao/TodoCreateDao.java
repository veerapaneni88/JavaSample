package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.EventStageDoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Ccmn43dDao
 *
 * Aug 7, 2017- 9:38:37 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface TodoCreateDao {
	/**
	 * Legacy Name: ccmn43dAUDdam
	 * 
	 * @param pInputDataRec
	 * @return Ccmn43doDto @
	 */
	public EventStageDoDto cudTODO(EventStageDiDto pInputDataRec) throws DataNotFoundException;

	/**
	 * 
	 * Method Name: audToDo Method Description:This method will call specific
	 * SQLs based in the Impact or MPS call
	 * 
	 * @param ccmn43di
	 * @return EventStageDoDto
	 * @throws DataNotFoundException
	 */
	public EventStageDoDto audToDo(EventStageDiDto eventStageDiDto);

	/**
	 * 
	 * Method Name: audToDoMobile Method Description:This method is called in
	 * Impact to perform AUD operation on Todo.
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto
	 * @throws DataNotFoundException
	 */
	public EventStageDoDto audToDoMobile(EventStageDiDto eventStageDiDto);

	/**
	 * Method Name: audToDoImpact Method Description:This method is called in
	 * MPS to perform AUD operation on Todo.
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto
	 * @throws DataNotFoundException
	 */
	public EventStageDoDto audToDoImpact(EventStageDiDto eventStageDiDto);

	/**
	 * 
	 * Method Name: completeTodo Method Description:This will Complete the Todo
	 * by putting the dt completed.
	 * 
	 * @param eventStageDiDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long completeTodo(EventStageDiDto eventStageDiDto);

}
