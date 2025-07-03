package us.tx.state.dfps.service.servicedlvryclosure.dao;

import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureEventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dlvry
 * closure Event Dao class Jun 4, 2018- 5:25:57 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface DlvryClosureEventDao {
	/**
	 * Method Name: retrvEvent Method Description: retrv event info
	 * 
	 * @param idStage
	 * @param eventType
	 * @return
	 */
	public DlvryClosureEventDto retrvEvent(long idStage, String eventType);
}
