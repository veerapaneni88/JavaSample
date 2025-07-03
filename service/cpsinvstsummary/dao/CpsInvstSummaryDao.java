package us.tx.state.dfps.service.cpsinvstsummary.dao;

import java.util.List;

import us.tx.state.dfps.service.cpsinvstsummary.dto.RiskAssessmentInfoDto;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods that make calls to database Mar 28, 2018- 3:46:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CpsInvstSummaryDao {

	/**
	 * Method Name: getRiskAssessmentInfo Method Description: Retrieves risk
	 * assessment info (CSECFAD)
	 * 
	 * @param idStage
	 * @return List<RiskAssessmentInfoDto>
	 */
	public List<RiskAssessmentInfoDto> getRiskAssessmentInfo(Long idStage);

	/**
	 * Method Name: getCpsInvstDetail Method Description: Calls DAO method from
	 * CpsInvstDetailDao and transfers data from domain object to DTO (DAM:
	 * CINV95D)
	 * 
	 * @param idStage
	 * @return List<CpsInvstDetailDto>
	 */
	public List<CpsInvstDetailDto> getCpsInvstDetail(Long idStage);

	/**
	 * Method Name: getIncomingDetail Method Description: Calls DAO method from
	 * IncomingDetailDao and transfers data from domain object to DTO (DAM:
	 * CINT07D)
	 * 
	 * @param idStage
	 * @return
	 */
	public IncomingDetailDto getIncomingDetail(Long idStage);

}
