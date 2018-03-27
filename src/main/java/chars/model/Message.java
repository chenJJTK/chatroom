package chars.model;

import java.sql.Timestamp;

/**
 * @Author: chenwj
 * @Description:
 * @Date: 2018/3/21
 */
public class Message {
    private Integer type;//0：公聊；1：私聊；3：系统提示
    private  Integer messageId;
    private String fromId;
    private String fromName;
    private String toId;
    private String toName;
    private String messageText;
    private Timestamp messageDate;

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Timestamp messageDate) {
        this.messageDate = messageDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", messageId=" + messageId +
                ", fromId='" + fromId + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toId='" + toId + '\'' +
                ", toName='" + toName + '\'' +
                ", messageText='" + messageText + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }
}
