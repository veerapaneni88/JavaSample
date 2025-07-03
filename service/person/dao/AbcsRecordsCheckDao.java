package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.service.recordscheck.dto.AbcsRecordsCheckDto;
import us.tx.state.dfps.service.recordscheck.dto.AddressPersonLinkDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AbcsRecordsCheckDao will all Dao operation to fetch the records
 * from table which are mapped Record check screen. Mar 15, 2018- 2:02:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface AbcsRecordsCheckDao {

	/**
	 * Method Description: This Method will retrieve the resource contract Info
	 * details form record check, resource address tables by passing idRecCheck
	 * as input.
	 * 
	 * @param idRecCheck
	 * @return ResourceContractInfoDto @
	 */
	public ResourceContractInfoDto getResourceContractInfo(Long idRecCheck);

	/**
	 * Method Description: This Method will retrieve the staff contact Info
	 * details form employee and person tables by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto @
	 */
	public EmployeePersonDto getStaffContactInfo(Long idPerson);

	/**
	 * Method Description: This Method will retrieve the employee supervisor
	 * Info details form employee job history and person tables by passing
	 * idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto @
	 */
	public List<EmployeePersonDto> getEmployeeSupervisorInfo(Long idPerson);

	/**
	 * Method Description: This Method will retrieve the staff worker phone Info
	 * details form person phone table by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto @
	 */
	public EmployeePersonDto getEmployeeWorkPhoneInfo(Long idPerson);

	/**
	 * Method Description: This Method will fetches the current primary address
	 * for a person by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return AddressPersonLinkDto @
	 */
	public AddressPersonLinkDto getPersonAddressDtl(Long idPerson);

    AbcsRecordsCheckDto getAbcsRecordsCheckDetails(Long idRecCheck);
}
