package cn.aulang.job.admin.datax.uitls;

import com.mongodb.ConnectionString;
import cn.aulang.job.admin.datax.core.Column;
import cn.aulang.job.core.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoDB帮助类
 *
 * @author wulang
 */
public class MongoUtils {

    public static List<Column> parseColumn(String column) {
        return Arrays.stream(StringUtils.split(column, Constants.SPLIT_COMMA))
                .map(e -> StringUtils.split(e, Constants.SPLIT_COLON))
                .filter(e -> e.length == 2)
                .map(e -> Column.of(e[0], e[1]))
                .collect(Collectors.toList());
    }

    public static List<String> toIpAndPort(String jdbcUrl) {
        ConnectionString connectionString = new ConnectionString(jdbcUrl);
        return connectionString.getHosts();
    }

    public static String getDbName(String jdbcUrl) {
        ConnectionString connectionString = new ConnectionString(jdbcUrl);
        return connectionString.getDatabase();
    }
}
