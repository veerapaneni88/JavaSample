package us.tx.state.dfps.service.apscareformsnarrative.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.apscareformsnarrative.dao.ApsCareFormsNarrativeDao;
import us.tx.state.dfps.service.apscareformsnarrative.dto.*;
import us.tx.state.dfps.service.apscareformsnarrative.service.ApsCareFormsNarrativeService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsCareFormsNarrativeServicePrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Care Forms Narrative - civ35o00.
 * Jan 19th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsCareFormsNarrativeServiceImpl implements ApsCareFormsNarrativeService {

    @Autowired
    ApsCareFormsNarrativeServicePrefillData apsCareFormsNarrativeServicePrefillData;

    @Autowired
    CapsCaseDao capsCaseDao;

    @Autowired
    ApsCareFormsNarrativeDao apsCareFormsNarrativeDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    EventDao eventDao;

    /**
     * method to get Aps Care Forms Narrative data
     *
     * @param apsCommonReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsCareFormsNarrativeData(ApsCommonReq apsCommonReq) {

        ApsCareFormsNarrativeServiceDto apsCareFormsNarrativeServiceDto = new ApsCareFormsNarrativeServiceDto();

        apsCareFormsNarrativeServiceDto.setIdEvent(apsCommonReq.getIdEvent());
        apsCareFormsNarrativeServiceDto.setCapsCaseDto(capsCaseDao.getCapsCaseByid(apsCommonReq.getIdCase()));

        EventDto eventDto = eventDao.getEventByid(apsCommonReq.getIdEvent());
        apsCareFormsNarrativeServiceDto.setPerson(personDao.getPersonByPersonId(eventDto.getIdPerson()));
        //Call for CINVD8D
        apsCareFormsNarrativeServiceDto.setCare(apsCareFormsNarrativeDao.getApsCareData(apsCommonReq.getIdEvent()));
        List<ApsCareDomainDto> apsCareDomainDtoList = apsCareFormsNarrativeDao.getApsCareDomaindata(apsCommonReq.getIdEvent());
        List<ApsCareCategoryDto> apsCareCategoryDtoList = apsCareFormsNarrativeDao.getApsCareCategoryData(apsCommonReq.getIdEvent());
        List<ApsCareFactorDto> apsCareFactorDtoList = apsCareFormsNarrativeDao.getApsCareFactorData(apsCommonReq.getIdEvent());

        List<ApsCareFormsDto> apsCareFormsDtoList = new ArrayList<>();

        for (ApsCareDomainDto apsCareDomain : apsCareDomainDtoList) {
            ApsCareFormsDto apsCareFormsDto = new ApsCareFormsDto();
            String codeCareDomain = apsCareDomain.getCdCareDomain();
            apsCareFormsDto.setCdCareDomain(apsCareDomain.getCdCareDomain());
            apsCareFormsDto.setIdEvent(apsCareDomain.getIdEvent());
            apsCareFormsDto.setCdAllegationFocus(apsCareDomain.getCdAllegationFocus());
            apsCareFormsDto.setTxtDomain(apsCareDomain.getTxtDomain());
            if (!TypeConvUtil.isNullOrEmpty(apsCareCategoryDtoList)) {
                List<ApsCareCategoryDto> careCategoryDtoList = apsCareCategoryDtoList.stream().filter(careCategory -> codeCareDomain.equals(careCategory.getCdCareDomain())).collect(Collectors.toList());
                apsCareFormsDto.setApsCareCategoryDtoList(careCategoryDtoList);
            }
            if (!TypeConvUtil.isNullOrEmpty(apsCareFactorDtoList)) {
                List<ApsCareFactorDto> apsCareFactorDto = apsCareFactorDtoList.stream().filter(careFactor -> codeCareDomain.equals(careFactor.getCdCareDomain())).collect(Collectors.toList());
                apsCareFormsDto.setApsCareFactorDtoList(apsCareFactorDto);
            }
            apsCareFormsDtoList.add(apsCareFormsDto);
        }
        apsCareFormsNarrativeServiceDto.setApsCareFormsDtos(apsCareFormsDtoList);

        return apsCareFormsNarrativeServicePrefillData.returnPrefillData(apsCareFormsNarrativeServiceDto);
    }
}
