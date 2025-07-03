package us.tx.state.dfps.service.legal.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.AssignSaveGroupReq;
import us.tx.state.dfps.service.common.request.LegalActionSaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.legal.dto.LegalActionSaveInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionSaveOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for LegalActionSaveServiceImpl Oct 25, 2017- 10:34:56 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface LegalActionSaveService {
	/**
	 * 
	 * Method Name: legalActionsOutcomeSave Method Description:This is the save
	 * service for the Legal Action/Outcome window. TUXEDO: CSUB39S
	 * 
	 * @param legalActionSaveInDto
	 * @return LegalActionSaveOutDto
	 * @throws ParseException
	 * @
	 */
	public LegalActionSaveOutDto legalActionsOutcomeSave(LegalActionSaveReq legalActionSaveReq);

	/**
	 * 
	 * Method Name: legalActionsOutcomeSaveMultiple Method Description:This is
	 * the save service for the Legal Action/Outcome window for multiple TUXEDO:
	 * CSUB39S stages and person selected.
	 * 
	 * @param legalActionSaveInDtos
	 * @return List<LegalActionSaveOutDto> @
	 */
	public List<LegalActionSaveOutDto> legalActionsOutcomeSaveMultiple(
			List<LegalActionSaveInDto> legalActionSaveInDtos);

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * for fetching the primary and secondary case-worker's employee email
	 * addresses based on the event id
	 * 
	 * @param idEvent
	 * @param dtCourtSchedule
	 * @param hostName
	 * @param cdSubType
	 * @param idCase
	 * @param fromAssign
	 * @return String
	 */
	public String fetchEmployeeEmail(AssignSaveGroupReq assignSaveGroupReq,Long idEvent, Date dtCourtSchedule, String hostName, String cdSubType, Long idCase,
			Boolean fromAssign);

	/**
	 *Method Name:	checkTMCExists
	 *Method Description:
	 *@param idStage
	 *@param cdLegalStatStatus
	 *@return
	 */
	public CommonHelperRes checkTMCExists(Long idStage, String cdLegalStatStatus);
}
