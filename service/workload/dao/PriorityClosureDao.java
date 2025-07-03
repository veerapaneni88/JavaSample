/**
 * 
 */
package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.PriorityClosure;
import us.tx.state.dfps.common.domain.VctmNotifctnRsrcChild;
import us.tx.state.dfps.common.domain.VictimNotification;
import us.tx.state.dfps.common.domain.VictimNotificationRsrc;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.workload.dto.IntakeNotfChildDto;
import us.tx.state.dfps.service.workload.dto.IntakeNotfFacilityDto;
import us.tx.state.dfps.service.workload.dto.PriorityClosureLicensingDto;


/**
 *Class Description:This interface contains the methods for priority closure notification section added as part of FCL project
 *Oct 15, 2019- 3:08:14 PM
 *© 2019 Texas Department of Family and Protective Services 
 ***********  Change History *********************************
 *Oct 15, 2019  mullar2	 artf128805 : FCL changes 
*/
public interface PriorityClosureDao {
	
	
	
	
	/**
	 *Method Name:	getDefaultVictimNotificationDetails
	 *Method Description:
	 *@param intakeStageId
	 *@return
	 */
	List<IntakeNotfChildDto> getDefaultVictimNotificationDetails(Long intakeStageId);
	

	ResourceDto getDefaultNotificationFacilityDetails(Long oldestVictimId);
	
	
	List<IntakeNotfChildDto>  getDefaultFacilityChildrenDetails(Long facilityId);
	
	
	List<IntakeNotfChildDto> getNotifiedVictimNotificationDetails(Long intakeStageId);
	
	IntakeNotfFacilityDto getNotifiedFacilityDetails(Long intakeStageId);
	
	List<IntakeNotfChildDto> getNotifiedFacilityChildrenDetails(Long victimNotificationRsrcId);
	
	ResourceDto getSavedFacilityDetails(Long facilityId) ;
	
	IntakeNotfChildDto getLatestVictimDetails(Long idPersonRevised,Long idSubstageRevised);
	
	List<IntakeNotfChildDto> fetchRevisedVictimDetails(Long idRevisedPerson); 
	
	Date getIntakeDate(Long intakeStageId);
	
	void saveVictimNotificationDetails(VictimNotification victimNotification);
	
	void saveFacilityDetails(VictimNotificationRsrc victimNotificationRsrc);
	
	void deleteChildrenDetails(Long victimNotificationRsrcId);
	
	void saveChildrenDetails(List<VctmNotifctnRsrcChild> vctmNotifctnRsrcChildList);
	
	void updateRciIndicator(Long idStage);
	
	Object loadEntity(Long primaryId,Class<?> clazz);
	
	void deleteRsrcChildren(List<VctmNotifctnRsrcChild> savedVctmNotifctnRsrcChildsList) ;

	/**
	 * Checks if there is a record exists for the idPerson,idStage,idSubStage and idWorker combination.
	 * isSupervisor is used to switch between supervisor and worker
	 * @param idPerson
	 * @param idStage
	 * @param idSubStage
	 * @param idWorker
	 * @param isSupervisor
	 * @return
	 */
	public boolean checkIfAlertAlreadyExists(Long idPerson,Long idStage,Long idSubStage,Long idWorker,boolean isSupervisor);

	/**
	 * Method Name:	getIntakeLicensingDetails
	 * Method Description:Fetch licensing details of intake stage
	 * @param intakeStageId
	 * @return priorityClosure
	 */
	public PriorityClosureLicensingDto getIntakeLicensingDetails(Long intakeStageId) ;

	/**
	 * Method Name:	getPriorityClosureLicensingDetails
	 * Method Description: Fetch licensing details of intake stage from Priority_Closure table
	 * @param idStage
	 * @return priorityClosure
	 */
	public PriorityClosure getPriorityClosureLicensingDetails(Long idStage) ;

	/**
	 * Method Name:	savePriorityClosureLicensingDetails
	 * Method Description: Save licensing details to Priority_Closure table
	 * @param priorityClosure
	 */
	public void savePriorityClosureLicensingDetails(PriorityClosure priorityClosure) ;

	}
