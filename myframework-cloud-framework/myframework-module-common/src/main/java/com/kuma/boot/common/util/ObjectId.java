/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.util;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局唯一标识符，类似 MongoDB ObjectId
 *
 * @author kuma
 */
public final class ObjectId implements Comparable<ObjectId>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final int OBJECT_ID_LENGTH = 12;
    private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

    private static final int RANDOM_VALUE1;
    private static final short RANDOM_VALUE2;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final int timestamp;
    private final int counter;
    private final int randomValue1;
    private final short randomValue2;

    static {
        try {
            SecureRandom secureRandom = new SecureRandom();
            RANDOM_VALUE1 = secureRandom.nextInt(0x01000000);
            RANDOM_VALUE2 = (short) secureRandom.nextInt(0x00008000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectId get() {
        return new ObjectId();
    }

    public ObjectId() {
        this(new Date());
    }

    public ObjectId(final Date date) {
        this(dateToTimestampSeconds(date), NEXT_COUNTER.getAndIncrement() & LOW_ORDER_THREE_BYTES, false);
    }

    private ObjectId(final int timestamp, final int counter, final boolean checkCounter) {
        this(timestamp, RANDOM_VALUE1, RANDOM_VALUE2, counter, checkCounter);
    }

    private ObjectId(final int timestamp, final int randomValue1, final short randomValue2, final int counter, final boolean checkCounter) {
        if (checkCounter && ((counter & 0xff000000) != 0)) {
            throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
        }
        this.timestamp = timestamp;
        this.counter = counter & LOW_ORDER_THREE_BYTES;
        this.randomValue1 = randomValue1;
        this.randomValue2 = randomValue2;
    }

    public ObjectId(final byte[] bytes) {
        this(ByteBuffer.wrap(bytes));
    }

    public ObjectId(final ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < OBJECT_ID_LENGTH) {
            throw new IllegalArgumentException("buffer can not be null and must have at least 12 bytes");
        }
        this.timestamp = makeInt(buffer.get(), buffer.get(), buffer.get(), buffer.get());
        this.randomValue1 = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
        this.randomValue2 = makeShort(buffer.get(), buffer.get());
        this.counter = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(OBJECT_ID_LENGTH);
        putToByteBuffer(buffer);
        return buffer.array();
    }

    public void putToByteBuffer(final ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < OBJECT_ID_LENGTH) {
            throw new IllegalArgumentException("buffer can not be null and must have at least 12 bytes");
        }
        buffer.put(int3(this.timestamp));
        buffer.put(int2(this.timestamp));
        buffer.put(int1(this.timestamp));
        buffer.put(int0(this.timestamp));
        buffer.put(int2(this.randomValue1));
        buffer.put(int1(this.randomValue1));
        buffer.put(int0(this.randomValue1));
        buffer.put(short1(this.randomValue2));
        buffer.put(short0(this.randomValue2));
        buffer.put(int2(this.counter));
        buffer.put(int1(this.counter));
        buffer.put(int0(this.counter));
    }

    public String toHexString() {
        char[] chars = new char[OBJECT_ID_LENGTH * 2];
        int i = 0;
        for (byte b : toByteArray()) {
            chars[i++] = HEX_CHARS[(b >> 4) & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    @Override
    public String toString() {
        return toHexString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectId objectId = (ObjectId) o;
        return timestamp == objectId.timestamp && counter == objectId.counter
                && randomValue1 == objectId.randomValue1 && randomValue2 == objectId.randomValue2;
    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + counter;
        result = 31 * result + randomValue1;
        result = 31 * result + randomValue2;
        return result;
    }

    @Override
    public int compareTo(final ObjectId other) {
        if (other == null) throw new NullPointerException();
        byte[] byteArray = toByteArray();
        byte[] otherByteArray = other.toByteArray();
        for (int i = 0; i < OBJECT_ID_LENGTH; i++) {
            if (byteArray[i] != otherByteArray[i]) {
                return ((byteArray[i] & 0xff) < (otherByteArray[i] & 0xff)) ? -1 : 1;
            }
        }
        return 0;
    }

    private static int dateToTimestampSeconds(final Date time) {
        return (int) (time.getTime() / 1000);
    }

    private static int makeInt(final byte b3, final byte b2, final byte b1, final byte b0) {
        return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
    }

    private static short makeShort(final byte b1, final byte b0) {
        return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff)));
    }

    private static byte int3(final int x) { return (byte) (x >> 24); }
    private static byte int2(final int x) { return (byte) (x >> 16); }
    private static byte int1(final int x) { return (byte) (x >> 8); }
    private static byte int0(final int x) { return (byte) x; }
    private static byte short1(final short x) { return (byte) (x >> 8); }
    private static byte short0(final short x) { return (byte) x; }
}
