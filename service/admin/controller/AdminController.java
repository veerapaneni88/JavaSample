package us.tx.state.dfps.service.admin.controller;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.EmployeeService;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EditEmployeeReq;
import us.tx.state.dfps.service.common.request.ExtUserChildPlanStatusEnum;
import us.tx.state.dfps.service.common.request.ExternalUserAccessCaprReq;
import us.tx.state.dfps.service.common.request.ExternalUserUnitReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.request.RegionCdReq;
import us.tx.state.dfps.service.common.request.SaveUnitReq;
import us.tx.state.dfps.service.common.request.SearchEmployeeByIdReq;
import us.tx.state.dfps.service.common.request.SearchEmployeeReq;
import us.tx.state.dfps.service.common.request.SearchExternalUserReq;
import us.tx.state.dfps.service.common.request.SearchUnitSupervisorReq;
import us.tx.state.dfps.service.common.request.SubscriptionReq;
import us.tx.state.dfps.service.common.request.UnitDetailReq;
import us.tx.state.dfps.service.common.request.UnitListReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EditEmployeeRes;
import us.tx.state.dfps.service.common.response.EmployeeEjbRes;
import us.tx.state.dfps.service.common.response.EmployeeProfileRes;
import us.tx.state.dfps.service.common.response.ExternalUserRes;
import us.tx.state.dfps.service.common.response.SaveUnitRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeByIdRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeRes;
import us.tx.state.dfps.service.common.response.SearchUnitSupervisorRes;
import us.tx.state.dfps.service.common.response.SubscriptionRes;
import us.tx.state.dfps.service.common.response.TodoRes;
import us.tx.state.dfps.service.common.response.UnitDetailRes;
import us.tx.state.dfps.service.common.response.UnitListRes;
import us.tx.state.dfps.service.common.util.JSONUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.NotValidEntityException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dto.UnitDto;
// WTF ompiler an't find it
// import us.tx.state.dfps.common.web.MessagesConstants;


/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * controller for employee, unit related REST service impl. Dec 21, 2017-
 * 4:15:08 PM © 2017 Texas Department of Family and Protective Services
 */
