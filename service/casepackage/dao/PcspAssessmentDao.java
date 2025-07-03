package us.tx.state.dfps.service.casepackage.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentQuestionDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
import us.tx.state.dfps.service.casepackage.dto.PcspAsmntLkupDto;
import us.tx.state.dfps.service.casepackage.dto.PcspPrsnLinkDto;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

public interface PcspAssessmentDao {

	/**
	 * This method will retrieve the person children ohm details from
	 * stage_person_link table for the stage
	 * 
	 * @param pcspAssessmentReq
	 * @param stageId
	 * @param personType
	 * @return List<PCSPAssmtValueDto> @
	 */
	public List<PCSPPersonDto> getPersonChildOhmDtls(PCSPAssessmentReq pcspAssessmentReq, Long stageId,
			String personType);

	/**
	 * This method will retrieve the primary care giver details from
	 * stage_person_link table for the stage
	 * 
	 * @param pcspAssessmentReq
	 * @param stageId
	 * @return List<PCSPAssmtValueDto> @
	 */
	public List<PCSPPersonDto> getPrimaryCaregiverDtls(PCSPAssessmentReq pcspAssessmentReq, Long stageId);

	/**
	 * This method will retrieve the person details from stage_person_link table
	 * for the stage
	 * 
	 * @param stageId
	 * @return List<PCSPAssmtValueDto> @
	 */
	public List<PCSPPersonDto> getPersonDtls(Long stageId);

	/**
	 * This Method is used to check if the placement is open
	 * 
	 * @param personId
	 * @return Boolean @
	 */
	public Boolean getPlacementOpen(Long personId);

	/**
	 * This Method is used to check if the placement is open
	 * 
	 * @param personId
	 * @return Boolean @
	 */
	public Boolean getLegacyPlacementOpen(Long personId);

	/**
	 * This Method is used to get the placement end date.
	 * 
	 * @param personId
	 * @return Date @
	 */
	public Date getPlacementEndDate(Long personId);

	/**
	 * This Method is used to get the placement end date(to date of decision
	 * validation).
	 * 
	 * @param personId
	 * @return Date @
	 */
	public Date getLegacyPlacementEndDate(Long personId);

	/**
	 * This Method is used to get the SUB stage start date of a child.
	 * 
	 * @param personId
	 * @return Date @
	 */
	public Date getSubStageStartDate(Long personId);

	/**
	 * This Method is used to check if answers are saved for the assessment
	 * 
	 * @param eventId
	 * @return Boolean @
	 */
	public Boolean getResponseSaved(Long eventId);

	/**
	 * This is used to retrieve PCSPAssessment dtls based on PCSP Assessment
	 * Event Id and Stage Id It pulls back the sections, questions and responses
	 * 
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */
	public PCSPAssessmentDto getPcspAssessmentDtls(Long eventId);

	/**
	 * This is used to get the intake start date
	 * 
	 * @param caseId
	 * @return Date @
	 */
	public Date getIntakeStageStartDate(Long caseId);

	/**
	 * This is used to get Saved children in pcsp_prsn_link
	 * 
	 * @param idPcspAssmt
	 * @return Set<Integer> @
	 */
	public List<PcspPrsnLinkDto> getpcspPrsnLinkChild(Long idPcspAssmt);

	/**
	 * This is used to get Saved ohm in pcsp_prsn_link
	 * 
	 * @param idPcspAssmt
	 * @return Set<Integer> @
	 */
	public List<PcspPrsnLinkDto> getpcspPrsnLinkOhm(Long idPcspAssmt);

	/**
	 * This Method will retrieve all sections, questions, responses. When
	 * iterating through the results we have to determine what changed (i.e.
	 * sections, questions, responses) in order to determine which objects to
	 * create.
	 * 
	 * @param idPcspAssmt
	 * @param idPcspAsmntlookup
	 * @return PCSPAssessmentDto @
	 */
	public List<PCSPAssessmentDto> getPcspSections(Long idPcspAssmt, Long idPcspAsmntlookup);

