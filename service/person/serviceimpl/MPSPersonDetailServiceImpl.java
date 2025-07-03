package us.tx.state.dfps.service.person.serviceimpl;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.PersonRaceMps;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.mobile.PersonRaceMpsDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.contacts.dao.MPSStatsDao;
import us.tx.state.dfps.service.person.dao.MPSPersonDetailDao;
import us.tx.state.dfps.service.person.service.MPSPersonDetailService;

@Service
@Transactional
public class MPSPersonDetailServiceImpl implements MPSPersonDetailService {

    private static final String AUD_TYPE_A = "A" ;
    private static final String AUD_TYPE_U = "U";
    private static final String AUD_TYPE_D = "D";
    @Autowired
    MPSPersonDetailDao mpsPersonDetailDao;
    @Autowired
    private MPSStatsDao mpsStatsDao;
    @Autowired
    private MobileUtil mobileUtil;

    @Override
    public IncomingPersonMpsRes retrievePersonDetails(PersonDetailsReq personDetailsReq) {

        IncomingPersonMps incomingPersonMps = mpsPersonDetailDao.fetchIncomingPersonById(personDetailsReq.getIdPerson());
        List<PersonRaceMps> personRaceMpsList = mpsPersonDetailDao.fetchPersonRaceMPSByIncomingPersonId(personDetailsReq.getIdPerson());
        IncomingPersonMpsDto incomingPersonMpsDto = new IncomingPersonMpsDto();
        IncomingPersonMpsRes incomingPersonMpsRes = new IncomingPersonMpsRes();
        BeanUtils.copyProperties(incomingPersonMps, incomingPersonMpsDto);
        List<String> raceList = !personRaceMpsList.isEmpty() ?
                personRaceMpsList.stream().map(PersonRaceMps::getCdRace).collect(Collectors.toList()) : null;

        incomingPersonMpsRes.setIncomingPersonMpsDto(incomingPersonMpsDto);
        incomingPersonMpsRes.setCdPersonRace(raceList);
        return incomingPersonMpsRes;
    }

    public Long savePersonDetails(IncomingPersonMpsDto incomingPersonMpsDto) {

        IncomingPersonMps incomingPersonMps = new IncomingPersonMps();
        BeanUtils.copyProperties(incomingPersonMpsDto, incomingPersonMps);
        populateBean(incomingPersonMps);
        Long idIncomingPersonMps = mpsPersonDetailDao.saveIncomingPersonDetails(incomingPersonMps);

        IncomingPersonMps savedIncomingPersonMps = mpsPersonDetailDao.fetchIncomingPersonById(idIncomingPersonMps);

        String audType= AUD_TYPE_A;
        if (0L != incomingPersonMpsDto.getIdIncomingPersonMps()) {
            audType= AUD_TYPE_U;
        }
        if (0L != incomingPersonMpsDto.getIdIncomingPersonMps()) {
            mpsPersonDetailDao.deletePersonRaceMps(incomingPersonMpsDto.getIdIncomingPersonMps());

        }
        for (PersonRaceMpsDto raceDto : incomingPersonMpsDto.getPersonRaceMpsDtoSet()) {
            PersonRaceMps personRaceMps = new PersonRaceMps();
            personRaceMps.setIncomingPersonMps(savedIncomingPersonMps);
            personRaceMps.setCdRace(raceDto.getCdRace());
            personRaceMps.setDtLastUpdate(new Date());
            mpsPersonDetailDao.savePersonRaceMps(personRaceMps);
        }

        if (mobileUtil.isMPSEnvironment())
        {
            callMPSStatsHelper(audType,incomingPersonMps.getIdIncomingPersonMps(), incomingPersonMpsDto.getIdCase(),incomingPersonMpsDto.getIdStage());

        }
        return idIncomingPersonMps;
    }

    private void callMPSStatsHelper(String audType, long idIncomingPersonMps, long idCase, Long idStage)
    {

        MPSStatsValueDto mpsStatsValueDto =  new MPSStatsValueDto();

        if( ServiceConstants.REQ_FUNC_CD_ADD.equals(audType) )
        {
            mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_ADD);
        }
        else if(ServiceConstants.REQ_FUNC_CD_UPDATE.equals(audType))
        {
            mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_UPDATE);
            mpsStatsValueDto.setIdReference(idIncomingPersonMps);
        }
        else
        {
            mpsStatsValueDto.setCdDmlType(audType);
            mpsStatsValueDto.setIdReference(idIncomingPersonMps);
        }

        mpsStatsValueDto.setIdCase(idCase);
        mpsStatsValueDto.setIdStage(idStage);
        mpsStatsValueDto.setCdReference(CodesConstant.CMPSSTAT_011);
        mpsStatsDao.logStatsToDB( mpsStatsValueDto );

    }

    private void populateBean(IncomingPersonMps incomingPersonMps) {

        if (ServiceConstants.Zero_Value == incomingPersonMps.getIdIncomingPersonMps()) {
            incomingPersonMps.setIndStagePersRelated("Y");
            incomingPersonMps.setIndMpsPersUsed("N");
            incomingPersonMps.setCdPersSearchInd("M");
            incomingPersonMps.setDtPersonAdded(new Date());
        }
        String nameFull = FormattingHelper.formatFullName(incomingPersonMps.getNmFirst(),
                incomingPersonMps.getNmMiddle(),
                incomingPersonMps.getNmLast());
        incomingPersonMps.setNmFull(nameFull);
        incomingPersonMps.setIndDobApprox
                (StringUtils.isEmpty(incomingPersonMps.getIndDobApprox()) ? "N" :
                        "true".equals(incomingPersonMps.getIndDobApprox())? "Y":incomingPersonMps.getIndDobApprox());
        incomingPersonMps.setDtLastUpdate(new Date());
    }

    public void deletePersonDetails(CommonHelperReq commonHelperReq) {

        if (!ObjectUtils.isEmpty(commonHelperReq.getIdPerson())) {

            IncomingPersonMps incomingPersonMps = mpsPersonDetailDao.fetchIncomingPersonById(commonHelperReq.getIdPerson());
            if (CollectionUtils.isNotEmpty(incomingPersonMps.getPersonRaceMpses())) {
                mpsPersonDetailDao.deletePersonRaceMps(incomingPersonMps.getIdIncomingPersonMps());
            }
            mpsPersonDetailDao.deleteIncomingPersonDetails(commonHelperReq.getIdPerson());
            if (mobileUtil.isMPSEnvironment())
            {
                String audType = AUD_TYPE_D;
                callMPSStatsHelper(audType,incomingPersonMps.getIdIncomingPersonMps(), incomingPersonMps.getIdCase(),incomingPersonMps.getIdStage());

            }
        }
    }


}
