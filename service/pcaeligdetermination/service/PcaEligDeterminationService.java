package us.tx.state.dfps.service.pcaeligdetermination.service;

import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDeterminationService Oct 16, 2017- 10:40:01 AM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PcaEligDeterminationService {

	/**
	 * Method Name: fetchEligibilityDetermination Method Description:This method
	 * returns Pca Eligibility Determination Record using Application Event.
	 * 
	 * @param idAppEvent
	 * @return PcaApplAndDetermDBDto @
	 */
	public PcaApplAndDetermDBDto fetchEligibilityDetermination(Long idAppEvent);

	/**
	 * Method Name: saveEligibilityDetermination Method Description:This method
	 * saves(updates) Pca Eligibility Determination information. Empty
	 * Eligibility Determination Record will be created when the worker submits
	 * Pca Application. So this function needs to handle updates only.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long @
	 */
	public Long saveEligibilityDetermination(PcaApplAndDetermDBDto pcaAppDetermDB);

	/**
	 * Method Name: determinePrelimEligibility Method Description:This method
	 * determines Preliminary Eligibility and the saves the Determination to
	 * database.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long
	 */
	public Long determinePrelimEligibility(PcaApplAndDetermDBDto pcaAppDetermDB);

	/**
	 * Method Name: determineFinalEligibility Method Description:This method
	 * determines Final Eligibility and the saves the determination to database.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long
	 */
	public Long determineFinalEligibility(PcaApplAndDetermDBDto pcaAppDetermDB);

	/**
	 * Method Name: selectDetermFromIdPcaApp Method Description: This method
	 * returns Eligibility Determination Record for the given Application Id
	 * 
	 * @param idPcaEligApplicationReq
	 * @return PcaEligDeterminationDto @
	 */
	public PcaEligDeterminationDto selectDetermFromIdPcaApp(Long idPcaEligApplication);

}
