//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.provider;

import cn.hutool.core.util.RandomUtil;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCharacter;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomProvider {
    private static final String[] CHARACTERS = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    public static final int NUM_MIN_INDEX = 0;
    public static final int NUM_MAX_INDEX = 8;
    public static final int CHAR_MIN_INDEX = 8;
    public static final int CHAR_MAX_INDEX;
    public static final int UPPER_MIN_INDEX = 8;
    public static final int UPPER_MAX_INDEX = 31;
    public static final int LOWER_MIN_INDEX = 31;
    public static final int LOWER_MAX_INDEX;
    private static final int[][] COLOR;
    private static final String[] DICTIONARY;
    private static final List<String> WORDS;

    public static int randomInt(final int startInclusive, final int endExclusive) {
        return RandomUtil.randomInt(startInclusive, endExclusive);
    }

    public static int randomInt(int bound) {
        return RandomUtil.randomInt(bound);
    }

    public static List<String> randomWords(int wordCount) {
        return RandomUtil.randomEles(WORDS, wordCount);
    }

    public static Color[] randomColors(int number) {
        List<Color> colors = IntStream.range(0, number).mapToObj((i) -> randomColor()).toList();
        Color[] result = new Color[colors.size()];
        return (Color[])colors.toArray(result);
    }

    public static Color randomColor() {
        int[] color = COLOR[randomInt(COLOR.length)];
        return new Color(color[0], color[1], color[2]);
    }

    public static Color randomColor(int min, int max) {
        if (min > 255) {
            min = 255;
        }

        if (max > 255) {
            max = 255;
        }

        if (min < 0) {
            min = 0;
        }

        if (max < 0) {
            max = 0;
        }

        if (min > max) {
            min = 0;
            max = 255;
        }

        return new Color(randomInt(min, max), randomInt(min, max), randomInt(min, max));
    }

    private static String randomCharacter(CaptchaCharacter captchaCharacter) {
        return CHARACTERS[randomInt(captchaCharacter.getStart(), captchaCharacter.getEnd())];
    }

    public static String[] randomCharacters(int number, CaptchaCharacter captchaCharacter) {
        List<String> characters = IntStream.range(0, number).mapToObj((i) -> randomCharacter(captchaCharacter)).collect(Collectors.toList());
        String[] result = new String[characters.size()];
        return (String[])characters.toArray(result);
    }

    static {
        CHAR_MAX_INDEX = CHARACTERS.length;
        LOWER_MAX_INDEX = CHAR_MAX_INDEX;
        COLOR = new int[][]{{0, 135, 255}, {51, 153, 51}, {255, 102, 102}, {255, 153, 0}, {153, 102, 0}, {153, 102, 153}, {51, 153, 153}, {102, 102, 255}, {0, 102, 204}, {204, 51, 51}, {0, 153, 204}, {0, 51, 102}};
        DICTIONARY = new String[]{"的", "一", "了", "是", "我", "不", "在", "人", "们", "有", "来", "他", "这", "上", "着", "个", "地", "到", "大", "里", "说", "就", "去", "子", "得", "也", "和", "那", "要", "下", "看", "天", "时", "过", "出", "小", "么", "起", "你", "都", "把", "好", "还", "多", "没", "为", "又", "可", "家", "学", "只", "以", "主", "会", "样", "年", "想", "生", "同", "老", "中", "十", "从", "自", "面", "前", "头", "道", "它", "后", "然", "走", "很", "像", "见", "两", "用", "她", "国", "动", "进", "成", "回", "什", "边", "作", "对", "开", "而", "己", "些", "现", "山", "民", "候", "经", "发", "工", "向", "事", "命", "给", "长", "水", "几", "义", "三", "声", "于", "高", "手", "知", "理", "眼", "志", "点", "心", "战", "二", "问", "但", "身", "方", "实", "吃", "做", "叫", "当", "住", "听", "革", "打", "呢", "真", "全", "才", "四", "已", "所", "敌", "之", "最", "光", "产", "情", "路", "分", "总", "条", "白", "话", "东", "席", "次", "亲", "如", "被", "花", "口", "放", "儿", "常", "气", "五", "第", "使", "写", "军", "吧", "文", "运", "再", "果", "怎", "定", "许", "快", "明", "行", "因", "别", "飞", "外", "树", "物", "活", "部", "门", "无", "往", "船", "望", "新", "带", "队", "先", "力", "完", "却", "站", "代", "员", "机", "更", "九", "您", "每", "风", "级", "跟", "笑", "啊", "孩", "万", "少", "直", "意", "夜", "比", "阶", "连", "车", "重", "便", "斗", "马", "哪", "化", "太", "指", "变", "社", "似", "士", "者", "干", "石", "满", "日", "决", "百", "原", "拿", "群", "究", "各", "六", "本", "思", "解", "立", "河", "村", "八", "难", "早", "论", "吗", "根", "共", "让", "相", "研", "今", "其", "书", "坐", "接", "应", "关", "信", "觉", "步", "反", "处", "记", "将", "千", "找", "争", "领", "或", "师", "结", "块", "跑", "谁", "草", "越", "字", "加", "脚", "紧", "爱", "等", "习", "阵", "怕", "月", "青", "半", "火", "法", "题", "建", "赶", "位", "唱", "海", "七", "女", "任", "件", "感", "准", "张", "团", "屋", "离", "色", "脸", "片", "科", "倒", "睛", "利", "世", "刚", "且", "由", "送", "切", "星", "导", "晚", "表", "够", "整", "认", "响", "雪", "流", "未", "场", "该", "并", "底", "深", "刻", "平", "伟", "忙", "提", "确", "近", "亮", "轻", "讲", "农", "古", "黑", "告", "界", "拉", "名", "呀", "土", "清", "阳", "照", "办", "史", "改", "历", "转", "画", "造", "嘴", "此", "治", "北", "必", "服", "雨", "穿", "内", "识", "验", "传", "业", "菜", "爬", "睡", "兴", "形", "量", "咱", "观", "苦", "体", "众", "通", "冲", "合", "破", "友", "度", "术", "饭", "公", "旁", "房", "极", "南", "枪", "读", "沙", "岁", "线", "野", "坚", "空", "收", "算", "至", "政", "城", "劳", "落", "钱", "特", "围", "弟", "胜", "教", "热", "展", "包", "歌", "类", "渐", "强", "数", "乡", "呼", "性", "音", "答", "哥", "际", "旧", "神", "座", "章", "帮", "啦", "受", "系", "令", "跳", "非", "何", "牛", "取", "入", "岸", "敢", "掉", "忽", "种", "装", "顶", "急", "林", "停", "息", "句", "区", "衣", "般", "报", "叶", "压", "慢", "叔", "背", "细"};
        WORDS = Arrays.stream(DICTIONARY).collect(Collectors.toList());
    }
}
