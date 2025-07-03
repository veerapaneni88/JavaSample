package us.tx.state.dfps.service.investigation.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.facility.dto.FacilityInvCnclsnValueDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvCnclsnDetailDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvProviderDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvstRsrcLinkRsrcAddDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ProgramAdminDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ResourceAddressDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityInvCnclsnDao Sep 9, 2017- 10:35:44 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FacilityInvCnclsnDao {

	/**
	 * Method Name: getInvestigatedFacilityList Method Description:This method
	 * is used to get the list of Facilities involved in the Facility
	 * Investigation conclusion.
	 * 
	 * @param stageId
	 *            - The id of the current stage.
	 * @return List<FacilityInvProviderDto> - The list of facility details.
	 */
	public List<FacilityInvProviderDto> getInvestigatedFacilityList(Long stageId);

	/**
	 * Method Name: getInvestigatedFacility Method Description: gets the list of
	 * facilities for the given resourceId
	 * 
	 * @param resourceId
	 * @return FacilityInvCnclsnValueDto @
	 */
	public FacilityInvCnclsnValueDto getInvestigatedFacility(int resourceId);

	/**
	 * Method Name: getProgramAdmins Method Description:This method is used to
	 * retrieve the program admins for the New EMR Investigation.
	 * 
	 * @param idPerson
	 *            - The id of program admin
	 * @return List<ProgramAdminDto> - The list of Program Admin details.
	 */
	public List<ProgramAdminDto> getProgramAdmins(Long idPerson);

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param idStage
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isDispositionMissing(Long idStage);

	/**
	 * Method Description: This Method will retrieve the Facility Invst Resource
	 * link details for the given FacilInvstRsrcLink ID.
	 * 
	 * @param idFacilInvstRsrcLink
	 * @return FacilityInvCnclsnDetailDto @
	 */
	public List<FacilityInvstRsrcLinkRsrcAddDto> getInvstRcrcLinkDetails(Long idFacilInvstRsrcLink);

	/**
	 * Method Name: getAllegedFacilityIDs Method Description:This Method will
	 * retrieve the list of facilities associated with the allegation for the
	 * given stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return - The list of facility resource ids.
	 */
	public List<Long> getAllegedFacilityIDs(Long idStage);

	/**
	 * Method Description: This Method will retrieve the resource address list
	 * for the given Resource ID.
	 * 
	 * @param idResource
	 * @return List<ResourceAddressDto> @
	 */
	public List<ResourceAddressDto> getRsrcAddressByResourceID(Long idResource, List<String> facilityTypeCodes);

	/**
	 * Method Description: This Method will save the resource address details
	 * for Address Type 9.
	 * 
	 * @param resourceAddressDto
	 * @return ResourceAddressDto @
	 */
	public ResourceAddressDto saveResourceAddressDtl(ResourceAddressDto resourceAddressDto);

	/**
	 * Method Description: This Method will save the facility to the link table
	 * as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	public void saveFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto);

	/**
	 * Method Description: This Method will update the resource address details.
	 * 
	 * @param resourceAddressDto
	 * @
	 */
	public void updateResourceAddressDtl(ResourceAddressDto resourceAddressDto);

	/**
	 * Method Description: This Method will update the facility to the link
	 * table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	public void updateFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto);

	/**
	 * Method Description: This Method will delete the facility from the link
	 * table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	public void deleteFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto);

	/**
	 * Method Description: This Method will initialize Facilities OverallDispo
	 * in the Facility link table
	 * 
	 * @param idSatge
	 * @
	 */
	public void initializeFacilitiesOverallDispo(Long idSatge);

	/**
	 * Method Name: getFacilityAllegationListDetails Method Description:This
	 * Method will retrieve the facility and allegation details associated with
	 * the Stage
	 * 
	 * @param idSatge
	 *            - The id of the current stage.
	 * @return List<FacilityAllegationDto> - The list of facility allegation
	 *         details.
	 */
	public List<FacilityAllegationDto> getFacilityAllegationListDetails(Long idSatge);

	/**
	 * Method Description: This Method will delete the Resource Address delete
	 * from the Resorce table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	public void deleteResourceAddressDtl(Long idRsrcAdress);

	/**
	 * Method Name: getInvFacilPrimaryAddressList Method Description:This method
	 * is used to retrieve the address of the facilities involved in the
	 * Facility Investigation conclusion.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<ResourceAddressDto> - The list of resource address details.
	 */
	public List<ResourceAddressDto> getInvFacilPrimaryAddressList(Long idStage);

	/**
	 * Method Name: getAllegedVictimsPrimaryAddressList Method Description:This
	 * method is used to retrieve the primary address of the victims in the
	 * facility allegations in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return - The list of resource address details.
	 */
	public List<ResourceAddressDto> getAllegedVictimsPrimaryAddressList(Long idStage);

	/**
	 * Method Name: getCurrentPriority Method Description:This Method will
	 * retrieve the current priority associated with the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return String - The current priority of the stage.
	 */
	public String getCurrentPriority(Long idStage);

	/**
	 * Method Name: getApprovalStatusInfo Method Description:This Method will
	 * get the approval information using id event.
	 * 
	 * @param idEvent
	 *            - The id of the facility conclusion event.
	 * @return FacilityInvCnclsnDetailDto - The dto will the approval details.
	 */
	public FacilityInvCnclsnDetailDto getApprovalStatusInfo(Long idEvent);

	/**
	 * Method Description: This Method will retrieve the program Admins
	 * 
	 * @param idPerson
	 * @return Map<Integer, String> @
	 */
	public Map<Integer, String> getProgramAdminDetails(Long idPerson);

	/**
	 * Method Description: This Method will queries the stage person link table
	 * for all clients with person type PRN and rel int SL or OV then queries
	 * the person_id table to check if each of them have an active Medicaid
	 * identifier It returns a map of person id keys and an indicator (Y or N)
	 * for each person indicating if Medicaid identifier is missing.
	 * 
	 * @param idStage
	 * @return Map<Integer, String> @
	 */
	public Map<Long, String> getPersMedicaidMissingInd(Long idStage);

	/**
	 * Method Name: getReportableConductExists Method Description:This method is
	 * used to check if reportable conduct is selected in any of the facility
	 * allegations in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return Boolean - The boolean value to indicate if reportable conduct
	 *         exists or not.
	 */
	public boolean getReportableConductExists(Long idStage);

	/**
	 * Method Name: getFacilityInvestigationDetail Method Description:This
	 * method is used to retrieve the facility investigation details.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return FacilInvDtlDto - The dto will have the facility investigation
	 *         details.
	 */
	public FacilInvDtlDto getFacilityInvestigationDetail(Long idStage);

	/**
	 * Method Name: getFaceToFaceContact Method Description:This method is used
	 * to retrieve the contact details of Face-To-Face contact in the stage.
	 * 
	 * @param contactDto
	 *            - The dto with the input paramters to fetch the face-to-face
	 *            contact details.
	 * @return ContactDto - The dto with the contact details.
	 */
	public ContactDto getFaceToFaceContact(ContactDto contactDto);

	/**
	 * Method Name: getMinDateFacilityAllegation Method Description:This method
	 * is used to retrieve the earliest facility allegation date for the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return Date - The date when the allegation occurred.
	 */
	public Date getMinDateFacilityAllegation(Long idStage);

	/**
	 * Method Name: getFacilityInjuryDetails Method Description:This method is
	 * used to retrieve the list of Injury details in the Facility Allegations
	 * in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<FacilAllegInjuryDto> - The list of injury details.
	 */
	public List<FacilAllegInjuryDto> getFacilityInjuryDetails(Long idStage);

	/**
	 * Method Name: getVictimsForStage Method Description:This method is used to
	 * get the list of person details who were entered as victims in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<StagePersonLinkDto> - The list of stage person link details.
	 */
	public List<StagePersonLinkDto> getVictimsForStage(Long idStage);

	/**
	 * Method Name: getMergeCount Method Description:This method is used to get
	 * the count of Person Merge for a particular person.
	 * 
	 * @param idPerson
	 *            - The id of the person.
	 * @return Long - The merge count number.
	 */
	public Long getMergeCount(Long idPerson);

	/**
	 * Method Name: getAdminReviewDetails Method Description:This method is used
	 * to retrieve the ARI details.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return AdminReviewDto - The dto will have the ARI details.
	 */
	public AdminReviewDto getAdminReviewDetails(Long idStage);

	/**
	 * Method Name: updateFacilityInvCnclsnDetails Method Description: This
	 * method is used to update the facility investigation details.
	 * 
	 * @param facilInvDtlDto
	 *            - The dto with the facility investigation details which has to
	 *            be updated in the db.
	 * @return FacilInvDtlDto - The dto with the updated last update date and
	 *         other facility investigation details .
	 * @throws Exception
	 */
	public FacilInvDtlDto updateFacilityInvCnclsnDetails(FacilInvDtlDto facilInvDtlDto) throws Exception;

	/**
	 * Method Name: getEvidenceList Method Description:This method is used to
	 * retrieve the list of evidence list contacts in the case.
	 * 
	 * @param idCase
	 *            - The id of the case.
	 * @return List<Long> - The list of the id case .
	 */
	public List<Long> getEvidenceList(Long idCase);

	/**
	 * Method Name: getFacilityAllegationListForVictim Method Description:This
	 * method is used to check the facility allegation for input - id stage, id
	 * person , disposition.
	 * 
	 * @param pInputDataRec
	 *            - This dto will have the input parameters for retrieving the
	 *            facility allegations.
	 * @return List<AllegationStageOutDto> - The list of facility allegation
	 *         details.
	 */
	public List<AllegationStageOutDto> getFacilityAllegationListForVictim(AllegationStageInDto pInputDataRec);

	/**
	 * Method Name: getNameTiedToFacilAllegOfCRC Method Description:This method
	 * is used to retrieve the administrator name for the facilities involved in
	 * the Facility Investigation conclusion.
	 * 
	 * @param idSatge
	 *            - The id of the stage.
	 * @return - The list of Provider information.
	 */
	public List<FacilityAllegationDto> getNameTiedToFacilAllegOfCRC(Long idSatge);

	/**
	 * Method Name: getRsrcAddressFromCapsRsrc Method Description:
	 * 
	 * @param idResource
	 * @param facilityTypeCodes
	 * @return
	 */
	public List<ResourceAddressDto> getRsrcAddressFromCapsRsrc(Long idResource, List<String> facilityTypeCodes);

}
