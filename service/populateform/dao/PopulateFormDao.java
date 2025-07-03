package us.tx.state.dfps.service.populateform.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CINV38S Class
 * Class Description: PopulateFormDao is the super class of populateformDaoImpl
 * Feb 3, 2018- 2:01:02 PM © 2017 Texas Department of Family and Protective
 * Services
 */

public interface PopulateFormDao {

	/**
	 * Gets the allegation by id.
	 *
	 * @param idAllegationStage
	 * @return the allegation by id @ the service exception
	 */
	public List<AllegationWithVicDto> getAllegationById(Long idStage);

	/**
	 * Gets the uq dispositon by id.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idPerpetrator
	 *            the id perpetrator
	 * @return the uq dispositon by id @ the service exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	
	// Changed the Return Type to List - Warranty Defect 10787
	public List<AllegationWithVicDto> getUqDispositonById(Long idStage, Long idPerson);

	/**
	 * Gets the uq allegation by id.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idPerpetrator
	 *            the id perpetrator
	 * @return the uq allegation by id @ the service exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<AllegationDto> getUqAllegationById(Long idStage, Long idPerson);

	/**
	 * Gets the uq stage by id.
	 *
	 * @param idAlgStage
	 *            the id alg stage
	 * @param idPerpetrator
	 *            the id perpetrator
	 * @return the uq stage by id @ the service exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	// Changed the Return Type to List - Warranty Defect 10787
	public List<AllegationWithVicDto> getUqStageById(Long idStage, Long idPerson);

	/**
	 * Checks if is span gender.
	 *
	 * @param idPerson
	 *            the id person
	 * @return the person gender spanish dto @ the service exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public PersonGenderSpanishDto isSpanGender(Long idPerson);

	/**
	 * Return email by id.
	 *
	 * @param idPerson
	 *            the id person
	 * @return the person email dto @ the service exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PersonEmailDto> returnEmailById(Long idPerson);

}
