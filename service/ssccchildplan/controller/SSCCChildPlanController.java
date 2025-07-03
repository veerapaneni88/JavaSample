package us.tx.state.dfps.service.ssccchildplan.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;
import us.tx.state.dfps.service.common.request.SSCCChildPlanGuideTopicReq;
import us.tx.state.dfps.service.common.request.SSCCChildPlanParticipReq;
import us.tx.state.dfps.service.common.request.SSCCChildPlanReq;
import us.tx.state.dfps.service.common.response.SSCCChildPlanGuideTopicRes;
import us.tx.state.dfps.service.common.response.SSCCChildPlanRes;
import us.tx.state.dfps.service.ssccchildplan.service.SSCCChildPlanService;
import us.tx.state.dfps.service.ssccchildplan.utility.SSCCChildPlanUtility;
import us.tx.state.dfps.service.subcare.dto.ChildPlanGuideTopicDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCChildPlanController - Handles all retrieve and save
 * operations for the Child Plan module. Oct 31, 2017- 8:31:53 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/ssccChildPlan")
public class SSCCChildPlanController {

	@Autowired
	private SSCCChildPlanService ssccChildPlanService;

	@Autowired
	private SSCCChildPlanUtility ssccChildPlanUtility;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-SSCCChildPlanControllerLog");

	/**
	 * Method Name: querySSCCChildPlan Method Description:Retrieves the sscc
	 * child plan details from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/querySSCCChildPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes querySSCCChildPlan(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method querySSCCChildPlan in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(ssccChildPlanReq.getSsccChildPlanDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		SSCCChildPlanRes ssccChildPlanRes = new SSCCChildPlanRes();
		ssccChildPlanRes
				.setSsccChildPlanDto(ssccChildPlanService.querySSCCChildPlan(ssccChildPlanReq.getSsccChildPlanDto()));

		LOG.debug("Exiting method querySSCCChildPlan in SSCCChildPlanController");

		return ssccChildPlanRes;
	}

	/**
	 * Method Name: getChildPlanParticipList Method Description: Retrieves the
	 * sscc child plan details from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/getChildPlanParticipList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes getChildPlanParticipList(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method getChildPlanParticipList in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(ssccChildPlanReq.getSsccChildPlanDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}

		List<SSCCChildPlanDto> listOfSSCCChildPlanDto = ssccChildPlanService
				.getChildPlanParticipList(ssccChildPlanReq.getSsccChildPlanDto());

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		outSSCCChildPlanRes.setListOfSsccChildPlanDto(listOfSSCCChildPlanDto);

		LOG.debug("Exiting method getChildPlanParticipList in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: querySSCCChildPlanTopic Method Description: Retrieves a
	 * single child plan topic from the database.
	 * 
	 * @param ssccChildPlanGuideTopicReq
	 * @return SSCCChildPlanGuideTopicRes
	 *
	 * 
	 */
	@RequestMapping(value = "/querySSCCChildPlanTopic", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanGuideTopicRes querySSCCChildPlanTopic(
			@RequestBody SSCCChildPlanGuideTopicReq ssccChildPlanGuideTopicReq) {

		LOG.debug("Entering method querySSCCChildPlanTopic in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(ssccChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanGuideTopicDto.mandatory", null, Locale.US));
		}

		SSCCChildPlanGuideTopicRes outSSCCChildPlanGuideTopicRes = new SSCCChildPlanGuideTopicRes();
		outSSCCChildPlanGuideTopicRes.setTransactionId(ssccChildPlanGuideTopicReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanGuideTopicReq.getTransactionId());

		outSSCCChildPlanGuideTopicRes.setSsCChildPlanGuideTopicDto(ssccChildPlanService
				.querySSCCChildPlanTopic(ssccChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto()));

		LOG.debug("Exiting method querySSCCChildPlanTopic in SSCCChildPlanController");

