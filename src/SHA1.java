import java.math.BigInteger;
import java.util.Scanner;

public class SHA1 {
    public static byte[] hash(byte[] m) {
        m = padding(m);
        int[] w = new int[80];
        int[] h = new int[] { 0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0 };
        int group = m.length / 64;
        for (int i = 0; i < group; i++) {
            for (int j = 0; j < 16; j++)
                w[j] = ((m[i * 64 + j * 4] & 0xff) << 24) | ((m[i * 64 + j * 4 + 1] & 0xff) << 16)
                        | ((m[i * 64 + j * 4 + 2] & 0xff) << 8) | (m[i * 64 + j * 4 + 3] & 0xff);
            for (int j = 16; j < 80; j++)
                w[j] = shiftLeft(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1);
            h = f(h, w);
        }
        return toBtyes(h);
    }

    private static byte[] padding(byte[] m) {
        int len = m.length;
        byte[] padM = new byte[len % 64 < 56 ? (len / 64 + 1) * 64 : (len / 64 + 2) * 64];
        System.arraycopy(m, 0, padM, 0, len);
        padM[len] = (byte) 0x80;
        for (int i = len + 1; i < padM.length - 8; i++)
            padM[i] = 0;
        System.arraycopy(getLength(len), 0, padM, padM.length - 8, 8);
        return padM;
    }

    private static int[] f(int[] h, int[] w) {
        int[] result = new int[5];
        System.arraycopy(h, 0, result, 0, 5);
        int[] kt = { 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6 };
        int tmp;
        for (int i = 0; i < 80; i++) {
            tmp = shiftLeft(h[0], 5) + ft(h[1], h[2], h[3], i) + h[4] + w[i] + kt[i / 20];
            h[4] = h[3];
            h[3] = h[2];
            h[2] = shiftLeft(h[1], 30);
            h[1] = h[0];
            h[0] = tmp;
        }
        for (int i = 0; i < 5; i++)
            result[i] += h[i];
        return result;
    }

    private static int ft(int b, int c, int d, int t) {
        if (t <= 19)
            return (b & c) | ((~b) & d);
        else if (t <= 39)
            return b ^ c ^ d;
        else if (t <= 59)
            return (b & c) | (b & d) | (c & d);
        else
            return b ^ c ^ d;
    }

    private static int shiftLeft(int a, int n) {
        return a << n | a >>> (32 - n);
    }

    private static byte[] getLength(int byteLength) {
        byte[] len = new byte[8];
        byteLength <<= 3;
        for (int i = 7; i >= 0; i--) {
            len[i] = (byte) (byteLength & 0xff);
            byteLength >>= 8;
        }
        return len;
    }

    private static byte[] toBtyes(int[] h) {
        byte[] b = new byte[h.length * 4];
        for (int i = 0; i < h.length; i++) {
            b[i * 4] = (byte) (h[i] >>> 24);
            b[i * 4 + 1] = (byte) (h[i] >>> 16);
            b[i * 4 + 2] = (byte) (h[i] >>> 8);
            b[i * 4 + 3] = (byte) h[i];
        }
        return b;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("message: ");
        byte[] m = scanner.nextLine().getBytes();
        byte[] h = hash(m);
        System.out.println(new BigInteger(1, h).toString(16));
        scanner.close();
    }
}