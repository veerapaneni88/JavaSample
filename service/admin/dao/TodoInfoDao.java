package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.TodoInfoInDto;
import us.tx.state.dfps.service.admin.dto.TodoInfoOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cses08dDao
 * Aug 10, 2017- 2:05:26 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface TodoInfoDao {

	public List<TodoInfoOutDto> getTodoInfo(TodoInfoInDto pInputDataRec);
}
