package us.tx.state.dfps.service.investigation.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.forms.dto.ApsFacilNarrDto;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.workload.dto.ExternalDocumentDetailDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods used by service CINV71S Apr 30, 2018- 2:47:01 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FacilityAbuseInvReportDao {

	/**
	 * Method Name: getContactNarr Method Description: This method retreives
	 * contact narr (DAM: CLSC13D)
	 * 
	 * @param idStage
	 * @param dtSearchDateFrom
	 * @param dtSearchDateTo
	 * @return List<ContactNarrDto>
	 */
	public List<ContactNarrDto> getContactNarr(Long idStage, Date dtSearchDateFrom, Date dtSearchDateTo);

	/**
	 * Method Name: getExtDocAlleg Method Description: Retrieves all info about
	 * Ext Documentation Allegation based upon idCase (DAM: CLSC22D)
	 * 
	 * @param idCase
	 * @param dtSearchDateFrom
	 * @param dtSearchDateTo
	 * @return List<ExternalDocumentDetailDto>
	 */
	public List<ExternalDocumentDetailDto> getExtDocAlleg(Long idCase, Date dtSearchDateFrom, Date dtSearchDateTo);

	/**
	 * Method Name: getPrimaryWorker Method Description: Gets name of primary
	 * worker plus other information (DAM: CCMN30D)
	 * 
	 * @param idStage
	 * @return StagePersDto
	 */
	public StagePersDto getPrimaryWorker(Long idStage);

	/**
	 * Method Name: getApprovalDate Method Description: Selects Most Recent
	 * Approval Date for a given Approval ID (DAM: CSESF5D)
	 * 
	 * @param idApproval
	 * @param cdApproversStatus
	 * @return Date
	 */
	public Date getApprovalDate(Long idApproval, String cdApproversStatus);

	/**
	 * Method Name: getApprover Method Description: Return the approver name
	 * associated with a particular EventID (DAM: CSECF0D)
	 * 
	 * @param idEvent
	 * @return ExtreqDto
	 */
	public ExtreqDto getApprover(Long idEvent);

	/**
	 * Method Name: getStageIdForDataFix Method Description: Return the idStage to Launch the 
	 * APS Abuse and Neglect in Editable Mode
	 *  
	 * @param idStage
	 * @return Long
	 */
	public Long getStageIdForDataFix(Long idStage);

	public ApsFacilNarrDto getApsFacilNarr(Long idStage) throws SQLException;
	
	public void updateApsFacilNarr(byte[] document, Long idStage, Date dtLastUpdate) throws SQLException;

}
