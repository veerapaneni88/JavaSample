package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * FetchToDoDao Aug 10, 2017- 2:05:26 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface FetchToDoDao {
	/**
	 * Method Name: getTodoInfo Method Description: This method retrieves
	 * details from Info table
	 * 
	 * @param pInputDataRec
	 * @return List<FetchToDoDto> @
	 */
	public List<FetchToDoDto> getTodoInfo(FetchToDodiDto pInputDataRec);

}
