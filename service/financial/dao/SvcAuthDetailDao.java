package us.tx.state.dfps.service.financial.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.common.request.ServiceAuthDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.response.ServiceAuthDetailRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dto.ContractServiceDto;
import us.tx.state.dfps.service.financial.dto.LegalStatusValueDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.EquivalentSvcDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.RowQtyDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON21S Class
 * Description: Service Authorization Detail DAO interface. Mar 31, 2017 -
 * 5:40:44 PM
 */

public interface SvcAuthDetailDao {

	/**
	 * 
	 * Method Description: This Method will receive Id Svc Auth and return List
	 * of Auth Detail records and its correspond Nm Person Full based upon Id
	 * Person. Service: CCON21S DAM: CLSC20D
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return List<ServiceAuthorizationDetailDto> @
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
	 * @return LegalStatusValueDto
	 * @throws DataNotFoundException
	 */
	public LegalStatusValueDto selectLatestLegalStatus(Long idPerson, String cdLegalStatStatus);

	/**
	 * 
	 * Method Name: getCountSVCCCodeExists Method Description:Service
	 * Authorization Detail Window when the save pushbutton is clicked.This DAM
	 * is a new service code exists in the Equivalency table for the given time
	 * period and open stages for the client. This DAM will also be used to
	 * check if the service code is exempt from the Equivalency table edit by
	 * querying the Non_Equivalency table. Finally, this DAM will be used to see
	 * if the service code exists in the Equivalency table for the given time
	 * period when a user adds new Contract Services to a Contract.
	 * 
	 * @param equivalentSvcDetailDto
	 *            - contains StartDate,End date, Idperson, Idevent
	 * @param reqFunc
	 *            - determines to which case it should execute (1/2/3/4)
	 * @return RowQtyDto
	 */
	public RowQtyDto getCountSVCCCodeExists(EquivalentSvcDetailDto equivalentSvcDetailDto, String reqFunc);

	/**
	 * 
	 * Method Name: callVerifyDuplicate Method Description: to verify that a
	 * completed service_auth does not exist for a given stage for service code
	 * 69A.
	 * 
	 * @param serviceAuthDetailDto
	 *            - contains service code
	 * @param serviceAuthDtlReq
	 *            - stage id
	 * @return Long
	 */
	public Long callVerifyDuplicate(ServiceAuthDetailDto serviceAuthDetailDto, ServiceAuthDetailReq serviceAuthDtlReq);

	/**
	 * 
	 * Method Name: retrieveBudgetAmount Method Description: Retrieves budget
	 * amount available for a service.
	 * 
	 * @param serviceAuthDetailDto
	 * @param serviceAuthDtlReq
	 * @return ContractServiceDto
	 */
	public ContractServiceDto retrieveBudgetAmount(ServiceAuthDetailDto serviceAuthDetailDto,
			ServiceAuthDetailReq serviceAuthDtlReq);

	/**
	 * 
	 * Method Name: performAUDForServiceAuthDetail (CAUD13DI) Method
	 * Description: This method used to save the data into SVC_AUTH_DETAIL table
	 * 
	 * @param serviceAuthDetailDto
	 *            -input to table for which data to be inserted
	 * @return Long idSvcAuthDtl
	 */
	public Long performAUDForServiceAuthDetail(ServiceAuthDetailDto serviceAuthDetailDto);

	/**
	 * 
	 * Method Name: performAUDForKinship Method Description:This method used to
	 * save the data into KINSHIP table
	 * 
	 * @param kinshipDto
	 *            -input to table for which data to be inserted
	 * @return LOng idKinship
	 */
	public Long performAUDForKinship(KinshipDto kinshipDto);

	/**
	 * 
	 * Method Name: retrieveServiceAuthDetail Method Description: Call
	 * Service(Cses25D) To retrieve ServiceAuthDetail Data by using
	 * idSvcAuthDetail which is unique key for SVC_AUTH_DTL table
	 * 
	 * @param idSvcAuthDtl
	 *            - unique key for SVC_AUTH_DTL table
	 * @return ServiceAuthDetailDto -which holds Service AUthorization Detail
	 *         data
	 */
	public ServiceAuthDetailDto retrieveServiceAuthDetail(Long idSvcAuthDtl);

	/**
	 * 
	 * Method Name: rtrvCntrctSvcAndCntyList Method Description: retrieve
	 * details from tables CONTRACT_SERVICE, CONTRACT_COUNTY , STAGE_PERSON_LINK
	 * ,PERSON,Situation and Stage
	 * 
	 * @param serviceAuthDtlReq
	 *            - data contains Id_contract which retrieves the record
	 * @return ServiceAuthorizationDetailRes - list of ContarctService and
	 *         County Details and Situation And Stage details and
	 *         StagePersonLink details
	 */
	public ServiceAuthDetailRes rtrvCntrctSvcAndCntyList(ServiceAuthDetailReq serviceAuthDtlReq);

