package us.tx.state.dfps.service.workload.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.web.bean.AddressDetailBean;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.casepackage.dto.ARPendingStagesDto;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.service.workload.dto.PriorityHistoryDto;

public interface WorkLoadDao {

	/**
	 * 
	 * Method Description: This method will retrieve the ID PERSON for a given
	 * role, for a given stage. It's used to find the primary worker for a given
	 * stage. Dam Name: CINV51D
	 * 
	 * @param idStage
	 * @param cdStgPersRole
	 * @return Long @
	 */
	public Long getPersonIdByRole(Long idStage, String cdStgPersRole);

	/**
	 * 
	 * Method Description: Returns CPS stageIds that has pending investigation
	 * conclusions.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @param idWorker
	 *            - Worker ID @
	 */
	List<Long> getInvCnclPendingStages(Long idWorker);

	/**
	 * 
	 * Method Description: Returns an array of stage ID's representing the
	 * subset of a passed array of stage ID's that are currently checked out to
	 * MPS
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @param stageIds
	 *            - Stage IDs to be filtered @
	 */
	List<Long> getCheckedOutStages(List<Long> stageIds);

	/**
	 * 
	 * Method Description: Optimize workload page query to improve page loading
	 * times. Returns the list of Stage Ids that has A-R as a prior stage
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @param idWorker
	 *            - Worker ID @
	 */
	List<Long> getStagesWithARasPriorStage(Long idWorker);

	/**
	 * 
	 * Method Description: Returns list of AR stages that requires worker
	 * attention
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @param idWorker
	 *            - Worker ID @
	 */
	List<ARPendingStagesDto> getARPendingStages(Long idWorker);

	/**
	 * 
	 * Method Description: Returns Extension request Event object for the given
	 * AR stage.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @param idStage
	 *            - Stage ID @
	 */
	ARPendingStagesDto getARExtensionRequest(Long idStage);

	public Map<String, Object> getIdEventPersonInfo(Long idStage);

	/**
	 * Method Description:Returns cd mobile Status given a Workload Case Id to
	 * it.
	 * 
	 * @param idCase
	 *            in CommonHelperReq
	 * @return String @
	 */
	String getCdMobileStatus(Long idCase);

	/**
	 * Method-Description:Returns the role of a person in a stage, provided the
	 * case is in that person's workload. If the person does not have a role, or
	 * the stage is no in their workload, will return empty string (""). Should
	 * always return PRIMARY ("PR") or SECONDARY ("SE").
	 *
	 * @param IdPerson
	 * @param IdStage
	 * @return the role of the person in that stage in their workload. @
	 */
	String getRoleInWorkloadStage(Long idStage, Long idPerson);

	/**
	 * Method-Description:This method will fetch all active stages for a person
	 * 
	 * @param PersonID
	 * @return List of all active Stage Id(s) @
	 */
	List<Long> getActiveStagesForPerson(Long idPerson);

	/**
	 * Method-Description:Check Configuration table ONLINE_PARAMETERS, if Tlets
	 * Check needs to be Enabled or Disabled
	 * 
	 * @return Boolean -- true or false @
	 */
	Boolean disableTletsCheck();

	/**
	 * Method Description: This method is to find if the contact with the
	 * purpose of initial already exist.
	 * 
	 * @paramidStage
	 * @returnBoolean -- true or False
	 * 
	 */
	Boolean getContactPurposeStatus(Long idStage);


	/**
	 * Method Description: This method is to find if there is an approved
	 * contact for the given case id.
	 * 
	 * @paramidCaseContactType
	 * @returnBoolean -- true or False
	 * 
	 */
	Boolean isAprvContactInCase(Long idCase, String idContactType);

	/**
	 * Method Description: Returns an a boolean value based on whether or not
	 * either of the two passed person ID's is tied to a stage currently checked
	 * out to MPS
	 * 
	 * @param idPerson1
	 *            and idPerson2
	 * @returnBoolean -- true or False
	 * 
	 */
	Boolean getCheckedOutPersonStatus(Long idPerson, Long idPerson2);

	ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson);

	ArrayList<StagePersonValueDto> getStagesForPerson(Long idPerson);

	public List<Long> getPersonIdsByRole(Long idStage, String cdStagePersRole);

	Long getStagePersonIdByRole(Long idStage, String cdStgPersRole);

	public List<PriorityHistoryDto> getPriorityTracking(Long idStage);

	public void savePriorityTracking(PriorityClosureSaveReq priorityClosureSaveReq);

	public TreeMap<Long, String> getLatestChildPlanEvent(Long idCase, String cdStage);

	/**
	 * Method Name: getAssignedWorkersForStage Method Description: Fetch the
	 * Primary and Secondary Workers assigned to the passed Stage ID
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> getAssignedWorkersForStage(Long idStage);
	
	public String getStagePersRole(Long idStage, Long idPerson);

	/**
	 * Artifact ID: artf151569
	 * Method Name: hasAppEventExistsBeforeFBSSRef
	 * Method Description: This method is used to check whether the current stage approval has been created before the
	 * FPR Release, and there is no approval FBSS Referral event
	 *
	 * @param idApproval
	 * @return
	 */
	Boolean hasAppEventExistsBeforeFBSSRef(Long idApproval);

	/**
	 * Artifact ID: artf140443
	 *Method Name:	isCaseAssignedToPerson
	 *Method Description:checks if a case is assigned to the case worker
	 *@param idPerson
	 *@param idCase
	 *@return
	 */

	boolean isCaseAssignedToPerson(Long idPerson, Long idCase);

	/**
	 * Retreives the open RCL INV stages where the idPerson is a victim
	 * @param idPerson
	 * @return
	 */
	public List<StagePersonValueDto> getOpenRCLINVStagesForPerson(Long idPerson);

	public boolean getWorkloadHasLoginUserForStageAndCase(Long stageId, Long caseId, Long loginUserId);


	/**
	 * Find the Program admin role by logged in user id and security role
	 *
	 * @param idUser - logged in user
	 * @param securityRole - security role
	 * @return - return as Assigned dto
	 */
    AssignedDto findEMRProgramAdminByUseridAndSecurityRole(int idUser, String securityRole);


	boolean getExecStaffSecurityForEMR(Long idPerson, String securityRole);

	public boolean insertValidatedAddressApi(AddressDetailBean addressDetailBean);

	public AddressDetailBean getValidatedAddressApi(String guid);
}