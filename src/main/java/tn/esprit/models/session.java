package tn.esprit.models;
import tn.esprit.utils.DataBase;


public class session {private static user currentUser;


    public static user getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(user user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

}
