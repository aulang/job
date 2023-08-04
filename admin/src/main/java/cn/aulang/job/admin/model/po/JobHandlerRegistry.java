package cn.aulang.job.admin.model.po;

import cn.aulang.job.core.model.RegisterHandler;
import cn.aulang.common.crud.id.LongIdEntity;
import cn.aulang.common.crud.id.LongIdGenId;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import java.util.Date;

/**
 * 执行器的处理器
 *
 * @author wulang
 */
@Data
@Table(name = "job_handler_registry")
@EqualsAndHashCode(callSuper = true)
public class JobHandlerRegistry extends LongIdEntity {

    @Id
    @KeySql(genId = LongIdGenId.class)
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;

    /**
     * 更新时间，用作版本号
     */
    private Long updateTime;

    public JobHandlerRegistry() {
    }

    public JobHandlerRegistry(String appName, RegisterHandler handler, Date updateTime) {
        this.appName = appName;
        this.name = handler.getName();
        this.title = handler.getTitle();
        this.updateTime = updateTime.getTime();
    }
}
