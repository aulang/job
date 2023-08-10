package cn.aulang.job.core.enums;

/**
 * Glue代码类型
 *
 * @author wulang
 */
public enum GlueTypeEnum {

    BEAN("Bean", "处理任务", false, null, null),
    GROOVY("Groovy", "Groovy", true, "groovy", ".groovy"),
    SHELL("Shell", "Shell", true, "bash", ".sh"),
    PYTHON("Python", "Python", true, "python", ".py"),
    PHP("PHP", "PHP", true, "php", ".php"),
    NODEJS("Nodejs", "Nodejs", true, "node", ".js"),
    POWERSHELL("PowerShell", "PowerShell", true, "powershell", ".ps1");

    /**
     * 名称
     */
    private final String name;
    /**
     * 标题
     */
    private final String title;
    /**
     * 是否脚本
     */
    private final boolean isScript;
    /**
     * 执行命令
     */
    private final String cmd;
    /**
     * 脚本文件后缀名
     */
    private final String suffix;

    GlueTypeEnum(String name, String title, boolean isScript, String cmd, String suffix) {
        this.name = name;
        this.title = title;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public boolean isScript() {
        return isScript;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSuffix() {
        return suffix;
    }

    public static GlueTypeEnum match(String name) {
        if (name == null) {
            return null;
        }

        for (GlueTypeEnum item : GlueTypeEnum.values()) {
            if (item.name.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}
