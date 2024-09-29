package dijisoz.signer.esign.pades;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureException;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class PadesValidation extends PadesBase {
    private PadesValidationInfo validationInfo;
    private ContainerValidationResult validationResult;
    private PadesValidationResult result;

    public PadesValidationResult validate(){

        try {
            SignatureContainer readContainer = SignatureFactory.readContainer(new FileInputStream(validationInfo.getFilePath()), createContext(validationInfo.getWorkingDirectory()));
            validationResult = readContainer.verifyAll();
            result = PadesValidationResult.builder().result(validationResult).build();
        } catch (FileNotFoundException | SignatureException e) {
            System.out.println(e);
        }

        return result;
    }
}
