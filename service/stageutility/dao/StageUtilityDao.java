package us.tx.state.dfps.service.stageutility.dao;

import java.util.ArrayList;
import java.util.List;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:42 PM ©
 * 2017 Texas Department of Family and Protective Services
 * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
public interface StageUtilityDao {

	/**
	 * 
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idStage
	 * @return StageValueBeanDto
	 * @throws DataNotFoundException
	 */
	public StageValueBeanDto retrieveStageInfo(Long idStage);

	/**
	 * Method Name: findPrimaryChildForStage Method Description: This method
	 * returns Primary Child for the Stage.
	 * 
	 * @param idStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long findPrimaryChildForStage(Long idStage);

	/**
	 * Method Name: findWorkersForStage Method Description:This method returns
	 * the primary and secondary workers assigned to the stage with the given
	 * security profiles. If the security profile list parameter is empty or
	 * null, it returns all the primary and secondary workers assigned to the
	 * stage.
	 * 
	 * @param idStage
	 * @param secProfiles
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> findWorkersForStage(Long idStage, List<String> secProfiles);

	/**
	 * Method Name: getDeathReasonMissing Method Description: This method gets
	 * count of persons on stage with DOD but no death code, for use in
	 * validation of INV stage closures.
	 * 
	 * @param idStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getDeathReasonMissing(Long idStage);

	/**
	 * Method Name: getCheckedOutStagesForPerson Method Description: This method
	 * Fetches the list of stages which has this person and are checkout to MPS
	 * 
	 * @param idPerson
	 * @return ArrayList<StagePersonValueDto
	 * @throws DataNotFoundException
	 */
	public ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson);

	/**
	 * Method Name: getStageListForPC Method Description: This method is to
	 * check if a person in Primary Child in open stage
	 * 
	 * @param idPerson
	 * @param b
	 * @return ArrayList<StageValueBeanDto>
	 * @throws DataNotFoundException
	 */
	public ArrayList<StageValueBeanDto> getStageListForPC(Long idPerson, Boolean b);

	/**
	 * 
	 * Method Name: getStageListForChild Method Description: get stage list for
	 * child
	 * 
	 * @param idPerson
	 * @return
	 */
	public ArrayList<StageValueBeanDto> getStageListForChild(Long idPerson);
	
	/**
	 * Method Name: updateStage Method Description: Method to update 
	 * IND_VICTIM_NOTIFICATION_STATUS in the stage table 
	 * artf129782: Licensing Investigation Conclusion
	 *  @param Stage
	 */
	public Long updateVictimNotificationStatus( Long idStage);
}
