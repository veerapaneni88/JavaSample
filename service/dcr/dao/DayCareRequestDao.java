package us.tx.state.dfps.service.dcr.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.dto.StaffDto;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.dto.DayCareSearchListDto;
import us.tx.state.dfps.service.dcr.dto.SSCCDayCareRequestDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dto.EventIdDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Day Care
 * Request Dao Interface Jul 22, 2017- 7:15:59 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface DayCareRequestDao {

	public boolean arePersonsInSameDCRequest(Long idPerson1, Long idPerson2);

	public DayCareRequestDto listDcRequestDatesForPerson(Long idPerson);

	/**
	 * Method: retrieveSvcAuthPerson Method Description: Retrieve the list of
	 * persons from dayCare related Service Auth
	 * 
	 * @param idSvcAuthEvent
	 *            - Service Authorization event
	 * @return List<PersonDiDto> - returns List of PersonId
	 */
	public List<PersonDiDto> retrieveSvcAuthPerson(Long idSvcAuthEvent);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuthPersonDtl Method Description:
	 * Retrieve the person details related to DayCareRequest for Service Auth
	 * Detail
	 * 
	 * @param idDayCareEvent
	 *            - Day Care Service Authorization event
	 * @param idStage
	 *            - ID Stage
	 * @return List<DayCarePersonValueDto> - List of DayCarePersonDto which
	 *         contains list of person having day care
	 */
	public List<DayCarePersonDto> retrieveDayCareRequestSvcAuthPersonDtl(Long idDayCareEvent, Long idStage);

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuth Method Description: Retrieve
	 * the daycarerequest event id from dayCare_svc_auth_link table
	 * 
	 * @param idSvcAuthEvent
	 *            - Service Authorization event id
	 * @return dayCareEventDto - Day care event Id
	 */
	public EventIdDto retrieveDayCareRequestSvcAuth(Long idSvcAuthEvent);

	/**
	 * 
	 * Method Name: fetchDayCareDetailsForSvcAuthEventId Method Description:This
	 * function retrieve the DayCare Request details for the given Service Auth
	 * Event.
	 * 
	 * @param idSvcAuthEvent
	 * @return DayCareRequestDto
	 * @throws DataNotFoundException
	 */
	public DayCareRequestDto fetchDayCareDetailsForSvcAuthEventId(Long idSvcAuthEvent);

	/**
	 * 
	 * Method Name: getDaycareCoordinator Method Description:This function
	 * returns Regional Day Care Coordinator.
	 * 
	 * @param idEvent
	 * @return PersonValueDto
	 * @throws DataNotFoundException
	 */
	public PersonValueDto getDaycareCoordinator(Integer idEvent);

	/**
	 * Method Name: insertDayCareRequest Method Description:Inserts the details
	 * of DayCareRequestValueDto
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertDayCareRequest(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * Method Name: insertSSCCDayCareRequest Method Description:This method
	 * inserts new record into SSCC_DAYCARE_REQUEST table.
	 * 
	 * @param ssccDayCareReqDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareReqDto);

	/**
	 * Method Name: updateDayCareRequestDetail Method Description:Updates
	 * Daycare request detail from DAYCARE_REQUEST table.
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */

	/**
	 * Method Name: updateSSCCDayCareRequest Method Description:This method
	 * updates SSCC_DAYCARE_REQUEST table using ssccDayCareRequestDto
	 * 
	 * @param ssccDayCareRequestDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareRequestDto);

	/**
	 * Method Name: updateDayCarePersonLink Method Description:Update the
	 * DAYCARE_PERSON_LINK
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateDayCarePersonLink(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * Method Name: retrievePersonDayCareDetails Method Description:Retrieve all
	 * valid daycare requests for input person
	 * 
	 * @param dayCareRequestValueDto
	 * @return DayCareRequestValueDto
	 * @throws DataNotFoundException
	 */
	public DayCareRequestDto retrievePersonDayCareDetails(DayCareRequestDto dayCareRequestValueDto);

	/**
	 * Method Name: updateApproversStatus Method Description:Updates the
	 * Approvers Status Code to Invalid
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateApproversStatus(Long idEvent);

	/**
	 * Method Name: getAppEventId Method Description:Get Daycare Request
	 * Approval id event
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getAppEventId(Long idEvent);

	/**
	 * Method Name: updateAppEventStatus Method Description:Update Daycare
	 * Approval event status to invalid
	 * 
	 * @param idApprovalEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateAppEventStatus(Long idApprovalEvent);

	/**
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idDayCareRequest
	 * 
	 * @param idEvent
	 * @return SSCCDayCareRequestDto
	 * @throws DataNotFoundException
	 */
	public SSCCDayCareRequestDto retrieveSSCCDayCareRequest(Long idEvent);

	/**
	 * Method Name: deleteSSCCDayCareRequest Method Description:This method
	 * deletes SSCC DayCare Request Record using idSSCCDayCareRequest
	 * 
	 * @param idSSCCDayCareRequest
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteSSCCDayCareRequest(Long idSSCCDayCareRequest);

	/**
	 * Method Name: deleteDayCareRequest Method Description:called complex
	 * delete procedure to delete daycare.
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteDayCareRequest(Long idEvent);

	/**
	 * Method Name: fetchActiveReferralForChild Method Description:This function
	 * returns Active SSCC Referral for the Child.
	 * 
	 * @param idPerson
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchActiveReferralForChild(Long idPerson);

	/**
	 * Method Name: fetchReferralsForAllPersonsInDaycareRequest Method
	 * Description:This function returns All the Referrals(Active and Inactive)
	 * for the DayCare Request.
	 * 
	 * @param idDayCareRequest
	 * @return List<SSCCRefDto>
	 * @throws DataNotFoundException
	 */
	public List<SSCCRefDto> fetchReferralsForAllPersonsInDaycareRequest(Long idDayCareRequest);

	/**
	 * Method Name: retrieveDCReqEventForSvcAuthEvent Method Description:This
	 * function returns Day Care Request Event Id for the given Service
	 * Auhtorization Event Id. If No Day Care Request Found, returns 0.
	 * 
	 * @param idSvcAuthEvent
	 * @returnLong
	 * @throws DataNotFoundException
	 */
	public Long retrieveDCReqEventForSvcAuthEvent(Long idSvcAuthEvent);

	/**
	 * Method Name: getDayCareApprovers Method Description:This method returns
	 * array of Long values
	 * 
	 * @param idEvent
	 * @return Long[]
	 * @throws DataNotFoundException
	 */
	public Long[] getDayCareApprovers(Long idEvent);

	/**
	 * Method Name: updateDayCareRequestDetail Method Description:Updates
	 * Daycare request detail from DAYCARE_REQUEST table.
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateDayCareRequestDetail(DayCareRequestDto dayCareRequestDto);

	/**
	 * 
	 * Method Name: daycareSearch Method Description: This is DAO layer for Day
	 * Care Search(CLASS)
	 * 
	 * @param searchDto
	 * @return List<DayCareSearchListDto>
	 */
	public List<DayCareSearchListDto> daycareSearch(DayCareSearchListDto searchDto);

	/**
	 * 
	 * Method Name: getFacilityById Method Description: This is DAO layer for
	 * retrieving facility by facility id
	 * 
	 * @param idFacility
	 * @return DayCareSearchListDto
	 */
	public DayCareSearchListDto getFacilityById(Long idFacility);

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
	boolean isDayCareRequestLinkedToServiceAuth(Long idEvent);

	/**
	 * 
	 * Method Name: generateServiceAuth Method Description:
	 * 
	 * @param idDayCareEvent
	 * @param idUser
	 * @param idSvcAuthEvent
	 * @return
	 */
	Long generateServiceAuth(Long idDayCareEvent, Long idUser, Long idSvcAuthEvent);

	/**
	 * 
	 * Method Name: getStaffInformation Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	StaffDto getStaffInformation(Long idStage);

	/**
	 * Method Name: getDaycareCodes Method Description: GET DAY CARE CODE
	 * 
	 * @param
	 * @return Map<String, String>
	 */
	public Map<String, String> getDaycareCodes();

	/**
	 * 
	 * Method Name: dayCareService Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return
	 */

	boolean dayCareService(Long idDayCareRequest);
	
	/**
	 * Method Name: isIcpcProgramSpecialist Method Description:This method is to
	 * check if whether a person is ICPC program specialist or not.
	 * 
	 * @param idDayCareRequest
	 * @return
	 */
	public boolean isIcpcProgramSpecialist(Long idPerson);

}
