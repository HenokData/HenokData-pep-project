package Service;

import java.util.List;
import java.util.Optional;

import DAO.MessageDAO;
import Model.Message; 

public class MessageService {

    // filed
    private final MessageDAO messageDAO;
    
    // constractor 

    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }

    /* 
     * 
     */


    public Message createMessage(int postedBy, String messageText){
        if (messageText.isBlank() || messageText.length() > 255) {
            throw new IllegalArgumentException("Invalid data message");
            
        }

        Message newMessage = new Message();

        newMessage.setPosted_by(postedBy);
        newMessage.setMessage_text(messageText); //  message_text
        newMessage.setTime_posted_epoch(System.currentTimeMillis()); // setTime_posted_epoch
        return messageDAO.save(newMessage);
        
    }
    // method to fetch all messages 
    public List<Message> getAllMessages(){
        return messageDAO.findAll();
    }

    public Optional<Message> getMessageById(int messageId){
        return messageDAO.findById(messageId);
    }

    public void deleteMessage(int messageId){
        messageDAO.delete(messageId);
    }

    public Optional<Message> updateMessageText(int messageId, String newText){
        if (newText.isBlank() || newText.length() > 255) {
            throw new IllegalArgumentException("Invalued data message");
            
        }
        return messageDAO.updateMessageText(messageId, newText);
    }

    public List<Message> getMessageByUserId(int accountId){
        return messageDAO.findByUserId(accountId);
    }
    
}
