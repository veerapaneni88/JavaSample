package us.tx.state.dfps.service.oncall.serviceimpl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.EmpOnCallLinkPersonPhoneDao;
import us.tx.state.dfps.service.admin.dao.RtrvOnCallDao;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.OnCallCountyReq;
import us.tx.state.dfps.service.common.request.OnCallSearchReq;
import us.tx.state.dfps.service.common.response.AddOnCallResponse;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.OnCallCountyRes;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.oncall.service.OnCallService;
import us.tx.state.dfps.service.person.dto.RtrvOnCallCntyDto;
import us.tx.state.dfps.service.workload.dao.OnCallDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneInDto;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneOutDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

@Service
@Transactional
public class OnCallServiceImpl implements OnCallService {
	@Autowired
	MessageSource messageSource;

	@Autowired
	CommonService commonService;

	@Autowired
	RtrvOnCallDao rtrvOnCallDao;

	@Autowired
	OnCallDao onCallDao;

	@Autowired
	EmpOnCallLinkPersonPhoneDao empOnCallLinkPersonPhoneDao;

	@Autowired
	TodoDao todoDao;

	@Override
	public AddOnCallResponse addProposedShiftOrBlock(AddOnCallInDto pInputMsg) {
		RtrvOnCallInDto ccmn16diDto = new RtrvOnCallInDto();

		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getCdRegion())) {
			ccmn16diDto.setCdRegion(pInputMsg.getCdRegion());
		}

		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getCdOnCallProgram())) {
			ccmn16diDto.setCdOnCallProgram(pInputMsg.getCdOnCallProgram());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getCdOnCallCounty())) {
			ccmn16diDto.setCdOnCallCounty(pInputMsg.getCdOnCallCounty());
		}
		ccmn16diDto.setCdCountyCounter((long) pInputMsg.getCdOnCallCounty().size());
		AddOnCallResponse addOnCallResponse = new AddOnCallResponse();
		if (pInputMsg.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {

			onCallDao.deleteOnCall(pInputMsg);
			if (pInputMsg.getIndOnCallFilled().equals(ServiceConstants.STRING_IND_Y)) {
				callToEmpOnCallLinkAndTodo(pInputMsg.getIdOnCall(), pInputMsg);
				onCallDao.deleteEmpOnCallLinkByOnCallId(pInputMsg.getIdOnCall());
			}
			addOnCallResponse.setMessage("Deleted Successfully");
		}

		return addOnCallResponse;
	}

	public void callToEmpOnCallLinkAndTodo(Long idOnCall, AddOnCallInDto pInputMsg) {
		//artf204878- Changed Date format for Oncall Delete task dates
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ServiceConstants.DATE_FORMAT_MMddyyyy);
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(ServiceConstants.TIME_FORMAT);
		EmpOnCallLinkPersonPhoneInDto empOnCallLinkPersonPhoneInDto = new EmpOnCallLinkPersonPhoneInDto();
		empOnCallLinkPersonPhoneInDto.setIdOnCall(idOnCall);
		List<EmpOnCallLinkPersonPhoneOutDto> empOnCallLinkPersonPhoneList = empOnCallLinkPersonPhoneDao
				.getEmployeeOnCallList(empOnCallLinkPersonPhoneInDto);
		if (!empOnCallLinkPersonPhoneList.isEmpty()) {
			ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
			for (EmpOnCallLinkPersonPhoneOutDto empOnCallLinkPersonPhoneOutDto : empOnCallLinkPersonPhoneList) {
				TodoDto todoDto = new TodoDto();
				todoDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				todoDto.setCdTodoType("A");
				todoDto.setIdTodoPersCreator(0L);
				todoDto.setDtTodoCreated(new Date()); // need confirmation
				todoDto.setDtTodoCompleted(new Date()); // need confirmation
				todoDto.setDtTodoDue(new Date()); // need confirmation
				todoDto.setDtTodoTaskDue(null); // need confirmation
				todoDto.setIdTodoPersAssigned(empOnCallLinkPersonPhoneOutDto.getIdPerson());
				if (pInputMsg.getReqFuncCd().equals(ServiceConstants.DELETE)) {
					todoDto.setTodoDesc("On-Call Deletion.");
				} else {
					todoDto.setTodoDesc("On-Call Modification.");
				}
				todoDto.setTodoLongDesc("The following On-Call");
				if (pInputMsg.getReqFuncCd().equals(ServiceConstants.DELETE)) {
					if (pInputMsg.getCdOnCallType().equals("BL")) {
						todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(" Block "));
					} else {
						todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(" Shift "));
					}
				} else {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(""));// old_schedule
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("  The new schedule is:"));
				}

				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc().concat("   Region: ").concat(pInputMsg.getCdRegion()));
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("   County: "));
				for (String cdOnCallCounty : pInputMsg.getCdOnCallCounty()) {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(cdOnCallCounty));
				}
				if (pInputMsg.getCdOnCallCounty().size() > 1) {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(pInputMsg.getCdOnCallMultOrAll()));
				} else {
					todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat(pInputMsg.getCdOnCallCounty().get(0)));
				}
				todoDto.setTodoLongDesc(
						todoDto.getTodoLongDesc().concat("   Program: ").concat(pInputMsg.getCdOnCallProgram()));
				LocalDateTime onCallDate = DateUtils.getDateTime(pInputMsg.getDtOnCallStart(), pInputMsg.getTmOnCallStart());
				String startDate = dateFormat.format(onCallDate);
				String startTime = timeFormat.format(onCallDate);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("   Start Date: ")
						.concat(startDate));
				todoDto.setTodoDesc(
						todoDto.getTodoDesc().concat(startDate).concat(" "));
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("   Start Time: ")
						.concat(startTime));
				todoDto.setTodoDesc(
						todoDto.getTodoDesc().concat(startTime).concat(" THRU "));
				LocalDateTime onCallEndDate = DateUtils.getDateTime(pInputMsg.getDtOnCallEnd(), pInputMsg.getTmOnCallEnd());
				String endDate = dateFormat.format(onCallEndDate);
				String endTime = timeFormat.format(onCallEndDate);
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("   End Date: ")
						.concat(endDate));
				todoDto.setTodoDesc(
						todoDto.getTodoDesc().concat(endDate).concat(" "));
				todoDto.setTodoLongDesc(todoDto.getTodoLongDesc().concat("   End Time: ")
						.concat(endTime));
				todoDto.setTodoDesc(todoDto.getTodoDesc().concat(endTime));

				todoDao.todoAUD(todoDto, archInputDto);

			}
		}
	}

	public Date formatTime(Date date, String time_type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (time_type == "starttime") {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else if (time_type == "endtime") {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
		}
		return cal.getTime();
	}

	/**
	 * 
	 * Method Name: rtrvOnCallCountyDtl Method Description: This service
	 * retrieves data for On Call List window's county list box.
	 * 
	 * @param onCallCountyReq
	 * @return OnCallCountyRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public OnCallCountyRes rtrvOnCallCountyDtl(OnCallCountyReq onCallCountyReq) {
		OnCallCountyRes onCallCountyRes = new OnCallCountyRes();
		List<RtrvOnCallCntyDto> rtrvOnCallCntyDtoList = new ArrayList<>();
		rtrvOnCallCntyDtoList = onCallDao.getOnCallCounty(onCallCountyReq.getCdRegion(), onCallCountyReq.getIdOnCall());
		onCallCountyRes.setRtrvOnCallCntyDtoList(rtrvOnCallCntyDtoList);
		return onCallCountyRes;

	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getRouterPersonOnCall
	 * Method Description: Retrieves the Person with router designation based on the program, county,
	 * start date and end date (including the start time and end time)
	 *
	 * @param onCallSearchReq
	 * @return
	 */
	@Override
	public Person getRouterPersonOnCall(OnCallSearchReq onCallSearchReq) {

		return onCallDao.getRouterPersonOnCall(onCallSearchReq.getCdOnCallProgram(),
				onCallSearchReq.getCdOnCallCounty());
	}
}
