package de.dailab.apppets.plib.keyGenerator.certs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Base64;

import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Random;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

;

/**
 * Created by arik on 14.06.2017.
 */

public class X509CertificateHandler {

    final public static String CERT_O = "DAI-Laboratory, TU-Berlin";
    final public static String CERT_OU = "CC-SEC";
    final public static String CERT_L = "Berlin";
    final public static String CERT_CN = "AppPETs-Privacy-Library";
    final public static String CERT_ST = "Berlin";
    final public static String CERT_C = "DE";

    final private static long VALIDITY_IN_DAYS = 3650L;

    public static Certificate generateSelfSignedPlibCertificate(Context context, KeyPair keyPair)
            throws IOException, CertificateException, java.security.cert.CertificateException {

        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate = new Date(
                System.currentTimeMillis() + VALIDITY_IN_DAYS * 24 * 60 * 60 * 1000);

        String name = getApplicationName(context);
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, (name == null ? "" : name + " ") + CERT_CN);
        nameBuilder.addRDN(BCStyle.OU, CERT_OU);
        nameBuilder.addRDN(BCStyle.O, CERT_O);
        nameBuilder.addRDN(BCStyle.L, CERT_L);
        nameBuilder.addRDN(BCStyle.ST, CERT_ST);
        nameBuilder.addRDN(BCStyle.C, CERT_C);

        X500Name x500Name = nameBuilder.build();
        Random random = new SecureRandom();

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo
                .getInstance(keyPair.getPublic().getEncoded());
        X509v3CertificateBuilder v1CertGen = new X509v3CertificateBuilder(x500Name, BigInteger
                .valueOf(Math.abs(random.nextLong())), startDate, endDate, x500Name,
                subjectPublicKeyInfo);

        // Prepare Signature:
        ContentSigner sigGen = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("SC")
                    .build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
        // Self sign :
        assert sigGen != null;
        X509CertificateHolder x509CertificateHolder = v1CertGen.build(sigGen);
        X509Certificate certNew = null;
        certNew = X509Certificate.getInstance(x509CertificateHolder.getEncoded());
        Certificate[] certs = new Certificate[1];
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream in = new ByteArrayInputStream(x509CertificateHolder.getEncoded());
        Certificate c = cf.generateCertificate(in);
        return c;
    }

    private static String getApplicationName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager
                    .getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (String) (applicationInfo != null ?
                packageManager.getApplicationLabel(applicationInfo) : null);
    }

    public static String convertToBase64PEMString(final X509Certificate certificate)
            throws javax.security.cert.CertificateEncodingException {


        String cert_begin = "-----BEGIN CERTIFICATE-----\r\n";
        String end_cert = "\n-----END CERTIFICATE-----\n";

        String pemCertPre = Base64.encodeToString(certificate.getEncoded(), Base64.NO_WRAP);
        String pemCert = cert_begin + pemCertPre + end_cert;
        return pemCert;
    }

    public static Certificate readCertificateFromPEMFile(final String filePath) {
        BufferedReader in = null;
        String s1;
        String s2;
        StringBuffer sb = new StringBuffer();
        try {
            in = new BufferedReader(new FileReader(new File(filePath)));
            while ((s1 = in.readLine()).toLowerCase().contains("begin certificate") == false) {

            }
            while ((s1 = in.readLine()).toLowerCase().contains("end certificate") == false) {
                sb.append(s1.trim());
            }
            s2 = sb.toString();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (s2 == null) {
            return null;
        }
        try {
            byte[] bb = Base64.decode(s2, Base64.NO_WRAP);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream in0 = new ByteArrayInputStream(bb);
            Certificate certNew = cf.generateCertificate(in0);
            if (certNew == null) {
                throw new Exception();
            }
            return certNew;
        } catch (Exception e) {
            return null;
        }
    }
}
