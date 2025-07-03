package us.tx.state.dfps.service.resourcehistoryaudit.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.ResourceHistoryAudit;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryAuditInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * has methods for Resource hsitory Jan 4, 2018- 2:00:53 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ResourceHistoryAuditDao {

	/**
	 * 
	 * Method Name: getResourceHistoryAuditByIdRescAndHmeStatus Method
	 * Description: DAM NAME: CLSS82D; This DAM will select a full row from the
	 * Resource History Audit table given an Id_Resource & facility type.
	 * 
	 * @param resourceHistoryAuditInDto
	 * @return List<ResourceHistoryAudit>
	 */
	public List<ResourceHistoryAudit> getResourceHistoryAuditByIdRescAndHmeStatus(
			ResourceHistoryAuditInDto resourceHistoryAuditInDto);
}