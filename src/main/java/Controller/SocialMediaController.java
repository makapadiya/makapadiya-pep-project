package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import Service.ServiceException;

import java.util.List;


/**
 * You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    MessageService messageService;
    AccountService accountService;

    public SocialMediaController(){
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }


    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // 1: Our API should be able to process new User registrations.
        app.post("/register", this::postRegisterUserHandler);

        // 2: Our API should be able to process User logins.
        app.post("/login", this::postLoginUserHandler);

        // 3: Our API should be able to process the creation of new messages.
        app.post("/messages", this::postCreateMessageHandler);

        // 4: Our API should be able to retrieve all messages.
        app.get("/messages", this::getAllMessagesHandler);

        // 5: Our API should be able to retrieve a message by its ID.
        app.get("/messages/{message_id}", this::getMessageByIdHandler);

        // 6: Our API should be able to delete a message identified by a message ID.
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        // 7: Our API should be able to update a message text identified by a message ID.
        app.patch("/messages/{message_id}", this::updateMessageHandler);

        // 8: Our API should be able to retrieve all messages written by a particular user.
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        // app.start(8080);

        return app;
    }


    // 1: Our API should be able to process new User registrations.

    /**
     * This method handles the registration process for new users.
     * It expects a POST request to "/register" with the new account details in the
     * request body.
     *
     * @param ctx the Javalin context object representing the current HTTP request
     *            and response
     * @throws JsonProcessingException if an error occurs during JSON parsing or
     *            serialization
     */

    private void postRegisterUserHandler(Context ctx) throws JsonProcessingException {
        
        // Retrieve the message from the path parameter
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        try {

            // Call the accountService to register new user (account)
            Account addedAccount = accountService.registerUser(account);
            
            if(addedAccount!=null) {

                // If User Registration successfull then the response body should contain a JSON of the Account
                ctx.json(mapper.writeValueAsString(addedAccount));
            
            } else {

                // If the registration is not successful, the response status should be 400. (Client error)
                ctx.status(400);
            
            }
        
        } catch (ServiceException e) {

            // Handle ServiceException and set the status code to 400 (Bad Request)
            ctx.status(400);
        
        }

    }


    // 2: Our API should be able to process User logins.

    /**
     * This method handles the login process for users.
     * It expects a POST request to "/login" with the account credentials in the request body.
     *
     * @param ctx the Javalin context object representing the current HTTP request
     *            and response
     * @throws JsonProcessingException if an error occurs during JSON parsing or
     *            serialization
     */

    private void postLoginUserHandler(Context ctx) throws JsonProcessingException {
        
        // Retrieve account from the path parameter
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        try {

            // The login will be successful if and only if the username and password provided
            // in the request body JSON match a real account existing on the database
            Account loggedInAccount = accountService.loginUser(account);
            
            if(loggedInAccount!=null){
                
                // Send the logged-in account as a JSON response
                ctx.json(mapper.writeValueAsString(loggedInAccount));

            }else{

                // If User does not Exist, the login is not successful, the response status should be 401. (Unauthorized)
                ctx.status(401);
            
            }

        } catch (ServiceException e) {

            // Handle ServiceException and set the status code to 401 (Unauthorized)
            ctx.status(401);

        }

    }


    // 3: Our API should be able to process the creation of new messages.

    /**
     * This method handles the creation of new messages.
     * It expects a POST request to "/messages" with the message details in the request body.
     *
     * @param ctx the Javalin context object representing the current HTTP request
     *            and response
     * @throws JsonProcessingException if an error occurs during JSON parsing or
     *            serialization
     */

    private void postCreateMessageHandler(Context ctx) throws JsonProcessingException {
        
        // Retrieve the message from the path parameter
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        
        // Call the messageService to create new message
        Message createdMessage = messageService.createMessage(message);

        if(createdMessage!=null){

            // Send the created message as a JSON response
            ctx.json(mapper.writeValueAsString(createdMessage));

        }else{

            // Set the status code to 400 (Bad Request)
            ctx.status(400);
        
        }

    }


    // 4: Our API should be able to retrieve all messages.

    /**
     * This method retrieves all messages.
     * It expects a GET request to "/messages".
     *
     * @param ctx the Javalin context object representing the current HTTP request and response
     */

    public void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
        
        // Call the messageService to retrieve all messages
        List<Message> messages = messageService.getAllMessages();
        
        ctx.json(messages);

    }


    // 5: Our API should be able to retrieve a message by its ID.

    /**
     * This method handles the retrieval of a specific message by its ID.
     * It expects a GET request to "/messages/{message_id}".
     *
     * @param ctx the Javalin context object representing the current HTTP request and response
     */

    private void getMessageByIdHandler(Context ctx) throws JsonProcessingException {

        try {

            // Retrieve the message ID & message from the path parameter
            int id = Integer.parseInt(ctx.pathParam("message_id"));

            // Attempt to retrieve the message by its ID
            Message message = messageService.getMessageById(id);
            
            if ( message != null ) {
                
                ctx.json(message);

            } else {
                
                // If the message is not found, set the response status to 200 (OK)
                ctx.status(200); // As per test expectations, return a 200 status even if the message is not found.
                
                ctx.result(""); // Response body is empty as the message was not found.

            }

            // Catch block for NumberFormatException is required to handle cases where the
            // 'message_id' path parameter cannot be parsed to an integer. Without this, an
            // invalid 'message_id' could lead to unhandled exceptions and potential
            // application crashes.

        } catch (NumberFormatException e) {

            ctx.status(400); // Respond with a 'Bad Request' status for invalid 'message_id'.

        } catch (ServiceException e) {

            ctx.status(200); // Respond with a '200' status even in case of a service error.

            ctx.result(""); // Response body is empty as there was a service error.

        }

    }


    // 6: Our API should be able to delete a message identified by a message ID.

    /**
     * This method handles the deletion of a specific message by its ID.
     * It expects a DELETE request to "/messages/{message_id}".
     *
     * @param ctx the Javalin context object representing the current HTTP request and response
     */

    private void deleteMessageHandler(Context ctx) throws JsonProcessingException {

        // Retrieve the message ID from the path parameter
        int id = Integer.parseInt(ctx.pathParam("message_id"));

        // Attempt to retrieve the message by its ID
        Message idMessage = messageService.getMessageById(id);
        if ( idMessage != null) {
            // The message exists, so delete it
            messageService.deleteMessageById(id);
            ctx.status(200);
            ctx.json(idMessage);
        } else {
            // The message does not exist
            // Set the response status to 200 (OK) to indicate successful deletion
            ctx.status(200);
        }

    }


    // 7: Our API should be able to update a message text identified by a message ID.

    /**
     * This method handles the update of a specific message by its ID.
     * It expects a PATCH request to "/messages/{message_id}" with the new content
     * of the message in the request body.
     *
     * @param ctx the Javalin context object representing the current HTTP request
     *            and response
     * @throws JsonProcessingException if an error occurs during JSON parsing or
     *            serialization
     */

    private void updateMessageHandler(Context ctx) throws JsonProcessingException {

        // Retrieve the message from the path parameter
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);

        // Retrieve the message ID from the path parameter
        int id = Integer.parseInt(ctx.pathParam("message_id"));

        // Call the messageService to update message by ID
        Message messageById = messageService.updateMessageById(id, message);
        
        if(messageById!=null){
            ctx.json(mapper.writeValueAsString(messageById));
        }else{

            // Set the status code to 400 (Bad Request)
            ctx.status(400);
        }

    }


    // 8: Our API should be able to retrieve all messages written by a particular user.

    /**
     * This method retrieves all messages associated with a specific account ID.
     * It expects a GET request to "/accounts/{account_id}/messages".
     *
     * @param ctx the Javalin context object representing the current HTTP request and response
     */

    private void getMessagesByAccountIdHandler(Context ctx) throws JsonProcessingException {

        try {

            // Retrieve the account ID from the path parameter
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));

            // Call the messageService to retrieve messages by account ID
            List<Message> messages = messageService.getMessagesByAccountId(accountId);

            if ( messages != null ) {
                ctx.json(messages);
            } else {
                // If the message is not found, set the response status to 200 (OK)
                ctx.status(200); // As per test expectations, return a 200 status even if the message is not found.
            }

        } catch (ServiceException e) {

            System.out.println(e.getMessage());
            // Handle ServiceException and set the status code to 400 (Bad Request)
            ctx.status(400);

        }
        
    }

}