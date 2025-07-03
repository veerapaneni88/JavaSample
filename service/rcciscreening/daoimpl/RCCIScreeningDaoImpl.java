package us.tx.state.dfps.service.rcciscreening.daoimpl;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.dto.RCCIScreeningDto;
import us.tx.state.dfps.service.rcciscreening.dao.RCCIScreeningDao;

@Repository
public class RCCIScreeningDaoImpl implements RCCIScreeningDao {

    @Value("${RCCIScreeningDaoImpl.getIntakeData}")
    private String getIntakeDataSql;

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public RCCIScreeningDto getIntakeData(long idCase) {
        return (RCCIScreeningDto) sessionFactory.getCurrentSession().createSQLQuery(getIntakeDataSql)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("intakeId", StandardBasicTypes.LONG)
                .addScalar("incomingDate", StandardBasicTypes.DATE)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(RCCIScreeningDto.class)).uniqueResult();
    }
}
