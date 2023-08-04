package cn.aulang.job.admin.enums;

/**
 * 数据类型
 *
 * @author wulang
 */
public enum DatabaseTypeEnum {

    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "mysqlreader", "mysqlwriter"),
    ORACLE("Oracle", "oracle.jdbc.OracleDriver", "oraclereader", "oraclewriter"),
    MARIADB("MariaDB", "org.mariadb.jdbc.Driver", "mysqlreader", "mysqlwriter"),
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "postgresqlreader", "postgresqlwriter"),
    SQLSERVER("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sqlserverreader", "sqlserverwriter"),
    MONGODB("MongoDB", null, "mongodbreader", "mongodbwirter");

    private final String name;
    private final String driver;
    private final String readerName;
    private final String writerName;

    DatabaseTypeEnum(String name, String driver, String readerName, String writerName) {
        this.name = name;
        this.driver = driver;
        this.readerName = readerName;
        this.writerName = writerName;
    }

    public String getName() {
        return name;
    }

    public String getDriver() {
        return driver;
    }

    public String getReaderName() {
        return readerName;
    }

    public String getWriterName() {
        return writerName;
    }

    public static DatabaseTypeEnum match(String name) {
        for (DatabaseTypeEnum item : DatabaseTypeEnum.values()) {
            if (item.name.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}
