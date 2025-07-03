package us.tx.state.dfps.service.riskreasmnt.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.SDMRiskReassessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to provide the method definitions for the service implementation for
 * fetching , saving and delete SDM Risk Reassessment details. Jun 14, 2018-
 * 3:30:37 PM © 2017 Texas Department of Family and Protective Services
 */
public interface SDMRiskReassessmentService {

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method
	 * is used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.This method fetches the data by calling the dao
	 * implementation.This method performs the business logic to show the risk
	 * details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to retrieve the SDM
	 *            Risk Reassessment details.
	 * @return SDMRiskReassessmentRes - This dto will have the SDM Risk
	 *         Reassessment details.
	 */
	public SDMRiskReassessmentRes getSDMRiskReassessmentDtls(SDMRiskReasmntDto sdmRiskReasmntDto,
			Long idRiskReasmntLkp);

	/**
	 * Method Name: saveSDMRiskReassessmentDetails Method Description:This
	 * method is used to save the SDM Risk Reassessment details.This method
	 * performs the business logic to determine to create or update the risk
	 * reassessment details. Also this method calls the implementation to
	 * invalidate the existing tasks.This method calls the dao implementation to
	 * save/update the details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment details to be
	 *            saved or updated.
	 * @return SDMRiskReassessmentRes - This dto will hold the saved/updated SDM
	 *         Risk reassessment details.
	 * @throws Exception
	 */
	SDMRiskReassessmentRes saveSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq) throws Exception;

	/**
	 * Method Name: checkRiskReasmntExists Method Description:This method is
	 * used to check if for a particular person , a Risk Reassessment exists in
	 * PROC or PEND status.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to check if a risk
	 *            reassessment exists.
	 * @return SDMRiskReassessmentRes - This dto will hold the risk reassessment
	 *         id if present for a person in the stage.
	 */
	public SDMRiskReassessmentRes checkRiskReasmntExists(Long idPerson, String indHshldPrmryScndry,
			List<String> eventStatusList, Long idStage);

	/**
	 * Method Name: deleteSDMRiskReassessmentDtls Method Description:This method
	 * is used to delete the SDM Risk reassessment details.This method calls the
	 * service to delete the event and calls the dao implementation to delete
	 * the risk reassessment.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment id to be
	 *            deleted.
	 * @return
	 */
	public SDMRiskReassessmentRes deleteSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq);

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method
	 * is used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.This method fetches the data by calling the dao
	 * implementation.This method performs the business logic to show the risk
	 * details.
	 * 
	 * @param sdmRiskReasmntDto
	 * @param idRiskReasmntLkp
	 * @return
	 */
	PreFillDataServiceDto getSDMRiskReassessmentDetails(Long idEvent, String cdStage);

}
