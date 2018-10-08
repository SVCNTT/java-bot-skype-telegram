package com.snow.wolf.bot.config;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

/**
 * Created by toanmai on 09/09/2017.
 */
public class MainConfig {

    public static JsonObject glbConfig;

    public static JsonObject partnersConfig;

    public static JsonObject partnersFee;

    public static HashMap<String, String> storesInfo;

    public static String configPath;

    public static String pageShowQr;

    public static String pageShow404;

    public static String pageShow503;

    private static boolean allServiceStopOrStart;

    private static JsonObject configHeadOfNumber;

    private static int maxRandomTime = 500;

    public MainConfig() {
    }


    public MainConfig(String configPath) {
        if (glbConfig == null) {
            this.configPath = configPath;
            try {
                String json = FileUtils.readFileToString(new File(configPath));
                allServiceStopOrStart = true;
                glbConfig   = new JsonObject(json);
            } catch (Exception ex) {
            }
        }
    }


    public static void reload() throws IOException {
        String json     = FileUtils.readFileToString(new File(configPath));
        glbConfig       = new JsonObject(json);
        maxRandomTime   = getMaxRandomTime();
    }

    public static String getUploadFolder() {
        return glbConfig.getJsonObject("help_partner_pay", new JsonObject()).getString("upload_folder", "");
    }

    public JsonObject getConfig() {
        return glbConfig;
    }

    public static void setServiceStopOrStart(boolean serviceStopOrStart) {
        allServiceStopOrStart = serviceStopOrStart;
    }

    public static boolean getAllServiceStatus() {
        return allServiceStopOrStart;
    }

    private static JsonObject getNapasConfig() {
        return glbConfig.getJsonObject("napas_config", new JsonObject());
    }

    private static JsonObject getNapasConfAutoQuery() {
        return getNapasConfig().getJsonObject("auto_query" , new JsonObject());
    }

    public static JsonObject getRangeTimeQueryPending () {
        return getNapasConfAutoQuery().getJsonObject("time_range", new JsonObject());
    }

    private static String getNapasPubKeyPath () {
        return getNapasConfig().getString("pubKey", "/app/mservice/sdk/backend/conf/napasPubKey.cer");
    }

    private static String getNapasPriKeyPath () {
        return getNapasConfig().getString("priKey", "/app/mservice/sdk/backend/conf/napasPriKey.cer");
    }

    public static JsonObject getNapasIssuer() {
        return getNapasConfig().getJsonObject("issuer", new JsonObject());
    }

    public static String getNapasMerchantCode() {
        return getNapasConfig().getString("merchant_code", "NAPASTEST");
    }

    public static String  getNapasPaymentHost() {
        return getNapasConfig().getString("host", "");
    }

    public static String  getNapasPath(String type) {
        return getNapasConfig().getJsonObject("path", new JsonObject()).getString(type);
    }

    public static long getNapasRetryQueryTime() {
        return getNapasConfAutoQuery().getLong("time_retry", 600000L);
    }


    public static int getNapasRequestTimeout() {
        return getNapasConfig().getInteger("timeout", 60000);
    }

    private static JsonObject getMailAlertConfig() {
        return glbConfig.getJsonObject("mail_alert", new JsonObject());
    }

    public static String getMailAlertDomain() {
        return getMailAlertConfig().getString("domain", "");
    }

    public static String getMailAlertSignature() {
        return getMailAlertConfig().getString("signature", "");
    }
    
    public static String getMailAlertToUsers() {
        return getMailAlertConfig().getString("toUsers", "");
    }

    /* ------------- config service key ----------- */
    public static JsonObject getServiceKey() {
        return glbConfig.getJsonObject("service_key");
    }

    public static String getServiceAccessKey() {
        return getServiceKey().getString("access_key", "");
    }

    public static String getServiceSecretKey() {
        return getServiceKey().getString("secret_key", "");
    }

    /* -------------  service key ----------- */
    public static boolean getEnvironment() {
        return glbConfig.getBoolean("env_pro", false);
    }

