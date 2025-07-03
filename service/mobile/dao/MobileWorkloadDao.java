package us.tx.state.dfps.service.mobile.dao;

import us.tx.state.dfps.service.workload.dto.SyncErrorDto;

import java.util.List;

public interface MobileWorkloadDao {

    void saveConfirm(Long idPerson, List<String> idStages);

    List<SyncErrorDto> getSyncErrorSql(Long idPerson);

    void deleteSyncError(List<String> idSyncErrors);

    void deleteSyncErrorsByType(Long idUser, String cdMsgType);

    void deleteSyncErrorsForUser(Long userID);

    void updateWorkloadErrorsForUser(Long userID);

    void updateVersionForUser(Long userID, String nbrVersion, String latestVersion);
}
