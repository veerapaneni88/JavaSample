package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service
 * CPER03S for form per03o00, to populate Person Comparison Form May 31, 2018-
 * 10:00:54 AM © 2017 Texas Department of Family and Protective Services
 */
public interface PersonComparisonService {

	/**
	 * Method Name: getPersonComparison Method Description: Populates form
	 * per03o00, which Populates the Person Comparison Form.
	 * 
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPersonComparison(PersonDtlReq personDtlReq);

}
