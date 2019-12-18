package com.zq.kyb.core.dao.redis;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;

import java.sql.SQLException;
import java.util.*;

/**
 * 用于系统高并发的事务队列
 */
public class JedisUtil {

    private static JedisPool jedisPool;

    public static String redisHost = null;

    public static int redisPort = 0;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接数, 默认8个
        config.setMaxTotal(1000);

        //最大空闲连接数, 默认8个
        config.setMaxIdle(1000);

        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(10000);//10秒超时

        //最小空闲连接数, 默认0
        config.setMinIdle(5);

        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(false);

        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(30000);

        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        config.setSoftMinEvictableIdleTimeMillis(30000);

        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        config.setMinEvictableIdleTimeMillis(30000);

        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(true);

        Properties prop = new Properties();
        InputStream in = Object.class.getResourceAsStream("/redis.properties");
        redisHost = "127.0.0.1";
        redisPort = 6379;
        try {
            if (in != null) {
                prop.load(in);
                redisHost = prop.getProperty("redis_host").trim();
                if (prop.containsKey("redis_port")) {
                    String redis_port = prop.getProperty("redis_port").trim();
                    if (StringUtils.isNotEmpty(redis_port)) {
                        redisPort = Integer.valueOf(redis_port);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jedisPool = new JedisPool(config, redisHost, redisPort, 5000);
    }

    /**
     * 插入一个数据到队列
     *
     * @param type  类型
     * @param value 值
     */
    public static void pushValue(String type, String value) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(type, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by pushValue");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 队列中获取一个数据
     *
     * @param type 类型
     */
    public static String popValue(String type) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpop(type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by popValue");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取当前队列的数据
     *
     * @param type 类型
     */
    public static List<String> list(String type) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(type, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by list");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取当前队列的数据
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, String value) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by set");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取当前队列的数据
     *
     * @param key
     * @param value
     */
    public static void set(byte[] key, byte[] value) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by set byte");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取分布式锁
     *
     * @param key
     */
    public static void expire(String key, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            jedis.expire(key, seconds);//30秒还没有释放,则自动释放
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by expire");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取分布式锁
     *
     * @param key
     */
    private static Long getLock(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long r = jedis.setnx(key, "");
            if (r == 0) {

            } else {
                jedis.expire(key, 30);//30秒还没有被删除,则自动释放
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by getLock");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static void setex(String key, String value, int expire) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, expire, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by setex");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 阻塞当前线程的方式获取锁,默认25秒超时
     *
     * @param key
     * @throws InterruptedException
     */
    public static void whileGetLock(String key, Integer timeOutSeconds) throws InterruptedException {
        Long lock = JedisUtil.getLock(key);
        int times = 0;
        timeOutSeconds = timeOutSeconds != null && timeOutSeconds > 0 ? timeOutSeconds : 25;
        timeOutSeconds = timeOutSeconds * 4;//250毫秒一次检查
        while (lock == 0 && times < timeOutSeconds) {//尝试25次,每秒一次,如果获取不成功就返回错误
            lock = JedisUtil.getLock(key);
            times++;
            Thread.sleep(250);
        }
        if (lock == 0) {
            throw new UserOperateException(408, "[001]系统忙,稍后再试");//获取锁超时
        }
    }

    public static Long incr(String type) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.get(type);
            if (StringUtils.isNotEmpty(value)) {
                try {
                    jedis.set(type, Long.valueOf(value).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return jedis.incr(type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除
     *
     * @param key
     */
    public static void del(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除
     *
     * @param key
     */
    public static void del(byte[] key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by del");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static String get(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by get");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Set<String> keys(String pattern) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by keys");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static byte[] get(byte[] key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jedis error by get byte");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void main(String[] args) {

        //测试是否做到同步,避免脏数据
        // checkSync();

        //测试是否链接释放了
        // checkPoll();

        //测试数据库脏数据问题
        // JedisUtilTest.checkDBSync();
    }


    /**
     * 测试连接池是否释放了
     */
    private static void checkPoll() {
        //int i = 0;
        for (int i = 0; i < 500; i++) {
            final int index = i;
            new Thread() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(new java.util.Random().nextInt(6000));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    print(index);
                    // get
                    Logger.getLogger(this.getClass()).info(index + ": ---SellerNo: " + get("SellerNo"));
                    //
                    setex("__test", "joey", 300);
                    Logger.getLogger(this.getClass()).info(index + ": ---__test: " + get("SellerNo"));
                    print(index);
                }
            }.start();
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(40000);
                    print(-1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void print(int index) {
        Logger.getLogger(JedisUtil.class).info(index + ": Active: " + jedisPool.getNumActive());
        Logger.getLogger(JedisUtil.class).info(index + ": gIdle: " + jedisPool.getNumIdle());
        Logger.getLogger(JedisUtil.class).info(index + ": Waiters: " + jedisPool.getNumWaiters());
    }

    private static void checkSync() {
        redis.clients.jedis.Jedis jedis = jedisPool.getResource();

        //数组的操作

        // 添加数据
        jedis.lpush("lists", "vector");
        jedis.lpush("lists", "ArrayList");
        jedis.lpush("lists", "LinkedList");
        // 数组长度
        Logger.getLogger(JedisUtil.class).info(jedis.llen("lists"));
        // 排序
        //Logger.getLogger(JedisUtil.class).info(jedis.sort("lists"));
        // 字串
        Logger.getLogger(JedisUtil.class).info(jedis.lrange("lists", 0, 3));
        // 修改列表中单个值
        jedis.lset("lists", 0, "hello list!");
        // 获取列表指定下标的值
        Logger.getLogger(JedisUtil.class).info(jedis.lindex("lists", 1));
        // 删除列表指定下标的值
        Logger.getLogger(JedisUtil.class).info(jedis.lrem("lists", 1, "vector"));
        // 删除区间以外的数据
        Logger.getLogger(JedisUtil.class).info(jedis.ltrim("lists", 0, 1));
        // 列表出栈
        Logger.getLogger(JedisUtil.class).info(jedis.lpop("lists"));
        // 整个列表值
        Logger.getLogger(JedisUtil.class).info(jedis.lrange("lists", 0, -1));

        //启动多个线程来抢锁
        final int[] ii = {0};
        for (int i = 0; i < 5; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        Long j = JedisUtil.getLock("test_lock");
                        Logger.getLogger(JedisUtil.class).info("[Thread " + (ii[0]++) + "]get lock: " + j);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JedisUtil.del("test_lock");
                    }
                }
            }.start();
        }
    }
}
