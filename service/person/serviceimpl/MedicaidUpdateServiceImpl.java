package us.tx.state.dfps.service.person.serviceimpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.MedicaidUpdate;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.person.dao.MedicaidUpdateDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.service.MedicaidUpdateService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN44S Class
 * Description: This class is use for actions for MedicaidUpdate table Apr 9,
 * 2017 - 4:19:51PM
 */

@Service
@Transactional
public class MedicaidUpdateServiceImpl implements MedicaidUpdateService {

	@Autowired
	MedicaidUpdateDao medicaidUpdateDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	StageDao stageDao;

	/**
	 * This dam will add rows to the Medicaid Update table Service Name-
	 * CCMN44S, DAM Name-CAUD99D
	 * 
	 * @param medicaidUpdateDto
	 * @param action
	 * @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void editMedicaidUpdate(MedicaidUpdateDto medicaidUpdateDto, String action) {

		MedicaidUpdate medicaidUpdate = new MedicaidUpdate();
		Date date = new Date();

		if (!ObjectUtils.isEmpty(action)) {
			if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				medicaidUpdate.setDtLastUpdate(date);
				if (medicaidUpdateDto.getIdMedUpdPerson() != null) {
					Person person = personDao.getPersonByPersonId(medicaidUpdateDto.getIdMedUpdPerson());
					medicaidUpdate.setPerson(person);
				}

				if (medicaidUpdateDto.getIdMedUpdStage() != null) {
					Stage stage = stageDao.getStageEntityById(medicaidUpdateDto.getIdMedUpdStage());
					medicaidUpdate.setStage(stage);
				}
				if (medicaidUpdateDto.getIdMedUpdRecord() != null) {
					medicaidUpdate.setIdMedUpdRecord(medicaidUpdateDto.getIdMedUpdRecord());
				}
				if (medicaidUpdateDto.getCdMedUpdType() != null) {
					medicaidUpdate.setCdMedUpdType(medicaidUpdateDto.getCdMedUpdType());
				}
				if (medicaidUpdateDto.getCdMedUpdTransType() != null) {
					medicaidUpdate.setCdMedUpdTransType(medicaidUpdateDto.getCdMedUpdTransType());
				}
				medicaidUpdateDao.saveMedicaidUpdate(medicaidUpdate);

			}

		}

	}

}
