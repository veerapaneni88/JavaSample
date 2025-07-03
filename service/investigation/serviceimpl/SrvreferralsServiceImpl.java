package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.CpsChecklist;
import us.tx.state.dfps.common.domain.CpsChecklistItem;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.CpsChecklistDto;
import us.tx.state.dfps.common.dto.CpsChecklistItemDto;
import us.tx.state.dfps.common.dto.CpsChecklistItemsDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.request.SrvreferralsReq;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.SrvreferralsRes;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.investigation.service.SrvreferralsService;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * 
 * @author VISWAV
 *
 */
@Service
@Transactional
public class SrvreferralsServiceImpl implements SrvreferralsService {

	@Autowired
	private SrvreferralslDao srvrflDao;

	@Autowired
	PostEventService postEventService;
	@Autowired
	ApprovalCommonService approvalService;

	private static final Logger log = Logger.getLogger(SrvreferralsServiceImpl.class);

	public SrvreferralsServiceImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve data from the event,
	 * cps_checklist and cps_checklist_item tables. It uses the event id and
	 * stage id for existing records and a null event id for new records.
	 * Service Name: CINV54S
	 * 
	 * @param srvrflReq
	 * @return SrvreferralsRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public SrvreferralsRes getSrvrflInfo(SrvreferralsReq srvrflReq) {
		SrvreferralsRes srvrflRes = new SrvreferralsRes();
		CpsChecklist cpsChecklist = srvrflDao.getCpsChecklistByEventId(srvrflReq.getUlIdEvent());
		if (cpsChecklist != null) {
			srvrflRes.setcpsChecklist(this.populateCpsChecklist(cpsChecklist));
			srvrflRes.setCpsChecklistItemsDto(new CpsChecklistItemsDto());
			srvrflRes.getCpsChecklistItemsDto().getCpsChecklistItemDto()
					.addAll(this.populateCpsChecklistItem(cpsChecklist.getCpsChecklistItems(), srvrflRes));
			srvrflRes.getCpsChecklistItemsDto()
					.setRowQty(Long.valueOf(srvrflRes.getCpsChecklistItemsDto().getCpsChecklistItemDto().size()));
			if (cpsChecklist.getEvent() != null) {
				srvrflRes.setSzCdEventStatus(cpsChecklist.getEvent().getCdEventStatus());
			}
		} else {
			srvrflRes.setcpsChecklist(new CpsChecklistDto());
		}
		if (srvrflReq.getSzCdStage() != null && srvrflReq.getSzCdStage().equals(ServiceConstants.INV_Stage)) {
			Contact contact = srvrflDao.getContactByStageId(srvrflReq.getUlIdStage());
			if (contact != null) {
				if (srvrflRes.getcpsChecklist() != null)
					srvrflRes.getcpsChecklist().setDtCPSInvstDtlBegun(contact.getDtContactOccurred());
			} else {
				srvrflRes.setMessage("MSG_INV_NOT_BEGUN");
			}
		}
		srvrflRes.setTransactionId(srvrflReq.getTransactionId());
		log.info("TransactionId :" + srvrflReq.getTransactionId());
		return srvrflRes;
	}

	/**
	 * 
	 * @param cpsChecklist
	 * @return
	 */
	private CpsChecklistDto populateCpsChecklist(CpsChecklist cpsChecklist) {
		CpsChecklistDto cpsChecklistDto = new CpsChecklistDto();
		if (cpsChecklist == null) {
			return cpsChecklistDto;
		}
		cpsChecklistDto.setIdCpsChecklist(cpsChecklist.getIdCpsChecklist());
		cpsChecklistDto.setIdEvent(cpsChecklist.getEvent().getIdEvent());
		cpsChecklistDto.setTsLastUpdate(cpsChecklist.getDtLastUpdate());
		cpsChecklistDto.setIdCase(cpsChecklist.getCapsCase().getIdCase());
		cpsChecklistDto.setIdStage(cpsChecklist.getStage().getIdStage());
		cpsChecklistDto.setDtFirstReferral(cpsChecklist.getDtFirstReferral());
		cpsChecklistDto.setIndSvcRefChklstNoRef(cpsChecklist.getIndReferral());
		cpsChecklistDto.setCdFamilyResponse(cpsChecklist.getCdFamilyResp());
		cpsChecklistDto.setChklstComments(cpsChecklist.getTxtComments());
		cpsChecklistDto.setIdAmtAnnualHouseholdIncome(cpsChecklist.getAmtAnnualHouseholdIncome());
		if (cpsChecklist.getNbrNumberInHousehold() != null) {
			cpsChecklistDto.setNbrNumberInHousehold(cpsChecklist.getNbrNumberInHousehold().longValue());
		}
		cpsChecklistDto.setIndIncomeQualification(cpsChecklist.getIndIncomeQualification());
		cpsChecklistDto.setIndEligVerifiedByStaff(cpsChecklist.getIndEligVerifiedByStaff());
		cpsChecklistDto.setIndProblemNeglect(cpsChecklist.getIndProblemNeglect());
		cpsChecklistDto.setIndCitizenshipVerify(cpsChecklist.getIndCitizenshipVerify());
		cpsChecklistDto.setIndChildRmvlReturn(cpsChecklist.getIndChildRmvlReturn());
		cpsChecklistDto.setCdEarlyTermRsn(cpsChecklist.getCdEarlyTermRsn());
		cpsChecklistDto.setDtEligStart(cpsChecklist.getDtEligStart());
		cpsChecklistDto.setDtEligEnd(cpsChecklist.getDtEligEnd());
		return cpsChecklistDto;
	}

