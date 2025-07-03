package us.tx.state.dfps.service.extreq.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.extreq.ExtreqDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSVC27s
 * Extension Request Mar 15, 2018- 10:29:53 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface ExtreqDao {

	/**
	 * Method Name: getExtreqInfo Method Description: Retrieves Approver
	 * information DAM CSEC23D
	 * 
	 * @return ExtreqDto
	 * @throws DataNotFoundException
	 */
	public ExtreqDto getExtreqInfo(Long idEvent);

}
