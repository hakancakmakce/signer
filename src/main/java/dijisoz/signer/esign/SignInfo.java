/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign;

import java.io.File;

import lombok.Builder;
import lombok.Data;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.crypto.params.RSAPSSParams;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureType;

/**
 *
 * @author hakan
 */
@Data
@Builder
public class SignInfo {
    private File file;
    private String algorithm;
    private RSAPSSParams algorithmParams;
    private ECertificate eCertificate;
    private String password;
    private SignStructure structure;
    private SignatureFormat format; 
    private SignatureType type;
    private Boolean isQualified;
}
