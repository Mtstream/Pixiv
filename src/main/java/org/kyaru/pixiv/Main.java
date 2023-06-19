package org.kyaru.pixiv;


import org.kyaru.pixiv.model.service.SchemeLoader;
import org.kyaru.pixiv.model.service.spidermethod.TaskItem;
import org.kyaru.pixiv.model.service.spiderscheme.SearchByArtwork;
import org.kyaru.pixiv.model.service.spiderscheme.SearchByAuthor;
import org.kyaru.pixiv.model.utils.requester.RequestClient;

import java.nio.file.Path;
import java.util.HashMap;

public class Main {
    private static String cookie = "first_visit_datetime_pc=2023-06-12+16%3A38%3A58; p_ab_id=8; p_ab_id_2=6; p_ab_d_id=1965722499; yuid_b=M5mRRic; privacy_policy_agreement=5; PHPSESSID=80214179_shCh9uU1i58sUFDb2Q6wOFiV2bHJHiJ3; device_token=d7212258d1153825e510a2b976c183de; privacy_policy_agreement=5; c_type=23; privacy_policy_notification=0; a_type=0; b_type=2; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; tag_view_ranking=0xsDLqCEW6~Yv2C9XifX_~Lt-oEicbBr~_EOd7bsGyl~b_rY80S-DW~7fCik7KLYi~4fhov5mA8J~uC2yUZfXDc~CkDjyRo6Vc~eVxus64GZU~IZ_jDBexhJ~5oPIfUbtd6~6Xu4BT4Xar~KMpT0re7Sq~9Gbahmahac~ziiAzr_h04~3Sgz5yTKJd~tgP8r-gOe_~MM6RXH_rlN~Ie2c51_4Sp~W4_X_Af3yY~KN7uxuR89w~NGpDowiVmM~SsnYN0n7AD~HLWLeyYOUF~HY55MqmzzQ~-7RnTas_L3~uusOs0ipBx~D9BseuUB5Z~6rYZ-6JKHq~bfM8xJ-4gy~hLlWCuvlzt~6vriIwKZAv~azESOjmQSV~MnGbHeuS94~j3MwF6LlsC~Ub4lsBRaC-~QOlvfk_Wxj~0seibWKRti~ZeMHmcDGS6~rjhT-2evFj~FuiUhV49xc~ujS7cIBGO-~TfrNiL_Ydg~DADQycFGB0~AWOkHyRoo0~rNkf0BT2S-~JVyhuIMU0O~Hfa80LsJiJ~cZZVw1W0gP~jyqGpomNeR~RthHN5LPvq~pMSzttXB7K~XDEWeW9f9i~lcD8ZJd9p7~5U2rd7nRim~Pt9XriSgeT~xha5FQn_XC~KexWqtgzW1~v3nOtgG77A~faHcYIP1U0~QYP1NVhSHo~_6heNC0O-R~j6sKkcbNBV~dDpJWy2NGQ~0cDh881LSw~Iv8GCd7EuQ~4i9bTBXFoE~P5-w_IbJrm~m7Ok4YJ8uN~SfOO7rOFdt~eVyGfBhp-I~JbHTIrAPZr~dujxU2nNni~yI2NoF2DS8~fbUyQrXMR3~bXMh6mBhl8~P2akwxd5Hr~bcAbumoPKA~dR6PlMr8IZ~NAVlUJuHDQ~yTfty1xk17~3cT9FM3R6t~IOEPYP3pVd~aKhT3n4RHZ~kfohPZSK7g~MJvgZqqEAe~NJN_Sd1EhI~eAg20hju2a~LtW-gO6CmS~v23eGReMdj~3W4zqr4Xlx~uW5495Nhg-~co0lRz54id~nofT_HAoUA~0Sds1vVNKR~nmhKtIFWev~YV0NSmOpUL~F8u6sord4r~q303ip6Ui5; first_visit_datetime=2023-06-20+00%3A16%3A32; webp_available=1; login_ever=yes; __cf_bm=0TTuzNsmRUBZB4K3FV6NvVjtYnobT6uyP1BsuBPFH0Q-1687187793-0-ARmCNk4eeKWK8JtR7fxojyetz7tJtl1PiRvur14Id15BG45yb45NjoyKBtB1Aa9MdHjInC1JUaSjb3bEHY8G0XpiXsLOYYGndiShkBzN2iadqTakVqUNsahLWbEL+PumRA==";
    private static Path defaultPath = Path.of("src", "IOResult");
    private static final HashMap<TaskItem.Type, Path> outputPathMap = new HashMap<>() {
        {
            put(TaskItem.Type.NOR_IMG, Path.of("NOR"));
            put(TaskItem.Type.NOR_GIF, Path.of("NOR"));
            put(TaskItem.Type.R18_IMG, Path.of("R18"));
            put(TaskItem.Type.R18_GIF, Path.of("R18"));
        }
    };
    private static int sourceLimit = 50;

    public static void main(String[] args) {
        RequestClient requestClient = new RequestClient(cookie);
        SchemeLoader sl = new SchemeLoader(new RequestClient(cookie), defaultPath, outputPathMap, sourceLimit);
        sl.getDownloader(new SearchByAuthor("89121236")).download();

    }
}