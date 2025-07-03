package us.tx.state.dfps.service.placement.serviceimpl;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.web.WebConstants;

import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_64;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_67;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_80;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_86;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_QP;
import static us.tx.state.dfps.service.common.CodesConstant.CPLMNTYP_030;
import static us.tx.state.dfps.service.common.CodesConstant.CPLMNTYP_032;
import static us.tx.state.dfps.service.common.CodesConstant.CPLMNTYP_040;
import static us.tx.state.dfps.service.common.CodesConstant.CPLMNTYP_050;
import static us.tx.state.dfps.service.common.CodesConstant.CPLMNTYP_060;
import static us.tx.state.dfps.service.common.ServiceConstants.BOOLEAN_FALSE;
import static us.tx.state.dfps.service.common.ServiceConstants.BOOLEAN_TRUE;
import static us.tx.state.dfps.service.common.ServiceConstants.FFPSA_BATCH_PARAM;
import static us.tx.state.dfps.service.common.ServiceConstants.FFPSA_BATCH_PROGRAM;
import static us.tx.state.dfps.service.common.ServiceConstants.MAX_DATE;
import static us.tx.state.dfps.service.common.ServiceConstants.N;
import static us.tx.state.dfps.service.common.ServiceConstants.SQL_DATE_FORMAT;
import static us.tx.state.dfps.service.common.ServiceConstants.Y;


import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.common.dto.CommonDto;
import us.tx.state.dfps.common.dto.ContractDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.PersonAssignedIdToDoDto;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.domain.*;

import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.phoneticsearch.util.JNDIUtil;
import us.tx.state.dfps.service.admin.dao.AdoptionSubsidyDao;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dao.EmpSecClassLinkDao;
import us.tx.state.dfps.service.admin.dao.UnitEmpLinkDao;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.CommonTodoService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;

import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.ApproversDao;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.request.SavePlacementDetailReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.HmEligibilityReq;
import us.tx.state.dfps.service.common.response.HmEligibilityRes;

import us.tx.state.dfps.service.common.response.SavePlacementDtlRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.contacts.dao.SysNbrValidationDao;
import us.tx.state.dfps.service.email.EmailNotificationDto;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.heightenedmonitoring.dao.HeightenedMonitoringDao;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.legalstatus.service.LegalStatusService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.MedicaidUpdateService;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dao.RunawayMissingChildDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.placement.dto.PlacementDtlGpDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.placement.service.TemporaryAbsenceService;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dao.BatchParametersDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.ChildBillOfRightsDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dao.*;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.web.todo.bean.ToDoStagePersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.NbrValidationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ValidationMsgDto;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;


import static us.tx.state.dfps.service.common.ServiceConstants.*;

import java.sql.Timestamp;