		return outSSCCChildPlanGuideTopicRes;
	}

	/**
	 * Method Name: querySSCCChildPlanTopicData Method Description: Retrieves
	 * the child plan topic data from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/querySSCCChildPlanTopicData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes querySSCCChildPlanTopicData(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method querySSCCChildPlanTopicData in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(ssccChildPlanReq.getSsccChildPlanDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}

		SSCCChildPlanRes ssccChildPlanRes = new SSCCChildPlanRes();
		ssccChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		ssccChildPlanRes.setListOfSSCCChildPlanGuideTopicDto(
				ssccChildPlanService.querySSCCChildPlanTopicData(ssccChildPlanReq.getSsccChildPlanDto()));

		LOG.debug("Exiting method querySSCCChildPlanTopicData in SSCCChildPlanController");

		return ssccChildPlanRes;
	}

	/**
	 * Method Name: queryAssignedTopics Method Description: Retrieves the
	 * assigned topics from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/queryAssignedTopics", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes queryAssignedTopics(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method queryAssignedTopics in SSCCChildPlanController");

		Long idRsrc = ssccChildPlanReq.getIdRsrc();
		String cdPlanType = ssccChildPlanReq.getCdPlanType();

		if (ObjectUtils.isEmpty(idRsrc)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.idRsrc.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(cdPlanType)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.cdPlanType.mandatory", null, Locale.US));
		}

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		outSSCCChildPlanRes.setTopics(ssccChildPlanService.queryAssignedTopics(idRsrc, cdPlanType));

		LOG.debug("Exiting method queryAssignedTopics in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: deleteSSCCParticipant Method Description: Deletes
	 * participant from the database.
	 * 
	 * @param ssccChildPlanParticipReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/deleteSSCCParticipant", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes deleteSSCCParticipant(
			@RequestBody SSCCChildPlanParticipReq ssccChildPlanParticipReq) {

		LOG.debug("Entering method deleteSSCCParticipant in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		SSCCChildPlanParticipDto ssccChildPlanParticipDto = ssccChildPlanParticipReq.getSsccChildPlanParticipDto();
		if (ObjectUtils.isEmpty(ssccChildPlanParticipDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanParticipDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.deleteSSCCParticipant(ssccChildPlanParticipDto));

		LOG.debug("Exiting method deleteSSCCParticipant in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: saveSSCCChildPlanParticip Method Description: inserts or
	 * updates the sscc child plan participant.
	 * 
	 * @param ssccChildPlanParticipReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/saveSSCCChildPlanParticip", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes saveSSCCChildPlanParticip(
			@RequestBody SSCCChildPlanParticipReq ssccChildPlanParticipReq) {

		LOG.debug("Entering method saveSSCCChildPlanParticip in SSCCChildPlanController");

		SSCCChildPlanParticipDto ssccChildPlanParticipDto = ssccChildPlanParticipReq.getSsccChildPlanParticipDto();
		if (ObjectUtils.isEmpty(ssccChildPlanParticipDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanParticipDto.mandatory", null, Locale.US));
		}

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.saveSSCCChildPlanParticip(ssccChildPlanParticipDto));

		LOG.debug("Exiting method saveSSCCChildPlanParticip in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: deleteSSCCTopicsForPlan Method Description: Deletes
	 * associated topics from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/deleteSSCCTopicsForPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes deleteSSCCTopicsForPlan(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method deleteSSCCTopicsForPlan in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		List<String> topics = ssccChildPlanReq.getTopics();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(topics)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.topics.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.deleteSSCCTopicsForPlan(ssccChildPlanDto, topics));

		LOG.debug("Exiting method deleteSSCCTopicsForPlan in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: updateSSCCChildPlan Method Description: Saves the sscc child
	 * plan data to the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateSSCCChildPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateSSCCChildPlan(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method updateSSCCChildPlan in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.updateSSCCChildPlan(ssccChildPlanDto));

		LOG.debug("Exiting method updateSSCCChildPlan in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: unlinkSSCCChildPlan Method Description: Unlinks the sscc
	 * child plan data from the child plan event.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/unlinkSSCCChildPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes unlinkSSCCChildPlan(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method unlinkSSCCChildPlan in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.unlinkSSCCChildPlan(ssccChildPlanDto));

		LOG.debug("Exiting method unlinkSSCCChildPlan in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: updateSSCCChildPlanStatus Method Description: Updates sscc
	 * child plan status in the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateSSCCChildPlanStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateSSCCChildPlanStatus(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method updateSSCCChildPlanStatus in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.updateSSCCChildPlanStatus(ssccChildPlanDto));

		LOG.debug("Exiting method updateSSCCChildPlanStatus in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: updateSSCCChildPlanParticipStatus Method Description:
	 * updates the sscc child plan participant status.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateSSCCChildPlanParticipStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateSSCCChildPlanParticipStatus(
			@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method updateSSCCChildPlanParticipStatus in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.updateSSCCChildPlanParticipStatus(ssccChildPlanDto));

		LOG.debug("Exiting method updateSSCCChildPlanParticipStatus in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: saveSSCCChildPlan Method Description: Inserts the sscc child
	 * plan data into the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/saveSSCCChildPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes saveSSCCChildPlan(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method saveSSCCChildPlan in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		ssccChildPlanService.saveSSCCChildPlan(ssccChildPlanDto);

		LOG.debug("Exiting method saveSSCCChildPlan in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: saveSSCCChildPlanTopics Method Description: Inserts the sscc
	 * child plan topics into the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/saveSSCCChildPlanTopics", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes saveSSCCChildPlanTopics(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method saveSSCCChildPlanTopics in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		List<String> topics = ssccChildPlanReq.getTopics();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(topics)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.topics.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.saveSSCCChildPlanTopics(ssccChildPlanDto, topics));

		LOG.debug("Exiting method saveSSCCChildPlanTopics in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: saveSSCCChildPlanTopics Method Description: Inserts the sscc
	 * child plan topics into the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/saveChildPlanGuideTopic", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes saveChildPlanGuideTopic(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method saveChildPlanGuideTopic in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		ChildPlanGuideTopicDto childPlanGuideTopicDto = ssccChildPlanReq.getChildPlanGuideTopicDto();
		if (ObjectUtils.isEmpty(childPlanGuideTopicDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.childPlanGuideTopicDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes
				.setUpdateResult(ssccChildPlanUtility.saveChildPlanGuideTopicForSSCCData(childPlanGuideTopicDto));
		LOG.debug("Exiting method saveChildPlanGuideTopic in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: createTimelineRecord Method Description: Save timeline
	 * record for sscc child plan in the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/createTimelineRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes createTimelineRecord(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method createTimelineRecord in SSCCChildPlanController");
		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		SSCCTimelineDto ssccTimelineDto = ssccChildPlanReq.getSsccTimelineDto();

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(ssccTimelineDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccTimelineDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes
				.setUpdateResult(ssccChildPlanUtility.createTimelineRecord(ssccChildPlanDto, ssccTimelineDto));
		LOG.debug("Exiting method saveChildPlanGuideTopic in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: createTimelineRecord Method Description: Save timeline
	 * record for sscc child plan in the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/queryTimelineRecordList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes queryTimelineRecordList(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method queryTimelineRecordList in SSCCChildPlanController");
		SSCCTimelineDto ssccTimelineDto = ssccChildPlanReq.getSsccTimelineDto();

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();

		if (ObjectUtils.isEmpty(ssccTimelineDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccTimelineDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setSsccTimelineDtoList(ssccChildPlanUtility.queryTimelineRecordList(ssccTimelineDto));
		LOG.debug("Exiting method queryTimelineRecordList in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: insertChildPlanParticipants Method Description: Inserts the
	 * sscc child plan participant data into the child plan participant table on
	 * approval.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/insertChildPlanParticipants", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes insertChildPlanParticipants(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method insertChildPlanParticipants in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		Long idCase = ssccChildPlanReq.getIdCase();
		if (ObjectUtils.isEmpty(ssccChildPlanDto) && ObjectUtils.isEmpty(idCase)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(idCase)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.idCase.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService.insertChildPlanParticipants(ssccChildPlanDto, idCase));

		LOG.debug("Exiting method insertChildPlanParticipants in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: queryNewUsedSSCCChildPlanTopics Method Description:
	 * Retrieves the sscc child plan new used topics from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/queryNewUsedSSCCChildPlanTopics", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)

	public  SSCCChildPlanRes queryNewUsedSSCCChildPlanTopics(
			@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method queryNewUsedSSCCChildPlanTopics in SSCCChildPlanController");

		SSCCChildPlanRes ssccChildPlanRes = new SSCCChildPlanRes();
		ssccChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		List<String> topics = ssccChildPlanReq.getTopics();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(topics)) {
			LOG.debug(
					"Topic list is empty during running method queryNewUsedSSCCChildPlanTopics in SSCCChildPlanController");
		}
		ssccChildPlanRes.setListOfSSCCChildPlanGuideTopicDto(
				ssccChildPlanService.queryNewUsedSSCCChildPlanTopics(ssccChildPlanDto, topics));

		LOG.debug("Exiting method queryNewUsedSSCCChildPlanTopics in SSCCChildPlanController");

		return ssccChildPlanRes;
	}

	/**
	 * Method Name: getActiveSSCCReferral Method Description: Uses the given
	 * stage id to retrieve any active SSCC referral for the primary child of
	 * that stage.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/getActiveSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes getActiveSSCCReferral(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method getActiveSSCCReferral in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		if (ObjectUtils.isEmpty(ssccChildPlanReq.getStageId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.stageId.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setReferralId(ssccChildPlanService.getActiveSSCCReferral(ssccChildPlanReq.getStageId()));

		LOG.debug("Exiting method getActiveSSCCReferral in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: updateSSCCCTopicStatus Method Description: Saves the updated
	 * sscc child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateSSCCTopicStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateSSCCTopicStatus(
			@RequestBody SSCCChildPlanGuideTopicReq inSSCCChildPlanGuideTopicReq) {

		LOG.debug("Entering method updateSSCCCTopicStatus in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(inSSCCChildPlanGuideTopicReq.getTransactionId());
		LOG.info("TransactionId :" + inSSCCChildPlanGuideTopicReq.getTransactionId());

		if (ObjectUtils.isEmpty(inSSCCChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanGuideTopicDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(ssccChildPlanService
				.updateSSCCCTopicStatus(inSSCCChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto()));

		LOG.debug("Exiting method updateSSCCCTopicStatus in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: updateChildPlanTopic Method Description: Inserts the sscc
	 * child plan topic data into the corresponding child plan topic table on
	 * approval.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateChildPlanTopic", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateChildPlanTopic(@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method updateChildPlanTopic in SSCCChildPlanController");

		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());

		SSCCChildPlanDto ssccChildPlanDto = ssccChildPlanReq.getSsccChildPlanDto();
		SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto = ssccChildPlanReq.getSsccChildPlanGuideTopicDto();
		if (ObjectUtils.isEmpty(ssccChildPlanDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(ssccChildPlanGuideTopicDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanGuideTopicDto.mandatory", null, Locale.US));
		}
		outSSCCChildPlanRes.setUpdateResult(
				ssccChildPlanService.updateChildPlanTopic(ssccChildPlanDto, ssccChildPlanGuideTopicDto));

		LOG.debug("Exiting method updateChildPlanTopic in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}

	/**
	 * Method Name: querySSCCChildPlanParticipants Method Description: Retrieves
	 * the sscc child plan participant data from the database.
	 * 
	 * @param ssccChildPlanReq
	 * @return SSCCChildPlanRes
	 */
	@RequestMapping(value = "/querySSCCChildPlanParticipants", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes querySSCCChildPlanParticipants(
			@RequestBody SSCCChildPlanReq ssccChildPlanReq) {

		LOG.debug("Entering method querySSCCChildPlanParticipants in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(ssccChildPlanReq.getSsccChildPlanDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanDto.mandatory", null, Locale.US));
		}
		SSCCChildPlanRes ssccChildPlanRes = new SSCCChildPlanRes();
		ssccChildPlanRes.setTransactionId(ssccChildPlanReq.getTransactionId());
		LOG.info("TransactionId :" + ssccChildPlanReq.getTransactionId());
		ssccChildPlanRes.setListOfSSCCChildPlanParticipDto(
				ssccChildPlanService.querySSCCChildPlanParticipants(ssccChildPlanReq.getSsccChildPlanDto()));

		LOG.debug("Exiting method querySSCCChildPlanParticipants in SSCCChildPlanController");

		return ssccChildPlanRes;
	}

	/**
	 * Method Name: updateSSCCCTopic Method Description: Saves the updated sscc
	 * child plan topic to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicReq
	 * @return SSCCChildPlanRes
	 *
	 * 
	 */
	@RequestMapping(value = "/updateSSCCTopic", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCChildPlanRes updateSSCCTopic(
			@RequestBody SSCCChildPlanGuideTopicReq inSSCCChildPlanGuideTopicReq) {

		LOG.debug("Entering method updateSSCCCTopic in SSCCChildPlanController");

		if (ObjectUtils.isEmpty(inSSCCChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccChildPlan.ssccChildPlanGuideTopicDto.mandatory", null, Locale.US));
		}
		SSCCChildPlanRes outSSCCChildPlanRes = new SSCCChildPlanRes();
		outSSCCChildPlanRes.setTransactionId(inSSCCChildPlanGuideTopicReq.getTransactionId());
		LOG.info("TransactionId :" + inSSCCChildPlanGuideTopicReq.getTransactionId());
		outSSCCChildPlanRes.setUpdateResult(
				ssccChildPlanService.updateSSCCCTopic(inSSCCChildPlanGuideTopicReq.getSsccChildPlanGuideTopicDto()));

		LOG.debug("Exiting method updateSSCCCTopic in SSCCChildPlanController");

		return outSSCCChildPlanRes;
	}
}
