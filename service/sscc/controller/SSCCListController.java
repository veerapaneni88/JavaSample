package us.tx.state.dfps.service.sscc.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SSCCListReq;
import us.tx.state.dfps.service.common.response.SSCCListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sscc.service.SSCCListService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * contains method implementations for SSCCListController Oct 26, 2017- 3:48:53
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/ssccList")
public class SSCCListController {

	@Autowired
	private SSCCListService ssccListService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-SSCCListControllerLog");
	public static final String SSCCLIST_USERDTO_MANDATORY = "ssccList.userDto.mandatory";

	@RequestMapping(value = "/displaySSCCList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes displaySSCCList(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method displaySSCCList in SSCCListController");

		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListHeaderDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccList.ssccListHeaderDto.mandatory", null, Locale.US));
		}
		return ssccListService.displaySSCCListBean(ssccListReq);
	}

	/**
	 * 
	 * Method Name: saveSSCCList Method Description:Inserts a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListReq
	 * @return ssccListRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/saveSSCCList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes saveSSCCList(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method saveSSCCList in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListDto())) {
			throw new InvalidRequestException(messageSource.getMessage("SSCCLIST_USERDTO_MANDATORY", null, Locale.US));
		}

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setTransactionId(ssccListReq.getTransactionId());
		ssccListRes.setSsccListDto(ssccListService.saveSSCCList(ssccListReq.getSsccListDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + ssccListReq.getTransactionId());
		LOG.debug("Exiting method saveSSCCList in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: fetchSSCCList Method Description:Fetches a row from the
	 * SSCC_LIST table using Referral Id
	 * 
	 * @param ssccListReq
	 * @return ssccListRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/fetchSSCCList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes fetchSSCCList(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method fetchSSCCList in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getIdSSCCReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setTransactionId(ssccListReq.getTransactionId());
		ssccListRes.setSsccList(ssccListService.fetchSSCCList(ssccListReq.getIdSSCCReferral()));
		LOG.info(ServiceConstants.TRANSACTION_ID + ssccListReq.getTransactionId());
		LOG.debug("Exiting method fetchSSCCList in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListReq
	 * @return ssccListRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/updateSSCCList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes updateSSCCList(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method updateSSCCList in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListDto())) {
			throw new InvalidRequestException(messageSource.getMessage(SSCCLIST_USERDTO_MANDATORY, null, Locale.US));
		}

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setTransactionId(ssccListReq.getTransactionId());
		ssccListRes.setIdSsccList(ssccListService.updateSSCCList(ssccListReq.getSsccListDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + ssccListReq.getTransactionId());
		LOG.debug("Exiting method updateSSCCList in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: userHasAccessToSSCCListPage Method Description:Returns true
	 * if the region is a valid SSCC Catchment region
	 * 
	 * @param ssccListReq
	 * @return ssccListRes
	 * 
	 * 
	 */
	@RequestMapping(value = "/userHasAccessToSSCCListPage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes userHasAccessToSSCCListPage(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method userHasAccessToSSCCListPage in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getUserDto())) {
			throw new InvalidRequestException(messageSource.getMessage(SSCCLIST_USERDTO_MANDATORY, null, Locale.US));
		}

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setTransactionId(ssccListReq.getTransactionId());
		ssccListRes
				.setIsValidSSCCCatchmentRegion(ssccListService.userHasAccessToSSCCListPage(ssccListReq.getUserDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + ssccListReq.getTransactionId());
		LOG.debug("Exiting method userHasAccessToSSCCListPage in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: fetchCdCatchmentFromIdCatchment Method Description: fetch
	 * SSCC CdCatchment From IdCatchment
	 * 
	 * @param ssccListReq
	 *            isUserSSCCExternal ssccList/getUserSSCC
	 * @return ssccListRes
	 */
	@RequestMapping(value = "/fetchCdCatchment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes fetchCdCatchmentFromIdCatchment(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method  fetchCdCatchmentFromIdCatchment in SSCCListController");
		/*
		 * if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListDto())) { throw
		 * new InvalidRequestException(messageSource.getMessage(
		 * SSCCLIST_USERDTO_MANDATORY, null, Locale.US)); }
		 */

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setSsccListDto(ssccListService.fetchCdCatchmentFromIdCatchment(ssccListReq.getSsccListDto()));
		LOG.debug("Exiting method fetchCdCatchmentFromIdCatchment in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: isUserSSCCExternal Method Description: fetch SSCC
	 * CdCatchment From IdCatchment
	 * 
	 * @param ssccListReq
	 *            isUserSSCCExternal ssccList/getUserSSCC
	 * @return ssccListRes
	 */
	@RequestMapping(value = "/getUserSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes isUserSSCCExternal(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method isUserSSCCExternal in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccList.ssccListDto.mandatory", null, Locale.US));
		}

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setUserSSCCExternal(ssccListService.isUserSSCCExternal(ssccListReq.getSsccListDto()));
		LOG.debug("Exiting method isUserSSCCExternal in SSCCListController");
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: isUserSSCCExternal Method Description: fetch SSCC
	 * CdCatchment From IdCatchment
	 * 
	 * @param ssccListReq
	 *            isUserSSCCExternal ssccList/getUserSSCC
	 * @return ssccListRes
	 */
	@RequestMapping(value = "/getSSCCCatchmentAccess", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCListRes getSSCCCatchmentAccess(@RequestBody SSCCListReq ssccListReq) {
		LOG.debug("Entering method getSSCCCatchmentAccess in SSCCListController");
		if (TypeConvUtil.isNullOrEmpty(ssccListReq.getSsccListDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ssccList.ssccListDto.mandatory", null, Locale.US));
		}

		SSCCListRes ssccListRes = new SSCCListRes();
		ssccListRes.setSsccCatchmentAccess(ssccListService.userHasSSCCCatchmentAccess(ssccListReq.getSsccListDto()));
		LOG.debug("Exiting method getSSCCCatchmentAccess in SSCCListController");
		return ssccListRes;
	}
}
