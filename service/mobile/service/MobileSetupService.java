package us.tx.state.dfps.service.mobile.service;

import us.tx.state.dfps.mobile.SyncStatisticsDto;
import us.tx.state.dfps.mobile.UserDetailsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;

import java.sql.Timestamp;
import java.util.Date;

public interface MobileSetupService {

    void completeSetup(CommonHelperReq commonHelperReq);

    Long getCodesTablesRowCount();

    Long getCodesTablesEndDatedRowCount();

    Timestamp getLastMessageUpdateTimestamp();

    void saveTokenBody(UserDetailsDto userDetailsDto);

    UserDetailsDto getTokenBody();

    Date getMPSSyncStats();

    void saveMpsSyncStatistics(SyncStatisticsDto syncStatisticsDto);
}
