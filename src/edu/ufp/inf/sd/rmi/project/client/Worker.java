package edu.ufp.inf.sd.rmi.project.client;

import com.lambdaworks.crypto.SCryptUtil;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


// worker é um thread, esta constatemente a receber mensagens do rabbit(servidor)
//worker esta constantemnte a tentar descodificar as hash para tentar encontrar a pass original
//observer usamos apenas o rabbit, rabbit para tudo
public class Worker implements Runnable {

    String taskID;
    String securePassword;
    Client client;
    Boolean cancel =false;

    public Worker(String taskID, String securePass, Client client) {
        this.taskID = taskID;
        this.securePassword = securePass;
        this.client = client;
    }

    @Override
    //cria canal e recebe mensagens por esse canal
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        try {
            connection = factory.newConnection();
            connection.addShutdownListener(new ShutdownListener() {
                public void shutdownCompleted(ShutdownSignalException cause)
                {
                    System.out.println(cause);
                    Thread.currentThread().interrupt();
                }
            });
            Channel channel = connection.createChannel();

            channel.queueDeclare(this.taskID, true, false, false, null);
            System.out.println(" [*] Waiting for messages in channel [" + this.taskID + "]. To exit press CTRL+C");

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(message);
                JSONArray jArray = json.getJSONArray("values");

                try {
                    if (cancel){
                        channel.close();
                    }
                    doWork(jArray);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(this.taskID, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void doWork(JSONArray jsonArray) {
        int i;
        for (i = 0; i < jsonArray.length(); i++) {
            if (SCryptUtil.check(jsonArray.getString(i), this.securePassword)) { //verificar se hash q trabalha é a pass original, q ele tenta descobrir
                System.out.println("Found the key: " + jsonArray.getString(i)); //se for imprime para a consola
                cancel = true;
                client.answerFromThread(jsonArray.getString(i));    //e avisa o pai de qual é a pass, passa a orginal ao pai
                //pai sabe como a pass foi enviada, a tarefa acabou e tem de avisar o servidor
            }
        }
    }
}