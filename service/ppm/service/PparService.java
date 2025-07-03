package us.tx.state.dfps.service.ppm.service;

import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:interface
 * for Permanency Planning May 29, 2018- 4:41:13 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PparService {

	/**
	 * Method Name: getPpaCaseReview CSUB78S Method Description: Populates form
	 * csc39o00, which Populates Permanency Planning Review Invited Parties and
	 * Participation Information.
	 * 
	 * @return PreFillDataServiceDto
	 */

	public PreFillDataServiceDto getPpaCaseReview(PpmReq ppmReq);

	/**
	 * Method Name: getPpaCaseReviewAdmin CSUB51S Method Description: Populates
	 * form csc30o00,Used to record the results of the Permanency Planning
	 * Administrative Case Review for a child in substitute care.
	 * 
	 * @return PreFillDataServiceDto
	 */

	public PreFillDataServiceDto getPpaCaseReviewAdmin(PpmReq ppmReq);

}
