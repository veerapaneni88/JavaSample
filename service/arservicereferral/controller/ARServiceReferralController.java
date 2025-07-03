package us.tx.state.dfps.service.arservicereferral.controller;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.arservicereferral.service.ARServiceReferralService;
import us.tx.state.dfps.service.common.request.ARServRefDetailReq;
import us.tx.state.dfps.service.common.request.ARServRefListReq;
import us.tx.state.dfps.service.common.request.ARServiceReferralUpdtReq;
import us.tx.state.dfps.service.common.response.ARServRefDetailRes;
import us.tx.state.dfps.service.common.response.ARServRefListRes;
import us.tx.state.dfps.service.common.response.ARServiceReferralUpdtRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ARServiceReferralController for ARServiceReferralBean. Alternative Response
 * Service referral EJB to save, retrieve, update and delete service referral
 * values. Sep 6, 2017- 8:08:06 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("/arServiceReferral")
public class ARServiceReferralController {

	@Autowired
	ARServiceReferralService arServiceReferralService;

	@Autowired
	MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-ARServiceReferralControllerLog");

	/**
	 * Method Name: getARServRefDetails Method Description: This method fetches
	 * ServiceReferral values for ServiceAndReferrals List Page for a stage ID
	 * or service referral ID.
	 * 
	 * @param arServiceReferralReq
	 * @return arServiceReferralSaveRes
	 */
	@RequestMapping(value = "/getARServRefDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARServRefListRes getARServRefDetails(@RequestBody ARServRefListReq arServiceReferralReq) {

		if (TypeConvUtil.isNullOrEmpty(arServiceReferralReq.getStageId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ARServiceReferralDelReq.mandatory", null, Locale.US));
		}
		
		LOG.debug("Exiting method getARServRefDetails in ARServiceReferralController");
		return arServiceReferralService.getARServRefDetails(arServiceReferralReq.getStageId());
	}

	/**
	 * Method Name: getARServicesReferralsDetails Method Description: This
	 * method fetches Service Referral Details for a stage ID or service
	 * referral ID.
	 * 
	 * @param arServRefDetailReq
	 * @return ARServRefDetailRes
	 * 
	 */
	@RequestMapping(value = "/getARServicesReferralsDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARServRefDetailRes getARServicesReferralsDetails(@RequestBody ARServRefDetailReq arServRefDetailReq) {
		ARServRefDetailRes arServRefDetailRes = new ARServRefDetailRes();
		if (TypeConvUtil.isNullOrEmpty(arServRefDetailReq.getIdStage())
				&& TypeConvUtil.isNullOrEmpty(arServRefDetailReq.getIdServRefChklist())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ARServiceReferralDelReq.mandatory", null, Locale.US));
		}
		arServRefDetailRes = arServiceReferralService.getARServiceReferralsDetails(arServRefDetailReq);
		LOG.debug("Exiting method getARServicesReferralsDetails in ARServiceReferralController");
		return arServRefDetailRes;
	}

	/**
	 * Method Name: getsaveARServRefDetails Method Description: Method to save
	 * ar service referral values.
	 * 
	 * @param arServiceReferralSaveReq
	 * @return arServiceReferralSaveRes
	 */
	@RequestMapping(value = "/saveARServRefDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARServRefListRes getsaveARServRefDetails(@RequestBody ARServRefListReq arServiceReferralSaveReq) {
		if (TypeConvUtil.isNullOrEmpty(arServiceReferralSaveReq.getArServRefList())) {
			throw new InvalidRequestException(messageSource
					.getMessage("arServiceReferralSaveReq.getArServiceReferralDto.mandatory", null, Locale.US));
		}
		ARServRefListRes arServRefListRes = new ARServRefListRes();
		List<ARServRefListDto> arServRefList = arServiceReferralService.saveARServRefDetails(arServiceReferralSaveReq);
		arServRefListRes.setARList(arServRefList);
		LOG.debug("Exiting method getsaveARServRefDetails in ARServiceReferralController");
		return arServRefListRes;
	}

	/**
	 * Method Name: getdeleteARServiceReferral Method Description: Method to
	 * delete ar service referral based on service referral id(s).
	 * 
	 * @param arServiceReferralDelReq
	 * @return arServiceReferralSaveRes
	 * 
	 */
	@RequestMapping(value = "/deleteARServiceReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARServRefListRes getdeleteARServiceReferral(@RequestBody ARServRefListReq arServiceReferralDelReq) {

		String indexStr = arServiceReferralDelReq.getIndex();
		int[] ids = TypeConvUtil.isNullOrEmpty(indexStr) ? new int[0] : getIndexArray(indexStr);
		List<ARServRefListDto> arServRefList = arServiceReferralDelReq.getArServRefList();
		ARServRefListDto arServRefListDto = null;
		ARServRefListRes arServRefListRes = new ARServRefListRes();
		for (int index : ids) {
			arServRefListDto = arServRefList.get(index);
			if (TypeConvUtil.isNullOrEmpty(arServRefListDto.getIdArServRefChklist())
					&& TypeConvUtil.isNullOrEmpty(arServiceReferralDelReq.getStageId())) {
				throw new InvalidRequestException(messageSource.getMessage(
						"arServiceReferralSaveReq.getIdServiceReferrals.getIdStage.mandatory", null, Locale.US));
			}
			arServRefListRes = arServiceReferralService.deleteARServiceReferral(
					arServRefListDto.getIdArServRefChklist(), arServiceReferralDelReq.getStageId());
		}

		LOG.debug("Exiting method getdeleteARServiceReferral in ARServiceReferralController");
		return arServRefListRes;
	}

	/**
	 * Method Name: getupdateMultipleServRefs Method Description: Updates
	 * multiple service referrals, with new comments and final outcome.
	 * 
	 * @param arServiceReferralUpdtReq
	 * @return arServiceReferralSaveRes
	 * 
	 */
	
	@RequestMapping(value = "/updateMultipleServRefs", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARServiceReferralUpdtRes getupdateMultipleServRefs(
			@RequestBody ARServiceReferralUpdtReq arServiceReferralUpdtReq) {
		if (TypeConvUtil.isNullOrEmpty(arServiceReferralUpdtReq.getIdServiceReferrals())
				&& TypeConvUtil.isNullOrEmpty(arServiceReferralUpdtReq.getTxtComments())
				&& TypeConvUtil.isNullOrEmpty(arServiceReferralUpdtReq.getCdFinalOutcome())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"arServiceReferralSaveReq.getIdServiceReferrals.getIdStage.getTxtComments.getCdFinalOutcome.mandatory",
					null, Locale.US));
		}
		LOG.info("TransactionId :" + arServiceReferralUpdtReq.getTransactionId());
		return arServiceReferralService.updateMultipleServRefs(arServiceReferralUpdtReq.getIdServiceReferrals(),
				arServiceReferralUpdtReq.getTxtComments(), arServiceReferralUpdtReq.getCdFinalOutcome());
	}
	 

	/**
	 * Gets the index array.
	 *
	 * @param index the index
	 * @return the index array
	 */
	private int[] getIndexArray(String index) {
		int[] ids = new int[0];
		if (null != index) {
			String[] indexes = StringUtils.split(index, ",");
			ids = (null != indexes) ? new int[indexes.length] : new int[0];
			int i = 0;
			for (String s : indexes)
				ids[i++] = Integer.parseInt(s);
		}
		return ids;
	}
}
