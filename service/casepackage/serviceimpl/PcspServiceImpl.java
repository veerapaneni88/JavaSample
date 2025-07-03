package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.PcspExtnDtl;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspAssessmentDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.*;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.PCSPAssessmentRes;
import us.tx.state.dfps.service.common.response.PcspPlcmntRes;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.PriorStageInRevRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dao.PcspDao;
import us.tx.state.dfps.service.pcsp.dto.PcspValueDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;

@Service
@Transactional
public class PcspServiceImpl implements PcspService {

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PcspAssessmentDao pcspAssessmentDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	PcspDao pcspDao;

	@Autowired
	AlertService alertService;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	ApprovalStatusDao approvalStatusDao;


	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	StageDao stageDao;

	@Autowired
	CodesDao codesDao;

	@Autowired
	private EventDao eventDao;

	private static final Logger logger = Logger.getLogger(PcspServiceImpl.class);

	/**
	 *
	 * Method Description: This method is used to retrieve placement details of
	 * a caseid from PCSP_PLCMNT table.
	 *
	 * @param caseId
	 * @return PcspRes @
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PcspRes getPcspPlacementsService(Long caseId) {
		PcspRes res = new PcspRes();
		res.setPcspList(pcspListPlacmtDao.getPcspPlacemnts(caseId));
		return res;
	}

	/**
	 *
	 * Method Description: This method is used to retrieve assessment details of
	 * a caseid from PCSP_ASMNT table.
	 *
	 * @param caseId
	 * @return PcspRes @
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PcspRes getPcspAssessmentsService(Long caseId) {
		PcspRes resp = new PcspRes();
		List<PcspDto> pcspList = pcspListPlacmtDao.getPcspAssessmnt(caseId);
		for (PcspDto p : pcspList) {
			if (null != p.getIdPrimaryAssmt() && p.getIdPrimaryAssmt() != 0) {
				p.setIdPrimaryAssmtEvent(pcspListPlacmtDao.getPrimaryAssmntEvent(p.getIdPrimaryAssmt()));
			} else {
				p.setIdPrimaryAssmtEvent(0L);
			}
		}
		resp.setPcspList(pcspList);
		return resp;
	}

	/**
	 *
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param PcspPlcmntId
	 * @return PcspPlcmntResponse @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PcspPlcmntRes getPcspPlacemetInfo(Long PcspPlcmntId, Long caseId) {
		PcspPlcmntRes response = new PcspPlcmntRes();
		PcspPlcmntDtlDto pcspPlcmntDtl = new PcspPlcmntDtlDto();
		EventReq eventRequest = new EventReq();
		List<String> eventType = Arrays.asList("LEG");
		List<EventListDto> legalActionEventList = Collections.emptyList();
		eventRequest.setUlIdCase(caseId);
		eventRequest.setEventType(eventType);
		PcspPlcmntDto pcspList = pcspListPlacmtDao.getPcspPlacemetInfo(PcspPlcmntId);
		List<PcspExtnDtlDto> pcspExtnDtlDtoList = pcspListPlacmtDao.getPcspPlacementExtDtl(PcspPlcmntId);
		if(!TypeConvUtil.isNullOrEmpty(pcspExtnDtlDtoList) && pcspExtnDtlDtoList.size()!=0) {
			pcspList.setPcspExtnDtlDtos(pcspExtnDtlDtoList);
			PcspExtnDtlDto pcspExtnDtlDto = pcspExtnDtlDtoList.get(0);
			if (pcspExtnDtlDto!=null && pcspExtnDtlDto.getExtensionNumber() >= 2 && pcspExtnDtlDto.getExtensionNumber() <= 4) {
				if (pcspExtnDtlDto.getIndToContPcsp() ==null || pcspExtnDtlDto.getIndToContPcsp().equals("N") ){
					legalActionEventList = eventDao.getEventDetails(eventRequest);
					if(!TypeConvUtil.isNullOrEmpty(legalActionEventList) ){
						pcspExtnDtlDto.setLegalActionEventList(legalActionEventList);
					}
				}else if(pcspExtnDtlDto.getIndToContPcsp().equals("Y")){
					logger.debug("Displaying selected legal Event saved for PCSP extention details");
					if(pcspExtnDtlDto.getIdCase()!=null && pcspExtnDtlDto.getLegalActionEventId()!=null){
						EventDto selectedLegalEvent =  eventDao.getSelectedLegalEvent(pcspExtnDtlDto.getIdCase(),pcspExtnDtlDto.getLegalActionEventId());
						pcspPlcmntDtl.setSelectedLegalActionEvent(selectedLegalEvent);
					}

				}
			}

		}
		if (!TypeConvUtil.isNullOrEmpty(pcspList)) {
			Long childId = pcspList.getIdPerson();
			Long carGvreId = pcspList.getIdPrsnCrgvr();
			String childName = personDtlService.getPersonFullName(childId);//code updated for defect 6503 artf81655
			String careGiverName = personDtlService.getPersonFullName(carGvreId);
			Date subStageStartDate = pcspListPlacmtDao.getSubStageStartDate(pcspList.getIdPerson());
			pcspList.setDtSubStageStartDate(subStageStartDate);
			pcspPlcmntDtl.setChildName(childName);
			pcspPlcmntDtl.setCareGiver(careGiverName);
			pcspPlcmntDtl.setPcspPlcmntDto(pcspList);
		}
		List<PcspStageVerfctnDto> PcspStageVerlist = pcspListPlacmtDao.getPcspStageVerfctn(PcspPlcmntId);
		if (PcspStageVerlist != null && PcspStageVerlist.size() > 0) {
			Iterator<PcspStageVerfctnDto> itr = PcspStageVerlist.iterator();
			while (itr.hasNext()) {
				PcspStageVerfctnDto verfObj = itr.next();
				Long idPerson = verfObj.getIdCreatedPerson();
				Person nmPersonFull = personDao.getPersonByPersonId(idPerson);
				verfObj.setUpdatedBy(nmPersonFull.getNmPersonFull());
				pcspPlcmntDtl.setStageVerList(PcspStageVerlist);
			}
		}
		response.setPcspPlcmntDtl(pcspPlcmntDtl);
		return response;
	}

	/**
	 *
	 * Method Description: This method is used to update placements details from
	 * PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param pcspPlcmntDB
	 * @return String @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PcspPlcmntRes updatePcspPlcmntDetails(PcspReq pcspreq) {
		PcspPlcmntDto pcspPlcmntDto = pcspreq.getPcspplcmnt();
		PcspPlcmntRes response = new PcspPlcmntRes();
		PcspPlcmntDtlDto pcspPlcmntDtl = new PcspPlcmntDtlDto();
		boolean isFprStageOpen = false;
		String priorStage = null;
		/* if (errorMsgs.isEmpty()) { */
		if (pcspPlcmntDto.getIndVerified()) {
			// initialize stage verification record for save
			PcspStageVerfctnDto pcspStageVerfctnDto = new PcspStageVerfctnDto();
			pcspStageVerfctnDto.setIdPcspPlcmnt(pcspPlcmntDto.getIdPcspPlcmnt());
			pcspStageVerfctnDto.setIdStageVerfd(pcspreq.getStageId());
			pcspStageVerfctnDto.setIdPrsnVerfd(pcspreq.getIdUser());
			pcspStageVerfctnDto.setIdCreatedPerson(pcspreq.getIdUser());
			pcspStageVerfctnDto.setIdLastUpdatePerson(pcspreq.getIdUser());
			if (null != pcspStageVerfctnDto) {
				pcspListPlacmtDao.insertPcspStageVerfctn(pcspStageVerfctnDto);
			}
		}
		if (!ObjectUtils.isEmpty(pcspPlcmntDto.getRenewalDate())) {
			PcspExtnDtl extnDtl = new PcspExtnDtl();
			extnDtl.setDtRenewal(pcspPlcmntDto.getRenewalDate());
			extnDtl.setDtExtnExpry(pcspPlcmntDto.getExtensionExpiryDate());
			extnDtl.setNbrExtn(pcspPlcmntDto.getExtensionNumber());
			extnDtl.setIdCase(pcspreq.getCaseId());
			if(pcspPlcmntDto.getExtensionNumber() >=3 && pcspPlcmntDto.getIndToContPcsp().equals("Y")){
				extnDtl.setLegalActionEventId(pcspPlcmntDto.getPcspLegalActionEvent().getIdEvent());
			}
			extnDtl.setCdGoal(pcspPlcmntDto.getExtsnCdGoal());
			extnDtl.setIndToContPcsp(pcspPlcmntDto.getIndToContPcsp());
			extnDtl.setIndAtrnyParentAgrmnt(pcspPlcmntDto.getIndAtrnyParentAgrmnt());
			extnDtl.setCreatedPersonId(pcspreq.getIdUser());
			extnDtl.setLastUpdatedPersonId(pcspreq.getIdUser());
			pcspListPlacmtDao.insertPcspExtnDtl(extnDtl, pcspPlcmntDto.getIdPcspPlcmnt());
		}

