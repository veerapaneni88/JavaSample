package us.tx.state.dfps.service.notiftolawenforce.service;

import us.tx.state.dfps.service.common.request.NotifToLawEnforceReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * populates the APS Facility Notice to Law Enforcement form.> Tuxedo service
 * name: cinv80s Mar 14, 2018- 5:16:53 PM © 2017 Texas Department of Family and
 * Protective Services
 */

public interface NotifToLawEnforcementService {

	/**
	 * Service Name: cinv80s Method Description:This class populates the APS
	 * Facility Notice to Law Enforcement form.
	 *
	 * @param notifToLawEnforceReq
	 * @return PreFillDataServiceDto @ the service exception
	 */

	public PreFillDataServiceDto getLawEnforcementNoticed(NotifToLawEnforceReq notifToLawEnforceReq);

}
