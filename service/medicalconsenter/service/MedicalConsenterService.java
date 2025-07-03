package us.tx.state.dfps.service.medicalconsenter.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalConsenterService Oct 31, 2017- 4:06:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface MedicalConsenterService {

	/**
	 * Method Name: saveMedicalConsenterDetail Method Description:Saves the
	 * Medical Consenter data to the database
	 * 
	 * @param medConsBean
	 * @param dto
	 * @return MedicalConsenterDto
	 */
	public MedicalConsenterDto saveMedicalConsenterDetail(MedicalConsenterDto medicalConsenterDto, EventInputDto dto);

	/**
	 * Method Name: selectPersonId Method Description:Select personId from case
	 * and stage ids in database
	 * 
	 * @param medicalConsenterDto
	 * @return Long @
	 */
	public MedicalConsenterDto selectPersonId(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: queryMedicalConsenterList Method Description:Select list of
	 * medical consenter detail from case and stage ids in database.
	 * 
	 * @param medicalConsenterDto
	 * @return List<MedicalConsenterDto> @
	 */
	public List<MedicalConsenterDto> queryMedicalConsenterList(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: queryMedicalConsenterRecord Method Description:Select a
	 * medical consenter detail for the given primary id in database.
	 * 
	 * @param idMedCons
	 * @return MedicalConsenterDto
	 */
	public MedicalConsenterDto queryMedicalConsenterRecord(Long idMedCons);

	/**
	 * Method Name: endDateRecordType Method Description:End Date Type of
	 * another record before saving new Medical Consenter
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 */
	public MedicalConsenterDto endDateRecordType(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: updateMedicalConsenterEndDate Method Description:Update the
	 * Medical Consenter end date to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 */
	public MedicalConsenterDto updateMedicalConsenterEndDate(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: checkDfpsStaff Method Description: Check if a Medical
	 * Consenter is a DFPS Staff
	 * 
	 * @param staffId
	 * @return Boolean
	 * 
	 */
	public Boolean checkDfpsStaff(Long staffId);

	/**
	 * Method Name: checkMedicalConsenterStatus Method Description:check Medical
	 * Consenter Status from database.
	 * 
	 * @param medicalConsenterDto
	 * @return Boolean
	 */
	public Boolean checkMedicalConsenterStatus(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: isPersonMedicalConsenter Method Description:check if the
	 * person is in Medical Consenter table.
	 * 
	 * @param szIdPerson
	 * @param szIdStage
	 * @return Boolean
	 */
	public Boolean isPersonMedicalConsenter(String szIdPerson, String szIdStage);

	/**
	 * Method Name: getMedicalConsenterIdForEvent Method Description:Get the
	 * medical consenter id based on the medical consenter creation event id.
	 * 
	 * @param ulIdEvent
	 * @return Long
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
	 */
	public Long isPersonMedicalConsenterType(Long ulIdPerson, Long ulIdCase, Long ulIdChild);

	/**
	 * Method Name: updateMedicalConsenterRecord Method Description:Update the
	 * Medical Consenter Record.
	 * 
	 * @param medicalConsenterDto
	 * @return Long
	 */
	public Long updateMedicalConsenterRecord(MedicalConsenterDto medicalConsenterDto);

	/**
	 * Method Name: getPrimaryChild Method Description:Get Primary Child given
	 * stage id and Case id
	 * 
	 * @param ulIdCase
	 * @param ulIdStage
	 * @return Long
	 */
	public Long getPrimaryChild(Long ulIdCase, Long ulIdStage);

	/**
	 * Method Name: getStageType Method Description:Method retrieves the type of
	 * input stage.
	 * 
	 * @param ulIdStage
	 * @return String
	 */
	public String getStageType(Long ulIdStage);

	/**
	 * Method Name: getCorrespStage Method Description:If Input stage is SUB/ADO
	 * the method retrieves corresponding ADO/SUB stage id from stage_link
	 * table.
	 * 
	 * @param ulIdStage
	 * @return Long
	 */
	public Long getCorrespStage(Long ulIdStage);

	/**
	 * Method Name: personAddrExists Method Description:This implements two
	 * logic 1. Checks if the person in the Medical Censenter have atleast one
	 * Zip code in the associated addresses and 2.if the peron have atleast one
	 * assoicated address.
	 * 
	 * @param ulIdPerson
	 * @return Boolean
	 */
	public Boolean personAddrExists(Long ulIdPerson);

	/**
	 * Method Name: checkAlertTodoExists Method Description:Method to check if
	 * Medical Consenter Alert Exists for stage
	 * 
	 * @param ulIdStage
	 * @param ulIdCase
	 * @param cdTodoType
	 * @param txtTodoDesc
	 * @return Boolean
	 */
	public Boolean checkAlertTodoExists(Long ulIdStage, Long ulIdCase, String cdTodoType, String txtTodoDesc);

	/**
	 * Method Name: getPrimaryWorker Method Description:Fetch Primary Worker for
	 * given stage only when stage is active.
	 * 
	 * @param ulIdStage
	 * @return
	 */
	public Long getPrimaryWorker(Long ulIdStage);

	/**
	 * Method Name: checkPrimBackExists Method Description:Method returns true
	 * if Primary child for the given stage has atleast one Primary and one
	 * Backup MC's that are court authorized.
	 * 
	 * @param ulIdStage
	 * @param ulIdChild
	 * @return
	 */
	public Boolean checkPrimBackExists(Long ulIdStage, Long ulIdChild);

	/**
	 * Method Name: checkMCCourtAuth Method Description:Method returns true if
	 * the Primary Child has atleast one Medical Consenter either in
	 * SUBCARE/ADOPTION stage or in related ADOPTION/SUBCARE stage that is
	 * marked as court authorized(IND_CRT_AUTH is 'Y')
	 * 
	 * @param ulIdStage
	 * @param ulIdChild
	 * @return
	 */
	public Boolean checkMCCourtAuth(Long ulIdStage, Long ulIdChild);

	/**
	 * Method Name: isActiveMedCons Method Description:isActiveMedCons gets
	 * count of active medical consenters for the person.
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isActiveMedCons(Long idPerson);

	/**
	 * Method Name: updatePersonNameSfx Method Description:Method to get the
	 * Person Name Suffix
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto
	 */
	public MedicalConsenterDto updatePersonNameSfx(MedicalConsenterDto medicalConsenterDto);

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
	 * is to update Medical Consenter forms(Designation of Medical Consenter
	 * Form 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form is launched and saved
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes updateMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq);
}
