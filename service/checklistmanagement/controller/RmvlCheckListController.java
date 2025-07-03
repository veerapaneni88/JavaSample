package us.tx.state.dfps.service.checklistmanagement.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService;
import us.tx.state.dfps.service.common.request.RmvlCheckListReq;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.RmvlCheckListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@RestController
@RequestMapping("/removalChecklist")
public class RmvlCheckListController {

	@Autowired
	RmvlCheckListService removalChecklistService;

	@Autowired
	MessageSource messageSource;
	private static final Logger log = Logger.getLogger(RmvlCheckListController.class);

	/**
	 * Method Description: Method to get all the checklist.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/getchecklists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes getCheckLists() {
		return removalChecklistService.getRmvlChcklsts();

	}

	/**
	 * Method Description: Method to get a single checklist.
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/getchecklistdtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes getCheckListDtl(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		Long listId = checklistReq.getIdRmvlChcklstLookup();
		Long idPerson = checklistReq.getIdPerson();
		RmvlCheckListRes result = new RmvlCheckListRes();
		result = removalChecklistService.getRmvlChcklstDtl(listId,idPerson, checklistReq.getIdRmvlChcklstLink(), checklistReq.getIdStage(), checklistReq.getIdRmvlEvent());
		if (null == result.getRmvlChcklstLookupDto()) {
			throw new DataNotFoundException(
					messageSource.getMessage("rmvlChcklst.rmvlChcklstDtl.data ", null, Locale.US));
		}
		return result;
	}

	/**
	 * Method Description: Method to copy existing checklist to a new checklist.
	 * 
	 * @param checklistReq
	 * @return
	 */
	@RequestMapping(value = "/copychecklist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes copyChecklist(@RequestBody RmvlCheckListReq checklistReq) {
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdRmvlChcklstLookup())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.copyRmvlChcklst(checklistReq.getIdRmvlChcklstLookup());

	}

	/**
	 * Method Description: Method to delete a task from a checklist section
	 * 
	 * @param idRmvlCheckList,
	 *            idRmvlCheckSecDtl and idRmvlCheckTaskDtl
	 * @return
	 */
	@RequestMapping(value = "/deletetask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes deleteTask(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdRmvlChcklstTaskLookupList())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.deleteRmvlChcklstTaskDtl(checklistReq.getIdRmvlChcklstTaskLookupList());
	}

	/**
	 * Method Description: Method to save a checklist.
	 * 
	 * @param checklistReq
	 * @return
	 */
	@RequestMapping(value = "/savechecklist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes saveRmvlCheckList(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(checklistReq.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.saveRmvlCheckList(checklistReq.getRmvlChcklstLookupDto(),
				checklistReq.getReqFuncCd());
	}

	/**
	 * Method Description: Method to update a checklist.
	 * 
	 * @param checklistReq
	 * @return
	 */
	@RequestMapping(value = "/updatechecklist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes updateRmvlCheckList(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getRmvlChcklstLookupDto().getIdRmvlChcklstLookup())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.updateRmvlCheckList(checklistReq.getRmvlChcklstLookupDto(), checklistReq.getIdPerson(), checklistReq.getIdRmvlChcklstLink());
	}

	/**
	 * Method Description: Fetches the Removal Checklist Link Records for the
	 * passed Person ID and Stage ID. If no records are fetched then it means
	 * that the checklist is being created for the first time.
	 * 
	 * @param checklistReq
	 *            - Input request consisting of the person ID and the stage ID
	 * @return - RmvlCheckListRes
	 */
	@RequestMapping(value = "/getlinks", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes getRmvlChcklstLink(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdPerson())
				|| TypeConvUtil.isNullOrEmpty(checklistReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.getRmvlChcklstLink(checklistReq.getIdPerson(), checklistReq.getIdStage(), checklistReq.getIdRmvlEvent());
	}

	/**
	 * Method Description: Method to save a checklist.
	 * 
	 * @param checklistReq
	 * @return
	 */
	@RequestMapping(value = "/indrecordexist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  boolean indRecordExist(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdRmvlEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.indRecordExist(checklistReq.getIdRmvlEvent());
	}

	/**
	 * Method Description: Method to save a checklist.
	 * 
	 * @param checklistReq
	 * @return
	 */
	@RequestMapping(value = "/getpersonlist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CnsrvtrshpRemovalRes getPersonList(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdRmvlEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.getPersonList(checklistReq.getIdRmvlEvent());
	}

	/**
	 * 
	 * @param RmvlChcklstRspnDto
	 * @return
	 */
	@RequestMapping(value = "/savechcklstrspn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes saveRmvlChcklstRspn(@RequestBody RmvlCheckListReq checklistReq) {

		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getRmvlChcklstRspnDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.saveRmvlChcklstRspn(checklistReq.getRmvlChcklstRspnDto());

	}

	/**
	 * 
	 * @param RmvlChcklstRspnDto
	 * @return
	 */
	@RequestMapping(value = "/updatechcklstrspn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes updateRmvlChcklstRspn(@RequestBody RmvlCheckListReq checklistReq) {

		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getRmvlChcklstRspnDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.updateRmvlChcklstRspn(checklistReq.getRmvlChcklstRspnDto());
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 */

	@RequestMapping(value = "/getrmvlchcklstrspn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes getRmvlChcklstRspn(@RequestBody RmvlCheckListReq checklistReq) {
		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.getRmvlChcklstRspn(checklistReq.getIdPerson());

	}

	/**
	 * 
	 * @param RmvlChcklstLinkDto
	 * @return
	 */
	@RequestMapping(value = "/savermvlchcklstlink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes saveRmvlChcklstLink(@RequestBody RmvlCheckListReq checklistReq) {

		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getRmvlChcklstLinkDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.saveRmvlChcklstLink(checklistReq.getRmvlChcklstLinkDto());
	}

	/**
	 * 
	 * @param RmvlChcklstLinkDto
	 * @return
	 */
	@RequestMapping(value = "/updatermvlchcklstlink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RmvlCheckListRes updateRmvlChcklstLink(@RequestBody RmvlCheckListReq checklistReq) {

		log.info("TransactionId :" + checklistReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(checklistReq.getRmvlChcklstLinkDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return removalChecklistService.updateRmvlChcklstLink(checklistReq.getRmvlChcklstLinkDto());
	}

}
