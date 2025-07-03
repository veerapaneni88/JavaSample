package us.tx.state.dfps.service.conservatorship.serviceimpl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import microsoft.exchange.webservices.data.core.ExchangeService;
import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.childplan.dao.ChildPlanDtlDao;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ConservRmvlSaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ConservRmvlSaveRes;
import us.tx.state.dfps.service.common.util.OutlookUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDtlsDao;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalInsUpdDelDao;
import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRemovalInsDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharAdultInsUpdDelDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharChildInsUpdDelDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonInsUpdDelDao;
import us.tx.state.dfps.service.conservatorship.dao.StageStagePersonLinkStgRoleDao;
import us.tx.state.dfps.service.conservatorship.dao.StageStartCloseDao;
import us.tx.state.dfps.service.conservatorship.dao.StageUpdByStageStartIdDao;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.conservatorship.service.ConservRmvlSaveService;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelOutDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalOutDto;
import us.tx.state.dfps.service.cvs.dto.ConservatorshipRowOneDto;
import us.tx.state.dfps.service.cvs.dto.ConservatorshipRowThreeDto;
import us.tx.state.dfps.service.cvs.dto.ConservatorshipRowTwoDto;
import us.tx.state.dfps.service.cvs.dto.EventRowUpdateTwoDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsOutDto;
import us.tx.state.dfps.service.cvs.dto.RemovalCharAdultInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelOutDto;
import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelOutDto;
import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleInDto;
import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleOutDto;
import us.tx.state.dfps.service.cvs.dto.StageStartCloseInDto;
import us.tx.state.dfps.service.cvs.dto.StageStartCloseOutDto;
import us.tx.state.dfps.service.cvs.dto.StageUpdByStageStartIdInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.outlook.AppointmentDto;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This is the save service for Conservatorship Removal which
 * includes creating a new Conservatorship Removal Detail, Removal Reason
 * record, Child Removal Characteristic record & Adult Removal Characteristic
 * record.
 * 
 * Aug 15, 2017-8:31:55 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
public class ConservRmvlSaveServiceImpl implements ConservRmvlSaveService {

	public static final ResourceBundle emailConfigBundle = ResourceBundle.getBundle("EmailConfig");

	@Autowired
	MessageSource messageSource;

	// Ccmn06u
	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	// Ccmn01u
	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	// Ccmn62d
	@Autowired
	EventUpdEventStatusDao eventUpdEventStatusDao;

	// Ccmn05u
	@Autowired
	ApprovalCommonService approvalCommonService;

	// Cses20d
	@Autowired
	CnsrvtrshpRemovalDtlsDao cnsrvtrshpRemovalDtlsDao;

	@Autowired
	CnsrvtrshpRemovalDao cnsrvtrshpRemovalDao;

	// Caud29d
	@Autowired
	CnsrvtrshpRemovalInsUpdDelDao cnsrvtrshpRemovalInsUpdDelDao;

	// Caud30d
	@Autowired
	RemovalReasonInsUpdDelDao removalReasonInsUpdDelDao;

	// Caud31d
	@Autowired
	RemovalCharChildInsUpdDelDao removalCharChildInsUpdDelDao;

	// Caud32d
	@Autowired
	RemovalCharAdultInsUpdDelDao removalCharAdultInsUpdDelDao;

	// Caud41d
	@Autowired
	PersonHomeRemovalInsDao personHomeRemovalInsDao;

	// Ccmn03u
	@Autowired
	CloseOpenStageService closeOpenStageService;

	// Ccmne1d
	@Autowired
	StageStartCloseDao stageStartCloseDao;

	// Cinvc4d
	@Autowired
	StageUpdByStageStartIdDao stageUpdByStageStartIdDao;

	// Csub84d
	@Autowired
	StageStagePersonLinkStgRoleDao stageStagePersonLinkStgRoleDao;

	@Autowired
	ChildPlanDtlDao childPlanDtlDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	AlertService alertService;

	@Autowired
	private ApprovalDao approvalDao;

	@Autowired
	OutlookUtil outlookUtil;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	private static final Logger log = Logger.getLogger(ConservRmvlSaveServiceImpl.class);

