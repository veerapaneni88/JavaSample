package us.tx.state.dfps.service.financial.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.ServiceAuthDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.response.ServiceAuthDetailRes;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.financial.dto.LegalStatusValueDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON21S Service
 * Class Description: This class is use for retrieving service authorization
 * detail list List April 1, 2017 - 3:19:51 PM
 */
public interface ServiceAuthorizationService {

	/**
	 * 
	 * Method Description: This Method will retrieve a list of Service
	 * Authorization Detail records based upon IdSvcAuth from the Service
	 * Authorization Detail window. It will also retrieve NmPersonFull based
	 * upon IdPerson from the Person table. Service Name: CCON21S
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return List<ServiceAuthorizationDetailDto>
	 * @throws Exception
	 */

	public List<ServiceAuthorizationDetailDto> getSerAuthDetail(
			ServiceAuthorizationDetailReq serviceAuthorizationDetailReq);

	/**
	 * Method Name: selectLatestLegalStatus Method Description: This method
	 * fetches Latest Legal Status Record for the given Person and Legal Status
	 * from the database.
	 * 
	 * @param idPerson
	 * @param cdLegalStatStatus
	 * @return LegalStatusValueDto @
	 */
	LegalStatusValueDto selectLatestLegalStatus(Long idPerson, String cdLegalStatStatus);

	/**
	 * Method Name: saveServiceAuthDetail Method Description: This is the Save
	 * Service for Service Authorization Detail. First it will check whether or
	 * not the Person has already been authorized services for the resource
	 * during the specified time period. It will then retrieve Budget
	 * information and validate against the Amount Requested. If the Amount
	 * Requested decreases the current budget to less than 15%, then a To Do is
	 * initiated. If the above passes validation, then the Save Dam is called
	 * for Svc Auth Dtl and Event Person Link tables.
	 * 
	 * @param serviceAuthDtlReq
	 *            - contains ServiceAuthorization Detail and list of person to
	 *            be saved
	 * @return ServiceAuthDetailRes - gives Error message or Success message
	 */
	public ServiceAuthDetailRes saveServiceAuthDetail(ServiceAuthDetailReq serviceAuthDtlReq);

	/**
	 * 
	 * Method Name: retrieveServiceAuthDetail Method Description: his retrieval
	 * service will * either populate the Service combo box, Persons Listbox *
	 * and/or Svc Auth Dtl Listbox. If the Window Mode is * Inquire, then a
	 * single row for Svc Auth Dtl Listbox will * be retrieved; if the Window
	 * Mode is Modify, then a single * row for Svc Auth Dtl Listbox will be
	 * retrieved, and a list * of Services will also be retrieved; if the Window
	 * Mode is * New and no detail record exists, then a list of Services * will
	 * be retrieved, a list of Persons will be retrieved and * the Dt Situation
	 * Opened will be retrieved. However, if the * window mode is New and a
	 * detail record does exist, then * a single row for Svc Auth Dtl Listbox, a
	 * list os Service * and Dt Situation Opened will be retrieved.
	 * 
	 * @param serviceAuthDtlReq
	 *            - Contains idStage , idContract to retrieve the data
	 * @return ServiceAuthDetailRes - this gets data to display service
	 *         authorization detail
	 */
	public ServiceAuthDetailRes retrieveServiceAuthDetail(ServiceAuthDetailReq serviceAuthDtlReq);

	/**
	 * This method is used to retrieve the day care information and person
	 * details who are part of day care
	 */
	public DayCareRequestDto retrieveDayCareReqAndPersonDetail(Long idEvent);

	/**
	 * Method Name: dayCarePersonList Method Description: This method is to
	 * retrieve the person list for day care.
	 */
	public ServiceAuthDetailRes dayCarePersonList(DayCareRequestDto dayCareRequestDto, Long idEvent);

	/**
	 * Method Name: dayCarePersonListForSvcAuthDtlId Method Description: This
	 * method is used to retrive the person information based on event id and
	 * stage id and see if the service is terminated.
	 */
	public ServiceAuthDetailRes dayCarePersonListForSvcAuthDtlId(DayCareRequestDto dayCareRequestDto, Long idEvent,
			Long idSvcAuthDtl);

	/**
	 * MethodName: updateSSCCListDC MethodDescription:This method updates
	 * SSCC_LIST table with IND_SSCC_DAYCARE = 'Y', DT_SSCC_DAYCARE = system
	 * date for Day Care Requests. EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 * 
	 */

	public long updateSSCCListDC(long idSSCCReferral);

	/**
	 * This method is used to validate if the detail entered is having correct
	 * legal status and living arrangement based on case id, person id and
	 * resource id
	 * 
	 * @param idCase
	 * @param idPerson
	 * @param idResource
	 * @return
	 */
	public ServiceAuthDetailRes validateLegalStatusAndLivingArr(Long idCase, Long idPerson, Long idResource);

	/**
	 * Method Name: validateChildInformation Method Description: This method is
	 * used to validate the child information for non-TANF KINSHIP CODEs
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return
	 */
	public boolean validateChildInformation(ServiceAuthorizationDetailReq serviceAuthorizationDetailReq);

	/**
	 * Method Name: getLegalEpisodePaymentDate Method Description: Retrieve the
	 * Kinship child ID_SVC_AUTH_DTL from Kinship table
	 * 
	 * @param idPersonInput
	 * @return
	 */
	ServiceAuthDetailRes getLegalEpisodePaymentDate(Long idPersonInput, Date dtEffective, Long idResource, String cdSvcAuthService);
}
