package BuffAndDebuff;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

// Buff 类型枚举（替代原来的 switch-case）
public enum BuffType {
    // 枚举常量定义（每个常量对应一个 buffID）
    // 格式：枚举名(buffID, 目标属性数组, 效果计算函数, 持续时间计算函数)
    LOW_ATTACK_BOOST(0, new int[]{0},
        level -> new double[]{0.1 * level},
        level -> 60.0 * level),

    MEDIUM_ATTACK_BOOST(1, new int[]{0},
        level -> new double[]{0.15 * level},
        level -> 50.0 * level),

    HIGH_ATTACK_BOOST(2, new int[]{0},
        level -> new double[]{0.225 * level},
        level -> 40.0 * level),

    DESPERATE_STRIKE(3, new int[]{0, 3, 5},
        level -> new double[]{1.0 + 0.3 * level, -0.8 + 0.1 * level, -1.0},
        level -> (level != 0) ? 50.0 / level : 0.0);

    // 枚举属性
    private final int buffID;
    private final int[] targets;
    private final Function<Integer, double[]> effectsCalculator;
    private final Function<Integer, Double> durationCalculator;

    // 缓存不同等级的 Buff 实例（ConcurrentMap<Level, BuffData>）
    private final ConcurrentMap<Integer, BuffData> levelCache = new ConcurrentHashMap<>();

    /**
     * 枚举构造函数
     * @param buffID          Buff 唯一标识
     * @param targets         固定目标属性数组
     * @param effectsCalculator  效果值计算函数（输入等级，返回效果数组）
     * @param durationCalculator 持续时间计算函数（输入等级，返回持续时间）
     */
    BuffType(int buffID, int[] targets,
             Function<Integer, double[]> effectsCalculator,
             Function<Integer, Double> durationCalculator) {
        this.buffID = buffID;
        this.targets = Arrays.copyOf(targets, targets.length);
        this.effectsCalculator = effectsCalculator;
        this.durationCalculator = durationCalculator;
    }

    /**
     * 根据等级获取 Buff 数据（带缓存）
     */
    public BuffData getBuff(int level) {
        return levelCache.computeIfAbsent(level, lv ->
            new BuffData(
                Arrays.copyOf(targets, targets.length),
                effectsCalculator.apply(lv),
                durationCalculator.apply(lv)
            )
        );
    }

    /**
     * 根据 buffID 查找对应的枚举常量
     */
    public static BuffType fromID(int buffID) {
        for (BuffType type : values()) {
            if (type.buffID == buffID) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid buff ID: " + buffID);
    }

    // 不可变的 Buff 数据类
    public static final class BuffData {
        private final int[] targets;
        private final double[] effects;
        private final double duration;
        private final String name;
        private final String description;
        private BuffData(int[] targets, double[] effects, double duration,String name) {
            this.targets = Arrays.copyOf(targets, targets.length);
            this.effects = Arrays.copyOf(effects, effects.length);
            this.duration = duration;
        }

        // 以下为 Getter 方法（返回拷贝保证不可变性）
        public int[] getTargets() {
            return Arrays.copyOf(targets, targets.length);
        }

        public double[] getEffects() {
            return Arrays.copyOf(effects, effects.length);
        }

        public double getDuration() {
            return duration;
        }
    }
}

// 使用示例
class Demo {
    public static void main(String[] args) {
        // 获取 buffID=3, level=5 的实例
        BuffType.BuffData buff = BuffType.fromID(3).getBuff(5);
        
        System.out.println("目标属性: " + Arrays.toString(buff.getTargets()));
        System.out.println("效果值: " + Arrays.toString(buff.getEffects()));
        System.out.println("持续时间: " + buff.getDuration());
    }
}