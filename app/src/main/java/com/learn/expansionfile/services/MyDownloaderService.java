package com.learn.expansionfile.services;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;
import com.learn.expansionfile.receiver.FileAlarmReceiver;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public class MyDownloaderService extends DownloaderService {

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOn047lRIZwQNWRruPg6/46pdn8SbNQlosYd3K+X+U4jVKI7gyhLqL2yJhMgnnf2Y1GHLGfI6SgcVPOMJf4/Ctxir2RepB63ewkaz/ycjybIyQRtXkervbSnafVxOJnAcVbdMFOuLeeXMq6SVDJY11wvt1gHD1612EY/nl8gAcl2Pgw4/YtZBFlgBL8J6SzFJiWOiAOzovRrhAGDBaqho3ZuH6I6O7bY+nED78tXFUIN9+lMYCVhpSr6i5mQfaCsOS8zK1wCDXh3RtpQBLheBkLEGGrraWmk57aqMwJ6sse5BJM4auk+YyQFjaSALwgLEz6ppdYdkBYujckc4o5r8QIDAQAB";

    // You should also modify this salt
    public static final byte[] SALT = new byte[]{1, 42, -12, -1, 54, 98,
            -100, -12, 43, 2, -8, -4, 9, 5, -106, -107, -33, 45, -1, 84
    };

    @Override
    public String getPublicKey() {
        return PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return FileAlarmReceiver.class.getName();
    }
}
