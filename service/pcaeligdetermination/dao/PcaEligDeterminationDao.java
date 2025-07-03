package us.tx.state.dfps.service.pcaeligdetermination.dao;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDeterminationDao Oct 13, 2017- 3:50:50 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public interface PcaEligDeterminationDao {

	/**
	 * Method Name: selectPcaEligDeterminationFromEvent Method Description:This
	 * method fetches data from Eligibility Determination table using idEvent
	 * 
	 * @param idAppEvent
	 * @return PcaEligDeterminationDto
	 * @throws DataNotFoundException
	 */
	public PcaEligDeterminationDto selectPcaEligDeterminationFromEvent(Long idAppEvent);

	/**
	 * Method Name: updateEligibilityDetermination Method Description:This
	 * method updates PCA_ELIG_DETERM table using PcaEligDeterminationDto.
	 * 
	 * @param determValueBean
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateEligibilityDetermination(PcaEligDeterminationDto determValueBean);

	/**
	 * Method Name: insertPcaEligDetermination Method Description:This method
	 * inserts new record into PCA_ELIG_DETERM table.
	 * 
	 * @param determValueBean
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertPcaEligDetermination(PcaEligDeterminationDto determValueBean);

	/**
	 * Method Name: selectPcaEligDetermination Method Description:This method
	 * fetches data from Eligibility Determination table using idPcaEligDeterm
	 * 
	 * @param idPcaEligDeterm
	 * @return PcaEligDeterminationDto
	 * @throws DataNotFoundException
	 */
	public PcaEligDeterminationDto selectPcaEligDetermination(Long idPcaEligDeterm);

	/**
	 * Method Name: selectEligFromIdPcaApp Method Description:This method
	 * fetches data from PCA_ELIG_DETERM table using idPcaEligApplication
	 * 
	 * @param idPcaEligApplication
	 * @return PcaEligDeterminationDto
	 * @throws DataNotFoundException
	 */
	public PcaEligDeterminationDto selectEligFromIdPcaApp(Long idPcaEligApplication);

}
