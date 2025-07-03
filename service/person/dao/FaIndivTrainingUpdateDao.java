package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.service.person.dto.FaIndivTrainingInDto;
import us.tx.state.dfps.service.person.dto.FaIndivTrainingOutDto;

public interface FaIndivTrainingUpdateDao {
	public void updateFaIndivTraining(FaIndivTrainingInDto faIndivTrainingInDto,
			FaIndivTrainingOutDto faIndivTrainingOutDto);

}
