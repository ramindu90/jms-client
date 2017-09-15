1. Libs in the Prerequisite sections should be added.
https://docs.wso2.com/display/EI611/Sample+901:+Inbound+Endpoint+JMS+Protocol+Sample#Sample901:InboundEndpointJMSProtocolSample-Prerequisites

2. Following Jndi queue topic configuration instructions are should be included in conf/jndi.properties file
   queue.ClientReq = ClientReq
   queue.BEReq = BEReq
   queue.BERes = BERes

3. Uncomment JMSReceiver and Sender configurations mentioned in https://docs.wso2.com/display/EI611/Configure+with+ActiveMQ

4. Add QuadJMS.xml in this project resources to <EI_HOME>/repository/deployment/server/synapse-configs/default/proxy-services

5. Start ActiveMQ server using ./activemq console

5. Run synchrounous.Server

6. Start WSO2 EI

7. Run synchrounous.Client

8. You will see the sent message with some additional information