    /* -------------  regex ----------- */
    public static JsonObject getRegex() {
        return glbConfig.getJsonObject("base_regex");
    }

    public static String getPhoneRegex() {
        return getRegex().getString("phone_regex", "^((\\+84)|0)\\d{9,10}$");
    }

    public static String getPinRegex() {
        return getRegex().getString("pin_regex", "^[0-9]{6}$");
    }

    public static String getRequestIdRegex() {
        return getRegex().getString("request_id_regex", "^[0-9a-zA-Z]([-_.]*[0-9a-zA-Z]+)*$");
    }

    public static String getAccountRegex() {
        return getRegex().getString("account_regex", "^[0-9a-zA-Z]([-_.@]*[0-9a-zA-Z]+)*$");
    }

    public static String getEmvcoRegex() {
        return getRegex().getString("emvco_regex", "^[0-9a-zA-Z]([-_. ]*[0-9a-zA-Z]+)*$");
    }

    public static String getPaymentCodeRegex() {
        return getRegex().getString("payment_code_regex", "^[0-9]{18}$");
    }

    public static String getEmailRegex() {
        return getRegex().getString("email_regex", "^((\"[\\w-\\s]+\")|([\\w-]+(?:\\.[\\w-]+)*)|(\"[\\w-\\s]+\")([\\w-]+(?:\\.[\\w-]+)*))(@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$)|(@\\[?((25[0-5]\\.|2[0-4][0-9]\\.|1[0-9]{2}\\.|[0-9]{1,2}\\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\]?$)");
    }

    public static String getBasicRegex() {
        return getRegex().getString("basic_regex", "^[a-zA-Z0-9]*$");
    }

    public static String getPartnerCodeRegex() {
        return getRegex().getString("partner_code_regex", "^[a-zA-Z0-9_]*$");
    }

    public static String getSlugRegex() {
        return getRegex().getString("slug_regex", "^[a-zA-Z0-9\\-]*$");
    }

    public static String getCustomerNameRegex() {
        return getRegex().getString("name_regex", "^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]*$");
    }

    public static String getDescriptionRegex() {
        return getRegex().getString("description_regex", "^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ#-._]*$");
    }

    public static String getUrlRegex() {
        return getRegex().getString("url_regex", "^(http|https)://.*$");
    }

    /* -------------  rabbit config ----------- */
    public static JsonObject getRabbitConfig() {
        return glbConfig.getJsonObject("rabbitQueue", new JsonObject()).getJsonObject("basic", new JsonObject());
    }

    public static JsonObject getRabbitVisaConfig() {
        return glbConfig.getJsonObject("rabbitQueue", new JsonObject()).getJsonObject("visa", new JsonObject());
    }

    public static JsonObject getRabbitCoreConfig() {
        return getRabbitConfig().getJsonObject("coreConfig", new JsonObject());
    }

    public static String getCoreGoldenGateQueue() {
        return getRabbitCoreConfig().getString("rabbitQueueName", "");
    }

    public static String getRabbitCoreConfigUser() {
        return getRabbitCoreConfig().getString("agent", "");
    }

    public static String getRabbitCoreConfigPin() {
        return getRabbitCoreConfig().getString("pin", "");
    }

    public static String getRabbitConfigUser() {
        return getRabbitConfig().getString("user", "");
    }

    public static String getRabbitConfigPassword() {
        return getRabbitConfig().getString("pass", "");
    }

    public static JsonArray getRabbitConfigAddesses() {
        return getRabbitConfig().getJsonArray("addresses");
    }

    public static JsonObject getServerQueues() {
        return getRabbitConfig().getJsonObject("server_queue");
    }

    public static JsonObject getClientQueues() {
        return getRabbitConfig().getJsonObject("client_queue");
    }

    public static String getPromotionQueue() {
        return getServerQueues().getString("get_discount", "ha_promotion_discount");
    }

    public static String getConnectorTokenQueue() {
        return getServerQueues().getString("connector_update_token", "ha_qu_core_test_new_req");
    }

