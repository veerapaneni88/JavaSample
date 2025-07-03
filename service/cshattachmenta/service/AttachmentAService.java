/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is Service Interface for Child Sexual Aggression page.
 *Sep 17, 2018- 4:36:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.cshattachmenta.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;

/**
 * service-business- FCL Class 
 * Name: getCshDetailsByIDPerson
 * Description: This is service Interface for Sexual Victimization history Page.
 * 
 * @param IdPerson
 * © 2019 Texas Department of Family and Protective Services
 * Artifact ID: artf128756
 */
 public interface AttachmentAService {

	/**
	 * Method Name: fetchCSA Method Description: Fetch CSA Episodes and
	 * Incidents
	 * 
	 * @param csaDto
	 * @return
	 */
	 public PreFillDataServiceDto getCshDetailsByIDPerson(Long IdPerson);
}