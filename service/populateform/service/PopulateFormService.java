package us.tx.state.dfps.service.populateform.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CINV38S
 * Class Description: This method is used to call the DAO to fetch the required
 * Response data to return back to the controller Jan 15, 2018 - 12:44:36 PM ©
 * 2017 Texas Department of Family and Protective Services
 * 
 */
public interface PopulateFormService {

	/**
	 * Service Name: CINV38S Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then retrieving
	 * data from caps_case, stage, code_type, stage_prog, person, person_email,
	 * etc tables to get the forms populated.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PopulateFormRes @ the service exception
	 */
	public PreFillDataServiceDto getFormsPopulated(PopulateFormReq populateFormReq);

}
