/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Impl for select FSNA and select FSNA Evaluation
 *Jul 6, 2018- 10:24:50 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplanfsna.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.familyplanfsna.dao.FPSelectFSNADao;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNADto;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNAValidationDto;
import us.tx.state.dfps.service.familyplanfsna.service.FPSelectFSNAService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * Impl for select FSNA and select FSNA Evaluation> Jul 6, 2018- 10:24:50 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
public class FPSelectFSNAServiceImpl implements FPSelectFSNAService {

	@Autowired
	FPSelectFSNADao selectFSNADao;

	/**
	 * Method Name: getFsnaList Method Description: get FSNA List for FPR, FSU
	 * and FRE Stages.
	 * 
	 * @param idStage,
	 *            cdStage
	 * @return List<SelectFSNADto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<SelectFSNADto> getFsnaList(Long idStage, String cdStage) {
		return selectFSNADao.getFPFsnaList(idStage, cdStage);
	}

	/**
	 * Method Name: getFsnaValidationList Method Description: get FSNA
	 * Validation View for FPR, FSU and FRE Stages.
	 * 
	 * @param idStage
	 * @return List<SelectFSNAValidationDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<SelectFSNAValidationDto> getFsnaValidationList(Long idStage) {
		return selectFSNADao.getFSNAValidation(idStage);
	}

}
