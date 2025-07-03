package us.tx.state.dfps.service.person.daoimpl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.PersonRaceMps;
import us.tx.state.dfps.service.person.dao.MPSPersonDetailDao;

@Repository
public class MPSPersonDetailDaoImpl implements MPSPersonDetailDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public IncomingPersonMps fetchIncomingPersonById(Long idIncomingPersonMps) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingPersonMps.class);
        criteria.add(Restrictions.eq("idIncomingPersonMps", idIncomingPersonMps));
        IncomingPersonMps incomingPersonMps = (IncomingPersonMps) criteria.uniqueResult();
        return incomingPersonMps;
    }

    @Override
    public List<PersonRaceMps> fetchPersonRaceMPSByIncomingPersonId(Long idIncomingPersonMps) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRaceMps.class);
        criteria.add(Restrictions.eq("incomingPersonMps.idIncomingPersonMps", idIncomingPersonMps));
        List<PersonRaceMps> personRaceMpsList =  criteria.list();
        return personRaceMpsList;
    }

    @Override
    public Long saveIncomingPersonDetails(IncomingPersonMps incomingPersonMps) {
        Long id;
        if (0L != incomingPersonMps.getIdIncomingPersonMps()) {
            sessionFactory.getCurrentSession().saveOrUpdate(incomingPersonMps);
            id = incomingPersonMps.getIdIncomingPersonMps();
        } else {
            id = (Long) sessionFactory.getCurrentSession().save(incomingPersonMps);
        }
        return id;
    }

    @Override
    public void savePersonRaceMps(PersonRaceMps personRaceMps) {

        sessionFactory.getCurrentSession().save(personRaceMps);
    }

    @Override
    public void deletePersonRaceMps(Long idIncomingPersonMps) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRaceMps.class);
        criteria.add(Restrictions.eq("incomingPersonMps.idIncomingPersonMps", idIncomingPersonMps));
        List<PersonRaceMps> personRaceMpsList = criteria.list();
        if(CollectionUtils.isNotEmpty(personRaceMpsList)){
            for(PersonRaceMps personRaceMps:personRaceMpsList) {
                sessionFactory.getCurrentSession().delete(personRaceMps);
            }
        }
    }

    @Override
    public void deleteIncomingPersonDetails(Long idIncomingPersonMps) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingPersonMps.class);
        IncomingPersonMps incomingPersonMps = (IncomingPersonMps) criteria.add(Restrictions.eq("idIncomingPersonMps", idIncomingPersonMps)).uniqueResult();
        sessionFactory.getCurrentSession().delete(incomingPersonMps);
    }


}