@Api(tags = { "identity" })
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	UnitService unitService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CaseSummaryService caseSummaryService;

	private int BAD_REQUEST = 400;
	private int OK = 2;
	private static final Logger log = Logger.getLogger(AdminController.class);

	/**
	 * Method Description: This method is used to get list of all unit with
	 * giving nbr unit, unit program, unit region and external indicator Service
	 * Name: CCMN24S
	 * 
	 * @param searchUnitListReq
	 * @return unitListRes
	 */
	@RequestMapping(value = "/searchunitlist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public UnitListRes searchUnitListDtls(@RequestBody UnitListReq searchUnitListReq) {
		if (TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("unitlist.unitprogram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitRegion())) {
			throw new InvalidRequestException(
					messageSource.getMessage("unitlist.unitregion.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + searchUnitListReq.getTransactionId());
		return unitService.getUnitList(searchUnitListReq);
	}

	/**
	 * Method Description: This method is used to get the detail of unit by
	 * giving input as id unit to retrieve unit details and id person to check
	 * person has access to view unit detail Service Name: CCMN23S
	 * 
	 * @param unitDetailReq
	 * @return searchUnitDetailRes
	 */
	@RequestMapping(value = "/unitdetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public UnitDetailRes retrieveUnitDetails(@RequestBody UnitDetailReq unitDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(unitDetailReq.getUlIdUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("common.unitid.mandatory", null, Locale.US));
		}
		UnitDetailRes searchUnitDetailRes = new UnitDetailRes();
		searchUnitDetailRes = unitService.getUnitDetail(unitDetailReq);
		log.info("TransactionId :" + unitDetailReq.getTransactionId());
		return searchUnitDetailRes;
	}

	/**
	 * Retrieve a list of staff legacy service name - CCMN03S
	 * 
	 * @param searchEmployeeReq
	 * @return
	 */
	@ApiOperation(value = "Get staff details", tags = { "staffSearch" })
	@RequestMapping(value = "/staffsearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SearchEmployeeRes staffSearch(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		log.info("TransactionId :" + searchEmployeeReq.getTransactionId());
		JSONUtil util = new JSONUtil();
		log.debug(util.objectToJsonString(searchEmployeeReq));
		SearchEmployeeRes searchEmployeeRes = employeeService.searchEmployees(searchEmployeeReq);
		log.debug(util.objectToJsonString(searchEmployeeRes));
		return searchEmployeeRes;
	}

	/**
	 * 
	 * Method Description: Search employee by id, Service name - CCMN04S
	 * 
	 * @paramsearchEmployeeByIdreq
	 * @return
	 */
	@RequestMapping(value = "/searchEmployeeById", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SearchEmployeeByIdRes employeeSearchById(@RequestBody SearchEmployeeByIdReq searchEmployeeByIdreq)
			{
		if (TypeConvUtil.isNullOrEmpty(searchEmployeeByIdreq.getIdEmployee())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.idPerson.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + searchEmployeeByIdreq.getTransactionId());
		return employeeService.searchEmployeeById(searchEmployeeByIdreq);
	}

	/**
	 * 
	 * Method Description: Edit employee @param
	 * updateEmployeeReq @return
	 */
	// CCMN05S
	@RequestMapping(value = "/editEmployee", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EditEmployeeRes employeeEdit(@RequestBody EditEmployeeReq editEmployeeReq){
		if (TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
			throw new InvalidRequestException(
					messageSource.getMessage("employee.editAction.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + editEmployeeReq.getTransactionId());
		return employeeService.editEmployee(editEmployeeReq);
	}

	/**
	 * 
	 * Method Name: validateEmployee Method Description: validate employee
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	@RequestMapping(value = "/validateEmployee", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EditEmployeeRes validateEmployee(@RequestBody EditEmployeeReq editEmployeeReq) {
		log.info("TransactionId :" + editEmployeeReq.getTransactionId());
		return employeeService.validateEmployee(editEmployeeReq);
	}

	/**
	 * 
	 * Method Name: validateRoleChange Method Description: validate role change
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	@RequestMapping(value = "/validateRoleChange", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EditEmployeeRes validateRoleChange(@RequestBody EditEmployeeReq editEmployeeReq) {
		log.info("TransactionId :" + editEmployeeReq.getTransactionId());
		return employeeService.validateRoleChange(editEmployeeReq);
	}

	/**
	 * 
	 * Method Name: validateUnitChange Method Description: validate unit change
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	@RequestMapping(value = "/validateUnitChange", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EditEmployeeRes validateUnitChange(@RequestBody EditEmployeeReq editEmployeeReq) {
		log.info("TransactionId :" + editEmployeeReq.getTransactionId());
		return employeeService.validateUnitChange(editEmployeeReq);
	}

	/**
	 * 
	 * @param saveUnitReq
	 * @return
	 */
	// CCMN22S
	@RequestMapping(value = "/saveUnit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SaveUnitRes unitUpdate(@RequestBody SaveUnitReq saveUnitReq) {

		if (TypeConvUtil.isNullOrEmpty(saveUnitReq.getUlIdUnit())&& !saveUnitReq.getReqFuncCd().equals("A")) {
			throw new InvalidRequestException(messageSource.getMessage("unit.idUnit.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + saveUnitReq.getTransactionId());
		return unitService.saveUnit(saveUnitReq);
	}

	// CCMN08S
	/**
	 * 
	 * @param searchUnitSupervisorReq
	 * @return
	 */
	@RequestMapping(value = "/searchUnitSupervisor", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SearchUnitSupervisorRes searchUnitSupervisor(@RequestBody SearchUnitSupervisorReq searchUnitSupervisorReq) {
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getUnitProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitProgram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.nbrUnit.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getIndExternal())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.indExternal.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + searchUnitSupervisorReq.getTransactionId());
		return unitService.searchUnitSupervisor(searchUnitSupervisorReq);
	}

	/**
	 * getLastUpdateDate: Method will fetch the last update date for the passed
	 * Entity Class
	 * 
	 * Service Name - NA (Util Method getCaseCheckoutStatus)
	 * 
	 * @param commonHelperReq
	 * @return Date
	 */
	@RequestMapping(value = "/getLastUpdateDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getLastUpdateDate(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getEntityClass())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.entityClass.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getPrimaryKey())) {
			throw new InvalidRequestException(messageSource.getMessage("common.primaryKey.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getEntityID())) {
			throw new InvalidRequestException(messageSource.getMessage("common.entityID.mandatory", null, Locale.US));
		}
		return caseSummaryService.getLastUpdateDate(commonHelperReq);
	}

	// CARC01S
	/**
	 * Method Name: getEmployeeProfileByLogon Method Description: This method
	 * gets the Employee Security Profile using Employee's Logon Id.
	 * 
	 * @param searchEmployeeReq
	 * @return employeeProfileRes
	 */
	@ApiOperation(value = "Get employee info", tags = { "identity" })
	@RequestMapping(value = "/getEmployeeProfileByLogon", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeProfileRes getEmployeeProfileByLogon(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		if (TypeConvUtil.isNullOrEmpty(searchEmployeeReq.getSearchLogonId())
				&& (ObjectUtils.isEmpty(searchEmployeeReq.getSearchId())
						|| searchEmployeeReq.getSearchId().equals(0l))) {
			throw new InvalidRequestException(messageSource.getMessage("employee.logon.mandatory", null, Locale.US));
		}
		EmployeeProfileRes employeeProfileRes = employeeService
				.getEmployeeProfileByLogon(searchEmployeeReq.getSearchLogonId(), searchEmployeeReq.getSearchId());

		return employeeProfileRes;
	}

	/**
	 * Method Name: updateEmpoyeeLogon Method Description: This method updates
	 * the Employee-Logon value. This is required for impersonisation.
	 * 
	 * @param searchEmployeeReq
	 * @return employeeProfileRes
	 */
	@RequestMapping(value = "/updateEmpoyeeLogon", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeProfileRes updateEmpoyeeLogon(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		if (null == searchEmployeeReq.getSearchLogonId() || null == searchEmployeeReq.getSearchId()) {
			throw new InvalidRequestException(messageSource.getMessage("employee.logon.mandatory", null, Locale.US));
		}
		employeeService.updateEmployeeLogon(searchEmployeeReq.getSearchLogonId(), searchEmployeeReq.getSearchId());
		EmployeeProfileRes employeeProfileRes = new EmployeeProfileRes();
		return employeeProfileRes;
	}

	/**
	 * 
	 * Method Description: Tuxedo Service Name:externalUserUnit EJB Service
	 * 
	 * @param externalUserUnitReq
	 */
	@RequestMapping(value = "/saveExternalUnitMember", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SaveUnitRes saveNewExternalUnitDetail( @RequestBody ExternalUserUnitReq externalUserUnitReq) {

		SaveUnitRes saveUnitRes = new SaveUnitRes();
		if ((externalUserUnitReq.getUlIdUnit() == null)) {
			saveUnitRes.setErrorDto(saveError("unit.idUnit.mandatory",-1));
			saveUnitRes.setActionResult("Error saving unit");
		}
		try {
			validateCriteria(externalUserUnitReq.getUnitDto());
			unitService.saveNewExternalUnitDetail(externalUserUnitReq);
			saveUnitRes.setActionResult("Inserted Successfully");
		} catch (InvalidRequestException | NotValidEntityException | ServiceLayerException ex) {
			saveUnitRes.setErrorDto(saveError(ex.getLocalizedMessage(), ex.getErrorCode().intValue()));
			saveUnitRes.setActionResult("Error saving unit");
		} catch (Exception e) {
			saveUnitRes.setErrorDto(saveError(e.getLocalizedMessage(),-1));
			saveUnitRes.setActionResult("Error saving unit");
		}
		return saveUnitRes;
	}

	private ErrorDto saveError(String message,int errorCode) {

		log.log(Level.ERROR,message);
		ErrorDto errorDto = new ErrorDto();
		errorDto.setErrorCode(errorCode);
		errorDto.setErrorMsg(message);
		return errorDto;
	}

	/**
	 * Returns cd_catchment for id_catchment
	 * 
	 * @param regionCdReq
	 * @return list<catchments
	 */
	@RequestMapping(value = "/fetchCatchmentsForRegion", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<String> fetchCatchmentsForRegion(@RequestBody RegionCdReq regionCdReq) {
		return unitService.fetchCatchmentsForRegion(regionCdReq.getRegionCd());
	}

	protected void validateCriteria(UnitDto unitDto) throws InvalidRequestException{
		if (null == unitDto) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitDto.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(unitDto.getCdUnitProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitProgram.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(unitDto.getCdUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitRegion.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(unitDto.getNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.nbrUnit.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(unitDto.getIndExternal())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.indExternal.mandatory", null, Locale.US));
		}
		if (unitDto.getIdUnitParent() == null) {
			throw new InvalidRequestException(messageSource.getMessage("unit.parent.mandatory", null, Locale.US));
		}
		if (null == unitDto.getIdPerson()) {
			throw new InvalidRequestException(messageSource.getMessage("employee.idPerson.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(unitDto.getCdUnitSpecialization())) {
			throw new InvalidRequestException(
					messageSource.getMessage("unit.specialization.mandatory", null, Locale.US));
		}
	}

	@RequestMapping(value = "/isExternalUnit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes isExternalUnit(@RequestBody SearchUnitSupervisorReq searchUnitSupervisorReq) {
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("unitlist.unitprogram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getUnitRegion())) {
			throw new InvalidRequestException(
					messageSource.getMessage("unitlist.unitregion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchUnitSupervisorReq.getNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.nbrUnit.mandatory", null, Locale.US));
		}
		return unitService.isExteralUnit(searchUnitSupervisorReq.getUnitProgram(),
				searchUnitSupervisorReq.getUnitRegion(), searchUnitSupervisorReq.getNbrUnit());
	}

	/**
	 * Gets the employee by class.
	 *
	 * @param searchEmployeeReq
	 *            the search employee req
	 * @return the employee by class
	 */
	@RequestMapping(value = "/getEmployeeByClass", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoRes getEmployeeByClass(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		TodoRes todoRes = new TodoRes();
		todoRes.setEmployeeList(employeeService.getEmployeeByClass(searchEmployeeReq.getEmployeeClass(),
				searchEmployeeReq.getSecurityClass()));
		return todoRes;
	}

	/**
	 * 
	 * Method Name: isExternalStaff Method Description: Check if the person is
	 * external staff
	 * 
	 * @param personDtlReq
	 * @return EmployeeEjbRes
	 */
	@RequestMapping(value = "/isExternalStaff", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeEjbRes isExternalStaff(@RequestBody PersonDtlReq personDtlReq) {
		EmployeeEjbRes employeeEjbRes = new EmployeeEjbRes();
		employeeEjbRes.setExternalStaff(employeeService.isExternalStaff(personDtlReq.getIdPerson()));
		return employeeEjbRes;
	}

	/**
	 * 
	 * Method Name: isExternalStaff Method Description: Check if the person is
	 * external staff
	 * 
	 * @param searchEmployeeReq
	 * @return EmployeeEjbRes
	 */
	@RequestMapping(value = "/getLocalPlacementSupervisor", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeProfileRes getLocalPlacementSupervisor(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		EmployeeProfileRes employeeRes = new EmployeeProfileRes();
		employeeRes.setEmployeePersonDto(
				employeeService.getLocalPlacementSupervisor(searchEmployeeReq.getSearchUnitRegion()));
		return employeeRes;
	}

	/**
	 * 
	 * Method Name: searchExternalUser Method Description: get external user
	 * information
	 * 
	 * @param searchExternalUserReq
	 * @return ExternalUserRes
	 */
	@RequestMapping(value = "/searchexternaluser", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ExternalUserRes searchExternalUser(@RequestBody SearchExternalUserReq searchExternalUserReq) {
		if (TypeConvUtil.isNullOrEmpty(searchExternalUserReq.getIdExtUser())) {
			throw new InvalidRequestException(messageSource.getMessage("external.iduser.mandatory", null, Locale.US));
		}
		ExternalUserRes externalUserRes = new ExternalUserRes();
		externalUserRes.setExternalUserDto(employeeService.searchExternalUser(searchExternalUserReq.getIdExtUser()));
		return externalUserRes;
	}

	/**
	 * 
	 * Method Name: externalUserAccessToChildAPR Method Description: check for
	 * external user access to child active placement resources
	 * 
	 * @param externalUserAccessCaprReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/externalUserAccessToChildAPR", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes externalUserAccessToChildAPR(
			@RequestBody ExternalUserAccessCaprReq externalUserAccessCaprReq) {

		Long idPerson = externalUserAccessCaprReq.getIdPerson();
		Long idExtUserLoggedIn = externalUserAccessCaprReq.getIdExtUserLoggedIn();

		if (TypeConvUtil.isNullOrEmpty(idPerson)) {
			throw new InvalidRequestException(messageSource.getMessage("external.iduser.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(idExtUserLoggedIn)) {
			throw new InvalidRequestException(
					messageSource.getMessage("external.idrccploggeduser.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();

		Boolean accessTochildAPR = employeeService.externalUserAccessToChildAPR(idPerson, idExtUserLoggedIn);
		commonHelperRes.setExternalUserAccessTochildAPR(accessTochildAPR);

		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: externalUserBGCheck Method Description: check for external
	 * user BG check
	 * 
	 * @param externalUserAccessCaprReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/externalUserBGCheck", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes externalUserBGCheck(@RequestBody ExternalUserAccessCaprReq externalUserAccessCaprReq) {

		Long idExtUserLoggedIn = externalUserAccessCaprReq.getIdExtUserLoggedIn();

		if (TypeConvUtil.isNullOrEmpty(idExtUserLoggedIn)) {
			throw new InvalidRequestException(
					messageSource.getMessage("external.idrccploggeduser.mandatory", null, Locale.US));
		}

		boolean extUserBGCheck = employeeService.externalUserBackGroundCheck(idExtUserLoggedIn);
		CommonHelperRes commonHelperRes = new CommonHelperRes();

		commonHelperRes.setExternalUserBGCheck(extUserBGCheck);

		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: externalUserAccessToChildAPR Method Description: check for
	 * external user access to child active placement resources
	 * 
	 * @param externalUserAccessCaprReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/externalUserAccessToChildPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ExternalUserRes validateExtUserAccessToChildPlan(
			@RequestBody ExternalUserAccessCaprReq externalUserAccessCaprReq) {

		Long idPerson = externalUserAccessCaprReq.getIdPerson();
		Long idExtUserLoggedIn = externalUserAccessCaprReq.getIdExtUserLoggedIn();

		if (TypeConvUtil.isNullOrEmpty(idPerson)) {
			throw new InvalidRequestException(messageSource.getMessage("external.iduser.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(idExtUserLoggedIn)) {
			throw new InvalidRequestException(
					messageSource.getMessage("external.idrccploggeduser.mandatory", null, Locale.US));
		}

		ExtUserChildPlanStatusEnum extUserChildPlanStatusEnum = employeeService
				.validateExtUserAccessToChildPlan(idPerson, idExtUserLoggedIn);
		ExternalUserRes externalUserRes = new ExternalUserRes();

		externalUserRes.setExtUserChildPlanStatusEnum(extUserChildPlanStatusEnum);

		return externalUserRes;
	}

	/**
	 * 
	 * Method Name: isExternalStaff Method Description: Check if the person is
	 * external staff
	 * 
	 * @param searchUnitSupervisorReq
	 * @return EmployeeEjbRes
	 */
	@RequestMapping(value = "/getLPSList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SearchUnitSupervisorRes getLocalPlacementSupervisorList(
			@RequestBody SearchUnitSupervisorReq searchUnitSupervisorReq) {
		SearchUnitSupervisorRes searchUnitSupervisorRes = new SearchUnitSupervisorRes();
		searchUnitSupervisorRes.setAssignBean(employeeService.getLocalPlacementSupervisorList(
				searchUnitSupervisorReq.getUnitRegion(), searchUnitSupervisorReq.getAssignBean()));
		return searchUnitSupervisorRes;
	}
	
	/**
	 * 
	 * gets Employee Security Profiles by Logon Id
	 * 
	 * @param searchEmployeeReq
	 * @return EmployeeProfileRes
	 */
	@RequestMapping(value = "/getEmployeeSecLinkByLogonId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeProfileRes getEmployeeSecLinkByLogonId(@RequestBody SearchEmployeeReq searchEmployeeReq) {
		if (TypeConvUtil.isNullOrEmpty(searchEmployeeReq.getSearchLogonId())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.logon.mandatory", null, Locale.US));
		}
		EmployeeProfileRes employeeProfileRes = employeeService.getEmployeeSecLinkByLogonId(searchEmployeeReq.getSearchLogonId());

		return employeeProfileRes;
	}

	@RequestMapping(value = "/subscriptions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SubscriptionRes createSubscriptions(@RequestBody SubscriptionReq subscriptionReq) {

		if (TypeConvUtil.isNullOrEmpty(subscriptionReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.personId.mandatory", null, Locale.US));
		}
		return employeeService.createSubscriptions(subscriptionReq);
	}

	@RequestMapping(value = "/delete-subscriptions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SubscriptionRes deleteSubscriptions(@RequestBody SubscriptionReq subscriptionReq) {

		if (TypeConvUtil.isNullOrEmpty(subscriptionReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.personId.mandatory", null, Locale.US));
		}
		return employeeService.deleteSubscriptions(subscriptionReq);
	}

	@RequestMapping(value = "/get-subscriptions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SubscriptionRes getSubscriptions(@RequestBody SubscriptionReq subscriptionReq) {

		if (TypeConvUtil.isNullOrEmpty(subscriptionReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.personId.mandatory", null, Locale.US));
		}
		return employeeService.getSubscriptions(subscriptionReq);
	}

	@RequestMapping(value = "/update-subscriptions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SubscriptionRes updateSubscriptions(@RequestBody SubscriptionReq subscriptionReq) {

		if (TypeConvUtil.isNullOrEmpty(subscriptionReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("employee.personId.mandatory", null, Locale.US));
		}
		return employeeService.updateSubscriptions(subscriptionReq);
	}
}
