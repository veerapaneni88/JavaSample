package us.tx.state.dfps.service.ssccchildplan.dao;

import java.util.List;

import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCChildPlanDao - SSCCChildPlanDao Performs some of the database
 * activities the Child Plan Conversation. Nov 1, 2017- 12:27:00 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SSCCChildPlanDao {

	/**
	 * Method Name: querySSCCChildPlan Method Description: Retrieves the sscc
	 * child plan details from the database using idevent.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	public SSCCChildPlanDto querySSCCChildPlan(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: querySSCCChildPlanByPK Method Description: Retrieves the
	 * sscc child plan details from the database using id_sscc_child_plan.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	public SSCCChildPlanDto querySSCCChildPlanByPK(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: queryLastUpdateParticip Method Description: Retrieve most
	 * recent dt_last_update for sscc particpants.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	public SSCCChildPlanDto queryLastUpdateParticip(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: getChildPlanParticipList Method Description:
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanDto>
	 */
	public List<SSCCChildPlanDto> getChildPlanParticipList(SSCCChildPlanDto ssccChildPlanDto);

	/**
	 * Method Name: querySSCCChildPlanTopic Method Description: Retrieve SSCC
	 * child plan topic info for a single topic.
	 * 
	 * @param ssccChildPlanGuideTopicDto
	 * @return SSCCChildPlanGuideTopicDto
	 */
	public SSCCChildPlanGuideTopicDto querySSCCChildPlanTopic(SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto);

	/**
	 * Method Name: updateSSCCCTopic Method Description: Saves the updated sscc
	 * child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	public Long updateSSCCCTopic(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: querySSCCChildPlanParticipants Method Description: Retrieve
	 * SSCC child plan participant info.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanParticipDto>
	 */
	public List<SSCCChildPlanParticipDto> querySSCCChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateChildPlanTopic Method Description: Inserts the sscc
	 * child plan topic data into the corresponding child plan topic table on
	 * approval.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	public Long updateChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto,
			SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: updateSSCCCTopicStatus Method Description: Saves the updated
	 * sscc child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	public Long updateSSCCCTopicStatus(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto);

	/**
	 * Method Name: getActiveSSCCReferral Method Description: Uses the given
	 * stage id to retrieve any active SSCC referral for the primary child of
	 * that stage.
	 *
	 * @param stageId
	 * @return Long
	 */
	public Long getActiveSSCCReferral(Long stageId) throws DataNotFoundException;

	/**
	 * Method Name: selectNewUsingChildPlanTopic Method Description: Retrieves
	 * the sscc child plan new used topics from the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topic
	 * @return SSCCChildPlanGuideTopicDto
	 */
	public SSCCChildPlanGuideTopicDto selectNewUsingChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto, String topic);

	/**
	 * Method Name: insertChildPlanParticip Method Description: Inserts the sscc
	 * child plan participant data into the child plan participant table on
	 * approval.
	 * 
	 * @param next
	 * @param ulIdEvent
	 * @param idCase
	 */
	public Long insertChildPlanParticip(SSCCChildPlanParticipDto next, Long ulIdEvent, Long idCase);

	/**
	 * Method Name: insertSSCCChildPlan Method Description: Inserts the sscc
	 * child plan data into the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long insertSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCChildPlanParticipStatus Method Description:
	 * updates the sscc child plan participant status.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long updateSSCCChildPlanParticipStatus(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateChildPlanPermGoals Method Description: Inserts the
	 * details of SSCCChildPlanValueBean perm goals into CHILD_PLAN on approval.
	 *
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long updateChildPlanPermGoals(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCChildPlanStatus Method Description: Updates sscc
	 * child plan status in the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long updateSSCCChildPlanStatus(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: unlinkSSCCChildPlan Method Description: Unlinks the sscc
	 * child plan data from the child plan event.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long unlinkSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: updateSSCCChildPlan Method Description: Saves the sscc child
	 * plan data to the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	public Long updateSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: deleteSSCCTopicForPlan Method Description: Deletes
	 * associated topics from the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param next
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteSSCCTopicForPlan(SSCCChildPlanDto inSSCCChildPlanDto, String szCdTopic);

	/**
	 * Method Name: insertSSCCChildPlanParticip Method Description: inserts the
	 * sscc child plan participant.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	public Long insertSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto);

	/**
	 * Method Name: updateSSCCChildPlanParticip Method Description: inserts the
	 * sscc child plan participant.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	public Long updateSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto);

	/**
	 * Method Name: deleteSSCCParticipant Method Description: Deletes
	 * participant from the database.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	public Long deleteSSCCParticipant(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto);

	/**
	 * Method Name: queryAssignedTopics Method Description: Retrieves the
	 * assigned topics from the database.
	 * 
	 * @param idRsrc
	 * @param cdPlanType
	 * @return List<String>
	 */
	public List<String> queryAssignedTopics(Long idRsrc, String cdPlanType);

	/**
	 * Method Name: querySSCCChildPlanTopicData Method Description: Retrieve
	 * SSCC child plan topic info.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto>
	 */
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicData(SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: querySSCCChildPlanTopicDataForDeletedCP Method Description:
	 * Retrieve SSCC child plan topic info.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto>
	 */
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicDataForDeletedCP(
			SSCCChildPlanDto inSSCCChildPlanDto);

	/**
	 * Method Name: insertSSCCChildPlanTopic Method Description: Inserts the
	 * sscc child plan topics into the database.
	 * 
	 * @param ssccChildPlanBean
	 * @param topic
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertSSCCChildPlanTopic(SSCCChildPlanDto ssccChildPlanBean, String topic);

	/**
	 * Method Name: clearChildPlanTopic Method Description: Clears narrative
	 * field of associated child plan narrative table for SSCC assigned toics
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topic
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long clearChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto, String topic);

}
