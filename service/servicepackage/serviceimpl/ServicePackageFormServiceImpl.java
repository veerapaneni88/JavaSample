package us.tx.state.dfps.service.servicepackage.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ServicePackageReq;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.ServicePackageFormDto;
import us.tx.state.dfps.service.forms.util.AssessmentRequestPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.servicepackage.service.ServicePackageFormService;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
@Transactional
public class ServicePackageFormServiceImpl implements ServicePackageFormService {

    @Autowired
    private PcaDao pcaDao;

    @Autowired
    private StageDao stageDao;

    @Autowired
    private PlacementActPlannedDao placementActPlannedDao;

    @Autowired
    private LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private SubcareLOCFormDao subcareLOCFormDao;

    @Autowired
    private CommonApplicationDao commonApplicationDao;

    @Autowired
    private ServicePackageDao servicePackageDao;

    @Autowired
    private EventPersonLinkDao eventPersonLinkDao;

    @Autowired
    private AssessmentRequestPrefillData assessmentRequestPrefillData;

    /**
     *
     * Method Name: getServicePackageDetails Method Description: service
     * implementation for CSUB44S and and implements the DAMS
     *
     * @param placementFormReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getServicePackageDetails(ServicePackageReq servicePackageReq) {

        ServicePackageFormDto servicePackageFormDto = new ServicePackageFormDto();

        Long idPersonWorker = ServiceConstants.NULL_VAL;
        Long idPersonChild = ServiceConstants.NULL_VAL;

        StageCaseDtlDto stageCaseDtl = pcaDao.getStageAndCaseDtls(servicePackageReq.getIdStage());
        servicePackageFormDto.setStageCaseDtlDto(stageCaseDtl);
        StagePersonDto stagePerson = stageDao.getStagePersonLinkDetails(servicePackageReq.getIdStage(),
                ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
        servicePackageFormDto.setStagePersonDto(stagePerson);

        idPersonWorker = stagePerson.getIdTodoPersWorker();
        if (!ObjectUtils.isEmpty(idPersonWorker)) {

            PersonPhoneDto personPhone = subcareLOCFormDao.getPhnNbrbyPersonId(idPersonWorker,
                    ServiceConstants.FAX_NUMBER);
            servicePackageFormDto.setPersonPhoneDto(personPhone);

            List<ServicePackageDtlDto> servicePackageDtlDtos =
                    servicePackageDao.getServicePackage(servicePackageReq.getCaseId(),servicePackageReq.getIdStage(),
                            servicePackageReq.getSvcPkgId());

            EmployeePersPhNameDto employeePersPhName = employeeDao.searchPersonPhoneName(idPersonWorker);
            servicePackageFormDto.setEmployeePersPhNameDto(employeePersPhName);

            servicePackageFormDto.setServicePackageDtlDto(servicePackageDtlDtos.get(0));
            idPersonChild = servicePackageDtlDtos.get(0).getPersonId();
            if (!ObjectUtils.isEmpty(idPersonChild)) {
                PersonDto person = subcareLOCFormDao.getChildInfoByPersonId(idPersonChild);
                servicePackageFormDto.setPersonDto(person);
                Date phoneend = servicePackageFormDto.getPersonPhoneDto() != null
                        ? servicePackageFormDto.getPersonPhoneDto().getDtPersonPhoneEnd()
                        : ServiceConstants.GENERIC_END_DATE;
                PersonIdDto personId = subcareLOCFormDao.getMedicaidNbrByPersonId(idPersonChild,
                        ServiceConstants.CNUMTYPE_MEDICAID_NUMBER, ServiceConstants.STRING_IND_N, phoneend);
                servicePackageFormDto.setPersonIdDto(personId);
                LegalStatusPersonMaxStatusDtInDto legalStatusPersonMaxStatusDtInDto = new LegalStatusPersonMaxStatusDtInDto();
                legalStatusPersonMaxStatusDtInDto.setIdPerson(idPersonChild);
                List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxList = legalStatusPersonMaxStatusDtDao
                        .getRecentLegelStatusRecord(legalStatusPersonMaxStatusDtInDto);
                servicePackageFormDto.setLegalStatusPersonMaxList(legalStatusPersonMaxList);
                PlacementActPlannedInDto placementActPlannedInDto = new PlacementActPlannedInDto();
                placementActPlannedInDto.setIdPlcmtChild(idPersonChild);
                List<PlacementActPlannedOutDto> placementActPlanned = placementActPlannedDao
                        .getPlacementRecord(placementActPlannedInDto);
                servicePackageFormDto.setPlacementActPlannedList(placementActPlanned);

            }
        }
        return assessmentRequestPrefillData.returnPrefillData(servicePackageFormDto);
    }


}
