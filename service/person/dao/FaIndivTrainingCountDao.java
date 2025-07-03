package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.service.person.dto.FaIndivCountMscInDto;
import us.tx.state.dfps.service.person.dto.FaIndivCountMscOutDto;

public interface FaIndivTrainingCountDao {
	public void getFaIndivTrainingCount(FaIndivCountMscInDto faIndivCountMscInDto,
			FaIndivCountMscOutDto faIndivCountMscOutDto);

}
