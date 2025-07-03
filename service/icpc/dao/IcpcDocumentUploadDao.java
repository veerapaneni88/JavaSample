package us.tx.state.dfps.service.icpc.dao;

import us.tx.state.dfps.service.icpcdocument.dto.IcpcDocumentDto;
import us.tx.state.dfps.service.icpcdocument.dto.IcpcFileStorageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

public interface IcpcDocumentUploadDao {

	IcpcDocumentDto fetchDocument(Long idDocument);

	IcpcFileStorageDto fetchFileStorage(Long idFileStorage);

	IcpcDocumentDto saveDocument(IcpcDocumentDto icpcDocumentDto,Boolean indInsert);

	IcpcFileStorageDto saveFileStorage(IcpcFileStorageDto IcpcFileStorageDto);

	void deleteDocument(Long idDocument);

	Long createAlert(TodoDto todoDto);
	
	/**
	 * Method Name: createTask Method Description: This method create task in TODO
	 * Table. EJB Name : ToDoBean.java
	 * 
	 * @param todoDto
	 * @return long
	 */
	Long createTask(TodoDto todoDto);

	/**
	 *  Method Name: updateTaskComplete
	 *  Method Description: This method to update the task completed
	 *
	 * @param idEvent
	 * @param txtTodoDesc
	 * @param userId
	 */
	void updateTaskComplete(Long idEvent, String txtTodoDesc, Long userId);

}
