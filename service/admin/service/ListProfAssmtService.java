package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.common.request.ListProfAssmtReq;
import us.tx.state.dfps.service.common.response.ListProfAssmtRes;
import us.tx.state.dfps.service.workload.dto.TodoDto;

public interface ListProfAssmtService

{
	public ListProfAssmtRes getListProfAssmtService(ListProfAssmtReq listProfAssmtReq);

	public TodoDto getTodoDtl(Long idEvent);

}