package us.tx.state.dfps.service.IndianChildStatus.serviceimpl;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.IndianChildStatus.dao.IndianChildStatusDao;
import us.tx.state.dfps.service.IndianChildStatus.service.IndianChildStatusService;
import us.tx.state.dfps.service.common.request.IndianChildStatusInfoReq;
import us.tx.state.dfps.service.common.response.IndianChildStatusInfoRes;
import us.tx.state.dfps.web.IndianChildStatus.dto.*;

import javax.transaction.Transactional;
import java.util.*;

/**
 *
 * IndianChildStatusServiceImpl 2022 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class IndianChildStatusServiceImpl implements IndianChildStatusService {

    @Autowired
    IndianChildStatusDao indianChildStatusDao;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public IndianChildStatusInfoRes saveIndianChildStatusDetails(IndianChildStatusInfoReq indianChildStatusInfoReq) {
        IndianChildStatusInfoRes indianChildStatusInfoRes= new IndianChildStatusInfoRes();
        IndianChildStatusDto indianChildStatusDto  =new IndianChildStatusDto();

        try {
            TribeChildStatusDto tribeChildStatusDto=indianChildStatusDao.getChildTribeStatusByStageId(indianChildStatusInfoReq.getIdStage());
            if (tribeChildStatusDto!=null && tribeChildStatusDto.getIdTribeChildStatus()!=null && tribeChildStatusDto.getIdTribeChildStatus()>0) {
                // update
                if (!indianChildStatusInfoReq.getCdIcwaElig().equalsIgnoreCase(tribeChildStatusDto.getCdIcwaElig())) {
                   TribeChildStatus tribeChildStatus= new TribeChildStatus();
                    BeanUtils.copyProperties(tribeChildStatusDto,tribeChildStatus);
                    tribeChildStatus.setDtCreated(new Date());
                    tribeChildStatus.setDtLastUpdate(new Date());
                    tribeChildStatus.setCdIcwaElig(indianChildStatusInfoReq.getCdIcwaElig());
                    indianChildStatusDao.updateTribeChildStatus(tribeChildStatus);
                }
            }else{
                // insert
             TribeChildStatus tribeChildStatus=   populateTribeChildStatus(indianChildStatusInfoReq);
                indianChildStatusDao.saveTribeChildStatus(tribeChildStatus);
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }
        indianChildStatusInfoRes.setIndianChildStatusDto(indianChildStatusDto);
        return indianChildStatusInfoRes;
    }

    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
   public IndianChildStatusInfoRes getIndianChildStatusDetails(IndianChildStatusInfoReq indianChildStatusInfoReq){
        Long stageId=indianChildStatusInfoReq.getIdStage();
        IndianChildStatusInfoRes indianChildStatusInfoRes= new IndianChildStatusInfoRes();
        TribeChildStatusDto tribeChildStatusDto=indianChildStatusDao.getChildTribeStatusByStageId(stageId);
        indianChildStatusInfoRes.setTribeChildStatusDto(tribeChildStatusDto);
        return indianChildStatusInfoRes;
    }

    /**
     * @param IndianChildStatusInfoReq
     * @return TribeChildStatus
     */
    private TribeChildStatus populateTribeChildStatus(IndianChildStatusInfoReq indianChildStatusInfoReq){
        TribeChildStatus tribeChildStatus=new TribeChildStatus();
        Set<IcwaInterviewDtls> icwaInterviewDtlsSet = new HashSet<>();
        tribeChildStatus.setIdStage(indianChildStatusInfoReq.getIdStage());
        tribeChildStatus.setIndIcwaIntrvwed(indianChildStatusInfoReq.getIndIcwaIntrvwed());
        tribeChildStatus.setIdCreatedPerson(indianChildStatusInfoReq.getIdCreatedPerson());
        tribeChildStatus.setIdLastUpdatePerson(indianChildStatusInfoReq.getIdCreatedPerson());
        tribeChildStatus.setDtCreated(new Date());
        tribeChildStatus.setDtLastUpdate(new Date());
        tribeChildStatus.setCdIcwaElig(indianChildStatusInfoReq.getCdIcwaElig());

        for(String cdIcwaIntrvwed :indianChildStatusInfoReq.getInterviwedPersonList()){
            IcwaInterviewDtls  icwaInterviewDtls =new IcwaInterviewDtls();
            icwaInterviewDtls.setCdIcwaIntrvwed(cdIcwaIntrvwed);
            icwaInterviewDtls.setTribeChildStatus(tribeChildStatus);
            icwaInterviewDtls.setDtLastUpdate(new Date());
            icwaInterviewDtlsSet.add(icwaInterviewDtls);
            icwaInterviewDtls.setDtCreated(new Date());
            icwaInterviewDtls.setIdCreeatedPerson(indianChildStatusInfoReq.getIdCreatedPerson());
            icwaInterviewDtls.setIdLastUpdatePerson(indianChildStatusInfoReq.getIdCreatedPerson());
            icwaInterviewDtls.setDtLastUpdate(new Date());
        }

        tribeChildStatus.setIcwaInterviewDtls(icwaInterviewDtlsSet);
     return tribeChildStatus;
    }

    /**
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public IndianChildStatusInfoRes getFederalTribeStateList(IndianChildStatusInfoReq indianChildStatusInfoReq){
        IndianChildStatusInfoRes indianChildStatusInfoRes=new IndianChildStatusInfoRes();
        List<FederalStateTribeListDto>  federalStateTribeListDtoList= indianChildStatusDao.getFederalTribeStateList();
        indianChildStatusInfoRes.setFederalStateTribeListDtoList(federalStateTribeListDtoList);
       return indianChildStatusInfoRes;
   }


    /**
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public IndianChildStatusInfoRes saveIndianTribeNotification(IndianChildStatusInfoReq indianChildStatusInfoReq) {
        TribeChildStatus tribeChildStatus = (TribeChildStatus) sessionFactory.getCurrentSession().createCriteria(TribeChildStatus.class).add(Restrictions.eq("idStage", indianChildStatusInfoReq.getIdStage())).uniqueResult();
        IndianChildStatusInfoRes indianChildStatusInfoRes=new IndianChildStatusInfoRes();
        IcwaTribeNotif icwaTribeNotif=null;
       if (tribeChildStatus.getIdTribeChildStatus()>0) {
            icwaTribeNotif = new IcwaTribeNotif();
           icwaTribeNotif.setIdTribeChildStatus(tribeChildStatus.getIdTribeChildStatus());
           icwaTribeNotif.setCdIcwaConfirm(indianChildStatusInfoReq.getCdIcwaConfirm());
           icwaTribeNotif.setDtIcwaConfirm(indianChildStatusInfoReq.getDtIcwaConfirm());
           icwaTribeNotif.setIndIcwaLegalNotice(indianChildStatusInfoReq.getIndIcwaLegalNotice());
           icwaTribeNotif.setDtCreated(new Date());
           icwaTribeNotif.setDtLastUpdate(new Date());
           icwaTribeNotif.setIdCreatedPerson(indianChildStatusInfoReq.getIdCreatedPerson());
           if (indianChildStatusInfoReq.getIdIcwaTribeNotif()!=null && indianChildStatusInfoReq.getIdIcwaTribeNotif()>0){
               icwaTribeNotif.setIdIcwaTribeNotif(indianChildStatusInfoReq.getIdIcwaTribeNotif());
           }
           indianChildStatusDao.saveTribeNotification(icwaTribeNotif);
       }
        if (indianChildStatusInfoReq.getIdFederalStateTribeList()!=null && indianChildStatusInfoReq.getIdFederalStateTribeList()>0) {
            IcwaChildTribeLink icwaChildTribeLink = new IcwaChildTribeLink();
            icwaChildTribeLink.setIdTribeChildStatus(tribeChildStatus.getIdTribeChildStatus());
            icwaChildTribeLink.setIdFederalStateTribeList(indianChildStatusInfoReq.getIdFederalStateTribeList());
            icwaChildTribeLink.setDtCreated(new Date());
            icwaChildTribeLink.setDtLastUpdate(new Date());
            icwaChildTribeLink.setIdCreatedPerson(indianChildStatusInfoReq.getIdCreatedPerson());
            if (null!=icwaTribeNotif) {
                icwaChildTribeLink.setIdIcwaTribeNotif(icwaTribeNotif.getIdIcwaTribeNotif());
            }
            icwaChildTribeLink.setIcwaTribeNotif(icwaTribeNotif);
            indianChildStatusDao.saveIcwaTribeChildLink(icwaChildTribeLink);

        }

        return indianChildStatusInfoRes;
    }

    /**
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public IndianChildStatusInfoRes getIcwaTribeNotifDetails(IndianChildStatusInfoReq indianChildStatusInfoReq){
        IndianChildStatusInfoRes indianChildStatusInfoRes =new IndianChildStatusInfoRes();
             List<IcwaInfoListDto> icwaInfoListDtoList=null;
        if (indianChildStatusInfoReq.getIdTribeChildStatus()>0) {
            icwaInfoListDtoList = indianChildStatusDao.getIcwaTribeNotifDetails(indianChildStatusInfoReq.getIdTribeChildStatus());

        }
        indianChildStatusInfoRes.setIcwaInfoListDtoList(icwaInfoListDtoList);
        return indianChildStatusInfoRes;
    }


    /**
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
   public  IndianChildStatusInfoRes  getIcwaTribeNotifRecord(IndianChildStatusInfoReq indianChildStatusInfoReq){
        IndianChildStatusInfoRes indianChildStatusInfoRes =new IndianChildStatusInfoRes();
        IcwaTribeNotifDto icwaTribeNotifDto= indianChildStatusDao.getIcwaTribeNotifRecord(indianChildStatusInfoReq.getIdIcwaTribeNotif());
        indianChildStatusInfoRes.setIcwaTribeNotifDto(icwaTribeNotifDto);
        return indianChildStatusInfoRes;
    }

    /**
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
   public  IndianChildStatusInfoRes deleteIcwaTribeNotif(IndianChildStatusInfoReq indianChildStatusInfoReq){
        IndianChildStatusInfoRes indianChildStatusInfoRes =new IndianChildStatusInfoRes();
        IcwaChildTribeLink icwaChildTribeLink = (IcwaChildTribeLink) sessionFactory.getCurrentSession().createCriteria(IcwaChildTribeLink.class).add(Restrictions.eq("idIcwaChildTribeLink", indianChildStatusInfoReq.getIdIcwaChildTribeLink())).uniqueResult();
        IcwaTribeNotif icwaTribeNotif = (IcwaTribeNotif) sessionFactory.getCurrentSession().createCriteria(IcwaTribeNotif.class).add(Restrictions.eq("idIcwaTribeNotif", icwaChildTribeLink.getIdIcwaTribeNotif())).uniqueResult();
        indianChildStatusDao.deleteIcwaTribeChildLik(icwaChildTribeLink);
        indianChildStatusDao.deleteIcwaTribeNotif(icwaTribeNotif);
        return indianChildStatusInfoRes;
    }
}
