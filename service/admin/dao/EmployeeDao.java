package us.tx.state.dfps.service.admin.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import us.tx.state.dfps.common.domain.EmpJobHistory;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.EmployeeSkill;
import us.tx.state.dfps.common.domain.ExtrnlUser;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.EmpJobHisDto;
import us.tx.state.dfps.service.admin.dto.EmpSkillDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSearchDto;
import us.tx.state.dfps.service.admin.dto.SSCCCatchmentDto;
import us.tx.state.dfps.service.common.request.SearchEmployeeReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.externaluser.dto.ExternalUserDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.web.workload.bean.AssignBean;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 12, 2017 - 1:59:28 PM
 */
public interface EmployeeDao {
	/**
	 * 
	 * Method Description:legacy service name - CCMN50D
	 * 
	 * @param searchEmployeeReq
	 * @return @
	 */
	public HashMap<Long, EmployeeSearchDto> searchEmpbyInput(SearchEmployeeReq searchEmployeeReq);

	/**
	 * 
	 * Method Description:legacy service name - CCMNF9D
	 * 
	 * @param searchEmployeeReq
	 * @return @
	 */
	public HashMap<Long, EmployeeSearchDto> searchEmpbystageList(SearchEmployeeReq searchEmployeeReq);

	public Long EmpbyInputRecCount(SearchEmployeeReq searchEmployeeReq);

	public Long EmpbyStageListRecCount(SearchEmployeeReq searchEmployeeReq);

	/**
	 * 
	 * Method Description:getEmployeeById
	 * 
	 * @param employeeId
	 * @return
	 */
	// CCMN04S
	public EmployeeDetailDto getEmployeeById(Long employeeId);

	/**
	 * Method Description: getEmployeeEntityById
	 *
	 * @param employeeId
	 * @return
	 */
	// CCMN05S
	public Employee getEmployeeEntityById(Long employeeId);

	/**
	 * 
	 * Method Description:getEmployeeByIdAndLastUpdate
	 * 
	 * @param employeeId
	 * @param date
	 * @return
	 */
	// CCMN70D
	public Employee getEmployeeByIdAndLastUpdate(Long employeeId, Date date);

	/**
	 * 
	 * Method Description:getEmployeeByIdEmpUnit
	 * 
	 * @param idEmpUnit
	 * @return
	 */
	// CCMN03S
	public List<Employee> getEmployeeByIdEmpUnit(Long idEmpUnit);

	/**
	 * 
	 * Method Description:getEmployeeByIdOffice
	 * 
	 * @param idOffice
	 * @return
	 */
	// CCMN03S
	public List<Employee> getEmployeeByIdOffice(Long idOffice);

	/**
	 * 
	 * Method Description:searchEmploeeSkill
	 * 
	 * @param searchSkill
	 * @return
	 */

	// CCMN03S
	public List<Long> searchEmploeeSkill(String searchSkill);

	/**
	 * 
	 * Method Description:searchEmployeeByMailCode
	 * 
	 * @param mailCode
	 * @return
	 */
	// CCMN03S
	public List<Employee> searchEmployeeByMailCode(String mailCode);

	/**
	 * 
	 * Method Description:updateEmployee
	 * 
	 * @param employee
	 */
	// CCMN05S
	public void updateEmployee(Employee employee);

	/**
	 * 
	 * Method Description:updateEmployeeSkill
	 * 
	 * @param employeeSkill
	 */
	// CCMN05S
	public void updateEmployeeSkill(EmployeeSkill employeeSkill);

	/**
	 * 
	 * Method Description:getEmployeeSkillById
	 * 
	 * @param employeeSkillId
	 * @return
	 */
	// CCMN05S
	public EmployeeSkill getEmployeeSkillById(Long employeeSkillId);

	/**
	 * 
	 * Method Description:getEmployeeSkillByIdSkill
	 * 
	 * @param personId
	 * @param skill
	 * @return
	 */
	// CCMN98D
	public EmployeeSkill getEmployeeSkillByIdSkill(Long personId, String skill);

