/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is DAO Interface for CSA Page.
 *Sep 17, 2018- 4:40:34 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.csa.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CsaEpisodesIncdnt;
import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is DAO
 * Interface for Child Sexual Aggression Page.> Sep 17, 2018- 4:40:34 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public interface CSADao {

	/**
	 * Method Name: fetchCSA Method Description: Method to fetch CSA Episodes
	 * and Incidents
	 *
	 * @param idPerson
	 * @return
	 */
	CSADto fetchCSA(CSADto csaDto);

	CSADto fetchCSAIncdnts(CSADto csaDto);
	/**
	 * Method Name: saveOrUpdateCSA Method Description: Method to Save and
	 * Update CSA Episodes and incidents
	 *
	 * @param csaDto
	 */
	CSADto saveOrupdateCSA(CSADto csaDto);

	/**
	 * Method Name: updateCsaEpisode Method Description:Update CsaEpisode for
	 * Person merge
	 *
	 * @param csaEpisodeDto
	 */
	void updateCsaEpisode(CSAEpisodeDto csaEpisodeDto);

	/**
	 * Method Name: updateCsaEpisode Method Description:Update CsaEpisode for
	 * Person merge
	 *
	 * @param csaEpisodeDto
	 */
	void updateCsaIncident(CsaEpisodesIncdntDto csaEpisodeDto);

	/**
	 * Method Name: fetchCSAList Method Description:fetch CSA List
	 *
	 * @param idPerson
	 * @return
	 */
	List<CSAEpisodeDto> fetchCSAList(Long idPerson);

	/**
	 * Method Name: fetchCSAIncidentList Method Description:fetch CSA Incident List
	 *
	 * @param idPerson
	 * @return
	 */
	List<CsaEpisodesIncdntDto> fetchCSAIncidentList(Long idPerson);

	/**
	 * Method Name: updateNoCharacteristics Method Description: This method is to
	 * set No Characteristics Flag when CSA Episode and Incident are recorded.
	 *
	 * @param idPerson
	 * @return
	 */
	void updateNoCharacteristics(Long idPerson);

	List<CSAEpisodeDto> fetchCSAList(Long idPerson, Boolean doSortByEpisodeStart);

	List<CsaEpisodesIncdntDto> fetchCSAIncidentList(Long idPerson, Boolean doSortByIncident);

	List<CsaEpisodesIncdntDto> getCSAVictimList(Long idVictim);

	void deleteCsaEpisodeIncidents(List<Long> episodeIncidentIds, Long idPerson);

	void deleteCsaEpisodes(List<Long> idCsaEpisodes, Long idUser);

	/**
	 * added for PPM 65209
	 * @param idPlacementTa
	 * @return
	 */
	public List<CsaEpisodesIncdnt> fetchCSAEpiIncdntByTA(Long idPlacementTa);
	public void updateCSAForTA(Long idPlacementTa, Long user);

}