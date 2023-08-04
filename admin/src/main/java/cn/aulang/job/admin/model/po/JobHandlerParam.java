package cn.aulang.job.admin.model.po;

import cn.aulang.job.admin.utils.NumberUtils;
import cn.aulang.job.core.enums.DataType;
import cn.aulang.job.core.model.HandlerParamField;
import cn.aulang.common.crud.id.LongIdEntity;
import cn.aulang.common.crud.id.LongIdGenId;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.annotation.KeySql;

import java.util.Date;

/**
 * 处理器参数字段
 *
 * @author wulang
 */
@Data
@Table(name = "job_handler_param")
@EqualsAndHashCode(callSuper = true)
public class JobHandlerParam extends LongIdEntity {

    @Id
    @KeySql(genId = LongIdGenId.class)
    private Long id;

    /**
     * 应用名称
     */
    @NotNull
    private String appName;
    /**
     * 处理器名称（方法名）
     */
    @NotNull
    private String handlerName;
    /**
     * 名称
     */
    @NotNull
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    @NotNull
    private String type;

    /**
     * 是否必须
     */
    private Boolean required;
    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 日期格式化
     */
    private String pattern;
    /**
     * 是否数组
     */
    private Boolean isArray;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间，用作版本号
     */
    private Long updateTime;


    @Transient
    private Object value;

    public Object getValue() {
        if (value != null) {
            return value;
        }

        if (isArray != null && !isArray && StringUtils.isNotBlank(defaultValue)) {
            value = covertValue(type, defaultValue);
        }

        return value;
    }

    public Object covertValue(String type, String defaultValue) {
        if (DataType.INTEGER.getCode().equals(type)) {
            return NumberUtils.parseLong(defaultValue, null);
        } else if (DataType.FLOAT.getCode().equals(type)) {
            return NumberUtils.parseDouble(defaultValue, null);
        } else if (DataType.BOOLEAN.getCode().equals(type)) {
            return "true".equalsIgnoreCase(defaultValue);
        } else {
            return defaultValue;
        }
    }

    public JobHandlerParam() {
    }

    public JobHandlerParam(String appName, String handlerName, HandlerParamField field, Date updateTime) {
        this.appName = appName;
        this.handlerName = handlerName;
        this.name = field.getName();
        this.title = field.getTitle();
        this.type = field.getType();
        this.required = field.getRequired();
        this.defaultValue = field.getDefaultValue();
        this.pattern = field.getPattern();
        this.isArray = field.getIsArray();
        this.remark = field.getRemark();
        this.updateTime = updateTime.getTime();
    }
}
