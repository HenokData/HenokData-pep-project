package DAO;

// import static org.mockito.Mockito.when;

// import java.lang.StackWalker.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {

    public Message save(Message message){
        
        String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setInt(1, message.getPosted_by());
                preparedStatement.setString(2, message.getMessage_text());
                preparedStatement.setLong(3, message.getTime_posted_epoch());
                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();

                if (rs.next()) {
                    message.setMessage_id(rs.getInt(1)); // set genereted messange_id 
                    
                }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    /*
     * 
     * 
     * 
     */

    public Optional<Message> findById(int messageId){ 
        
        String sql = "SELECT * FROM Message WHERE message_id = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, messageId);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                    );

                    return Optional.of(message);
                    
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return Optional.empty();


    }

    /* Find all this will help us to reterive all mesages form the database
     * 
     */
    public List<Message> findAll(){
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message";

        try(Connection connection = ConnectionUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                    );
                    messages.add(message);
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return messages;
    }

    /*
     * 
     * 
     */

    public List<Message> findByUserId(int accountId){

        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM Message WHERE posted_by = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, accountId);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                    );
                    messages.add(message);
                    
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
        
    }

    /* Deleat
     * 
     */
    public void delete(int messageId){

        String sql = "DELETE FROM Message WHERE message_id = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                
                preparedStatement.setInt(1, messageId);
                preparedStatement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* UPDATE 
     * 
     */
    public Optional<Message> updateMessageText(int messageId, String newText){

        String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, newText);
                preparedStatement.setInt(2, messageId);
                preparedStatement.executeUpdate();

                return findById(messageId);

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
