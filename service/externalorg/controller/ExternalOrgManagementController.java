/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This class is used as rest service controller to control all
 *the request from web client for respective request for External Organization
 *Jul 9, 2018- 3:27:04 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ExternalOrgSearchReq;
import us.tx.state.dfps.service.common.request.OrganizationDetailReq;
import us.tx.state.dfps.service.common.response.ExternalOrgSearchRes;
import us.tx.state.dfps.service.common.response.OrganizationDetailRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.externalorg.service.ExternalOrgSearchService;
import us.tx.state.dfps.service.externalorg.service.ExternalOrganizationService;

@RestController
@RequestMapping("/externalOrgMngmnt")
public class ExternalOrgManagementController {
	private static final String TRANSACTION_ID = "TransactionId :";
	private static final Logger LOG = Logger.getLogger("ServiceBusiness-ExternalOrganizationControllerLog");

	public ExternalOrgManagementController() {
	}

	public static final Logger log = Logger.getLogger(ExternalOrgManagementController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	ExternalOrgSearchService externalOrgSearchService;

	@Autowired
	ExternalOrganizationService externalOrganizationService;

	/**
	 * Method Name: executeOrgSearch Method Description: Rest Controller method
	 * to call the Organization Search Service.
	 * 
	 * @param searchReq
	 * @return response
	 */
	@RequestMapping(value = "/searchOrganizations", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExternalOrgSearchRes executeOrgSearch(@RequestBody ExternalOrgSearchReq searchReq) {
		log.info("Service executeOrgSearch : Execution Started");
		ExternalOrgSearchRes response = null;
		if (ObjectUtils.isEmpty(searchReq.getSearchParam())) {
			// If no Search parameter is found in the request object, Throw
			// invalid req as
			// search parameter is not found.
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrg.searchParam.mandatory", null, Locale.US));
		}
		// If Search parameters are available then execute the search.
		response = externalOrgSearchService.executeOrganizationSearch(searchReq.getSearchParam());
		log.info("Service executeOrgSearch : Return Response");
		return response;
	}

	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description:This method is
	 * used to fetch the external organization detail
	 * 
	 * @param organizationDetailReq
	 * @return OrganizationDetailRes
	 */
	@RequestMapping(value = "/fetchExternalOrg", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OrganizationDetailRes fetchExternalOrganization(
			@RequestBody OrganizationDetailReq organizationDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(organizationDetailReq)
				|| TypeConvUtil.isNullOrEmpty(organizationDetailReq.getIdEin())) {
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrgDtl.idEin.mandatory", null, Locale.US));
		}
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		organizationDetailRes.setOrganizationDetailDto(
				externalOrganizationService.fetchExternalOrganization(organizationDetailReq.getIdEin()));
		// below code is to populate the person Name for the Status history list
		if (!ObjectUtils.isEmpty(organizationDetailRes.getOrganizationDetailDto()) && !ObjectUtils
				.isEmpty(organizationDetailRes.getOrganizationDetailDto().getListOrganizationStatusHistoryDto())) {
			organizationDetailRes.getOrganizationDetailDto().getListOrganizationStatusHistoryDto().stream()
					.forEach(orgStatusHistoryDto -> {
						if (!ObjectUtils.isEmpty(orgStatusHistoryDto)
								&& !ObjectUtils.isEmpty(orgStatusHistoryDto.getIdCreatedPerson())) {
							orgStatusHistoryDto.setNmUpdatePerson(externalOrganizationService
									.getPersonName(orgStatusHistoryDto.getIdCreatedPerson()));
						}
					});
		}
		LOG.debug(TRANSACTION_ID + organizationDetailReq.getTransactionId());
		return organizationDetailRes;

	}

	/**
	 * 
	 * Method Name: externalOrganizationAUD Method Description:This method is
	 * used to Add/Update/Delete the External organization details
	 * 
	 * @param organizationDetailReq
	 * @return OrganizationDetailRes
	 */
	@RequestMapping(value = "/externalOrgAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OrganizationDetailRes externalOrganizationAUD(
			@RequestBody OrganizationDetailReq organizationDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(organizationDetailReq)
				|| TypeConvUtil.isNullOrEmpty(organizationDetailReq.getOrganizationDetailDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrgDtl.OrganizationDetailDto.mandatory", null, Locale.US));
		}
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		// Calling the Service to add/update/delete the organization detail.
		organizationDetailRes = externalOrganizationService.externalOrganizationAUD(organizationDetailReq);
		LOG.debug(TRANSACTION_ID + organizationDetailReq.getTransactionId());
		return organizationDetailRes;

	}

	@RequestMapping(value = "/getExtOrgDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OrganizationDetailRes getExtOrgDtls(@RequestBody OrganizationDetailReq organizationDetailReq) {

		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		organizationDetailRes
				.setOrganizationDetailDto(externalOrganizationService.getExtOrgDtls(organizationDetailReq));
		return organizationDetailRes;

	}

	/**
	 * 
	 * Method Name: deleteExternalOrgDetails Method Description: This method is
	 * used to delete the Identifier detail, Phone detail and Email details
	 * related to particular organization.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	@RequestMapping(value = "/externalOrgEIP", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OrganizationDetailRes deleteEIPDetails(
			@RequestBody OrganizationDetailReq organizationDetailReq) {
		if (ObjectUtils.isEmpty(organizationDetailReq) || ObjectUtils.isEmpty(organizationDetailReq.getIdEip())) {
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrgDtl.idEip.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(organizationDetailReq) || ObjectUtils.isEmpty(organizationDetailReq.getTableName())) {
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrgDtl.tableName.mandatory", null, Locale.US));
		}
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		// Calling the Service to delete the requested row from table.
		String status = externalOrganizationService.deleteEIPDetails(organizationDetailReq);
		LOG.debug(TRANSACTION_ID + organizationDetailReq.getTransactionId());
		organizationDetailRes.setStatus(status);
		return organizationDetailRes;

	}

	/**
	 * 
	 * Method Name: validateOrgDetail Method Description: This method is used to
	 * check if there is already a EIN , Legal Name and TIN Identifier Exists
	 * with the given ID for any other organization.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	@RequestMapping(value = "/validateOrgDetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OrganizationDetailRes validateOrgDetail(
			@RequestBody OrganizationDetailReq organizationDetailReq) {
		if (ObjectUtils.isEmpty(organizationDetailReq)
				|| ObjectUtils.isEmpty(organizationDetailReq.getOrganizationDetailDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("externalOrgDtl.OrganizationDetailDto.mandatory", null, Locale.US));
		}
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		organizationDetailRes = externalOrganizationService.validateOrgDetail(organizationDetailReq);
		return organizationDetailRes;

	}

}
