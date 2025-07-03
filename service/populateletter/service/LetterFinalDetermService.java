package us.tx.state.dfps.service.populateletter.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * to declare Service method CINV78S Jul 6, 2018- 2:22:04 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface LetterFinalDetermService {

	/**
	 * Method Name: getLetter Method Description: Makes DAO calls and sends data
	 * to prefill
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getLetter(PopulateFormReq req);

}
