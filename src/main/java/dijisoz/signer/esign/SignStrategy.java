/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dijisoz.signer.esign;

/**
 *
 * @author hakan
 */
public interface SignStrategy {
    SignResult<Object> sign();
    void validate();
}
