/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External User Organization Role Mapping Data access Layer.
 *July 17, 2018- 3:33:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.OrgDtl;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingHistoryDtl;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExtUserMgmntRes;

public interface ExtUserOrgMappingDao {

	/**
	 * Method Name: getExtUserOrgMappingList Method Description:This method is
	 * used to get the resources in an external organization.
	 * 
	 * @param idPerson
	 * @return
	 */
	ExtOrgRoleMappingDto getExtUserOrgMappingList(Long idPerson);

	/**
	 * Method Name: getOrgResourcesList Method Description:This method is used
	 * to get the resources in an external organization.
	 * 
	 * @param idOrgEin
	 * @return ExtOrgRoleMappingDto
	 */
	public ExtOrgRoleMappingDto getOrgResourcesList(Long idOrgEin, boolean indRCCPUser);

	/**
	 * Method Name: deleteOrgResources Method Description:This method is used to
	 * delete the resources from an external organization.
	 * 
	 * @param idOrgDtl
	 * @param idResourcesList
	 */
	public void deleteOrgResources(Long idOrgDtl, List<Long> idResourcesList);

	/**
	 * Method Name: addOrganizationResource Method Description:This method is
	 * used to add resources to an external organization.
	 * 
	 * @param idOrgDtl
	 * @param idResourcesList
	 * @param idUser
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes addOrganizationResource(Long idOrgDtl, List<Long> idResourcesList, Long idUser);

	/**
	 * Method Name: saveAssignOrgRoleDtls Method Description:This method is used
	 * to save the Assign Org & Role details.
	 * 
	 * @param extOrgRoleMappingDto
	 * @return ExtUserMgmntRes
	 */
	public ExtUserMgmntRes saveAssignOrgRoleDtls(ExtOrgRoleMappingDto extOrgRoleMappingDto, Long idUser);

	/**
	 * Method Name: getAssignOrgRoleDtls Method Description:This method is used
	 * to get the Assign Org & Role details.
	 * 
	 * @param idExtUserOrgLink
	 * @return ExtOrgRoleMappingDto
	 */
	public ExtOrgRoleMappingDto getAssignOrgRoleDtls(Long idExtUserOrgLink);

	/**
	 * Method Name: getHistoryOrgLink Method Description: This method used to
	 * get the history records associated to idExtUserOrgLink
	 * 
	 * @param idExtUserOrgLink
	 * @return
	 */
	List<ExtOrgRoleMappingHistoryDtl> getExtUserOrgMappingHistory(Long idExtUserOrgLink);

	/**
	 * 
	 * Method Name: deleteExtUserOrgLinks Method Description:
	 * 
	 * @param idPerson
	 * @param idExtUserOrgLink
	 * @return
	 */
	ServiceResHeaderDto deleteExtUserOrgLinks(Long idPerson, Long idExtUserOrgLink, OrgDtl orgDtl);

	/**
	 * 
	 * Method Name: deleteExtUserResRoles Method Description:
	 * 
	 * @param idExtUserRsrcLink
	 * @param idOrgRoleDtl
	 * @return
	 */
	ServiceResHeaderDto deleteExtUserResRoles(Long idExtUserRsrcLink, Long idOrgRoleDtl);

	public Long saveExtUserRsrcLink(ExternalOrgDto orgUserLink, Long idUser);

	/**
	 * Method Name: getExtUserRsrcDtls Method Description:This method is used to
	 * retrieve the external user resource association details.
	 * 
	 * @param idExtUserOrgLink
	 * @return ExtOrgRoleMappingDto
	 */
	public ExtOrgRoleMappingDto getExtUserRsrcDtls(Long idExtUserOrgLink);

	/**
	 * Method Name: deleteExtUserRsrcAssociation Method Description:This method
	 * is used to delete the external user to resource association.
	 * 
	 * @param idExtUserRsrcLink
	 */
	public void deleteExtUserRsrcAssociation(Long idExtUserRsrcLink);

	/**
	 * 
	 * Method Name: getExternaluserId, this method is to get the User id Method
	 * Description:
	 * 
	 * @param idPerson
	 * @return
	 */
	public String getExternaluserId(Long idPerson);

	/**
	 * Method Name: setExtrnlUserLoginAgrmntDtl Method Description:This method
	 * will update agreement accept related column when user first time login
	 * and clicks on Agree button over agreement consent page
	 * 
	 * @param idExtrnlUser
	 */
	public CommonHelperRes setExtrnlUserLoginAgrmntDtl(Long idExtrnlUser);

	/**
	 * Method Name: checkExtUserBgCheckClear Method Description: Method Name:
	 * checkExtUserBgCheckClear Method Description:This method will check if the
	 * background check for the user is cleared and active.
	 * 
	 * @param idUser
	 * @param idOrgEin
	 * @param orgRsrcAsscPageName
	 * @return ErrorDto
	 */
	public ErrorDto checkExtUserBgCheckClear(Long idUser, Long idOrgEin, String orgRsrcAsscPageName);

}
