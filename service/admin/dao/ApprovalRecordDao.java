package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ApprovalHostDto;
import us.tx.state.dfps.service.admin.dto.ApprovalRecordDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for calling Ccmn55dDaoImpl Aug 8, 2017- 9:09:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ApprovalRecordDao {

	public List<ApprovalHostDto> getIDApproval(ApprovalRecordDto pInputDataRec);

}
