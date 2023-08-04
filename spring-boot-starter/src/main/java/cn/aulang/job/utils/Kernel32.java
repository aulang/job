package cn.aulang.job.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Windows内核32
 *
 * @author wulang
 */
// CHECKSTYLE:OFF
public interface Kernel32 extends Library {

    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

    /**
     * 获取进程号
     *
     * @param hProcess 句柄
     * @return 进程号
     */
    long GetProcessId(Long hProcess);
}
// CHECKSTYLE:ON