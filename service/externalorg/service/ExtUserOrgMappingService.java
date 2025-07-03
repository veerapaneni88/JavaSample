/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External User Organization Mapping Service.
 *July 17, 2018- 3:33:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.service;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExtUserMgmntReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingHistoryReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingListReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgResourceLinkDelReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExtUserMgmntRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingHistoryRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingListRes;

public interface ExtUserOrgMappingService {

	/**
	 * Method Name: getExtUserOrgMappingList Method Description:This method is
	 * used to get the resources in an external organization.
	 * 
	 * @param extUserOrgMappingListReq
	 * @return ExtUserOrgMappingListRes
	 */
	ExtUserOrgMappingListRes getExtUserOrgMappingList(ExtUserOrgMappingListReq extUserOrgMappingListReq);

	/**
	 * Method Name: getOrgResourcesList Method Description:This method is used
	 * to get the resources in an external organization.
	 * 
	 * @param idOrgDtl
	 * @param indRCCPUser
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes getOrgResourcesList(Long idOrgDtl, boolean indRCCPUser);

	/**
	 * Method Name: deleteOrgResources Method Description:This method is used to
	 * delete the resources from an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes deleteOrgResources(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: addOrganizationResource Method Description:This method is
	 * used to add resources to an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes addOrganizationResource(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: saveAssignOrgRoleDtls Method Description:This method is used
	 * to save the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes saveAssignOrgRoleDtls(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: getAssignOrgRoleDtls Method Description:This method is used
	 * to get the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes getAssignOrgRoleDtls(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: getExtUserOrgMappingHistory Method Description:This m
	 * 
	 * @param ExtUserOrgMappingHistoryReq
	 * @return
	 */
	ExtUserOrgMappingHistoryRes getExtUserOrgMappingHistory(ExtUserOrgMappingHistoryReq req);

	/**
	 * 
	 * Method Name: deleteExtUserOrgResourceLink Method Description:
	 * 
	 * @param req
	 * @return
	 */
	ServiceResHeaderDto deleteExtUserOrgResourceLink(ExtUserOrgResourceLinkDelReq req);

	/*
	 * Method Name: saveExtUserRsrcLink Method Description:
	 * 
	 * @param orgUserLink
	 * 
	 * @param idUser
	 * 
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes saveExtUserRsrcLink(ExternalOrgDto orgUserLink, Long idUser);

	/**
	 * Method Name: getExtUserRsrcDtls Method Description:
	 * 
	 * @param extUserMgmntReq
	 * @return
	 */
	public ExtUserMgmntRes getExtUserRsrcDtls(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: deleteExtUserRsrcAssociation Method Description:
	 * 
	 * @param extUserMgmntReq
	 * @return
	 */
	public ExtUserMgmntRes deleteExtUserRsrcAssociation(ExtUserMgmntReq extUserMgmntReq);

	/**
	 * Method Name: setExtrnlUserLoginAgrmntDtl Method Description: This method
	 * will interact with dao layer method to update external user agreement
	 * accept detail
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public CommonHelperRes setExtrnlUserLoginAgrmntDtl(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: checkExtUserBgCheckClear Method Description:This method will
	 * interact with the dao implementation to check if the background check for
	 * the user is cleared and active.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes checkExtUserBgCheckClear(ExtUserMgmntReq extUserMgmntReq);

}
