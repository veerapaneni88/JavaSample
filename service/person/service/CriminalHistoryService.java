package us.tx.state.dfps.service.person.service;

import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestBody;

import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryUpdateReq;
import us.tx.state.dfps.service.common.response.CrimHistoryRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryRes;
import us.tx.state.dfps.service.person.dto.CriminalHistoryValueBean;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Criminal History
 * Service Class Description: This class is use for retrieving Background Check
 * List Mar 24, 2017 - 3:19:51 PM
 */
public interface CriminalHistoryService {

	/**
	 * Method Name: saveCriminalHistory Method Description:
	 * 
	 * @param criminalHistoryValueBean
	 * @return
	 */
	public CriminalHistoryRes saveCriminalHistoryNarrative(CriminalHistoryValueBean criminalHistoryValueBean,
			String result);

	/**
	 * Method Name: updateCriminalHistoryRec Method Description: This service
	 * will Update rows in the Criminal History Table for a given IdCrimHist.
	 * Also, this service will delete rows from Crim Hist Narr Table for a given
	 * IdCrimHist when the XIndDeleteNarr flag is set for that IdCrimHist.
	 * Service Name: CCFC32S
	 * 
	 * @param CriminalHistoryUpdateReq
	 * @return CriminalHistoryRes
	 */
	public CriminalHistoryRes updateCriminalHistoryRec(CriminalHistoryUpdateReq criminalHistoryUpdateReq);

	/**
	 * 
	 * Method Description: This service will retrieve all rows from the Criminal
	 * History Table for a given ID_REC_CHECK. Service Name: CCFC31S
	 * 
	 * 
	 * @param CriminalHistoryReq
	 * @return CrimHistoryRes
	 */
	public CrimHistoryRes getCriminalHistoryList(@RequestBody CriminalHistoryReq criminalHistoryReq);

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get
	 * the idPerson if the Criminal History Action is null for the given
	 * Id_Stage.
	 * 
	 * @param idStage
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	public HashMap checkCrimHistAction(long idStage);

	/**
	 * Method Name: isCrimHistPending Method Description: To check if any DPS
	 * Criminal History check is pending.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isCrimHistPending(Long idStage);

	/**
	 *
	 * Method Description:This Method will retrieve information for the criminal
	 * History window. Tuxedo Service Name:isCrimHistNarrPresentForCriminalHistortRecord
	 *
	 *
	 * @param crimHistoryReq
	 * @return
	 */
	boolean isCrimHistNarrPresentForRecordCheck(CriminalHistoryReq crimHistoryReq);
}
