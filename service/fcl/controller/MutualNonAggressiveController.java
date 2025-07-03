package us.tx.state.dfps.service.fcl.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.SexualVictimHistoryReq;
import us.tx.state.dfps.service.common.response.SexualVictimHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fcl.service.MutualNonAggressiveService;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

import java.util.Locale;

@RestController
@RequestMapping("/mna")
/**
 * MutualNonAggressiveController is currently unused (4/12/2022) meaning there is no longer a way to create.
 * update or delete Mutual Non-Aggressive Incidents. We expect this functionality to be needed in the future, and so
 * we are retaining this code so we're ready when the requirements are put in place.
 */
public class MutualNonAggressiveController {

    @Autowired
    MutualNonAggressiveService mutualNonAggressiveService;
    @Autowired
    MessageSource messageSource;
    private static final Logger log = Logger.getLogger(MutualNonAggressiveController.class);

    @RequestMapping(value = "/getMutualNonAggressive", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public SexualVictimHistoryRes getMutualNonAggressive(@RequestBody SexualVictimHistoryReq request) {
        log.info("TransactionId :" + request.getTransactionId());
        if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        SexualVictimHistoryDto sexualVictimHistoryDto = mutualNonAggressiveService.getMutualNonAggressiveIncidents(request.getIdPerson());
        SexualVictimHistoryRes response = new SexualVictimHistoryRes();
        response.setSexualVictimHistoryDto(sexualVictimHistoryDto);
        return response;
    }

    @RequestMapping(value = "/updateMutualNonAggressive", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public SexualVictimHistoryRes updateMutualNonAggressive(@RequestBody SexualVictimHistoryReq request) {
        log.info("TransactionId :" + request.getTransactionId());
        mutualNonAggressiveService.saveOrUpdateMutualNonAggressiveIncidents(request.getSexualVictimHistoryDto());
        SexualVictimHistoryRes response = new SexualVictimHistoryRes();
        return response;
    }

    @RequestMapping(value = "/deleteMutualNonAggressive", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public SexualVictimHistoryRes deleteMutualNonAggressive(@RequestBody SexualVictimHistoryReq request) {
        log.info("TransactionId :" + request.getTransactionId());
        if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        mutualNonAggressiveService.deleteMutualNonAggressiveIncidents(request.getIncidentIds(), request.getIdPerson());
        SexualVictimHistoryRes response = new SexualVictimHistoryRes();
        return response;
    }
}
