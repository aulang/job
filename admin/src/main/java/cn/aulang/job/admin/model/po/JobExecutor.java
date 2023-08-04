package cn.aulang.job.admin.model.po;

import cn.aulang.job.admin.enums.AddressTypeEnum;
import cn.aulang.job.core.common.Constants;
import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行器
 *
 * @author wulang
 */
@Data
@Table(name = "job_executor")
@EqualsAndHashCode(callSuper = true)
public class JobExecutor extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 应用名称
     */
    @NotBlank
    private String appName;
    /**
     * 标题
     */
    @NotBlank
    private String title;
    /**
     * 地址类型：0自动注册；1手动录入
     */
    @NotNull
    private Integer addressType = AddressTypeEnum.AUTO.getCode();
    /**
     * 支持的Glue类型
     */
    private String glueType;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 手动录入地址
     */
    @Transient
    private String addresses;

    /**
     * 手动选择的Glue
     */
    @Transient
    private List<String> glueTypes;

    /**
     * 获取glueTypes
     */
    public List<String> getGlueTypes() {
        if (glueTypes != null) {
            return glueTypes;
        } else if (StringUtils.isNotBlank(glueType)) {
            glueTypes = Arrays.stream(StringUtils.split(glueType, Constants.SPLIT_COMMA))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
        } else {
            glueTypes = new ArrayList<>();
        }

        return glueTypes;
    }
}
