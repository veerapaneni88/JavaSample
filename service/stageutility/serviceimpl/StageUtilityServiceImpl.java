package us.tx.state.dfps.service.stageutility.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:32 PM ©
 * 2017 Texas Department of Family and Protective Services
 * * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@Service
@Transactional
public class StageUtilityServiceImpl implements StageUtilityService {

    private static final Logger log = Logger.getLogger(StageUtilityServiceImpl.class);
    @Autowired
    private StageUtilityDao stageUtilityDao;
    @Autowired
    private StageDao stageDao;
    @Autowired
    private CodesDao codesDao;

    /**
     * Method Name: retrieveStageInfo Method Description: This method retrieves
     * information from Stage table using idStage.
     *
     * @param idStage
     * @return StageValueBeanDto @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public StageValueBeanDto retrieveStageInfo(Long idStage) {
        log.debug("Entering method retrieveStageInfo in StageUtilityService");

        StageValueBeanDto stageValBean = null;
        try {

            stageValBean = stageUtilityDao.retrieveStageInfo(idStage);
        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method retrieveStageInfo in StageUtilityService");
        return stageValBean;
    }

    /**
     * Method Name: findPrimaryChildForStage Method Description: This method
     * returns Primary Child for the Stage.
     *
     * @param idStage
     * @return Long @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Long findPrimaryChildForStage(Long idStage) {
        log.debug("Entering method findPrimaryChildForStage in StageUtilityService");

        Long idPersonPC = 0l;
        try {
            idPersonPC = stageUtilityDao.findPrimaryChildForStage(idStage);
        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method findPrimaryChildForStage in StageUtilityService");
        return idPersonPC;
    }

    /**
     * Method Name: findWorkersForStage Method Description: This method returns
     * the primary and secondary workers assigned to the stage with the given
     * security profiles. If the security profile list parameter is empty or
     * null, it returns all the primary and secondary workers assigned to the
     * stage.
     *
     * @param idStage
     * @param secProfiles
     * @return List<Long> @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<Long> findWorkersForStage(Long idStage, List<String> secProfiles) {
        log.debug("Entering method findWorkersForStage in StageUtilityService");

        List<Long> eligWorkers = null;
        try {
            eligWorkers = stageUtilityDao.findWorkersForStage(idStage, secProfiles);
        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method findWorkersForStage in StageUtilityService");
        return eligWorkers;
    }

    /**
     * Method Name: fetchReasonForDeathMissing Method Description: This method
     * gets count of persons on stage with DOD but no death code, for use in
     * validation of INV stage closures.
     *
     * @param idStage
     * @return Long @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Long fetchReasonForDeathMissing(Long idStage) {
        log.debug("Entering method fetchReasonForDeathMissing in StageUtilityService");

        Long reasonMissingCount = -1l;

        try {

            reasonMissingCount = stageUtilityDao.getDeathReasonMissing(idStage);
        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method fetchReasonForDeathMissing in StageUtilityService");
        return reasonMissingCount;
    }

    /**
     * Method Name: getCheckedOutStagesForPerson Method Description: This method
     * Fetches the list of stages which has this person and are checkout to MPS
     *
     * @param idPerson
     * @return ArrayList<StagePersonValueDto> @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson) {
        log.debug("Entering method getCheckedOutStagesForPerson in StageUtilityService");

        ArrayList<StagePersonValueDto> checkedOutStages = null;
        try {

            checkedOutStages = stageUtilityDao.getCheckedOutStagesForPerson(idPerson);
        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method getCheckedOutStagesForPerson in StageUtilityService");
        return checkedOutStages;
    }

    /**
     * Method Name: isPrimaryChildInOpenStage Method Description: This method is
     * to check if a person in Primary Child in open stage
     *
     * @param idPerson
     * @return boolean @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isPrimaryChildInOpenStage(Long idPerson) {
        log.debug("Entering method isPrimaryChildInOpenStage in StageUtilityService");

        ArrayList<StageValueBeanDto> pcStages = null;
        try {

            pcStages = stageUtilityDao.getStageListForPC(idPerson, Boolean.TRUE);
            if (pcStages.size() > 0) {
                return true;
            }

        } catch (DataNotFoundException e) {
            log.error(e.getMessage());
        }

        log.debug("Exiting method isPrimaryChildInOpenStage in StageUtilityService");
        return false;
    }

    /**
     * Method Name: isChildInSubStage Method Description: This method is to
     * check if a person in open SUB stage
     *
     * @param idPerson
     * @return boolean @
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isChildInSubStage(Long idPerson) {
        log.debug("Entering method isChildInSubStage in StageUtilityService");

        ArrayList<StageValueBeanDto> pcStages = null;
        try {

            pcStages = stageUtilityDao.getStageListForPC(idPerson, Boolean.TRUE);
            if (CollectionUtils.isNotEmpty(pcStages)) {
                if (pcStages.stream().anyMatch(pcStage -> CodesConstant.CSTAGES_SUB.equals(pcStage.getCdStage()))) {
                    return true;
                }
            }

        } catch (DataNotFoundException e) {
            return false;
        }

        log.debug("Exiting method isChildInSubStage in StageUtilityService");
        return false;
    }
    /**
     * Method Name: updateStageInfo Method Description: This method is to
     * update the stage table IND_VICTIM_NOTIFICATION_STATUS column
     * artf129782: Licensing Investigation Conclusion
     * @param idPerson
     * @return boolean @
     */
    @Override
    public void updateStageInfo(Long idStage) {

        StageValueBeanDto stageValueBeanDto = new StageValueBeanDto();
        stageValueBeanDto.setIdStage(stageUtilityDao.updateVictimNotificationStatus(idStage));

    }

    /**
     * This method will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
     * If the stage start date is prior to the APS Release Date, it returns true, else false.
     *
     * @param stageStartDate
     * @return
     */
    @Override
    public Boolean checkPreSingleStageByStartDate(Date stageStartDate) {
        Date relDate = codesDao.getAppRelDate(ServiceConstants.CRELDATE_NOV_2020_APS);
        return !ObjectUtils.isEmpty(stageStartDate) && DateUtils.isBefore(stageStartDate, relDate);
    }

    /**
     * Method Helps to check stage is pre single stage or not
     *
     * @param idCase - selected case id
     * @return return true stage start date is before CRELDATE_NOV_2020_APS else return false
     */
    @Override
    public boolean checkPreSingleStageByCaseId(Long idCase) {
        Date relDate = codesDao.getAppRelDate(ServiceConstants.CRELDATE_NOV_2020_APS);
        List<StageDto> stageDtoList = stageDao.getOpenStageByIdCase(idCase);
        if(!CollectionUtils.isEmpty(stageDtoList)) {
            return !ObjectUtils.isEmpty(stageDtoList.get(0).getDtStageStart()) && DateUtils.isBefore(stageDtoList.get(0).getDtStageStart(), relDate);
        }
        return false;
    }

}
