package BuffAndDebuff;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

// Buff 类型枚举（包含名称和描述）
public enum BuffType {
    // 枚举常量定义（每个常量对应一个 buffID）
    // 格式：枚举名(buffID, 目标属性数组, 效果计算函数, 持续时间计算函数, 名称, 描述)
    
    LOW_ATTACK_BOOST(0, new int[]{0},
        level -> new double[]{0.1 * level},
        level -> 600.0 * level,
        "低级攻击提升",
        "攻击力提升10%*level，持续6*level秒"),
    MEDIUM_ATTACK_BOOST(1, new int[]{0},
        level -> new double[]{0.15 * level},
        level -> 500.0 * level,
        "中级攻击提升",
        "攻击力提升15%*level，持续5*level秒"),
    HIGH_ATTACK_BOOST(2, new int[]{0},
        level -> new double[]{0.225 * level},
        level -> 400.0 * level,
        "高级攻击提升",
        "攻击力提升22.5%*level，持续4*level秒"),

    DESPERATE_STRIKE(3, new int[]{0, 3, 5},
        level -> new double[]{1.0 + 0.3 * level, -0.8 + 0.1 * level, -1.0},
        level -> (level != 0) ? 500.0 / level : 0.0,
        "破釜沉舟",
        "提升攻击力(100%+30%*level)，降低防御(80%-10%*level)，减少速度100%，持续5/level秒"),
    
    LOW_DEFENSE_BOOST(4, new int[]{3},
        level -> new double[]{0.10 * level},
        level -> 600.0 * level,
        "低级防御提升",
        "防御力提升10%*level，持续6*level秒"),
    MEDIUM_DEFENSE_BOOST(5, new int[]{3},
        level -> new double[]{0.15 * level},
        level -> 500.0 * level,
        "中级防御提升",
        "防御力提升15%*level，持续5*level秒"),
    HIGH_DEFENSE_BOOST(6, new int[]{3},
        level -> new double[]{0.225 * level},
        level -> 400.0 * level,
        "高级防御提升",
        "防御力提升22.5%*level，持续4*level秒"),
    
    STRONG_SURVIVE_WILLING(7,new int[]{0,3,5},
        level -> new double[]{0,0.8+0.2*level,0.6+0.4*level},
        level -> 1000.0+200*level,
        "超级求生欲爆发",
        "攻击力-100%，但是防御力提升80%+20%*level,速度提升60%+40%*level\n嘻嘻，我一定要活下去"),

        /*未完待续 */
    
        ;
    
    // 枚举属性
    private final int buffID;
    private final int[] targets;
    private final Function<Integer, double[]> effectsCalculator;
    private final Function<Integer, Double> durationCalculator;
    private final String name;
    private final String description;

    // 缓存不同等级的 Buff 实例（ConcurrentMap<Level, BuffData>）
    private final ConcurrentMap<Integer, BuffData> levelCache = new ConcurrentHashMap<>();

    /**
     * 枚举构造函数
     * @param buffID          Buff 唯一标识
     * @param targets         固定目标属性数组
     * @param effectsCalculator  效果值计算函数（输入等级，返回效果数组）
     * @param durationCalculator 持续时间计算函数（输入等级，返回持续时间）
     * @param name            Buff 名称
     * @param description     Buff 描述
     */
    BuffType(int buffID, int[] targets,
             Function<Integer, double[]> effectsCalculator,
             Function<Integer, Double> durationCalculator,
             String name,
             String description) {
        this.buffID = buffID;
        this.targets = Arrays.copyOf(targets, targets.length);
        this.effectsCalculator = effectsCalculator;
        this.durationCalculator = durationCalculator;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据等级获取 Buff 数据（带缓存）
     */
    public BuffData getBuff(int level) {
        return levelCache.computeIfAbsent(level, lv ->
            new BuffData(
                this,  // 传递当前 BuffType 实例
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

    // Getter 方法
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // 不可变的 Buff 数据类
    public static final class BuffData {
        private final BuffType type;
        private final int[] targets;
        private final double[] effects;
        private final double duration;

        private BuffData(BuffType type, int[] targets, double[] effects, double duration) {
            this.type = type;
            this.targets = Arrays.copyOf(targets, targets.length);
            this.effects = Arrays.copyOf(effects, effects.length);
            this.duration = duration;
        }

        // Getter 方法（返回拷贝保证不可变性）
        public BuffType getType() {
            return type;
        }

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
        for (int i = 0; i < 8; i++) {
            BuffType.BuffData buff = BuffType.fromID(i).getBuff(5); 
            System.out.println("Buff名称: " + buff.getType().getName());
            System.out.println("Buff描述: " + buff.getType().getDescription());
            System.out.println("目标属性: " + Arrays.toString(buff.getTargets()));
            System.out.println("效果值: " + Arrays.toString(buff.getEffects()));
            System.out.println("持续时间: " + buff.getDuration());
        }
        
    }
}