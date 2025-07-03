package us.tx.state.dfps.service.arsafetyassmt.dao;

import java.util.List;

import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtDao Sep 19, 2017- 2:19:16 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ARSafetyAssmtDao {

	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser);

	/**
	 * Method Name: getARSafetyAssmtAreas Method Description:Retrieves
	 * ARSafetyAssmtArea List from database.
	 * 
	 * @param cdAssmtType
	 * @param idStage
	 * @return List<ARSafetyAssmtAreaValueDto> @
	 */
	public List<ARSafetyAssmtAreaValueDto> getARSafetyAssmtAreas(String cdAssmtType, Integer idStage);

	/**
	 * Method Name: getARSafetyAssmtFactors Method Description:Retrieves
	 * ARSafetyAssmtFactor List from database.
	 * 
	 * @param idArea
	 * @param idArSafetyAssmt
	 * @return List<ARSafetyAssmtFactorValueDto> @
	 */
	public List<ARSafetyAssmtFactorValueDto> getARSafetyAssmtFactors(Integer idArea, Integer idArSafetyAssmt);

	/**
	 * Method Name: getARSafetyAssmtFactorVals Method Description:Retrieves
	 * ARSafetyAssmtFactorVal List from database.
	 * 
	 * @param idFactor
	 * @return List<ARSafetyAssmtFactorValValueDto> @
	 */
	public List<ARSafetyAssmtFactorValValueDto> getARSafetyAssmtFactorVals(Integer idFactor);

	/**
	 * Method Name: isConclusionPageAprv Method Description:This methos returns
	 * true When AR Conclusion page is Approved and Closure reason is 'INV -
	 * Child Fatality Allegations or INV - Removal or INV - CPS Decision or INV
	 * - Family Request for a given stage
	 * 
	 * @param idStage
	 * @return Boolean @
	 */
	public Boolean isConclusionPageAprv(Integer idStage);

	/**
	 * Method Name: getARSafetyAssmtFactor Method Description:Retrieves
	 * ARSafetyAssmtFactor from database
	 * 
	 * @param idFactor
	 * @param idSafetyAssmt
	 * @return ARSafetyAssmtValueDto @
	 */
	public ARSafetyAssmtFactorValueDto getARSafetyAssmtFactor(Integer idFactor, Integer idSafetyAssmt);

}