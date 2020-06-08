import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * SHA-1第一类生日攻击
 */
public class FirstBirthdayAttack {
    /**
     * 第一类生日攻击
     * 
     * @param h 要攻击的hash值
     * @param n 相同位数(前n位)
     * @return 前n位相同的一个消息
     */
    public static byte[] attack(byte[] h, int n) {
        Random random = new Random();
        BigInteger a;
        byte[] b;
        byte[] h1;
        // 不断随机产生消息，计算hash值，和给出的hash值对比
        while (true) {
            a = new BigInteger(240, random);
            b = a.toByteArray();
            h1 = SHA1.hash(b);
            if (sameBits(h, h1) >= n)
                return b;
        }
    }

    /**
     * 计算从最高位开始连续相同的位数
     */
    private static int sameBits(byte[] a, byte[] b) {
        byte c;
        int n = 0;
        for (int i = 0; i < a.length; i++) {
            c = (byte) (a[i] ^ b[i]);
            //若c的某一位为0，则a和b的对应位相同
            for (int j = 7; j >= 0; j--) {
                if (c >> j == 0)
                    n++;
                else
                    return n;
            }
        }
        return n;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("hash:");
        byte[] h = new byte[20];
        char[] c = scanner.next().toCharArray();
        for (int i = 0; i < 20; i++)
            h[i] = (byte) Integer.parseInt(String.valueOf(c, 2 * i, 2), 16);
        System.out.print("length of same bits:");
        int n = scanner.nextInt();
        byte[] b = attack(h, n);
        byte[] h1 = SHA1.hash(b);
        System.out.printf("message with same %d bits:\n", n);
        System.out.println(new BigInteger(1, b).toString(16));
        System.out.println("hash:");
        System.out.print(new BigInteger(1, h1).toString(16));
        scanner.close();
    }
}