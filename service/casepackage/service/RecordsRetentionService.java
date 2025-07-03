package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.casepackage.dto.RecordsRetnSaveInDto;
import us.tx.state.dfps.service.common.request.RecordsRetentionRtrvReq;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * RecordsRetentionService, CCFC19S Class Description: This class is use for
 * retrieving CapsCase
 *
 */
public interface RecordsRetentionService {

	/**
	 * This service will retrieve all columns for an ID Case from the RECORDS
	 * RETENTION table. There will be one row for a specified ID Case.
	 * Additionally, it will retrieve a full row from the CAPS CASE table to get
	 * the closure date for case. It calls DAMS: CCMNC5D - CASE SMP and CSES56D
	 * - REC RETN RTRV.
	 * 
	 * Service Name - CCFC19S
	 * 
	 * @param recordsRetentionRtrvReq
	 * @return @
	 */
	public RecordsRetentionRtrvRes recordsRetentionRtrv(RecordsRetentionRtrvReq recordsRetentionRtrvReq);
	
	/**
	 * Method Name: saveRecordsRetention Method Description: This method is used
	 * to do all updated to the RECORDS_RETENTION table. This is the equivalent
	 * of legacy Tuxedo service CCFC51U
	 * 
	 * @param recordsRetnSaveInDto
	 * @return void
	 */
	public void saveRecordsRetention(RecordsRetnSaveInDto recordsRetnSaveInDto);

}
