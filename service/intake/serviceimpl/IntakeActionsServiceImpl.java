package us.tx.state.dfps.service.intake.serviceimpl;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION 
 * Tuxedo Service Name: 
 * Class Description:
 * Mar 23, 2017 - 1:28:11 PM
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.response.IntNarrBlobRes;
import us.tx.state.dfps.service.common.response.RetrvCallEntryRes;
import us.tx.state.dfps.service.common.response.RtrvAllegRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.intake.dao.IncmgDtrmFactorsDao;
import us.tx.state.dfps.service.intake.dao.IncomingNarrativeDao;
import us.tx.state.dfps.service.intake.dao.IntakeActionsCustomDao;
import us.tx.state.dfps.service.intake.dto.ActivePhonenumberRecDto;
import us.tx.state.dfps.service.intake.dto.CallDcsnAUDDto;
import us.tx.state.dfps.service.intake.dto.CallEntrySvcDto;
import us.tx.state.dfps.service.intake.dto.DetermListAUDDto;
import us.tx.state.dfps.service.intake.dto.IntakeActionMapperHelper;
import us.tx.state.dfps.service.intake.dto.SpecHDDto;
import us.tx.state.dfps.service.intake.service.IntakeActionsService;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneOutDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

@Service
@Transactional
public class IntakeActionsServiceImpl implements IntakeActionsService {

	@Autowired
	private EventDao eventDao;

	@Autowired
	private IncmgDtrmFactorsDao incmgDtrmFactorsDao;

	@Autowired
	private IncomingDetailDao incomingDetailDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private IncomingNarrativeDao incomingNarrativeDao;

	@Autowired
	private IntakeActionsCustomDao intakeActionsCustomDao;

	@Autowired
	private PersonPhoneDao personPhoneDao;

	private static final Logger log = Logger.getLogger(IntakeActionsServiceImpl.class);

