package com.enecuum.lib;

import android.util.Log;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

public class LibCryptoMobile {

    // All hex test values for test:
    private final String p = "80000000000000000000000000000000000200014000000000000000000000000000000000010000800000020000000000000000000000000000000000080003"; // is prime!
    private final String a = "0 1";
    private final String b = "0 0"; // y^2 = x^3 + [1,0]*x + [0,0]
    private final String order = "80000000000000000000000000000000000200014000000000000000000000000000000000010000800000020000000000000000000000000000000000080004"; // already hex, p+1; not used
    private final String irred = "2 1 1 80000000000000000000000000000000000200014000000000000000000000000000000000010000800000020000000000000000000000000000000000080002"; // x^2 + x + (p-1)
    private final String gx = "25a41f7b42f7c1775b983cb548e48cc646b0fdfa375d95127ce79f0bd42f43de76957c705b7f6440a23a79793d447e5fac6f5b46ad4e76e16ee670858bbd6763 520acc54c0e383d454c21aae0a4f3f7c333efff7e043378fb197d024eccf73c138139e0b6e320254840bc823d28e7323cc02d4e9c61751e366ebcd5def3324f1";
    private final String gy = "67de09049fab6cb27343f0f6ba348cc212bc6f892700d17bced2ba3856bcc7b77162431d6bea89d22adcb8bd53ada074ee18fa1c54cc6cbd45e06136c84f05a4 3e239fb4f7e10e12f9a9450adad92807154b0f327407c16d051e7542532da03cd92bd64a430d79b44a1bbc30670fc61ba21e5511e6e43d1b6707d8a727c046aa";
    private final int k = 2; // not used
    private final String s1x = "7b6955fdb8aeefc1dd6c96fffe9506ac85d1ab5cf317d04a581e08677ec6952ef8737fae365845d5eac55e26250518d4a079adbaff8184b0f1903c71c5e4a358 1b0b0c09f9384ae54ff51c33a03765388ac1ba940c04ba62317fa19c7b66d35bac4168648d12c1859d8c65fb926d6fe2e3911419bff5acf414fa3b85a4f89724";
    private final String s1y = "740e974e6515ffbac90510c3111337b97adc0dc5e5ab9b0a35e25c55d531b6c890d3b675e17f64c376e33bfdde9936f9e7cc56eb384da9dcd0a2e5af9423b277 d1252740dbc31524b4fc44990690ac8423c2d47005bc807a574714fb3cb6ca77b3c21f2d5205b37c2258c22a7a7be853e5c29aff3668467fddf334ba2b25333";
    private final String s2x = "0 4245123bac69a6c89693f07e321dc7b72d18d9e78e95321bd546ecae2ff1bc35d70253393a0b942f34b8669375942924728ad44c98e950c229e43b68fd81e739";
    private final String s2y = "0 211989be20581151af1f4da67b63288725a361d051957615f3eef74e6eda2a487439ff071ed2628b3211fbe627aa8f35d7b79f9219d60b3eb50dbe122a60ae64";
    private final String pk_lpos = "7cd925afaffb8466029213a05ae0faaff9c533dfb3ae446dbfcb971e45e2cacf";
    private final String mhash = "e3ef5e0cb7f89dfc1744003d9927bf588936e5a348641cf3984643a96014e22a";

//    private final String mpkx = "6a0749ce31ffa36f8cf731eb3d6c2f4b103aab27883deee19ee9c96b41beaf9baebd9a3aa50c35dbdf47745c18e413c858348055355704bce4124fc5385393af 7fb893cc0d30e20011f4f3542ea6451a9ccca7455544188c2855abdaf356049516b9f33708bc68db3f4825420b5c6857de7ae4c47ea628b9501259f0e808e2a7";
//    private final String mpky = "1aea36519d7ab31157aacf25b7a7b6e832b208750152e145bceaa394986a952aaf1f7ba2cf670e065bf7f66649171a7cda23e7938e8f03144c3cd5ff18a39fe4 4a303928b9a05fe5bb9401aebb78edb1bd792658c17c4745d0d74e588dd6843c6a8c2b9e054714a104ce3ebd05c7bf4582d8b848ac41e6a649f88847e25019da";

//    private final String mpkx2 = "1 3880112175745769906129488456404571855313298496010825544782964964123833489278388802716228358058131528518953774702792145679842058700395879994915548189595077 2080522071627240824848138990257546083185256914799209977492439073918549876575237775765153830820632785082314470786485656726830053964692583149237097989320386";
//    private final String mpky2 = "1 5670372393738907536622431434430349740406923175794846022394579096835401282364953141839015655887440665019253725982752473996102582433456993998941961062159678 2218500320645904249775794701597206295659537597791055170003216970928769044251419537635292801466765144220996985453770076693072401342092975755321573943161131";

//    private final String mpkx = "4a1597df1daf506c6471069af7b64071021f26676ff305ebc9760c345ce88c5d5855f85c5ee7bff0f998960154a6da2ca722b705615c88f50ea62933049695c5 27b9614b006b3387a13b3746918bd47aa189b483fbf5e36b13468946608d9ef8cb08c9ddfbbdf818aafbc7b6917ee5de3bef4ab83943207363f4091f5e32aec2";
//    private final String mpky = "6c4433f2104926310d6685c6d949ea95da57113e9ed819b7f9232de7418534ed3f14341d9ef44defd370b1d94e9fd311e4a9573f52e0c240ec6406eedd3b953e 2a5bcdbe2e04446f8bf3185bd39c33a1f7d75eeae6ab18919f7cd67b4095d02df457a6565d7a74339bab46a320aba5940c97eeb5e28830430e4a34df3f6e312b";