		pcspListPlacmtDao.updatePcspPlcmntDet(pcspPlcmntDto);
		if (null != pcspPlcmntDto.getDtEnd()) {
			pcspListPlacmtDao.updatePcspPlcmntEvent(pcspPlcmntDto.getIdEvent());
			SelectStageDto selectStageDtoCurrent = caseSummaryDao.getStage(pcspreq.getStageId(), ServiceConstants.STAGE_CURRENT);
			if(ServiceConstants.CSTAGES_FPR.equalsIgnoreCase(selectStageDtoCurrent.getCdStage())){
				Long workeridStage=null;
				// Get Prior Stage and check is it A-R Stage and its closed. 
				SelectStageDto selectStageDtoPrior = caseSummaryDao.getStage(pcspreq.getStageId(), ServiceConstants.STAGE_PRIOR);
				if(null == selectStageDtoPrior.getDtStageClose() || ServiceConstants.GENERIC_END_DATE.compareTo(selectStageDtoPrior.getDtStageClose()) == 0){
					workeridStage = selectStageDtoPrior.getIdStage();
				} else if(ServiceConstants.A_R_STAGE.equalsIgnoreCase(selectStageDtoPrior.getCdStage())){
					// selectStageDtoLater = caseSummaryDao.getStage(selectStageDtoPrior.getIdStage(), ServiceConstants.STAGE_LATER);
					List<StageDto>	 selectStageDtoLaterList =  approvalStatusDao.fetchCpsPriorOrProgressedStage(selectStageDtoPrior.getIdStage(), ServiceConstants.CSTAGES_AR, true);
					// get A-R later Stage check is it INV and opened.  
					if(!ObjectUtils.isEmpty(selectStageDtoLaterList)){
						StageDto  selectStageDtoLaterINV = selectStageDtoLaterList.get(0);
						if(ServiceConstants.INV_Stage.equalsIgnoreCase(selectStageDtoLaterINV.getCdStage()) && (null == selectStageDtoLaterINV.getDtStageClose() ||
								ServiceConstants.GENERIC_END_DATE.compareTo(selectStageDtoLaterINV.getDtStageClose()) == 0)){
							workeridStage = selectStageDtoLaterINV.getIdStage();
						}
					}
				}
				if(workeridStage != null){
					// Get the PE and SE For Open INV
					List<Long> caseWorkePersonIdList  = workLoadDao.getAssignedWorkersForStage(workeridStage);
					if(!caseWorkePersonIdList.isEmpty()){
						// for creating alerts
						for(Long caseWorkePersonId : caseWorkePersonIdList){
							alertService.createFbssAlert(workeridStage, caseWorkePersonId,
									null, pcspreq.getCaseId(), ServiceConstants.PCSP_TEXT_DESC,
									ServiceConstants.PCSP_TEXT_DESC);
						}
					}
				}
			}
		}
		pcspPlcmntDtl.setPcspPlcmntDto(pcspPlcmntDto);
		pcspPlcmntDtl.setSucessMsg("Saved Sucessfully");
		response.setPcspPlcmntDtl(pcspPlcmntDtl);
		logger.info("TransactionId :" + pcspreq.getTransactionId());
		return response;
		/*
		 * } else { pcspPlcmntDtl.setErrorMessages(errorMsgs);
		 * response.setPcspPlcmntDtl(pcspPlcmntDtl); log.info("TransactionId :"
		 * + pcspreq.getTransactionId()); return response; }
		 */
	}

	@Transactional
	public static boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	/**
	 * This Method will retrieve the list of PCSP primary caregiver and children
	 * details
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes getPcspPersonDetails(PCSPAssessmentReq pcspAssessmentReq) {
		List<PCSPPersonDto> personList = new ArrayList<PCSPPersonDto>();
		List<PCSPPersonDto> careGiverList = new ArrayList<PCSPPersonDto>();
		List<PCSPPersonDto> childrenList = new ArrayList<PCSPPersonDto>();
		List<PCSPPersonDto> ohmList = new ArrayList<PCSPPersonDto>();
		PCSPAssessmentDto pcspAssessmentDto;
		if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto())) {
			pcspAssessmentDto = pcspAssessmentReq.getPcspAssessmentDto();
		} else {
			pcspAssessmentDto = new PCSPAssessmentDto();
		}
		// pcspAssessmentReq.getPcspAssessmentDto()
		if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getIdEvent())) {
			if (pcspAssessmentReq.getPcspAssessmentDto().getCdEventStatus()
					.equalsIgnoreCase(ServiceConstants.CEVTSTAT_COMP)
					|| pcspAssessmentReq.getPcspAssessmentDto().getCdEventStatus()
					.equalsIgnoreCase(ServiceConstants.CEVTSTAT_APRV)
					|| pcspAssessmentReq.getPcspAssessmentDto().getCdEventStatus()
					.equalsIgnoreCase(ServiceConstants.CEVTSTAT_PEND)) {
				childrenList = pcspAssessmentDao.getPersonChildOhmDtls(pcspAssessmentReq,
						pcspAssessmentReq.getPcspAssessmentDto().getIdStage(), ServiceConstants.PCSPPRSN_010);
				ohmList = pcspAssessmentDao.getPersonChildOhmDtls(pcspAssessmentReq,
						pcspAssessmentReq.getPcspAssessmentDto().getIdStage(), ServiceConstants.PCSPPRSN_030);
				careGiverList = pcspAssessmentDao.getPrimaryCaregiverDtls(pcspAssessmentReq,
						pcspAssessmentReq.getPcspAssessmentDto().getIdStage());
				pcspAssessmentDto.setChildrenAssessedList(childrenList);
				pcspAssessmentDto.setOhmAssessedList(ohmList);
				pcspAssessmentDto.setPgAssessedList(careGiverList);
			}
		} else {
			personList = pcspAssessmentDao.getPersonDtls(pcspAssessmentReq.getPcspAssessmentDto().getIdStage());
			List<PCSPPersonDto> personListDtls = new ArrayList<PCSPPersonDto>();
			if (null != personList && personList.size() > 0) {
				for (PCSPPersonDto person : personList) {
					person.setShowLegalWarning(pcspAssessmentDao.getshowLegalWarning(person.getIdPerson()));
					person.setAddressExists(pcspAssessmentDao.getaddressExists(person.getIdPerson()));
					person.setPhoneExists(pcspAssessmentDao.getphoneExists(person.getIdPerson()));
					if (!TypeConvUtil.isNullOrEmpty(person.getDtPersonBirth())) {
						Integer prsnAge = DateUtils.getAge(person.getDtPersonBirth());
						person.setPersonAge(prsnAge.longValue());
					}
					personListDtls.add(person);
				}
			}
			if (null != personListDtls && personListDtls.size() > 0) {
				for (PCSPPersonDto person : personListDtls) {
					if (!TypeConvUtil.isNullOrEmptyAge(person.getPersonAge())) {
						if ((person.getPersonAge() >= ServiceConstants.MIN_PG_AGE)) {
							careGiverList.add(person);
						}
					}
				}
			}
			if (null != personListDtls && personListDtls.size() > 0) {
				for (PCSPPersonDto person : personListDtls) {
					if (!TypeConvUtil.isNullOrEmptyAge(person.getPersonAge())) {
						if ((person.getPersonAge() <= ServiceConstants.MAX_VICTIM_AGE)) {
							person.setPlacementOpen(pcspAssessmentDao.getPlacementOpen(person.getIdPerson()));
							person.setLegacyPlacementOpen(
									pcspAssessmentDao.getLegacyPlacementOpen(person.getIdPerson()));
							person.setDtEnd(pcspAssessmentDao.getPlacementEndDate(person.getIdPerson()));
							person.setDtLegacyPlacementEnd(
									pcspAssessmentDao.getLegacyPlacementEndDate(person.getIdPerson()));
							person.setDtSubStageStart(pcspAssessmentDao.getSubStageStartDate(person.getIdPerson()));
							childrenList.add(person);
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentDto)
					&& pcspAssessmentDto.getIdPrsncrgvr() != ServiceConstants.ZERO_VAL) {
				if (!CollectionUtils.isEmpty(careGiverList)) {
					for (PCSPPersonDto person : personListDtls) {
						if (!person.getIdPerson().equals(pcspAssessmentDto.getIdPrsncrgvr())) {
							ohmList.add(person);
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(careGiverList)) {
				pcspAssessmentDto.setPgAssessedList(careGiverList);
			}
			if (!TypeConvUtil.isNullOrEmpty(childrenList)) {
				pcspAssessmentDto.setChildrenAssessedList(childrenList);
			}
			if (!TypeConvUtil.isNullOrEmpty(ohmList)) {
				pcspAssessmentDto.setOhmAssessedList(ohmList);
			}
			if (TypeConvUtil.isNullOrEmpty(pcspAssessmentDto.getSavedChildrenAssessed())) {
				pcspAssessmentDto.setSavedChildrenAssessed(null);
			}
			if (TypeConvUtil.isNullOrEmpty(pcspAssessmentDto.getSavedOhmAssessed())) {
				pcspAssessmentDto.setSavedOhmAssessed(null);
			}
		}
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		pcspAssessmentRes.setPcspAssessmentDto(pcspAssessmentDto);
		return pcspAssessmentRes;
	}

	/**
	 * This Method is used to check if answers are saved for the assessment
	 *
	 * @param eventId
	 * @return Boolean @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes getResponseSaved(Long eventId) {
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		pcspAssessmentRes.setIsResponseSave(pcspAssessmentDao.getResponseSaved(eventId));
		return pcspAssessmentRes;
	}

	/**
	 * This Method is used to retrieve PCSPAssessment dtls based on PCSP
	 * Assessment Event Id and Stage Id It pulls back the sections, questions
	 * and responses
	 *
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public PCSPAssessmentRes getPcspAssessmentDtls(Long eventId) {
		Set<Integer> childrenSet = new HashSet<Integer>();
		Set<Integer> ohmSet = new HashSet<Integer>();
		PCSPAssessmentDto pcspAssessmentDto = pcspAssessmentDao.getPcspAssessmentDtls(eventId);
		pcspAssessmentDto.setDtIntakeStart(pcspAssessmentDao.getIntakeStageStartDate(pcspAssessmentDto.getIdCase()));
		List<PcspPrsnLinkDto> childSet = pcspAssessmentDao.getpcspPrsnLinkChild(pcspAssessmentDto.getIdPcspAsmnt());
		for (PcspPrsnLinkDto pcspPrsnLinkDto : childSet) {
			if (pcspPrsnLinkDto.getCdPcspPrsnType().equals(ServiceConstants.PCSPPRSN_TYPE10)) {
				childrenSet.add(Integer.valueOf(pcspPrsnLinkDto.getIdPerson().intValue()));
			} else if (pcspPrsnLinkDto.getCdPcspPrsnType().equals(ServiceConstants.PCSPPRSN_TYPE30)) {
				ohmSet.add(Integer.valueOf(pcspPrsnLinkDto.getIdPerson().intValue()));
			}
		}
		List<PCSPAssessmentDto> pcspAssessmentDtoList = pcspAssessmentDao
				.getPcspSections(pcspAssessmentDto.getIdPcspAsmnt(), pcspAssessmentDto.getIdPcspAsmntLookup());
		List<PCSPAssessmentSectionDto> sections = new ArrayList<>();
		PCSPAssessmentSectionDto pcspAssessmentSectionDto = null;
		PCSPAssessmentQuestionDto pcspAssessmentquestionDB = null;
		PCSPAssessmentResponseDto pcspAssessmentresponseDB = null;
		Long previousSectionLkpId = ServiceConstants.ZERO_VAL;
		Long previousQuestionLkpId = ServiceConstants.ZERO_VAL;
		Long previousResponseLkpId = ServiceConstants.ZERO_VAL;
		Long previousLinkId = ServiceConstants.ZERO_VAL;
		for (PCSPAssessmentDto pcspAssessmentDtl : pcspAssessmentDtoList) {
			Long sectionLkpId = pcspAssessmentDtl.getIdPcspSctnLookup();
			Long questionLkpId = pcspAssessmentDtl.getIdPcspQstnLookup();
			Long responseLkpId = pcspAssessmentDtl.getIdPcspRspnsLookup();
			Long linkId = pcspAssessmentDtl.getIdPcspSctnQstnLinkLookup();
			if (sectionLkpId != previousSectionLkpId && !TypeConvUtil.isNullOrEmpty(sectionLkpId)) {
				pcspAssessmentSectionDto = new PCSPAssessmentSectionDto();
				pcspAssessmentSectionDto.setIdPcspSctnLookup(sectionLkpId);
				pcspAssessmentSectionDto.setSctnOrder(pcspAssessmentDtl.getSctnOrder());
				pcspAssessmentSectionDto.setSctn(pcspAssessmentDtl.getSctn());
				pcspAssessmentSectionDto.setSctnName(pcspAssessmentDtl.getSctnName());
				pcspAssessmentquestionDB = new PCSPAssessmentQuestionDto();
				pcspAssessmentquestionDB.setIdPcspQstnLookup(pcspAssessmentDtl.getIdPcspQstnLookup());
				pcspAssessmentquestionDB.setSctnQstnOrder(pcspAssessmentDtl.getSctnQstnOrder());
				pcspAssessmentquestionDB.setQstn(pcspAssessmentDtl.getQstn());
				pcspAssessmentquestionDB.setQstnName(pcspAssessmentDtl.getQstnName());
				pcspAssessmentquestionDB.setToolTip(pcspAssessmentDtl.getToolTip());
				pcspAssessmentquestionDB.setCdAnswr(pcspAssessmentDtl.getCdAnswr());
				pcspAssessmentquestionDB.setOtherDscrptn(pcspAssessmentDtl.getOtherDscrptn());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(pcspAssessmentDtl.getIdSctnQstnLookup());
				pcspAssessmentquestionDB.setIdPcspRspnsLookup(pcspAssessmentDtl.getIdPcspRspnsLookup());
				pcspAssessmentquestionDB.setIdPcspRspns(pcspAssessmentDtl.getIdPcspRspns());
				pcspAssessmentquestionDB.setCdResponse(pcspAssessmentDtl.getCdResponse());
				pcspAssessmentquestionDB.setIndCrmnlAbuseHist(pcspAssessmentDtl.getIndCrmnlAbuseHist());
				pcspAssessmentquestionDB.setDtLastUpdate(pcspAssessmentDtl.getDtLastUpdate());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(linkId);
				pcspAssessmentSectionDto.getQuestions().add(pcspAssessmentquestionDB);
				sections.add(pcspAssessmentSectionDto);
				if (!TypeConvUtil.isNullOrEmpty(responseLkpId) && responseLkpId != 0) {
					pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
					pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
					pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
					pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
					pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
				}
			} else if (questionLkpId != previousQuestionLkpId && !TypeConvUtil.isNullOrEmpty(questionLkpId)) {
				pcspAssessmentquestionDB = new PCSPAssessmentQuestionDto();
				pcspAssessmentquestionDB.setIdPcspQstnLookup(pcspAssessmentDtl.getIdPcspQstnLookup());
				pcspAssessmentquestionDB.setSctnQstnOrder(pcspAssessmentDtl.getSctnQstnOrder());
				pcspAssessmentquestionDB.setQstn(pcspAssessmentDtl.getQstn());
				pcspAssessmentquestionDB.setQstnName(pcspAssessmentDtl.getQstnName());
				pcspAssessmentquestionDB.setToolTip(pcspAssessmentDtl.getToolTip());
				pcspAssessmentquestionDB.setCdAnswr(pcspAssessmentDtl.getCdAnswr());
				pcspAssessmentquestionDB.setOtherDscrptn(pcspAssessmentDtl.getOtherDscrptn());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(pcspAssessmentDtl.getIdSctnQstnLookup());
				pcspAssessmentquestionDB.setIdPcspRspnsLookup(pcspAssessmentDtl.getIdPcspRspnsLookup());
				pcspAssessmentquestionDB.setIdPcspRspns(pcspAssessmentDtl.getIdPcspRspns());
				pcspAssessmentquestionDB.setCdResponse(pcspAssessmentDtl.getCdResponse());
				pcspAssessmentquestionDB.setIndCrmnlAbuseHist(pcspAssessmentDtl.getIndCrmnlAbuseHist());
				pcspAssessmentquestionDB.setDtLastUpdate(pcspAssessmentDtl.getDtLastUpdate());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(linkId);
				pcspAssessmentSectionDto.getQuestions().add(pcspAssessmentquestionDB);
				if ((responseLkpId != previousResponseLkpId) && (linkId != previousLinkId)
						&& !TypeConvUtil.isNullOrEmpty(responseLkpId) && !TypeConvUtil.isNullOrEmpty(linkId)) {
					pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
					pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
					pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
					pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
					pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
				}
			} else {
				pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
				pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
				pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
				pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
				pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
			}
			previousSectionLkpId = sectionLkpId;
			previousQuestionLkpId = questionLkpId;
			previousResponseLkpId = responseLkpId;
			previousLinkId = linkId;
		}
		Long supervisorId = pcspAssessmentDto.getIdPrsnSprvsr();
		Long personId = pcspAssessmentDto.getIdPrsnPd();
		PersonDto personDto = null;
		String supName = "";
		String pdPrsnName = "";
		if (!(TypeConvUtil.isNullOrEmpty(supervisorId)) && supervisorId > ServiceConstants.ZERO_VAL) {
			personDto = personDao.getPersonById(supervisorId);
			supName = personDto.getNmPersonFull();
		}
		if (!(TypeConvUtil.isNullOrEmpty(personId)) && personId > ServiceConstants.ZERO_VAL) {
			personDto = personDao.getPersonById(personId);
			pdPrsnName = personDto.getNmPersonFull();
		}
		pcspAssessmentDto.setPdFullName(pdPrsnName);
		pcspAssessmentDto.setSupFullName(supName);
		pcspAssessmentDto.setSavedChildrenAssessed(childrenSet);
		pcspAssessmentDto.setSavedOhmAssessed(ohmSet);
		pcspAssessmentDto.setSections(sections);
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		pcspAssessmentRes.setPcspAssessmentDto(pcspAssessmentDto);
		return pcspAssessmentRes;
	}

	/**
	 * This method will returns pcsp Assessment Data bean to build a New pcsp
	 * Assessment form in Impact. This would contain questions, answers related
	 * to latest pcsp Assessment version.
	 *
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes getQueryPageData(Long eventId) {
		Set<Integer> childrenSet = new HashSet<Integer>();
		Set<Integer> ohmSet = new HashSet<Integer>();
		PCSPAssessmentDto pcspAssessmentDto = pcspAssessmentDao.getPcspAssessmentDtls(eventId);
		List<PcspPrsnLinkDto> childSet = pcspAssessmentDao.getpcspPrsnLinkChild(pcspAssessmentDto.getIdPcspAsmnt());
		pcspAssessmentDto.setDtIntakeStart(pcspAssessmentDao.getIntakeStageStartDate(pcspAssessmentDto.getIdCase()));
		for (PcspPrsnLinkDto pcspPrsnLinkDto : childSet) {
			if (pcspPrsnLinkDto.getCdPcspPrsnType().equals(ServiceConstants.PCSPPRSN_TYPE10)) {
				childrenSet.add(Integer.valueOf(pcspPrsnLinkDto.getIdPerson().intValue()));
			}
		}
		List<PcspPrsnLinkDto> OhmSet = pcspAssessmentDao.getpcspPrsnLinkOhm(pcspAssessmentDto.getIdPrmryAsmnt());
		for (PcspPrsnLinkDto pcspPrsnLinkDto : OhmSet) {
			if (pcspPrsnLinkDto.getCdPcspPrsnType().equals(ServiceConstants.PCSPPRSN_TYPE30)) {
				ohmSet.add(Integer.valueOf(pcspPrsnLinkDto.getIdPerson().intValue()));
			}
		}
		List<PCSPAssessmentDto> pcspAssessmentDtoList = pcspAssessmentDao
				.getQueryPageData(pcspAssessmentDto.getIdPcspAsmntLookup());
		List<PCSPAssessmentSectionDto> sections = new ArrayList<>();
		PCSPAssessmentSectionDto pcspAssessmentSectionDto = null;
		PCSPAssessmentQuestionDto pcspAssessmentquestionDB = null;
		PCSPAssessmentResponseDto pcspAssessmentresponseDB = null;
		Long previousSectionLkpId = ServiceConstants.ZERO_VAL;
		Long previousQuestionLkpId = ServiceConstants.ZERO_VAL;
		Long previousResponseLkpId = ServiceConstants.ZERO_VAL;
		Long previousLinkId = ServiceConstants.ZERO_VAL;
		for (PCSPAssessmentDto pcspAssessmentDtl : pcspAssessmentDtoList) {
			Long sectionLkpId = pcspAssessmentDtl.getIdPcspSctnLookup();
			Long questionLkpId = pcspAssessmentDtl.getIdPcspQstnLookup();
			Long responseLkpId = pcspAssessmentDtl.getIdPcspRspnsLookup();
			Long linkId = pcspAssessmentDtl.getIdPcspSctnQstnLinkLookup();
			if (sectionLkpId != previousSectionLkpId && !TypeConvUtil.isNullOrEmpty(sectionLkpId)) {
				pcspAssessmentSectionDto = new PCSPAssessmentSectionDto();
				pcspAssessmentSectionDto.setIdPcspSctnLookup(sectionLkpId);
				pcspAssessmentSectionDto.setSctnOrder(pcspAssessmentDtl.getSctnOrder());
				pcspAssessmentSectionDto.setSctn(pcspAssessmentDtl.getSctn());
				pcspAssessmentSectionDto.setSctnName(pcspAssessmentDtl.getSctnName());
				pcspAssessmentquestionDB = new PCSPAssessmentQuestionDto();
				pcspAssessmentquestionDB.setIdPcspQstnLookup(pcspAssessmentDtl.getIdPcspQstnLookup());
				pcspAssessmentquestionDB.setSctnQstnOrder(pcspAssessmentDtl.getSctnQstnOrder());
				pcspAssessmentquestionDB.setQstn(pcspAssessmentDtl.getQstn());
				pcspAssessmentquestionDB.setQstnName(pcspAssessmentDtl.getQstnName());
				pcspAssessmentquestionDB.setToolTip(pcspAssessmentDtl.getToolTip());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(linkId);
				pcspAssessmentSectionDto.getQuestions().add(pcspAssessmentquestionDB);
				sections.add(pcspAssessmentSectionDto);
				if (!TypeConvUtil.isNullOrEmpty(responseLkpId) && responseLkpId != 0) {
					pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
					pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
					pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
					pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
					pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
				}
			} else if (questionLkpId != previousQuestionLkpId && !TypeConvUtil.isNullOrEmpty(questionLkpId)) {
				pcspAssessmentquestionDB = new PCSPAssessmentQuestionDto();
				pcspAssessmentquestionDB.setIdPcspQstnLookup(pcspAssessmentDtl.getIdPcspQstnLookup());
				pcspAssessmentquestionDB.setSctnQstnOrder(pcspAssessmentDtl.getSctnQstnOrder());
				pcspAssessmentquestionDB.setQstn(pcspAssessmentDtl.getQstn());
				pcspAssessmentquestionDB.setQstnName(pcspAssessmentDtl.getQstnName());
				pcspAssessmentquestionDB.setToolTip(pcspAssessmentDtl.getToolTip());
				pcspAssessmentquestionDB.setIdQuestionSectionLink(linkId);
				pcspAssessmentSectionDto.getQuestions().add(pcspAssessmentquestionDB);
				if ((responseLkpId != previousResponseLkpId) && (linkId != previousLinkId)
						&& !TypeConvUtil.isNullOrEmpty(responseLkpId) && !TypeConvUtil.isNullOrEmpty(linkId)) {
					pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
					pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
					pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
					pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
					pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
				}
			} else {
				pcspAssessmentresponseDB = new PCSPAssessmentResponseDto();
				pcspAssessmentresponseDB.setRspns(pcspAssessmentDtl.getRspns());
				pcspAssessmentresponseDB.setRspnsName(pcspAssessmentDtl.getRspnsName());
				pcspAssessmentresponseDB.setIdPcspRspnsLookup(responseLkpId);
				pcspAssessmentquestionDB.getResponses().add(pcspAssessmentresponseDB);
			}
			previousSectionLkpId = sectionLkpId;
			previousQuestionLkpId = questionLkpId;
			previousResponseLkpId = responseLkpId;
			previousLinkId = linkId;
		}
		pcspAssessmentDto.setSavedChildrenAssessed(childrenSet);
		pcspAssessmentDto.setSavedOhmAssessed(ohmSet);
		pcspAssessmentDto.setSections(sections);
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		pcspAssessmentRes.setPcspAssessmentDto(pcspAssessmentDto);
		return pcspAssessmentRes;
	}

	/**
	 * This Method is used to save the PCSPAssessment details to the PCSP_ASSMT
	 * table
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes savePcspAssessment(PCSPAssessmentReq pcspAssessmentReq) {
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		String retMsg = ServiceConstants.EMPTY_STRING;
		PCSPAssessmentDto pcspAssessmentDto = pcspAssessmentReq.getPcspAssessmentDto();
		if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent())) {
			Boolean isResponseExist = pcspAssessmentDao
					.getResponseSaved(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent());
			PCSPAssessmentDto pcspAsmntDto = pcspAssessmentDao
					.getPcspAssessmentDtls(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent());
			Timestamp existingLastUpdatedTime = new Timestamp(
					pcspAssessmentReq.getPcspAssessmentDto().getDtLastUpdate().getTime());
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String _12HourDate = _12HourSDF.format(pcspAsmntDto.getDtLastUpdate().getTime());
			Timestamp newLastUpdatedTime = Timestamp.valueOf(_12HourDate);
			if (existingLastUpdatedTime.equals(newLastUpdatedTime)
					|| existingLastUpdatedTime.after(newLastUpdatedTime)) {
				if (!isResponseExist) {
					Set<Integer> deleteChildAssessed = new HashSet<Integer>(
							pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed());
					if (!TypeConvUtil
							.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed())) {
						deleteChildAssessed
								.removeAll(pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed());
					}
					Set<Integer> saveChildAssessed = new HashSet<Integer>(
							pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed());
					if (!TypeConvUtil
							.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed())) {
						saveChildAssessed
								.removeAll(pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed());
					}
					// complete update children
					for (Integer idperson : deleteChildAssessed) {
						Long personId = idperson.longValue();
						pcspAssessmentDao.deletePcspPersonLink(
								pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt(), personId);
					}
					for (Integer idperson : saveChildAssessed) {
						Long personId = idperson.longValue();
						pcspAssessmentDao.savePcspPersonLink(pcspAssessmentReq.getPcspAssessmentDto(), personId,
								ServiceConstants.PCSPPRSN_010);
					}
					// table
					if (!TypeConvUtil
							.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSelectedOhmAssessed())) {
						Set<Integer> saveOhmAssessed = new HashSet<Integer>(
								pcspAssessmentReq.getPcspAssessmentDto().getSelectedOhmAssessed());
						if (!TypeConvUtil
								.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSelectedOhmAssessed())) {
							for (Integer idperson : saveOhmAssessed) {
								Long personId = idperson.longValue();
								pcspAssessmentDao.savePcspPersonLink(pcspAssessmentReq.getPcspAssessmentDto(), personId,
										ServiceConstants.PCSPPRSN_030);
							}
						}
					}
					// to add section 1-6 response
					List<PCSPAssessmentSectionDto> sections = pcspAssessmentReq.getPcspAssessmentDto().getSections();
					for (PCSPAssessmentSectionDto sectionDB : sections) {
						List<PCSPAssessmentQuestionDto> questions = sectionDB.getQuestions();
						if (null != questions && questions.size() > 0) {
							for (PCSPAssessmentQuestionDto questionDB : questions) {
								pcspAssessmentDao.savePcspResponse(pcspAssessmentReq.getPcspAssessmentDto(),
										questionDB);
							}
						}
					}
				}
				// saved
				if (isResponseExist && pcspAssessmentReq.getPcspAssessmentDto().isSection7Completed()
						&& !pcspAssessmentReq.getPcspAssessmentDto().isSection8Completed()) {
					List<PCSPAssessmentSectionDto> sections = pcspAssessmentReq.getPcspAssessmentDto().getSections();
					if (null != sections && sections.size() > 0) {
						for (PCSPAssessmentSectionDto sectionDB : sections) {
							List<PCSPAssessmentQuestionDto> questions = sectionDB.getQuestions();
							if (null != questions && questions.size() > 0
									&& sectionDB.getSctnName().equalsIgnoreCase(ServiceConstants.ONE)) {
								for (PCSPAssessmentQuestionDto questionDto : questions) {
									questionDto
											.setIdPcspAsmnt(pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt());
									questionDto.setLoggedInUser(
											pcspAssessmentReq.getPcspAssessmentDto().getLoggedInUser());
									pcspAssessmentDao.updatePcspRespCriminal(questionDto);
								}
							}
						}
					}
				}
				// updating responses
				if (isResponseExist && pcspAssessmentReq.getPcspAssessmentDto().isSection8Completed()) {
					List<PCSPAssessmentSectionDto> sections = pcspAssessmentReq.getPcspAssessmentDto().getSections();
					if (null != sections && sections.size() > 0) {
						for (PCSPAssessmentSectionDto sectionDB : sections) {
							List<PCSPAssessmentQuestionDto> questions = sectionDB.getQuestions();
							if (null != questions && questions.size() > 0
									&& sectionDB.getSctnName().equalsIgnoreCase(ServiceConstants.EIGHT)) {
								for (PCSPAssessmentQuestionDto questionDto : questions) {
									questionDto
											.setIdPcspAsmnt(pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt());
									questionDto.setLoggedInUser(
											pcspAssessmentReq.getPcspAssessmentDto().getLoggedInUser());
									pcspAssessmentDao.updatePcspResp(questionDto);
								}
							}
						}
					}
				}
				pcspAssessmentDao.updateAssessment(pcspAssessmentReq.getPcspAssessmentDto());
				retMsg = ServiceConstants.SUCCESS;
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				pcspAssessmentRes.setErrorDto(errorDto);
			}
		} else {
			Boolean placementExists = pcspAssessmentDao.getPlacementExists(
					pcspAssessmentReq.getPcspAssessmentDto().getIdPrsncrgvr(),
					pcspAssessmentReq.getPcspAssessmentDto().getIdCase());
			if (placementExists) {
				pcspAssessmentDto.setCdAsmntTyp(ServiceConstants.PCSPPRSN_020);
			} else {
				pcspAssessmentDto.setCdAsmntTyp(ServiceConstants.PCSPPRSN_010);
			}
			//PPM 82779, Assessment sections are removed and the new one's are called Decisions now.
			StageDto stageDto = stageDao.getStageById(pcspAssessmentReq.getPcspAssessmentDto().getIdStage());
			Date pcspEnhancementRelDate = codesDao.getAppRelDate(ServiceConstants.CRELDATE_2024_PCSP_IMPACT);
			if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent())) {
				if(DateUtils.isAfter(stageDto.getDtStageStart(),pcspEnhancementRelDate)){
					pcspAssessmentDto.setIdEvent(createAndReturnEventid(pcspAssessmentReq,
							ServiceConstants.CEVNTTYP_PCD));
				} else{
					pcspAssessmentDto.setIdEvent(createAndReturnEventid(pcspAssessmentReq,
							ServiceConstants.CEVNTTYP_ASM));
				}
			}
			PcspAsmntLkupDto pcspAsmntLkupDto = pcspAssessmentDao.getPcspAsmntLkup(pcspAssessmentDto);
			Long idPrimaryAsmt = pcspAssessmentDao.getPrimaryAsmtId(pcspAssessmentDto.getIdPrsncrgvr(),
					pcspAssessmentDto.getIdCase());
			pcspAssessmentDto.setIdPcspAsmnt(
					pcspAssessmentDao.savePcspAssessment(pcspAssessmentDto, pcspAsmntLkupDto, idPrimaryAsmt));
			Set<Integer> deleteChildAssessed = new HashSet<Integer>(
					pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed());
			if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed())) {
				deleteChildAssessed.removeAll(pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed());
			}
			Set<Integer> saveChildAssessed = new HashSet<Integer>(
					pcspAssessmentReq.getPcspAssessmentDto().getSelectedChildrenAssessed());
			if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed())) {
				saveChildAssessed.removeAll(pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed());
			}
			// complete update children
			for (Integer idperson : deleteChildAssessed) {
				Long personId = idperson.longValue();
				pcspAssessmentDao.deletePcspPersonLink(pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt(),
						personId);
			}
			for (Integer idperson : saveChildAssessed) {
				Long personId = idperson.longValue();
				pcspAssessmentDao.savePcspPersonLink(pcspAssessmentReq.getPcspAssessmentDto(), personId,
						ServiceConstants.PCSPPRSN_010);
			}
			retMsg = ServiceConstants.SUCCESS;
		}
		pcspAssessmentRes.setPcspAssessmentDto(pcspAssessmentDto);
		pcspAssessmentRes.setRtnMsg(retMsg);
		return pcspAssessmentRes;
	}

	private Long createAndReturnEventid(PCSPAssessmentReq pcspAssessmentReq, String eventType) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		if (ServiceConstants.CEVNTTYP_ASM.equalsIgnoreCase(eventType) && pcspAssessmentReq.getPcspAssessmentDto()
				.getCdAsmntTyp().equalsIgnoreCase(ServiceConstants.PCSPPRSN_020)) {
			postEventIPDto.setEventDescr(ServiceConstants.PCSP_ADDENDUM);
		}
		postEventIPDto.setCdTask(ServiceConstants.EMPTY_STRING);
		postEventIPDto.setIdPerson(pcspAssessmentReq.getUserProfileDto().getIdUser());
		postEventIPDto.setIdStage(pcspAssessmentReq.getPcspAssessmentDto().getIdStage());
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setUserId(pcspAssessmentReq.getUserProfileDto().getIdUserLogon());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		if (!(TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent()))
				&& pcspAssessmentReq.getPcspAssessmentDto().getIdEvent() > ServiceConstants.ZERO_VAL) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			postEventIPDto.setIdEvent(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent());
			postEventIPDto.setTsLastUpdate(pcspAssessmentReq.getPcspAssessmentDto().getEventDtLastUpdate());
			if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getDtEventOccurred())) {
				postEventIPDto.setDtEventOccurred(new Date());
			}
		} else {
			postEventIPDto.setDtEventOccurred(new Date());
		}
		postEventIPDto.setTsLastUpdate(pcspAssessmentReq.getPcspAssessmentDto().getEventDtLastUpdate());
		postEventIPDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
		//PPM 82779, Cleaned up the code and added Decision event type
		postEventIPDto.setCdEventType(eventType);
		if (ServiceConstants.CEVNTTYP_ASM.equalsIgnoreCase(eventType) && pcspAssessmentReq.getPcspAssessmentDto()
				.getCdAsmntTyp().equalsIgnoreCase(ServiceConstants.PCSPPRSN_010)) {
			postEventIPDto.setEventDescr(ServiceConstants.PCSP_ASSESSMENT);
		} else if (eventType.equalsIgnoreCase(ServiceConstants.CEVNTTYP_PCP)) {
			postEventIPDto.setEventDescr(ServiceConstants.PCSP_DTLREC);
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		} else if(eventType.equalsIgnoreCase(ServiceConstants.CEVNTTYP_PCD)) {
			postEventIPDto.setEventDescr(ServiceConstants.PCSP_DECISION);
		}
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * This Method will complete the Assessment Process
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes completeAssessment(PCSPAssessmentReq pcspAssessmentReq) {
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		PCSPAssessmentDto pcspAssessmentDto = pcspAssessmentReq.getPcspAssessmentDto();
		Timestamp existingLastUpdatedTime = new Timestamp(
				pcspAssessmentReq.getPcspAssessmentDto().getDtLastUpdate().getTime());
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PCSPAssessmentDto pcspAsmntDto = pcspAssessmentDao.getPcspAssessmentDtls(pcspAssessmentDto.getIdEvent());
		String _12HourDate = _12HourSDF.format(pcspAsmntDto.getDtLastUpdate().getTime());
		Timestamp newLastUpdatedTime = Timestamp.valueOf(_12HourDate);
		if (existingLastUpdatedTime.equals(newLastUpdatedTime) || existingLastUpdatedTime.after(newLastUpdatedTime)) {
			EventDto eventDto = new EventDto();
			eventDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			eventDto.setIdEvent(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent());
			eventDto.setDtLastUpdate(pcspAssessmentReq.getPcspAssessmentDto().getDtLastUpdate());
			eventDto.setIdPerson(pcspAssessmentReq.getPcspAssessmentDto().getLoggedInUser());
			pcspAssessmentDao.updateEvntStatusToComp(eventDto);
			pcspAssessmentDao.updateAssmtCompDate(pcspAssessmentReq.getPcspAssessmentDto());
			if (ServiceConstants.PCSPPRSN_010
					.equalsIgnoreCase(pcspAssessmentReq.getPcspAssessmentDto().getCdPlcmntDecsn())) {
				List<PCSPPersonDto> childrenList = new ArrayList<PCSPPersonDto>();
				childrenList = pcspAssessmentReq.getPcspAssessmentDto().getChildrenAssessedList();
				for (PCSPPersonDto pcspPersonDto : childrenList) {
					if (null != pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed()
							&& pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed().size() > 0) {
						if (pcspAssessmentReq.getPcspAssessmentDto().getSavedChildrenAssessed()
								.contains(pcspPersonDto.getIdPerson().intValue())) {
							Long eventId = createAndReturnEventid(pcspAssessmentReq,
									ServiceConstants.CEVNTTYP_PCP);
							pcspAssessmentDao.savePcspPlacmt(pcspAssessmentReq.getPcspAssessmentDto(), pcspPersonDto,
									eventId);
						}
					}
				}
			}
		} else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			pcspAssessmentRes.setErrorDto(errorDto);
		}
		pcspAssessmentRes.setPcspAssessmentDto(pcspAssessmentDto);
		return pcspAssessmentRes;
	}

	/**
	 *  This method will delete PCSP assessment details in PROC
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes @
	 */
	@Override
	@Transactional
	public PCSPAssessmentRes deletePcspAssmtDetails(PCSPAssessmentReq pcspAssessmentReq) {
		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();
		PCSPAssessmentDto pcspAssessmentDto = pcspAssessmentReq.getPcspAssessmentDto();
		pcspAssessmentRes
				.setRtnMsg(pcspAssessmentDao.deletePcspAssmntDetials(pcspAssessmentReq.getPcspAssessmentDto()));
		pcspAssessmentReq.setPcspAssessmentDto(pcspAssessmentDto);
		return pcspAssessmentRes;
	}

	/*
	 * This method checks to see if case open date is before Mar 16 release
	 *
	 * @param CommonHelperReq
	 *
	 * @return PCSPAssessmentRes
	 *
	 * @
	 */
	@Override
	public PCSPAssessmentRes checkLegacyPCSP(CommonHelperReq commonHelperReq) {
		PCSPAssessmentRes pcspRes = new PCSPAssessmentRes();
		CapsCaseDto capsCaseDto = null;
		Boolean isCheckLegacyPcsps = Boolean.FALSE;
		capsCaseDto = capsCaseDao.getCapsInfo(commonHelperReq.getIdCase());
		if (null != capsCaseDto) {
			Date dateCaseOpen = capsCaseDto.getDtCaseOpened();
			if (null != dateCaseOpen) {
				if (dateCaseOpen.before(ServiceConstants.MAR_2016_IMPACT)) {
					isCheckLegacyPcsps = Boolean.TRUE;
				}
			}
		}
		pcspRes.setIsCheckLegacyPCSP(isCheckLegacyPcsps);
		return pcspRes;
	}

	/*
	 * This method will fetch the the meth indicator flag
	 *
	 * @param idStage
	 *
	 * @return PCSPAssessmentRes -- String meth Ind
	 */
	@Override
	public PCSPAssessmentRes getMethIndicator(CommonHelperReq commonHelperReq) {
		PCSPAssessmentRes pcspRes = new PCSPAssessmentRes();
		pcspRes.setMethIndicator(pcspListPlacmtDao.getMethIndicator(commonHelperReq.getIdStage()));
		return pcspRes;
	}

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
	@Override
	public PriorStageInRevRes getPriorStageInReverseChronologicalOrder(CommonHelperReq commonHelperReq) {
		PriorStageInRevRes priorStageInRevRes = new PriorStageInRevRes();
		priorStageInRevRes = pcspListPlacmtDao.getPriorStageInReverseChronologicalOrder(commonHelperReq.getIdStage(),
				commonHelperReq.getStageType());
		return priorStageInRevRes;
	}

	/*
	 * This method checks to see there are open placements for input case and if
	 * they can be verified for the input stage and reason closed. Error message
	 * is returned if no open placements are allowed or if no verification
	 * records exists for allowable closed reasons.
	 *
	 * @param CommonHelperReq
	 *
	 * @response PCSPAssessmentRes
	 */
	@Override
	public PCSPAssessmentRes valPCSPPlcmt(CommonHelperReq commonHelperReq) {

		PCSPAssessmentRes pcspAssessmentRes = new PCSPAssessmentRes();

		if (null != commonHelperReq.getCdStageReasonClosed()) {
			if (pcspListPlacmtDao.hasOpenPCSPlacement(commonHelperReq.getIdCase())) {

				Boolean checkForPCSPVerifiedOrEnded = checkReasonClosed(commonHelperReq.getCdStage(),
						commonHelperReq.getCdStageReasonClosed(), commonHelperReq.getIdCase());

				if (!ObjectUtils.isEmpty(checkForPCSPVerifiedOrEnded)) {
					if (checkForPCSPVerifiedOrEnded) {
						if (pcspListPlacmtDao.hasOpenPCSPlacementNotVerify(commonHelperReq.getIdCase(),
								commonHelperReq.getIdStage())) {
							pcspAssessmentRes.setValPCSPPlcmtErr(ServiceConstants.MSG_PCSP_VAL_PLCMNT_VERFD_ENDED);
						}
					} else {
						pcspAssessmentRes.setValPCSPPlcmtErr(ServiceConstants.MSG_PCSP_VAL_PLCMNT_ENDED);
					}
				}
			}
		}
		return pcspAssessmentRes;
	}

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
	public int valPCSPPlcmt(String cdStage, Long idCase, Long idStage) {

		int errMsg = 0;

		if (StringUtils.isNotBlank(cdStage)
				&& (cdStage.equals(ServiceConstants.CSTAGES_FSU) || cdStage.equals(ServiceConstants.CSTAGES_FRE))) {

			// check if open placements exists for case
			if (pcspListPlacmtDao.hasOpenPCSPlacement(idCase)) {
				// check if other stages are open, if so allow placements to be
				// be verified
				if (pcspListPlacmtDao.hasOtherStagesOpen(idCase, cdStage)) {
					// check if all open placements have been verified in the
					// stage, if not return error message
					if (pcspListPlacmtDao.hasOpenPCSPlcmntNotVrfd(idCase, idStage))
						errMsg = ServiceConstants.MSG_PCSP_VAL_PLCMNT_VERFD_ENDED;
				} else
					errMsg = ServiceConstants.MSG_PCSP_VAL_PLCMNT_ENDED; // return
				// error
				// message
				// if
				// all
				// placements
				// have
				// to
				// be
				// ended
			}
		}

		return errMsg;
	}

	private Boolean checkReasonClosed(String cdStage, String cdStageReasonClosed, Long idCase) {

		Boolean isCheckReasonClosed = Boolean.FALSE;

		// For INV Stage - returns if any other stages open or not
		if (ServiceConstants.CSTAGES_INV.equals(cdStage)) {
			isCheckReasonClosed = pcspListPlacmtDao.hasOtherStagesOpen(idCase, cdStage);
		}

		// For A-R Stage
		// Closure Reason 'FPR/FBSS' - returns if any other stages open or not
		// Closure Reason 'INV - CPS Decision', 'INV - Removal', 'INV - Child Fatality Allegations', 'INV - Family Request' - return TRUE
		// Other Closure Reasons - return FALSE
		if (ServiceConstants.CSTAGES_AR.equals(cdStage)) {
			if (CodesConstant.CCLOSAR_060.equals(cdStageReasonClosed)) {
				isCheckReasonClosed = pcspListPlacmtDao.hasOtherStagesOpen(idCase, cdStage);
			} else if (Arrays.asList(CodesConstant.CCLOSAR_070, CodesConstant.CCLOSAR_080, CodesConstant.CCLOSAR_090,
					CodesConstant.CCLOSAR_100).contains(cdStageReasonClosed)) {
				isCheckReasonClosed = Boolean.TRUE;
			}
		}

		// For FPR Stage
		// Closure Reason 'Child removed from home/CVS' - returns TRUE
		// other Closure Reasons - returns FALSE if no other stages are open else returns NULL
		if (ServiceConstants.CSTAGES_FPR.equals(cdStage)) {
			if (CodesConstant.CCFPCLOS_07.equals(cdStageReasonClosed)) {
				isCheckReasonClosed = Boolean.TRUE;
			} else if (pcspListPlacmtDao.hasOtherStagesOpen(idCase, cdStage)) {
				isCheckReasonClosed = null;
			}
		}

		return isCheckReasonClosed;
	}

	/*
	 *
	 * This method checks to see if there are any open assessments for stage
	 *
	 * @param CommonHelperReq --IdStage
	 *
	 * @return CpsInvCnclsnRes
	 */
	@Override
	public CpsInvCnclsnRes hasOpenPCSPAsmntForStage(CommonHelperReq commonHelperReq) {
		CpsInvCnclsnRes cpsInvCnclsnRes = new CpsInvCnclsnRes();
		cpsInvCnclsnRes
				.setHasOpenPCSPAssessment(pcspListPlacmtDao.hasOpenPCSPAsmntForStage(commonHelperReq.getIdStage()));
		return cpsInvCnclsnRes;
	}

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 *
	 * @param CommonHelperReq -- stageId
	 *
	 * @return CpsInvCnclsnRes -- boolean response
	 */
	@Override
	public CpsInvCnclsnRes getContactPurposeStatus(CommonHelperReq commonHelperReq) {
		CpsInvCnclsnRes cpsInvCnclsnRes = new CpsInvCnclsnRes();
		cpsInvCnclsnRes
				.setContactPurposeStatus(pcspListPlacmtDao.getContactPurposeStatus(commonHelperReq.getIdStage()));
		return cpsInvCnclsnRes;
	}

	/*
	 * This method queries the database to find if the contact with the purpose
	 * of initiation already exist.
	 *
	 * @param CommonHelperReq -- stageId
	 *
	 * @return CpsInvCnclsnRes -- boolean response
	 */
	@Override
	public CpsInvCnclsnRes getCntctPurposeInitiationStatus(CommonHelperReq commonHelperReq) {
		CpsInvCnclsnRes cpsInvCnclsnRes = new CpsInvCnclsnRes();
		cpsInvCnclsnRes
				.setContactPurposeStatus(
						pcspListPlacmtDao.getCntctPurposeInitiationStatus(commonHelperReq.getIdStage()));
		return cpsInvCnclsnRes;
	}

	/*
	 * Check to see if the given event id has an entry on the approval event
	 * link table. If it does, the event has been submitted for approval at one
	 * point.(CPS Inst Conclusion Page)
	 */
	@Override
	public CpsInvCnclsnRes hasBeenSubmittedForApprovalCps(CommonHelperReq commonHelperReq) {
		CpsInvCnclsnRes cpsInvCnclsnRes = new CpsInvCnclsnRes();
		cpsInvCnclsnRes
				.setApprovalStatus(pcspListPlacmtDao.setHasBeenSubmittedForApprovalCps(commonHelperReq.getIdEvent()));
		return cpsInvCnclsnRes;
	}

	/**
	 *
	 * Method Name: getChildPCSPEndDate Method Description:Retrieve open PCSP of
	 * the particular child
	 *
	 * @param pcspValueDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<PcspValueDto> getChildPCSPEndDate(PcspValueDto pcspValueDto) {
		List<PcspValueDto> pcspValueList = new ArrayList<PcspValueDto>();
		pcspValueList = pcspListPlacmtDao.getChildPCSPEndDate(pcspValueDto.getIdPerson());
		return pcspValueList;
	}

	@Override
	public List<PcspDto> displayPCSPList(Long idCase, String cdStage) {
		return pcspListPlacmtDao.displayPCSPList(idCase, cdStage);
	}

	/**
	 *
	 * Method Name: getPersonDetails Method Description: Retrieve PCSP child
	 * name and cargiver name
	 *
	 * @param idStage
	 * @return List<PcspDto>
	 */

	public List<PcspDto> getPersonDetails(Long idStage) {
		return pcspDao.getPersonDetails(idStage);
	}

}
