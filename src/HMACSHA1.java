import java.math.BigInteger;
import java.util.Scanner;

/**
 * HMAC-SHA-1
 */
public class HMACSHA1 {
    /**
     * 加密
     * 
     * @param m 消息
     * @param k 密钥
     * @return MAC
     */
    public static byte[] encrypt(byte[] m, byte[] k) {
        // 对k进行填充得到k+
        byte[] kPlus = paddingK(k);
        byte[] si = new byte[64];
        // si=k+ ⊕ ipad
        for (int i = 0; i < 64; i++)
            si[i] = (byte) (kPlus[i] ^ 0x36);
        // in1=si||m
        byte[] in1 = new byte[64 + m.length];
        System.arraycopy(si, 0, in1, 0, 64);
        System.arraycopy(m, 0, in1, 64, m.length);
        // h = H(si||m)
        byte[] h = SHA1.hash(in1);
        byte[] s0 = new byte[64];
        // s0=k+ ⊕ opad
        for (int i = 0; i < 64; i++)
            s0[i] = (byte) (kPlus[i] ^ 0x5c);
        // in2=s0||h
        byte[] in2 = new byte[84];
        System.arraycopy(s0, 0, in2, 0, 64);
        System.arraycopy(h, 0, in2, 64, 20);
        // 输出H(s0||h)
        return SHA1.hash(in2);
    }

    /**
     * 对k进行填充
     */
    private static byte[] paddingK(byte[] k) {
        byte[] kPlus = new byte[64];
        // 如果k超过512位，令k=H(k)
        if (k.length > 64)
            k = SHA1.hash(k);
        // 在k左边填充0，得到512位k+
        System.arraycopy(k, 0, kPlus, 64 - k.length, k.length);
        return kPlus;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("message:");
        byte[] m = scanner.nextLine().getBytes();
        System.out.println("k:");
        char[] c = scanner.next().toCharArray();
        byte[] k = new byte[c.length / 2];
        for (int i = 0; i < k.length; i++)
            k[i] = (byte) Integer.parseInt(String.valueOf(c, 2 * i, 2), 16);
        byte[] h = encrypt(m, k);
        System.out.print(new BigInteger(1, h).toString(16));
        scanner.close();
    }
}
