package us.tx.state.dfps.service.workload.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.CaseTodoReq;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN12S Service
 * Class Description: This class is use for retrieving case to do list List Mar
 * 30, 2017 - 3:19:51 PM
 */
public interface CaseTodoService {
	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * case and a time period. Service Name: CCMN12S
	 * 
	 * @param caseTodoReq
	 * @return List<TodoDto> @
	 */
	List<TodoDto> getTodoList(CaseTodoReq caseTodoReq);
}
