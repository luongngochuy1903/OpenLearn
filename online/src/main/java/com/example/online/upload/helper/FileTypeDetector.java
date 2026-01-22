package com.example.online.upload.helper;

public final class FileTypeDetector {
    public enum FileType {
        IMAGE,
        VIDEO,
        UNKNOWN
    }

    public static FileType detect(byte[] header) {
        if (isPng(header) || isJpeg(header) || isWebp(header)) {
            return FileType.IMAGE;
        }
        if (isMp4(header) || isWebm(header) || isMov(header)) {
            return FileType.VIDEO;
        }
        return FileType.UNKNOWN;
    }

    /* ===== IMAGE ===== */

    private static boolean isPng(byte[] h) {
        return h.length >= 4 &&
                h[0] == (byte) 0x89 &&
                h[1] == 0x50 &&
                h[2] == 0x4E &&
                h[3] == 0x47;
    }

    private static boolean isJpeg(byte[] h) {
        return h.length >= 3 &&
                h[0] == (byte) 0xFF &&
                h[1] == (byte) 0xD8 &&
                h[2] == (byte) 0xFF;
    }

    private static boolean isWebp(byte[] h) {
        return h.length >= 12 &&
                h[0]=='R' && h[1]=='I' && h[2]=='F' && h[3]=='F' &&
                h[8]=='W' && h[9]=='E' && h[10]=='B' && h[11]=='P';
    }

    /* ===== VIDEO ===== */

    private static boolean isMp4(byte[] h) {
        return h.length >= 12 &&
                h[4]=='f' && h[5]=='t' && h[6]=='y' && h[7]=='p';
    }

    private static boolean isMov(byte[] h) {
        return h.length >= 12 &&
                h[4]=='f' && h[5]=='t' && h[6]=='y' && h[7]=='p' &&
                (h[8]=='q' || h[8]=='m');
    }

    private static boolean isWebm(byte[] h) {
        return h.length >= 4 &&
                h[0]==0x1A && h[1]==0x45 &&
                h[2]==(byte)0xDF && h[3]==(byte)0xA3;
    }
}