	/**
	 * 
	 * Method Name: getPlacementHistory Method Description:
	 * 
	 * @param idPlcmtChild-
	 *            idPerson whose age is > 18
	 * @param idPlcmtAdult-
	 *            primary client id
	 * @param idRsrcFacil-
	 *            resource id
	 * @param serviceCode-
	 *            service from SvcHeader
	 * @param cdPlcmtLivArr-Living
	 *            Arrangement
	 * @return List<PlacementDto> - get List of Placement
	 */
	public List<PlacementDto> getPlacementHistory(Long idPlcmtChild, Long idPlcmtAdult, Long idRsrcFacil,
			String serviceCode, String cdPlcmtLivArr);

	/**
	 * 
	 * Method Name: retrieveKinshipDetail Method Description: retrieve List
	 * Kinship detail for idPerson(CSESA7D)
	 * 
	 * @param idPerson
	 * @return KinshipDto
	 */
	public KinshipDto retrieveKinshipDetail(Long idPerson);

	/**
	 * 
	 * Method Name: retrieveInvoiceDetail Method Description: retrieve Invoice
	 * phase from INVOICE and DELVRD_SVC_DTL table(CSESA8D)
	 * 
	 * @param idSvcAuthDtl
	 *            - service authorization detail id
	 * @return List<String> - Invoice phase
	 */
	public List<String> retrieveInvoiceDetail(Long idSvcAuthDtl);

	/**
	 * 
	 * Method Name: getPlacementHistory Method Description: This function finds
	 * existing kinship Records.CLSS92D DAM
	 * 
	 * @param idSvcAuthDtl
	 * @return List<PersonDto>
	 */
	public List<PersonDto> retrieveKinshipForExisting(Long idSvcAuthDtl);

	public PlacementDto getMaxPlacementEndDtRecord(Long idPlcmtChild, Long idRsrcFacil, String serviceCode,
			String cdPlcmtLivArr);

	/**
	 * Method Name: getTermDayCareSvcAuth Method Description: retrieve the count
	 * of service auth detail audit based on the idSvcAuthDtl
	 */
	boolean getTermDayCareSvcAuth(Long idSvcAuthDtl);

	/**
	 * This method sets SSCC_LIST table with IND_NONSSCC_SVC_AUTH = 'Y'
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 * 
	 */
	public long updateSSCCListDao(long idSSCCReferral);

	/**
	 * This method updates SSCC_LIST table with IND_SSCC_DAYCARE = 'Y',
	 * DT_SSCC_DAYCARE = system date for Day Care Requests.
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 */
	public long updateSSCCListDCDao(long idSSCCReferral);

	/**
	 * Method Name: getServiceAuthorizationEventDetails Method Description: This
	 * method fetches the Service Authorization Event details
	 * 
	 * @param idCase
	 * @param idStage
	 * @param eventTypeCode
	 * @return List<ServiceAuthEventLinkValueDto>
	 * @throws DataNotFoundException
	 */
	public List<SvcAuthEventLinkInDto> getServiceAuthorizationEventDetails(Long idCase, Long idStage,
			String eventTypeCode) throws DataNotFoundException;

	/**
	 * 
	 * Method Name: checkForTimeMisMatchException Method Description: This
	 * method used to find the time mismatch exception
	 * 
	 * @param serviceAuthDetailDto
	 *            -input to table for which data to be inserted
	 * @param errorDto
	 */
	void checkForTimeMisMatchException(ServiceAuthDetailDto serviceAuthDetailDto, ErrorDto errorDto);

	/**
	 * 
	 * Method Name: selectPaymentDate Method Description: Retrieve the Kinship
	 * child ID_SVC_AUTH_DTL from Kinship table and the service auth effective
	 * date.
	 * 
	 * @param idPersonInput
	 * @return
	 */
	ServiceAuthDetailDto selectPaymentDate(Long idPersonInput);

	/**
	 * Method Name: getLegalEpisodeOfCare Method Description:For a given person
	 * get the episode of care.
	 * 
	 * @param idPersonInput
	 * @param dtEffective
	 * @return
	 */
	ServiceAuthDetailDto getLegalEpisodeOfCare(Long idPersonInput, Date dtEffective);

	/**
	 * Method Name: isLegalEpisodePaymentExists Method Description:This method
	 * checks whether a payment already exist for a given legal status episode.
	 * 
	 * @param serviceAuthDetailDto
	 * @return
	 */
	boolean isLegalEpisodePaymentExists(ServiceAuthDetailDto serviceAuthDetailDto);

	/**
	 *Method Name:	getExistingSiblingGrpForResouce
	 *Method Description:This method fetches the Sibling Group list for a given Resource
	 *@param idResource
	 *@return
	 */
	List<ServiceAuthDetailDto> getExistingSiblingGrpForResouce(Long idResource);

	public void termServiceAuthDetails(Long resourceId);

}
