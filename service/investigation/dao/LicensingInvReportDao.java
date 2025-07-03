package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.service.investigation.dto.LicInvRepPrincipalsDto;
import us.tx.state.dfps.service.person.dto.PersonListAlleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * DAO methods to be implemented Apr 13, 2018- 11:33:10 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface LicensingInvReportDao {

	/**
	 * Method Name: getPrincipals Method Description: Principal Information
	 * retrieval (DAM: CLSCE1D)
	 * 
	 * @param idStage
	 * @return List<LicInvSumPrincipalsDto>
	 */
	public List<LicInvRepPrincipalsDto> getPrincipals(Long idStage);

	/**
	 * Method Name: getPersonAllegationHistDtls Method Description: This method
	 * is retrieve all person id's in the alleged_perpetrator column on the
	 * allegation_history window for each of the allegations on the allegation
	 * table for the stage_id passed in if the case program is AFC. DAM Name:
	 * CLSC89D
	 * 
	 * @param idStage
	 */
	public List<PersonListAlleDto> getPersonAllegationHistDtls(Long idStage);

	/**
	 * Method Name: getAllVicitimAllegationHist. Method Description: This method
	 * will retrieve all victims for a case from the ALLEGATION_HISTORY table.
	 * DAM Name: CLSC90D
	 * 
	 * @param idStage
	 * @return List<PersonListAlleDto>
	 */
	public List<PersonListAlleDto> getAllVicitimAllegationHist(Long idStage);

}
