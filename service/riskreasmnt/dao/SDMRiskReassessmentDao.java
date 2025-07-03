package us.tx.state.dfps.service.riskreasmnt.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.common.request.SDMRiskReassessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to declare the method definitions which will be used to retrieve,
 * save, delete SDM Risk Reassessment details. Jun 14, 2018- 3:55:48 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface SDMRiskReassessmentDao {

	/**
	 * Method Name: getNewReassessmentData Method Description:This method is
	 * used to retrieve the details for a new SDM Risk Reassessment.
	 * 
	 * @param idRiskAsmntLkp
	 *            - The id of the risk reassessment lookup.
	 * @param sdmRiskReasmntDto
	 *            - This dto will hold the input parameters for getting the risk
	 *            reassessment details.
	 * @return SDMRiskReasmntDto - This dto will hold the details of the new
	 *         risk reassessment.
	 */
	public SDMRiskReasmntDto getNewReassessmentData(Long idRiskAsmntLkp, SDMRiskReasmntDto sdmRiskReasmntDto);

	/**
	 * Method Name: saveSDMRiskReassessmentDtls Method Description:This method
	 * is used to save the SDM Risk Reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment details to be
	 *            saved.
	 * @return SDMRiskReassessmentRes - This dto will hold the saved SDM Risk
	 *         reassessment details.
	 */
	public SDMRiskReassessmentRes saveSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq);

	/**
	 * Method Name: getReunificationAssessmentList Method Description:This
	 * method is used to retrieve the SDM Re-unification assessment details for
	 * a particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return List<ReunificationAssessmentDto> - This collection will hold the
	 *         SDM Re-unification assessment details.
	 */
	public List<ReunificationAssessmentDto> getReunificationAssessmentList(Long idStage);

	/**
	 * Method Name: getFamilySubstituteCareStageId Method Description:This
	 * method is used to get the id stage of the FSU stage which was created
	 * from an INV stage.
	 * 
	 * @param idPriorStage
	 *            - The id of the INV stage.
	 * @param idStage
	 *            - The id of the SUB stage which gets created from an INV
	 *            stage.
	 * @return Long - The id of the FSU stage.
	 */
	public Long getFamilySubstituteCareStageId(Long idPriorStage, Long idStage);

	/**
	 * Method Name: checkRiskReasmntExists Method Description:This method is
	 * used to check if a risk reassessment exists for a person which is in PROC
	 * or PEND status.
	 * 
	 * @param idPerson
	 *            - The id of the person.
	 * @param indHshldPrmryScndry
	 *            - The indicator to indicate if the person is a household
	 *            person , primary person or secondary person.
	 * @param eventStatusList
	 *            - The list of event status.
	 * @return Long - The id of the risk reasmnt for a particular person if it
	 *         exists.
	 */
	public Long checkRiskReasmntExists(Long idPerson, String indHshldPrmryScndry, List<String> eventStatusList,
			Long idStage);

	/**
	 * Method Name: getPreviousReasmntDate Method Description:This method is
	 * used to retrieve the previous risk reassessment created date in a
	 * particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return Date - The date of the previous reassessment.
	 */
	public Date getPreviousReasmntDate(Long idStage);

	/**
	 * Method Name: getExistingReassessmentData Method Description:This method
	 * is used to retrieve an existing SDM Risk Reassessment details.
	 * 
	 * @param sdmRiskReasmntDto
	 *            - This dto will hold the input parameters for getting the risk
	 *            reassessment details.
	 * @return SDMRiskReasmntDto - This dto will hold the details of the new
	 *         risk reassessment.
	 * 
	 */
	public SDMRiskReasmntDto getExistingReassessmentData(SDMRiskReasmntDto sdmRiskReasmntDto);

	/**
	 * Method Name: getReunificationAssessmentList Method Description:This
	 * method is used to retrieve the SDM FSNA assessment details for a
	 * particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return List<CpsFsnaDto> - This collection will hold the SDM FSNA
	 *         assessment details.
	 */
	public List<CpsFsnaDto> getFSNAAssessmentList(Long idPriorStage);

	/**
	 * Method Name: deleteEventPersonLink Method Description:This method is used
	 * to delete the event person link.
	 * 
	 * @param idEevent
	 *            - The id of the event.
	 */
	public void deleteEventPersonLink(Long idEevent);

}
