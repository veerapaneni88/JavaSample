package us.tx.state.dfps.service.populateform.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method May 29, 2018- 1:30:25 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface LetterToParentService {

	/**
	 * Method Name: getParentLetter Method Description: Makes DAO calls and
	 * returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getParentLetter(PopulateFormReq req);

}
