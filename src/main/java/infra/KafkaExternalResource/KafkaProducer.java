package infra.KafkaExternalResource;

import Stepping.IRunning;
import infra.Message;

public class KafkaProducer extends IRunning {
    public KafkaProducer() { super(KafkaProducer.class.getName());

    }

    @Override
    public void run() {

    }

    public void send(Message message){

    }
}
