package com.certifyingcenter.certifyingcenter.certificates;

import com.certifyingcenter.certifyingcenter.entryies.ServerRootCertEntity;
import com.certifyingcenter.certifyingcenter.repositories.ServerRootCertEntityRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

@Service
public class ServerRootCertificateService {
    private final ServerRootCertEntityRepository serverRootCertEntityRepository;


    @Autowired
    public ServerRootCertificateService(ServerRootCertEntityRepository serverRootCertEntityRepository) {
        this.serverRootCertEntityRepository = serverRootCertEntityRepository;
    }



    public void generateAndSaveANewRootCertificate() throws NoSuchProviderException, NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException {

        Security.addProvider(new BouncyCastleProvider());


        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
        BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));
        PublicKey publicKey = rootKeyPair.getPublic();
        PrivateKey privateKey = rootKeyPair.getPrivate();

        X500Name rootCertIssuer = new X500NameBuilder( BCStrictStyle.INSTANCE )
                .addRDN(BCStrictStyle.C, "RU")
                .addRDN(BCStyle.ST, "Moscow")
                .addRDN(BCStyle.L, "Moscow" )
                .addRDN(BCStyle.O, "Root CA")
                .addRDN(BCStyle.OU, "IT")
                .addRDN(BCStyle.CN, "www.root-cert.com/emailAddress=root_cert@root.cert")
                .build();

        X500Name rootCertSubject = rootCertIssuer;
        ContentSigner rootCertContentSigner = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer, rootSerialNum, startDate, endDate, rootCertSubject, publicKey);

        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(publicKey));


        X509CertificateHolder rootCertHolder = rootCertBuilder.build(rootCertContentSigner);
        X509Certificate rootCert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(rootCertHolder);


        ServerRootCertEntity serverRootCertEntity =
                serverRootCertEntityRepository.findByCertName("server_cert");

        if(serverRootCertEntity == null){
            serverRootCertEntity = new ServerRootCertEntity();
            serverRootCertEntity.setCertName("server_cert");
        }

        serverRootCertEntity.setCertByteArray(rootCert.getEncoded());
        serverRootCertEntity.setCertPrivateKeyByteArray(privateKey.getEncoded());

        serverRootCertEntityRepository.save(serverRootCertEntity);

    }
}
