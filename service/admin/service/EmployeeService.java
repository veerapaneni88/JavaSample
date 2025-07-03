package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.common.domain.EmpJobHistory;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.EmployeeSkill;
import us.tx.state.dfps.common.domain.ExtrnlUser;
import us.tx.state.dfps.common.domain.UnitEmpLink;
import us.tx.state.dfps.service.admin.dto.EmpJobHisDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonIdDto;
import us.tx.state.dfps.service.admin.dto.EmpSkillDto;
import us.tx.state.dfps.service.admin.dto.EmpUnitDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSearchByIdDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.request.EditEmployeeReq;
import us.tx.state.dfps.service.common.request.ExtUserChildPlanStatusEnum;
import us.tx.state.dfps.service.common.request.SearchEmployeeByIdReq;
import us.tx.state.dfps.service.common.request.SearchEmployeeReq;
import us.tx.state.dfps.service.common.request.SubscriptionReq;
import us.tx.state.dfps.service.common.response.EditEmployeeRes;
import us.tx.state.dfps.service.common.response.EmployeeProfileRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeByIdRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeRes;
import us.tx.state.dfps.service.common.response.SubscriptionRes;
import us.tx.state.dfps.service.externaluser.dto.ExternalUserDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.workload.bean.AssignBean;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CARC01S Class
 * Description: Apr 12, 2017 - 1:32:43 PM
 */
public interface EmployeeService {
	public SearchEmployeeRes searchEmployees(SearchEmployeeReq searchEmployeeReq);

	/**
	 * 
	 * Method Description:searchEmployeeById
	 * 
	 * @param searchEmployeeByIdReq
	 * @return
	 */
	// CCMN04S
	public SearchEmployeeByIdRes searchEmployeeById(SearchEmployeeByIdReq searchEmployeeByIdReq);

	/**
	 * 
	 * Method Description:getEmployeeSearchByIdDto
	 * 
	 * @param employeeDetailDto
	 * @return
	 */
	// CCMN04S, CCMN03S
	public EmployeeSearchByIdDto getEmployeeSearchByIdDto(EmployeeDetailDto employeeDetailDto);

	/**
	 * 
	 * Method Description:editEmployee
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	// CCMN05S
	public EditEmployeeRes editEmployee(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:getEmployeeDetailDto
	 * 
	 * @param employee
	 * @return
	 */
	// CCMN04S
	public EmployeeDetailDto getEmployeeDetailDto(Employee employee);

	/**
	 * 
	 * Method Description:getEmpUnitDto
	 * 
	 * @param uel
	 * @return
	 */
	// CCMN05S
	public EmpUnitDto getEmpUnitDto(UnitEmpLink uel);

	/**
	 * 
	 * Method Description:getEmployeePersonDto
	 * 
	 * @param person
	 * @return
	 */
	// CCMN04S
	public EmpPersonDto getEmployeePersonDto(PersonDto personDto);

	/**
	 * 
	 * Method Description:getEmpJobHisDto
	 * 
	 * @param ejh
	 * @return
	 */
	// CCMN04S
	public EmpJobHisDto getEmpJobHisDto(EmpJobHistory ejh);

	/**
	 * 
	 * Method Description:getEmpSkillDto
	 * 
	 * @param employeeSkill
	 * @return
	 */
	// CCMN04S
	public EmpSkillDto getEmpSkillDto(EmployeeSkill employeeSkill);

	/**
	 * 
	 * Method Description:deleteEmployee
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	// CCMN05S
	public EditEmployeeRes deleteEmployee(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:addEmployee
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	// CCMN05S
	public EditEmployeeRes addEmployee(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:updateEmployee
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	// CCMN05S
	public EditEmployeeRes updateEmployee(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:SaveUpdateEmpPersonDto
	 * 
	 * @param empPersonDto
	 * @param action
	 * @return
	 */
	// CCMN71D
	public EmpPersonDto saveUpdateEmpPersonDto(EmpPersonDto empPersonDto, String action);;

