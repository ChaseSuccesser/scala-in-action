package com.ligx.cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Author: ligongxing.
  * Date: 2018年03月29日.
  */
class EventListener extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberJoined(member) => log.info("Member is Joining: {}", member.address)
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case MemberLeft(member) => log.info("Member is Leaving: {}", member.address)
    case MemberExited(member) => log.info("Member is Exiting: {}", member.address)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member.address)
      cluster.down(member.address)
    case _: MemberEvent =>
  }
}
