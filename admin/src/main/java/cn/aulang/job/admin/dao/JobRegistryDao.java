package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 节点注册DAO
 *
 * @author wulang
 */
@Mapper
public interface JobRegistryDao extends MybatisRepository<JobRegistry, Long> {

    /**
     * 获取应用注册节点列表
     *
     * @param type    注册类型
     * @param appName 应用名称
     * @return 注册节点列表
     */
    List<JobRegistry> findByTypeAndAppName(
            @Param("type") String type,
            @Param("appName") String appName
    );

    /**
     * 删除应用所有注册节点
     *
     * @param type    注册类型
     * @param appName 应用名称
     * @return 删除记录数
     */
    int deleteByTypeAndAppName(
            @Param("type") String type,
            @Param("appName") String appName
    );

    /**
     * 获取健康的执行器地址
     * <p>1. 自动注册节点心跳未超期</p>
     * <p>2. 手动录入节点为离线</p>
     *
     * @param type       注册类型：固定值EXECUTOR执行器
     * @param appName    应用名称
     * @param auto       类地址型：固定值0自动注册
     * @param online     健康状态：固定值1在线
     * @param updateTime 心跳超时时间
     * @param manual     类地址型：固定值1手动录入
     * @param offline    健康状态：固定值-1离线
     * @return 健康的执行器地址列表
     */
    List<String> findHealthExecutorAddress(
            @Param("type") String type,
            @Param("appName") String appName,
            @Param("auto") int auto,
            @Param("online") int online,
            @Param("updateTime") Date updateTime,
            @Param("manual") int manual,
            @Param("offline") int offline
    );

    /**
     * 获取健康的调度器地址
     *
     * @param type       注册类型：固定值ADMIN执行器
     * @param auto       类地址型：固定值0自动注册
     * @param updateTime 心跳超时时间
     * @return 健康的调度器地址列表
     */
    List<String> findHealthAdminAddress(@Param("type") String type, @Param("auto") int auto, @Param("updateTime") Date updateTime);

    /**
     * 获取自动注册的调度器地址
     *
     * @param type 注册类型：固定值ADMIN执行器
     * @param auto 类地址型：固定值0自动注册
     * @return 自动注册的调度器地址
     */
    List<String> findAutoAdminAddress(@Param("type") String type, @Param("auto") int auto);

    /**
     * 获取应用指定地址节点
     *
     * @param type    注册类型
     * @param appName 应用名称
     * @param address 应用地址
     * @return 注册节点
     */
    JobRegistry getTypeAndAppNameAndAddress(@Param("type") String type,
                                            @Param("appName") String appName,
                                            @Param("address") String address);

    /**
     * 刷新心跳时间
     *
     * @param id          ID
     * @param addressType 地址类型：固定值0自动注册
     * @param health      健康状态：固定值1健康在线
     * @param updateTime  更新时间
     * @return 更新记录数
     */
    int refreshUpdateTime(@Param("id") Long id,
                          @Param("addressType") int addressType,
                          @Param("health") int health,
                          @Param("updateTime") Date updateTime);

    /**
     * 删除注册节点
     *
     * @param type    注册类型
     * @param appName 应用名称
     * @param address 应用地址
     * @return 删除记录数
     */
    int deleteByTypeAndAppNameAndAddress(@Param("type") String type,
                                         @Param("appName") String appName,
                                         @Param("address") String address);

    /**
     * 查找需要健康检查的节点
     *
     * @param type       注册类型：固定值EXECUTOR执行器
     * @param auto       类地址型：固定值0自动注册
     * @param updateTime 心跳超时时间
     * @param manual     类地址型：固定值1手动录入
     * @return 需要健康检查的节点列表
     */
    List<JobRegistry> findNeedHeathCheckNodes(@Param("type") String type,
                                              @Param("auto") int auto,
                                              @Param("updateTime") Date updateTime,
                                              @Param("manual") int manual);
}
