package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.common.request.CaseMergeSplitSaveReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateRes;
import us.tx.state.dfps.service.common.request.RetrvCaseMergeReq;
import us.tx.state.dfps.service.common.response.CaseMergeUpdateRes;
import us.tx.state.dfps.service.common.response.RetrvCaseMergeRes;

import java.util.List;

public interface CaseMergeService {

	public RetrvCaseMergeRes getCaseMerges(RetrvCaseMergeReq retrvCaseMergeReq);

	/**
	 * This service will save case merges and case splits to the database with
	 * the pending flag. The actual DB updates will be done in a batch process.
	 * 
	 * @param caseMergeUpdateReq
	 * @return @
	 */
	CaseMergeUpdateRes manageCaseMerge(CaseMergeSplitSaveReq caseMergeSplitSaveReq);

	/**
	 * This Service will verify that the ID case passed to it is an existing
	 * case that has not previously been a Merge From case. The service will
	 * also verify that the ID case passed is not pending another merge. If the
	 * security requirement have not been met, the service will also verify that
	 * the logged in user is authorized to perform the merge. Finally, a series
	 * of edit checks will be run to see if cases are eligible to be merged.
	 *
	 */
	CaseMergeSplitValidateRes verifyCaseMerge(CaseMergeSplitValidateReq caseMergeReq);

	List<Long> getNeubusCaseList(Long idCase);
}
