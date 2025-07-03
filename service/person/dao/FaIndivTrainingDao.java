package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.service.person.dto.FaIndivTrainLssInDto;
import us.tx.state.dfps.service.person.dto.FaIndivTrainLssOutDto;

public interface FaIndivTrainingDao {
	public void getFaIndivTraining(FaIndivTrainLssInDto faIndivTrainLssInDto,
			FaIndivTrainLssOutDto faIndivTrainLssOutDto);

}