	/**
	 * 
	 * @param cpsChecklistItems
	 * @param srvrflRes
	 * @return
	 */
	private List<CpsChecklistItemDto> populateCpsChecklistItem(Set<CpsChecklistItem> cpsChecklistItems,
			SrvreferralsRes srvrflRes) {
		if (cpsChecklistItems == null || cpsChecklistItems.isEmpty()) {
			return new ArrayList<CpsChecklistItemDto>();
		}
		List<CpsChecklistItemDto> cpsChecklistItemsList = new ArrayList<CpsChecklistItemDto>();
		CpsChecklistItemDto cpsChecklistItemDto = null;
		for (CpsChecklistItem cpsChecklistItem : cpsChecklistItems) {
			cpsChecklistItemDto = new CpsChecklistItemDto();
			cpsChecklistItemDto.setIdChklstItem(cpsChecklistItem.getIdCpsChecklistItem());
			cpsChecklistItemDto.setTsLastUpdate(cpsChecklistItem.getDtLastUpdate());
			cpsChecklistItemDto.setIdCpsChecklist(srvrflRes.getcpsChecklist().getIdCpsChecklist());
			cpsChecklistItemDto.setIdEvent(srvrflRes.getcpsChecklist().getIdEvent());
			cpsChecklistItemDto.setIdCase(srvrflRes.getcpsChecklist().getIdCase());
			cpsChecklistItemDto.setIdStage(srvrflRes.getcpsChecklist().getIdStage());
			cpsChecklistItemDto.setCdSvcReferred(cpsChecklistItem.getCdSrvcReferred());
			cpsChecklistItemsList.add(cpsChecklistItemDto);
		}
		return cpsChecklistItemsList;
	}

