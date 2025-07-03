package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.workload.dao.PriorityClosureDao;
import us.tx.state.dfps.service.workload.dto.IntakeNotfChildDto;
import us.tx.state.dfps.service.workload.dto.IntakeNotfFacilityDto;
import us.tx.state.dfps.service.workload.dto.PriorityClosureLicensingDto;

/**
 *Class Description:This class contains the methods for priority closure notification section added as part of FCL project
 *Oct 15, 2019- 3:08:14 PM
 *© 2019 Texas Department of Family and Protective Services 
 ***********  Change History *********************************
 *Oct 15, 2019  mullar2	 artf128805 : FCL changes 
*/

@Repository
public class PriorityClosureDaoImpl implements PriorityClosureDao {	
	

	@Value("${PriorityClosureDao.getDefaultFacilityDetails}")
	String getDefaultFacilityDetailsSql;

	@Value("${PriorityClosureDao.getDefaultVictimNotificationDetails}")
	String getDefaultVictimNotificationDetailsSql;	
	
	@Value("${PriorityClosureDao.getLatestVictimDetails}")
	String getLatestVictimDetailsSql;
	
	
	@Value("${PriorityClosureDao.getDefaultFacilityChildrenDetails}")
    String getDefaultFacilityChildrenDetailsSql;
	
   @Value("${PriorityClosureDao.getRevisedPersonDetails}")
    String getRevisedPersonDetailsSql;   
   
   
   @Value("${PriorityClosureDao.deleteVctmNotifctnRsrcChildSql}")
   String deleteVctmNotifctnRsrcChildSql;
	
	@Autowired
	private SessionFactory sessionFactory;

	public static final Logger log = Logger.getLogger(PriorityClosureDaoImpl.class);

	public PriorityClosureDaoImpl() {

	}

