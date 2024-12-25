package com.akichou.mqttprotocol.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttProperties {

    private String serverUri ;

    private String username ;

    private String password ;

    private String clientIdPrefix ;

    private String outputMessageHandlerDefaultTopicName ;
}
