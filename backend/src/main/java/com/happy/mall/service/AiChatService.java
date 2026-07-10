package com.happy.mall.service;

import com.happy.mall.entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI聊天服务 - 本地模拟版本
 */
@Service
public class AiChatService {

    // 关键词和回复映射
    private final Map<String, List<String>> keywordResponses = new HashMap<>();

    // 默认回复
    private final List<String> defaultResponses = Arrays.asList(
        "抱歉，我还在学习中，暂时无法回答这个问题。你可以问我关于学习方法、考试技巧、时间管理等问题哦！😊",
        "这个问题有点难住我了呢～不过你可以问我关于学习的问题，比如：\n- 如何高效学习？\n- 怎么提高记忆力？\n- 期末怎么复习？",
        "我主要擅长回答学习相关的问题哦！你可以试试问我：\n📚 学习方法\n📝 考试技巧\n⏰ 时间管理\n💪 学科建议"
    );

    // 问候回复
    private final List<String> greetings = Arrays.asList(
        "你好呀！👋 我是你的AI学习小助手，有什么可以帮助你的吗？",
        "嗨！很高兴见到你！我是Happy Campus的AI助手，可以帮你解答学习相关的问题～",
        "你好！🎓 我是学习小助手，随时准备为你服务！",
        "你好！😊 今天有什么学习上的问题想问我吗？"
    );

    public AiChatService() {
        initKeywordResponses();
    }

