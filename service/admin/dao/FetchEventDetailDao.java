package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.FetchEventAdminDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches the Event Details using EventID Aug 5, 2017- 7:12:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FetchEventDetailDao {

	public FetchEventDetailDto getEventDetail(FetchEventAdminDto fetchEventAdminDto);

}
