package us.tx.state.dfps.service.sscc.dao;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtHeaderDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtMedCnsntrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtNarrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for SSCC Placement Option Circumstances Data Access Aug 10, 2018- 6:27:58 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface SSCCPlcmtOptCircumDao {

	/**
	 * Method Name: readSSCCPlcmtHeader Method Description: Method signature for
	 * read of SSCCPlcmtOptCirum
	 * 
	 * @param idSSCCPlcmtHeader
	 * @return SSCCPlcmtHeaderDto
	 */
	SsccPlcmtHeader readSSCCPlcmtHeader(Long idSSCCPlcmtHeader);

	/**
	 * Method Name: getDfpsStaff Method Description: Method Signature for
	 * getting the DFPS Staff for Stage.
	 * 
	 * @param idStage
	 * @return Set<Long>
	 */
	Set<Long> getDfpsStaff(Long idStage);

	/**
	 * Method Name: getCpaOthrMedCnsntrs Method Description: Method signature
	 * for getting CPA Med Consenters.
	 * 
	 * @param idRsrcSSCC
	 * @param idRsrcFacil
	 * @return Set<Long>
	 */
	Set<Long> getCpaOthrMedCnsntrs(Long idRsrcSSCC, Long idRsrcFacil);

	/**
	 * Method Name: MapfindMedCnsntrPersByNmDobSsn Method Description: Method
	 * signature for getting person on name, dob and ssn.
	 * 
	 * @param ssccPlcmtMedCnsntr
	 * @return List<Long>
	 */
	List<Long> findMedCnsntrPersByNmDobSsn(SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntr);

	/**
	 * Method Name: getSSNByPerson Method Description: Method signature for the
	 * find person ssn.
	 * 
	 * @param idPerson
	 * @return String
	 */
	String getSSNByPerson(Long idPerson);

	/**
	 * Method Name: saveSSCCPlcmtHeader Method Description: This method saves or
	 * updates the SSCC Placement Header
	 * 
	 * @param ssccPlcmtHeader
	 */
	void saveSSCCPlcmtHeader(SsccPlcmtHeader ssccPlcmtHeader);

	/**
	 * Method Name: getCorrospondingPlcmtDt Method Description:
	 * 
	 * @param idStage
	 * @param idRsrcSSCC
	 * @param idRsrcFacil
	 * @return
	 */
	Date getCorrospondingPlcmtDt(Long idStage, Long idRsrcSSCC, Long idRsrcFacil);

	/**
	 * Method Name: getMcCourtAuthCnt Method Description:
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	Long getMcCourtAuthCnt(Long idSSCCReferral);

	/**
	 * Method Name: saveSSCCPlcmtName Method Description: This method inserts a
	 * record into SSCCPlcmtName
	 * 
	 * @param ssccPlcmtName
	 * @return
	 */
	void saveSSCCPlcmtName(SsccPlcmtName ssccPlcmtName);

	/**
	 * Method Name: saveSSCCPlcmtMedCnsntr Method Description: This method
	 * inserts a record into SSCCPlcmtMedCnsntr
	 * 
	 * @param ssccPlcmtMedCnsntr
	 */
	void saveSSCCPlcmtMedCnsntr(SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr);

	/**
	 * Method Name: saveSSCCPlcmtInfo Method Description: This method inserts a
	 * record into SSCCPlcmtInfo
	 * 
	 * @param ssccPlcmtInfo
	 */
	void saveSSCCPlcmtInfo(SsccPlcmtInfo ssccPlcmtInfo);

	/**
	 * Method Name: saveSsccPlcmtPlaced Method Description:This method inserts a
	 * record into SsccPlcmtPlaced
	 * 
	 * @param ssccPlcmtPlaced
	 * @return
	 */
	void saveSSCCPlcmtPlaced(SsccPlcmtPlaced ssccPlcmtPlaced);

	/**
	 * Method Name: saveSsccPlcmtCircumstance Method Description: This method
	 * inserts a record into SsccPlcmtCircumstance
	 * 
	 * @param ssccPlcmtCircumstance
	 */
	void saveSsccPlcmtCircumstance(SsccPlcmtCircumstance ssccPlcmtCircumstance);

	/**
	 * Method Name: getSSCCPlcmtPlaced Method Description: This method retrieves
	 * SSCCPlcmtPlaced from DB by its id
	 * 
	 * @param idSSCCPlcmtPlaced
	 * @return
	 */
	SsccPlcmtPlaced getSSCCPlcmtPlaced(Long idSSCCPlcmtPlaced);

	/**
	 * Method Name: getSSCCPlcmtName Method Description: This method retrieves
	 * SSCCPlcmtName from DB by its id
	 * 
	 * @param idSSCCPlcmtName
	 * @return
	 */
	SsccPlcmtName getSSCCPlcmtName(Long idSSCCPlcmtName);

	/**
	 * Method Name: getSSCCPlcmtMedCnsntr Method Description: This method
	 * retrieves SSCCPlcmtMedCnsntr from DB by its id
	 * 
	 * @param idSSCCPlcmtMedCnsntr
	 * @return
	 */
	SsccPlcmtMedCnsntr getSSCCPlcmtMedCnsntr(Long idSSCCPlcmtMedCnsntr);

	/**
	 * Method Name: getSSCCPlcmtInfo Method Description: This method retrieves
	 * SSCCPlcmtInfo from DB by its id
	 * 
	 * @param idSSCCPlcmtInfo
	 * @return
	 */
	SsccPlcmtInfo getSSCCPlcmtInfo(Long idSSCCPlcmtInfo);

	/**
	 * Method Name: getSSCCPlcmtCircumstance Method Description: This method
	 * retrieves SSCCPlcmtCircumstance from DB by its id
	 * 
	 * @param idSSCCPlcmtCircumstance
	 * @return
	 */
	SsccPlcmtCircumstance getSSCCPlcmtCircumstance(Long idSSCCPlcmtCircumstance);

	/**
	 * Method Name: getSavedSSCCMedCnsntr Method Description: Method signature
	 * for retrieving the Med Concenter.
	 * 
	 * @param idSSCCPlcmtHeader
	 * @param medCnsntrType
	 * @return
	 */
	SsccPlcmtMedCnsntr getSavedSSCCMedCnsntr(Long idSSCCPlcmtHeader, String medCnsntrType);

	/**
	 * Method Name: getAgencyHmMedCnsntr Method Description: Method Signature
	 * for getAgencyHmMedCnsntr
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	HashMap<String, Long> getAgencyHmMedCnsntr(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: excpCareBudgetDaysExceeded Method Description: Method
	 * Signature for excpCareBudgetDaysExceeded
	 * 
	 * @param ssccPlcmtHeaderDto
	 * @param useDtPlcmtStart
	 * @return
	 */
	Boolean excpCareBudgetDaysExceeded(SSCCPlcmtHeaderDto ssccPlcmtHeaderDto, Boolean useDtPlcmtStart);

	/**
	 * Method Name: checkHeaderStatusChanged Method Description: Method
	 * Signature for checkHeaderStatusChanged Method.
	 * 
	 * @param ssccPlcmtHdrDto
	 * @return
	 */
	Boolean checkHeaderStatusChanged(SSCCPlcmtHeaderDto ssccPlcmtHdrDto);

	/**
	 * Method Name: deletePlcmtHeader Method Description:this method deletes
	 * placement header
	 * 
	 * @param idSSCCPlcmtHeader
	 */
	void deletePlcmtHeader(Long idSSCCPlcmtHeader);

	/**
	 * Method Name: deletePlcmtMedCnsntrs Method Description:This method deletes
	 * placement medical consenters
	 * 
	 * @param idSSCCPlcmtMedCnsntrs
	 */
	void deletePlcmtMedCnsntrs(List<Long> idSSCCPlcmtMedCnsntrs);

	/**
	 * Method Name: deletePlcmtCircumstance Method Description:This method
	 * deletes the placement circumstance
	 * 
	 * @param idSSCCPlcmtCircumstance
	 */
	void deletePlcmtCircumstance(Long idSSCCPlcmtCircumstance);

	/**
	 * Method Name: deletePlcmtName Method Description: This method deletes the
	 * placement name.
	 * 
	 * @param idSSCCPlcmtName
	 */
	void deletePlcmtName(Long idSSCCPlcmtName);

	/**
	 * Method Name: deletePlcmtInfo Method Description: This method deletes
	 * placement info
	 * 
	 * @param idSSCCPlcmtInfo
	 */
	void deletePlcmtInfo(Long idSSCCPlcmtInfo);

	/**
	 * Method Name: deletePlcmtPlaced Method Description: This method deletes
	 * placement placed.
	 * 
	 * @param idSsccPlcmtPlaced
	 */
	void deletePlcmtPlaced(Long idSsccPlcmtPlaced);

	/**
	 * Method Name: deletePlcmtNarr Method Description: This method deletes
	 * placement narrative.
	 * 
	 * @param idSSCCPlcmtNarr
	 */
	void deletePlcmtNarr(Long idSSCCPlcmtNarr);

	/**
	 * Method Name: checkForRevChange Method Description: Method Signature for
	 * checkForRevChange Method.
	 * 
	 * @param ssccMedCnsntrDtoMap
	 * @return
	 */
	Boolean checkForRevChange(HashMap<String, SSCCPlcmtMedCnsntrDto> ssccMedCnsntrDtoMap);

	/**
	 * Method Name: getMaxDtPlcmtEnd Method Description: Method Signature for
	 * GetMaxDtPlcmt for Child.
	 * 
	 * @param idActiveRef
	 * @return
	 */
	Date getMaxDtPlcmtEnd(Long idActiveRef);

	/**
	 * Method Name: getActivePlcmtCnt Method Description: Method Signature for
	 * getActivePlcmtCnt
	 * 
	 * @param idActiveRef
	 * @return
	 */
	Long getActivePlcmtCnt(Long idActiveRef);

	/**
	 * Method Name: getSSCCPlcmtInPlcmtCnt Method Description: Method Signature
	 * for getSSCCPlcmtInPlcmtCnt
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	Long getSSCCPlcmtInPlcmtCnt(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: getActiveMedCnsntr Method Description: This method returns a
	 * Hash Map of Active Medical Consenters by Med consenter types
	 * 
	 * @param idActiveRef
	 * @return
	 */
	HashMap<String, MedicalConsenterDto> getActiveMedCnsntr(Long idActiveRef);

	/**
	 * Method Name: updatePersonStatusOnSSCCPlcmt Method Description:
	 * 
	 * @param idPerson
	 */
	void updatePersonStatusOnSSCCPlcmt(Long idPerson);

	/**
	 * Method Name: checkDtPlcmtStartChanged Method Description: Method
	 * Signature to check if DtPlcmtStart is changed.
	 * 
	 * @param ssccPlcmtHdrDto
	 * @return
	 */
	Boolean checkDtPlcmtStartChanged(SSCCPlcmtHeaderDto ssccPlcmtHdrDto);

	/**
	 * Method Name: getCrspndingPlcmtSILPair Method Description: Method
	 * Signature to find corresponding SSCC Placement Living Arrangement count
	 * for any parent for the child in stage
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	Long getCrspndingPlcmtSILPair(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: getDupOptOrCircumStatus Method Description: Method Signature
	 * for Service to check for OUtstanding Status in Placement Opt Circum.
	 * 
	 * @param idSSCCReferal
	 * @param cdSSCCPlcmtType
	 * @return
	 */
	String getDupOptOrCircumStatus(Long idSSCCReferal, String cdSSCCPlcmtType);

	/**
	 * Method Name: getSSCCPlcmtNarrative Method Description: Method Signature
	 * to fetch the Narrative from DB.
	 * 
	 * @param idSSCCPlcmtHdr
	 * @param nbrVersion
	 * @return
	 */
	SsccPlcmtNarr getSSCCPlcmtNarrative(Long idSSCCPlcmtHdr, Long nbrVersion);

	/**
	 * Method Name: saveSSCCPlcmtNarr Method Description: Method Signature to
	 * Save the SSCC Plcmt Narrative.
	 * 
	 * @param ssccPlcmtName
	 */
	void saveSSCCPlcmtNarr(SsccPlcmtNarr ssccPlcmtName);

	/**
	 * Method Name: savePlcmtIssueNarr Method Description: Method Signature for
	 * Saving Placement Issue Narrative from SSCC Placement Narrtive.
	 *
	 * @param ssccPlcmtNarrDto
	 * @param idCase
	 * @param idEvent
	 */
	void savePlcmtIssueNarr(SSCCPlcmtNarrDto ssccPlcmtNarrDto, Long idCase, Long idEvent);

	/**
	 * Method Name: getMedicaidByPerson Method Description: Method fetches the Medicaid
	 * for the given person id.
	 *
	 * @param idPerson
	 * @return person Medicaid
	 */
	String getMedicaidByPerson(Long idPerson);
}
