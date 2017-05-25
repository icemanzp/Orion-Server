/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.jack.netty.server.securechat;

import java.io.InputStream;

/**
 * A bogus key store which provides all the required information to
 * create an example SSL connection.
 * <p>
 * To generate a bogus key store:
 * <pre>
 * keytool  -genkey -alias securechat -keysize 2048 -validity 36500
 *          -keyalg RSA -dname "CN=securechat"
 *          -keypass secret -storepass secret
 *          -keystore cert.jks
 * </pre>
 */
public final class SecureChatKeyStore {

    public static InputStream asInputStream(String jksPath) {

        InputStream in = SecureChatKeyStore.class.getResourceAsStream(jksPath);
        return in;
    }

    public static char[] getCertificatePassword(String jksPath) {
        return jksPath.toCharArray();
    }

    public static char[] getKeyStorePassword(String jksPath) {
        return jksPath.toCharArray();
    }

    private SecureChatKeyStore() {
        // Unused
    }
}
