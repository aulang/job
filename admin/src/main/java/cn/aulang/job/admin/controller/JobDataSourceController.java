package cn.aulang.job.admin.controller;

import cn.aulang.common.web.CRUDControllerSupport;
import cn.aulang.job.admin.datax.db.Column;
import cn.aulang.job.admin.datax.db.Table;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.dto.ParseColumnDTO;
import cn.aulang.job.admin.model.dto.QueryDataDTO;
import cn.aulang.job.admin.model.po.JobDataSource;
import cn.aulang.job.admin.service.JobDataSourceService;
import cn.aulang.job.core.model.Response;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.List;
import java.util.Map;

/**
 * 数据源
 *
 * @author wulang
 */
@Slf4j
@RestController
@RequestMapping("/datasource")
public class JobDataSourceController extends CRUDControllerSupport<JobDataSource, Long> {

    private final JobDataSourceService dataSourceService;

    @Autowired
    public JobDataSourceController(JobDataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    protected JobDataSourceService service() {
        return dataSourceService;
    }

    @GetMapping("/page")
    public Pageable<JobDataSource> page(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "dbName", required = false) String dbName,
            @RequestParam(name = "groupName", required = false) String groupName,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "15") int size) {
        return dataSourceService.page(name, type, dbName, groupName, page, size);
    }

    @GetMapping("/group")
    public List<String> groupNames() {
        return dataSourceService.groupNames();
    }

    @PostMapping("/test")
    public Response<Boolean> test(@RequestBody JobDataSource dataSource) {
        boolean test = dataSourceService.test(dataSource, false);
        return Response.success(test);
    }

    @GetMapping("/{id}/test")
    public Response<Boolean> test(@PathVariable("id") Long id) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        boolean test = dataSourceService.test(dataSource, true);
        return Response.success(test);
    }

    @GetMapping("/{id}/table-name")
    public Response<List<String>> tableNames(@PathVariable("id") Long id) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            List<String> tableNames = dataSourceService.getTableNames(dataSource);
            return Response.success(tableNames);
        } catch (Exception e) {
            log.error("Fail to get tables", e);
            return Response.fail(e.getMessage());
        }
    }

    @PostMapping("/{id}/column-name")
    public Response<List<String>> columnNames(@PathVariable("id") Long id, @RequestBody ParseColumnDTO body) {
        if (StringUtils.isAllBlank(body.getTable(), body.getSql())) {
            throw new IllegalArgumentException("Table and SQL must have one");
        }

        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            List<String> columns = dataSourceService.getColumnNames(dataSource, body.getTable(), body.getSql());
            return Response.success(columns);
        } catch (Exception e) {
            log.error("Fail to get table columns", e);
            return Response.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}/tables")
    public Response<List<Table>> tables(@PathVariable("id") Long id) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            List<Table> tables = dataSourceService.getTables(dataSource);
            return Response.success(tables);
        } catch (Exception e) {
            log.error("Fail to get tables", e);
            return Response.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}/columns")
    public Response<List<Column>> columns(@PathVariable("id") Long id, @RequestParam("tableName") String tableName) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            List<Column> columns = dataSourceService.getColumns(dataSource, tableName);
            return Response.success(columns);
        } catch (Exception e) {
            log.error("Fail to get table columns", e);
            return Response.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}/data")
    public Pageable<Map<String, Object>> data(@PathVariable("id") Long id,
                                              @RequestParam("tableName") String tableName,
                                              @RequestParam(required = false) String where,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "15") int size,
                                              @RequestParam(required = false) List<String> columns) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return new SimplePage<>();
        }

        try {
            String[] columnArray = null;
            if (!CollectionUtils.isEmpty(columns)) {
                columnArray = columns.toArray(new String[0]);
            }

            return dataSourceService.getData(dataSource, tableName, where, sort, page, size, columnArray);
        } catch (Exception e) {
            log.error("Fail to get table data", e);
            throw new JobException(e.getMessage());
        }
    }

    @PostMapping("/{id}/data")
    public Pageable<Map<String, Object>> data(@PathVariable("id") Long id, @RequestBody @Valid QueryDataDTO query) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return new SimplePage<>();
        }

        try {
            String[] columns = null;
            if (!CollectionUtils.isEmpty(query.getColumns())) {
                columns = query.getColumns().toArray(new String[0]);
            }

            return dataSourceService.getData(dataSource,
                    query.getTableName(),
                    query.getWhere(),
                    query.getSort(),
                    query.getPage(),
                    query.getSize(),
                    columns);
        } catch (Exception e) {
            log.error("Fail to get table data", e);
            throw new JobException(e.getMessage());
        }
    }

    @PostMapping("/{id}/insert")
    public Response<Long> insert(@PathVariable("id") Long id,
                                 @RequestParam("tableName") String tableName,
                                 @RequestBody Map<String, Object> body) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            long result = dataSourceService.insertData(dataSource, tableName, body);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Fail to update table data", e);
            throw new JobException(e.getMessage());
        }
    }

    @PostMapping("/{id}/update")
    public Response<Long> update(@PathVariable("id") Long id,
                                 @RequestParam("tableName") String tableName,
                                 @RequestBody Map<String, Object> body) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            long result = dataSourceService.updateData(dataSource, tableName, body);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Fail to update table data", e);
            throw new JobException(e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public Response<Long> delete(@PathVariable("id") Long id,
                                 @RequestParam("tableName") String tableName,
                                 @RequestBody Map<String, Object> body) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            long result = dataSourceService.deleteData(dataSource, tableName, body);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Fail to delete table data", e);
            throw new JobException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public Response<Long> delete(@PathVariable("id") Long id,
                                 @RequestParam("tableName") String tableName,
                                 @RequestParam("id") String rowId) {
        JobDataSource dataSource = dataSourceService.get(id);
        if (dataSource == null) {
            return Response.fail("DataSource id: " + id + " not exists");
        }

        try {
            long result = dataSourceService.deleteDataById(dataSource, tableName, rowId);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Fail to delete table data", e);
            throw new JobException(e.getMessage());
        }
    }
}
