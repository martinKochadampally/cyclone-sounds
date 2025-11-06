package cycloneSounds.websocketChats;


/**
 * This class controls how the DM functionality works. Determines DM by sender and reciever which contains the content
 */
public class MessageSocket {
    private String sender;
    private String receiver;
    private String content;

    public MessageSocket() {
    }


    public MessageSocket(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}