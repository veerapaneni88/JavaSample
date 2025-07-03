package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.CaseFileMgtReq;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageIdDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.service.workload.dto.StageResourceDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S Class
 * Description: Office DAO Interface Mar 24, 2017 - 7:15:39 PM
 */

public interface StagePersonLinkDao {

	/**
	 * 
	 * Method Description: This Method will will retrieve the COUNT from the
	 * stage table where the ID PERSON entered along with the ID CASE match an
	 * ID PERSON (primary worker) in one of the stages in the case DAM:CMSC36D
	 * Service: CCFC21S
	 * 
	 * @param ulIdCase
	 * @param ulIdPerson
	 * @return @
	 */
	public Long getPrimaryCaseWorker(CaseFileMgtReq caseFileMgtReq);

	/**
	 * 
	 * Method Description: Method is implemented in StagePersonLinkDaoImpl to
	 * perform AUD operations Service Name: CCMN25S
	 * 
	 * @param archInputDto
	 * @param stagePersonLinkDto
	 * @return String @
	 */
	public String getStagePersonLinkAUD(StagePersonLinkDto stagePersonLinkDto, ServiceReqHeaderDto serviceReqHeaderDto);

	/**
	 * 
	 * Method Description: This Method will retrieves all ID_STAGE's from the
	 * STAGE_PERSON_LINK table where ID_PERSON matches the ID_PERSON passed to
	 * this dam from the service. Dam Name: CCMNA2D
	 * 
	 * @param idPerson
	 * @return List<StageDto> @
	 */
	public List<StageIdDto> getStageIdList(Long idPerson);

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
	public Long getPersonIdByRole(Long idStage, String cdStagePersRole);

	/**
	 * 
	 * Method Description: This Method Changes New Assignement Indicator from
	 * True to false. Dam Name: CCMN52D Service Name: CCMN14S
	 * 
	 * @param idPerson
	 * @param ulIdStage
	 * @return List<StageDto> @
	 */
	public String StgPrsnLinkUpdt(List<Long> ulIdStage, Long ulIdPerson);

	/**
	 * 
	 * Method Description: Method to retrieve Kinship details to populate the
	 * CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public StgPersonLinkDto getStagePersonLinkDetails(Long uidPerson, Long uidStage);

	/**
	 * Make sure to delete all therecords in the STAGE PERSON LINK table where
	 * ID STAGE equals the Input.ID STAGE and the CD_STAGE_PERS_ROLE = 'PR' or
	 * 'SE'
	 * 
	 * @param idStage
	 * @
	 */
	public void deletePRSEStagePersonLinkByIdStage(Long idStage);

	/**
	 * 
	 * Method Description: Method to update Kinship details and populate the CVS
	 * Home window. EJB - CVS FA HOME , DAM - CINVA4D
	 * 
	 * @param stagePersonLink
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public StgPersonLinkDto updateStagePersonLinkDetails(StagePersonLink stagePersonLink);

	/**
	 * 
	 * Method Description: Method to get the StagePersonLinkId from the table
	 * 
	 * @param stagePersonLink
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public Long getStagePersonLinkId(Long uidStage, Long uidPerson);

	/**
	 * This DAM is used by CloseOpenStage (Stage Progression) to perform DELETE
	 * functionality on the TODO table. Given ID STAGE, delete all
	 * system-generated (where person created is null), non-completed (where
	 * date completed is null) todos which are not tied to a monthly contact
	 * event (contact type = 'CMST' and 'C3MT'). The timestamp is not used.
	 * There is no ADD or UPDATE functionality. Service Name: CCMN03U, Dam Name-
	 * CCMNH1D
	 * 
	 * @param idStage
	 * @
	 */
	public void deleteTodoForClosingStage(Long idStage);

	/**
	 * This dam rtrieves all principals linked to stage along with their county,
	 * region, name, stage role, & stage relation
	 * 
	 * 
	 * Service Name: CCMN03U, Dam Name- CLSC18D
	 * 
	 * @param idStage
	 * @param stageType
	 * @return @
	 */
	public List<StagePrincipalDto> getStagePrincipalByIdStageType(Long idStage, String stageType);

