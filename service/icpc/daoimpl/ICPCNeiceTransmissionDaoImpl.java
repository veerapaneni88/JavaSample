package us.tx.state.dfps.service.icpc.daoimpl;

import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.icpc.dao.ICPCNeiceTransmissionDao;
import us.tx.state.dfps.service.icpc.dto.ICPCAgencyDto;
import us.tx.state.dfps.service.icpc.dto.ICPCCodeType;
import us.tx.state.dfps.service.icpc.dto.ICPCTransmissionDto;
import us.tx.state.dfps.service.icpc.dto.NeiceCaseLinkDto;
import us.tx.state.dfps.service.icpc.dto.NeiceStateParticpantDTO;
import us.tx.state.dfps.service.icpc.dto.NeiceTransmittalDto;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.NEICEDocument100AType;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.PlacementAugmentationType;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.PlacementType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.ChildEligibilityCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.ChildLegalPlacementStatusCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.InitialReportRequestedCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.PlacementIVESubsidyCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.PlacementPublicPrivateCategoryCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.ReportPeriodicityCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.SupervisingServicesRequestCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.ChildAugmentationType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.ChildType;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class ICPCNeiceTransmissionDaoImpl implements ICPCNeiceTransmissionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private LookupDao lookupDao;

    @Value("${ICPCNeiceTransmissionDaoImpl.getNeiceDetails}")
    private String getNeiceDetails;

    @Value("${ICPCNeiceTransmissionDaoImpl.getReceivingAgencyId}")
    private String getReceivingAgencyId;

    @Value("${ICPCNeiceTransmissionDaoImpl.getSendingAgencyId}")
    private String getSendingAgencyId;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchNeiceData}")
    private String fetchNeiceDataSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchPlacementResourceNeiceId}")
    private String fetchPlacementResourceNeiceIdSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchAgencyStateDetails}")
    private String fetchAgencyStateDetailsSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchAgencyEntityDetails}")
    private String fetchAgencyEntityDetailsSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.updateNeiceTransmittalStatus}")
    private String updateNeiceTransmittalStatusSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.updatePersonProcessedInd}")
    private String updatePersonProcessedIndSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchResourceDetailsByName}")
    private String fetchResourceDetailsByNameSql;

    @Value("${ICPCNeiceTransmissionDaoImpl.fetchCaseLinkByNeiceCaseId}")
    private String fetchCaseLinkByNeiceCaseIdSql;

    @Override
    public NeiceTransmittalDto getNeiceDetails(Long idTransmission, Long idNeiceTransmittalPerson) {
        NeiceTransmittalDto neiceTransmittalDto = null;

            neiceTransmittalDto = (NeiceTransmittalDto) sessionFactory
                    .getCurrentSession().createSQLQuery(getNeiceDetails)
                    .addScalar("idNeiceTransmittal", StandardBasicTypes.LONG)
                    .addScalar("idNeiceTransmittalPerson", StandardBasicTypes.LONG)
                    .addScalar("dtDocumentCreate", StandardBasicTypes.DATE)
                    .addScalar("indPlcmtPriority", StandardBasicTypes.STRING)
                    .addScalar("idNeiceCase", StandardBasicTypes.STRING)
                    .addScalar("idStateCase", StandardBasicTypes.STRING)
                    .addScalar("cdTransmittalCatg", StandardBasicTypes.STRING)
                    .addScalar("cdTransmittalPurpose", StandardBasicTypes.STRING)
                    .addScalar("dtRequestDue", StandardBasicTypes.DATE)
                    .addScalar("cdHomeStudyCatg", StandardBasicTypes.STRING)
                    .addScalar("indUrgent", StandardBasicTypes.STRING)
                    .addScalar("cdUrgentReason", StandardBasicTypes.STRING)
                    .addScalar("txtUrgentOthReason", StandardBasicTypes.STRING)
                    .addScalar("indConcurrence", StandardBasicTypes.STRING)
                    .addScalar("idPersonNeice", StandardBasicTypes.STRING)
                    .addScalar("idPersonState", StandardBasicTypes.STRING)
                    .addScalar("indIcwaElig", StandardBasicTypes.STRING)
                    .addScalar("indTitleIvEElig", StandardBasicTypes.STRING)
                    .addScalar("cdLegalStatus", StandardBasicTypes.STRING)
                    .addScalar("cdPlcmtCategory", StandardBasicTypes.STRING)
                    .addScalar("txtPlcmtCategoryOth", StandardBasicTypes.STRING)
                    .addScalar("txtLegalOthStatus", StandardBasicTypes.STRING)
                    .addScalar("indPlcmtPrivatePublic", StandardBasicTypes.STRING)
                    .addScalar("cdDecision", StandardBasicTypes.STRING)
                    .addScalar("dtDecision", StandardBasicTypes.DATE)
                    .addScalar("cdReasonDenial", StandardBasicTypes.STRING)
                    .addScalar("txtReasonDenial", StandardBasicTypes.STRING)
                    .addScalar("txtRemarks", StandardBasicTypes.STRING)
                    .addScalar("dtPlacement", StandardBasicTypes.DATE)
                    .addScalar("cdPlacementType", StandardBasicTypes.STRING)
                    .addScalar("cdCompactTermRsn", StandardBasicTypes.STRING)
                    .addScalar("txtCompactTermRsn", StandardBasicTypes.STRING)
                    .addScalar("cdReceivingState", StandardBasicTypes.STRING)
                    .addScalar("cdSendingState", StandardBasicTypes.STRING)
                    .addScalar("nmReceivingAgency", StandardBasicTypes.STRING)
                    .addScalar("nmSendingAgency", StandardBasicTypes.STRING)
                    .setParameter("idTransmission", idTransmission)
                    .setParameter("idNeiceTransmittalPerson", idNeiceTransmittalPerson)
                    .setResultTransformer(Transformers.aliasToBean(NeiceTransmittalDto.class)).uniqueResult();


        return neiceTransmittalDto;
    }

    @Override
    public ICPCTransmissionDto fetchNeiceData(Long idNeiceTransmittal, Long idNeiceTransmittalPerson) {

        return (ICPCTransmissionDto) sessionFactory
                .getCurrentSession()
                .createSQLQuery(fetchNeiceDataSql)
                .addScalar("idPersonNeice", StandardBasicTypes.STRING)
                .addScalar("documentData", StandardBasicTypes.BINARY)
                .setParameter("idNeiceTransmittal", idNeiceTransmittal)
                .setParameter("idNeiceTransmittalPerson", idNeiceTransmittalPerson)
                .setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class))
                .uniqueResult();
    }

    @Override
    public IcpcSubmission saveIcpcSubmission(Long idNeiceTransmittal, Long userId, Long idStage) {

        IcpcSubmission icpcSubmission = new IcpcSubmission();
        icpcSubmission.setIdReqSubmission(idNeiceTransmittal);
        icpcSubmission.setIdStage(idStage);
        icpcSubmission.setIdCreatedPerson(userId);
        icpcSubmission.setIdLastUpdatePerson(userId);
        icpcSubmission.setDtCreated(new Date());
        icpcSubmission.setDtLastUpdate(new Date());

        Long idIcpcSubmission =  (Long) sessionFactory.getCurrentSession().save(icpcSubmission);

        icpcSubmission.setIdIcpcSubmission(idIcpcSubmission);

        return icpcSubmission;

    }

    @Override
    public IcpcRequest saveIcpcRequest(NEICEDocument100AType neiceDocument100AType, IcpcSubmission icpcSubmission,
                                       Long userId) {

        NeiceTransmittalDto receivingAgenctDto = (NeiceTransmittalDto) sessionFactory
                .getCurrentSession()
                .createSQLQuery(getReceivingAgencyId)
                .addScalar("idReceivingStateAgency", StandardBasicTypes.LONG)
                .setParameter("agencyName", neiceDocument100AType.getDocumentRecipient()
                        .getEntityOrganization()
                        .getOrganizationName())
                .setResultTransformer(Transformers.aliasToBean(NeiceTransmittalDto.class))
                .uniqueResult();

        NeiceTransmittalDto sendingAgenctDto = (NeiceTransmittalDto) sessionFactory
                .getCurrentSession()
                .createSQLQuery(getSendingAgencyId)
                .addScalar("idSendingStateAgency", StandardBasicTypes.LONG)
                .setParameter("agencyName", neiceDocument100AType.getDocumentSource()
                        .getEntityOrganization()
                        .getOrganizationName())
                .setResultTransformer(Transformers.aliasToBean(NeiceTransmittalDto.class))
                .uniqueResult();

        IcpcRequest icpcRequest = new IcpcRequest();
        icpcRequest.setIdIcpcSubmission(icpcSubmission.getIdIcpcSubmission());
        icpcRequest.setIdIcpcPortalRequest(icpcSubmission.getIdReqSubmission());
        icpcRequest.setCdRequestType("A");
        icpcRequest.setCdReceivingState(neiceDocument100AType.getDocumentRecipient()
                .getEntityOrganization()
                .getOrganizationLocation()
                .getAddress()
                .getLocationState()
                .getLocationStateUSPostalServiceCode()
                .value());
        icpcRequest.setCdSendingState(neiceDocument100AType.getDocumentSource()
                .getEntityOrganization()
                .getOrganizationLocation()
                .getAddress()
                .getLocationState()
                .getLocationStateUSPostalServiceCode()
                .value());
        icpcRequest.setIdReceivingStateAgency(
                !ObjectUtils.isEmpty(receivingAgenctDto) ? receivingAgenctDto.getIdReceivingStateAgency() : null);
        icpcRequest.setIdSendingStateAgency(
                !ObjectUtils.isEmpty(sendingAgenctDto) ? sendingAgenctDto.getIdSendingStateAgency() : null);
        icpcRequest.setIdCreatedPerson(userId);
        icpcRequest.setIdLastUpdatePerson(userId);
        icpcRequest.setDtCreated(new Date());
        icpcRequest.setDtLastUpdate(new Date());

        Long idIcpcRequest = (Long) sessionFactory.getCurrentSession()
                .save(icpcRequest);

        icpcRequest.setIdIcpcRequest(idIcpcRequest);

        return icpcRequest;

    }

    @Override
    public IcpcEventLink saveIcpcEventLink(Long idCase, Long userId, String idNeiceCase, long idIcpcRequest,
                                           Event event) {

        IcpcEventLink icpcEventLink = new IcpcEventLink();
        icpcEventLink.setIdIcpcRequest(idIcpcRequest);
        icpcEventLink.setIdNeiceCase(idNeiceCase);
        if (!ObjectUtils.isEmpty(idCase)) {
            CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession()
                    .get(CapsCase.class, idCase);
            icpcEventLink.setCapsCase(capsCase);
        }
        icpcEventLink.setIdCreatedPerson(userId);
        icpcEventLink.setIdLastUpdatePerson(userId);
        icpcEventLink.setDtCreated(new Date());
        icpcEventLink.setDtLastUpdate(new Date());
        icpcEventLink.setEvent(event);

        Long idIcpcEventLink = (Long) sessionFactory.getCurrentSession()
                .save(icpcEventLink);

        icpcEventLink.setIdIcpcEventLink(idIcpcEventLink);

        return icpcEventLink;
    }

    @Override
    public IcpcPlacementRequest saveIcpcPlcmtRequest(Date dtCreated, ChildType childType, Long userId, long idIcpcRequest,
                                                     Event event, Date homeStudyDueDt) {

        NEICEDocument100AType neiceDocument100AType = childType.getHomeStudyRequest()
                .getNeiceDocument100A();

        IcpcPlacementRequest icpcPlacementRequest = new IcpcPlacementRequest();

        /*icpcPlacementRequest.setIndPriority(!ObjectUtils.isEmpty(neiceTransmittalDto.getIndPlcmtPriority())?
                neiceTransmittalDto.getIndPlcmtPriority().charAt(0) : null);
        icpcPlacementRequest.setDtHomeStudyDue(neiceTransmittalDto.getDtRequestDue());*/

        icpcPlacementRequest.setDtReceived(dtCreated);
        icpcPlacementRequest.setDtHomeStudyDue(homeStudyDueDt);
        icpcPlacementRequest.setIndIcwaEligible(Optional.ofNullable(childType.getChildAugmentation())
                .map(ChildAugmentationType::getChildICWAEligibilityCode)
                .filter(v -> !ChildEligibilityCodeType.PENDING.equals(v))
                .map(ChildEligibilityCodeType::value)
                .map(v -> v.charAt(0))
                .orElse(null));
        icpcPlacementRequest.setIndTitleIve(Optional.ofNullable(childType.getChildAugmentation())
                .map(ChildAugmentationType::getChildIVEDeterminationStatusCode)
                .map(ChildEligibilityCodeType::value)
                .map(v -> v.charAt(0))
                .orElse(null));

        Optional.ofNullable(childType.getChildAugmentation())
                .map(ChildAugmentationType::getChildCurrentLegalPlacementStatusCode)
                .map(ChildLegalPlacementStatusCodeType::value)
                .ifPresent(v -> icpcPlacementRequest.setCdLegalStatus(
                        lookupDao.encode(ICPCCodeType.LEGAL_STATUS.neiceType(), v)));
        icpcPlacementRequest.setTxtLegalStatus(Optional.ofNullable(childType.getChildAugmentation())
                .map(ChildAugmentationType::getChildCurrentLegalPlacementStatusOtherText)
                .orElse(null));

        icpcPlacementRequest.setCdCareType(lookupDao.encode(ICPCCodeType.TYPE_OF_CARE.neiceType(),
                neiceDocument100AType.getPlacementResource()
                        .getPlacementCategoryCode()
                        .value()));
        icpcPlacementRequest.setTxtxCareType(neiceDocument100AType.getPlacementResource()
                .getPlacementCategoryCodeOtherText());

        if (!ObjectUtils.isEmpty(neiceDocument100AType.getPlacementPublicPrivateCategoryCode())) {
            if (PlacementPublicPrivateCategoryCodeType.PUBLIC.equals(
                    neiceDocument100AType.getPlacementPublicPrivateCategoryCode())) {
                icpcPlacementRequest.setIndPubPrivPlcmt(ServiceConstants.Y);
            } else {
                icpcPlacementRequest.setIndPubPrivPlcmt(ServiceConstants.N);
            }
        }

        Optional.ofNullable(neiceDocument100AType.getIntendedPlacement())
                .map(PlacementType::getPlacementAugmentation)
                .map(PlacementAugmentationType::getPlacementIVESubsidyCode)
                .map(PlacementIVESubsidyCodeType::value)
                .ifPresent(v -> icpcPlacementRequest.setCdSubsidy(
                        lookupDao.encode(ICPCCodeType.SUBSIDY.neiceType(), v)));

        Optional.ofNullable(neiceDocument100AType.getInitialReportRequestedCode())
                .map(InitialReportRequestedCodeType::value)
                .ifPresent(v -> icpcPlacementRequest.setCdInitialReport(
                        lookupDao.encode(ICPCCodeType.INITIAL_REPORT.neiceType(), v)));

        Optional.ofNullable(neiceDocument100AType.getSupervisingServicesRequestCode())
                .map(SupervisingServicesRequestCodeType::value)
                .ifPresent(v -> icpcPlacementRequest.setCdSprvsrySrvcs(
                        lookupDao.encode(ICPCCodeType.SUPERVISORY_SERVICES.neiceType(), v)));
        icpcPlacementRequest.setTxtSprvsrySrvcs(neiceDocument100AType.getSupervisingServicesRequestOtherText());

        Optional.ofNullable(neiceDocument100AType.getSupervisoryReportPeriodicityCode())
                .filter(type -> !ReportPeriodicityCodeType.SEMI_ANNUAL.equals(type))
                .map(ReportPeriodicityCodeType::value)
                .ifPresent(v -> icpcPlacementRequest.setCdSprvsryRprts(
                        lookupDao.encode(ICPCCodeType.SUPERVISORY_REPORT.neiceType(), v)));
        icpcPlacementRequest.setTxtSprvsryRprts(neiceDocument100AType.getSupervisoryReportPeriodicityOtherText());

        icpcPlacementRequest.setIdIcpcRequest(idIcpcRequest);
        icpcPlacementRequest.setIdCreatedPerson(userId);
        icpcPlacementRequest.setIdLastUpdatePerson(userId);
        icpcPlacementRequest.setDtCreated(new Date());
        icpcPlacementRequest.setDtLastUpdate(new Date());

        Long idIcpcPlacementRequest = (Long) sessionFactory.getCurrentSession()
                .save(icpcPlacementRequest);

        icpcPlacementRequest.setIdIcpcPlacementRequest(idIcpcPlacementRequest);

        return icpcPlacementRequest;
    }

    @Override
    public Long saveIcpcTransmittal(NeiceTransmittalDto neiceTransmittalDto, Long userId, IcpcRequest icpcRequest) {
        IcpcTransmittal icpcTransmittal = new IcpcTransmittal();
        icpcTransmittal.setIcpcRequest(icpcRequest);
        icpcTransmittal.setCdTransmittalType(lookupDao.encode("NCTRTP", neiceTransmittalDto.getCdTransmittalCatg()));
        icpcTransmittal.setCdTransmittalPurpose(
                lookupDao.encode("NCTMPR", neiceTransmittalDto.getCdTransmittalPurpose()));
        icpcTransmittal.setCdHomeStudyType(lookupDao.encode("NCHSTY", neiceTransmittalDto.getCdHomeStudyCatg()));
        icpcTransmittal.setCdUrgentReqReason(lookupDao.encode("NCUGRN", neiceTransmittalDto.getCdUrgentReason()));
        icpcTransmittal.setTxtUrgentReqReason(neiceTransmittalDto.getTxtUrgentOthReason());
        icpcTransmittal.setIndConcurrence(neiceTransmittalDto.getIndConcurrence());
        //icpcTransmittal.setCdAddtnlInfo();todo
        icpcTransmittal.setCdTransmittalStatus("10");

        icpcTransmittal.setIdCreatedPerson(userId);
        icpcTransmittal.setIdLastUpdatePerson(userId);
        icpcTransmittal.setDtCreated(new Date());
        icpcTransmittal.setDtLastUpdate(new Date());

        Long icpcTransmittalId = (Long) sessionFactory.getCurrentSession()
                .save(icpcTransmittal);

        return icpcTransmittalId;
    }

    @Override
    public void saveIcpcRequestPersonLink(Long userId, IcpcRequest icpcRequest, Long idPerson, String personType,
                                          String idPersonNeice, String cdPlacementCode) {

        IcpcRequestPersonLink personLink = new IcpcRequestPersonLink();
        personLink.setIcpcRequest(icpcRequest);
        personLink.setCdPersonType(personType);
        if (!ObjectUtils.isEmpty(idPerson)) {
            Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
            personLink.setPerson(person);
        }
        personLink.setIdPersonNeice(idPersonNeice);

        personLink.setIdCreatedPerson(userId);
        personLink.setIdLastUpdatePerson(userId);
        personLink.setDtCreated(new Date());
        personLink.setDtLastUpdate(new Date());

        sessionFactory.getCurrentSession()
                .save(personLink);
    }

    @Override
    public void saveIcpcPlcmntStatus(Long userId, IcpcRequest icpcRequest, Long idChild,
                                     NeiceTransmittalDto neiceTransmittalDto, IcpcPlacementRequest plcmntRequest) {
        IcpcPlacementStatus status = new IcpcPlacementStatus();
        status.setDtPlacement(neiceTransmittalDto.getDtPlacement());
        status.setIdIcpcRequest(icpcRequest.getIdIcpcRequest());
        status.setDtPlcmntTerm(neiceTransmittalDto.getDtDecision());
        status.setCdCompactTermRsn(neiceTransmittalDto.getCdCompactTermRsn());
        status.setTxtTerminationNotes(neiceTransmittalDto.getTxtDocumentSummary());
        status.setIcpcPlacementRequest(plcmntRequest);
        status.setDtPlacement(neiceTransmittalDto.getDtPlacement());
        status.setIdCreatedPerson(userId);
        status.setIdLastUpdatePerson(userId);
        status.setDtCreated(new Date());
        status.setDtLastUpdate(new Date());

        sessionFactory.getCurrentSession()
                .save(status);
    }

    @Override
    public Event createEventDtls(Long idStage, Long idUser, String eventDesc, Long idCase, Long idChild) {

        Event event = new Event();

        if (!ObjectUtils.isEmpty(idStage)) {
            Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
            event.setStage(stage);
        }
        if (!ObjectUtils.isEmpty(idUser)) {
            Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idUser);
            event.setPerson(person);
        }
        event.setCdEventType("ICA");
        event.setCdEventStatus("PROC");
        event.setCdTask("9620");
        event.setTxtEventDescr(eventDesc);
        event.setIdCase(idCase);
        event.setDtEventCreated(new Date());
        event.setDtLastUpdate(new Date());
        event.setDtEventOccurred(new Date());

        Long idEvent = (Long) sessionFactory.getCurrentSession().save(event);

        event.setIdEvent(idEvent);

        EventPersonLink personLink = new EventPersonLink();
        personLink.setDtLastUpdate(new Date());
        if (!ObjectUtils.isEmpty(idChild)) {
            Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idChild);
            personLink.setPerson(person);
        }
        personLink.setIdCase(idCase);
        personLink.setEvent(event);

        sessionFactory.getCurrentSession().saveOrUpdate(personLink);

        return event;
    }

    @Override
    public IcpcDocument saveIcpcDocument(ICPCTransmissionDto attachmentDto, IcpcRequest icpcRequest,
                                 IcpcSubmission icpcSubmission, ICPCPlacementReq icpcPlacementReq,
                                 StagePersonLinkDto personLinkDto) {

        IcpcDocument icpcDocument = new IcpcDocument();
        if (icpcPlacementReq.getCaseSpecific()) {
            icpcDocument.setIcpcSubmission(icpcSubmission);
        }
        if (icpcPlacementReq.getSpecific100A()) {
            icpcDocument.setIcpcRequest(icpcRequest);
        }
        if (!icpcPlacementReq.getCaseSpecific() && !icpcPlacementReq.getSpecific100A()) {
            icpcDocument.setIcpcSubmission(icpcSubmission);
            icpcDocument.setIdPerson(personLinkDto.getIdPerson());
        }

        icpcDocument.setCdType(lookupDao.encode("NCDCTP", attachmentDto.getCdDocType()));
        icpcDocument.setDtCreated(new Date());
        icpcDocument.setNmFile(attachmentDto.getNmAttachment());
        icpcDocument.setSysMimeType(attachmentDto.getAttchmntFormat());
        icpcDocument.setTxtDetails(attachmentDto.getDocumentDescription());
        icpcDocument.setDtUpload(new Date());
        icpcDocument.setIdCreatedPerson(icpcPlacementReq.getIdUser());
        icpcDocument.setIdLastUpdatePerson(icpcPlacementReq.getIdUser());
        icpcDocument.setDtCreated(new Date());
        icpcDocument.setDtLastUpdate(new Date());

        Long idIcpcDocument = (Long) sessionFactory.getCurrentSession()
                .save(icpcDocument);

        icpcDocument.setIdIcpcDocument(idIcpcDocument);
        return icpcDocument;
    }

    @Override
    public void saveIcpcFileStorage(IcpcDocument icpcDocument, ICPCTransmissionDto attachmentDto, ICPCPlacementReq icpcPlacementReq) {

        try {
            IcpcFileStorage icpcFileStorage = new IcpcFileStorage();
            icpcFileStorage.setIcpcDocument(icpcDocument);
            icpcFileStorage.setFileDocumentData(new SerialBlob(attachmentDto.getDocumentData()));
            icpcFileStorage.setIdCreatedPerson(icpcPlacementReq.getIdUser());
            icpcFileStorage.setIdLastUpdatePerson(icpcPlacementReq.getIdUser());
            icpcFileStorage.setDtCreated(new Date());
            icpcFileStorage.setDtLastUpdate(new Date());

            sessionFactory.getCurrentSession().save(icpcFileStorage);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String fetchPlacementResourceNeiceId(String idNeiceCase) {

        return (String) sessionFactory.getCurrentSession()
                .createSQLQuery(fetchPlacementResourceNeiceIdSql)
                .addScalar("idResourceNeice", StandardBasicTypes.STRING)
                .setParameter("idNeiceCase", idNeiceCase)
                .list()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public NeiceStateParticpantDTO fetchAgencyStateDetails(String agencyNm) {
        return (NeiceStateParticpantDTO) sessionFactory.getCurrentSession()
                .createSQLQuery(fetchAgencyStateDetailsSql)
                .addScalar("neiceStateParticipantId", StandardBasicTypes.LONG)
                .addScalar("stateCode", StandardBasicTypes.STRING)
                .addScalar("stateNm", StandardBasicTypes.STRING)
                .setParameter("agencyNm", agencyNm)
                .setResultTransformer(Transformers.aliasToBean(NeiceStateParticpantDTO.class))
                .uniqueResult();
    }

    @Override
    public ICPCAgencyDto fetchAgencyEntityDetails(String entityNm) {
        return (ICPCAgencyDto) sessionFactory.getCurrentSession()
                .createSQLQuery(fetchAgencyEntityDetailsSql)
                .addScalar("idEntity", StandardBasicTypes.LONG)
                .setParameter("entityNm", entityNm)
                .setResultTransformer(Transformers.aliasToBean(ICPCAgencyDto.class))
                .uniqueResult();
    }

    @Override
    public void updateNeiceTransmittalStatus(Long idNeiceTransmittal, Long idUser) {

        sessionFactory.getCurrentSession().createSQLQuery(updateNeiceTransmittalStatusSql)
                .setParameter("idNeiceTransmittal", idNeiceTransmittal)
                .setParameter("idUser", String.valueOf(idUser))
                .executeUpdate();
    }

    @Override
    public void updatePersonProcessedInd(Long idNeiceTransmittal, Long idUser, Long idNeiceTransmittalPerson) {

        sessionFactory.getCurrentSession().createSQLQuery(updatePersonProcessedIndSql)
                .setParameter("idNeiceTransmittal", idNeiceTransmittal)
                .setParameter("idUser", String.valueOf(idUser))
                .setParameter("idNeiceTransmittalPerson", idNeiceTransmittalPerson)
                .executeUpdate();
    }

    @Override
    public List<CapsResourceLinkDto> fetchResourceDetailsByNeiceName(String placementResourceNm) {

        return (List<CapsResourceLinkDto>) sessionFactory.getCurrentSession()
                .createSQLQuery(fetchResourceDetailsByNameSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .setParameter("nmPlacementResource", placementResourceNm)
                .setResultTransformer(Transformers.aliasToBean(CapsResourceLinkDto.class))
                .list();
    }

    @Override
    public NeiceCaseLinkDto fetchNeiceCaseLinkById(String idNeiceCase) {

        return (NeiceCaseLinkDto) sessionFactory
                .getCurrentSession()
                .createSQLQuery(fetchCaseLinkByNeiceCaseIdSql)
                .addScalar("idNeiceCaseLink", StandardBasicTypes.LONG)
                .addScalar("idNeiceCase", StandardBasicTypes.STRING)
                .addScalar("idStateCase", StandardBasicTypes.STRING)
                .setParameter("idNeiceCase", idNeiceCase)
                .setResultTransformer(Transformers.aliasToBean(NeiceCaseLinkDto.class))
                .uniqueResult();
    }

    @Override
    public void saveOrUpdateNeicePersonResource(Long idNeiceCaseLink, String idNeice, Long idPersonResource,
                                                String cdType, Long idUser) {

        List<NeicePersonResourceLink> neicePersonResourceLinks = sessionFactory.getCurrentSession()
                .createCriteria(NeicePersonResourceLink.class)
                .add(Restrictions.eq("neiceCaseLink.idNeiceCaseLink", idNeiceCaseLink))
                .add(Restrictions.eq("idNeice", idNeice))
                .list();

        if (!CollectionUtils.isEmpty(neicePersonResourceLinks)) {
            if (neicePersonResourceLinks.size() == 1 && !ObjectUtils.isEmpty(
                    neicePersonResourceLinks.get(0)) && cdType.equalsIgnoreCase(neicePersonResourceLinks.get(0)
                    .getCdType()) && Arrays.asList("C", "P")
                    .contains(cdType)) {

                NeicePersonResourceLink neicePersonResourceLink = neicePersonResourceLinks.get(0);
                neicePersonResourceLink.setIdPerson(idPersonResource);
                neicePersonResourceLink.setIdLastUpdateBy(String.valueOf(idUser));
                neicePersonResourceLink.setTsLastUpdate(new Date());
                sessionFactory.getCurrentSession()
                        .saveOrUpdate(neicePersonResourceLink);
            }
        }
    }
}
