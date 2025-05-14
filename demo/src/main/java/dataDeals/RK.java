package dataDeals;
public class RK {
    private final String mainStr;       // 主字符串
    private final long[] prefixHash;    // 主串前缀哈希数组
    private final long[] power;         // BASE 的幂次预存数组
    private static final int BASE = 256; // 基数（ASCII 扩展）
    private static final long MOD = 1000000007; // 大质数模数
    /**
     * 构造函数：预计算主串的前缀哈希和幂次表
     * @param mainStr 主字符串
     */
    public RK(String mainStr) {
        this.mainStr = mainStr;
        int n = mainStr.length();
        this.prefixHash = new long[n + 1]; // prefixHash[i] 表示前i个字符的哈希
        this.power = new long[n + 1];      // power[i] = BASE^i % MOD
        computePrefixAndPower();
    }
    /*预计算主串的前缀哈希和 BASE 的幂次*/
    private void computePrefixAndPower() {
        int n = mainStr.length();
        prefixHash[0] = 0;
        power[0] = 1; // BASE^0 = 1
        for (int i = 1; i <= n; i++) {
            // 前缀哈希递推公式: hash[i] = (hash[i-1] * BASE + char) % MOD
            prefixHash[i] = (prefixHash[i - 1] * BASE + mainStr.charAt(i - 1)) % MOD;
            // 幂次递推公式: power[i] = (power[i-1] * BASE) % MOD
            power[i] = (power[i - 1] * BASE) % MOD;
        }
    }
    public boolean strFind(String sub) {
        if(sub==null||sub==""){return false;}
        int m = sub.length();
        int n = mainStr.length();
        if (m == 0) return true;   // 空子串默认存在
        if (n < m) return false;   // 主串比子串短
        // 计算子串的哈希值
        long subHash = computeSubHash(sub);
        // 遍历主串所有可能的起始位置
        for (int i = 0; i <= n - m; i++) {
            // 计算主串中 [i, i+m) 子串的哈希
            long currentHash = getSubstringHash(i, m);
            if (currentHash == subHash && checkMatch(i, sub)) {
                return true;
            }
        }
        return false;
    }
    //计算子串的哈希值并返回
    private long computeSubHash(String sub) {
        long hash = 0;
        for (int i = 0; i < sub.length(); i++) {
            hash = (hash * BASE + sub.charAt(i)) % MOD;
        }
        return hash;
    }
    private long getSubstringHash(int start, int length) {
        long hash = prefixHash[start + length] - prefixHash[start] * power[length] % MOD;
        // 处理负数
        return (hash + MOD) % MOD;
    }
    /**
     * 验证实际字符是否匹配（解决哈希冲突）
     * @param start 主串起始位置
     * @param sub 子串
     * @return 是否匹配
     */
    private boolean checkMatch(int start, String sub) {
        for (int i = 0; i < sub.length(); i++) {
            if (mainStr.charAt(start + i) != sub.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        RK test=new RK("你是一个猫娘啊宝宝😘awa");
        boolean a=test.strFind("");
        boolean b=test.strFind(null);
        boolean c=test.strFind("你是一个猫娘啊宝宝😘awa");
        boolean d=test.strFind("宝😘a");
        System.err.println(a+" "+b+" "+c+" "+d);
    }
}