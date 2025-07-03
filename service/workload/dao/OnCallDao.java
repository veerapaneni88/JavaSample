package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.EmpOnCallLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.person.dto.RtrvOnCallCntyDto;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkDto;
import us.tx.state.dfps.service.workload.dto.OnCallScheduleDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is provides the method definitions to handle the save/update/delete
 * operations on the ON_CALL,ON_CALL_COUNTY and EMP_ON_CALL_LINK tables. Feb 10,
 * 2018- 9:04:55 PM © 2017 Texas Department of Family and Protective Services
 */
public interface OnCallDao {

	/**
	 * 
	 * Method Description:getCurrentOnCallByPersonId
	 * 
	 * @param personId
	 * @return List<Long>
	 */
	// CCMN05S
	public List<Long> getCurrentOnCallByPersonId(Long personId);

	/**
	 * 
	 * Method Description:getOnCallByPersonId
	 * 
	 * @param personId
	 * @return List<EmpOnCallLink>
	 */
	// CCMN05S
	public List<EmpOnCallLink> getOnCallByPersonId(Long personId);

	// CCMN05S
	/**
	 * Method Name: deleteEmpOnCallLink Method Description:This method is used
	 * to delete record from the EMP_ON_CALL_LINK tables
	 * 
	 * @param empOnCallLink
	 *            - The instance of the domain entity which needs to be deleted
	 *            from the db.
	 */
	public void deleteEmpOnCallLink(EmpOnCallLink empOnCallLink);

	/**
	 * Method Name: addOnCall Method Description:This method is used to create a
	 * new on call schedule.
	 * 
	 * @param onCallDto
	 *            - This dto is used to hold the values related to the on call
	 *            details.
	 * @return OnCallScheduleDetailDto- This dto is used to hold the values of
	 *         the saved on call schedules.
	 */
	public OnCallScheduleDetailDto addOnCall(AddOnCallInDto onCallDto);

	/**
	 * Method Name: updateOnCall Method Description:This method is used to
	 * update the on call details in the ON_CALL and ON_CALL_COUNTY tables.
	 * 
	 * @param onCallDto
	 *            - The dto with the values to be updated in the ON_CALL and
	 *            ON_CALL_COUNTY tables.
	 * @return - The id of the on call which was updated.
	 */
	public Long updateOnCall(AddOnCallInDto onCallDto);

	/**
	 * Method Name: deleteOnCall Method Description:This method is used to
	 * delete a on call schedule.
	 * 
	 * @param onCallDto
	 *            - The dto with the values of the on call which needs to
	 *            deleted.
	 */
	public void deleteOnCall(AddOnCallInDto onCallDto);

	/**
	 * Method Name: addEmpOnCallLink Method Description:This method is used to
	 * insert a record in the EMP_ON_CALL_LINK table
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 * @return
	 */
	public Long addEmpOnCallLink(EmpOnCallLinkDto empOnCallLinkDto);

	/**
	 * Method Name: updateEmpOnCalllink Method Description:This method updates
	 * the EMP_ON_CALL_LINK table.
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 * @return
	 */
	public Long updateEmpOnCalllink(EmpOnCallLinkDto empOnCallLinkDto);

	/**
	 * Method Name: deleteEmpOnCallLink Method Description:This method is used
	 * to delete a record in the EMP_ON_CALL_LINK table.
	 * 
	 * EMP_ON_CALL_LINK table.
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 */
	public void deleteEmpOnCallLink(EmpOnCallLinkDto empOnCallLinkDto);

	/**
	 * Method Name: deleteEmpOnCallLinkByOnCallId Method Description:This method
	 * is used to delete employee from the EMP_ON_CALL_LINK table based on the
	 * on call id.
	 * 
	 * @param idOncall
	 *            - The value of the on call for which the employee/employees
	 *            has to be deleted.
	 */
	public void deleteEmpOnCallLinkByOnCallId(long idOncall);

	/**
	 * 
	 * Method Name: getOnCallCounty Method Description: This method gets
	 * information from On Call County table.
	 * 
	 * @param cdRegion
	 *            - The code of the region.
	 * @param idOnCall
	 *            - The id of the on call schedule.
	 * @return List<RtrvOnCallCntyDto>
	 */
	List<RtrvOnCallCntyDto> getOnCallCounty(String cdRegion, Long idOnCall);

	/**
	 * 
	 * Method Name: updateOnCallFiled Method Description: This method updates
	 * the onCallFiled column for the particular ON_CALL
	 * 
	 * @param onCallDto
	 *            - The dto with the values to be updated in the ON_CALL and
	 *            table.
	 * @return
	 */
	public Date updateOnCallFiled(AddOnCallInDto onCallDto);

	/**
	 * Artifact ID: artf151569
	 * Method Name: getRouterPersonOnCall
	 * Method Description: Retrieves the Person with router designation based on the program, county,
	 * start date and end date (including the start time and end time)
	 *
	 * @param cdProgram
	 * @param cdCounty
	 * @return
	 */
	Person getRouterPersonOnCall(String cdProgram, List<String> cdCounty);

}
