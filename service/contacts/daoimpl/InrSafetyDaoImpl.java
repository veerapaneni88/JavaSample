package us.tx.state.dfps.service.contacts.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.InrDuplicateGroupingLink;
import us.tx.state.dfps.common.domain.InrSafetyFollowup;
import us.tx.state.dfps.service.admin.dto.ContactDetailSaveDiDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.InrSafetyDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventPersonRetrvRowOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.InrSafetyFieldDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


@Repository

public class InrSafetyDaoImpl implements InrSafetyDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${InrSafetyDaoImpl.getFollowUpSql}")
    private String getFollowUpSql;

    @Autowired
    MessageSource messageSource;


    /**
     * Method Name: getFollowUp Method Description:This populates a number
     * of fields about the contact. If it gets a SQL_NOT_FOUND error, it will
     * throw it.
     *
     * @param contactFieldDiDto
     * @return InrSafetyFieldDto
     */
    @Override
    public List<InrSafetyFieldDto> getFollowUpList(ContactFieldDiDto contactFieldDiDto) {
        SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFollowUpSql)
                .addScalar("idInrSafetyFollowup", StandardBasicTypes.LONG)
                .addScalar("idInrGroup", StandardBasicTypes.LONG)
                .addScalar("dtFollowUp", StandardBasicTypes.TIMESTAMP)
                .addScalar("txtPlansFutureActns", StandardBasicTypes.STRING)
                .addScalar("txtSafetyActionItem", StandardBasicTypes.STRING)
                .addScalar("dtSafetyActionDue", StandardBasicTypes.DATE)
                .addScalar("indFollowup", StandardBasicTypes.STRING)
                .addScalar("dtCreated", StandardBasicTypes.DATE)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("dtPurged", StandardBasicTypes.TIMESTAMP)
                .setParameter("groupNum", contactFieldDiDto.getGroupNum())
                .setResultTransformer(Transformers.aliasToBean(InrSafetyFieldDto.class));


        List<InrSafetyFieldDto> inrFieldDoDtoList = (List<InrSafetyFieldDto>) sQLQuery.list();
        return inrFieldDoDtoList;
    }

    // updateFollowupDetails is robust. it handles added rows, deleted rows, and updated rows.
    @Override
    public void updateFollowupDetails(ContactDetailSaveDiDto contactDetailSaveDiDto) {
        if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto)) {
            Long nbrGrpNum = contactDetailSaveDiDto.getGroupNum();
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrSafetyFollowup.class);
            criteria.add(Restrictions.eq("idInrGroup", nbrGrpNum));
            List<InrSafetyFollowup> inrSafetyFollowupList = criteria.list();

            Map<Long, InrSafetyFieldDto> inrSafetyActionIdToSafetyAction = new HashMap<>();
            if(!CollectionUtils.isEmpty(contactDetailSaveDiDto.getInrSafetyFieldDtoList())){
                for (InrSafetyFieldDto currParamObject : contactDetailSaveDiDto.getInrSafetyFieldDtoList()) {
                    // skip empty parameter objects
                    if(currParamObject.getDtSafetyActionDue() != null) {
                        if (currParamObject.getIdInrSafetyFollowup() != null) {
                            inrSafetyActionIdToSafetyAction.put(currParamObject.getIdInrSafetyFollowup(), currParamObject);
                        }
                    }
                }
            }

            // handle changes to existing rows
            if(!CollectionUtils.isEmpty(inrSafetyFollowupList)) {
                for (InrSafetyFollowup inrSafetyFollowup : inrSafetyFollowupList) {
                    InrSafetyFieldDto paramObjectfromUser = inrSafetyActionIdToSafetyAction.get(inrSafetyFollowup.getIdInrSafetyFollowup());

                    if (paramObjectfromUser != null) {
                        // SAFETY ACTION ITEMS
                        inrSafetyFollowup.setTxtSafetyActionItem(paramObjectfromUser.getTxtSafetyActionItem());
                        inrSafetyFollowup.setDtSafetyActionDue(paramObjectfromUser.getDtSafetyActionDue());
                        inrSafetyFollowup.setIndFollowup(paramObjectfromUser.getIndFollowup());
                        // FOLLOW UP ITEMS
                        if (!StringUtils.isEmpty(paramObjectfromUser.getTxtPlansFutureActns())) {
                            inrSafetyFollowup.setTxtPlansFutureActns(paramObjectfromUser.getTxtPlansFutureActns());
                        }
                        if (!StringUtils.isEmpty(paramObjectfromUser.getDtFollowUp())) {
                            inrSafetyFollowup.setDtFollowUp(paramObjectfromUser.getDtFollowUp());
                        }
                        // AUDIT ITEMS
                        inrSafetyFollowup.setIdLastUpdatePerson(contactDetailSaveDiDto.getIdLastEmpUpdate());
                        sessionFactory.getCurrentSession().saveOrUpdate(inrSafetyFollowup);
                    } else {
                        Criteria deleteCriteria = sessionFactory.getCurrentSession().createCriteria(InrSafetyFollowup.class);
                        deleteCriteria.add(Restrictions.eq("idInrSafetyFollowup", inrSafetyFollowup.getIdInrSafetyFollowup()));
                        InrSafetyFollowup safetyActionObjToDelete = (InrSafetyFollowup) deleteCriteria.uniqueResult();
                        sessionFactory.getCurrentSession().delete(safetyActionObjToDelete);
                    }
                }
            }

            // handle added rows
            if(!CollectionUtils.isEmpty(contactDetailSaveDiDto.getInrSafetyFieldDtoList())) {
                for (InrSafetyFieldDto currParamObject : contactDetailSaveDiDto.getInrSafetyFieldDtoList()) {
                    if (currParamObject.getIdInrSafetyFollowup() == null && currParamObject.getDtSafetyActionDue() != null) {
                        saveFollowupDetails(currParamObject, contactDetailSaveDiDto.getGroupNum(), contactDetailSaveDiDto.getIdLastEmpUpdate());
                    }
                }
            }
        }
    }


    /**
     * Method Name: saveFollowup Method Description:save Contact Table
     *
     * @param
     */
    private void saveFollowupDetails(InrSafetyFieldDto inrSafetyFieldDto, Long groupNum, Long userId) {
        InrSafetyFollowup inrSafetyFollowup = new InrSafetyFollowup();
        inrSafetyFollowup.setIdInrGroup(groupNum);
        // SAFETY ACTION ITEMS
        inrSafetyFollowup.setTxtSafetyActionItem(inrSafetyFieldDto.getTxtSafetyActionItem());
        inrSafetyFollowup.setDtSafetyActionDue(inrSafetyFieldDto.getDtSafetyActionDue());
        inrSafetyFollowup.setIndFollowup(inrSafetyFieldDto.getIndFollowup());
        // FOLLOW UP ITEMS
        if (!StringUtils.isEmpty(inrSafetyFieldDto.getTxtPlansFutureActns())) {
            inrSafetyFollowup.setTxtPlansFutureActns(inrSafetyFieldDto.getTxtPlansFutureActns());
        }
        if (!StringUtils.isEmpty(inrSafetyFieldDto.getDtFollowUp())) {
            inrSafetyFollowup.setDtFollowUp(inrSafetyFieldDto.getDtFollowUp());
        }
        // AUDIT ITEMS
        inrSafetyFollowup.setIdCreatedPerson(userId);
        inrSafetyFollowup.setIdLastUpdatePerson(userId);
        sessionFactory.getCurrentSession().save(inrSafetyFollowup);
    }

    @Override
    public void deleteActionItemsByGroup(Long groupNum) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrSafetyFollowup.class);
        criteria.add(Restrictions.eq("idInrGroup", groupNum));
        List<InrSafetyFollowup> inrSafetyFollowupList = criteria.list();

        if(!CollectionUtils.isEmpty(inrSafetyFollowupList)) {
            for (InrSafetyFollowup inrSafetyFollowup : inrSafetyFollowupList) {
                sessionFactory.getCurrentSession().delete(inrSafetyFollowup);
            }
        }
    }

}
