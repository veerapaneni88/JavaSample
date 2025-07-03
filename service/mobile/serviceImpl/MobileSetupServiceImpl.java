package us.tx.state.dfps.service.mobile.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.mobile.SyncStatisticsDto;
import us.tx.state.dfps.mobile.UserDetailsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.JNDIUtil;
import us.tx.state.dfps.service.mobile.dao.MobileSetupDao;
import us.tx.state.dfps.service.mobile.service.MobileSetupService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

@Service
@Transactional
public class MobileSetupServiceImpl implements MobileSetupService {

    private static final Logger logger = Logger.getLogger(MobileSetupServiceImpl.class);
    private static final String MOBILINK_URL = "mobilink.url";
    private static final String MOBILINK_PORT = "mobilink.port";

    private static final String DECODE_EXCEPTION = "mobile.token.decode.exception";
    @Autowired
    MessageSource messageSource;
    @Autowired
    MobileSetupDao mobileSetupDao;


    @Override
    public void completeSetup(CommonHelperReq commonHelperReq) {
        commonHelperReq.setHostName(JNDIUtil.lookUpString(MOBILINK_URL));
        String port = StringUtils.isEmpty(JNDIUtil.lookUpString(MOBILINK_PORT)) ? "2439" : JNDIUtil.lookUpString(MOBILINK_PORT);
        commonHelperReq.setDataAcsInd(port);
        mobileSetupDao.completeSetup(commonHelperReq);

    }

    public Long getCodesTablesRowCount() {
        return new Long(mobileSetupDao.getCodesTablesRowCount());
    }

    public Long getCodesTablesEndDatedRowCount() {
        return new Long(mobileSetupDao.getCodesTablesEndDatedRowCount());
    }

    public Timestamp getLastMessageUpdateTimestamp() {
        return mobileSetupDao.getLastMessageUpdateTimestamp();
    }

    @Override
    public void saveTokenBody(UserDetailsDto userDetailsDto) {
        String userDetailsJson = null;
        try {
            userDetailsJson = new ObjectMapper().writeValueAsString(userDetailsDto);
        } catch (JsonProcessingException e) {
            throw new InvalidRequestException(messageSource.getMessage(DECODE_EXCEPTION, null, Locale.US));
        }
        mobileSetupDao.saveTokenBody(userDetailsJson);
    }

    @Override
    public UserDetailsDto getTokenBody() {
        UserDetailsDto userDetailsDto = null;
        try {
            userDetailsDto = new ObjectMapper().readValue(mobileSetupDao.getTokenBody(), UserDetailsDto.class);
        } catch (IOException e) {
            throw new InvalidRequestException(messageSource.getMessage(DECODE_EXCEPTION, null, Locale.US));
        }
        return userDetailsDto;
    }

    public Date getMPSSyncStats() {
        Date lastSyncDate = mobileSetupDao.getLastSynchTime();
        return lastSyncDate;
    }

    @Override
    public void saveMpsSyncStatistics(SyncStatisticsDto syncStatisticsDto) {
        mobileSetupDao.populateSyncStats(syncStatisticsDto);
    }
}
