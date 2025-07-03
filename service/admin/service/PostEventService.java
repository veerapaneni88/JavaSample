package us.tx.state.dfps.service.admin.service;

import java.util.Date;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.admin.dto.PostDto;
import us.tx.state.dfps.service.admin.dto.PostOutputDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PostEventService Aug 7, 2017- 6:26:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface PostEventService {
	/**
	 * callCcmn01uService
	 * 
	 * @param postDto
	 * @return PostOutputDto @
	 */
	public PostOutputDto callCcmn01uService(PostDto postDto);

	/**
	 * PostEvent
	 * 
	 * @param pInputMsg
	 * @return PostOutputDto @
	 */
	public PostOutputDto PostEvent(PostDto postDto);

	/**
	 * CallCCMN46D
	 * 
	 * @param pInputMsg
	 * @return PostOutputDto @
	 */
	public PostOutputDto CallCCMN46D(PostDto pInputMsg);

	/**
	 * CallCCMN68D
	 * 
	 * @param pInputMsg
	 * @return PostOutputDto @
	 */
	public PostOutputDto CallCCMN68D(PostDto pInputMsg);

	/**
	 * CallCCMN45D
	 * 
	 * @param pInputMsg
	 * @return PostOutputDto @
	 */
	public PostOutputDto CallCCMN45D(PostDto pInputMsg);

	/**
	 * mappContDTOtoCcmn68DTO
	 * 
	 * @param pInputDto
	 * @return Ccmn68diDto
	 */
	public EventLinkInDto mappContDTOtoCcmn68DTO(PostDto pInputDto);

	/**
	 * mappContDTOtoCcmn46DTO
	 * 
	 * @param pInputDto
	 * @return Ccmn46diDto
	 */
	public EventInputDto mappContDTOtoCcmn46DTO(PostDto pInputDto);

	/**
	 * convertDateformat
	 * 
	 * @param oldDateString
	 * @return Date
	 */
	public Date convertDateformat(String oldDateString);

	/**
	 * Method Name: postEvent Method Description:This method
	 * creates/updates/deletes Event (Event & EVENT_PERSON_LINK for Primary
	 * Child) using personId, stageId, eventType, task code and event
	 * description.
	 * 
	 * @param eventValueDto
	 * @param cReqFuncCd
	 * @param idStagePersonLinkPerson
	 * @return Long @
	 */
	public Long postEvent(EventValueDto eventValueDto, String cReqFuncCd, Long idStagePersonLinkPerson);
	
	/**
	 * 
	 * Method Description: This Method is implemented in PostEventImpl. Service
	 * Name: CCMN25s
	 * 
	 * @param archInputDto
	 * @param postEventIPDto
	 * @return PostEventOPDto
	 */

	PostEventOPDto checkPostEventStatus(PostEventIPDto postEventIPDto, ServiceReqHeaderDto ServiceReqHeaderDto);

}
