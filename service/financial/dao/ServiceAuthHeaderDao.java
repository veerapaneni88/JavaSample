package us.tx.state.dfps.service.financial.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.ServiceAuthorizationHeaderDto;
import us.tx.state.service.servicedlvryclosure.dto.OutcomeMatrixDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao
 * interface for all service calls in service authorization Header> June 27,
 * 2018- 3:05:39 PM © 2017 Texas Department of Family and Protective Services.
 */
public interface ServiceAuthHeaderDao {

	/**
	 * Method name: getOutcomeMatrixListBystageId Method Description: retrieve
	 * OUTCOME MATRIX LIST FOR GIVEN STAGE ID
	 * 
	 * @param idStage
	 * @return
	 */
	public List<OutcomeMatrixDto> getOutcomeMatrixListBystageId(Long idStage);

	/**
	 * Method Name: getDtSituationOpened Method Description: retrieves situation
	 * opened date from Stage entity
	 * 
	 * @param idStage
	 * @return
	 */
	public Date getDtSituationOpened(Long idStage);

	/**
	 * calls the DELETE_SERVICE_AUTH procedure in the COMPLEX_DELETE package,
	 * given an ID_SVC_AUTH
	 * 
	 * @param idSvcAuth
	 * @return
	 */
	public Long deleteServiceAuth(Long idSvcAuth);

	/**
	 * Method Name: saveServiceAuthorization Method Description: This method is
	 * used to save the service Authorization Header information
	 * 
	 * @param serviceAuthorizationHeaderDto
	 * @param idPerson
	 *            CAUD33D
	 * @return
	 */
	public Long saveServiceAuthorization(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto, Long idPerson);

	/**
	 * Method Name: getDtLastUpdateForServAuthHeader Method Description:This
	 * method gets the latest last update date for service Authorization
	 * 
	 * @param idSvcAuth
	 * @return
	 */
	Date getDtLastUpdateForServAuthHeader(Long idSvcAuth);

	/**
	 * This DAM receives ID STAGE and calls the SFI stored procedure
	 * 
	 * @param idStage
	 * @return
	 *//*
		 * public double getTotalCompletedSFIAuths(Long idStage);
		 */

}
