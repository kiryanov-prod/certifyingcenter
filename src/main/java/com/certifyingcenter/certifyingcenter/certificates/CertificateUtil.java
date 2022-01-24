package com.certifyingcenter.certifyingcenter.certificates;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;


public class CertificateUtil {
    public PublicKey convertMultipartFileToPublicKey(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] keyBytes = multipartFile.getBytes();
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    public PrivateKey convertMultipartFileToPrivateKey(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = multipartFile.getBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);


        return privateKey;
    }

    public byte[] convertPublicKeyToByteArray(PublicKey publicKey){
        return publicKey.getEncoded();
    }
    public byte[] convertPrivateKeyToByteArray(PrivateKey privateKey){
        return privateKey.getEncoded();
    }

    public PublicKey convertByteArrToPublicKey(byte [] arr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(arr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }


    public byte [] convertPrivateKeyToByteArr(PrivateKey privateKey){
        return privateKey.getEncoded();
    }
    public PrivateKey convertByteArrToPrivateKey(byte [] arr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(arr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }



    //TODO
    public X509Certificate generateCertificate(
            PublicKey publicUserKey ,
            PrivateKey privateKeyRootCertCA,
            X500Name rootCertIssuer,
            X509Certificate rootCert,
            String domainName,
            String city,
            String organization,
            String department,
            String region,
            String county,
            String email) throws CertificateException, OperatorCreationException, NoSuchAlgorithmException, CertIOException {
        Security.addProvider(new BouncyCastleProvider());
        X500Name issuedCertSubject = new X500NameBuilder( BCStrictStyle.INSTANCE )
                .addRDN(BCStrictStyle.C, county)
                .addRDN(BCStyle.ST, region)
                .addRDN(BCStyle.L, city )
                .addRDN(BCStyle.O, organization)
                .addRDN(BCStyle.OU, department)
                .addRDN(BCStyle.CN, String.format("%s/emailAddress=%s",domainName, email))
                .build();



        BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));


        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, publicUserKey);
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC");

        ContentSigner csrContentSigner = csrBuilder.build(privateKeyRootCertCA);
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(rootCertIssuer, issuedCertSerialNum, startDate, endDate, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootCert));
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));

        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));

        // TODO fix
        issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(new ASN1Encodable[] {
                new GeneralName(GeneralName.dNSName, "mydomain.local"),
                new GeneralName(GeneralName.iPAddress, "127.0.0.1")
        }));

        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(issuedCertHolder);

    }

    public String convertCertificateToString(X509Certificate certificate) {
        try {
            StringWriter stringWriter = new StringWriter();
            try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
                pemWriter.writeObject(certificate);
            }
            return stringWriter.toString();
        } catch (IOException ignore) { }
        return null;
    }

    public X509Certificate convertByteArrToCertificate(byte [] arr){
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = new ByteArrayInputStream(arr);
            return (X509Certificate)certFactory.generateCertificate(inputStream);
        } catch (Exception ignored){}
        return null;
    }


}
