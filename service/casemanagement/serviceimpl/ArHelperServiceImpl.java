package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.common.domain.CpsArCnclsnDetail;
import us.tx.state.dfps.common.dto.CpsArInvCnclsnDto;
import us.tx.state.dfps.common.dto.ServiceReferralDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casemanagement.service.ArHelperService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ArInvCnclsnReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.ArHelperRes;
import us.tx.state.dfps.service.common.response.ArInvCnclsnRes;
import us.tx.state.dfps.service.common.response.ArValidationRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * IMPACT MORDENIZATION PHASE II Class Description: Service Class for
 * Alternative Response Conclusion
 *
 */
@Service
@Transactional
public class ArHelperServiceImpl implements ArHelperService {

	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	PersonListByStageDao personListByStageDao;
	
	@Autowired
	CpsInvReportDao cpsInvReportDao;

	public ArHelperRes isRiskIndicated(Long idCase) {
		String risk = ServiceConstants.AR_NO;
		Long count = arHelperDao.isRiskIndicated(idCase);
		if (count > 0) {
			risk = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(risk);
		;
		return arHelperRes;
	}

	public ArHelperRes hasBeenSubmittedForApproval(Long ulIdEvent) {
		boolean bSubmittedForApproval = false;
		Long count = arHelperDao.hasBeenSubmittedForApproval(ulIdEvent);
		if (count > 0) {
			bSubmittedForApproval = true;
		} else {
			bSubmittedForApproval = false;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setBooleanVal(bSubmittedForApproval);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isLegalActionExists(Long idCase) {
		String isLegalActionExists = ServiceConstants.AR_NO;
		Long count = arHelperDao.isLegalActionExists(idCase);
		if (count > 0) {
			isLegalActionExists = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isLegalActionExists);
		return arHelperRes;
	}
	
	@Override
	public ArHelperRes getContactPurposeFamAssmtRScheduling(Long idCase) {
		Long count = arHelperDao.getContactPurposeFamAssmtRScheduling(idCase);
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setLongVal(count);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isPendingDayCareApprvalExists(Long idStage) {
		String isPendingDayCareApprvalExists = ServiceConstants.AR_NO;
		Long count = arHelperDao.isPendingDayCareApprvalExists(idStage);
		if (count > 0) {
			isPendingDayCareApprvalExists = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isPendingDayCareApprvalExists);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isPendingSeviceAuthApprvalExists(Long idStage) {
		String isPendingSeviceAuthApprvalExists = ServiceConstants.AR_NO;
		Long count = arHelperDao.isPendingSeviceAuthApprvalExists(idStage);
		if (count > 0) {
			isPendingSeviceAuthApprvalExists = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isPendingSeviceAuthApprvalExists);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isInitialSftyAssmntRejectedOrPending(Long idStage) {
		String isInitialSftyAssmntRejectedOrPending = ServiceConstants.AR_NO;
		Long count = arHelperDao.isInitialSftyAssmntRejectedOrPending(idStage);
		if (count > 0) {
			isInitialSftyAssmntRejectedOrPending = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isInitialSftyAssmntRejectedOrPending);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isExtensionRequestPending(Long idStage) {
		String isExtensionRequestPending = ServiceConstants.AR_NO;
		Long count = arHelperDao.isExtensionRequestPending(idStage);
		if (count > 0) {
			isExtensionRequestPending = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isExtensionRequestPending);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isMoreReferenceChildExists(Long idStage) {
		String isMoreReferenceChildExists = ServiceConstants.AR_NO;
		Long count = arHelperDao.isMoreReferenceChildExists(idStage);
		if (count > 1) {
			isMoreReferenceChildExists = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isMoreReferenceChildExists);
		return arHelperRes;
	}

	@Override
	public ArHelperRes getServiceReferral(Long idStage) {
		String risk = ServiceConstants.AR_NO;
		Long count = arHelperDao.getServiceReferral(idStage);
		if (count > 0) {
			risk = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(risk);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isLegalActionPending(Long idStage) {
		String isLegalActionPending = ServiceConstants.AR_NO;
		String eventStatus = arHelperDao.isLegalActionPending(idStage);
		if (!TypeConvUtil.isNullOrEmpty(eventStatus)) {
			if (eventStatus.equalsIgnoreCase(ServiceConstants.AR_ES_010)) {
				isLegalActionPending = ServiceConstants.AR_YES;
			}
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isLegalActionPending);
		return arHelperRes;
	}

	@Override
	public ArHelperRes isPendingIntlSafetyAssmtApprvalExists(Long idStage) {
		String isPendingIntlSafetyAssmtApprvalExists = ServiceConstants.AR_NO;
		Long count = arHelperDao.isPendingIntlSafetyAssmtApprvalExists(idStage);
		if (count > 0) {
			isPendingIntlSafetyAssmtApprvalExists = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(isPendingIntlSafetyAssmtApprvalExists);
		return arHelperRes;
	}

	@Override
	public ArHelperRes getApprovalEvents(Long idEvent) {
		String risk = ServiceConstants.AR_NO;
		Long count = arHelperDao.getApprovalEvents(idEvent);
		if (count > 0) {
			risk = ServiceConstants.AR_YES;
		}
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(risk);
		return arHelperRes;
	}

	@Override
	public ArHelperRes getEventByStageAndEventType(Long idStage, String evntType) {
		Long event = arHelperDao.getEventByStageAndEventType(idStage, evntType);
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setLongVal(event);
		return arHelperRes;
	}

	@Override
	public PersonDtlRes getPersonCharacteristics(Long idStage) {
		PersonDtlRes personDtlRes = new PersonDtlRes();
		personDtlRes.setPersonDtoList(arHelperDao.getPersonCharacteristics(idStage));
		return personDtlRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.casemanagement.service.ArHelperService#
	 * getOutcome(java.lang.Long)
	 */
	@Override
	public ArHelperRes getOutcome(Long idStage) {
		ArHelperRes arHelperRes = new ArHelperRes();
		Long outCome = 0l;
		List<ServiceReferralDto> serviceReferralDto = new ArrayList<>();
		try {
			serviceReferralDto = arHelperDao.getServiceReferrals(idStage);
			if (!ObjectUtils.isEmpty(serviceReferralDto) && serviceReferralDto.size() > 1) {
				for (ServiceReferralDto serviceReferralDtoObject : serviceReferralDto) {
					if (null == serviceReferralDtoObject.getCdFinalOutcome()) {
						outCome = 1l;
						break;
					}
				}
			} else if (!ObjectUtils.isEmpty(serviceReferralDto) && serviceReferralDto.size() == 1) {
				for (ServiceReferralDto serviceReferralDtoObject : serviceReferralDto) {
					if (!StringUtils.isEmpty(serviceReferralDtoObject.getIndrfrl())
							&& "Y".equals(serviceReferralDtoObject.getIndrfrl())) {
						outCome = 0l;
					} else if (StringUtils.isEmpty(serviceReferralDtoObject.getCdFinalOutcome())) {
						outCome = 1l;
					}
				}
			} else if (null != serviceReferralDto && serviceReferralDto.size() == 0) {
				outCome = 2l;
			}
		} finally {
			arHelperRes.setLongVal(outCome);
		}
		return arHelperRes;
	}

	public ArHelperRes getIdAssessmentHousehold(Long idEvent) {
		Long idHousehold = arHelperDao.getIdAssessmentHousehold(idEvent);
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setLongVal(idHousehold);
		return arHelperRes;
	}

	public ArHelperRes updIdAssessmentHousehold(CommonHelperReq commonHelperReq) {
		arHelperDao.updIdAssessmentHousehold(commonHelperReq.getIdEvent(), commonHelperReq.getPrimaryKey());
		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setStringVal(ServiceConstants.SAVE_SUCCESS);
		return arHelperRes;
	}

	/**
	 * Method Description: Method to get ArConclusionDetail characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException @
	 */
	public ArInvCnclsnRes getArInvConclusionDetail(CommonHelperReq commonHelperReq) {
		CpsArInvCnclsnDto cpsArInvCnclsnDto = new CpsArInvCnclsnDto();
		cpsArInvCnclsnDto = arHelperDao.getArinvCnclsnDetail(commonHelperReq);
		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		arInvCnclsnRes.setCpsArInvCnclsnDto(cpsArInvCnclsnDto);
		return arInvCnclsnRes;
	}

	/**
	 * Method Description: Method to save ArConclusionDetail characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException @
	 */
	public ArInvCnclsnRes saveArInvConclusionDetail(ArInvCnclsnReq arInvCnclsnReq) {
		CpsArInvCnclsnDto cpsArInvCnclsnDto = arInvCnclsnReq.getCpsArInvCnclsnDto();
		CpsArCnclsnDetail cpsArCnclsnDetail = new CpsArCnclsnDetail();
		BeanUtils.copyProperties(cpsArInvCnclsnDto, cpsArCnclsnDetail);
		cpsArCnclsnDetail.setTxtImmAction(cpsArInvCnclsnDto.getImmAction());
		cpsArCnclsnDetail.setTxtChildSafety(cpsArInvCnclsnDto.getChildSafety());
		cpsArCnclsnDetail.setTxtFinalAssmtCnclsn(cpsArInvCnclsnDto.getFinalAssmtCnclsn());
		cpsArCnclsnDetail.setTxtRiskFindReasons(cpsArInvCnclsnDto.getRiskFindReasons());
		cpsArCnclsnDetail.setTxtRsnArClsd(cpsArInvCnclsnDto.getRsnArClsd());
		cpsArCnclsnDetail.setTxtRsnArOpnSrvcs(cpsArInvCnclsnDto.getRsnArOpnSrvcs());
		if (!ObjectUtils.isEmpty(cpsArCnclsnDetail.getIdAssessmentHouseholdLink())
				&& 0l == cpsArCnclsnDetail.getIdAssessmentHouseholdLink()) {
			cpsArCnclsnDetail.setIdAssessmentHouseholdLink(null);
		}
		cpsArInvCnclsnDto = arHelperDao.saveArinvCnclsnDetail(cpsArCnclsnDetail);

		// Commenting the Below Code block for Defect 4787. Since the
		// Notification Event should be created on AR approval not on AR
		// Submission.
		/*
		 * if (arInvCnclsnReq.getcReqFuncCd().equalsIgnoreCase(ServiceConstants.
		 * REQ_FUNC_CD_SEARCH)) { PostEventIPDto postEventIPDto = new PostEventIPDto();
		 * ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		 * postEventIPDto.setEventDescr(ServiceConstants.COURTESY_DTL_REC);
		 * postEventIPDto.setCdTask(ServiceConstants.EMPTY_STRING);
		 * postEventIPDto.setIdPerson(arInvCnclsnReq.getCpsArInvCnclsnDto().
		 * getIdCreatedPerson());
		 * postEventIPDto.setIdStage(arInvCnclsnReq.getCpsArInvCnclsnDto().
		 * getIdStage()); postEventIPDto.setDtEventOccurred(new Date());
		 * postEventIPDto.setSzUserId(arInvCnclsnReq.getSzUserId());
		 * archInputDto.setcReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		 * postEventIPDto.setTsLastUpdate(new Date());
		 * postEventIPDto.setCdEventType("NOT"); postEventIPDto.setDtEventOccurred(new
		 * Date()); String stageName =
		 * arInvCnclsnReq.getCpsArInvCnclsnDto().getNmStage(); String caseWorker =
		 * arInvCnclsnReq.getCpsArInvCnclsnDto().getNmCaseworker(); String Desc =
		 * ServiceConstants.AR_CLOSE_NOT_DESC.concat(stageName).concat(
		 * ServiceConstants.COMMA).concat(arInvCnclsnReq.getCpsArInvCnclsnDto().
		 * getIdCase().toString()).concat("entered by ").concat(caseWorker);
		 * postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_PROC);
		 * postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_COMP); PostEventOPDto
		 * postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto,
		 * archInputDto); }
		 */
		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		arInvCnclsnRes.setCpsArInvCnclsnDto(cpsArInvCnclsnDto);
		return arInvCnclsnRes;
	}

	/**
	 * Method Description: Method to get Validations for AR conclusion page.
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException @
	 */
	public ArValidationRes getArInvConclusionValidation(CommonHelperReq commonHelperReq) {
		ArValidationRes arValidationRes = new ArValidationRes();
		arValidationRes.setrA1(Boolean.FALSE);
		arValidationRes.setrA2(Boolean.FALSE);
		arValidationRes.setrA3(Boolean.FALSE);
		arValidationRes.setrA4(Boolean.FALSE);
		arValidationRes.setrA5(Boolean.FALSE);
		arValidationRes.setrA6(Boolean.FALSE);
		arValidationRes.setrA7(Boolean.FALSE);
		arValidationRes.setrA8(Boolean.FALSE);
		arValidationRes.setrA9(Boolean.FALSE);
		arValidationRes.setrA10(Boolean.FALSE);
		String Q9Answer = ServiceConstants.N;
		String Q10Answer = ServiceConstants.N;
		Long ifRaComplete = 0l;
		String decision = ServiceConstants.BLANK;
		Long count = 0l;
		if (!ObjectUtils.isEmpty(commonHelperReq.getEntityID())) {
			Q9Answer = arHelperDao.isSaQuestionAnswered(commonHelperReq.getEntityID(), 41l);
			Q10Answer = arHelperDao.isSaQuestionAnswered(commonHelperReq.getEntityID(), 42l);
			ifRaComplete = arHelperDao.isSdmRaComplete(commonHelperReq.getEntityID());
			decision = arHelperDao.getCdSafetyDecn(commonHelperReq.getEntityID());
			count = arHelperDao.isInitialSafetyassessmentComplete(commonHelperReq.getIdEvent());
		}
		arHelperDao.isFsuStageOpened(commonHelperReq.getIdCase());
		Long pcspCompletecount = arHelperDao.isPcspComplete(commonHelperReq.getIdStage());
		Long caseClosurePcspCount = arHelperDao.isPcspCompletewith060(commonHelperReq.getIdStage());
		String pcspProc = arHelperDao.isPcspProc(commonHelperReq.getIdStage());
		Long sdmSaProcCount = arHelperDao.isSdmSaProc(commonHelperReq.getIdStage());
		// Defect 11484 - do not check the entity id while validating the count
		if (count == 0) {
			arValidationRes.setrA1(Boolean.TRUE);
		}
		if (ServiceConstants.Y.equalsIgnoreCase(Q10Answer)) {
			arValidationRes.setrA2(Boolean.TRUE);
			arValidationRes.setrA3(Boolean.TRUE);
		}
		if (ServiceConstants.Y.equalsIgnoreCase(Q9Answer) && pcspCompletecount == 0) {
			arValidationRes.setrA4(Boolean.TRUE);
		}
		if (ServiceConstants.Y.equalsIgnoreCase(Q9Answer) && pcspCompletecount > 0 && caseClosurePcspCount == 0) {
			arValidationRes.setrA5(Boolean.TRUE);
		}
		if (ServiceConstants.Y != Q9Answer && !ObjectUtils.isEmpty(commonHelperReq.getEntityID())
				&& ServiceConstants.SDM_SAFEWITHPLAN.equalsIgnoreCase(decision)) {
			arValidationRes.setrA6(Boolean.TRUE);
		}
		if (ifRaComplete == 0) {
			arValidationRes.setrA8(Boolean.TRUE);
		}
		if (ServiceConstants.Y.equalsIgnoreCase(pcspProc)) {
			arValidationRes.setrA9(Boolean.TRUE);
		}
		if (sdmSaProcCount > 0) {
			arValidationRes.setrA10(Boolean.TRUE);
		}
		return arValidationRes;
	}

	/**
	 * Method Description: Method to fetch print notification
	 * 
	 * @param idEvent
	 * @return Boolean
	 * 
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CommonHelperRes getPrintNotificationFlag(CommonHelperReq commonHelperReq) {
		CommonHelperRes res = new CommonHelperRes();
		res.setResult(arHelperDao.getPrintNotificationFlag(commonHelperReq.getIdEvent()));
		return res;
	}

	/**
	 * Method Description: Method to fetch getConclusionEventId
	 * 
	 * @param idEvent
	 * @return Boolean
	 * @throws @
	 * 
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CommonHelperRes getConclusionEventId(CommonHelperReq commonHelperReq) {
		CommonHelperRes res = new CommonHelperRes();
		res.setIdEvent((arHelperDao.getConclusionEventId(commonHelperReq.getIdEvent(), commonHelperReq.getSzCdTask())));
		return res;
	}

	/**
	 * Method Name: checkCrimHistPerson Method Description: This method returns true
	 * if the criminal history action is null, else will return false
	 * 
	 * @param idPerson
	 * @return boolean @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean checkCrimHistPerson(long idPerson) {

		return arHelperDao.checkCrimHistPerson(idPerson);
	}

	/**
	 * Method Name: selectARConclusion Method Description: get the AR Conclusion
	 * Detail for the current Stage.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ArInvCnclsnDto selectARConclusion(long idStage) {

		return arHelperDao.selectARConclusion(idStage);
	}

	/**
	 * Method Name: updateARConclusion Method Description: update the AR Conclusion
	 * and AR EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateARConclusion(ArInvCnclsnDto arInvCnclsnDto) {

		arHelperDao.updateARConclusion(arInvCnclsnDto);
	}

	/**
	 * Method Name: getArEaEligibilityDetails Method Description: retrieves the AR
	 * EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */

	public ArInvCnclsnDto getArEaEligibilityDetails(ArInvCnclsnDto arInvCnclsnDto) {
		//[artf163982] ALM#15522 Some A-R cases still not displaying questions and correct determination in Alternative Response Report
		if(null != arInvCnclsnDto && arInvCnclsnDto.getIdEvent() > 0){
			arInvCnclsnDto.setArEaEligibilityDtoList(arHelperDao.getArEaEligibilityDetails(arInvCnclsnDto));
		}
		return arInvCnclsnDto;

	}

	/**
	 * Method Name: invalidateApprovalStatus Method Description: Retrieve the
	 * Approval Id's and Delete the To-Do/Approval Event
	 * 
	 * @param ArInvCnclsnDto
	 */
	@Override
	public void invalidateApprovalStatus(ArInvCnclsnDto arInvCnclsnDto) {
		arHelperDao.invalidateApprovalStatus(arInvCnclsnDto);

	}

	/**
	 * 
	 * Method Name: validationForSexualVctmizationQues Method Description: this
	 * method check if all the PRN less then 18 have answered Sexual Vctmization
	 * Question
	 * 
	 * @param Long
	 * @return boolean
	 */

	public boolean validationForSexualVctmizationQues(Long stageId) {
		int personage = ServiceConstants.Zero_INT;
		Date dtDtSystemDate = new Date();
		if (stageId != null) {
			List<ContactPrincipalsCollateralsDto> listPpl = personListByStageDao.getPRNPersonDetailsForStage(stageId,
					ServiceConstants.PRINCIPAL);
			if (!ObjectUtils.isEmpty(listPpl)) {
				
				for(ContactPrincipalsCollateralsDto person:listPpl) {
					personage = DateUtils.calculatePersonsAgeInYears(person.getDtPersonBirth(),dtDtSystemDate);
					if( personage < ServiceConstants.MIN_PG_AGE) {
						boolean answered =
								personListByStageDao.getIndChildSxVctmzinHistory(person.getIdPerson());
						if(!answered) { 
							return false; } 
						} 
					}

			}

		}

		return true;
	}

	/**
	 * Method Name: isFbssReferralApproved
	 * Method Desc: Checks with the FBSS Referral is approved in the case if idHouseHoldPerson not null then
	 * selected for house hold else for any house hold
	 *
	 * @param idHouseHoldPerson
	 * @param idCase
	 * @return
	 */
	@Override
	public List<EventDto> isFbssReferralApproved(Long idCase, List<String> taskCodes) {
		return arHelperDao.isFbssReferralApproved( idCase,taskCodes);
	}
}
