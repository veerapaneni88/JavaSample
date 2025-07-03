package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.AdminEventInputDto;
import us.tx.state.dfps.service.admin.dto.AdminEventOutputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<Ccmn01uService> Aug 7, 2017- 6:26:50 PM Â© 2017 Texas Department
 * of Family and Protective Services
 */
public interface AdminEventService {

	public AdminEventOutputDto postEvent(AdminEventInputDto pInputMsg);
}
