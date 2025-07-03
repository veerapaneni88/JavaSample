package us.tx.state.dfps.service.childbackground.service;

import us.tx.state.dfps.service.common.request.ChildBackgroundReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ChildBackgroundServiceService will have all operation which are mapped to
 * ChildBackground module. March 20, 2018- 2:01:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ChildBackgroundService {

	/**
	 * 
	 * Method Name: getChildBackgroundInfo Service Name : CSUB74S Method
	 * Description:The Child background summary document will provide
	 * information about the child including Identification data, NEEDS,
	 * ASSESSMENTS, and History Data, and Biological Family data
	 * 
	 * @param childBackgroundReq
	 * @return @
	 */
	public PreFillDataServiceDto getChildBackgroundInfo(ChildBackgroundReq childBackgroundReq);

}
