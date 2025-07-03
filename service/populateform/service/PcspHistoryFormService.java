package us.tx.state.dfps.service.populateform.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * interface for cinv98s> Mar 29, 2018- 12:17:16 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PcspHistoryFormService {
	/**
	 * Service Name: CINV98S Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then Populates
	 * the Outcome Matrix Forms & Narrative
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPcspHistoryForm(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSVC60S Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then Populates
	 * the Pcsp Assessment Form
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPcspAssessmentForm(PopulateFormReq populateFormReq, String docType);
}
