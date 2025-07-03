package us.tx.state.dfps.service.subcare.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.BatchParameters;
import us.tx.state.dfps.service.subcare.dao.BatchParametersDao;

@Repository
@SuppressWarnings("unchecked")
public class BatchParametersDaoImpl implements BatchParametersDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public BatchParameters getBatchParameters(String program, String parameter) {

        return (BatchParameters) sessionFactory.getCurrentSession().createCriteria(BatchParameters.class)
            .add(Restrictions.eq("id.nmBatchProgram", program))
            .add(Restrictions.eq("id.nmBatchParameter", parameter))
            .uniqueResult();
    }
}
