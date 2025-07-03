/**
 * 
 */
package us.tx.state.dfps.service.subcare.service;

import us.tx.state.dfps.service.common.request.ExceptionalCareReq;
import us.tx.state.dfps.service.common.response.ExceptionalCareRes;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description: Class to get the exceptional care list to display in
 * placement
 * 
 * Feb 15, 2018- 9:56:31 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ExceptionalCareService {

	/**
	 * 
	 * Method Name: displayExceptCareList
	 * 
	 * Method Description: Fetches list of Exceptional Care records for a Person
	 * from SSCC_EXCEPT_CARE table
	 * 
	 * @param ExceptionalCareReq
	 * @return
	 */
	public ExceptionalCareRes displayExceptCareList(ExceptionalCareReq req);

	ExceptionalCareRes updateSsccExceptCare(ExceptionalCareReq req);

	ExceptionalCareRes updateSsccECStartDate(ExceptionalCareReq req);

	ExceptionalCareRes updateSsccECStartEndDates(ExceptionalCareReq req);

	/**
	 * Method Name: getPlacementDates Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param idPlcmtEvent
	 * @return
	 */
	public ExceptionalCareRes getPlacementDates(Long idPlcmtEvent);

	/**
	 * Method Name: saveExceptionalCare Method Description: Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param exceptionalCareDto
	 * @param cdSavetype
	 */
	public ExceptionalCareRes saveExceptionalCare(ExceptionalCareDto exceptionalCareDto, String cdSavetype);

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:Gets an
	 * active child sscc referral from the SSCC_REFERRAL table
	 * 
	 * @param idStage
	 * @return
	 */
	public ExceptionalCareRes getActiveChildPlcmtReferral(Long idStage);

	/**
	 * Method Name: getExcpCareDays Method Description: Gets numbers of
	 * exceptional care days in a contract period
	 * 
	 * @param exceptionalCareDto
	 * @return
	 */
	public ExceptionalCareRes getExcpCareDays(ExceptionalCareDto exceptionalCareDto);

}
