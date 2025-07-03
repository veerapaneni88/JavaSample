package us.tx.state.dfps.service.apscontractLogNarrative.service;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apscontactlognarrative.dao.ApsContactLogNarrativeDao;
import us.tx.state.dfps.service.apscontactlognarrative.dto.APSContactLogNarrativeDto;
import us.tx.state.dfps.service.apscontactlognarrative.dto.APSSafetyAssessmentContactDto;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.request.ApsContactLogNarrativeReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.APSContactLogNarrativePrefillData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for APSCLN-Log of Contact Narratives December 29 2021, 2021- 2:36:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ApsContactLogNarrativeServiceImpl implements ApsContactLogNarrativeService {

    @Autowired
    APSContactLogNarrativePrefillData apsContactLogNarrativePrefillData;

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    ApsContactLogNarrativeDao apsContactLogNarrativeDao;

    @Autowired
    EventPersonLinkDao eventPersonLinkDao;

    public static final int MSG_SYS_NO_CNCT_NARR = 8388;

    /**
     * @param apsContactLogNarrativeReq narrative req data
     * @return return to prefill data
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getapscontactlognarrative(ApsContactLogNarrativeReq apsContactLogNarrativeReq) {

        APSContactLogNarrativeDto apsContactLogNarrativeDto = new APSContactLogNarrativeDto();

        apsContactLogNarrativeDto.setDtSampleFrom(apsContactLogNarrativeReq.getDtSampleFrom());
        apsContactLogNarrativeDto.setDtSampleTo(apsContactLogNarrativeReq.getDtSampleTo());

        // CSEC02D
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsContactLogNarrativeReq.getIdStage());
        apsContactLogNarrativeDto.setGenericCaseInfoDto(genericCaseInfoDto);

        List<Long> safetyEventList = apsContactLogNarrativeDao.getsafetyAssmtEventsByStage(apsContactLogNarrativeReq.getIdStage());

        // Get contacts created through contact faceplate (exclude contacts created through safety assessment)
        List<Long> contactEventIdList = apsContactLogNarrativeDao.getContactEvents(apsContactLogNarrativeReq.getIdStage());

        for (Long safetyEvent : safetyEventList) {
            contactEventIdList.add(apsContactLogNarrativeDao.getFirstContactEventBySA(safetyEvent));
        }

        List<APSSafetyAssessmentContactDto> apsSafetyAssessmentContactDtos = new ArrayList<>();
        List<APSSafetyAssessmentContactDto> sortedApsSafetyAssessmentContactDtos = new ArrayList<>();
        for (Long idEvent : contactEventIdList) {
            APSSafetyAssessmentContactDto apsSafetyAssessmentContactDto = apsContactLogNarrativeDao.getDtContactoccured(idEvent);
            apsSafetyAssessmentContactDtos.add(apsSafetyAssessmentContactDto);
        }

        if (CollectionUtils.isNotEmpty(apsSafetyAssessmentContactDtos)) {
            sortedApsSafetyAssessmentContactDtos = apsSafetyAssessmentContactDtos.stream()
                    .sorted(Comparator.comparing(APSSafetyAssessmentContactDto::getDateContactOccurred)).collect(Collectors.toList());
        }

        List<Long> sortedContactEvents = new ArrayList<>();
        for (APSSafetyAssessmentContactDto db : sortedApsSafetyAssessmentContactDtos) {
            sortedContactEvents.add(db.getContactEventId());
        }

        if (!CollectionUtils.isNotEmpty(sortedContactEvents)) {
            throw new ServiceException(String.valueOf(MSG_SYS_NO_CNCT_NARR));
        }

        apsContactLogNarrativeDto.setSortedContactEventIds(sortedContactEvents);
        return apsContactLogNarrativePrefillData.returnPrefillData(apsContactLogNarrativeDto);
    }
}

