package us.tx.state.dfps.service.person.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.HomeMembTrainRtrvReq;
import us.tx.state.dfps.service.common.request.HomeMembTrainSaveReq;
import us.tx.state.dfps.service.common.response.HomeMembTrainRtrvRes;
import us.tx.state.dfps.service.common.response.HomeMembTrainSaveRes;
import us.tx.state.dfps.service.person.service.FAIndivTrainingService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FAIndivTrainingController May 8, 2018- 5:11:21 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/faHomeMemb")
public class FAIndivTrainingController {

    @Autowired
    FAIndivTrainingService faIndivTrainingService;

    private static final Logger log = Logger.getLogger("ServiceBusiness-AdminControllerLog");

    /**
     * Method Name: faHomeMembRetrive Method Description: This service will call
     * a DAM to retrieve all FA Home Member Training rows where IdPerson equals
     * the IdPerson passed into the service. CFAD32S
     *
     * @param objHomeMembTrainRtrviDto
     * @return HomeMembTrainRtrvoDto
     */
    @RequestMapping(value = "/faHomeMembRetrive", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public HomeMembTrainRtrvRes faHomeMembRetrive(@RequestBody HomeMembTrainRtrvReq objHomeMembTrainRtrviDto) {
        log.debug("Entering method faHomeMembRetrive in FAIndivTrainingController");

        HomeMembTrainRtrvRes objHomeMembTrainRtrvoDto =
                faIndivTrainingService.getFAHomeMemberTrainingList(objHomeMembTrainRtrviDto);
        log.debug("Exiting method faHomeMembRetrive in FAIndivTrainingController");
        return objHomeMembTrainRtrvoDto;
    }

    /**
     * Method Name: HomeMembTrainSaveoDto Method Description:This service will
     * first loop through all rows sent to the service looking for a
     * "Pre-Service" type row. If one is found, a DAM is called to determine if
     * any preservice training sessions have been previously saved to the
     * database. If not, then PostEvent is called and a ToDo is created which
     * will be associated with the newly created event. Following this
     * processing, any rows which were Added/Updated/Deleted on the window will
     * be Added/Updated/Deleted from the database. FA_INDIV_TRAINING CFAD33S
     *
     * @param objHomeMembTrainSaveiDto
     * @return HomeMembTrainSaveoDto
     */
    @RequestMapping(value = "/faHomeMembSave", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public HomeMembTrainSaveRes faHomeMembSave(@RequestBody HomeMembTrainSaveReq objHomeMembTrainSaveiDto) {
        log.debug("Entering method faHomeMembSave in FAIndivTrainingController");
        HomeMembTrainSaveRes objHomeMembTrainSaveoDto =
                faIndivTrainingService.homeMembTrainSave(objHomeMembTrainSaveiDto);
        log.debug("Exiting method faHomeMembSave in FAIndivTrainingController");
        return objHomeMembTrainSaveoDto;
    }

    @RequestMapping(value = "/faHomeMembTrainingDelete", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public void faHomeMembTrainingDelete(@RequestBody Long trainingId) {
        log.debug("Entering method faHomeMembTrainingDelete in FAIndivTrainingController");
        faIndivTrainingService.homeMembTrainDelete(trainingId);
        log.debug("Exiting method faHomeMembDelete in FAIndivTrainingController");
    }
}
