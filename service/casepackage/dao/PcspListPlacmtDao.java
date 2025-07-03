package us.tx.state.dfps.service.casepackage.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.PcspExtnDtl;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.PcspExtnDtlDto;
import us.tx.state.dfps.service.casepackage.dto.PcspPlcmntDto;
import us.tx.state.dfps.service.casepackage.dto.PcspStageVerfctnDto;
import us.tx.state.dfps.service.common.response.PriorStageInRevRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pcsp.dto.PcspValueDto;

public interface PcspListPlacmtDao {

	/**
	 * 
	 * Method Description: This Method will retrieve list of all PCSP placements
	 * in a case
	 * 
	 * @param caseId
	 * @return List<PcspDto> @
	 */

	public List<PcspDto> getPcspPlacemnts(Long caseId);

	/**
	 * 
	 * Method Description: Method is implemented in PcspDaoImpl to get
	 * Assessment details
	 * 
	 * @param caseId
	 * @param PcspResponse
	 * @
	 */
	public List<PcspDto> getPcspAssessmnt(Long caseId);

	/**
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_PLCMNT, PCSP_ASMNT tables.
	 * 
	 * @param caseId
	 * @
	 */
	public PcspPlcmntDto getPcspPlacemetInfo(Long caseId);

	/**
	 *
	 * @param PcspPlcmntId
	 */
	public List<PcspExtnDtlDto> getPcspPlacementExtDtl(Long PcspPlcmntId);

	/**
	 * Method Description: This method is used to retrieve child, caregiver from
	 * NAME table.
	 * 
	 * @param caseId
	 * @
	 */
	public String getNameDetail(Long caseId);

	/**
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_STAGE_VERFCTN table.
	 * 
	 * @param caseId
	 * @
	 */
	public List<PcspStageVerfctnDto> getPcspStageVerfctn(Long caseId);

	/**
	 * Method Description: This method is used to update placements details from
	 * PCSP_PLCMNT, PCSP_ASMNT tables.
	 * 
	 * @param pcspPlcmntDB
	 * @
	 */
	public void updatePcspPlcmntDet(PcspPlcmntDto pcspPlcmntDB);

	/**
	 * Method Description: This method is used to update placements details in
	 * EVENT table.
	 * 
	 * @param idEvent
	 * @
	 */
	public void updatePcspPlcmntEvent(Long idEvent);

	public Long insertPcspStageVerfctn(PcspStageVerfctnDto verfDto);

	public Date getSubStageStartDate(Long personId);

	public Long getPrimaryAssmntEvent(Long primaryAssmtId);

	/*
	 * This method will fetch the the meth indicator flag
	 * 
	 * @param idStage
	 * 
	 * @return PCSPAssessmentRes -- String meth Ind
	 */
	public String getMethIndicator(Long idStage);

	/**
	 * 
	 * Returns any prior stage ID for any given stage ID and a type request.
	 * Example. If a INT stage needs be found for a case thats currently in a
	 * FPR stage. Pass FPR stage ID and 'INT'
	 *
	 * @param CommonHelperReq
	 *            --ulIdStage id of the stage for which to retrieve the
	 *            corresponding prior stage
	 * @param CommonHelperReq
	 *            cdStageType stage type code. Example INT, INV etc
	 * @return PriorStageInRevRes
	 */
	public PriorStageInRevRes getPriorStageInReverseChronologicalOrder(Long idStage, String stageType);

	/*
	 * 
	 * This method checks to see if there are any open assessments for stage
	 *
	 * @param CommonHelperReq --IdStage
	 * 
	 * @return CpsInvCnclsnRes
	 */
	public Boolean hasOpenPCSPAsmntForStage(Long idStage);

	/*
	 * This checks if there any open pcsp placements for case verified
	 * 
	 * @param idCase
	 * 
	 * @return boolean
	 *
	 */
	public Boolean hasOpenPCSPlacement(Long idCase);

	/*
	 * This checks if there any open pcsp placements for case Not verified
	 * 
	 * @param idCase
	 * 
	 * @return boolean
	 *
	 */
	public Boolean hasOpenPCSPlacementNotVerify(Long idCase, Long idStage);

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 * 
	 * @param CommonHelperReq -- stageId
	 * 
	 * @return CpsInvCnclsnRes -- boolean response
	 */
	public Boolean getContactPurposeStatus(Long idStage);

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initiation already exist.
	 *
	 * @param CommonHelperReq -- stageId
	 *
	 * @return CpsInvCnclsnRes -- boolean response
	 */
	public Boolean getCntctPurposeInitiationStatus(Long idStage);

	/*
	 * Check to see if the given event id has an entry on the approval event
	 * link table. If it does, the event has been submitted for approval at one
	 * point.(CPS Inst Conclusion Page)
	 */
	public Boolean setHasBeenSubmittedForApprovalCps(Long idEvent);

	public List<PcspDto> getPcspPlacemnts(List<PcspDto> pcspDTOs, Long caseId, PcspDto pcspDto);

	/**
	 * Method Name: getChildPCSPEndDate Method Description: Retrieves the PCSP
	 * End date of a particular child from the CHILD_SAFETY_PLACEMENT table
	 * 
	 * @param idPerson
	 * @return List<PcspValueDto>
	 * @throws DataNotFoundException
	 */
	public List<PcspValueDto> getChildPCSPEndDate(Long idPerson);

	/**
	 * 
	 * This method checks if there any open pcsp placements that have not been
	 * stage verified
	 * 
	 * @param idCase
	 * @param idStage
	 * 
	 * @return boolean
	 * 
	 */
	boolean hasOtherStagesOpen(Long idCase, String cdStage);

	/**
	 * This method checks if there any open pcsp placements that have not been
	 * stage verified
	 * 
	 * @param idCase
	 * @param idStage
	 * @return boolean
	 */
	Boolean hasOpenPCSPlcmntNotVrfd(Long idCase, Long idStage);

	/**
	 * 
	 * Method Name: displayPCSPList Method Description: Get PCSP List for AR
	 * Conclusion Page Display
	 * 
	 * @param idCase
	 * @param cdStage
	 * @return List<PcspDto>
	 */
	public List<PcspDto> displayPCSPList(Long idCase, String cdStage);
	
	
	
	/**  This Method will check is FPR Stage is Open and brings status string
	 * @param idCase
	 * @return
	 */
	public boolean hasFPRStagesOpen(Long idCase);
	
	
	
	/**  This Method will check is get the Prior Stage 
	 * @param idCase
	 * @param cdStage
	 * @return
	 */
	public String getPriorStage(Long idCase, Long idStage);

	public Long getPriorStageId(Long idCase, Long idStage);

	public Long insertPcspExtnDtl(PcspExtnDtl extnDtl, Long pcspPlcmntDto);
}
