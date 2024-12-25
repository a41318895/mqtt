package com.akichou.mqttprotocol.config;

import com.akichou.mqttprotocol.properties.MqttProperties;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.UUID;

import static com.akichou.mqttprotocol.constant.MqttConstants.*;


@Configuration
@EnableScheduling
@EnableConfigurationProperties(MqttProperties.class)
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties properties ;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {

        DefaultMqttPahoClientFactory factory =
                new DefaultMqttPahoClientFactory() ;

        MqttConnectOptions connectOptions =
                new MqttConnectOptions() ;

        connectOptions.setServerURIs(new String[]{properties.getServerUri()}) ;
        connectOptions.setUserName(properties.getUsername()) ;
        connectOptions.setPassword(properties.getPassword().toCharArray()) ;

        factory.setConnectionOptions(connectOptions) ;

        return factory ;
    }

    @Bean
    public MessageChannel mqttInputChannel() {

        return new DirectChannel() ;
    }
    @Bean
    public MessageProducer inboundAdapter() {

        String randomTestClientId = properties.getClientIdPrefix() + UUID.randomUUID() ;

        // Receive from port 1883, from clientId:testClient, about testTopic 1 & 2
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        properties.getServerUri(),
                        randomTestClientId,
                        mqttClientFactory(),
                        TOPIC_FIRST, TOPIC_SECOND
                ) ;

        adapter.setCompletionTimeout(INBOUND_ADAPTER_COMPLETION_TIMEOUT) ;
        adapter.setConverter(new DefaultPahoMessageConverter()) ;
        adapter.setQos(INBOUND_ADAPTER_QOS) ;
        adapter.setOutputChannel(mqttInputChannel()) ;

        return adapter ;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttInputHandler() {

        return message -> System.out.println(message.getPayload() + " from channel-1") ;
    }

    @Bean
    public MessageChannel mqttInputChannel2() {

        return new DirectChannel() ;
    }
    @Bean
    public MessageProducer inboundAdapter2() {

        String randomTestClientId = properties.getClientIdPrefix() + UUID.randomUUID() ;

        // Receive from port 1883, from clientId:testClient, about testTopic 1 & 2
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        properties.getServerUri(),
                        randomTestClientId,
                        mqttClientFactory(),
                        TOPIC_THIRD
                ) ;

        adapter.setCompletionTimeout(INBOUND_ADAPTER_COMPLETION_TIMEOUT) ;
        adapter.setConverter(new DefaultPahoMessageConverter()) ;
        adapter.setQos(INBOUND_ADAPTER_QOS) ;
        adapter.setOutputChannel(mqttInputChannel2()) ;

        return adapter ;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel2")
    public MessageHandler mqttInputHandler2() {

        return message -> System.out.println(message.getPayload() + " from channel-2") ;
    }

    @Bean
    public MessageChannel mqttOutputChannel() {

        return new DirectChannel() ;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler mqttOutputHandler() {

        String randomTestClientId = properties.getClientIdPrefix() + UUID.randomUUID() ;

        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(randomTestClientId, mqttClientFactory()) ;

        messageHandler.setAsync(true) ;
        messageHandler.setDefaultTopic(properties.getOutputMessageHandlerDefaultTopicName()) ;
        messageHandler.setCompletionTimeout(OUTPUT_HANDLER_COMPLETION_TIMEOUT);
        messageHandler.setConverter(new DefaultPahoMessageConverter()) ;
        messageHandler.setQosExpressionString(OUTPUT_HANDLER_QOS_STRING) ;

        return messageHandler ;
    }
    @MessagingGateway(defaultRequestChannel = "mqttOutputChannel")
    public interface MyCustomGateway {

        void sendMsgToMqtt(String messageData) ;
    }

}