	/**
	 * 
	 * Method Description: Method to check if already a kin from the table
	 * 
	 * @param stagePersonLink
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public String getIsKin(CvsFaHomeReq cvsFaHomeReq);

	/**
	 * 
	 * Method Description: Method to check if another primary caregiver is
	 * present for CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return isKin @
	 */

	public String getIsExistPrimaryKin(CvsFaHomeReq cvsFaHomeReq);

	/**
	 * This DAM receives ID STAGE from the service and returns one or more rows
	 * from the STAGE_PERSON_LINK table.
	 * 
	 * 
	 * Service Name - CCMN03U, DAM Name - CCMNB9D
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<StagePersonLinkDto> getStagePersonLinkByIdStage(Long idStage);

	/**
	 * This method is to get the stagepersonlink by stage id DAM CINVB1D
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<AllegtnPrsnDto> getStagePrsnLinkByStageId(Long idStage, String cdStagePersType);

	/**
	 * This DAM will retreive full rows from the Stage_Person_Link table and the
	 * Caps_Resource table for a given child. It is intended to be used to
	 * determine the FAD Family members and case name for the new Post-Adoption
	 * stage being created at stage progression.
	 * 
	 * Service Name - CCMN03U, DAM Name - CLSS63D
	 * 
	 * @param idPerson
	 * @param plannedType
	 * @return @
	 */
	public List<StageResourceDto> getStageResourceForChild(Long idPerson, String plannedType);

	/**
	 * Retrieves Primary Child id and Case id from Stage_person_link given stage
	 * id as input.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA5D
	 * 
	 * @param idStage
	 * @return @
	 */
	public StagePersonLinkDto getPrimaryChildIdByIdStage(Long idStage);

	/**
	 * Retrieves a corresponding ADO stage id from stage_link given a SUB stage
	 * id.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA6D
	 * 
	 * @param idStage
	 * @return @
	 */
	public Long getIdADOStageByIdSUBStage(Long idStage);

	/**
	 * Retrieves SUB stage id from stage_link given correspoding ADO stage id.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA7D
	 * 
	 * @param idStage
	 * @return @
	 */
	public Long getIdSUBStageByIdADOStage(Long idStage);

	void updateStagePersonLink(AllegationDetailDto allegationDetail);

	StagePersonLink getStagePersonLink(Long idStage, Long idPerson);

	// CMSC23D
	List<StagePersDto> getStageListByIdPerson(Long idPerson);

	/**
	 * 
	 * Method Name: getPersonLegalStatus(CCMNH9D) Method Description:Retrieves
	 * all the person with Legal Statuses in a given Case.
	 * 
	 * @param idCase
	 * @param cdEventType
	 * @return
	 */
	List<StagePersDto> getPersonLegalStatus(Long idCase, String cdEventType);

	/**
	 * Method Name: isChildPrimary Method Description: This method is used to
	 * check if the child is primary.
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	public boolean isChildPrimary(Long idPerson, Long idStage, Long idCase);

	/**
	 * Method name:getChildPrimaryInfo Method Description: This method returns
	 * the Priamry Child Information for the given Person ID
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return
	 */
	public StagePersonLinkDto getChildPrimaryInfo(Long idPerson, Long idCase);

	/**
	 * Method Name: getStagePersonLinkDtl Method Description:CINV80D - This DAM
	 * retrieves the Person Id for the given idStage and cdStagePersType
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<StagePersonLink>
	 */
	public List<StagePersonLink> getStagePersonLinkDtl(Long idStage, String cdStagePersType);

	public List<StagePersonLink> getStagePersonLinkNonHpRole(Long idStage);

	/**
	 * artf251080 : Kinship Case will NOT Approve
	 * get primary caregiver for the given stage id
	 * @param stageId
	 * @return
	 */
	public Long getPrimaryCareGiverbyStage(Long stageId);
	/**
	 * Method Name: StgPersonLinkDtlsBasedOnCase -This Method
	 * retrieves the Person Id for the given case and cdStagePersType
	 *
	 * @param idCase
	 * @param cdStagePersType
	 * @return List<StagePersonLinkDto>
	 */
	public List<StagePersonLinkDto> getStgPersonLinkDtlsBasedOnCase(Long idCase, String cdStagePersType);

}