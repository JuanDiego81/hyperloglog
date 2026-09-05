package hyperloglog;

public class HyperLogLogExperiment {
    static HyperLogLog hlog = new HyperLogLog(1024);

    public static void test() {
        hlog.processFromNumbers(1_000_000, 2_000_000);
        System.out.println(hlog.cardinalityEstimation());
    }

    public static void main(String[] args) {
        HyperLogLogExperiment.test();
    }
}
