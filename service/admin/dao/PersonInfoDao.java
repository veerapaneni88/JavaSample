package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.admin.dto.PersonDoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonEmployeeInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonEmployeeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao
 * gets an employee's supervisor name and ID. Aug 10, 2017- 3:04:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PersonInfoDao {
	/**
	 * 
	 * Method Name: getPersonName Method Description:
	 * 
	 * @param personDiDto
	 * @return List<PersonDoDto>
	 */
	public List<PersonDoDto> getPersonName(PersonDiDto personDiDto);

	/**
	 * 
	 * Method Name: getSupervisor Method Description: This DO gets an employee's
	 * supervisor name and ID.
	 * 
	 * @param personEmployeeInDto
	 * @return PersonEmployeeOutDto
	 * @throws DataNotFoundException
	 */
	public List<PersonEmployeeOutDto> getSupervisor(PersonEmployeeInDto personEmployeeInDto)
			throws DataNotFoundException;

	public EmployeeDto getSelectEmployee(Long personId);
}
