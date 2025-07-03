package us.tx.state.dfps.service.investigation.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.LicensingInvstDtl;
import us.tx.state.dfps.service.admin.dao.StageStagePersonLinkDao;
import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.contact.dto.MailDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CCIReporterLetterFormPrefillData;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dto.CCIReporterLetterFormDto;
import us.tx.state.dfps.service.investigation.service.CCIReporterLetterFormService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CCIReporterLetterFormServiceImpl implements CCIReporterLetterFormService {
    @Autowired
    LicensingInvstDtlDao licensingInvstDtlDao;

    @Autowired
    AddressDao addressDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    StageStagePersonLinkDao stagePersonLinkDao;

    @Autowired
    CCIReporterLetterFormPrefillData licensingInvFormPrefillData;

    private static final Logger log = Logger.getLogger(CCIReporterLetterFormServiceImpl.class);

    /**
     * This method fetches all prefill data for the CCI notification letter. (notification type ARRN) Note that the
     * person launching the letter may not be the primary on the case, so for the letter we ignore them
     * and search for the actual primary. Also, we use fetchCurrentPrimaryAddress and getStaffMailAndPhone which have
     * significant business logic ensuring we get the 'right' and standardized address and phone.
     * FCL - PPM# 55916 - artf128755
     * @param req DTO contaning the paramaters needed, only  idReporter and idStage are used.
     * @return
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public PreFillDataServiceDto retrieveOperationIdentity(LicensingInvCnclusnReq req) {
        log.debug("Entering method getMedicalConsentForms in MedicalConsenterFormServiceImpl");
        CCIReporterLetterFormDto prefillDto = new CCIReporterLetterFormDto();

        prefillDto.setSystemDate(new Date());

        AddressValueDto reporterAddress = addressDao.fetchCurrentPrimaryAddress(Long.valueOf(req.getIdPerson()));
        prefillDto.setReporterAddress(reporterAddress);

        PersonDto reporterPerson = personDao.getPersonById(Long.valueOf(req.getIdPerson()));
        prefillDto.setReporterPerson(reporterPerson);

        List<LicensingInvstDtl> operationDetails = licensingInvstDtlDao.getLicensingInvstDtlDaobyParentId(req.getIdStage());
        if (operationDetails != null && operationDetails.size() > 0) {
            prefillDto.setOperationDetails(operationDetails.get(0));
        } else {
            LicensingInvstDtl emptyRecord = new LicensingInvstDtl();
            emptyRecord.setNbrAcclaim(0);
            emptyRecord.setNmResource("OPERATION NOT FOUND");
        }

        // Find the primary worker, since they may not be the one generating the letter.
        Long workerId = stagePersonLinkDao.getPrimaryWorkerIdForStage(Long.valueOf(req.getIdStage()));

        PersonDto investigatorPerson = personDao.getPersonById(workerId);
        prefillDto.setInvestigatorPerson(investigatorPerson);

        MailDto investigatorPhone = personDao.getStaffMailAndPhone(workerId);
        prefillDto.setInvestigatorPhone(investigatorPhone);

        return licensingInvFormPrefillData.returnPrefillData(prefillDto);
    }
}
