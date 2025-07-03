package us.tx.state.dfps.service.adoptionplan.service;

import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for CSUB65S Apr 16, 2018- 1:03:46 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface AdoptionPlanCposService {

	/**
	 * Method Name: getPlan Method Description: Gathers data about case plan for
	 * a child and pre-adoptive family in an adoptive placement
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getPlan(CommonApplicationReq req);

}
