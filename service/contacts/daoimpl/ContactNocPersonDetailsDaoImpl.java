package us.tx.state.dfps.service.contacts.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.ContactNarrative;
import us.tx.state.dfps.common.domain.ContactNocPerson;
import us.tx.state.dfps.service.contacts.dao.ContactNocPersonDetailsDao;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class ContactNocPersonDetailsDaoImpl implements ContactNocPersonDetailsDao {
    @Autowired
    SessionFactory sessionFactory;

    @Value("${ContactNocPersonDaoImpl.saveContactNocPerson}")
    private String saveContactNocPersonSql;


    @Override
    @Transactional
    public Long saveContactNocPersonDetail(ContactNocPerson contactNocPersons) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(saveContactNocPersonSql);
        query.setParameter("idCase", contactNocPersons.getIdCase());
        query.setParameter("eventId", contactNocPersons.getEventId());
        query.setParameter("cdStage", contactNocPersons.getCdStage());
        query.setParameter("cdStageProgram", contactNocPersons.getCdStageProgram());
        query.setParameter("cdStagePersRelInt",contactNocPersons.getCdStagePersRelInt());
        query.setParameter("nmStage", contactNocPersons.getNmStage());
        query.setParameter("nmPersonFull", contactNocPersons.getNmPersonFull());
        query.setParameter("indLanguage",contactNocPersons.getIndLanguage());
        query.setParameter("indDistMthd",contactNocPersons.getIndDistMthd());
        query.setParameter("txtEmail", contactNocPersons.getTxtEmail());
        query.setParameter("dtPurged", new Date());
        query.setParameter("dtLastUpdate", new Date());
        query.setParameter("idCreatedPerson",contactNocPersons.getIdCreatedPerson());

        Date createdDate = contactNocPersons.getDtCreated() != null ?  contactNocPersons.getDtCreated() : new Date() ;
        query.setParameter("dtCreated", createdDate);
        query.setParameter("idLastUpdatePerson",contactNocPersons.getIdLastUpdatePerson());

        int rowsInserted = query.executeUpdate();
        return (long) rowsInserted;
    }

    @Override
    public List<ContactNocPersonDetailDto> getAllNewPersonDetails(ContactNocPersonDetailDto contactNocPersons) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNocPerson.class);
        criteria.add(Restrictions.eq("idCase", contactNocPersons.getIdCase()));
        //criteria.add(Restrictions.eq("cdStage", contactNocPersons.getCdStage()));
        criteria.add(Restrictions.eq("eventId", contactNocPersons.getEventId()));

        List<ContactNocPerson> contactNocList = criteria.list();

        List<ContactNocPersonDetailDto> contactNocPersonList  = new ArrayList<>();
        for (ContactNocPerson contactNocPerson : contactNocList) {
            ContactNocPersonDetailDto cnpd = new ContactNocPersonDetailDto();

            cnpd.setIdContactNocPerson(contactNocPerson.getIdContactNocPersons());
            cnpd.setIdCase(contactNocPerson.getIdCase());
            cnpd.setCdStage(contactNocPerson.getCdStage());
            cnpd.setCdStageProgram(contactNocPerson.getCdStageProgram());
            cnpd.setCdStagePersRelInt(contactNocPerson.getCdStagePersRelInt());
            cnpd.setNmStage(contactNocPerson.getNmStage());
            cnpd.setNmPersonFull(contactNocPerson.getNmPersonFull());
            cnpd.setIndLanguage(contactNocPerson.getIndLanguage());
            cnpd.setIndDistMthd(contactNocPerson.getIndDistMthd());
            cnpd.setTxtEmail(contactNocPerson.getTxtEmail());
            cnpd.setDtPurged(contactNocPerson.getDtPurged());

            contactNocPersonList.add(cnpd);
        }

        return contactNocPersonList;
    }

    @Override
    public List<ContactNocPersonDetailDto> getAllNewPersonBasedOnEventId(long eventId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNocPerson.class);

        criteria.add(Restrictions.eq("eventId",eventId));
        List<ContactNocPerson> contactNocList = criteria.list();

        List<ContactNocPersonDetailDto> contactNocPersonList  = new ArrayList<>();
        for (ContactNocPerson contactNocPerson : contactNocList) {
            ContactNocPersonDetailDto cnpd = new ContactNocPersonDetailDto();

            cnpd.setIdContactNocPerson(contactNocPerson.getIdContactNocPersons());
            cnpd.setIdCase(contactNocPerson.getIdCase());
            cnpd.setCdStage(contactNocPerson.getCdStage());
            cnpd.setCdStageProgram(contactNocPerson.getCdStageProgram());
            cnpd.setCdStagePersRelInt(contactNocPerson.getCdStagePersRelInt());
            cnpd.setNmStage(contactNocPerson.getNmStage());
            cnpd.setNmPersonFull(contactNocPerson.getNmPersonFull());
            cnpd.setIndLanguage(contactNocPerson.getIndLanguage());
            cnpd.setIndDistMthd(contactNocPerson.getIndDistMthd());
            cnpd.setTxtEmail(contactNocPerson.getTxtEmail());
            cnpd.setDtPurged(contactNocPerson.getDtPurged());

            contactNocPersonList.add(cnpd);
        }

        return contactNocPersonList;
    }

    @Override
    public ContactNocPerson getContactNocPersonDetailById(long id) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNocPerson.class);
        criteria.add(Restrictions.eq("idContactNocPersons", id));

       return (ContactNocPerson) criteria.uniqueResult();
    }

}
