package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.ChangeCountyReq;
import us.tx.state.dfps.service.common.response.ChangeCountyRes;

public interface ChangeCountyService {
	public ChangeCountyRes changeCountyService(ChangeCountyReq changeCountyReq);

	public void updateCountyInStage(ChangeCountyReq changeCountyReq, ChangeCountyRes changeCountyRes);

	public void updateCountyInCase(ChangeCountyReq changeCountyReq, ChangeCountyRes changeCountyRes);

}
