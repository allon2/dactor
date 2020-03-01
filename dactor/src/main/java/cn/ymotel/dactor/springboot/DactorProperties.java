package cn.ymotel.dactor.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

//@Component
@ConfigurationProperties(prefix ="dactor")
public class DactorProperties {
    private boolean enabled = true;
    private int bufferSize=1024;
    private int threadmin=-1;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getThreadmin() {
        return threadmin;
    }

    public void setThreadmin(int threadmin) {
        this.threadmin = threadmin;
    }

    public int getThreadmax() {
        return threadmax;
    }

    public void setThreadmax(int threadmax) {
        this.threadmax = threadmax;
    }

    public int getChecktime() {
        return checktime;
    }

    public void setChecktime(int checktime) {
        this.checktime = checktime;
    }

    private int threadmax=300;
    private int checktime=1000;
    private boolean monitor=false;

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }
}
