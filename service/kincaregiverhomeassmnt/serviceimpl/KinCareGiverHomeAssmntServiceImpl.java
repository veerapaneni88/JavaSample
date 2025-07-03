package us.tx.state.dfps.service.kincaregiverhomeassmnt.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverAddressDto;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverCaseInfoDto;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverHomeInfoDto;
import us.tx.state.dfps.service.common.request.KinCareGiverHomeDetailsReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.KinCareGiverHomeAssmntPrefillData;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.dao.KinCareGiverHomeAssmntDao;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.service.KinCareGiverHomeAssmntService;


/**
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@Service
@Transactional
public class KinCareGiverHomeAssmntServiceImpl implements KinCareGiverHomeAssmntService {

    @Autowired
    KinCareGiverHomeAssmntDao kinCareGiverHomeAssmntDao;

    @Autowired
    KinCareGiverHomeAssmntPrefillData kinCareGiverHomeAssmntPrefillData;

    /**
     * Method Name: getKinCareGiverHomeDetails Method Description: Request to get
     * the Home Assessment Template Data in prefill data format
     *
     * @param careGiverDetailsReq
     * @return
     */
    @Override
    public PreFillDataServiceDto getKinCareGiverHomeDetails(KinCareGiverHomeDetailsReq careGiverDetailsReq) {

        KinCareGiverHomeInfoDto kinCareGiverHomeInfoDto = new KinCareGiverHomeInfoDto();
        KinCareGiverCaseInfoDto caseInfo = kinCareGiverHomeAssmntDao.getKinCareGiverCaseInfo(careGiverDetailsReq.getIdStage());
        kinCareGiverHomeInfoDto.setCaseInfoDto(caseInfo);
        KinCareGiverAddressDto addressDto = kinCareGiverHomeAssmntDao.getKinCareGiverAddress(careGiverDetailsReq.getIdResource());
        kinCareGiverHomeInfoDto.setCareGiverAddressDto(addressDto);

        return kinCareGiverHomeAssmntPrefillData.returnPrefillData(kinCareGiverHomeInfoDto);
    }
}
