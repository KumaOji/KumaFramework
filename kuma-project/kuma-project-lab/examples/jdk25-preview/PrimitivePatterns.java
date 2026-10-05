// JDK25 third preview. Kept outside src/main/java so normal project builds need no preview flag.
public class PrimitivePatterns {
    public static void main(String[] args) {
        int small = 127;
        int large = 128;
        if (!(small instanceof byte b) || b != 127) throw new IllegalStateException("exact byte match failed");
        if (large instanceof byte _) throw new IllegalStateException("lossy narrowing must not match");
        String result = switch (42L) {
            case int i -> "exact-int:" + i;
            default -> "not-int";
        };
        if (!result.equals("exact-int:42")) throw new IllegalStateException("primitive switch failed");
        System.out.println("JDK25 primitive patterns preview: exact narrowing and primitive switch passed.");
    }
}
