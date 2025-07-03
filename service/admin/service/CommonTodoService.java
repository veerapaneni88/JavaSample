package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.CommonTodoInDto;
import us.tx.state.dfps.service.admin.dto.CommonTodoOutDto;

public interface CommonTodoService {

	public CommonTodoOutDto TodoCommonFunction(CommonTodoInDto pInputMsg);

	public String replace_str(String orig, String substr, String rep);

	public CommonTodoOutDto callCsub40uService(CommonTodoInDto pInputMsg);
}