	/**
	 *Method Name:	getDefaultVictimNotificationDetails
	 *Method Description:Used to fetch the default Victim Notification Details
	 *@param intakeStageId
	 *@return
	 */
	@Override
	public List<IntakeNotfChildDto> getDefaultVictimNotificationDetails(Long intakeStageId) {	  
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getDefaultVictimNotificationDetailsSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING).addScalar("txtLegalStatus", StandardBasicTypes.STRING)				
				.addScalar("idCase", StandardBasicTypes.LONG)				
				.addScalar("idSubStage", StandardBasicTypes.LONG)				
				.addScalar("idWorkerPerson", StandardBasicTypes.LONG)				
				.addScalar("nmWorkerPerson", StandardBasicTypes.STRING)					
				.addScalar("idSupervisor", StandardBasicTypes.LONG).addScalar("nmSupervisor", StandardBasicTypes.STRING).addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)			
				.setParameter("intakeStageId", intakeStageId)
				.setResultTransformer(Transformers.aliasToBean(IntakeNotfChildDto.class));
		List<IntakeNotfChildDto> victimNotificationList=(List<IntakeNotfChildDto>) query.list();
		return victimNotificationList;
	}

	
	/**
	 *Method Name:	getDefaultNotificationFacilityDetails
	 *Method Description:Used to fetch default Facility details for the Oldest Victim in the intake
	 *@param oldestVictimId
	 *@return
	 */
	@Override
	public ResourceDto getDefaultNotificationFacilityDetails(Long oldestVictimId) {
		ResourceDto resourceDto =new ResourceDto();
		
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getDefaultFacilityDetailsSql)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("nmResource", StandardBasicTypes.STRING) 
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdInvJurisdiction", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)    
				.addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCnty", StandardBasicTypes.STRING)	
				.setParameter("oldestVictimId", oldestVictimId)
				.setResultTransformer(Transformers.aliasToBean(ResourceDto.class));
		resourceDto = (ResourceDto) query.uniqueResult();
		

		return resourceDto;
	}

	
	/**
	 *Method Name:	getDefaultFacilityChildrenDetails
	 *Method Description:Used to fetch the Children in the facility
	 *@param facilityId
	 *@return
	 */
	@Override
	public List<IntakeNotfChildDto> getDefaultFacilityChildrenDetails(Long facilityId) {
	    Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getDefaultFacilityChildrenDetailsSql)
	            .addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdLegalStatStatus", StandardBasicTypes.STRING).addScalar("txtLegalStatus", StandardBasicTypes.STRING)               
                .addScalar("idCase", StandardBasicTypes.LONG)               
                .addScalar("idSubStage", StandardBasicTypes.LONG)               
                .addScalar("idWorkerPerson", StandardBasicTypes.LONG).addScalar("nmWorkerPerson", StandardBasicTypes.STRING)                 
                .addScalar("idSupervisor", StandardBasicTypes.LONG).addScalar("nmSupervisor", StandardBasicTypes.STRING)                
                .setParameter("idRsrcFacil", facilityId)
                .setResultTransformer(Transformers.aliasToBean(IntakeNotfChildDto.class));
        List<IntakeNotfChildDto> childrenInFacilityList=(List<IntakeNotfChildDto>) query.list();
        return childrenInFacilityList;
	}


	/**
	 *Method Name:	getNotifiedVictimNotificationDetails
	 *Method Description:Used to fetch the saved Victim Notification Details
	 *@param intakeStageId
	 *@return
	 */
	@Override
	public List<IntakeNotfChildDto> getNotifiedVictimNotificationDetails(Long intakeStageId) {
		List<IntakeNotfChildDto> notifiedVictimList = new ArrayList<>();
		
		List<VictimNotification> victimNotificationList = (List<VictimNotification>) sessionFactory.getCurrentSession().createCriteria(VictimNotification.class)
				.add(Restrictions.eq("idStage", intakeStageId)).list();
		
		if(victimNotificationList!=null){
		    victimNotificationList.forEach(entity->{
				Stage notifiedStage = (Stage)(sessionFactory.getCurrentSession().createCriteria(Stage.class)
						.add(Restrictions.eq("idStage", entity.getIdSubStage())).uniqueResult());
		        IntakeNotfChildDto dto=new IntakeNotfChildDto();
		        BeanUtils.copyProperties(entity,dto);
		        if (notifiedStage != null) {
					dto.setCdStage(notifiedStage.getCdStage());
				}
		        notifiedVictimList.add(dto);
		    });
		}else {
		    return null;
		}
		return notifiedVictimList;
	}
	

	
	/**
	 *Method Name:	getSavedFacilityDetails
	 *Method Description:Used to fetch the saved facility details
	 *@param facilityId
	 *@return
	 */
	@Override
	public ResourceDto getSavedFacilityDetails(Long facilityId) {		 
	    CapsResource resource= (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,facilityId);		
		if(resource!=null){
            ResourceDto resourceDto=new ResourceDto();
            BeanUtils.copyProperties(resource,resourceDto);
            return resourceDto;
        }else {
            return null;
        }	
		
	}

	/**
	 * Method Name:	getIntakeLicensingDetails
	 * Method Description:Fetch licensing details of intake stage
	 * @param intakeStageId
	 * @return priorityClosure
	 */
	@Override
	public PriorityClosureLicensingDto getIntakeLicensingDetails(Long intakeStageId) {
		PriorityClosure priorityClosure = getPriorityClosureLicensingDetails(intakeStageId);
		if(priorityClosure!=null){
			PriorityClosureLicensingDto priorityClosureLicensingDto=new PriorityClosureLicensingDto();
			BeanUtils.copyProperties(priorityClosure,priorityClosureLicensingDto);
			return priorityClosureLicensingDto;
		}else {
			return null;
		}
	}

	/**
	 * Method Name:	getPriorityClosureLicensingDetails
	 * Method Description: Fetch licensing details of intake stage from Priority_Closure table
	 * @param idStage
	 * @return priorityClosure
	 */
	@Override
	public PriorityClosure getPriorityClosureLicensingDetails(Long idStage) {
		PriorityClosure priorityClosure = (PriorityClosure) sessionFactory.getCurrentSession().createCriteria(PriorityClosure.class)
				.add(Restrictions.eq("idStage", idStage)).uniqueResult();
		return priorityClosure;
	}

	/**
	 * Method Name:	savePriorityClosureLicensingDetails
	 * Method Description: Save licensing details to Priority_Closure table
	 * @param priorityClosure
	 */
	@Override
	public void savePriorityClosureLicensingDetails(PriorityClosure priorityClosure) {
		sessionFactory.getCurrentSession().save(priorityClosure);
	}

	/**
	 *Method Name:	getNotifiedFacilityDetails
	 *Method Description:Used to fetch the Notified Facility Details
	 *@param intakeStageId
	 *@return
	 */
	@Override
	public IntakeNotfFacilityDto getNotifiedFacilityDetails(Long intakeStageId) { 		
	    VictimNotificationRsrc resource = (VictimNotificationRsrc) sessionFactory.getCurrentSession().createCriteria(VictimNotificationRsrc.class)
				.add(Restrictions.eq("idStage", intakeStageId)).uniqueResult();	    
		if(resource!=null){
		    ResourceDto resourceDto=new ResourceDto();
		    IntakeNotfFacilityDto facilityDto=new IntakeNotfFacilityDto();		   
		    BeanUtils.copyProperties(resource,resourceDto);
		    facilityDto.setFacilityDetail(resourceDto);
		    Set<VctmNotifctnRsrcChild> vctmNotifctnRsrcChilds=resource.getVctmNotifctnRsrcChilds();
		    List<IntakeNotfChildDto> childrenInFacilityList=new ArrayList<>();
		    vctmNotifctnRsrcChilds.forEach(childentity->{
		        IntakeNotfChildDto childDto=new IntakeNotfChildDto();
		        BeanUtils.copyProperties(childentity,childDto);
		        childrenInFacilityList.add(childDto);
		    });
		    facilityDto.setChildrenInFacilityList(childrenInFacilityList);
		    return facilityDto;
		}else {
            return null;
        }
		
		
	}
	

	/**
	 *Method Name:	getNotifiedFacilityChildrenDetails
	 *Method Description:Used to fetch the Notified children in facility details
	 *@param victimNotificationRsrcId
	 *@return
	 */
	@Override
	public List<IntakeNotfChildDto> getNotifiedFacilityChildrenDetails(Long victimNotificationRsrcId) {
		List<IntakeNotfChildDto> notifiedChildrenList = new ArrayList<>();
		List<VctmNotifctnRsrcChild> vctmNotifctnRsrcChildList= (List<VctmNotifctnRsrcChild>) sessionFactory.getCurrentSession().createCriteria(VctmNotifctnRsrcChild.class)
				.add(Restrictions.eq("victimNotificationRsrc.idVictimNotificationRsrc", victimNotificationRsrcId)).list();
		if(vctmNotifctnRsrcChildList!=null){
		    vctmNotifctnRsrcChildList.forEach(entity->{
                IntakeNotfChildDto dto=new IntakeNotfChildDto();
               
                    BeanUtils.copyProperties(entity,dto);
                    notifiedChildrenList.add(dto);
            });
        }else {
            return null;
        }
		return notifiedChildrenList;
	}

	
	/**
	 *Method Name:	getLatestVictimDetails
	 *Method Description:Used to fetch the details of the revised victim
	 *@param wrkrIdPerson
	 *@param victimIdPerson
	 *@param suprvIdPerson
	 *@return
	 */
	@Override
	public IntakeNotfChildDto getLatestVictimDetails(Long idPersonRevised,Long idSubstageRevised) {
		
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getLatestVictimDetailsSql)
		        .addScalar("nmPersonFullRevised", StandardBasicTypes.STRING)
                .addScalar("cdLegalStatStatusRevised", StandardBasicTypes.STRING).addScalar("txtLegalStatusRevised", StandardBasicTypes.STRING)               
                .addScalar("idCase", StandardBasicTypes.LONG)                
                .addScalar("idWorkerRevised", StandardBasicTypes.LONG)               
                .addScalar("nmWorkerRevised", StandardBasicTypes.STRING)                 
                .addScalar("idSupervisorRevised", StandardBasicTypes.LONG).addScalar("nmSupervisorRevised", StandardBasicTypes.STRING)
				.setParameter("idsubstage", idSubstageRevised).setParameter("idrevisedperson", idPersonRevised)
				.setResultTransformer(Transformers.aliasToBean(IntakeNotfChildDto.class));
		
		return (IntakeNotfChildDto)query.uniqueResult();
	}

    /**
     *Method Name:	fetchRevisedVictimDetails
     *Method Description:Fetch the details of the revised person id
     *@param idRevisedPerson
     *@return
     */
    @Override
    public List<IntakeNotfChildDto> fetchRevisedVictimDetails(Long idRevisedPerson) {
      
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getRevisedPersonDetailsSql)
                .addScalar("nmPersonFullRevised", StandardBasicTypes.STRING)
                .addScalar("idPersonRevised", StandardBasicTypes.LONG)
                .addScalar("cdLegalStatStatusRevised", StandardBasicTypes.STRING).addScalar("txtLegalStatusRevised", StandardBasicTypes.STRING)               
                .addScalar("idCase", StandardBasicTypes.LONG)               
                .addScalar("idSubStage", StandardBasicTypes.LONG)               
                .addScalar("idWorkerRevised", StandardBasicTypes.LONG)               
                .addScalar("nmWorkerRevised", StandardBasicTypes.STRING)                 
                .addScalar("idSupervisorRevised", StandardBasicTypes.LONG).addScalar("nmSupervisorRevised", StandardBasicTypes.STRING)        
                .setParameter("idrevisedperson", idRevisedPerson)
                .setResultTransformer(Transformers.aliasToBean(IntakeNotfChildDto.class));
        List<IntakeNotfChildDto> victimNotificationList=(List<IntakeNotfChildDto>) query.list();
        return victimNotificationList;
    }
    /**
     *Method Name:	getIntakeDate
     *Method Description:Fetch the intake Date
     *@param intakeStageId
     *@return
     */
    @Override
    public Date getIntakeDate(Long intakeStageId) {         
        Date intakeDate = (Date) sessionFactory.getCurrentSession().createCriteria(IncomingDetail.class, "incomingDetail")
                .add(Restrictions.eq("idStage", intakeStageId)).setProjection(Projections.property("dtIncomingCall")).uniqueResult();
        return intakeDate;
    }
    /**
     *Method Name:	saveVictimNotificationDetails
     *Method Description:To save the Victim Notification details
     *@param victimNotification
     */
    @Override
    public void saveVictimNotificationDetails(VictimNotification victimNotification){
        sessionFactory.getCurrentSession().saveOrUpdate(victimNotification);
    }
    /**
     *Method Name:	saveFacilityDetails
     *Method Description:Save the facility Details
     *@param victimNotificationRsrc
     */
    @Override
    public void saveFacilityDetails(VictimNotificationRsrc victimNotificationRsrc){
       
        sessionFactory.getCurrentSession().saveOrUpdate(victimNotificationRsrc);       
    }
    
    /**
     *Method Name:	deleteChildrenDetails
     *Method Description:Used to delete the children in facility when the resource is changed using resource search
     *@param victimNotificationRsrcId
     */
    @Override
    public void deleteChildrenDetails(Long victimNotificationRsrcId) {
        
        List<VctmNotifctnRsrcChild> vctmNotifctnRsrcChildList= (List<VctmNotifctnRsrcChild>) sessionFactory.getCurrentSession().createCriteria(VctmNotifctnRsrcChild.class)
                .add(Restrictions.eq("victimNotificationRsrc.idVictimNotificationRsrc", victimNotificationRsrcId)).list();
        if(vctmNotifctnRsrcChildList!=null){
            vctmNotifctnRsrcChildList.forEach(entity->{
                sessionFactory.getCurrentSession().delete(entity);
            });
        }
        sessionFactory.getCurrentSession().flush();
    }
    
    /**
     *Method Name:	saveChildrenDetails
     *Method Description:Used to save the changed resources children details
     *@param vctmNotifctnRsrcChildList
     */
    @Override
    public void saveChildrenDetails(List<VctmNotifctnRsrcChild> vctmNotifctnRsrcChildList) {
        vctmNotifctnRsrcChildList.forEach(entity->{
            sessionFactory.getCurrentSession().save(entity);
        });
        sessionFactory.getCurrentSession().flush();
    }
    
    /**
     *Method Name:	updateRciIndicator
     *Method Description:Used to update the new RCI indicator in Stage table on click of notify in priority closure 
     *@param idStage
     */
    @Override
    public void updateRciIndicator(Long idStage) {      
        Stage stageentity= (Stage) sessionFactory.getCurrentSession().get(Stage.class,idStage);
        stageentity.setIndVictimNotifStatus("Y");
        sessionFactory.getCurrentSession().update(stageentity);
    }
    
  
    @Override
     public Object loadEntity(Long primaryId,Class<?> clazz){
                BiFunction<Long, Class<?> , Object > fetchEntity = (primaryKey, entity) -> {
        return sessionFactory.getCurrentSession().get(entity,primaryKey); 
        };
        if(ObjectUtils.isEmpty(primaryId)) {
            return null;
        }
        return  fetchEntity.apply(primaryId, clazz);
      }

	@Override
	public void deleteRsrcChildren(List<VctmNotifctnRsrcChild> savedVctmNotifctnRsrcChildsList) {
		List<Long> idList=savedVctmNotifctnRsrcChildsList.stream().map(child -> child.getIdVctmNotifctnRsrcChild()).collect(Collectors.toList());
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(deleteVctmNotifctnRsrcChildSql).setParameterList("ids", idList);
		query.executeUpdate();
	}

	/**
	 * Checks if there is a record exists for the idPerson,idStage,idSubStage and idWorker combination.
	 * isSupervisor is used to switch between supervisor and worker
	 * @param idPerson
	 * @param idStage
	 * @param idSubStage
	 * @param idWorker
	 * @param isSupervisor
	 * @return
	 */
	@Override
	public boolean checkIfAlertAlreadyExists(Long idPerson,Long idStage,Long idSubStage,Long idWorker,boolean isSupervisor){
		Criteria victimNotificationCriteria=sessionFactory.getCurrentSession().createCriteria(VictimNotification.class);
		victimNotificationCriteria.add(Restrictions.eq("idPerson",idPerson));
		victimNotificationCriteria.add(Restrictions.eq("idStage",idStage));
		victimNotificationCriteria.add(Restrictions.eq("idSubStage",idSubStage));
		if(!isSupervisor){
			victimNotificationCriteria.add(Restrictions.eq("idWorkerPerson",idWorker));
		} else {
			victimNotificationCriteria.add(Restrictions.eq("idSupervisor",idWorker));
		}

		return !victimNotificationCriteria.list().isEmpty();
	}
    
 }
