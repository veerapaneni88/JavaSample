package us.tx.state.dfps.service.legalnotice.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.LegalNoticeFormReq;
import us.tx.state.dfps.service.common.request.LegalNoticeReq;
import us.tx.state.dfps.service.common.request.MailDateSaveReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.LegalNoticeListRes;
import us.tx.state.dfps.service.common.response.LegalNoticeRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legalnotice.service.LegalNoticeService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * service controller class for legal Notice detail> June 07, 2018- 1:15:28 PM ©
 * 2017 Texas Department of Family and Protective Services.
 */
@RestController
@RequestMapping("/legalNotice")
public class LegalNoticeController {

	/** The legal status service. */
	@Autowired
	LegalNoticeService legalNoticeService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(LegalNoticeController.class);

	/**
	 * Method Name: getLegalNoticeDetail Method Description :This is the
	 * retrieval service for Legal Notice detail.
	 *
	 * @param LegalNoticeReq
	 * @return LegalNoticeRes
	 */
	@RequestMapping(value = "/getLegalNoticeDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalNoticeRes getLegalNoticeDetail(@RequestBody LegalNoticeReq legalNoticeReq) {
		LegalNoticeRes legalNoticeRes = new LegalNoticeRes();
		log.info("Entering method getLegalNoticeDetail in LegalNoticeController");

		if (TypeConvUtil.isNullOrEmpty(legalNoticeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalNoticeReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		// Call service function for fetching detail
		legalNoticeRes = legalNoticeService.fetchLegalNoticeDtl(legalNoticeReq);

		log.info("Exiting method getLegalNoticeDetail in LegalNoticeController");

		return legalNoticeRes;
	}

	/**
	 * Method Name: saveLegalNoticeDtl Method Description: This method will add
	 * , update legal notice detail.
	 *
	 * @param legalNoticeReq
	 * @return LegalNoticeRes
	 */
	@RequestMapping(value = "/saveLegalNoticeDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceResHeaderDto saveLegalNoticeDtl(@RequestBody LegalNoticeReq legalNoticeReq){
		log.info("Entering method saveLegalNoticeDtl in LegalNoticeController");

		// call service layer
		return legalNoticeService.saveLegalNoticeDtl(legalNoticeReq);
	}

	/**
	 * Method Name: saveAndCompleteLegalNoticeDtl Method Description: This
	 * method will save and complete legal notice detail.
	 *
	 * @param LegalNoticeReq
	 * @return LegalNoticeRes
	 */
	@RequestMapping(value = "/saveAndCompLegalNoticeDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceResHeaderDto saveAndCompleteLegalNoticeDtl(@RequestBody LegalNoticeReq legalNoticeReq){
		log.info("Entering method saveAndCompleteLegalNoticeDtl in LegalNoticeController");

		// call service layer
		return legalNoticeService.saveLegalNoticeDtl(legalNoticeReq);
	}

	/**
	 * Method Name: getLegalNoticeList Method Description: This method will
	 * retrieve legal notice list.
	 *
	 * @param LegalNoticeReq
	 * @return LegalNoticeListRes
	 */
	@RequestMapping(value = "/getLegalNoticeList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalNoticeListRes getLegalNoticeList(@RequestBody LegalNoticeReq legalNoticeReq) {
		if (ObjectUtils.isEmpty(legalNoticeReq.getIdCase()) || legalNoticeReq.getIdCase() == 0) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(legalNoticeReq.getIdStage()) || legalNoticeReq.getIdStage() == 0) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return legalNoticeService.getLegalNoticeList(legalNoticeReq);
	}

	/**
	 * Method Name: getLegalNoticeForm Method Description: This method will
	 * retrieve legal notice form to be displayed in the legal notice list
	 * 
	 * @param LegalNoticeFormReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getLegalNoticeForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getLegalNoticeForm(@RequestBody LegalNoticeFormReq legalNoticeFormReq) {

		if (TypeConvUtil.isNullOrEmpty(legalNoticeFormReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalNoticeFormReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(legalNoticeService.getLegalNoticeForm(legalNoticeFormReq)));
		return commonFormRes;

	}

	/**
	 * Method Name: saveMailedDate Method Description: This method will save
	 * mailed date
	 *
	 * @param LegalNoticeReq
	 * @return LegalNoticeListRes
	 */
	@RequestMapping(value = "/saveMailedDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ServiceResHeaderDto saveMailedDate(@RequestBody MailDateSaveReq mailDateSaveReq) {
		if (ObjectUtils.isEmpty(mailDateSaveReq.getRecepnt())
				|| (ObjectUtils.isEmpty(mailDateSaveReq.getRecepnt().getIdLegalNoticeRecpnt()))) {
			throw new InvalidRequestException(
					messageSource.getMessage("MailDateSaveReq.idLegalNoticeRecpnt", null, Locale.US));
		}

		if (ObjectUtils.isEmpty(mailDateSaveReq.getRecepnt())
				|| (ObjectUtils.isEmpty(mailDateSaveReq.getRecepnt().getDtCreated()))) {
			throw new InvalidRequestException(messageSource.getMessage("MailDateSaveReq.dtCreated", null, Locale.US));
		}

		return legalNoticeService.saveMailedDate(mailDateSaveReq);
	}

}
