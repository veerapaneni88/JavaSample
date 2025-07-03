package us.tx.state.dfps.service.investigation.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dao for
 * cinv68s Mar 16, 2018- 11:32:16 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface FacilityInvSumDao {

	/**
	 * Method Name: getContactDate Method Description: Retrieve the Date the
	 * first Request for Review contact occurred given a specific id_stage. DAM
	 * : CINVB8D
	 * 
	 * @return Date
	 * @throws DataNotFoundException
	 */
	public Date getContactDate(Long idStage);

	/**
	 * Method Name: getAllegationInfo Method Description: This DAM retrieves a
	 * full row from the Allegation ,Facility_Injury, and Person tables. DAM :
	 * CLSC17D
	 * 
	 * @return FacilityAllegationInfoDto
	 * @throws DataNotFoundException
	 */
	public FacilityAllegationInfoDto getAllegationInfo(Long idStage);

	/**
	 * Method Name: getAllegationInfo Method Description: This DAM retrieves a
	 * full row from the Allegation and Person tables. DAM : CLSCG0D
	 * 
	 * @return FacilityAllegationInfoDto @throws
	 */
	public List<FacilityAllegationInfoDto> getAllegationType(Long idStage);

}
