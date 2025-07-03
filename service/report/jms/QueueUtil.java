package us.tx.state.dfps.service.report.jms;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import us.tx.state.dfps.common.exception.ReportsException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.JNDIUtil;

@Component
public class QueueUtil {
	private static final Logger log = Logger.getLogger(QueueUtil.class);
		
	public static final String CONNECTION_FACTORY_JNDI = "jms/senderQCF";
	public static final String QUEUE_JNDI = "jms/reportQueue";
	public static final String REPORT_LIST_ID_PROP = "idReportList";
	

	/**
	 * Method Name: sendMessage 
	 * Method Description: This method sends message to JMS queue
	 * 
	 * @param messageToSend
	 * @return String Success Message
	 */
	public String sendMessage(String messageToSend) throws JMSException, NamingException, ReportsException {
		Connection connection = null;
		String errMessage = ServiceConstants.ERR_OCC_PLS_TRY_AGAIN;
		
			// Initialize connection factory
			QueueConnectionFactory quefactory = (QueueConnectionFactory) JNDIUtil.lookUpObject(CONNECTION_FACTORY_JNDI);
			connection = quefactory.createQueueConnection();
			// connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.info("Queue Session: Created");

			// Initialize queue
			Queue queue = (Queue) JNDIUtil.lookUpObject(QUEUE_JNDI);
			log.info("Queue object found");

			MessageProducer producer = session.createProducer(queue);

			// Create and Send Text message
			TextMessage message = session.createTextMessage();
			message.setText(messageToSend);
			message.setStringProperty(REPORT_LIST_ID_PROP, REPORT_LIST_ID_PROP);
			log.info("Sending message: " + message.getText());
			producer.send(message);
			producer.send(session.createMessage());

			// to browse msgs written in the queue
			QueueBrowser browser = session.createBrowser(queue);
			Enumeration<?> msgs = browser.getEnumeration();

			// If browser message has content, print to log
			if (!msgs.hasMoreElements()) {
				log.info("No messages in queue");
			} else {
				while (msgs.hasMoreElements()) {
					Message tempMsg = (Message) msgs.nextElement();
					log.info("Message: " + tempMsg);
				}
			}
			errMessage = ServiceConstants.SUCCESS;
		
		return errMessage;
	}	
}
