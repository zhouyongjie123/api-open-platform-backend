package com.zyj.core.unit;

public enum MemoryUnit {
    BYTES(MemoryUnit.BYTES_SCALE),
    KILOBYTES(MemoryUnit.KILOBYTES_SCALE),
    MEGABYTES(MemoryUnit.MEGABYTES_SCALE),
    GIGABYTES(MemoryUnit.GIGABYTES_SCALE);

    private static final long BYTES_SCALE = 1L;

    private static final long KILOBYTES_SCALE = 1024 * BYTES_SCALE;

    private static final long MEGABYTES_SCALE=1024 * KILOBYTES_SCALE;

    private static final long GIGABYTES_SCALE = 1024 * MEGABYTES_SCALE;

    private final long bytes;

    MemoryUnit(long bytes) {
        this.bytes = bytes;
    }

    /**
     * 将内存值转换为字节
     *
     * @param value 内存值
     * @return 字节数
     */
    public long toBytes(long value) {
        return value * bytes;
    }

    /**
     * 将字节转换为指定单位的内存值
     *
     * @param bytes 字节数
     * @return 转换后的内存值
     */
    public double fromBytes(long bytes) {
        return (double) bytes / this.bytes;
    }
}

