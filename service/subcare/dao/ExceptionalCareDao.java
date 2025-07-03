package us.tx.state.dfps.service.subcare.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.common.request.ExceptionalCareReq;
import us.tx.state.dfps.service.common.response.ExceptionalCareRes;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;

public interface ExceptionalCareDao {

	/**
	 * Method Name: saveExceptionalCare Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param exceptionalCareVB
	 * @return
	 */
	public ExceptionalCareDto saveExceptionalCare(ExceptionalCareDto exceptionalCareVB);

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
	public List<ExceptionalCareDto> displayExceptCareList(ExceptionalCareReq req);

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
	public ExceptionalCareDto getPlacementDates(Long idPlcmtEvent);

	/**
	 * Method Name: saveExceptionalCare Method Description: Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param exceptionalCareDto
	 * @param cdSavetype
	 */
	public ExceptionalCareRes SaveExceptionalCare(ExceptionalCareDto exceptionalCareDto, String cdSaveType);

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:Gets an
	 * active child sscc referral from the SSCC_REFERRAL table
	 * 
	 * @param idStage
	 * @return
	 */
	public SSCCExceptCareDesignationDto getActiveChildPlcmtReferral(Long idStage);

	/**
	 * Method Name: getExcpCareDays Method Description: Gets numbers of
	 * exceptional care days in a contract period
	 * 
	 * @param exceptionalCareDto
	 * @return
	 */
	public Integer getExcpCareDays(ExceptionalCareDto exceptionalCareDto);

}