    /**
     * 初始化关键词回复
     */
    private void initKeywordResponses() {
        // ========== 问候类 ==========
        keywordResponses.put("你好|嗨|hi|hello|在吗|在不在", greetings);

        keywordResponses.put("你是谁|你叫什么|介绍下自己", Arrays.asList(
            "我是Happy Campus的AI学习助手！🤖\n\n我可以帮你：\n📚 解答学习问题\n💪 提供学习方法建议\n📝 分享考试技巧\n⏰ 时间管理指导\n🎯 各科学习建议\n\n有什么问题尽管问我！"
        ));

        keywordResponses.put("谢谢|感谢|thanks|thank|多谢", Arrays.asList(
            "不客气！很高兴能帮到你～有什么其他问题随时问我！😊",
            "这是我应该做的！祝你学习顺利！📚",
            "不用谢！学习加油哦！💪",
            "能帮到你我很开心！有问题随时找我～"
        ));

        keywordResponses.put("再见|拜拜|bye|88|晚安", Arrays.asList(
            "再见！祝你学习进步！有问题随时来找我～👋",
            "拜拜！好好学习，天天向上！📚",
            "晚安！早点休息，明天继续加油！🌙"
        ));

        // ========== 学习方法类 ==========
        keywordResponses.put("高效学习|怎么学|如何学习|学习方法|怎么高效", Arrays.asList(
            "📚 高效学习方法推荐：\n\n1️⃣ 番茄工作法：25分钟专注 + 5分钟休息\n2️⃣ 费曼学习法：用自己的话解释知识\n3️⃣ 间隔重复：今天→明天→3天→7天→15天\n4️⃣ 主动学习：做笔记、提问、讨论\n5️⃣ 思维导图：把知识可视化\n\n💡 记住：理解比死记硬背更重要！",
            "想要高效学习？试试这些技巧：\n\n🎯 设定明确目标\n📝 做思维导图总结\n⏰ 利用碎片时间\n✅ 完成后给自己奖励\n😴 保证充足睡眠\n\n记住，学习效率比学习时长更重要！",
            "高效学习的秘诀：\n\n1️⃣ 找到自己的学习节奏\n2️⃣ 理解优先，记忆其次\n3️⃣ 多做练习，学以致用\n4️⃣ 定期复习，防止遗忘\n5️⃣ 保持好奇心，主动探索"
        ));

        keywordResponses.put("学习计划|制定计划|规划|计划表", Arrays.asList(
            "📋 制定学习计划的方法：\n\n1️⃣ 确定目标：想要达到什么效果\n2️⃣ 分解任务：大目标拆成小任务\n3️⃣ 安排时间：给每个任务分配时间\n4️⃣ 预留弹性：不要排太满\n5️⃣ 定期回顾：每周检查完成情况\n\n📅 示例：\n• 长期：期末考到XX分\n• 周计划：完成XX章节\n• 日计划：做XX练习\n\n💡 计划要可执行，不要太空泛！"
        ));

        // ========== 记忆类 ==========
        keywordResponses.put("记不住|记忆力|背诵|记忆方法|怎么背|背不下", Arrays.asList(
            "🧠 高效记忆方法：\n\n1️⃣ 间隔重复法：\n   今天学 → 明天复习 → 3天后 → 7天后 → 15天后\n\n2️⃣ 联想记忆法：\n   把新知识和已知知识联系起来\n\n3️⃣ 费曼学习法：\n   用自己的话讲给别人听\n\n4️⃣ 思维导图法：\n   把知识点可视化\n\n5️⃣ 多感官参与：\n   看、读、写、听一起用\n\n💡 理解比死记硬背更有效！",
            "记忆小技巧：\n\n🧠 记忆宫殿：把知识放在想象的空间里\n📝 首字连词：用每个要点的第一个字组成词\n🎵 编成口诀：把内容编成顺口溜\n🃏 制作卡片：正面问题，背面答案\n👥 互相提问：和同学互相考\n\n💡 睡前复习效果最好！"
        ));

        keywordResponses.put("遗忘|忘记|忘了|记混", Arrays.asList(
            "😅 遗忘是正常的！不用担心：\n\n1️⃣ 艾宾浩斯遗忘曲线告诉我们：\n   • 20分钟后忘42%\n   • 1天后忘66%\n   • 1周后忘77%\n\n2️⃣ 所以要及时复习！\n   • 学完后1小时内回顾\n   • 当天睡前再看一遍\n   • 第2天、第4天、第7天复习\n\n3️⃣ 多次复习 = 长期记忆\n\n💡 别怕忘记，多复习就好了！"
        ));

        // ========== 学习动力类 ==========
        keywordResponses.put("不想学|没动力|学不进去|厌学|懒|没兴趣", Arrays.asList(
            "💪 找回学习动力的方法：\n\n1️⃣ 设定小目标，完成后给自己奖励\n2️⃣ 找个学习搭子互相监督\n3️⃣ 换个学习环境（图书馆、咖啡厅）\n4️⃣ 从感兴趣的科目开始\n5️⃣ 想想学习的长远好处\n\n🎯 试试「5分钟法则」：\n告诉自己只学5分钟，往往开始后就能继续下去！\n\n记住：不想学的时候，行动是最好的解药！",
            "学习没动力？试试这些：\n\n🌟 想象一下成功的自己\n🏆 设立奖励机制\n👥 找志同道合的伙伴\n📱 远离手机干扰\n⏰ 只学10分钟试试\n\n💡 动力来自行动，不是等待！先开始，感觉就来了～"
        ));

        keywordResponses.put("压力大|焦虑|紧张|烦躁|崩溃", Arrays.asList(
            "🧘 减压放松方法：\n\n1️⃣ 运动：跑步、打球、瑜伽\n2️⃣ 听音乐：舒缓的音乐放松心情\n3️⃣ 深呼吸：4秒吸气→7秒屏息→8秒呼气\n4️⃣ 与人交流：找朋友聊聊天\n5️⃣ 适当休息：学习1小时休息10分钟\n6️⃣ 写日记：把烦恼写下来\n\n💡 适度压力是动力，过度压力要释放！\n记住：身体是革命的本钱！",
            "感到焦虑很正常，试试这些：\n\n🫁 深呼吸：慢慢吸气，慢慢呼气\n🚶 散步：出去走走，换个环境\n🎵 听歌：听喜欢的音乐\n📝 写下来：把担心的事写出来\n💬 说出来：和朋友家人聊聊\n😴 早点睡：睡眠很重要\n\n💡 你已经很棒了，给自己一点时间～"
        ));

        keywordResponses.put("无聊|没意思|好无聊", Arrays.asList(
            "无聊的时候可以：\n\n📚 看一本好书\n🎧 听听播客或有声书\n📝 写写日记或随笔\n🚶 出去散散步\n🎬 看一部纪录片\n💡 学一个新技能\n👥 约朋友聊聊\n\n或者问我一些学习问题，我来帮你！😊"
        ));

        // ========== 笔记方法 ==========
        keywordResponses.put("怎么做笔记|笔记方法|记笔记|笔记技巧|做笔记", Arrays.asList(
            "📝 高效笔记方法：\n\n1️⃣ 康奈尔笔记法：\n┌─────────┬─────────┐\n│ 关键词   │ 课堂笔记 │\n│ 问题     │         │\n├─────────┼─────────┤\n│    总结（用自己的话）    │\n└─────────────────────────┘\n\n2️⃣ 思维导图法：\n• 中心主题 → 分支 → 细节\n• 用颜色区分不同层级\n\n3️⃣ 要点笔记法：\n• 只记关键词和重点\n• 用符号标记（★重要、？疑问）\n\n💡 笔记不是抄书，而是整理知识！",
            "记笔记的技巧：\n\n✅ 用自己的话记录\n✅ 重点内容用不同颜色标注\n✅ 课后及时整理补充\n✅ 定期回顾笔记\n✅ 留白，方便后续添加\n\n⚠️ 避免：\n❌ 逐字抄写\n❌ 只记不看\n❌ 字迹潦草"
        ));

        // ========== 考试技巧 ==========
        keywordResponses.put("考试|复习|期末|备考|考试技巧|答题技巧|怎么答题", Arrays.asList(
            "📝 考试复习建议：\n\n1️⃣ 提前2-3周开始复习\n2️⃣ 整理课堂笔记和重点\n3️⃣ 做往年真题熟悉题型\n4️⃣ 组建学习小组互相督促\n5️⃣ 保持良好作息，不要熬夜\n\n📋 复习策略：\n• 先过一遍课本，标记重点\n• 做笔记总结每章要点\n• 大量练习，查漏补缺\n• 考前模拟，找感觉\n\n加油，你一定可以的！💪",
            "📝 考试答题技巧：\n\n1️⃣ 先易后难：先做会的，再做不会的\n2️⃣ 合理分配时间：不要在一题上花太多时间\n3️⃣ 审题要仔细：看清题目要求再作答\n4️⃣ 选择题技巧：排除法、对比法\n5️⃣ 简答题：分点作答，条理清晰\n6️⃣ 检查：留5-10分钟检查\n\n⚠️ 注意：\n• 不要空着不写，写了就有可能得分\n• 字迹工整，卷面整洁",
            "期末复习小贴士：\n\n📅 制定复习计划\n📚 整理知识框架\n📝 多做练习题\n👥 和同学讨论\n😴 保证睡眠\n\n💡 复习重点：\n• 老师强调的内容\n• 往年真题考点\n• 自己薄弱的部分\n\n记得来找我问具体学科的复习方法！"
        ));

        // ========== 时间管理 ==========
        keywordResponses.put("时间管理|时间规划|怎么安排时间|合理安排|没时间", Arrays.asList(
            "⏰ 时间管理技巧：\n\n1️⃣ 制定每日计划表\n2️⃣ 使用番茄工作法（25分钟专注+5分钟休息）\n3️⃣ 分清轻重缓急：\n   • 重要紧急 > 重要不紧急\n   • 紧急不重要 > 不重要不紧急\n4️⃣ 利用碎片时间（通勤、排队时背单词）\n5️⃣ 设置固定学习时间段\n\n📋 推荐工具：\n• 番茄Todo\n• Forest专注森林\n• 滴答清单\n\n💡 坚持21天就能养成好习惯！",
            "时间总是不够用？试试这些：\n\n⏰ 早起1小时，效率翻倍\n📝 前一天晚上规划第二天\n🎯 每天只定3个重要任务\n📵 学习时手机静音\n⏰ 给每个任务设定截止时间\n✅ 完成后打勾，有成就感\n\n💡 时间就像海绵里的水，挤挤总会有的！"
        ));

        // ========== 学科建议 ==========
        keywordResponses.put("高数|高等数学|微积分|线性代数|数学", Arrays.asList(
            "📐 高等数学学习建议：\n\n1️⃣ 基础要打牢：极限、导数、积分是核心\n2️⃣ 多做题：光看不练是学不会的\n3️⃣ 总结题型：每类题目的解题套路\n4️⃣ 错题本：把错题整理起来反复看\n\n📚 推荐资源：\n• 宋浩老师视频（B站）\n• 同济版教材\n• 学长学姐的笔记\n\n💡 高数不难，关键是多练习！遇到不会的题多问老师和同学！"
        ));

        keywordResponses.put("英语|英语学习|学英语|背单词|四六级|四级|六级", Arrays.asList(
            "🌍 英语学习方法：\n\n📖 词汇：\n• 每天背30-50个单词\n• 用艾宾浩斯遗忘曲线复习\n• 在语境中记单词\n\n🎧 听力：\n• 每天听30分钟英语\n• 精听+泛听结合\n• 跟读模仿\n\n📝 阅读：\n• 先看题目再读文章\n• 积累生词和句型\n\n✍️ 写作：\n• 背诵优秀范文\n• 积累常用句型\n\n💡 英语是积累的过程，坚持最重要！"
        ));

        keywordResponses.put("编程|写代码|学编程|程序|代码|python|java|c语言", Arrays.asList(
            "💻 编程学习建议：\n\n1️⃣ 选一门语言入门：\n   • Python：最简单，适合初学者\n   • Java：应用广泛，就业好\n   • C语言：打好基础\n\n2️⃣ 学习方法：\n   • 边学边做项目\n   • 多看别人代码\n   • 遇到bug别怕，调试是必经之路\n\n📚 学习资源：\n• 菜鸟教程\n• B站视频教程\n• LeetCode刷题\n• GitHub开源项目\n\n💡 编程最重要的是动手写，光看是学不会的！"
        ));

        keywordResponses.put("物理|大学物理|力学|电磁学", Arrays.asList(
            "🔭 大学物理学习建议：\n\n1️⃣ 理解概念：物理重在理解，不是死记公式\n2️⃣ 多画图：受力分析图、电路图等\n3️⃣ 多做题：尤其是计算题\n4️⃣ 联系实际：物理来源于生活\n\n📚 学习技巧：\n• 先理解再记忆公式\n• 做题时画好示意图\n• 注意单位和量纲\n• 整理常见题型\n\n💡 物理虽然难，但理解了就很有趣！"
        ));

        keywordResponses.put("化学|有机化学|无机化学", Arrays.asList(
            "🧪 化学学习建议：\n\n1️⃣ 元素周期表要熟悉\n2️⃣ 化学方程式要会配平\n3️⃣ 有机化学：理解反应机理\n4️⃣ 多做实验题\n\n📚 学习技巧：\n• 制作化学方程式卡片\n• 整理常见反应类型\n• 画反应流程图\n• 联系生活中的化学现象\n\n💡 化学需要记忆+理解，两者缺一不可！"
        ));

        // ========== 实用功能 ==========
        keywordResponses.put("图书馆|自习室|哪里学习|学习地点", Arrays.asList(
            "🏫 推荐学习地点：\n\n1️⃣ 图书馆：\n• 安静、氛围好\n• 有学习资源\n• 需要早去占座\n\n2️⃣ 自习室：\n• 专门的学习空间\n• 可能需要预约\n\n3️⃣ 教室：\n• 没课的教室\n• 比较安静\n\n4️⃣ 宿舍：\n• 方便但容易分心\n• 建议戴耳机\n\n💡 找个适合自己的环境，学习效率会提高很多！"
        ));

        keywordResponses.put("挂科|补考|重修|不及格", Arrays.asList(
            "😰 挂科了怎么办？\n\n1️⃣ 不要慌：补考/重修都能过\n2️⃣ 分析原因：是没学懂还是没复习\n3️⃣ 制定计划：认真准备补考\n4️⃣ 寻求帮助：问老师、同学\n5️⃣ 利用资源：资料市场找相关资料\n\n📚 补考准备：\n• 找往年试卷练习\n• 重点复习老师划的内容\n• 组建学习小组讨论\n\n💪 一次挂科不代表什么，重要的是吸取教训！"
        ));

        keywordResponses.put("论文|写论文|作业|写作业|报告|实验报告", Arrays.asList(
            "📄 论文/作业写作建议：\n\n1️⃣ 先列大纲：确定结构再写\n2️⃣ 查阅资料：多看参考文献\n3️⃣ 独立思考：有自己的观点\n4️⃣ 注意格式：按照要求排版\n5️⃣ 检查修改：写完一定要检查\n\n⚠️ 注意事项：\n• 不要抄袭，用自己的话表达\n• 引用要标注来源\n• 提前完成，留时间修改\n\n💡 好论文是改出来的！"
        ));

        keywordResponses.put("实习|就业|找工作|简历|面试", Arrays.asList(
            "💼 实习/就业准备：\n\n📄 简历：\n• 突出相关经历和技能\n• 简洁明了，一页为佳\n• 针对不同岗位调整\n\n🎤 面试：\n• 提前了解公司背景\n• 准备自我介绍（1-2分钟）\n• 准备常见问题回答\n• 着装得体\n\n📚 提升竞争力：\n• 考取相关证书\n• 参加实习积累经验\n• 学习实用技能"
        ));

        keywordResponses.put("考证|证书|考什么证|值得考的证|有用的证", Arrays.asList(
            "📜 大学值得考的证书：\n\n必考：\n• 英语四六级（CET-4/6）\n• 计算机二级\n\n推荐考：\n• 驾驶证\n• 普通话证书\n• 教师资格证\n\n专业相关：\n• 金融：证券从业、基金从业\n• 法律：法律职业资格\n• 会计：初级会计、CPA\n• 计算机：软考、华为认证\n\n💡 根据自己的专业和职业规划选择！"
        ));

        keywordResponses.put("怎么找搭子|找学习搭子|一起学习|学习伙伴|找人一起", Arrays.asList(
            "👥 找学习搭子的方法：\n\n1️⃣ 在「找学习搭子」功能发布信息\n2️⃣ 选择类型：自习、考研、四六级、编程等\n3️⃣ 填写你的目标和时间\n4️⃣ 等待其他同学加入\n\n🎯 搭子的好处：\n• 互相监督，不容易偷懒\n• 交流讨论，加深理解\n• 互相鼓励，保持动力\n• 分享资料，共同进步\n\n💡 有了学习搭子，学习路上不再孤单！"
        ));

        keywordResponses.put("课程评价|选课|这门课怎么样|老师怎么样|给分", Arrays.asList(
            "⭐ 关于课程评价：\n\n你可以在「学长学姐说」查看课程评价：\n\n📊 可以看到：\n• 整体评分（1-5星）\n• 课程难度\n• 考试难度\n• 给分情况\n• 学长学姐的建议\n\n💡 选课前先来看评价，避免踩坑！\n\n如果你上过某门课，也欢迎分享你的评价，帮助学弟学妹～"
        ));

        // ========== 考研相关 ==========
        keywordResponses.put("考研|研究生|考研复习|考研准备", Arrays.asList(
            "🎓 考研准备指南：\n\n1️⃣ 确定目标院校和专业\n2️⃣ 制定详细的复习计划\n3️⃣ 收集真题和复习资料\n4️⃣ 找考研搭子互相鼓励\n5️⃣ 保持心态稳定\n\n📚 各科建议：\n• 英语：每天背单词、做阅读\n• 数学：多做题、总结题型\n• 政治：后期集中复习\n• 专业课：找学长学姐的笔记\n\n💪 考研是一场持久战，坚持就是胜利！"
        ));

        // ========== 情绪支持 ==========
        keywordResponses.put("怎么办|帮帮我|救救我", Arrays.asList(
            "别担心，我来帮你！😊\n\n告诉我具体是什么问题，比如：\n📚 学习上的问题\n📝 考试复习\n⏰ 时间管理\n💪 学习动力\n🧠 记忆方法\n\n我会尽力给你建议的！"
        ));

        keywordResponses.put("加油|鼓励|支持", Arrays.asList(
            "💪 加油！你一定可以的！\n\n相信自己，每一天的努力都不会白费！\n\n📚 好好学习\n🎯 朝着目标前进\n✨ 成为更好的自己\n\n我在这里支持你！😊"
        ));

        keywordResponses.put("开心|高兴|快乐|哈哈", Arrays.asList(
            "😊 看到你开心我也很开心！\n\n保持好心情，学习效率会更高哦～\n\n有什么想聊的随时找我！"
        ));

        keywordResponses.put("难过|伤心|不开心|郁闷", Arrays.asList(
            "😢 不开心的时候可以：\n\n🎵 听听喜欢的音乐\n🚶 出去走走散散心\n💬 和朋友聊聊天\n📝 写写日记\n🎯 想想开心的事\n\n记住：困难是暂时的，一切都会好起来的！💪\n\n有什么想说的可以告诉我，我在这里听你～"
        ));

        // ========== 帮助 ==========
        keywordResponses.put("帮助|help|功能|能做什么|你会什么", Arrays.asList(
            "🤖 我可以帮你：\n\n📚 学习方法咨询\n📝 考试技巧指导\n⏰ 时间管理建议\n🧠 记忆方法分享\n💪 学习动力激励\n📐 各科学习建议\n📝 笔记方法指导\n🎓 考研备考建议\n📜 考证规划\n👥 找学习搭子\n😰 挂科补考指导\n📄 论文写作建议\n💼 实习就业指导\n\n直接问我问题就好！"
        ));

        keywordResponses.put("推荐|建议|意见", Arrays.asList(
            "💡 我可以给你各种学习建议：\n\n• 学习方法推荐\n• 时间管理建议\n• 考试复习策略\n• 各科学习技巧\n• 笔记方法\n• 记忆技巧\n\n告诉我你想要哪方面的建议，我来帮你！"
        ));
    }

