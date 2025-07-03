package us.tx.state.dfps.service.cciinvreport.daoimpl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.AfcarsResponse;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvContactDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvIntakePersonDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvLetterDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvReportPersonDto;
import us.tx.state.dfps.service.cciinvreport.dao.CciInvReportDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CciInvReportDaoImpl implements CciInvReportDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Value("${CciInvReportDaoImpl.getPrincipals}")
    private transient String getPrincipalsSql;

    @Value("${CciInvReportDaoImpl.getPersonSplInfo}")
    private transient String getPersonSplInfoSql;

    @Value("${CciInvReportDaoImpl.getColCharacteristics}")
    private transient String getColCharacteristicsSql;

    @Value("${CciInvReportDaoImpl.getPrnCharacteristics}")
    private transient String getPrnCharacteristicsSql;

    @Value("${CciInvReportDaoImpl.getPrnAfcarsCharacteristics}")
    private transient String getPrnAfcarsCharacteristicsSql;

    @Value("${CciInvReportDaoImpl.getColAfcarsCharacteristics}")
    private transient String getColAfcarsCharacteristicsSql;

    @Value("${CciInvReportDaoImpl.getInvInitiatedDate}")
    private transient String getInvInitiatedDate;

    @Value("${CciInvReportDaoImpl.getIntakeWithReporter}")
    private transient String getIntakeWithReporterSql;

    @Value("${CciInvReportDaoImpl.getIntake}")
    private transient String getIntakesSql;

    @Value("${CciInvReportDaoImpl.getContactList}")
    private transient String getContactListSql;

    @Value("${CciInvReportDaoImpl.getPrincCollatList}")
    private transient String getPrincCollatListSql;

    @Value("${CciInvReportDaoImpl.getLetterDetail}")
    private transient String getLetterDetailSql;

    @Autowired
    CpsIntakeReportDao cpsIntakeReportDao;


    @Override
    public List<CciInvReportPersonDto> getPrincipals(Long idStage, String cdStagePersType) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrincipalsSql)
                .addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .addScalar("nbrPersonAge", StandardBasicTypes.STRING)
                .addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("cdPersonSex", StandardBasicTypes.STRING)
                .addScalar("dtPersonDeath", StandardBasicTypes.DATE)
                .addScalar("cdPersonDeath", StandardBasicTypes.STRING)
                .addScalar("cdPersLang", StandardBasicTypes.STRING)
                .addScalar("cdPersEthnGrp", StandardBasicTypes.STRING).addScalar("persRace", StandardBasicTypes.STRING)
                .addScalar("cdEthn", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("cdStagePersRole", StandardBasicTypes.STRING)
                .addScalar("txtStagePersNote", StandardBasicTypes.STRING)
                .addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
                .addScalar("cdPersChar", StandardBasicTypes.STRING)
                .addScalar("addrPersStLn1", StandardBasicTypes.STRING)
                .addScalar("addrPersCity", StandardBasicTypes.STRING)
                .addScalar("cdPersState", StandardBasicTypes.STRING).addScalar("nbrPersId", StandardBasicTypes.STRING)
                .addScalar("persZip", StandardBasicTypes.STRING)
                .addScalar("cdLegalStatus", StandardBasicTypes.STRING)
                .addScalar("dtLegalStatus", StandardBasicTypes.DATE).setParameter("idStage", idStage).setParameter("cdStagePersType",cdStagePersType)
                .setResultTransformer(Transformers.aliasToBean(CciInvReportPersonDto.class));
        return query.list();
    }

    @Override
    public List<CciInvReportPersonDto> getPersonSplInfo(Long idStage) {
        String querySql = getPersonSplInfoSql;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(querySql)
                .addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .addScalar("cdPersonSex", StandardBasicTypes.STRING)
                .addScalar("txtStagePersNote", StandardBasicTypes.STRING)
                .addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(CciInvReportPersonDto.class));
        return query.list();
    }

    @Override
    public List<CharacteristicsDto> getColCharacteristicsByStage(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getColCharacteristicsSql)
                .addScalar("idcharacId", StandardBasicTypes.LONG).addScalar("idpersonId", StandardBasicTypes.LONG)
                .addScalar("cdCharacCategory", StandardBasicTypes.STRING)
                .addScalar("cdCharacCode", StandardBasicTypes.STRING)
                .addScalar("dtCharacStart", StandardBasicTypes.DATE).addScalar("dtCharacEnd", StandardBasicTypes.DATE)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdStatus", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class));
        List<CharacteristicsDto> resultList = (List<CharacteristicsDto>) query.list();
        return resultList;
    }

    @Override
    public List<AfcarsResponse> getColAfcarsCharacteristicsByStage(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getColAfcarsCharacteristicsSql)
                .addScalar("idAfcarsResponse", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdResponseType", StandardBasicTypes.STRING)
                .addScalar("cdResponse", StandardBasicTypes.STRING)
                .addScalar("dtBegin", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(AfcarsResponse.class));
        List<AfcarsResponse> resultList = (List<AfcarsResponse>) query.list();
        return resultList;
    }

    @Override
    public List<CharacteristicsDto> getPrnCharacteristicsByStage(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrnCharacteristicsSql)
                .addScalar("idcharacId", StandardBasicTypes.LONG).addScalar("idpersonId", StandardBasicTypes.LONG)
                .addScalar("cdCharacCategory", StandardBasicTypes.STRING)
                .addScalar("cdCharacCode", StandardBasicTypes.STRING)
                .addScalar("dtCharacStart", StandardBasicTypes.DATE).addScalar("dtCharacEnd", StandardBasicTypes.DATE)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdStatus", StandardBasicTypes.STRING)
                .addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class));
        List<CharacteristicsDto> resultList = (List<CharacteristicsDto>) query.list();
        return resultList;
    }

    @Override
    public List<AfcarsResponse> getPrnAfcarsCharacteristicsByStage(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrnAfcarsCharacteristicsSql)
                .addScalar("idAfcarsResponse", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdResponseType", StandardBasicTypes.STRING)
                .addScalar("cdResponse", StandardBasicTypes.STRING)
                .addScalar("dtBegin", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(AfcarsResponse.class));
        List<AfcarsResponse> resultList = (List<AfcarsResponse>) query.list();
        return resultList;
    }

    @Override
    public Date getDtInvInitiatedByStage(Long idCase, Long idStage) {
        Date invInitiatedDate = (Date) sessionFactory.getCurrentSession().createSQLQuery(getInvInitiatedDate)
                .setParameter("idCase", idCase).setParameter("idStage", idStage).uniqueResult();
        return (!ObjectUtils.isEmpty(invInitiatedDate)) ? invInitiatedDate : null;
    }

    public List<CciInvIntakePersonDto> getIntakes(Long stageId) {
        List<CciInvIntakePersonDto> cciInvIntakePersonDtoList = new  ArrayList<CciInvIntakePersonDto>();

        Query query1 = sessionFactory.getCurrentSession().createSQLQuery(getIntakeWithReporterSql)
                .addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPriorStage", StandardBasicTypes.LONG)
                .addScalar("txtStagePersonNote", StandardBasicTypes.STRING)
                .addScalar("cdStagePersonRelInt", StandardBasicTypes.STRING)
                .addScalar("cdStageType", StandardBasicTypes.STRING)
                .addScalar("dtStageStart", StandardBasicTypes.DATE)
                .setParameter("stageId", stageId)
                .setResultTransformer(Transformers.aliasToBean(CciInvIntakePersonDto.class));
        cciInvIntakePersonDtoList = query1.list();

        if(ObjectUtils.isEmpty(cciInvIntakePersonDtoList)) {
            Query query2 = sessionFactory.getCurrentSession().createSQLQuery(getIntakesSql)
                    .addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPriorStage", StandardBasicTypes.LONG)
                    .addScalar("cdStageType", StandardBasicTypes.STRING)
                    .addScalar("dtStageStart", StandardBasicTypes.DATE)
                    .setParameter("stageId", stageId)
                    .setResultTransformer(Transformers.aliasToBean(CciInvIntakePersonDto.class));
            if(!ObjectUtils.isEmpty(query2.list())) {
                cciInvIntakePersonDtoList = query2.list();
                IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao
                        .getStageIncomingDetails(cciInvIntakePersonDtoList.get(0).getIdPriorStage());
                if(ServiceConstants.CRPTRINT_SF.equals(incomingStageDetailsDto.getCdIncmgCallerInt())){
                    if(!ObjectUtils.isEmpty(cciInvIntakePersonDtoList) && !ObjectUtils.isEmpty(cciInvIntakePersonDtoList.get(0))){
                        CciInvIntakePersonDto cciInvIntakePersonDto = cciInvIntakePersonDtoList.get(0);
                        cciInvIntakePersonDto.setTxtStagePersonNote(!ObjectUtils.isEmpty(incomingStageDetailsDto) ? incomingStageDetailsDto.getTxtReporterNotes() : "");
                    }
                }
            }
        }
        return cciInvIntakePersonDtoList;
    }

    @Override
    public List<CciInvContactDto> getContactList(Long idStage, String mergedStages){
        if (StringUtils.isBlank(mergedStages)) {
            return new ArrayList<CciInvContactDto>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getContactListSql);
        sb.append(" AND T.ID_CONTACT_STAGE IN (");
        sb.append(mergedStages);
        sb.append(" ) ORDER BY dtContactOccurred");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString())
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("idContactStage", StandardBasicTypes.LONG)
                .addScalar("idContactWorker", StandardBasicTypes.LONG)
                .addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .addScalar("cdStage", StandardBasicTypes.STRING)
                .addScalar("dtContactOccurred", StandardBasicTypes.DATE)
                .addScalar("cdContactPurpose", StandardBasicTypes.STRING)
                .addScalar("dtContactApprv", StandardBasicTypes.DATE)
                .addScalar("cdContactType", StandardBasicTypes.STRING)
                .addScalar("cdContactLocation", StandardBasicTypes.STRING)
                .addScalar("cdContactMethod", StandardBasicTypes.STRING)
                .addScalar("cdContactOthers", StandardBasicTypes.STRING)
                .addScalar("indContactAttempted", StandardBasicTypes.STRING)
                .addScalar("narrativeBlob", StandardBasicTypes.BLOB)
                .addScalar("idTemplate", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(CciInvContactDto.class));
        List<CciInvContactDto> resultList = (List<CciInvContactDto>) query.list();

        if (!ObjectUtils.isEmpty(resultList)) {
            resultList.forEach(contactDto -> {
                if (!ObjectUtils.isEmpty(contactDto.getNarrativeBlob()))
                    try {
                        contactDto.setNarrative(TypeConvUtil.getNarrativeData(contactDto.getNarrativeBlob().getBinaryStream()));
                        contactDto.setNarrativeBlob(null);
                    } catch (SQLException e) {
                        DataLayerException dataLayerException = new DataLayerException(e.getMessage());
                        dataLayerException.initCause(e);
                        throw dataLayerException;
                    }
            });
        }

        return resultList;
    }

    @Override
    public List<CciInvReportPersonDto> getPrincCollatList(Long idEvent, Long idStage){
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrincCollatListSql)
                .addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdStagePersType", StandardBasicTypes.STRING)
                .addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
                .addScalar("cdStagePersRole",  StandardBasicTypes.STRING)
                .setParameter("idEvent", idEvent).setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(CciInvReportPersonDto.class));
        List<CciInvReportPersonDto> resultList = (List<CciInvReportPersonDto>) query.list();
        return resultList;
    }

    @Override
    public List<CciInvLetterDto> getLetterDetailList(Long idStage, Long idCase) {
        String querySql = getLetterDetailSql;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(querySql)
                .addScalar("dtCreated", StandardBasicTypes.DATE)
                .addScalar("letterType", StandardBasicTypes.STRING)
                .addScalar("letterMethod", StandardBasicTypes.STRING)
                .addScalar("letterTo", StandardBasicTypes.STRING)
                .addScalar("idLetter", StandardBasicTypes.LONG).setParameter("idStage", idStage)
                .setParameter("idCase",idCase)
                .setResultTransformer(Transformers.aliasToBean(CciInvLetterDto.class));
        List<CciInvLetterDto> resultList = (List<CciInvLetterDto>) query.list();
        return resultList;
    }
}
