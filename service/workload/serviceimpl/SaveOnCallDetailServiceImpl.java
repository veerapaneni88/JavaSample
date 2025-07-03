package us.tx.state.dfps.service.workload.serviceimpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.RtrvOnCallDao;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.workload.dao.OnCallDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkDto;
import us.tx.state.dfps.service.workload.dto.OnCallScheduleDetailDto;
import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailDto;
import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailRes;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.SaveOnCallDetailService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<The purpose
 * of this class is to save/update the on call schedule details Aug 2, 2017-
 * 8:41:47 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class SaveOnCallDetailServiceImpl implements SaveOnCallDetailService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	RtrvOnCallDao rtrvOnCallDao;

	@Autowired
	OnCallDao onCallDao;

	@Autowired
	TodoDao toDoDao;

	private static final Logger log = Logger.getLogger(SaveOnCallDetailServiceImpl.class);

	private static final int MAX_ON_CALL_SCHEDULE_FOR_COUNTY_PROG = 350;

	private static final String WINDOW_MODE_NEW_USING = "2";

	private static final String WINDOW_MODE_MODIFY = "4";

	private static final String BLOCK = "BL";

	private static final String FILLED = "Y";

	private static final String ALL_COUNTIES_COUNTY_CODE = "255";
	private static final String ALL_COUNTIES_COUNTY_DECODE = "ALL";
	private static final String MULTIPLE_COUNTIES_DECODE = "MULT";
	private static final String SCHEDULE_FILLED = "Y";

	/**
	 * Method Name: saveOnCallDetail Method Description:This service checks for
	 * overlap and to save the on call schedule details.
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @return saveOnCallDetailRes - This dto is used to hold the response of
	 *         the add/update operation of on call schedule. @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveOnCallDetailRes saveOnCallDetail(SaveOnCallDetailDto saveOnCallDetailDto) {
		boolean indProceedService = true;
		log.debug("Entering method callSaveOnCallDetailService in SaveOnCallDetailServiceImpl");
		SaveOnCallDetailRes saveOnCallDetailRes = new SaveOnCallDetailRes();

		/*
		 * If the mode is new using , then overlap is checked with the new start
		 * date and end date with the existing on call schedule start date and
		 * end date If the over lap does not existing , then new entries are
		 * created in the ON_CALL, EMP_ON_CALL_LINK and _TODO tables
		 */

		if (WINDOW_MODE_NEW_USING.equals(saveOnCallDetailDto.getSysCdWinMode())) {
			/*
			 * The check for overlap is called to see if an overlap exists or
			 * not and if the overlap exists then do not create new on call as
			 * it will cause an overlap
			 */
			checkForOverlapExists(saveOnCallDetailDto, saveOnCallDetailRes);
			if (!ObjectUtils.isEmpty(saveOnCallDetailRes) && !ObjectUtils.isEmpty(saveOnCallDetailRes.getErrorDto())) {
				indProceedService = false;

			}
			/* If the overlap does not exist then a new on call is created */
			if (indProceedService) {
				saveNewOnCallSchedule(saveOnCallDetailDto, saveOnCallDetailRes);
			}
		}
		/* If the mode is not new using then only update the ON_CALL table */
		else {
			// Check for overlap and if overlap does not exist then update the
			// ON_CALL
			// ,ON_CALL_COUNTY, _TODO tables in the method
			addProposedShiftOrBlock(saveOnCallDetailDto, saveOnCallDetailRes);
			if (!ObjectUtils.isEmpty(saveOnCallDetailRes) && ObjectUtils.isEmpty(saveOnCallDetailRes.getErrorDto())) {
				// Update the EMP_ON_CALL_LINK table
				updateOnCallSchedule(saveOnCallDetailDto, saveOnCallDetailRes);
			}

		}

		log.debug("Exiting method callSaveOnCallDetailService in SaveOnCallDetailServiceImpl");
		// return control from the service implementation
		return saveOnCallDetailRes;
	}

	/**
	 * 
	 * Method Name: checkForOverlapExists Method Description: This service
	 * checks for overlap .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule. @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public void checkForOverlapExists(SaveOnCallDetailDto saveOnCallDetailDto,
			SaveOnCallDetailRes saveOnCallDetailRes) {

		LocalDateTime newStartDateTime = DateUtils.getDateTime(
				saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart(),
				saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());
		LocalDateTime newEndDateTime = DateUtils.getDateTime(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd(),
				saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());

		log.debug("Entering method checkForOverlapExists in SaveOnCallDetailServiceImpl");
		RtrvOnCallInDto rtrvOnCallInDto = new RtrvOnCallInDto();
		// Populate the request dto to fetch all the on call schedule for the
		// county/program combination
		rtrvOnCallInDto.setCdOnCallCounty(saveOnCallDetailDto.getOnCallSchedule().getOnCallCounty());
		rtrvOnCallInDto.setCdOnCallProgram(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram());
		rtrvOnCallInDto.setCdCountyCounter(1l);
		rtrvOnCallInDto.setCdRegion(saveOnCallDetailDto.getOnCallSchedule().getCdRegion());
		// Call the dao implementation to fetch the on call schedule list
		List<RtrvOnCallOutDto> onCallList = rtrvOnCallDao.getOnCallForCountyProgram(rtrvOnCallInDto);

		/*
		 * If the number of the records for the county/program is 350 then
		 * restrict the user from creating more on call schedule
		 */
		if (MAX_ON_CALL_SCHEDULE_FOR_COUNTY_PROG != onCallList.size()) {

			rtrvOnCallInDto.setDtOnCallEnd(Date.from(newEndDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			rtrvOnCallInDto.setDtOnCallStart(Date.from(newStartDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			rtrvOnCallInDto.setCdOnCallType(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallType());
			if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall())) {
				rtrvOnCallInDto.setIdOnCall(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall());
			}
			int endMin = newEndDateTime.getMinute();
			int startMin = newStartDateTime.getMinute();
			if (endMin == 0) {
				endMin = 00;
			}
			if (startMin == 0) {
				startMin = 00;
			}
			rtrvOnCallInDto.setTmOnCallEnd((newEndDateTime.getHour()) + "" + ((endMin == 0) ? "00" : endMin));
			rtrvOnCallInDto.setTmOnCallStart((newStartDateTime.getHour()) + "" + ((startMin == 0) ? "00" : startMin));

			/*
			 * Call the dao layer to check if the overlap exists . If overlap
			 * exists then return error code to the business delegate
			 */
			if (rtrvOnCallDao.checkOverLapExists(rtrvOnCallInDto)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(Messages.MSG_CMN_OVERLAP_ADD);
				saveOnCallDetailRes.setErrorDto(errorDto);
			}

		}
		/*
		 * return error code to the business delegate to indicate the maximum
		 * number of on call for the county/program already exists
		 */
		else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(Messages.MSG_CMN_ON_CALL_TOO_MANY);
			saveOnCallDetailRes.setErrorDto(errorDto);
		}
	}

	/**
	 * 
	 * Method Name: saveNewOnCallSchedule Method Description: This service
	 * creates a new on call schedule .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule. @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveNewOnCallSchedule(SaveOnCallDetailDto saveOnCallDetailDto,
			SaveOnCallDetailRes saveOnCallDetailRes) {
		log.debug("Entering method CallCCMN20D in SaveOnCallDetailServiceImpl");
		OnCallScheduleDetailDto onCallScheduleDetailDto = new OnCallScheduleDetailDto();
		AddOnCallInDto addOnCallInDto = new AddOnCallInDto();
		EmpOnCallLinkDto empOnCallLinkDto = new EmpOnCallLinkDto();
		TodoDto todoDto = new TodoDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		addOnCallInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		// Program
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram())) {
			addOnCallInDto.setCdOnCallProgram(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram());
		}
		// Type
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule())) {
			addOnCallInDto.setCdOnCallType(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallType());
		}

		// Region
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getCdRegion())) {
			addOnCallInDto.setCdRegion(saveOnCallDetailDto.getOnCallSchedule().getCdRegion());
		}
		// Start Date
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart())
				&& !ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart())) {
			addOnCallInDto.setDtOnCallStart(Date.from(DateUtils
					.getDateTime(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart(),
							saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// End Date
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd())
				&& !ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd())) {
			addOnCallInDto.setDtOnCallEnd(Date.from(DateUtils
					.getDateTime(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd(),
							saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// On_Call_Filed
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getOnCallFilled())) {
			addOnCallInDto.setIndOnCallFilled(saveOnCallDetailDto.getOnCallSchedule().getOnCallFilled());
		}
		// On_Call_County
		if (!CollectionUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getOnCallCounty())) {
			addOnCallInDto.setCdOnCallCounty(saveOnCallDetailDto.getOnCallSchedule().getOnCallCounty());
		}
		// Call the dao implementation to insert a new row in ON_CALL table and
		// ON_CALL_COUNTY table
		onCallScheduleDetailDto = onCallDao.addOnCall(addOnCallInDto);
		if (!ObjectUtils.isEmpty(onCallScheduleDetailDto)) {
			saveOnCallDetailRes.setOnCallScheduleDetailDto(onCallScheduleDetailDto);
			// Setting the onCallId in the request for EMP_ON_CALL_LINK table
			empOnCallLinkDto.setIdOnCall(onCallScheduleDetailDto.getIdOnCall());

			// Setting the values in the request for TO_DO table
			todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
			todoDto.setIdTodoPersCreator(0l);
			todoDto.setDtTodoCreated(new Date());
			todoDto.setDtTodoCompleted(new Date());
			todoDto.setDtTodoDue(new Date());
			/*
			 * Iterating over the records in the employee list to create new
			 * records in the EMP_ON_CALL_LINK table and _TODO table
			 */
			saveOnCallDetailDto.getEmpOnCallDtoList().forEach(empOnCallLink -> {
				todoDto.setTodoDesc(ServiceConstants.EMPTY_STRING);
				todoDto.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
				todoDto.setIdTodoPersAssigned(empOnCallLink.getIdPerson());
				todoDto.setTodoDesc("On-Call Addition.");
				todoDto.setTodoLongDesc("You have been added to the following On-Call");
				if (BLOCK.equalsIgnoreCase(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallType()))
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + " Block:");
				else
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + " Shift:");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Region: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdRegion());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   County: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdOnCallMultOrAll());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Program: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Start Date: ");
				LocalDateTime onCallDate = DateUtils.getDateTime(
						saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart(),
						saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());
				String startDate = "" + onCallDate.getMonth() + onCallDate.getDayOfMonth() + onCallDate.getYear();
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + startDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + startDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + "  ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Start Time: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());
				todoDto.setTodoDesc(todoDto.getTodoDesc() + saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());
				todoDto.setTodoDesc(todoDto.getTodoDesc() + " THRU ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   End Date: ");
				LocalDateTime onCallEndDate = DateUtils.getDateTime(
						saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd(),
						saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());
				String endDate = "" + onCallEndDate.getMonth() + onCallEndDate.getDayOfMonth()
						+ onCallEndDate.getYear();
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + endDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + endDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + "  ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   End Time: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());
				todoDto.setTodoDesc(todoDto.getTodoDesc() + saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Contact Order: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getEmpOnCallCntctOrd());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   On-Call Designation: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getCdEmpOnCallDesig());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   On-Call Phone: ");
				String formattedPhone = !ObjectUtils.isEmpty(empOnCallLink.getEmpOnCallPhone1())
						? FormattingHelper.formatPhone(empOnCallLink.getEmpOnCallPhone1()) : null;
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + formattedPhone);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Ext: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getEmpOnCallExt1());
				if (todoDto.getTodoLongDesc().length() > 300) {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().substring(0, 300));
				}
				empOnCallLinkDto.setIdPerson(empOnCallLink.getIdPerson());
				empOnCallLinkDto.setCdEmpOnCallDesig(empOnCallLink.getCdEmpOnCallDesig());
				empOnCallLinkDto.setEmpOnCallPhone1(empOnCallLink.getEmpOnCallPhone1());
				empOnCallLinkDto.setEmpOnCallExt1(empOnCallLink.getEmpOnCallExt1());
				empOnCallLinkDto.setEmpOnCallPhone2(empOnCallLink.getEmpOnCallPhone2());
				empOnCallLinkDto.setEmpOnCallExt2(empOnCallLink.getEmpOnCallExt2());
				empOnCallLinkDto.setEmpOnCallCntctOrd(empOnCallLink.getEmpOnCallCntctOrd());
				// Calling the dao implementation to insert a new record in the
				// _TODO table
				TodoDto savedToDo = toDoDao.todoAUD(todoDto, serviceReqHeaderDto);
				if (!ObjectUtils.isEmpty(savedToDo)) {
					// Calling the dao implementation to insert a new record in
					// the EMP_ON_CALL_LINK
					// table
					onCallDao.addEmpOnCallLink(empOnCallLinkDto);

				}
			});

			log.debug("Exiting method CallCCMN20D in SaveOnCallDetailServiceImpl");
		}
	}

	/**
	 * 
	 * Method Name: updateOnCallSchedule Method Description: This service update
	 * a on call schedule .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule.
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateOnCallSchedule(SaveOnCallDetailDto saveOnCallDetailDto, SaveOnCallDetailRes saveOnCallDetailRes) {
		log.debug("Entering method updateOnCallSchedule in SaveOnCallDetailServiceImpl");
		//artf204878- Added Date format for saving oncall task dates
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ServiceConstants.DATE_FORMAT_MMddyyyy);
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(ServiceConstants.TIME_FORMAT);
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		TodoDto todoDto = new TodoDto();
		EmpOnCallLinkDto empOnCallLinkDto = new EmpOnCallLinkDto();
		AddOnCallInDto addOnCallInDto = new AddOnCallInDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDto.setIdTodoPersCreator(0l);
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoCompleted(new Date());
		todoDto.setDtTodoDue(new Date());

		/*
		 * Iterating over the entries in the employee list and if the
		 * SzCdScrDataAction is ADD, DELETE,UPDATE then update the
		 * EMP_ON_CALL_LINK table
		 */
		if (!CollectionUtils.isEmpty(saveOnCallDetailDto.getEmpOnCallDtoList())) {

			saveOnCallDetailDto.getEmpOnCallDtoList().forEach(empOnCallLink -> {

				String indActionEmpOnCallLink = ServiceConstants.EMPTY_STR;
				todoDto.setTodoDesc(ServiceConstants.EMPTY_STRING);
				todoDto.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
				/*
				 * Based on the SzCdScrDataAction setting the description and
				 * long description accordingly for _TODO table
				 */
				if (ServiceConstants.REQ_FUNC_CD_ADD.equals(empOnCallLink.getCdScrDataAction())) {
					todoDto.setTodoDesc("On-Call Addition.");
					todoDto.setTodoLongDesc("You have been added to the following On-Call");
					indActionEmpOnCallLink = ServiceConstants.REQ_FUNC_CD_ADD;

				} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(empOnCallLink.getCdScrDataAction())) {
					todoDto.setTodoDesc("On-Call Modification.");
					todoDto.setTodoLongDesc("You have been modified in the following On-Call");
					indActionEmpOnCallLink = ServiceConstants.REQ_FUNC_CD_UPDATE;
				} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(empOnCallLink.getCdScrDataAction())) {
					todoDto.setTodoDesc("On-Call Deletion.");
					todoDto.setTodoLongDesc("You have been deleted from the following On-Call");
					indActionEmpOnCallLink = ServiceConstants.REQ_FUNC_CD_DELETE;
				}

				todoDto.setIdTodoPersAssigned(empOnCallLink.getIdPerson());
				if (BLOCK.equalsIgnoreCase(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallType()))
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + " Block:");
				else
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + " Shift:");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Region: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdRegion());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   County: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdOnCallMultOrAll());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Program: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Start Date: ");
				LocalDateTime onCallDate = DateUtils.getDateTime(
						saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart(),
						saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());

				String startDate = dateFormat.format(onCallDate);
				String startTime = timeFormat.format(onCallDate);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + startDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + startDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + " ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Start Time: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + startTime);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + startTime);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + " THRU ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   End Date: ");
				LocalDateTime onCallEndDate = DateUtils.getDateTime(
						saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd(),
						saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());
				String endDate = dateFormat.format(onCallEndDate);
				String endTime = timeFormat.format(onCallEndDate);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + endDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + endDate);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + " ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   End Time: ");
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc() + endTime);
				todoDto.setTodoDesc(todoDto.getTodoDesc() + endTime);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Contact Order: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getEmpOnCallCntctOrd());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   On-Call Designation: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getCdEmpOnCallDesig());
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   On-Call Phone: ");
				String formattedPhone = !ObjectUtils.isEmpty(empOnCallLink.getEmpOnCallPhone1())
						? FormattingHelper.formatPhone(empOnCallLink.getEmpOnCallPhone1()) : null;
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + formattedPhone);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + "   Ext: ");
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc() + empOnCallLink.getEmpOnCallExt1());
				if (todoDto.getTodoLongDesc().length() > 300) {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().substring(0, 300));
				}
				empOnCallLinkDto.setIdOnCall(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall());
				empOnCallLinkDto.setIdPerson(empOnCallLink.getIdPerson());
				empOnCallLinkDto.setCdEmpOnCallDesig(empOnCallLink.getCdEmpOnCallDesig());
				empOnCallLinkDto.setEmpOnCallCntctOrd(empOnCallLink.getEmpOnCallCntctOrd());
				empOnCallLinkDto.setEmpOnCallPhone1(empOnCallLink.getEmpOnCallPhone1());
				empOnCallLinkDto.setEmpOnCallExt1(empOnCallLink.getEmpOnCallExt1());
				empOnCallLinkDto.setEmpOnCallPhone2(empOnCallLink.getEmpOnCallPhone2());
				empOnCallLinkDto.setEmpOnCallExt2(empOnCallLink.getEmpOnCallExt2());
				empOnCallLinkDto.setIdEmpOnCallLink(empOnCallLink.getIdEmpOnCallLink());
				if (!ObjectUtils.isEmpty(empOnCallLink.getDtLastUpdateStr())) {
					empOnCallLinkDto
							.setDtLastUpdate((Date) jsonStringToObject(empOnCallLink.getDtLastUpdateStr(), Date.class));
				}

				// Calling the dao implementation to create a new record in the
				// _TODO table.
				toDoDao.todoAUD(todoDto, serviceReqHeaderDto);

				switch (indActionEmpOnCallLink) {
				case ServiceConstants.REQ_FUNC_CD_ADD:
					onCallDao.addEmpOnCallLink(empOnCallLinkDto);
					break;
				case ServiceConstants.REQ_FUNC_CD_UPDATE:
					onCallDao.updateEmpOnCalllink(empOnCallLinkDto);
					break;
				case ServiceConstants.REQ_FUNC_CD_DELETE:

					empOnCallLinkDto.setIdEmpOnCallLink(empOnCallLink.getIdEmpOnCallLink());
					onCallDao.deleteEmpOnCallLink(empOnCallLinkDto);
					break;

				}
			});
		}

		// Setting the IndOnCallFilled to Y for updating the ON_CALL table
		addOnCallInDto.setIndOnCallFilled(FILLED);
		addOnCallInDto.setIdOnCall(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall());
		addOnCallInDto.setDtLastUpdate(saveOnCallDetailDto.getOnCallSchedule().getDtLastUpdate());
		OnCallScheduleDetailDto onCallScheduleDetailDto = new OnCallScheduleDetailDto();
		// Calling the dao implementation to update the ON_CALL table
		onCallScheduleDetailDto.setDtLastUpdate(onCallDao.updateOnCallFiled(addOnCallInDto));
		onCallScheduleDetailDto.setIdOnCall(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall());
		saveOnCallDetailDto.setOnCallSchedule(onCallScheduleDetailDto);

		log.debug("Exiting method CallCCMN22D in SaveOnCallDetailServiceImpl");
	}

	/**
	 * Method Name: addProposedShiftOrBlock Method Description:This method is
	 * used to update the on call schedule information in ON_CALL,ON_CALL_COUNTY
	 * tables if a overlap does not exist.
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void addProposedShiftOrBlock(SaveOnCallDetailDto saveOnCallDetailDto,
			SaveOnCallDetailRes saveOnCallDetailRes) {

		// check for overlap before updating the on call schedyle details
		checkForOverlapExists(saveOnCallDetailDto, saveOnCallDetailRes);
		if (!ObjectUtils.isEmpty(saveOnCallDetailRes) && ObjectUtils.isEmpty(saveOnCallDetailRes.getErrorDto())
				&& WINDOW_MODE_MODIFY.equals(saveOnCallDetailDto.getSysCdWinMode())) {

			// Calling the populate method to populate the request dto to be
			// passed to the
			// dao layer
			AddOnCallInDto addOnCallInDto = populateRequest(saveOnCallDetailDto);
			// setting the date along with the time part while saving to the
			// database
			addOnCallInDto.setDtOnCallStart(Date.from(DateUtils
					.getDateTime(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart(),
							saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart())
					.atZone(ZoneId.systemDefault()).toInstant()));
			addOnCallInDto.setDtOnCallEnd(Date.from(DateUtils
					.getDateTime(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd(),
							saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd())
					.atZone(ZoneId.systemDefault()).toInstant()));
			// Calling the dao layer to update the on call details
			onCallDao.updateOnCall(addOnCallInDto);

		}
	}

	/**
	 * Method Name: populateRequestForSave Method Description:This method is
	 * used to populate the request for saving the oncall schedule.
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @return addOnCallInDto - The dto is used to hold the values for iserting
	 *         a new record in the ON_CALL, ON_CALL_COUNTY table.
	 */
	private AddOnCallInDto populateRequest(SaveOnCallDetailDto saveOnCallDetailDto) {
		AddOnCallInDto addOnCallInDto = new AddOnCallInDto();
		List<String> selectedCounties = new ArrayList<String>();
		/*
		 * If the region is selected as State-wide then populate
		 * ALL_COUNTIES_COUNTY_CODE in the selected counties
		 */
		if (CodesConstant.CREGIONS_98.equals(saveOnCallDetailDto.getOnCallSchedule().getCdRegion())) {
			addOnCallInDto.setCdOnCallMultOrAll(ALL_COUNTIES_COUNTY_DECODE);
			selectedCounties.add(ALL_COUNTIES_COUNTY_CODE);

		}
		/*
		 * else if more than 1 county is selected then populate
		 * CdOnCallMultOrAll as MULT and the counties in the selected counties
		 */
		else {
			selectedCounties = saveOnCallDetailDto.getOnCallSchedule().getOnCallCounty();
			if (selectedCounties.size() > 1) {
				addOnCallInDto.setCdOnCallMultOrAll(MULTIPLE_COUNTIES_DECODE);
			}
			// Else set an dummy value
			else {
				addOnCallInDto.setCdOnCallMultOrAll(ServiceConstants.STR_ZERO_VAL);
			}
		}
		addOnCallInDto.setCdOnCallCounty(selectedCounties);
		addOnCallInDto.setIndOnCallFilled(SCHEDULE_FILLED);
		// If the request is an update then set the id on call
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall())) {
			addOnCallInDto.setIdOnCall(saveOnCallDetailDto.getOnCallSchedule().getIdOnCall());
		}
		// If the request is an update then set the last update date
		if (!ObjectUtils.isEmpty(saveOnCallDetailDto.getOnCallSchedule().getDtLastUpdate())) {
			addOnCallInDto.setDtLastUpdate(saveOnCallDetailDto.getOnCallSchedule().getDtLastUpdate());
		}
		// Program
		addOnCallInDto.setCdOnCallProgram(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallProgram());
		// Region
		addOnCallInDto.setCdRegion(saveOnCallDetailDto.getOnCallSchedule().getCdRegion());
		// Type
		addOnCallInDto.setCdOnCallType(saveOnCallDetailDto.getOnCallSchedule().getCdOnCallType());
		// End time
		addOnCallInDto.setTmOnCallEnd(saveOnCallDetailDto.getOnCallSchedule().getTmOnCallEnd());
		// Start Date
		addOnCallInDto.setTmOnCallStart(saveOnCallDetailDto.getOnCallSchedule().getTmOnCallStart());
		// End Date
		addOnCallInDto.setDtOnCallEnd(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallEnd());
		// Start Date
		addOnCallInDto.setDtOnCallStart(saveOnCallDetailDto.getOnCallSchedule().getDtOnCallStart());
		// Add or Update Indicator
		addOnCallInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

		return addOnCallInDto;
	}

	/**
	 * Method Name: jsonStringToObject Method Description:This method is used to
	 * convert a json string to an Object
	 * 
	 * @param inputJson
	 *            - The json string which has to be converted to an object.
	 * @param inputClass
	 *            - The type of object the json has to be converted.
	 * @return outputObj
	 */
	private Object jsonStringToObject(String inputJson, Class<? extends Object> inputClass) {

		Object outputObj = null;
		try {
			inputJson = inputJson.replace("~S~", "\'");
			ObjectMapper mapper = new ObjectMapper();
			outputObj = mapper.readValue(inputJson, inputClass);
		} catch (IOException ioExp) {
			log.info("error occured while parsing the json string to object in saveoncalldetailserviceimpl");
		}

		return outputObj;
	}
}
