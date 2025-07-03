/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 3, 2018- 12:13:32 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.dcr.service;

import java.util.List;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the service implementation of the Type of service DCI page Apr 3, 2018-
 * 12:13:32 PM © 2017 Texas Department of Family and Protective Services
 */
public interface TypeOfServiceDCRService {

	/**
	 * Method Name: getTypeOfServiceDetail Method Description:
	 * DayCareRequestBean
	 * 
	 * @param dayCareRequestReq
	 * @return DayCareRequestBean
	 */
	DayCareRequestBean getTypeOfServiceDetail(DayCareRequestReq dayCareRequestReq);

	/**
	 * Method Name: deleteTypeOfService Method Description: This method deletes
	 * child daycare service type
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	void deleteTypeOfService(DayCareRequestReq dayCareRequestReq);

	/**
	 * Method Name: saveDayCarePersonInfo Method Description: This method
	 * saves(insert/update) Day Care Request Person Information
	 * 
	 * @param dayCareRequestReq
	 * @return
	 * @throws InvalidRequestException
	 */
	DayCareRequestRes saveTypeofService(DayCareRequestReq dayCareRequestReq);
	
	
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
	 * Method Name: getOverlapRecsForSvcAuth Method Description: for a service
	 * auth detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param dayCarePersonDto
	 * @param idPerson
	 * @return
	 */
	boolean checkOverlapRecsForSvcAuth(DayCarePersonDto dayCarePersonDto, Long idPerson,Long idEvent);

}
