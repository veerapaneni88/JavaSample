package us.tx.state.dfps.service.security.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EmployeeTempAssignDto;
import us.tx.state.dfps.service.admin.dto.StaffSecurityRtrvoDto;
import us.tx.state.dfps.service.common.request.StaffSecurityMaintenanceReq;
import us.tx.state.dfps.service.common.response.StaffSecurityMaintenanceRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * StaffSecurityMaintenaceService class will be used to interact with staff
 * security maintenance dao layer Sept 25, 2018- 10:57:26 AM © 2018 Texas
 * Department of Family and Protective Services
 */
public interface StaffSecurityMaintenaceService {

	/**
	 * 
	 * Method Name: staffSecurityRtrvService Method Description:This method will
	 * retrieve staff security maintenance details for selected staff
	 * 
	 * @param StaffSecurityRtrviDto
	 * @return StaffSecurityRtrvoDto
	 */
	public StaffSecurityRtrvoDto staffSecurityRtrvService(Long idPerson, Boolean externalUser);

	/**
	 * 
	 * Method Name: staffSecurityAudService Method Description: This method will
	 * save staff security maintenance details for selected staff
	 * 
	 * @param StaffSecurityRtrviDto
	 * @return StaffSecurityRtrvoDto
	 */
	public StaffSecurityMaintenanceRes staffSecurityAudService(StaffSecurityMaintenanceReq staffSecurityMaintenanceReq);

	/**
	 * Method Name: getStaffSecurityDesigneeDtls Method Description: This
	 * service retrieves all designees for a given employee. It performs a full
	 * row retrieval of the EMP_TEMP_ASSIGN table for a given ID PERSON.
	 * 
	 * @param idPerson
	 * @return List<EmployeeTempAssignDto>
	 */
	public List<EmployeeTempAssignDto> getStaffSecurityDesigneeDtls(Long idPerson);

}
