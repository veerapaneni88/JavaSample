package us.tx.state.dfps.service.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.FacilityDetailDto;
import us.tx.state.dfps.service.admin.service.FacilityDetailService;
import us.tx.state.dfps.service.common.request.ApprovalStatusReq;
import us.tx.state.dfps.service.common.request.FacilityDetailReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.request.HmStatusReq;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;
import us.tx.state.dfps.service.common.response.HmStatusRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringDto;

import java.util.List;
import java.util.Locale;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:RsrCityDetailRtrvController..Returns res as json format Aug 22,
 * 2017- 12:36:25 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/facilityDetail")
public class FacilityDetailServiceController {

	@Autowired
	FacilityDetailService facilityDetailService;

	@Autowired
	MessageSource messageSource;

	//PPM 60692-artf178534
	@Autowired
	HeightenedMonitoringService heightenedMonitoringService;

	/**
	 * Method Name: getFacilityDetails Method Description: This method is used
	 * to getFacilityDetails (CRES09S)
	 * 
	 * @param facilityDetailReq
	 * @return FacilityDetailRes
	 */
	@RequestMapping(value = "/getFacilityDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public FacilityDetailRes getFacilityDetails(@RequestBody FacilityDetailReq facilityDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(facilityDetailReq.getIdResource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FacilityDetailServiceController.IdResource.mandatory", null, Locale.US));
		}
		List<FacilityDetailDto> facilityDetails = facilityDetailService.getFacilityDetails(facilityDetailReq);
		FacilityDetailRes res = new FacilityDetailRes();
		res.setFacilityDetails(facilityDetails);
		return res;
	}

	/**
	 * Method Name: saveFacilityDetails Method Description: This method is used
	 * to saveFacilityDetails (CRES10S)
	 * 
	 * @param facilityDetailSaveReq
	 * @return Cres10soDto
	 */
	@RequestMapping(value = "/saveFacilityDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FacilityDetailRes saveFacilityDetails(@RequestBody FacilityDetailSaveReq facilityDetailSaveReq) {
		FacilityDetailRes facilityDetailRes = facilityDetailService.saveFacilityDetails(facilityDetailSaveReq);
		return facilityDetailRes;
	}

	/**
	 * Method Name: save Method Description: This method is used
	 * to saveHeightenedMonitoringStatus
	 *
	 * @param facilityDetailSaveReq
	 * @return Cres10soDto
	 */
	@RequestMapping(value = "/saveHeightenedMonitoringStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FacilityDetailRes saveHeightenedMonitoringStatus(@RequestBody FacilityDetailSaveReq facilityDetailSaveReq) {
		FacilityDetailRes facilityDetailRes = facilityDetailService.saveFacilityDetails(facilityDetailSaveReq);
		return facilityDetailRes;
	}

	/**
	 * PPM 60692-artf178534 - method to get HM status history
	 * @param hmStatusReq
	 * @return HmStatusRes
	 */
	@RequestMapping(value = "/getHmStatusDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public HmStatusRes getHmStatusDetails(@RequestBody HmStatusReq hmStatusReq) {
		List<HeightenedMonitoringDto> hmStatusHistoryList = null;
		if (TypeConvUtil.isNullOrEmpty(hmStatusReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(hmStatusReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return heightenedMonitoringService.getHmStatusDetails(hmStatusReq);
	}

	/**
	 * PPM 60692-artf178534 - method to get Facility type and parent Id for a given child resoirce Id
	 * @param idResource
	 * @return HmStatusRes
	 */
	@RequestMapping(value = "/getFacilTypAndParentRsrc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public HmStatusRes getFacilTypAndParentRsrc(@RequestBody HmStatusReq hmStatusReq) {
		HeightenedMonitoringDto hmDto = null;
		if (TypeConvUtil.isNullOrEmpty(hmStatusReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(hmStatusReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return heightenedMonitoringService.getFacilTypAndParentRsrc(hmStatusReq);
	}

	/**
	 * MethodName: getPendingApprovalCount MethodDescription: Fetch Pending
	 * Approval Count of the Pending Approvals for the given Approval Id.
	 *
	 * @param ApprovalReq
	 * @return ApprovalRes
	 *
	 */
	@RequestMapping(value = "/getOpenHTFacilityServiceTypeCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FacilityDetailRes getOpenHTFacilityServiceTypeCount(@RequestBody FacilityDetailReq facilityDetailReq) {

		if (TypeConvUtil.isNullOrEmpty(facilityDetailReq.getIdResource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approval.idApproval.mandatory", null, Locale.US));
		}

		FacilityDetailRes facilityDetailRes = new FacilityDetailRes();
		facilityDetailRes.setOpenHTCount(facilityDetailService.getOpenHTFacilityServiceTypeCount(facilityDetailReq.getIdResource()));
		return facilityDetailRes;
	}
}
