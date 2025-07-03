package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.workload.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.workload.dto.AvailStaffGroupDto;
import us.tx.state.dfps.service.workload.dto.OnCallDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN80S
 * Tuxedo DAM Name :CCMN27D, CCMN28D, CCMN29D, CCMN79D, CDYN03D Class
 * Description: Assign Dao Class Apr 28, 2017 - 4:40:50 PM
 */

public interface AssignDao {

	// CCMN27D

	/**
	 * 
	 * Method Description: This method is used to retrieve Available Staff based
	 * on idUnit passed to DAM. Tuxedo Service Name:CCMN80S Tuxedo DAM Name
	 * :CCMN27D
	 * 
	 * @param idUnit
	 * @
	 */
	public List<AvailStaffGroupDto> getAvailStaffInfo(Long idUnit);

	// ccmn29d
	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name :CCMN29D
	 * 
	 * @param idStage
	 * @
	 */
	public List<AssignmentGroupDto> getAssignmentgroup(Long idStage);

	// CCMN79D
	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name : CCMN79D
	 * 
	 * @param onCallProgram
	 * @param onCallCounty
	 * @param dtOnCallStart
	 * @
	 */
	public OnCallDto getOnCallId(String onCallProgram, String onCallCounty, Date dtOnCallStart);

	// CCMN28D
	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name :CCMN28D
	 * 
	 * @param idOnCall
	 * @
	 */
	public List<AvailStaffGroupDto> getOnCallEmp(Long idOnCall);

	// Cdyn03d

	/**
	 * 
	 * Method Description: Tuxedo Service Name: CCMN80S Tuxedo DAM Name :
	 * CDYN03D
	 * 
	 * @param idStage
	 * @
	 */
	public Boolean getIndCSSReviewContact(Long idStage);

}
