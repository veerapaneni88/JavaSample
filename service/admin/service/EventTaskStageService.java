package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CheckStageInpDto;
import us.tx.state.dfps.service.admin.dto.CheckStageOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * EventTaskStageService Jul 3, 2018- 12:09:46 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface EventTaskStageService {

	/**
	 * Check stage event status.
	 *
	 * @param checkStageInpDto
	 *            the check stage inp dto
	 * @return the string
	 */
	public String checkStageEventStatus(CheckStageInpDto checkStageInpDto);

	/**
	 * Method Name: callCSES71D Method Description:This Method will retrieve the
	 * Stage Details for particular stage id by calling the DAO layer.
	 * 
	 * @param ccmn06uiDto
	 * @param pbStageIsClosed
	 * @return returnValue @
	 */
	public List<CheckStageOutDto> callCSES71D(CheckStageInpDto ccmn06uiDto, boolean pbStageIsClosed);

	/**
	 * Method Name: callCcmn06uService Method Description:This Method will
	 * retrieve the Stage Details for particular stage id by calling the DAO
	 * layer.
	 * 
	 * @param ccmn06uiDto
	 * @return ccmn06uoDto @
	 */
	public CheckStageOutDto callCcmn06uService(CheckStageInpDto ccmn06uiDto);

}