    public static String getCreateCodeQueue() {
        return getServerQueues().getString("create_code", "ha_qu_sdk_createcode_dev");
    }

    public static String getAppChangeTokenQueue() {
        return getServerQueues().getString("sdk_token", "ha_qu_sdk_app_token_dev");
    }

    public static String getVisaQueue() {
        return getServerQueues().getString("visa", "ha_connector_creditcard_req");
    }

    public static String getNotiTransQueue() {
        return getServerQueues().getString("noti_tranhis", "ha_qu_be_clientdispatcher");
    }

    public static String getRefundQueue() {
        return getServerQueues().getString("refund", "ha_qu_gw_refund_request_2");
    }

    /* ------------- get db config ------------ */
    public static JsonObject getDbConfig() {
        return glbConfig.getJsonObject("database", new JsonObject());
    }

    public static JsonObject getOracleDbConfig() {
        return getDbConfig().getJsonObject("oracle", new JsonObject());
    }

    public static JsonObject getRedisConfig() {
        return getDbConfig().getJsonObject("redis_v2", new JsonObject());
    }

    public static String getRedisHost() {
        return getRedisConfig().getString("redis_promotion_host", "172.16.13.22");
    }

    public static Integer getRedisPort() {
        return getRedisConfig().getInteger("redis_promotion_port", 6379);
    }

    public static Integer getRedisTimeout() {
        return getRedisConfig().getInteger("redis_timeout", 10000);
    }

    public static String getRedisPassword() {
        return getRedisConfig().getString("redis_auth", "mservice");
    }

    public static Integer getRedisBeSelectDb() {
        return getRedisConfig().getInteger("be_db_select" , 0);
    }

    public static Integer getRedisSdkSelectDb() {
        return getRedisConfig().getInteger("sdk_db_select" , 7);
    }

    /* ----------------------- Cassandra Database ---------------------------- */
    public static JsonObject getCassandraConfig() {
        return glbConfig.getJsonObject("database").getJsonObject("cassandra" , new JsonObject());
    }

    public static String[] getCassandraHosts () {
        return getCassandraConfig().getString("hosts").split(",");
    }

    public static int getCassandraPort() {
        return getCassandraConfig().getInteger("port", 9042);
    }

    public static String getCassandraUser() {
        return getCassandraConfig().getString("username", "cassandra");
    }

    public static String getCassandraPass() {
        return getCassandraConfig().getString("password", "{{{VFW0O8W09LmmlHhtyatYDFHhg==!^*#$RIG");
    }

    /* ------------- get service active discount ------------ */
    public static boolean getServiceActiveDiscount(String serviceId) {
        return glbConfig.getJsonObject("discount_payment", new JsonObject()).getJsonObject(serviceId, new JsonObject()).getBoolean("visible", false);
    }

    /*------------------- get kafka config ------------------------*/
    public static JsonObject getKafkaConfig() {
        return glbConfig.getJsonObject("kafka_producer", new JsonObject());
    }

    public static JsonObject getKafkaTopicProducer() {
        return getKafkaConfig().getJsonObject("events.topic", new JsonObject());
    }

    public static String getKafkaPaymentTopic() {
        return getKafkaTopicProducer().getString("payment", "test_events");
    }

    public static String getKafkaCreatePayCodeTopic() {
        return getKafkaTopicProducer().getString("create_code", "sdk_create_paycode");
    }

    /*------------------- get host port service info ---------------------*/
    public static JsonObject getListServiceInfo() {
        return glbConfig.getJsonObject("service_host_port", new JsonObject());
    }

    public static String getDefaultHostIp() {
        return getListServiceInfo().getString("host_ip", "10.10.10.171");
    }

    public static Integer getBasicPort() {
        return getListServiceInfo().getJsonObject("port").getInteger("base", 8090);
    }

    /*--------------------- get connector bank service ---------------------------*/
    public static JsonObject getConnectorBankService() {
        return glbConfig.getJsonObject("bank_service", new JsonObject());
    }

