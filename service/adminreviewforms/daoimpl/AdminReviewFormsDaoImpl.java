package us.tx.state.dfps.service.adminreviewforms.daoimpl;

import org.apache.log4j.Logger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.adminreviewforms.dao.AdminReviewFormsDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;


@Repository
public class AdminReviewFormsDaoImpl implements AdminReviewFormsDao {

    private static final Logger logger = Logger.getLogger(AdminReviewFormsDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${AdminReviewFormsDaoImpl.getAdminReviewByEventId}")
    private String getAdminReviewSqlbyEventId;

//
//    @Override
//   public AdminReviewDto getNotificationRequest(Long id) {
////        AdminReviewDto adminReviewDto = (AdminReviewDto) ((SQLQuery) sessionFactory.getCurrentSession()
////                .createSQLQuery(getAdminReviewByEventIDSql).setParameter("idEvent", id))
////                .setResultTransformer(Transformers.aliasToBean(AdminReviewDto.class)).list().get(0);
//        return adminReviewDto;
////    }

    @Override
    public AdminReviewDto getNotificationRequest(Long id) {
        return null;
    }

    //CSE65D
    @Override
    public AdminReviewDto getAdminReviewByEventId(Long idEvent) {
        AdminReviewDto adminReviewDto = null;
        Query adminReviewDtoQuery = sessionFactory.getCurrentSession().createSQLQuery(getAdminReviewSqlbyEventId)
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idStageRelated", StandardBasicTypes.LONG)
                .addScalar("cdAdminRvAppealResult", StandardBasicTypes.STRING)
                .addScalar("cdAdminRvAppealType", StandardBasicTypes.STRING)
                .addScalar("cdAdminRvAuth", StandardBasicTypes.STRING)
                .addScalar("cdAdminRvStatus", StandardBasicTypes.STRING)
                .addScalar("dtAdminRvAppealNotif", StandardBasicTypes.DATE)
                .addScalar("dtAdminRvAppealReview", StandardBasicTypes.DATE)
                .addScalar("dtAdminRvDue", StandardBasicTypes.DATE)
                .addScalar("dtAdminRvEmgcyRel", StandardBasicTypes.DATE)
                .addScalar("dtAdminRvHearing", StandardBasicTypes.DATE)
                .addScalar("dtAdminRvReqAppeal", StandardBasicTypes.DATE)
                .addScalar("cdAdminRvReqBy", StandardBasicTypes.STRING)
                .addScalar("nmAdminRvReqBy", StandardBasicTypes.STRING)
                .addScalar("indAdminRvEmgcyRel", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(AdminReviewDto.class));
        try {
            adminReviewDto = (AdminReviewDto) adminReviewDtoQuery.list().get(0);

        } catch (DataNotFoundException | DataLayerException e) {

        }

        return adminReviewDto;

    }

}
