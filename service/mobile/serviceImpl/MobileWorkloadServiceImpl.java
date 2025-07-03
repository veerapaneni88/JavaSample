package us.tx.state.dfps.service.mobile.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.common.response.SyncErrorRes;
import us.tx.state.dfps.service.mobile.dao.MobileWorkloadDao;
import us.tx.state.dfps.service.mobile.service.MobileWorkloadService;
import us.tx.state.dfps.service.workload.dto.SyncErrorDto;

import java.util.List;

@Service
@Transactional
public class MobileWorkloadServiceImpl implements MobileWorkloadService {
    @Autowired
    MobileWorkloadDao mobileWorkloadDao;

    @Override
    public void saveConfirm(Long idPerson, List<String> idStages) {
        mobileWorkloadDao.saveConfirm(idPerson, idStages);

    }

    @Override
    public SyncErrorRes getSyncErrors(Long idPerson) {
        List<SyncErrorDto> syncErrorList = mobileWorkloadDao.getSyncErrorSql(idPerson);
        SyncErrorRes response = new SyncErrorRes();
        response.setSyncErrorDtos(syncErrorList);
        return response;
    }

    @Override
    public void deleteSyncErrors(List<String> items) {
        mobileWorkloadDao.deleteSyncError(items);
    }

    @Override
    public void deleteSyncErrorsByType(Long idUser, String cdMsgtype) {
        mobileWorkloadDao.deleteSyncErrorsByType(idUser, cdMsgtype);
    }

    @Override
    public void authenticateMpsUser(Long userID, String nbrVersion, String latestVersion) {
        mobileWorkloadDao.deleteSyncErrorsForUser(userID);
        mobileWorkloadDao.updateWorkloadErrorsForUser(userID);
        mobileWorkloadDao.updateVersionForUser(userID, nbrVersion, latestVersion);
    }


}
