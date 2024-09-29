/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.authentication;

import org.json.JSONObject;

import lombok.Data;

/**
 *
 * @author hakan
 */
@Data
public class LoginResponse {
    private boolean isLogged;
    private String token;
    private String error;
    private JSONObject jsonObject; 
    
    public LoginResponse(String jsonResponse){
        jsonObject = new JSONObject(jsonResponse);
        this.isLogged = jsonObject.getBoolean("authenticated");
        this.token = jsonObject.getString("token");
        this.error = "";
    }
}
