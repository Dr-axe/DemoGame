package BuffAndDebuff;
import java.util.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
public final class Buff {
    private final int[] targets;
    private final double[] effects;
    private final double buffTime;

    private static final ConcurrentMap<Integer, ConcurrentMap<Integer, Buff>> CACHE = new ConcurrentHashMap<>();
    // 工厂方法，获取Buff实例（带缓存）
    public static Buff getInstance(int buffID, int level) {
        return CACHE.computeIfAbsent(buffID, k -> new ConcurrentHashMap<>())
                   .computeIfAbsent(level, k -> new Buff(buffID, k));
    }
    private Buff(int buffID, int level) {
        switch (buffID) {
            case 0: // 低效攻击提升
                targets = new int[]{0};
                effects = new double[]{0.1 * level};
                buffTime = 60 * level;
                break;
            case 1: // 一般攻击提升
                targets = new int[]{0};
                effects = new double[]{0.15 * level};
                buffTime = 50 * level;
                break;
            case 2: // 高效攻击提升
                targets = new int[]{0};
                effects = new double[]{0.225 * level};
                buffTime = 40 * level;
                break;
            case 3: // 破釜沉舟
                targets = new int[]{0, 3, 5};
                effects = new double[]{
                    1.0 + 0.3 * level,
                    -0.8 + 0.1 * level,
                    -1.0
                };
                buffTime = (level != 0) ? 50.0 / level : 0; // 避免除以零
                break;
            default:
                targets = new int[0];
                effects = new double[0];
                buffTime = 0;
                break;
        }
    }

    // 获取方法（返回拷贝）
    public int[] getTargets() {
        return Arrays.copyOf(targets, targets.length);
    }

    public double[] getEffects() {
        return Arrays.copyOf(effects, effects.length);
    }

    public double getBuffTime() {
        return buffTime;
    }
}
