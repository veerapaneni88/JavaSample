package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.workload.dto.UnitSummaryDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN29S Class.
 * Description: Unit Summary Page.
 */

public interface UnitSummaryDao {

	/**
	 * 
	 * Method Description: This method is designed to retrieve the ID UNIT, Unit
	 * Approver's ID PERSON and the Unit Approver's Unit Member Role given CD
	 * UNIT PROGRAM, CD UNIT REGION, and NBR UNIT.
	 * 
	 * @param assignWorkloadReq
	 * @return List<UnitSummaryDto> @ DAM Name: CCMN33D
	 */
	List<UnitSummaryDto> unitValidity(AssignWorkloadReq assignWorkloadReq);

	/**
	 * 
	 * Method Description: This method is designed to compare the CD UNIT MEMBER
	 * ROLE of a unit member given an ID PERSON and an ID UNIT to two CD UNIT
	 * MEMBER ROLEs that it is given.
	 * 
	 * @param unitSummaryDto
	 * @return Long
	 * @, DataNotFoundException DAM Name: CCMN32D
	 */
	Long checkAcessForUnit(UnitSummaryDto unitSummaryDto);

	/**
	 * 
	 * Method Description:This method is designed to retrieve all columns from
	 * the Unit Summary View given an ID UNIT
	 * 
	 * @param unitSummaryDto,assignWorkloadReq
	 * @return List<UnitSummaryDto> @ DAM Name: CCMN67D
	 */
	List<UnitSummaryDto> getUnitSummary(UnitSummaryDto unitSummaryDto, AssignWorkloadReq assignWorkloadReq);

	/**
	 * 
	 * Method Description:This method is called by the Unit Summary Search
	 * service, this method retrieves the total assaigments, Total Primary
	 * stages for the given PERSON ID
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSEC79D, CSEC80D
	 */
	Long getTotalAssignments(Long idPerson, String flag);

	/**
	 * 
	 * Method Description:This method will retrieve the total number of
	 * investigations assigned to the workload that are less than 60 days old
	 * and greater than 30 days.
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSECC3D, CSECC4D
	 */
	Long getInvSvcAssignmentsTa(Long idPerson, String flag);

	/**
	 * 
	 * Method Description:This method will retrieve all PRIMARY aps
	 * investigation assignments that are over 30 days old and 60 days old.
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSECC5D, CSECC6D
	 */
	Long getInvSvcAssignmentsTpa(Long idPerson, String flag);

}
