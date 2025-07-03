package us.tx.state.dfps.service.investigation.serviceimpl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.FacilityInjury;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AllegationAUDReq;
import us.tx.state.dfps.service.common.request.GetFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.common.response.GetFacilAllegDetailRes;
import us.tx.state.dfps.service.common.response.UpdtFacilAllegDetailRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.AllegationBusinessException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDetailDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.investigation.dto.FacilInvstFacilDto;
import us.tx.state.dfps.service.investigation.service.AllegtnService;
import us.tx.state.dfps.service.investigation.service.FacilAllgDtlService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CINV07S,CINV08S,CINV10S Class Description: This class is to
 * retrieves,saves,updates,multi update Facility Allegation Detail page.
 */
@Service
@Transactional
public class FacilAllgDtlServiceImpl implements FacilAllgDtlService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FacilAllgDtlDao facilAllgDtlDao;

	@Autowired
	EventStagePersonLinkInsUpdDao objCcmn87dDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	StageDao stageDao;

	@Autowired
	CaseMergeCustomDao caseMergeCustomDao;

	@Autowired
	AllegtnService allegtnService;

	@Autowired
	AllegtnDao allegtnDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	@Autowired
	CapsResourceDao capsResourceDao;

	private static final long ALLEGATION_BUSINESS_EXCEPTION = 4107l;

	/**
	 * 
	 * Method Description: Populates the Allegation List for Facility
	 * Allegations. legacy DAM name - CINV70S,CINV08D,CINVF8D,CSEC54D,CCMNB5D
	 * 
	 * @param idAllegation
	 * @return GetFacilAllegDetailRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public GetFacilAllegDetailRes getallegtnlist(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		GetFacilAllegDetailRes res = new GetFacilAllegDetailRes();
		FacilAllegDetailDto facilDet = new FacilAllegDetailDto();
		if (!ObjectUtils.isEmpty(getFacilAllegDetailReq.getIdAllegation())) {
			facilDet = facilAllgDtlDao.getallegtnlist(getFacilAllegDetailReq);
			res.setFacilAllegInjuryList(facilAllgDtlDao.getInjuryDtl(getFacilAllegDetailReq));
		}
		facilDet.setDtIncomingCall(facilAllgDtlDao.getdtIncCall(getFacilAllegDetailReq));

		List<AllegtnPrsnDto> allegtnPrsnList = facilAllgDtlDao.getFacilAllegDtl(getFacilAllegDetailReq);
		if (allegtnPrsnList.isEmpty()) {
			res.setMessage(ServiceConstants.MSG_NO_ROWS_RETURN);
		} else {
			res.setAllegtnPrsnDtoList(allegtnPrsnList);
		}
		// CCMN87D
		EventStagePersonLinkInsUpdInDto pCCMN87DInputRec = new EventStagePersonLinkInsUpdInDto();
		pCCMN87DInputRec.setIdStage(getFacilAllegDetailReq.getIdStage());
		pCCMN87DInputRec.setCdEventType(ServiceConstants.CPGRMS_CCL);
		pCCMN87DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos = objCcmn87dDao.getEventAndStatusDtls(pCCMN87DInputRec);
		if (!ccmn87doDtos.isEmpty()) {
			facilDet.setCdEventStatus(ccmn87doDtos.get(0).getCdEventStatus());
		}
		if (!ObjectUtils.isEmpty(getFacilAllegDetailReq.getIdStage()) && getFacilAllegDetailReq.getIdStage() != 0L) {
			res.setFacilInvstFacilList(facilAllgDtlDao.getFacilitysInvCnclsnList(getFacilAllegDetailReq));
		}

		if (!ObjectUtils.isEmpty(getFacilAllegDetailReq.getIdAllegation())
				&& getFacilAllegDetailReq.getIdAllegation() != 0L) {
			FacilInvstFacilDto facility = facilAllgDtlDao.getFacilityInvCnclsn(getFacilAllegDetailReq);
			if (!ObjectUtils.isEmpty(facility)) {
				facilDet.setIdFacilAllegResource(facility.getIdFacilResource());
				facilDet.setfacilAllegResourceId(facility.getIdFacilResource());
				facilDet.setFacilAllegMHMR(facility.getCdMhmrCompCode());
			}
		}
		//artf275584 : Facility Allegation - Unable to Save INV Allegations
		if(!ObjectUtils.isEmpty(facilDet) && !ObjectUtils.isEmpty(facilDet.getIdAllegedPerpetrator())){
			facilDet.setOpenInvAfcAllegedPerpetratorId(facilDet.getIdAllegedPerpetrator());
		}

		res.setFacilAllegDetailDto(facilDet);
		return res;
	}

	/**
	 * This method is to update facility allegation details legacy service name
	 * - CINV08S
	 * 
	 * @param updtFacilAllegDetailReq
	 * @return UpdtFacilAllegDetailRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public UpdtFacilAllegDetailRes updateFacilAlleg(UpdtFacilAllegDetailReq updtFacilAllegDetailReq) {
		FacilAllegDetailDto facilAllegDetailDto = updtFacilAllegDetailReq.getFacilAllegDetailDto();
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(facilAllegDetailDto.getCdTask());
		inCheckStageEventStatusDto.setIdStage(facilAllegDetailDto.getIdStage());
		inCheckStageEventStatusDto.setCdReqFunction(updtFacilAllegDetailReq.getReqFuncCd());
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdTodo())) {
			inCheckStageEventStatusDto.setIdStage(facilAllegDetailDto.getIdTodo());
		}
		checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		if (ServiceConstants.CASE_MERGED_IN_ERROR.equals(facilAllegDetailDto.getCdAllegDisposition())) {

			StageDto stage = stageDao.getStageById(facilAllegDetailDto.getIdStage());
			List<CaseMergeDetailDto> caseMergeList = caseMergeCustomDao.getCaseMergeByIdCaseMergeTo(stage.getIdCase());

			if (!ObjectUtils.isEmpty(caseMergeList)
					&& caseMergeList
							.size() < 1
					&& caseMergeList.stream().filter(o -> !ObjectUtils.isEmpty(o.getIndCaseMergeInv())
							&& ServiceConstants.STRING_IND_Y.equals(o.getIndCaseMergeInv())).count() > 0)
				throw new AllegationBusinessException(ALLEGATION_BUSINESS_EXCEPTION);

		}
		AllegationDetailDto allegationDetail = populateAllegationDetailDto(facilAllegDetailDto);
		UpdtFacilAllegDetailRes updtFacilAllegDetailRes = new UpdtFacilAllegDetailRes();
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(updtFacilAllegDetailReq.getReqFuncCd())) {

			if (!ServiceConstants.CSTAGES_INT.equals(facilAllegDetailDto.getCdAllegIncidentStage())) {
				facilAllgDtlDao.callBlankOverallDispositionFAC(facilAllegDetailDto.getIdStage());

				facilAllgDtlDao.deleteFacilAlleg(facilAllegDetailDto.getIdAllegation());
				allegtnService.updtVictimPerpRoles(populateAllegationDetailDto(facilAllegDetailDto));
			} else {
				if (allegtnDao.findDuplicates(allegationDetail, updtFacilAllegDetailReq.getReqFuncCd())) {
					AllegationAUDReq allegationAUDReq = new AllegationAUDReq();
					allegationAUDReq.setSzCdStageProgram(updtFacilAllegDetailReq.getCdStageProgram());
					allegtnService.handleDeletion(allegationAUDReq);
				} else
					throw new AllegationBusinessException(4060l);
			}
		} else {
			if (allegtnDao.findDuplicates(allegationDetail, updtFacilAllegDetailReq.getReqFuncCd())) {
				throw new AllegationBusinessException(9011l);
			} else {
				facilAllgDtlDao.callBlankOverallDispositionFAC(facilAllegDetailDto.getIdStage());
				stagePersonLinkDao.updateStagePersonLink(allegationDetail);
				Long idAllegation = allegtnDao.updateAllegation(pupulateAllegation(updtFacilAllegDetailReq),
						updtFacilAllegDetailReq.getReqFuncCd(), true);
				facilAllegDetailDto.setIdAllegation(idAllegation);
				updtFacilAllegwihInjury(updtFacilAllegDetailReq);
				updtFacilAllegDetailRes.setIdAllegation(idAllegation);
				allegationDetail.setIdAllegation(0l);
				allegtnService.updtVictimPerpRoles(allegationDetail);

			}
		}
		if /* An ID EVENT has been passed in, and the status is PEND */
		(!TypeConvUtil.isNullOrEmpty(updtFacilAllegDetailReq.getIdEvent())) {
			// invalidate approval
			EventDto eventDto = eventDao.getEventByid(updtFacilAllegDetailReq.getIdEvent());
			if (ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventDto.getCdEventStatus())) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(updtFacilAllegDetailReq.getIdEvent());
				approvalService.callCcmn05uService(approvalCommonInDto);
			}
		}
		return updtFacilAllegDetailRes;
	}

	/**
	 * Method Name: updtFacilAllegwihInjury Method Description: Used to update
	 * the facil alleg and injury details
	 * 
	 * @param updtFacilAllegDetailReq
	 */
	private void updtFacilAllegwihInjury(UpdtFacilAllegDetailReq updtFacilAllegDetailReq) {
		FacilAllegDetailDto facilAllegDetailDto = updtFacilAllegDetailReq.getFacilAllegDetailDto();
		List<FacilAllegInjuryDto> facilAllegInjuryDtoList = updtFacilAllegDetailReq.getFacilAllegInjuryList();
		FacilAlleg facilAlleg = null;
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(updtFacilAllegDetailReq.getReqFuncCd())) {
			facilAlleg = new FacilAlleg();
			facilAlleg.setIdAllegation(facilAllegDetailDto.getIdAllegation());
		} else
			facilAlleg = facilAllgDtlDao.loadFacilAllegation(facilAllegDetailDto.getIdAllegation());
		if (!ObjectUtils.isEmpty(facilAlleg) &&
				  !ObjectUtils.isEmpty(facilAlleg.getDtLastUpdate()) &&
				  !ObjectUtils.isEmpty(facilAllegDetailDto.getDtLastUpdate()) &&
				  facilAllegDetailDto.getDtLastUpdate()
				  .compareTo(facilAlleg.getDtLastUpdate()) != 0) {
					 throw new ServiceLayerException(String.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), new Long(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH),null);
		}
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdFacilAllegResource())) {
			CapsResource capsResource = capsResourceDao
					.getCapsResourceById(facilAllegDetailDto.getIdFacilAllegResource());
			facilAlleg.setCapsResource(capsResource);
		}else{
			facilAlleg.setCapsResource(null);
		}
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getDtFacilAllegIncident())) {
			try {
				String time = !ObjectUtils.isEmpty(facilAllegDetailDto.getIncmgCallTime())
						? facilAllegDetailDto.getIncmgCallTime() : ServiceConstants.TIME_MINIMUM;
				facilAlleg.setDtFacilAllegIncident(
						DateUtils.getTimestamp(facilAllegDetailDto.getDtFacilAllegIncident(), time));
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
		}else{
			// Defect 11719 - update DB even if the value is null
			facilAlleg.setDtFacilAllegIncident(null);
		}
		facilAlleg.setNbrFacilAllegMhmr(facilAllegDetailDto.getFacilAllegMHMR());
		facilAlleg.setTxtFacilAllegCmnts(facilAllegDetailDto.getFacilAllegCmnts());
		if (ObjectUtils.isEmpty(facilAllegDetailDto.getIndFacilAllegSupvd())
				|| (!(ObjectUtils.isEmpty(facilAllegDetailDto.getIndFacilAllegSupvd())) &&
				facilAllegDetailDto.getIndFacilAllegSupvd().equalsIgnoreCase(ServiceConstants.STRING_IND_N))) {
			facilAlleg.setIndFacilAllegSupvd(ServiceConstants.STRING_IND_N);
		}
		else{
			facilAlleg.setIndFacilAllegSupvd(facilAllegDetailDto.getIndFacilAllegSupvd());
		}
		if (ObjectUtils.isEmpty(facilAllegDetailDto.getIndFacilAllegAbOffGr()))
			facilAlleg.setIndFacilAllegAbOffGr(ServiceConstants.STRING_IND_N);
		else
			facilAlleg.setIndFacilAllegAbOffGr(facilAllegDetailDto.getIndFacilAllegAbOffGr());
		facilAlleg.setCdFacilAllegNeglType(facilAllegDetailDto.getCdFacilAllegNeglType());
		facilAlleg.setCdFacilAllegInjSer(facilAllegDetailDto.getCdFacilAllegInjSer());
		facilAlleg.setDtFacilAllegInvstgtr(facilAllegDetailDto.getDtFacilAllegInvstgtr());
		facilAlleg.setDtFacilAllegSuprReply(facilAllegDetailDto.getDtFacilAllegSuprReply());
		facilAlleg.setCdFacilAllegSrcSupr(facilAllegDetailDto.getCdFacilAllegSrcSupr());
		facilAlleg.setCdFacilAllegSrc(facilAllegDetailDto.getCdFacilAllegSrc());
		facilAlleg.setCdFacilAllegDispSupr(facilAllegDetailDto.getCdFacilAllegDispSupr());
		facilAlleg.setCdFacilAllegClssSupr(facilAllegDetailDto.getCdFacilAllegClssSupr());
		facilAlleg.setCdFacilAllegClss(facilAllegDetailDto.getCdFacilAllegInvClass());
		facilAlleg.setIndFacilAllegCancelHist(facilAllegDetailDto.getIndFacilAllegCancelHist());
		facilAlleg.setDtLastUpdate(new Date());
		Set<FacilityInjury> deleteList = new HashSet<FacilityInjury>();

		for (FacilityInjury injuryEntity : facilAlleg.getFacilityInjuries()) {

			FacilAllegInjuryDto injuryDto = null;

			if (!ObjectUtils.isEmpty(facilAllegInjuryDtoList)) {
				injuryDto = facilAllegInjuryDtoList.stream()
						.filter(o -> o.getIdFacilityInjury().equals(injuryEntity.getIdFacilityInjury())).findFirst()
						.orElse(null);
			}
			if (ObjectUtils.isEmpty(injuryDto)) {
				deleteList.add(injuryEntity);
			} else {
				BeanUtils.copyProperties(injuryDto, injuryEntity);
				injuryEntity.setDtLastUpdate(new Date());
			}
		}
		facilAlleg.getFacilityInjuries().removeAll(deleteList);
		FacilityInjury injuryentity = null;
		if (!ObjectUtils.isEmpty(updtFacilAllegDetailReq.getFacilAllegInjuryList())) {
			List<FacilAllegInjuryDto> addList = updtFacilAllegDetailReq.getFacilAllegInjuryList().stream()
					.filter(o -> ObjectUtils.isEmpty(o.getIdFacilityInjury())).collect(Collectors.toList());
			for (FacilAllegInjuryDto dto : addList) {
				injuryentity = new FacilityInjury();
				BeanUtils.copyProperties(dto, injuryentity);
				injuryentity.setDtLastUpdate(new Date());
				injuryentity.setFacilAlleg(facilAlleg);
				facilAlleg.getFacilityInjuries().add(injuryentity);
			}
		}
		facilAllgDtlDao.updateFacilAlleg(facilAlleg, updtFacilAllegDetailReq.getReqFuncCd(), false);

	}

	/**
	 * Method Name: pupulateAllegation Method Description: populates allegation
	 * details using idAllegation
	 * 
	 * @param updtFacilAllegDetailReq
	 * @return
	 */
	private Allegation pupulateAllegation(UpdtFacilAllegDetailReq updtFacilAllegDetailReq) {
		FacilAllegDetailDto facilAllegDetailDto = updtFacilAllegDetailReq.getFacilAllegDetailDto();
		Allegation allegation = null;
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdAllegation())) {
			allegation = allegtnDao.getAllegationById(facilAllegDetailDto.getIdAllegation());
		} else {
			allegation = new Allegation();
			allegation.setIdAllegation(facilAllegDetailDto.getIdAllegation());
		}
		Stage stage = new Stage();
		stage.setIdStage(facilAllegDetailDto.getIdStage());
		allegation.setStage(stage);
		allegation.setCdAllegType(facilAllegDetailDto.getCdAllegType());
		allegation.setCdAllegDisposition(facilAllegDetailDto.getCdAllegDisposition());
		allegation.setDtLastUpdate(new Date());

		allegation.setCdAllegIncidentStage(facilAllegDetailDto.getCdAllegIncidentStage());
		allegation.setDtLastUpdate(new Date());
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdVictim())) {
			Person victim = new Person();
			victim.setIdPerson(facilAllegDetailDto.getIdVictim());
			allegation.setPersonByIdVictim(victim);
		}
		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdAllegedPerpetrator())) {
			Person perp = new Person();
			perp.setIdPerson(facilAllegDetailDto.getIdAllegedPerpetrator());
			allegation.setPersonByIdAllegedPerpetrator(perp);
		} else {
			allegation.setPersonByIdAllegedPerpetrator(null);
		}
		return allegation;
	}

	/**
	 * Method Name: populateAllegationDetailDto Method Description: This method
	 * is used to populate all the dto values for the allegation details
	 * 
	 * @param facilAllegDetailDto
	 * @return
	 */
	private AllegationDetailDto populateAllegationDetailDto(FacilAllegDetailDto facilAllegDetailDto) {
		AllegationDetailDto allegationDetail = new AllegationDetailDto();
		allegationDetail.setIdStage(facilAllegDetailDto.getIdStage());
		allegationDetail.setIdAllegation(facilAllegDetailDto.getIdAllegation());
		allegationDetail.setIdVictim(facilAllegDetailDto.getIdVictim());
		allegationDetail.setCdAllegType(facilAllegDetailDto.getCdAllegType());
		allegationDetail.setIdAllegedPerpetrator(facilAllegDetailDto.getIdAllegedPerpetrator());
		return allegationDetail;

	}

	/**
	 * Method Description:This service to update multiple allegations Legacy
	 * Service:CINV10S
	 * 
	 * @param updtFacilAllegMultiDtlReq
	 * @return void
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateFacilAllegMulti(UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq) {
		boolean indCaseMergeInvFlag = true;
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getCdTask());
		inCheckStageEventStatusDto.setIdStage(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getIdStage());
		inCheckStageEventStatusDto.setCdReqFunction(updtFacilAllegMultiDtlReq.getReqFuncCd());
		if (!ObjectUtils.isEmpty(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getIdTodo())) {
			inCheckStageEventStatusDto.setIdStage(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getIdTodo());
		}
		checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);

		if (!ServiceConstants.CASE_MERGED_IN_ERROR
				.equals(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getCdFacilAllegDispInv())) {
			FacilAllegDetailDto facilDet = facilAllgDtlDao.retriveIdCase(updtFacilAllegMultiDtlReq);
			List<CaseMergeDetailDto> listCaseMergeDto = caseMergeCustomDao
					.getCaseMergeByIdCaseMergeTo(facilDet.getIdCase());

			for (CaseMergeDetailDto rec : listCaseMergeDto) {
				if (!rec.getIndCaseMergeInv().equals(ServiceConstants.STRING_IND_Y) && indCaseMergeInvFlag) {
					indCaseMergeInvFlag = false;
				}
			}
		}
		if (indCaseMergeInvFlag) {
			facilAllgDtlDao.callBlankOverallDispositionFAC(updtFacilAllegMultiDtlReq.getIdStage());

			if (!StringUtils.isEmpty(updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getCdFacilAllegSrc())) {
				List<Long> allegationsIds = updtFacilAllegMultiDtlReq.getIdAllegation();
				for (Long id : allegationsIds) {
					FacilAllegDetailDto facilAlegDet = updtFacilAllegMultiDtlReq.getFacilAllegDetailDto();
					facilAlegDet.setIdAllegation(id);
					facilAllgDtlDao.updateMultiFacilAllgWithDisp(facilAlegDet, ServiceConstants.REQ_FUNC_CD_UPDATE);

				}
				if (!StringUtils.isEmpty(updtFacilAllegMultiDtlReq.getIdEvent())
						&& updtFacilAllegMultiDtlReq.getIdEvent() != 0L) {
					facilAllgDtlDao.getEventDetailsUpdate(updtFacilAllegMultiDtlReq.getIdEvent(),
							CodesConstant.CEVTSTAT_COMP);
				}

			}
		}
	}
}
