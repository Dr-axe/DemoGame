package Character.CharacterStat_common;
import java.util.concurrent.ThreadLocalRandom;
import decides.Rand;

public class CharStatTest {
    public static void main(String[] args) {
        // 生成截断正态分布的等级（μ=50，σ=15，范围0-100）
        int level = generateTruncatedLevel();
        
        // 创建角色
        CharStat_Common character = new CharStat_Common(level);
        
        // 打印角色面板
        printCharacterSheet(character);
    }

    // 生成0-100的截断正态分布等级
    private static int generateTruncatedLevel() {
        final double mu = 50;    // 均值
        final double sigma = 15; // 标准差
        int level;
        
        do {
            level = (int) Math.round(ThreadLocalRandom.current().nextGaussian() * sigma + mu);
        } while (level < 0 || level > 100); // 强制限制在0-100
        
        return level;
    }

    // 角色面板打印方法
    private static void printCharacterSheet(CharStat_Common c) {
        System.out.println("╔════════════════════════════════╗");
        System.out.printf("║ %-15s Lv.%-3d   ║\n", "角色属性面板", c.level);
        System.out.println("╠════════════════════════════════╣");
        
        // 基础属性
        String[] statNames = {
            "攻击力", "暴击率", "暴击伤害", "防御力", "生命值", "移速",
            "格挡率", "格挡突破率", "闪避率", "闪避破解率", "防御无视率", "防御无视值",
            "坚韧度", "意志力", "技巧", "感知"
        };
        
        for (int i = 0; i < c.basicStats.length; i++) {
            String formattedValue = String.format("%.2f", c.basicStats[i]);
            if (statNames[i].contains("率") || statNames[i].contains("暴击伤害")) {
                formattedValue = String.format("%.1f%%", c.basicStats[i] * 100);
            }
            System.out.printf("║ %-8s: %-12s ║\n", statNames[i], formattedValue);
        }

        // 技能部分
        System.out.println("╠════════════════════════════════╣");
        System.out.printf("║ %-6s: %-3d | %-7s: %-3d ║\n", 
            "工艺", c.craftSkill, "农耕", c.farmSkill);
        System.out.printf("║ %-6s: %-3d | %-7s: %-3d ║\n", 
            "建造", c.constructSkill, "守卫", c.guardSkill);
        System.out.printf("║ %-6s: %-3d | %-7s: %-3d ║\n", 
            "烹饪", c.cookSkill, "交涉", c.chatSkill);
        System.out.printf("║ %-6s: %-3d | %-7s: %-3d ║\n", 
            "贸易", c.tradeSkill, "统御", c.lordSkill);
        System.out.println("╚════════════════════════════════╝");
    }
}