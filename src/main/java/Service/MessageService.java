package Service;

import DAO.MessageDAO;
import Model.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) and
 * persistence layer (DAO). That means that the Service class performs tasks that aren't done through the web or
 * SQL: programming tasks like checking that the input is valid, conducting additional security checks, or saving the
 * actions undertaken by the API to a logging file.
 *
 * It's perfectly normal to have Service methods that only contain a single line that calls a DAO method. An
 * application that follows best practices will often have unnecessary code, but this makes the code more
 * readable and maintainable in the long run!
 */
public class MessageService {
    public MessageDAO messageDAO;

    /**
     * No-args constructor for messageService which creates a MessageDAO.
     * There is no need to change this constructor.
     */
    public MessageService(){
        messageDAO = new MessageDAO();
    }

    /**
     * Constructor for a MessageService when a MessageDAO is provided.
     * This is used for when a mock MessageDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of MessageService independently of MessageDAO.
     * There is no need to modify this constructor.
     * @param messageDAO
     */
    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }


    /**
     * Use the messageDAO to retrieve all messages.
     * @return all messages.
     */
    public List<Message> getAllMessages() {
        
        return messageDAO.getAllMessages();

    }
    

    /**
     * Retrieve a Message by its ID using the MessageDAO
     *
     * @param id The ID of the Message
     * @return Optional containing the found Message
     */
    public Message getMessageById(int id) {

            Message message = messageDAO.getMessageById(id);
            
            if ( message == null ) {
                return null;
            }

            return message;
    
    }


    /**
     * retrieve all messages written by a particular user
     * @return all messages written by a particular user
     */
    public List<Message> getMessagesByAccountId(int accountId) {
        
        List<Message> messages = messageDAO.getMessagesByAccountId(accountId);

        if ( messages == null ) {
            return new ArrayList<>();
        }

        return messages;

    }


    /**
     * Use the messageDAO to persist a message to the database.
     * As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. 
     * The request body will contain a JSON representation of a message, which should be persisted to the database, 
     * but will not contain a message_id.
     * @param message a message object.
     * @return message The response status should be 200, which is the default. 
     * The new message should be persisted to the database. 
     * If the creation of the message is not successful, the response status should be 400. (Client error)
     */

    public Message createMessage(Message message) {

        // Check for the message_text is not blank, is not over 255 characters
        if( message.getMessage_text().isBlank() || message.getMessage_text().length() > 255 ) {
            return null;
        }

        // Check for message Posted By already exists
        Message messagePosted_byExists = messageDAO.getMessagePosted_by( message.getPosted_by() );

        // If all above conditions are met, the response body should contain a JSON of the Account
        if ( messagePosted_byExists != null ) {

            Message persistedMessage =  messageDAO.insertMessage(message);

            return persistedMessage;

        }

        // Return null if  Posted By user does not exist & message is not added to database
        return null;
    }


    /**
     * update a message by its ID
     * @return message by its ID
     */
    public Message updateMessageById(int id, Message message) {
        
        // Check for the message_text is not blank, is not over 255 characters, and posted_by refers to a real, existing user
        if( message.getMessage_text().isEmpty() || message.getMessage_text()==null || message.getMessage_text().length() > 255 ) {
            return null;
        }

        // Retrieve the existing message by its ID
        Message retrievedMessage = this.getMessageById(id);

        // Check if the message exists
        if ( retrievedMessage == null ) {
            return null;
        }

        return messageDAO.updateMessageById(id, message);
        
    }
    

    /**
     * delete a message by its ID
     */
    public void deleteMessageById(int id) {
        
        // delete a message by its ID
        boolean hasDeletedMessage = messageDAO.deleteMessageById(id);

    }
    

}
