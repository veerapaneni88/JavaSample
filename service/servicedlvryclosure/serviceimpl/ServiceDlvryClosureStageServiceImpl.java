package us.tx.state.dfps.service.servicedlvryclosure.serviceimpl;

import us.tx.state.dfps.service.admin.dao.ServiceDlvryClosureStageDao;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageInDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageOutDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ServiceDlvryClosurePrefillData;
import us.tx.state.dfps.service.servicedelivery.dto.ClosureFormDto;
import us.tx.state.dfps.service.servicedlvryclosure.service.ServiceDlvryClosureStageService;
import java.util.List;

import org.apache.tiles.request.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for ServiceDlvryClosureStageServiceImpl. June 30, 2022- 8:50:18 PM ©
 * 2022 Texas Department of Family and Protective Services
 */
@Service
public class ServiceDlvryClosureStageServiceImpl implements ServiceDlvryClosureStageService {

    @Autowired
    ServiceDlvryClosureStageDao  serviceDlvryClosureStageDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    ServiceDlvryClosurePrefillData serviceDlvryClosurePrefillData;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public List<ServiceDlvryClosureStageOutDto> retrvDecisionDate(ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto) {
        return serviceDlvryClosureStageDao.retrvDecisionDate(serviceDlvryClosureStageInDto);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getClosureFormInformation(CommonApplicationReq request) {

        ClosureFormDto closureFormDto = new ClosureFormDto();
        ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto = new ServiceDlvryClosureStageInDto();
        serviceDlvryClosureStageInDto.setIdStage(request.getIdStage());
        List<ServiceDlvryClosureStageOutDto> serviceDlvryClosureStageOutDtoList = retrvDecisionDate(serviceDlvryClosureStageInDto);

        if(!CollectionUtils.isEmpty(serviceDlvryClosureStageOutDtoList)){
            ServiceDlvryClosureStageOutDto dlvryClosureStageOutDto = serviceDlvryClosureStageOutDtoList.get(0);
            closureFormDto.setCdClientAdvised(CodesConstant.A.equalsIgnoreCase(dlvryClosureStageOutDto.getCdClientAdvised()) ? CodesConstant.YES : CodesConstant.NO);
            closureFormDto.setDtClientAdvised(dlvryClosureStageOutDto.getDtClientAdvised());
            closureFormDto.setDtSvcDelvDecision(dlvryClosureStageOutDto.getDtDtSvcDelvDecision());
            closureFormDto.setIndECS(CodesConstant.Y.equalsIgnoreCase(dlvryClosureStageOutDto.getIndECS())?CodesConstant.YES : CodesConstant.NO);
            closureFormDto.setIndECSVer(CodesConstant.Y.equalsIgnoreCase(dlvryClosureStageOutDto.getIndECSVer())?CodesConstant.YES : CodesConstant.NO);
        }
        StageValueBeanDto stageValueBeanDto = stageDao.retrieveStageInfoList(request.getIdStage());
        if(stageValueBeanDto !=null){
            closureFormDto.setCaseName(stageValueBeanDto.getNmStage());
            closureFormDto.setCaseNumber(stageValueBeanDto.getIdCase());
            closureFormDto.setStageClosureCmnts(stageValueBeanDto.getStageClosureCmnts());
            closureFormDto.setReasonSelected(stageValueBeanDto.getCdStageReasonClosed());
            closureFormDto.setCdStageProgram(stageValueBeanDto.getCdStageProgram());
            closureFormDto.setStageCode(stageValueBeanDto.getCdStage());
            closureFormDto.setStageType(stageValueBeanDto.getCdStageType());
        }
        return serviceDlvryClosurePrefillData.returnPrefillData(closureFormDto);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public List<ServiceDlvryClosureStageOutDto> retrvDecisionDateAps(ServiceDlvryClosureStageInDto serviceDlvryClosureStageInDto) {
        return serviceDlvryClosureStageDao.retrvDecisionDateAps(serviceDlvryClosureStageInDto);
    }
}
