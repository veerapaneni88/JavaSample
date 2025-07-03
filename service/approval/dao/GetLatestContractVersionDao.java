package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.GetLatestContractVersionReq;
import us.tx.state.dfps.approval.dto.GetLatestContractVersionRes;

public interface GetLatestContractVersionDao {

	public GetLatestContractVersionRes getLatestContractVersion(
			GetLatestContractVersionReq getLatestContractVersionReq);
}
