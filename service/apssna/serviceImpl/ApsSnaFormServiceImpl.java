package us.tx.state.dfps.service.apssna.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ApsSna;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.apssna.dao.ApsSnaDao;
import us.tx.state.dfps.service.apssna.dto.ApsSnaAnswerDto;
import us.tx.state.dfps.service.apssna.dto.ApsSnaFormResponseDTO;
import us.tx.state.dfps.service.apssna.dto.ApsSnaFormServiceDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import us.tx.state.dfps.service.apssna.service.ApsSnaFormService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsSnaFormServicePrefillData;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsSnaServiceImpl for APSSNA Form Service.
 * July 14, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Service
@Transactional
public class ApsSnaFormServiceImpl implements ApsSnaFormService {

    @Autowired
    ApsSnaDao apsSnaDao;

    @Autowired
    ApsSnaFormServicePrefillData apsSnaFormServicePrefillData;

    @Autowired
    SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    PersonUtil personUtil;

    /**
     * method to PrefillDataService object for ApsSnaForm Service
     * @param apsCommonReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsSnaDetails(ApsCommonReq apsCommonReq) {
        ApsSnaFormServiceDto apsSnaFormServiceDto = new ApsSnaFormServiceDto();

        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsCommonReq.getIdStage());
        apsSnaFormServiceDto.setGenericCaseInfoDto(genericCaseInfoDto);

        if(!TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdEvent())) {
            //Populate primary caretaker
            List<Long> getEventPersonLink = sdmSafetyAssessmentDao.getEventPersonLink(apsCommonReq.getIdEvent());
            apsSnaFormServiceDto.setPrimaryCaretakerNA(!ObjectUtils.isEmpty(getEventPersonLink));
            if (apsSnaFormServiceDto.isPrimaryCaretakerNA()) {
                apsSnaFormServiceDto.setPrimaryCaretakerName(personUtil.getPersonFullName(apsCommonReq.getIdEvent()));
            }
            //Populate Sna
            ApsSna apsSna = apsSnaDao.getApsSna(apsCommonReq.getIdEvent());
            apsSnaFormServiceDto.setApsSna(apsSna);

            // Populate SNA Responses
            if (!TypeConvUtil.isNullOrEmpty(apsSna)) {
                List<ApsSnaFormResponseDTO> apsSnaResponseList = apsSnaDao.getApsSnaResponseByIdEvent(apsSna.getIdApsSna());
                apsSnaFormServiceDto.setApsSnaResponsesList(apsSnaResponseList);

                //Populate SNA Answers list
                ArrayList<ApsSnaAnswerDto> apsSnaAnswerLookupList = new ArrayList<>();
                for (ApsSnaFormResponseDTO apsSnaResponse : apsSnaResponseList) {
                    List<ApsSnaAnswerDto> apsSnaAnswerLookupByDomiainIdList = apsSnaDao.getApsSnaAnswers(apsSnaResponse.getApsSNADomainLkpId());
                    apsSnaAnswerLookupByDomiainIdList.forEach(aps -> apsSnaAnswerLookupList.add(aps));
                }
                apsSnaFormServiceDto.setApsSnaAnswerLookupList(apsSnaAnswerLookupList);
            }
            apsSnaFormServiceDto.setStrengthsAssessedA(apsSnaDao.getSnaResponses(apsCommonReq.getIdEvent(), "a"));
            apsSnaFormServiceDto.setStrengthsAssessedB(apsSnaDao.getSnaResponses(apsCommonReq.getIdEvent(), "b"));
            apsSnaFormServiceDto.setStrengthsAssessedC(apsSnaDao.getSnaResponses(apsCommonReq.getIdEvent(), "c"));
        }
        return apsSnaFormServicePrefillData.returnPrefillData(apsSnaFormServiceDto);
    }

    /**
     *  Method getApsSnaDetails will populate the STRENGTHS AND NEEDS ASSESSMENTS information based on case
     * @param idCase
     * @return
     */
    @Override
    public List<ApsStrengthsAndNeedsAssessmentDto> getApsSnaDetailsforCaseReview(Long idCase) {
        List<ApsStrengthsAndNeedsAssessmentDto> apsSnaDetailsList = new ArrayList<>();
        List<ApsStrengthsAndNeedsAssessmentDto> apsSnaEventsList = apsSnaDao.getSnaEvents(idCase);
        apsSnaEventsList.forEach(apsStrengthsAndNeedsAssessmentDto -> {
            apsStrengthsAndNeedsAssessmentDto.setStrengthsAssessedA(apsSnaDao.getSnaResponses(apsStrengthsAndNeedsAssessmentDto.getEventId(),"a"));
            apsStrengthsAndNeedsAssessmentDto.setNeedsAssessedB(apsSnaDao.getSnaResponses(apsStrengthsAndNeedsAssessmentDto.getEventId(),"b"));
            apsStrengthsAndNeedsAssessmentDto.setNeedsAssessedC(apsSnaDao.getSnaResponses(apsStrengthsAndNeedsAssessmentDto.getEventId(),"c"));
            apsSnaDetailsList.add(apsStrengthsAndNeedsAssessmentDto);
        });
        return apsSnaDetailsList;
    }
}
