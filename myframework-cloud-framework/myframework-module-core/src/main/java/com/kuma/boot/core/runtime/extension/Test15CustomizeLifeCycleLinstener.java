/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.core.runtime.extension;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.context.SmartLifecycle;

/**
 * 在使用Spring开发时，我们都知道，所有bean都交给Spring容器来统一管理，
 * 如果我们想在容器本身的生命周期（比如容器启动、停止）事件上做一些工作怎么办呢？
 * Spring提供了以下接口：Lifecycle接口
 *
 * 对Lifecycle和SmartLifecycle有了更全面的认知，如果对执行顺序没有要求，在关闭的时候也没有性能或者时间要求，那么就用Lifecycle吧，因为更简单
 * ，如果在乎顺序，也期望关闭时多个Lifecycle实例能并行执行，快速结束，SmartLifecycle无疑更适合。
 */
public class Test15CustomizeLifeCycleLinstener implements SmartLifecycle {

    public boolean isRunningFlag() {
        return runningFlag;
    }

    public void setRunningFlag(boolean runningFlag) {
        this.runningFlag = runningFlag;
    }

    private boolean runningFlag = false;

    /**
     * 容器关闭后： spring容器发现当前对象实现了SmartLifecycle，就调用stop(Runnable)， 如果只是实现了Lifecycle，就调用stop()	 *
     * SmartLifecycle子类的才有的方法，当isRunning方法返回true时，该方法才会被调用。
     */
    @Override
    public void stop(Runnable callback) {
        LogUtils.info("stop(Runnable)");
        /**
         * 如果你让isRunning返回true，需要执行stop这个方法，那么就不要忘记调用callback.run()。
         * 否则在你程序退出时，Spring的DefaultLifecycleProcessor会认为你这个TestSmartLifecycle没有stop完成，程序会一直卡着结束不了，等待一定时间（默认超时时间30秒）后才会自动结束。
         */
        callback.run();
        setRunningFlag(false);
    }

    /**
     * bean初始化完毕后，该方法会被执行
     *
     * 1. 我们主要在该方法中启动任务或者其他异步服务，比如开启MQ接收消息<br/>
     * 2. 当上下文被刷新（所有对象已被实例化和初始化之后）时，将调用该方法，默认生命周期处理器将检查每个SmartLifecycle对象的isAutoStartup()方法返回的布尔值。
     * 如果为“true”，则该方法会被调用，而不是等待显式调用自己的start()方法。
     */
    @Override
    public void start() {
        LogUtils.info("start");

        // 执行完其他业务后，可以修改 isRunning = true
        setRunningFlag(true);
    }

    /**
     * 	容器关闭后： spring容器发现当前对象实现了SmartLifecycle，就调用stop(Runnable)， 如果只是实现了Lifecycle，就调用stop()
     *
     * 接口Lifecycle的子类的方法，只有非SmartLifecycle的子类才会执行该方法。<br/>
     * 1. 该方法只对直接实现接口Lifecycle的类才起作用，对实现SmartLifecycle接口的类无效。<br/>
     * 2. 方法stop()和方法stop(Runnable callback)的区别只在于，后者是SmartLifecycle子类的专属。
     */
    @Override
    public void stop() {
        LogUtils.info("stop");

        setRunningFlag(false);
    }
    /**
     * 控制多个SmartLifecycle的回调顺序的，返回值越小越靠前执行start()方法，越靠后执行stop()方法
     *
     * 如果工程中有多个实现接口SmartLifecycle的类，则这些类的start的执行顺序按getPhase方法返回值从小到大执行。<br/>
     * 例如：1比2先执行，-1比0先执行。 stop方法的执行顺序则相反，getPhase返回值较大类的stop方法先被调用，小的后被调用。
     */
    @Override
    public int getPhase() {
        // 默认为0
        return 0;
    }

    /**
     * 当前状态，用来判你的断组件是否在运行。
     *
     * 1. 只有该方法返回false时，start方法才会被执行。<br/>
     * 2. 只有该方法返回true时，stop(Runnable callback)或stop()方法才会被执行。
     */
    @Override
    public boolean isRunning() {
        // 默认返回false
        return isRunningFlag();
    }
    /**
     * start方法被执行前先看此方法返回值，返回false就不执行start方法了
     *
     * 根据该方法的返回值决定是否执行start方法。<br/>
     * 返回true时start方法会被自动执行，返回false则不会。
     */
    @Override
    public boolean isAutoStartup() {
        // 默认为false
        return true;
    }
}