	/**
	 * This Method will retrieve all sections, questions, responses. When
	 * iterating through the results we have to determine what changed (i.e.
	 * sections, questions, responses) in order to determine which objects to
	 * create.
	 * 
	 * @param idPcspAsmntlookup
	 * @return PCSPAssessmentDto @
	 */
	public List<PCSPAssessmentDto> getQueryPageData(Long idPcspAsmntlookup);

	/**
	 * This Method will delete children Or OHM from PCSP_PRSN_LINK table
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	public String deletePcspPersonLink(Long assessmentId, Long idPerson);

	/**
	 * This method is used to insert children and Other house hold members
	 * selected in the assessment/addendum into the pcsp_prsn_link table
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	public String savePcspPersonLink(PCSPAssessmentDto pcspAssessmentDto, Long idPerson, String cdPrsnType);

	/**
	 * This method is used to add new records in PCSP Response table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	public String savePcspResponse(PCSPAssessmentDto pcspAssessmentDto, PCSPAssessmentQuestionDto questionDto);

	/**
	 * This Method is used to update the response table with text description
	 * and the criminal and abuse history for Q4 and Q5 of section 1
	 * 
	 * @param pcspAssessmentQuestionDto
	 * @return String @
	 */
	public String updatePcspRespCriminal(PCSPAssessmentQuestionDto pcspAssessmentQuestionDto);

	/**
	 * This Method is used to update the response in PCSP Response table
	 * 
	 * @param pcspAssessmentQuestionDto
	 * @return String @
	 */
	public String updatePcspResp(PCSPAssessmentQuestionDto pcspAssessmentQuestionDto);

	/**
	 * This Method is used to update the assessment in Pcsp Assessment table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	public String updateAssessment(PCSPAssessmentDto pcspAssessmentDto);

	/**
	 * This Method is used to check if placement exists for the primary
	 * caregiver for the same case
	 * 
	 * @param caregiverId
	 * @param caseId
	 * @return Boolean @
	 */
	public Boolean getPlacementExists(Long caregiverId, Long caseId);

	/**
	 * This Method is used to insert record into PCSP_ASMNT table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	public Long savePcspAssessment(PCSPAssessmentDto pcspAssessmentDto, PcspAsmntLkupDto pcspAsmntLkupDto,
			Long idPrimaryAsmt);

	/**
	 * This Method is used to get PCSP Assessment lookup details.
	 * 
	 * @param pcspAssessmentDto
	 * @return PcspAsmntLkupDto @
	 */
	public PcspAsmntLkupDto getPcspAsmntLkup(PCSPAssessmentDto pcspAssessmentDto);

	/**
	 * This Method is used to get the parent assessment Id for the addendum
	 * 
	 * @param idCaregiver
	 * @param idCase
	 * @return Long @
	 */
	public Long getPrimaryAsmtId(Long idCaregiver, Long idCase);

	/**
	 * This Method is used to sets the status of PCSP Assessment Event to
	 * COMP(Complete)
	 * 
	 * @param eventDto
	 * @return Long @
	 */
	public Long updateEvntStatusToComp(EventDto eventDto);

	/**
	 * This Method will insert completedDate for the PCSP_ASMNT table when the
	 * assessment is complete.
	 * 
	 * @param pcspAssessmentDto
	 * @return Long @
	 */
	public Long updateAssmtCompDate(PCSPAssessmentDto pcspAssessmentDto);

	/**
	 * This method is used to insert records to placements tables when the
	 * assessment is complete.
	 * 
	 * @param pcspAssessmentDto
	 * @return Long @
	 */
	public Long savePcspPlacmt(PCSPAssessmentDto pcspAssessmentDto, PCSPPersonDto pcspPersonDto, Long eventId);

	/**
	 * This Method is used to delete all the pcsp assessment related information
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	public String deletePcspAssmntDetials(PCSPAssessmentDto pcspAssessmentDto);

	/**
	 * This Method is used to check if the Child selected has an active Legal
	 * Status.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	public Boolean getshowLegalWarning(Long idPerson);

	/**
	 * This Method is used to check whether primary address exists for this
	 * person.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	public Boolean getaddressExists(Long idPerson);

	/**
	 * This Method is used to check whether primary phone number exists for this
	 * person.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	public Boolean getphoneExists(Long idPerson);

}
