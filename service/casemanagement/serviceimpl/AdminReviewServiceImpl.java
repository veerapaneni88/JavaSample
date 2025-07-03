package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.AdminAllegation;
import us.tx.state.dfps.common.domain.AdminReview;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.service.casemanagement.service.AdminReviewService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.response.CloseOpenStageRes;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AdminReviewServiceImpl implements AdminReviewService {

    @Autowired
    CloseOpenStageService closeOpenStageService;

    @Autowired
    AdminReviewDao adminReviewDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    PcaDao pcaDao;

    @Autowired
    TodoCommonFunctionService todoCommonFunctionService;

    @Autowired
    StageWorkloadDao stageWorkloadDao;

    public CloseOpenStageRes createAdminReview(CloseOpenStageInputDto closeOpenStageInputDto){

        CloseOpenStageOutputDto  closeOpenStageOutputDto = closeOpenStageService.closeOpenStage(closeOpenStageInputDto);

        // start the new code to create admin_allegation, event_person_link and to do here

        //Get the open Admin review for the given stage id
        if(!TypeConvUtil.isNullOrEmpty(closeOpenStageOutputDto)) {
            AdminReviewDto adminReviewDto = adminReviewDao.getAdminReviewOpenStagesByStageId(closeOpenStageOutputDto.getIdStage());

            if(! TypeConvUtil.isNullOrEmpty(adminReviewDto) && ! ObjectUtils.isEmpty(adminReviewDto.getIdPerson() )){


                    List<AllegationDto> allegationDtoList =  adminReviewDao.getAllegationsByStageIdPersonId(
                            adminReviewDto.getIdStageRelated(), adminReviewDto.getIdPerson());
                    allegationDtoList.stream().forEach(allegation -> createAdminAllegation(allegation,
                            adminReviewDto.getIdStage(), Long.valueOf(closeOpenStageInputDto.getUserLogonId())));



                   // ** Call CAUDA5D
                   if((! ServiceConstants.APS_PROGRAM.equalsIgnoreCase(closeOpenStageInputDto.getCdStageProgram())||
                        ServiceConstants.AFC_STAGE.equalsIgnoreCase(closeOpenStageInputDto.getCdStageProgram()))) {
                       stageWorkloadDao.updateClosureReason(closeOpenStageInputDto.getIdStage(), null);
                    }

                //** DAM Name:     CCMN19D
                //**
                //** Description:  The purpose of this dam (ccmn19dQUERYdam) is to retrieve
                //**               the PRIMARY (PR) or HISTORICAL PRIMARY (HP) worker
                //**               and NM_STAGE of the ID_STAGE which is passed into the dam.

                StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(closeOpenStageInputDto.getIdStage(),
                        ServiceConstants.PRIMARY_ROLE);

                // CCMN60D - Supervisor information
                SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(stagePersonDto.getIdTodoPersWorker());


                //Create to do
                TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
                if (!ObjectUtils.isEmpty(stagePersonDto)
                        && !StringUtils.isEmpty(stagePersonDto.getIdTodoPersWorker())) {

                    todoCommonFunctionDto.setSysIdTodoCfPersAssgn(stagePersonDto.getIdTodoPersWorker());
                }
                createTodo(todoCommonFunctionDto,closeOpenStageInputDto,adminReviewDto);

                //Assign the to-do to the supervisor

                if (!ObjectUtils.isEmpty(supervisorDto)
                        && !StringUtils.isEmpty(supervisorDto.getIdPerson())) {

                    todoCommonFunctionDto.setSysIdTodoCfPersAssgn(supervisorDto.getIdPerson());
                }
                createTodo(todoCommonFunctionDto,closeOpenStageInputDto,adminReviewDto);
            }
        }
        CloseOpenStageRes closeOpenStageRes = new CloseOpenStageRes();
        closeOpenStageRes.setCloseOpenStageOutputDto(closeOpenStageOutputDto);
        return closeOpenStageRes;
    }


    private void createAdminAllegation(AllegationDto allegationDto, Long idStage, Long loginUserId){

        AdminAllegation adminAllegation = new AdminAllegation();
        adminAllegation.setDtLastUpdate(new Date());
        adminAllegation.setDtCreated(new Date());
        if (!TypeConvUtil.isNullOrEmpty(idStage) ){
            Stage arStage = stageDao.getStageEntityById(idStage);
            adminAllegation.setStageByIdAdminAllegArStage(arStage);
        }

        if (!TypeConvUtil.isNullOrEmpty(allegationDto.getIdAllegationStage())) {
            Stage stage = stageDao.getStageEntityById(allegationDto.getIdAllegationStage());
            adminAllegation.setStageByIdAdminAllegStage(stage);
        }

        if(! TypeConvUtil.isNullOrEmpty(allegationDto.getIdVictim())){
            Person victim = personDao.getPersonByPersonId(allegationDto.getIdVictim());
            adminAllegation.setPersonByIdAdminAllegVictim(victim);
        }

        if(! TypeConvUtil.isNullOrEmpty(allegationDto.getIdAllegedPerpetrator())){
            Person allegPerpetrator = personDao.getPersonByPersonId(allegationDto.getIdAllegedPerpetrator());
            adminAllegation.setPersonByIdAdminAllegPerpetratr(allegPerpetrator);
        }
        adminAllegation.setIdCase(allegationDto.getIdCase());
        adminAllegation.setIndAdminAllegPrior(ServiceConstants.Y.charAt(0));
        adminAllegation.setCdAdminAllegDispostiion(allegationDto.getCdAllegDisposition());
        adminAllegation.setCdAdminAllegIncdntStg(allegationDto.getCdAllegIncidentStage());
        adminAllegation.setCdAdminAllegSeverity(allegationDto.getCdAllegSeverity());
        adminAllegation.setCdAdminAllegType(allegationDto.getCdAllegType());
        adminAllegation.setIdCreatedPerson(loginUserId);
        adminAllegation.setIdLastUpdatePerson(loginUserId);

        adminReviewDao.saveAdminAllegation(adminAllegation);

    }


    private void createTodo(TodoCommonFunctionDto todoCommonFunctionDto,
                            CloseOpenStageInputDto closeOpenStageInputDto,
                            AdminReviewDto adminReviewDto){

        TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();

        String todoDescriptionAFC = "ARV in " + closeOpenStageInputDto.getNmPersonFull() +
                " Case requested re ";
        String todoDescriptionOthers = "ARV in Case " + closeOpenStageInputDto.getNmPersonFull() +
                " requested by ";

        if(!ObjectUtils.isEmpty(closeOpenStageInputDto)
                && !StringUtils.isEmpty(closeOpenStageInputDto.getIdPerson())){
            todoDescriptionAFC = todoDescriptionAFC + closeOpenStageInputDto.getIdPerson();
            todoDescriptionOthers = todoDescriptionOthers + closeOpenStageInputDto.getIdPerson();
        }else{
            todoDescriptionAFC = todoDescriptionAFC + closeOpenStageInputDto.getNmPersonFull();
            todoDescriptionOthers = todoDescriptionOthers + closeOpenStageInputDto.getNmPersonFull();
        }
        if (ServiceConstants.AFC_STAGE.equalsIgnoreCase(closeOpenStageInputDto.getCdStageProgram())) {
            todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.CFC028);
            todoCommonFunctionDto.setSysTxtTodoCfLongDesc(todoDescriptionAFC);
        }
        else  {
            todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.CFC012);
            todoCommonFunctionDto.setSysTxtTodoCfLongDesc(todoDescriptionOthers);
        }

        if (!ObjectUtils.isEmpty(closeOpenStageInputDto)
                && !StringUtils.isEmpty(closeOpenStageInputDto.getUserLogonId())) {
            todoCommonFunctionDto
                    .setSysIdTodoCfPersCrea(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));
            todoCommonFunctionDto
                    .setSysIdTodoCfPersWkr(Long.valueOf(closeOpenStageInputDto.getUserLogonId()));
        }


        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdStage())) {
            todoCommonFunctionDto.setSysIdTodoCfStage(adminReviewDto.getIdStage());
        }
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto.getIdEvent())) {
                todoCommonFunctionDto.setSysIdTodoCfEvent(adminReviewDto.getIdEvent());
        }

        todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
        // CSUB40U
        todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);

    }
}
