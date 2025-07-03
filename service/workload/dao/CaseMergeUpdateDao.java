package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.domain.CaseMerge;
import us.tx.state.dfps.service.workload.dto.CaseMergeUpdateDto;
import us.tx.state.dfps.service.workload.dto.EventStageDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCFC41S
 * Class Description:surface layer for CaseMergeUpdateDaoImpl Apr 19, 2017 -
 * 12:30:40 PM
 */

public interface CaseMergeUpdateDao {
	/**
	 * 
	 * Method Description:call saveCaseMerge in CaseMergeUpdateDaoImpl
	 * DAM-CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeUpdateDto @
	 */
	public CaseMergeUpdateDto saveCaseMerge(CaseMerge caseMerge);

	/**
	 * 
	 * Method Description:call updateCaseMerge in CaseMergeUpdateDaoImpl
	 * DAM-CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeUpdateDto @
	 */
	public CaseMergeUpdateDto updateCaseMerge(CaseMerge caseMerge);

	/**
	 * 
	 * Method Description:call deleteCaseMerge in CaseMergeUpdateDaoImpl
	 * DAM-CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeUpdateDto @
	 */
	public CaseMergeUpdateDto deleteCaseMerge(CaseMerge caseMerge);

	/**
	 * 
	 * Method Description: call searchAllMergedCases in CaseMergeUpdateDaoImpl
	 * DAM-CMSC38D
	 * 
	 * @param caseId
	 * @return List<CaseMergeUpdateDto> @
	 */
	public List<CaseMergeUpdateDto> searchAllMergedCases(Long caseId);

	/**
	 * 
	 * Method Description:call searchCaseName in CaseMergeUpdateDaoImpl
	 * DAM-CMSC38D
	 * 
	 * @param idCase
	 * @return String @
	 */
	public String searchCaseName(Long idCase);

	/**
	 * 
	 * Method Description:call searchPersonNameFull in CaseMergeUpdateDaoImpl if
	 * ulIdCaseMergePersSplit or ulIdCaseMergePersMerge has value DAM-CMSC38D
	 * 
	 * @param idPerson
	 * @return String @
	 */
	String searchPersonNameFull(Long idPerson);

	/**
	 * CLSS86D : Gets records from EVENT & STAGE tables given idCase, event
	 * status and event type
	 * 
	 * 
	 */
	EventStageDto searchCaseStage(Long idCase, String pendStatus, String programCCL);

	/**
	 * CLSC67D : This DAM will retrieve a full row from the Case Merge table
	 * based on Id Case Merge From.
	 * 
	 * @param caseMergeId
	 * @return @
	 */
	List<CaseMergeUpdateDto> checkForReverseMerge(Long caseMergeId);

	/**
	 * CSESA6D : This DAM checks the number of times that a case has been merged
	 * if it has been merged three times then do not let the next merge thru
	 * update ErrorCount
	 * 
	 * @param caseMergeId
	 * @return @
	 */
	Integer checkForMergeCounts(Long caseMergeId);

	/**
	 * CSESF2D : This function will check whether the records retention
	 * destruction date of both Case Merge To or Case Merge From is in the
	 * future. Also compare the records retention destruction date of the first
	 * case with the date of case opened of other case, then throw appropriate
	 * error message once users click on the validation button in the Case
	 * Merge/Split Detail window.
	 */
	Map<String, Date> checkForRecDstryDate(Long caseMergeId);
}
