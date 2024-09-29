/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.pades;

import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignStrategy;

/**
 *
 * @author hakan
 */
public class PadesStrategyFactory {
    private static SignStrategy strategy;
    
    public static SignStrategy getSignStrategy(SignInfo signInfo){
        switch(signInfo.getType()){
            case ES_XL: 
                strategy = new PadesEslStrategy(signInfo);
                break;
            case ES_BES:
                strategy = new PadesBesStrategy(signInfo);
                break;
            case ES_T:
                strategy = new PadesEstStrategy(signInfo);
                break;
            default:
                throw new IllegalArgumentException("Geçersiz imza tipi: " + signInfo.getType());
        }
        
        return strategy;
    }
}
