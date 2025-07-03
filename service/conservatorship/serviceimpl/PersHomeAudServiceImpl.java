package us.tx.state.dfps.service.conservatorship.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersHomeAudReq;
import us.tx.state.dfps.service.common.response.PersHomeAudRes;
import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRmvlAUDDao;
import us.tx.state.dfps.service.conservatorship.service.PersHomeAudService;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemInputDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersHomeAudServiceImpl for Person Home ADD,DELETE,Update Mar 1,
 * 2018- 6:18:29 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PersHomeAudServiceImpl implements PersHomeAudService {

	// Ccmn06u
	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	// Caud12d
	@Autowired
	PersonHomeRmvlAUDDao personHomeRmvlAUDDao;

	private static final Logger log = Logger.getLogger(PersHomeAudServiceImpl.class);

	/**
	 * Method Name: personHomeAud Method Description:Update Persons in the home
	 * 
	 * @param persHomeAudReq
	 * @return PersHomeAudRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersHomeAudRes personHomeAud(PersHomeAudReq persHomeAudReq) {
		log.debug("Entering method callPersHomeAudService in PersHomeAudServiceImpl");
		PersHomeAudRes persHomeAudRes = new PersHomeAudRes();
		String RetVal = ServiceConstants.NULL_STRING;
		PersonHomeRemInputDto personHomeRemInputDto = new PersonHomeRemInputDto();
		PersonHomeRemOutDto personHomeRemOutDto = new PersonHomeRemOutDto();
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		short usRow = 0;

		stageTaskInDto.setReqFuncCd(persHomeAudReq.getReqFuncCd());
		stageTaskInDto.setIdStage(persHomeAudReq.getIdStage());
		stageTaskInDto.setCdTask(persHomeAudReq.getCdTask());
		RetVal = stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto);
		if (!ObjectUtils.isEmpty(RetVal)) {
			RetVal = ServiceConstants.FND_SUCCESS;
		}

		if (ServiceConstants.FND_SUCCESS.equalsIgnoreCase(RetVal)) {
			while (usRow < persHomeAudReq.getConservatorshipPersonRowZeroDtos().size()) {
				personHomeRemInputDto.setReqFuncCd(
						persHomeAudReq.getConservatorshipPersonRowZeroDtos().get(usRow).getCdScrDataAction());
				personHomeRemInputDto.setIdPersHmRemoval(
						persHomeAudReq.getConservatorshipPersonRowZeroDtos().get(usRow).getIdPerson());
				personHomeRemInputDto
						.setIdEvent(persHomeAudReq.getConservatorshipPersonRowZeroDtos().get(usRow).getIdEvent());
				personHomeRemInputDto.setTsLastUpdate(
						persHomeAudReq.getConservatorshipPersonRowZeroDtos().get(usRow).getTsLastUpdate());
				personHomeRmvlAUDDao.personHomeRemovalAUD(personHomeRemInputDto, personHomeRemOutDto);
				usRow++;
			}
		}

		log.debug("Exiting method callPersHomeAudService in PersHomeAudServiceImpl");
		return persHomeAudRes;
	}

}
