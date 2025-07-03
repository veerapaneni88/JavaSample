/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 15, 2018- 2:36:25 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.service.RunawayMissingChildService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 2:36:25 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@Api(tags = { "missingchild" })
@RequestMapping("/runawayMissingChild")
public class RunAwayMissingChildController {

	@Autowired
	MessageSource messageSource;
	@Autowired
	RunawayMissingChildService runawayMsngService;
	private static final Logger log = Logger.getLogger(RunAwayMissingChildController.class);

	/**
	 * 
	 * Method Name: fetchRunawayMissingList Method Description: This method call
	 * the serviceimpl to fetch the list of Missing child detail and child
	 * recovery detail.
	 * 
	 * @param commonHelperReq
	 * @return RunawayChildMissingRes
	 */
	@ApiOperation(value = "Get missing child list", tags = { "missingchild" })
	@RequestMapping(value = "/fetchRunawayMissingList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RunawayChildMissingRes fetchRunawayMissingList(@RequestBody CommonHelperReq commonHelperReq) {
		RunawayChildMissingRes runawayMissingRes = new RunawayChildMissingRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		runawayMissingRes = runawayMsngService.fetchRunawayMissingList(commonHelperReq);
		return runawayMissingRes;
	}

	/**
	 * 
	 * Method Name: fetchRunawayMissingList Method Description: This method call
	 * the serviceimpl to fetch the Missing child detail
	 * 
	 * @param msngChldReq
	 * @return MissingChildDetailRes
	 */
	@ApiOperation(value = "Get missing child detail", tags = { "missingchild" })
	@RequestMapping(value = "/fetchMissingChildDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MissingChildDetailRes fetchMissingChildDetail(
			@RequestBody MissingChildRetrieveReq msngChldReq) {
		MissingChildDetailRes msngChildRetrieveRes = new MissingChildDetailRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(msngChldReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(msngChldReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		msngChildRetrieveRes = runawayMsngService.fetchMissingChildDetail(msngChldReq);
		return msngChildRetrieveRes;
	}

	@ApiOperation(value = "Get child recovery detail", tags = { "missingchild" })
	@RequestMapping(value = "/fetchChildRecoveryDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildRecoveryDetailRes fetchChildRecoveryDetail(
			@RequestBody ChildRecoveryRetreiveReq chldRcvryReq) {
		ChildRecoveryDetailRes childRcvryRetrieveRes = new ChildRecoveryDetailRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(chldRcvryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(chldRcvryReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		childRcvryRetrieveRes = runawayMsngService.fetchChildRecoveryDetail(chldRcvryReq);
		return childRcvryRetrieveRes;
	}

	@RequestMapping(value = "/saveChildRecoveryDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildRecoveryDetailSaveRes saveChildRecoveryDetail(
			@RequestBody ChildRecoverySaveReq chldRcvySaveReq) {
		ChildRecoveryDetailSaveRes childRcvrysaveRes = new ChildRecoveryDetailSaveRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(chldRcvySaveReq.getEventDto().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(chldRcvySaveReq.getEventDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		childRcvrysaveRes = runawayMsngService.saveChildRecoveryDetail(chldRcvySaveReq);
		return childRcvrysaveRes;
	}

	// artf198170 : enable delete function for fixer
	@RequestMapping(value = "/deleteChildRecoveryDetail", headers = {"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildRecoveryDetailSaveRes deleteChildRecoveryDetail(@RequestBody ChildRecoverySaveReq chldRcvySaveReq) {
		ChildRecoveryDetailSaveRes childRcvrysaveRes = new ChildRecoveryDetailSaveRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(chldRcvySaveReq.getEventDto().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(chldRcvySaveReq.getEventDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		childRcvrysaveRes = runawayMsngService.deleteChildRecoveryDetail(chldRcvySaveReq);
		return childRcvrysaveRes;
	}

	@RequestMapping(value = "/saveChildMissingDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MissingChildDetailSaveRes saveChildMissingDetail(
			@RequestBody MissingChildSaveReq msngChildSaveReq) {
		MissingChildDetailSaveRes msngChldSaveRes = new MissingChildDetailSaveRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(msngChildSaveReq.getEventDto().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(msngChildSaveReq.getEventDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		msngChldSaveRes = runawayMsngService.saveMissingChildDetail(msngChildSaveReq);
		return msngChldSaveRes;
	}

	// artf198170 : enable delete function for fixer
	@RequestMapping(value = "/deleteChildMissingDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MissingChildDetailSaveRes deleteChildMissingDetail(
			@RequestBody MissingChildSaveReq msngChildSaveReq) {
		MissingChildDetailSaveRes msngChldSaveRes = new MissingChildDetailSaveRes();
		log.debug("Entering method fetchRunawayMissingList in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(msngChildSaveReq.getEventDto().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.Tablename.mandatory", null, Locale.US));
		}
		// check for mandatory idstage
		if (TypeConvUtil.isNullOrEmpty(msngChildSaveReq.getEventDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		msngChldSaveRes = runawayMsngService.deleteMissingChildDetail(msngChildSaveReq);
		return msngChldSaveRes;
	}

	@RequestMapping(value = "/fetchMissingChildIds", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public MissingChildIdsRes fetchMissingChildIds(
			@RequestBody MissingChildIdsReq missingChildIdsReq) {
		MissingChildIdsRes missingChildIdsRes = new MissingChildIdsRes();
		log.debug("Entering method fetchMissingChildIds in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(missingChildIdsReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdEvent.mandatory", null, Locale.US));
		}
		missingChildIdsRes = runawayMsngService.fetchMissingChildIds(missingChildIdsReq.getIdEvent());
		return missingChildIdsRes;
	}

	@RequestMapping(value = "/fetchChildRecoveryIds", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ChildRecoveryIdsRes fetchChildRecoveryIds(
			@RequestBody ChildRecoveryIdsReq childRecoveryIdsReq) {
		ChildRecoveryIdsRes childRecoveryIdsRes = new ChildRecoveryIdsRes();
		log.debug("Entering method fetchChildRecoveryIds in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(childRecoveryIdsReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdEvent.mandatory", null, Locale.US));
		}
		childRecoveryIdsRes = runawayMsngService.fetchChildRecoveryIds(childRecoveryIdsReq.getIdEvent());
		return childRecoveryIdsRes;
	}

	@RequestMapping(value = "/fetchChildRecoveryLastUpdate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ChildRecoveryLastUpdIdsRes fetchChildRecoveryLastUpdate(
			@RequestBody ChildRecoveryIdsReq childRecoveryIdsReq) {
		ChildRecoveryLastUpdIdsRes childRecoveryLastUpdIdsRes = new ChildRecoveryLastUpdIdsRes();
		log.debug("Entering method fetchChildRecoveryLastUpdate in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(childRecoveryIdsReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdEvent.mandatory", null, Locale.US));
		}
		childRecoveryLastUpdIdsRes = runawayMsngService.fetchChildRecoveryLastUpdate(childRecoveryIdsReq.getIdEvent());
		return childRecoveryLastUpdIdsRes;
	}

	@RequestMapping(value = "/fetchMissingChild", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public MissingChildRes fetchMissingChild(
			@RequestBody MissingChildReq missingChildReq) {
		MissingChildRes missingChildRes = new MissingChildRes();
		log.debug("Entering method fetchMissingChild in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(missingChildReq.getIdChldMsngDtl())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdChldMsngDtl.mandatory", null, Locale.US));
		}
		missingChildRes = runawayMsngService.fetchMissingChild(missingChildReq.getIdChldMsngDtl());
		return missingChildRes;
	}

	@RequestMapping(value = "/fetchDetailForValidation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RunawayMsngRcvryRes fetchDetailForValidation(
			@RequestBody RunawayMsngRcvryReq runawayMsngRcvryReq) {
		RunawayMsngRcvryRes runawayMsngRcvryRes = new RunawayMsngRcvryRes();
		log.debug("Entering method fetchDetailForValidation in RunAwayMissingChildController");
		// check for mandatory idCase
		if (TypeConvUtil.isNullOrEmpty(runawayMsngRcvryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(runawayMsngRcvryReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdPerson.mandatory", null, Locale.US));
		}
		runawayMsngRcvryRes = runawayMsngService.fetchDetailForValidation(runawayMsngRcvryReq.getIdPerson(), runawayMsngRcvryReq.getIdCase());
		return runawayMsngRcvryRes;
	}

	@RequestMapping(value = "/fetchDtRemoval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RunawayMsngDtRemovalRes fetchDtRemoval(
			@RequestBody RunawayMsngDtRemovalReq runawayMsngDtRemovalReq) {
		RunawayMsngDtRemovalRes runawayMsngDtRemovalRes = new RunawayMsngDtRemovalRes();
		log.debug("Entering method fetchDtRemoval in RunAwayMissingChildController");
		// check for mandatory idStage
		if (TypeConvUtil.isNullOrEmpty(runawayMsngDtRemovalReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RunAwayMissingChildController.IdStage.mandatory", null, Locale.US));
		}
		runawayMsngDtRemovalRes = runawayMsngService.fetchDtRemoval(runawayMsngDtRemovalReq.getIdStage());
		return runawayMsngDtRemovalRes;
	}
}
