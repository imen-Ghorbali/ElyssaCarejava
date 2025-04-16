package tn.esprit.services;
import tn.esprit.models.user;
import tn.esprit.utils.DataBase;
import tn.esprit.interfaces.IService;
import java.sql.*;
import java.util.ArrayList;

public class Service implements IService<user> {

    private Connection cnx;

    public Service() {
        cnx = DataBase.getInstance().getCnx();
    }

    @Override
    public void add(user u) {
       /* String qry = "INSERT INTO `user`(`name`, `email`,`roles`, `password`) VALUES (?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);

            pstm.setString(1, u.getName());
            pstm.setString(2, u.getEmail());
            pstm.setString(3, u.getRole().name());
            pstm.setString(4, u.getPassword());

            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
    }


    @Override
    public ArrayList<user> getAll() {
        /*ArrayList<user> user = new ArrayList<>();
        String qry ="SELECT * FROM `user`";
        try {
            Statement stm = cnx.createStatement();

            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()){
                user u = new user();
                u.setId(rs.getInt(1));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("Email"));
                String roleStr = rs.getString("roles");
                Role role = Role.valueOf(roleStr); // transforme "PATIENT" -> Role.PATIENT
                u.setRoles(role);
                u.setPassword(rs.getString("password"));
                user.add(u);


            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
        return new ArrayList<>(); // implémentation à faire
    }


    @Override
    public void update(user u) {
        // implémentation à faire
    }

    @Override
    public boolean delete(user u) {
        return false; // implémentation à faire


    }
}

