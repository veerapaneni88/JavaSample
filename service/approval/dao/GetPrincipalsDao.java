package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.GetPrincipalsReq;
import us.tx.state.dfps.approval.dto.GetPrincipalsRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 5:32:32 PM © 2018 Texas Department of Family and Protective Services
 */

public interface GetPrincipalsDao {

	public GetPrincipalsRes getPrincipals(GetPrincipalsReq getPrincipalsResq);

}
