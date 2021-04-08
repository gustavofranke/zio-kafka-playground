`sbt "runMain pack.MyApp"`

`runMain pack.MyApp`

`sbt assembly`

consumer group protocol
* allows for automatic resource management and load balancing
* group management is isolated in the group coordinator and this allows each consumer to focus only on the application-level work of consuming messages
* The group coordinator is ultimately responsible for tracking two things:
    *  the partitions of subscribed topics
    * members in the group.
*  Any changes to these require the group to react in order to ensure that all topic partitions are being consumed from and that all members are actively consuming.
*  So when it detects such changes, the group coordinator picks up its one and only tool: the consumer group rebalance.
* Rebalance
    * Broker side: membership
        * JoinGroup
        * HeartBeat
        * LeaveGroup
    * Client side: assignment
        * SyncGroup
        * Leader
CooperativeStickyAssignor
* Cooperative: DOESN’T revoke partitions from their assignment.
* Sticky: The assignment produced by the sticky assignor DOESN’T change every time the group membership or topic metadata changes, it returns partitions to their previous owners.