package us.tx.state.dfps.service.icpc.serviceimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.IcpcDocUploadReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.IcpcDocUploadRes;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.icpc.dao.ICPCPlacementDao;
import us.tx.state.dfps.service.icpc.dao.IcpcDocumentUploadDao;
import us.tx.state.dfps.service.icpc.service.IcpcDocumentUploadService;
import us.tx.state.dfps.service.icpcdocument.dto.IcpcDocumentDto;
import us.tx.state.dfps.service.icpcdocument.dto.IcpcFileStorageDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;

@Service
@Transactional
public class IcpcDocumentUploadServiceImpl implements IcpcDocumentUploadService {

	@Autowired
	private IcpcDocumentUploadDao icpcDocumentUploadDao;
	
	@Autowired
	private PersonListService personListService;
	
	@Autowired
	private DayCareRequestDao dayCareRequestDao;
	
	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private ApprovalDao approvalDao;

	@Autowired
	private TodoDao todoDao;

	@Autowired
	private ICPCPlacementDao icpcPlacementDao;
	
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IcpcDocUploadRes fetchDocument(IcpcDocUploadReq icpcDocUploadReq) {

		IcpcDocUploadRes icpcDocUploadRes = new IcpcDocUploadRes();
		IcpcDocumentDto icpcDocDto = icpcDocumentUploadDao
				.fetchDocument(icpcDocUploadReq.getIcpcDocumentDto().getIdIcpcDocument());
		IcpcFileStorageDto icpcFileDto = icpcDocumentUploadDao.fetchFileStorage(icpcDocDto.getIdIcpcDocument());

		icpcDocUploadRes.setIcpcDocumentDto(icpcDocDto);
		icpcDocUploadRes.setIcpcFileStorageDto(icpcFileDto);

		return icpcDocUploadRes;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IcpcDocUploadRes saveDocument(IcpcDocUploadReq icpcDocUploadReq) {
		IcpcDocUploadRes icpcDocUploadRes = new IcpcDocUploadRes();
		IcpcDocumentDto icpcDocumentDto = new IcpcDocumentDto();
		Boolean indInsert = true;
		Map<String, String> documentTaskMap = new HashMap<>();
		documentTaskMap.put(CodesConstant.ICPCDCTP_155, "Complete Home Study has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_160, "Concurrence report has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_205, "Safe and Timely Report has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_135, "Case Closure Document has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_175, "Foster Care License has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_195, "Preliminary Home Evaluation has been added for ");
		documentTaskMap.put(CodesConstant.ICPCDCTP_215, "Report Home Study Status has been added for ");

		if ("A".equalsIgnoreCase(icpcDocUploadReq.getReqFuncCd())) {
			if (null != icpcDocUploadReq.getIcpcDocumentDto().getIdIcpcDocument()) {
				if (0 < icpcDocUploadReq.getIcpcDocumentDto().getIdIcpcDocument()) {
					indInsert = false;
				}
			}
			icpcDocumentDto = icpcDocumentUploadDao.saveDocument(icpcDocUploadReq.getIcpcDocumentDto(),indInsert);
			icpcDocUploadRes.setIcpcDocumentDto(icpcDocumentDto);
			Long idDocument = icpcDocumentDto.getIdIcpcDocument();
			icpcDocUploadReq.getIpFileStorageDto().setIdIcpcDocument(idDocument);
			if (indInsert) {
				icpcDocUploadRes.setIcpcFileStorageDto(
						icpcDocumentUploadDao.saveFileStorage(icpcDocUploadReq.getIpFileStorageDto()));
			}
				String toDoDesc = null;
				String docType = icpcDocUploadReq.getIcpcDocumentDto().getCdType();
				if (CodesConstant.ICPCDCTP_42.equalsIgnoreCase(docType)) {
					toDoDesc = ServiceConstants.HOME_STUDY;
				} else if (CodesConstant.ICPCDCTP_52.equalsIgnoreCase(docType)) {
					toDoDesc = ServiceConstants.INITIAL_ASSESSMENT_AVAILABLE;
				} else if (CodesConstant.ICPCDCTP_87.equalsIgnoreCase(docType)) {
					toDoDesc = ServiceConstants.SUPERVISION_REPORT_AVAILABLE;
				}
				Long[] approvers = new Long[0];
				approvers = dayCareRequestDao.getDayCareApprovers(icpcDocUploadReq.getIdEvent());
				CommonHelperRes commonHelperRes = personListService
						.getPrimaryCaseworkerForStage(icpcDocUploadReq.getIdStage());
				Long idPersonPrimary = commonHelperRes.getUlIdPerson();
				Date dueDate = new Date();
				SelectStageDto selectStageDto = caseSummaryDao.getStage(icpcDocUploadReq.getIdStage(),
						ServiceConstants.STAGE_CURRENT);
				String cdStageType = selectStageDto.getCdStageType();
				HashMap<Long, Long> regMap = new HashMap<Long, Long>();
				HashMap<Long, Long> cicMap = new HashMap<Long, Long>();
				if ((CodesConstant.ICPCDCTP_42.equals(docType) || CodesConstant.ICPCDCTP_52.equals(docType))
						&& CodesConstant.CSTGTYPE_REG.equals(cdStageType)) {
					TodoDto todoDtoTask = new TodoDto();
					todoDtoTask.setIdTodoEvent(icpcDocUploadReq.getIdEvent());
					todoDtoTask.setDtTodoDue(dueDate);
					todoDtoTask.setIdTodoStage(icpcDocUploadReq.getIdStage());
					todoDtoTask.setUserId(icpcDocUploadReq.getIdUser().toString());
					todoDtoTask.setIdTodoPersAssigned(idPersonPrimary);
					todoDtoTask.setIdTodoPersWorker(icpcDocUploadReq.getIdUser());
					todoDtoTask.setIdTodoCase(icpcDocUploadReq.getIdCase());
					todoDtoTask.setTodoDesc(toDoDesc);
					todoDtoTask.setTodoLongDesc(toDoDesc);
					// artf224200 : add the task code before saving the record into TODO table
					todoDtoTask.setCdTask(icpcDocUploadReq.getCdTask());
					icpcDocumentUploadDao.createTask(todoDtoTask);
					regMap.put(idPersonPrimary, idPersonPrimary);
					List<Long> workerList = workLoadDao.getAssignedWorkersForStage(icpcDocUploadReq.getIdStage());
					workerList.stream().forEach(idWorker -> {
						if (!ObjectUtils.isEmpty(idWorker) && !idWorker.equals(idPersonPrimary)) {
							TodoDto todoDto = new TodoDto();
							todoDto.setDtTodoDue(dueDate);
							todoDto.setIdTodoStage(icpcDocUploadReq.getIdStage());
							todoDto.setUserId(icpcDocUploadReq.getIdUser().toString());
							todoDto.setIdTodoPersAssigned(idWorker);
							icpcDocumentUploadDao.createAlert(todoDto);
							regMap.put(idWorker, idWorker);
						}
					});
					/**
					 * ICPC Regional Coordinator needs an Alert for Home Study &
					 * Initial Assessment - Person ID from approvers list
					 **/
					if (!ObjectUtils.isEmpty(approvers) && approvers.length > ServiceConstants.ZERO_VAL) {
						for (int i = 0; i < approvers.length; i++) {
							Long approver = approvers[i];
							if (!ObjectUtils.isEmpty(regMap.get(approver))) {
								TodoDto todoDto = new TodoDto();
								todoDto.setDtTodoDue(dueDate);
								todoDto.setIdTodoStage(icpcDocUploadReq.getIdStage());
								todoDto.setUserId(icpcDocUploadReq.getIdUser().toString());
								todoDto.setIdTodoPersAssigned(approver);
								icpcDocumentUploadDao.createAlert(todoDto);
							}
						}
					}

				}
				if (CodesConstant.ICPCDCTP_42.equals(docType) || CodesConstant.ICPCDCTP_52.equals(docType)
						|| CodesConstant.ICPCDCTP_87.equals(docType)) {

					/**
					 * To Do for A Supervision Report is uploaded - Primary
					 * CaseWorker assigned to Case
					 **/
					if (CodesConstant.ICPCDCTP_87.equals(docType)) {
						createTaskForDoc(icpcDocUploadReq, dueDate, idPersonPrimary, toDoDesc);
						cicMap.put(idPersonPrimary, idPersonPrimary);
					}

					List<Long> workerList = workLoadDao.getAssignedWorkersForStage(icpcDocUploadReq.getIdStage());
					workerList.stream().forEach(idWorker -> {
						/**
						 * To Do for An Initial Assessment, Home Study is
						 * uploaded for a C-IC Case for ICPC Program Specialist
						 * assigned to Case
						 **/
						if ( dayCareRequestDao.isIcpcProgramSpecialist(idWorker) && CodesConstant.CSTGTYPE_CIC.equals(cdStageType)
								&& ((CodesConstant.ICPCDCTP_42.equals(docType)
										|| CodesConstant.ICPCDCTP_52.equals(docType)))) {
							TodoDto todoDtoTask = new TodoDto();
							todoDtoTask.setIdTodoEvent(icpcDocUploadReq.getIdEvent());
							todoDtoTask.setDtTodoDue(dueDate);
							todoDtoTask.setIdTodoStage(icpcDocUploadReq.getIdStage());
							todoDtoTask.setUserId(icpcDocUploadReq.getIdUser().toString());
							todoDtoTask.setIdTodoPersAssigned(idWorker);
							todoDtoTask.setIdTodoPersWorker(icpcDocUploadReq.getIdUser());
							todoDtoTask.setIdTodoCase(icpcDocUploadReq.getIdCase());
							// artf224200 : add the task code before saving the record into TODO table
							todoDtoTask.setCdTask(icpcDocUploadReq.getCdTask());
							if (CodesConstant.ICPCDCTP_42.equalsIgnoreCase(docType)) {
								todoDtoTask.setTodoDesc(ServiceConstants.HOME_STUDY);
								todoDtoTask.setTodoLongDesc(ServiceConstants.HOME_STUDY);
							} else if (CodesConstant.ICPCDCTP_52.equalsIgnoreCase(docType)) {
								todoDtoTask.setTodoDesc(ServiceConstants.INITIAL_ASSESSMENT_AVAILABLE);
								todoDtoTask.setTodoLongDesc(ServiceConstants.INITIAL_ASSESSMENT_AVAILABLE);
							}
							icpcDocumentUploadDao.createTask(todoDtoTask);
							cicMap.put(idPersonPrimary, idPersonPrimary);
						}
						 // Supervision Report
						if (!ObjectUtils.isEmpty(idWorker) && !idWorker.equals(idPersonPrimary) && (CodesConstant.ICPCDCTP_87.equals(docType))) {
							TodoDto todoDto = new TodoDto();
							todoDto.setDtTodoDue(dueDate);
							todoDto.setIdTodoStage(icpcDocUploadReq.getIdStage());
							todoDto.setUserId(icpcDocUploadReq.getIdUser().toString());
							todoDto.setIdTodoPersAssigned(idWorker);
							icpcDocumentUploadDao.createAlert(todoDto);
							cicMap.put(idWorker, idWorker);
						}
					});
					/**
					 * ICPC Regional Coordinator needs an Alert for Supervision
					 * Report - Person ID from approvers list
					 **/
					if ((CodesConstant.ICPCDCTP_87.equals(docType)) && !ObjectUtils.isEmpty(approvers)
							&& approvers.length > ServiceConstants.ZERO_VAL) {
						for (int i = 0; i < approvers.length; i++) {
							Long approver = approvers[i];
							if (!ObjectUtils.isEmpty(cicMap.get(approver))) {
								TodoDto todoDto = new TodoDto();
								todoDto.setDtTodoDue(dueDate);
								todoDto.setIdTodoStage(icpcDocUploadReq.getIdStage());
								todoDto.setUserId(icpcDocUploadReq.getIdUser().toString());
								todoDto.setIdTodoPersAssigned(approver);
								icpcDocumentUploadDao.createAlert(todoDto);
							}

						}
					}

				}

				if (documentTaskMap.containsKey(icpcDocUploadReq.getIcpcDocumentDto()
						.getCdType())) {

					createTaskForDoc(icpcDocUploadReq, new Date(), idPersonPrimary,
							documentTaskMap.get(icpcDocUploadReq.getIcpcDocumentDto()
									.getCdType()) +
									selectStageDto.getNmStage());
				}
		}

		if ("D".equalsIgnoreCase(icpcDocUploadReq.getReqFuncCd())) {
			icpcDocumentUploadDao.deleteDocument(icpcDocUploadReq.getIcpcDocumentDto().getIdIcpcDocument());
		}
		return icpcDocUploadRes;
	}

	/**
	 * Method Description: This method is used to create task for the document uploaded
	 *
	 * @param icpcDocUploadReq
	 * @param dueDate
	 * @param idPersonPrimary
	 * @param toDoDesc
	 */
	private void createTaskForDoc(IcpcDocUploadReq icpcDocUploadReq, Date dueDate, Long idPersonPrimary,
								  String toDoDesc) {
		TodoDto todoDtoTask = new TodoDto();
		todoDtoTask.setIdTodoEvent(icpcDocUploadReq.getIdEvent());
		todoDtoTask.setDtTodoDue(dueDate);
		todoDtoTask.setIdTodoStage(icpcDocUploadReq.getIdStage());
		todoDtoTask.setUserId(icpcDocUploadReq.getIdUser()
				.toString());
		todoDtoTask.setIdTodoPersAssigned(idPersonPrimary);
		todoDtoTask.setIdTodoPersWorker(icpcDocUploadReq.getIdUser());
		todoDtoTask.setIdTodoCase(icpcDocUploadReq.getIdCase());
		todoDtoTask.setTodoDesc(toDoDesc);
		todoDtoTask.setTodoLongDesc(toDoDesc);
		todoDtoTask.setCdTask(icpcDocUploadReq.getCdTask());
		icpcDocumentUploadDao.createTask(todoDtoTask);
	}

	@Override
	public void updateTaskComplete(Long idEvent, String cdType, Long userId) {

		Map<String, String> documentTaskMap = new HashMap<>();
		documentTaskMap.put(CodesConstant.ICPCDCTP_42, "Home Study");
		documentTaskMap.put(CodesConstant.ICPCDCTP_52, "Initial Assessment");
		documentTaskMap.put(CodesConstant.ICPCDCTP_87, "Supervision Report");
		documentTaskMap.put(CodesConstant.ICPCDCTP_155, "Complete Home Study");
		documentTaskMap.put(CodesConstant.ICPCDCTP_160, "Concurrence report");
		documentTaskMap.put(CodesConstant.ICPCDCTP_205, "Safe and Timely Report");
		documentTaskMap.put(CodesConstant.ICPCDCTP_135, "Case Closure Document");
		documentTaskMap.put(CodesConstant.ICPCDCTP_175, "Foster Care License");
		documentTaskMap.put(CodesConstant.ICPCDCTP_195, "Preliminary Home Evaluation");
		documentTaskMap.put(CodesConstant.ICPCDCTP_215, "Report Home Study Status");

		icpcDocumentUploadDao.updateTaskComplete(idEvent, documentTaskMap.get(cdType), userId);

	}

}