import static us.tx.state.dfps.common.web.ToDoConstants.APP_SUB_PLCMT;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_APP;
/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PlacementServiceImpl Oct 11, 2017- 6:57:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PlacementServiceImpl implements PlacementService {

	@Autowired
	private PlacementDao placementDao;

	@Autowired
	private CodesDao codesDao;

	@Autowired
	private BatchParametersDao batchParametersDao;

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	private StageEventStatusCommonService checkStageEventStatus;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	EventDao eventDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	SrvreferralslDao srvreferralsDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	MedicaidUpdateService medicaidUpdateService;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	LegalStatusDao legalStatusDao;

	@Autowired
	EligibilityDao eligibilityDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	SysNbrValidationDao nbrValidationDao;

	@Autowired
	AdoptionSubsidyDao adoptionSubsidyDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	EmpSecClassLinkDao empSecClassLinkDao;

	@Autowired
	UnitEmpLinkDao unitEmpLinkDao;

	@Autowired
	ApprovalDao approvalDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	ApprovalEventLinkDao approvalEventLinkDao;

	@Autowired
	LegalStatusService legalStatusService;

	@Autowired
	CommonTodoService commonTodoService;

	@Autowired
	PersonLocPersonDao personLOCPersonDao;

	@Autowired
	TemporaryAbsenceService temporaryAbsenceService;

	@Autowired
	TemporaryAbsenceDao temporaryAbsenceDao;

	@Autowired
	RunawayMissingChildDao runawayMissingChildDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	HeightenedMonitoringDao heightenedMonitoringDao;

	@Autowired
	ApproversDao approversDao;

	@Autowired
	ServicePackageDao servicePackageDao;

	private static final Logger log = Logger.getLogger(PlacementServiceImpl.class);

	public static final String TYPE_NON_PAID = "040";


	/**
	 * Method Name: checkLegalAction Method Description:This method Checks for
	 * the proper Legal Action
	 *
	 * @param placementValueDto
	 * @return Boolean @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkLegalAction(PlacementValueDto placementValueDto) {

		Boolean boolLegalAction = ServiceConstants.FALSEVAL;
		boolLegalAction = placementDao.checkLegalAction(placementValueDto);
		return boolLegalAction;
	}

	/**
	 * Method Name: fetchPlacement Method Description:This method retrieves
	 * Placement Details from the database using idPlcmtEvent.
	 *
	 * @param idPlcmtEvent
	 * @return PlacementDto @;
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementDto fetchPlacement(Long idPlcmtEvent) {

		return processPlacement(placementDao.selectPlacement(idPlcmtEvent));

	}

	/**
	 * Method Name: fetchLatestPlacement Method Description:This method
	 * retrieves Latest Placement for the given
	 *
	 * @param stageId
	 * @return PlacementDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementDto fetchLatestPlacement(Long stageId) {

		return placementDao.selectLatestPlacement(stageId);

	}

	/**
	 * Method Name: findActivePlacements Method Description:Fetches the most
	 * recent open Active Placement for the idPerson
	 *
	 * @param idPerson
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> findActivePlacements(Long idPerson) {
		return placementDao.findActivePlacements(idPerson);

	}

	/**
	 * Method Name: checkPlcmtDateRange Method Description:This method checks if
	 * there is any Placement for the Child in the Range of Placement Start
	 * Date.
	 *
	 * @param idPerson
	 * @param dtPlcmtStart
	 * @return List<PlacementValueDto> @
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> checkPlcmtDateRange(Long idPerson, Date dtPlcmtStart) {
		return placementDao.checkPlcmtDateRange(idPerson, dtPlcmtStart);

	}

	/**
	 * Method Name: findAllPlacementsForStage Method Description:This method
	 * returns all the placements for the given Stage
	 *
	 * @param stageId
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> findAllPlacementsForStage(Long stageId) {

		return placementDao.findAllPlacementsForStage(stageId);

	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> findAllQTRPPlacements(Long childID) {

		return placementDao.findAllQTRPPlacements(childID);

	}

	/**
	 * Method Name: getContractedSvcLivArr Method Description:This method
	 * returns an active SIL contract service to filter the living arrangement
	 * for SSCC SIL placement
	 *
	 * @param idResource
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getContractedSvcLivArr(Long idResource) {

		return placementDao.getActiveSilContract(idResource);

	}

	/**
	 * Method Name: getResourceSvcLivArr Method Description:This method returns
	 * List of SIL resource services to filter the living arrangement for SIL
	 * placement
	 *
	 * @param idResource
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getResourceSvcLivArr(Long idResource) {

		return placementDao.getSILRsrsSvc(idResource);

	}

	/**
	 * Method Name: getActiveSilContract Method Description:This method returns
	 * an active SIL
	 *
	 * @param idResource
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getActiveSilContract(Long idResource) {
		return placementDao.getAllContractPeriods(idResource);

	}

	/**
	 * Method Name: getAllContractPeriods Method Description:This method returns
	 * all contract periods
	 *
	 * @param idResource
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getAllContractPeriods(Long idResource) {
		return placementDao.getAllContractPeriods(idResource);

	}

	/**
	 * Method Name: getCorrespondingPlacement Method Description:This method
	 * returns List corresponding parent placement for the child within a stage
	 *
	 * @param stageId
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getCorrespondingPlacement(Long stageId) {

		return placementDao.getCorrespondingPlacement(stageId);

	}

	/**
	 * Method Name: getContractCounty Method Description:This method gets
	 * address county in SIL Contract services
	 *
	 * @param placementValueDto
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getContractCounty(PlacementValueDto placementValueDto) {

		return placementDao.getContractCounty(placementValueDto);

	}

	/**
	 * Method Name: getAddCntInSilRsrc Method Description:This method gets
	 * address county in SIL resource services
	 *
	 * @param idResource
	 * @param szCountyCode
	 * @param livArr
	 * @return List<PlacementValueDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto> getAddCntInSilRsrc(Long idResource, String szCountyCode, String livArr) {

		return placementDao.getAddCntInSilRsrc(idResource, szCountyCode, livArr);

	}

	/**
	 * Method Name: isSSCCPlacement Method Description:This method checks to see
	 * if the placement is a SSCC placement
	 *
	 * @param idPlcmtEvent
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isSSCCPlacement(Long idPlcmtEvent) {
		Boolean bPlacementSSCC = ServiceConstants.FALSEVAL;
		if (!ObjectUtils.isEmpty(idPlcmtEvent) && idPlcmtEvent > 0)
			bPlacementSSCC = placementDao.isSSCCPlacement(idPlcmtEvent);
		return bPlacementSSCC;

	}

	/**
	 * Method Name: createExceptCareAlert Method Description:This method to
	 * create an alert to do to staff who created Exception Care
	 *
	 * @param szNmStage
	 * @param szCdStage
	 * @param userId
	 * @param stageId
	 * @param szCdTask
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long createExceptCareAlert(Long idEvent, String szCdTask, Long stageId, Long userId, String szCdStage,
			String szNmStage) {
		Long creater = ServiceConstants.ZERO_VAL;
		creater = placementDao.getExeptCareCreaters(idEvent);
		StringBuilder alertdesc = new StringBuilder();
		alertdesc.append(ServiceConstants.SSCC_EXCEPTIONAL_CARE);
		alertdesc.append(szNmStage);
		alertdesc.append(ServiceConstants.MODIFIED);
		String toDoDesc = (alertdesc.toString());
		if (creater != userId) {

			createRdccToDo(toDoDesc, toDoDesc, ServiceConstants.CD_TODO_INFO_ALERT, creater, userId, stageId, idEvent);
		}
		return creater;

	}

	/**
	 *
	 * Method Name: createRdccToDo Method Description:This method retrieves data
	 * from todocommunication method
	 *
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @
	 */
	private void createRdccToDo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Long idPrsnAssgn,
			Long idUser, Long idStage, Long idEvent) {

		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		// todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		mergeSplitToDoDto.setDtTodoCfDueFrom(null);
		mergeSplitToDoDto.setIdTodoCfPersCrea(idUser);
		mergeSplitToDoDto.setIdTodoCfStage(idStage);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn.intValue());
		mergeSplitToDoDto.setIdTodoCfPersWkr(idUser);
		if (toDoDesc != null)
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * Method Name: isActSsccCntrctExist Method Description:This method gets
	 * active SSCC contract services for the catchment area
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto isActSsccCntrctExist(PlacementReq placeReq) {

		return placementDao.isActSsccCntrctExist(placeReq);

	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:This method
	 * gets valid child placement referral information for the stage id (an
	 * active referral here is not base on the status, it's base on the referral
	 * recorded and discharged dates.
	 *
	 * @param stageId
	 * @return PlacementValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto getActiveChildPlcmtReferral(Long stageId) {
		return placementDao.getActiveChildPlcmtReferral(stageId);
	}

	/**
	 * Method Name: updateIndPlcmtSSCC Method Description:This method updates
	 * indicator placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateIndPlcmtSSCC(PlacementValueDto placementValueDto) {

		return placementDao.updateIndPlcmtSSCC(placementValueDto);

	}

	/**
	 * Method Name: updateIdPlcmtSSCC Method Description:This method updates id
	 * placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateIdPlcmtSSCC(PlacementValueDto placementValueDto) {

		return placementDao.updateIdPlcmtSSCC(placementValueDto);

	}

	/**
	 * Method Name: updateChildPlanDue Method Description:This method updates id
	 * placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateChildPlanDue(PlacementValueDto placementValueDto) {

		return placementDao.updateChildPlanDue(placementValueDto);

	}

	/**
	 * Method Name: updateIndEfcActive Method Description:This method updates
	 * indicator EFC Active
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateIndEfcActive(PlacementValueDto placementValueDto) {

		return placementDao.updateIndEfcActive(placementValueDto);

	}

	/**
	 * Method Name: updateIndEfc Method Description:This method updates
	 * indicator EFC
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateIndEfc(PlacementValueDto placementValueDto) {

		return placementDao.updateIndEfc(placementValueDto);

	}

	/**
	 * Method Name: updateIndLinkedPlcmtData Method Description: This method
	 * updates indicator linked placement data
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto updateIndLinkedPlcmtData(PlacementValueDto placementValueDto) {

		return placementDao.updateIndLinkedPlcmtData(placementValueDto);

	}

	/**
	 * Method Name: getIndPlcmtSSCC Method Description:This method gets indicate
	 * placement sscc
	 *
	 * @param idReferral
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto getIndPlcmtSSCC(Long idReferral) {

		return placementDao.getIndPlcmtSSCC(idReferral);

	}

	/**
	 * Method Name: getActiveSSCCReferral Method Description:This method gets
	 * active sscc referral for stage id
	 *
	 * @param stageId
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto getActiveSSCCReferral(Long stageId) {
		PlacementValueDto placmentDto = new PlacementValueDto();
		List<PlacementValueDto> placmentDtoList = placementDao.getActiveSSCCReferral(stageId);
		if (!CollectionUtils.isEmpty(placmentDtoList)) {
			placmentDto = placmentDtoList.get(0);
		}
		return placmentDto;

	}

	/**
	 * Method Name: childLastestPlcmtSSCC Method Description:This method gets
	 * child latest placement sscc
	 *
	 * @param stageId
	 * @param dtPlcmtStart
	 * @return PlacementValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto childLastestPlcmtSSCC(Long stageId, Date dtPlcmtStart) {

		return placementDao.childLastestPlcmtSSCC(stageId, dtPlcmtStart);

	}

	/**
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets
	 * the latest child plan initiate info within a stage at the time of
	 * approval of an sscc placement
	 *
	 * @param idReferral
	 * @return PlacementValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementValueDto getChildPlanInitiateInfo(Long idReferral) {

		return placementDao.getChildPlanInitiateInfo(idReferral);

	}

	/**
	 * Method Name: getExceptionalCareDaysUsed Method Description:This method
	 * gets the number of exceptional care used in a contract period
	 *
	 * @param idReferral
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getExceptionalCareDaysUsed(Long idReferral) {

		return placementDao.getExcpCareDaysUsed(idReferral);

	}

	/**
	 * Method Description: The method is used for the approval invalidation as
	 * applicable
	 *
	 * @param savePlacementDtlReq
	 *            - Placement Detail Request
	 * @param idEvent
	 *            - Id Event
	 */
	private void invalidateApproval(SavePlacementDetailReq savePlacementDtlReq, Long idEvent) {

		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		if (!CollectionUtils.isEmpty(savePlacementDtlReq.getIdEvent())
				&& !ObjectUtils.isEmpty(savePlacementDtlReq.getIdEvent().get(1))
				&& savePlacementDtlReq.getIdEvent().get(1) > 0) {
			approvalCommonInDto.setIdEvent(savePlacementDtlReq.getIdEvent().get(1));
			ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
			approvalCommonOutDto = approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);
		}
		EventDto eventDto = new EventDto();
		// If the approver is doing the save, then the events should not
		// be demoted
		if (!savePlacementDtlReq.getSysNbrReserved1()) {
			eventDto.setIdEvent(savePlacementDtlReq.getIdEvent().get(1));
			eventDto.setCdEventStatus(ServiceConstants.COMPLETE);
		} else {
			eventDto.setIdEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
			eventDto.setCdEventStatus(savePlacementDtlReq.getPostEventIPDto().getCdEventStatus());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto()) && !ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getEventDescr()))
			eventDto.setEventDescr(savePlacementDtlReq.getPostEventIPDto().getEventDescr());
		// CallCCMN62D
		idEvent = eventDao.updateEventForPlacement(eventDto, savePlacementDtlReq.getReqFuncCd());
	}

	/**
	 * Method Description: The method will check if there are any open ADO
	 * placements for the child. If there are, then an indicator will be passed
	 * to the placement save, which will allow the Save to happen and later the
	 * ADO placement will be ended
	 *
	 * @param savePlacementDtlReq
	 *            - Placement Detail Request
	 * @param placementDtoList
	 *            - The list of ADO Placements
	 * @return List<String>
	 */
	private List<String> checkForOpenADOPlacements(SavePlacementDetailReq savePlacementDtlReq,
			List<PlacementDto> placementADOList) {

		ResourceServiceInDto resourceServiceInDto = new ResourceServiceInDto();
		List<String> returnIndicatorsList = new ArrayList<>();
		String indEndADOPlacement = ServiceConstants.EMPTY_STRING;
		String indNewPlacementEndsPrior = ServiceConstants.EMPTY_STRING;
		resourceServiceInDto.setIdPlcmtChild(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
		// Legacy call of CSUB88D replaced. Retrieves all open ADO
		// placements for the child
		List<PlacementDto> placementDtoList = placementDao.getPlacementsByChildId(resourceServiceInDto);

		int i = 0;
		if (!CollectionUtils.isEmpty(placementDtoList)) {
			for (i = 0; i < placementDtoList.size(); i++) {
				if ((ServiceConstants.ADOPT.equalsIgnoreCase(placementDtoList.get(i).getCdStage())
						|| ServiceConstants.PAD.equalsIgnoreCase(placementDtoList.get(i).getCdStage()))
						&& (ObjectUtils.isEmpty(placementDtoList.get(i).getDtPlcmtEnd()) || ServiceConstants.MAX_DATE.equals(placementDtoList.get(i).getDtPlcmtEnd())
								)
						&& (ServiceConstants.ADOPTIVE_PLACEMENT.equalsIgnoreCase(placementDtoList.get(i).getCdPlcmtLivArr())
								|| ServiceConstants.NONFPS_ADOPT_HOME
								.equalsIgnoreCase(placementDtoList.get(i).getCdPlcmtLivArr()))) {
					// Not all setter done
					if (i < (placementDtoList.size() - 1)) {
						placementDtoList.get(i).setDtPlcmtEnd(placementDtoList.get(i + 1).getDtPlcmtStart());
					} else {
						placementDtoList.get(i).setDtPlcmtEnd(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart());
					}
					placementADOList.add(placementDtoList.get(i));
					/*
					 * Compare start dates between open ADO placement in closed ADO
					 * stage and new placement being saved on page. Only end open
					 * ADO placement when new placement start date being added is
					 * equal to or after start date of open ADO placement.
					 */

					// if the start date of new placement is after the start
					// date of existing open placement and the existing open ADO
					// placement is in a closed stage
					if ((savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
							.after(placementDtoList.get(i).getDtPlcmtStart()) || savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
							.equals(placementDtoList.get(i).getDtPlcmtStart()))
							&& !ObjectUtils.isEmpty(placementDtoList.get(i).getDtStageClosed())) {
						indEndADOPlacement = ServiceConstants.Y;
						indNewPlacementEndsPrior = ServiceConstants.N;
					}
					// if the start date of new placement is after the start
					// date of existing open placement and the existing open ADO
					// placement is in an open stage
					else if ((savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
							.after(placementDtoList.get(i).getDtPlcmtStart()) || savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
							.equals(placementDtoList.get(i).getDtPlcmtStart()))
							&& ObjectUtils.isEmpty(placementDtoList.get(i).getDtStageClosed())) {
						indEndADOPlacement = ServiceConstants.N;
						indNewPlacementEndsPrior = ServiceConstants.N;
					}
					// if the start date of new placement is BEFORE the start
					// date of existing open placement
					if (savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
							.before(placementDtoList.get(i).getDtPlcmtStart())) {
						indEndADOPlacement = ServiceConstants.N;
						// if new placement is added prior to open ADO and is
						// not ended - end date is null.
						if (ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd()) || ServiceConstants.MAX_DATE.equals(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd())) {
							indNewPlacementEndsPrior = ServiceConstants.N;
						} else {

							/*
							 * Make sure the new placement being added has an end
							 * date that is EQUAL TO or BEFORE the start date of the
							 * open ADO placement.
							 */
							if (savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd()
									.before(placementDtoList.get(i).getDtPlcmtStart()) || savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd()
									.equals(placementDtoList.get(i).getDtPlcmtStart())) {
								indNewPlacementEndsPrior = ServiceConstants.Y;
							}
						}
					}
				} /* end if */
			} /* end for loop */
		}

		if (!StringUtils.isEmpty(indEndADOPlacement)) {
			returnIndicatorsList.add(indEndADOPlacement);
		}
		if (!StringUtils.isEmpty(indNewPlacementEndsPrior)) {
			returnIndicatorsList.add(indNewPlacementEndsPrior);
		}
		return returnIndicatorsList;
	}

	/**
	 * Method Description: The method will populate the save request for the
	 * Placement Save/Update
	 *
	 * @param savePlacementDtlReq
	 *            - Placement Detail Request
	 * @return PlacementAUDDto
	 */
	private PlacementAUDDto populatePlacementSaveReq(SavePlacementDetailReq savePlacementDtlReq) {

		PlacementAUDDto placementAUDReq = new PlacementAUDDto();
		placementAUDReq.setTxtTrashBags(savePlacementDtlReq.getTxtTrashBags());
		placementAUDReq.setIndTrashBags(savePlacementDtlReq.getIndTrashBags());
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(savePlacementDtlReq.getReqFuncCd())
				&& ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {
			// callcaud45d
			placementAUDReq.setReqFuncCd(savePlacementDtlReq.getIndNewActualPlcmt());

		} else {
			placementAUDReq.setReqFuncCd(savePlacementDtlReq.getReqFuncCd());
		}
		placementAUDReq.setIndPrfrmValidation(savePlacementDtlReq.getIndPrfrmValidation());
		if (ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getIdEvent())
				&& 0L == savePlacementDtlReq.getPostEventIPDto().getIdEvent()) {
			placementAUDReq.setIdPlcmtEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
		} else {
			placementAUDReq.setIdPlcmtEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
			placementAUDReq.setTsLastUpdate(savePlacementDtlReq.getPlacementDtlGpDto().getTsLastUpdate());
		} /* end else */
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getIdStage())) {
			placementAUDReq.setIdStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getIdCase())) {
			placementAUDReq.setIdCase(savePlacementDtlReq.getIdCase());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtWriteHistory())) {
			placementAUDReq
			.setIndPlcmtWriteHistory(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtWriteHistory());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtAdult())) {
			placementAUDReq.setIdPlcmtAdult(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtAdult());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild())) {
			placementAUDReq.setIdPlcmtChild(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdContract())) {
			placementAUDReq.setIdContract(savePlacementDtlReq.getPlacementDtlGpDto().getIdContract());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcAgency())) {
			placementAUDReq.setIdRsrcAgency(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcAgency());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())) {
			placementAUDReq.setIdRsrcFacil(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtCity())) {
			placementAUDReq.setAddrPlcmtCity(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtCity());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtCnty())) {
			placementAUDReq.setAddrPlcmtCnty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtCnty());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtLn1())) {
			placementAUDReq.setAddrPlcmtLn1(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtLn1());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtLn2())) {
			placementAUDReq.setAddrPlcmtLn2(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtLn2());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtSt())) {
			placementAUDReq.setAddrPlcmtSt(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtSt());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtZip())) {
			placementAUDReq.setAddrPlcmtZip(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtZip());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtLivArr())) {
			placementAUDReq.setCdPlcmtLivArr(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtLivArr());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())) {
			placementAUDReq.setCdPlcmtRemovalRsn(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtActPlanned())) {
			placementAUDReq.setCdPlcmtActPlanned(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtActPlanned());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType())) {
			placementAUDReq.setCdPlcmtType(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtService())) {
			placementAUDReq.setCdPlcmtService(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtService());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtCaregvrDiscuss())) {
			placementAUDReq
			.setDtPlcmtCaregvrDiscuss(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtCaregvrDiscuss());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtChildDiscuss())) {
			placementAUDReq.setDtPlcmtChildDiscuss(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtChildDiscuss());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtChildPlan())) {
			placementAUDReq.setDtPlcmtChildPlan(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtChildPlan());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtSxVctmztnHistoryDiscuss())) {
			placementAUDReq.setDtSxVctmztnHistoryDiscuss(savePlacementDtlReq.getPlacementDtlGpDto().getDtSxVctmztnHistoryDiscuss());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndSxVctmztnHistoryDiscuss())) {
			placementAUDReq.setIndSxVctmztnHistoryDiscuss(savePlacementDtlReq.getPlacementDtlGpDto().getIndSxVctmztnHistoryDiscuss());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEducLog())) {
			placementAUDReq.setDtPlcmtEducLog(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEducLog());
		}
		/*
		 **
		 ** If the end date is null, set it to max date, then see if the end date
		 * is larger than the start date, if so skip the dam call and return the
		 * appropriate message
		 */

		if (ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd())) {
			placementAUDReq.setDtPlcmtEnd(ServiceConstants.GENERIC_END_DATE);
		} else {
			placementAUDReq.setDtPlcmtEnd(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtMeddevHistory())) {
			placementAUDReq
			.setDtPlcmtMeddevHistory(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtMeddevHistory());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtParentsNotif())) {
			placementAUDReq.setDtPlcmtParentsNotif(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtParentsNotif());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtPreplaceVisit())) {
			placementAUDReq
			.setDtPlcmtPreplaceVisit(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtPreplaceVisit());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtSchoolRecords())) {
			placementAUDReq
			.setDtPlcmtSchoolRecords(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtSchoolRecords());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getDtPlcmtPermEff())) {
			placementAUDReq.setDtPlcmtPermEff(savePlacementDtlReq.getDtPlcmtPermEff());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart())) {
			placementAUDReq.setDtPlcmtStart(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtContCntct())) {
			placementAUDReq.setIndPlcmtContCntct(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtContCntct());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtEducLog())) {
			placementAUDReq.setIndPlcmtEducLog(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtEducLog());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmetEmerg())) {
			placementAUDReq.setIndPlcmetEmerg(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmetEmerg());
		}
		// artf255991 : BR 4.13 T3C Placement Indicator Logic - New Placement Save
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndT3CPlcmt())) {
			placementAUDReq.setIndT3CPlcmet(savePlacementDtlReq.getPlacementDtlGpDto().getIndT3CPlcmt());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtNoneApply())) {
			placementAUDReq.setIndPlcmtNotApplic(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtNoneApply());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtSchoolDoc())) {
			placementAUDReq.setIndPlcmtSchoolDoc(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtSchoolDoc());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNbrPlcmtPhoneExt())) {
			placementAUDReq.setPlcmtPhoneExt(savePlacementDtlReq.getPlacementDtlGpDto().getNbrPlcmtPhoneExt());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNbrPlcmtTelephone())) {
			placementAUDReq.setPlcmtTelephone(savePlacementDtlReq.getPlacementDtlGpDto().getNbrPlcmtTelephone());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtAgency())) {
			placementAUDReq.setPlcmtAgency(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtAgency());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtContact())) {
			placementAUDReq.setPlcmtContact(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtContact());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtFacil())) {
			placementAUDReq.setPlcmtFacil(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtFacil());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtPersonFull())) {
			placementAUDReq.setPlcmtPersonFull(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtPersonFull());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtAddrComment())) {
			placementAUDReq.setTxtPlcmtAddrComment(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtAddrComment());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtDiscussion())) {
			placementAUDReq.setTxtPlcmtDiscussion(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtDiscussion());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtDocuments())) {
			placementAUDReq.setTxtPlcmtDocuments(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtDocuments());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtRemovalRsn())) {
			placementAUDReq.setTxtPlcmtRemovalRsn(savePlacementDtlReq.getPlacementDtlGpDto().getPlcmtRemovalRsn());
		}
		if (!CollectionUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtInfo())) {
			List<String> selectedCdplcmts = savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtInfo();
			for (String cdPlcmt : selectedCdplcmts) {
				setCdplcmtValue(cdPlcmt, placementAUDReq);
			}
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsnSubtype())) {
			placementAUDReq.setCdPlcmtRemovalRsnSubtype(
					savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsnSubtype());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getIdPerson())) {
			placementAUDReq.setIdLastUpdatePerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			if(ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(savePlacementDtlReq.getReqFuncCd())) {
				placementAUDReq.setIdCreatedPerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			}
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC())) {
			placementAUDReq.setIdRsrcSSCC(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtSSCC())) {
			placementAUDReq.setNmPlcmtSSCC(savePlacementDtlReq.getPlacementDtlGpDto().getNmPlcmtSSCC());
		}
		if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtStartEndDtDiff())) {
			placementAUDReq
			.setIndPlcmtStartEndDtDiff(savePlacementDtlReq.getPlacementDtlGpDto().getIndPlcmtStartEndDtDiff());
		}

		return placementAUDReq;
	}

	/**
	 * Method Description: The main Save method for the Placement Information
	 * page
	 *
	 * @param savePlacementDtlReq
	 *            - Placement Detail Request
	 * @param idEvent
	 *            - Id Event
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SavePlacementDtlRes saveOrUpdatePlacementDtl(SavePlacementDetailReq savePlacementDtlReq) {

		SavePlacementDtlRes savePlacementDtlRes = new SavePlacementDtlRes();
		StageTaskInDto inCheckStageEventStatusDto = new StageTaskInDto();
		inCheckStageEventStatusDto.setCdTask(savePlacementDtlReq.getPostEventIPDto().getCdTask());
		inCheckStageEventStatusDto.setIdStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());
		inCheckStageEventStatusDto.setReqFuncCd(savePlacementDtlReq.getReqFuncCd());
		Long idEvent = 0L;
		Integer isEventNew = 0;
		Date dtDtPlcmtEnd = null;
		Long idEligibilityWorker = 0L;
		String cdEligSelected = null;
		String indCloseADOPlcmt = ServiceConstants.N;
		String indNewPlcmtEndsPrior = ServiceConstants.N;
		Long idPALStage = 0L;
		Long idAdoptSubWorker = 0L;
		Long idHomeStage = 0L;
		Long openSlots = 0L;
		Long idFADRecruiter = 0L;
		Long idFADWorker = 0L;
		Long idPALWorker = 0L;
		Long nbrStagesOpen = 0L;
		TodoCreateOutDto toDoCreateOutput = new TodoCreateOutDto();
		ValidationMsgDto palTrainingNbrOutput = new ValidationMsgDto();
		ResourceDto resourceDto = new ResourceDto();
		String action = savePlacementDtlReq.getReqFuncCd();
		PlacementAUDDto placementAUDRes = new PlacementAUDDto();
		List<PlacementDto> placementDtoList = new ArrayList<>();

		Placement placementBeforeUpdate = null;
		boolean isExistingQRTPPlacement =  FALSEVAL;
		String cdLivingArrangementType = savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtLivArr();

		if(N.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {
			placementBeforeUpdate = placementDao
				.retrievePlacementByEventId(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
			//Condition for checking artf268446  If an existing placement's living arrangement is updated to QRTP
			 isExistingQRTPPlacement = ((!ObjectUtils.isEmpty(placementBeforeUpdate.getCdPlcmtLivArr()) && placementBeforeUpdate.getCdPlcmtLivArr().equals(cdLivingArrangementType)));
	}


		/**************************************************************************
		 ** (BEGIN): Common Function: ccmn06u ** Check Stage/Event common
		 * function
		 **************************************************************************/
		String eventStageStatus = checkStageEventStatus.checkStageEventStatus(inCheckStageEventStatusDto);
		if (eventStageStatus.equalsIgnoreCase(ServiceConstants.ARC_SUCCESS)) {
			// If the placement event status is PEND and the placement is being
			// saved, then the pending approval should be invalidated
			if (!savePlacementDtlReq.getApprovalMode() && (ServiceConstants.PEND).equalsIgnoreCase(savePlacementDtlReq.getCdEventStatus().get(2))) {
				savePlacementDtlRes.setIdEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
				invalidateApproval(savePlacementDtlReq, idEvent);
			}


			// Prevent staff from ending an adoptive placement if the placement
			// is linked to an active adoption assistance
			if ((ServiceConstants.ADOPTIVE_PLACEMENT).equalsIgnoreCase(cdLivingArrangementType)
					|| (ServiceConstants.NONFPS_ADOPT_HOME).equalsIgnoreCase(cdLivingArrangementType)
					&& !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd())) {
				// Call CSUB89D
				PlacementDtlGpDto placementDtlGpDto = placementDao
						.getDtActiveADO(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
				/*
				 ** If the dtDtadoptionEnd retrieved occurred after the
				 * dtDtPlcmtEnd, the dtDtadoptionEnd should have ended prior.
				 */
				if (!ObjectUtils.isEmpty(placementDtlGpDto) && placementDtlGpDto.getDtAdptSubEnd().compareTo(placementDtlGpDto.getDtAdptSubEffective()) != 0) {
					if (!ObjectUtils.isEmpty(placementDtlGpDto.getDtAdptSubEnd())) {
						if (placementDtlGpDto.getDtAdptSubEnd()
								.after(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd())) {
							ServiceConstants.MSG_ADPT_SUB_ACTIVE.toString();
						}
					}
				}
			}
			/*
			 * The Invalidate Approval function also has to be called for the
			 * Placement event if its status was pending. This needs to be done
			 * so that the record on the Approvers table is set from 'PEND' to
			 * 'INVD', which will give the standard Invalid Approval message
			 * when navigating on the now invalid Approval ToDo.
			 */
			if (!savePlacementDtlReq.getApprovalMode() && (ServiceConstants.PEND).equalsIgnoreCase(savePlacementDtlReq.getCdEventStatus().get(0))) {
				savePlacementDtlRes.setIdEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				if (!CollectionUtils.isEmpty(savePlacementDtlReq.getIdEvent())
						&& !ObjectUtils.isEmpty(savePlacementDtlReq.getIdEvent().get(1))
						&& savePlacementDtlReq.getIdEvent().get(1) > 0) {
					approvalCommonInDto.setIdEvent(savePlacementDtlReq.getIdEvent().get(1));
					ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
					pOutputMsg = approvalCommonService.InvalidateAprvl(approvalCommonInDto, pOutputMsg);
				}
			} // end if placement event is pending

			if ((ServiceConstants.PEND).equalsIgnoreCase(savePlacementDtlReq.getCdEventStatus().get(2))
					&& !ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(savePlacementDtlReq.getReqFuncCd())
					&& !savePlacementDtlReq.getSysNbrReserved1()) {
				EventDto eventDto = new EventDto();
				eventDto.setIdEvent(savePlacementDtlReq.getIdEvent().get(1));
				eventDto.setCdEventStatus(ServiceConstants.COMPLETE);
				if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto()) && !ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getEventDescr()))
					eventDto.setEventDescr(savePlacementDtlReq.getPostEventIPDto().getEventDescr());
				// CallCCMN62D
				idEvent = eventDao.updateEventForPlacement(eventDto, action);
			} else {
				ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
				savePlacementDtlReq.getPostEventIPDto().setTsLastUpdate(new Date());
				String cdCurrentEventStatus = savePlacementDtlReq.getCdEventStatus().get(0);

				if ((ServiceConstants.NEW).equalsIgnoreCase(cdCurrentEventStatus)
						|| ServiceConstants.EMPTY_STR.equalsIgnoreCase(cdCurrentEventStatus)) {
					/* Event Person Link information */
					isEventNew = 1;
					Long idPlacementChild = savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild();
					if (!ObjectUtils.isEmpty(idPlacementChild)) {
						PostEventDto postEventDto = new PostEventDto();
						List<PostEventDto> postEventDtoList = new ArrayList<>();
						postEventDto.setIdPerson(idPlacementChild);
						postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);

						postEventDtoList.add(postEventDto);
						savePlacementDtlReq.getPostEventIPDto().setPostEventDto(postEventDtoList);
					}

				}

				if (ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getIdEvent())
						|| 0L == savePlacementDtlReq.getPostEventIPDto().getIdEvent()) {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				} else {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
				}
				PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(savePlacementDtlReq.getPostEventIPDto(),
						serviceReqHeaderDto);
				idEvent = postEventOPDto.getIdEvent();
				if (!ObjectUtils.isEmpty(idEvent)) {
					savePlacementDtlRes.setIdEvent(idEvent);
				}

			}
		} else {
			ErrorDto errorDto = new ErrorDto();
			if (ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH.equals(eventStageStatus))
				errorDto.setErrorCode(Messages.MSG_SYS_EVENT_STS_MSMTCH);
			else if (ServiceConstants.MSG_SYS_MULT_INST.equals(eventStageStatus))
				errorDto.setErrorCode(Messages.MSG_SYS_MULT_INST);
			if (errorDto.getErrorCode() != 0)
				savePlacementDtlRes.setErrorDto(errorDto);
		}

		//FFPSA 138.28 Congregate care placement change may require changes to foster care eligibility
		boolean isChangeReqToFosterCareElig = false;

		if (0L != idEvent) {

			if (0L != savePlacementDtlReq.getPostEventIPDto().getIdEvent()
					|| !ObjectUtils.isEmpty(savePlacementDtlReq.getPostEventIPDto().getIdEvent())) {
				TodoDto toDoDto = new TodoDto();
				toDoDto.setIdTodoEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
				// Legacy call of CINV43D replaced. DAM names mentioned here for
				// quick reference and analysis
				srvreferralsDao.updateOrSaveToDO(toDoDto);
			}

			// Call CSES37D Placement simple retrieve
			if (1 == isEventNew) {
				dtDtPlcmtEnd = null;
			} else {
				// Legacy call of CSES37D replaced.
				Placement placementEntity = new Placement();
				placementEntity = placementDao.selectPlacement(idEvent);
				PlacementDto placementDto = processPlacement(placementEntity);
				dtDtPlcmtEnd = placementDto.getDtPlcmtEnd();
			}

			if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
					&& 0L != savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil()
					&& StringUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
					&& (ServiceConstants.FPS_CONTRACTED_PLCMT)
					.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType())) {

				// Legacy call of CRES04D replaced. Retrieves the Home
				// information for the passed resource ID
				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());

				// The below check will be by passed is the placement is a SSCC
				// placement
				if (ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC())
						|| 0L == savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC()) {
					if (!ObjectUtils.isEmpty(resourceDto) && (ServiceConstants.GENERAL_RES_CONTRACT
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()) || ServiceConstants.CFACTYP2_64
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {

						// Legacy call of CLSS67D replaced. Retrieve the
						// Contracts for the passed Resource ID
						List<ContractDto> contractDtlList = placementDao
								.getContractDtl(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());
						String validContract = ServiceConstants.EMPTY_STR;
						for (ContractDto contractDtl : contractDtlList) {

							PlacementDtlGpDto placementDtlGpDto = new PlacementDtlGpDto();
							placementDtlGpDto
									.setAddrPlcmtCnty(savePlacementDtlReq.getPlacementDtlGpDto().getAddrPlcmtCnty());

							placementDtlGpDto
									.setDtPlcmtStart(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart());
							placementDtlGpDto.setIdContract(contractDtl.getIdContract());

							// Legacy call of CLSCE9D replaced. Retrieves the
							// services offered by the Resource
							validContract = placementDao.getDistinctService(placementDtlGpDto);
							// This has to be checked with Yash. Seems the code
							// is missing which populates the validContract
							// *Check*
							if (!ObjectUtils.isEmpty(validContract)) {

								// Legacy call of CLSSB1D replaced. Retrieves
								// the Contract IDs for the passed Resource ID
								// and checks if the Contract is active for the
								// Placement Date
								PlacementDtlGpDto placementDtlGp = new PlacementDtlGpDto();
								placementDtlGp
										.setDtPlcmtStart(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart());
								placementDtlGp.setIdContract(contractDtl.getIdContract());
								Long idContract = placementDao.getContractSignedOrNot(placementDtlGp);
								// Once this call is made, there should be a
								// check on the number of records returned and
								// based on that the ValidContract boolean
								// should be set. If valid contract, then only
								// the flow should proceed, else the service
								// process should be stopped. *Check*
								log.info("ID Contontract Returned " + idContract);
								savePlacementDtlReq.getPlacementDtlGpDto()
										.setIdContract(contractDtl.getIdContract());
								break;
							}
						}
					} // End if
				}
			}
			if (WebConstants.YES.equals(savePlacementDtlReq.getPlacementDtlGpDto().getIndT3CPlcmt())
			      && !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto())) {
				Long validContract = null;
				if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC())
						&& 0L != savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC()) {
					Long idResource = savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcSSCC();
					validContract = getValidT3CContract(savePlacementDtlReq, idResource);
				} else if (
						!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcAgency())
								&& 0L != savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcAgency()) {
					Long idResource = savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcAgency();
					validContract = getValidT3CContract(savePlacementDtlReq, idResource);
				} else if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
						&& 0L != savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil()) {
					Long idResource = savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil();
					validContract = getValidT3CContract(savePlacementDtlReq, idResource);
				}

				if (!ObjectUtils.isEmpty(validContract)) {
					log.info("ID Contontract Returned " + validContract);
					savePlacementDtlReq.getPlacementDtlGpDto()
							.setIdContract(validContract);
				} else {

					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_T3C_NO_VALID_CONTRACT);
					savePlacementDtlRes.setErrorDto(errorDto);
					if (!ObjectUtils.isEmpty(savePlacementDtlRes.getErrorDto())) {
						throw new ServiceLayerException("", new Long(savePlacementDtlRes.getErrorDto().getErrorCode()), null);
					}
				}
			}
			/*
			 * Check to see if any open ADO Placement(s) exists for the
			 * placement child. If open ADO placements are indicated, pass
			 * indicator into caud45d to skip edit on open ADO placements, which
			 * will allow the placement page save to happen. Then, after a
			 * successful placement page save, close the open ADO placement(s).
			 */
			if (!ObjectUtils.isEmpty(resourceDto)
					&& ServiceConstants.SUBCARE.equalsIgnoreCase(savePlacementDtlReq.getCdStage())
					&& ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
					&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(savePlacementDtlReq.getReqFuncCd())) {

				List<String> adoPlacementEndIndicatorsList = checkForOpenADOPlacements(savePlacementDtlReq,
						placementDtoList);
				if (!CollectionUtils.isEmpty(adoPlacementEndIndicatorsList)
						&& adoPlacementEndIndicatorsList.size() == 2) {
					indCloseADOPlcmt = adoPlacementEndIndicatorsList.get(0);
					indNewPlcmtEndsPrior = adoPlacementEndIndicatorsList.get(1);
				}
			}
			/* Add/Update AUD DAM: caud45d ** Placement Generic AUD dam */

			/*
			 * There are two cases within the DAM to UPDATE the PLACEMENT table.
			 * Updates from ACTUAL to ACTUAL use Case Update. And Updates from
			 * PLANNED to ACTUAL use Case Indicator Yes.
			 */
			/*
			 * There are two cases within the DAM to UPDATE the PLACEMENT table.
			 * Updates from ACTUAL to ACTUAL use Case Update. And Updates from
			 * PLANNED to ACTUAL use Case Indicator Yes.
			 */

			PlacementAUDDto placementAUDReq = new PlacementAUDDto();
			placementAUDReq = populatePlacementSaveReq(savePlacementDtlReq);
			/*
			 ** Populate the bIndCloseADOPlcmt indicator into pCAUD45DInputRec,
			 * used in CAUD45D dam to skip or execute edits on open placements.
			 */
			if (!ObjectUtils.isEmpty(indCloseADOPlcmt)) {
				placementAUDReq.setIndCloseADOPlcmt(indCloseADOPlcmt);
			}
			/*
			 * Populate the bIndNewPlcmtEndsPrior indicator into
			 * pCAUD45DInputRec. used in CAUD45D dam to skip or execute edits on
			 * open placements.
			 */
			if (!ObjectUtils.isEmpty(indNewPlcmtEndsPrior)) {
				placementAUDReq.setIndNewPlcmtEndsPrior(indNewPlcmtEndsPrior);
			}

			//FFPSA 138.28 Congregate care placement change may require changes to foster care eligibility
			isChangeReqToFosterCareElig = setFosterCareEligibility(placementBeforeUpdate, savePlacementDtlReq, placementAUDReq);

			if (savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart().before(placementAUDReq.getDtPlcmtEnd()) ||
					savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart().equals(placementAUDReq.getDtPlcmtEnd())) {
				//Added [artf158265] ALM ID : 15182 : PD 62910 : KIN Relative Plcmnt Code Value Missing.
				/*
				 * If one of Placement Information Check Boxes Relative Placement -
				 * Grandparent (180) OR Relative Placement - Aunt/Uncle (190) OR
				 * Relative Placement - Other Relative (200) is selected automatically
				 * populate Relative Placement (040)
				 */
				if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtInfo())
						&& savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtInfo().stream()
						.anyMatch(c -> ServiceConstants.PLCMT_REL_GRAND_PARENT.equalsIgnoreCase(c) ||
								ServiceConstants.PLCMT_REL_AUNT_UNCLE.equalsIgnoreCase(c) ||
								ServiceConstants.PLCMT_REL_OTHER_REL.equalsIgnoreCase(c))) {
					placementAUDReq.setCdPlcmtInfo4(ServiceConstants.PLCMT_RELATIVE);
				}
				//End [artf158265] ALM ID : 15182 : PD 62910 :KIN Relative Plcmnt Code Value Missing.
				// Call CAUD45D
				placementAUDRes = savePlacementInformation(placementAUDReq);

				cloneHMRequestFromLatestPlcmnt(placementAUDReq, savePlacementDtlReq);

				saveOrUpdateChildBillOfRightsHistory(savePlacementDtlReq);
				if(ObjectUtils.isEmpty(placementAUDRes.getErrorDto()))
				{

					if(cdLivingArrangementType .equals(ServiceConstants.QRTP_Living_Arrangment) &&( !isExistingQRTPPlacement || ServiceConstants.Y.equals(savePlacementDtlReq.getIndNewActualPlcmt()))){
						sendEmailNotificationforQRTP(savePlacementDtlReq);
					}
				}
			} else {
				placementAUDRes.setNbrValidationMsg(ServiceConstants.MSG_ERR_BAD_FUNC_CD);
			}
			savePlacementDtlRes.setPlacementAUDRes(placementAUDRes);
			if (!ObjectUtils.isEmpty(placementAUDRes.getErrorDto())) {
				throw new ServiceLayerException("", new Long(placementAUDRes.getErrorDto().getErrorCode()), null);
			}
			/****************************************************************************************
			 ** End the open ADO Placements.
			 ***************************************************************************************/

			if (ServiceConstants.Y.equalsIgnoreCase(indCloseADOPlcmt)) {
				// Call CSUB87D
				String message = placementDao.updateOpenDDPlacement(placementDtoList,
						savePlacementDtlReq.getPostEventIPDto().getIdPerson());
				if (ServiceConstants.SUCCESS.equalsIgnoreCase(message)) {
					// callCCMN46D dam will update placement EVENT record
					for (PlacementDto placementDto : placementDtoList) {
						EventDto eventDto = new EventDto();
						ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
						eventDto.setIdEvent(placementDto.getIdPlcmtEvent());
						eventDto.setIdPerson(placementDto.getPersonId());
						eventDto.setIdStage(placementDto.getStageId());
						eventDto.setDtEventOccurred(placementDto.getDtEventOccurred());
						eventDto.setDtLastUpdate(placementDto.getDtEventLastUpdate());
						eventDto.setCdEventStatus(placementDto.getCdEventStatus());
						eventDto.setCdTask(placementDto.getCdTask());
						eventDto.setCdEventType(placementDto.getCdEventType());
						eventDto.setEventDescr(placementDto.getTxtEventDescr());
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
						eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);
					}
				}
			} /* automated ending of open ADO placements */

			/*
			 * This function will be called to create Automatic Placement in PCA
			 * Stage when the SUB stage placement is closed with Reason 'Child
			 * Placed in PCA'
			 */
			PlacementAUDDto placementAUDDto = new PlacementAUDDto();
			if ((ServiceConstants.SUBCARE.equalsIgnoreCase(savePlacementDtlReq.getCdStage()))
					&& (ServiceConstants.REMRSN_PLC_IN_PCA)
					.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
					&& (ServiceConstants.Y)
					.equalsIgnoreCase(savePlacementDtlReq.getpCAPlacmentDtlDto().getIndNewPCAPlcmt())) {
				// CallCreatePCAPlacement
				placementAUDDto = this.CallCreatePCAPlacement(savePlacementDtlReq);
			}

			if ((ServiceConstants.SQL_SUCSS).equals(placementAUDDto.getNbrValidationMsg())) {
				if (ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndGeneric())) {
					/**************************************************************************
					 ** (BEGIN): Retrieve DAM: cses37d ** Placement simple
					 * retrieve
					 **************************************************************************/
					PlacementDto placementDto = processPlacement(
							placementDao.selectPlacement(placementAUDDto.getIdPlcmtEvent()));
					// Output from cses37d set to tslastupdate with dtlastupdate
					// need to verify with legacy for exact sol.
					/*
					 * memcpy(pOutputMsg->tsLastUpdate[PLCMT],
					 * pCSES37DOutputRec->tsLastUpdate, SYS_TS_LAST_UPDATE_LEN);
					 */
					savePlacementDtlRes.setTsLastUpdate(placementDto.getDtLastUpdate());
				} /*
				 * end if:
				 * (savePlacementDtlReq.getIndGeneric().equalsIgnoreCase(
				 * ServiceConstants.INDICATOR_YES))
				 */
			} else {
				PlacementDto placementDto = processPlacement(placementDao.selectPlacement(idEvent));
				savePlacementDtlRes.setTsLastUpdate(placementDto.getDtLastUpdate());
			}

			/*
			 * end else: (placementAUDDto.getNbrValidationMsg().equals(
			 * ServiceConstants.SQL_SUCSS))
			 */
		} /* end if (0L != idEvent) */


		/*
		 * Remove cSysIndPlcmtChgAdrAdm from the if statement below, so
		 * addresses can be send to TIERS. Using this indicator in the logic
		 * would prevents a medicaid_update row from being written to the
		 * Medicaid_Update table if the cSysIndPlcmtDifMedAdr is selected from
		 * the page.
		 */
		MedicaidUpdateDto medicaidUpdateDto = new MedicaidUpdateDto();
		if (((ServiceConstants.ACTUAL)
				.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtActPlanned())
				&& (ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtChgAdrAdm()))
				|| (ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {

			medicaidUpdateDto.setIdMedUpdStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());
			if (!TypeConvUtil.isNullOrEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild())) {
				medicaidUpdateDto.setIdMedUpdPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
			}
			if (!TypeConvUtil.isNullOrEmpty(savePlacementDtlRes.getIdEvent())) {
				medicaidUpdateDto.setIdMedUpdRecord(savePlacementDtlRes.getIdEvent());
			}
			medicaidUpdateDto.setCdMedUpdType(ServiceConstants.PLACEMENT);
			medicaidUpdateDto.setCdMedUpdTransType(ServiceConstants.SUSTAINED);
			medicaidUpdateService.editMedicaidUpdate(medicaidUpdateDto, ServiceConstants.REQ_FUNC_CD_ADD);
			/* (END): Add/Update AUD DAM:** Medicaid_update AUD dam */
		}


		if (((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt()))
				&& ((ServiceConstants.RETURN_HOME_PL)
						.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
						|| (ServiceConstants.PLACEMENT_ADOPTION)
						.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn()))) {

			if ((ServiceConstants.RETURN_HOME_PL)
					.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())) {
				/**************************************************************************
				 ** (BEGIN): Retrieve DAM: cmsc09d ** Nbr of other SUBCARE stages
				 * in case
				 **************************************************************************/
				CommonDto commonDto = new CommonDto();
				commonDto.setCdStage(ServiceConstants.SUBCARE);
				commonDto.setIdCase(savePlacementDtlReq.getIdCase());
				commonDto.setIdStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());
				//ALM defect :13377 - Set value to cdLivArr as 'DA' and cdRemRsn as '010' of commonDto object.
				//If we add these values, getOpenPlacementCountForCase() is no longer needed to check if FRE stage needs to be triggered.
				commonDto.setCdLivArr(ServiceConstants.LIVING_ARRANGEMENT);
				commonDto.setCdRemRsn(ServiceConstants.RETURN_HOME_PL);
				nbrStagesOpen = placementDao.getCountSubcareStage(commonDto);
		}

			if (nbrStagesOpen == 0) {
				/**************************************************************************
				 ** (BEGIN): ** Stage progression simple retrieve
				 **************************************************************************/
				String cdStage = ServiceConstants.SUBCARE;
				String cdStageProgram = ServiceConstants.CAPS_PROG_CPS;
				String cdStageReasonClosed = ServiceConstants.NULL_VALUE;
				if ((ServiceConstants.RETURN_HOME_PL)
						.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())) {
					cdStageReasonClosed = ServiceConstants.FAM_REUNIF;
				} else if ((ServiceConstants.PLACEMENT_ADOPTION)
						.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())) {
					cdStageReasonClosed = ServiceConstants.ADOPT;
				}
				// CCMNB8D
				List<StageProgDto> stageProgDtoList = stageProgDao.getStgProgroession(cdStage, cdStageProgram,
						cdStageReasonClosed);
				if (!CollectionUtils.isEmpty(stageProgDtoList)) {
					if (((ServiceConstants.CLOSE_OPEN_STAGE)
							.equalsIgnoreCase(stageProgDtoList.get(0).getIndStageProgClose()))
							|| ((ServiceConstants.OPEN_STAGE)
									.equalsIgnoreCase(stageProgDtoList.get(0).getIndStageProgClose()))) {

						/**************************************************************************
						 ** (BEGIN): Common Function ** CloseOpenStage common
						 * function
						 **************************************************************************/
						CloseOpenStageInputDto closeOpenStageInputDto = new CloseOpenStageInputDto();
						closeOpenStageInputDto.setIdStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());
						closeOpenStageInputDto.setCdStageProgram(ServiceConstants.CAPS_PROG_CPS);
						closeOpenStageInputDto.setIdPerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
						/*
						 **
						 ** The stage progression table should have a row with
						 * CdStage SUB and CdStageRsnClosed as ADO or FRE
						 * depending on the situation. We should use this value
						 * instead of hardcoding to find a match on the stage
						 * progression table to open the FRE or ADO stage
						 */
						closeOpenStageInputDto.setCdStage(stageProgDtoList.get(0).getCdStageProgRsnClose());
						closeOpenStageInputDto.setCdStageOpen(stageProgDtoList.get(0).getCdStageProgOpen());
						closeOpenStageInputDto.setCdStageReasonClosed(stageProgDtoList.get(0).getCdStageProgRsnClose());
						closeOpenStageInputDto
						.setDtStageStart(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd());
						closeOpenStageInputDto
						.setScrIdPrimChild(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
						if ((stageProgDtoList.get(0).getIndStageProgClose())
								.equalsIgnoreCase(ServiceConstants.CLOSE_OPEN_STAGE)) {
							closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.N);
						}

						if ((stageProgDtoList.get(0).getIndStageProgClose())
								.equalsIgnoreCase(ServiceConstants.OPEN_STAGE)) {
							closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.Y);
						}
						CloseOpenStageOutputDto closeOpenStageOutput = closeOpenStageService
								.closeOpenStage(closeOpenStageInputDto);
						log.info("Close Open Stage Output Ends Closing Stage" + closeOpenStageOutput.getIdStage());
					}
				} // end if:!ObjectUtils.isEmpty(stageProgDtoList)
			} // end if:(0L == nbrStagesOpen || 0 == nbrStagesOpen)
		} /* end if: RemovalPlcmt, RETURN_HOME or ADOPTION */

		/*************************************************************************
		 ** Start here with logic if there's a new placement being added or a
		 * placement removal being saved.
		 **************************************************************************/

		if (ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				|| ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())) {

			/***********************************************************************
			 ** do the logic for updating the slots on the new facility. The
			 * slots should be decreased for a new placement, and increased for
			 * a removal
			 ***********************************************************************/

			/*
			 ** use id resource to get resource facility type. if the facility
			 * type is 70 or 71, then update the open slots
			 */
			if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())) {

				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());

			} /* end if there was an id resource */

			/* added the condition to only do this for F/A Homes */
			if (!ObjectUtils.isEmpty(resourceDto)
					&& !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
					&& ((ServiceConstants.FPS_FA_HOME).equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
							|| (ServiceConstants.PRIV_AGENCY_ADPT_HOME)
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {
				String updateSlots = null;
				if (ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {
					updateSlots = "-1";
				} else if (ServiceConstants.Y.equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())) {
					updateSlots = "1";
				}
				/* Update NBR_RSRC_OPEN_SLOTS */
				// CMSC16D
				openSlots = legalStatusDao.updateNbrRscOpenSlots(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil(),
						updateSlots);


			} /* end update NBR_RSRC_OPEN_SLOTS for F/A Homes */

			/**************************************************************************
			 ** (BEGIN): ** CSES38D Eligibility simple retrieve
			 **************************************************************************/
			List<EligibilityOutDto> eligibilityRecordList = null;
			EligibilityOutDto eligibilityRec = new EligibilityOutDto();
			EligibilityInDto eligibilityInputDto = new EligibilityInDto();
			eligibilityInputDto.setIdPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
			eligibilityInputDto.setDtScrDtCurrentDate(new Date());
			// Call CSES38D
			eligibilityRecordList = eligibilityDao.getEligibilityRecord(eligibilityInputDto);
			if (!CollectionUtils.isEmpty(eligibilityRecordList) && eligibilityRecordList.size() > 0) {
				eligibilityRec = eligibilityRecordList.get(0);
				// transfer to local variable for future conditions
				if (!ObjectUtils.isEmpty(eligibilityRec)) {
					idEligibilityWorker = eligibilityRec.getIdPersonUpdate();
					cdEligSelected = eligibilityRec.getCdEligSelected();
				}
			}

			/**************************************************************************
			 ** (BEGIN): ** Stage & StagePersonLink retrieve
			 **************************************************************************/

			// CSEC29D
			List<StagePersDto> newNewStagePersDtoList = null;

			newNewStagePersDtoList = stageDao.getStagesByAttributes(
					savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild(), ServiceConstants.PRIMARY_CHILD,
					savePlacementDtlReq.getIdCase(), ServiceConstants.PAL);
			if (!CollectionUtils.isEmpty(newNewStagePersDtoList) && newNewStagePersDtoList.size() > 0) {
				if (!ObjectUtils.isEmpty(newNewStagePersDtoList.get(0))) {
					idPALStage = newNewStagePersDtoList.get(0).getIdStage();
				}
			}
			if (0 < idPALStage) {
				/*
				 ** Determine Primary worker for the Pal Stage
				 */
				/**************************************************************************
				 ** (BEGIN): ** Get IdPerson given IdStage & Role
				 **************************************************************************/
				// CINV51D
				idPALWorker = workLoadDao.getPersonIdByRole(idPALStage,
						ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

				if (!ObjectUtils.isEmpty(idPALWorker)) {
					if ((0L < idPALWorker) && (!ServiceConstants.RETURN_HOME_PL
							.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
							|| (ServiceConstants.WITH_RELATIVES).equalsIgnoreCase(
									savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
							|| (ServiceConstants.COURT_ORDERED).equalsIgnoreCase(
									savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
							|| (ServiceConstants.EMANCIPATED).equalsIgnoreCase(
									savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn()))) {
						/*
						 ** PAL Training Elements processing
						 */

						/**************************************************************************
						 ** (BEGIN):** Check # of PAL training elements
						 **************************************************************************/
						NbrValidationDto palTrainingInput = new NbrValidationDto();

						palTrainingInput
						.setIdPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
						palTrainingInput.setIdStage(idPALStage);
						palTrainingNbrOutput = nbrValidationDao.getUlSysNbrValidationMsg(palTrainingInput);

					} /*
					 * end if: getIdPlcmtChild && one of four
					 * RemovalRsn's
					 */

				} // end if : idPalWorker
				/**************************************************************************
				 ** (END): ** Get IdPerson given IdStage & Role
				 **************************************************************************/
			} // end if idPalStage

			if (savePlacementDtlReq.getCdStage().equalsIgnoreCase(ServiceConstants.ADOPT)) {
				/*
				 ** Get Adoption Subsidy worker for the child
				 */
				/**************************************************************************
				 ** (BEGIN): ** Adoption_subsidy simple retrieve
				 **************************************************************************/
				AdoptionSubsidyInDto adpotionSubsidyDto = new AdoptionSubsidyInDto();
				adpotionSubsidyDto
				.setAdptSubPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
				adpotionSubsidyDto.setDtPersonDeath(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd());
				List<AdoptionSubsidyOutDto> adoptionSubsidyDto = null;
				adoptionSubsidyDto = adoptionSubsidyDao.getAdoptionSubsidyRecord(adpotionSubsidyDto);

				if (!CollectionUtils.isEmpty(adoptionSubsidyDto) && adoptionSubsidyDto.size() > 0) {

					/**************************************************************************
					 ** Call the Stage Person Link Retrieval Dam -
					 * CMSC23D
					 **
					 ** Description - This DAM a list of Stages where the
					 * Id Person (and all persons merged with them) are
					 * on the Stage Person Link.
					 **************************************************************************/
					List<StagePersDto> stageList = null;
					// CMSC23D
					stageList = stagePersonLinkDao
							.getStageListByIdPerson(adoptionSubsidyDto.get(0).getAdptSubPerson());

					/*
					 ** Determine if the current stage is an OPEN
					 * ADOPTION STAGE
					 */
					if (0L == idAdoptSubWorker) {
						for (StagePersDto stagePers : stageList) {

							/*
							 ** If the dam call is successful and the
							 * stage returned is ADO (Adoption) and the
							 * DtStageClosed != NULL_DATE then use
							 * IdStage as Input to obtain staff for that
							 * Stage.
							 */
							if ((stagePers.getCdStage().equalsIgnoreCase(ServiceConstants.ADOPT))
									&& (ObjectUtils.isEmpty(stagePers.getDtStageClose()))) {
								List<StagePersonLinkDto> newStagePersonLinkDtoList = null;
								// CCMNB9D
								newStagePersonLinkDtoList = stagePersonLinkDao
										.getStagePersonLinkByIdStage(stagePers.getIdStage());
								if (0L == idAdoptSubWorker) {
									for (StagePersonLinkDto newStagePersonLinkDto : newStagePersonLinkDtoList) {
										if (newStagePersonLinkDto.getCdStagePersType()
												.equalsIgnoreCase(ServiceConstants.STAFF)) {
											/*
											 ** Use IdPerson Returned
											 * from CCMNB9 as input
											 */
											// CLSCB4D
											List<EmpSecClassLink> empSecClassLinkList = null;
											empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(
													newStagePersonLinkDto.getIdPerson());

											/*
											 ** loop through the returned
											 * security profiles looking
											 * for the
											 * SEC_ADOPT_ASSIST_SPEC
											 * attribute
											 */
											if (0L == idAdoptSubWorker) {
												for (EmpSecClassLink empSecClassLink : empSecClassLinkList) {
													if (ServiceConstants.CHAR_ONE == empSecClassLink
															.getSecurityClass().getTxtSecurityClassProfil()
															.charAt(71)) {
														idAdoptSubWorker = newStagePersonLinkDto.getIdPerson();
														break;
													}
												}
											} // End List of CLSCB4D
										}
									}
								} // End List of CCMNB9D

							}

						}
						/**************************************************************************
						 ** End Call to Stage Person Link Retrieval Dam
						 **************************************************************************/
					} // End List of CMSC23D

				}
				/**************************************************************************
				 ** (END): ** Adoption_subsidy simple retrieve
				 **************************************************************************/
			} // End
			// savePlacementDtlReq.getCdStage().equalsIgnoreCase(ServiceConstants.ADOPT)

			/*
			 ** Added "&& pInputMsg->req.ulIdRsrcFacil is not NULL" to
			 * the following if statement. This prevents calling CRES04D
			 * when the placement type is Non-Certified Person and no
			 * resource id exists.
			 */
			if (((ServiceConstants.FOST_ADOPT)
					.equalsIgnoreCase(savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType())
					|| (ServiceConstants.ADOPT).equalsIgnoreCase(savePlacementDtlReq.getCdStage()))
					&& (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil()))) {
				/*
				 ** Get ulIdRsrcFaHomeStage given a ulIdResource
				 */
				/**************************************************************************
				 ** (BEGIN): ** Caps_resource simple retrieve
				 **************************************************************************/

				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());
				if (!ObjectUtils.isEmpty(resourceDto)) {
					if (!ObjectUtils.isEmpty(resourceDto.getIdStage())) {
						idHomeStage = resourceDto.getIdStage();
					}
					if (!ObjectUtils.isEmpty(resourceDto.getNbrRsrcOpenSlots())) {
						openSlots = resourceDto.getNbrRsrcOpenSlots();
					}
				}
				/*
				 ** Get primary worker for the FAD Home
				 */
				/**************************************************************************
				 ** (BEGIN): ** Get IdPerson given IdStage & Role
				 **************************************************************************/
				if (!ObjectUtils.isEmpty(idHomeStage)) {
					// cinv51d
					idFADWorker = workLoadDao.getPersonIdByRole(idHomeStage,
							ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
					/*
					 ** Get the FAD Home recruiter If szCdRsrcRegion is
					 * NOT 99 (State Office: CAPS_UNIT_STATE_OFFICE)
					 */
					if (!ServiceConstants.CAPS_UNIT_STATE_OFFICE
							.equalsIgnoreCase(resourceDto.getCdRsrcRegion())) {
						/**************************************************************************
						 ** (BEGIN): Retrieve DAM: csec37d ** Find
						 * employee with skill retrieve
						 **************************************************************************/
						// 4230
						String cdUnitRegion = ServiceConstants.EMPTY_STR;
						UnitEmpLinkDto unitEmpLinkInput = new UnitEmpLinkDto();
						unitEmpLinkInput.setCdEmpSkill(ServiceConstants.RECRUITER);
						unitEmpLinkInput.setCdUnitProgram(ServiceConstants.CAPS_PROG_CPS);
						if (ServiceConstants.CAPS_UNIT_SWI.equalsIgnoreCase(resourceDto.getCdRsrcRegion())) {
							unitEmpLinkInput.setCdUnitRegion(ServiceConstants.CAPS_REGION_SWI);
						} else {
							cdUnitRegion = cdUnitRegion
									.concat(ServiceConstants.STR_ZERO_VAL + resourceDto.getCdRsrcRegion());
							unitEmpLinkInput.setCdUnitRegion(cdUnitRegion);
						}
						// CSEC37D
						List<UnitEmpLinkDto> unitEmplLinkOutput = unitEmpLinkDao
								.getUnitEmpLinkDtl(unitEmpLinkInput);
						if (!CollectionUtils.isEmpty(unitEmplLinkOutput) && unitEmplLinkOutput.size() > 0) {
							idFADRecruiter = unitEmplLinkOutput.get(0).getIdPerson();
						}
						/**************************************************************************
						 ** (END): Retrieve DAM: csec37d ** Find employee
						 * with skill retrieve
						 **************************************************************************/
					}
					/**************************************************************************
					 ** (END): Retrieve DAM: cinv51d ** Get IdPerson
					 * given IdStage & Role
					 **************************************************************************/
				}
				/**************************************************************************
				 ** (END): Retrieve DAM: cres04d ** Caps_resource simple
				 * retrieve
				 **************************************************************************/
			} /*
			 * end if: RetVal && FOST_ADOPT =
			 * pInputMsg->....szCdPlcmtType
			 */

			/**************************************************************************
			 ** (END): Retrieve DAM: csec29d ** Stage & StagePersonLink
			 * retrieve
			 **************************************************************************/
			// end if CSEC29D response
			/**************************************************************************
			 ** (END): Retrieve DAM: cses38d ** Eligibility simple retrieve
			 **************************************************************************/
		} // End Call CSES38D

		/* end if: NewActual or NewRemoval */

		/*
		 ** if the placement is not a new one or a removal, check to see if the
		 * resource was changed. If it is, update open slots on both the old and
		 * new resources. If the placement end date has already been entered,
		 * don't update open slots.
		 */

		if ((ServiceConstants.N).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (ServiceConstants.N).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())
				&& (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
						&& !savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil()
						.equals(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal()))
				&& (ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd()))) {
			/***************************
			 ** first do the new resource
			 ****************************/
			if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())) {
				// Call CRES04D
				resourceDto = new ResourceDto();
				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());
			}
			if (!ObjectUtils.isEmpty(resourceDto)
					&& !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
					&& (ServiceConstants.FPS_FA_HOME.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
							|| ServiceConstants.PRIV_AGENCY_ADPT_HOME
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {
				/*
				 * Update NBR_RSRC_OPEN_SLOTS - subtracts one from the open
				 * slots
				 */
				// Call CMSC16D
				String updateSlots = "-1";
				openSlots = legalStatusDao.updateNbrRscOpenSlots(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil(),
						updateSlots);
			} /* end update NBR_RSRC_OPEN_SLOTS for new F/A Home */

			/******************************
			 ** second do the old resource
			 *******************************/
			if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal())) {
				/*
				 * overwrite the cdRsrcFacilType variable from output of
				 * resource service
				 */
				// CRES04D
				resourceDto = new ResourceDto();
				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal());
			}
			if (!ObjectUtils.isEmpty(resourceDto)
					&& !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal())
					&& (ServiceConstants.FPS_FA_HOME.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
							|| ServiceConstants.PRIV_AGENCY_ADPT_HOME
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {
				/* Update NBR_RSRC_OPEN_SLOTS - adds one to the open slots */
				String updateSlots = "1";
				// CMSC16D
				openSlots = legalStatusDao.updateNbrRscOpenSlots(
						savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal(), updateSlots);
			} /* end update NBR_RSRC_OPEN_SLOTS for old F/A Home */
		} /* end if resource id was changed on an update */

		/*
		 * starts added the condition to only do this for F/A Homes When the end
		 * date is removed from the existing placement BUSINESS CASE: if the end
		 * date is removed from the placement Number of open slots MUST be
		 * decreased by 1
		 */
		if (savePlacementDtlReq.getIndNewActualPlcmt().equalsIgnoreCase(ServiceConstants.N)
				&& savePlacementDtlReq.getIndNewRemovalPlcmt().equalsIgnoreCase(ServiceConstants.N)
				&& (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
						&& savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil()
						.equals(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacilOriginal()))
				&& (ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd()))
				&& (!ObjectUtils.isEmpty(dtDtPlcmtEnd))) {
			if (!ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())) {
				// Call CRES04D
				resourceDto = new ResourceDto();
				resourceDto = capsResourceDao
						.getResourceById(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil());

			}
			if (!ObjectUtils.isEmpty(resourceDto)
					&& !ObjectUtils.isEmpty(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil())
					&& ((ServiceConstants.FPS_FA_HOME).equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
							|| (ServiceConstants.PRIV_AGENCY_ADPT_HOME)
							.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {
				/*
				 * Update NBR_RSRC_OPEN_SLOTS - subtracts one from the open
				 * slots
				 */
				// Call CMSC16D
				String updateSlots = "-1";
				openSlots = legalStatusDao.updateNbrRscOpenSlots(savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil(),
						updateSlots);
			} /* NBR_RSRC_OPEN_SLOTS decreased by 1 for old F/A Home */
		} // end if

		/*
		 ** Call DAM if Elig Worker has not been retrieved and there is a
		 * mismatch between Facility and Child's LOC
		 */
		if ((savePlacementDtlReq.getIndPlcmtLocMatch().equalsIgnoreCase(ServiceConstants.Y))
				&& (0L == idEligibilityWorker)) {
			/**********************************************************************
			 ** (BEGIN): Retrieve DAM: cses38d ** Eligibility simple retrieve
			 **********************************************************************/
			List<EligibilityOutDto> eligibilityRecordList = null;
			EligibilityOutDto eligibilityRec = new EligibilityOutDto();
			EligibilityInDto eligibilityInputDto = new EligibilityInDto();
			eligibilityInputDto.setIdPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
			eligibilityInputDto.setDtScrDtCurrentDate(new Date());
			// Call CSES38D
			eligibilityRecordList = eligibilityDao.getEligibilityRecord(eligibilityInputDto);

			if (!CollectionUtils.isEmpty(eligibilityRecordList) && eligibilityRecordList.size() > 0) {
				eligibilityRec = eligibilityRecordList.get(0);
				// transfer to local variable for future conditions
				if (!ObjectUtils.isEmpty(eligibilityRec)) {
					idEligibilityWorker = eligibilityRec.getIdPersonUpdate();
					cdEligSelected = eligibilityRec.getCdEligSelected();
				}
			}
			/**********************************************************************
			 ** (END): Retrieve DAM: cses38d ** Eligibility simple retrieve
			 **********************************************************************/
		} /* end if Facility/Child LOC Mismatch */

		/*
		 ** Create Todo's from todo_info table
		 */

		Map<Integer, Boolean> toDoFlags = new HashMap<>();
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtDifMedAdr())) {
			/*
			 * change the following piece of code if in the future alert Update
			 * Medicaid type of address on Addr List/Dtl. is needed.
			 */
			toDoFlags.put(ServiceConstants.SUB005, ServiceConstants.FALSEVAL);
		} /* end if */

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt()) && savePlacementDtlReq
				.getPlacementDtlGpDto().getCdPlcmtType().equalsIgnoreCase(ServiceConstants.FOST_ADOPT)
				&& (0L < idHomeStage) && (0L < idFADWorker)) {
			toDoFlags.put(ServiceConstants.SUB028, ServiceConstants.TRUEVAL);
		} /* end if */
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (0L < idEligibilityWorker)
				&& (!savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType()
						.equalsIgnoreCase(ServiceConstants.FOST_ADOPT)
						&& savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType()
						.equalsIgnoreCase(ServiceConstants.CONTRACTED))
				&& (!cdEligSelected.equalsIgnoreCase(ServiceConstants.NOT_ELIG)
						&& !cdEligSelected.equalsIgnoreCase(ServiceConstants.COUNTY_PAID))) {
			toDoFlags.put(ServiceConstants.SUB030, ServiceConstants.TRUEVAL);
		} /* end if */

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (0L < idEligibilityWorker)
				&& (!savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType()
						.equalsIgnoreCase(ServiceConstants.FOST_ADOPT)
						&& savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType()
						.equalsIgnoreCase(ServiceConstants.CONTRACTED))
				&& (!cdEligSelected.equalsIgnoreCase(ServiceConstants.NOT_ELIG)
						&& !cdEligSelected.equalsIgnoreCase(ServiceConstants.COUNTY_PAID))) {
			toDoFlags.put(ServiceConstants.SUB030, ServiceConstants.TRUEVAL);
		} /* end if */

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt()) && (0L < idPALStage)
				&& (0L < idPALWorker)) {
			toDoFlags.put(ServiceConstants.SUB031, ServiceConstants.TRUEVAL);
		} /* end if */

		/* If LOC/FLOC Mismatch set flag to true */
		/* Remove :INDICATOR_YES== pInputMsg->SysIndNewActualPlcmt */
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtLocMatch())
				&& (0L < idEligibilityWorker)) {
			toDoFlags.put(ServiceConstants.SUB048, ServiceConstants.TRUEVAL);
		} else if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtLocMatch())
				&& (0L == idEligibilityWorker)) {
			toDoFlags.put(ServiceConstants.SUB032, ServiceConstants.TRUEVAL);
		}

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtFacCntrct())) {
			toDoFlags.put(ServiceConstants.SUB035, ServiceConstants.TRUEVAL);
		}
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndPlcmtFacCntrct())
				&& (0L < idEligibilityWorker)) {
			toDoFlags.put(ServiceConstants.SUB036, ServiceConstants.TRUEVAL);
		}

		/*
		 **
		 ** 0 or 1 rows returned from NbrStageOpen is OK
		 */
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())
				&& (ServiceConstants.RETURN_HOME_PL).equalsIgnoreCase(
						savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtRemovalRsn())
				&& (0L < nbrStagesOpen)) {
			toDoFlags.put(ServiceConstants.SUB037, ServiceConstants.TRUEVAL);
		}

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt()) && (0L < idPALStage)
				&& (0L < idPALWorker)) {
			toDoFlags.put(ServiceConstants.SUB038, ServiceConstants.TRUEVAL);
		}

		/*
		 ** use Recruiter until we figure out what a ulIdAdoptSubWorker actually
		 * does
		 */
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())
				&& (0L < idAdoptSubWorker)) {
			toDoFlags.put(ServiceConstants.SUB039, ServiceConstants.TRUEVAL);
		}
		/*
		 **
		 ** ulIdSendPalFollowUp should be validated against TRUE not
		 * INDICATOR_YES
		 */

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())
				&& (ServiceConstants.NUM_TRUE == palTrainingNbrOutput.getUlSysNbrValidationMsg()) && (0L < idPALStage)
				&& (0L < idPALWorker)) {
			toDoFlags.put(ServiceConstants.SUB041, ServiceConstants.TRUEVAL);
		}

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& ((ServiceConstants.ADOPT).equalsIgnoreCase(savePlacementDtlReq.getCdStage())) && (0L < idHomeStage)
				&& (0L < idFADRecruiter)) {
			toDoFlags.put(ServiceConstants.SUB042, ServiceConstants.TRUEVAL);
		}

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& ((ServiceConstants.ADOPT).equalsIgnoreCase(savePlacementDtlReq.getCdStage())) && (0L < idHomeStage)
				&& (0L < idFADRecruiter)) {
			toDoFlags.put(ServiceConstants.SUB042, ServiceConstants.TRUEVAL);
		}
		// End of FND_Success

		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt()) && (0L > openSlots)
				&& (0L < idHomeStage) && (0L < idFADWorker)) {
			toDoFlags.put(ServiceConstants.FAD050, ServiceConstants.TRUEVAL);
		}
		Long idEligibilityEvent = null;
		if ((ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())
				&& (ServiceConstants.Y).equalsIgnoreCase(savePlacementDtlReq.getIndEligPlcmt())) {
			idEligibilityEvent = placementDao.getOpenEligibilityEvent(savePlacementDtlReq.getPostEventIPDto().getIdStage());
			if(!ObjectUtils.isEmpty(idEligibilityEvent)) {
				toDoFlags.put(ServiceConstants.SUB050, ServiceConstants.TRUEVAL);
			}
		}

		//FFPSA 138.28 Congregate care placement change may require changes to foster care eligibility
		if(N.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {
			if (isChangeReqToFosterCareElig) {
				idEligibilityEvent = placementDao.getOpenEligibilityEvent(savePlacementDtlReq.getPostEventIPDto().getIdStage());
				if (!ObjectUtils.isEmpty(idEligibilityEvent)) {
					toDoFlags.put(ServiceConstants.SUB051, ServiceConstants.TRUEVAL);
				}
			}
		}

		int i = 0;
		for (i = 0; i <= ServiceConstants.SUB051; i++) {
			if (ServiceConstants.TRUEVAL == toDoFlags.get(i)) {
				/**************************************************************************
				 ** (BEGIN): Common Function: csub40u ** ToDo common function:
				 * from ToDoInfo
				 **************************************************************************/
				TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
				MergeSplitToDoDto commonTodoInDto = new MergeSplitToDoDto();
				commonTodoInDto.setIdTodoCfPersCrea(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
				commonTodoInDto.setIdTodoCfEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
				commonTodoInDto.setDtTodoCfDueFrom(new Date());
				/*
				 ** Note: the ulSysIdTodoCfStage may be overwritten within the
				 * case below.
				 */
				commonTodoInDto.setIdTodoCfStage(savePlacementDtlReq.getPostEventIPDto().getIdStage());

				switch (i) {

				case ServiceConstants.SUB005:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_05_CODE);
					break;
				case ServiceConstants.SUB028:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_28_CODE);
					commonTodoInDto.setIdTodoCfStage(idHomeStage);
					commonTodoInDto.setIdTodoCfPersAssgn(idFADWorker);
					commonTodoInDto.setTodoCfDesc(savePlacementDtlReq.getNmStage().concat(ServiceConstants.WAS_PLACED_ON
							.concat(DateUtils.stringDt(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()))));
					break;
				case ServiceConstants.SUB030:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_30_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idEligibilityWorker);
					break;
				case ServiceConstants.SUB031:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_31_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idPALWorker);
					commonTodoInDto.setIdTodoCfStage(idPALStage);
					break;
				case ServiceConstants.SUB032:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_32_CODE);
					break;
				case ServiceConstants.SUB033:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_33_CODE);
					break;
				case ServiceConstants.SUB034:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_34_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idEligibilityWorker);
					break;
				case ServiceConstants.SUB035:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_35_CODE);
					break;
				case ServiceConstants.SUB036:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_36_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idEligibilityWorker);
					break;
				case ServiceConstants.SUB037:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_37_CODE);
					break;
				case ServiceConstants.SUB038:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_38_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idPALWorker);
					commonTodoInDto.setIdTodoCfStage(idPALStage);
					break;
				case ServiceConstants.SUB039:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_39_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idAdoptSubWorker);
					break;
				case ServiceConstants.SUB042:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_42_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idFADRecruiter);
					break;
				case ServiceConstants.SUB041:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_41_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idPALWorker);
					commonTodoInDto.setIdTodoCfStage(idPALStage);
					break;

				case ServiceConstants.SUB048:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_48_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idEligibilityWorker);
					break;

				case ServiceConstants.FAD050:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_FAD50_CODE);
					commonTodoInDto.setIdTodoCfPersAssgn(idFADWorker);
					commonTodoInDto.setIdTodoCfStage(idHomeStage);
					commonTodoInDto.setTodoCfDesc((ServiceConstants.CAPACITY_EXCEEDED)
							.concat(savePlacementDtlReq.getNmStage() + ServiceConstants.CAPACITY_EXCEEDED1));
					break;

				case ServiceConstants.SUB050:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_50_CODE);
					commonTodoInDto.setIdTodoCfEvent(idEligibilityEvent);
					commonTodoInDto.setTodoCfDesc(ServiceConstants.SUB_050_TODO_INFO_DESC
							.concat(DateUtils.stringDt(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart())
									+ ServiceConstants.PERIOD));
					break;

				case ServiceConstants.SUB051:
					commonTodoInDto.setCdTodoCf(ServiceConstants.TODO_INFO_51_CODE);
					commonTodoInDto.setIdTodoCfEvent(idEligibilityEvent);
					commonTodoInDto.setTodoCfDesc(ServiceConstants.SUB_051_TODO_INFO_DESC.replace("{DATE}",
						DateUtils.stringDt(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()))
							+ ServiceConstants.PERIOD);
					break;
				}

				/*
				 * If it's a SUB050 we need to possibly create multiple To-Dos
				 */

				if (ServiceConstants.TODO_INFO_50_CODE.equalsIgnoreCase(commonTodoInDto.getCdTodoCf())
					|| ServiceConstants.TODO_INFO_51_CODE.equalsIgnoreCase(commonTodoInDto.getCdTodoCf())) {
					// CallWhoToSendToDo
					List<WorkLoadDto> workLoadList = this.getWorkloadsForEligibility(
							savePlacementDtlReq.getPostEventIPDto().getIdStage(), commonTodoInDto.getIdTodoCfEvent());
					for (WorkLoadDto workloadDto : workLoadList) {
						commonTodoInDto.setIdTodoCfPersAssgn(workloadDto.getIdWrldPerson());
						/*
						 ** Call TodoCommonFunction
						 */
						todoCreateInDto.setMergeSplitToDoDto(commonTodoInDto);
						toDoCreateOutput = commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);

					}
				} /* end if TODO_INFO_50_CODE */
				else {
					/*
					 ** Call TodoCommonFunction
					 */
					todoCreateInDto.setMergeSplitToDoDto(commonTodoInDto);
					toDoCreateOutput = commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
				}
				/**************************************************************************
				 ** (END): Common Function: csub40u ** ToDo common function: from
				 * ToDoInfo
				 **************************************************************************/
				log.info("ToDo common function: from ToDoInfo " + toDoCreateOutput.getIdTodo());
			} /* end if: (TRUE == ToDoFlags[i]) */

		} // end for loop


		if (savePlacementDtlReq.getIndNewActualPlcmt().equalsIgnoreCase(ServiceConstants.Y)) {
			savePlacementDtlRes.setNbrRsrcOpenSlots(openSlots);
		} else {
			savePlacementDtlRes.setNbrRsrcOpenSlots(0L);
		}
		List<Date> dtBORReviewEarliestLst = placementDao.getEarliestReviewBillOfRights(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
		if(!ObjectUtils.isEmpty(dtBORReviewEarliestLst)){
			savePlacementDtlRes.setDtBOREarliest(dtBORReviewEarliestLst.get(0));
		}
		//code for PPM 65209 update dt end for Missing TA
		Date currentPlcmtEndDt = formatDate(savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd());
		TemporaryAbsenceDto temporaryAbsenceDto =  temporaryAbsenceDao.getLatestMissingTA(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
		if(temporaryAbsenceDto!=null){
			Date childRecoveryDt = runawayMissingChildDao.getRecoveryDate(temporaryAbsenceDto.getIdChldMsngEvent());
			if(currentPlcmtEndDt==null && childRecoveryDt!=null && !CompareDates(childRecoveryDt, temporaryAbsenceDto.getDtTemporaryAbsenceEnd())){
				temporaryAbsenceDao.updateTAEndDate(temporaryAbsenceDto.getIdPlacementTa(), childRecoveryDt,savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			}else if(currentPlcmtEndDt!=null && childRecoveryDt!=null ){
				if(currentPlcmtEndDt.before(childRecoveryDt) && !CompareDates(currentPlcmtEndDt, temporaryAbsenceDto.getDtTemporaryAbsenceEnd())){
					temporaryAbsenceDao.updateTAEndDate(temporaryAbsenceDto.getIdPlacementTa(), currentPlcmtEndDt,savePlacementDtlReq.getPostEventIPDto().getIdPerson());
				}else if(!Objects.equals(childRecoveryDt, temporaryAbsenceDto.getDtTemporaryAbsenceEnd())){
					temporaryAbsenceDao.updateTAEndDate(temporaryAbsenceDto.getIdPlacementTa(), childRecoveryDt,savePlacementDtlReq.getPostEventIPDto().getIdPerson());
				}
			}else if(childRecoveryDt==null && !CompareDates(currentPlcmtEndDt, temporaryAbsenceDto.getDtTemporaryAbsenceEnd())){
				temporaryAbsenceDao.updateTAEndDate(temporaryAbsenceDto.getIdPlacementTa(), currentPlcmtEndDt,savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			}
		}
		return savePlacementDtlRes;
	}

	private Long getValidT3CContract(SavePlacementDetailReq savePlacementDetailReq, Long idResource) {

		List<ContractDto> contractDtlList = placementDao.getContractDtl(idResource);
		Long validContractId = null;
		for (ContractDto contractDtl : contractDtlList) {

			PlacementDtlGpDto placementDtlGpDto = new PlacementDtlGpDto();
			placementDtlGpDto
					.setAddrPlcmtCnty(savePlacementDetailReq.getPlacementDtlGpDto().getAddrPlcmtCnty());

			placementDtlGpDto
					.setDtPlcmtStart(savePlacementDetailReq.getPlacementDtlGpDto().getDtPlcmtStart());
			placementDtlGpDto.setIdContract(contractDtl.getIdContract());

			List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageDao.getServicePackageDetails(
					savePlacementDetailReq.getIdCase(),
					savePlacementDetailReq.getIdStage());
			List<String> codesList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(servicePackageDtlDtoList)) {
				ServicePackageDtlDto servicePackageDtlDto = servicePackageDtlDtoList.get(0);
				codesList.add(servicePackageDtlDto.getSvcPkgCd());
				List<String> addonList = servicePackageDtlDtoList.stream()
						.map(ServicePackageDtlDto::getSvcPkgAddonCd).filter(Objects::nonNull)
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(addonList)) {
					codesList.addAll(addonList);
				}
			}

			List<String> contractServices = placementDao.getContractServices(placementDtlGpDto,
					codesList);

			if (!contractServices.isEmpty() && contractServices.containsAll(codesList)) {
				validContractId = contractDtl.getIdContract();
				break;
			}

		}
		return validContractId;
	}


	private void SendCourtHearingENotification(EmailNotificationDto emailRequest)
					throws AddressException, MessagingException{

		emailRequest.setFromAddress(QRTP_FROM_ADDRESS);
		emailRequest.setEmailToList(emailRequest.getEmailToList());
		// Create the structure of the email
		// Create Mime Message Object.
		MimeMessage message = createMimeMessageObjectQRTP(emailRequest);
		addAddrToMessageQRTP(emailRequest, message);
		// Add header and message body.
		message.setText(emailRequest.getMessage());
		message.setSubject(emailRequest.getSubject());
		Transport.send(message);

		System.out.println("Email Sent!!!");
	}

	private void addAddrToMessageQRTP(EmailNotificationDto emailRequest, MimeMessage message)
			throws AddressException, MessagingException {

		String fromAdress = emailRequest.getFromAddress();

		if (!ObjectUtils.isEmpty(emailRequest.getEmailToList())) {
			InternetAddress[] toAddressList = new InternetAddress[emailRequest.getEmailToList().size()];
			for (int emailAddressIndex = 0; emailAddressIndex < emailRequest.getEmailToList().size(); emailAddressIndex++) {
				toAddressList[emailAddressIndex] = new InternetAddress(
						emailRequest.getEmailToList().get(emailAddressIndex));
			}
			message.addRecipients(Message.RecipientType.TO, toAddressList);

		}

		if (!ObjectUtils.isEmpty(emailRequest.getEmailCcList())) {
			InternetAddress[] ccAddressList = new InternetAddress[emailRequest.getEmailCcList().size()];
			for (int emailAddressIndex = 0; emailAddressIndex < emailRequest.getEmailCcList().size(); emailAddressIndex++) {
				ccAddressList[emailAddressIndex] = new InternetAddress(
						emailRequest.getEmailCcList().get(emailAddressIndex));
			}
			message.addRecipients(Message.RecipientType.CC, ccAddressList);

		}


		if (!ObjectUtils.isEmpty(emailRequest.getRecipientCopy()))
			message.addRecipients(Message.RecipientType.CC, emailRequest.getRecipientCopy());
		if (!ObjectUtils.isEmpty(emailRequest.getRecipientBcc()))
			message.addRecipients(Message.RecipientType.BCC, emailRequest.getRecipientBcc());
		message.setFrom(new InternetAddress(fromAdress));
	}


	private void cloneHMRequestFromLatestPlcmnt(PlacementAUDDto placementAUDDto, SavePlacementDetailReq savePlacementDetailReq) {
		if (savePlacementDetailReq.isCloneHM()) {
			HMRequest existingHMRequest = new HMRequest();
			List<EventListDto> hMRequest = heightenedMonitoringDao.getHmmRequestEventList(savePlacementDetailReq.getIdStage());
			if(!hMRequest.isEmpty()) {
				EventListDto existingRequest = hMRequest.get(0);
				existingHMRequest = heightenedMonitoringDao.getHmRequestByEventId(existingRequest.getIdEvent());
			}
			if (!ObjectUtils.isEmpty(existingHMRequest)) {
				Stage stage = stageDao.getStageEntityByIdStage(savePlacementDetailReq.getIdStage());
				Event hmRequestEvent = createEvent(placementAUDDto, stage, savePlacementDetailReq, CEVNTTYP_HMM, HMM_SERV_REF_TASK);
				createHMRequest(existingHMRequest, hmRequestEvent, placementAUDDto);
				createHMNarrative(existingHMRequest, hmRequestEvent);
				Event hmApprovalEvent = createEvent(placementAUDDto, stage, savePlacementDetailReq, APPROVAL_EVENT_TYPE, HMM_SERV_REF_TASK_APRV);
				createApproval(hmApprovalEvent);
				createApprovalEventLink(hmApprovalEvent, hmRequestEvent.getIdEvent());
				CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(hmApprovalEvent.getIdCase());
				Todo todo = createTodo(hmApprovalEvent, capsCase);
				createApprover(hmApprovalEvent, todo);
				Optional<HMReqChildLink> childLink = getExistingChildLink(existingHMRequest,hmRequestEvent, placementAUDDto.getIdPlcmtChild());
				updatePlcmntEventStatus(childLink, savePlacementDetailReq.getPostEventIPDto().getIdEvent());
				Event placementAprvEvent = createEvent(placementAUDDto, stage, savePlacementDetailReq, APPROVAL_EVENT_TYPE, APP_SUB_PLCMT);
				createApproval(placementAprvEvent);
				createApprovalEventLink(placementAprvEvent, savePlacementDetailReq.getPostEventIPDto().getIdEvent());
				Todo placementTodo = createTodo(placementAprvEvent, capsCase);
				createApprover(placementAprvEvent, placementTodo);

			}
		}
	}

	private void sendEmailNotificationforQRTP(SavePlacementDetailReq savePlacementDtlReq) {


				if (savePlacementDtlReq.getIsSendEmailNotification() ){
					if (!savePlacementDtlReq.getToEmailList().equals(null) &&
							(ServiceConstants.N).equalsIgnoreCase(savePlacementDtlReq.getIndNewRemovalPlcmt())) {
						try {
						EmailNotificationDto emailRequest = createEmailRequestForQRTP(savePlacementDtlReq);
						this.SendCourtHearingENotification(emailRequest);
						}catch (MessagingException ex) {
							log.error("[sendQRTPDueDateEmail] " + ex.getMessage());
						}
					}
				}

	}

	private MimeMessage createMimeMessageObjectQRTP(EmailNotificationDto emailRequest) {
		Properties properties = System.getProperties();

		properties.put(WebConstants.MAIL_SMTP_HOST, JNDIUtil.getSmtpHost(WebConstants.SMTP_HOST_NAME));
		int smtpPort = WebConstants.TWENTY_FIVE;
		properties.put(WebConstants.MAIL_SMTP_PORT, WebConstants.EMPTY_STRING + smtpPort);
		// Get a mail session
		Session mailSession = Session.getInstance(properties, null);
		// Define a new mail message
		MimeMessage message = new MimeMessage(mailSession);
		return message;
	}
	private EmailNotificationDto createEmailRequestForQRTP(SavePlacementDetailReq savePlacementDtlReq) {
		EmailNotificationDto emailRequest = new EmailNotificationDto();
		emailRequest.setContextType(ServiceConstants.APPLICATION_PDF);
		emailRequest.setFromAddress(ServiceConstants.QRTP_FROM_ADDRESS);

		emailRequest.setEmailToList(savePlacementDtlReq.getToEmailList());

		StringBuilder emailTitle = new StringBuilder();
		emailTitle.append(ServiceConstants.QRTP_EMAIL_SUBJECT) ;
		emailTitle.append(ServiceConstants.SPACE) ;
		emailTitle.append(savePlacementDtlReq.getNmStage()) ;
		emailTitle.append(ServiceConstants.SPACE) ;
		emailTitle.append(savePlacementDtlReq.getChildID()) ;
		emailRequest.setSubject(emailTitle.toString());
		StringBuilder emailConent = new StringBuilder();
		emailConent.append(ServiceConstants.QRTP_EMAIL_CONTENT);
		emailConent.append("\n");
		emailConent.append("\n");
		emailConent.append("\n");
		emailConent.append(ServiceConstants.CourtHearing_DUE_DATE);
		emailConent.append(savePlacementDtlReq.getQrtpPlacementDueDt());
		emailRequest.setMessage(emailConent.toString());
		emailRequest.setWriteByteToInMemory(Boolean.TRUE);
		return emailRequest;
	}

	private boolean CompareDates(Date dtEnd, Date taEndDt) {
		if(dtEnd==null && taEndDt!=null)return false;
		if(dtEnd!=null && taEndDt==null)return false;
		if(dtEnd==null && taEndDt==null)return true;
		return dtEnd.compareTo(taEndDt)==0;
	}

	private Date formatDate(Date dt){
		if(dt==null || DateUtils.getMaxJavaDate().compareTo(dt)==0) return null;
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(0);
		return dt;
	}

	/**
	 * FFPSA 138.28 Congregate care placement change may require changes to foster care eligibility
	 * @return
	 */
	private boolean setFosterCareEligibility (Placement placementBeforeUpdate, SavePlacementDetailReq savePlacementDtlReq, PlacementAUDDto placementAUDReq) {

		if(N.equalsIgnoreCase(savePlacementDtlReq.getIndNewActualPlcmt())) {
			Long newFacility = savePlacementDtlReq.getPlacementDtlGpDto().getIdRsrcFacil();
			Long oldFacility = ObjectUtils.isEmpty(placementBeforeUpdate.getCapsResourceByIdRsrcFacil()) ? null :
				placementBeforeUpdate.getCapsResourceByIdRsrcFacil().getIdResource();

			boolean isPlcmtStartAfterFerderalDeadline = savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
				.compareTo(DateUtils.stringToDate(codesDao.getCodesTable(CodesConstant.CFFPSAIVE,
					CodesConstant.CFFPSAIVE_FFPSA_DL).stream().findFirst().get().getDecode())) >= 0;

			boolean isPlcmtCreatedBeforeBatchRun = savePlacementDtlReq.getPostEventIPDto().getDtEventOccurred()
				.before(DateUtils.stringToDateTime(
					batchParametersDao.getBatchParameters(FFPSA_BATCH_PROGRAM, FFPSA_BATCH_PARAM).getTxtParameterValue(),
					SQL_DATE_FORMAT));

			setCongregatedCareIndicator(placementAUDReq);
			boolean isPlcmtAnytimeCongreteCare = Y.equalsIgnoreCase(placementAUDReq.getIndCongregateCare())
				|| Y.equalsIgnoreCase(placementBeforeUpdate.getIndCongregateCare());

			boolean isPlcmtTypeChanged = !savePlacementDtlReq.getPlacementDtlGpDto().getCdPlcmtType()
				.equalsIgnoreCase(placementBeforeUpdate.getCdPlcmtType());
			boolean isPlcmtStartDtChanged = savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtStart()
				.compareTo(placementBeforeUpdate.getDtPlcmtStart()) != 0;
			// Either old or new facility is null or facility is changed, set the flag
			boolean isFacilityChanged = ((ObjectUtils.isEmpty(newFacility) ^ ObjectUtils.isEmpty(oldFacility))
				|| (!ObjectUtils.isEmpty(newFacility) && !ObjectUtils.isEmpty(oldFacility) && !newFacility.equals(oldFacility)));
			Date newPlcmtEndDate = savePlacementDtlReq.getPlacementDtlGpDto().getDtPlcmtEnd();
			Date oldPlcmtEndDate = placementBeforeUpdate.getDtPlcmtEnd();
			boolean isPlcmtEndDtChanged = (((ObjectUtils.isEmpty(newPlcmtEndDate) ^ ObjectUtils.isEmpty(oldPlcmtEndDate))
				&& (!ObjectUtils.isEmpty(oldPlcmtEndDate) && oldPlcmtEndDate.compareTo(MAX_DATE) != 0))
				|| (!ObjectUtils.isEmpty(newPlcmtEndDate) && !ObjectUtils.isEmpty(oldPlcmtEndDate)
				&& newPlcmtEndDate.compareTo(oldPlcmtEndDate) != 0));

			if (isPlcmtStartAfterFerderalDeadline && isPlcmtCreatedBeforeBatchRun && isPlcmtAnytimeCongreteCare
				&& (isPlcmtTypeChanged || isPlcmtStartDtChanged || isPlcmtEndDtChanged || isFacilityChanged)) {
				return true;
			}
		}
		return false;
	}

	/**
	* FFPSA: UC 138 : Placement Information - Congregate Care Placement Determination
	*/
	private void setCongregatedCareIndicator(PlacementAUDDto placementAUDDto) {
		ResourceDto resourceDto;
		String placementType = placementAUDDto.getCdPlcmtType();
		String facilityType = null;

		// Default: BR 138.23 Placement is not a congregate care
		placementAUDDto.setIndCongregateCare(BOOLEAN_FALSE);

		if(!ObjectUtils.isEmpty(placementType) && !ObjectUtils.isEmpty(placementAUDDto.getIdRsrcFacil())) {
			resourceDto = capsResourceDao.getResourceById(placementAUDDto.getIdRsrcFacil());

			if (!ObjectUtils.isEmpty(resourceDto)) {
				facilityType = resourceDto.getCdRsrcFacilType();
			}

			// BR 138.18-22 Determine if placement is a congregate care setting
			switch (placementType) {
				case CPLMNTYP_030: // FPS Contracted Foster Placement
				case CPLMNTYP_050: // TYC
				case CPLMNTYP_060: // JPC
				case CPLMNTYP_040: // Non-FPS Paid
					if (CFACTYP2_80.equalsIgnoreCase(facilityType) // General Residential Operations GRO
						|| CFACTYP2_67.equalsIgnoreCase(facilityType) // Emergency Shelter
						|| CFACTYP2_QP.equalsIgnoreCase(facilityType) // QRTP
						|| CFACTYP2_64.equalsIgnoreCase(facilityType)) { // Residential Treat RTC
						placementAUDDto.setIndCongregateCare(BOOLEAN_TRUE);
					}
					break;
				case CPLMNTYP_032: // TEP
					if(CFACTYP2_86.equalsIgnoreCase(facilityType)) { // TEP (Temporary Emergency Placement)
						placementAUDDto.setIndCongregateCare(BOOLEAN_TRUE);
					}
					break;
			}
		}
		log.info("setCongregatedCareIndicator :: PlcmtEventId :" + placementAUDDto.getIdPlcmtEvent() +
			": ResourceId :" + placementAUDDto.getIdRsrcFacil() + ": placementType :" + placementType + ": facilityType :" + facilityType
		+ ": CongregatedCareIndicator :" + placementAUDDto.getIndCongregateCare());
	}

	public PlacementAUDDto savePlacementInformation(PlacementAUDDto placementAUDDto) {

		Integer errorCode = ServiceConstants.Zero;
		PlacementAUDDto PlacementAUDRes = new PlacementAUDDto();
		Long checkOverlapPlacementinDiffCases;

		if(ObjectUtils.isEmpty(placementAUDDto.getIndCongregateCare())) {
			setCongregatedCareIndicator(placementAUDDto);
		}
		PlacementAUDRes.setIndCongregateCare(placementAUDDto.getIndCongregateCare());

			switch (placementAUDDto.getReqFuncCd()) {

			case ServiceConstants.REQ_FUNC_CD_ADD:

				// check if there's such a ID_STAGE first
				Long stageCount = placementDao.getEventStageCount(placementAUDDto.getIdStage());

				if (ObjectUtils.isEmpty(stageCount)) {
					throw new ServiceLayerException("Stage Not Present");
				}

				// get Placement Events for child
				List<Long> idPlacementEventList = placementDao.getPlacementEventList(placementAUDDto.getIdPlcmtChild(),
						placementAUDDto.getCdPlcmtActPlanned());
				Long idPlacementEvent = 0L;
				if (!ObjectUtils.isEmpty(idPlacementEventList)) {
					idPlacementEvent = idPlacementEventList.get(0);
				}
				if (!ObjectUtils.isEmpty(idPlacementEvent)) {
					//check for records
					if (ServiceConstants.ACTUAL_TYPE.equalsIgnoreCase(placementAUDDto.getCdPlcmtActPlanned())) {
						/*
						 * check for other OPEN PLACEMENTS for child IN CLOSED
						 * STAGES. If open ADO placements already identified for
						 * auto closure, skip this edit. Do not trigger edit if
						 * placement added and ended prior to open placement.
						 */
						if (ServiceConstants.N.equalsIgnoreCase(placementAUDDto.getIndCloseADOPlcmt())
								&& ServiceConstants.N.equalsIgnoreCase(placementAUDDto.getIndNewPlcmtEndsPrior())) {
							Long openPlacementCount = placementDao.checkOpenPlacement(placementAUDDto);

							if (!ObjectUtils.isEmpty(openPlacementCount) && openPlacementCount > 0L) {
								errorCode = Messages.MSG_OPNPLCMT_CLSDSTG_FIXER;
							}
						}

						/*
						 * check for other open placements for same child IN
						 * DIFFERENT CASES. If open ADO placements identified
						 * already for auto closure, Do not trigger edit if
						 * placement added and ended prior to open placement.
						 */

						if (ServiceConstants.N.equalsIgnoreCase(placementAUDDto.getIndCloseADOPlcmt())
								&& ServiceConstants.N.equalsIgnoreCase(placementAUDDto.getIndNewPlcmtEndsPrior())
								&& errorCode == 0L) {
							Long openPlacementDiffCasesCount = placementDao.checkOtherOpenPlacement(placementAUDDto);

							if (!ObjectUtils.isEmpty(openPlacementDiffCasesCount) && openPlacementDiffCasesCount > 0L) {
								errorCode = Messages.MSG_OPNPLCMT_OPNSTAGE_DIFFCASE;

							}
						}

						/*
						 * VALIDATE 1: Check if new records overlaps other records
						 * on LEFT (works whether new record overlaps 1 or more
						 * existing records.
						 *
						 * Added conditional so that placements that start and end
						 * on the same day are not included in the overlap/gap
						 * validation
						 */
						if (errorCode == 0L) {
							List<Long> leftOverlappingPlacements = placementDao.checkLeftOverlaps(placementAUDDto);

							if (!ObjectUtils.isEmpty(leftOverlappingPlacements) && leftOverlappingPlacements.size() > 0) {
								for (Long idPlacemntEvent : leftOverlappingPlacements) {

									/*
									 * added additional SQL select statement to check if
									 * there are any open placements in open stages. If
									 * the stage is closed, it does not matter if the
									 * placement does not have an end-date.
									 *
									 * select count for the number of rows given a
									 * certain id_plcmnt_event that has open subcare
									 * stages that are not end-dated.
									 */

									Long openPlacementInOpenStageCount = placementDao
											.getOpenPlacementInOpenStages(idPlacemntEvent);

									if (!ObjectUtils.isEmpty(openPlacementInOpenStageCount)
											&& openPlacementInOpenStageCount > 0L) {
										errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
									} else {
										// the subcare stage is closed and check if the
										// placement is end dated.
										Long openPlacementInClosedStagesCount = placementDao
												.getOpenPlacementInClosedStages(idPlacemntEvent);
										if (!ObjectUtils.isEmpty(openPlacementInClosedStagesCount)
												&& openPlacementInClosedStagesCount > 0L) {
											errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
										}
									}
								}
							}
						}

						if (errorCode == 0L) {
							/*
							 * VALIDATE 2: Check if new records overlaps other records
							 * on RIGHT (works whether new record overlaps 1 or more
							 * existing records)
							 *
							 * Added conditional so that placements that start and end
							 * on the same day are not included in the overlap/gap
							 * validation
							 */
							List<Long> rightOverlappingPlacements = placementDao.checkRightOverlaps(placementAUDDto);

							if (!ObjectUtils.isEmpty(rightOverlappingPlacements) && rightOverlappingPlacements.size() > 0) {
								errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;
							}

						}

						if (errorCode == 0L) {
							/*
							 * VALIDATE 3: Check if new records is either identical OR
							 * within a record
							 *
							 * Added conditional so that placements that start and end
							 * on the same day are not included in the overlap/gap
							 * validation
							 */
							List<Long> identicalPlacements = placementDao.getIdenticalRecords(placementAUDDto);

							if (!ObjectUtils.isEmpty(identicalPlacements) && identicalPlacements.size() > 0) {
								for (Long identicalPlacementEvent : identicalPlacements) {
									/*
									 * select count for the number of rows given a
									 * certain id_plcmnt_event that has open subcare
									 * stages that are not end-dated.
									 */

									Long openPlacementsCount = placementDao
											.getOpenPlacementInOpenStages(identicalPlacementEvent);

									if (!ObjectUtils.isEmpty(openPlacementsCount) && openPlacementsCount > 0L) {
										errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
									}
								}
							}
						}

					}

					if (errorCode == 0L) {
						if (ServiceConstants.Y.equalsIgnoreCase(placementAUDDto.getIndPrfrmValidation())) {
							/*
							 * VALIDATE 4:
							 *
							 * Check if the gap on LEFT of dtPlcmtStart is bigger than 1
							 * day. SELECT statement will return record if it finds one,
							 * which means gap is >= 1.0 day ==> ERROR!
							 *
							 * Added conditional so that placements that start and end
							 * on the same day are not included in the overlap/gap
							 * validation
							 */
							List<PlacementDto> placementsWithLeftGap = placementDao.checkLeftGaps(placementAUDDto);

							if (!ObjectUtils.isEmpty(placementsWithLeftGap) && placementsWithLeftGap.size() > 0) {
								errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
							}

							/*
							 * VALIDATE 5:
							 *
							 * Check if the gap on RIGHT of dtPlcmtStart is bigger than
							 * 1 day. SELECT statement will return record if it finds
							 * one, which means gap is >= 1.0 day ==> ERROR!
							 *
							 * Added conditional so that placements that start and end
							 * on the same day are not included in the overlap/gap
							 * validation
							 */

							List<PlacementDto> placementsWithRightGap = placementDao.checkRightGaps(placementAUDDto);
							if (!ObjectUtils.isEmpty(placementsWithRightGap) && placementsWithRightGap.size() > 0) {
								errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
							}

						}
					}

				}
				/*
				 * VALIDATE 6:
				 *
				 * check other placements overlap for same child IN DIFFERENT CASES.
				 */
				/*PlacementDto placementDto = new PlacementDto();
			placementDto.setDtPlcmtStart(placementAUDDto.getDtPlcmtStart());
			placementDto.setDtPlcmtEnd(placementAUDDto.getDtPlcmtEnd());
			checkOverlapPlacementinDiffCases = placementDao.checkPlacementsOverlapingInDifferenctCases(placementAUDDto,
					placementDto);

			if (!ObjectUtils.isEmpty(checkOverlapPlacementinDiffCases) && checkOverlapPlacementinDiffCases > 0L
					&& errorCode == 0L) {
				errorCode = Messages.MSG_OPNPLCMT_OPNSTAGE_DIFFCASE;
			}*/
				/*
				 * Here record is ready to be inserted.
				 *
				 * New record could be:
				 *
				 * 1. NON-ACTUAL type: Just insert it in directly regardless 2.
				 * ACTUAL type: a. No record exists of ACTUAL type. Then this is a
				 * brand new record. Just insert it into the chain. b. ACTUAL
				 * records exists, but this new record passes all validation. Just
				 * insert it into the chain
				 */

				if (errorCode > ServiceConstants.Zero) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(errorCode);
					PlacementAUDRes.setErrorDto(errorDto);
				} else {

					placementDao.savePlacement(placementAUDDto);
					PlacementAUDRes.setNbrValidationMsg(ServiceConstants.SQL_SUCSS);
				}
				break;
			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				boolean checkForLeftGaps = false;
				boolean checkForRightGaps = false;
				PlacementDto primaryPlacementDto = new PlacementDto();
				if (placementAUDDto.getCdPlcmtActPlanned().equalsIgnoreCase(ServiceConstants.ACTUAL_TYPE)) {
					/*
					 * Check if there's a record of this Primary Key at all. It
					 * should already exist in order to do an update.
					 *
					 * If existed, gets START and END date for other processing
					 * (with timestamp removed.)
					 */
					primaryPlacementDto = placementDao.getPlacementForUpdate(placementAUDDto);

					/*
					 * If the 2 dates (i.e., dtPlcmtStart vs curr_plcmt_start) are
					 * different then the user wants to change that end (left or
					 * right)
					 *
					 * Use function sign(abs(...) to return either 0 or +1
					 *
					 * (Start_Date = LEFT END) (End_Date = RIGHT END)
					 *
					 * 0 = No update that end (date are same) 1 = update that end
					 * (date are different)
					 */

					Date dtCurrPlacementStart = primaryPlacementDto.getCurrPlcmtStart();
					Date dtCurrPlacementEnd = primaryPlacementDto.getCurrPlcmtEnd();

					Date dtPlacementStart = primaryPlacementDto.getDtPlcmtStart();
					Date dtPlacmenetEnd = primaryPlacementDto.getDtPlcmtEnd();

					if (!ObjectUtils.isEmpty(dtPlacementStart) && !ObjectUtils.isEmpty(dtCurrPlacementStart)
							&& dtPlacementStart.compareTo(dtCurrPlacementStart) != 0)
						checkForLeftGaps = true;
					if (!ObjectUtils.isEmpty(dtPlacmenetEnd) && !ObjectUtils.isEmpty(dtCurrPlacementEnd)
							&& dtPlacmenetEnd.compareTo(dtCurrPlacementEnd) != 0) {
						checkForRightGaps = true;

					}
					/*
					 * VALIDATE 1:
					 *
					 * check for LEFT-SIDE OVERLAP If new START_DATE overlaps any of
					 * its LEFT record(s)
					 *
					 * (If its overlaps some, then it must at least overlaps its
					 * immediate previous record, and that's what we want to know)
					 *
					 * Unlike checking for GAP_EXIST_1 and GAP_EXIST_2 (where we
					 * check for these gaps only if the new date for that end is
					 * different from the corresponding date in the existing record)
					 * we must always check for OVERLAP_1 and OVERLAP_2.
					 */

					List<Long> leftOverlappingPlacements = placementDao.checkLeftOverlapForUpdate(placementAUDDto,
							primaryPlacementDto);

					if (!ObjectUtils.isEmpty(leftOverlappingPlacements)) {
						for (Long leftOverlapPlacementEvent : leftOverlappingPlacements) {
							/*
							 * added additional SQL select statement to check if
							 * there are any open placements in open stages. If the
							 * stage is closed, it does not matter if the placement
							 * does not have an end-date.
							 */

							/*
							 ** select count for the number of rows given a certain
							 * id_plcmnt_event that has open subcare stages that are
							 * not end-dated.
							 */
							Long openSubStageCount = placementDao.getOpenSubStageCount(leftOverlapPlacementEvent);

							if (!ObjectUtils.isEmpty(openSubStageCount) && openSubStageCount > 0L) {
								errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
							} else {
								// the subcare stage is closed and check if the
								// placement is end dated.
								Long openPlcmtCloseStageCount = placementDao
										.getOpenPlacementInClosedStages(leftOverlapPlacementEvent);

								if (!ObjectUtils.isEmpty(openPlcmtCloseStageCount) && openPlcmtCloseStageCount > 0L) {
									errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
								}
							}
						}
					}

					/*
					 * VALIDATE 2:
					 *
					 * check for RIGHT-SIDE OVERLAP
					 *
					 * If new START_DATE overlaps any of its RIGHT record(s)
					 *
					 * (If its overlaps some, then it must at least overlaps its
					 * immediate next record, and that's what we want to know)
					 */

					List<Long> rightOverlappingPlacements = placementDao.checkRightOverlapForUpdate(placementAUDDto,
							primaryPlacementDto);

					if (!ObjectUtils.isEmpty(rightOverlappingPlacements) && rightOverlappingPlacements.size() > 0) {
						errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;

					}
				} // End of Actual Type
				if (ServiceConstants.Y.equalsIgnoreCase(placementAUDDto.getIndPrfrmValidation())) {
					if (checkForLeftGaps) {
						/*
						 * VALIDATE 4:
						 *
						 * Is Gap LEFT of dtPlcmtStart >= 1.0 day Check this gap
						 * ONLY IF hI_dtDtPlcmtStart != curr_ploc_star because: if
						 * the 2 are the same, then the user does NOT want /* to
						 * update that end. Only when the 2 are different does it
						 * mean that the user wants to update that end
						 */
						List<PlacementDto> placementWithLeftGap = placementDao.checkLeftGapForUpdate(placementAUDDto,
								primaryPlacementDto);
						if (!ObjectUtils.isEmpty(placementWithLeftGap) && placementWithLeftGap.size() > 0) {
							errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
						}
					}

					if (checkForRightGaps) {

						/*
						 * VALIDATE 5:
						 *
						 * Gap RIGHT of dtPlcmtEnd Check this gap ONLY IF dtPlcmtEnd
						 * <> curr_plcmt_end
						 */

						List<PlacementDto> placementWithRightGap = placementDao.checkRightGapForUpdate(placementAUDDto,
								primaryPlacementDto);
						if (placementWithRightGap.size() > 0 && !placementWithRightGap.isEmpty()) {
							errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
						}
					}
				}

				/*
				 * VALIDATE 6:
				 *
				 * check other placements overlap for same child IN DIFFERENT CASES.
				 */

				checkOverlapPlacementinDiffCases = placementDao.checkPlacementsOverlapingInDifferenctCases(placementAUDDto,
						primaryPlacementDto);

				if (!ObjectUtils.isEmpty(checkOverlapPlacementinDiffCases) && checkOverlapPlacementinDiffCases > 0L) {
					errorCode = Messages.MSG_OPNPLCMT_OPNSTAGE_DIFFCASE;
				}
				/*
				 * Here record is ready to be updated. New record could be:
				 *
				 * 1. NON-BLOC type: Just update it regardless if time overlaps
				 *
				 * 2. BLOC type: Pass all validation (supposing it is requeted to do
				 * so)
				 *
				 * Update current record with information from host input variables
				 */
				if (errorCode > ServiceConstants.Zero) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(errorCode);
					PlacementAUDRes.setErrorDto(errorDto);
				} else {
					placementDao.updatePlacement(placementAUDDto);
				}

				/*
				 * end Kinship record when placement ended for DD/DF Living
				 * Arrangements if no payment made yet.
				 */

				if (ServiceConstants.ARC_MAX_YEAR != placementAUDDto.getDtPlcmtEnd().getYear()
						&& (ServiceConstants.PLCMT_LIV_ARR_PRIMARY.equalsIgnoreCase(placementAUDDto.getCdPlcmtLivArr())
								|| ServiceConstants.PLCMT_LIV_ARR_FICTIVE
								.equalsIgnoreCase(placementAUDDto.getCdPlcmtLivArr()))) {
					placementDao.updateKinshipRecord(placementAUDDto);
				}

				break;

				/*
				 * UPDATE from PLANNED to ACTUAL A new case was added specifically to
				 * handle the scenario when a planned placement has already been saved
				 * and the start date remains the same. The problem with using the Case
				 * Update is that if the start date does not change no date validation
				 * will occur. This is due to two incorrect assumptions 1) that the date
				 * would have been validated on the insert and 2) that unless a change
				 * occurred there would be no reason to validate the date and check for
				 * overlap/gap.
				 */
			case ServiceConstants.Y:
				placementDao.checkIdStage(placementAUDDto);

				/*
				 * Check if there's any record of this ID_EVENT and STAGE. If none,
				 * then everything passed. No need to go through all these
				 * validation.
				 */

				List<Long> placementEventList = placementDao.getPlacementEventForChild(placementAUDDto);

				if (!CollectionUtils.isEmpty(placementEventList)) {
					if (placementAUDDto.getCdPlcmtActPlanned().equalsIgnoreCase(ServiceConstants.ACTUAL_TYPE)) {
						/*
						 * VALIDATE 1:
						 *
						 * Check if new records overlaps other records on LEFT (works
						 * whether new record overlaps 1 or more existing records
						 */

						List<Long> newLeftOverlappingPlacements = placementDao.checkLeftOverlapForNewRecords(placementAUDDto);
						if (!ObjectUtils.isEmpty(newLeftOverlappingPlacements) && newLeftOverlappingPlacements.size() > 0) {
							for (Long idNewLeftOverlappingPlacement : newLeftOverlappingPlacements) {
								/*
								 * check if there are any open placements in open
								 * stages. If the stage is closed, it does not matter if
								 * the placement does not have an end-date.
								 */

								/*
								 * select count for the number of rows given a certain
								 * id_plcmnt_event that has open subcare stages that are
								 * not end-dated.
								 */

								Long openSubStageCount = placementDao.getOpenSubStageCount(idNewLeftOverlappingPlacement);
								if (!ObjectUtils.isEmpty(openSubStageCount) && openSubStageCount > 0L) {
									errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
								} else {

									// the subcare stage is closed and check if the placement is end dated.

									Long openPlcmtCloseStageCount = placementDao
											.getOpenPlacementInClosedStages(idNewLeftOverlappingPlacement);
									if (!ObjectUtils.isEmpty(openPlcmtCloseStageCount) && openPlcmtCloseStageCount > 0L) {
										errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
									}
								}
							}
						}
						/*
						 * VALIDATE 2:
						 *
						 * Check if new records overlaps other records on RIGHT (works
						 * whether new record overlaps 1 or more existing records
						 */

						List<Long> newRightOverlappingPlacements = placementDao.checkRightOverlapForNewRecords(placementAUDDto);

						if (!ObjectUtils.isEmpty(newRightOverlappingPlacements) && newRightOverlappingPlacements.size() > 0) {
							errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;
						}
						/*
						 * VALIDATE 3:
						 *
						 * Check if new records is either identical OR within a record
						 */

						List<Long> newIdenticalPlacements = placementDao.getIdenticalNewRecords(placementAUDDto);
						if (!ObjectUtils.isEmpty(newIdenticalPlacements) && newIdenticalPlacements.size() > 0) {
							/*
							 * check if there are any open placements in open stages. If
							 * the stage is closed, it does not matter if the placement
							 * does not have an end-date.
							 */
							for (Long idNewIdenticalPlacement : newIdenticalPlacements) {

								/*
								 * select count for the number of rows given a certain
								 * id_plcmnt_event that has open subcare stages that are
								 * not end-dated.
								 */
								Long checkForSUBStageCount = placementDao.getOpenSubStageCount(idNewIdenticalPlacement);
								if (!ObjectUtils.isEmpty(checkForSUBStageCount) && checkForSUBStageCount > 0L) {
									errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;

								} else {

									// the subcare stage is closed and check if the placement is end dated.

									Long openPlcmtCloseStageCount = placementDao
											.getOpenPlacementInClosedStages(idNewIdenticalPlacement);

									if (!ObjectUtils.isEmpty(openPlcmtCloseStageCount) && openPlcmtCloseStageCount > 0L) {
										errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_1;
									}
								}
							}
						}
					}
					if (placementAUDDto.getIndPrfrmValidation().equalsIgnoreCase(ServiceConstants.Y)) {
						/*
						 * VALIDATE 4:
						 *
						 * Check if the gap on LEFT of dtPlcmtStart is bigger than 1
						 * day. Method return record if it finds one, which means gap is
						 * >= 1.0 day
						 */
						List<PlacementDto> newplacementsWithLeftGap = placementDao
								.getNewRecordsHavingLeftGapMoreThanOneDay(placementAUDDto);
						if (!ObjectUtils.isEmpty(newplacementsWithLeftGap) && newplacementsWithLeftGap.size() > 0) {
							errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
						}

						/*
						 * VALIDATE 5:
						 *
						 * Check if the gap on RIGHT of dtPlcmtStart is bigger than 1
						 * day. Method will return record if it finds one, which means
						 * gap is >= 1.0 day
						 */

						List<PlacementDto> newplacementsWithRightGap = placementDao
								.getNewRecordsHavingRightGapMoreThanOneDay(placementAUDDto);
						if (newplacementsWithRightGap.size() > 0 && !newplacementsWithRightGap.isEmpty()) {
							errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
						}
					}

				}

				/*
				 * Here record is ready to be updated.
				 *
				 * ACTUAL type: ACTUAL records exists, but this new record passes
				 * all validation.
				 */
				if (errorCode > ServiceConstants.Zero) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(errorCode);
					PlacementAUDRes.setErrorDto(errorDto);
				} else {
					placementDao.saveAndClosePlacement(placementAUDDto);
					PlacementAUDRes.setNbrValidationMsg(ServiceConstants.SQL_SUCSS);
				}
				/*
				 * end Kinship record when placement ended for DD/DF Living
				 * Arrangements, if no payment made yet.
				 */

				if (ServiceConstants.ARC_MAX_YEAR != placementAUDDto.getDtPlcmtEnd().getYear()
						&& (ServiceConstants.PLCMT_LIV_ARR_PRIMARY.equalsIgnoreCase(placementAUDDto.getCdPlcmtLivArr())
								|| ServiceConstants.PLCMT_LIV_ARR_FICTIVE
								.equalsIgnoreCase(placementAUDDto.getCdPlcmtLivArr()))) {
					placementDao.updateKinshipInd(placementAUDDto);
				}
				break;
			}

		return PlacementAUDRes;

	}

	private PlacementAUDDto CallCreatePCAPlacement(SavePlacementDetailReq savePlacementDtlReq) {

		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		postEventIPDto.setTsLastUpdate(savePlacementDtlReq.getPostEventIPDto().getTsLastUpdate());
		postEventIPDto.setDtEventOccurred(new Date());
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventIPDto.setIdStage(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdPCAStage());
		postEventIPDto.setCdTask(ServiceConstants.PCA_PLCMT_CD_TASK);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
		postEventIPDto.setCdEventType(savePlacementDtlReq.getPostEventIPDto().getCdEventType());
		postEventIPDto.setEventDescr(savePlacementDtlReq.getpCAPlacmentDtlDto().getEventDescr());
		postEventIPDto.setIdPerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
		/* Setup Data For Event Person Link information */

		List<PostEventDto> postEventDtos = new ArrayList<>();
		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventDto.setIdPerson(savePlacementDtlReq.getPlacementDtlGpDto().getIdPlcmtChild());
		postEventDtos.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtos);

		// PostEvent
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		// Commented Unused Code
		/*
		 * if (!ObjectUtils.isEmpty(postEventOPDto)) { Long idNewPlcmtEvent =
		 * postEventOPDto.getIdEvent(); }
		 */
		// CSES37D
		PlacementDto placementDto = processPlacement(
				placementDao.selectPlacement(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdPCAAppPlcmtEvent()));

		/* Create Placement Record */

		/* Retrieve Resource Details for the give Resource Facility ID. */
		// CRES04D
		ResourceDto resourceDto = capsResourceDao
				.getResourceById(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdRsrcFacil());

		// CallCAUD45D
		PlacementAUDDto placementAUDDto = new PlacementAUDDto();
		placementAUDDto.setIdPlcmtEvent(postEventOPDto.getIdEvent());
		placementAUDDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		placementAUDDto.setIdCase(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdPCACase());
		placementAUDDto.setIdStage(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdPCAStage());
		placementAUDDto.setCdPlcmtType(ServiceConstants.PCA_PLCMT_TYPE);
		placementAUDDto.setCdPlcmtLivArr(savePlacementDtlReq.getpCAPlacmentDtlDto().getCdPCAPlcmtLivArr());
		/* Clear Placement Removal Reason for PCA Placement */
		placementAUDDto.setCdPlcmtRemovalRsn(ServiceConstants.NULL_STRING);
		placementAUDDto.setTxtPlcmtRemovalRsn(ServiceConstants.NULL_STRING);
		/** Start Date of PCA Placment will be the End Date of SUB Placement **/
		placementAUDDto.setDtPlcmtStart(placementDto.getDtPlcmtEnd());
		placementAUDDto.setDtPlcmtEnd(ServiceConstants.GENERIC_END_DATE);
		/* populate other attributse. */
		placementAUDDto.setIdRsrcAgency(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdRsrcAgency());
		placementAUDDto.setIdRsrcFacil(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdRsrcFacil());
		placementAUDDto.setIdContract(savePlacementDtlReq.getpCAPlacmentDtlDto().getIdContract());
		placementAUDDto.setIdPlcmtChild(placementDto.getIdPlcmtChild());
		/* Set CdPlcmtActPlanned to ACTUAL */
		placementAUDDto.setCdPlcmtActPlanned(ServiceConstants.ACTUAL);
		/* Copy Name, Address and Phone Number */
		placementAUDDto.setPlcmtFacil(resourceDto.getNmResource());
		placementAUDDto.setAddrPlcmtLn1(resourceDto.getAddrRsrcStLn1());
		placementAUDDto.setAddrPlcmtLn2(resourceDto.getAddrRsrcStLn2());
		placementAUDDto.setAddrPlcmtCity(resourceDto.getAddrRsrcCity());
		placementAUDDto.setAddrPlcmtSt(resourceDto.getCdRsrcState());
		placementAUDDto.setAddrPlcmtZip(resourceDto.getAddrRsrcZip());
		placementAUDDto.setAddrPlcmtCnty(resourceDto.getCdRsrcCnty());
		placementAUDDto.setPlcmtTelephone(resourceDto.getNbrRsrcPhn());
		placementAUDDto.setPlcmtPhoneExt(resourceDto.getNbrRsrcPhoneExt());
		/*
		 * Copy Placement Information Checkboxes Only Relative and Fictive Kin
		 * values should be copied
		 */
		placementAUDDto.setCdPlcmtInfo4(placementDto.getCdPlcmtInfo4());
		placementAUDDto.setCdPlcmtInfo5(placementDto.getCdPlcmtInfo5());
		placementAUDDto.setCdPlcmtInfo18(placementDto.getCdPlcmtInfo18());
		placementAUDDto.setCdPlcmtInfo19(placementDto.getCdPlcmtInfo19());
		placementAUDDto.setCdPlcmtInfo20(placementDto.getCdPlcmtInfo20());
		/*
		 * If one of Placement Information Check Boxes Relative Placement -
		 * Grandparent (180) OR Relative Placement - Aunt/Uncle (190) OR
		 * Relative Placement - Other Relative (200) is selected automatically
		 * populate Relative Placement (040)
		 */

		if (ServiceConstants.PLCMT_REL_GRAND_PARENT.equalsIgnoreCase(placementDto.getCdPlcmtInfo18())
				|| ServiceConstants.PLCMT_REL_AUNT_UNCLE.equalsIgnoreCase(placementDto.getCdPlcmtInfo19())
				|| ServiceConstants.PLCMT_REL_OTHER_REL.equalsIgnoreCase(placementDto.getCdPlcmtInfo20())) {
			placementAUDDto.setCdPlcmtInfo4(ServiceConstants.PLCMT_RELATIVE);
		}
		placementAUDDto.setCdPlcmtService(placementDto.getCdPlcmtService());
		placementAUDDto.setIndPlcmtNotApplic(placementDto.getIndPlcmtNotApplic());

		/**
		 * Agency Home field should not be populated in case of Pca Placement
		 **/
		placementAUDDto.setPlcmtAgency(ServiceConstants.EMPTY_STR);
		placementAUDDto.setIdLastUpdatePerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
		// FFPSA ALM 18330 - New auto PCA placement creation
		placementAUDDto.setIdCreatedPerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());

		// Record is ready to call CAUD45D dam
		placementAUDDto = savePlacementInformation(placementAUDDto);
		return placementAUDDto;
	}

	/**
	 * Gets a list of workers if any of the secondary workers assigned are eligibility specialist,
	 * if not returns primary worker
	 * @param idStage
	 * @param idEvent
	 * @return
	 */
	private List<WorkLoadDto> getWorkloadsForEligibility(Long idStage, Long idEvent) {
		List<WorkLoadDto> workloadDtoList = new ArrayList<>();

		boolean indEmpIsSpecialist = ServiceConstants.FALSEVAL;
		int i = -1;
		int primaryWorkerIndex = 0;
		boolean secondaryWorkerExist = ServiceConstants.FALSEVAL;
		boolean validEligibilityExist = ServiceConstants.FALSEVAL;
		if (!ObjectUtils.isEmpty(idEvent)) {
			EligibilityDto eligibilityDtl = placementDao.getEligibilityDtl(idEvent);
			if (!ObjectUtils.isEmpty(eligibilityDtl) && (ObjectUtils.isEmpty(eligibilityDtl.getDtEligEnd())
					||DateUtils.getDefaultFutureDate().equals(eligibilityDtl.getDtEligEnd()))
					&& ((ServiceConstants.TITLE_IV_E.equalsIgnoreCase(eligibilityDtl.getCdEligSelected()))
							|| (ServiceConstants.STATE_PAID.equalsIgnoreCase(eligibilityDtl.getCdEligSelected()))
							|| (ServiceConstants.MAO.equalsIgnoreCase(eligibilityDtl.getCdEligSelected())))) {
				validEligibilityExist = ServiceConstants.TRUE_VALUE;
			}
			if (!validEligibilityExist) {
				return workloadDtoList;
			}
		}
		// callCSEC86D
		List<WorkLoadDto> fetchedWorkloadDtoList = legalStatusDao.getWorkLoadsForStage(idStage);

		/*
		 * Find if there is a secondary worker and also keep the index of
		 * primary worker. if there is no secondary worker we need to send the
		 * todo to primary.
		 */
		if (!CollectionUtils.isEmpty(fetchedWorkloadDtoList) && fetchedWorkloadDtoList.size() > 0) {
			for (i = 0; i < fetchedWorkloadDtoList.size(); i++) {
				if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
						.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_PRIMARY)) {
					primaryWorkerIndex = i;
				} else if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
						.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_SECONDARY)) {
					secondaryWorkerExist = ServiceConstants.TRUEVAL;
					if(primaryWorkerIndex!=-1){
						break;
					}
				}
			} /* end for loop */
		}
		/*
		 * if there is a secondary worker, check to see if they are eligibility
		 * specialists.
		 */
		for (i = 0; i < fetchedWorkloadDtoList.size() && secondaryWorkerExist; i++) {
			indEmpIsSpecialist = ServiceConstants.FALSEVAL;
			if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
					.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_SECONDARY)) {
				// CallCLSCB4D
				indEmpIsSpecialist = this.getEmployeeSecurityProfile(fetchedWorkloadDtoList.get(i).getIdWrldPerson());

			}
			if (indEmpIsSpecialist) {
				workloadDtoList.add(fetchedWorkloadDtoList.get(i));
			}
		}
		if (workloadDtoList.size() == 0) {
			workloadDtoList.add(fetchedWorkloadDtoList.get(primaryWorkerIndex));
		}

		return workloadDtoList;
	}

	/**
	 *
	 * Method Name: getEmployeeSecurityProfile Method Description: call service
	 * CLSCB4D , check employee security profile
	 *
	 * @param idPerson
	 * @return
	 */
	private Boolean getEmployeeSecurityProfile(long idPerson) {
		Boolean empHasSecurity = Boolean.FALSE;
		List<EmpSecClassLink> empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(idPerson);
		for (EmpSecClassLink empSecClassLink : empSecClassLinkList) {
			if (ServiceConstants.CHAR_ONE == empSecClassLink.getSecurityClass().getTxtSecurityClassProfil()
					.charAt(11)) {
				empHasSecurity = Boolean.TRUE;
				break;
			}
		}
		return empHasSecurity;
	}/* CallCLSCB4D */

	/**
	 * Method Name: getPriorPlacementsById Method Description: This method
	 * returns prior placement list based on idPlacementEvent
	 *
	 * @param idPriorPlacementEvent
	 * @return CommonHelperRes
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getPriorPlacementsById(Long idPriorPlacementEvent) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		List<Long> idPlacementEvents = new ArrayList<>();

		idPlacementEvents = placementDao.getPriorPlacementsById(idPriorPlacementEvent);
		commonHelperRes.setIdEvents(idPlacementEvents);
		return commonHelperRes;
	}

	/**
	 * Method Name: getIndChildSibling1 Method Description: This method returns
	 * prior placement Sibling based on idPerson
	 *
	 * @param idPerson
	 * @return CommonHelperRes
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getIndChildSibling1(Long idPerson) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		String indChildSibling1 = "";

		indChildSibling1 = placementDao.getIndChildSibling1(idPerson);
		commonHelperRes.setCdEventStatus(indChildSibling1);
		return commonHelperRes;
	}

	/**
	 * Method Name: getEligibility Method Description: This method returns prior
	 * eligibility based on idPerson
	 *
	 * @param idPerson
	 * @return CommonHelperRes
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getEligibilityEvent(Long idPerson) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		EligibilityDto eligibility = placementDao.getEligibilityEvent(idPerson);
		if (null != eligibility) {
			commonHelperRes.setCdEventStatus(eligibility.getCdEligActual());
			commonHelperRes.setIdEvent(eligibility.getIdEligEvent());
		}
		return commonHelperRes;
	}

	/**
	 * Method Name: setCdplcmtValue Method Description: This method is used to
	 * set the cd plcmt values in teh save request
	 *
	 * @param savePlacementDtlReq
	 * @param placementAUDReq
	 */
	private void setCdplcmtValue(String cdPlcmt, PlacementAUDDto placementAUDReq) {

		switch (cdPlcmt) {

		case "010":
			placementAUDReq.setCdPlcmtInfo1(cdPlcmt);
			break;
		case "020":
			placementAUDReq.setCdPlcmtInfo2(cdPlcmt);
			break;
		case "030":
			placementAUDReq.setCdPlcmtInfo3(cdPlcmt);
			break;
		case "040":
			placementAUDReq.setCdPlcmtInfo4(cdPlcmt);
			break;
		case "050":
			placementAUDReq.setCdPlcmtInfo5(cdPlcmt);
			break;
		case "060":
			placementAUDReq.setCdPlcmtInfo6(cdPlcmt);
			break;
		case "070":
			placementAUDReq.setCdPlcmtInfo7(cdPlcmt);
			break;
		case "080":
			placementAUDReq.setCdPlcmtInfo8(cdPlcmt);
			break;
		case "090":
			placementAUDReq.setCdPlcmtInfo9(cdPlcmt);
			break;
		case "100":
			placementAUDReq.setCdPlcmtInfo10(cdPlcmt);
			break;
		case "110":
			placementAUDReq.setCdPlcmtInfo11(cdPlcmt);
			break;
		case "120":
			placementAUDReq.setCdPlcmtInfo12(cdPlcmt);
			break;
		case "130":
			placementAUDReq.setCdPlcmtInfo13(cdPlcmt);
			break;
		case "140":
			placementAUDReq.setCdPlcmtInfo14(cdPlcmt);
			break;
		case "150":
			placementAUDReq.setCdPlcmtInfo15(cdPlcmt);
			break;
		case "160":
			placementAUDReq.setCdPlcmtInfo16(cdPlcmt);
			break;
		case "170":
			placementAUDReq.setCdPlcmtInfo17(cdPlcmt);
			break;
		case "180":
			placementAUDReq.setCdPlcmtInfo18(cdPlcmt);
			break;
		case "190":
			placementAUDReq.setCdPlcmtInfo19(cdPlcmt);
			break;
		case "200":
			placementAUDReq.setCdPlcmtInfo20(cdPlcmt);
			break;
		}
	}

	/**
	 * MethodName:createIcpcRejectToDo MethodDescription:
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public void createIcpcRejectToDo(Long idEvent, String cdTask, Long idStage, Long userId, String cdStage) {
		List<Long> approvers = placementDao.getApprovers(idEvent);
		EventDto eventDto = eventDao.getEventByid(idEvent); // getting case
		// worker
		Long caseWorker = eventDto.getIdPerson();
		String todoTask = getToDoTask(cdTask); // Checking Task codes
		String formType = "";
		if (ServiceConstants.CD_TASK_SUB_PLCMNT_REQ.equals(cdTask))
			formType = "100A";
		else
			formType = "100B";
		if (caseWorker > ServiceConstants.Zero) {
			String toDoCaseWorkerDesc = formType + " Submitted request not Approved, you must resubmit.";
			this.createIcpcToDo(toDoCaseWorkerDesc, toDoCaseWorkerDesc, todoTask, new Date(), caseWorker, userId,
					idStage, idEvent);
		}
		if (ObjectUtils.isEmpty(approvers)) {
			for (Long idPerson : approvers) {
				if (idPerson != userId) // do not create ToDo to the rejector
				{
					String toDoApprvrDesc = formType + " Approval rejected.";
					this.createIcpcAlert(toDoApprvrDesc, toDoApprvrDesc, ServiceConstants.CD_TODO_INFO_ALERT, cdTask,
							new Date(), idPerson, userId, idStage, idEvent);
				}
			}
		}
	}

	/**
	 * MEthodName: createIcpcAlert MethodDescription:This Method will create
	 * ICPC alert
	 *
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param cdTask
	 * @param dtToDoDue
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public void createIcpcAlert(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, String cdTask,
			Date dtToDoDue, Long idPrsnAssgn, Long idUser, Long idStage, Long idEvent) {
		// TODO Auto-generated method stub
		CommonTodoInDto csub40uig00 = new CommonTodoInDto();
		csub40uig00.setSysCdTodoCf(cdTodoInfoType);

		// test
		csub40uig00.setSysCdTodoCf(cdTask);
		// test
		if (dtToDoDue != null)
			csub40uig00.setDtSysDtTodoCfDueFrom(DateUtils.toCastorDate(dtToDoDue));
		csub40uig00.setSysIdTodoCfPersCrea(idUser);
		csub40uig00.setSysIdTodoCfStage(idStage);
		csub40uig00.setSysIdTodoCfPersAssgn(idPrsnAssgn);
		csub40uig00.setSysIdTodoCfPersWkr(idUser);

		if (toDoDesc != null)
			csub40uig00.setSysTxtTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			csub40uig00.setSysTxtTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			csub40uig00.setSysIdTodoCfEvent(idEvent);
		}

		commonTodoService.TodoCommonFunction(csub40uig00);
	}

	private static String getToDoTask(String cdTask) {
		if (ServiceConstants.CD_TASK_SUB_PLCMNT_REQ.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_SUB_PLCMNT_REQ;
		if (ServiceConstants.CD_TASK_ADO_PLCMNT_REQ.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_ADO_PLCMNT_REQ;
		if (ServiceConstants.CD_TASK_SUB_PLCMNT_STTS.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_SUB_PLCMNT_STTS;
		if (ServiceConstants.CD_TASK_ADO_PLCMNT_STTS.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_ADO_PLCMNT_STTS;
		else
			return null;
	}

	/**
	 * MethodName:createIcpcToDo MethodDescription:This method will create ICPC
	 * TODO
	 *
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param dtToDoDue
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 *
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public void createIcpcToDo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Date dtToDoDue,
			Long idPrsnAssgn, Long idUser, Long idStage, Long idEvent) {
		// TODO Auto-generated method stub
		CommonTodoInDto commonTodoInDto = new CommonTodoInDto();
		commonTodoInDto.setSysCdTodoCf(cdTodoInfoType);

		if (dtToDoDue != null)
			commonTodoInDto.setDtSysDtTodoCfDueFrom(DateUtils.toCastorDate(dtToDoDue));
		commonTodoInDto.setSysIdTodoCfPersCrea(idUser);
		commonTodoInDto.setSysIdTodoCfStage(idStage);
		commonTodoInDto.setSysIdTodoCfPersAssgn(idPrsnAssgn);
		commonTodoInDto.setSysIdTodoCfPersWkr(idUser);

		if (toDoDesc != null)
			commonTodoInDto.setSysTxtTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			commonTodoInDto.setSysTxtTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			commonTodoInDto.setSysIdTodoCfEvent(idEvent);
		}

		commonTodoService.TodoCommonFunction(commonTodoInDto);

	}

	/**
	 * Method Name: alertPlacementReferral Method Description: This method is
	 * called in save method of placement detail an legal status page to create
	 * alert for the primary assigned caseworker to complete the 2077 Referral
	 * within 7 days of when a child Placement is entered and saved.
	 *
	 * @param savePlacementDtlReq
	 * @param checkFlag
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void alertPlacementReferral(AlertPlacementLsDto alertPlacementLsDto) {
		String cdCntyRegion = null;
		String cdCounty = null;
		if (!ObjectUtils.isEmpty(alertPlacementLsDto) && !ObjectUtils.isEmpty(alertPlacementLsDto.getCdCounty())) {
			cdCounty = alertPlacementLsDto.getCdCounty();
		}
		if (!ObjectUtils.isEmpty(cdCounty)) {
			cdCntyRegion = placementDao.getCountyRegion(cdCounty);
		}
		if (!ObjectUtils.isEmpty(cdCntyRegion)) {
			alertPlacementLsDto.setCdCntyRegion(cdCntyRegion);
		}
		placementDao.alertPlacementReferral(alertPlacementLsDto);
	}

	/**
	 * Method Name: getLatestPlcmntEvent Method Description: This method is to
	 * retrieve the latest placement event.
	 *
	 * @param eventDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EventDto getLatestPlcmntEvent(EventDto eventDto) {
		return placementDao.getLatestPlcmntEvent(eventDto.getIdEvent(), eventDto.getIdStage(),
				eventDto.getCdEventType());
	}

	/**
	 * Method Description - Processes the Placement Entity and creates the
	 * PlacementDto object to be returned to the caller
	 *
	 * @param placement
	 * @return PlacementDto
	 */
	@Override
	public PlacementDto processPlacement(Placement placement) {
		PlacementDto placementDto = new PlacementDto();
		if (!ObjectUtils.isEmpty(placement)) {
			if (!ObjectUtils.isEmpty(placement.getIdPlcmtEvent()) && placement.getIdPlcmtEvent() > 0) {
				placementDto.setIdPlcmtEvent(Long.valueOf(placement.getIdPlcmtEvent()));
			}
			placementDto.setDtPlcmtStart(placement.getDtPlcmtStart());
			placementDto.setDtPlcmtEnd(placement.getDtPlcmtEnd());
			placementDto.setCaseId(placement.getIdCase());
			if (!ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcAgency())
					&& !ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcAgency().getIdResource())) {
				placementDto.setIdRsrcAgency(placement.getCapsResourceByIdRsrcAgency().getIdResource());
			}
			if (!ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcFacil())
					&& !ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcFacil().getIdResource())) {
				placementDto.setIdRsrcFacil(placement.getCapsResourceByIdRsrcFacil().getIdResource());
			}

			placementDto.setCdPlcmtLivArr(placement.getCdPlcmtLivArr());
			placementDto.setExistingCdLivArr(placement.getCdPlcmtLivArr());
			placementDto.setCdPlcmtRemovalRsn(placement.getCdPlcmtRemovalRsn());
			placementDto.setCdPlcmtActPlanned(placement.getCdPlcmtActPlanned());
			placementDto.setCdPlcmtType(placement.getCdPlcmtType());
			placementDto.setExistingCdPlcmtType(placement.getCdPlcmtType());
			placementDto.setCdPlcmtService(placement.getCdPlcmtService());
			if (((TYPE_NON_PAID).equalsIgnoreCase(placement.getCdPlcmtType()))) {
				if (!ObjectUtils.isEmpty(placement.getNmPlcmtAgency())) {
					placementDto.setNmPlcmtAgencyPaid(placement.getNmPlcmtAgency());
				}
				if (!ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcAgency())
						&& !ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcAgency().getIdResource())) {
					placementDto.setIdRsrcAgencyPaid(placement.getCapsResourceByIdRsrcAgency().getIdResource());
				}
				if (!ObjectUtils.isEmpty(placement.getNmPlcmtFacil())) {
					placementDto.setNmPlcmtFacilPaid(placement.getNmPlcmtFacil());
				}
				if (!ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcFacil())
						&& !ObjectUtils.isEmpty(placement.getCapsResourceByIdRsrcFacil().getIdResource())) {
					placementDto.setIdRsrcFacilPaid(placement.getCapsResourceByIdRsrcFacil().getIdResource());
				}
			}
			placementDto.setCdPlcmtInfo1(placement.getCdPlcmtInfo1());
			placementDto.setCdPlcmtInfo2(placement.getCdPlcmtInfo2());
			placementDto.setCdPlcmtInfo3(placement.getCdPlcmtInfo3());
			placementDto.setCdPlcmtInfo4(placement.getCdPlcmtInfo4());
			placementDto.setCdPlcmtInfo5(placement.getCdPlcmtInfo5());
			placementDto.setCdPlcmtInfo6(placement.getCdPlcmtInfo6());
			placementDto.setCdPlcmtInfo7(placement.getCdPlcmtInfo7());
			placementDto.setCdPlcmtInfo8(placement.getCdPlcmtInfo8());
			placementDto.setCdPlcmtInfo9(placement.getCdPlcmtInfo9());
			placementDto.setCdPlcmtInfo10(placement.getCdPlcmtInfo10());
			placementDto.setCdPlcmtInfo11(placement.getCdPlcmtInfo11());
			placementDto.setCdPlcmtInfo12(placement.getCdPlcmtInfo12());
			placementDto.setCdPlcmtInfo13(placement.getCdPlcmtInfo13());
			placementDto.setCdPlcmtInfo14(placement.getCdPlcmtInfo14());
			placementDto.setCdPlcmtInfo15(placement.getCdPlcmtInfo15());
			placementDto.setCdPlcmtInfo16(placement.getCdPlcmtInfo16());
			placementDto.setCdPlcmtInfo17(placement.getCdPlcmtInfo17());
			placementDto.setCdPlcmtInfo18(placement.getCdPlcmtInfo18());
			placementDto.setCdPlcmtInfo19(placement.getCdPlcmtInfo19());
			placementDto.setCdPlcmtInfo20(placement.getCdPlcmtInfo20());
			if (placement.getPersonByIdPlcmtAdult() != null) {
				placementDto.setIdPlcmtAdult(placement.getPersonByIdPlcmtAdult().getIdPerson());
			}
			if (placement.getPersonByIdPlcmtChild() != null) {
				placementDto.setIdPlcmtChild(placement.getPersonByIdPlcmtChild().getIdPerson());
			}
			if (placement.getContract() != null) {
				placementDto.setIdContract(placement.getContract().getIdContract());
			}

			placementDto.setAddrPlcmtCity(placement.getAddrPlcmtCity());
			placementDto.setAddrPlcmtCnty(placement.getAddrPlcmtCnty());
			placementDto.setAddrPlcmtLn1(placement.getAddrPlcmtLn1());
			placementDto.setAddrPlcmtLn2(placement.getAddrPlcmtLn2());
			placementDto.setAddrPlcmtSt(placement.getAddrPlcmtSt());
			placementDto.setAddrPlcmtZip(placement.getAddrPlcmtZip());

			placementDto.setDtPlcmtCaregvrDiscuss(placement.getDtPlcmtCaregvrDiscuss());
			placementDto.setDtPlcmtEducLog(placement.getDtPlcmtEducLog());
			placementDto.setDtPlcmtMeddevHistory(placement.getDtPlcmtMeddevHistory());
			placementDto.setDtSxVctmztnHistoryDiscuss(placement.getDtSxVctmztnHistoryDiscuss());
			placementDto.setDtPlcmtParentsNotif(placement.getDtPlcmtParentsNotif());
			placementDto.setDtPlcmtLastPrebill(placement.getDtPlcmtLastPrebill());
			placementDto.setDtPlcmtPreplaceVisit(placement.getDtPlcmtPreplaceVisit());
			placementDto.setDtPlcmtSchoolRecords(placement.getDtPlcmtSchoolRecords());
			placementDto.setDtPlcmtChildDiscuss(placement.getDtPlcmtChildDiscuss());
			placementDto.setDtPlcmtChildPlan(placement.getDtPlcmtChildPlan());
			placementDto.setDtPlcmtStart(placement.getDtPlcmtStart());
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtContCntct())) {
				placementDto.setIndPlcmtContCntct(Boolean.TRUE);
			} else {
				placementDto.setIndPlcmtContCntct(Boolean.FALSE);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtEducLog())) {
				placementDto.setIndPlcmtEducLog(Boolean.TRUE);
			} else {
				placementDto.setIndPlcmtEducLog(Boolean.FALSE);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtEmerg())) {
				placementDto.setIndPlcmtEmerg(ServiceConstants.Y);
			} else {
				placementDto.setIndPlcmtEmerg(ServiceConstants.N);
			}

			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndT3CPlcmt())) {
				placementDto.setIndT3CPlcmt(ServiceConstants.Y);
			} else {
				placementDto.setIndT3CPlcmt(ServiceConstants.N);
			}

			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtNotApplic())) {
				placementDto.setIndPlcmtNotApplic(ServiceConstants.Y);
			} else {
				placementDto.setIndPlcmtNotApplic(ServiceConstants.N);
			}

			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtSchoolDoc())) {
				placementDto.setIndPlcmtSchoolDoc(Boolean.TRUE);
			} else {
				placementDto.setIndPlcmtSchoolDoc(Boolean.FALSE);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndPlcmtWriteHistory())) {
				placementDto.setIndPlcmtWriteHistory(Boolean.TRUE);
			} else {
				placementDto.setIndPlcmtWriteHistory(Boolean.FALSE);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndCongregateCare())) {
				placementDto.setIndCongregateCare(ServiceConstants.Y);
			} else {
				placementDto.setIndCongregateCare(ServiceConstants.N);
			}

			placementDto.setNbrPlcmtPhoneExt(placement.getNbrPlcmtPhoneExt());
			placementDto.setNbrPlcmtTelephone(placement.getNbrPlcmtTelephone());
			placementDto.setNmPlcmtAgency(placement.getNmPlcmtAgency());
			placementDto.setNmPlcmtContact(placement.getNmPlcmtContact());
			placementDto.setNmPlcmtFacil(placement.getNmPlcmtFacil());
			placementDto.setNmPlcmtPersonFull(placement.getNmPlcmtPersonFull());
			placementDto.setTxtPlcmtAddrComment(placement.getTxtPlcmtAddrComment());
			placementDto.setTxtPlcmtDiscussion(placement.getTxtPlcmtDiscussion());
			placementDto.setTxtPlcmtDocuments(placement.getTxtPlcmtDocuments());
			placementDto.setTxtPlcmtRemovalRsn(placement.getTxtPlcmtRemovalRsn());
			placementDto.setDtPlcmtPermEff(placement.getDtPlcmtPermEff());
			placementDto.setIndTrashBags(placement.getIndTrashBags());
			placementDto.setTxtTrashBags(placement.getTxtTrashBags());
			placementDto.setCdRmvlRsnSubtype(placement.getCdRmvlRsnSubtype());
			placementDto.setIdLastUpdatePerson(placement.getIdLastUpdatePerson());
			placementDto.setIdRsrcSSCC(placement.getIdRsrcSscc());
			placementDto.setNmPlcmtSscc(placement.getNmPlcmtSscc());
			placementDto.setDtLastUpdate(placement.getDtLastUpdate());
			placementDto.setIndPlcmtStartEndDtDiff(placement.getIndPlcmtLessThan24Hrs());
			// artf176932 - Add NA checkbox for Sexual History Attachment A
			if (ServiceConstants.Y.equalsIgnoreCase(placement.getIndSxVctmztnHistoryDiscuss())) {
				placementDto.setIndSxVctmztnHistoryDiscuss(Boolean.TRUE);
			} else {
				placementDto.setIndSxVctmztnHistoryDiscuss(Boolean.FALSE);
			}
			placementDto.setDtCreated(placement.getDtCreated());

		}
		return placementDto;
	}

	/**
	 * Method Name: chckPlcmntEndedOrNot. Method Description: This Method is
	 * used to check whether placement is Ended or Not.
	 *
	 * @param idPlcmntEvent
	 * @return Boolean
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes chckPlcmntEndedOrNot(Long idPlcmntEvent) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Placement placement = placementDao.selectPlacement(idPlcmntEvent);
		// PlacementDto placementDto = new PlacementDto();
		if (!ObjectUtils.isEmpty(placement) && !ObjectUtils.isEmpty(placement.getDtPlcmtEnd())
				&& !ServiceConstants.GENERIC_END_DATE.equals(placement.getDtPlcmtEnd())) {
			commonHelperRes.setPlcmntEnded(Boolean.TRUE);
		} else {
			commonHelperRes.setPlcmntEnded(Boolean.FALSE);
		}
		return commonHelperRes;
	}
	/**
	 * Method Name: getActiveTepContract
	 * Method Description:This method returns
	 * an active TEP
	 *
	 * @param idResource
	 * @return PlacementValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  PlacementValueDto getActiveTepContract(PlacementReq placementReq) {
		return placementDao.getActiveTepContract(placementReq);
	}


	/**
	 * Method Name: getCountOfActiveTfcPlmnts
	 * Method Description:This method returns
	 * an active TEP
	 *
	 * @param idResource
	 * @return PlacementValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  CommonCountRes getCountOfActiveTfcPlmnts(PlacementReq placementReq) {

		if (ObjectUtils.isEmpty(placementReq.getIdPerson())
				|| placementReq.getIdPerson() == ServiceConstants.ZERO_VAL) {
			ToDoStagePersonDto toDoStagePersonDto = new ToDoStagePersonDto();
			if (!ObjectUtils.isEmpty(placementReq)) {
				toDoStagePersonDto.setIdStage(placementReq.getIdStage());
				// CdStagePersRole is set to "primary child"
				toDoStagePersonDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
			}
			// 1. Call CINV51D
			List<PersonAssignedIdToDoDto> personDtoList = personLOCPersonDao
					.retrievePersonByRoleAndStage(toDoStagePersonDto);
			if (!CollectionUtils.isEmpty(personDtoList)) {
				placementReq.setIdPerson(personDtoList.get(0).getIdTodoPersAssigned());
			}
		}
		return placementDao.getCountOfActiveTfcPlmnts(placementReq);

	}

	/**
	 * Method Name: getCountOfAllPlacements
	 * Method Description:This method returns
	 * an active TEP
	 *
	 * @param idResource
	 * @return PlacementValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  CommonCountRes getCountOfAllPlacements(PlacementReq placementReq) {

		if (ObjectUtils.isEmpty(placementReq.getIdPerson())
				|| placementReq.getIdPerson() == ServiceConstants.ZERO_VAL) {
			ToDoStagePersonDto toDoStagePersonDto = new ToDoStagePersonDto();
			if (!ObjectUtils.isEmpty(placementReq)) {
				toDoStagePersonDto.setIdStage(placementReq.getIdStage());
				// CdStagePersRole is set to "primary child"
				toDoStagePersonDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
			}
			// 1. Call CINV51D
			List<PersonAssignedIdToDoDto> personDtoList = personLOCPersonDao
					.retrievePersonByRoleAndStage(toDoStagePersonDto);
			if (!CollectionUtils.isEmpty(personDtoList)) {
				placementReq.setIdPerson(personDtoList.get(0).getIdTodoPersAssigned());
			}
		}
		return placementDao.getCountOfAllPlacements(placementReq);

	}

	public List<PlacementValueDto> getPlacementHistory(Long idPerson) {
		List<PlacementValueDto> retVal = new ArrayList<>();
		List<PlacementDto> daoResponseList = placementDao.getPlacementDetailsByChildId(idPerson);

		if (daoResponseList != null && daoResponseList.size() > 0) {
			daoResponseList.stream().forEach(currDaoResponse -> {
				PlacementValueDto responseDto = new PlacementValueDto();
				BeanUtils.copyProperties(currDaoResponse, responseDto);
				responseDto.setIdPerson(currDaoResponse.getIdPlcmtChild());
				responseDto.setPlcmtType(currDaoResponse.getCdPlcmtType());
				responseDto.setTemporaryAbsenceDtoList(placementDao.getTemporaryAbsenceList(currDaoResponse.getIdPlcmtEvent()));
				Date compareDate = DateUtils.getJavaDate(4712, 12, 31);
				if (compareDate.equals(currDaoResponse.getDtPlcmtEnd())) {
					responseDto.setDtPlcmtEnd(null);
				}
				retVal.add(responseDto);
			});
		}
		return retVal;
	}
	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	/**
	 * @param savePlacementDtlReq
	 */
	private void saveOrUpdateChildBillOfRightsHistory (SavePlacementDetailReq savePlacementDtlReq) {
		boolean isPlacementApproved = false;
		if((ServiceConstants.CEVTSTAT_APRV).equals(savePlacementDtlReq.getCdEventStatus().get(0))){
			isPlacementApproved = true;
		}
		ChildBillOfRightsDto childBillOfRightsDto = new ChildBillOfRightsDto();
		if(!ObjectUtils.isEmpty(savePlacementDtlReq)){
			childBillOfRightsDto.setIdPlcmtEvent(savePlacementDtlReq.getPostEventIPDto().getIdEvent());
			childBillOfRightsDto.setIdCreatedPerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			childBillOfRightsDto.setIdLastUpdatePerson(savePlacementDtlReq.getPostEventIPDto().getIdPerson());
			if(!ObjectUtils.isEmpty(savePlacementDtlReq.getDtBillOfRightsInit())){
				childBillOfRightsDto.setDtBillOfRights(savePlacementDtlReq.getDtBillOfRightsInit());
				childBillOfRightsDto.setCdBillOfRightsType(ServiceConstants.CHILD_BILL_OF_RIGHTS_TYPE_INITIAL);
				placementDao.saveIntialBillOfRights(childBillOfRightsDto);
			}if(!ObjectUtils.isEmpty(savePlacementDtlReq.getDtBillOfRightsReview())){
				childBillOfRightsDto.setDtBillOfRights(savePlacementDtlReq.getDtBillOfRightsReview());
				childBillOfRightsDto.setCdBillOfRightsType(ServiceConstants.CHILD_BILL_OF_RIGHTS_TYPE_REVIEW);
				placementDao.saveToChildBillOfRightsHistory(childBillOfRightsDto, isPlacementApproved, savePlacementDtlReq.getIndNewActualPlcmt());
			}
		}
	}



	/**
	 * @param stageId
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementValueDto>  getChildPlcmtReferrals(Long stageId) {
		return placementDao.getChildPlcmtReferrals(stageId);
	}

	public boolean chkValidFPSContractRsrc(Long idResource, Long idRsrcSscc){
		return placementDao.chkValidFPSContractRsrc(idResource, idRsrcSscc);
	}

	@Override
	public List<PlacementValueDto>  getChildPlacement(Long idPerson, Long stageId) {
		return placementDao.getChildPlacement(idPerson, stageId);
	}

	@Override
	public Boolean getAddOnSvcPkg(Long idPerson, Long idStage){
		return placementDao.getAddOnSvcPkg(idPerson, idStage);
	}

	@Override
	public HmEligibilityRes checkPlacementHmEligibility(HmEligibilityReq placementReq) {
		HmEligibilityRes hmEligibilityRes = new HmEligibilityRes();
		PlacementDto placementDto = placementDao.getLatestPlacement(placementReq.getIdPerson(), placementReq.getIdCase());
		if(placementDto.getIdRsrcFacil()!=null) {
			if (!ObjectUtils.isEmpty(placementDto) &&
					!Y.equalsIgnoreCase(placementDto.getIndT3CPlcmt())
					&& placementDto.getIdRsrcFacil().longValue() == placementReq.getIdResource()
					.longValue()) {
				List<EventListDto> hmEvents = heightenedMonitoringDao.getHmmRequestEventList(
						placementReq.getIdStage());
				if (!hmEvents.isEmpty()) {
					EventListDto existingRequest = hmEvents.get(0);
					HMRequest hmRequest = heightenedMonitoringDao.getHmRequestByEventId(
							existingRequest.getIdEvent());
					if(hmRequest.getCapsResourceByIdRsrcFacil()!=null) {
						if (!hmRequest.getIdCreatedPerson().equals(Long.valueOf("999999983"))
								&& hmRequest.getCapsResourceByIdRsrcFacil().getIdResource()
								.equals(placementReq.getIdResource())) {
							hmEligibilityRes.setCloneHM(!ObjectUtils.isEmpty(hmEvents.get(0))
									&& heightenedMonitoringDao.checkHMStatusActive(placementReq.getIdResource()));
						}
					}
				}
			}
		}
			return hmEligibilityRes;
	}

	private Event createEvent(PlacementAUDDto placementAUDDto, Stage stage, SavePlacementDetailReq savePlacementDetailReq,
							  String eventType, String taskCode) {
		Event event= new Event();
		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
		event.setIdCase(placementAUDDto.getIdCase());
		event.setStage(stage);
		event.setCdEventType(eventType);
		event.setPerson(personDao.getPerson(999999983L));
		event.setCdTask(taskCode);
		event.setTxtEventDescr(getEventDescription(eventType, taskCode, stage.getNmStage(), placementAUDDto));
		event.setDtEventOccurred(placementAUDDto.getDtPlcmtStart());
		event.setCdEventStatus(APPROVE_EVENT_STATUS);
		event.setDtEventCreated(systemTime);
		event.setDtLastUpdate(systemTime);
		eventDao.updateEvent(event, REQ_FUNC_CD_ADD);
		return event;
	}

	private HMRequest createHMRequest(HMRequest existingHMRequest, Event hmRequestEvent, PlacementAUDDto placementAUDDto) {
		HMRequest request = new HMRequest();
		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
		request.setCapsResourceByIdRsrcAgency(existingHMRequest.getCapsResourceByIdRsrcAgency());
		request.setDtExpire(DateUtils.addToDate(new Date(),0,0,13));
		request.setCapsResourceByIdRsrcFacil(existingHMRequest.getCapsResourceByIdRsrcFacil());
		request.setIdEvent(hmRequestEvent);
		request.setDtStart(placementAUDDto.getDtPlcmtStart());
		request.setTxtBestInterestDescr(existingHMRequest.getTxtBestInterestDescr());
		request.setDtCreated(systemTime);
		request.setDtLastUpdated(systemTime);
		request.setIdCreatedPerson(999999983L);
		request.setIdLastUpdatedPerson(999999983L);
		request.setIndHmPlcmntEmrgncy(N);
		request.setIndCourtOrdered(N);
		request.setHmReqChildLinks(getChildLink(request, existingHMRequest, placementAUDDto, hmRequestEvent));
		heightenedMonitoringDao.saveOrUpdateHMRequest(request);
		return request;
	}

	private Set<HMReqChildLink> getChildLink(HMRequest hmRequest,HMRequest existingHMRequest, PlacementAUDDto placementAUDDto, Event hmRequestEvent) {

		Set<HMReqChildLink> childLinks = new HashSet<>();
		HMReqChildLink hmReqChildLink = new HMReqChildLink();
		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());

		hmReqChildLink.setHmRequest(hmRequest);
		hmReqChildLink.setPerson(personDao.getPerson(placementAUDDto.getIdPlcmtChild()));
		hmReqChildLink.setStage(hmRequestEvent.getStage());
		hmReqChildLink.setDtCreated(systemTime);
		hmReqChildLink.setIdCreatedPerson(999999983L);
		hmReqChildLink.setDtLastUpdated(systemTime);
		hmReqChildLink.setIdLastUpdatedPerson(999999983L);

		Optional<HMReqChildLink> existingChildLink = getExistingChildLink(existingHMRequest, hmRequestEvent, placementAUDDto.getIdPlcmtChild());
		if(existingChildLink.isPresent()){
			hmReqChildLink.setCdChildLegalRegion(existingChildLink.get().getCdChildLegalRegion());
			hmReqChildLink.setIndSexualAggrsnHist(existingChildLink.get().getIndSexualAggrsnHist());
			hmReqChildLink.setIndSexualVictmsnHist(existingChildLink.get().getIndSexualVictmsnHist());
			if(existingChildLink.get().getPlacement()!=null) {
				hmReqChildLink.setPlacement(placementDao.retrievePlacementByEventId(placementAUDDto.getIdPlcmtEvent()));
			}
		}
		childLinks.add(hmReqChildLink);
		return childLinks;
	}

	private Optional<HMReqChildLink> getExistingChildLink(HMRequest existingHMRequest, Event hmRequestEvent, Long idPlcmntChild){
		return existingHMRequest.getHmReqChildLinks().stream().
				filter(childLink -> childLink.getStage().getIdStage().longValue() == hmRequestEvent.getStage().getIdStage() &&
						childLink.getPerson().getIdPerson().longValue() == idPlcmntChild).findFirst();
	}

	private void createHMNarrative(HMRequest existingHMRequest, Event hmRequestEvent){
		HmReqNarr existingNarrative = heightenedMonitoringDao.findHmReqNarrByIdEvent(existingHMRequest.getIdEvent().getIdEvent());
		if(!ObjectUtils.isEmpty(existingNarrative)){
			HmReqNarr hmReqNarr = new HmReqNarr();
			Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
			hmReqNarr.setEvent(hmRequestEvent);
			hmReqNarr.setTxtNarrative(existingNarrative.getTxtNarrative());
			hmReqNarr.setIdDocumentTemplate(existingNarrative.getIdDocumentTemplate());
			hmReqNarr.setDtCreated(systemTime);
			hmReqNarr.setCreatedPerson(hmRequestEvent.getPerson());
			hmReqNarr.setDtLastUpdated(systemTime);
			hmReqNarr.setLastUpdatedPerson(hmRequestEvent.getPerson());
			heightenedMonitoringDao.saveOrUpdateHMReqNarrative(hmReqNarr);
		}
	}


	private void createApprovalEventLink(Event approvalEvent, Long hmEventId) {
		ApprovalEventLink approvalEventLink = new ApprovalEventLink();
		approvalEventLink.setIdApproval(approvalEvent.getIdEvent());
		approvalEventLink.setIdCase(approvalEvent.getIdCase());
		approvalEventLink.setIdEvent(hmEventId);
		approvalEventLink.setDtLastUpdate(new Date());
		approvalEventLinkDao.saveorUpdate(approvalEventLink);

	}

	private void createApproval(Event event) {
		Approval approval = new Approval();
		approval.setIdApproval(event.getIdEvent());
		approval.setDtLastUpdate(new Date());
		approval.setPerson(event.getPerson());
		approval.setTxtApprovalTopic(event.getTxtEventDescr());
		approvalDao.saveorUpdate(approval);
	}

	private Todo createTodo(Event event, CapsCase capsCase) {
		Todo todo = new Todo();
		todo.setDtLastUpdate(new Date());
		todo.setPersonByIdTodoPersAssigned(event.getPerson());
		todo.setCapsCase(capsCase);
		todo.setEvent(event);
		todo.setStage(event.getStage());
		todo.setPersonByIdTodoPersWorker(event.getPerson());
		todo.setDtTodoDue(new Date());
		todo.setCdTodoTask(HMM_SERV_REF_TASK_APRV);
		todo.setTxtTodoDesc(event.getTxtEventDescr());
		todo.setCdTodoType(TASK_TODO);
		todo.setTxtTodoLongDesc(HMM_SERV_REF_TASK_APRV.equals(event.getCdEventType()) ? HM_TODO_DESC + event.getTxtEventDescr() : "");
		todo.setDtTodoCreated(new Date());
		todo.setDtTodoTaskDue(new Date());
		todo.setDtTodoCompleted(new Date());
		todoDao.saveorUpdate(todo);
		return todo;
	}

	public void createApprover(Event event, Todo todo){
		Approvers approver = new Approvers();
		approver.setDtLastUpdate(new Date());
		approver.setPerson(event.getPerson());
		approver.setIdApproval(event.getIdEvent());
		approver.setIdTodo(todo.getIdTodo());
		approver.setCdApproversStatus(APPROVED);
		approver.setDtApproversDetermination(new Date());
		approver.setDtApproversRequested(new Date());
		approver.setTxtApproversCmnts(HM_T3C_APPRVR_DESC);
		approver.setDtDeterminationRecorded(new Date());
		approversDao.updtApprovers(approver, ServiceConstants.REQ_FUNC_CD_ADD);
	}

	private void updatePlcmntEventStatus(Optional<HMReqChildLink> childLink, Long idPlcmntEvnt) {
		EventDto eventDto = new EventDto();
		eventDto.setIdEvent(idPlcmntEvnt);
		eventDto.setCdEventStatus(childLink.isPresent() && !ObjectUtils.isEmpty(childLink.get().getPlacement()) ? APPROVE_EVENT_STATUS : COMPLETE_EVENT_STATUS);
		eventDao.updateEventForPlacement(eventDto, REQ_FUNC_CD_UPDATE);
	}

	private String getEventDescription(String eventType, String taskCode, String stageName, PlacementAUDDto placementAUDDto){
		if(CEVNTTYP_HMM.equalsIgnoreCase(eventType)){
			return HM_EVENT_DESC + DateUtils.stringDt(placementAUDDto.getDtPlcmtStart()) + " " + stageName + " " + placementAUDDto.getPlcmtFacil();
		} else if(CEVNTTYP_APP.equalsIgnoreCase(eventType)){
			return HMM_SERV_REF_TASK_APRV.equalsIgnoreCase(taskCode) ? HM_EVENT_RSRC_DESC + placementAUDDto.getIdRsrcFacil() + "')'"
					: HM_T3C_PLCMT_TODO_DESC + stageName;
		}
		return "";
	}

	@Override
	public List<PlacementValueDto>  getParentPlacement(Long idPerson, Long stageId) {
		return placementDao.getParentPlacement(idPerson, stageId);
	}

	@Override
	public CommonCountRes  getCountCPBPlcmntsForYouthParent(Long idPerson, Long stageId,Date placementStartDate) {
		return placementDao.getCountCPBPlcmntsForYouthParent(idPerson, stageId,placementStartDate);
	}

	@Override
	public 	Boolean checkAlocBlocForNonT3cPlcmt(Long idCase, Date dtPlcmtStart){
		return placementDao.checkAlocBlocForNonT3cPlcmt(idCase, dtPlcmtStart);
	}

}
