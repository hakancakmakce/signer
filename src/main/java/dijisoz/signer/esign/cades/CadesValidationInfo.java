/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.cades;

import lombok.Builder;
import lombok.Data;
import tr.gov.tubitak.uekae.esya.api.asn.profile.TurkishESigProfile;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;

/**
 *
 * @author hakan
 */
@Data
@Builder
public class CadesValidationInfo {
    private byte[] signature;
    private ISignable externalContent; 
    private ValidationPolicy policy;
    private TurkishESigProfile turkishESigProfile;
}
