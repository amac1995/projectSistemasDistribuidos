package edu.ufp.inf.sd.rmi.project.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class PubSubChannel implements Runnable {

    public String channelID;
    public String msg;
    public String op;

    public PubSubChannel(String channelID, String msg, String op) throws Exception {
        this.channelID = channelID;
        this.msg = msg;
        this.op = op;
    }

    public PubSubChannel(String channelID, String op) throws Exception {
        this.channelID = channelID;
        this.op = op;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        System.out.println("Channel [" + channelID + "].Mensagem a enviar: " + op);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(channelID, "fanout");
            String message = "{\"operation\":\"" + op + "\", \"values\":" + channelID + "}";
            channel.basicPublish(channelID, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdate() {

    }
}