    /**
     * 生成AI回复
     */
    public ChatMessage generateResponse(String userMessage) {
        String response = findResponse(userMessage);
        return new ChatMessage(response, "assistant");
    }

    /**
     * 根据用户消息查找匹配的回复
     */
    private String findResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase().trim();

        // 遍历所有关键词
        for (Map.Entry<String, List<String>> entry : keywordResponses.entrySet()) {
            String[] keywords = entry.getKey().split("\\|");
            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword.toLowerCase())) {
                    List<String> responses = entry.getValue();
                    return responses.get(new Random().nextInt(responses.size()));
                }
            }
        }

        // 如果没有匹配的关键词，返回默认回复
        return defaultResponses.get(new Random().nextInt(defaultResponses.size()));
    }

    /**
     * 获取欢迎消息
     */
    public ChatMessage getWelcomeMessage() {
        String welcome = "你好！👋 我是Happy Campus的AI学习助手！\n\n我可以帮你：\n\n📚 学习方法指导\n📝 考试技巧分享\n⏰ 时间管理建议\n🧠 记忆方法\n💪 学习动力激励\n🎯 各科学习建议\n\n点击下方热门问题或直接输入你的问题！";
        return new ChatMessage(welcome, "assistant");
    }

    /**
     * 生成搭子描述内容
     */
    public String generateBuddyContent(String type, String title, String target, String location, String timeInfo, int maxMembers) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎯 ").append(title != null ? title : "学习搭子").append("\n\n");
        sb.append("📋 类型：").append(type != null ? type : "自习").append("\n");
        if (target != null && !target.isEmpty()) {
            sb.append("🎯 目标：").append(target).append("\n");
        }
        if (location != null && !location.isEmpty()) {
            sb.append("📍 地点：").append(location).append("\n");
        }
        if (timeInfo != null && !timeInfo.isEmpty()) {
            sb.append("⏰ 时间：").append(timeInfo).append("\n");
        }
        sb.append("👥 人数：").append(maxMembers).append("人\n\n");
        sb.append("期待和大家一起学习进步！💪");
        return sb.toString();
    }

    /**
     * 生成资料描述
     */
    public String generateMaterialDescription(String title, String category, String subject, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("📖 ").append(title != null ? title : "学习资料").append("\n\n");
        sb.append("📚 分类：").append(category != null ? category : "其他").append("\n");
        if (subject != null && !subject.isEmpty()) {
            sb.append("📝 学科：").append(subject).append("\n");
        }
        sb.append("\n");
        if (content != null && !content.isEmpty()) {
            sb.append(content);
        } else {
            sb.append("优质学习资料，欢迎选购！");
        }
        return sb.toString();
    }

    /**
     * 生成课程评价
     */
    public String generateReviewContent(String courseName, String teacherName, int rating, int difficulty, String tips) {
        StringBuilder sb = new StringBuilder();
        sb.append("📚 ").append(courseName != null ? courseName : "课程评价").append("\n\n");
        if (teacherName != null && !teacherName.isEmpty()) {
            sb.append("👨‍🏫 授课教师：").append(teacherName).append("\n");
        }
        sb.append("⭐ 整体评分：").append(rating).append("/5\n");
        sb.append("📊 课程难度：").append(difficulty).append("/5\n\n");
        if (tips != null && !tips.isEmpty()) {
            sb.append("💡 学习建议：").append(tips);
        } else {
            sb.append("认真听课，按时完成作业，祝大家取得好成绩！");
        }
        return sb.toString();
    }
}
