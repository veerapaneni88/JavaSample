package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.MedCnsntrFormLog;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN19S Class
 * Description: This is used to perform the add, update & delete operations in
 * Medical Concenter table. Apr 3 , 2017 - 3:50:30 PM
 */

public interface MedicalConsenterDao {

	/**
	 * Updates end date value in Medical Consenter record given Case id and
	 * Primary Child id.
	 * 
	 * Service Name : CCMN02U, DAM Name : CAUDK8D
	 * 
	 * @param dateEnd
	 * @param idCase
	 * @param idPerson
	 * @
	 */
	public void updateMedicalConcenterByAttributes(Date dateEnd, Long idCase, Long idPerson);

	/**
	 * Method Name: fetchActiveMedConsRecForPerson Method Description: Fetches
	 * the list of records where a person is active medical consenter
	 * 
	 * @param idMedConPerson
	 * @return List<MedicalConsenterDto>
	 * @throws DataNotFoundException
	 */
	public List<MedicalConsenterDto> fetchActiveMedConsRecForPerson(Long idMedConPerson);

	/**
	 * Method Name: selectPersonIdFromDao Method Description:Select personId
	 * from case and stage ids in database.
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long selectPersonIdFromDao(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: updateMedicalConsenterDetail Method Description:update the
	 * new Medical Consenter to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 * @throws DataNotFoundException
	 */
	public MedicalConsenterDto updateMedicalConsenterDetail(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: addMedicalConsenterDetail Method Description:Saves the new
	 * Medical Consenter to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 * @throws DataNotFoundException
	 */
	public MedicalConsenterDto addMedicalConsenterDetail(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: getStageType Method Description:Method retrieves the type of
	 * input stage.
	 * 
	 * @param ulIdStage
	 * @return String
	 * @throws DataNotFoundException
	 */
	public String getStageType(Long ulIdStage);

	/**
	 * Method Name: getPrimaryWorker Method Description:Fetch Primary Worker for
	 * given stage only when stage is active.
	 * 
	 * @param stageId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getPrimaryWorker(Long stageId);

	/**
	 * Method Name: queryMedicalConsenterList Method Description:Select list of
	 * Medical Consenter record from case and stage ids in database.
	 * 
	 * @param caseId
	 * @param stageId
	 * @return List<MedicalConsenterDto>
	 * @throws DataNotFoundException
	 */
	public List<MedicalConsenterDto> queryMedicalConsenterList(Long caseId, Long stageId);

	/**
	 * Method Name: queryMedicalConsenterRecord Method Description:Select a
	 * medical consenter detail for the given primary id in database.
	 * 
	 * @param idMedCons
	 * @return MedicalConsenterDto
	 * @throws DataNotFoundException
	 */
	public MedicalConsenterDto queryMedicalConsenterRecord(Long idMedCons);

	/**
	 * Method Name: updateEndDateRecordType Method Description:End Date Type of
	 * another record before saving new Medical Consenter
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateEndDateRecordType(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: updateEndDate Method
	 * Description:updateMedicalConsenterEndDate Method Description:Update the
	 * Medical Consenter end date to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateEndDate(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: isDFPSStaff Method Description:query if a given person id,
	 * there is a record in the employee table, if found record, return true
	 * else false
	 * 
	 * @param staffId
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isDFPSStaff(Long staffId);

	/**
	 * Method Name: isPersonMedicalConsenter Method Description:Check if the
	 * person is in Medical Consenter from Person Detail page
	 * 
	 * @param szIdPerson
	 * @param szIdStage
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isPersonMedicalConsenter(String szIdPerson, String szIdStage);

	/**
	 * Method Name: getMedicalConsenterIdForEvent Method Description:Get the
	 * medical consenter id based on the medical consenter creation event id.
	 * 
	 * @param ulIdEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getMedicalConsenterIdForEvent(Long ulIdEvent);

	/**
	 * Method Name: isPersonMedicalConsenterType Method Description:check if the
	 * person is already Medical Consenter type.
	 * 
	 * @param ulIdPerson
	 * @param ulIdCase
	 * @param ulIdChild
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long isPersonMedicalConsenterType(Long ulIdPerson, Long ulIdCase, Long ulIdChild);

	/**
	 * Method Name: updateEndDateConsenterRecord Method Description: update end
	 * Date Medical Consenter record specific to stage
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateEndDateConsenterRecord(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: getCorrespStage Method Description:To retrieve a
	 * corresponding Stage for a given SUB or ADO stages.
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getCorrespStage(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: hasPersonAddrZip Method Description:Checks if the person
	 * being added as a Medical Censenter have atleast one Zip code in any of
	 * its addresses.
	 * 
	 * @param ulIdPerson
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean hasPersonAddrZip(Long ulIdPerson);

	/**
	 * Method Name: indToDoExists Method Description: Get boolean indicating if
	 * Primary Child for Case has both Primary and Backup Medical Consenters.
	 * 
	 * @param ulIdStage
	 * @param ulIdCase
	 * @param cd_todo_type
	 * @param txt_Todo_Desc
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean indToDoExists(Long ulIdStage, Long ulIdCase, String cd_todo_type, String txt_Todo_Desc);

	/**
	 * Method Name:checkMCPBExist Method Description:Method returns true if
	 * Primary child for the given stage has atleast one Primary and one Backup
	 * MC's that are court authorized.
	 * 
	 * @param ulIdStage
	 * @param ulIdChild
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean checkMCPBExist(Long ulIdStage, Long ulIdChild);

	/**
	 * Method Name: checkMCCourtAuth Method Description:Method returns true if
	 * Primary child for the given stage has atleast one Primary and one Backup
	 * MC's that are court authorized.
	 * 
	 * @param ulIdStage
	 * @param ulIdRelatedStage
	 * @param ulIdChild
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean checkMCCourtAuth(Long ulIdStage, Long ulIdRelatedStage, Long ulIdChild);

	/**
	 * Method Name: isActiveMedCons Method Description:isActiveMedCons returns
	 * count of number of active medical consenters for the person id.
	 * 
	 * @param idPerson
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isActiveMedCons(Long idPerson);

	/**
	 * Method Name: getPersonNameSfx Method Description:Method to get the Person
	 * Name Suffix
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 * @throws DataNotFoundException
	 */
	public MedicalConsenterDto getPersonNameSfx(MedicalConsenterDto medicalConsenterDto);

	/**
	 * 
	 * Method Name: audMdclConsenterFormLog Method Description: This method is
	 * to save Medical Consenter forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form launched using launch button in
	 * detail page, If Save and Complete button is clicked in Detail page update
	 * the status as 'COMP', If Delete button clicked delete the record.
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes audMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq);

	/**
	 * 
	 * Method Name: getMdclConsenterFormLogList Method Description: This method
	 * is to get the list to display forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) associated with the Medical Consenters in the
	 * Medical Consenter List page
	 *
	 * @param medicalConsenterFormLogReq
	 * @return MedicalConsenterRes
	 */
	public MedicalConsenterRes getMdclConsenterFormLogList(MedicalConsenterFormLogReq medicalConsenterFormLogReq);

	/**
	 * 
	 * Method Name: updateMdclConsenterFormLog Method Description: This method
	 * is to save Medical Consenter forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form is launched and Saved
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes updateMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq);

	/**
	 * 
	 * Method Name: getMedCnsntrFormLog Method Description: This method is to
	 * get MedCnsntrFormLog
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	public MedCnsntrFormLog getMedCnsntrFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq);
}
