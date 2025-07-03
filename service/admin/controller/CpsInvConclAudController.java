package us.tx.state.dfps.service.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.CpsInvConclAudiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclAudoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclusionReqDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistiDto;
import us.tx.state.dfps.service.admin.service.CpsInvConclAudService;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.request.CpsInvSubstanceReq;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 1:52:30 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/cpsinvconclaud")
public class CpsInvConclAudController {

	@Autowired
	CpsInvConclAudService objCpsInvConclAudService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsInvConclAudController.class);

	/**
	 * 
	 * Method Name: CpsInvConclAudoDto Method Description: This service updates
	 * information modified on the CPS Investigation Conclusion window.
	 * 
	 * @param cpsInvConclusionReqDto
	 * @return CpsInvConclAudoDto
	 */
	@RequestMapping(value = "/cpsinvconclaudodto", headers = {
			"Accept=application/json;charset=windows-1252" }, method = RequestMethod.POST)
	public CpsInvConclAudoDto CpsInvConclAudoDto(@RequestBody CpsInvConclusionReqDto cpsInvConclusionReqDto) {
		log.debug("Entering method CpsInvConclAudoDto in CpsInvConclAudController");
		CpsInvConclAudiDto objCpsInvConclAudiDto = cpsInvConclusionReqDto.getCpsInvConclAudidto();
		SaveEmergAssistiDto saveEmergAssistiDto = cpsInvConclusionReqDto.getSaveEmergAssistiDto();
		CpsInvNoticesClosureReq cpsInvNoticeClosureReq = cpsInvConclusionReqDto.getCpsInvNoticesClosureReq();
		CpsInvSubstanceReq cpsInvSubstanceReq = cpsInvConclusionReqDto.getCpsInvSubstanceReq();
		CpsInvConclAudoDto objCpsInvConclAudoDto = null;
		objCpsInvConclAudoDto = objCpsInvConclAudService.saveCpsInvestigationDetails(objCpsInvConclAudiDto,
				saveEmergAssistiDto, cpsInvNoticeClosureReq,  cpsInvSubstanceReq	);
		log.debug("Exiting method CpsInvConclAudoDto in CpsInvConclAudController");
		return objCpsInvConclAudoDto;
	}
}
