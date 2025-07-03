package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn57dDaoImpl Aug 8, 2017- 10:26:07 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface ApprovalEventDao {

	public List<ApprovalEventOutputDto> getApprovalEventLink(ApprovalEventLinkDto pInputDataRec);

}
