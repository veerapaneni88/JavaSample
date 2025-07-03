package us.tx.state.dfps.service.approval.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.approval.service.GoldWebService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.JNDIUtil;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * * This class creates the "envelope" for the DFPS form and handles
 * * the details of sending to / receiving from the MuleSoft for DADS webservice
 */
@Service
public class DADSWebServiceClient {

    private static final Logger log = Logger.getLogger(DADSWebServiceClient.class);

    @Autowired
    GoldWebService goldWebService;

    /**
     * Method helps to send the xml data to MUleSoft and get the response
     *
     * @param formXml - form xml
     * @return - gold communication
     */
    public GoldCommunicationDto sendForm(String formXml, Long webTransId) {
        log.info("Entering to DADSWebServiceClient:sendForm");
        String returnMessage;
        String xmlRequest = null;
        GoldCommunicationDto goldCommunicationDto = new GoldCommunicationDto();

        try {
            String urlString = JNDIUtil.lookUpString(ServiceConstants.GOLD_INTERFACE_MULESOFT_URL) +"api/guardianship/referral";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String trimXml = formXml.replace("\r", "");
            //String XML request
            xmlRequest = MD5Util.buildXMLRequest(trimXml);

            buildHeadersForMuleSoft(xmlRequest, connection, webTransId);
            // send request
            OutputStream outStream = connection.getOutputStream();
            DataOutputStream output = new DataOutputStream(outStream);
            output.writeBytes(xmlRequest);
            output.close();

            InputStream inStream = connection.getInputStream();
            DataInputStream input = new DataInputStream(inStream);
            byte[] workBuf = new byte[10240];
            int bytesRead;

            bytesRead = getBytesRead(input, workBuf);

            input.close();

            // check for not enough data read back
            if (bytesRead == workBuf.length)
                log.info("Internal buffer for reading response is too small. Current size: "
                        + workBuf.length);

            // truncate work buffer
            byte[] respBuf = new byte[bytesRead];
            System.arraycopy(workBuf, 0, respBuf, 0, bytesRead);
            returnMessage = new String(respBuf);

        } catch (java.net.SocketTimeoutException stex) {
            goldCommunicationDto.setReturnCode(1);
            returnMessage = stex.getMessage();
            log.info(stex);
        } catch (ConnectException cx) {
            goldCommunicationDto.setReturnCode(2);
            returnMessage = cx.getMessage();
            log.info(cx.getMessage());
        } catch (IOException ie) {
            goldCommunicationDto.setReturnCode(2);
            returnMessage = ie.getMessage();
            log.info(ie.getMessage());
        }
        goldCommunicationDto.setSentXml(xmlRequest);
        goldCommunicationDto.setReturnEnvelope(returnMessage);
        log.info("Exit from DADSWebServiceClient:sendForm");
        return goldCommunicationDto;
    }


    /**
     * Method Helps to build MuleSoft requested Headers information.
     *
     * @param xmlRequest - xml string
     * @param connection http connection
     */
    private void buildHeadersForMuleSoft(String xmlRequest, HttpURLConnection connection, Long webTransId) {
        log.info("Entering to DADSWebServiceClient:buildHeadersForMuleSoft");
        // build HTTP request header
        String xRequestTraceId = "APS_" + webTransId + "_" + System.currentTimeMillis();
        connection.setRequestProperty("Accept", "application/xml");
        connection.setRequestProperty("Content-type", "application/xml");
        connection.setRequestProperty("Content-length", String.valueOf(xmlRequest.length()));
        connection.setRequestProperty("client_id", JNDIUtil.lookUpString(ServiceConstants.GOLD_INTERFACE_CLIENT_ID));
        connection.setRequestProperty("client_secret", JNDIUtil.lookUp(ServiceConstants.GOLD_INTERFACE_CLIENT_SECRET));
        connection.setRequestProperty("x-request-trace-id", xRequestTraceId);
        log.info("x-request-trace-id for track issues with MuleSoft: " + xRequestTraceId);
        log.info("Exit from DADSWebServiceClient:buildHeadersForMuleSoft");
    }

    /**
     * Method helps to convert to Data input streams to bytes
     *
     * @param input - data input stream
     * @param workBuf bytes data
     * @return - total number off bytes read
     * @throws IOException
     */
    private int getBytesRead(DataInputStream input, byte[] workBuf) throws IOException {
        int bytesRead;
        for (bytesRead = 0; bytesRead < workBuf.length; ++bytesRead) {
            try {
                workBuf[bytesRead] = input.readByte();
            } catch (EOFException ex) {
                break;
            }
        }
        return bytesRead;
    }

}
