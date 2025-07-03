package us.tx.state.dfps.service.casepackage.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.casepackage.dto.ServAuthRetrieveDto;
import us.tx.state.dfps.service.common.request.ServAuthRetrieveReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.KinMonthlyExtPaymentDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailRecDto;
import us.tx.state.dfps.service.workload.dto.ServiceAuthorizationDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON24S Class
 * Description: ServAuthRetrieve DAO Interface Mar 23, 2017 - 3:55:39 PM
 */

public interface ServiceAuthorizationDao {

	/**
	 * Method Description: This Method to retrieve the list of rows from the
	 * SERVICE_AUTHORIZATION_TABLE based on inputs Dam Name: CSES23D, CLSSS25D,
	 * CLSC14D, CSEC13D
	 *
	 * @param servAuthRetrieveReq
	 * @return List<ServAuthRetrieveDto>
	 * @,DataNotFoundException
	 */

	public List<ServAuthRetrieveDto> getAuthDetails(ServAuthRetrieveReq servAuthRetrieveReq);

	/**
	 * This DAM performs a full row retrieval from the
	 * SERVICE_AUTHORIZATION_TABLE when IDSVC_AUth is equal to the input
	 * variable.
	 * <p>
	 * Service Name : CCMN03U, DAM Name : CSES23D
	 *
	 * @param idSvcAuth
	 * @return @
	 */
	public ServiceAuthorizationDto getServiceAuthorizationById(Long idSvcAuth);

	/**
	 * This DAM performs a full row retrieval from the
	 * SERVICE_AUTHORIZATION_TABLE when IDSVC_AUth is equal to the input
	 * variable.
	 * <p>
	 * Service Name : CCMN03U, DAM Name : CSES23D
	 *
	 * @param idSvcAuth
	 * @return @
	 */
	public ServiceAuthorization getServiceAuthorizationEntityById(Long idSvcAuth);

	/**
	 * This DAM selects a full row from the svc_auth_detail with id_svc_auth as
	 * input.
	 * <p>
	 * Service Name : CCMN03U, DAM Name : CLSS24D
	 *
	 * @param idSvcAuth
	 * @return @
	 */
	public List<SVCAuthDetailDto> getSVCAuthDetailDtoById(Long idSvcAuth);

	/**
	 * Method Name: getSVCAuthDetailRecord Service Name : CCMN03U, DAM Name :
	 * CLSC36D Method Description: This DAM will retrieve the required
	 * information for each service auhtorization detail record retrieved.
	 *
	 * @param idSvcAuth
	 * @return @
	 */
	public List<SVCAuthDetailRecDto> getSVCAuthDetailRecord(Long idSvcAuth);

	/**
	 * The DAM will insert a new SVC_AUTH_ID for a particular event.
	 * <p>
	 * Service Name : CCMN03U, DAM Name : CAUD34D
	 *
	 * @param svcAuthEventLink
	 * @
	 */
	public void svcAuthEventLinkSave(SvcAuthEventLink svcAuthEventLink);

	ArrayList<SVCAuthDetailDto> getServiceAuthDtlListForPerson(Long personId);

	public ArrayList<ServiceAuthDto> getOverlappingSvcAuthDtlListForPerson(SVCAuthDetailDto svcAuthBean, Long personId);

	/**
	 * * Description: This dam will retrieve list of Dt Svc Auth Dt Term from the
	 * * Svc Auth Dtl Event Link table. DAM Name : CLSC60D
	 *
	 * @param idCase
	 * @
	 */

	public List<Date> getSvcAuthDtTerm(Long idCase);

	/**
	 * Method Name: insertIntoServiceAuthorizationEventLinks Method Description:
	 * This method batch inserts/updates records into SVC_AUTH_EVENT_LINK table
	 *
	 * @param serviceAuthEventLinkValueBeans
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<Long> insertIntoServiceAuthorizationEventLinks(
			List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans) throws DataNotFoundException;

	/**
	 * Method Name: insertIntoEventPersonLinks Method Description: This method
	 * batch inserts into Event_person_link table using EventPersonValueBean
	 * list
	 *
	 * @param spLinkBeans
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */

	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans) throws DataNotFoundException;

	/**
	 * Method Name:	checkSvcAuthEventLinkExists
	 * Method Description: This method checks if the SvcAuthEventLink exists for an event
	 *
	 * @param idSvcAuthEvent
	 * @return
	 */
	boolean checkSvcAuthEventLinkExists(Long idSvcAuthEvent);

	/**
	 * SR 45217 - HB4 Long Term Solution
	 * update the service auth details with term date
	 *
	 * @param homeInfoBean
	 */
	public int updateServiceAuthDetails(KinHomeInfoDto homeInfoBean, Long contractId,
										String serviceCode, KinChildDto kinChildDto);

	public int termOtherServiceAuthDetails(KinHomeInfoDto homeInfoBean, KinChildDto kinChildDto);

	public int getServiceAuthUnitsUsed(Long childId, Date legalStartDate, String serviceCode, boolean termed);

	public int getServiceAuthUnitsRequested(Long childId, Date legalStartDate, String serviceCode, boolean termed);

	public boolean getServiceAuthDtlOpenExists(Long childId, Date legalStatusDate, String serviceCode);

	public boolean getServiceAuthDtlClosedExists(Long childId, Date legalStatusDate, String serviceCode);

	public boolean getIs68OPaidFull(Long childId, Date legalStatusDate, Long resourceId);

	public Date getSADPendingTermDate(Long childId,  Date legalStartDate, String serviceCode);

	public Date getSADCurrentMonthTermDate(Long childId, Date legalStartDate, String serviceCode);

	public Set getSAHeaderIdSet(Long resourceId, Long contractId, Long caseId);

	public Long insertServcAuth(KinHomeInfoDto homeInfoBean, KinChildDto childBean, String serviceCode, Long contractId,
								KinHomeInfoDto savedBean, Long placementsAdultId);

	public ServiceAuthorization serviceAuthorizationSave(ServiceAuthorization serviceAuthorization);

	public ServiceAuthorization populateServcAuth(KinHomeInfoDto homeInfoBean, KinChildDto childBean, String serviceCode, Long contractId,
												  KinHomeInfoDto savedBean, Long placementsAdultId);

	public int insertServcAuthEventLink(KinHomeInfoDto homeInfoBean, Long serviceAuthId, Long eventId);

	public Date insertServiceAuthDtil(KinHomeInfoDto homeInfoBean, Long childId, Long serviceAuthId,
									   String serviceCode, double rate,  Date startDate,  int numUnitsRemaining,
									   int months, int lineItem, boolean isTanf);
	public Long getServiceAuthId(Long resourceId, Long childId);

	public List<SVCAuthDetailDto> getServiceAuthList(Long resourceId, Long personId);

	public Long insertMonthlyExtensionSADetail(KinMonthlyExtPaymentDto childBean, Long serviceAuthId, double rate, int totalUnitsAllowed, int lineItem);

	/*
	 *** DAM Name:     CLSCA0D
	 ** Selects a full row from the svc_auth_detail with id_svc_auth as input.
	 *  param : caseId

	 */
	public List<ServiceAuthDto> getSvcAuthEventInfo(Long idCase);

}
