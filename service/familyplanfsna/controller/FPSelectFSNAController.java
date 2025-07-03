/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is Service Controller method for select FSNA and select FSNA Evaluation Page
 *Jul 6, 2018- 10:22:27 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplanfsna.controller;

import java.util.List;
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
import us.tx.state.dfps.service.common.request.FPSelectFSNAReq;
import us.tx.state.dfps.service.common.response.FPSelectFSNARes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNADto;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNAValidationDto;
import us.tx.state.dfps.service.familyplanfsna.service.FPSelectFSNAService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is
 * Service Controller method for select FSNA and select FSNA Evaluation Page>
 * Jul 6, 2018- 10:22:27 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/familyPlan")
public class FPSelectFSNAController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FPSelectFSNAService familyPlanFSNAService;

	/**
	 * This service is to get the list of FSNA and Validation information for
	 * Select FSNA and Select FSNA Evaluation
	 * 
	 * @param fsnaReq
	 * @return
	 */
	@RequestMapping(value = "/selectFSNA", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FPSelectFSNARes getFSNAList(@RequestBody FPSelectFSNAReq fsnaReq) {
		FPSelectFSNARes fsnaRes = new FPSelectFSNARes();
		if (TypeConvUtil.isNullOrEmpty(fsnaReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(fsnaReq.getCdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		List<SelectFSNADto> fsnaList = familyPlanFSNAService.getFsnaList(fsnaReq.getIdStage(), fsnaReq.getCdStage());
		List<SelectFSNAValidationDto> fsnaValidationList = familyPlanFSNAService
				.getFsnaValidationList(fsnaReq.getIdStage());
		fsnaRes.setFsnaList(fsnaList);
		fsnaRes.setFsnaValidationList(fsnaValidationList);
		return fsnaRes;
	}

}
