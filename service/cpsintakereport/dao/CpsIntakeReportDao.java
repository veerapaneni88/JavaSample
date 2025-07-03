package us.tx.state.dfps.service.cpsintakereport.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.PriorityChangeInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.common.dto.AgencyHomeInfoDto;
import us.tx.state.dfps.common.dto.ClassIntakeDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaServiceImpl will implemented all operation defined in
 * CpsIntakeReportService Interface related CpsIntakeReport module. Feb 9, 2018-
 * 2:02:21 PM © 2017 Texas Department of Family and Protective Services
 */

public interface CpsIntakeReportDao {

	/**
	 * 
	 * Method Name: getStageIncomingDetails Method Description: Retrieves
	 * incoming detail plus BJN number of worker. DAM: CINT65D
	 * 
	 * @param IdStage
	 * @return
	 * @throws DataNotFoundException
	 */

	public IncomingStageDetailsDto getStageIncomingDetails(Long IdStage);

	/**
	 * 
	 * Method Name: getPersonList Method Description:List DAM retrieves all
	 * persons in a specific set for a specified stage ID, considering closure
	 * and end dating. The set to retrieve is specified on input. DAM : CINT66D
	 * 
	 * @param idStage
	 * @param iQueryType
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<PersonDto> getPersonList(Long idStage, int iQueryType);

	/**
	 * 
	 * Method Name: getPhoneInfo Method Description:List DAM retrieves all phone
	 * info for a specified stage ID, considering closure and end dating. DAM
	 * Name: CallCINT62D
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */

	public List<PhoneInfoDto> getPhoneInfo(Long idStage);

	/**
	 * 
	 * Method Name: getWorkerInfo Method Description:List DAM retrieves, based
	 * on event type and stage ID, the worker information associated with the
	 * event. DAM Name: CINT67D
	 * 
	 * @param idStage
	 * @param cdEventType
	 * @return
	 * @throws DataNotFoundException
	 */

	public List<WorkerInfoDto> getWorkerInfo(Long idStage, String cdEventType);

	/**
	 * DAM Name: CINT68D Method Name: getPriorityChangeInfo Method Description:
	 * List DAM retrieves history of priority change information for a specified
	 * stage ID.
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<PriorityChangeInfoDto> getPriorityChangeInfo(Long idStage);

	/**
	 * DAM Name: CLSC52D Method Name: getApprovalList Method Description: Return
	 * all approvers for a specific intake.
	 * 
	 * @param idEvent
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ApprovalDto> getApprovalList(Long idEvent);

	/**
	 * Method Name: getAgencyHomeInfoDto Method Description: Return
	 * agency home information.
	 *
	 * @param facilityId
	 * @return
	 * @throws DataNotFoundException
	 */
	public AgencyHomeInfoDto getAgencyHomeInfoDto(Long facilityId);

	/**
	 * Method Name: getAgencyHomeInfoDto Method Description: Return
	 * agency home information.
	 *
	 * @param resourceId
	 * @return
	 * @throws DataNotFoundException
	 */
	public AgencyHomeInfoDto getResourceInfoDto(Long resourceId);

	/**
	 * Method Name: getIntakeClassData Method Description: Return
	 * incident date, self report, facilityId from Intake table in class application.
	 *
	 * @param caseId
	 * @return
	 * @throws DataNotFoundException
	 */
	public ClassIntakeDto getIntakeClassData(Long caseId);




	/**
	 *
	 * Method Name: getCaseIncomingDetails Method Description: Get the Intake stage summary
	 * ( artf135380 Get all Intake stages )
	 *
	 * @param idCase
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */

	public List<IncomingStageDetailsDto> getCaseIncomingDetails(Long idCase,Long idStage);

	/**
	 * Method Name: getIncomingPersonTxtEmail Method Description: If a reporter is NOT a 'DFPS Staff' ,
	 * AND , if a reporter is related ( Relate feature in SWI ) , email data is captured in the
	 * TXT_EMAIL column of the INCOMING_PERSON table..
	 *
	 * @param idPerson
	 * @param idStage
	 * @return String
	 */
	public String getIncomingPersonTxtEmail(Long idPerson, Long idStage);

}
