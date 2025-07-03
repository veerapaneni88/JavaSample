package us.tx.state.dfps.service.security.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSecurityClassLinkDto;
import us.tx.state.dfps.service.admin.dto.EmployeeTempAssignDto;
import us.tx.state.dfps.service.admin.dto.SecurityClassInfoDto;
import us.tx.state.dfps.service.admin.dto.StaffSecurityRtrvoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * Service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface retrieves, save and update Staff Security Maintenance Detail Sept
 * 25, 2018- 3:14:35 PM © 2018 Texas Department of Family and Protective
 * Services
 */
public interface StaffSecurityMaintenanceDao {
	/**
	 * 
	 * Method - fetchEmployeeDtl Method Description: This method will perform
	 * full row retrieval on the Employee table, respective dam is cses00d
	 * 
	 * @param staffSecurityRtrvoDto
	 * @param idPerson
	 * @return StaffSecurityRtrvoDto
	 * @throws DataNotFoundException
	 */
	public StaffSecurityRtrvoDto fetchEmployeeDtl(StaffSecurityRtrvoDto staffSecurityRtrvoDto, Long idPerson,
			boolean externalUser);

	/**
	 * 
	 * Method - updateEmpLogonDtl Method Description: This method performs an
	 * update on the Employee table
	 * 
	 * @param EmployeePersonIdInDto
	 * @return EmployeePersonIdOutDto
	 * @throws DataNotFoundException
	 */
	public ErrorDto updateEmpLogonDtl(String reqFuncCd, String idEmployeeLogon, Long idPerson, Date dtLastUpdate,
			boolean externalUser, String idUser);

	/**
	 *
	 * Method - insertDeleteEmpClassLinkDtl Method Description: This method will
	 * add or delete rows on the EMP_SEC_CLASS_LINK table for a selected user.
	 * Which saves Employee listbox data on the Staff Security Window
	 *
	 * @param EmpSecClassLinkInsDelInDto
	 * @return EmpSecClassLinkInsDelOutDto
	 * @throws DataNotFoundException
	 */
	public void insertDeleteEmpClassLinkDtl(String cdDataActionOutcome, Long idEmpSecLink, Long idSecurityPerson,
								String nmSecurityClass, Date dtLastUpdateSecurity, Long idCreatedPerson, Date dtCreated, Long idLastUpdatePerson);



	/**
	 * 
	 * Method - saveUpdateEmpTempAssignDtl Method Description: This method is
	 * used to Add/Update/Delete from table EMP_TEMP_ASSIGN
	 * 
	 * @param EmpTempAssignInsUpdDelInDto
	 * @return EmpTempAssignInsUpdDelOutDto
	 */
	public void saveUpdateEmpTempAssignDtl(String cdDataActionOutcome, Long idEmpTempAssign, Long idEmpPerson,
			Long idPersonDesignee, Date dtAssignExpiration, Date dtLastUpdateEmp);

	/**
	 * 
	 * Method - fetchEmpTempAssignDtl Method Description: This method retrieves
	 * a list of all employees who the employee may act as (up to 5), as well as
	 * the expiration date of the assignment. Respective dam is clss15d.
	 * 
	 * @param PersonEmpTempAssignInDto
	 * @return PersonEmpTempAssignOutDto
	 * @throws DataNotFoundException
	 */
	public List<EmployeeTempAssignDto> fetchEmpTempAssignDtl(Long idPerson);

	/**
	 * Method : fetchStaffSecurityDtl Method description : This Method will
	 * fetch staff security details, respective dam is clss12d
	 * 
	 * @return SecurityClassInfoDto
	 */
	public List<SecurityClassInfoDto> fetchStaffSecurityDtl();

	/**
	 * Method : fetchEmpSecClassLinkDtl Method description : This Method
	 * retrieves employee sec link details, respective dam is clscb3D
	 * 
	 * @param idPerson
	 * @return EmployeeSecurityClassLinkDto
	 */
	public List<EmployeeSecurityClassLinkDto> fetchEmpSecClassLinkDtl(Long idPerson);

}