    // BIT-DEV MPK
    private final String mpkx = "28299a3ed26554ba90926e7f8a3e30f0c7c39213a06fc9092b6d8a5f94eafe19244df73456c79cd23c381ff53ba7bab9e5c2ed4804a2e34ca03574d688033e72 3a07a0aa1535dc62a7473a5693865c7175e02e51966ac8494c7b24bef76831524b2a8fca493d7e538df85ddd6ec2ab47fcbc37f98f09b7554193f007eb46c67e";
    private final String mpky = "612a16ea1924a1bfcfe59f995acc8e84795c428d3983fb619685dcbd438f864799809ca1cc7f4f192707848d0556c809aa6760df959826bb6347801c79bb56fb 6b12bb5b5d9ed6eaf40e9537b0468c45942c69992f9da12e4e7291eb860032075191c8fd93b05f5e97551c191daadc5a73d9f4f2785930c379a6dca924c6be82";


    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("integer");
    }

    public String checkSumTest(int length) {
        String a = getRandom(length);
        String b = getRandom(length);
        String c = getRandom(length);
        String text = "";
        final int radix = 16;
        text += "Best viewed in landscape orientation.\n";
        text += mobileVerify();
        text += "Current radix is " + radix + "\n";
        text += "Testing for numbers with " + length + " digits.\n";
        BigInteger abi = new BigInteger(a, radix);
        BigInteger bbi = new BigInteger(b, radix);
        BigInteger cCorrect = abi.add(bbi);
        String cCorrect16 = cCorrect.toString(radix);
        text += createLine(a, b, c, cCorrect16.equals(c));
        text += createLine(a, b, cCorrect16, true);
        text += createLine("1", "2", "3", true);
        text += createLine("1", "2", "4", false);
        return text;
    }

    public native boolean checkSum(String a, String b, String c);

    public native boolean verifyMobile(String p, String a, String b, String order, String irred,
                                       String gx, String gy, int k, String s1x, String s1y, String s2x, String s2y,
                                       String pk_lpos, String mhash, String mpkx, String mpky);

    public boolean verifyMobileCompat(String k_hash,
                                      String m_hash,
                                      String leader_id,
                                      Long nonce,
                                      List<String> s1xList,
                                      List<String> s1yList,
                                      List<String> s2xList,
                                      List<String> s2yList) {

        String s1x1 = new BigInteger(s1xList.get(0)).toString(16);
        String s1x2 = new BigInteger(s1xList.get(1)).toString(16);
        String s1x = s1x1.concat(" ").concat(s1x2);

        Log.d("SocketService", String.format("s1x: %s", s1x));

        String s1y1 = new BigInteger(s1yList.get(0)).toString(16);
        String s1y2 = new BigInteger(s1yList.get(1)).toString(16);
        String s1y = s1y1.concat(" ").concat(s1y2);

        Log.d("SocketService", String.format("s1y: %s", s1y));

        String s2x = "0 " + new BigInteger(s2xList.get(0)).toString(16);
        String s2y = "0 " + new BigInteger(s2yList.get(0)).toString(16);

        Log.d("SocketService", String.format("s2x: %s", s2x));
        Log.d("SocketService", String.format("s2y: %s", s2y));

        String pklpos = SageSign.INSTANCE.getSha256(k_hash.concat(leader_id).concat(nonce.toString()));

        //mhash
        String h = SageSign.INSTANCE.getSha256(m_hash.concat(leader_id));

        Log.d("SocketService", String.format("pklpos: %s", pklpos));
        Log.d("SocketService", String.format("h: %s", h));

        return verifyMobile(p, a, b, order, irred, gx, gy, k,
                s1x, //data.leader_sign.r.x
                s1y, //data.leader_sign.r.y
                s2x, //data.leader_sign.s.x
                s2y, //data.leader_sign.s.y
                pklpos, //sha256(data.mblock_data.k_hash + data.leader_id + data.mblock_data.nonce)
                h, //data.m_hash
                mpkx, mpky);
    }

    private boolean verifyMobile() {
        return verifyMobile(p, a, b, order, irred, gx, gy, k, s1x, s1y, s2x, s2y, pk_lpos, mhash, mpkx, mpky);
    }

    private boolean verifyMobileIncorrect() {
        return verifyMobile(p, a, b, order, irred, gy, gx, k, s1x, s1y, s2x, s2y, pk_lpos, mhash, mpkx, mpky);
    }

    private String getRandom(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int value = rand.nextInt(10);
            if (value == 0) {
                value = 1;
            }
            sb.append(value);
        }
        return sb.toString();
    }

    private String createLine(String a, String b, String c, boolean expected) {
        boolean result = checkSum(a, b, c);
        String res = "";
        if (result != expected) {
            res += "Warning:\n";
        }
        res += a + " + " + b + " = " + c + "\nLibrary returns: " + result + " (sholud be " + expected + ")\n";
        return res;
    }

    private String mobileVerify() {
        long begin = System.currentTimeMillis();
        boolean res = verifyMobile();
        long end = System.currentTimeMillis();
        long time = end - begin;
        String text = "";
        text += "Verify correct mobile is " + res + ", iteration time is " + time + "ms\n";
        begin = System.currentTimeMillis();
        res = verifyMobileIncorrect();
        end = System.currentTimeMillis();
        time = end - begin;
        text += "Verify incorrect mobile is " + res + ", iteration time is " + time + "ms\n";
        return text;
    }
}
