
package us.tx.state.dfps.service.common.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.domain.Task;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.CommonDateReq;
import us.tx.state.dfps.service.common.request.OnlineParamsReq;
import us.tx.state.dfps.service.common.request.TaskReq;
import us.tx.state.dfps.service.common.response.CodeLookUpRes;
import us.tx.state.dfps.service.common.response.CommonDateRes;
import us.tx.state.dfps.service.common.response.ICPCPlacementRes;
import us.tx.state.dfps.service.common.response.MessageHelpLinkRes;
import us.tx.state.dfps.service.common.response.MessageLookUpRes;
import us.tx.state.dfps.service.common.response.OnlineParamsRes;
import us.tx.state.dfps.service.common.response.ReportLookUpRes;
import us.tx.state.dfps.service.common.response.TaskRes;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.lookup.dto.CodeTableRowAttributes;
import us.tx.state.dfps.service.lookup.dto.CodesTableViewCEMPJBCLAttributes;
import us.tx.state.dfps.service.lookup.dto.MessageAttribute;
import us.tx.state.dfps.service.lookup.dto.MessageHelpLinkDto;
import us.tx.state.dfps.service.lookup.dto.ReportDto;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.workload.dto.TaskDto;

/**
 * ImpactWS- IMPACT PHASE 2 MODERNIZATION
 *
 * Apr 11, 2017- 2:35:54 PM
 *
 */
@RestController
@Api(tags = { "staffSearch" })
@RequestMapping("/lookup")
public class LookUpController {

	@Autowired
	LookupService lookupService;

	@Autowired
	CommonService commonService;

	private static final Logger log = Logger.getLogger(LookUpController.class);

