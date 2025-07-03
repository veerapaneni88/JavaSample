/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is Service Interface for Child Sexual Aggression page.
 *Sep 17, 2018- 4:36:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.csa.service;

import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is
 * Service Interface for Child Sexual Aggression Page.> Sep 17, 2018- 4:36:45 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface CSAService {

	/**
	 * Method Name: fetchCSA Method Description: Fetch CSA Episodes and
	 * Incidents
	 * 
	 * @param csaDto
	 * @return
	 */
	public CSADto fetchCSA(CSADto csaDto);

	/**
	 * Method Name: saveCSA Method Description: Save & update CSA Episodes and
	 * Incidents
	 * 
	 * @param csaDto
	 * @return
	 */
	public CSADto saveCSA(CSADto csaDto);

	/**
	 * Method Name: getCSADetailsByIDPersonAndEpisodes Method Description: This
	 * is the service to get the CSA Episode details for CSA form display
	 * 
	 * @param csaDto
	 * @param selectedEpisodes
	 * @return
	 */
	PreFillDataServiceDto getCSADetailsByIDPersonAndEpisodes(CSADto csaDto, String selectedEpisodes);

	/**
	 *
	 *Method Name:	deleteIncidents
	 *Method Description: delete incidents based on incident ids
	 *@param episodeIncidentIds
	 *@param idPerson
	 */
	public void deleteCsaEpisodeIncidents(List<Long> episodeIncidentIds, Long idPerson);

	public void deleteCsaEpisodes(List<Long> idCsaEpisodes, Long idUser);

	}
