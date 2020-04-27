package cn.ymotel.dactor.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * <p>名称：IdWorker.java</p>
 * <p>描述：分布式自增长ID</p>
 * <pre>
 *     Twitter的 Snowflake　JAVA实现方案
 * </pre>
 * 核心代码为其IdWorker这个类实现，其原理结构如下，我分别用一个0表示一位，用—分割开部分的作用：
 * 1||0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000
 * 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，
 * 然后5位datacenter标识位，5位机器ID（并不算标识符，实际是为线程标识），
 * 然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * 这样的好处是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和机器ID作区分），
 * 并且效率较高，经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 * <p>
 * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @author Polim
 */
public class IdWorker {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(IdWorker.class);

    // 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
    private static final long TWEPOCH = 1288834974657L;
    // 机器标识位数
    private static final long WORKERIDBITS = 5L;
    // 数据中心标识位数
    private static final long DATACENTERIDBITS = 5L;
    // 机器ID最大值
    private static final long MAXWORKERID = -1L ^ (-1L << WORKERIDBITS);
    // 数据中心ID最大值
    private static final long MAXDATACENTERID = -1L ^ (-1L << DATACENTERIDBITS);
    // 毫秒内自增位
    private static final long SEQUENCEBITS= 12L;
    // 机器ID偏左移12位
    private static final long WORKERIDSHIFT = SEQUENCEBITS;
    // 数据中心ID左移17位
    private static final long DATACENTERIDSHIFT = SEQUENCEBITS + WORKERIDBITS;
    // 时间毫秒左移22位
    private static final long TIMESTAMPLEFTSHIFT = SEQUENCEBITS + WORKERIDBITS + DATACENTERIDBITS;

    private static final long SEQUENCEMASK= -1L ^ (-1L << SEQUENCEBITS);
    /* 上次生产id时间戳 */
    private static long lastTimestamp = -1L;
    // 0，并发控制
    private long sequence = 0L;

    private final long workerId;
    // 数据标识id部分
    private final long datacenterId;

    public IdWorker() {
        this.datacenterId = getDatacenterId(MAXDATACENTERID);
        this.workerId = getMaxWorkerId(datacenterId, MAXWORKERID);
    }
   private static IdWorker idWorker=new IdWorker();
    public static IdWorker getInstance(){
        return idWorker;
    }

    /**
     * @param workerId     工作机器ID
     * @param datacenterId 序列号
     */
    public IdWorker(long workerId, long datacenterId) {
        if (workerId > MAXWORKERID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAXWORKERID));
        }
        if (datacenterId > MAXDATACENTERID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAXDATACENTERID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     *
     *
     * @return 获取下一个ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCEMASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - TWEPOCH) << TIMESTAMPLEFTSHIFT)
                | (datacenterId << DATACENTERIDSHIFT)
                | (workerId << WORKERIDSHIFT) | sequence;

    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * <p>
     * 获取 MAXWORKERID
     * </p>
     */
    protected static long getMaxWorkerId(long datacenterId, long MAXWORKERID) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (MAXWORKERID + 1);
    }

    public long getDatacenterId() {
        return this.datacenterId;
    }

    public long getWorkerId() {
        return this.workerId;
    }

    /**
     * <p>
     * 数据标识id部分
     * </p>
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();// 返回所有网络接口的一个枚举实例
            byte[] mac = null;
            while (e.hasMoreElements()) {
                NetworkInterface network = e.nextElement();// 获得当前网络接口
                if (network != null) {
                    if (network.getHardwareAddress() != null) {
                        // 获得MAC地址
                        byte[] addres = network.getHardwareAddress();
                        if (addres != null && addres.length > 1) {
                            mac = addres;
                            break;
                        }
                    }
                } else {
                    logger.info("获取MAC地址发生异常");
                }
            }
            if (mac == null) {
                id = 1L;
            } else {
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            logger.error("getDatacenterId(long)", e); //$NON-NLS-1$
            if (logger.isDebugEnabled()) {
                logger.debug("getDatacenterId(long) -  getDatacenterId: " + e.getMessage()); //$NON-NLS-1$
            }
        }
        return id;
    }

//    public static void main(String[] args) {
////        String name = ManagementFactory.getRuntimeMXBean().getName();
//
////    	System.out.println(name);
//        IdWorker idWorker = new IdWorker(31,31);
//        System.out.println("idWorker="+idWorker.nextId());
//        IdWorker id = new IdWorker();
//        System.out.println("id="+id.nextId());
//        System.out.println(id.datacenterId);
//        System.out.println(id.MAXDATACENTERID);
//    }
}