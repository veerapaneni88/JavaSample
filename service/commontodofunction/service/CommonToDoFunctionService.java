package us.tx.state.dfps.service.commontodofunction.service;

import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;

public interface CommonToDoFunctionService {

	public TodoCreateOutDto callCSUB40U(TodoCreateInDto todoCreateOutDto);

	public TodoCreateOutDto TodoCommonFunction(TodoCreateInDto todoCreateOutDto);

	public TodoCreateOutDto doCSUB40UO(TodoCreateInDto todoCreateOutDto);

}
