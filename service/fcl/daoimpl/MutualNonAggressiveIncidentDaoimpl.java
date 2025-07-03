package us.tx.state.dfps.service.fcl.daoimpl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ChildSxMutalIncdnt;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.fcl.dao.MutualNonAggressiveIncidentDao;
import us.tx.state.dfps.web.fcl.dto.MutualNonAggressiveIncidentDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

import java.util.Date;
import java.util.List;

/**
 *service-business- FCL
 *Class Description:<DaoImpl class for Sexual Mutual Non Aggressive Incidents>
 *
 * There is no longer a way to create, update, or delete Mutual Non-Aggressive Incidents, but MutualNonAggressiveIncidentDao
 * is still used during person merge to handle any Mutual non-Aggressive incidents that were created in the brief window
 * where the functionality was live. We expect this functionality to be needed in the future, and so
 * we expect this code to come into full use in the future.
 *
 */
@Repository
public class MutualNonAggressiveIncidentDaoimpl  implements MutualNonAggressiveIncidentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<ChildSxMutalIncdnt> getMutualNonAggressiveIncidents(Long idPerson) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSxMutalIncdnt.class);
        criteria.add(Restrictions.eq("person.idPerson", idPerson)).addOrder(Order.desc("dtIncident"));
        return (List<ChildSxMutalIncdnt>) criteria.list();
    }

    @Override
    public List<ChildSxMutalIncdnt> getIdPersonMutualIncidents(Long idPersonMutual) {
        return (List<ChildSxMutalIncdnt>) sessionFactory.getCurrentSession().createCriteria(ChildSxMutalIncdnt.class)
                .add(Restrictions.eq("personMutual.idPerson", idPersonMutual))
                .list();
    }

    @Override
    public void saveMutualNonAggressiveIncidents(SexualVictimHistoryDto sexualVictimHistoryDto) {
        if (CollectionUtils.isNotEmpty(sexualVictimHistoryDto.getConsensualIncidents())){
            for (MutualNonAggressiveIncidentDto incidentDto : sexualVictimHistoryDto.getConsensualIncidents()) {
                ChildSxMutalIncdnt incident;
                Date date = new Date();
                Boolean saveIncident = false;
                if (ObjectUtils.isEmpty(incidentDto.getIdIncident()) || incidentDto.getIdIncident().equals(ServiceConstants.ZERO)) {
                    incident = new ChildSxMutalIncdnt();
                    incident.setDtCreated(date);
                    incident.setIdCreatedPerson(incidentDto.getIdCreatedBy());
                    Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, incidentDto.getIdPerson());
                    incident.setPerson(person);
                    saveIncident = true;
                } else {
                    incident = (ChildSxMutalIncdnt) sessionFactory.getCurrentSession().createCriteria(ChildSxMutalIncdnt.class).add(Restrictions.eq("idChildSxMutualIncdnt", incidentDto.getIdIncident())).uniqueResult();
                    incidentDto.setIndApproxDate(ServiceConstants.Y.equals(incidentDto.getIndApproxDate()) ? ServiceConstants.Y : ServiceConstants.N);
                    saveIncident = hasModifiedIncident(incident, incidentDto);
                }
                if (saveIncident) {
                    incident.setDtIncident(incidentDto.getDtIncident());
                    incident.setDtLastUpdate(date);
                    incident.setIdLastUpdatePerson(sexualVictimHistoryDto.getIdLastUpdatedPerson());
                    incident.setIndApproxDate(ServiceConstants.Y.equals(incidentDto.getIndApproxDate()) ? ServiceConstants.Y : ServiceConstants.N);
                    incident.setTxtMutualIncdntDesc(formatString(incidentDto.getAllPertinentInfoDesc()));
                    incident.setCdMutualCtgy(incidentDto.getCdMutualCategory());
                    incident.setIndChildInCare(incidentDto.getIndChildInCare());
                    incident.setIdPlcmtEvent(incidentDto.getIdPlacement());
                    if (incidentDto.getIdPersonOther() != null) {
                        Person personMutual = (Person) sessionFactory.getCurrentSession().load(Person.class, incidentDto.getIdPersonOther());
                        incident.setPersonMutual(personMutual);
                    } else {
                        incident.setPersonMutual(null);
                    }
                    sessionFactory.getCurrentSession().saveOrUpdate(incident);
                }
            }
        }
    }

    @Override
    public void deleteMutualNonAggressiveIncidents(List<Long> incidentIds, Long idUser) {
        for (Long idIncident : incidentIds) {
            if (!ObjectUtils.isEmpty(idIncident) && !ServiceConstants.ZERO.equals(idIncident)) {
                ChildSxMutalIncdnt incident = (ChildSxMutalIncdnt) sessionFactory.getCurrentSession().createCriteria(ChildSxMutalIncdnt.class).add(Restrictions.eq("idChildSxMutualIncdnt", idIncident)).uniqueResult();
                if (!ObjectUtils.isEmpty(incident)) {
                    incident.setIdLastUpdatePerson(idUser);
                    incident.setDtLastUpdate(new Date());
                    sessionFactory.getCurrentSession().saveOrUpdate(incident);
                    sessionFactory.getCurrentSession().delete(incident);
                }
            }
        }
    }

    private boolean hasModifiedIncident(ChildSxMutalIncdnt incident, MutualNonAggressiveIncidentDto incidentDto) {
        return !incident.getDtIncident().equals(incidentDto.getDtIncident())
                || !incident.getTxtMutualIncdntDesc().equals(incidentDto.getAllPertinentInfoDesc())
                || !incident.getIndApproxDate().equals(incidentDto.getIndApproxDate())
                || !incidentDto.getIndChildInCare().equalsIgnoreCase(incident.getIndChildInCare())
                || isPlcmtEventChanged(incident.getIdPlcmtEvent(), incidentDto.getIdPlacement())
                || ObjectUtils.nullSafeEquals(incident.getCdMutualCtgy(), incidentDto.getCdMutualCategory())
                || isPersonMutualChanged(incident.getPersonMutual(), incidentDto);
    }

    private boolean isPlcmtEventChanged(Long dbPlcmtEvent, Long newPlcmtEvent) {
        if (ObjectUtils.isEmpty(dbPlcmtEvent) && ObjectUtils.isEmpty(newPlcmtEvent)) {
            return false;
        }
        if (ObjectUtils.isEmpty(dbPlcmtEvent) || ObjectUtils.isEmpty(newPlcmtEvent)) {
            return true;
        }
        return !dbPlcmtEvent.equals(newPlcmtEvent);
    }

    // helper function to make the or statement that calls this pretty.
    private boolean isPersonMutualChanged(Person dbVersion, MutualNonAggressiveIncidentDto userVersion) {
        if (dbVersion == null) {
            if (userVersion.getIdPersonOther() == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return dbVersion.getIdPerson().equals(userVersion.getIdPersonOther());
        }
    }

    private String formatString(String s) {
        if (!ObjectUtils.isEmpty(s) && s.length() > 4000)
            s = s.substring(0, 4000);
        return s;
    }
}
