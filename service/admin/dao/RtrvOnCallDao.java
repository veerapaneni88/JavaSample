package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.RtrvOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * contains method defintions to check for overlap with existing on call
 * schedules and also used to fetch the counties for a particular region for an
 * on call schedule Aug 17, 2017- 5:56:34 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface RtrvOnCallDao {

	/**
	 * Method Name: getOnCallForCountyProgram Method Description:This method is
	 * used to retrieve the list of on call for a particular combination of
	 * county, region and program
	 * 
	 * @param rtrvOnCallInDto
	 *            - This dto is used to hold the input values for retrieving the
	 *            list of oncall schedule.
	 * @return List<RtrvOnCallOutDto> - This collection holds the list of on
	 *         call schedules. @
	 */
	public List<RtrvOnCallOutDto> getOnCallForCountyProgram(RtrvOnCallInDto rtrvOnCallInDto);

	/**
	 * Method Name: checkOverLapExists Method Description:This method is used to
	 * check if the on call schedule which is tried to be modified/added
	 * overlaps with the existing on call schedule available.
	 * 
	 * @param rtrvOnCallInDto-
	 *            This dto is used to hold the input values for checking if an
	 *            overlap exists. list of oncall schedule.
	 * @return boolean - This boolean value is to determine if an overlap exists
	 *         or not.
	 */
	public boolean checkOverLapExists(RtrvOnCallInDto rtrvOnCallInDto);
}
