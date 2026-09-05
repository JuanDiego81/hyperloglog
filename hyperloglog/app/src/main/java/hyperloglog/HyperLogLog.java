package hyperloglog;

import java.util.Scanner;

public class HyperLogLog {
    private final int m;
    private final int shift;
    private final double MCONSTANT;
    private final int[] REGISTERS;
    private final int[] A = { 0x21ae4036, 0x32435171, 0xac3338cf, 0xea97b40c, 0x0e504b22, 0x9ff9a4ef, 0x111d014d,
            0x934f3787, 0x6cd079bf, 0x69db5c31, 0xdf3c28ed, 0x40daf2ad, 0x82a5891c, 0x4659c7b0, 0x73dc0ca8, 0xdad3aca2,
            0x00c74c7e, 0x9a2521e2, 0xf38eb6aa, 0x64711ab6, 0x5823150a, 0xd13a3a9a, 0x30a5aa04, 0x0fb9a1da, 0xef785119,
            0xc9f0b067, 0x1e7dde42, 0xdda4a7b2, 0x1a1c2640, 0x297c0633, 0x744edb48, 0x19adce93 };

    public HyperLogLog(int m) {
        this.m = m;
        // Changing m causes index out of bound errors if the right shift of the f
        // method is not adapted. We use the below formula to get the correct int to
        // shift by
        int p = (int) (Math.log(m) / Math.log(2));
        this.shift = 32 - p - 1;
        this.MCONSTANT = 0.7213 / (1 + (1.079 / m));
        this.REGISTERS = new int[m];
    }

    public int h(int x) {
        int hash = 0;
        for (int i = 0; i < A.length; i++) {
            int innerProduct = Integer.bitCount(A[i] & x);
            int parity = innerProduct & 1;
            hash = hash | (parity << i);
        }
        return hash;
    }

    public int f(int x) {
        return ((x * 0xbc164501) & 0x7fffffff) >> shift;
    }

    public int rho(int x) {
        // We add one as the algorithm considers the first bit from left to be 1
        return Integer.numberOfLeadingZeros(x) + 1;
    }

    public void processFromFile(Scanner sc) {
        while (sc.hasNextLine()) {
            String currLine = sc.nextLine();
            long value;
            if (currLine.startsWith("0x")) {
                value = Long.parseLong(currLine.substring(2), 16);
            } else {
                value = Long.parseLong(currLine);
            }
            int curr = (int) value;
            int index = f(curr);
            int x = h(curr);
            REGISTERS[index] = Integer.max(REGISTERS[index], rho(x));
        }
        // Feed the array res into the cardinality estimation method to get an
        // estimation for the number of distinct elements
    }

    public void processFromNumbers(int start, int end) {
        // The result of this must be included in the report
        for (int i = start; i < end; i++) {
            int curr = i;
            int index = f(curr);
            int x = h(curr);
            REGISTERS[index] = Integer.max(REGISTERS[index], rho(x));
        }

    }

    public long cardinalityEstimation() {
        double sum = 0.0;
        final double pow32 = Math.pow(2.0, 32);
        for (int j = 0; j < m; j++) {
            sum += Math.pow(2.0, -REGISTERS[j]);
        }
        double rawEstimate = MCONSTANT * m * m * Math.pow(sum, -1);
        int V = 0;
        for (int j = 0; j < REGISTERS.length; j++) {
            if (REGISTERS[j] == 0)
                V++;
        }
        if (rawEstimate <= (2.5 * m) && V > 0) {
            double result = m * Math.log((double) m / (double) V);
            return Math.round(result);
        }
        if (rawEstimate > (1.0 / 30.0) * Math.pow(2.0, 32)) {
            rawEstimate = -pow32 * Math.log(1 - (rawEstimate / pow32));
        }

        return Math.round(rawEstimate);
    }

    public static void main(String[] args) {
        // [256, 1024, 4096]

        Scanner sc = new Scanner(System.in);
        if ("hyperloglog256".equals(args[0])) {
            HyperLogLog hlog = new HyperLogLog(256);
            hlog.processFromFile(sc);
            System.out.println(hlog.cardinalityEstimation());

        } else if ("hyperloglog1024".equals(args[0])) {
            HyperLogLog hlog = new HyperLogLog(1024);
            hlog.processFromFile(sc);
            System.out.println(hlog.cardinalityEstimation());

        } else if ("hyperloglog4096".equals(args[0])) {
            HyperLogLog hlog = new HyperLogLog(4096);
            hlog.processFromFile(sc);
            System.out.println(hlog.cardinalityEstimation());
        } else {
            System.out.println("null");
        }
    }

}
