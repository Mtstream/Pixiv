package org.kyaru.pixiv;
import org.kyaru.pixiv.service.ServiceInterface;


public class Main {
    private static final String COOKIE = "first_visit_datetime_pc=2023-06-27+18%3A35%3A46; p_ab_id=0; p_ab_id_2=5; p_ab_d_id=564720459; yuid_b=VVdgEg; c_type=23; privacy_policy_notification=0; a_type=0; b_type=2; privacy_policy_agreement=5; PHPSESSID=80214179_YE50EoT0X1mi1SwKWvF3SZYs3l8cuRsS; device_token=da071fc30c5cfa1d18a1d6875c2b7fe3; privacy_policy_agreement=6; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; cf_clearance=gTcGHtcR13GoLOMttjJIIskcegPKqSreIINlAcgA3n8-1691309112-0-1-21e36fd7.a1b6652c.76e23037-0.2.1691309112; __cf_bm=JR2pk_yDLkJeNpLPo7lbIhWy2hp04XCr4pA5wy_eetg-1691326279-0-AROWsFw7mXLwzBKiKhW8HpvGqOBe9PzefJ3ORLDjQ8B4CXoYUtnhFnvCtrtOrJiKELPSV5vQY+3Lq+k5UMbmA+WsXhsHS4lOknZD9coeRyCc";
    public static void main(String[] args) {
        ServiceInterface.config().setter()
                .setDefault(COOKIE)
                .setProxy("127.0.0.1", 7890);
        ServiceInterface.download().byAuthor("2253649");
    }
}