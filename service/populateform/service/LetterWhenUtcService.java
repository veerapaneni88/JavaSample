package us.tx.state.dfps.service.populateform.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * to declare Service method CINV57S Jul 5, 2018- 11:30:29 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface LetterWhenUtcService {

	/**
	 * Method Name: getUtcLetter Method Description: Makes DAO calls and sends
	 * data to prefill
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getUtcLetter(PopulateFormReq req);
}
