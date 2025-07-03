package us.tx.state.dfps.service.utility.dao;

import org.jsoup.*;
import org.jsoup.parser.*;
import org.jsoup.select.*;
import org.springframework.util.*;
import us.tx.state.dfps.common.exception.*;
import us.tx.state.dfps.phoneticsearch.IIRHelper.*;
import us.tx.state.dfps.service.common.util.pkware.DCL.*;

import java.util.*;

public class BlobUtil {

    public static final String CHARACTER_ENCODING = "windows-1252";

    // adapted from us.tx.state.dfps.forms.utils.FormsUtility
    public static String unwrapBlobWithoutDecode(byte[] blobData) {
        String data = null;
        try {
            data = CompressionHelper.decompressData(blobData).toString(CHARACTER_ENCODING);
            data = FormattingHelper.prepareNarrativeForTextArea(data.toString());
        } catch (Exception e) {
            FormsException formsException = new FormsException(e.getMessage());
            formsException.initCause(e);
            throw formsException;
        }
        return data;
    }

    // adapted from us.tx.state.dfps.forms.utils.FormsUtility
    public static Hashtable<String, String> readDOMXML(byte[] xmlData, String argNodeList) {
        Hashtable<String, String> hashtable = new Hashtable();
        try {
            org.jsoup.nodes.Document blobDocument = Jsoup.parse(new String(xmlData), "", Parser.xmlParser());
            blobDocument.outputSettings(new org.jsoup.nodes.Document.OutputSettings().prettyPrint(false));
            Elements blobElements = blobDocument.select("userEdit");
            if (!ObjectUtils.isEmpty(blobElements)) {
                for (org.jsoup.nodes.Element blobelement : blobElements) {
                    String fieldName = "";
                    String fieldValue = "";
                    fieldName = blobelement.getElementsByTag("fieldName").text();
                    fieldValue = (ObjectUtils.isEmpty(blobelement.getElementsByTag("fieldValue").text())) ? ""
                            : new String(decode(blobelement.getElementsByTag("fieldValue").text()),CHARACTER_ENCODING);
                    hashtable.put(fieldName, fieldValue);
                }
            }

        } catch (Exception ex) {
            FormsException formsException = new FormsException(ex.getMessage());
            formsException.initCause(ex);
            throw formsException;
        }
        return hashtable;
    }

    // adapted from us.tx.state.dfps.forms.utils.FormsUtility
    public static byte[] decode(String encoded) {
        // cache the length of the encoded string
        final int encodedLength = encoded.length();

        // calculate the width of a single line
        int width = 0;
        for (width = 0; width < encodedLength; width++) {
            if (encoded.charAt(width) <= ' ') {
                break;
            }
        }

        // calculate how much padding is on the end of the encoded string
        int pad = 0;
        for (int i = encodedLength - 1; (i > 0) && (encoded.charAt(i) == '='); i--) {
            pad++;
        }

        // calculate the size of the decoded data
        int decodedLength = 3 * (encodedLength - (encodedLength / (width + 1)) - pad) / 4;

        // allocate space for the decoded array
        byte[] decoded = new byte[decodedLength];

        // step through the encoded data, four bytes at a time
        for (int i = 0, rawIndex = 0; i < encodedLength; i += 4, rawIndex += 3) {
            // if we encounter white space at the end of a line, eat it.
            while (i < encodedLength && encoded.charAt(i) <= ' ') {
                i++;
            }
            if (i >= encodedLength) {
                break;
            }

            // decode the next four bytes
            int block = (DECODE[encoded.charAt(i)] << 18) + (DECODE[encoded.charAt(i + 1)] << 12)
                    + (DECODE[encoded.charAt(i + 2)] << 6) + DECODE[encoded.charAt(i + 3)];

            // unpack the block into three bytes in the decoded array
            for (int j = 2; j >= 0; j--) {
                if (rawIndex + j < decoded.length) {
                    decoded[rawIndex + j] = (byte) (block & 0xff);
                }
                block >>= 8;
            }
        }

        // return the decoded array
        return decoded;
    }

    // setup static arrays, so encoding/decoding a byte is O(1)
    protected static final char[] ENCODE = new char[0xff];
    protected static final char[] DECODE = new char[0xff];

    static {
        for (char c = 'A'; c <= 'Z'; c++) {
            DECODE[c] = (char) (c - 'A');
            ENCODE[c - 'A'] = c;
        }

        for (char c = 'a'; c <= 'z'; c++) {
            DECODE[c] = (char) (c - 'a' + 26);
            ENCODE[c - 'a' + 26] = c;
        }

        for (char c = '0'; c <= '9'; c++) {
            DECODE[c] = (char) (c - '0' + 52);
            ENCODE[c - '0' + 52] = c;
        }

        DECODE['+'] = 62;
        ENCODE[62] = '+';

        DECODE['/'] = 63;
        ENCODE[63] = '/';

        DECODE['='] = 0;
    }
}
