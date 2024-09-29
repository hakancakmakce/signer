/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.authentication;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author hakan
 */
public class LoginRequest implements Serializable{
    private String username;
    private String password;
    

    public LoginRequest (String userName, String password) {
        this.username = userName;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();

            // JSON object oluştur
            ObjectNode jsonObject = objectMapper.createObjectNode();

            // JSON object'e veri ekle
              jsonObject.put("username", this.username);
        jsonObject.put("password", this.password);

            
        String jsonString = "";
        try {
            // JSON object'i string'e çevir
             jsonString = objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(LoginRequest.class.getName()).log(Level.SEVERE, null, ex);
        }

        
      
        return jsonString;
    }
}
