package us.tx.state.dfps.service.icpc.daoimpl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.IcpcDocument;
import us.tx.state.dfps.common.domain.IcpcFileStorage;
import us.tx.state.dfps.common.domain.IcpcRequest;
import us.tx.state.dfps.common.domain.IcpcSubmission;
import us.tx.state.dfps.common.domain.IcpcTransmittalDocLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.domain.TodoInfo;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.icpc.dao.ICPCPlacementDao;
import us.tx.state.dfps.service.icpc.dao.IcpcDocumentUploadDao;
import us.tx.state.dfps.service.icpcdocument.dto.IcpcDocumentDto;
import us.tx.state.dfps.service.icpcdocument.dto.IcpcFileStorageDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dto.TodoInfoCommonDto;

@Repository
public class IcpcDocumentUploadDaoImpl implements IcpcDocumentUploadDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	TodoDao todoDao;
	
	@Autowired
	ICPCPlacementDao icpcPlacementDao;

	@Value("${IcpcDocumentUploadDaoImpl.updateTaskComplete}")
	private String updateTaskCompleteSql;

	private static final Logger log = Logger.getLogger(IcpcDocumentUploadDaoImpl.class);

	
	/**
	 * method name: fetchDocument Description: This method fetches ICPC Document.
	 * 
	 * @param IcpcDocumentDto
	 * @return icpcDocumentDto
	 *
	 */
	@Override
	public IcpcDocumentDto fetchDocument(Long idDocument) {

		IcpcDocumentDto icpcDocumentDto = new IcpcDocumentDto();
		IcpcDocument icpcDocument = (IcpcDocument) sessionFactory.getCurrentSession().get(IcpcDocument.class,
				idDocument);
		BeanUtils.copyProperties(icpcDocument, icpcDocumentDto,"idIcpcRequest","idIcpcSubmission","idPerson");
		if(!ObjectUtils.isEmpty(icpcDocument.getIcpcRequest()) &&  !ObjectUtils.isEmpty(icpcDocument.getIcpcRequest().getIdIcpcRequest()) && 0 < icpcDocument.getIcpcRequest().getIdIcpcRequest()){
			icpcDocumentDto.setIdIcpcRequest(icpcDocument.getIcpcRequest().getIdIcpcRequest());
			icpcDocumentDto.setCdDocSpecific("100A");
		}
		if(!ObjectUtils.isEmpty(icpcDocument.getIcpcSubmission()) &&  !ObjectUtils.isEmpty(icpcDocument.getIcpcSubmission().getIdIcpcSubmission()) && 0 < icpcDocument.getIcpcSubmission().getIdIcpcSubmission()){
			icpcDocumentDto.setIdIcpcSubmission(icpcDocument.getIcpcSubmission().getIdIcpcSubmission());
			icpcDocumentDto.setCdDocSpecific("Case");
		}
		if(!ObjectUtils.isEmpty(icpcDocument.getIdPerson()) &&  0 < icpcDocument.getIdPerson()){
			icpcDocumentDto.setIdPerson(icpcDocument.getIdPerson());
			icpcDocumentDto.setCdDocSpecific("Person");
		}
		return icpcDocumentDto;
	}

	
	/**
	 * method name: insertPRTTask Description: This method saves ICPC Document.
	 * 
	 * @param idFileStorage
	 * @return icpcFileStorageDto
	 *
	 */
	@Override
	public IcpcFileStorageDto fetchFileStorage(Long idFileStorage) {

		IcpcFileStorageDto icpcFileStorageDto = new IcpcFileStorageDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcFileStorage.class);
		IcpcDocument icpcDocument = new IcpcDocument();
		icpcDocument.setIdIcpcDocument(idFileStorage);
		criteria.add(Restrictions.eq("icpcDocument", icpcDocument));
		IcpcFileStorage icpcFileStorage = (IcpcFileStorage) criteria.uniqueResult();
		BeanUtils.copyProperties(icpcFileStorage, icpcFileStorageDto);
		try {
			if(!ObjectUtils.isEmpty(icpcFileStorage.getFileDocumentData())){
				icpcFileStorageDto.setFileDocumentData(getBytes(icpcFileStorage.getFileDocumentData().getBinaryStream()));
			}			
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new DataLayerException(e.getMessage());
		}
		return icpcFileStorageDto;
	}

	/**
	 * method name: insertPRTTask Description: This method saves ICPC Document.
	 * 
	 * @param IcpcDocumentDto
	 * @return IcpcDocumentDto
	 *
	 */
	@Override
	public IcpcDocumentDto saveDocument(IcpcDocumentDto icpcDocumentDto,Boolean indInsert) {
		IcpcDocument icpcDocument = new IcpcDocument();
		IcpcRequest icpcRequest = new IcpcRequest();
		IcpcSubmission icpcSubmission = new IcpcSubmission();
		if(!indInsert){
			icpcDocument = (IcpcDocument) sessionFactory.getCurrentSession().get(IcpcDocument.class,
					icpcDocumentDto.getIdIcpcDocument());
			icpcDocument.setTxtDetails(icpcDocumentDto.getTxtDetails());
			icpcDocument.setCdType(icpcDocumentDto.getCdType());
			icpcDocument.setIdLastUpdatePerson(icpcDocumentDto.getIdLastUpdatePerson());
		}
		Long idIcpcSubmission = null;
		if(!"100A".equalsIgnoreCase(icpcDocumentDto.getCdDocSpecific())){
			if(!ObjectUtils.isEmpty(icpcDocumentDto.getIdCase()) && 0 < icpcDocumentDto.getIdCase()){
				Long idIntakeStage = icpcPlacementDao.retrieveIntakeStageId(icpcDocumentDto.getIdCase(), CodesConstant.CSTAGES_INT);
				//artf254020: handling requests with case-Specific file deletion issues
				idIcpcSubmission = icpcPlacementDao.getIdICPCSubmission(idIntakeStage);
				if (ObjectUtils.isEmpty(idIcpcSubmission) || idIcpcSubmission == 0)
					idIcpcSubmission = icpcPlacementDao.getIdICPCSubmissionByRequest(icpcDocumentDto.getIdIcpcRequest());
			}else{
				idIcpcSubmission =  icpcDocumentDto.getIdIcpcSubmission();
			}
			icpcDocument.setIcpcRequest(null);
			icpcDocument.setIdPerson(null);
		}
		if("100A".equalsIgnoreCase(icpcDocumentDto.getCdDocSpecific()) && !ObjectUtils.isEmpty(icpcDocumentDto.getIdIcpcRequest()) && 0 < icpcDocumentDto.getIdIcpcRequest()){
			icpcRequest.setIdIcpcRequest(icpcDocumentDto.getIdIcpcRequest());
			icpcDocument.setIcpcRequest(icpcRequest);
			icpcDocument.setIcpcSubmission(null);
			icpcDocument.setIdPerson(null);
		}
		
		if("Person".equalsIgnoreCase(icpcDocumentDto.getCdDocSpecific()) && !ObjectUtils.isEmpty(icpcDocumentDto.getIdIcpcRequest()) && 0 < icpcDocumentDto.getIdIcpcRequest()){
			Long idPrimaryChild = icpcPlacementDao.getIcpcPrimaryChild(icpcDocumentDto.getIdIcpcRequest());
			if(!ObjectUtils.isEmpty(idPrimaryChild)){
				icpcDocument.setIdPerson(idPrimaryChild);
			}
		}
		//Defect# 12822 - Added a check to set the idIcpcSubmission only if it has greater than zero and not null
		if(!ObjectUtils.isEmpty(idIcpcSubmission) && 0l != idIcpcSubmission){
			icpcSubmission.setIdIcpcSubmission(idIcpcSubmission);
			icpcDocument.setIcpcSubmission(icpcSubmission);
		}
		if(indInsert){
			BeanUtils.copyProperties(icpcDocumentDto, icpcDocument,"icpcRequest","icpcSubmission","idPerson");
			icpcDocument.setDtCreated(new Date());
			icpcDocument.setDtUpload(new Date());
		}
		if (null != icpcDocument.getIdIcpcDocument()) {
			if (0 == icpcDocument.getIdIcpcDocument()) {
				icpcDocument.setIdIcpcDocument(null);
			}
		}
		icpcDocument.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(icpcDocument);
		BeanUtils.copyProperties(icpcDocument, icpcDocumentDto);
		return icpcDocumentDto;
	}

	/**
	 * method name: insertPRTTask Description: This method saves ICPC Document.
	 * 
	 * @param IcpcFileStorageDto
	 * @return IcpcFileStorageDto
	 *
	 */
	@Override
	public IcpcFileStorageDto saveFileStorage(IcpcFileStorageDto icpcFileStorageDto) {

		IcpcFileStorage icpcFileStorage = new IcpcFileStorage();
		Blob blob = null;
		try {
			blob = new javax.sql.rowset.serial.SerialBlob(icpcFileStorageDto.getFileDocumentData());
		} catch (SerialException e) {
			log.error(e.getMessage());
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		IcpcDocument icpcDocument = new IcpcDocument();
		icpcDocument.setIdIcpcDocument(icpcFileStorageDto.getIdIcpcDocument());
		icpcFileStorage.setIcpcDocument(icpcDocument);
		icpcFileStorage.setFileDocumentData(blob);
		icpcFileStorage.setDtLastUpdate(new Date());
		icpcFileStorage.setDtCreated(new Date());
		BeanUtils.copyProperties(icpcFileStorageDto, icpcFileStorage);
		icpcFileStorage.setDtLastUpdate(new Date());
		icpcFileStorage.setDtCreated(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(icpcFileStorage);
		return icpcFileStorageDto;
	}
	
	/**
	 * method name: insertPRTTask Description: This method saves ICPC Document.
	 * 
	 * @param idDocument
	 * @return null
	 *
	 */

	@Override
	public void deleteDocument(Long idDocument) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcFileStorage.class);
		IcpcDocument icpcDocument = new IcpcDocument();
		icpcDocument.setIdIcpcDocument(idDocument);
		criteria.add(Restrictions.eq("icpcDocument", icpcDocument));
		IcpcFileStorage icpcFileStorage = (IcpcFileStorage) criteria.uniqueResult();

		Criteria docLinkCriteria = sessionFactory.getCurrentSession().createCriteria(IcpcTransmittalDocLink.class);
		docLinkCriteria.add(Restrictions.eq("icpcDocument", icpcDocument));
		List<IcpcTransmittalDocLink> icpcTransmittalDocLinks = docLinkCriteria.list();

		icpcDocument = (IcpcDocument) sessionFactory.getCurrentSession().load(IcpcDocument.class, idDocument);
		if (icpcTransmittalDocLinks != null && !icpcTransmittalDocLinks.isEmpty()) {
			icpcTransmittalDocLinks.forEach(link -> sessionFactory.getCurrentSession()
					.delete(link));

		}
		sessionFactory.getCurrentSession().delete(icpcFileStorage);
		sessionFactory.getCurrentSession().delete(icpcDocument);

	}

	/**
	 * @param inputStream
	 * @return
	 */
	private byte[] getBytes(InputStream inputStream) {
		BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			byte[] b = new byte[2048];
			for (int n = buffInputStream.read(b); n > 0; n = buffInputStream.read(b)) {
				byteOutputStream.write(b, 0, n);
			}
		} catch (Exception e) {
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return byteOutputStream.toByteArray();
	}

	
	/**
	 * Method Name: updateTodo Method Description: This method update _TODO
	 * Table. EJB Name : ToDoBean.java
	 *
	 * @param todoLists
	 */
	@Override
	public Long createAlert(TodoDto todoDto) {
		Long result = ServiceConstants.ZERO;
		Todo todo = new Todo();
		if (todoDto.getIdTodoPersAssigned() != 0) {
			Person person = new Person();
			person.setIdPerson(todoDto.getIdTodoPersAssigned());
			todo.setPersonByIdTodoPersAssigned(person);
		}
		TodoInfoCommonDto todoInfoCommonDto = todoDao.getTodoInfoByTodoInfo(ServiceConstants.CD_TODO_INFO_ALERT_ICPC_DOC);
		TodoInfo todoInfo = new TodoInfo();
		if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto) && todoInfoCommonDto.getIndTodoInfoEnabled().equals(ServiceConstants.STRING_IND_Y)) {
			if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getIdTodoInfo())) {
				todoInfo.setIdTodoInfo(todoInfoCommonDto.getIdTodoInfo());
				todo.setTodoInfo(todoInfo);
			}
			if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoTask())) {
				todo.setCdTodoTask(todoInfoCommonDto.getCdTodoInfoTask());
			}
			if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoType())) {
				todo.setCdTodoType(todoInfoCommonDto.getCdTodoInfoType());
			}
			Stage stage = new Stage();
			stage.setIdStage(todoDto.getIdTodoStage());
			todo.setDtTodoDue(todoDto.getDtTodoDue());
			if(!ObjectUtils.isEmpty(todoDto.getTodoDesc())) {
				todo.setTxtTodoDesc(todoDto.getTodoDesc());
			}else{
				todo.setTxtTodoDesc(todoInfoCommonDto.getTodoInfoDesc());
			}
			todo.setTxtTodoLongDesc(todoInfoCommonDto.getTodoInfoLongDesc());
			todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
			todo.setDtLastUpdate(new Date());
			todo.setStage(stage);
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
		}
		return result;	}
	
	
	/**
	 * Method Name: createTask Method Description: This method create task in TODO
	 * Table. EJB Name : ToDoBean.java
	 * 
	 * @param todoDto
	 * @return long
	 */
	@Override
	public Long createTask(TodoDto todoDto) {
		Long result = ServiceConstants.ZERO;
		Todo todo = new Todo();
		if (todoDto.getIdTodoPersAssigned() != 0) {
			Person person = new Person();
			person.setIdPerson(todoDto.getIdTodoPersAssigned());
			Stage stage = new Stage();
			stage.setIdStage(todoDto.getIdTodoStage());
			CapsCase capsCase = new CapsCase();
			capsCase.setIdCase(todoDto.getIdTodoCase());
			todo.setCapsCase(capsCase);
			Event event = new Event();
			event.setIdEvent(todoDto.getIdTodoEvent());
			todo.setEvent(event);
			Person personWork = new Person();
			personWork.setIdPerson(todoDto.getIdTodoPersWorker());
			todo.setPersonByIdTodoPersWorker(personWork);
			todo.setDtTodoDue(todoDto.getDtTodoDue());
			todo.setTxtTodoDesc(todoDto.getTodoDesc());
			todo.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
			todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
			todo.setCdTodoType("T");
			todo.setDtLastUpdate(new Date());
			todo.setStage(stage);
			todo.setPersonByIdTodoPersAssigned(person);
			todo.setCdTodoTask(todoDto.getCdTask());
			todo.setDtTodoCreated(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
		}
		return result;
	}

	/**
	 *  Method Name: updateTaskComplete
	 *  Method Description: This method to update the task completed
	 *
	 * @param idEvent
	 * @param txtTodoDesc
	 * @param userId
	 */
	@Override
	public void updateTaskComplete(Long idEvent, String txtTodoDesc, Long userId) {

		sessionFactory.getCurrentSession().createSQLQuery(updateTaskCompleteSql)
				.setParameter("idEvent", idEvent)
				.setParameter("txtTodoDesc", txtTodoDesc)
				.setParameter("idUser", userId)
				.executeUpdate();
	}
}
