package us.tx.state.dfps.service.prt.dao;

import java.util.List;

import us.tx.state.dfps.service.baseprtsession.dto.PRTTaskPersonLinkValueDto;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyValueDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionDao for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PrtStrategyDao {

	/**
	 * @param strategy
	 * @return Long
	 */
	public Long insertPrtStrategy(PRTStrategyValueDto strategy);

	/**
	 * @param task
	 * @return Long
	 */
	public Long insertPrtTask(PRTTaskValueDto task);

	/**
	 * @param taskPersonLink
	 * @return Long
	 */
	public Long insertTaskPersonLink(PRTTaskPersonLinkValueDto taskPersonLink);

	/**
	 * @param idPrtTask
	 * @return Long
	 */
	public Long deletePrtTaskPersonLink(Long idPrtTask);

	/**
	 * @param prtTaskValueDto
	 * @return Long
	 */
	public Long updatePrtTask(PRTTaskValueDto prtTaskValueDto);

	/**
	 * @param idPrtTask
	 * @return Long
	 */
	public Long deletePrtTask(Long idPrtTask);

	/**
	 * @param prtStrategyValueDto
	 * @return Long
	 */
	public Long updatePrtStrategy(PRTStrategyValueDto prtStrategyValueDto);

	/**
	 * Method Name: selectPrtTasks Method Description:.
	 *
	 * @param idPrtStrategy
	 *            the id prt strategy
	 * @param b
	 *            the b
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTTaskDto> selectPrtTasks(Long idPrtStrategy, boolean b) throws DataNotFoundException;

	/**
	 * Method Name: selectChildrenForTask Method Description:.
	 *
	 * @param idPrtTask
	 *            the id prt task
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTTaskPersonLinkDto> selectChildrenForTask(Long idPrtTask) throws DataNotFoundException;

	/**
	 * PRT Strategy.
	 */

	/**
	 * This method fetches single row from PRT_STRATEGY table.
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param ActionPlanType
	 * 
	 * @return List <PRTEventLinkValueDto> or null if record not found.
	 * 
	 * 
	 */

	public List<PRTStrategyDto> selectPrtStrategy(Long idPrtActPlnOrFollowup, ActionPlanType planType);

	public List<PRTPersonLinkDto> selectInitialChildrenForTask(Long idPrtTask) throws DataNotFoundException;

	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActplnFollowup);

}
