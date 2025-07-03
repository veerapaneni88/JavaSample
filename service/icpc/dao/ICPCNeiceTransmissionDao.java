/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * Class Description: DAO Interface for the ICPC page services
 * Aug 03, 2018- 4:24:06 PM
 * © 2017 Texas Department of Family and Protective Services
 */
package us.tx.state.dfps.service.icpc.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.IcpcDocument;
import us.tx.state.dfps.common.domain.IcpcEventLink;
import us.tx.state.dfps.common.domain.IcpcPlacementRequest;
import us.tx.state.dfps.common.domain.IcpcRequest;
import us.tx.state.dfps.common.domain.IcpcSubmission;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.icpc.dto.ICPCAgencyDto;
import us.tx.state.dfps.service.icpc.dto.ICPCTransmissionDto;
import us.tx.state.dfps.service.icpc.dto.NeiceCaseLinkDto;
import us.tx.state.dfps.service.icpc.dto.NeiceStateParticpantDTO;
import us.tx.state.dfps.service.icpc.dto.NeiceTransmittalDto;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.NEICEDocument100AType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.ChildType;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DAO
 * Interface for the ICPC page services Aug 03, 2018- 4:24:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */

public interface ICPCNeiceTransmissionDao {

    public NeiceTransmittalDto getNeiceDetails(Long idTransmission, Long idNeiceTransmittalPerson);

    ICPCTransmissionDto fetchNeiceData(Long idNeiceTransmittal, Long idNeiceTransmittalPerson);

    IcpcSubmission saveIcpcSubmission(Long idNeiceTransmittal, Long userId, Long idStage);

    IcpcRequest saveIcpcRequest(NEICEDocument100AType neiceDocument100AType, IcpcSubmission icpcSubmission,
                                Long userId);

    IcpcEventLink saveIcpcEventLink(Long idCase, Long userId, String idNeiceCase, long idIcpcRequest, Event event);

    IcpcPlacementRequest saveIcpcPlcmtRequest(Date icpcPlacementReq, ChildType childType, Long userId, long idIcpcRequest,
                                              Event event, Date homeStudyDueDt);

    Long saveIcpcTransmittal(NeiceTransmittalDto neiceTransmittalDto, Long userId, IcpcRequest idIcpcRequest);

    void saveIcpcRequestPersonLink(Long userId, IcpcRequest icpcRequest, Long idPerson, String personType,
                                   String idPersonNeice, String cdPlacementCode);

    void saveIcpcPlcmntStatus(Long userId, IcpcRequest icpcRequest, Long idChild,
                              NeiceTransmittalDto neiceTransmittalDto, IcpcPlacementRequest plcmntRequest);

    Event createEventDtls(Long idStage, Long idUser, String eventDesc, Long idCase, Long idChild);

    IcpcDocument saveIcpcDocument(ICPCTransmissionDto attachmentDto, IcpcRequest icpcRequestId,
                                  IcpcSubmission idIcpcSubmission,
                                  ICPCPlacementReq icpcPlacementReq, StagePersonLinkDto personLinkDto);

    void saveIcpcFileStorage(IcpcDocument icpcDocument, ICPCTransmissionDto attachmentDto,
                             ICPCPlacementReq icpcPlacementReq);

    String fetchPlacementResourceNeiceId(String idNeiceCase);

    NeiceStateParticpantDTO fetchAgencyStateDetails(String agencyNm);

    ICPCAgencyDto fetchAgencyEntityDetails(String entityNm);

    void updateNeiceTransmittalStatus(Long idNeiceTransmittal, Long idUser);

    List<CapsResourceLinkDto> fetchResourceDetailsByNeiceName(String placementResourceNm);

    void updatePersonProcessedInd(Long idNeiceTransmittal, Long idUser, Long idNeiceTransmittalPerson);

    NeiceCaseLinkDto fetchNeiceCaseLinkById(String idNeiceCase);

    void saveOrUpdateNeicePersonResource(Long idNeiceCaseLink, String idNeice, Long idPersonResource, String cdType, Long idUser);
}
