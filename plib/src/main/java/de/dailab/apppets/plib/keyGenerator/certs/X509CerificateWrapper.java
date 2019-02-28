package de.dailab.apppets.plib.keyGenerator.certs;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Date;

import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

/**
 * Created by arik on 13.06.2017.
 */

public class X509CerificateWrapper {

    private Certificate cert = null;
    private X509Certificate x509 = null;

    private boolean isX509;
    private boolean timeValid;
    private PublicKey publicKey = null;
    private String issuer;
    private String subject;
    private Date notBefore = null;
    private Date notAfter = null;
    private BigInteger serialNr = null;
    private String signatueAlgorithm = null;
    private int version;

    public X509CerificateWrapper(Certificate cert) {
        this.cert = cert;
        x509 = adaptAsX509Certificate(cert);
        isX509 = true;
        boolean valid = true;
        try {
            assert x509 != null;
            x509.checkValidity();
        } catch (CertificateExpiredException e) {
            valid = false;
        } catch (CertificateNotYetValidException e) {
            valid = false;
        }
        timeValid = valid;
        publicKey = x509.getPublicKey();
        issuer = x509.getIssuerDN().getName();
        notAfter = x509.getNotAfter();
        notBefore = x509.getNotBefore();
        serialNr = x509.getSerialNumber();
        signatueAlgorithm = x509.getSigAlgName();
        subject = x509.getSubjectDN().getName();
        version = 1 + x509.getVersion();
    }

    public X509CerificateWrapper(X509Certificate cert) {
        try {
            this.cert = null;
        } catch (Exception e) {
        }
        x509 = cert;
        isX509 = true;
        boolean valid = true;
        try {
            x509.checkValidity();
        } catch (CertificateExpiredException e) {
            valid = false;
        } catch (CertificateNotYetValidException e) {
            valid = false;
        }
        timeValid = valid;
        publicKey = x509.getPublicKey();
        issuer = x509.getIssuerDN().getName();
        notAfter = x509.getNotAfter();
        notBefore = x509.getNotBefore();
        serialNr = x509.getSerialNumber();
        signatueAlgorithm = x509.getSigAlgName();
        subject = x509.getSubjectDN().getName();
        version = 1 + x509.getVersion();
    }

    /**
     * Tries to adapt certificate into x509 format if possible, otherwise null will be returned
     *
     * @param cert
     * @return
     */
    public static X509Certificate adaptAsX509Certificate(Certificate cert) {

        byte[] bytes = new byte[0];
        try {
            bytes = cert.getEncoded();
            return X509Certificate.getInstance(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Certificate getCert() {

        return cert;
    }

    public X509Certificate getX509() {

        return x509;
    }

    public boolean isX509() {

        return isX509;
    }

    public boolean isTimeValid() {

        return timeValid;
    }

    public PublicKey getPublicKey() {

        return publicKey;
    }

    public String getIssuer() {

        return issuer;
    }

    public String getSubject() {

        return subject;
    }

    public Date getNotBefore() {

        return notBefore;
    }

    public Date getNotAfter() {

        return notAfter;
    }

    public BigInteger getSerialNr() {

        return serialNr;
    }

    public String getSignatueAlgorithm() {

        return signatueAlgorithm;
    }

    public int getVersion() {

        return version;
    }

}
