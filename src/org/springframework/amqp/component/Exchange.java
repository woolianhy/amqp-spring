package org.springframework.amqp.component;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.amqp.AMQException;
import org.springframework.amqp.message.Message;

import java.util.Collections;
import java.io.IOException;

public class Exchange extends AbstractNamedComponent {

    public enum Type {

        FANOUT,
        DIRECT,
        TOPIC,
        HEADER;

        public String toString() {
            return super.toString().toLowerCase().intern();
        }

    }

    public enum Property {

        DURABLE,
        AUTO_DELETE

    }

    private static final Log log = LogFactory.getLog(Exchange.class);

    private Property property;
    private Type type;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void declare(boolean passive) {

        if (log.isInfoEnabled())
            log.info(String.format("Declaring exchange %s", this));

        try {
            getChannel().exchangeDeclare(getName(),
                    type.toString(),
                    passive,
                    property == Property.DURABLE,
                    property == Property.AUTO_DELETE,
                    Collections.EMPTY_MAP);
        } catch (IOException e) {
            throw new AMQException(String.format("Could not declared exchange '%s'", this), e);
        }

    }

    public void send(Message message) {

        try {
            getChannel().basicPublish(getName(), message.getRoutingKey(), message.getProperties(), message.getPayload());
        } catch (IOException e) {
            throw new AMQException(String.format("Could not send message '%s'", message), e);
        }

    }

    @Override
    public String toString() {
        return "Exchange{" +
                "name=" + getName() +
                ",property=" + property +
                ", type=" + type +
                '}';
    }

}