package us.tx.state.dfps.service.seriousincident.daoImpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.seriousincident.dao.SeriousIncidentDocumentDao;
import us.tx.state.dfps.service.homedetails.dto.ChildrenPlacementInfoDto;
import us.tx.state.dfps.service.seriousincidentdocument.dto.HomeApprovalEventInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

@Repository
public class SeriousIncidentDocumentDaoImpl implements SeriousIncidentDocumentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${SeriousIncidentDocumentDaoImpl.getHomeApprovalEventInfo}")
    private String getHomeApprovalEventInfoSql;

    @Override
    public HomeApprovalEventInfoDto getHomeApprovalEventInfo(Long idStage) {
        Query query = sessionFactory.getCurrentSession()
                .createSQLQuery(getHomeApprovalEventInfoSql)
                .addScalar("preferredCapacity", StandardBasicTypes.INTEGER)
                .addScalar("dtEventOccured", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(HomeApprovalEventInfoDto.class));
        return (HomeApprovalEventInfoDto)  query.uniqueResult();
    }
}
