package DAO;

// import java.net.Socket;
// import java.util.ArrayList;
// import java.util.List;

import java.sql.*;
import java.util.Optional;
import Model.Account;
import Util.ConnectionUtil; 

public class AccountDAO {

    // work the requrement for ACCOUNT DAO 
    public Optional<Account> findByUsernameAndPassword(String username, String password){
        
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql))
        
        {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Account account = new Account (

                rs.getInt("account_id"),
                rs.getString("username"),
                rs.getString("password")

                   
                );
                return Optional.of(account);
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    /* findById
     * 
     */
    public Optional<Account> findById(int accountId){
        String sql = "SELECT * FROM Account WHERE account_id = ? ";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, accountId);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    return Optional.of(new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password")));

                    
                }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    /* find by user name
     * 
     */
    public Optional<Account> findByUsername(String username){

        String sql = "SELECT * FROM Account WHERE username = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                    return Optional.of(account);
                    
                }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /* save the account using username and password
     * 
     */

    public Account save(Account account){
        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, account.getUsername());
                preparedStatement.setString(2, account.getPassword());
                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();

                if (rs.next()) {
                    account.setAccount_id(rs.getInt(1)); // genereted account_id
                    
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return account; 
    }
    
    
}
