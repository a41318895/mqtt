package com.akichou.mqttprotocol.component;

import com.akichou.mqttprotocol.config.MqttConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MqttSender {

    private final MqttConfig.MyCustomGateway mqttMessageGateway ;

    public MqttSender(MqttConfig.MyCustomGateway MqttMessageGateway) {

        this.mqttMessageGateway = MqttMessageGateway ;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void sendMessageWhenApplicationStart() {

        mqttMessageGateway.sendMsgToMqtt("The Application Started Message") ;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    private void sendMessageScheduled() {

        mqttMessageGateway.sendMsgToMqtt("The Application Scheduled Message") ;
    }
}
