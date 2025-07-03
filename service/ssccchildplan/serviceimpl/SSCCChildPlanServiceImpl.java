package us.tx.state.dfps.service.ssccchildplan.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.ssccchildplan.dao.SSCCChildPlanDao;
import us.tx.state.dfps.service.ssccchildplan.service.SSCCChildPlanService;
import us.tx.state.dfps.service.ssccchildplan.utility.SSCCChildPlanUtility;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCChildPlanServiceImpl - Handles all retrieve and save
 * operations for the Child Plan module. Oct 31, 2017- 8:36:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SSCCChildPlanServiceImpl implements SSCCChildPlanService {

	@Autowired
	private SSCCChildPlanDao ssccChildPlanDao;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private SSCCChildPlanUtility ssccChildPlanUtility;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-SSCCChildPlanServiceLog");

	/**
	 * Method Name: querySSCCChildPlan Method Description: Retrieves the sscc
	 * child plan details from the database.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCChildPlanDto querySSCCChildPlan(SSCCChildPlanDto ssccChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlan in SSCCChildPlanService");

		if (!ObjectUtils.isEmpty(ssccChildPlanDto.getIdEvent()) && ssccChildPlanDto.getIdEvent() > 0) {
			ssccChildPlanDto = ssccChildPlanDao.querySSCCChildPlan(ssccChildPlanDto);
		} else {
			ssccChildPlanDto = ssccChildPlanDao.querySSCCChildPlanByPK(ssccChildPlanDto);
		}
		if (!ObjectUtils.isEmpty(ssccChildPlanDto))
			ssccChildPlanDto = ssccChildPlanDao.queryLastUpdateParticip(ssccChildPlanDto);

		LOG.debug("Exiting method querySSCCChildPlan in SSCCChildPlanService");

		return ssccChildPlanDto;
	}

	/**
	 * Method Name: getChildPlanParticipList Method Description: Retrieves the
	 * sscc child plan details from the database.
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanDto>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCChildPlanDto> getChildPlanParticipList(SSCCChildPlanDto ssccChildPlanDto) {

		LOG.debug("Entering method getChildPlanParticipList in SSCCChildPlanService");

		List<SSCCChildPlanDto> ssccChildPlanList = new ArrayList<>();
		try {
			ssccChildPlanList = ssccChildPlanDao.getChildPlanParticipList(ssccChildPlanDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method getChildPlanParticipList in SSCCChildPlanService");

		return ssccChildPlanList;
	}

	/**
	 * Method Name: querySSCCChildPlanTopic Method Description: Retrieves a
	 * single child plan topic from the database.
	 * 
	 * @param ssccChildPlanGuideTopicDto
	 * @return SSCCChildPlanGuideTopicDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCChildPlanGuideTopicDto querySSCCChildPlanTopic(SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto) {

		LOG.debug("Entering method querySSCCChildPlanTopic in SSCCChildPlanService");

		SSCCChildPlanGuideTopicDto outSSCCChildPlanGuideTopicDto = new SSCCChildPlanGuideTopicDto();
		try {
			outSSCCChildPlanGuideTopicDto = ssccChildPlanDao.querySSCCChildPlanTopic(ssccChildPlanGuideTopicDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method querySSCCChildPlanTopic in SSCCChildPlanService");

		return outSSCCChildPlanGuideTopicDto;
	}

	/**
	 * Method Name: querySSCCChildPlanTopicData Method Description: Retrieves
	 * the child plan topic data from the database.
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicData(SSCCChildPlanDto ssccChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlanTopicData in SSCCChildPlanService");

		List<SSCCChildPlanGuideTopicDto> listOfSSCCChildPlanGuideTopicDto = new ArrayList<SSCCChildPlanGuideTopicDto>();

		if (!ObjectUtils.isEmpty(ssccChildPlanDto.getIdChildPlanEvent()) && ssccChildPlanDto.getIdChildPlanEvent() > 0) {
			listOfSSCCChildPlanGuideTopicDto = ssccChildPlanDao.querySSCCChildPlanTopicData(ssccChildPlanDto);
		} else {
			listOfSSCCChildPlanGuideTopicDto = ssccChildPlanDao
					.querySSCCChildPlanTopicDataForDeletedCP(ssccChildPlanDto);
		}

		LOG.debug("Exiting method querySSCCChildPlanTopicData in SSCCChildPlanService");

		return listOfSSCCChildPlanGuideTopicDto;
	}

	/**
	 * Method Name: queryAssignedTopics Method Description: Retrieves the
	 * assigned topics from the database.
	 * 
	 * @param idRsrc
	 * @param cdPlanType
	 * @return List<String>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> queryAssignedTopics(Long idRsrc, String cdPlanType) {

		LOG.debug("Entering method queryAssignedTopics in SSCCChildPlanService");

		List<String> topics = new ArrayList<>();
		topics = ssccChildPlanDao.queryAssignedTopics(idRsrc, cdPlanType);

		LOG.debug("Exiting method queryAssignedTopics in SSCCChildPlanService");

		return topics;
	}

	/**
	 * Method Name: deleteSSCCParticipant Method Description: Deletes
	 * participant from the database.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteSSCCParticipant(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto) {

		LOG.debug("Entering method deleteSSCCParticipant in SSCCChildPlanService");
		Long updateResult = 0l;
		updateResult = ssccChildPlanDao.deleteSSCCParticipant(inSSCCChildPlanParticipDto);

		LOG.debug("Exiting method deleteSSCCParticipant in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: saveSSCCChildPlanParticip Method Description: inserts or
	 * updates the sscc child plan participant.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto) {

		LOG.debug("Entering method saveSSCCChildPlanParticip in SSCCChildPlanService");

		Long updateResult = 0l;
		Long idSSCCChildPlanParticipant = inSSCCChildPlanParticipDto.getIdSSCCChildPlanParticipant();
		if (idSSCCChildPlanParticipant != null && idSSCCChildPlanParticipant > 0) {
			updateResult = ssccChildPlanDao.updateSSCCChildPlanParticip(inSSCCChildPlanParticipDto);
		} else {
			updateResult = ssccChildPlanDao.insertSSCCChildPlanParticip(inSSCCChildPlanParticipDto);
		}

		LOG.debug("Exiting method saveSSCCChildPlanParticip in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: deleteSSCCTopicsForPlan Method Description: Deletes
	 * associated topics from the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topics
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteSSCCTopicsForPlan(SSCCChildPlanDto inSSCCChildPlanDto, List<String> topics) {

		LOG.debug("Entering method deleteSSCCTopicsForPlan in SSCCChildPlanService");
		Long updateResult = 0l;
		for (String topic : topics) {
			ssccChildPlanDao.deleteSSCCTopicForPlan(inSSCCChildPlanDto, topic);
			updateResult++;
		}
		LOG.debug("Exiting method deleteSSCCTopicsForPlan in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlan Method Description: Saves the sscc child
	 * plan data to the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlan in SSCCChildPlanService");
		Long updateResult = 0l;
		updateResult = ssccChildPlanDao.updateSSCCChildPlan(inSSCCChildPlanDto);

		LOG.debug("Exiting method updateSSCCChildPlan in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: unlinkSSCCChildPlan Method Description: Unlinks the sscc
	 * child plan data from the child plan event.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long unlinkSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method unlinkSSCCChildPlan in SSCCChildPlanService");

		Long updateResult = 0l;
		updateResult = ssccChildPlanDao.unlinkSSCCChildPlan(inSSCCChildPlanDto);
		LOG.debug("Exiting method unlinkSSCCChildPlan in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlanStatus Method Description: Updates sscc
	 * child plan status in the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCChildPlanStatus(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlanStatus in SSCCChildPlanService");

		Long updateResult = 0l;
		if (ServiceConstants.CSSCCSTA_60.equals(inSSCCChildPlanDto.getCdStatus())) {
			inSSCCChildPlanDto.setIndReadyForReview(ServiceConstants.Y);
		} else {
			inSSCCChildPlanDto.setIndReadyForReview(ServiceConstants.N);
		}
		updateResult = ssccChildPlanDao.updateSSCCChildPlanStatus(inSSCCChildPlanDto);

		if (ServiceConstants.CSSCCSTA_90.equals(inSSCCChildPlanDto.getCdStatus())) {
			inSSCCChildPlanDto = ssccChildPlanDao.querySSCCChildPlan(inSSCCChildPlanDto);
			updateResult = ssccChildPlanDao.updateChildPlanPermGoals(inSSCCChildPlanDto);
		}

		LOG.debug("Exiting method updateSSCCChildPlanStatus in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlanParticipStatus Method Description:
	 * updates the sscc child plan participant status.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCChildPlanParticipStatus(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlanParticipStatus in SSCCChildPlanService");

		Long updateResult = 0l;
		updateResult = ssccChildPlanDao.updateSSCCChildPlanParticipStatus(inSSCCChildPlanDto);

		LOG.debug("Exiting method updateSSCCChildPlanParticipStatus in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: saveSSCCChildPlan Method Description: Inserts the sscc child
	 * plan data into the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method saveSSCCChildPlan in SSCCChildPlanService");

		ssccChildPlanDao.insertSSCCChildPlan(inSSCCChildPlanDto);

		LOG.debug("Exiting method saveSSCCChildPlan in SSCCChildPlanService");

	}

	/**
	 * Method Name: saveSSCCChildPlanTopics Method Description: Inserts the sscc
	 * child plan topics into the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topics
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveSSCCChildPlanTopics(SSCCChildPlanDto inSSCCChildPlanDto, List<String> topics) {

		LOG.debug("Entering method saveSSCCChildPlanTopics in SSCCChildPlanService");

		Long updateResult = 0l;
		for (String topic : topics) {
			updateResult = ssccChildPlanDao.insertSSCCChildPlanTopic(inSSCCChildPlanDto, topic);
			String topicPairCode = lookupDao.simpleDecodeSafe(ServiceConstants.CCPPAIR1, topic);
			if (isValid(topicPairCode)) {
				SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto = new SSCCChildPlanGuideTopicDto();
				SSCCChildPlanGuideTopicDto outSSCCChildPlanGuideTopicDto = new SSCCChildPlanGuideTopicDto();
				inSSCCChildPlanGuideTopicDto.setIdSCCCChildPlan(inSSCCChildPlanDto.getIdSsccChildPlan());
				inSSCCChildPlanGuideTopicDto.setStCode(topicPairCode);
				outSSCCChildPlanGuideTopicDto = ssccChildPlanDao.querySSCCChildPlanTopic(inSSCCChildPlanGuideTopicDto);
				String status = outSSCCChildPlanGuideTopicDto.getCdStatus();

				if (isValid(status) && status.equals(ServiceConstants.CSSCCSTA_60)) {
					inSSCCChildPlanGuideTopicDto.setCdStatus(ServiceConstants.CSSCCSTA_30);
					inSSCCChildPlanGuideTopicDto.setIndReadyForReview("N");
					inSSCCChildPlanGuideTopicDto.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
					updateResult = ssccChildPlanDao.updateSSCCCTopicStatus(inSSCCChildPlanGuideTopicDto);
					ssccChildPlanUtility.createTimelineRecord(inSSCCChildPlanDto, ServiceConstants.CSSCCTBL_40,
							lookupDao.simpleDecodeSafe(ServiceConstants.CCPTOPCS,
									inSSCCChildPlanGuideTopicDto.getStCode())
									+ " - "
									+ ssccChildPlanUtility
											.getSSCCStatusString(inSSCCChildPlanGuideTopicDto.getCdStatus()),
							inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
					inSSCCChildPlanGuideTopicDto.setStCode(topic);
					updateResult = ssccChildPlanDao.updateSSCCCTopicStatus(inSSCCChildPlanGuideTopicDto);
					ssccChildPlanUtility.createTimelineRecord(inSSCCChildPlanDto, ServiceConstants.CSSCCTBL_40,
							lookupDao.simpleDecodeSafe(ServiceConstants.CCPTOPCS,
									inSSCCChildPlanGuideTopicDto.getStCode())
									+ " - "
									+ ssccChildPlanUtility
											.getSSCCStatusString(inSSCCChildPlanGuideTopicDto.getCdStatus()),
							inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
				} else if (isValid(status) && !status.equals(ServiceConstants.CSSCCSTA_20)) {
					inSSCCChildPlanGuideTopicDto.setStCode(topic);
					inSSCCChildPlanGuideTopicDto.setCdStatus(status);
					inSSCCChildPlanGuideTopicDto
							.setIdLastUpdatePerson(inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
					updateResult = ssccChildPlanDao.updateSSCCCTopicStatus(inSSCCChildPlanGuideTopicDto);
					ssccChildPlanUtility.createTimelineRecord(inSSCCChildPlanDto, ServiceConstants.CSSCCTBL_40,
							lookupDao.simpleDecodeSafe(ServiceConstants.CCPTOPCS,
									inSSCCChildPlanGuideTopicDto.getStCode())
									+ " - "
									+ ssccChildPlanUtility
											.getSSCCStatusString(inSSCCChildPlanGuideTopicDto.getCdStatus()),
							inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
				}
			}
			updateResult = ssccChildPlanDao.clearChildPlanTopic(inSSCCChildPlanDto, topic);
		}

		LOG.debug("Exiting method saveSSCCChildPlanTopics in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: insertChildPlanParticipants Method Description: Inserts the
	 * sscc child plan participant data into the child plan participant table on
	 * approval.
	 *
	 * @param inSSCCChildPlanDto
	 * @param idCase
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long insertChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto, Long idCase) {

		LOG.debug("Entering method insertChildPlanParticipants in SSCCChildPlanService");

		Long updateResult = 0l;
		List<SSCCChildPlanParticipDto> participants = inSSCCChildPlanDto.getSsccChildPlanParticipDtoList();
		try {
			for (SSCCChildPlanParticipDto ssCCChildPlanParticipDto : participants) {
				ssccChildPlanDao.insertChildPlanParticip(ssCCChildPlanParticipDto,
						inSSCCChildPlanDto.getIdChildPlanEvent(), idCase);
				updateResult++;
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method insertChildPlanParticipants in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: queryNewUsedSSCCChildPlanTopics Method Description:
	 * Retrieves the sscc child plan new used topics from the database.
	 * 
	 * @param ssccChildPlanDto
	 * @param topics
	 * @return List<SSCCChildPlanGuideTopicDto>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCChildPlanGuideTopicDto> queryNewUsedSSCCChildPlanTopics(SSCCChildPlanDto ssccChildPlanDto,
			List<String> topics) {

		LOG.debug("Entering method queryNewUsedSSCCChildPlanTopics in SSCCChildPlanService");

		List<SSCCChildPlanGuideTopicDto> guideTopicList = new ArrayList<SSCCChildPlanGuideTopicDto>();
		for (String topic : topics) {
			SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto = new SSCCChildPlanGuideTopicDto();
			ssccChildPlanGuideTopicDto = ssccChildPlanDao.selectNewUsingChildPlanTopic(ssccChildPlanDto, topic);
			if (!ObjectUtils.isEmpty(ssccChildPlanGuideTopicDto))
				guideTopicList.add(ssccChildPlanGuideTopicDto);
		}

		LOG.debug("Exiting method queryNewUsedSSCCChildPlanTopics in SSCCChildPlanService");

		return guideTopicList;
	}

	/**
	 * Method Name: getActiveSSCCReferral Method Description: Uses the given
	 * stage id to retrieve any active SSCC referral for the primary child of
	 * that stage.
	 * 
	 * @param stageId
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getActiveSSCCReferral(Long stageId) {

		LOG.debug("Entering method getActiveSSCCReferral in SSCCChildPlanService");
		Long referralId = 0l;
		try {
			referralId = ssccChildPlanDao.getActiveSSCCReferral(stageId);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method getActiveSSCCReferral in SSCCChildPlanService");

		return (referralId);
	}

	/**
	 * Method Name: updateSSCCCTopicStatus Method Description: Saves the updated
	 * sscc child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCCTopicStatus(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateSSCCCTopicStatus in SSCCChildPlanService");

		Long updateResult = 0l;
		try {
			updateResult = ssccChildPlanDao.updateSSCCCTopicStatus(inSSCCChildPlanGuideTopicDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method updateSSCCCTopicStatus in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: updateChildPlanTopic Method Description:Inserts the sscc
	 * child plan topic data into the corresponding child plan topic table on
	 * approval.
	 *
	 * @param inSSCCChildPlanDto
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto,
			SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateChildPlanTopic in SSCCChildPlanService");

		Long updateResult = 0l;
		try {
			updateResult = ssccChildPlanDao.updateChildPlanTopic(inSSCCChildPlanDto, inSSCCChildPlanGuideTopicDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method updateChildPlanTopic in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: querySSCCChildPlanParticipants Method Description: Retrieves
	 * the sscc child plan participant data from the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanParticipDto>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCChildPlanParticipDto> querySSCCChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlanParticipants in SSCCChildPlanService");

		List<SSCCChildPlanParticipDto> sscchildPlanParticipDtoList = new ArrayList<SSCCChildPlanParticipDto>();
		sscchildPlanParticipDtoList = ssccChildPlanDao.querySSCCChildPlanParticipants(inSSCCChildPlanDto);

		LOG.debug("Exiting method querySSCCChildPlanParticipants in SSCCChildPlanService");

		return sscchildPlanParticipDtoList;
	}

	/**
	 * Method Name: updateSSCCCTopic Method Description: Saves the updated sscc
	 * child plan topic to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCCTopic(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateSSCCCTopic in SSCCChildPlanService");

		Long updateResult = 0l;
		try {
			updateResult = ssccChildPlanDao.updateSSCCCTopic(inSSCCChildPlanGuideTopicDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method updateSSCCCTopic in SSCCChildPlanService");

		return updateResult;
	}

	/**
	 * Method Name: isValid Method Description:Checks to see if a given string
	 * is valid. This includes checking that the string is not null or empty.
	 * 
	 * @param value
	 * @return boolean
	 */
	private boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();

		return (trimmedString.length() > 0);
	}
}
