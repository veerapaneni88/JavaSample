package us.tx.state.dfps.service.interstatecompact.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * interface class for receiving requests from ICPformsController and transition
 * to Dao layer> May 1, 2018- 10:16:35 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ICPformsService {

	/**
	 * Service Name: CSUB42S Method Description: This service will get icp01o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getICPlacementReqForm(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSUB43S Method Description: This service will get icp02o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getICstatusform(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSUB30S Method Description: This service will get icp14o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getPriorityHomeStudyReq(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSUB32S Method Description: This service will get icp18o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getICcoverLetter(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSUB20S Method Description: This service will get icp20o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getICfinancialPlan(PopulateFormReq populateFormReq);

	/**
	 * Service Name: CSUB33S Method Description: This service will get icp22o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto @ the service exception
	 */
	public PreFillDataServiceDto getICtransmittalMemo(PopulateFormReq populateFormReq);
}
