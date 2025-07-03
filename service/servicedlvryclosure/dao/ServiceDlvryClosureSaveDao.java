package us.tx.state.dfps.service.servicedlvryclosure.dao;

import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureSaveDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: interface
 * to save service dlvry delv decision Jun 4, 2018- 5:06:52 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ServiceDlvryClosureSaveDao {
	/**
	 * Method Name: saveOrUpdatevcDelvDecision Method Description: method to
	 * save delv decision
	 * 
	 * @param dlvryClosureSaveDto
	 * @return int
	 */
	public int saveOrUpdatevcDelvDecision(DlvryClosureSaveDto dlvryClosureSaveDto);
}
