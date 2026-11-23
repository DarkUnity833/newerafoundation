package net.darkunity.newerafoundation.storage;

/** Formatting of item counts for the storage terminal. */
public final class CountFormat {
    private static final String[] SUFFIX = {"K", "M", "B", "T", "Qa", "Qi"};
    private static final long[] UNIT = {1_000L, 1_000_000L, 1_000_000_000L, 1_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000_000_000L};
    private CountFormat() {}

    /** 1-999, then 1K-999K, 1M-999M, 1B-999B... Values below 10 of a unit keep one decimal (1.9M); always rounded down. */
    public static String compact(long n) {
        if (n < 1000) return Long.toString(Math.max(0, n));
        int i = 0;
        while (i + 1 < UNIT.length && n >= UNIT[i] * 1000L) i++;
        long unit = UNIT[i];
        long whole = n / unit;
        if (whole >= 10) return whole + SUFFIX[i];
        long tenth = (n % unit) * 10 / unit;
        return whole + (tenth > 0 ? "." + tenth : "") + SUFFIX[i];
    }

    /** Exact value with dots as thousands separators: 1.923.652 */
    public static String exact(long n) {
        String s = Long.toString(Math.abs(n));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (s.length() - i) % 3 == 0) sb.append('.');
            sb.append(s.charAt(i));
        }
        return (n < 0 ? "-" : "") + sb;
    }
}
