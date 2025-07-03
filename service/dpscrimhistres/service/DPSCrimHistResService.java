package us.tx.state.dfps.service.dpscrimhistres.service;

import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.common.request.DPSCrimHistResReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * CCFC34S to populate DPS Criminal History Result Apr 30, 2018- 2:32:31 PM ©
 * 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 08/24/2023 thompswa artf251083 add getNameWithSuffix.
 */
public interface DPSCrimHistResService {

	public PreFillDataServiceDto getCrimHistRes(DPSCrimHistResReq dPSCrimHistResReq);


	/**
	 * return full name as last, first, middle ( middle initial false ) string(or "Unknown").
	 */
	public String getNameWithSuffix( EmpNameDto empNameDto );

}
