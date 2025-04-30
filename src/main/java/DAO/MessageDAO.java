package DAO;

import Util.ConnectionUtil;
import Model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A DAO is a class that mediates the transformation of data between the format of objects in Java to rows in a
 * database.
 *
 * We may assume that the database has already created a table named 'message'.
 * It contains similar values as the Message class:
 * message_id, which is of type int, and is a primary key & auto increment,
 * posted_by, which is of type int, and is a foreign key associated with the column 'account_id' of 'account'
 * message_text, which is of type varchar(255),
 * time_posted_epoch, which is of type bigint.
 */
public class MessageDAO {

    /**
     * Retrieve all messages from the Message table.
     * @return A List of all messages in the database.
     */
    public List<Message> getAllMessages(){

        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {

            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){

                Message message = new Message( rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch") );

                messages.add(message);

            }

        }catch(SQLException e){

            System.out.println(e.getMessage());
        
        }

        return messages;
    }


    /**
     * Retrieve a message from the Message table, identified by message_id.
     * @return a message identified by message_id.
     */
    public Message getMessageById(int id){
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message WHERE message_id = ?";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,id);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){

                Message messageById = new Message( rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch") );

                return messageById;

            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }


    /**
     * retrieve a message from the Message table, identified by its posted_by (user).
     * @return a message identified by posted_by.
     */
    public Message getMessagePosted_by(int posted_by){
        Connection connection = ConnectionUtil.getConnection();
        try {

            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,posted_by);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                
                Message message = new Message( rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch") );

                return message;

            }

        }catch(SQLException e){

            System.out.println(e.getMessage());
        
        }
        
        return null;
    
    }


    /**
     * Retrieve all messages written by a particular user
     * @return all messages written by a particular user
     */
    public List<Message> getMessagesByAccountId(int accountId){

        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            
            String sql = "SELECT * FROM message WHERE posted_by = ? AND posted_by IN (SELECT account_id FROM account)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, accountId);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message messagesByAccountId = new Message( rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch") );
                
                messages.add(messagesByAccountId);

                return messages;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
        
    }


    /**
     * Insert a message into the Message table.
     * Unlike some of the other insert problems, the primary key here will be provided by the client as part of the
     * Message object. Given the specific nature of an ISBN as both a numerical organization of messages outside of this
     * database, and as a primary key, it would make sense for the client to submit an ISBN when submitting a message.
     * You only need to change the sql String and leverage PreparedStatement's setString and setInt methods.
     */
    public Message insertMessage(Message message){
        
        Connection connection = ConnectionUtil.getConnection();

        String sql = "INSERT INTO message ( posted_by, message_text, time_posted_epoch) VALUES ( ?, ?, ? )" ;

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();


            // After executing the INSERT statement using preparedStatement.executeUpdate()
            // The ResultSet object named generatedKeys is obtained by calling
            // preparedStatement.getGeneratedKeys()

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            // Check if any keys were generated by iterating over the ResultSet using
            // generatedKeys.next()

            if (generatedKeys.next()) {

                // By iterating over the ResultSet using generatedKeys.next(), we can access
                // the generated key value(s). In this case, since we expect only one key
                // (the ID of the inserted message), we use generatedKeys.getInt(1) to retrieve
                // the value of the first column in the result set, which represents the
                // generated ID.

                // Retrieve the generated ID
                int generatedId = (int) generatedKeys.getLong(1);

                // Finally, the retrieved ID is used to create a new Message object, combining
                // it with the other attributes of the inserted message.

                // Create a new Message object with the generated ID and other attributes
                return new Message(generatedId, message.getPosted_by(), message.getMessage_text(),
                        message.getTime_posted_epoch());

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        
        }

        return null;

    }


    /**
     * Update a message into the Message table, identified by message_id.
     * @return updated message by its ID
     */
    public Message updateMessageById(int id, Message message){
        
        Connection connection = ConnectionUtil.getConnection();
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        int rowsUpdated = 0;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, message.getMessage_text());
            preparedStatement.setInt(2, id);

            rowsUpdated = preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        if ( rowsUpdated > 0 ) {

            // Retrieve the updated message by its ID
            Message updatedMessage = this.getMessageById(id);

            return new Message(id, updatedMessage.getPosted_by(), updatedMessage.getMessage_text(),
                               updatedMessage.getTime_posted_epoch());
        }

        return null;
    }


    /**
     * Delete a message from the Message table, identified by message_id.
     * @return true if the deletion was successful; false if the message was not
     *         found in the database.
     */
    public boolean deleteMessageById(int id){
        Connection connection = ConnectionUtil.getConnection();
        
        int rowsUpdated = 0;

        try {

            String sql = "DELETE FROM message WHERE message_id = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,id);

            rowsUpdated = preparedStatement.executeUpdate();

        }catch(SQLException e){

            System.out.println(e.getMessage());
        
        }
        
        return rowsUpdated > 0;
    
    }

}
