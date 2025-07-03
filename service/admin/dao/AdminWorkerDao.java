package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AdminWorkerDao Aug 9, 2017- 1:13:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface AdminWorkerDao {
	/**
	 * 
	 * Method Name: getWorkLoad Method Description: Get person work details
	 * 
	 * @param pInputDataRec
	 * @return List<AdminWorkerOutpDto> @
	 */
	public List<AdminWorkerOutpDto> getWorkLoad(AdminWorkerInpDto pInputDataRec);

	public AdminWorkerOutpDto getPersonInRole(AdminWorkerInpDto adminWorkerInpDto);

	/**
	 * Method Name: getInvStageOpenEvent Method Description: This method
	 * retrieves Event associated with Opening of Investigation Stage.
	 * 
	 * @param idCase
	 * @return EventDto
	 */
	public Event getInvStageOpenEvent(long idCase);

	/**
	 * Method Name: retrieveStageInfo Method Description:This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idARStage
	 * @return Stage
	 */
	public Stage retrieveStageInfo(long idARStage);

	public AdminWorkerOutpDto getVictim(AdminWorkerInpDto adminWorkerInpDto);

}
