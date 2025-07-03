/*
 * 
 */

package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.common.request.LegalActionEventReq;
import us.tx.state.dfps.service.common.response.LegalActionEventRes;
import us.tx.state.dfps.service.common.response.LegalActionsRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Interface for fetching legal event details Aug 13, 2017- 4:10:19 PM © 2017
 * Texas Department of Family and Protective Services.
 */

public interface LegalActionEventService {

	/**
	 * Gets the most recent FDTC subtype.
	 *
	 * @param legalActionEventReq
	 *            the person id
	 * @return LegalActionEventRes the most recent FDTC subtype @ the service
	 * exception
	 */
	public LegalActionEventRes getMostRecentFDTCSubtype(LegalActionEventReq legalActionEventReq);

	/**
	 * Gets the open FBSS stage.
	 *
	 * @param legalActionEventReq
	 *            the legal action event req
	 * @return LegalActionEventRes the open FBSS stage @ the service exception
	 */
	public LegalActionEventRes getOpenFBSSStage(LegalActionEventReq legalActionEventReq);

	/**
	 * Gets the legal action rel fictive kin. This method fetches Legal Action
	 * Date of PMC to Relative or Fictive Kin
	 *
	 * @param legalActionEventReq
	 *            the legal action event in dto
	 * @return LegalActionEventRes the legal action rel fictive kin @ the
	 * service exception
	 */
	public LegalActionEventRes getLegalActionRelFictiveKin(LegalActionEventReq legalActionEventReq);

	/**
	 * This is a method to set all input parameters(of the stored procedure).
	 */
	public LegalActionsRes executeStoredProc(List<Object> arrayList);

	

	/**
	 * Method Name: selectLatestLegalActionOutcome
	 * Method Description: selects the most recent outcome for Legal action
	 *
	 * @param legalActionEventInDto
	 * @return LegalActionEventOutDto
	 */
	
	public LegalActionEventOutDto selectLatestLegalActionOutcome(LegalActionEventInDto legalActionEventInDto);
}
