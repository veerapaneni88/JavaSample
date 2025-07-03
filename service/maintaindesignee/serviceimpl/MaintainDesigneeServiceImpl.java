package us.tx.state.dfps.service.maintaindesignee.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.MaintainDesigneeDto;
import us.tx.state.dfps.service.admin.dao.EmpTempAssignDao;
import us.tx.state.dfps.service.admin.dto.EmpTempAssignDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.EmpTempAssignReq;
import us.tx.state.dfps.service.maintaindesignee.dao.MaintainDesigneeDao;
import us.tx.state.dfps.service.maintaindesignee.service.MaintainDesigneeService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtServiceImpl Sep 20, 2017- 9:47:04 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class MaintainDesigneeServiceImpl implements MaintainDesigneeService {
	@Autowired
	MaintainDesigneeDao maintainDesigneeDao;

	@Autowired
	EmpTempAssignDao empTempAssignDao;

	@Autowired
	MessageSource messageSource;

	/**
	 * CLSS16D - This DAM retrieves all designee assignments for the given
	 * employee
	 * 
	 * @param MaintainDesigneeFetchReq
	 * @return MaintainDesigneeRes
	 */
	@Override
	public List<MaintainDesigneeDto> getDesigneeDtls(Long idPerson) {

		return maintainDesigneeDao.getDesigneeDtls(idPerson);
	}

	/**
	 * CARC17D - This DAM adds/updates/deletes the EmpTempAssign Table
	 * 
	 * @param EmpTempAssignReq
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public Long updateEmpTempAssign(EmpTempAssignReq empTempAssignReq) {

		if (!ObjectUtils.isEmpty(empTempAssignReq.getEmpTempAssign())) {
			for (EmpTempAssignDto empTempAssignDto : empTempAssignReq.getEmpTempAssign()) {
				empTempAssignDao.updateEmpTempAssign(empTempAssignDto, empTempAssignReq.getReqFuncCd());
			}

		}

		return ServiceConstants.ZERO;
	}

}
