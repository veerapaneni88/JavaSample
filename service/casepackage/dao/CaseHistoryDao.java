/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 17, 2017- 10:51:11 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCommonDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryStagePCSPDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryUtcStageDto;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 17, 2017- 10:51:11 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CaseHistoryDao {

	/**
	 * 
	 * Method Name: getCaseHistory Method Description: Get Case History details
	 * for the passed case id. New Service created as part of IMPACT Phase II
	 * Release I screen - Case History
	 * 
	 * @param idCase
	 * @return RetrvCaseHistoryRes
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CaseHistoryCaseDto> getCaseHistoryCaseList(RetrvCaseHistoryReq retrvCaseHistoryReq);

	/**
	 * 
	 * Method Name: getCaseHistoryStageCommonList Method Description: For each
	 * of the Cases obtained in the case history for a case, the stage details
	 * are fetched in the below method
	 * 
	 * @param idCase
	 * @param idTransaction
	 * @return List<CaseHistoryCommonDto>
	 */
	public List<CaseHistoryCommonDto> getCaseHistoryStageCommonList(Long idCase, String idTransaction);

	/**
	 * Method Description: getCaseHistoryStageClosedList Get the stages closed
	 * details for the passed case id
	 * 
	 * @param: List<idCase>
	 * @param: idTransaction
	 * @return: List<CaseHistoryUtcStageDto>
	 */
	public List<CaseHistoryUtcStageDto> getCaseHistoryStageClosedList(List<Long> idCase, String idTransaction);

	/**
	 * Method Description: This method is used to get the PCSP details for the
	 * FPR stage to be displayed in the Case History Page
	 * 
	 * @param idCase
	 *            - Case ID for which the records should be fetched
	 * @return List<CaseHistoryStagePCSPDto> - List of PCSP records for the Case
	 */
	public List<CaseHistoryStagePCSPDto> getPCSPDetails(Long idCase);

}
