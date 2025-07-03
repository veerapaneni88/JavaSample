package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailDto;
import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to declare the methods which has to be implemented for
 * adding/updating the on call schedule details. Feb 9, 2018- 2:49:51 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface SaveOnCallDetailService {

	/**
	 * Method Name: saveOnCallDetail Method Description:This service checks for
	 * overlap and to save the on call schedule details.
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @return saveOnCallDetailRes - This dto is used to hold the response of
	 *         the add/update operation of on call schedule.
	 */
	public SaveOnCallDetailRes saveOnCallDetail(SaveOnCallDetailDto saveOnCallDetailDto);

	/**
	 * 
	 * Method Name: checkForOverlapExists Method Description: This service
	 * checks for overlap .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule. @
	 */
	public void checkForOverlapExists(SaveOnCallDetailDto saveOnCallDetailDto, SaveOnCallDetailRes saveOnCallDetailRes);

	/**
	 * 
	 * Method Name: saveNewOnCallSchedule Method Description: This service
	 * creates a new on call schedule .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule. @
	 */
	public void saveNewOnCallSchedule(SaveOnCallDetailDto saveOnCallDetailDto, SaveOnCallDetailRes saveOnCallDetailRes);

	/**
	 * 
	 * Method Name: updateOnCallSchedule Method Description: This service update
	 * a on call schedule .
	 * 
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the input values of on call details
	 *            to be saved/updated.
	 * @param saveOnCallDetailRes
	 *            - This dto is used to hold the response of the add/update
	 *            operation of on call schedule.
	 * @return @
	 */
	public void updateOnCallSchedule(SaveOnCallDetailDto saveOnCallDetailDto, SaveOnCallDetailRes saveOnCallDetailRes);

}
