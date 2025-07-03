package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.ContractVersionAudReq;
import us.tx.state.dfps.approval.dto.ContractVersionAudRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 19,
 * 2018- 1:05:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface ContractVersionAudDao {

	public ContractVersionAudRes contractVersionAud(ContractVersionAudReq ContractVersionAudRes);
}
