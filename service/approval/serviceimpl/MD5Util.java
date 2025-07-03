package us.tx.state.dfps.service.approval.serviceimpl;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.JNDIUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


public class MD5Util {

    private static final String APS = "aps";
    private static final String CPS = "cps";
    private static final String APS_DOC_NAME = "APS_GUARDIANSHIP_REFERRAL_FORM";
    private static final String CPS_DOC_NAME = "CPS_GUARDIANSHIP_REFERRAL_FORM";
    private static final String SECRET_KEY =  JNDIUtil.lookUpString(ServiceConstants.GOLD_INTERFACE_ENCRYPTION_KEY);
    private static final String ALGORITHM = "SHA-512";

    /**
     * Method helps to convert the String data to Document
     *
     * @param raw - String xml data
     * @return - xml data converted to document
     */
    public static Document stringToXMLDoc(String raw) {
        Document doc = null;

        try {
            DocumentBuilderFactory docBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder =
                    docBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream bas = new
                    ByteArrayInputStream(raw.getBytes());
            InputSource is = new InputSource(bas);
            doc = docBuilder.parse(is);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return doc;
    }

    /**
     * Determine whether aps or cps from form xml
     *
     * @param formXml - generated xml
     * @return - return program name as string
     */
    private static String getProgram(String formXml) {
        Document doc = stringToXMLDoc(formXml);
        Node firstChild = doc.getFirstChild();
        String docName = firstChild.getNodeName();
        String docSpec = "";

        if (docName.indexOf(APS_DOC_NAME) > -1)
            docSpec = APS;
        else if (docName.indexOf(CPS_DOC_NAME) > -1)
            docSpec = CPS;

        return docSpec;
    }

    /**
     * Method helps to create signature and append to document based on generated xml payload
     *
     * @param serviceName    - type of service
     * @param serviceRequest - request
     * @param payload        - node xml
     * @return - updated document with web-request
     */
    private static Document buildXMLRequest(String serviceName, String serviceRequest, Node payload) {
        DocumentBuilder db;
        Document doc;
        try {
            // create XML doc
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = db.newDocument();

            // add root
            Node root = doc.createElement("web-request");
            doc.appendChild(root);

            // add <request-auth>
            Element reqNode = doc.createElement("request-auth");
            reqNode.setAttribute("requestor", "impact");
            reqNode.setAttribute("signature", getDigitalSignature(payload, "impact", SECRET_KEY, ALGORITHM));
            root.appendChild(reqNode);

            // add <service>
            Element serviceNode = doc.createElement("service");
            serviceNode.setAttribute("name", serviceName);
            serviceNode.setAttribute("request", serviceRequest);
            root.appendChild(serviceNode);

            // add payload
            serviceNode.appendChild(doc.importNode(payload.getFirstChild(), true));

            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Unable to build request\n", e);
        }
    }

    /**
     * Method helps to add the required xml element like servie
     *
     * @param formXml string xml
     * @return after updating the required service xml elements xml string
     */
    public static String buildXMLRequest(String formXml) {

        Document payload = stringToXMLDoc(formXml);
        Document req = buildXMLRequest(getProgram(formXml), "update", payload);
        return getXMLString(req);
    }

    /**
     * Method helps to convert the bytes data into
     *
     * @param b
     * @return
     */
    private static String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++)
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        return result;
    }

    private static String getXMLStringWithoutXMLHeader(Node xmlNode) {
        // remove the <?xml...?> line
        return Pattern.compile("<\\?xml.+?<", Pattern.DOTALL).matcher(getXMLString(xmlNode)).replaceFirst("<");
    }

    /**
     * Method helps to generate the signature with passing secret key and algorithm
     *
     * @param xmlNode
     * @param requestor
     * @param secretKey
     * @param algorithm
     * @return
     */
    public static String getDigitalSignature(Node xmlNode, String requestor, String secretKey, String algorithm) {
        // get text of service request node
        String requestNodeText = getXMLStringWithoutXMLHeader(xmlNode);
        if (requestNodeText == null)
            return "";

        // concatenate the secret key
        requestNodeText += secretKey;

        // build digital signature for this
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(requestNodeText.getBytes());
            String hash = getHexString(md.digest());
            return hash.trim();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm problem while creating digital signature\n", e);
        }
    }

    private static String getXMLString(Node xmlNode) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xmlNode);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            throw new RuntimeException("Unable to get XML string from Doc\n", e);
        }
    }


}