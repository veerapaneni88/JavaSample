package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneInDto;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is used
 * to retrieve Details on call Aug 4, 2017- 2:31:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface EmpOnCallLinkPersonPhoneDao {

	/**
	 * Method Name: getEmployeeOnCallList Method Description:This method
	 * retrieves data from EMP_ON_CALL_LINK,PERSON and PERSON_PHONE tables.
	 * 
	 * @param empOnCallLinkPersonPhoneInDto
	 * @return List<EmpOnCallLinkPersonPhoneOutDto>
	 */
	public List<EmpOnCallLinkPersonPhoneOutDto> getEmployeeOnCallList(
			EmpOnCallLinkPersonPhoneInDto empOnCallLinkPersonPhoneInDto);
}
