package us.tx.state.dfps.service.notiftolawenforce.dao;

import java.util.List;

import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDateDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<dao class
 * for cinv80s> Mar 14, 2018- 5:42:47 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface NotifToLawEnforcementDao {

	/**
	 * method description: This DAO performs a Query of the Facility
	 * Investigation Dtl table.
	 * 
	 * @param idStage
	 * @return FacilInvDtlDto
	 */
	public FacilInvDtlDto getFacilityInvDtlbyId(Long idStage);

	/**
	 * method description: This DAO gets prior stage info, stage starts date by
	 * idStage. Service Name - CINV80S, DAM Name - CINV86D
	 * 
	 * @param idStage
	 * @return PriorStageDto
	 */
	public PriorStageDto getPriorStagebyId(Long idStage);

	/**
	 * method description: This DAO is to fetch multiple MHMR facility
	 * addresses. Service Name - CINV80S, DAM Name - CLSCGCD
	 * 
	 * @param idStage,
	 *            idCase
	 * @return List<MultiAddressDto>
	 */
	public List<MultiAddressDto> getMultiAddress(Long idStage, Long idCase);

	/**
	 * Method Name: getMinContactDate Method Description: This Dao is to fetch
	 * earliest contact date based on id stage.
	 * 
	 * @param idStage
	 * @return ContactDateDto
	 */
	public ContactDateDto getMinContactDate(Long idStage);
}
