package com.snow.wolf.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mservice.sdk.model.model.Otp;
import com.mservice.sdk.model.model.PayTrans;
import com.mservice.sdk.model.model.QrCodePayment;
import com.mservice.sdk.model.tranhis.MomoUserDataMsg;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by toanmai on 05/05/2017.
 */
public class DataUtil {

    private static final char BELL_CHAR = 7;
    private static final String BELL = "" + BELL_CHAR;

    private static final char GS_CHAR = 29;
    private static final String GS = "" + GS_CHAR;

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static Random rand = new Random();

    private static final int MAX_RANDOM_TIME = 500;

    public static int strToInt(String input) {
        if (input == null) return 0;
        input = input.trim();
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String decode(String encodedStr) {

        if (encodedStr == null || encodedStr.isEmpty()) {
            return "";
        }
        //decoding byte array into base64
        byte[] decoded = Base64.decodeBase64(encodedStr.getBytes());
        return new String(decoded);

    }

    public static String encode(String orgStr) {
        //encoding  byte array into base 64
        byte[] encoded = Base64.encodeBase64(orgStr.getBytes());
        return new String(encoded);
    }

    public static long strToLong(String input) {
        if (input == null) return 0;
        input = input.trim();
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isJsonArray(String s) {
        JsonArray resJson = null;
        try {
            resJson = new JsonArray(s);
        } catch (Exception e) {
        }
        return resJson != null;
    }

    public static boolean isJsonObject(String s) {
        JsonObject resJson = null;
        try {
            resJson = new JsonObject(s);
        } catch (Exception e) {
        }
        return resJson != null;
    }

    public static long calculateFeeMethod(long staticFee, long dynamicFee, long transAmount)
    {
        long fee = 0;
        try
        {
            NumberFormat formatter = new DecimalFormat("#0.00");
            String amount = formatter.format(staticFee + (dynamicFee / 100) * transAmount);
            fee = (long) Math.ceil(Double.parseDouble(amount));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fee = (long) Math.ceil(Double.parseDouble(String.valueOf((staticFee + (dynamicFee / 100) * transAmount))));
        }
        return fee;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String signHmacSHA256(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(hash);
    }

    public static String signHmacSHA256(String data, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
        return toHexString(rawHmac);
    }

    public static Map<String,String> convertTransToHashMap(PayTrans messageData) {
        ObjectMapper oMapper = new ObjectMapper();
        Map<String,String> tranMap = oMapper.convertValue(messageData, Map.class);
        return tranMap;
    }

    public static Map<String,String> convertOrderTransToHashMap(JsonObject messageData) {
        Map<String,String> tranMap = messageData.mapTo(Map.class);
        return tranMap;
    }

    public static HashMap<String,String> convertOtpDataToHashMap(Otp messageData) {
        ObjectMapper oMapper = new ObjectMapper();
        HashMap<String,String> tranMap = oMapper.convertValue(messageData, HashMap.class);
        return tranMap;
    }

    public static HashMap<String,String> convertMoMoUserDataToHashMap(MomoUserDataMsg messageData) {
        ObjectMapper oMapper = new ObjectMapper();
        HashMap<String,String> tranMap = oMapper.convertValue(messageData, HashMap.class);
        return tranMap;
    }

    public static Map<String,String> convertQrCodeToHashMap(QrCodePayment qrCodePay) {
        ObjectMapper oMapper = new ObjectMapper();
        HashMap<String,String> qrTrans = oMapper.convertValue(qrCodePay, HashMap.class);
        return qrTrans;
    }

    public static Map<String,String> convertObjectToHashMap(Object qrCodePay) {
        ObjectMapper oMapper = new ObjectMapper();
        HashMap<String,String> dataConvert = oMapper.convertValue(qrCodePay, HashMap.class);
        return dataConvert;
    }

    public static MomoUserDataMsg convertMapToMoMoUser(Map<String, String> mapData) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(mapData);
        final MomoUserDataMsg momoUserDataMsg =gson.fromJson(jsonElement, MomoUserDataMsg.class);
        return momoUserDataMsg;
    }

    public static String createRequestID() {
        return String.format("%s%s", System.currentTimeMillis(), RandomStringUtils.randomNumeric(4));
    }

    public static String createTransId() {
        return String.format("%s%s", System.currentTimeMillis(), RandomStringUtils.randomNumeric(1));
    }

    public static String autoGenSessionKey() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public static boolean isValidJsonObject(String text) {
        try {
            JsonObject jsonObject = new JsonObject(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String hashSHA256(String input) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input.getBytes());
            BigInteger dis = new BigInteger(1, sha.digest());
            String result = dis.toString(16);
            if (!result.startsWith("0") && result.length() < 64) {
                result = "0" + result;
            }
//            return result;
            return toHexString(result.getBytes());
        } catch (Exception ex) {
            return "";
        }
    }

    public static String socketHashSHA256(String input) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input.getBytes());
            BigInteger dis = new BigInteger(1, sha.digest());
            String result = dis.toString(16);
            if (!result.startsWith("0") && result.length() < 64) {
                result = "0" + result;
            }
            return result;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String signSHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(text.getBytes("UTF-8"), 0, text.length());
        byte[] hashValue = digest.digest();
        return toHexString(hashValue).toUpperCase();
    }

    public static String formatAmount(long amount) {

        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String hashPhone(String phoneNumber) {
        return phoneNumber.length() < 7 ? "" : phoneNumber.replace(phoneNumber.substring(4,7), "***");
    }

    public static String randomOtp() {
        return RandomStringUtils.randomNumeric(6);
    }

    public static JsonObject parseLotteSocketData(String data){
        JsonObject joReceive = new JsonObject();
        String[] listGS = data.split(GS);
        String[] listBELL;
        for(String gs : listGS){
            listBELL = gs.split(BELL);
            if(listBELL.length > 1){

                joReceive.put(listBELL[0].trim(), listBELL[1].trim());
            }
        }
        return joReceive;
    }

    public static String convertToRawString(String text) {
        try {
            String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static boolean isNullOrEmpty(Object value){
        if(value == null){
            return true;
        }

        return "".equals(value.toString().trim());
    }

    /**
     * Generate ID with format yyyyMMddHHmmss and append random number at the end
     */
    public static String genFormatedId(int subfixSize) {
    	int random = Integer.parseInt(RandomStringUtils.randomNumeric(subfixSize));
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = df.format(new Date());
		return String.format(today + "%0" + subfixSize + "d", random);
    }
}
