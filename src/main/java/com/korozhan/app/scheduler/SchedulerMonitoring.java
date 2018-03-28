package com.korozhan.app.scheduler;

import com.korozhan.app.util.ParamsManager;
import com.sun.management.OperatingSystemMXBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Singleton;
import java.lang.management.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
@Startup
public class SchedulerMonitoring {
    private static Logger LOGGER = Logger.getLogger(SchedulerMonitoring.class.getName());

    private static final String SCHEDULER_NAME = "MONITORING_SCHEDULER";
    public static final boolean MONITORING_ON = ParamsManager.getBoolProperty("MONITORING_ON");
    private static final Long MONITORING_TIMEOUT = ParamsManager.getLongProperty("MONITORING_TIMEOUT", 0L);
    private static final Long INITIAL_TIMEOUT = ParamsManager.getLongProperty("INITIAL_TIMEOUT", 0L);
    private static final boolean PER_THREAD_DETAILS = ParamsManager.getBoolProperty("PER_THREAD_DETAILS");

    public static final String TIME_PATTERN = "%02d %02d:%02d:%02d";

    public long totalUserTime;
    public long totalCPUTime;

    public long totalCPUTimeDelta;
    public long totalUserTimeDelta;

    private long lastTime;
    private long totalProcessTime;
    private long totalProcessTimeDelta;

    private final HashMap<Long, Times> history = new HashMap<Long, Times>();
    private Map<String, String> cleanThreadNames = new HashMap<String, String>();

    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int avilableProcessors = Runtime.getRuntime().availableProcessors();

    private class Times {
        public long id;
        public long lastCpuTime;
        public long lastUserTime;
    }

    @Resource
    TimerService timerService;

    @PostConstruct
    public void createTimer() {
        timerService.getTimers().forEach(timer ->
        {
            if (timer.getInfo().equals(SCHEDULER_NAME))
                timer.cancel();
        });
        if (MONITORING_ON && MONITORING_TIMEOUT.longValue() != 0)
            timerService.createTimer(INITIAL_TIMEOUT, MONITORING_TIMEOUT, SCHEDULER_NAME);
    }

