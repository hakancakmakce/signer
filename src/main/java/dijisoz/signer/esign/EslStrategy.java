/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign;

/**
 *
 * @author hakan
 */

public interface EslStrategy extends SignStrategy {
    abstract SignResult<Object> signEsl();
} 

