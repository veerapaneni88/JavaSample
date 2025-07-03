package us.tx.state.dfps.service.populateform.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.pcsphistoryform.dto.PcspHistoryDto;
import us.tx.state.dfps.service.afistatement.dto.AFIStatementDto;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentQuestionDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentSectionDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.PcspPlcmntDto;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.PCSPAssessmentRes;
import us.tx.state.dfps.service.contacts.dao.ContactEventPersonDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PcspAssessmentFormPrefillData;
import us.tx.state.dfps.service.forms.util.PcspHistoryFormPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.populateform.dao.PcspHistoryFormDao;
import us.tx.state.dfps.service.populateform.service.PcspHistoryFormService;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactDetailsOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.StageProgramDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<ServiceImpl
 * class for cinv98s, Populates the Outcome Matrix Forms & Narrative> Mar 29,
 * 2018- 12:17:30 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PcspHistoryFormServiceImpl implements PcspHistoryFormService {

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	PcspHistoryFormDao pcspHistoryDao;

	@Autowired
	PcspHistoryFormPrefillData pcspHistoryFormPrefillData;

	@Autowired
	ContactEventPersonDao contactEventPersonDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	PcspAssessmentFormPrefillData pcspAssessmentFormPrefillData;

	@Autowired
	PcspService pcspService;

	public PcspHistoryFormServiceImpl() {

	}

	/**
	 * Form name: pcsphist (Pcsp History detail form). Description: The form
	 * service will populate pcsp detail history in Parent Child Safety
	 * Placement List.
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getPcspHistoryForm(PopulateFormReq populateFormReq) {

		PcspHistoryDto histDto = new PcspHistoryDto();

		// get nmCase
		CaseSummaryDto caseName = caseSummaryDao.getCaseInfo(populateFormReq.getIdCase());
		histDto.setCaseSummaryDto(caseName);

		// set idcase in histDto
		histDto.setIdCase(populateFormReq.getIdCase());

		/*
		 * populate Pcsp History List section.
		 */
		List<PcspDto> pcspDto = pcspListPlacmtDao.getPcspPlacemnts(populateFormReq.getIdCase());
		for (PcspDto dto : pcspDto) {
			PcspPlcmntDto pcspPlcmntDto = pcspListPlacmtDao.getPcspPlacemetInfo(dto.getIdPlacement());
			dto.setEndRsnOther(pcspPlcmntDto.getEndRsnOther());
			dto.setPcspExtnDtlDtoList(pcspListPlacmtDao.getPcspPlacementExtDtl(dto.getIdPlacement()));
		}
		histDto.setPcspDto(pcspDto);

		return pcspHistoryFormPrefillData.returnPrefillData(histDto);
	}

	/**
	 * Form name: pcspasmt/pcspandm (Pcsp Assessment form). Description: The
	 * form service will populate the Addendum in the pcspasmt template based on
	 * Assessment Lookup Id. the form service will also populate the
	 * Understanding and Agreement in the pcspandm template based on the
	 * docType.
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getPcspAssessmentForm(PopulateFormReq populateFormReq, String docType) {

		PcspHistoryDto assessDto = new PcspHistoryDto();
		AFIStatementDto aFIStatementDto = new AFIStatementDto();
		// set docType passed from controller into dto for prefilldata class
		aFIStatementDto.setDocType(docType);

		/*
		 * call CSYS11D This dam retrieves the primary worker/supervisor
		 * associated with the stage.
		 */

		ContactDetailsOutDto contactDetailsOutDto = new ContactDetailsOutDto();
		contactDetailsOutDto.setUlIdEvent(populateFormReq.getIdEvent().intValue());
		StageProgramDto stageProgramDto = contactEventPersonDao.getContactDetails(contactDetailsOutDto);

		/*
		 * get Stage info by type;
		 */
		EventDto eventDto = pcspHistoryDao.getIdStageFromEvent(populateFormReq.getIdEvent());
		List<StageDto> stageDto = pcspHistoryDao.getStageDetails(eventDto.getIdStage());
		assessDto.setIdStage(eventDto.getIdStage());

		/*
		 * get assessment information; get assessment details by passing id
		 * event;
		 */
		PCSPAssessmentRes pcspRes = pcspService.getPcspAssessmentDtls(populateFormReq.getIdEvent());
		PCSPAssessmentReq pcspReq = new PCSPAssessmentReq();

		// set pcspAssessmentDto from pcspRes into request object;
		pcspReq.setPcspAssessmentDto(pcspRes.getPcspAssessmentDto());

		/*
		 * get pcsp person details by passing pcspReq with PcspAssessmentDto
		 * from pcspRes passed in;
		 */
		PCSPAssessmentRes pcspRes2 = pcspService.getPcspPersonDetails(pcspReq);

		// get caregiver phone/address
		PCSPAssessmentDto pcspAssessmentDto = pcspHistoryDao.getPcspasmntDetails(pcspRes2.getPcspAssessmentDto());

		// starts: Eliminates unwanted sections and questions from questionList;
		if (!ObjectUtils.isEmpty(pcspAssessmentDto) && !ObjectUtils.isEmpty(pcspAssessmentDto.getSections())) {
			boolean dropSection = false;
			List<PCSPAssessmentSectionDto> reducedSectionList = new ArrayList<PCSPAssessmentSectionDto>();
			for (PCSPAssessmentSectionDto sctn : pcspAssessmentDto.getSections()) {
				PCSPAssessmentSectionDto newSection = new PCSPAssessmentSectionDto();
				newSection.setSctn(sctn.getSctn());
				newSection.setSctnName(sctn.getSctnName());
				List<PCSPAssessmentQuestionDto> questionList = (List<PCSPAssessmentQuestionDto>) sctn.getQuestions();
				List<PCSPAssessmentQuestionDto> reducedQuestionList = new ArrayList<PCSPAssessmentQuestionDto>();
				if (!ObjectUtils.isEmpty(questionList)) {
					for (PCSPAssessmentQuestionDto question : questionList) {
						if (ServiceConstants.STRING_SEVEN.equalsIgnoreCase(sctn.getSctnName())) {
							if (ServiceConstants.ORDER_ONE.equals(question.getSctnQstnOrder())
									&& CodesConstant.CAEO_010.equalsIgnoreCase(pcspAssessmentDto.getCdPlcmntDecsn())) {
								reducedQuestionList.add(question);
							}
							if (ServiceConstants.ORDER_TWO.equals(question.getSctnQstnOrder())
									&& ServiceConstants.STRING_IND_Y
											.equalsIgnoreCase(pcspAssessmentDto.getIndPlcdCourtOrder())) {
								reducedQuestionList.add(question);
							}
							if (ServiceConstants.ORDER_THREE.equals(question.getSctnQstnOrder())
									&& CodesConstant.CAEO_020.equalsIgnoreCase(pcspAssessmentDto.getCdPlcmntDecsn())) {
								reducedQuestionList.add(question);
								dropSection = true;
							}
						} else {
							reducedQuestionList.add(question);
						}
					}
				}
				newSection.setQuestions(reducedQuestionList);
				if (dropSection && ServiceConstants.STRING_EIGHT.equalsIgnoreCase(sctn.getSctnName())) {
					// Do not add section eight if the drop section is true.
					// else add other section irrespective of section number and
					// drope section.
				} else {
					reducedSectionList.add(newSection);
				}
				pcspAssessmentDto.setSections(reducedSectionList);

				// set pcspAssessmentDto into main dto assessDto
				assessDto.setFirstSectionAssessmentDto(pcspAssessmentDto);
			}
		}
		// End: Eliminates unwanted sections and questions from questionList

		aFIStatementDto.setStageProgramDto(stageProgramDto);
		assessDto.setaFIStatementDto(aFIStatementDto);
		assessDto.setStageDto(stageDto);

		return pcspAssessmentFormPrefillData.returnPrefillData(assessDto);
	}
}
