package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性哈希路由
 */
public class ConsistentHashExecutorRouter implements ExecutorRouter {

    private static final int VIRTUAL_NODE_NUM = 128;

    private long hash(String key) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();

        byte[] keyBytes;
        keyBytes = key.getBytes(StandardCharsets.UTF_8);

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        return hashCode & 0xffffffffL;
    }

    public String hashJob(long jobId, List<String> addressList) {
        TreeMap<Long, String> addressRing = new TreeMap<>();

        for (String address : addressList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash("SHARD-" + address + "-NODE-" + i);
                addressRing.put(addressHash, address);
            }
        }

        long jobHash = hash(String.valueOf(jobId));

        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }

        return addressRing.firstEntry().getValue();
    }

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.get(0));
        }

        String address = hashJob(triggerParam.getJobId(), addressList);
        return Response.success(address);
    }
}
