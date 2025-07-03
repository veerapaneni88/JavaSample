package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.CaseTodoReq;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.CaseTodoService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN12S Class
 * Description: This class is doing service Implementation for to do list Mar
 * 30, 2017 - 4:23:07 PM
 */
@Service
@Transactional
public class CaseTodoServiceImpl implements CaseTodoService {

	@Autowired
	TodoDao todoDao;
	private static final Logger log = Logger.getLogger(CaseTodoServiceImpl.class);

	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * case and a time period. Service Name: CCMN12S
	 * 
	 * @param caseTodoReq
	 * @return List<CaseTodoDto> @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<TodoDto> getTodoList(CaseTodoReq caseTodoReq) {
		List<TodoDto> todoServiceOutput = todoDao.getTodoList(caseTodoReq);
		log.info("TransactionId :" + caseTodoReq.getTransactionId());
		return todoServiceOutput;
	}
}
