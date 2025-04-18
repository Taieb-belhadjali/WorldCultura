package WorldCultura.services;

import WorldCultura.interfaces.IService;
import WorldCultura.models.user;
import WorldCultura.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class userService implements IService<user> {
    private final Connection cnx;

    public userService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(user user) {
        String qry = "INSERT INTO user (nom, prenom, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = cnx.prepareStatement(qry)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<user> getAll() {
        String qry = "SELECT * FROM user";
        List<user> users = new ArrayList<>();
        try (PreparedStatement statement = cnx.prepareStatement(qry);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                user user = new user();
                user.setId(resultSet.getInt("id"));
                user.setNom(resultSet.getString("nom"));
                user.setPrenom(resultSet.getString("prenom"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(resultSet.getString("roles"));
                // Password is intentionally excluded for security
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void update(user user) {
        String qry = "UPDATE user SET nom = ?, prenom = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(qry)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            //statement.setString(5, user.getRole());
            statement.setInt(5, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(user user) {
        String qry = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(qry)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public user find(user user) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public user findById(int id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
