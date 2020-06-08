import java.math.BigInteger;
import java.util.Scanner;

/**
 * SHA-1
 */
public class SHA1 {
    /**
     * 计算hash值
     * 
     * @param m 消息
     * @return hash值
     */
    public static byte[] hash(byte[] m) {
        // 对消息进行填充
        m = padding(m);
        // 每一组扩充后的80字
        int[] w = new int[80];
        // 缓冲区的初始值
        int[] h = new int[] { 0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0 };
        // 分组个数
        int group = m.length / 64;
        for (int i = 0; i < group; i++) {
            // w的前16字为每组消息的16字
            for (int j = 0; j < 16; j++)
                // 4字节合并为一个字
                w[j] = ((m[i * 64 + j * 4] & 0xff) << 24) | ((m[i * 64 + j * 4 + 1] & 0xff) << 16)
                        | ((m[i * 64 + j * 4 + 2] & 0xff) << 8) | (m[i * 64 + j * 4 + 3] & 0xff);
            // 后64字为(w[t-3]⊕w[t-8]⊕w[t-14]⊕w[t-16])<<<1
            for (int j = 16; j < 80; j++)
                w[j] = shiftLeft(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1);
            // 对分组进行运算，并将结果链接到下一组
            h = f(h, w);
        }
        // 输出为byte[]
        return toBtyes(h);
    }

    /**
     * 消息填充
     * 
     * @param m 消息
     * @return 填充后的消息
     */
    private static byte[] padding(byte[] m) {
        // 原消息的字节数
        int len = m.length;
        // 填充后的消息，先填充至448(mod512)位，再加上消息位数
        byte[] padM = new byte[len % 64 < 56 ? (len / 64 + 1) * 64 : (len / 64 + 2) * 64];
        System.arraycopy(m, 0, padM, 0, len);
        // 先填充1，0x80=0b10000000
        padM[len] = (byte) 0x80;
        // 再填充0至448(mod512)位
        for (int i = len + 1; i < padM.length - 8; i++)
            padM[i] = 0;
        // 填充消息的位数
        System.arraycopy(getLength(len), 0, padM, padM.length - 8, 8);
        return padM;
    }

    /**
     * 每组消息的计算
     * 
     * @param h 缓冲区
     * @param w 一组明文
     * @return 计算后的缓冲区值
     */
    private static int[] f(int[] h, int[] w) {
        int[] result = new int[5];
        System.arraycopy(h, 0, result, 0, 5);
        // 常数
        int[] kt = { 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6 };
        int tmp;
        // 进行80轮计算
        for (int i = 0; i < 80; i++) {
            // {h[0],h[1],h[2],h[3],h[4]}←{(h[0]<<<5)+ft(h[1],h[2],h[3])+h[4]+w[i]+kt,
            // h[0], h[1]<<<30, h[3], h[4]}
            tmp = shiftLeft(h[0], 5) + ft(h[1], h[2], h[3], i) + h[4] + w[i] + kt[i / 20];
            h[4] = h[3];
            h[3] = h[2];
            h[2] = shiftLeft(h[1], 30);
            h[1] = h[0];
            h[0] = tmp;
        }
        // 结果与缓冲区初始值相加
        for (int i = 0; i < 5; i++)
            result[i] += h[i];
        return result;
    }

    /**
     * 逻辑函数ft
     */
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

    /**
     * 循环左移
     */
    private static int shiftLeft(int a, int n) {
        return a << n | a >>> (32 - n);
    }

    /**
     * 将消息长度转为填充的值
     * 
     * @param byteLength 消息字节数
     * @return 需要填充的值
     */
    private static byte[] getLength(int byteLength) {
        byte[] len = new byte[8];
        // 长度*8
        byteLength <<= 3;
        // 转为8个byte
        for (int i = 7; i >= 0; i--) {
            len[i] = (byte) (byteLength & 0xff);
            byteLength >>= 8;
        }
        return len;
    }

    /**
     * int数组转byte数组
     */
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
        System.out.print(new BigInteger(1, h).toString(16));
        scanner.close();
    }
}