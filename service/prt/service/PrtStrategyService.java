package us.tx.state.dfps.service.prt.service;

import java.util.List;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.response.PrtStrategyRes;
import us.tx.state.dfps.service.prt.dto.PRTStrategyValueDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionService for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PrtStrategyService {

	/**
	 * method name: insertPRTTask Description: This method saves PRT Task.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long insertPRTTask(List<PRTTaskValueDto> list);

	/**
	 * method name: updatePRTTask Description: This method updates PRT Task.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long updatePRTTask(PRTTaskValueDto prtTaskValueDto);

	/**
	 * method name: deletePRTTask Description: This method deletes PRT Task
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long deletePRTTask(Long idPrtTask);

	/**
	 * method name: insertPRTStrategy Description: This method saves new PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long insertPRTStrategy(PRTStrategyValueDto prtStrategyValueDto);

	/**
	 * method name: updatePRTStrategy Description: This method updates PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long updatePRTStrategy(PRTStrategyValueDto prtStrategyValueDto);

	/**
	 * method name: deletePRTStrategy Description: This method deletes PRT
	 * Strategy and all its associated tasks.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public Long deletePRTStrategy(Long idPrtStrategy);

	/**
	 * method name: fetchPRTStrategy Description: This method updates PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */
	public PrtStrategyRes fetchPRTStrategy(PRTStrategyValueDto prtStrategyValueDto);

	/**
	 * method name: fetchPrtStrategyList description: This method updates Latest
	 * Child Plan Goals into PRT Tables.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */

	public PrtStrategyRes fetchPRTStrategyList(PRTStrategyValueDto prtStrategyValueDto);

	/**
	 * method name: fetchChildren description: This method fetches children
	 * associated with the PRT
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * @
	 */

	public PrtStrategyRes fetchInitialChildList(PRTStrategyValueDto prtStrategyValueDto);

}
