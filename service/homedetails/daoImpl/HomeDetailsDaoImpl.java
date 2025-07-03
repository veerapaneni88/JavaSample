package us.tx.state.dfps.service.homedetails.daoImpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.homedetails.dao.HomeDetailsDao;
import us.tx.state.dfps.service.homedetails.dto.ChildrenPlacementInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

@Repository
public class HomeDetailsDaoImpl implements HomeDetailsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${HomeDetailsDaoImpl.getHomeInfo}")
    private String getHomeInfoSql;

    @Value("${HomeDetailsDaoImpl.getHouseHoldMembers}")
    private String getHouseHoldMembersSql;

    @Value("${HomeDetailsDaoImpl.getCurrentPlacedChildren}")
    private String getChildrenSql;

    @Override
    public HomeInfoDto getHomeInfoDto(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getHomeInfoSql)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                .addScalar("streetLine1", StandardBasicTypes.STRING)
                .addScalar("streetLine2", StandardBasicTypes.STRING)
                .addScalar("county", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("zipCode", StandardBasicTypes.STRING)
                .addScalar("homeCategory", StandardBasicTypes.STRING)
                .addScalar("homeType1", StandardBasicTypes.STRING)
                .addScalar("homeType2", StandardBasicTypes.STRING)
                .addScalar("homeType3", StandardBasicTypes.STRING)
                .addScalar("homeType4", StandardBasicTypes.STRING)
                .addScalar("homeType5", StandardBasicTypes.STRING)
                .addScalar("homeType6", StandardBasicTypes.STRING)
                .addScalar("homeType7", StandardBasicTypes.STRING)
                .addScalar("maritalStatus", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(HomeInfoDto.class));
        return (HomeInfoDto) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HomeInfoDto> getHomeInfoDtoList(Long idStage) {
        return (List<HomeInfoDto>) sessionFactory.getCurrentSession().createSQLQuery(getHomeInfoSql)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                .addScalar("streetLine1", StandardBasicTypes.STRING)
                .addScalar("streetLine2", StandardBasicTypes.STRING)
                .addScalar("county", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("zipCode", StandardBasicTypes.STRING)
                .addScalar("homeCategory", StandardBasicTypes.STRING)
                .addScalar("homeType1", StandardBasicTypes.STRING)
                .addScalar("homeType2", StandardBasicTypes.STRING)
                .addScalar("homeType3", StandardBasicTypes.STRING)
                .addScalar("homeType4", StandardBasicTypes.STRING)
                .addScalar("homeType5", StandardBasicTypes.STRING)
                .addScalar("homeType6", StandardBasicTypes.STRING)
                .addScalar("homeType7", StandardBasicTypes.STRING)
                .addScalar("maritalStatus", StandardBasicTypes.STRING)
                .addScalar("phoneNumber", StandardBasicTypes.STRING)
                .addScalar("annualIncome", StandardBasicTypes.STRING)
                .addScalar("phoneNumberExtension", StandardBasicTypes.STRING)
                .addScalar("dateMarried",StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(HomeInfoDto.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HouseHoldMembersDto> getHouseHoldMembers(Long idStage) {
        return (List<HouseHoldMembersDto>) sessionFactory.getCurrentSession()
                .createSQLQuery(getHouseHoldMembersSql)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("nameSuffix", StandardBasicTypes.STRING)
                .addScalar("roleInHome", StandardBasicTypes.STRING)
                .addScalar("dateOfBirth", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setParameter("maxDate", ServiceConstants.MAX_DATE)
                .setResultTransformer(Transformers.aliasToBean(HouseHoldMembersDto.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ChildrenPlacementInfoDto> getChildrenInfo(Long idResource) {
        return (List<ChildrenPlacementInfoDto>) sessionFactory.getCurrentSession()
                .createSQLQuery(getChildrenSql)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("nameSuffix", StandardBasicTypes.STRING)
                .addScalar("livingArrangement", StandardBasicTypes.STRING)
                .addScalar("dateOfBirth", StandardBasicTypes.DATE)
                .addScalar("placementDate", StandardBasicTypes.DATE)
                .addScalar("authorizedLoc", StandardBasicTypes.STRING)
                .addScalar("authorizedPlocStartDate", StandardBasicTypes.DATE)
                .setParameter("idResource", idResource)
                .setParameter("maxDate", ServiceConstants.MAX_DATE)
                .setResultTransformer(Transformers.aliasToBean(ChildrenPlacementInfoDto.class)).list();
    }
}
