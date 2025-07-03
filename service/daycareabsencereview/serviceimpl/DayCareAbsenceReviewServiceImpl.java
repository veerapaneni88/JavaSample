/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 27, 2018- 6:23:44 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.daycareabsencereview.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import us.tx.state.dfps.common.domain.Facility;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionOutputDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.daycareabsencereview.service.DayCareAbsenceReviewService;
import us.tx.state.dfps.service.person.dao.DayCareAbsenceReviewDao;
import us.tx.state.dfps.service.person.dto.DayCareAbsReviewDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 27, 2018- 6:23:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class DayCareAbsenceReviewServiceImpl implements DayCareAbsenceReviewService {

	@Autowired
	private DayCareAbsenceReviewDao dayCareAbsenceReviewDao;

	@Autowired
	private TodoCommonFunctionService commonToDoFunctionService;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-DayCareAbsenceReviewServiceLog");

	/**
	 * Method Name: getRdccPerson Method Description: This method retrieves RDCC
	 * Person using idTodo
	 * 
	 * @param idTodo
	 * @return idRdccPerson
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/getRdccPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Long getRdccPerson(Long idTodo) {
		Long idRdccPerson;
		idRdccPerson = dayCareAbsenceReviewDao.getRdccPerson(idTodo);
		LOG.info(ServiceConstants.TRANSACTION_ID + idRdccPerson);
		return idRdccPerson;
	}

	/**
	 * Method Name: getDaycareSvcAuthInfo Method Description: This method
	 * retrieves DayCare Service Authorization detail information using idTodo
	 * 
	 * @param idTodo
	 * @return daycareAbsenceReviewDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/getDaycareSvcAuthInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public DayCareAbsReviewDto getDaycareSvcAuthInfo(Long idTodo) {
		DayCareAbsReviewDto daycareAbsenceReviewDto;
		// retrieves Day Care Authorization Service Info using idToDo
		daycareAbsenceReviewDto = dayCareAbsenceReviewDao.getDaycareSvcAuthInfo(idTodo);
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsenceReviewDto);
		return daycareAbsenceReviewDto;
	}

	/**
	 * Method Name: getDaycareSvcAuthPerson Method Description: This method
	 * retrieves DayCare Service Authorization person using idPerson and idEvent
	 * 
	 * @param idPerson
	 * @param idEvent
	 * @return
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/getDaycareSvcAuthPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public DayCareAbsReviewDto getDaycareSvcAuthPerson(Long idPerson, Long idEvent) {
		DayCareAbsReviewDto dayCareAbsReviewDto = null;
		ArrayList<DayCareAbsReviewDto> daycareAbsenceReviewDtoList = new ArrayList<>();
		daycareAbsenceReviewDtoList = dayCareAbsenceReviewDao.getDaycareSvcAuthPerson(idPerson, idEvent);
		// List in DaoImpl is returned as an ArrayList which maintains insertion
		// order from the
		// query which which terminated with an "order by" clause. Thus, we
		// return the last
		// value from daycareRewDtoList which will be the desired record.
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsenceReviewDtoList);
		if (CollectionUtils.isNotEmpty(daycareAbsenceReviewDtoList)) {
			dayCareAbsReviewDto = daycareAbsenceReviewDtoList.get(daycareAbsenceReviewDtoList.size() - 1);
		} else {
			dayCareAbsReviewDto = new DayCareAbsReviewDto();
		}
		return dayCareAbsReviewDto;
	}

	/**
	 * Method Name: createRDCCAlert Method Description:This method to create an
	 * alert to do to regional day care staff
	 * 
	 * @param idTodo
	 * @param dayCareAbsReviewDto
	 * @return
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/createRDCCAlert", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Long createRDCCAlert(Long idTodo, DayCareAbsReviewDto dayCareAbsReviewDto) {
		Long idRdccPerson = dayCareAbsenceReviewDao.getRdccPerson(idTodo);
		Long userId = dayCareAbsReviewDto.getIdUser();
		Long idStage = dayCareAbsReviewDto.getIdStage();
		Long idEvent = dayCareAbsReviewDto.getIdEvent();
		String dayCareRsrcName = getChildDayCareRsrcName(dayCareAbsReviewDto);
		String todoInfoAlert = ServiceConstants.DAR002;
		// Short Description DayCare 5 Day Absence -{Name Person
		// Full}-Caseworker chose to continue daycare.
		StringBuilder alertShortDesc = new StringBuilder();
		alertShortDesc.append(ServiceConstants.DAYCARE);
		alertShortDesc.append(dayCareAbsReviewDto.getNmPersonFull());
		alertShortDesc.append(ServiceConstants.DAYCARE_TERMINATE);
		// Long Description {termination date}.
		StringBuilder alertLongDesc = new StringBuilder();
		alertLongDesc.append(dayCareAbsReviewDto.getNmPersonFull());
		alertLongDesc.append(ServiceConstants.HYPHEN);
		alertLongDesc.append(dayCareAbsReviewDto.getIdPerson());
		alertLongDesc.append(ServiceConstants.DAYCARE_REPORT);
		alertLongDesc.append(dayCareRsrcName);
		alertLongDesc.append(ServiceConstants.BETWEEN);
		alertLongDesc.append(dayCareAbsReviewDto.getDtAbsentBegin());
		alertLongDesc.append(ServiceConstants.HYPHEN);
		alertLongDesc.append(dayCareAbsReviewDto.getDtAbsentEnd());
		alertLongDesc.append(ServiceConstants.CASEWORKER_TERMINATE);
		alertLongDesc.append(dayCareAbsReviewDto.getDtDaycareTermDate());
		if (idRdccPerson > 0) {
			createWorkerToDo(alertShortDesc.toString(), alertLongDesc.toString(), todoInfoAlert, idRdccPerson, userId,
					idStage, idEvent);
		}
		LOG.info(ServiceConstants.TRANSACTION_ID + idRdccPerson);
		return null;
	}

	/**
	 * 
	 * Method Name: createDaycareSupervisorAlert Method Description:This method
	 * to create an alert to do to regional day care staff
	 * 
	 * @param idTodo
	 * @param dayCareAbsReviewDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/createDaycareSupervisorAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long createDaycareSupervisorAlert(Long idTodo, DayCareAbsReviewDto dayCareAbsReviewDto) {
		Long idDayCareSupervisor = dayCareAbsenceReviewDao.getDaycareSupervisor(idTodo);
		Long userId = dayCareAbsReviewDto.getIdUser();
		Long idStage = dayCareAbsReviewDto.getIdStage();
		Long idEvent = dayCareAbsReviewDto.getIdEvent();
		String dayCareRsrcName = getChildDayCareRsrcName(dayCareAbsReviewDto);
		String todoInfoAlert = ServiceConstants.DAR001;
		// Short Description DayCare 5 Day Absence -{Name Person
		// Full}-Caseworker chose to continue daycare.
		StringBuilder alertShortDesc = new StringBuilder();
		alertShortDesc.append(ServiceConstants.DAYCARE);
		alertShortDesc.append(dayCareAbsReviewDto.getNmPersonFull());
		alertShortDesc.append(ServiceConstants.DAYCARE_CONTINUE);
		// Long Description {termination date}.
		StringBuilder alertLongDesc = new StringBuilder();
		alertLongDesc.append(dayCareAbsReviewDto.getNmPersonFull());
		alertLongDesc.append(ServiceConstants.HYPHEN);
		alertLongDesc.append(dayCareAbsReviewDto.getIdPerson());
		alertLongDesc.append(ServiceConstants.DAYCARE_REPORT);
		alertLongDesc.append(dayCareRsrcName);
		alertLongDesc.append(ServiceConstants.BETWEEN);
		alertLongDesc.append(dayCareAbsReviewDto.getDtAbsentBegin());
		alertLongDesc.append(ServiceConstants.HYPHEN);
		alertLongDesc.append(dayCareAbsReviewDto.getDtAbsentEnd());
		alertLongDesc.append(ServiceConstants.CASEWORKER_CONTINUE);
		DayCareAbsReviewDto daycareTodo = getDaycareTodo(idTodo);
		if (!ObjectUtils.isEmpty(daycareTodo.getIdTodo()) && daycareTodo.getIdTodo().equals(idTodo)
				&& daycareTodo.getDtTodoCompleted() == null && idDayCareSupervisor > 0) {
			createWorkerToDo(alertShortDesc.toString(), alertLongDesc.toString(), todoInfoAlert, idDayCareSupervisor,
					userId, idStage, idEvent);
		}
		LOG.info(ServiceConstants.TRANSACTION_ID + todoInfoAlert);
		return null;
	}

	/**
	 * 
	 * Method Name: createWorkerToDo Method Description:Sends alert to do to
	 * case worker To do.
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @return Long
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/createWorkerToDo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Long createWorkerToDo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Long idPrsnAssgn,
			Long idUser, Long idStage, Long idEvent) {
		Date dtDtSystemDate = new Date();
		TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
		TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
		todoCommonFunctionDto.setSysCdTodoCf(cdTodoInfoType);
		todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(dtDtSystemDate);
		todoCommonFunctionDto.setSysIdTodoCfPersCrea(idUser);
		todoCommonFunctionDto.setSysIdTodoCfStage(idStage);
		todoCommonFunctionDto.setTsLastUpdate(dtDtSystemDate);
		todoCommonFunctionDto.setSysIdTodoCfPersAssgn(idPrsnAssgn);
		todoCommonFunctionDto.setSysIdTodoCfPersWkr(idUser);
		if (toDoDesc != null)
			todoCommonFunctionDto.setSysTxtTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			todoCommonFunctionDto.setSysTxtTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			todoCommonFunctionDto.setSysIdTodoCfEvent(idEvent);
		}
		todoCommonFunctionDto.setDayCareAbsInd(Boolean.TRUE);
		todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
		TodoCommonFunctionOutputDto todoCreateOutDto = commonToDoFunctionService
				.TodoCommonFunction(todoCommonFunctionInputDto);
		LOG.info(ServiceConstants.TRANSACTION_ID + todoCreateOutDto);
		return null;
	}

	/**
	 * 
	 * Method Name: getChildDayCareRsrcName Method Description:this method Gets
	 * Child day care resource name event using idTodo.
	 * 
	 * @param daycareAbsRevVB
	 * @return String
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/getChildDayCareRsrcName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public String getChildDayCareRsrcName(DayCareAbsReviewDto daycareAbsRevVB) {
		List<Facility> daycareFacilityList;
		daycareFacilityList = dayCareAbsenceReviewDao.getChildDayCareRsrcName(daycareAbsRevVB);
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareFacilityList);
		if (ServiceConstants.Zero == daycareFacilityList.size())
			return ServiceConstants.EMPTY_STR;
		else if (ServiceConstants.TWO_INT <= daycareFacilityList.size())
			return ServiceConstants.MULTIPLE_DAYCARES;
		else
			return daycareFacilityList.get(ServiceConstants.Zero).getNmFclty();
	}

	/**
	 * Method Name: updateTwcAbsTrans Method Description:this method Updates
	 * TWC_ABSENT_TRANS table using idTodo
	 * 
	 * @param daycareAbsRewVB
	 * @param idTodo
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/updateTwcAbsTrans", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Long updateTwcAbsTrans(DayCareAbsReviewDto daycareAbsRewVB, Long idTodo) {
		Long count;
		count = dayCareAbsenceReviewDao.updateTwcAbsTrans(daycareAbsRewVB, idTodo);
		LOG.info(ServiceConstants.TRANSACTION_ID + count);
		return count;
	}

	/**
	 * Method Name: markDaycareTodoCompleted Method Description:this method
	 * marks day care to do completed per idTodo.
	 * 
	 * @param idTodo
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/markDaycareTodoCompleted", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long markDaycareTodoCompleted(DayCareAbsReviewDto dayCareAbsReviewDto) {
		Long idTodo = dayCareAbsReviewDto.getIdTodo();
		Long count = ServiceConstants.ZERO_VAL;
		DayCareAbsReviewDto daycareTodo = getDaycareTodo(idTodo);
		if (daycareTodo.getIdTodo().equals(idTodo)) {
			count = dayCareAbsenceReviewDao.markDaycareTodoCompleted(dayCareAbsReviewDto);
		}
		LOG.info(ServiceConstants.TRANSACTION_ID + count);
		return count;
	}

	/**
	 * Method Name: getDaycareTodo Method Description: this method Gets day care
	 * incomplete day care to do.
	 * 
	 * @param idTodo
	 * @return DayCareAbsReviewDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/getDaycareTodo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public DayCareAbsReviewDto getDaycareTodo(Long idTodo) {
		DayCareAbsReviewDto daycareTodoDto;
		daycareTodoDto = dayCareAbsenceReviewDao.getDaycareTodo(idTodo);
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareTodoDto);
		return daycareTodoDto;
	}

	/**
	 * Method Name: isTodoCreatedForStage Method Description:This method checks
	 * to see if there is a day care absent to do created by the Batch process
	 * per stage
	 * 
	 * @param daycareAbsRewVB
	 * @return Boolean
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/isTodoCreatedForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Boolean isTodoCreatedForStage(DayCareAbsReviewDto daycareAbsRewVB) {
		Boolean isTodoCreated;
		isTodoCreated = dayCareAbsenceReviewDao.isTodoCreatedForStage(daycareAbsRewVB);
		LOG.info(ServiceConstants.TRANSACTION_ID + isTodoCreated);
		return isTodoCreated;
	}
}