	private static final String PROD_HOST_NAME = "PROD";

	private static final String MM_DD_YYYY_FORMAT = "MM/dd/YYYY";

	/**
	 * Method Name: conservatorshipRemovalSave Method Description : This method
	 * is for Conservatorship Removal which includes creating a new
	 * Conservatorship Removal Detail, Removal Reason record, Child Removal
	 * Characteristic record & Adult Removal Characteristic record.
	 * 
	 * @param conservRmvlSaveReq
	 * @return ConservRmvlSaveoDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ConservRmvlSaveRes conservatorshipRemovalSave(ConservRmvlSaveReq conservRmvlSaveReq) {
		log.debug("Entering method callConservRmvlSaveService in ConservRmvlSaveServiceImpl");
		Date NULL_VALDAT = null;
		ConservRmvlSaveRes conservRmvlSaveRes = new ConservRmvlSaveRes();
		CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto = new CnsrvtrshpRemovalInsUpdDelInDto();
		CnsrvtrshpRemovalInsUpdDelOutDto cnsrvtrshpRemovalInsUpdDelOutDto = new CnsrvtrshpRemovalInsUpdDelOutDto();
		CnsrvtrshpRemovalInDto cnsrvtrshpRemovalInDto = new CnsrvtrshpRemovalInDto();
		RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto = new RemovalReasonInsUpdDelInDto();
		RemovalReasonInsUpdDelOutDto removalReasonInsUpdDelOutDto = new RemovalReasonInsUpdDelOutDto();
		RemovalCharChildInsUpdDelInDto removalCharChildInsUpdDelInDto = new RemovalCharChildInsUpdDelInDto();
		RemovalCharChildInsUpdDelOutDto removalCharChildInsUpdDelOutDto = new RemovalCharChildInsUpdDelOutDto();
		RemovalCharAdultInsUpdDelInDto removalCharAdultInsUpdDelInDto = new RemovalCharAdultInsUpdDelInDto();
		PersonHomeRemovalInsInDto personHomeRemovalInsInDto = new PersonHomeRemovalInsInDto();
		PersonHomeRemovalInsOutDto personHomeRemovalInsOutDto = new PersonHomeRemovalInsOutDto();
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();
		PostEventStageStatusOutDto postEventStageStatusOutDto = new PostEventStageStatusOutDto();
		CloseOpenStageInputDto closeOpenStageInputDto = new CloseOpenStageInputDto();
		CloseOpenStageOutputDto closeOpenStageOutputDto = new CloseOpenStageOutputDto();
		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
		EventUpdEventStatusInDto eventUpdEventStatusInDto = new EventUpdEventStatusInDto();
		String RetVal = ServiceConstants.NULL_STRING;
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		StageUpdByStageStartIdInDto stageUpdByStageStartIdInDto = new StageUpdByStageStartIdInDto();
		StageStartCloseInDto pCCMNE1DInputRec = new StageStartCloseInDto();
		StageStagePersonLinkStgRoleInDto stageStagePersonLinkStgRoleInDto = new StageStagePersonLinkStgRoleInDto();
		List<CnsrvtrshpRemovalOutDto> resource20 = null;
		List<StageStartCloseOutDto> resource01 = null;
		List<StageStagePersonLinkStgRoleOutDto> resource84 = null;
		int rowcount = 0;
		Long idStageCP = ServiceConstants.NULL_VAL;
		stageTaskInDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
		stageTaskInDto.setIdStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
		stageTaskInDto.setCdTask(conservRmvlSaveReq.getEventRowUpdateOneDto().getCdTask());
		String stageEventStatus = stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto);
		SimpleDateFormat sdf = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_yyyyMMddHHmmss);
		String strDate = sdf.format(conservRmvlSaveReq.getConservatorshipRowZeroDto().getTsLastUpdate());
		Timestamp newLastUpdatedTime = Timestamp.valueOf(strDate);
		if (!TypeConvUtil.isNullOrEmpty(stageEventStatus) && stageEventStatus.equals(ServiceConstants.ARC_SUCCESS)) {
			RetVal = ServiceConstants.FND_SUCCESS;
		}
		List<Long> idEvents = new ArrayList<Long>();

		idEvents.add(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdEvent());
		List<CnsrvtrshpRemovalDto> removalDtos = cnsrvtrshpRemovalDao.getCnsrvtrshpRemovalDtl(idEvents);
		if (!CollectionUtils.isEmpty(removalDtos)) {
			if (!removalDtos.get(0).getDtLastUpdate().equals(newLastUpdatedTime)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				conservRmvlSaveRes.setErrorDto(errorDto);
				return conservRmvlSaveRes;
			}
		}
		Date rmvlDtBeforeUpdate = NULL_VALDAT;
		boolean isSuccess = ServiceConstants.FND_SUCCESS.equals(RetVal);
		if (isSuccess) {
			if (ServiceConstants.Zero == conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent()) {
				postEventStageStatusInDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
			} else {
				postEventStageStatusInDto.setReqFuncCd(ServiceConstants.UPDATE);
			}
			postEventStageStatusInDto.setCdEventStatus(conservRmvlSaveReq.getEventRowUpdateOneDto().getCdEventStatus());
			postEventStageStatusInDto.setIdStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
			postEventStageStatusInDto.setCdTask(conservRmvlSaveReq.getEventRowUpdateOneDto().getCdTask());
			postEventStageStatusInDto.setCdEventType(conservRmvlSaveReq.getEventRowUpdateOneDto().getCdEventType());
			postEventStageStatusInDto
					.setDtEventOccurred(conservRmvlSaveReq.getEventRowUpdateOneDto().getDtEventOccurred());
			postEventStageStatusInDto.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
			postEventStageStatusInDto.setIdPerson(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson());
			postEventStageStatusInDto.setEventDescr(conservRmvlSaveReq.getEventRowUpdateOneDto().getTxtEventDescr());
			postEventStageStatusInDto
					.setDtEventLastUpdate(conservRmvlSaveReq.getEventRowUpdateOneDto().getTsLastUpdate());
			if (ServiceConstants.Zero == conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent()) {
				EventRowUpdateTwoDto eventRowUpdateTwoDto = new EventRowUpdateTwoDto();
				eventRowUpdateTwoDto.setIdPerson(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
				eventRowUpdateTwoDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
				List<EventRowUpdateTwoDto> rowccmn01uig01Dtos = new ArrayList<EventRowUpdateTwoDto>();
				rowccmn01uig01Dtos.add(eventRowUpdateTwoDto);
				postEventStageStatusInDto.setLirowCcmn01UiG00(rowccmn01uig01Dtos);
			}
			postEventStageStatusOutDto = postEventStageStatusService
					.callPostEventStageStatusService(postEventStageStatusInDto);
			if (!TypeConvUtil.isNullOrEmpty(postEventStageStatusOutDto)) {
				if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(conservRmvlSaveReq.getReqFuncCd())) {
					conservRmvlSaveRes.setIdEvent(postEventStageStatusOutDto.getIdEvent());
					conservRmvlSaveReq.setIdEvent(postEventStageStatusOutDto.getIdEvent());
				} else {
					conservRmvlSaveRes.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
				}
				if (!TypeConvUtil.isNullOrEmpty(conservRmvlSaveReq.getSysNbrReserved1())
						&& !conservRmvlSaveReq.getSysNbrReserved1()) {
					eventUpdEventStatusInDto.setIdEvent(conservRmvlSaveReq.getIdEvent());
					eventUpdEventStatusInDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMP);
					eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
					eventUpdEventStatusDao.updateEvent(eventUpdEventStatusInDto);
					// Always invalidate the conclusion event.
					approvalCommonInDto.setSysNbrReserved1(conservRmvlSaveReq.getSysNbrReserved1());
					approvalCommonInDto.setIdEvent(conservRmvlSaveReq.getIdConclusionEvent());
					approvalCommonOutDto = approvalCommonService.callCcmn05uService(approvalCommonInDto);
					if (TypeConvUtil.isNullOrEmpty(approvalCommonOutDto)) {
						RetVal = ServiceConstants.FAIL;
					}
				}
				if (ServiceConstants.Zero < conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdEvent()) {
					cnsrvtrshpRemovalInDto.setIdEvent(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdEvent());
					resource20 = cnsrvtrshpRemovalDtlsDao.getrmvldtls(cnsrvtrshpRemovalInDto);
					if (!TypeConvUtil.isNullOrEmpty(resource20)
							&& !TypeConvUtil.isNullOrEmpty(resource20.get(ServiceConstants.Zero).getDtRemoval())) {
						rmvlDtBeforeUpdate = resource20.get(ServiceConstants.Zero).getDtRemoval();
					}
				}
				cnsrvtrshpRemovalInsUpdDelInDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
				if (ServiceConstants.Zero == conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent()) {
					cnsrvtrshpRemovalInsUpdDelInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
				} else {
					cnsrvtrshpRemovalInsUpdDelInDto
							.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
				}
				cnsrvtrshpRemovalInsUpdDelInDto
						.setIdVictim(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
				cnsrvtrshpRemovalInsUpdDelInDto
						.setTmLNbrRemovalAgeMo(conservRmvlSaveReq.getConservatorshipRowZeroDto().getNbrRemovalAgeMo());
				cnsrvtrshpRemovalInsUpdDelInDto
						.setTmLNbrRemovalAgeYr(conservRmvlSaveReq.getConservatorshipRowZeroDto().getNbrRemovalAgeYr());
				cnsrvtrshpRemovalInsUpdDelInDto
						.setCIndRemovalNACare(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIndRemovalNACare());
				cnsrvtrshpRemovalInsUpdDelInDto.setCIndRemovalNaChild(
						conservRmvlSaveReq.getConservatorshipRowZeroDto().getIndRemovalNaChild());
				cnsrvtrshpRemovalInsUpdDelInDto
						.setDtRemoval(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
				cnsrvtrshpRemovalInsUpdDelInDto
						.setTsLastUpdate(conservRmvlSaveReq.getConservatorshipRowZeroDto().getTsLastUpdate());
				cnsrvtrshpRemovalInsUpdDelOutDto = cnsrvtrshpRemovalInsUpdDelDao
						.cnsrvtrshpRemovalInsUpdDel(cnsrvtrshpRemovalInsUpdDelInDto);
				if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelOutDto)) {
					if (ServiceConstants.Zero == conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent()) {
						cnsrvtrshpRemovalInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
					} else {
						cnsrvtrshpRemovalInDto.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
					}
					resource20 = cnsrvtrshpRemovalDtlsDao.getrmvldtls(cnsrvtrshpRemovalInDto);
					for (ConservatorshipRowOneDto rowcSub15sig01Dto : conservRmvlSaveReq
							.getConservatorshipRowOneDtos()) {
						if (!TypeConvUtil.isNullOrEmpty(rowcSub15sig01Dto.getCdSysDataActionOutcome())) {
							removalReasonInsUpdDelInDto.setReqFuncCd(rowcSub15sig01Dto.getCdSysDataActionOutcome());
							if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(conservRmvlSaveReq.getReqFuncCd())) {
								removalReasonInsUpdDelInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
							} else {
								removalReasonInsUpdDelInDto
										.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
							}
							removalReasonInsUpdDelInDto.setTsLastUpdate(rowcSub15sig01Dto.getTsLastUpdate());
							removalReasonInsUpdDelInDto.setCdRemovalReason(rowcSub15sig01Dto.getCdRemovalReason());
							removalReasonInsUpdDelOutDto = removalReasonInsUpdDelDao
									.removalReasonInsUpdDel(removalReasonInsUpdDelInDto);
							if (!TypeConvUtil.isNullOrEmpty(removalReasonInsUpdDelOutDto)
									&& RetVal.equals(ServiceConstants.FND_SUCCESS)) {
								RetVal = ServiceConstants.FND_SUCCESS;
							}
						}
					}
					for (ConservatorshipRowTwoDto rowcSub15sig02Dto : conservRmvlSaveReq
							.getConservatorshipRowTwoDtos()) {
						if ((!TypeConvUtil.isNullOrEmpty(rowcSub15sig02Dto.getCdSysDataActionOutcome()))
								&& (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
							removalCharChildInsUpdDelInDto.setReqFuncCd(rowcSub15sig02Dto.getCdSysDataActionOutcome());
							if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(conservRmvlSaveReq.getReqFuncCd())) {
								removalCharChildInsUpdDelInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
							} else {
								removalCharChildInsUpdDelInDto
										.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
							}
							if (!TypeConvUtil.isNullOrEmpty(rowcSub15sig02Dto.getCdRemovChildChar())
									&& !TypeConvUtil.isNullOrEmpty(rowcSub15sig02Dto.getCIndCharChildCurrent())) {
								removalCharChildInsUpdDelInDto
										.setCdRemovChildChar(rowcSub15sig02Dto.getCdRemovChildChar());
								Date startDate = rowcSub15sig02Dto.getTsLastUpdate();
								removalCharChildInsUpdDelInDto.setTsLastUpdate(startDate);
								removalCharChildInsUpdDelOutDto = removalCharChildInsUpdDelDao
										.removalCharChildInsUpdDel(removalCharChildInsUpdDelInDto);
								if (!TypeConvUtil.isNullOrEmpty(removalCharChildInsUpdDelOutDto)
										&& RetVal.equals(ServiceConstants.FND_SUCCESS)) {
									RetVal = ServiceConstants.FND_SUCCESS;
								}
							}
						}
					}
					for (ConservatorshipRowThreeDto rowCsub15sig03Dto : conservRmvlSaveReq
							.getConservatorshipRowThreeDtos()) {
						if ((!TypeConvUtil.isNullOrEmpty(rowCsub15sig03Dto.getCdSysDataActionOutcome()))
								&& (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
							if (!TypeConvUtil.isNullOrEmpty(conservRmvlSaveRes)) {
								removalCharAdultInsUpdDelInDto
										.setReqFuncCd(rowCsub15sig03Dto.getCdSysDataActionOutcome());
								if (ServiceConstants.REQ_FUNC_CD_ADD
										.equalsIgnoreCase(conservRmvlSaveReq.getReqFuncCd())) {
									removalCharAdultInsUpdDelInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
								} else {
									removalCharAdultInsUpdDelInDto
											.setIdEvent(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdEvent());
								}
								removalCharAdultInsUpdDelInDto
										.setCdRemovAdultChar(rowCsub15sig03Dto.getCdRemovAdultChar());
								removalCharAdultInsUpdDelInDto.setTsLastUpdate(rowCsub15sig03Dto.getTsLastUpdate());
								removalCharAdultInsUpdDelDao.removalCharAdultInsUpdDel(removalCharAdultInsUpdDelInDto);
								if (!TypeConvUtil.isNullOrEmpty(removalCharChildInsUpdDelOutDto)
										&& RetVal.equals(ServiceConstants.FND_SUCCESS)) {
									RetVal = ServiceConstants.FND_SUCCESS;
								}
							}
						}
					}
				}
			}
		}
		if ((conservRmvlSaveReq.getIndSearchChange() != null) && (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
			personHomeRemovalInsInDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
			personHomeRemovalInsInDto.setIdEvent(conservRmvlSaveRes.getIdEvent());
			personHomeRemovalInsInDto.setSysIdNewEvent(conservRmvlSaveRes.getIdEvent());
			personHomeRemovalInsOutDto = personHomeRemovalInsDao.personHomeRemovalAUD(personHomeRemovalInsInDto);
			if (TypeConvUtil.isNullOrEmpty(personHomeRemovalInsOutDto) && RetVal.equals(ServiceConstants.FND_SUCCESS)) {
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}

		if ((ServiceConstants.FND_SUCCESS.equals(RetVal))
				&& ((ServiceConstants.STATUS_NEW.equalsIgnoreCase(conservRmvlSaveReq.getCdEventStatus())))) {
			closeOpenStageInputDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
			closeOpenStageInputDto.setCdStage(ServiceConstants.SUBCARE_STAGE);
			closeOpenStageInputDto.setCdStageOpen(ServiceConstants.SUBCARE_STAGE);
			closeOpenStageInputDto.setCdStageReasonClosed(ServiceConstants.SUBCARE_STAGE);
			closeOpenStageInputDto.setCdStageProgram(ServiceConstants.STAGE_PROGRAM);
			closeOpenStageInputDto.setIdStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
			closeOpenStageInputDto.setIdPerson(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson());
			closeOpenStageInputDto.setNmPersonFull(conservRmvlSaveReq.getNmPersonFull());
			closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.YES);
			closeOpenStageInputDto.setDtStageStart(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
			closeOpenStageInputDto.setScrIdPrimChild(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
			closeOpenStageOutputDto = closeOpenStageService.closeOpenStage(closeOpenStageInputDto);
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto) && RetVal.equals(ServiceConstants.FND_SUCCESS)) {
				RetVal = ServiceConstants.FND_SUCCESS;
				idStageCP = closeOpenStageOutputDto.getIdStage();
			}
		}
		//
		if ((ServiceConstants.FND_SUCCESS.equals(RetVal))
				&& (ServiceConstants.STATUS_NEW.equalsIgnoreCase(conservRmvlSaveReq.getCdEventStatus()))) {
			closeOpenStageInputDto.setReqFuncCd(conservRmvlSaveReq.getReqFuncCd());
			closeOpenStageInputDto.setCdStage(ServiceConstants.SUBCARE_STAGE);
			closeOpenStageInputDto.setCdStageOpen(ServiceConstants.FAMILY_SUB_STAGE);
			closeOpenStageInputDto.setCdStageReasonClosed(ServiceConstants.SUBCARE_STAGE);
			closeOpenStageInputDto.setCdStageProgram(ServiceConstants.STAGE_PROGRAM);
			closeOpenStageInputDto.setIdStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
			closeOpenStageInputDto.setIdPerson(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson());
			closeOpenStageInputDto.setDtStageStart(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
			closeOpenStageInputDto.setScrIdPrimChild(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
			closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.YES);
			closeOpenStageOutputDto = closeOpenStageService.closeOpenStage(closeOpenStageInputDto);
			if (!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto) && RetVal.equals(ServiceConstants.FND_SUCCESS)) {
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			pCCMNE1DInputRec.setIdCase(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdCase());
			try {
				resource01 = stageStartCloseDao.getStageDtls(pCCMNE1DInputRec);
			} catch (DataNotFoundException de) {
				log.error(de.getMessage());
				RetVal = ServiceConstants.FND_FAIL;
			}
			if (!TypeConvUtil.isNullOrEmpty(resource01) && resource01.size() > ServiceConstants.Zero) {
				RetVal = ServiceConstants.FND_SUCCESS;
				for (StageStartCloseOutDto ccmne1doDto : resource01) {
					if (ccmne1doDto.getCdStage().equalsIgnoreCase(ServiceConstants.FAMILY_SUB_STAGE)
							&& (TypeConvUtil.isNullOrEmpty(ccmne1doDto.getDtStageClose()))) {
						long diff = ServiceConstants.Zero;
						long days = ServiceConstants.Zero;
						if (!TypeConvUtil.isNullOrEmpty(ccmne1doDto.getDtStageStart()) && !TypeConvUtil
								.isNullOrEmpty(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval())) {
							diff = ccmne1doDto.getDtStageStart().getTime()
									- conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval().getTime();
							days = TimeUnit.MILLISECONDS.toDays(diff);
						}
						if (days > ServiceConstants.Zero) {
							stageUpdByStageStartIdInDto.setIdStage(ccmne1doDto.getIdStage());
							stageUpdByStageStartIdInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							stageUpdByStageStartIdInDto
									.setDtStageStart(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
							try {
								rowcount = stageUpdByStageStartIdDao.setStgDetails(stageUpdByStageStartIdInDto);
							} catch (DataNotFoundException de) {
								log.error(messageSource.getMessage("Cinvc4d.not.updated", null, Locale.US));
								RetVal = ServiceConstants.FND_FAIL;
							}
							if (rowcount > ServiceConstants.Zero) {
								RetVal = ServiceConstants.FND_SUCCESS;
							}
						}
					}
				}
			}
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			long diff = ServiceConstants.Zero;
			long days = ServiceConstants.Zero;
			if (!TypeConvUtil.isNullOrEmpty(rmvlDtBeforeUpdate)
					&& !TypeConvUtil.isNullOrEmpty(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval())) {
				diff = rmvlDtBeforeUpdate.getTime()
						- conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval().getTime();
				days = TimeUnit.MILLISECONDS.toDays(diff);
			}
			if (days > ServiceConstants.Zero) {
				stageStagePersonLinkStgRoleInDto
						.setIdPriorStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
				stageStagePersonLinkStgRoleInDto
						.setIdPerson(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
				stageStagePersonLinkStgRoleInDto.setCdStage(ServiceConstants.SUBCARE_STAGE);
				stageStagePersonLinkStgRoleInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
				try {
					resource84 = stageStagePersonLinkStgRoleDao.getStageID(stageStagePersonLinkStgRoleInDto);
				} catch (DataNotFoundException de) {
					log.error(messageSource.getMessage("getStageID.not.found.ulIdCase", null, Locale.US));
					RetVal = ServiceConstants.FND_FAIL;
				}
				if (!TypeConvUtil.isNullOrEmpty(resource84) && RetVal.equals(ServiceConstants.FND_SUCCESS)) {
					RetVal = ServiceConstants.FND_SUCCESS;
					stageUpdByStageStartIdInDto.setIdStage(resource84.get(ServiceConstants.Zero).getIdStage());
					stageUpdByStageStartIdInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					stageUpdByStageStartIdInDto
							.setDtStageStart(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
					try {
						rowcount = stageUpdByStageStartIdDao.setStgDetails(stageUpdByStageStartIdInDto);
					} catch (DataNotFoundException de) {
						log.error(messageSource.getMessage("Cinvc4d.not.updated", null, Locale.US));
						RetVal = ServiceConstants.FND_FAIL;
					}
					if (rowcount > ServiceConstants.Zero) {
						RetVal = ServiceConstants.FND_SUCCESS;
					}
				}
			}
		}
		log.debug("Exiting method callConservRmvlSaveService in ConservRmvlSaveServiceImpl");
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)
				&& ServiceConstants.ZERO.equals(conservRmvlSaveRes.getIdEvent())) {
			conservRmvlSaveRes.setIdEvent(conservRmvlSaveReq.getIdEvent());
		}
		// Create a new child and Alert message, if the child is removing for
		// first
		// time. or removal date changed for existing records
		if (ServiceConstants.WINDOW_MODE_NEW.equalsIgnoreCase(conservRmvlSaveReq.getSysCdWinMode())
				|| ServiceConstants.WINDOW_MODE_NEW_USING.equalsIgnoreCase(conservRmvlSaveReq.getSysCdWinMode())
				|| (rmvlDtBeforeUpdate
						.compareTo(conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval()) != 0)) {
			// create new child plan, if there is no existing child plan for the
			// victim
			if (ServiceConstants.WINDOW_MODE_NEW.equalsIgnoreCase(conservRmvlSaveReq.getSysCdWinMode())
					|| ServiceConstants.WINDOW_MODE_NEW_USING.equalsIgnoreCase(conservRmvlSaveReq.getSysCdWinMode())) {
				createNewChildPlan(conservRmvlSaveReq, idStageCP);
			}
			CommonHelperReq commonHelperReq = new CommonHelperReq();
			commonHelperReq.setIdStage(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage());
			CommonHelperRes commonHelperRes = approvalDao.getPrimaryWorkerIdForStage(commonHelperReq);

			// Create Alert to notify the Primary assigned worker that an
			// Initial Child Plan is due.
			alertService.createAlert(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdStage(),
					commonHelperRes.getUlIdPerson(), conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim(),
					conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdCase(),
					ServiceConstants.INITIAL_CHILD_PLAN_DUE,
					conservRmvlSaveReq.getConservatorshipRowZeroDto().getDtRemoval());
		}

		conservRmvlSaveRes.setIdStageFSU(closeOpenStageOutputDto.getIdStage());

		return conservRmvlSaveRes;
	}

	/**
	 * Method Name: createNewChildPlan Method Description: This Method will
	 * create a new child plan for the victim who is removed from home, if there
	 * is no child plan exist for the person
	 * 
	 * @param idPerson
	 */
	private void createNewChildPlan(ConservRmvlSaveReq conservRmvlSaveReq, Long idStage) {
		Boolean chidPlanIsExist = childPlanDtlDao
				.chckVictimHasExistingCP(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
		if (!chidPlanIsExist) {
			PostEventIPDto postEventIPDto = new PostEventIPDto();
			Date date = new Date(System.currentTimeMillis());
			ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
			postEventIPDto.setCdTask(ServiceConstants.CHILD_PLAN_TASK_CODE_SUB);
			postEventIPDto.setIdPerson(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson());
			postEventIPDto.setIdStage(idStage);
			postEventIPDto.setDtEventOccurred(date);
			postEventIPDto.setUserId(String.valueOf(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson()));
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_NEW);
			postEventIPDto.setDtEventOccurred(date);
			postEventIPDto.setTsLastUpdate(date);
			postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_CSP);
			//added the code as the P2 child plan was not writing to event person link table
			List<PostEventDto> postEventDtos = new ArrayList<>();
			PostEventDto postEventDto = new PostEventDto();
			postEventDto.setIdPerson(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
			postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);			
			postEventDtos.add(postEventDto);
			postEventIPDto.setPostEventDto(postEventDtos);
			
			PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
			ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = new ChildPlanOfServiceDtlDto();
			if (!ObjectUtils.isEmpty(postEventOPDto.getIdEvent())) {
				childPlanOfServiceDtlDto.setIdChildPlanEvent(postEventOPDto.getIdEvent());
				childPlanOfServiceDtlDto.setIdPerson(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdVictim());
				childPlanOfServiceDtlDto.setIdCase(conservRmvlSaveReq.getConservatorshipRowZeroDto().getIdCase());
				childPlanOfServiceDtlDto.setCdCspPlanType(ServiceConstants.CHILD_PLAN_INITIAL);
				// create child plan for the vicitim.
				childPlanDtlDao.saveChildPlanDtl(childPlanOfServiceDtlDto,
						String.valueOf(conservRmvlSaveReq.getEventRowUpdateOneDto().getIdPerson()), false);
			}
		}
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void createCalendarEventForFSU(String hostName, Date dtRemoval, Long idStage) {
		SelectStageDto selectStageDto = caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtRemoval); // Now use removal date to
								// calculate the invite
								// time.
		cal.add(Calendar.DATE, 21);// Adding 21 day's to the
									// removal date
		Date startDate = cal.getTime();
		// below logic is to generate the end time of the day
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);
		Date endDate = cal.getTime();

		/*
		 * check if new date (removal date +21) is after current date and there
		 * is no approved initial FSNA for the stage.
		 */
		if (startDate.after(new Date())) {
			List<String> emailAndPasswordList = outlookUtil.getImpactEmailAndPassword();
			ExchangeService exchangeService = outlookUtil.getOutlookExchangeService(emailAndPasswordList.get(0),
					emailAndPasswordList.get(1));
			AppointmentDto appointmentDto = new AppointmentDto();

			DateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY_FORMAT);
			String formatDate = dateFormat.format(startDate);

			// Initial FSNA Due MM/DD/YYYY for Last Name, First Initial.
			appointmentDto
					.setAppointmentSubject("Initial FSNA Due " + formatDate + " for " + selectStageDto.getNmCase());
			appointmentDto.setIndAllDayEvent(Boolean.TRUE);
			List<String> emailAddressList = new ArrayList<String>();
			if (!PROD_HOST_NAME.equalsIgnoreCase(hostName)) {
				emailAddressList.add(emailConfigBundle.getString("AssignCVSRemoval.emailId." + hostName));
			} else {
				// get all the primary and secondary workers for FSU stage.
				emailAddressList = employeeDao.getEmployeeEmailAddressList(selectStageDto.getIdStage());
			}
			appointmentDto.setReceiverEmailAddress(emailAddressList);
			appointmentDto.setDtStartDate(startDate);
			appointmentDto.setDtEndDate(endDate);

			appointmentDto.setRemainderMins(10080);
			// Create calendar event and the remainder is sent to (due date-7)
			outlookUtil.sendAppointment(appointmentDto, exchangeService);
		}
	}
}
