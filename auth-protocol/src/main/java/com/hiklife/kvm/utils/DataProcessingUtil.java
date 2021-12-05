/*
 * Copyright 2012-2018 CETHIK CETITI All Rights Reserved.
 */
package com.hiklife.kvm.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Hexadecimal conversion
 */

public class DataProcessingUtil {
    private final static Logger log = LoggerFactory.getLogger(DataProcessingUtil.class);
    private static String hexString = "0123456789ABCDEF";
    private static Pattern hexStringPattern = Pattern.compile("[A-Fa-f0-9]+");
    private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    /**
     * Gets chars array from bytes array
     *
     * @param bytes
     * @return
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        return cb.array();
    }

    /**
     * Convert bytes array into String
     *
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] cmdChars = getChars(bytes);
        String cmd = String.valueOf(cmdChars);
        cmd = cmd.trim();
        cmd = cmd.replaceAll("\n", "");
        return cmd;
    }

    /**
     * Convert String into bytes array
     *
     * @param cmd
     * @return
     */
    public static byte[] stringToBytes(String cmd) {
        byte[] msgBytes = null;
        cmd = cmd.trim();
        StringBuilder handler = new StringBuilder();
        handler.append(cmd);
        handler.append('\n');
        cmd = handler.toString();
        log.debug("PS-->Reader:{}", cmd);
        try {
            msgBytes = cmd.getBytes("UTF-8");
            return msgBytes;
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString());
        }
        return null;
    }

    /**
     * Converts hex string to byte array :"EE88" --->{0xEE, 0x88}
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        byte[] combhexBt = null;
        if (StringUtils.isNotBlank(hexString)) {
            if (hexString.length() % 2 != 0) {
                hexString = "0" + hexString;
            }

            if (hexStringPattern.matcher(hexString).find()) {
                String hexStrLow = hexString.toUpperCase();
                byte[] strBytes = hexStrLow.getBytes();
                byte[] hexBytes = new byte[strBytes.length];
                combhexBt = new byte[(strBytes.length) / 2];
                for (int i = 0; i < strBytes.length; i++) {
                    if ((strBytes[i] >= '0') && (strBytes[i] <= '9')) {
                        hexBytes[i] = (byte) (strBytes[i] - 0x30);
                    } else if (strBytes[i] >= 'A' && (strBytes[i] <= 'F')) {
                        hexBytes[i] = (byte) (strBytes[i] - 0x37);
                    }
                }

                for (int i = 0; i < combhexBt.length; i++) {
                    int pos = i * 2;
                    combhexBt[i] = (byte) ((hexBytes[pos] << 4) | hexBytes[pos + 1]);
                }
            }
        }
        return combhexBt;
    }

    /**
     * Converts byte array to hex string :{0x28, 0x07} ---> "0728"
     *
     * @param combhexBt
     * @return
     */
    public static String bytesToHexString(byte[] combhexBt) {
        if (combhexBt == null || combhexBt.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(combhexBt.length * 2);
        for (int i = 0; i < combhexBt.length; i++) {
            sb.append(hexString.charAt((combhexBt[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((combhexBt[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    public static String bytesToHexString(byte[] combhexBt, int off, int length) {
        if (combhexBt == null || combhexBt.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(combhexBt.length * 2);
        for (int i = off; i < length; i++) {
            sb.append(hexString.charAt((combhexBt[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((combhexBt[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * Converts phone number string to byte
     * array:13677778888-->{0x01,0x36,0x77,0x77,0x88,0x88}
     *
     * @param phoneNumber
     * @return
     */
    public static byte[] convertPhoneNumberToBCDArray(String phoneNumber) {
        byte[] combBCDBt = null;
        if (StringUtils.isNotBlank(phoneNumber)) {
            if (NUMBER_PATTERN.matcher(phoneNumber).find()) {
                String addedPhoneNumber = '0' + phoneNumber;
                byte[] strBytes = addedPhoneNumber.getBytes();
                byte[] hexBytes = new byte[strBytes.length];
                combBCDBt = new byte[(strBytes.length) / 2];

                for (int i = 0; i < strBytes.length; i++) {
                    hexBytes[i] = (byte) (strBytes[i] - 0x30);
                }

                for (int i = 0; i < combBCDBt.length; i++) {
                    int pos = i * 2;
                    combBCDBt[i] = (byte) ((hexBytes[pos] << 4) | hexBytes[pos + 1]);
                }
            }
        }
        return combBCDBt;
    }

    /**
     * Converts byte array to phone number string
     * :{0x01,0x36,0x77,0x77,0x88,0x88}-->13677778888
     *
     * @param combBCDBt
     * @return
     */
    public static String convertBCDArrayToPhoneNumber(byte[] combBCDBt) {
        if (combBCDBt == null || combBCDBt.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(combBCDBt.length * 2);
        for (int i = 0; i < combBCDBt.length; i++) {
            sb.append(hexString.charAt((combBCDBt[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((combBCDBt[i] & 0x0f) >> 0));
        }
        return sb.toString().substring(1);
    }

    /**
     * Converts int value to byte array :4556 ---> { 0x45, 0x56}
     *
     * @param value
     * @return
     */
    public static byte[] intToHexBytes(int value) {
        byte[] hexBytes = null;
        if (value > 9999 || value < 0) {
            return hexBytes;
        }

        int a = value / 1000;
        int b = (value % 1000) / 100;
        int c = (value % 100) / 10;
        int d = value % 10;

        hexBytes = new byte[2];
        hexBytes[1] = (byte) (a * 16 + b);
        hexBytes[0] = (byte) (c * 16 + d);

        return hexBytes;
    }

    /**
     * Converts hex byte array to int value: { 0x56, 0x45 } ---> 4556
     *
     * @param byteArray
     * @return
     */
    public static int hexBytesToInt(byte[] byteArray) {
        int a = (byteArray[1] >> 4) & 0x0F;
        int b = byteArray[1] & 0x0F;
        int c = (byteArray[0] >> 4) & 0x0F;
        int d = byteArray[0] & 0x0F;
        return a * 1000 + b * 100 + c * 10 + d;
    }

    public static int hexBytesToInt(byte[] byteArray, int offset) {
        int a = (byteArray[offset + 1] >> 4) & 0x0F;
        int b = byteArray[offset + 1] & 0x0F;
        int c = (byteArray[offset] >> 4) & 0x0F;
        int d = byteArray[offset] & 0x0F;
        return a * 1000 + b * 100 + c * 10 + d;
    }

    /**
     * 以大端模式将int转成byte[]
     */
    public static byte[] intToBytesBig(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以小端模式将int转成byte[]
     *
     * @param value
     * @return
     */
    public static byte[] intToBytesLittle(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以大端模式将byte[]转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 以小端模式将byte[]转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }


    /**
     * Returns the given byte[] as hex encoded string.
     *
     * @param data a byte[] array.
     * @return a hex encoded String.
     */
    public static final String toHex(byte[] data) {
        return toHex(data, 0, data.length);
    }// toHex

    /**
     * Returns a <tt>String</tt> containing unsigned hexadecimal numbers as
     * digits. The <tt>String</tt> will coontain two hex digit characters for
     * each byte from the passed in <tt>byte[]</tt>.<br>
     * The bytes will be separated by a space character.
     * <p/>
     *
     * @param data   the array of bytes to be converted into a hex-string.
     * @param off    the offset to start converting from.
     * @param length the number of bytes to be converted.
     * @return the generated hexadecimal representation as <code>String</code>.
     */
    public static final String toHex(byte[] data, int off, int length) {
        // double size, two bytes (hex range) for one byte
        StringBuffer buf = new StringBuffer(data.length * 2);
        for (int i = off; i < length; i++) {
            // don't forget the second hex digit
            if (((int) data[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) data[i] & 0xff, 16));
            if (i < data.length - 1) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }// toHex

    /**
     * Returns a <tt>byte[]</tt> containing the given byte as unsigned
     * hexadecimal number digits.
     * <p/>
     *
     * @param i the int to be converted into a hex string.
     * @return the generated hexadecimal representation as <code>byte[]</code>.
     */
    public static final byte[] toHex(int i) {
        StringBuffer buf = new StringBuffer(2);
        // don't forget the second hex digit
        if (((int) i & 0xff) < 0x10) {
            buf.append("0");
        }
        buf.append(Long.toString((int) i & 0xff, 16).toUpperCase());
        return buf.toString().getBytes();
    }// toHex


    /**
     * Converts the register (a 16 bit value) into an unsigned short.
     *
     * @param bytes a register as <tt>byte[2]</tt>.
     * @return the unsigned short value as <tt>int</tt>.
     * @see java.io.DataInput
     */
    public static final int registerToUnsignedShort(byte[] bytes) {
        return ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff));
    }// registerToUnsignedShort

    public static final int registerToUnsignedShort(byte[] bytes, int off) {
        return ((bytes[off] & 0xff) << 8 | (bytes[off + 1] & 0xff));
    }// registerToUnsignedShort

    /**
     * Converts the given unsigned short into a register (2 bytes).
     *
     * @param v
     * @return the register as <tt>byte[2]</tt>.
     * @see java.io.DataOutput
     */
    public static final byte[] unsignedShortToRegister(int v) {
        byte[] register = new byte[2];
        register[0] = (byte) (0xff & (v >> 8));
        register[1] = (byte) (0xff & v);
        return register;
    }// unsignedShortToRegister

    /**
     * Converts the given register (16-bit value) into a <tt>short</tt>.
     *
     * @param bytes bytes a register as <tt>byte[2]</tt>.
     * @return the signed short as <tt>short</tt>.
     */
    public static final short registerToShort(byte[] bytes) {
        return (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
    }// registerToShort

    /**
     * Converts the register (16-bit value) at the given index into a
     * <tt>short</tt>.
     *
     * @param bytes a <tt>byte[]</tt> containing a short value.
     * @param idx   an offset into the given byte[].
     * @return the signed short as <tt>short</tt>.
     */
    public static final short registerToShort(byte[] bytes, int idx) {
        return (short) ((bytes[idx] << 8) | (bytes[idx + 1] & 0xff));
    }// registerToShort

    /**
     * Converts the given <tt>short</tt> into a register (2 bytes).
     *
     * @param s
     * @return a register containing the given short value.
     */
    public static final byte[] shortToRegister(short s) {
        byte[] register = new byte[2];
        register[0] = (byte) (0xff & (s >> 8));
        register[1] = (byte) (0xff & s);
        return register;
    }// shortToRegister

    /**
     * Converts a byte[4] binary int value to a primitive int.<br>
     *
     * @param bytes registers as <tt>byte[4]</tt>.
     * @return the integer contained in the given register bytes.
     */
    public static final int registersToInt(byte[] bytes) {
        return (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
    }// registersToInt

    public static final int registersToInt(byte[] bytes, int off) {
        return (((bytes[0 + off] & 0xff) << 24) | ((bytes[1 + off] & 0xff) << 16) | ((bytes[2 + off] & 0xff) << 8) | (bytes[3 + off] & 0xff));
    }// registersToInt

    /**
     * Converts an int value to a byte[4] array.
     *
     * @param v the value to be converted.
     * @return a byte[4] containing the value.
     */
    public static final byte[] intToRegisters(int v) {
        byte[] registers = new byte[4];
        registers[0] = (byte) (0xff & (v >> 24));
        registers[1] = (byte) (0xff & (v >> 16));
        registers[2] = (byte) (0xff & (v >> 8));
        registers[3] = (byte) (0xff & v);
        return registers;
    }// intToRegisters

    /**
     * Converts a byte[8] binary long value into a long primitive.
     *
     * @param bytes a byte[8] containing a long value.
     * @return a long value.
     */
    public static final long registersToLong(byte[] bytes) {
        return ((((long) (bytes[0] & 0xff) << 56) | ((long) (bytes[1] & 0xff) << 48)
                | ((long) (bytes[2] & 0xff) << 40) | ((long) (bytes[3] & 0xff) << 32)
                | ((long) (bytes[4] & 0xff) << 24) | ((long) (bytes[5] & 0xff) << 16)
                | ((long) (bytes[6] & 0xff) << 8) | ((long) (bytes[7] & 0xff))));
    }// registersToLong

    /**
     * Converts a long value to a byte[8].
     *
     * @param v the value to be converted.
     * @return a byte[8] containing the long value.
     */
    public static final byte[] longToRegisters(long v) {
        byte[] registers = new byte[8];
        registers[0] = (byte) (0xff & (v >> 56));
        registers[1] = (byte) (0xff & (v >> 48));
        registers[2] = (byte) (0xff & (v >> 40));
        registers[3] = (byte) (0xff & (v >> 32));
        registers[4] = (byte) (0xff & (v >> 24));
        registers[5] = (byte) (0xff & (v >> 16));
        registers[6] = (byte) (0xff & (v >> 8));
        registers[7] = (byte) (0xff & v);
        return registers;
    }// longToRegisters

    /**
     * Converts a byte[4] binary float value to a float primitive.
     *
     * @param bytes the byte[4] containing the float value.
     * @return a float value.
     */
    public static final float registersToFloat(byte[] bytes) {
        return Float.intBitsToFloat((((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16)
                | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff)));
    }// registersToFloat

    /**
     * Converts a float value to a byte[4] binary float value.
     *
     * @param f the float to be converted.
     * @return a byte[4] containing the float value.
     */
    public static final byte[] floatToRegisters(float f) {
        return intToRegisters(Float.floatToIntBits(f));
    }// floatToRegisters

    /**
     * Converts a byte[8] binary double value into a double primitive.
     *
     * @param bytes a byte[8] to be converted.
     * @return a double value.
     */
    public static final double registersToDouble(byte[] bytes) {
        return Double
                .longBitsToDouble(((((long) (bytes[0] & 0xff) << 56) | ((long) (bytes[1] & 0xff) << 48)
                        | ((long) (bytes[2] & 0xff) << 40) | ((long) (bytes[3] & 0xff) << 32)
                        | ((long) (bytes[4] & 0xff) << 24) | ((long) (bytes[5] & 0xff) << 16)
                        | ((long) (bytes[6] & 0xff) << 8) | ((long) (bytes[7] & 0xff)))));
    }// registersToDouble

    /**
     * Converts a double value to a byte[8].
     *
     * @param d the double to be converted.
     * @return a byte[8].
     */
    public static final byte[] doubleToRegisters(double d) {
        return longToRegisters(Double.doubleToLongBits(d));
    }// doubleToRegisters

    /**
     * Converts an unsigned byte to an integer.
     *
     * @param b the byte to be converted.
     * @return an integer containing the unsigned byte value.
     */
    public static final int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }// unsignedByteToInt

    /**
     * Returs the low byte of an integer word.
     *
     * @param wd
     * @return the low byte.
     */
    public static final byte lowByte(int wd) {
        return (Integer.valueOf(0xff & wd).byteValue());
    }// lowByte

    // TODO: John description.

    /**
     * @param wd
     * @return the hi byte.
     */
    public static final byte hiByte(int wd) {
        return (Integer.valueOf(0xff & (wd >> 8)).byteValue());
    }// hiByte

    // TODO: John description.

    /**
     * @param hibyte
     * @param lowbyte
     * @return a word.
     */
    public static final int makeWord(int hibyte, int lowbyte) {
        int hi = 0xFF & hibyte;
        int low = 0xFF & lowbyte;
        return ((hi << 8) | low);
    }// makeWord

    /**
     * Calculates checksum of GBox message.
     *
     * @param gBoxPacket
     * @return
     */
    public static final byte calculateCheckSum(byte[] gBoxPacket) {
        byte checkSum = 0;
        for (int i = 0; i < gBoxPacket.length - 2; i++) {
            checkSum ^= gBoxPacket[i];
        }

        return checkSum;
    }

    /**
     * Converts BCD value to DEC value.
     *
     * @param bcdValue
     * @return
     */
    public static final int convertBCDToDEC(byte bcdValue) {
        int decValue = ((bcdValue >> 4) & 0x0F) * 10 + (bcdValue & 0x0F);
        return decValue;
    }

    /**
     * Converts BCD bytes to String
     *
     * @param bcdBytes
     * @return
     */
    public static final String convertBCDToString(byte[] bcdBytes) {
        if (bcdBytes == null || bcdBytes.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bcdBytes.length * 2);
        for (int i = 0; i < bcdBytes.length; i++) {
            sb.append(hexString.charAt((bcdBytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bcdBytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * Converts String To BCD
     *
     * @param str
     * @return
     */
    public static final byte[] convertStringToBCD(String str) {
        byte[] combBCDBt = null;
        if (StringUtils.isNotBlank(str)) {
            if (str.length() % 2 != 0) {
                str = "0" + str;
            }

            if (NUMBER_PATTERN.matcher(str).find()) {
                byte[] strBytes = str.getBytes();
                byte[] hexBytes = new byte[strBytes.length];
                combBCDBt = new byte[(strBytes.length) / 2];

                for (int i = 0; i < strBytes.length; i++) {
                    hexBytes[i] = (byte) (strBytes[i] - 0x30);
                }

                for (int i = 0; i < combBCDBt.length; i++) {
                    int pos = i * 2;
                    combBCDBt[i] = (byte) ((hexBytes[pos] << 4) | hexBytes[pos + 1]);
                }
            }
        }
        return combBCDBt;
    }

    /**
     * convert date time string to timestamp
     *
     * @param dateString
     * @param format
     * @return
     */
    public static final Timestamp convertStringToTimestamp(String dateString, String format) {
        if (format == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Date newDate = null;

        try {
            newDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            newDate = null;
        }
        return new Timestamp(newDate.getTime());

    }

    /**
     * Convert TimeStamp To BCD
     *
     * @param time
     * @return
     */
    public static final byte[] convertTimeStampToBCD(Timestamp time) {
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String str = df.format(time);

        return convertStringToBCD(str);
    }


    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }


    /**
     * 其他进制转十进制
     * @param number
     * @return
     */
    public static int scale2Decimal(String number, int scale) {
        checkNumber(number);
        if (2 > scale || scale > 32) {
            throw new IllegalArgumentException("scale is not in range");
        }
        // 不同其他进制转十进制,修改这里即可
        int total = 0;
        String[] ch = number.split("");
        int chLength = ch.length;
        for (int i = 0; i < chLength; i++) {
            total += Integer.valueOf(ch[i]) * Math.pow(scale, chLength - 1 - i);
        }
        return total;

    }

    public static void checkNumber(String number) {
        String regexp = "^\\d+$";
        if (null == number || !number.matches(regexp)) {
            throw new IllegalArgumentException("input is not a number");
        }
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);

    }
}