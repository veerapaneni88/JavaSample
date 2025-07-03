package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ProfAssmtNarr;
import us.tx.state.dfps.common.domain.ProfessionalAssmt;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.investigation.dao.MedicalMentalAssessmentDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 15, 2017- 6:26:03 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class MedicalMentalAssessmentDaoImpl implements MedicalMentalAssessmentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    MessageSource messageSource;

    public MedicalMentalAssessmentDaoImpl() {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * us.tx.state.dfps.service.investigation.dao.MedicalMentalAssessmentDao#
     * medicalAssessmentAUD(us.tx.state.dfps.service.casepackage.dto.
     * MdclMentalAssmntDtlDto)
     */
    @Override
    public Long medicalAssessmentAUD(MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto, String action) {
        Long result = 0L;
        if (ServiceConstants.REQ_FUNC_CD_ADD.equals(action)) {
            ProfessionalAssmt profassessment = new ProfessionalAssmt();
            Person person = new Person();
            Event event = new Event();
            event.setIdEvent(mdclMentalAssmntDtlDto.getIdEvent());
            profassessment.setEvent(event);
            profassessment.setIdPersonPrincipal(mdclMentalAssmntDtlDto.getIdPersonPrincipal());

            profassessment.setIdEvent(mdclMentalAssmntDtlDto.getIdEvent());
            if (null == mdclMentalAssmntDtlDto.getIdPersonProfessional()
                    || 0L == mdclMentalAssmntDtlDto.getIdPersonProfessional()) {
                profassessment.setPerson(null);
            } else {
                person.setIdPerson(mdclMentalAssmntDtlDto.getIdPersonProfessional());
                profassessment.setPerson(person);
            }
            profassessment.setNmProfAssmtName(mdclMentalAssmntDtlDto.getNameProfAssmtName());
            profassessment.setNmProfAssmtPrincipal(mdclMentalAssmntDtlDto.getNameProfAssmtPrincipal());
            profassessment.setCdProfAssmtApptRsn(mdclMentalAssmntDtlDto.getCdProfAssmtApptRsn());
            profassessment.setTxtProfAssmtFindings(mdclMentalAssmntDtlDto.getProfAssmtFindings());
            profassessment.setTxtProfAssmtOther(mdclMentalAssmntDtlDto.getProfAssmtOther());
            profassessment.setTxtProfAssmtCmnts(mdclMentalAssmntDtlDto.getCommentsOther());
            if (null == mdclMentalAssmntDtlDto.getDtProfAssmtAppt()) {
                profassessment.setDtProfAssmtAppt(null);
            } else {
                profassessment.setDtProfAssmtAppt(mdclMentalAssmntDtlDto.getDtProfAssmtAppt());
            }
            profassessment.setAddrProfAssmtCity(mdclMentalAssmntDtlDto.getCityOther());
            profassessment.setAddrProfAssmtStLn1(mdclMentalAssmntDtlDto.getAddressLine1Other());
            profassessment.setAddrProfAssmtStLn2(mdclMentalAssmntDtlDto.getAddressLine2Other());
            profassessment.setAddrProfAssmtZip(mdclMentalAssmntDtlDto.getZipOther());
            profassessment.setCdProfAssmtCounty(mdclMentalAssmntDtlDto.getCountyOther());
            profassessment.setCdProfAssmtState(mdclMentalAssmntDtlDto.getStateOther());
            profassessment.setNbrProfAssmtPhone(mdclMentalAssmntDtlDto.getNumberOther());
            profassessment.setNbrProfAssmtPhoneExt(mdclMentalAssmntDtlDto.getExtensionOther());
            profassessment.setCdApptPurpose(mdclMentalAssmntDtlDto.getCdApptPurpose());
            profassessment.setDtLastUpdate(new Date());
            if (!ObjectUtils.isEmpty(mdclMentalAssmntDtlDto.getDtApptEnd())) {
                profassessment.setDtApptEnd(null);
            } else {
                profassessment.setDtApptEnd(mdclMentalAssmntDtlDto.getDtApptEnd());
            }
            // added for artf227810
            profassessment.setIndResultsPending(mdclMentalAssmntDtlDto.getIndResultsPending());
            // added for artf227810
            result = (Long) sessionFactory.getCurrentSession().save(profassessment);
        } else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(action)) {

            Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class,
                    mdclMentalAssmntDtlDto.getIdStage());
            if (!ObjectUtils.isEmpty(stage.getDtStageClose())) {
                throw new DataLayerException(ServiceConstants.MSG_SYS_STAGE_CLOSED);
            } else {
                ProfessionalAssmt profassessment = getProfessionalAssmt(mdclMentalAssmntDtlDto.getIdEvent());
                //Defect#13718- Modified code to get the person object by passing the idpersonprofessional from the professional assessment
                if (!ObjectUtils.isEmpty(profassessment)) {
                    if (ObjectUtils.isEmpty(mdclMentalAssmntDtlDto.getIdPersonProfessional())
                            || 0L == mdclMentalAssmntDtlDto.getIdPersonProfessional()) {
                        profassessment.setPerson(null);
                    } else {
                        Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
                                mdclMentalAssmntDtlDto.getIdPersonProfessional());
                        profassessment.setPerson(person);
                    }
                    profassessment.setNmProfAssmtName(mdclMentalAssmntDtlDto.getNameProfAssmtName());
                    profassessment.setNmProfAssmtPrincipal(mdclMentalAssmntDtlDto.getNameProfAssmtPrincipal());
                    profassessment.setCdProfAssmtApptRsn(mdclMentalAssmntDtlDto.getCdProfAssmtApptRsn());
                    profassessment.setTxtProfAssmtFindings(mdclMentalAssmntDtlDto.getProfAssmtFindings());
                    profassessment.setTxtProfAssmtOther(mdclMentalAssmntDtlDto.getProfAssmtOther());
                    profassessment.setTxtProfAssmtCmnts(mdclMentalAssmntDtlDto.getCommentsOther());
                    if (null == mdclMentalAssmntDtlDto.getDtProfAssmtAppt()) {
                        profassessment.setDtProfAssmtAppt(null);
                    } else {
                        profassessment.setDtProfAssmtAppt(mdclMentalAssmntDtlDto.getDtProfAssmtAppt());
                    }
                    profassessment.setAddrProfAssmtCity(mdclMentalAssmntDtlDto.getAddrProfAssmtCity());
                    profassessment.setAddrProfAssmtStLn1(mdclMentalAssmntDtlDto.getAddrProfAssmtStLn1());
                    profassessment.setAddrProfAssmtStLn2(mdclMentalAssmntDtlDto.getAddrProfAssmtStLn2());
                    profassessment.setAddrProfAssmtZip(mdclMentalAssmntDtlDto.getAddrProfAssmtZip());
                    profassessment.setCdProfAssmtCounty(mdclMentalAssmntDtlDto.getCdProfAssmtCounty());
                    profassessment.setCdProfAssmtState(mdclMentalAssmntDtlDto.getAddrProfAssmtState());
                    profassessment.setNbrProfAssmtPhone(mdclMentalAssmntDtlDto.getPhone());
                    profassessment.setNbrProfAssmtPhoneExt(mdclMentalAssmntDtlDto.getPhoneExtension());
                    profassessment.setTxtProfAssmtCmnts(mdclMentalAssmntDtlDto.getProfAssmtFindings());
                    profassessment.setCdApptPurpose(mdclMentalAssmntDtlDto.getPurposeSelected());
                    profassessment.setAddrProfAssmtCity(mdclMentalAssmntDtlDto.getCityOther());
                    profassessment.setAddrProfAssmtStLn1(mdclMentalAssmntDtlDto.getAddressLine1Other());
                    profassessment.setAddrProfAssmtStLn2(mdclMentalAssmntDtlDto.getAddressLine2Other());
                    profassessment.setAddrProfAssmtZip(mdclMentalAssmntDtlDto.getZipOther());
                    profassessment.setCdProfAssmtCounty(mdclMentalAssmntDtlDto.getCountyOther());
                    profassessment.setCdProfAssmtState(mdclMentalAssmntDtlDto.getStateOther());
                    profassessment.setNbrProfAssmtPhone(mdclMentalAssmntDtlDto.getNumberOther());
                    profassessment.setNbrProfAssmtPhoneExt(mdclMentalAssmntDtlDto.getExtensionOther());
                    profassessment.setTxtProfAssmtCmnts(mdclMentalAssmntDtlDto.getCommentsOther());
                    if (!ObjectUtils.isEmpty(mdclMentalAssmntDtlDto.getDtApptEnd())) {
                        profassessment.setDtApptEnd(null);
                    } else {
                        profassessment.setDtApptEnd(mdclMentalAssmntDtlDto.getDtApptEnd());
                    }
                    // Start:artf227810
                    profassessment.setIndResultsPending(mdclMentalAssmntDtlDto.getIndResultsPending());
                    // End:artf227810
                    sessionFactory.getCurrentSession().saveOrUpdate(profassessment);
                }
            }
        } else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(action)) {
            ProfessionalAssmt profassessment = getProfessionalAssmt(mdclMentalAssmntDtlDto.getIdEvent());
            ProfAssmtNarr profassessmentnarr = (ProfAssmtNarr) sessionFactory.getCurrentSession()
                    .get(ProfAssmtNarr.class, mdclMentalAssmntDtlDto.getIdEvent());
            EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
                    .get(EventPersonLink.class, mdclMentalAssmntDtlDto.getIdEvent());
            Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
                    mdclMentalAssmntDtlDto.getIdEvent());
            if (null != profassessment) {
                sessionFactory.getCurrentSession().delete(profassessment);
            }
            if (null != profassessmentnarr) {
                sessionFactory.getCurrentSession().delete(profassessmentnarr);
            }
            if (null != eventPersonLink) {
                sessionFactory.getCurrentSession().delete(eventPersonLink);
            }
            if (null != event) {
                sessionFactory.getCurrentSession().delete(event);
            }
        }
        return result;
    }

    /**
     * Method Name: verifyOpenStage Method Description:
     *
     * @param idStage
     * @return IdStage
     */
    @Override
    public Boolean verifyOpenStage(Long idStage) {
        Boolean isStageOpened = Boolean.FALSE;
        Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
        if (ObjectUtils.isEmpty(stage))
            throw new DataLayerException(messageSource.getMessage("Stage.notFound", null, Locale.US));
        if (ServiceConstants.Y.equals(stage.getIndStageClose())
                || ServiceConstants.ONE.equals(stage.getIndStageClose())) {
            isStageOpened = Boolean.TRUE;
        }
        return isStageOpened;
    }

    @Override
    public ProfessionalAssmt getProfessionalAssmt(Long idEvent) {
        // TODO Auto-generated method stub
        return (ProfessionalAssmt) sessionFactory.getCurrentSession().get(ProfessionalAssmt.class, idEvent);
    }

    /* (non-Javadoc)
     * @see us.tx.state.dfps.service.investigation.dao.MedicalMentalAssessmentDao#getStageDtls(java.lang.Long)
     */
    @Override
    public Stage getStageDtls(Long idStage) {
        Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
        return stage;
    }
}
