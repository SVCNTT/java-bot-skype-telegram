package com.snow.wolf.bot.database.oracle;

import com.mservice.common.util.Utils;
import com.mservice.sdk.model.model.*;
import com.mservice.sdk.model.util.DataUtil;
import com.mservice.sdk.model.util.StringKeyNames;
import com.snow.wolf.bot.config.MainConfig;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;

/**
 * Created by toanmai on 10/05/2017.
 */
public class OracleDbManager {

    public static Balance getAgentBalanceExtend(String phone, Logger logger) {
        Balance balance = new Balance(phone);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            StringBuilder query =
                    new StringBuilder()
                            .append("select ac.AVAIL_BALANCE, ac.TYPE_ ")
                            .append(" from  umarketadm.agent_ref ar ")
                            .append(" left join umarketadm.account ac on ac.agent = ar.bodyid ")
                            .append(" where ar.deleted = 0 and ac.TYPE_ in (1,4,3) ")
                            .append(" and ar.reference = ? ");
            conn = HakariConnectionFactory.getConnection();
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, phone);
            //1 momo; 2 mload,3 point,4 voucher
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                int balanceType = result.getInt("TYPE_");
                switch (balanceType) {
                    case 1 :
                        balance.momoBalance = result.getLong("AVAIL_BALANCE");
                        break;
                    case 3 :
                        balance.pointBalance = result.getLong("AVAIL_BALANCE");
                        break;
                    case 4 :
                        balance.voucherBalance = result.getLong("AVAIL_BALANCE");
                        break;
                }
            }
        } catch (Exception e) {
            logger.info(new StringBuilder(phone).append("| Get agent balance catch exception : ").append(e.getMessage()));
            balance.momoBalance = 0;
        } finally {
            Utils.close(conn);
            Utils.close(statement);
        }
        return balance;
    }

    public static boolean checkUserExits(String phoneNumber) {
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder query =
                    new StringBuilder()
                            .append("SELECT count(1) AS is_exist ")
                            .append("FROM ")
                            .append("umarketadm.agent_ref ar ")
                            .append("INNER JOIN umarketadm.agent_group_map agm ON ar.bodyid = agm.agentid ")
                            .append("LEFT JOIN umarketadm.agent_group ag ON ag.id = agm.agid ")
                            .append("WHERE ")
                            .append("ar.reference = ? and agm.agid = 19112 and ar.deleted = 0");
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, phoneNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                return result.getInt("is_exist") == 1;
            }
            return false;
        } catch (Exception ex) {
            System.out.println("dm lỗi rồi");
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return false;
    }

    public static HashMap<String, String> getAllStores() {
        HashMap<String, String> response = new HashMap<>();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder query =
                    new StringBuilder()
                            .append("SELECT * ")
                            .append("FROM MOMO_BUSSINESS_MERCHANT_INFO ")
                            .append("WHERE ")
                            .append("IS_ACTIVE = 1 ")
                            .append("AND STORE_SLUG is not null");
            statement = conn.prepareStatement(query.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                JsonObject item = new JsonObject();
                item.put(StringKeyNames.KEY_PARTNER_CODE, result.getString("PARTNER_CODE"));
                item.put(StringKeyNames.KEY_STORE_ID, result.getString("STORE_ID"));
                item.put(StringKeyNames.KEY_STORE_NAME, result.getString("COMPANY_NAME"));
                item.put(StringKeyNames.KEY_STORE_ADDRESS, result.getString("COMPANY_ADDRESS"));
                item.put(StringKeyNames.KEY_STORE_IMAGE_URL, MainConfig.getStoreAvatarDomain() + result.getString("AVATAR_SIGNATURE"));
                item.put(StringKeyNames.KEY_STORE_NOTIFY_PHONES, result.getString("STORE_NOTIFY_PHONES"));
                response.put(result.getString("STORE_SLUG"), item.toString());
            }
        }  catch (Exception ex) {
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return response;
    }

    public static JsonObject getAllPartners() {
        JsonObject response = new JsonObject();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder query =
                    new StringBuilder()
                            .append("SELECT * ")
                            .append("FROM SDK_PAYMENT_PARTNER ")
                            .append("WHERE ")
                            .append("visible = 1");
            statement = conn.prepareStatement(query.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                JsonObject resultData = new JsonObject();
                resultData.put("PARTNER_CODE",          result.getString("PARTNER_CODE"));
                resultData.put("PARTNER_NAME",          result.getString("PARTNER_NAME"));
                resultData.put("PARTNER_SERVICE_ID",    result.getString("PARTNER_SERVICE_ID"));
                resultData.put("PARTNER_AGENT",         result.getString("PARTNER_AGENT"));
                resultData.put("PARTNER_PHONENUMBER",   result.getString("PARTNER_PHONENUMBER"));
                resultData.put("PARTNER_QUERY_URL",     result.getString("PARTNER_QUERY_URL"));
                resultData.put("PARTNER_PUBLIC_KEY",    result.getString("PARTNER_PUBLIC_KEY"));
                resultData.put("PARTNER_PRIVATE_KEY",   result.getString("PARTNER_PRIVATE_KEY"));
                resultData.put("PARTNER_FEE",           result.getInt("PARTNER_FEE"));
                resultData.put("PARTNER_SECRET_KEY",    result.getString("PARTNER_SECRET_KEY"));
                resultData.put("IS_CONNECTOR_PAY",      result.getInt("IS_CONNECTOR_PAY"));
                resultData.put("PARTNER_QUEUE_NAME",    result.getString("CONNECTOR_QUEUE_NAME"));
                resultData.put("STATUS",                result.getInt("STATUS"));
                resultData.put("PAY_2_STEP",            result.getInt("PAY_2_STEP"));
                resultData.put("PAY_WITH_TOKEN",        result.getInt("PAY_WITH_TOKEN"));
                resultData.put("NOTIFY_TYPE",           result.getInt("NOTIFY_TYPE"));
                resultData.put("PARTNER_NOTIFY_URL",    result.getString("PARTNER_NOTIFY_URL"));
                resultData.put("TRAN_TIMEOUT",          result.getLong("TRAN_TIMEOUT"));
                resultData.put("BILL_EXPIRE_TIME",      result.getLong("BILL_EXPIRE_TIME"));
                response.put(result.getString("PARTNER_CODE"), resultData);
            }
        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return response;
    }

    public static JsonObject getSDKTrans (
            Logger logger,
            boolean getByBillId,
            String requestId,
            String queryData,
            String partnerCode,
            String payType) {
        JsonObject rowData = new JsonObject();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
//            "SELECT * FROM CONNECTOR.PROXY_TRANS WHERE REFERENCE_1 = ? AND SERVICE_CODE = ? AND REQUEST_TYPE = 'payBill'
            conn = HakariConnectionFactory.getConnection();
            StringBuilder queryString =
                    new StringBuilder("SELECT * ")
                            .append(" FROM SDK_PAYMENT_TRANSACTION ")
                            .append(" WHERE ")
                            .append(getByBillId ? " BILL_ID = ? " : " CORE_TRAN_ID = ? ")
                            .append(" AND PARTNER_CODE = ? ")
                            .append(" ORDER BY CREATE_DATE ASC");
            statement = conn.prepareStatement(queryString.toString());
            statement.setString(1, queryData);
            statement.setString(2, partnerCode);
            statement.setString(3, payType);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rowData.put(StringKeyNames.STATUS , resultSet.getLong("RESULT_CODE"));
                rowData.put(StringKeyNames.MESSAGE , resultSet.getString("RESULT_DESCRIPTION"));
                rowData.put(StringKeyNames.KEY_PARTNER_CODE , partnerCode);
                rowData.put(StringKeyNames.KEY_PARTNER_BILLID , resultSet.getString("BILL_ID"));
                rowData.put(StringKeyNames.TRANS_ID , resultSet.getLong("CORE_TRAN_ID"));
                rowData.put(StringKeyNames.AMOUNT , resultSet.getLong("TOTAL_AMOUNT"));
                rowData.put(StringKeyNames.DISCOUNT_AMOUNT , resultSet.getLong("DISCOUNT_AMOUNT"));
                rowData.put(StringKeyNames.FEE , resultSet.getLong("FEE"));
                rowData.put(StringKeyNames.KEY_PHONE_NUMBER , resultSet.getString("CUSTOMER_PHONE_NUMBER"));
                rowData.put(StringKeyNames.KEY_STORE_ID , resultSet.getString("STORE_ID"));
                rowData.put(StringKeyNames.KEY_REQUEST_DATE , String.valueOf(resultSet.getTimestamp("CREATE_DATE")));
                rowData.put(StringKeyNames.KEY_RESPONSE_DATE , String.valueOf(resultSet.getTimestamp("RESPONSE_DATE")));
                rowData.put(StringKeyNames.CUSTOMER_NAME , resultSet.getString("CUSTOMER_NAME"));
            }
        } catch (Exception ex) {
            logger.info(new StringBuilder(requestId).append("| Get transaction has error : ").append(ex.getMessage()));
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return rowData;
    }


    public static JsonArray getSDKListTrans (
            Logger logger,
            String requestId,
            String partnerCode,
            String fromDate,
            String toDate) {
        JsonArray listData = new JsonArray();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder queryString =
                    new StringBuilder("SELECT * ")
                            .append(" FROM SDK_PAYMENT_TRANSACTION ")
                            .append(" WHERE ")
                            .append(" PARTNER_CODE = ? ")
                            .append("and CREATE_DATE BETWEEN TO_DATE('")
                            .append(fromDate)
                            .append("' ,'dd-mm-yyyy hh24:mi:ss') and TO_DATE( '")
                            .append(toDate)
                            .append("','dd-mm-yyyy hh24:mi:ss')")
                            .append(" ORDER BY CREATE_DATE ASC");
            statement = conn.prepareStatement(queryString.toString());
            statement.setString(1, partnerCode);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject rowData = new JsonObject();
                rowData.put(StringKeyNames.STATUS , resultSet.getLong("RESULT_CODE"));
                rowData.put(StringKeyNames.MESSAGE , resultSet.getString("RESULT_DESCRIPTION"));
                rowData.put(StringKeyNames.KEY_PARTNER_CODE , partnerCode);
                rowData.put(StringKeyNames.KEY_PARTNER_BILLID , resultSet.getString("BILL_ID"));
                rowData.put(StringKeyNames.TRANS_ID , resultSet.getLong("CORE_TRAN_ID"));
                rowData.put(StringKeyNames.AMOUNT , resultSet.getLong("TOTAL_AMOUNT"));
                rowData.put(StringKeyNames.DISCOUNT_AMOUNT , resultSet.getLong("DISCOUNT_AMOUNT"));
                rowData.put(StringKeyNames.FEE , resultSet.getLong("FEE"));
                rowData.put(StringKeyNames.KEY_PHONE_NUMBER , DataUtil.hashPhone(resultSet.getString("CUSTOMER_PHONE_NUMBER")));
                rowData.put(StringKeyNames.KEY_STORE_ID , resultSet.getString("STORE_ID"));
                rowData.put(StringKeyNames.KEY_REQUEST_DATE , String.valueOf(resultSet.getTimestamp("CREATE_DATE")));
                rowData.put(StringKeyNames.KEY_RESPONSE_DATE , String.valueOf(resultSet.getTimestamp("RESPONSE_DATE")));
                rowData.put(StringKeyNames.CUSTOMER_NAME , resultSet.getString("CUSTOMER_NAME"));
                listData.add(rowData);
            }
        } catch (Exception ex) {
            logger.info(new StringBuilder(requestId).append("| Get transaction has error : ").append(ex.getMessage()));
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return listData;
    }

    public static JsonObject getCoreTranData(Logger logger, String requestId, String billId, String agent, long requestTime) {
        JsonObject rowData = new JsonObject();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder queryString =
                    new StringBuilder("SELECT * FROM MIS_ADMIN.ms_all_trans_paid ")
                            .append("WHERE created >= ? ")
                            .append("and account = ? ")
                            .append("and target  = ? ");
            statement = conn.prepareStatement(queryString.toString());
            statement.setTimestamp(1, new Timestamp(requestTime));
            statement.setString(2, billId);
            statement.setString(3, agent);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rowData.put(StringKeyNames.STATUS , resultSet.getInt("RESULT"));
                rowData.put(StringKeyNames.KEY_AGENT , agent);
                rowData.put(StringKeyNames.KEY_PARTNER_BILLID , resultSet.getString("ACCOUNT"));
                rowData.put(StringKeyNames.TRANS_ID , String.valueOf(resultSet.getLong("ID")));
                rowData.put(StringKeyNames.AMOUNT , resultSet.getLong("TOTAL_AMOUNT"));
                rowData.put(StringKeyNames.KEY_PHONE_NUMBER , resultSet.getString("INITIATOR"));
                rowData.put(StringKeyNames.KEY_EVCAMOUNT , resultSet.getLong("EVC_AMOUNT"));
                rowData.put(StringKeyNames.VC_NUMBER , resultSet.getLong("GIFT_IDS_COUNT"));
                rowData.put(StringKeyNames.DISCOUNT_AMOUNT , resultSet.getLong("TOTAL_AMOUNT") - resultSet.getLong("MM_AMOUNT"));
                rowData.put(StringKeyNames.KEY_REQUEST_DATE , String.valueOf(resultSet.getTimestamp("CREATED")));
            }
        } catch (Exception ex) {
            logger.info(new StringBuilder(requestId).append("| Get ms_all_trans_paid has error : ").append(ex.getMessage()));
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return rowData;
    }

    public static String nullToDefault(String val, String def) {
        return (val != null ? val : def);
    }

    public static JsonObject getAllPartnersFee() {
        JsonObject partnerFee = new JsonObject();
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder query =
                    new StringBuilder()
                            .append("SELECT * ")
                            .append("FROM SDK_PARTNER_FEE ")
                            .append("WHERE ")
                            .append("visible = 1");
            statement = conn.prepareStatement(query.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                JsonObject resultData = new JsonObject();
                resultData.put("SERVICE_ID", result.getString("SERVICE_ID"));
                resultData.put("PARTNER_CODE", result.getString("PARTNER_CODE"));
                resultData.put("AGENT_FEE", result.getString("AGENT_FEE"));
                resultData.put("STATIC_FEE", result.getLong("STATIC_FEE"));
                resultData.put("DYNAMIC_FEE", result.getString("DYNAMIC_FEE"));
                resultData.put("TYPE", result.getString("TYPE"));
                partnerFee.put(result.getString("PARTNER_CODE"), resultData);
            }
        } catch (Exception ex) {
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return partnerFee;
    }

    public static JsonObject getUserInfo(Logger logger, String customerNumber) {
        PreparedStatement statement = null;
        Connection conn = null;
        JsonObject userData = new JsonObject();
        try {
            conn = HakariConnectionFactory.getConnection();
            StringBuilder queryString =
                    new StringBuilder("SELECT * ")
                            .append(" FROM SOAP_ADMIN.USER_DATA ")
                            .append(" WHERE ")
                            .append(" USER_ID = ? ");
            statement = conn.prepareStatement(queryString.toString());
            statement.setString(1, customerNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String key = resultSet.getString("ATT_KEY");
                switch (key) {
                    case "userId" :
                        userData.put("number", resultSet.getString("ATT_VALUE"));
                        break;
                    case "phoneOs" :
                        userData.put("os", resultSet.getString("ATT_VALUE"));
                        break;
                    case "extra" :
                        userData.put("extra", resultSet.getString("ATT_VALUE"));
                        break;
                    case "lastSessionKey" :
                        userData.put("session_key", resultSet.getString("ATT_VALUE"));
                        break;
                    case "name" :
                        userData.put("name", resultSet.getString("ATT_VALUE"));
                        break;
                    case "bankName" :
                        userData.put("bnk_name", resultSet.getString("ATT_VALUE"));
                        break;
                    case "bankCode" :
                        userData.put("bnk_code", resultSet.getString("ATT_VALUE"));
                        break;
                    case "email" :
                        userData.put("email", resultSet.getString("ATT_VALUE"));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            logger.info(new StringBuilder(customerNumber).append("| Get user info has error : ").append(ex.getMessage()));
        } finally {
            Utils.close(statement);
            Utils.close(conn);
        }
        return userData;
    }

    public static JsonArray getListTransactionPending(String partnerCode) {
        PreparedStatement statement = null;
        Connection conn = null;
        JsonArray list = new JsonArray();
        try {
            conn = HakariConnectionFactory.getConnection();
            statement = conn.prepareStatement(
                            "SELECT * " +
                            "FROM SDK_ADMIN.SDK_PAYMENT_TRANSACTION " +
                            "WHERE RESULT_CODE = 9000 " +
                                    "AND PARTNER_CODE IN ? AND CREATE_DATE BETWEEN (sysdate - ?/1440) AND (sysdate - ?/1440)"
            );
            statement.setString(1, partnerCode);
            statement.setInt(2, MainConfig.getRangeTimeQueryPending().getInteger("max", 1440));
            statement.setInt(3, MainConfig.getRangeTimeQueryPending().getInteger("min", 10));

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                JsonObject element = new JsonObject();
                element.put(StringKeyNames.KEY_PARTNER_BILLID, result.getString("BILL_ID"));
                element.put(StringKeyNames.STATUS, ServiceErrors.MOMO_PENDING_PAYMENT);
                element.put(StringKeyNames.AMOUNT, result.getLong("TOTAL_AMOUNT"));
                element.put(StringKeyNames.KEY_CORE_TRANSID, result.getString("CORE_TRAN_ID"));
                element.put(StringKeyNames.KEY_PARTNER_CODE, result.getString("PARTNER_CODE"));
                element.put(StringKeyNames.PARTNER_TRANS_ID, result.getString("QRCODE"));
                element.put(StringKeyNames.CUSTOMER_NUMBER, result.getString("CUSTOMER_PHONE_NUMBER"));
                element.put(StringKeyNames.EXTRA_DATA, result.getString("EXTRA_DATA"));
                list.add(element);
            }
        } catch (Exception ex) {
        } finally {
            Utils.close(conn);
            Utils.close(statement);
        }
        return list;
    }
}
