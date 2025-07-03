package us.tx.state.dfps.service.apsinvsummary.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.apsinvsummary.dto.ApsInvSummaryServiceDto;
import us.tx.state.dfps.service.apsinvsummary.service.ApsInvSummaryService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsInvSummaryServicePrefillData;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Investigation Summary -- CFIV1200.
 * Dec 14, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsInvSummaryServiceImpl implements ApsInvSummaryService {

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    private StageDao stageDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    private PopulateFormDao populateFormDao;

    @Autowired
    private PopulateLetterDao populateLetterDao;

    @Autowired
    ApsInvstDetailDao apsInvstDetailDao;

    @Autowired
    ApsInvSummaryServicePrefillData apsInvSummaryServicePrefillData;

    @Autowired
    CommonApplicationDao commonApplicationDao;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getapsinvsummarydata(ApsCommonReq apsCommonReq) {

        ApsInvSummaryServiceDto apsInvSummaryServiceDto = new ApsInvSummaryServiceDto();

        List<ApsInvstDetail> apsInvstDetailList = null;

        // CSEC02D
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsCommonReq.getIdStage());
        apsInvSummaryServiceDto.setGenericCaseInfoDto(genericCaseInfoDto);

        // CLSC05D
        apsInvSummaryServiceDto.setAllegationWithVicList(populateFormDao.getAllegationById(apsCommonReq.getIdStage()));

        apsInvSummaryServiceDto.setStageDto(stageDao.getStageById(apsCommonReq.getIdStage()));

        //CINV44D
        apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(apsCommonReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(apsInvstDetailList)) {
            for (ApsInvstDetail apsInvstDetail : apsInvstDetailList) {
                if (apsInvstDetail.getIdCase().equals(apsInvSummaryServiceDto.getGenericCaseInfoDto().getIdCase())) {
                    apsInvSummaryServiceDto.setApsInvstDetail(apsInvstDetail);
                }
            }

        }
        /* Original worker details CSEC01D*/
        getWorkerInfo(apsCommonReq, apsInvSummaryServiceDto);
        /* retrieves principal and reporter info CallCLSC01D */
        List<CaseInfoDto> caseInfoDtoprincipal = populateLetterDao.getCaseInfoById(apsCommonReq.getIdStage(),
                ServiceConstants.PRINCIPAL);
        if (!TypeConvUtil.isNullOrEmpty(caseInfoDtoprincipal)) {
            apsInvSummaryServiceDto.setCaseInfoPrincipalList(caseInfoDtoprincipal.stream().sorted(Comparator.comparing(CaseInfoDto::getIdStagePersonLink)).collect(Collectors.toList()));
        }
        /* retrieves Collateral info CallCLSC01D */
        List<CaseInfoDto> caseInfoDtocollateral = populateLetterDao.getCaseInfoById(apsCommonReq.getIdStage(),
                ServiceConstants.COLLATERAL);
        if (!TypeConvUtil.isNullOrEmpty(caseInfoDtocollateral)) {
            apsInvSummaryServiceDto.setCaseInfoCollateralList(caseInfoDtocollateral.stream().sorted(Comparator.comparing(CaseInfoDto::getIndStagePersReporter)).collect(Collectors.toList()));
        }
        return apsInvSummaryServicePrefillData.returnPrefillData(apsInvSummaryServiceDto);
    }

    /**
     * method to get Worker data
     *
     * @param apsCommonReq
     * @param apsInvSummaryServiceDto
     */
    /* Original worker details CSEC01D*/
    private void getWorkerInfo(ApsCommonReq apsCommonReq, ApsInvSummaryServiceDto apsInvSummaryServiceDto) {
        if (null != apsCommonReq.getIdStage()) {
            StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(apsCommonReq.getIdStage(),
                    ServiceConstants.PRIMARY_WORKER);
            EmployeePersPhNameDto employeePersPhNameDto = null;
            if (!ObjectUtils.isEmpty(stagePersonDto.getIdTodoPersWorker())) {
                employeePersPhNameDto = employeeDao
                        .searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
                apsInvSummaryServiceDto.setEmployeePersPhNameDto(employeePersPhNameDto);
                NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(employeePersPhNameDto.getIdJobPersSupv());
                apsInvSummaryServiceDto.setSupNameDetailDto(nameDetailDto);
            }
        }
    }
}