	/**
	 * 
	 * Method Description: This Method will insert or update the event and
	 * cps_checklist tables. Also it can delete or add rows to the
	 * cps_checklist_item table. Service Name: CINV55S
	 * 
	 * @param srvrflReq
	 * @return SrvreferralsRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SrvreferralsRes saveOrUpdateSrvrflInfo(SrvreferralsReq srvrflReq) {
		CpsChecklistDto cpsChecklistDto = srvrflReq.getCpsChecklistDto();
		Long eventId = cpsChecklistDto.getIdEvent();
		CpsChecklist cpsChecklist = new CpsChecklist();
		if (cpsChecklistDto.getIdCpsChecklist() != null) {
			cpsChecklist = srvrflDao.getCpsChecklist(cpsChecklistDto.getIdCpsChecklist());
		}
		if (eventId == 0) {
			cpsChecklist.setEvent(srvrflDao.getEventById(createAndReturnEventid(srvrflReq)));
		} else {
			cpsChecklist.setEvent(srvrflDao.getEventById(eventId));
		}
		cpsChecklist.setDtLastUpdate(cpsChecklistDto.getTsLastUpdate());
		if (cpsChecklistDto.getIdCase() != null) {
			cpsChecklist.setCapsCase(srvrflDao.getCaseById(cpsChecklistDto.getIdCase()));
		}
		if (cpsChecklistDto.getIdStage() != null) {
			cpsChecklist.setStage(srvrflDao.getStageById(cpsChecklistDto.getIdStage()));
		}
		if (cpsChecklistDto.getDtFirstReferral() != null) {
			Date firstReferal = cpsChecklistDto.getDtFirstReferral();
			cpsChecklist.setDtFirstReferral(firstReferal);
		} else {
			cpsChecklist.setDtFirstReferral(null);
		}
		cpsChecklist.setIndReferral(cpsChecklistDto.getIndSvcRefChklstNoRef());
		cpsChecklist.setCdFamilyResp(cpsChecklistDto.getCdFamilyResponse());
		cpsChecklist.setTxtComments(cpsChecklistDto.getChklstComments());
		cpsChecklist.setIndCitizenshipVerify(cpsChecklistDto.getIndCitizenshipVerify());
		cpsChecklist.setIndProblemNeglect(cpsChecklistDto.getIndProblemNeglect());
		cpsChecklist.setIndEligVerifiedByStaff(cpsChecklistDto.getIndEligVerifiedByStaff());
		cpsChecklist.setIndIncomeQualification(cpsChecklistDto.getIndIncomeQualification());
		if (cpsChecklistDto.getNbrNumberInHousehold() != null) {
			cpsChecklist.setNbrNumberInHousehold(cpsChecklistDto.getNbrNumberInHousehold().byteValue());
		} else {
			cpsChecklist.setNbrNumberInHousehold(null);
		}
		cpsChecklist.setAmtAnnualHouseholdIncome(cpsChecklistDto.getIdAmtAnnualHouseholdIncome());
		cpsChecklist.setIndChildRmvlReturn(cpsChecklistDto.getIndChildRmvlReturn());
		cpsChecklist.setCdEarlyTermRsn(cpsChecklistDto.getCdEarlyTermRsn());
		cpsChecklist.setDtEligStart(cpsChecklistDto.getDtEligStart());
		cpsChecklist.setDtEligEnd(cpsChecklistDto.getDtEligEnd());
		// SAVE OR UPDATE
		srvrflDao.saveOrUpdateCpsChecklist(cpsChecklist);
		if (srvrflReq.getCpsChecklistItemsDto() == null || (srvrflReq.getCpsChecklistItemsDto() != null
				&& srvrflReq.getCpsChecklistItemsDto().getCpsChecklistItemDto().isEmpty())) {
			for (CpsChecklistItem dbCpsChecklistItem : cpsChecklist.getCpsChecklistItems()) {
				srvrflDao.deleteCpsChecklistItem(dbCpsChecklistItem);
			}
			cpsChecklist.getCpsChecklistItems().clear();
		} else {
			// Delete database check list item if doens't exist in request
			for (CpsChecklistItem dbCpsChecklistItem : cpsChecklist.getCpsChecklistItems()) {
				boolean isDbItemRequired = false;
				for (CpsChecklistItemDto cpsChecklistItemDto : srvrflReq.getCpsChecklistItemsDto()
						.getCpsChecklistItemDto()) {
					if (dbCpsChecklistItem.getCdSrvcReferred().equals(cpsChecklistItemDto.getCdSvcReferred())
							&& srvrflReq.getCpsChecklistDto().getIdEvent().longValue() == dbCpsChecklistItem.getEvent()
									.getIdEvent()
							&& srvrflReq.getCpsChecklistDto().getIdCpsChecklist().longValue() == dbCpsChecklistItem
									.getIdCpsChecklistItem()) {
						isDbItemRequired = true;
					}
				}
				if (!isDbItemRequired) {
					srvrflDao.deleteCpsChecklistItem(dbCpsChecklistItem);
				}
			}
			if (eventId == 0) {
				for (CpsChecklistItemDto cpsChecklistItemDto : srvrflReq.getCpsChecklistItemsDto()
						.getCpsChecklistItemDto()) {
					CpsChecklistItem newCpsChecklistItem = new CpsChecklistItem();
					newCpsChecklistItem.setCapsCase(cpsChecklist.getCapsCase());
					newCpsChecklistItem.setCdSrvcReferred(cpsChecklistItemDto.getCdSvcReferred());
					newCpsChecklistItem.setCpsChecklist(cpsChecklist);
					newCpsChecklistItem.setDtLastUpdate(cpsChecklistItemDto.getTsLastUpdate());
					newCpsChecklistItem.setEvent(cpsChecklist.getEvent());
					newCpsChecklistItem.setIdCpsChecklistItem(cpsChecklistItemDto.getIdChklstItem());
					newCpsChecklistItem.setStage(cpsChecklist.getStage());
					srvrflDao.saveCpsChecklistItem(newCpsChecklistItem);
					cpsChecklist.getCpsChecklistItems().add(newCpsChecklistItem);
				}
			} else {
				// Save request check list item if dosen't exist in Database
				for (CpsChecklistItemDto cpsChecklistItemDto : srvrflReq.getCpsChecklistItemsDto()
						.getCpsChecklistItemDto()) {
					boolean isExist = false;
					for (CpsChecklistItem databaseCpsChecklistItem : cpsChecklist.getCpsChecklistItems()) {
						if (cpsChecklistItemDto.getCdSvcReferred().equals(databaseCpsChecklistItem.getCdSrvcReferred())
								&& srvrflReq.getCpsChecklistDto().getIdEvent().longValue() == databaseCpsChecklistItem
										.getEvent().getIdEvent()
								&& srvrflReq.getCpsChecklistDto().getIdCpsChecklist()
										.longValue() == databaseCpsChecklistItem.getIdCpsChecklistItem()) {
							isExist = true;
						}
					}
					if (!isExist) {
						CpsChecklistItem newCpsChecklistItem = new CpsChecklistItem();
						newCpsChecklistItem.setCapsCase(cpsChecklist.getCapsCase());
						newCpsChecklistItem.setCdSrvcReferred(cpsChecklistItemDto.getCdSvcReferred());
						newCpsChecklistItem.setCpsChecklist(cpsChecklist);
						newCpsChecklistItem.setDtLastUpdate(cpsChecklistItemDto.getTsLastUpdate());
						newCpsChecklistItem.setEvent(cpsChecklist.getEvent());
						newCpsChecklistItem.setIdCpsChecklistItem(cpsChecklistItemDto.getIdChklstItem());
						newCpsChecklistItem.setStage(cpsChecklist.getStage());
						srvrflDao.saveCpsChecklistItem(newCpsChecklistItem);
						cpsChecklist.getCpsChecklistItems().add(newCpsChecklistItem);
					}
				}
			}
		}
		// Save or update checklist item in the _TODO table.
		for (Todo toDo : cpsChecklist.getEvent().getTodos()) {
			toDo.setDtTodoCompleted(new Date());
			srvrflDao.saveOrUpdateToDO(toDo);
		}
		// CCMN05U
		if (!srvrflReq.getSysNbrReserved1()
				&& cpsChecklist.getEvent().getCdEventStatus().equals(CodesConstant.CAPPDESG_PEND)) {

			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(cpsChecklist.getEvent().getIdEvent());
			approvalService.callCcmn05uService(approvalCommonInDto);
		}
		return prepareResponseObj(cpsChecklist, srvrflReq);
	}

	/**
	 * Retrieves the parental child safety placement details from the
	 * CHILD_SAFETY_PLCMT, PERSON, STAGE tables. Service Name: PCSPEjb
	 * 
	 * @param pcspReq
	 * @return PcspRes @
	 */
	public PcspRes displayPCSPList(PcspReq pcspReq) {
		PcspRes pcspOut = new PcspRes();
		List<PcspDto> pcsp = new ArrayList<>();
		pcsp = srvrflDao.getPcspList(pcspReq);
		pcspOut.setPcspList(pcsp);
		return pcspOut;
	}

