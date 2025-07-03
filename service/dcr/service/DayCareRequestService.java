package us.tx.state.dfps.service.dcr.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.dto.StaffDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.dto.DayCareSearchListDto;
import us.tx.state.dfps.service.dcr.dto.SSCCDayCareRequestDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.workload.dto.EventIdDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for daycarerequest service Sep 25, 2017- 12:08:39 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface DayCareRequestService {
	/**
	 * 
	 * Method Name: createTWCTransmissionFailureAlert Method Description:This
	 * function creates Alert to the RDCC and the CaseWorker notifying them of
	 * the failed transmission to TWC.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param idUser
	 * @
	 */
	public Long createTWCTransmissionFailureAlert(Long idSvcAuthEvent, Long idStage, Long idUser);

	/**
	 * 
	 * Method Name: saveDayCareRequestDetail Method Description:This method will
	 * insert/update the DAYCARE_REQUEST table
	 * 
	 * @param dayCareRequestValueDto
	 * @param saveType
	 * @return Long @
	 */
	public Long saveDayCareRequestDetail(DayCareRequestDto dayCareRequestValueDto, String saveType);

	/**
	 * 
	 * Method Name: saveDayCarePersonInfo Method Description:This method
	 * saves(insert/update) Day Care Request Person Information into the
	 * database.
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 */
	public Long saveDayCarePersonInfo(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * 
	 * Method Name: updateSSCCDayCareRequest Method Description:This method
	 * updates SSCC DayCare Request table.
	 * 
	 * @param ssccDayCareRequestDto
	 * @return Long
	 */
	public Long updateSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareRequestDto);

	/**
	 * 
	 * Method Name: updateDayCarePersonLink Method Description:This method is to
	 * update Daycare Person Link table
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long @
	 */
	public Long updateDayCarePersonLink(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * 
	 * Method Name: retrievePersonDayCareDetails Method Description:Retrieve all
	 * valid daycare requests for input person
	 * 
	 * @param dayCareRequestValueDto
	 * @return DayCareRequestValueDto @
	 */
	public DayCareRequestDto retrievePersonDayCareDetails(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * 
	 * Method Name: createDayCareRejectAlert Method Description:this method
	 * returns approvers count
	 * 
	 * @param idEvent
	 * @param szCdTask
	 * @param idStage
	 * @param userId
	 * @param szCdStage
	 * @param szNmStage
	 * @return Long @
	 */
	public Long createDayCareRejectAlert(Long idEvent, String szCdTask, Long idStage, Long userId, String szCdStage,
			String szNmStage);

	/**
	 * 
	 * Method Name: updateApproversStatus Method Description:Updates the
	 * Approvers Status to Invalid.
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	public Long updateApproversStatus(Long idEvent);

	/**
	 * 
	 * Method Name: updateAppEventStatus Method Description:Updates the APP
	 * event to Invalid.
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	public Long updateAppEventStatus(Long idEvent);

	/**
	 * 
	 * Method Name: createDayCareTerminationAlert Method Description:This method
	 * creates Alert for DayCare Coordinator when the Service Authorization is
	 * Terminated
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param userId
	 * @param idChild
	 * @param idSvcAuth
	 * @return Long @
	 */
	public Long createDayCareTerminationAlert(Long idEvent, Long idStage, Long userId, Long idChild, Long idSvcAuth);

	/**
	 * 
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idEvent.
	 * 
	 * @param idEvent
	 * @return SSCCDayCareRequestDto
	 * @th
	 */
	public SSCCDayCareRequestDto retrieveSSCCDayCareRequest(Long idEvent);

	/**
	 * 
	 * Method Name: deleteDayCareRequest Method Description:If DFPS Worker
	 * deletes DFPS DayCare Request Follow the current process of using DayCare
	 * Request Complex delete.
	 * 
	 * If DFPS Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables except for SSCC_DAYCARE_REQUEST table. set
	 * ID_DAYCARE_REQUEST = 0 in SSCC_DAYCARE_REQUEST table.
	 * 
	 * If SSCC Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables including SSCC_DAYCARE_REQUEST table.
	 * 
	 * @param idEvent
	 * @param isSSCCWorker
	 * @return SSCCDayCareRequestDto @
	 */
	public SSCCDayCareRequestDto deleteDayCareRequest(Long idEvent, Boolean isSSCCWorker);

	/**
	 * 
	 * Method Name: fetchActiveReferralForChild Method Description:This function
	 * returns Active SSCC Referral for the Child.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	public Long fetchActiveReferralForChild(Long idPerson);

	/**
	 * 
	 * Method Name: fetchReferralsForAllPersonsInDaycareRequest Method
	 * Description:This function returns All the Referrals(Active and Inactive)
	 * for the DayCare Request.
	 * 
	 * @param idDayCareRequest
	 * @return List<SSCCRefDto> @
	 */
	public List<SSCCRefDto> fetchReferralsForAllPersonsInDaycareRequest(Long idDayCareRequest);

	/**
	 * 
	 * Method Name: createTWCTransmissionAlert Method Description:This function
	 * creates Alert to RDCC notifying them that the Day Care Service
	 * Authorization has been approved but not transmitted to TWC.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param userId
	 * @return Long @
	 */
	public Long createTWCTransmissionAlert(Long idSvcAuthEvent, Long idStage, Long userId);

	/**
	 * 
	 * Method Name: createTWCTransmissionTerminateAlert Method Description: This
	 * function creates Alert to the User notifying them that the Approved Day
	 * Care Service Authorization has been terminated and Alert should be sent
	 * to the user.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param userId
	 * @return Boolean @
	 */
	public Boolean createTWCTransmissionTerminateAlert(Long idSvcAuthEvent, Long idStage, Long userId);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuthPersonDtl Method Description:
	 * Retrieve the person details related to DayCareRequest for Service Auth
	 * Detail
	 * 
	 * @param dayCareEventDto
	 *            - Day Care Service Authorization event and Id Stage
	 * @return DayCareRequestValueDto - contains List of DayCarePersonDto which
	 *         contains list of person having day care
	 */
	List<DayCarePersonDto> retrieveDayCareRequestSvcAuthPersonDtl(Long idEvent, Long idStage);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuth Method Description: Retrieve
	 * the daycarerequest event id from dayCare_svc_auth_link table
	 * 
	 * @param svcAuthEvent
	 *            - Service Authorization event id
	 * @return dayCareEventDto - Day care event Id
	 */
	public EventIdDto retrieveDayCareRequestSvcAuth(Long idSvcAuthEvent);

	/**
	 * Method Name: retrieveDayCarePersonLink Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return
	 */
	List<DayCarePersonDto> retrieveDayCarePersonLink(Long idDayCareRequest);

	/**
	 * 
	 * Method Name: hasChangedSystemResponses Method Description:
	 * 
	 * @param idPerson
	 * @param idDayCareRequest
	 */
	Boolean hasChangedSystemResponses(Long idPerson, Long idDayCareRequest);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestDetail Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	DayCareRequestDto retrieveDayCareRequestDetail(Long idEvent);

	/**
	 * 
	 * Method Name: isDayCareRequestLinkedToServiceAuth Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	Boolean isDayCareRequestLinkedToServiceAuth(Long idEvent);

	/**
	 * 
	 * Method Name: generateServiceAuth Method Description:
	 * 
	 * @param idDayCareEvent
	 * @param idUser
	 * @param idSvcAuthEvent
	 * @return idDaycareSvcAuthLink
	 */
	Long generateServiceAuth(Long idDayCareEvent, Long idUser, EventValueDto eventValueDto);

	/**
	 * 
	 * Method Name: getStaffInformation Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	StaffDto getStaffInformation(Long idStage);

	/**
	 * Method Name: dayCareService Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return Boolean
	 */
	Boolean dayCareService(Long idDayCareRequest);

	/**
	 * Method Name: populateAddress Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	List<DayCarePersonDto> populateAddress(Long idStage);

	/**
	 * 
	 * Method Name: daycareSearch Method Description:This is service layer for
	 * Day Care Search(CLASS)
	 * 
	 * @param searchDto
	 * @return
	 */
	public List<DayCareSearchListDto> daycareSearch(DayCareSearchListDto searchDto);

	/**
	 * 
	 * Method Name: getFacilityById Method Description: This is Service layer
	 * for retrieving facility by facility id
	 * 
	 * @param idFacility
	 * @return DayCareSearchListDto
	 */
	public DayCareSearchListDto getFacilityById(Long idFacility);

	/**
	 * Method Name: getDaycareCodes Method Description: GET DAY CARE CODE
	 * 
	 * @param
	 * @return Map<String, String>
	 */
	public Map<String, String> getDaycareCodes();

	/**
	 * Method Name: savePersonList Method Description: Service Interface to Save
	 * Person List
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param personListDto
	 * @param isApprovalMode
	 * @return DayCareRequestDto
	 */
	DayCareRequestDto savePersonList(long idStage, long idEvent, List<PersonListDto> personListDto,
			boolean isApprovalMode);

	/**
	 * Method Name: deletePerson Method Description:
	 * 
	 * @param idDayCareRequest
	 * @param idPerson
	 * @param idEvent
	 * @param approvalMode
	 * @return boolean
	 */
	boolean deletePerson(long idDayCareRequest, long idPerson, long idEvent, boolean approvalMode);

	/**
	 * Method Name: createDayCareRegionalAlert Method Description: Create Alert
	 * for Regional Daycare Coordinator when the Daycare Service Authorization
	 * is Terminated.
	 * 
	 * @param dayCareRequestDto
	 */
	public void createDayCareRegionalAlert(DayCareRequestDto dayCareRequestDto);

	/**
	 * Method Name: validateAndNotify Method Description:Interface for validate
	 * and notify
	 * 
	 * @param idPerson
	 * @param idEvent
	 * @return Long
	 */
	Long validateAndNotify(long idPerson, long idEvent);

	Long retrieveSvcAuthPersonCount(Long idEvent);

	/**
	 * Method Name: retrieveDayCareRequestDetailsForDisplay Method
	 * Description:Service to retrieve Day Care Request Details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idUser
	 */
	DayCareRequestRes retrieveDayCareRequestDetailsForDisplay(Long idStage, Long idEvent, Long idUser);

}
