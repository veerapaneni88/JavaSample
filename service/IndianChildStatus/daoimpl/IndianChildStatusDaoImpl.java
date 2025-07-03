package us.tx.state.dfps.service.IndianChildStatus.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.IndianChildStatus.dao.IndianChildStatusDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.web.IndianChildStatus.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * Class Description: IndianChildStatusDao Interface July 01, 2022
 */
@Repository
public class IndianChildStatusDaoImpl implements IndianChildStatusDao {


    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    MessageSource messageSource;


    @Value("${IndianChildStatusDaoImpl.getChildTribeStatusByStageIdSql}")
    private String getChildTribeStatusByStageIdSql;

    @Value("${IndianChildStatusDaoImpl.getIcwaTribeNotifDetailsSql}")
    private String getIcwaTribeNotifDetailsSql;

    @Override
    public void saveTribeChildStatus(TribeChildStatus tribeChildStatus){
        sessionFactory.getCurrentSession().save(tribeChildStatus);
    }

    @Override
    public void saveTribeNotification(IcwaTribeNotif icwaTribeNotif){
        sessionFactory.getCurrentSession().saveOrUpdate(icwaTribeNotif);
    }

    @Override
    public void saveIcwaTribeChildLink(IcwaChildTribeLink icwaChildTribeLink){
        sessionFactory.getCurrentSession().saveOrUpdate(icwaChildTribeLink);
    }


    @Override
    public void updateTribeChildStatus(TribeChildStatus tribeChildStatus) {
        sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(tribeChildStatus));
    }

    /**
     * getChildTribestatusByPersonIdAndStageId
     *
     * @param stageId
     * @return TribeChildStatus
     * @throws DataNotFoundException
     */
    @Override
    public TribeChildStatusDto getChildTribeStatusByStageId(Long stageId) {
         List<String> interviwedPersonList = new ArrayList<>();
        TribeChildStatus tribeChildStatus = null;
        TribeChildStatusDto tribeChildStatusDto=new TribeChildStatusDto();
        Query queryTribeChildstatus = sessionFactory.getCurrentSession().createQuery(getChildTribeStatusByStageIdSql);
        queryTribeChildstatus.setParameter("idStage", stageId);
        tribeChildStatus = (TribeChildStatus) queryTribeChildstatus.uniqueResult();

        if(tribeChildStatus!=null) {
            BeanUtils.copyProperties(tribeChildStatus, tribeChildStatusDto);
            Set <IcwaInterviewDtls> interviewPersonList=  tribeChildStatus.getIcwaInterviewDtls();

            List<IcwaInterviewDtlsDto> icwaInterviewDtlsDtoList = new ArrayList<>();

            for (IcwaInterviewDtls icwaInterviewDtls : interviewPersonList){
                IcwaInterviewDtlsDto icwaInterviewDtlsDto =new IcwaInterviewDtlsDto();
                icwaInterviewDtlsDto.setCdIcwaIntrvwed(icwaInterviewDtls.getCdIcwaIntrvwed());
                icwaInterviewDtlsDto.setIdIcwaInterviewDtls(icwaInterviewDtls.getIdIcwaInterviewDtls());
                icwaInterviewDtlsDto.setIdTribeChildStatus(icwaInterviewDtls.getTribeChildStatus().getIdTribeChildStatus());
                icwaInterviewDtlsDtoList.add(icwaInterviewDtlsDto);
                interviwedPersonList.add(icwaInterviewDtls.getCdIcwaIntrvwed());
            }
            tribeChildStatusDto.setInterviwedPersonList(interviwedPersonList);
            tribeChildStatusDto.setIcwaInterviewDtlsDtoList(icwaInterviewDtlsDtoList);
        }


        return tribeChildStatusDto;
    }

    /**
     * @param
     * @return List<FederalStateTribeListDto>
     */
    @Override
    public List<FederalStateTribeListDto> getFederalTribeStateList(){
        List<FederalStateTribeListDto> federalStateTribeListDtoList=new ArrayList<>();
        List<FederalStateTribeList> federalStateTribeList= sessionFactory.getCurrentSession().createCriteria(FederalStateTribeList.class).list();
        for ( FederalStateTribeList federalStateTribeListrecord : federalStateTribeList){
            FederalStateTribeListDto federalStateTribeListDto =new FederalStateTribeListDto();
            BeanUtils.copyProperties(federalStateTribeListrecord, federalStateTribeListDto);
            federalStateTribeListDtoList.add(federalStateTribeListDto);
        }
      return federalStateTribeListDtoList;
    }

    /**
     * @param idTribeChildstatus
     * @return  List<IcwaInfoListDto>
     */
    @Override
    public List<IcwaInfoListDto> getIcwaTribeNotifDetails(Long idTribeChildstatus){
        List<IcwaInfoListDto> icwaInfoListDtoList  =new ArrayList<>();
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIcwaTribeNotifDetailsSql)
                .addScalar("idLink", StandardBasicTypes.LONG)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("tribeNm", StandardBasicTypes.STRING)
                .addScalar("inqStatus", StandardBasicTypes.STRING)
                .addScalar("dtConfrm", StandardBasicTypes.DATE)
                .addScalar("indLegNotice", StandardBasicTypes.STRING)
                .addScalar("idIcwaTribeNotif", StandardBasicTypes.LONG)
                .setParameter("idTribeChildStatus", idTribeChildstatus)
                .setResultTransformer(Transformers.aliasToBean(IcwaInfoListDto.class));
        icwaInfoListDtoList = (List<IcwaInfoListDto>) query.list();
        return icwaInfoListDtoList;
    }

    /**
     * @param idIcwaTribeNotif
     * @return IcwaTribeNotifDto
     */
   @Override
    public IcwaTribeNotifDto getIcwaTribeNotifRecord(Long idIcwaTribeNotif){
       IcwaTribeNotifDto icwaTribeNotifDto= new IcwaTribeNotifDto();
       IcwaTribeNotif icwaTribeNotif = (IcwaTribeNotif) sessionFactory.getCurrentSession().createCriteria(IcwaTribeNotif.class).add(Restrictions.eq("idIcwaTribeNotif", idIcwaTribeNotif)).uniqueResult();
       if (icwaTribeNotif!=null) {
           BeanUtils.copyProperties(icwaTribeNotif, icwaTribeNotifDto);
       }
       return icwaTribeNotifDto;
    }

    /**
     *
     * Method Description:deletePerson
     *
     * @param IcwaTribeNotif
     * @throws DataNotFoundException
     */
    @Override
    public void deleteIcwaTribeNotif(IcwaTribeNotif icwaTribeNotif) throws DataNotFoundException {
        sessionFactory.getCurrentSession().delete(icwaTribeNotif);
    }

    /**
     *
     * Method Description:deletePerson
     *
     * @param IcwaChildTribeLink
     * @throws DataNotFoundException
     */
    @Override
    public void deleteIcwaTribeChildLik(IcwaChildTribeLink icwaChildTribeLink) throws DataNotFoundException {
        sessionFactory.getCurrentSession().delete(icwaChildTribeLink);
    }
}
