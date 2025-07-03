package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for PostEventStageStatusService Aug 7, 2017- 6:26:50 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PostEventStageStatusService {

	/**
	 * 
	 * Method Name: callPostEventStageStatusService Method Description: This
	 * service will perform common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto @
	 */
	public PostEventStageStatusOutDto callPostEventStageStatusService(PostEventStageStatusInDto pInputMsg);

	/**
	 * 
	 * Method Name: postEventOnly Method Description: This service will perform
	 * common-function Post Event table only.
	 * 
	 * @param postEventStageStatusInDto
	 * @return PostEventStageStatusOutDto
	 */
	public PostEventStageStatusOutDto postEventOnly(PostEventStageStatusInDto postEventStageStatusInDto);
}
