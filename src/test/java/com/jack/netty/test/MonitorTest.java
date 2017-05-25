/**
 * @Probject Name: netty-wfj-base
 * @Path: com.wfj.nettyMonitorTest.java
 * @Create By Jack
 * @Create In 2015年8月26日 下午5:38:10
 * TODO
 */
package com.jack.netty.test;

import junit.framework.TestCase;

import javax.management.MBeanServerConnection;
import java.io.*;
import java.lang.management.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Class Name MonitorTest
 * @Author Jack
 * @Create In 2015年8月26日
 */
public class MonitorTest extends TestCase {

    /**
     * 一下计算 CUP 占用率使用
     */
    private static final int CPUTIME = 30;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;

    /**
     * @param name
     */
    public MonitorTest(String name) {
        super(name);
    }

    public void testGetInfo() throws Exception {

        System.out.println("=======================通过java来获取相关系统状态============================ ");
        int i = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;// Java
        // 虚拟机中的内存总量,以字节为单位
        System.out.println("总的内存量 i is " + i + " MB");
        int j = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;// Java
        // 虚拟机中的空闲内存量
        System.out.println("空闲内存量 j is " + j + " MB");
        System.out.println("最大内存量 is " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");

        DecimalFormat df = new DecimalFormat("0.00");

        //显示JVM总内存
        long totalMem = Runtime.getRuntime().totalMemory();
        System.out.println(df.format(totalMem / 1024) + " KB");
        //显示JVM尝试使用的最大内存
        long maxMem = Runtime.getRuntime().maxMemory();
        System.out.println(df.format(maxMem / 1024) + " KB");
        //空闲内存
        long freeMem = Runtime.getRuntime().freeMemory();
        System.out.println(df.format(freeMem / 1024) + " KB");

        System.out.println("=======================OperatingSystemMXBean============================ ");
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
        com.sun.management.OperatingSystemMXBean osm;
        osm = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
                com.sun.management.OperatingSystemMXBean.class);

        String osName = osm.getName();

        System.out.println("osm.getName() " + osName);
        // 获取操作系统相关信息
        System.out.println("osm.getArch() " + osm.getArch());
        System.out.println("osm.getAvailableProcessors() " + osm.getAvailableProcessors());
        System.out.println("osm.getCommittedVirtualMemorySize() " + osm.getCommittedVirtualMemorySize());
        System.out.println("osm.getName() " + osm.getName());
        System.out.println("osm.getProcessCpuTime() " + osm.getProcessCpuTime());

        System.out.println("osm.getProcessCpuLoad() " + osm.getProcessCpuLoad());
        System.out.println("osm.getSystemCpuLoad() " + osm.getSystemCpuLoad());
        System.out.println("osm.getSystemLoadAverage() " + osm.getSystemLoadAverage());

        System.out.println("osm.getVersion() " + osm.getVersion());

        System.out.println("osm.getFreeSwapSpaceSize() " + osm.getFreeSwapSpaceSize() / 1024 + " KB");
        System.out.println("osm.getFreePhysicalMemorySize() " + osm.getFreePhysicalMemorySize() / 1024 + " KB");
        System.out.println("osm.getTotalPhysicalMemorySize() " + osm.getTotalPhysicalMemorySize() / 1024 + " KB");

       // InetAddress netAddress = InetAddress.getLocalHost();
        //System.out.println("host ip:" + netAddress.getHostAddress());
        //System.out.println("host name:" + netAddress.getHostName());
        printIP();

        DecimalFormat ff = new DecimalFormat("######0.00");
        double nanoBefore = System.nanoTime();
        double cpuBefore = osm.getProcessCpuTime();
        // Call an expensive task, or sleep if you are monitoring a remote process
        Thread.sleep(1000);
        double cpuAfter = osm.getProcessCpuTime();
        double nanoAfter = System.nanoTime();
        double percent;
        if (nanoAfter > nanoBefore) {
            percent = (((cpuAfter - cpuBefore) * 100L) / (nanoAfter - nanoBefore));
        } else {
            percent = 0;
        }
        System.out.println("Cpu usage: " + ff.format(percent) + "%");

        if ("windows".indexOf(osName) != -1) {
            System.out.println("System Cpu usage:" + ff.format(this.getCpuRatioForWindows()) + "%");
        } else {
            System.out.println("System Cpu usage:" + ff.format(this.getCpuRateForLinux(osm.getVersion())) + "%");
        }

        // 获取整个虚拟机内存使用情况
        System.out.println("=======================MemoryMXBean============================ ");
        MemoryMXBean mm = (MemoryMXBean) ManagementFactory.getMemoryMXBean();
        System.out.println("getHeapMemoryUsage " + mm.getHeapMemoryUsage());
        System.out.println("getNonHeapMemoryUsage " + mm.getNonHeapMemoryUsage());
        // 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
        System.out.println("=======================ThreadMXBean============================ ");
        ThreadMXBean tm = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        System.out.println("getThreadCount " + tm.getThreadCount());
        System.out.println("getPeakThreadCount " + tm.getPeakThreadCount());
        System.out.println("getCurrentThreadCpuTime " + tm.getCurrentThreadCpuTime());
        System.out.println("getDaemonThreadCount " + tm.getDaemonThreadCount());
        System.out.println("getCurrentThreadUserTime " + tm.getCurrentThreadUserTime());

        // 当前编译器情况
        System.out.println("=======================CompilationMXBean============================ ");
        CompilationMXBean gm = (CompilationMXBean) ManagementFactory.getCompilationMXBean();
        System.out.println("getName " + gm.getName());
        System.out.println("getTotalCompilationTime " + gm.getTotalCompilationTime());

        // 获取多个内存池的使用情况
        System.out.println("=======================MemoryPoolMXBean============================ ");
        List<MemoryPoolMXBean> mpmList = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean mpm : mpmList) {
            System.out.println("getUsage " + mpm.getUsage());

            System.out.println("getMemoryManagerNames " + mpm.getObjectName().getKeyProperty("name"));
        }
        // 获取GC的次数以及花费时间之类的信息
        System.out.println("=======================GarbageCollectorMXBeans============================ ");
        List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcm : gcmList) {
            System.out.println("getName " + gcm.getName());
            System.out.println("getCollectionCount " + gcm.getCollectionCount());
            System.out.println("getCollectionTime " + gcm.getCollectionTime());
        }
        // 获取运行时信息
        System.out.println("=======================RuntimeMXBean============================ ");
        RuntimeMXBean rmb = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        System.out.println("getClassPath " + rmb.getClassPath());
        System.out.println("getLibraryPath " + rmb.getLibraryPath());
        System.out.println("getVmName " + rmb.getVmName());
        System.out.println("getVmVendor " + rmb.getVmVendor());
        System.out.println("getVmVersion " + rmb.getVmVersion());

        assertTrue(true);
    }

    private void printIP() {

        List<String> res = new ArrayList<String>();
        try {
            NetworkInterface netInterfaces = NetworkInterface.getByName("en0");

            InetAddress ip = null;
            Enumeration nii = netInterfaces.getInetAddresses();
            while (nii.hasMoreElements()) {
                ip = (InetAddress) nii.nextElement();
                if (ip.getHostAddress().indexOf(":") == -1) {
                    res.add(ip.getHostAddress());
                    System.out.println("本机的ip=" + ip.getHostAddress());
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取 Linux 系统CPU速率
     *
     * @return double
     * @Methods Name getCpuRateForLinux
     * @Create In 2015年10月27日 By Jack
     */
    private static double getCpuRateForLinux(String linuxVersion) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        Double usage = new Double(0);
        try {
            Process process = Runtime.getRuntime().exec("top -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);

            String line = brStat.readLine();
            if (line != null) {
                while (line.toLowerCase().indexOf("cpu") < 0) {
                    line = brStat.readLine();
                }
                line = line.substring(line.indexOf(":") + 1);
                String useAvg[] = line.split(",");
                for (String item : useAvg) {
                    if (item.toLowerCase().indexOf("id") >= 0) {
                        usage = new Double(item.substring(0, item.indexOf("%")));
                        usage = 100 - usage;
                        break;
                    }
                }
            }

            return usage;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return 1;
        } finally {
            freeResource(is, isr, brStat);
        }
    }

    private static void freeResource(InputStream is, InputStreamReader isr,
            BufferedReader br) {
        try {
            if (is != null) {
                is.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * 获得CPU使用率.
     *
     * @return 返回cpu使用率
     * @author GuoHuang
     */
    private double getCpuRatioForWindows() {
        try {
            String procCmd = System
                    .getenv("windir") + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";

            // 取进程信息
            double[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            double[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                double idletime = c1[0] - c0[0];
                double busytime = c1[1] - c0[1];
                return Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 读取CPU信息.
     *
     * @param proc
     * @return
     * @author GuoHuang
     */
    private double[] readCpu(final Process proc) {
        double[] retn = new double[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            double idletime = 0;
            double kneltime = 0;
            double usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = line.substring(capidx, cmdidx - 1).trim();
                String cmd = line.substring(cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                String s1 = line.substring(kmtidx, rocidx - 1).trim();
                String s2 = line.substring(umtidx, wocidx - 1).trim();
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    if (s1.length() > 0) {
                        idletime += Long.valueOf(s1).longValue();
                    }
                    if (s2.length() > 0) {
                        idletime += Long.valueOf(s2).longValue();
                    }
                    continue;
                }
                if (s1.length() > 0) {
                    kneltime += Long.valueOf(s1).longValue();
                }
                if (s2.length() > 0) {
                    usertime += Long.valueOf(s2).longValue();
                }
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