    private static JsonObject getBankConfig(String bankCode) {
        return getConnectorBankService()
                .getJsonObject(bankCode, new JsonObject());
    }

    public static JsonObject getBankDataConfig(String bankCode) {
        return getBankConfig(bankCode).getJsonObject("data", new JsonObject());
    }

    public static boolean checkCashinQueueFlow(String bankCode) {
        return getBankConfig(bankCode)
                .getBoolean("useQueue", false);
    }

    /*---------------------------- get Visa Fee ----------------------------------*/
    public static JsonObject getVisaFee() {
        return glbConfig.getJsonObject("visa_fee", new JsonObject());
    }

    public static long getVisaStaticFee() {
        return getVisaFee().getLong("static_fee", 2000l);
    }

    public static Double getVisaDynamicFee() {
        return Double.valueOf(getVisaFee().getString("dynamic_fee", "1.6"))/100;
    }
    /*--------------------------- get agent pin -------------------------*/
    public static String getAgentPin(String agent) {
        return glbConfig.getJsonObject("agent_sercutity" , new JsonObject()).getString(agent, "");
    }

    /*--------------------------- get html page  ----------------------------*/
    public static String getJumPageHtml() {
        return pageShowQr;
    }

    public static String getJumPageHtml(String deepLink, String qrData, String serviceName) {
        return String.format(pageShowQr, qrData, deepLink, serviceName);
    }

    public static String get404PageHtml() {
        return pageShow404;
    }

    public static String get503PageHtml() {
        return pageShow503;
    }

    public static String getStoreAvatarDomain() {
        return glbConfig.getString("business_store_domain","");
    }

    private static JsonObject getRange() {
        return glbConfig.getJsonObject("range", new JsonObject());
    }

    private static JsonObject getRangeAmount() {
        return getRange().getJsonObject("amount", new JsonObject());
    }

    public static int getMinRandomTime() {
        return getRange().getJsonObject("random_time", new JsonObject()).getInteger("min", 10);
    }

    private static int getMaxRandomTime() {
        return getRange().getJsonObject("random_time", new JsonObject()).getInteger("max", 1000);
    }
    public static long getMinPaymentAmount() {
        return getRangeAmount().getLong("min", 1000L);
    }

    public static long getMaxPaymentAmount() {
        return getRangeAmount().getLong("max", 20000000L);
    }

    public static double getMinPercentUseVc() {
        return getRange().getDouble("min_percent_usevc", 0.5);
    }

    public static long getTokenExpireTime(String partnerCode) {
        return glbConfig.getJsonObject("expire_token", new JsonObject()).getLong(partnerCode, 0L);
    }

    /*--------------------------- List partner enable valid signature -----------------------------*/
    public static boolean isEnableValidSignature(String partnerCode) {
        return glbConfig.getJsonObject("partner_valid_signature", new JsonObject())
                .getBoolean(partnerCode, false);
    }

    public static boolean isCheckM4BSignature(String partnerCode) {
        return glbConfig.getJsonObject("m4b_check_signature", new JsonObject()).getBoolean(partnerCode, false);
    }

    private static JsonObject getCgvCouponConfig() {
        return glbConfig.getJsonObject("cgv_coupon_amount", new JsonObject());
    }

    public static long getCGVTicketCouponAmount() {
        return getCgvCouponConfig().getLong("ticket", 80000L);
    }

    public static long getCGVConcessionCouponAmount() {
        return getCgvCouponConfig().getLong("concession", 55000L);
    }

    private static JsonObject getCgvPromotionId() {
        return glbConfig.getJsonObject("cgv_coupon_amount", new JsonObject());
    }

    /*------------------------------ REGISTER AGENT CONFIG -----------------------------------*/
    private static JsonObject getAgentRegisterConfig() {
        return glbConfig.getJsonObject("agent_register", new JsonObject());
    }

    private static JsonObject getAgentRegisterGroupConfig() {
        return getAgentRegisterConfig().getJsonObject("group", new JsonObject());
    }
}
