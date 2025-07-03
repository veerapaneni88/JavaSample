package us.tx.state.dfps.service.safetycheck.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SafetyCheckReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.SafetyCheckRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.safetycheck.service.SafetyCheckService;

import java.util.Locale;

@RestController
@RequestMapping("/safetycheck")
public class SafetyCheckController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    SafetyCheckService safetyCheckService;

    private static final Logger LOG = Logger.getLogger(SafetyCheckController.class);

    /**
     * PPM 60692-artf178537-Start- Changes for Safety check List
     * Method Description: This method retrieves all the safety checks done for a resource
     *
     * @return SafetyCheckRes
     */
    @RequestMapping(value = "/displayList", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public SafetyCheckRes getSafetyCheckDetail(@RequestBody CommonHelperReq commonHelperReq) {
        LOG.debug("Entering method getSafetyCheckDetail in SafetyCheckController");
        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdResource())) {
            throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory",null,Locale.US));
        }
        SafetyCheckRes response = new SafetyCheckRes();
        response.setSafetyCheckDto(safetyCheckService.getSafetyCheckDetail(commonHelperReq.getIdResource()));
        return response;
    }

    /**
     * PPM 60692-artf179566- loadSafetyDetail
     * Method Description: This method is used to fetch safety check Details
     * @param idResource,idHMSafetyCheck
     * @return SafetyCheckRes
     */
    @GetMapping(value = "/details")
    public SafetyCheckRes loadSafetyDetail(@RequestParam(value = "idResource") Long idResource,@RequestParam(value = "idHMSafetyCheck",required = false) Long idHmSafetyCheck) {
        LOG.debug("Entering method getHmmRequestEventList in SafetyCheck service Controller");
        if (ObjectUtils.isEmpty(idResource) && ObjectUtils.isEmpty(idHmSafetyCheck)) {
            throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory",null,Locale.US));
        }
        SafetyCheckRes response = new SafetyCheckRes();
        response.setSafetyCheckDto(safetyCheckService.loadSafetyDetail(idResource,idHmSafetyCheck));
        return response;
    }
    /**
     * PPM 60692-artf179566- saveSafetyCheck
     * Method Description: This method is used to save safety check Details
     * @param safetyCheckReq
     * @return SafetyCheckRes
     */
    @RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public SafetyCheckRes saveSafetyCheck(@RequestBody SafetyCheckReq safetyCheckReq) {
        LOG.debug("Entering method saveSafetyCheck in SafetyCheck service Controller");
        if (ObjectUtils.isEmpty(safetyCheckReq.getSafetyCheckDto().getIdResource())) {
            throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory",null,Locale.US));
        }
        SafetyCheckRes response = new SafetyCheckRes();
        response.setSafetyCheckDto(safetyCheckService.saveSafetyCheck(safetyCheckReq.getSafetyCheckDto(),Long.valueOf(safetyCheckReq.getUserId())));
        return response;
    }
    /**
     * PPM 60692-artf179566- deleteSafetyCheck
     * Method Description: This method is used to delete safety check Details
     * @param safetyCheckReq
     * @return SafetyCheckRes
     */
    @RequestMapping(value = "/delete", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public SafetyCheckRes deleteSafetyCheck(@RequestBody SafetyCheckReq safetyCheckReq) {
        LOG.debug("Entering method deleteSafetyCheck in SafetyCheck service Controller");
        if (ObjectUtils.isEmpty(safetyCheckReq.getSafetyCheckDto().getIdHMSafetyCheck())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory",null,Locale.US));
        }
        SafetyCheckRes response = new SafetyCheckRes();
       safetyCheckService.deleteSafetyCheck(safetyCheckReq.getSafetyCheckDto());
        return response;
    }
    /**
     * PPM 60692-artf179566- deleteAttachment
     * Method Description: This method is used to delete Attachment
     * @param commonHelperReq
     * @return CommonHelperRes
     */
    @RequestMapping(value = "/deleteAttachment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public void deleteAttachment(@RequestBody CommonHelperReq commonHelperReq) {
        LOG.debug("Entering method deleteAttachment in SafetyCheck service Controller");
        if (ObjectUtils.isEmpty(commonHelperReq.getEntityID())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory",null,Locale.US));
        }
       safetyCheckService.deleteAttachment(commonHelperReq.getEntityID());
    }

    /**
     * Method Description: This method is used to fetch safety check if Doc Exists for narrative
     * @param idEvent
     * @return SafetyCheckRes
     */
    @GetMapping(value = "/docExist")
    public CommonHelperRes checkDocExist(@RequestParam(name = "idEvent") Long idEvent) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(safetyCheckService.isDocExist(idEvent));
        return commonHelperRes;
    }
}
