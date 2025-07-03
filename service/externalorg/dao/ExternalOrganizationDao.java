/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This interface is used for Add External Organization and External Organization Detail
 *Jul 5, 2018- 2:58:30 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.dao;

import us.tx.state.dfps.common.externalorg.dto.OrganizationDetailDto;

public interface ExternalOrganizationDao {
	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description: This method is
	 * used to fetch the external organization detail
	 * 
	 * @param idEin
	 * @return OrganizationDetailDto
	 */
	public OrganizationDetailDto fetchExternalOrganization(Long idEin);

	/**
	 * 
	 * Method Name: addExternalOrganization Method Description:This method is
	 * used to save the external organization into DB.
	 * 
	 * @param organizationDetailDto
	 * @return Long
	 */
	public Long addExternalOrganization(OrganizationDetailDto organizationDetailDto);

	/**
	 * 
	 * Method Name: updateExternalOrganization Method Description:This method is
	 * used to update the already existing record into DB.
	 * 
	 * @param organizationDetailDto
	 * @return OrganizationDetailRes
	 */
	public OrganizationDetailDto updateExternalOrganization(OrganizationDetailDto organizationDetailDto);

	/**
	 * 
	 * Method Name: deleteExternalOrganization Method Description:This method is
	 * used to delete the Organization from DB.
	 * 
	 * @param idOrgDtl
	 */
	public String deleteExternalOrganization(Long idOrgDtl);

	/**
	 * 
	 * Method Name: deleteEIPDetails Method Description: This method is used to
	 * delete the Identifier detail, Phone detail and Email details related to
	 * particular organization.
	 * 
	 * @param idEip
	 * @param tableName
	 * @return String
	 */
	public String deleteEIPDetails(Long idEip, String tableName);

	/**
	 * 
	 * Method Name: getIdentifierDtls Method Description: This method is used to
	 * check if there is already a TIN Identifier Exists with the given ID. for
	 * any other organization.
	 * 
	 * @param idTIN
	 * @param idOrgDtl
	 * @return boolean
	 */

	public boolean getIdentifierDtls(String idTIN, Long idOrgDtl);

	/**
	 * 
	 * Method Name: validateOrgDetail Method Description:This method is used to
	 * Validate the Org details
	 * 
	 * @param idTIN
	 * @param idOrgDtl
	 */
	public boolean validateOrgDetail(String nmLegal, Long idOrgDtl);
}