	/**
	 * 
	 * Method Description:getEmployeeSkillByPersonId
	 * 
	 * @param personId
	 * @return
	 */
	// CCMN05S
	public List<EmpSkillDto> getEmployeeSkillByPersonId(Long personId);

	/**
	 * 
	 * Method Description:getEmpJobHistoryByPersonId
	 * 
	 * @param personId
	 * @return
	 */
	// CCMN05S
	public List<EmpJobHisDto> getEmpJobHistoryByPersonId(Long personId);

	/**
	 * 
	 * Method Description:getEmpJobHistoryById
	 * 
	 * @param empJobHistoryId
	 * @return
	 */
	// CCMN05S
	public EmpJobHistory getEmpJobHistoryById(Long empJobHistoryId);

	/**
	 * 
	 * Method Description:getEmpJobHistoryByIdAndLastUpdate
	 * 
	 * @param empJobHistoryId
	 * @param date
	 * @return
	 */
	// CCMN05S
	public EmpJobHistory getEmpJobHistoryByIdAndLastUpdate(Long empJobHistoryId, Date date);

	/**
	 * 
	 * Method Description:updateEmpJobHistory
	 * 
	 * @param empJobHistory
	 */
	// CCMN05S
	public void updateEmpJobHistory(EmpJobHistory empJobHistory);

	/**
	 * 
	 * Method Description:saveEmpJobHistory
	 * 
	 * @param empJobHistory
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void saveEmpJobHistory(EmpJobHistory empJobHistory);

	/**
	 * 
	 * Method Description:saveEmployeeSkill
	 * 
	 * @param employeeSkill
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void saveEmployeeSkill(EmployeeSkill employeeSkill);

	/**
	 * 
	 * Method Description:saveEmployee
	 * 
	 * @param employee
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void saveEmployee(Employee employee);

	/**
	 * 
	 * Method Description:deleteEmpJobHistory
	 * 
	 * @param empJobHistory
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmpJobHistory(EmpJobHistory empJobHistory);

	/**
	 * 
	 * Method Description:deleteEmployeeSkill
	 * 
	 * @param employeeSkill
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmployeeSkill(EmployeeSkill employeeSkill);

	/**
	 * 
	 * Method Description:deleteEmployee
	 * 
	 * @param employee
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmployee(Employee employee);

	/**
	 * 
	 * Method Description:searchCurrentSupervisorByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN03S
	public Person searchCurrentSupervisorByPersonId(Long personId);

	/**
	 * 
	 * Method Description:searchCurrentJobByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN03S
	public EmpJobHistory searchCurrentJobByPersonId(Long personId);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMNF7D
	public void complexDeletePerson(Long personId);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */

	// CCMNF5D
	public void complexDeleteEmployee(Long personId);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmployeeJobHistoryByPersonId(Long personId);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmployeeHistoryByPersonId(Long personId);

	/**
	 * deleteApprovalByPersonId
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteApprovalByPersonId(Long personId);

	/**
	 * 
	 * Method Description: Method is implemented in StagePersonLinkDaoImpl to
	 * perform AUD operations Service Name: CCMN25S Dam Name : CCMN81D
	 * 
	 * @param idPerson
	 * @
	 */

	public String getEmployeeUpdate(Long idPerson, ServiceReqHeaderDto serviceReqHeaderDto);

	/**
	 * 
	 * Method Description: This Method will retrieve the active primary address,
	 * phone number, and name for an employee Dam Name : CSEC01D
	 * 
	 * @param idPerson
	 * @return PersonPhoneNameDto @
	 */
	public EmployeePersPhNameDto searchPersonPhoneName(Long idPerson);

	/**
	 * 
	 * Method Description: Method gets Employee Details using Employee LogonId -
	 * Service Name: CARC01S Dam Name : CARC01D
	 * 
	 * @param employeeLogon
	 * @return employee
	 * @ @throws
	 *       DataNotFoundException
	 */
	// CARC01D
	public EmployeeDto getEmployeeByLogonId(String employeeLogon);