	/**
	 * 
	 * Method Description:SaveUpdateEmpJobHisDto
	 * 
	 * @param empJobHisDto
	 * @param action
	 * @return
	 */
	// CCMN78D
	public EmpJobHisDto saveUpdateEmpJobHisDto(EmpJobHisDto empJobHisDto, String action);

	/**
	 * 
	 * Method Description:SaveUpdateEmployeeDetailDto
	 * 
	 * @param employeeDetailDto
	 * @param empNameDto
	 * @param action
	 * @return
	 */
	// CCMN70D
	//ALM ID : 11036 : receive employee name fields in EmpNameDto
	public EmployeeDetailDto saveUpdateEmployeeDetailDto(EmployeeDetailDto employeeDetailDto, EmpNameDto empNameDto, String action);

	/**
	 * 
	 * Method Description:SaveEmpPersonIdDto
	 * 
	 * @param empPersonIdDto
	 * @param action
	 * @return
	 */
	// CCMN73D
	public EmpPersonIdDto saveEmpPersonIdDto(EmpPersonIdDto empPersonIdDto, String action);

	/**
	 * 
	 * Method Description:SaveEmployeeProcedure
	 * 
	 * @param editEmployeeReq
	 */
	// CCMN05S
	public EditEmployeeRes saveEmployeeProcedure(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:UpdateEmployeeProcedure
	 * 
	 * @param editEmployeeReq
	 */
	// CCMN05S
	public EditEmployeeRes updateEmployeeProcedure(EditEmployeeReq editEmployeeReq);

	/**
	 * 
	 * Method Description:editEmpUnitDto
	 * 
	 * @param empUnitDto
	 * @param action
	 * @return
	 */
	// CCMN49D
	public EmpUnitDto editEmpUnitDto(EmpUnitDto empUnitDto, String action);

	/**
	 * 
	 * Method Description:SaveDeleteEmpSkillDto
	 * 
	 * @param empSkillDto
	 * @param action
	 * @return
	 */
	// CCMN98D
	public EmpSkillDto saveDeleteEmpSkillDto(EmpSkillDto empSkillDto, String action);

	/**
	 * 
	 * Method Description:SaveUpdateEmpNameDto
	 * 
	 * @param empNameDto
	 * @param action
	 * @return
	 */
	// CCMNA0D
	public EmpNameDto saveUpdateEmpNameDto(EmpNameDto empNameDto, String action);

	/**
	 * 
	 * Method Description:editPersonRaceEthnicityDto
	 * 
	 * @param personRaceDto
	 * @param personEthnicityDto
	 * @param action
	 */
	// CAUDD5D
	public void editPersonRaceEthnicityDto(PersonRaceDto personRaceDto, PersonEthnicityDto personEthnicityDto,
			String action);

	/**
	 * 
	 * Method Description:updateCaseName
	 * 
	 * @param empNameDto
	 */
	// CCMNH4D
	public void updateCaseName(String caseName, Long ulIdPerson, String nmPersonFull);

	/**
	 * 
	 * Method Description:updateStageName
	 * 
	 * @param empNameDto
	 */
	// CCMNH5D
	public void updateStageName(String caseName, Long ulIdPerson, String nmPersonFull);

	/**
	 * 
	 * Method Description:editPersonCategory
	 * 
	 * @param empDetailDto
	 * @param action
	 */
	// CCMNC2D
	public void editPersonCategory(EmployeeDetailDto empDetailDto, String action);

	/**
	 * 
	 * Method Description:getEmployeeProfileByLogon - This method retrieves the
	 * impersonated employee's security profile based on logged-in user's login
	 * id.
	 * 
	 * @param employeeLogon
	 * @return employeeProfileDto
	 */
	// CARC01D,CLSCB4D,CARC06D
	public EmployeeProfileRes getEmployeeProfileByLogon(String employeeLogon, Long idUser);

	/**
	 * 
	 * Method Description: This method updates the ID_EMPLOYEE_LOGON column
	 * 
	 * @param employeeLogonId
	 * @return
	 */
	public void updateEmployeeLogon(String employeeLogonId, Long personId);

	/**
	 * 
	 * Method Name: validateEmployee Method Description: This method validate
	 * employee form
	 * 
	 * @param editEmployeeReq
	 * @return
	 */
	public EditEmployeeRes validateEmployee(EditEmployeeReq editEmployeeReq);

	public EditEmployeeRes validateUnitChange(EditEmployeeReq editEmployeeReq);

	public EditEmployeeRes validateRoleChange(EditEmployeeReq editEmployeeReq);

	/**
	 * Gets the employee by class.
	 *
	 * @param employeeClass
	 *            the employee class
	 * @param securityClass
	 *            the security class
	 * @return the employee by class
	 */
	public List<EmployeePersonDto> getEmployeeByClass(String employeeClass, String securityClass);

	/**
	 * 
	 * Method Name: isExternalStaff Method Description: Check is the person is
	 * external staff
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean isExternalStaff(Long idPerson);

	/**
	 * 
	 * Method Name: getLocalPlacementSupervisor Method Description:Get LPS
	 * Supervisor for the region
	 * 
	 * @param cdRegion
	 * @return
	 */
	public EmployeePersonDto getLocalPlacementSupervisor(String cdRegion);

	/**
	 * Method Name: saveUpdateExtEmployeeDetailDto Method Description:To save
	 * and update the extrnl_employee table
	 * 
	 * @param employeeDetailDto
	 * @param action
	 * @return EmployeeDetailDto
	 */
	EmployeeDetailDto saveUpdateExtEmployeeDetailDto(EmpNameDto empNameDto, EmployeeDetailDto employeeDetailDto,
			String action);

	/**
	 * Method Name: getExternalEmployeeDetailDto Method Description:
	 * 
	 * @param employee
	 * @return
	 */
	EmployeeDetailDto getExternalEmployeeDetailDto(ExtrnlUser extrnlEmployee);

	/**
	 * 
	 * Method Name: searchExternalUser Method Description:get the external user
	 * details based on idExtUser
	 * 
	 * @param idExtUser
	 * @return ExternalUserDto
	 */
	ExternalUserDto searchExternalUser(Long idExtUser);

	/**
	 * 
	 * Method Name: externalUserAccessToChildAPR Method Description: check for
	 * external user access to child active placement resources
	 * 
	 * @param idPerson
	 * @param rccpLoggedInUserId
	 * @return Boolean
	 */
	Boolean externalUserAccessToChildAPR(Long idPerson, Long rccpLoggedInUserId);

	/**
	 * Method Name: validateExtUserAccessToChildPlan Method Description:
	 * validate for the ext user is active and has access to child plan
	 * 
	 * @param idchild
	 * @param rccpLoggedInUserId
	 * @return ExtLoggedUserChildPlanStatusEnum
	 */
	ExtUserChildPlanStatusEnum validateExtUserAccessToChildPlan(Long idchild, Long rccpLoggedInUserId);

	/**
	 * Method Name: externalUserBackGroundCheck Method Description:external user
	 * back ground check for logon id
	 * 
	 * @param idLogon
	 * @return boolean
	 */
	boolean externalUserBackGroundCheck(long idLogon);

	/**
	 * Gets the local placement supervisor list.
	 *
	 * @param searchUnitRegion
	 *            the search unit region
	 * @param assignBean
	 * @return the local placement supervisor list
	 */
	public AssignBean getLocalPlacementSupervisorList(String searchUnitRegion, AssignBean assignBean);

	/**
	 * Gets the Employee Security Profile by Logon Id
	 * 
	 * @param employeeLogon
	 * @return
	 */
	public EmployeeProfileRes getEmployeeSecLinkByLogonId(String employeeLogonId);

	public SubscriptionRes createSubscriptions(SubscriptionReq subscriptionReq);

	public SubscriptionRes deleteSubscriptions(SubscriptionReq subscriptionReq);

	public SubscriptionRes getSubscriptions(SubscriptionReq subscriptionReq);

	public SubscriptionRes updateSubscriptions(SubscriptionReq subscriptionReq);
}
