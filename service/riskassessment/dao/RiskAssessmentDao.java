package us.tx.state.dfps.service.riskassessment.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.InvstActionQuestion;
import us.tx.state.dfps.common.domain.RiskAssessment;
import us.tx.state.dfps.common.domain.RiskFactors;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskAssmtDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskFactorsDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for fetching stage details Aug 6, 2017- 4:06:09 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface RiskAssessmentDao {

	/**
	 * 
	 * Method Name: getRiskAssessmentFactorDtls Method Description: This method
	 * retrieves data from RISK_ASSESSMENT, RISK_FACTORS Tables. CINV14D
	 * 
	 * @param idStage
	 * @return List<RiskAssessmentFactorOutDto>
	 */
	public List<RiskAssessmentFactorDto> getRiskAssessmentFactorDtls(Long idStage);

	/**
	 * 
	 * Method Name: getRiskFactorsDtls Method Description: This method retrieves
	 * data from RiskFactors Table. CSEC76D
	 * 
	 * @param idEvent
	 * @return List<RiskFactorDto>
	 */
	public List<RiskFactorsDto> getRiskFactorsDtls(Long idEvent);

	/**
	 * Method Name: populateRiskData Method Description:
	 * 
	 * @param idCase
	 * @param idStage
	 * @return RiskAssmtValueDto
	 */
	public RiskAssmtValueDto populateRiskData(long idCase, long idStage);

	/**
	 * Method Name: addRiskAssmtDetails Method Description:
	 * 
	 * @param riskAssmtValueBean
	 * @return long
	 */
	public long addRiskAssmtDetails(RiskAssmtValueDto riskAssmtValueBean);

	/**
	 * Method Name: addAreaDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param formFactorBean
	 * @return long
	 */
	public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto formFactorBean);

	/**
	 * Method Name: addCategoryDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param newRiskAreaId
	 * @param formFactorBean
	 * @return long
	 */
	public long addCategoryDetails(long idRiskEvent, long newRiskAreaId, RiskAssmtValueDto formFactorBean);

	/**
	 * Method Name: addFactorDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param newRiskAreaId
	 * @param newRiskCategoryId
	 * @param formFactorBean
	 * @return long
	 */
	public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long newRiskCategoryId,
			RiskAssmtValueDto formFactorBean);

	/**
	 * Method Name: getInvstActionQuestion Method Description:CINV04D - This DAM
	 * retrieves the InvstActionQuestion based on the eventId
	 * 
	 * @param idEvent
	 * @return List<InvstActionQuestion>
	 */
	public List<InvstActionQuestion> getInvstActionQuestions(long idEvent);

	/**
	 * Method Name: getRiskFactor Method Description:CINV65D - This DAM
	 * retrieves the Risk Factors for the given idEvent and idPerson
	 * 
	 * @param idEvent
	 * @param idPerson
	 * @return List<RiskFactors>
	 */
	public List<RiskFactors> getRiskFactor(Long idEvent, Long idPerson);

	/**
	 * Method Name: getRiskAssessment Method Description:CINV64D - This DAM
	 * retrieves the Risk Assessment for the given idEvent
	 * 
	 * @param idEvent
	 * @return List<RiskAssessment>
	 */
	public List<RiskAssessment> getRiskAssessment(Long idEvent);

	/**
	 * 
	 * Method Name: queryRiskAssmt Method Description: This method retrieves
	 * data risk assessment details
	 * 
	 * @param idStage
	 * @param idCase
	 * @param idEvent
	 * @param nbrVersion
	 * @return List<RiskAssmtDtlDto>
	 */
	public List<RiskAssmtDtlDto> queryRiskAssmt(Long idStage, Long idCase, Long idEvent, Long nbrVersion);

	/**
	 * Method Name: queryRiskAssmtExists Method Description: Query the Risk
	 * Assessment to check if Risk Assessment already exists
	 * 
	 * @param idStage
	 * @param idCase
	 * @return RiskAssmtDtlDto
	 */
	public RiskAssmtDtlDto queryRiskAssmtExists(Long idStage, Long idCase);

	/**
	 * Method Name: queryPageData Method Description: Query the data needed to
	 * create the Risk Assessment page.
	 * 
	 * @param nbrVersion
	 * @return List<RiskAssmtDtlDto>
	 */
	public List<RiskAssmtDtlDto> queryPageData(Long nbrVersion);

	/**
	 * Method Name: checkRiskAssmtTaskCode SIR 24696, Check the stage table to
	 * see if INV stage is closed and event table, if it has task code for Risk
	 * Assessment.
	 * 
	 * @param idStage
	 * @param idCase
	 * @param idEvent
	 * @return RiskAssmtDtlDto
	 */
	public RiskAssmtDtlDto checkRiskAssmtTaskCode(Long idStage, Long idCase, Long idEvent);

	/**
	 * Method Name: checkIfRiskAssmtCreatedUsingIRA Query the
	 * IND_RISK_ASSMT_INTRANET column on the RISK_ASSESSMENT table to determine
	 * if the Risk Assessment was created using IRA or IMPACT. has task code for
	 * Risk Assessment.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return RiskAssmtDtlDto
	 */
	public RiskAssmtDtlDto checkIfRiskAssmtCreatedUsingIRA(Long idStage, Long idCase);

}
