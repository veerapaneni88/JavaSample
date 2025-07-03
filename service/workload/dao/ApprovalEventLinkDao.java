package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.Approval;
import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkEventDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN19S Class
 * Description: ApprovalEventLink DAO Interface Apr 3, 2017 - 3:45:39 PM
 */
public interface ApprovalEventLinkDao {

	/**
	 * 
	 * Method Description: This Method is used to retrieve approvaleventlink
	 * details based on idEvent. Dam Name: CCMN55D
	 * 
	 * @param idEvent
	 * @return approvalDto @
	 */
	public ApprovalEventLinkDto getApprovalEventLinkID(Long idEvent);

	/**
	 * 
	 * Method Description: This Method is will Add several records and delete
	 * the records to the APPROVAL_EVENT_LINK table based on the input & request
	 * indicator DAM Name: CCMN91D
	 * 
	 * @param archInputDto
	 * @param approvalEventLinkDto
	 * @return String @
	 */
	public String getApprovalEventLinkAUD(ServiceReqHeaderDto serviceReqHeaderDto,
			ApprovalEventLinkDto approvalEventLinkDto);

	/**
	 * 
	 * Method Description: This method will retrieve for given approval. DAM
	 * Name: CCMN57D
	 * 
	 * @param idApproval
	 * @return List<ApprovalEventLinkEventDto>
	 */
	public List<ApprovalEventLinkEventDto> approvalEventLinkSearchByApprovalId(Long idApproval);

	public void saveorUpdate(ApprovalEventLink approvalEventLink);
}
