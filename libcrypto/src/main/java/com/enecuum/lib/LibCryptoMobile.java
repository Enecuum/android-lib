package com.enecuum.lib;

import android.util.Log;

import com.enecuum.lib.BuildConfig;
import com.enecuum.lib.SageSign;
import com.enecuum.lib.api.main.ApiRouter;

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


    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("integer");
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
                                      List<String> s2yList,
                                      String mpkx,
                                      String mpky) {

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
                mpkx,
                mpky);
    }
}
