package us.tx.state.dfps.service.caregiver.serviceimpl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import flexjson.JSONTokener;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringEncrypter;
import us.tx.state.dfps.service.caregiver.service.CaregiverNotificationService;
import us.tx.state.dfps.service.casepackage.dto.MuleEsbResponseDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaregiverAckReq;
import us.tx.state.dfps.service.common.request.CaseSummaryReq;
import us.tx.state.dfps.service.common.util.JNDIUtil;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.*;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implement
 * the service to read, save and delete info for Caretaker Information page Feb
 * 8, 2018- 7:50:38 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
public class CaregiverNotificationServiceImpl implements CaregiverNotificationService {
    private static final Logger log = Logger.getLogger(CaregiverNotificationServiceImpl.class);
    private final static String ENDPOINT_UPLOAD = "/document";
    private final static String ENDPOINT_DOWNLOAD = "/document?documentId={documentId}&nuId={nuId}";
    private final static String ENDPOINT_SEND_ACK_REQ = "/sendAcknowledgement";
    protected final static String URL = "URL";
    protected final static String USERNAME = "USERNAME";
    protected final static String PASSWORD = "PASSWORD";
    public enum EsbConnectionType { DOWNLOAD, ACKNOWLEDGEMENT };

    public MuleEsbResponseDto muleEsbUploadFile(CaseSummaryReq careTakerInfoReq) {
        MuleEsbResponseDto retVal;
        String url = null; // declared here so it's possibly accessible when logging exceptions
        String sha1check = null;
        String responseBody = null;
        try {
            // convert file content to sendable resource
            ByteArrayResource fileWrapper = new ByteArrayResource(Base64.getDecoder().decode(careTakerInfoReq.getEncodedFileData())) {
                @Override
                public String getFilename() {
                    return careTakerInfoReq.getFilename();
                }
            };

            // calcualte sha1 to verify transmission
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            sha1check = byteToHex(digest.digest(Base64.getDecoder().decode(careTakerInfoReq.getEncodedFileData())));

            // build multipart body
            LinkedMultiValueMap map = new LinkedMultiValueMap();
            map.add("file", fileWrapper);
            map.add("caseId", careTakerInfoReq.getIdCase());
            map.add("stageId", careTakerInfoReq.getIdStage());
            map.add("sha1check", sha1check);
            map.add("documentType", careTakerInfoReq.getCdDocType());

            // fetch neubus conection info
            Map<String, String> neubusInfo = lookupNeubusConnection(EsbConnectionType.DOWNLOAD);

            // build headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("client_id", neubusInfo.get(USERNAME));
            headers.add("client_secret", neubusInfo.get(PASSWORD));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            url = neubusInfo.get(URL) + ENDPOINT_UPLOAD;
            RestTemplate restTemplate = configureRestCall(); // cannot be shared between threads.
            HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);

            if (log.isDebugEnabled()) {
                log.debug(StringEscapeUtils.escapeJava(("muleEsbUploadFile : url - " + url + ", caseId - " + careTakerInfoReq.getIdCase() + ", stageId - " +
                    careTakerInfoReq.getIdStage() + ", sha1check - " + sha1check + ", documentType - " +
                    careTakerInfoReq.getCdDocType())));
            }

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);

