package us.tx.state.dfps.service.IndianChildStatus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.service.IndianChildStatus.service.IndianChildStatusService;
import us.tx.state.dfps.service.common.request.IndianChildStatusInfoReq;
import us.tx.state.dfps.service.common.response.IndianChildStatusInfoRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * IndianChildStatusRestController will have all operation which are mapped to
 * indianchildstatus module. April 1, 2012- 9:00:00 AM © 2022 Texas Department of
 * Family and Protective Services
 */

@RestController
@RequestMapping("/indianchildstatus")
public class IndianChildStatusController {

   @Autowired
   IndianChildStatusService indianChildStatusService;

    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/saveIndianChildStatusDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes saveIndianChildStatusDetails(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.saveIndianChildStatusDetails(indianChildStatusInfoReq);
    }

    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/getIndianChildStatusDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes getIndianChildStatusDetails(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.getIndianChildStatusDetails(indianChildStatusInfoReq);
    }


    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/getFederalTribeStateList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes getFederalTribeStateList(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.getFederalTribeStateList(indianChildStatusInfoReq);
    }


    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/saveIndianTribeNotification", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes saveIndianTribeNotification(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.saveIndianTribeNotification(indianChildStatusInfoReq);
    }


    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/getIcwaTribeNotifDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes getIcwaTribeNotifDetails(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.getIcwaTribeNotifDetails(indianChildStatusInfoReq);
    }

    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/getIcwaTribeNotifRecord", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes getIcwaTribeNotifRecord(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.getIcwaTribeNotifRecord(indianChildStatusInfoReq);
    }

    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @RequestMapping(value = "/deleteIcwaTribeNotif", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public IndianChildStatusInfoRes deleteIcwaTribeNotif(@RequestBody IndianChildStatusInfoReq indianChildStatusInfoReq) {

        return indianChildStatusService.deleteIcwaTribeNotif(indianChildStatusInfoReq);
    }

}
