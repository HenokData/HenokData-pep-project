package Controller;


import java.util.List;
import java.util.Optional;
// import java.lang.StackWalker.Option;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Service.UserService;
import Service.MessageService; 


import Model.Account;
import Model.Message; 
import io.javalin.Javalin;
import io.javalin.http.Context;


/*
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */


public class SocialMediaController {

    // 1 start by defing a private filed
    private final UserService userService;
    private final MessageService messageService;

    // constractor  
    public SocialMediaController(UserService userService, MessageService messageService){
        this.userService = userService;
        this.messageService = messageService;
    }

    public SocialMediaController(){
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();
        this.userService = new UserService(accountDAO);
        this.messageService = new MessageService(messageDAO);
    } 

    // constracto this constractor make my code not work so lets try the above version 

    


    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Define endpoints 

        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerHandler);           // i start here my code  1 for user registration
        app.post("/login", this::loginHandler);                 // for user login  
        app.post("/messages", this::createMessageHandler);      // for creating message 
        app.get("/messages", this::getAllMessagesHandler);                // for reterving all messages
        app.get("/messages/{message_id}", this::getMessageByIdHandler);   // for reterving messages by id
        app.delete("/messages/{message_id}", this::deleteMessageHandler);  // for dealting a message
        app.patch("/messages/{message_id}", this::updateMessageHandler);      // for updating a message
        app.get("/accounts/{account_id}/messages", this::getMessageByUserHandler); // for reterving message by user 
        
        return app;
    }

    /** Handler for example endpoint
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


    /* Handler for user registration 
     * @ param context the javalin contex object manges information about both the HTTP reques and response
     */

    private void registerHandler(Context context){
        try {
            // pase the incoming json to an account object
            Account newAccount = context.bodyAsClass(Account.class);

            // VALEDATE USERNAME AND PASSWORD REQU.
            if (newAccount.getUsername().isBlank()) {
                context.status(400).result("");
                return;
            }
            if (newAccount.getPassword().length() < 4) {
                context.status(400).result("");
                return;
            } 
            // check if the username already exist
            Optional<Account> existingAccount = userService.findByUsername(newAccount.getUsername());
            if (existingAccount.isPresent()) {
                context.status(400).result("");
                return;
            }
            // if all validation passed save the new account and return with account_id 

            Account registeredAccount = userService.register(newAccount.getUsername(), newAccount.getPassword());
            context.status(200).json(registeredAccount);

        } catch (Exception e) {
            // Handle unexpected errors
            context.status(400).result("error processing registration " + e.getMessage());  
        }

        
    }
    /* Handler for user login 
     * The login will be successful if and only if the username and password provided in the request body JSON match
     * If successful, the response body should contain a JSON of the account in the response body, including its account_id.
     *  The response status should be 200 OK. If the login is not successful, the response status should be 401.
     */
    private void loginHandler(Context context){

        try {
            // parse incoming json
            Account loginRequest = context.bodyAsClass(Account.class);

            // check is username or password missing
            if (loginRequest.getUsername().isBlank() || loginRequest.getPassword().isBlank()) {
                context.status(401).result("");
                
            }
            //attemet to find the account by username and password
            Optional<Account> account = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

            if (account.isPresent()) {
                // login succesful 
                context.status(200).json(account.get());
                
            }else {
                // unsuccesful login
                context.status(401).result("");
            }
        } catch (Exception e) {
            // handle unexpected error
            context.status(401).result("error processing login " + e.getMessage());
        }
    }
    /* Creating a new message Handler 
     * The creation of the message will be successful if and only if the message_text is not blank
     * if succesful response status should be 200 if not response status should be 400
     */
    private void createMessageHandler(Context context){
        try {
            // parse the incoming json to message object
            Message newMessage = context.bodyAsClass(Message.class);

            // validat message is not blank and less than 255 char
            if (newMessage.getMessage_text().isBlank()) {
                context.status(400).result("");
                return;
            }
            if (newMessage.getMessage_text().length() > 255) {
                context.status(400).result("");
                return;
                
            }
            // validat that posted_by refers to an existing user
            if (!userService.findById(newMessage.getPosted_by()).isPresent()) {
                context.status(400).result("");
                return; 
            }
            // check the last filler 
            if (newMessage.getTime_posted_epoch() == 0) {
                newMessage.setTime_posted_epoch(1669947792);
                
            }

            // if all validation passed save the new message
            Message savedMessage = messageService.createMessage(newMessage.getPosted_by(), newMessage.getMessage_text());
            // context.status(200).json(savedMessage);
            savedMessage.setTime_posted_epoch(newMessage.getTime_posted_epoch());
            context.status(200).json(savedMessage);
        } catch (Exception e) {
            // handle unexpected error
            context.status(400).result();
        }
    } 

    /* All Message reteriver Handler 
     * The response body should contain a JSON representation of a list containing all messages retrieved from the database
     * It is expected for the list to simply be empty if there are no messages
     * The response status should always be 200, which is the default.
     */ 
    private void getAllMessagesHandler(Context context){
        try {
            // reterive all messages from messageservice 

            List<Message> messages = messageService.getAllMessages();

            // return all message as a json 

            context.status(200).json(messages);
        } catch (Exception e) {
            //  handle unexpected exception
            context.status(500).result("error retriving message " + e.getMessage());
        }
    }

    /* Handler to retrieve a message by its ID 
     * The response body should contain a JSON representation of the message identified by the message_id
     * It is expected for the response body to simply be empty if there is no such message
     */
    private void getMessageByIdHandler(Context context){
        try {
            // Extract message_id 
            int messageId = Integer.parseInt(context.pathParam("message_id"));

            // reterive message by id from the messageservice
            Optional<Message> message = messageService.getMessageById(messageId);

            // return json if found otherwise empty respose 
            if (message.isPresent()) {
                context.status(200).json(message.get());
                
            }else {
                context.status(200).result("");// return empty 
            } 
        }catch(NumberFormatException e){
            context.status(400).result("invalude message id format");
        } catch (Exception e) {
            //  handle exception
            context.status(500).result("error reterving message " + e.getMessage());
        }
    }

    /* Handler to able to delete a message identified by a message ID
     * The deletion of an existing message should remove an existing message from the database
     * 
     */

    private void deleteMessageHandler(Context context){

        try {
            // extract message form path param
            int messageId = Integer.parseInt(context.pathParam("message_id"));

            // first lets check if the message exist
            Optional<Message> messageToDelete = messageService.getMessageById(messageId);

            if (messageToDelete.isPresent()) {
                // if the message present delete the message and return the deleted message as json
                messageService.deleteMessage(messageId);
                context.status(200).json(messageToDelete.get());
                
            }else {
                // if the message does not exist return 200ok 
                context.status(200).result("");
            }
        }catch(NumberFormatException e){
            context.status(400).result("invalude message id format");
        }
         catch (Exception e) {
            //  handle unexpected exception
            context.status(500).result("error reterving message " + e.getMessage());
        }

    }
    /* Handler update 
     * The update of a message should be successful if and only if the message id already exists
     *  and the new message_text is not blank and is not over 255 characters
     */
    private void updateMessageHandler(Context context){
        try {
            // extract message_id
            int messageId = Integer.parseInt(context.pathParam("message_id"));

            // parse the reques body eo extract the new message_text
            Message requestMessage = context.bodyAsClass(Message.class);
            String newMessageText = requestMessage.getMessage_text();

            // validat new message 
            if (newMessageText == null || newMessageText.isBlank()) {
                context.status(400).result("");
                return;
            }
            if (newMessageText.length() > 255) {
                context.status(400).result("");
                return;
            }

            // update the message 

            Optional<Message> updatedMessage = messageService.updateMessageText(messageId, newMessageText);
            if (updatedMessage.isPresent()) {
                context.status(200).json(updatedMessage.get());
              
            }else {
                context.status(400).result("");
            }
        } catch(NumberFormatException e){
            context.status(400).result("invalid message id format");
        } catch (Exception e) {
            //  handle unexpected  exception
            context.status(500).result("error updating message " + e.getMessage());
        }

    }
    /* Handler getMessageByUserHandler 
     * The response body should contain a JSON representation of a list containing all messages posted by a particular user
     */

    private void getMessageByUserHandler(Context context){

        try {
            // extract accoung_id from the path
            int accountId = Integer.parseInt(context.pathParam("account_id"));

            // reterive message by account_id from messageService
            List<Message> userMessages = messageService.getMessageByUserId(accountId);
            context.status(200).json(userMessages);

        } catch(NumberFormatException e){
            context.status(400).result("invalid account id format");
        }
        catch (Exception e) {
            
            context.status(500).result("error reteriving message from user  " + e.getMessage());
        }

    }

}
