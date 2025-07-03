package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvCnclsnValidationDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface to retrieve Allegation Details> Aug 8, 2017- 3:27:47 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface AllegationStageDao {

	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: This method will get
	 * data from ALLEGATION and STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationStageOutDto> @
	 */
	public List<AllegationStageOutDto> getAllegationDtls(AllegationStageInDto pInputDataRec);

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: fetchCoSleepingData
	 * 
	 * @param idStage
	 * @return List<CPSInvConclValBeanRes> @
	 */
	public List<CpsInvCnclsnValidationDto> getCoSleepingData(Long idStage);

	/**
	 * Method Description: This method Returns any prior stage ID for any given
	 * stage ID and a type request. Method Name:
	 * fetchPriorStageInReverseChronologicalOrder
	 * 
	 * @param idStage
	 * @param cdStageType
	 * @return StageDto @
	 */
	public StageDto fetchPriorStageInReverseChronologicalOrder(Long idStage, String cdStageType);
}
