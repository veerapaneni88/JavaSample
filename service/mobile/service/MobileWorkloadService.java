package us.tx.state.dfps.service.mobile.service;

import us.tx.state.dfps.service.common.response.SyncErrorRes;

import java.util.List;

public interface MobileWorkloadService {

    void saveConfirm(Long idPerson, List<String> idStages);

    SyncErrorRes getSyncErrors(Long idPerson);

    void deleteSyncErrors(List<String> items);

    void deleteSyncErrorsByType(Long idUser, String cdMsgtype);

    void authenticateMpsUser(Long userID, String nbrVersion, String latestVersion);
}
