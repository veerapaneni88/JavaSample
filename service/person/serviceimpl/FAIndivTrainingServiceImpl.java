package us.tx.state.dfps.service.person.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.HomeMembTrainRtrvReq;
import us.tx.state.dfps.service.common.request.HomeMembTrainSaveReq;
import us.tx.state.dfps.service.common.response.HomeMembTrainRtrvRes;
import us.tx.state.dfps.service.common.response.HomeMembTrainSaveRes;
import us.tx.state.dfps.service.person.dao.FAHomeMemberDao;
import us.tx.state.dfps.service.person.dto.FaIndivTrainingDto;
import us.tx.state.dfps.service.person.service.FAIndivTrainingService;

@Service
@Transactional
public class FAIndivTrainingServiceImpl implements FAIndivTrainingService {

    @Autowired
    FAHomeMemberDao faHomeMemberDao;


    @Override
    public HomeMembTrainRtrvRes homeMembTrainRtrv(HomeMembTrainRtrvReq homeMembTrainRtrviDto) {
        return null;
    }

    @Override
    public HomeMembTrainSaveRes homeMembTrainSave(HomeMembTrainSaveReq homeMembTrainSaveiDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {
            Exception.class})
    public void homeMembTrainDelete(Long trainingId) {
        faHomeMemberDao.homeMembTrainDelete(trainingId);
    }

    /**
     * @param homeMembTrainRtrvReq
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public HomeMembTrainRtrvRes getFAHomeMemberTrainingList(HomeMembTrainRtrvReq homeMembTrainRtrvReq) {
        HomeMembTrainRtrvRes homeMembTrainRtrvRes = new HomeMembTrainRtrvRes();

        List<FaIndivTrainingDto> homeMemberTrainingList =
                faHomeMemberDao.getFAHomeMemberTrainingList(homeMembTrainRtrvReq);

        homeMembTrainRtrvRes.setHomeMemTrainRetrvRowDtos(homeMemberTrainingList);
        return homeMembTrainRtrvRes;
    }

}
