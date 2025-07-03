package us.tx.state.dfps.service.maintaindesignee.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.EmpTempAssignReq;
import us.tx.state.dfps.service.common.request.MaintainDesigneeFetchReq;
import us.tx.state.dfps.service.common.response.EmpTempAssignRes;
import us.tx.state.dfps.service.common.response.MaintainDesigneeFetchRes;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.maintaindesignee.service.MaintainDesigneeService;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service
 * Name:ARSafetyAssmtValueBean List, ARSafetyAssmt Details Class
 * Description:ARSafetyAssmtController class will have all operation which are
 * related to case and relevant page to case.
 * 
 */

@RestController
@RequestMapping("/maintaindesignee")
public class MaintainDesigneeController {

	@Autowired
	MaintainDesigneeService maintainDesigneeService;

	@Autowired
	MessageSource messageSource;

	/**
	 * CLSS16D - This DAM retrieves all designee assignments for the given
	 * employee
	 * 
	 * @param MaintainDesigneeFetchReq
	 * @return MaintainDesigneeRes
	 */
	@RequestMapping(value = "/getDesigneeDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MaintainDesigneeFetchRes getDesigneeDtls(
			@RequestBody MaintainDesigneeFetchReq maintainDesigneeReq) {

		MaintainDesigneeFetchRes maintainDesigneeRes = new MaintainDesigneeFetchRes();

		if (TypeConvUtil.isNullOrEmpty(maintainDesigneeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		maintainDesigneeRes
				.setMaintainDesignee(maintainDesigneeService.getDesigneeDtls(maintainDesigneeReq.getIdPerson()));
		return maintainDesigneeRes;

	}

	/**
	 * CARC17D - This DAM adds/updates/deletes the EmpTempAssign Table
	 * 
	 * @param MaintainDesigneeFetchReq
	 * @return MaintainDesigneeRes
	 */
	@RequestMapping(value = "/updateEmpTempAssign", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EmpTempAssignRes updateEmpTempAssign(@RequestBody EmpTempAssignReq empTempAssignReq) {

		EmpTempAssignRes empTempAssignRes = new EmpTempAssignRes();

		if (!ObjectUtils.isEmpty(empTempAssignReq.getEmpTempAssign())) {

			empTempAssignRes.setResult(maintainDesigneeService.updateEmpTempAssign(empTempAssignReq));

		}

		return empTempAssignRes;

	}

}
