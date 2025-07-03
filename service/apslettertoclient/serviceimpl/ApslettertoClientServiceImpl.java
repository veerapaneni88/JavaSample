package us.tx.state.dfps.service.apslettertoclient.serviceimpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.apslettertoclient.dto.ApslettertoClientServiceDto;
import us.tx.state.dfps.service.apslettertoclient.service.ApslettertoClientService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApslettertoClientServicePrefillData;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Letter to Client when cannot locate-- CIV37o00.
 * Dec 02, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ApslettertoClientServiceImpl implements ApslettertoClientService {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    StageDao personDao;

    @Autowired
    private StageDao stageDao;

    @Autowired
    private PopulateLetterDao populateLetterDao;

    @Autowired
    private DisasterPlanDao disasterPlanDao;

    @Autowired
    ApslettertoClientServicePrefillData apslettertoClientServicePrefillData;

    private static final List<String> nonVictimPersonRoles = Arrays.asList("HP","PC", "SE", "NO", "DP", "AP", "SP", "PR" );

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsLettertoClientData(ApsCommonReq apsCommonReq) {

        ApslettertoClientServiceDto apslettertoClientServiceDto = new ApslettertoClientServiceDto();

        GenericCaseInfoDto genCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsCommonReq.getIdStage());
        apslettertoClientServiceDto.setGenCaseInfoDto(genCaseInfoDto);

        // Call CLSC03D
        CodesTablesDto codesTablesDto = populateLetterDao
                .getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);
        apslettertoClientServiceDto.setCodesTablesDto(codesTablesDto);

        // CallCLSC01D _ Victim Info
        getVictimInfo(apsCommonReq, apslettertoClientServiceDto);

        /* retrieve stage_person_link CallCCMN19D */
        getWorkerInfo(apsCommonReq, apslettertoClientServiceDto);

        return apslettertoClientServicePrefillData.returnPrefillData(apslettertoClientServiceDto);
    }

    /**
     * method to get Victim data
     *
     * @param apsCommonReq
     * @param apslettertoClientServiceDto
     */
    private void getVictimInfo(ApsCommonReq apsCommonReq, ApslettertoClientServiceDto apslettertoClientServiceDto) {
        List<CaseInfoDto> caseInfoDtoPrincipalList = populateLetterDao.getCaseInfoById(apsCommonReq.getIdStage(),
                ServiceConstants.PRINCIPAL);
        if (!CollectionUtils.isEmpty(caseInfoDtoPrincipalList)) {
            Optional<CaseInfoDto> caseInfoDto = caseInfoDtoPrincipalList.stream().
                    filter(p -> !ObjectUtils.isEmpty(p.getCdStagePersRole()) && !nonVictimPersonRoles.contains(p.getCdStagePersRole())
                            && !p.getCdStagePersRole().contains(ServiceConstants.UNKNOWN1) && !ObjectUtils.isEmpty(p.getIndNameInvalid()) && ServiceConstants.STRING_IND_N.equals(p.getIndNameInvalid())
                            && !ObjectUtils.isEmpty(p.getIndNamePrimary()) && ServiceConstants.STRING_IND_Y.equals(p.getIndNamePrimary()))
                    .findFirst();
            if (caseInfoDto.isPresent()) {
                apslettertoClientServiceDto.setCaseInfoDto(caseInfoDto.get());
            }
        }
    }

    /**
     * method to get Worker data
     *
     * @param apsCommonReq
     * @param apslettertoClientServiceDto
     */
    private void getWorkerInfo(ApsCommonReq apsCommonReq, ApslettertoClientServiceDto apslettertoClientServiceDto) {
        if (null != apsCommonReq.getIdStage()) {
            StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(apsCommonReq.getIdStage(),
                    ServiceConstants.PRIMARY_WORKER);
            /* Original worker details CSEC01D*/
            if (!ObjectUtils.isEmpty(stagePersonDto.getIdTodoPersWorker())) {
                EmployeePersPhNameDto employeePersPhNameDto = employeeDao
                        .searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
                apslettertoClientServiceDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }
    }
}
