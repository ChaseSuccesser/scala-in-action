package com.ligx.db

import java.lang.management.ManagementFactory
import javax.management.{JMX, MBeanServer, ObjectName}

import com.zaxxer.hikari.HikariPoolMXBean
import slick.util.AsyncExecutorMXBean

/**
  * Author: ligongxing.
  * Date: 2018年02月21日.
  */
object DbMonitor extends App{

  def monitorConnPool(): Unit = {
    val mBeanServer: MBeanServer = ManagementFactory.getPlatformMBeanServer
    val poolName: ObjectName = new ObjectName("com.zaxxer.hikari:type=Pool (lgx_db)")
    val poolProxy: HikariPoolMXBean = JMX.newMXBeanProxy(mBeanServer, poolName, classOf[HikariPoolMXBean])
    val totalConnections = poolProxy.getTotalConnections
    val activeConnections = poolProxy.getActiveConnections
    val idleConnections = poolProxy.getIdleConnections
    val threadsAwaitConn = poolProxy.getThreadsAwaitingConnection

    println(s"totalCons: $totalConnections, activeCons: $activeConnections, idleCons: $idleConnections, threadsAwaitConn: $threadsAwaitConn")
  }

  def monitorThreadPool(): Unit = {
    val mBeanServer: MBeanServer = ManagementFactory.getPlatformMBeanServer
    val poolName: ObjectName = new ObjectName("slick:type=AsyncExecutor,name=lgx_db")
    val poolProxy: AsyncExecutorMXBean = JMX.newMXBeanProxy(mBeanServer, poolName, classOf[AsyncExecutorMXBean])

    val maxThreads = poolProxy.getMaxThreads
    val activeThreads = poolProxy.getActiveThreads
    val maxQueueSize = poolProxy.getMaxQueueSize
    val queueSize = poolProxy.getQueueSize

    println(s"maxThreads: $maxThreads, activeThreads: $activeThreads, maxQueueSize: $maxQueueSize, queueSize: $queueSize")
  }
}
