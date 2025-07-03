package us.tx.state.dfps.service.legal.dao;

import java.text.ParseException;
import java.util.List;

import us.tx.state.dfps.common.domain.LegalAction;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionsModificationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Inserts/Updates and Deletes the Legal Action. Oct 20, 2017-
 * 11:59:40 AM © 2017 Texas Department of Family and Protective Services
 */
public interface LeglActnModificationDao {
	/**
	 * 
	 * Method Name: insertOrUpdateLegalAction Method Description:Inserts/Updates
	 * and Deletes the Legal Action. DAM: caud03d
	 * 
	 * @param legalActionsModificationDto
	 * @throws ParseException
	 * @throws DataNotFoundException
	 */
	public LegalAction insertOrUpdateLegalAction(LegalActionsModificationDto legalActionsModificationDto);

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * to get the Email Id of Employee for given id event
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	public List<EmailDetailsDto> fetchEmployeeEmail(Long idEvent);

	/**
	 * 
	 * Method Name: fetchLegalActionEventIds Method Description:This Method is
	 * used for fetching the List of eventId along with
	 * dtScheduledCourt,cdLegalActActnSubtype for the given idCase for the task
	 * code '3030', event type 'LEG', action type 'CCVS' and sub type in
	 * '30','40','60'
	 * 
	 * @param idCase
	 * @return List<LegalActionRtrvOutDto>
	 */
	public List<LegalActionRtrvOutDto> fetchLegalActionEventIds(Long idCase);

	/**
	 *Method Name:	checkTMCExists
	 *Method Description:
	 *@param idStage
	 *@param cdLegalStatStatus
	 *@return
	 */
	public Long checkTMCExists(Long idStage, String cdLegalStatStatus);
	
	/**
	 * 
	 * Method Name: getEmailAddress Method Description: This Method is used
	 * to get the Email Id of Employee for given id person
	 * 
	 * @param idPersonList
	 * @return List<EmailDetailsDto>
	 */
	public List<EmailDetailsDto> getEmailAddress(List<Long> idPersonList, Long idEvent);
}
