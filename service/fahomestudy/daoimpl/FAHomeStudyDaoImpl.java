package us.tx.state.dfps.service.fahomestudy.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.fahomestudy.dao.FAHomeStudyDao;
import us.tx.state.dfps.service.fahomestudy.dto.InquiryDateDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

@Repository
public class FAHomeStudyDaoImpl implements FAHomeStudyDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${FAHomeStudyDaoImpl.getHouseHoldMembersInfo}")
    private String getHouseHoleMembersInfoSql;

    @Value("${FAHomeStudyDaoImpl.getInquiryDate}")
    private String getInquiryDateSql;

    @Override
    public List<HouseHoldMembersDto> getHouseHoldMembersInfo(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getHouseHoleMembersInfoSql)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("personType", StandardBasicTypes.STRING)
                .addScalar("personSex", StandardBasicTypes.STRING)
                .addScalar("dateOfBirth", StandardBasicTypes.DATE)
                .addScalar("occupation", StandardBasicTypes.STRING)
                .addScalar("language", StandardBasicTypes.STRING)
                .addScalar("ethinicity", StandardBasicTypes.STRING)
                .addScalar("education", StandardBasicTypes.STRING)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("nameSuffix", StandardBasicTypes.STRING)
                .addScalar("roleInHome", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(HouseHoldMembersDto.class));
        return query.list();
    }

    @Override
    public InquiryDateDto getInquiryDate(Long idStage) {
        InquiryDateDto inquiryDateDto = null;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getInquiryDateSql)
                .addScalar("resourceHistoryId", StandardBasicTypes.LONG)
                .addScalar("resourceId", StandardBasicTypes.LONG)
                .addScalar("effectiveDate", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(InquiryDateDto.class));
        List<InquiryDateDto> inquiryDateDtoList = query.list();
        if (!ObjectUtils.isEmpty(inquiryDateDtoList)) {
            inquiryDateDto = inquiryDateDtoList.get(0);
        }
        return inquiryDateDto;
    }
}
