/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 27, 2018- 6:22:48 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.daycareabsencereview.service;

import us.tx.state.dfps.service.person.dto.DayCareAbsReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 27, 2018- 6:22:48 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface DayCareAbsenceReviewService {

	/**
	 * Method Name: getRdccPerson Method Description:
	 * 
	 * @param idTodo
	 * @return
	 * 
	 */
	public Long getRdccPerson(Long idTodo);

	/**
	 * Method Name: getDaycareSvcAuthInfo Method Description:
	 * 
	 * @param idTodo
	 * @return
	 * 
	 */
	public DayCareAbsReviewDto getDaycareSvcAuthInfo(Long idTodo);

	/**
	 * Method Name: getDaycareSvcAuthPerson Method Description:
	 * 
	 * @param idPerson
	 * @param idEvent
	 * @return
	 * 
	 */
	public DayCareAbsReviewDto getDaycareSvcAuthPerson(Long idPerson, Long idEvent);

	/**
	 * Method Name: createRDCCAlert Method Description:
	 * 
	 * @param idTodo
	 * @param dayCareAbsReviewDto
	 * @return
	 * 
	 */
	public Long createRDCCAlert(Long idTodo, DayCareAbsReviewDto dayCareAbsReviewDto);

	/**
	 * Method Name: createDaycareSupervisorAlert Method Description:
	 * 
	 * @param idTodo
	 * @param dayCareAbsReviewDto
	 * @return
	 * 
	 */
	public Long createDaycareSupervisorAlert(Long idTodo, DayCareAbsReviewDto dayCareAbsReviewDto);

	/**
	 * Method Name: getChildDayCareRsrcName Method Description:
	 * 
	 * @param daycareAbsRevVB
	 * @return
	 * 
	 */
	public String getChildDayCareRsrcName(DayCareAbsReviewDto daycareAbsRevVB);

	/**
	 * Method Name: updateTwcAbsTrans Method Description:
	 * 
	 * @param daycareAbsRewVB
	 * @param idTodo
	 * @return
	 * 
	 */
	public Long updateTwcAbsTrans(DayCareAbsReviewDto daycareAbsRewVB, Long idTodo);

	/**
	 * Method Name: markDaycareTodoCompleted Method Description:
	 * 
	 * @param idTodo
	 * @return
	 * 
	 */
	public Long markDaycareTodoCompleted(DayCareAbsReviewDto dayCareAbsRevDto);

	/**
	 * Method Name: getDaycareTodo Method Description:
	 * 
	 * @param idTodo
	 * @return
	 * 
	 */
	public DayCareAbsReviewDto getDaycareTodo(Long idTodo);

	/**
	 * Method Name: isTodoCreatedForStage Method Description:
	 * 
	 * @param daycareAbsRewVB
	 * @return
	 * 
	 */
	public Boolean isTodoCreatedForStage(DayCareAbsReviewDto daycareAbsRewVB);
}
