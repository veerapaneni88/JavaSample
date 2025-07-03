/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 3, 2018- 12:12:42 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.dcr.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the service implementation of the Type of service DCI page Apr 3, 2018-
 * 12:12:42 PM © 2017 Texas Department of Family and Protective Services
 */
public interface TypeOfServiceDCRDao {

	/**
	 * Method Name: getXmlResponsesLast Method Description: This method gets an
	 * XML string that contains the answers to the displayed questions in the
	 * Decision Tree.
	 * 
	 * @param dayCareRequestReq
	 * @return String
	 */
	String getXmlResponsesLast(int idPerson, int idDayCareRequest);

	/**
	 * Method Name: getXmlResponsesSystem Method Description: This method gets
	 * an XML string that contains the answers to system questions. This is used
	 * by the Decision Tree.
	 * 
	 * @param dayCareRequestReq
	 * @return String
	 */
	String getXmlResponsesSystem(int idPerson, int idDayCareRequest, int idUser);

	/**
	 * Method Name: getApprovalDate Method Description: This method returns the
	 * Daycare Request approval date or '12/31/4712' if there is no Approval
	 * Date.
	 * 
	 * @param dayCareRequestReq
	 * @return Date
	 */
	Date getApprovalDate(int idDayCareRequest);

	/**
	 * Method Name: deleteResponses Method Description: This method delete the
	 * responses stored in the DAYCARE_PERSON_RESPONSE
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	ServiceResHeaderDto deleteResponses(int idDayCareRequest, int idPerson);

	/**
	 * Method Name: saveXmlResponses Method Description: Save the answers to the
	 * displayed questions in the Decision Tree. an XML string that contains
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	int saveXmlResponses(int idDayCareRequest, int idPerson, int idPersonLastUpdated, String xmlResponses);

	/**
	 * Method Name: deleteTypeOfService Method Description: This method deletes
	 * child daycare service type
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	void deleteTypeOfService(Long idPerson, Long idDayCareRequest, Long idLastUpdatedPerson);

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description: This method
	 * is to delete child/caregiver information from the
	 * DAYCARE_PERSON_FACIL_LINK table
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	void deleteDayCarePersonFacilLink(Long idPerson, Long idDayCareRequest, Long IdFacility);

	/**
	 * Method Name: retrieveDayCarePersonFacilLink Method Description: This
	 * method is to retrieve child/caregiver information from the
	 * DAYCARE_PERSON_FACIL_LINK table
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	DayCareRequestRes retrieveDayCarePersonFacilLink(DayCareRequestReq dayCareRequestReq);

	/**
	 * Method Name: getOverlapRecsForSvcAuth Method Description: for a service
	 * auth detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param dayCarePersonDto
	 * @param idPerson
	 * @return
	 */
	List<DayCarePersonDto> getOverlapRecsForSvcAuth(DayCarePersonDto dayCarePersonDto, Long idPerson);
	
	/**
	 * Method Name: getSvcAuthLink 
	 * Method Description: Method used to check the requested day care request is same as day care request serivceAuth link table.
	 * @param dayCarePersonDto
	 * @param dayCarePersonDtos
	 * @param idEvent
	 * @return boolean
	 */
	public boolean getSvcAuthLink(List<DayCarePersonDto> dayCarePersonDtos,DayCarePersonDto dayCarePersonDto,Long idEvent);
}