    @Timeout
    private void runTimer(Timer timer) {
        try {
            if (timer.getInfo().equals(SCHEDULER_NAME)) {
                LOGGER.info("Timer Service : " + timer.getInfo());

                OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                LOGGER.info("Total physical memory : " + ((OperatingSystemMXBean) osBean).getTotalPhysicalMemorySize());
                LOGGER.info("Free physical memory : " + ((OperatingSystemMXBean) osBean).getFreePhysicalMemorySize());
                LOGGER.info("Commited virtual memory : " + ((OperatingSystemMXBean) osBean).getCommittedVirtualMemorySize());
                LOGGER.info("Total swap space : " + ((OperatingSystemMXBean) osBean).getTotalSwapSpaceSize());
                LOGGER.info("Free swap space : " + ((OperatingSystemMXBean) osBean).getFreeSwapSpaceSize());
                LOGGER.info("Architecture : " + ((OperatingSystemMXBean) osBean).getArch());
                LOGGER.info("Processors : " + ((OperatingSystemMXBean) osBean).getAvailableProcessors());
                LOGGER.info("Process CPU time : " + convertTime(TimeUnit.NANOSECONDS.toMillis(((OperatingSystemMXBean) osBean).getProcessCpuTime())));

                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
                LOGGER.info("Maximum heap size : " + memoryBean.getHeapMemoryUsage().getMax());
                LOGGER.info("Committed heap size : " + memoryBean.getHeapMemoryUsage().getCommitted());
                LOGGER.info("Current heap size : " + memoryBean.getHeapMemoryUsage().getUsed());
                LOGGER.info("Pending objects count : " + memoryBean.getObjectPendingFinalizationCount());

                ClassLoadingMXBean clBeans = ManagementFactory.getClassLoadingMXBean();
                LOGGER.info("Current classes loaded : " + clBeans.getLoadedClassCount());
                LOGGER.info("Total classes loaded : " + clBeans.getTotalLoadedClassCount());
                LOGGER.info("Total classes unloaded : " + clBeans.getUnloadedClassCount());

                ManagementFactory.getGarbageCollectorMXBeans()
                        .forEach(gc ->LOGGER.info("Garbage collector : Name = '" + gc.getName() + "', Collections = " + gc.getCollectionCount() + ", Time = " + convertTime(gc.getCollectionTime())));

                LOGGER.info("Current Time : " + new Date());
                LOGGER.info("Next Timeout : " + timer.getNextTimeout());
                LOGGER.info("Time Remaining : " + convertTime(timer.getTimeRemaining()));

                ThreadMXBean tmxBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
                tmxBean.setThreadContentionMonitoringEnabled(true);
                tmxBean.setThreadCpuTimeEnabled(true);
                LOGGER.info("Threads count : " + tmxBean.getThreadCount());
                if (tmxBean.findDeadlockedThreads() != null)
                    LOGGER.info("Threads Deadlocked count : " + tmxBean.findDeadlockedThreads().length);
                if (tmxBean.findMonitorDeadlockedThreads() != null)
                    LOGGER.info("Threads MonitorDeadlocked count : " + tmxBean.findMonitorDeadlockedThreads().length);
                LOGGER.info("____________________________________________");

                StringBuilder builder = new StringBuilder();
                long tempTotalOtherTime = 0;
                long tempTotalUserTime = 0;

                for (long l : tmxBean.getAllThreadIds()) {
                    try {
                        final long c = tmxBean.getThreadCpuTime(l);
                        final long u = tmxBean.getThreadUserTime(l);

                        tempTotalOtherTime += c;
                        tempTotalUserTime += u;

                        if (PER_THREAD_DETAILS) {
                            if (c == -1 || u == -1) continue;

                            Times times = history.get(l);
                            if (times == null) {
                                times = new Times();
                                times.id = l;
                                history.put(l, times);
                            } else {
                                ThreadInfo ti = tmxBean.getThreadInfo(l);
                                LOGGER.info("Thread name: " + ti.getThreadName());
                                LOGGER.info("Thread cpu time: " + convertTime(TimeUnit.NANOSECONDS.toMillis(tmxBean.getThreadCpuTime(l))));
                                LOGGER.info("Thread user time: " + convertTime(TimeUnit.NANOSECONDS.toMillis(tmxBean.getThreadUserTime(l))));
                                LOGGER.info("Thread state: " + ti.getThreadState());
                                LOGGER.info("Thread trace: " + ti.getStackTrace());
                                LOGGER.info("Thread Blocked/Waiting On: ");
                                if (ti.getLockOwnerId() >= 0) {
                                    LOGGER.info(ti.getLockName() + " which is owned by " + ti.getLockOwnerName() + " (" + ti.getLockOwnerId() + ")");
                                } else {
                                    LOGGER.info("NONE");
                                }
                                LOGGER.info("");
                                long cpuDelta = c - times.lastCpuTime;
                                long userDelta = u - times.lastUserTime;

                                float cpuDeltaMS = toMilliseconds(cpuDelta);
                                float userDeltaMS = toMilliseconds(userDelta);

                                String threadName = ti.getThreadName();

                                String cleanThreadName = cleanThreadNames.get(threadName);
                                if (cleanThreadName == null) {
                                    cleanThreadName = threadName;

                                    cleanThreadName = cleanThreadName.replace("[", "(");
                                    cleanThreadName = cleanThreadName.replace("]", ")");
                                    cleanThreadName = cleanThreadName.replace(",", " ");
                                    cleanThreadNames.put(threadName, cleanThreadName);
                                }
                                builder.append('[')
                                        .append(cleanThreadName)
                                        .append('/')
                                        .append(numberFormat.format(cpuDeltaMS))
                                        .append('/')
                                        .append(numberFormat.format(userDeltaMS))
                                        .append(']');
                            }
                            times.lastCpuTime = c;
                            times.lastUserTime = u;
                        }
                    } catch (Throwable e) {
                        LOGGER.severe("TRHEAD EXCEPTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }
                long jvmCpuTime = getJVMCpuTime();
                LOGGER.info("Process CPU time: " + convertTime(TimeUnit.NANOSECONDS.toMillis(jvmCpuTime)));

                totalCPUTimeDelta = tempTotalOtherTime - this.totalCPUTime;
                totalUserTimeDelta = tempTotalUserTime - this.totalUserTime;
                totalProcessTimeDelta = jvmCpuTime - this.totalProcessTime;

                totalCPUTime = tempTotalOtherTime;
                totalUserTime = tempTotalUserTime;
                totalProcessTime = jvmCpuTime;

                long timeNow = System.currentTimeMillis();
                if (lastTime > 0) {
                    long elapsed = timeNow - lastTime;
                    float totalMS = elapsed * avilableProcessors;

                    float percentageCPU = 100f * toMilliseconds(totalCPUTimeDelta) / totalMS;
                    float percentageUser = 100f * toMilliseconds(totalUserTimeDelta) / totalMS;
                    float percentageDodgyProcessTime = 100f * toMilliseconds(totalProcessTimeDelta) / totalMS;

                    if (percentageCPU >= 0 && percentageUser >= 0) {
                        LOGGER.info(String.format("Summary cpu status : %.2f %% (%.2f %% user %.2f %% system) JVM process %.2f %%",
                                percentageCPU,
                                percentageUser,
                                percentageCPU - percentageUser,
                                percentageDodgyProcessTime));
                        if (PER_THREAD_DETAILS) {
                            LOGGER.info("Per thread cpu status [cleanThreadName / cpuDeltaMS / userDeltaMS]: " + builder.toString());
                        }
                    }
                }
                lastTime = timeNow;

                LOGGER.info("____________________________________________");
                LOGGER.info("Threads count : " + tmxBean.getThreadCount());
                LOGGER.info("____________________________________________");
            }
        } catch (NoClassDefFoundError e) {
            LOGGER.severe("OperatingSystemMXBean was not imported: " + e.getMessage());
        }
    }

    public long getJVMCpuTime() {
        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        if (!(bean instanceof OperatingSystemMXBean)) return 0L;
        return ((OperatingSystemMXBean) bean).getProcessCpuTime();
    }

    private String convertTime(long nanos) {
        return String.format(TIME_PATTERN, TimeUnit.MILLISECONDS.toDays(nanos),
                TimeUnit.MILLISECONDS.toHours(nanos) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(nanos)),
                TimeUnit.MILLISECONDS.toMinutes(nanos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nanos)),
                TimeUnit.MILLISECONDS.toSeconds(nanos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nanos)));
    }

    public float toMilliseconds(long nanos) {
        return nanos / 1000000f;
    }
}
