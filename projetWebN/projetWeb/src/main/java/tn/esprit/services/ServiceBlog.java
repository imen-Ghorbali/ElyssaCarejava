package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Blog;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;

public class ServiceBlog implements IService<Blog> {

    private Connection connection;

    public ServiceBlog() {
        connection = DataBase.getInstance().getCnx();
    }

    @Override
    public void add(Blog blog) {
        String sql = "INSERT INTO blog (titre, contenu, date_publication, auteur, image) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, blog.getTitre());
            ps.setString(2, blog.getContenu());
            ps.setString(3, blog.getDate_publication());
            ps.setString(4, blog.getAuteur());
            ps.setString(5, blog.getImage());

            ps.executeUpdate();
            System.out.println("✅ Blog ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Blog blog) {
        String sql = "UPDATE blog SET titre = ?, contenu = ?, date_publication = ?, auteur = ?, image = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, blog.getTitre());
            ps.setString(2, blog.getContenu());
            ps.setString(3, blog.getDate_publication());
            ps.setString(4, blog.getAuteur());
            ps.setString(5, blog.getImage());
            ps.setInt(6, blog.getId());

            ps.executeUpdate();
            System.out.println("✅ Blog modifié !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(Blog blog) {
        String sql = "DELETE FROM blog WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, blog.getId());
            ps.executeUpdate();
            System.out.println("✅ Blog supprimé !");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Blog> getAll() {
        ArrayList<Blog> blogs = new ArrayList<>();
        String sql = "SELECT * FROM blog";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Blog blog = new Blog(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("date_publication"),
                        rs.getString("auteur"),
                        rs.getString("image")
                );
                blogs.add(blog);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    // ➕ Optionnel : méthode supplémentaire si tu veux récupérer un blog par ID
    public Blog getById(int id) {
        String sql = "SELECT * FROM blog WHERE id = ?";
        Blog blog = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                blog = new Blog(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("date_publication"),
                        rs.getString("auteur"),
                        rs.getString("image")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return blog;
    }
}