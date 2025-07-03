/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 27, 2017- 2:19:56 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.stageprogression.service;

import us.tx.state.dfps.common.dto.BaseAddRiskAssmtValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.BaseAddRiskAssmtRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is
 * interface for Stage Progression Ejb. Sep 27, 2017- 2:19:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageProgressionService {

	/**
	 * 
	 * Method Name: createNewStage Method Description: This method creates new
	 * Stage from StageValueBean. StageValueBean object passed should be
	 * populated with Case and Stage Information.
	 * 
	 * @param stageValueBeanDto
	 * @return
	 */
	public Long createNewStage(StageValueBeanDto stageValueBeanDto);

	/**
	 * Method Name: createNewCaseAndStage Method Description:This method creates
	 * new Case and Stage during Stage Progression.
	 * 
	 * @param stageValueBeanDto
	 * @return StageValueBeanDto @
	 */
	public StageValueBeanDto createNewCaseAndStage(StageValueBeanDto stageValueBeanDto);

	/**
	 * Method Name: linkSecondaryWorkerToStage Method Description:This method
	 * links secondary worker to the stage by creating entry into Stage Person
	 * Link table.
	 * 
	 * @param idStage
	 * @param idSecWorker
	 * @return Long @
	 */
	public Long linkSecondaryWorkerToStage(Long idStage, Long idSecWorker);

	/**
	 * @param string
	 * @return
	 */
	public SafetyAssessmentRes addSafetyAssmt(SafetyAssessmentReq safetyAssessmentReq);

	/**
	 * Method Name: addRiskAssmt Method Description:
	 * 
	 * @param baseAddRiskAssmtValueDto
	 * @return BaseAddRiskAssmtRes
	 */
	public BaseAddRiskAssmtRes addRiskAssmt(BaseAddRiskAssmtValueDto baseAddRiskAssmtValueDto);
}
