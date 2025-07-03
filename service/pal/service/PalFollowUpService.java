package us.tx.state.dfps.service.pal.service;

import us.tx.state.dfps.service.common.request.PalFollowUpReq;
import us.tx.state.dfps.service.common.response.PalFollowUpRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for PalFollowUpService. Oct 9, 2017- 11:26:45 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PalFollowUpService {

	/**
	 * Method Name: retrievePal Method Description:
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	public PalFollowUpRes retrievePal(PalFollowUpReq palFollowUpReq);

	/**
	 * Method Name: retrievePalFollowUp Method Description: Retrieves the Pal
	 * Follow Up record set details from the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	public PalFollowUpRes retrievePalFollowUp(PalFollowUpReq palFollowUpReq);

	/**
	 * Method Name: insertPalFollowUp Method Description: Inserts the Pal Follow
	 * Up records into the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	public PalFollowUpRes insertPalFollowUp(PalFollowUpReq palFollowUpReq);

	/**
	 * Method Name: updatePal Method Description: Update the Pal table.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	public PalFollowUpRes updatePal(PalFollowUpReq palFollowUpReq);

}
