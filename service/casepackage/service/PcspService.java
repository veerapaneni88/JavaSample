package us.tx.state.dfps.service.casepackage.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.PCSPAssessmentRes;
import us.tx.state.dfps.service.common.response.PcspPlcmntRes;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.PriorStageInRevRes;
import us.tx.state.dfps.service.pcsp.dto.PcspValueDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

public interface PcspService {

	/**
	 *
	 * Method Description: This method is used to retrieve placement details of
	 * a caseid from PCSP_PLCMNT table.
	 *
	 * @param caseId
	 * @return PcspRes @
	 */

	public PcspRes getPcspPlacementsService(Long caseId);

	/**
	 *
	 * Method Description: This method is used to retrieve assessment details of
	 * a caseid from PCSP_ASMNT table.
	 *
	 * @param caseId
	 * @return PcspRes @
	 */

	public PcspRes getPcspAssessmentsService(Long caseId);

	/**
	 *
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param PcspPlcmntId
	 * @return PcspPlcmntResponse @
	 */

	public PcspPlcmntRes getPcspPlacemetInfo(Long PcspPlcmntId, Long caseID);

	/**
	 *
	 * Method Description: This method is used to update placements details from
	 * PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param pcspPlcmntDB
	 * @return String @
	 */

	public PcspPlcmntRes updatePcspPlcmntDetails(PcspReq pcspPlcmntDB);

	/**
	 * This Method will retrieve the list of PCSP primary caregiver and children
	 * details
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	public PCSPAssessmentRes getPcspPersonDetails(PCSPAssessmentReq pcspAssessmentReq);

	/**
	 * This Method is used to check if answers are saved for the assessment
	 *
	 * @param eventId
	 * @return Boolean @
	 */
	public PCSPAssessmentRes getResponseSaved(Long eventId);

	/**
	 * This Method is used to retrieve PCSPAssessment dtls based on PCSP
	 * Assessment Event Id and Stage Id It pulls back the sections, questions
	 * and responses
	 *
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */

	public PCSPAssessmentRes getPcspAssessmentDtls(Long eventId);

	/**
	 * This method will returns pcsp Assessment Data bean to build a New pcsp
	 * Assessment form in Impact. This would contain questions, answers related
	 * to latest pcsp Assessment version.
	 *
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */

	public PCSPAssessmentRes getQueryPageData(Long eventId);

	/**
	 * This Method is used to save the PCSPAssessmentDB to the PCSP_ASSMT table
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	public PCSPAssessmentRes savePcspAssessment(PCSPAssessmentReq pcspAssessmentReq);

	/**
	 * This Method will complete the Assessment Process
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	public PCSPAssessmentRes completeAssessment(PCSPAssessmentReq pcspAssessmentReq);

	/**
	 * This method will delete an assessment in PROC
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	public PCSPAssessmentRes deletePcspAssmtDetails(PCSPAssessmentReq pcspAssessmentReq);

	/*
	 * This method checks to see if case open date is before Mar 16 release
	 *
	 * @param CommonHelperReq
	 *
	 * @return PCSPAssessmentRes
	 *
	 * @
	 */
	public PCSPAssessmentRes checkLegacyPCSP(CommonHelperReq commonHelperReq);

	/*
	 * This method will fetch the the meth indicator flag
	 *
	 * @param idStage
	 *
	 * @return PCSPAssessmentRes -- String meth Ind
	 */

	public PCSPAssessmentRes getMethIndicator(CommonHelperReq commonHelperReq);

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
	public PriorStageInRevRes getPriorStageInReverseChronologicalOrder(CommonHelperReq commonHelperReq);

	/*
	 * This method checks to see there are open placements for input case and if
	 * they can be verified for the input stage and reason closed. Error message
	 * is returned if no open placements are allowed or if no verification
	 * records exists for allowable closed reasons.
	 */
	public PCSPAssessmentRes valPCSPPlcmt(CommonHelperReq commonHelperReq);

	/*
	 *
	 * This method checks to see if there are any open assessments for stage
	 *
	 * @param CommonHelperReq --IdStage
	 *
	 * @return CpsInvCnclsnRes
	 */
	public CpsInvCnclsnRes hasOpenPCSPAsmntForStage(CommonHelperReq commonHelperReq);

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 */
	public CpsInvCnclsnRes getContactPurposeStatus(CommonHelperReq commonHelperReq);

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initiation already exist.
	 */
	public CpsInvCnclsnRes getCntctPurposeInitiationStatus(CommonHelperReq commonHelperReq);

	/*
	 * Check to see if the given event id has an entry on the approval event
	 * link table. If it does, the event has been submitted for approval at one
	 * point.(CPS Inst Conclusion Page)
	 *
	 */
	public CpsInvCnclsnRes hasBeenSubmittedForApprovalCps(CommonHelperReq commonHelperReq);

	/**
	 *
	 * Method Name: getChildPCSPEndDate Method Description:Retrieve open PCSP of
	 * the particular child
	 *
	 * @param pcspValueDto
	 * @return
	 */
	public List<PcspValueDto> getChildPCSPEndDate(PcspValueDto pcspValueDto);

	/**
	 * This method checks to see there are open placements for input case and if
	 * they can be verified for the input stage and reason closed. Error message
	 * is returned if no open placements are allowed or if no verification
	 * records exists for allowable closed reasons.
	 *
	 * @param Connection
	 *            -
	 * @param cdStage
	 * @param cdStageReasonClosed
	 * @param idCase
	 * @param idStage
	 *
	 * @return int
	 *
	 */
	int valPCSPPlcmt(String cdStage, Long idCase, Long idStage);

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

	/**
	 *
	 * Method Name: getPersonDetails Method Description: Retrieve PCSP child
	 * name and cargiver name
	 *
	 * @param idStage
	 * @return List<PcspDto>
	 */

	public List<PcspDto> getPersonDetails(Long idStage);

}
