package us.tx.state.dfps.service.dpscrimhistres.service;

import us.tx.state.dfps.service.common.request.DPSCrimHistResReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * to populate fbifingerprinthistory Feb 5, 2021- 2:32:31 PM ©
 * 2021 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 02/05/2021 thompswa artf172715 initial. 
 */
public interface FbiFingerprintHistoryService {

	public PreFillDataServiceDto getFbiFingerprintHistRes(DPSCrimHistResReq dPSCrimHistResReq);

}