            retVal = parseStringResponse(responseEntity.getBody());
            if (retVal.getErrorMessage() != null) {
                log.error(StringEscapeUtils.escapeJava("muleEsbUploadFile Error : url - " + url + ", caseId - " + careTakerInfoReq.getIdCase() + ", stageId - " +
                    careTakerInfoReq.getIdStage() + ", sha1check - " + sha1check + ", documentType - " +
                    careTakerInfoReq.getCdDocType() + ", errorMessage - " + retVal.getErrorMessage() + ", errorCode = " +
                    retVal.getErrorCode()));
            }
        } catch (NoSuchAlgorithmException e) {
            retVal = new MuleEsbResponseDto();
            retVal.setErrorMessage(e.getMessage());
            log.error(StringEscapeUtils.escapeJava("muleEsbUploadFile AlgorithmException : url - " + url + ", caseId - " + careTakerInfoReq.getIdCase() + ", stageId - " +
                careTakerInfoReq.getIdStage() + ", sha1check - " + sha1check + ", documentType - " +
                careTakerInfoReq.getCdDocType() + ", exception - " + e.toString()));
        } catch (Exception e) {
            retVal = new MuleEsbResponseDto();
            if (e instanceof RestClientResponseException) {
                retVal.setErrorCode(String.valueOf(((RestClientResponseException)e).getRawStatusCode()));
                retVal.setErrorMessage(((RestClientResponseException)e).getStatusText());
                responseBody = ((RestClientResponseException)e).getResponseBodyAsString();
            } else {
                retVal.setErrorMessage("Unknown Error making remote call.");
            }

            log.error(StringEscapeUtils.escapeJava("muleEsbUploadFile Exception : url - " + url + ", caseId - " + careTakerInfoReq.getIdCase() + ", stageId - " +
                careTakerInfoReq.getIdStage() + ", sha1check - " + sha1check + ", documentType - " +
                careTakerInfoReq.getCdDocType() + ", exception - " + e.toString() + ", response body - " + responseBody));
        }

        return retVal;
    }

    public MuleEsbResponseDto muleEsbDownloadFile(String nuId, String documentId) {
        MuleEsbResponseDto retVal = null;
        String url = null;
        String responseBody = null;
        try {
            // fetch neubus conection info
            Map<String, String> neubusInfo = lookupNeubusConnection(EsbConnectionType.DOWNLOAD);

            // build headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("client_id", neubusInfo.get(USERNAME));
            headers.add("client_secret", neubusInfo.get(PASSWORD));

            url = neubusInfo.get(URL) + ENDPOINT_DOWNLOAD;
            RestTemplate restTemplate = configureRestCall(); // cannot be shared between threads.
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            if (log.isDebugEnabled()) {
                log.debug(StringEscapeUtils.escapeJava("muleEsbDownloadFile : url - " + url + ", documentId - " + documentId + ", nuId - " + nuId));
            }

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class, documentId, nuId);
            retVal = new MuleEsbResponseDto();
            retVal.setFileData(Base64.getEncoder().encodeToString(responseEntity.getBody()));
        } catch (Exception e) {
            retVal = new MuleEsbResponseDto();
            if (e instanceof RestClientResponseException) {
                retVal.setErrorCode(String.valueOf(((RestClientResponseException)e).getRawStatusCode()));
                if (((RestClientResponseException)e).getRawStatusCode() == 404) {
                    retVal.setErrorMessage("Document was not found");
                } else {
                    retVal.setErrorMessage(((RestClientResponseException)e).getStatusText());
                }
                responseBody = ((RestClientResponseException)e).getResponseBodyAsString();
            } else {
                retVal.setErrorMessage("Unknown Error making remote call. " + e.getMessage());
            }
            log.error(StringEscapeUtils.escapeJava("muleEsbDownloadFile Exception: url - " + url + ", documentId - " + documentId + ", nuId - " + nuId + ", error - " + e.toString() + ", response body - " + responseBody));
        }

        return retVal;
    }

    public MuleEsbResponseDto muleEsbSendAckRequest(CaregiverAckReq caregiverAckReq) {
        MuleEsbResponseDto retVal;
        String url = null;
        String json = null;
        String responseBody = null;
        try {
            // fetch neubus conection info
            Map<String, String> neubusInfo = lookupNeubusConnection(EsbConnectionType.ACKNOWLEDGEMENT);

            // build headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("client_id", neubusInfo.get(USERNAME));
            headers.add("client_secret", neubusInfo.get(PASSWORD));
            headers.setContentType(MediaType.APPLICATION_JSON);

            url = neubusInfo.get(URL) + ENDPOINT_SEND_ACK_REQ;
            RestTemplate restTemplate = configureRestCall(); // cannot be shared between threads.

            // for troubleshooting, stringify the json, but if it fails, it should not affect normal operation
            json = null;
            try {
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                json = ow.writeValueAsString(caregiverAckReq);
                log.debug(StringEscapeUtils.escapeJava("muleEsbSendAckRequest : url - " + url + ", json - " + json));
            } catch (JsonProcessingException e) {
                log.debug("muleEsbSendAckRequest : url - " + url + ", JsonProcessingException " + e.toString());
            }

            HttpEntity<CaregiverAckReq> httpEntity = new HttpEntity<>(caregiverAckReq, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
      
            retVal = parseStringResponse(responseEntity.getBody());
            if (retVal.getErrorMessage() != null) {
                log.error(StringEscapeUtils.escapeJava("muleEsbSendAckRequest : url - " + url + ", json - " + json +", errorMessage " + retVal.getErrorMessage()));
            }
        } catch (Exception e) {
            retVal = new MuleEsbResponseDto();
            if (e instanceof RestClientResponseException) {
                try{
                    retVal = parseStringResponse(((RestClientResponseException)e).getResponseBodyAsString());
          if (retVal.getErrorMessage() != null) {
            log.error(
                StringEscapeUtils.escapeJava(
                    "muleEsbSendAckRequest : url - "
                        + url
                        + ", json - "
                        + json
                        + ", errorMessage-> "
                        + retVal.getErrorMessage()));
          }
                }catch (Exception ex){
                    retVal.setErrorCode(String.valueOf(((RestClientResponseException)e).getRawStatusCode()));
                    retVal.setErrorMessage(((RestClientResponseException)e).getStatusText());
                    responseBody = ((RestClientResponseException)e).getResponseBodyAsString();
          log.error(
              StringEscapeUtils.escapeJava(
                  "muleEsbSendAckRequest Exception : url - "
                      + url
                      + ", json - "
                      + json
                      + ", errorMessage - "
                      + e.toString()
                      + ", response body - "
                      + responseBody));
                }
            } else {
                retVal.setErrorMessage("Unknown Error making remote call.");
        log.error(
            StringEscapeUtils.escapeJava(
                "muleEsbSendAckRequest : url - "
                    + url
                    + ", json - "
                    + json
                    + ", errorMessage "
                    + retVal.getErrorMessage()));
            }
        }

        return retVal;
    }

    public RestTemplate configureRestCall() {
        // RestTemplate was shared, but was causing concurrent modification exceptions is rare cases.
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(300000);
        httpRequestFactory.setConnectTimeout(300000);
        httpRequestFactory.setReadTimeout(300000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        return restTemplate;
    }

    // because JNDIUtil uses static methods we cannot inject mocks for testing, so this is untestable in unit test.
    protected Map<String, String> lookupNeubusConnection(EsbConnectionType esbConnectionType) {
        HashMap retVal = new HashMap<String, String>();
        String rawJndi;
        if (esbConnectionType == EsbConnectionType.DOWNLOAD) {
            rawJndi = JNDIUtil.lookUp(ServiceConstants.MULE_ESB_URL_DOWNLOAD);
        } else { // esbConnectionType == EsbConnectionType.ACKNOWLEDGEMENT
            rawJndi = JNDIUtil.lookUp(ServiceConstants.MULE_ESB_URL_ACKNOWLEDGEMENT);
        }
        String[] jndiTokens = rawJndi.split(",");
        retVal.put(URL, jndiTokens[0]);
        retVal.put(USERNAME, jndiTokens[1]);
        retVal.put(PASSWORD, decrypt(jndiTokens[2]));
        return retVal;
    }

    private MuleEsbResponseDto parseStringResponse(String rawResponse) {
        MuleEsbResponseDto res = new MuleEsbResponseDto();
        HashMap<String, Object> parsedResponse = (HashMap<String, Object>) new JSONTokener(rawResponse).nextValue();
        if (parsedResponse.containsKey("data")) { // upload success
            HashMap<String, Object> dataMap = (HashMap<String, Object>) parsedResponse.get("data");
            res.setFilename((String) dataMap.get("file_name"));
            res.setNuid((String) dataMap.get("nuid"));
            res.setDocumentId((String) dataMap.get("documentId"));
        } else if (parsedResponse.containsKey("success")) { // technically upload success, but also both Ack Req cases
            if ((Boolean)parsedResponse.get("success")) {
                // response body is unused, but lets capture results in case we use them some day.
                res.setSuccess((Boolean)parsedResponse.get("success"));
                res.setNuid((String)parsedResponse.get("nuid"));
                res.setDocumentId((String)parsedResponse.get("documentId"));
            } else {
                res.setErrorMessage((String) parsedResponse.get("message"));
            }
        } else {
            res.setErrorCode((String) parsedResponse.get("code")); // upload fail
            if (parsedResponse.get("message") != null && parsedResponse.get("message") instanceof List) {
                res.setErrorMessage(((List<String>) parsedResponse.get("message")).get(0));
            }
        }
        return res;
    }

    // cannot be private if we want to test it.
    protected String decrypt(String encryptedString) {
        String retVal = null;
        try {
            StringEncrypter encrypter = new StringEncrypter();
            retVal = encrypter.decrypt(encryptedString);
        } catch (Exception e) {
            log.warn(StringEscapeUtils.escapeJava("Exception while decrypting: " + e.getMessage()));
            // do nothing and let null return.
        }
        return retVal;
    }

    protected String encrypt(String plainString) {
        try {
            StringEncrypter encrypter = new StringEncrypter();
            return encrypter.encrypt(plainString);
        } catch (Exception e) {
            log.warn(StringEscapeUtils.escapeJava("Exception while encrypting: " + e.getMessage()));
            // do nothing and let null return.
        }
        return null;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
