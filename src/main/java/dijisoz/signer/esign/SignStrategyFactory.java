package dijisoz.signer.esign;

import dijisoz.signer.esign.cades.CadesStrategyFactory;
import dijisoz.signer.esign.pades.PadesStrategyFactory;
import dijisoz.signer.esign.xades.XadesStrategyFactory;

/**
 *
 * @author hakan
 */
public class SignStrategyFactory {
    private static SignStrategy strategy;
    
    public static SignStrategy getSignStrategy(SignInfo signInfo){
        switch(signInfo.getFormat()){
            case PAdES:
                strategy = PadesStrategyFactory.getSignStrategy(signInfo);
                break;
            case CAdES:
                strategy = CadesStrategyFactory.getSignStrategy(signInfo);
                break;
             case XAdES:
                strategy = XadesStrategyFactory.getSignStrategy(signInfo);
                break;
            default:
                throw new IllegalArgumentException("Geçersiz imza formatı: " + signInfo.getFormat());
        }
        
        return strategy;
    }
}
