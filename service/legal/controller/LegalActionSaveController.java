package us.tx.state.dfps.service.legal.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.LegalActionSaveReq;
import us.tx.state.dfps.service.common.request.LegalSaveReq;
import us.tx.state.dfps.service.common.request.LegalStatusUpdateReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.LegalActionSaveRes;
import us.tx.state.dfps.service.common.response.LegalSaveRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legal.dto.LegalActionSaveOutDto;
import us.tx.state.dfps.service.legal.service.LegalActionSaveService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * save service for the Legal Action/Outcome window. Oct 25, 2017- 10:33:06 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/legalActionOutcomeSave")
public class LegalActionSaveController {

	@Autowired
	private LegalActionSaveService legalActionSaveService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LegalActionSaveController.class);

	/**
	 * 
	 * Method Name: legalActionsOutcomeSave Method Description:This is the save
	 * service for the Legal Action/Outcome window. TUXEDO: CSUB39S
	 * 
	 * @param legalActionSaveReq
	 * @return LegalActionSaveRes
	 */
	@RequestMapping(value = "/legalActionSave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegalActionSaveRes legalActionsOutcomeSave(
			@RequestBody LegalActionSaveReq legalActionSaveReq) {
		log.debug("Entering method callCSUB39S in LegalActionSaveController");

		if (TypeConvUtil.isNullOrEmpty(legalActionSaveReq.getLegalActionSaveDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("LegalActionSaveDto.input.mandatory", null, Locale.US));
		}

		LegalActionSaveOutDto legalActionSaveOutDto = legalActionSaveService
				.legalActionsOutcomeSave(legalActionSaveReq);
		LegalActionSaveRes legalActionSaveRes = new LegalActionSaveRes();
		legalActionSaveRes.setLegalActionSaveOutDto(legalActionSaveOutDto);

		log.debug("Exiting method callCSUB39S in LegalActionSaveController");
		return legalActionSaveRes;
	}

	/**
	 * 
	 * Method Name: callCSUB39S Method Description:This is the save service for
	 * the Legal Action/Outcome window for multiple stages and person selected.
	 * 
	 * @param legalSaveReq
	 * @return LegalSaveRes
	 */
	@RequestMapping(value = "/callCSUB39S", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegalSaveRes callCSUB39S(@RequestBody LegalSaveReq legalSaveReq) {
		log.debug("Entering method callCSUB39S in LegalActionSaveController");

		if (TypeConvUtil.isNullOrEmpty(legalSaveReq.getLegalActionSaveInDtoList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("LegalActionSaveInDtoList.input.mandatory", null, Locale.US));
		}

		List<LegalActionSaveOutDto> legalActionSaveOutDtoList = legalActionSaveService
				.legalActionsOutcomeSaveMultiple(legalSaveReq.getLegalActionSaveInDtoList());
		LegalSaveRes legalSaveRes = new LegalSaveRes();
		legalSaveRes.setLegalActionSaveOutDtoList(legalActionSaveOutDtoList);

		log.debug("Exiting method callCSUB39S in LegalActionSaveController");
		return legalSaveRes;

	}
	
	@RequestMapping(value = "/checkTMCExists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes checkTMCExists(@RequestBody LegalStatusUpdateReq legalStatusUpdateReq) {
		return legalActionSaveService
		.checkTMCExists(Long.valueOf(legalStatusUpdateReq.getIdStage()),legalStatusUpdateReq.getLegalStatusOutDto().getCdLegalStatStatus());
		
	}
}
