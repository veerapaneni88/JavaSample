package us.tx.state.dfps.service.hip.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.HipGroupDtlReq;
import us.tx.state.dfps.service.common.request.UpdtRecordsReq;
import us.tx.state.dfps.service.common.response.FindrsRecordsRes;
import us.tx.state.dfps.service.common.response.HipGroupDtlRes;
import us.tx.state.dfps.service.common.response.HipGroupsRes;
import us.tx.state.dfps.service.common.response.UpdtRecordsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.hip.service.HipService;

@RestController
@RequestMapping("/hip")
public class HipController {
	@Autowired
	MessageSource messageSource;

	@Autowired
	HipService hipService;

	/**
	 * This service is to get all records for state wide intake
	 * 
	 * @param serviceReqHeaderDto
	 * @return @
	 */
	@RequestMapping(value = "/gethipgroups", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HipGroupsRes getHipGroups(@RequestBody ServiceReqHeaderDto serviceReqHeaderDto) {
		return hipService.getHipGroups(serviceReqHeaderDto);

	}

	/**
	 * This service is to get the group details for state wide intake
	 * 
	 * @param hipGroupDtlReq
	 * @return
	 */
	@RequestMapping(value = "/gethipgroupdetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HipGroupDtlRes getHipGroupDetail(@RequestBody HipGroupDtlReq hipGroupDtlReq) {
		if (TypeConvUtil.isNullOrEmpty(hipGroupDtlReq.getIdHipGroup())) {
			throw new InvalidRequestException(
					messageSource.getMessage("HipController.groupId.mandatory", null, Locale.US));
		}
		return hipService.getHipGroupDetail(hipGroupDtlReq);
	}

	/**
	 * This service is to get all HIP records for FINDRS to match and non match
	 * on the screen, this service is for IMPACT
	 * 
	 * @param serviceReqHeaderDto
	 * @return @
	 */
	@RequestMapping(value = "/gethiprecords", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FindrsRecordsRes getHipFindrsRecords(@RequestBody ServiceReqHeaderDto serviceReqHeaderDto) {
		return hipService.getHipFindrsRecords();
	}

	/**
	 * This service is to update match and non match in HIP tables , when FINDRS
	 * team does their update on the screen.
	 * 
	 * @param updtRecordsReq
	 * @return
	 */
	@RequestMapping(value = "/updthipRecords", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  UpdtRecordsRes updtHipRecords(@RequestBody UpdtRecordsReq updtRecordsReq) {
		if (TypeConvUtil.isNullOrEmpty(updtRecordsReq.getUpdatedRecords())) {
			throw new InvalidRequestException(
					messageSource.getMessage("HipController.UpdtRecordDto.mandatory", null, Locale.US));
		}
		return hipService.updtHipFindrsRecords(updtRecordsReq);
	}

}
