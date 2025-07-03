package us.tx.state.dfps.service.investigation.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.AllegedSxVctmztn;
import us.tx.state.dfps.common.domain.CvsNotifLog;
import us.tx.state.dfps.common.domain.LicensingInvstDtl;
import us.tx.state.dfps.service.common.request.NameChangeReq;
import us.tx.state.dfps.service.investigation.dto.ClassFacilityDto;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:48 PM
 * *********** Change History ****************************************
 * 06/09/2020 kanakas artf152402 : Prior investigation overwritten by later 
 */
public interface LicensingInvstDtlDao {
	/**
	 * 
	 * Method Description:legacy service name - CINV74D
	 * 
	 * @param uIdStage
	 * @return @
	 */
	List<LicensingInvstDtl> getLicensingInvstDtlDaobyParentId(Long uIdStage);

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 * 
	 * @param licensingInvstDtl
	 * @
	 */
	void saveLicensingInvstDtl(LicensingInvstDtl licensingInvstDtl);

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 * 
	 * @param licensingInvstDtl
	 * @
	 */
	void licensingInvstDtlDelete(LicensingInvstDtl licensingInvstDtl);

	/**
	 * This service is used to check whether questions in allegation were
	 * answered as Y
	 * 
	 * @param idStage
	 * @return
	 */
	boolean fetchAllegQuestionYAnswers(Long idStage);

	/**
	 * Method Name: getOverallDispositionExists
	 * Method Description: This method is used to query the database and see if a stage has an overall disposition
	 * present.
	 * artf128755 - CCI reporter letter
	 *
	 * @param idStage stage to be searched
	 * @return true or false depending on presence of overall disposition.
	 */
	boolean getOverallDispositionExists(Long idStage);

	/**
	 * This DAM will update the Invest_conclusion_restraint table.
	 * 
	 * @param invstRestraintDto
	 */
	void invstConclusionRestraintUpdate(InvstRestraintDto invstRestraintDto);

	/**
	 * This method is used to get information from CLASS_FACILITY_VIEW@class
	 * table
	 * 
	 * @param nbrRsrcFacilAcclaim
	 * @param nbrAgency
	 * @param nbrBranch
	 * @param indAgencyHome
	 * @return
	 */
	List<ClassFacilityDto> getClassFacilityView(Integer nbrRsrcFacilAcclaim, Integer nbrAgency, Integer nbrBranch,
			String indAgencyHome);

	/**
	 * retrieve the resource information based on facility number
	 * 
	 * @param nbrRsrcFacilAcclaim
	 * @return
	 */
	ResourceValueBeanDto getResourceByFacilityNbr(Long nbrRsrcFacilAcclaim);

	/**
	 * This service is used to check whether questions in allegation were
	 * answered
	 * 
	 * @param idStage
	 * @return
	 */
	boolean fetchAllegQuestionAnswers(Long idStage);

	/**
	 * This Method is used to get the information related to approval date and
	 * approval reason.
	 * 
	 * @param idStage
	 * @param idFacil
	 */
	LicensingInvstDtlDto getApprovalInfo(Long idStage, Integer idFacil);

	/**
	 *Method Name:	saveLicensingInvDtlBasedOnDtComplt
	 *Method Description:this method updates the licensing investigation completed date
	 *@param idEvent
	 *@param dtComplted
	 */
	void saveLicensingInvDtlBasedOnDtComplt(Long idEvent, Date dtComplted);
	
	
	/**
	 *Method Name:	saveAllegedBehaviorDetails
	 *Method Description:this method updates the licensing investigation completed date
	 *@param AllegedSxVctmztnDto
	 */
	void saveAllegedBehaviorDetails(AllegedSxVctmztn allegedSxVctmztn, AllegedSxVctmztnDto savedAllegedSxVctmztnDto);

	/**
	 * Method Name:	updateCaseName
	 * Method Description:this method updates the case name in CAPS_CASE table
	 * @param nameChangeReq
	 * @param county
	 */
	void updateCaseNameAndCounty(NameChangeReq nameChangeReq, String county);

	/**
	 * Method Name:	updateStageName
	 * Method Description:this method updates the stage name in STAGE table
	 * @param nameChangeReq
	 * @param county
	 */
	void updateStageNameAndCounty(NameChangeReq nameChangeReq, String county);

	/**
	 * Method Name:	updateSafetyPlanFacilityId
	 * Method Description:this method updates the ID_FCLTY_SEL column to null
	 * if the CD_SAFETY_PLAN_STATUS is "In process"(INP) in SAFETY_PLAN table
	 * @param idStage
	 * @param status
	 */
	public void updateSafetyPlanFacilityId(Long idStage, String status);

	/**
	 * Method Name:	saveCvsNotificationLog
	 * Method Description: Save data to CvsNotifLog
	 * @param cvsNotifLog
	 */
	public Long saveCvsNotificationLog(CvsNotifLog cvsNotifLog);

	/**
	 * This method is used to get list of CvsNotifLog
	 * @param idCase
	 * @param idVictimPerson
	 * @param cdEmailType
	 */

	public List<CvsNotifLog> getCVSNotificationLog(Long idCase, Long idVictimPerson, String cdEmailType);

	/**
	 * Method Name: getCVSNotificationAlert
	 * Method Description: This method is used to get CVS Notification alert for a person
	 *
	 * @param idCase
	 * @param idStage
	 * @param idPerson
	 */
	public List<TodoDto> getCVSNotificationAlert(Long idCase, Long idStage, Long idPerson);

	/**
	 * Method Name:	deleteCvsNotificationLog
	 * Method Description: Delete CvsNotifLog
	 * @param cvsNotifLog
	 */
	public void deleteCvsNotificationLog(CvsNotifLog cvsNotifLog) ;


	}
