package us.tx.state.dfps.service.subcontractor.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.request.SubcontrAreaServedReq;
import us.tx.state.dfps.service.common.request.SubcontrListRtrvReq;
import us.tx.state.dfps.service.common.request.SubcontrListSaveReq;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.response.SubcontrAreaServedRes;
import us.tx.state.dfps.service.common.response.SubcontrListRtrvRes;
import us.tx.state.dfps.service.common.response.SubcontrListSaveRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.subcontractor.dto.SubcontrListSaveiDto;
import us.tx.state.dfps.service.subcontractor.service.SbcntrListService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: This class will perform an add, update and/or delete to
 * the Resource Link table. Service Name :CCON16S
 * 
 * Aug 16, 2017- 2:40:39 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/subcontractor")
public class SubcontrListController {

	@Autowired
	SbcntrListService sbcntrListService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SbcntrListSaveValidator validator;

	/**
	 * This service will perform an add, update and/or delete to the Resource
	 * Link table.
	 * 
	 * Service name : CCON16S
	 * 
	 * @param objSbcntrListSaveiDto
	 * @return SbcntrListSaveRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SubcontrListSaveRes saveSubContractorList(@RequestBody SubcontrListSaveReq subcontrListSaveReq) {
		SubcontrListSaveRes response = null;
		List<SubcontrListSaveiDto> liSbcntrListSaveiDto = subcontrListSaveReq.getSbcntrListSaveiDtoList();
		if (TypeConvUtil.isNullOrEmpty(subcontrListSaveReq)) {
			throw new ServiceLayerException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		validator.validateInputs(liSbcntrListSaveiDto);
		// Invoking service method
		response = sbcntrListService.saveSubContractorList(liSbcntrListSaveiDto);
		return response;
	}

	/**
	 * 
	 * Method Name: findSubcontractor
	 * 
	 * Method Description:This service will retrieve all resources that have
	 * been designated as sub contractors for the prime resource. It will also
	 * retrieve all services for which the prime resource may provide. Service
	 * name : CCON15S
	 * 
	 * @param objSbcntrListRtrviDto
	 * @return SubcontrListRtrvRes @ @
	 */
	@RequestMapping(value = "/retrieve", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SubcontrListRtrvRes findSubcontractor(@RequestBody SubcontrListRtrvReq sbcntrListRtrviDto) {
		if (TypeConvUtil.isNullOrEmpty(sbcntrListRtrviDto.getIdRsrcLinkParent())) {
			throw new ServiceLayerException(
					messageSource.getMessage("sbcntrList.uRsrcLinkParent.mandatory", null, Locale.US));
		}
		return sbcntrListService.findSubcontractor(sbcntrListRtrviDto);
	}

	/**
	 * 
	 * Method Name: SbcntrListRtrvo
	 * 
	 * Method Description:This is the retrieval service for the Area Served
	 * window in order to populate the Area Served list in the predisplay. Rows
	 * will only be returned if the show indicator is set to yes. In addition,
	 * it will be determined if the service is contracted and stored in the
	 * contracted indicator for each row returned. prime resource may provide.
	 * 
	 * Service name : CRES05S
	 * 
	 * @param subcontrAreaServedReq
	 * @return SubcontrListRtrvRes @
	 */
	@RequestMapping(value = "/areaserved", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SubcontrAreaServedRes getAreaServedList(@RequestBody SubcontrAreaServedReq subcontrAreaServedReq) {
		if (TypeConvUtil.isNullOrEmpty(subcontrAreaServedReq.getIdResource())) {
			throw new ServiceLayerException(
					messageSource.getMessage("sbcntrList.uRsrcLinkParent.mandatory", null, Locale.US));
		}
		return sbcntrListService.getAreaServedList(subcontrAreaServedReq);
	}

	/**
	 * 
	 * Method Name: getResourceInfo
	 * 
	 * Method Description: This Method is used to get the basic resource
	 * information for validations
	 * 
	 * @param resourceReq
	 *            - request containing resource id
	 * @return ResourceRes - response with resource Information @
	 */

	@RequestMapping(value = "/resourceInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceRes getResourceInfo(@RequestBody ResourceReq resourceReq) {
		if (TypeConvUtil.isNullOrEmpty(resourceReq)) {
			throw new ServiceLayerException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(resourceReq.getIdResource())) {
			throw new ServiceLayerException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		return sbcntrListService.getResourceInfoById(resourceReq);
	}

}
