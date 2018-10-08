package com.snow.wolf.bot.database.redis;

import com.mservice.sdk.model.model.*;
import com.mservice.sdk.model.request.StandardPaymentRequest;
import com.mservice.sdk.model.response.StandardPaymentResponse;
import com.mservice.sdk.model.tranhis.MomoUserDataMsg;
import com.mservice.sdk.model.util.Constant;
import com.snow.wolf.bot.config.MainConfig;
import com.snow.wolf.util.DataUtil;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * Created by khoanguyen on 03/01/2017.
 */
public class RedisProcess {

    //    protected Jedis jedisv2;
    protected Jedis  jedisv1;

    private Jedis getJedis() {
        if (jedisv1 == null) {
            jedisv1 = new Jedis(
                    MainConfig.getRedisHost(),
                    MainConfig.getRedisPort(),
                    MainConfig.getRedisTimeout());
            if (!MainConfig.getRedisPassword().isEmpty()) {
                jedisv1.auth(MainConfig.getRedisPassword());
            }
        }
        return jedisv1;
    }

    public PhonesObject getCustomerPhoneInfo(String number) {
        getJedis().connect();
        getJedis().select(MainConfig.getRedisBeSelectDb());
        Map<String, String> userInfos = getJedis().hgetAll(Constant.BE_USER + number);
        getJedis().close();
        PhonesObject phoneObj = new PhonesObject();
        MomoUserDataMsg momoUserDataMsg = DataUtil.convertMapToMoMoUser(userInfos);
        if (momoUserDataMsg == null) {
            return null;
        }

        phoneObj.isNamed = momoUserDataMsg.isNamed;
        phoneObj.number = DataUtil.strToInt(number);
        phoneObj.lastImei = momoUserDataMsg.lastImei;
        phoneObj.bank_code = momoUserDataMsg.bankCode;
        phoneObj.cardId = momoUserDataMsg.personalId;
        phoneObj.bankPersonalId = momoUserDataMsg.bankCardId;
        phoneObj.address = momoUserDataMsg.address;
        phoneObj.appCode = momoUserDataMsg.appVer;
        phoneObj.appVer = momoUserDataMsg.appCode;
        phoneObj.createdDate = DataUtil.strToLong(momoUserDataMsg.createDate);
        phoneObj.name = momoUserDataMsg.name;
        phoneObj.email = momoUserDataMsg.email;
        phoneObj.bank_name = momoUserDataMsg.bankName;
        phoneObj.bank_account = momoUserDataMsg.bankAccount;
        phoneObj.phoneOs = momoUserDataMsg.phoneOs;
        phoneObj.extra = momoUserDataMsg.extra;
        phoneObj.sessionKey = momoUserDataMsg.lastSessionKey;
        phoneObj.webSessionKey = momoUserDataMsg.webLastSessionKey;
        phoneObj.deleted = momoUserDataMsg.isDeleted;
        phoneObj.isReged = momoUserDataMsg.isReged;
        phoneObj.isSetup = momoUserDataMsg.isSetup;
        phoneObj.isActived = momoUserDataMsg.isActived;
        phoneObj.isAgent = momoUserDataMsg.isShop;
        phoneObj.waitingReg = momoUserDataMsg.isRegWaiting;
        phoneObj.deviceInfo = momoUserDataMsg.deviceInfo;
        if (!number.equalsIgnoreCase(momoUserDataMsg.userId)) {
            return null;
        }
        return phoneObj;
    }

    public String getRedisInfo(String key, String field) {
        return getJedis().hget(key, field);
    }

    public boolean saveTransaction(String key, StandardPaymentRequest paymentRequest) {
        Map<String, String> data = DataUtil.convertTransToHashMap(new PayTrans(paymentRequest));
        getJedis().connect();
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        if(getJedis().exists(key)) {
            return false;
        } else {
            for (Map.Entry<String, String> entry : data.entrySet()){
                getJedis().hset(key, entry.getKey(),String.valueOf(entry.getValue()));
            }
        }
        getJedis().disconnect();
        return true;
    }
    public Map<String, String> getAllDataInfos(String key) {
        return getJedis().hgetAll(key);
    }
    public Map<String, String> getAllDataFromSdkDB(String key) {
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        return getJedis().hgetAll(key);
    }

    public Long delRedisInfo(String key, String field) {
        return getJedis().hdel(key, field);
    }
    public Long delRedisInfoFromSdkDB(String key, String field) {
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        return getJedis().hdel(key, field);
    }

    public Long delRedisKey(String key) {
        try {
            getJedis().connect();
            getJedis().select(MainConfig.getRedisSdkSelectDb());
            Long deleted = getJedis().del(key);
            getJedis().disconnect();
            return deleted;
        } catch (Exception ex) {
        }
        return -1l;
    }

    public boolean saveOrUpdateUser (String key, Map<String, String> data) {
        getJedis().connect();
        getJedis().select(MainConfig.getRedisBeSelectDb());
        /*if(getJedis().exists(key)) {
            return true;
        }*/
        for (Map.Entry<String, String> entry : data.entrySet()){
            getJedis().hset(key, entry.getKey(), String.valueOf(entry.getValue()));
        }
        getJedis().disconnect();
        return true;
    }

    public boolean saveDataToRedis(String key, Map<String, String> data, int timeExpired) {
        getJedis().connect();
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        for (Map.Entry<String, String> entry : data.entrySet()){
            getJedis().hset(key, entry.getKey(), String.valueOf(entry.getValue()));
        }
        // kiểm tra xem có cần expired ko
        if (timeExpired > 0)
            getJedis().expire(key, timeExpired);
        getJedis().disconnect();
        return true;
    }

    public boolean checkKeyExist(String key) {
        getJedis().connect();
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        if(getJedis().exists(key)) {
            getJedis().disconnect();
            return false; // used
        }
        getJedis().disconnect();
        return true; // not used
    }

    public boolean checkKeyDoSaving(String key) {
        getJedis().connect();
        getJedis().select(MainConfig.getRedisSdkSelectDb());
        if(getJedis().exists(key)) {
            getJedis().disconnect();
            return false; // used
        }

        getJedis().hset(key, "isExist", "1");
        getJedis().disconnect();
        return true; // not used
    }

}
