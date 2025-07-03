package us.tx.state.dfps.service.stage.service;

import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:07 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface StageService {

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idStage
	 * @return StageValueBeanDto @
	 */
	public StageValueBeanDto retrieveStageInfo(Long idStage);

	public Long findPriorSubStageId(Long idStage) throws DataNotFoundException;

	/**
	 * 
	 * Method Name: getStageAndCaseDtls Method Description: This method is used
	 * for populates the Risk Assessment Form and Spanish Family Service Plan
	 * Parent-Child Contact and Financial Support (Visitation Plan). CSUB61S &
	 * CSUB75S
	 * 
	 * @param idStage
	 * @return StageCaseDtlDto @
	 */
	public PreFillDataServiceDto getStageAndCaseDtls(Long idStage);
}
