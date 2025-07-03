package us.tx.state.dfps.service.servicauthform.service;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:52:46 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public interface ServiceAuthFormService {
	/**
	 * 
	 * Method Name: getServiceAuthFormData Method Description: Service
	 * Authorization form used by CPS to refer clients for paid services under
	 * PRS contracts.
	 * 
	 * @param commonHelperReq
	 * @return PreFillDataServiceDto
	 */
	PreFillDataServiceDto getServiceAuthFormData(CommonHelperReq commonHelperReq);


	/**
	 * Artifact ID: artf151569
	 * Method Name: copyOpenServiceAuthToFPR
	 * Method Description: Copies the open Service Authorizations from given stage to new stage if end date is after
	 * system date and event status is Approve
	 *
	 * @param idStage
	 * @param idNewStage
	 * @param idUser
	 * @param cdTask
	 */
	void copyOpenServiceAuthToNewStage(Long idStage, Long idNewStage, Long idUser, String cdTask);

}
