package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN05S Class
 * Description: Operations for EmployeeSecClassLink Apr 14, 2017 - 10:22:20 AM
 */
public interface EmpSecClassLinkDao {

	/**
	 * 
	 * Method Description:getEmpSecClassLinkByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public List<EmpSecClassLink> getEmpSecClassLinkByPersonId(Long personId);

	/**
	 * 
	 * Method Description:deleteEmpSecClassLink
	 * 
	 * @param escl
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmpSecClassLink(EmpSecClassLink escl);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmpSecClassLinkByPersonId(Long personId);

	/**
	 * 
	 * Method Description:getEmpSecClassLinkByPersonId - Get the Employee
	 * Security Class based on Id.
	 * 
	 * @param personId
	 * @return empSecClassLinkList
	 * @throws DataNotFoundException
	 * @
	 */
	// CLSCB4D
	public List<EmpSecClassLink> getEmployeeSecurityProfile(Long personId);

	/**
	 * Method Name: deleteExternalUserSecClassLinkByPersonId Method
	 * Description:getEmpSecClassLinkByPersonId - delete external user security
	 * class based on userid.
	 * 
	 * @param personId
	 */
	void deleteExternalUserSecClassLinkByPersonId(Long personId);

	/**
	 * 
	 * gets the employee security profile by logon id
	 * @param logonId
	 * @return
	 */
	public List<String> getEmployeeSecurityProfileByLogonId(String logonId);
}