	public IntakeActionsServiceImpl() {

	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RetrvCallEntryRes getCallEntrynDecsn(Long idPerson, Long idStage) {
		RetrvCallEntryRes callEntryRtrvOut = new RetrvCallEntryRes();
		StageDto stage;
		List<IncmgDetermFactors> incmgDetermFactors = null;
		List<PersonPhoneOutDto> personPhoneList = new ArrayList<>();
		IntakeActionMapperHelper intakeActionMapperHelper = new IntakeActionMapperHelper();
		IncomingDetail incomingDetail = incomingDetailDao.getincomingDetailbyId(idStage);
		intakeActionMapperHelper.setIncomingDetail(incomingDetail);
		if (!TypeConvUtil.isNullOrEmpty(incomingDetail)
				&& (null != incomingDetail.getIdStage() && incomingDetail.getIdStage() > 0l)
				&& (null != incomingDetail.getIdEvent() && incomingDetail.getIdEvent() > 0l)) {
			String eventStatus = eventDao.getEventStatus(incomingDetail.getIdEvent());
			intakeActionMapperHelper.setEventStatus(eventStatus);
			stage = stageDao.getStageById(idStage);
			intakeActionMapperHelper.setStage(stage);
			incmgDetermFactors = incmgDtrmFactorsDao.getincmgDetermFactorsById(idStage);
			intakeActionMapperHelper.setIncmgDetermFactors(incmgDetermFactors);
			PersonPhoneDto inPutPersonPhone = new PersonPhoneDto();
			inPutPersonPhone.setCdPersonPhoneType(ServiceConstants.indPersonPhoneType);
			inPutPersonPhone.setIndPersonPhoneInvalid(Boolean.FALSE);
			inPutPersonPhone.setIndPersonPhonePrimary(Boolean.TRUE);
			inPutPersonPhone.setIdPerson(idPerson);
			inPutPersonPhone.setIdStage(incomingDetail.getIdStage());
			personPhoneList = personPhoneDao.getPersonPhone(inPutPersonPhone);
			intakeActionMapperHelper.setPersonPhoneList(personPhoneList);
		}
		callEntryRtrvOut = entityToModel(intakeActionMapperHelper);
		// callEntryRtrvOut.setCallDcsnAUD(callDcsnAUD);
		// callEntryRtrvOut.setDetermListAUD(incmgDetermFactors);
		// callEntryRtrvOut.set
		log.info("TransactionId :" + callEntryRtrvOut.getTransactionId());
		return callEntryRtrvOut;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public IntNarrBlobRes getIntNarrBlobOutRec(Long idStage) {
		return (incomingNarrativeDao.getNarrativeById(idStage));
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RtrvAllegRes getAllegations(Long idStage) {
		return intakeActionsCustomDao.getAllegations(idStage);
	}

	private RetrvCallEntryRes entityToModel(IntakeActionMapperHelper intakeActionMapperHelper) {
		RetrvCallEntryRes callEntryRtrvOut = new RetrvCallEntryRes();
		ActivePhonenumberRecDto activePhonenumber = new ActivePhonenumberRecDto();
		SpecHDDto specHD = new SpecHDDto();
		List<DetermListAUDDto> determListAUDList = new ArrayList<>();
		CallEntrySvcDto callEntrySvcStruct = new CallEntrySvcDto();
		if (!TypeConvUtil.isNullOrEmpty(intakeActionMapperHelper)) {
			callEntrySvcStruct.setIdPerson(intakeActionMapperHelper.getIncomingDetail().getPerson().getIdPerson());
			callEntrySvcStruct.setIdStage(intakeActionMapperHelper.getIncomingDetail().getIdStage());
			if (null != intakeActionMapperHelper.getIncomingDetail().getCapsResource()) {
				callEntrySvcStruct
						.setIdResource(intakeActionMapperHelper.getIncomingDetail().getCapsResource().getIdCase());
			}
			if (null != intakeActionMapperHelper.getStage()) {
				callEntrySvcStruct.setNmStage(intakeActionMapperHelper.getStage().getNmStage());
			}
			callEntrySvcStruct.setCdIncmgCallerInt(intakeActionMapperHelper.getIncomingDetail().getCdIncmgCallerInt());
			callEntrySvcStruct.setCdIncmgAllegType(intakeActionMapperHelper.getIncomingDetail().getCdIncmgAllegType());
			callEntrySvcStruct.setDtIncomingCall(intakeActionMapperHelper.getIncomingDetail().getDtIncomingCall());
			callEntrySvcStruct
					.setTsIncmgCallDisp(intakeActionMapperHelper.getIncomingDetail().getDtIncomingCallDisposed());
			callEntrySvcStruct.setCdIncmgStatus(intakeActionMapperHelper.getIncomingDetail().getCdIncmgStatus());
			callEntrySvcStruct.setIdCase(intakeActionMapperHelper.getIncomingDetail().getIdCase());
			callEntrySvcStruct.setIdEvent(intakeActionMapperHelper.getIncomingDetail().getIdEvent());
			CallDcsnAUDDto callDcsnAUD = new CallDcsnAUDDto();
			callDcsnAUD.setIndOpenCaseFound(intakeActionMapperHelper.getIncomingDetail().getIndFoundOpenCase());
			callDcsnAUD.setCdAllegType(intakeActionMapperHelper.getIncomingDetail().getCdIncmgAllegType());
			callDcsnAUD.setCdIncmgSpecHandling(intakeActionMapperHelper.getIncomingDetail().getCdIncmgSpecHandling());
			callDcsnAUD.setIndIncmgSensitive(intakeActionMapperHelper.getIncomingDetail().getIndIncmgSensitive());
			callDcsnAUD.setIndIncmgSuspMeth(intakeActionMapperHelper.getIncomingDetail().getIndIncmgSuspMeth());
			callDcsnAUD.setIndIncmgWorkerSafety(intakeActionMapperHelper.getIncomingDetail().getIndIncmgWorkerSafety());
			callDcsnAUD.setIncmgWorkerSafety(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgWorkerSafety());
			callDcsnAUD.setIncomgSensitive(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgSensitive());
			callDcsnAUD.setIncomgSuspMeth(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgSuspMeth());
			callDcsnAUD.setIncmgSpecHandling(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgSpecHandling());
			callDcsnAUD.setIdStage(intakeActionMapperHelper.getIncomingDetail().getIdStage());
			if (null != intakeActionMapperHelper.getStage()) {
				callDcsnAUD.setCdStageReasonClosed(intakeActionMapperHelper.getStage().getCdStageReasonClosed());
				callDcsnAUD.setCdStageInitialPriority(intakeActionMapperHelper.getStage().getCdStageInitialPriority());
				callDcsnAUD.setCdStageCurrPriority(intakeActionMapperHelper.getStage().getCdStageCurrPriority());
				callDcsnAUD.setCdStageRsnPriorityChgd(intakeActionMapperHelper.getStage().getCdStageRsnPriorityChgd());
				callDcsnAUD.setNmStage(intakeActionMapperHelper.getStage().getNmStage());
				callDcsnAUD.setStagePriorityCmnts(intakeActionMapperHelper.getStage().getStagePriorityCmnts());
				callDcsnAUD.setIdUnit(intakeActionMapperHelper.getStage().getIdUnit());
			}
			callDcsnAUD.setIndIncmgNoFactor(intakeActionMapperHelper.getIncomingDetail().getIndIncmgNoFactor());
			// will have to populate all
			if (CollectionUtils.isNotEmpty(intakeActionMapperHelper.getPersonPhoneList())) {
				activePhonenumber.setNbrPhone(intakeActionMapperHelper.getPersonPhoneList().get(0).getPersonPhone());
				activePhonenumber.setNbrPhoneExtension(
						intakeActionMapperHelper.getPersonPhoneList().get(0).getPersonPhoneExtension());
			}
			if (CollectionUtils.isNotEmpty(intakeActionMapperHelper.getIncmgDetermFactors())) {
				for (IncmgDetermFactors rec : intakeActionMapperHelper.getIncmgDetermFactors()) {
					DetermListAUDDto determListAUD = new DetermListAUDDto();
					determListAUD.setCdIncmgDeterm(rec.getCdIncmgDeterm());
					determListAUD.setCdIncmgDetermType(rec.getCdIncmgDetermType());
					determListAUDList.add(determListAUD);
				}
			}
			specHD.setIdCase(intakeActionMapperHelper.getIncomingDetail().getIdCase());
			specHD.setCdCaseSpeclHndlg(intakeActionMapperHelper.getIncomingDetail().getCdIncmgSpecHandling());
			specHD.setIndCaseSensitive(intakeActionMapperHelper.getIncomingDetail().getIndIncmgSensitive());
			specHD.setIndCaseSuspMeth(intakeActionMapperHelper.getIncomingDetail().getIndIncmgSuspMeth());
			specHD.setIndCaseWorkerSafety(intakeActionMapperHelper.getIncomingDetail().getIndIncmgSuspMeth());
			specHD.setSpecHandling(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgSpecHandling());
			specHD.setCaseWorkerSafety(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgWorkerSafety());
			// specHD.setszTxtCaseSensitiveCmnts();
			specHD.setCaseSuspMeth(intakeActionMapperHelper.getIncomingDetail().getTxtIncmgSuspMeth());
			// specHD.settsSysTsLastUpdate2
			// specHD.setbIndLitigationHold
			// specHD.setszTxtLitigationHold
			callEntryRtrvOut.setCallDcsnAUD(callDcsnAUD);
			callEntryRtrvOut.setCallEntrySvcStruct(callEntrySvcStruct);
			callEntryRtrvOut.setActivePhonenumber(activePhonenumber);
			callEntryRtrvOut.setDetermListAUD(determListAUDList);
			callEntryRtrvOut.setSpecHD(specHD);
			callEntryRtrvOut.setSzCdEventStatus(intakeActionMapperHelper.getEventStatus());
		}
		log.info("TransactionId :" + callEntryRtrvOut.getTransactionId());
		return callEntryRtrvOut;
	}
}
