package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdIndEmpNewInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for updates the employee indicator. Aug 7, 2017- 3:42:30 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface StagePersonLinkUpdIndEmpNewDao {

	/**
	 * 
	 * Method Name: updateEmployeeIndicator Method Description:update emp
	 * indicator in stage person link
	 * 
	 * @param stagePersonLinkUpdIndEmpNewInDto
	 * @
	 */
	public void updateEmployeeIndicator(StagePersonLinkUpdIndEmpNewInDto stagePersonLinkUpdIndEmpNewInDto);
}
