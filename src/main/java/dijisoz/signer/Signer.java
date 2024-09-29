/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dijisoz.signer;

import dijisoz.signer.authentication.UserSession;
import dijisoz.signer.forms.Main;

/**
 *
 * @author hakan
 */
public class Signer {

    public static void main(String[] args) {
        UserSession session = UserSession.getInstance();
        Main main = new Main();
        session.addObserver(main);
        main.setVisible(true);
        
    }
}