	/*
	 * This method will return the codes used in the CODES_TABLES for look up at
	 * the front end.
	 * 
	 * @return CodeLookUpRes
	 */
	@RequestMapping(value = "/codes", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CodeLookUpRes buildCodeTrees() {

		CodeLookUpRes codeLookUpresponse = new CodeLookUpRes();
		HashMap<String, TreeMap<String, CodeAttributes>> codeCategoryMap = null;

		codeCategoryMap = lookupService.getCodes();

		codeLookUpresponse.setCodeCategoryMap(codeCategoryMap);

		return codeLookUpresponse;

	}

	/*
	 * This method will return the messages used in the MESSAGE for look up at
	 * the front end.
	 * 
	 * @return CodeLookUpRes
	 */
	@RequestMapping(value = "/messages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MessageLookUpRes buildMessageTree() {

		MessageLookUpRes msgLookUpresponse = new MessageLookUpRes();
		TreeMap<Integer, MessageAttribute> msgCategoryMap = null;

		msgCategoryMap = lookupService.getMessages();

		msgLookUpresponse.setMsgCategoryMap(msgCategoryMap);

		return msgLookUpresponse;

	}

	/**
	 * 
	 *Method Name:	getTaskColumnString
	 *Method Description:this method is used to get the task string.
	 *@param taskReq
	 *@return
	 */
	@RequestMapping(value = "/taskData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  String getTaskColumnString(@RequestBody TaskReq taskReq) {
		return lookupService.getTaskColumnString(taskReq.getTaskCode(), taskReq.getColumn());
	}

	/**
	 * 
	 *Method Name:	getTask
	 *Method Description:this method is used to get the task detail.
	 *@param taskReq
	 *@return
	 */
	@RequestMapping(value = "/getTask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TaskRes getTask(@RequestBody TaskReq taskReq) {
		TaskDto taskDto = new TaskDto();
		Task task = lookupService.getTask(taskReq.getTaskCode());
		BeanUtils.copyProperties(task, taskDto);
		taskDto.setTaskDecode(task.getTxtTaskDecode());
		TaskRes taskRes = new TaskRes();
		taskRes.setTask(taskDto);
		return taskRes;
	}

	/**
	 * 
	 *Method Name:	getCaseTodoTasks
	 *Method Description:this method is used to get the to do task list for a case.
	 *@param taskReq
	 *@return
	 */
	@RequestMapping(value = "/getCaseTodoTasks", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TaskRes getCaseTodoTasks(@RequestBody TaskReq taskReq) {
		List<TaskDto> taskDtoList = new ArrayList<TaskDto>();
		List<Task> taskList = lookupService.getCaseTodoTasks(taskReq.getSzCdStage(), taskReq.getSzCdStageProgram());
		if (taskList != null && taskList.size() > 0) {
			for (Task task : taskList) {
				TaskDto taskDto = new TaskDto();
				BeanUtils.copyProperties(task, taskDto);
				taskDto.setTaskDecode(task.getTxtTaskDecode());
				taskDtoList.add(taskDto);
			}
		}
		TaskRes taskRes = new TaskRes();
		taskRes.setTasks(taskDtoList);
		return taskRes;
	}

	/*
	 * This method will return the messages used in the CodeTableRow for look up
	 * at the front end.
	 * 
	 * @return CodeLookUpRes
	 * 
	 */
	@RequestMapping(value = "/codeTableRows", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CodeLookUpRes buildCodeTableRowTree() {

		CodeLookUpRes codeLookUpRes = new CodeLookUpRes();
		TreeMap<String, CodeTableRowAttributes> codeTableRowCategoryMap = null;

		codeTableRowCategoryMap = lookupService.getCodeTableRows();

		codeLookUpRes.setCodeTableRowCategoryMap(codeTableRowCategoryMap);
		return codeLookUpRes;

	}


	@ApiOperation(value = "Get employee job class codes", tags = { "staffSearch" })
	@RequestMapping(value = "/codeTableViewCEMPJBCL", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CodeLookUpRes getCodesTableView_CEMPJBCL() {

		CodeLookUpRes codeLookUpRes = new CodeLookUpRes();
		TreeMap<String, String> codeTableViewMap = null;

		codeTableViewMap = lookupService.getCodesTableView_CEMPJBCL();

		codeLookUpRes.setCodeTableViewMap(codeTableViewMap);
		return codeLookUpRes;

	}

	@RequestMapping(value = "/codeTableViewAllCEMPJBCL", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CodeLookUpRes getCodesTableViewAllCEMPJBCL() {

		CodeLookUpRes codeLookUpRes = new CodeLookUpRes();
		TreeMap<String, CodesTableViewCEMPJBCLAttributes> codeTableViewAllMap = null;

		codeTableViewAllMap = lookupService.getCodesTableViewAllCEMPJBCL();

		codeLookUpRes.setCodeTableViewCEMPJBCLMap(codeTableViewAllMap);
		return codeLookUpRes;

	}

	/**
	 * 
	 *Method Name:	getOnlineParameters
	 *Method Description:This method is used to get the online parameter based on the param name.
	 *@param onlineParamsReq
	 *@return
	 */
	@RequestMapping(value = "/getOnlineParameters", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  OnlineParamsRes getOnlineParameters(@RequestBody OnlineParamsReq onlineParamsReq) {

		return lookupService.getOnlineParams(onlineParamsReq.getTxtParamName());

	}

	/**
	 * 
	 * Method Name: buildMessageHelpLinkTree Method Description: This method
	 * will return the messageHelpLink for look up at the front end.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/messageHelpLink", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MessageHelpLinkRes buildMessageHelpLinkTree() {

		MessageHelpLinkRes msgHelpLinkRes = new MessageHelpLinkRes();
		TreeMap<Integer, MessageHelpLinkDto> msgHelpLinkMap = null;

		msgHelpLinkMap = lookupService.getMessageHelpLink();

		msgHelpLinkRes.setMessageHelpLinkMap(msgHelpLinkMap);

		return msgHelpLinkRes;

	}

	/**
	 * 
	 * Method Name: buildMessageHelpLinkTree Method Description: This method
	 * will return the messageHelpLink for look up at the front end.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getReleaseDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonDateRes getAppRelDate(@RequestBody CommonDateReq request) {

		CommonDateRes commonDateResponse = new CommonDateRes();
		Date date = commonService.getAppRelDate(request.getRelCode());

		commonDateResponse.setDate(date);

		return commonDateResponse;

	}

	/**
	 * Method Name: getTasks Method Description: This method is to load the
	 * tasks from the database to the dynacache
	 * 
	 * @return taskLookUpRes
	 */
	@RequestMapping(value = "/getTaskCodes", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TaskRes getTasks() {

		TaskRes taskLookUpRes = new TaskRes();
		taskLookUpRes = commonService.getTaskCodes();
		return taskLookUpRes;

	}

	/**
	 * 
	 *Method Name:	getReportsData
	 *Method Description:This method used to get hte reports data
	 *@return
	 */
	@RequestMapping(value = "/getReportsData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ReportLookUpRes getReportsData() {

		ReportLookUpRes response = new ReportLookUpRes();
		HashMap<String, ReportDto> reportMap;
		reportMap = lookupService.getReportsData();
		response.setReportMap(reportMap);
		return response;

	}

	/**
	 * 
	 *Method Name:	simpleDecodeSafe
	 *Method Description:this method is used to get the decoded value.
	 *@param req
	 *@return
	 */
	@RequestMapping(value = "/simpleDecodeSafe", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ReportLookUpRes simpleDecodeSafe(@RequestBody CommonApplicationReq req) {

		String response = null;
		ReportLookUpRes res = new ReportLookUpRes();
		response = lookupService.simpleDecodeSafe(req.getCodeCategory(), req.getEncodedValue());
		res.setDecode(response);
		return res;

	}

	/**
	 * 
	 *Method Name:	getAgencyVector
	 *Method Description:this method is used to get the agency detail
	 *@return
	 */
	@RequestMapping(value = "/getAgencyVector", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getAgencyVector() {

		ICPCPlacementRes res = new ICPCPlacementRes();
		res.setAllAgencyVector(lookupService.getAgencyVector());
		return res;

	}
}
