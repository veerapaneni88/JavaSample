package us.tx.state.dfps.service.mobile.dao;

import us.tx.state.dfps.mobile.SyncStatisticsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;

import java.sql.Timestamp;
import java.util.Date;

public interface MobileSetupDao {
    void completeSetup(CommonHelperReq commonHelperReq);

    void saveTokenBody(String body);

    String getTokenBody();

    void populateSyncStats(SyncStatisticsDto syncStatsDto);

    Date getLastSynchTime();

    int getCodesTablesRowCount();

    int getCodesTablesEndDatedRowCount();

    Timestamp getLastMessageUpdateTimestamp();

}
