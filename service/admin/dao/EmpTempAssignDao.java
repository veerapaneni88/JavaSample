package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.EmpTempAssign;
import us.tx.state.dfps.service.admin.dto.EmpTempAssignDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN05S Class
 * Description: CCMNH2D, Operations for EmpTempAssign Apr 14, 2017 - 10:24:03 AM
 */
public interface EmpTempAssignDao {

	/**
	 * 
	 * Method Description:getEmpTemAssignByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S, CCMNH2D
	public List<EmpTempAssign> getEmpTemAssignByPersonId(Long personId);

	/**
	 * 
	 * Method Description:deleteEmpTempAssign
	 * 
	 * @param eta
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public void deleteEmpTempAssign(EmpTempAssign eta);

	/**
	 * 
	 * @param personId
	 * @throws DataNotFoundException
	 * @
	 */

	// CCMN05S
	public void deleteEmpTempAssignByPersonId(Long personId);

	/**
	 * 
	 * Method Description: This method gets temporary assignments for the
	 * employee
	 * 
	 * @param personId
	 * @return empTempAssignList @
	 */
	// CARC06D
	public List<EmpTempAssign> getActiveEmpTempAssignByPersonId(Long personId);

	// CMSC45D
	/**
	 * 
	 * Method Description: This DAM was created for use by service CARC17S and
	 * returns a count of all of an employee's assignees (given ID PERSON
	 ** DESIGNEE) not equal to a given ID PERSON.
	 * 
	 * @param personId
	 * @return empTempAssignList @
	 */
	public Long getCountEmpTempAssignByPersonId(Long idPersonDesignee, Long idPersonEmp);

	// CAUD22D
	/**
	 * 
	 * Method Description: Add/Update/Delete from table EMP_TEMP_ASSIGN
	 * 
	 * @param EmpTempAssignDto
	 * @param reqFuncCd
	 * @return empTempAssignList
	 * 
	 */

	public void updateEmpTempAssign(EmpTempAssignDto empTempAssignDto, String reqFuncCd);
}
