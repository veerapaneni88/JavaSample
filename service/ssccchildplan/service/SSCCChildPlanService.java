package us.tx.state.dfps.service.ssccchildplan.service;

import java.util.List;

import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;

public interface SSCCChildPlanService {

	/**
	 * Method Name: querySSCCChildPlan Method Description:
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto @
	 */
	public SSCCChildPlanDto querySSCCChildPlan(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: getChildPlanParticipList Method Description:
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanDto> @
	 */
	public List<SSCCChildPlanDto> getChildPlanParticipList(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: querySSCCChildPlanTopic Method Description:
	 * 
	 * @param ssccChildPlanGuideTopicDto
	 * @return SSCCChildPlanGuideTopicDto @
	 */
	public SSCCChildPlanGuideTopicDto querySSCCChildPlanTopic(SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto);

	/**
	 * Method Name: querySSCCChildPlanTopicData Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto> @
	 */
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicData(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: queryAssignedTopics Method Description:
	 * 
	 * @param idRsrc
	 * @param cdPlanType
	 * @return List<String> @
	 */
	public List<String> queryAssignedTopics(Long idRsrc, String cdPlanType);

	/**
	 * Method Name: deleteSSCCParticipant Method Description:
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long @
	 */
	public Long deleteSSCCParticipant(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto);

	/**
	 * Method Name: deleteSSCCTopicsForPlan Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topics
	 * @return Long @
	 */
	public Long deleteSSCCTopicsForPlan(SSCCChildPlanDto inSSCCChildPlanDto, List<String> topics);

	/**
	 * Method Name: updateSSCCChildPlan Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long @
	 */
	public Long updateSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: unlinkSSCCChildPlan Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long @
	 */
	public Long unlinkSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCChildPlanStatus Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long @
	 */
	public Long updateSSCCChildPlanStatus(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCChildPlanParticipStatus Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long @
	 */
	public Long updateSSCCChildPlanParticipStatus(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: saveSSCCChildPlan Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long @
	 */
	public void saveSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: saveSSCCChildPlanTopics Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topics
	 * @return Long @
	 */
	public Long saveSSCCChildPlanTopics(SSCCChildPlanDto inSSCCChildPlanDto, List<String> topics);

	/**
	 * Method Name: insertChildPlanParticipants Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @param idCase
	 * @return Long @
	 */
	public Long insertChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto, Long idCase);

	/**
	 * Method Name: queryNewUsedSSCCChildPlanTopics Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topics
	 * @return List<SSCCChildPlanGuideTopicDto> @
	 */
	public List<SSCCChildPlanGuideTopicDto> queryNewUsedSSCCChildPlanTopics(SSCCChildPlanDto inSSCCChildPlanDto,
			List<String> topics);

	/**
	 * Method Name: getActiveSSCCReferral Method Description:
	 * 
	 * @param stageId
	 * @return Long @
	 */
	public Long getActiveSSCCReferral(Long stageId);

	/**
	 * Method Name: updateSSCCCTopicStatus Method Description:
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long @
	 */
	public Long updateSSCCCTopicStatus(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: updateChildPlanTopic Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long @
	 */
	public Long updateChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto,
			SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: querySSCCChildPlanParticipants Method Description:
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanParticipDto> @
	 */
	public List<SSCCChildPlanParticipDto> querySSCCChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCCTopic Method Description:
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long @
	 */
	public Long updateSSCCCTopic(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: saveSSCCChildPlanParticip Method Description:
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long @
	 */
	public Long saveSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto);

}