	private Long createAndReturnEventid(SrvreferralsReq srvrflReq) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		postEventIPDto.setEventDescr("Services and Referrals Checklist");
		postEventIPDto.setCdTask(srvrflReq.getCpsChecklistDto().getCdTask());
		postEventIPDto.setCdEventType("CHK");
		postEventIPDto.setIdPerson(srvrflReq.getCpsChecklistDto().getIdUser());
		postEventIPDto.setIdStage(srvrflReq.getCpsChecklistDto().getIdStage());
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setUserId(srvrflReq.getCpsChecklistDto().getIdUserLogon());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		// postEventIPDto.setDtDtEventOccurred(new Date());
		postEventIPDto.setTsLastUpdate(new Date());
		postEventIPDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	private SrvreferralsRes prepareResponseObj(CpsChecklist cpsChecklist, SrvreferralsReq srvrflReq) {
		SrvreferralsRes srvrflRes = new SrvreferralsRes();
		srvrflRes.setUlIdCpsChecklist(cpsChecklist.getIdCpsChecklist());
		srvrflRes.setSzCdEventStatus(cpsChecklist.getEvent().getCdEventStatus());
		CpsChecklistDto cpsChecklistDto = new CpsChecklistDto();
		cpsChecklistDto.setIdCpsChecklist(cpsChecklist.getIdCpsChecklist());
		cpsChecklistDto.setIdEvent(cpsChecklist.getEvent().getIdEvent());
		cpsChecklistDto.setTsLastUpdate(cpsChecklist.getDtLastUpdate());
		cpsChecklistDto.setIdCase(cpsChecklist.getCapsCase().getIdCase());
		cpsChecklistDto.setIdStage(cpsChecklist.getStage().getIdStage());
		// cpsChecklistDto.setDtDtCPSInvstDtlBegun();
		cpsChecklistDto.setDtFirstReferral(cpsChecklist.getDtFirstReferral());
		// cpsChecklistDto.SetIndSvcRefChklstNoRef(cpsChecklist.getIndReferral());
		cpsChecklistDto.setCdFamilyResponse(cpsChecklist.getCdFamilyResp());
		cpsChecklistDto.setChklstComments(cpsChecklist.getTxtComments());
		cpsChecklistDto.setIndCitizenshipVerify(cpsChecklist.getIndCitizenshipVerify());
		cpsChecklistDto.setIndProblemNeglect(cpsChecklist.getIndProblemNeglect());
		cpsChecklistDto.setIndEligVerifiedByStaff(cpsChecklist.getIndEligVerifiedByStaff());
		cpsChecklistDto.setIndIncomeQualification(cpsChecklist.getIndIncomeQualification());
		// cpsChecklistDto.setSzCdScrDataAction(cpsChecklist.getcd);
		cpsChecklistDto.setCdTask(cpsChecklist.getEvent().getCdTask());
		// cpsChecklistDto.setINbrNumberInHousehold(Long.valueOf(cpsChecklist.getNbrNumberInHousehold().toString()));
		// cpsChecklistDto.setIdAmtAnnualHouseholdIncome(cpsChecklist.getAmtAnnualHouseholdIncome());
		cpsChecklistDto.setIndChildRmvlReturn(cpsChecklist.getIndChildRmvlReturn());
		cpsChecklistDto.setCdEarlyTermRsn(cpsChecklist.getCdEarlyTermRsn());
		cpsChecklistDto.setDtEligStart(cpsChecklist.getDtEligStart());
		cpsChecklistDto.setDtEligEnd(cpsChecklist.getDtEligEnd());
		cpsChecklistDto.setIdUser(srvrflReq.getCpsChecklistDto().getIdUser());
		cpsChecklistDto.setIdUserLogon(srvrflReq.getCpsChecklistDto().getIdUserLogon());
		srvrflRes.setcpsChecklist(cpsChecklistDto);
		CpsChecklistItemsDto cpsChecklistItemsDto = new CpsChecklistItemsDto();
		CpsChecklistItemDto cpsChecklistItemDto = new CpsChecklistItemDto();
		List<CpsChecklistItemDto> itemList = new ArrayList<CpsChecklistItemDto>();
		for (CpsChecklistItem cpsChecklistItem : cpsChecklist.getCpsChecklistItems()) {
			cpsChecklistItemDto = new CpsChecklistItemDto();
			cpsChecklistItemDto.setCdSvcReferred(cpsChecklistItem.getCdSrvcReferred());
			cpsChecklistItemDto.setIdCpsChecklist(cpsChecklistItem.getCpsChecklist().getIdCpsChecklist());
			cpsChecklistItemDto.setIdChklstItem(cpsChecklistItem.getIdCpsChecklistItem());
			cpsChecklistItemDto.setTsLastUpdate(cpsChecklistItem.getDtLastUpdate());
			cpsChecklistItemDto.setIdEvent(cpsChecklistItem.getEvent().getIdEvent());
			cpsChecklistItemDto.setIdCase(cpsChecklistItem.getCapsCase().getIdCase());
			cpsChecklistItemDto.setIdStage(cpsChecklistItem.getStage().getIdStage());
			// cpsChecklistItemDto.setBScrIndOnOff(cpsChecklistItem.getsr);
			// cpsChecklistItemDto.setSzCdScrDataAction(cpsChecklistItem.get);
			itemList.add(cpsChecklistItemDto);
		}
		cpsChecklistItemsDto.setCpsChecklistItemDto(itemList);
		if (srvrflReq.getSzCdStage() != null && srvrflReq.getSzCdStage().equals(ServiceConstants.INV_Stage)) {
			Contact contact = srvrflDao.getContactByStageId(srvrflRes.getcpsChecklist().getIdStage());
			if (contact != null) {
				srvrflRes.getcpsChecklist().setDtCPSInvstDtlBegun(contact.getDtContactOccurred());
			}
		}
		if (cpsChecklist.getEvent() != null) {
			srvrflRes.setSzCdEventStatus(cpsChecklist.getEvent().getCdEventStatus());
		}
		srvrflRes.setCpsChecklistItemsDto(cpsChecklistItemsDto);
		srvrflRes.setTransactionId(srvrflReq.getTransactionId());
		log.info("TransactionId :" + srvrflReq.getTransactionId());
		return srvrflRes;
	}
}
