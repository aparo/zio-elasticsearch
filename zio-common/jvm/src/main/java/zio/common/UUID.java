/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class UUID {

    private static final RandomBasedUUIDGenerator RANDOM_UUID_GENERATOR = new RandomBasedUUIDGenerator();
    private static final UUIDGenerator TIME_UUID_GENERATOR = new TimeBasedUUIDGenerator();

    /** Generates a time-based UUID (similar to Flake IDs), which is preferred when generating an ID to be indexed into a Lucene index as
     *  primary key.  The id is opaque and the implementation is free to change at any time! */
    public static String base64UUID() {
        return TIME_UUID_GENERATOR.getBase64UUID();
    }

    /** Returns a Base64 encoded version of a Version 4.0 compatible UUID as defined here: http://www.ietf.org/rfc/rfc4122.txt, using the
     *  provided {@code Random} instance */
    public static String randomBase64UUID(Random random) {
        return RANDOM_UUID_GENERATOR.getBase64UUID(random);
    }

    /** Returns a Base64 encoded version of a Version 4.0 compatible UUID as defined here: http://www.ietf.org/rfc/rfc4122.txt, using a
     *  private {@code SecureRandom} instance */
    public static String randomBase64UUID() {
        return RANDOM_UUID_GENERATOR.getBase64UUID();
    }

    /**
     * Static factory to retrieve a type 3 (name based) <tt>UUID</tt> based on
     * the specified byte array.
     *
     * @param name a byte array to be used to construct a <tt>UUID</tt>.
     * @return a <tt>UUID</tt> generated from the specified array.
     */
    public static String nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("SHA1 not supported");
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] &= 0x0f;  /* clear version        */
        md5Bytes[6] |= 0x40;  /* set to version 4     */
        md5Bytes[8] &= 0x3f;  /* clear variant        */
        md5Bytes[8] |= 0x80;  /* set to IETF variant  */
        byte[] encoded = Base64.getUrlEncoder().encode(md5Bytes);
        // we know the bytes are 16, and not a multi of 3, so remove the 2 padding chars that are added
        assert encoded[encoded.length - 1] == '=';
        assert encoded[encoded.length - 2] == '=';
        // we always have padding of two at the end, encode it differently
        return new String(encoded, 0, encoded.length - 2);

    }

    /**
     * Static factory to retrieve a type 3 (name based) <tt>UUID</tt> based on
     * the specified string.
     *
     * @param name a string to be used to construct a <tt>UUID</tt>.
     * @return a <tt>UUID</tt> generated from the specified string.
     */
    //PARO
    public static String nameUUIDFromString(String name) {
        return nameUUIDFromBytes(name.getBytes());
    }

    public static String fromString(String name) {
        return nameUUIDFromBytes(name.getBytes());
    }

}
