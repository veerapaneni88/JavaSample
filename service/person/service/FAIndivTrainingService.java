package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.HomeMembTrainRtrvReq;
import us.tx.state.dfps.service.common.request.HomeMembTrainSaveReq;
import us.tx.state.dfps.service.common.response.HomeMembTrainRtrvRes;
import us.tx.state.dfps.service.common.response.HomeMembTrainSaveRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FAIndivTrainingService May 8, 2018- 5:10:45 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FAIndivTrainingService {
    /**
     * Method Name: homeMembTrainRtrv Method Description: homeMembTrainRtrv
     *
     * @param homeMembTrainRtrviDto
     * @return HomeMembTrainRtrvoDto
     */
    public HomeMembTrainRtrvRes homeMembTrainRtrv(HomeMembTrainRtrvReq homeMembTrainRtrviDto);

    /**
     * Method Name: homeMembTrainSave Method Description: homeMembTrainSave
     *
     * @param homeMembTrainSaveiDto
     * @return HomeMembTrainSaveoDto
     */
    public HomeMembTrainSaveRes homeMembTrainSave(HomeMembTrainSaveReq homeMembTrainSaveiDto);

    /**
     * homeMembTrainDelete
     *
     * @param trainingId
     * @return
     */
    public void homeMembTrainDelete(Long trainingId);

    /**
     * @param homeMembTrainRtrvReq
     * @return
     */
    public HomeMembTrainRtrvRes getFAHomeMemberTrainingList(HomeMembTrainRtrvReq homeMembTrainRtrvReq);

}
