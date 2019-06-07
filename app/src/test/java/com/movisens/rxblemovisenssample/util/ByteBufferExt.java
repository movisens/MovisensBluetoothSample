package com.movisens.rxblemovisenssample.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferExt {

    private ByteBuffer buffer;

    public static ByteBufferExt allocate(int i) {
        ByteBufferExt result = new ByteBufferExt();
        result.buffer = ByteBuffer.allocate(i);
        result.buffer.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }

    public static ByteBufferExt wrap(byte[] byteArray) {
        ByteBufferExt result = new ByteBufferExt();
        result.buffer = ByteBuffer.wrap(byteArray);
        result.buffer.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }

    public byte[] array() {
        return buffer.array();
    }

    public final int capacity() {
        return buffer.capacity();
    }

    public void getInt8(byte[] value, int i, int j) {
        buffer.get(value, i, j);
    }

    public void getUint8(short[] value, int offset, int length) {
        for (int i = 0; i < length; i++) {
            value[i + offset] = getUint8();
        }
    }

    public boolean getBoolean() {
        if (buffer.get() == (byte) 0) {
            return false;
        } else {
            return true;
        }
    }

    public float getFloat32() {
        return buffer.getFloat();
    }

    public short getInt16() {
        return buffer.getShort();
    }

    public int getInt32() {
        return buffer.getInt();
    }

    public long getInt64() {
        return buffer.getLong();
    }

    public byte getInt8() {
        return buffer.get();
    }

    public String getString() {
        String result = "";
        byte c;

        while ((c = buffer.get()) != 0) {
            result += (char) c;
        }

        return result;
    }

    public int getUint16() {
        int result = buffer.getShort();
        if (result < 0) {
            result += ((int) 1) << 16;
        }
        return result;
    }

    public long getUint32() {
        long result = buffer.getInt();
        if (result < 0) {
            result += ((long) 1) << 32;
        }
        return result;
    }

    public short getUint8() {
        short result = buffer.get();
        if (result < 0) {
            result += ((short) 1) << 8;
        }
        return result;
    }

    public void position(int i) {
        buffer.position(i);
    }

    public void putUint8(short[] value, int offset, int length) {
        for (int i = 0; i < length; i++) {
            putUint8(value[i + offset]);
        }
    }

    public void putInt8(byte[] value, int i, int j) {
        buffer.put(value, i, j);
    }

    public void putBoolean(boolean doEnable) {
        if (doEnable) {
            buffer.put((byte) 1);
        } else {
            buffer.put((byte) 0);
        }
    }

    public void putFloat32(Float value) {
        buffer.putFloat(value);
    }

    public void putInt16(short value) {
        buffer.putShort(value);
    }

    public void putInt32(int value) {
        buffer.putInt(value);
    }

    public ByteBuffer putInt64(long value) {
        return buffer.putLong(value);
    }

    public void putInt8(byte value) {
        buffer.put(value);
    }

    public void putString(String value) {
        buffer.put(value.getBytes());
        buffer.put((byte) 0);
    }

    public void putUint16(int value) {
        buffer.putShort((short) value);
    }

    public void putUint32(long value) {
        buffer.putInt((int) value);
    }

    public void putUint8(short value) {
        buffer.put((byte) value);
    }

    public void rewind() {
        buffer.rewind();
    }
}