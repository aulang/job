package cn.aulang.job.admin.datax.parser;

import cn.aulang.job.admin.datax.db.Column;
import cn.aulang.job.admin.datax.db.Table;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobDataSource;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MongoDB结构解析器
 *
 * @author wulang
 */
public class MongoDBStructureParser implements DatabaseStructureParser {

    private static final String AUTH_DB_NAME = "admin";
    private static final String ID_KEY = "_id";

    private final String dbName;
    private final MongoClient mongoClient;

    public MongoDBStructureParser(JobDataSource dataSource) {
        ConnectionString connectionString = new ConnectionString(dataSource.getJdbcUrl());
        this.dbName = connectionString.getDatabase() != null ? connectionString.getDatabase() : dataSource.getDbName();

        if (StringUtils.isAnyBlank(dataSource.getUsername(), dataSource.getPassword())) {
            this.mongoClient = MongoClients.create(connectionString);
        } else {
            MongoCredential credential = MongoCredential.createCredential(
                    dataSource.getUsername(),
                    AUTH_DB_NAME,
                    dataSource.getPassword().toCharArray());

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .credential(credential)
                    .build();

            this.mongoClient = MongoClients.create(settings);
        }
    }

    @Override
    public boolean test() {
        mongoClient.getDatabase(dbName).listCollectionNames().first();
        return true;
    }

    @Override
    public List<Table> getTables() {
        return getTableNames().stream().map(e -> {
            long count = mongoClient.getDatabase(dbName).getCollection(e).countDocuments();
            return Table.of(e, null, count);
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getTableNames() {
        return mongoClient.getDatabase(dbName).listCollectionNames().into(new ArrayList<>());
    }

    @Override
    public List<Column> getColumns(String tableName) {
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);
        Document document = collection.find().first();
        List<Column> list = new ArrayList<>();
        if (document == null || document.size() == 0) {
            return list;
        }

        document.forEach((k, v) -> {
            if (v != null) {
                String type = v.getClass().getSimpleName();

                Column column = new Column();
                column.setTable(tableName);
                column.setName(k);
                column.setType(type);

                if (ID_KEY.equals(k)) {
                    column.setIsNullable(false);
                    column.setIsPrimaryKey(true);
                } else {
                    column.setIsNullable(true);
                    column.setIsPrimaryKey(false);
                }

                list.add(column);
            }
        });

        return list;
    }

    @Override
    public List<String> getColumnNames(String tableName) {
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);
        Document document = collection.find().first();
        List<String> list = new ArrayList<>();
        if (document == null || document.size() == 0) {
            return list;
        }

        document.forEach((k, v) -> {
            if (null != v) {
                String type = v.getClass().getSimpleName();
                list.add(k + ":" + type);
            }
        });

        return list;
    }

    @Override
    public List<Column> getSqlColumns(String sql) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getSqlColumnNames(String sql) {
        return new ArrayList<>();
    }

    @Override
    public Object getMaxValue(String tableName, String columnName) {
        Document document = mongoClient.getDatabase(dbName)
                .getCollection(tableName)
                .find()
                .sort(new Document(columnName, -1))
                .limit(1)
                .first();

        if (document == null) {
            return null;
        }

        Object value = document.get(columnName);
        if (value instanceof ObjectId) {
            return ((ObjectId) value).toString();
        } else {
            return value;
        }
    }

    @Override
    public Pageable<Map<String, Object>> select(String tableName, String where, String sort, int page, int size, String... columns) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 1;
        }

        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);

        long total;

        FindIterable<Document> findIterable;

        if (StringUtils.isNotBlank(where)) {
            Document filter = Document.parse(where);
            findIterable = collection.find(filter);

            total = collection.countDocuments(filter);
        } else {
            findIterable = collection.find();
            total = collection.countDocuments();
        }

        SimplePage<Map<String, Object>> pageable = new SimplePage<>(page, size);
        if (total == 0) {
            return pageable;
        }

        if (columns != null && columns.length > 0) {
            Document projection = new Document();

            for (String column : columns) {
                projection.put(column, 1);
            }

            findIterable.projection(projection);
        }

        if (StringUtils.isNotBlank(sort)) {
            findIterable.sort(Document.parse(sort));
        }

        int skip = (page - 1) * size;
        findIterable.skip(skip).limit(size);

        List<Map<String, Object>> list = new ArrayList<>();

        try (MongoCursor<Document> cursor = findIterable.iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Map<String, Object> map = new HashMap<>();
                document.forEach((k, v) -> {
                    if (v instanceof ObjectId) {
                        map.put(k, ((ObjectId) v).toHexString());
                    } else {
                        map.put(k, format(tableName, k, v));
                    }
                });

                list.add(map);
            }
        }

        return pageable.setTotal(total).setList(list);
    }

    @Override
    public long insert(String tableName, Map<String, Object> values) throws Exception {
        if (CollectionUtils.isEmpty(values)) {
            return 0;
        }

        values.remove(ID_KEY);

        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);

        Document document = new Document(values);

        collection.insertOne(document);

        return 1;
    }

    @Override
    public long update(String tableName, Map<String, Object> values) {
        if (CollectionUtils.isEmpty(values)) {
            return 0;
        }

        Object id = values.remove(ID_KEY);

        if (!(id instanceof String)) {
            throw new JobException("No valid _id found");
        }

        if (values.isEmpty()) {
            throw new JobException("No  update values found");
        }

        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);

        List<Bson> sets = values.entrySet()
                .parallelStream()
                .map((e -> Updates.set(e.getKey(), e.getValue())))
                .collect(Collectors.toList());

        Bson update = Updates.combine(sets);
        Bson filter = Filters.eq(ID_KEY, new ObjectId(id.toString()));

        return collection.updateOne(filter, update).getModifiedCount();
    }

    @Override
    public long delete(String tableName, Map<String, Object> where) {
        if (CollectionUtils.isEmpty(where)) {
            return 0;
        }

        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);

        List<Bson> filters = where.entrySet()
                .parallelStream()
                .map(e -> {
                    if (ID_KEY.equals(e.getKey())) {
                        return Filters.eq(e.getKey(), new ObjectId(e.getValue().toString()));
                    } else {
                        return Filters.eq(e.getKey(), e.getValue());
                    }
                })
                .collect(Collectors.toList());

        return collection.deleteMany(Filters.and(filters)).getDeletedCount();
    }

    @Override
    public long deleteById(String tableName, String id) {
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(tableName);

        Bson filter = Filters.eq(ID_KEY, new ObjectId(id));

        return collection.deleteMany(filter).getDeletedCount();
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
