package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.common.response.LegalActionsRes;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:<DAO Interface for fetching legal event details>
 *
 * Aug 8, 2017- 4:02:33 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface LegalActionEventDao {

	/**
	 * Gets the most recent FDTC subtype.
	 *
	 * @param personId
	 *            the person id
	 * @return List<LegalActionEventOutDto> the most recent FDTC subtype @ the
	 * service exception
	 */
	public List<LegalActionEventOutDto> getMostRecentFDTCSubtype(LegalActionEventInDto legalActionEventInDto);

	/**
	 * Gets the open FBSS stage.
	 *
	 * @param caseId
	 *            the case id
	 * @return List the open FBSS stage @ the service exception
	 */
	public List<StageDto> getOpenFBSSStage(LegalActionEventInDto legalActionEventInDto);

	/**
	 * Gets the legal action rel fictive kin. This method fetches Legal Action
	 * Date of PMC to Relative or Fictive Kin
	 *
	 * @param idStage
	 *            the id stage
	 * @return LegalActionEventOutDto the legal action rel fictive kin @ the
	 * service exception
	 */
	public LegalActionEventOutDto getLegalActionRelFictiveKin(LegalActionEventInDto legalActionEventInDto);

	/**
	 * 
	 * Method Name: getLegalEventDtls Method Description: This method will get
	 * data from LEGAL_ACTION and EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<LegalActionEventOutDto> @
	 */
	public List<LegalActionEventOutDto> getLegalEventDtls(LegalActionEventInDto pInputDataRec);

	/**
	 * This is a method to set all input parameters(of the stored procedure).
	 */
	public LegalActionsRes executeStoredProc(List<Object> arrayList);

	/**
	 * 
	 * Method Name: getLegalActionType Method Description:CSESD6D
	 * 
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	LegalActionEventOutDto getLegalActionType(Long idPerson, Long idCase, Long idStage);

	/**
	 * Method Name: getCCORLegalActionType
	 * Method Description: gets the last CCOR Legal action
	 *
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	LegalActionEventOutDto getCCORCCVSLegalAction(Long idPerson, Long idCase, Long idStage);

	/**
	 * Method Name: selectLatestLegalActionOutcome
	 * Method Description: selects the most recent outcome for Legal action
	 *
	 * @param legalActionEventInDto
	 * @return LegalActionEventOutDto
	 */
	LegalActionEventOutDto selectLatestLegalActionOutcome(LegalActionEventInDto legalActionEventInDto);
}
