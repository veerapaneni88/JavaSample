package us.tx.state.dfps.service.admin.service;

import java.util.Date;

import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:TodoCreateService Aug 22, 2017- 9:00:18 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface TodoCreateService {
	public TodoCreateOutDto TodoCommonFunction(TodoCreateInDto pInputMsg);

	public String replace_str(String orig, String substr, String rep);

	public void getDays(Date d);

	public TodoCreateOutDto callCsub40uService(TodoCreateInDto pInputMsg);

}
