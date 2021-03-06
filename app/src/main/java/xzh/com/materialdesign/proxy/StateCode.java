package xzh.com.materialdesign.proxy;

/**
 * Created by dz on 2017/5/29.
 */

public class StateCode {
    public final static String AccountLogin="AccountLogin";
    public final static String PhoneValid="PhoneValid";
    public final static String PhoneLogin="PhoneLogin";
    public final static String GetLittleOrder="GetLittleOrder";
    public final static String OrderPublish="OrderPublish";
    public final static String GetCredit="GetCredit";
    public final static String OrderInfo="OrderInfo";
    public final static String Register="Register";

    public final static String PersonalInfo="PersonalInfo";
    public final static String ContactUs="ContactUs";
    public final static String OrderState="OrderState";
    public final static String OrderRecive="OrderRecive";
    public final static String OrderUpdate="OrderUpdate";
    public final static String OrderFinish="OrderFinish";
    public final static String OrderReview="OrderReview";
    public final static String GetReview="GetReview";
    public final static String OrderDrawback = "OrderDrawback";
    public final static String UserIdNull="错误：userId为空";

    //订单状态，0未接单，1已接单，2正在配送，3到达地点，4订单完成，5评价完成。-1订单取消。
    public final static int Order_Waiting=0;
    public final static int Order_Receive=1;
    public final static int Order_Sending=2;
    public final static int Order_Arrived=3;
    public final static int Order_Complete=4;
    public final static int Order_Evaluated=5;
    public final static int Order_Cancel=-1;

    //订单类型,积分1，现金2
    public final static int OrderType_Score=1;
    public final static int OrderType_Money=2;

    public final static int ListMax=15;

    //广播参数
    //public final static String BROAD="发起广播";
    public final static String BROAD_NICKNAME="nickName";
    public final static String BROAD_EMAIL="email";
    public final static String BROAD_NAME="name";
    public final static String BROAD_PHONE="phoneNum";
    public final static String BROAD_PWD="password";
    public final static String BROAD_SCHOOL="school";
    public final static String BROAD_SEX="sex";
    public final static String BROAD_CHANGENAME="changeName";
    public final static String BROAD_CHANGEPHONE="changePhone";
}
