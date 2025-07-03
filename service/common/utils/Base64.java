package us.tx.state.dfps.service.common.utils;

public class Base64 {
	private static final int DEFAULT_LINE_WIDTH = 76;
	private static final char NEWLINE = '\n';

	/**
	 * decode the given encoded String to the original raw data
	 *
	 * @param encoded
	 * @return the decoded data array
	 */
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

	/**
	 * encode the given raw data using the default line width
	 *
	 * @param rawData
	 *            the raw data array
	 * @return the encoded String
	 */
	public static String encode(byte[] rawData) {
		return encode(rawData, DEFAULT_LINE_WIDTH);
	}

	/**
	 * encode the given raw data using the given line width
	 *
	 * @param rawData
	 *            the raw data array
	 * @param width
	 *            the width at which to wrap lines
	 * @return the encoded String
	 */
	public static String encode(byte[] rawData, int width) {
		// compute the length of the encrypted data
		int encryptedLength = ((rawData.length + 2) / 3) * 4;

		// normalize line width, and adjust the encrypted length to match
		if (width > 3) {
			width -= width % 4;
			encryptedLength += 2 * (encryptedLength / width);
		} else {
			width = Integer.MAX_VALUE;
		}

		// allocate space to hold the encrypted data
		char[] encoded = new char[encryptedLength];

		// a counter to iterate through the encoded array as we fill it
		int count = 0;

		// the number of complete triples in the raw data
		int modLen = (rawData.length / 3) * 3;

		// start the outLen at four, since we wrote the length first...
		int outLen = 0;

		// step through the rawData
		for (int i = 0; i < modLen; i += 3, outLen += 4) {
			// wrap the lines at the given width
			if (outLen + 4 > width) {
				encoded[count++] = NEWLINE;
				outLen = 0;
			}

			// pack the next three bytes of the raw data into an int
			int block = ((rawData[i] & 0xff) << 16) + ((rawData[i + 1] & 0xff) << 8) + (rawData[i + 2] & 0xff);

			// encode the int into the array
			for (int j = 3; j >= 0; j--) {
				encoded[count + j] = ENCODE[(block & 0x3f)];
				block >>= 6;
			}

			// update the count
			count = count + 4;
		}

		// when we go past a width boundary, write a newline
		if (outLen >= width) {
			encoded[count++] = NEWLINE;
		}

		// if there are non-aligned bytes at the end...
		if (modLen < rawData.length) {
			// compute how much slack there is...
			int slack = rawData.length - modLen - 1;

			// write the actual data
			int block = 0;
			for (int i = 0; i < 3; i++) {
				byte b = (modLen + i < rawData.length) ? rawData[modLen + i] : 0;

				block <<= 8;
				block += (b < 0) ? b + 256 : b;
			}
			for (int i = 3; i >= 0; i--) {
				int sixBit = block & 0x3f;

				encoded[count + i] = ENCODE[sixBit];
				block >>= 6;
			}

			// pad the tail of the array with '='
			if (slack < 2) {
				encoded[count + 3] = '=';
				if (slack < 1) {
					encoded[count + 2] = '=';
				}
			}

			// update the byte count
			count = count + 4;
		}

		// construct a string from the encoded data, and return it.
		return new String(encoded, 0, count);
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
