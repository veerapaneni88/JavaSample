package us.tx.state.dfps.service.apsrora.serviceimpl;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apsrora.dao.ApsRoraDao;
import us.tx.state.dfps.service.apsrora.dto.APSRoraAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.APSRoraFollowupAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraResponseDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraServiceDto;
import us.tx.state.dfps.service.apsrora.service.ApsRoraService;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsRoraReportPrefillData;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Risk of recidivism Assessment -- apsrora.
 * Dec 29, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Service
public class ApsRoraServiceImpl implements ApsRoraService {

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    ApsRoraReportPrefillData apsroraReportPrefillData;

    @Autowired
    ApsRoraDao apsRoraDao;

    @Autowired
    ApsServicePlanDao apsServicePlanDao;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsRoraFormInformation(ApsCommonReq apsRoraReportReq) {

        ApsRoraServiceDto apsRoraServiceDto = new ApsRoraServiceDto();

        // getGenericCaseInfo (DAm Name : CallCSEC02D) Method
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsRoraReportReq.getIdStage());
        apsRoraServiceDto.setGenericCaseInfoDto(genericCaseInfoDto);

        ApsRoraDto apsRoraDto = getApsRoraFullDetails(apsRoraReportReq);

        apsRoraServiceDto.setApsRoraDto(apsRoraDto);
        return apsroraReportPrefillData.returnPrefillData(apsRoraServiceDto);
    }


    @Override
    public ApsRoraDto getApsRoraFullDetails(ApsCommonReq apsRoraReportReq) {
        ApsRoraDto apsRoraDto = apsRoraDao.getRoraReportData(apsRoraReportReq.getIdEvent());
        if (!Objects.isNull(apsRoraDto) && apsRoraDto.getApsRoraId() != 0L) {
            apsRoraDto.setProblemCount(apsServicePlanDao.getApsSPProblemCount(apsRoraReportReq.getIdStage()));
            List<ApsRoraResponseDto> apsRoraResponseDtoList = apsRoraDao.getRoraResponseReportData(apsRoraDto.getApsRoraId());
            apsRoraResponseDtoList.forEach(apsRoraResponseDto -> {
                List<APSRoraAnswerDto> apsRoraAnswerDtoList = apsRoraDao.getRoraAnswerReportData(apsRoraResponseDto.getQuestionLookupId());
                apsRoraAnswerDtoList.forEach(apsRoraAnswerDto -> {
                    if (ApsRoraDto.questionsWithFollowupSet.contains((String.valueOf(apsRoraResponseDto.getQuestionLookupId())))) {
                        List<APSRoraFollowupAnswerDto>  apsRoraFollowupAnswerDtoList = apsRoraDao.getRoraFollowupAnswerReportData(apsRoraResponseDto.getApsRoraResponseId(), apsRoraAnswerDto.getRoraAnswerId());
                        apsRoraAnswerDto.setFollowupAnswers(apsRoraFollowupAnswerDtoList);
                    }
                });
                apsRoraResponseDto.setAnswers(apsRoraAnswerDtoList);
            });
            apsRoraDto.setResponses(apsRoraResponseDtoList);
        }
        return apsRoraDto;
    }
}
