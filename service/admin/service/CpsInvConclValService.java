package us.tx.state.dfps.service.admin.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistOutDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.common.response.CPSInvConclValBeanRes;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Interface for fetching event, person details Aug 9, 2017- 6:24:56 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CpsInvConclValService {

	/**
	 * 
	 * Method Name: callCpsInvConclValService Method Description:This service
	 * performs server side validation for the CPS Investigation Conclusion
	 * window. The edits performed by the service depend on the decode string in
	 * DCD_EDIT_PROCESS. Once all required edits are passed, the service will
	 * set all the to-dos associated with the input ID_EVENT to 'COMPLETE' and
	 * return a list of all the ID_EVENTs associated with the input ID_STAGE.
	 * 
	 * @param cpsInvConclValiDto
	 * @return CpsInvConclValoDto
	 */
	public CpsInvConclValoDto cpsInvConclValidationService(CpsInvConclValiDto cpsInvConclValiDto);

	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will give list
	 * of data from Event table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStageTypeTaskOutDto> @
	 */
	public List<EventStageTypeTaskOutDto> getEventDtls(CpsInvConclValiDto cpsInvConclValiDto);

	/**
	 * 
	 * Method Name: getStageAndEventDetails Method Description: This method will
	 * give list of data from Event and Stage Person Link table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	public List<EventStagePersonLinkInsUpdOutDto> getStageAndEventDetails(CpsInvConclValiDto cpsInvConclValiDto,
			String cdTask, String cdEventType);

	/**
	 * 
	 * Method Name: updateTodoCompleted Method Description:
	 * 
	 * @param pInputMsg
	 * @
	 */
	public void updateTodoCompleted(CpsInvConclValiDto pInputMsg);

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will give
	 * list of data from Stage table.
	 * 
	 * @param CpsInvConclValiDto
	 * @return boolean
	 */
	public boolean getStageDetails(CpsInvConclValiDto cpsInvConclValiDto);

	/**
	 * 
	 * Method Name: checkServiceAuth Method Description:
	 * 
	 * @param CpsInvConclValiDto
	 * @param CpsInvConclValoDto
	 */
	public void checkServiceAuth(CpsInvConclValiDto cpsInvConclValiDto, CpsInvConclValoDto cpsInvConclValoDto);

	/**
	 * 
	 * Method Name: getStageAndEventDtls Method Description: This method will
	 * give list of data from Event and StagePersonLink table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	public List<EventStagePersonLinkInsUpdOutDto> getStageAndEventDtls(CpsInvConclValiDto pInputMsg);

	/**
	 * 
	 * Method Name: getAuthEventLink Method Description: This method will give
	 * list of data from ServiceAuth and Event table.
	 * 
	 * @param idSvcAuthEvent
	 * @return List<SvcAuthEventLinkOutDto>
	 */
	public List<SvcAuthEventLinkOutDto> getAuthEventLink(long idSvcAuthEvent);

	/**
	 * 
	 * Method Name: getServiceAuthentication Method Description: This method
	 * will give list of data from SVC_AUT_DTL table.
	 * 
	 * @param idSvcAuth
	 * @return List<SvcAuthDetailNameOutDto>
	 */
	public List<SvcAuthDetailNameOutDto> getServiceAuthentication(long idSvcAuth);

	/**
	 * 
	 * Method Name: getStageDtlsByDate Method Description: This method will give
	 * list of data from Stage table.
	 * 
	 * @param pInputMsg
	 * @return List<StageCdOutDto> @
	 */
	public List<StageCdOutDto> getStageDtlsByDate(CpsInvConclValiDto pInputMsg);

	/**
	 * 
	 * Method Name: consistencyCheck Method Description: This method will
	 * perform consistency check.
	 * 
	 * @param pInputMsg
	 * @
	 */
	public void consistencyCheck(CpsInvConclValiDto pInputMsg, CpsInvConclValoDto pOutputMsg);

	/**
	 * 
	 * Method Name: getEmerAssistDtls Method Description: This method will give
	 * list of data from Emergency Assist table.
	 * 
	 * @param idEvent
	 * @return List<EmergencyAssistOutDto>
	 */
	public List<EmergencyAssistOutDto> getEmerAssistDtls(long idEvent);

	/**
	 * 
	 * Method Name: getEventDetails Method Description: This method will give
	 * list of data from EVENT table.
	 * 
	 * @param pInputMsg
	 * @return List<EventRiskAssessmentOutDto>
	 */
	public List<EventRiskAssessmentOutDto> getEventDetails(CpsInvConclValiDto pInputMsg);

	/**
	 * 
	 * Method Name: getInvestmentDtls Method Description: This method will give
	 * list of data from Investment table.
	 * 
	 * @param pInputMsg
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	public List<CpsInvstDetailStageIdOutDto> getInvestmentDtls(CpsInvConclValiDto pInputMsg);

	/**
	 * 
	 * Method Name: getPersonDtls Method Description: This method will give list
	 * of data from PERSON table.
	 * 
	 * @param CpsInvConclValiDto
	 * @return List<StagePrincipalDto> @
	 */
	public List<StagePrincipalDto> getPersonDtls(CpsInvConclValiDto cpsInvConclValiDto,
			CpsInvConclValoDto cpsInvConclValoDto);

	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: This method will give
	 * list of data from ALLEGATION table.
	 * 
	 * @param pInputMsg
	 * @param ulIdPerson
	 * @param dtDtPersonDeath
	 * @param szCdPersonDeath
	 * @param szCdDeathRsnCps
	 * @return List<AllegationStageOutDto> @
	 */
	public List<AllegationStageOutDto> getAllegationDtls(CpsInvConclValiDto pInputMsg, CpsInvConclValoDto pOutputMsg,
			long ulIdPerson, Date dtDtPersonDeath, String szCdPersonDeath, String szCdDeathRsnCps);

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method Description:This method will
	 * give list of data from Criminal History table.
	 * 
	 * @param cpsInvConclValiDto
	 * @return Long
	 */
	public Long getCriminalCheckRecords(CpsInvConclValiDto cpsInvConclValiDto);

	/**
	 * 
	 * Method Name: getPriorStage Method Description:This method will give list
	 * of IdPrior Stage.
	 * 
	 * @param boolean
	 * @return CpsInvConclValiDto
	 */
	public boolean getPriorStage(CpsInvConclValiDto cpsInvConclValiDto);

	/**
	 * 
	 * Method Name: getLegalEventDtls Method Description: This method will give
	 * list of data from EVENT table.
	 * 
	 * @param pInputMsg
	 * @return List<LegalActionEventOutDto> @
	 */
	public List<LegalActionEventOutDto> getLegalEventDtls(CpsInvConclValiDto pInputMsg, CpsInvConclValoDto pOutputMsg,
			boolean bSubsequentSUB);

	/**
	 * 
	 * Method Name: getEventStatus Method Description: This method will give
	 * list of data from EVENT table.
	 * 
	 * @param pInputMsg
	 * @return List<EventStageTypeStatusOutDto> @
	 */
	public List<EventStageTypeStatusOutDto> getEventStatus(CpsInvConclValiDto pInputMsg);

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: fetchCoSleepingData
	 * 
	 * @param idStage
	 * @return CPSInvConclValBeanRes @
	 */
	public CPSInvConclValBeanRes fetchCoSleepingData(Long idStage);

	/**
	 * Method Description: This method Returns any prior stage ID for any given
	 * stage ID and a type request. Example. If a INT stage needs be found for a
	 * case thats currently in a FPR stage. Pass FPR stage ID and 'INT' Method
	 * Name: fetchPriorStageInReverseChronologicalOrder
	 * 
	 * @param idStage
	 * @param cdStageType
	 * @return CPSInvConclValBeanRes @
	 */
	public CPSInvConclValBeanRes getPriorStageInReverseChronologicalOrder(Long idStage, String cdStageType);
	
	/**
	 *  this method retrieves row from SVC_AUTH_EVENT_LINK based on caseid  
	 * Code written for defect 2337 artf55938
	 * @param idCase
	 * @return
	 */
	public List<SvcAuthEventLinkOutDto> getAuthEventLinkByCase(long idCase);

}
