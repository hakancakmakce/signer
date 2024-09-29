/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.util;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author hakan
 */
@Data
@Builder
public class RestApiClientResponse {
    private String result;
    private Boolean success;
    private String errorMessage;
}
