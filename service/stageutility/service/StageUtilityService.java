package us.tx.state.dfps.service.stageutility.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:07 PM ©
 * 2017 Texas Department of Family and Protective Services
 *  * * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
public interface StageUtilityService {

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idStage
	 * @return StageValueBeanDto 
	 */
	public StageValueBeanDto retrieveStageInfo(Long idStage);
	
	/**
	 * Method Name: updateStageInfo Method Description: This method updates
	 * information from Stage table using idStage.
	 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion 
	 * @param idStage
	 * 
	 */
	public void updateStageInfo(Long idStage);

	/**
	 * Method Name: findPrimaryChildForStage Method Description: This method
	 * returns Primary Child for the Stage.
	 * 
	 * @param idStage
	 * @return Long @
	 */
	public Long findPrimaryChildForStage(Long idStage);

	/**
	 * Method Name: findWorkersForStage Method Description: This method returns
	 * the primary and secondary workers assigned to the stage with the given
	 * security profiles. If the security profile list parameter is empty or
	 * null, it returns all the primary and secondary workers assigned to the
	 * stage.
	 * 
	 * @param idStage
	 * @param secProfiles
	 * @return List<Long> @
	 */
	public List<Long> findWorkersForStage(Long idStage, List<String> secProfiles);

	/**
	 * Method Name: fetchReasonForDeathMissing Method Description: This method
	 * gets count of persons on stage with DOD but no death code, for use in
	 * validation of INV stage closures.
	 * 
	 * @param idStage
	 * @return Long @
	 */
	public Long fetchReasonForDeathMissing(Long idStage);

	/**
	 * Method Name: getCheckedOutStagesForPerson Method Description: This method
	 * Fetches the list of stages which has this person and are checkout to MPS
	 * 
	 * @param idPerson
	 * @return ArrayList<StagePersonValueDto> @
	 */
	public ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson);

	/**
	 * Method Name: isPrimaryChildInOpenStage Method Description: This method is
	 * to check if a person in Primary Child in open stage
	 * 
	 * @param idPerson
	 * @return boolean @
	 */
	public boolean isPrimaryChildInOpenStage(Long idPerson);

	/**
	 * 
	 * Method Name: isChildInSubStage Method Description:This method is to check
	 * if a person in SUB open stage
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean isChildInSubStage(Long idPerson);

	/**
	 * This method will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
	 * If the stage start date is prior to the APS Release Date, it returns true, else false.
	 * @param stageStartDate
	 * @return
	 */
	public Boolean checkPreSingleStageByStartDate(Date stageStartDate);

	boolean checkPreSingleStageByCaseId(Long idCase);
}
