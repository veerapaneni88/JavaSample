package us.tx.state.dfps.service.kincaregiverhomeassmnt.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetailComments;
import us.tx.state.dfps.common.domain.PlacementAudit;
import us.tx.state.dfps.kincaregiverhomedetails.dto.CvsKinCaregiverHomeAssmtDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CvsKinCaregiverHomeAssmtPrefillData;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.dao.CvsKinCaregiverHomeAssmtDao;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.service.CvsKinCaregiverHomeAssmtService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonSearchDto;

import java.util.ArrayList;
import java.util.List;


/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
@Service
@Transactional
public class CvsKinCaregiverHomeAssmtServiceImpl implements CvsKinCaregiverHomeAssmtService {

    private static final Logger log = Logger.getLogger(CvsKinCaregiverHomeAssmtServiceImpl.class);

    @Autowired
    CvsKinCaregiverHomeAssmtDao cvsKinCaregiverHomeAssmtDao;

    @Autowired
    CvsKinCaregiverHomeAssmtPrefillData cvsKinCaregiverHomeAssmtPrefillData;

    @Autowired
    PersonDao personDao;
    

    /**
     * Method Name: getCvsKinCaregiverHomeAssmt Method Description: Request to get
     * the Home Assessment Data in prefill data format
     *
     * @param kinCaregiverReq
     * @return
     */
    @Override
    public PreFillDataServiceDto getCvsKinCaregiverHomeAssmt(PopulateFormReq kinCaregiverReq) {

        /** add all necessary prefill dto(s) to a single input dto*/
        CvsKinCaregiverHomeAssmtDto cvsKinCaregiverHomeAssmtDto = new CvsKinCaregiverHomeAssmtDto();

        /** the primary details including caregiver id are in the kin_hm_assmt_dtl table */
        KinHomeAssessmentDetail caseCaregiverInfo = cvsKinCaregiverHomeAssmtDao.getKinCaregiverCaseInfoByKamEvent(kinCaregiverReq.getIdEvent());
        cvsKinCaregiverHomeAssmtDto.setCaseCaregiverInfo(caseCaregiverInfo);

        /** get all the children placed */
        List<PlacementAudit> kinPlacementList= cvsKinCaregiverHomeAssmtDao.getKinPlacementInfoById(caseCaregiverInfo.getId());
        cvsKinCaregiverHomeAssmtDto.setKinPlacementList(kinPlacementList);

        /** get the general comments into a separate object from the denial comments */
        List<KinHomeAssessmentDetailComments> kinCommentsList= cvsKinCaregiverHomeAssmtDao.getKinHomeCommentsById(caseCaregiverInfo.getId());
        if (!ObjectUtils.isEmpty(kinCommentsList) && ServiceConstants.Zero_INT < kinCommentsList.size()) {
            if (CodesConstant.KINCMTYP_GLCM.equals(kinCommentsList.get(0).getCommentTypeCode())) {
                cvsKinCaregiverHomeAssmtDto.setGlcmComments(kinCommentsList.get(0));
            }
            for (KinHomeAssessmentDetailComments comments : kinCommentsList) {
                if (CodesConstant.KINCMTYP_DNCM.equals(comments.getCommentTypeCode())) {
                    cvsKinCaregiverHomeAssmtDto.setDncmComments(comments);
                    break;
                }
            }
        }
        /** get the caregiver and children id(s) into a single list to get the names */
        List<Long> kinPidList = new ArrayList<>();
        if (ServiceConstants.ZERO_VAL < caseCaregiverInfo.getKinCaregiverId())
            kinPidList.add(caseCaregiverInfo.getKinCaregiverId());
        if (!ObjectUtils.isEmpty(kinPlacementList) && ServiceConstants.Zero_INT < kinPlacementList.size()) {
            for (PlacementAudit child : kinPlacementList) {
                kinPidList.add(child.getIdPlcmtChild());
            }
        }
        cvsKinCaregiverHomeAssmtDto.setKinNameList(personDao.getPersonDetailForAllPid(kinPidList));

        return cvsKinCaregiverHomeAssmtPrefillData.returnPrefillData(cvsKinCaregiverHomeAssmtDto);
    }
}
