package us.tx.state.dfps.service.kin.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.KinPlacementInfoReq;
import us.tx.state.dfps.service.common.response.KinPlacementInfoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kin.service.KinPlacementInfoService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * executes the method in KinPlacementInfoBean Sep 6, 2017- 3:06:19 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/kinplacement")
public class KinPlacementInfoController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	KinPlacementInfoService kinPlacementInfoService;

	private static final Logger log = Logger.getLogger(KinPlacementInfoController.class);

	/**
	 * Method Name: getResourceDetails Method Description:Gets the Resource
	 * Details based on the personId if the person is Primary Kinship Caregiver.
	 * 
	 * @param kinPlacementInfoReq
	 * @return KinPlacementInfoRes
	 */
	@RequestMapping(value = "/getResourceDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  KinPlacementInfoRes getResourceDetails(@RequestBody KinPlacementInfoReq kinPlacementInfoReq) {

		log.info("TransactionId :" + kinPlacementInfoReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(kinPlacementInfoReq.getIdperson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("kinPlacementInfo.personId.mandatory", null, Locale.US));
		}
		KinPlacementInfoRes kinPlacementInfoRes = new KinPlacementInfoRes();
		kinPlacementInfoRes.setKinPlacementInfoValueDto(
				kinPlacementInfoService.getResourceDetails(kinPlacementInfoReq.getIdperson()));
		return kinPlacementInfoRes;
	}

	/**
	 * Method Name: getHomeStatus Method Description: Gets the status of the
	 * home based on the resourceId
	 * 
	 * @param kinPlacementInfoReq
	 * @return KinPlacementInfoRes
	 */
	@RequestMapping(value = "/getHomeStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  KinPlacementInfoRes getHomeStatus(@RequestBody KinPlacementInfoReq kinPlacementInfoReq) {

		log.info("TransactionId :" + kinPlacementInfoReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(kinPlacementInfoReq.getIdresource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("kinPlacementInfo.resourceId.mandatory", null, Locale.US));
		}

		KinPlacementInfoRes kinPlacementInfoRes = new KinPlacementInfoRes();
		kinPlacementInfoRes.setKinPlacementInfoValueDto(
				kinPlacementInfoService.getHomeStatus(kinPlacementInfoReq.getIdresource()));
		return kinPlacementInfoRes;
	}
}
