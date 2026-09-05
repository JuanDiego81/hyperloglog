package hyperloglog;

public class HashFunctionExperiment {
    public static void test() {
        HyperLogLog hlog = new HyperLogLog(1024);
        for (int i = 1; i <= 1_000_000; i++) {
            int hash = hlog.h(i);
            int pVal = hlog.rho(hash);
            // Pipe the result into a file using bash
            // Analyze with python
            System.out.println(pVal);
        }
    }
}