	/**
	 * 
	 * Method Description: This method updates the ID_EMPLOYEE_LOGON column
	 * 
	 * @param employeeLogonId
	 * @return
	 * @ @throws
	 *       DataNotFoundException
	 * 
	 */
	public void updateEmployeeLogon(String employeeLogonId, Long personId);

	/**
	 * Method Description: This method get the PlusCF1050BSecAttr Enabled
	 * 
	 * @param personId
	 */
	public boolean getSecAttrEnabled(Long personId);

	/**
	 * Method Description : retrieve employee details by user ID passed in
	 * 
	 * @param idUser
	 * @return
	 * @throws DataNotFoundException
	 */
	public EmployeeDto getEmployeeByIdUser(Long idUser);

	/**
	 * Method Name: getEmployeeOfficeIdentifier Method Description:returns the
	 * employee office id based on a staff id
	 * 
	 * @param idEmployee
	 * @return Long
	 */
	public Long getEmployeeOfficeIdentifier(Long idEmployee);

	/**
	 * Gets the employee by class. UIDS 2.3.3.5 - Remove a child from home -
	 * To-Do Detail
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
	public Employee getLocalPlacementSupervisor(String cdRegion);

	/**
	 * 
	 * Method Name: getEmployeeByEmployeeId Method Description: Get Employee by
	 * employee person Id
	 * 
	 * @param idEmp
	 * @return
	 */
	public Employee getEmployeeByEmployeeId(Long idEmp);

	/**
	 * Method Name:getExternalUserById Method Description:Fetch the external
	 * user information from extrnl_employee table
	 * 
	 * @param employeeId
	 * @return employeeDetailDto
	 */
	EmployeeDetailDto getExternalUserById(Long personId);

	/**
	 * Method Name: impactAccessForExternalUser Method Description:impact access
	 * check for external user
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	boolean impactAccessForExternalUser(Long idPerson);

	/**
	 * Method Name: getExternalUserEntityById Method Description:To get external
	 * employee by user id
	 * 
	 * @param employeeId
	 * @return ExtrnlEmployee
	 */
	ExtrnlUser getExternalUserEntityById(Long extUserId);

	/**
	 * Method Name: updateExternalEmployee Method Description:To update external
	 * employee
	 * 
	 * @param ExtrnlEmployee
	 */
	void updateExternalEmployee(ExtrnlUser extrnlEmployee);

	/**
	 * Method Name: externalUserAccessToChildActivePlacement Method Description:
	 * access check for external user to child placement resource
	 * 
	 * @param idExternalUser
	 * @param idChild
	 * @return boolean
	 */
	boolean externalUserAccessToChildActivePlacement(Long idExternalUser, Long idChild);

	/**
	 * Method Name: getExtUserByLogonId Method Description: This method gets
	 * External User Details using extUserId
	 * 
	 * @param extUserId
	 * @return
	 */
	ExternalUserDto getExtUserByLogonId(String extUserId);

	/**
	 * Method Name: externalUserBackGroundCheck Method Description: return true
	 * for external user back ground check passes
	 * 
	 * @param extUserId
	 * @return boolean
	 */
	boolean externalUserBackGroundCheck(long extUserId);

	/**
	 * Method Name: setExtrnlUserLoginUpdateLastDateTime Method Description:This
	 * method will update last login for external user
	 * 
	 * @param idExtrnlUser
	 */
	void setExtrnlUserLoginUpdateLastDateTime(Long idExtrnlUser);

	/**
	 * Gets the local placement supervisor list.
	 *
	 * @param string
	 *            the string
	 * @param assignBean
	 * @return the local placement supervisor list
	 */
	public AssignBean getLocalPlacementSupervisorList(String string, AssignBean assignBean);

	/**
	 * Method Name: getEmployeeEmailAddressList Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	public List<String> getEmployeeEmailAddressList(Long idStage);

	public List<SSCCCatchmentDto> fetchSSCCCatchment(Long personId);

	public String fetchVendorUrl(String region, String catchment);

}
