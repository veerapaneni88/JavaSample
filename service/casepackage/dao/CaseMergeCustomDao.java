package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.casepackage.dto.PersonMergeDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

public interface CaseMergeCustomDao {

	/**
	 * Select of all merged cases for one case (MERGED_TO cases). Select of all
	 * merged cases for one case (MERGED_FROM cases). Created single method for
	 * by creat9ing criteria as input (cases merged to give case and cases
	 * merged from gevan case ) Method Description: legacy service name -
	 * CMSC38D & CMSC50D
	 * 
	 * @param uidcase
	 * @param criteria
	 * @return @
	 */
	public List<CaseMergeDetailDto> getCaseMergeDetails(Long uidcase, String criteria);

	/**
	 * This DAM will retrieve a full row using ID CASE MERGE TO as an input.
	 * 
	 * Service name- CCMN20S, DAM name - CLSC68D
	 * 
	 * @param ulIdCaseMergeTo
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CaseMergeDetailDto> getCaseMergeByIdCaseMergeTo(Long ulIdCaseMergeTo);

	/**
	 * This DAM will retrieve a full row using ID CASE MERGE TO as an input.
	 * 
	 * Service name- CCMN20S, DAM name - CLSC68D
	 * 
	 * @param ulIdCaseMergeTo
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	public CapsCase getCapsCaseByid(Long idCase);

	/**
	 * This call will retrieve a full row using ID CASE as an input.
	 * 
	 * Service name- CCFC39S
	 * 
	 * @param uidcase
	 * @param criteria
	 * @return List<PersonMergeDto>
	 * @throws DataNotFoundException
	 * @
	 */
	public List<PersonMergeDto> retrievRecentPersonId(Long idCase);
}
