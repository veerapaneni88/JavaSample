/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This interface is used for Add External Organization and External Organization Detail
 *Jul 5, 2018- 2:37:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.service;

import us.tx.state.dfps.common.externalorg.dto.OrganizationDetailDto;
import us.tx.state.dfps.service.common.request.OrganizationDetailReq;
import us.tx.state.dfps.service.common.response.OrganizationDetailRes;

public interface ExternalOrganizationService {
	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description: This method is
	 * used to fetch the organization detail
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public OrganizationDetailDto fetchExternalOrganization(Long idEin);

	/**
	 * 
	 * Method Name: externalOrganizationAUD Method Description:This method is
	 * used to save the organization detail
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public OrganizationDetailRes externalOrganizationAUD(OrganizationDetailReq organizationDetailReq);

	/**
	 * Method Name: getExtOrgDtls Method Description:
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public OrganizationDetailDto getExtOrgDtls(OrganizationDetailReq organizationDetailReq);

	/**
	 * 
	 * Method Name: deleteEIPDetails Method Description: This method is used to
	 * delete the Identifier detail, Phone detail and Email details related to
	 * particular organization.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public String deleteEIPDetails(OrganizationDetailReq organizationDetailReq);

	/**
	 * 
	 * Method Name: getPersonName Method Description: This method is used to
	 * fetch the person details.
	 * 
	 * @param idPerson
	 * @return
	 */
	public String getPersonName(Long idPerson);

	/**
	 * 
	 * Method Name: getIdentifierDtls Method Description: This method is used to
	 * check if there is already a TIN Identifier Exists with the given ID. for
	 * any other organization.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public boolean getIdentifierDtls(OrganizationDetailReq organizationDetailReq);

	/**
	 * 
	 * Method Name: validateOrgDetail Method Description: This method is used to
	 * validate the data before saving the Org Details.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	public OrganizationDetailRes validateOrgDetail(OrganizationDetailReq organizationDetailReq);
}
