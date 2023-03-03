package com.cvsgo.util;

public interface AuthConstants {

    String TOKEN_TYPE = "Bearer";

    long ACCESS_TOKEN_TTL_MILLISECOND = 1000 * 60 * 30;

    long REFRESH_TOKEN_TTL_MILLISECOND = 1000 * 60 * 60 * 24 * 14;

}
