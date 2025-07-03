package us.tx.state.dfps.service.apsoutcomematrix.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.apsoutcomematrix.dto.ApsOutcomeMatrixServiceDto;
import us.tx.state.dfps.service.apsoutcomematrix.service.ApsOutcomeMatrixService;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsOutcomeMatrixServicePrefillData;
import us.tx.state.dfps.service.populateform.dao.PcspHistoryFormDao;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Outcome Matrix Form/Narrative - civ34o00.
 * Jan 28th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsOutcomeMatrixServiceImpl implements ApsOutcomeMatrixService {

    @Autowired
    ApsOutcomeMatrixServicePrefillData apsOutcomeMatrixServicePrefillData;

    @Autowired
    PcspHistoryFormDao pcspHistoryFormDao;

    @Autowired
    EventDao eventDao;

    /**
     * service implementation for getApsOutcomeMatrixData
     * @param apsCommonReq
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsOutcomeMatrixData(ApsCommonReq apsCommonReq) {

        ApsOutcomeMatrixServiceDto serviceDto = new ApsOutcomeMatrixServiceDto();



        //Call for CallCINVD5D
        if(!TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdCase())){
            //Call for CallCINVD5D
            serviceDto.setPcspCaseInfoDtoList(pcspHistoryFormDao.getPcspCase(apsCommonReq.getIdCase()));
            //Call for CallCINVD6D
            serviceDto.setCareDetailDtoList(pcspHistoryFormDao.getCareDetailInfo(apsCommonReq.getIdCase()));
            //Call for CallCINVD7D
            serviceDto.setCareNarrativeInfoDtoList(pcspHistoryFormDao.getCareNarrativeInfo(apsCommonReq.getIdCase()));
        }

       return apsOutcomeMatrixServicePrefillData.returnPrefillData(serviceDto);
    }
}
