package com.certifyingcenter.certifyingcenter.controllers;

import com.certifyingcenter.certifyingcenter.certificates.CertificateUtil;
import com.certifyingcenter.certifyingcenter.certificates.DateUtil;
import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;
import com.certifyingcenter.certifyingcenter.entryies.ServerRootCertEntity;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.models.CertificateRequest;
import com.certifyingcenter.certifyingcenter.repositories.CertificateEntityRepository;
import com.certifyingcenter.certifyingcenter.repositories.ServerRootCertEntityRepository;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

@Controller
public class CertificateRequestController {

    private final CertificateUtil certificateUtil;
    private final ServerRootCertEntityRepository serverRootCertEntityRepository;
    private final CertificateEntityRepository certificateEntityRepository;
    private final DateUtil dateUtil;
    private final UserRepositories userRepositories;

    @Autowired
    public CertificateRequestController(CertificateUtil certificateUtil, ServerRootCertEntityRepository serverRootCertEntityRepository, CertificateEntityRepository certificateEntityRepository, DateUtil dateUtil, UserRepositories userRepositories) {
        this.certificateUtil = certificateUtil;
        this.serverRootCertEntityRepository = serverRootCertEntityRepository;
        this.certificateEntityRepository = certificateEntityRepository;
        this.dateUtil = dateUtil;
        this.userRepositories = userRepositories;
    }




    @GetMapping("/create_certificate")
    public String getCertificateCreatePage(Principal principal, Model model) {
        if(principal!=null && principal.getName()!=null){
            //Login
            User user = userRepositories.findByUsername(principal.getName());
            String name  = user.getFirstName();
            model.addAttribute("first_name", name);
        }else {
            model.addAttribute("first_name", null);
        }
        model.addAttribute("certificateModel", new CertificateRequest());
        return "create_certificate_page";
    }

    @PostMapping("/create_certificate_send")
    public String createCertificate(@ModelAttribute CertificateRequest certificateRequest, Principal principal) {
        try {
            CertificateEntity certificateEntity = new CertificateEntity();
            //TODO
            //Получаем текущее время и дату (сервера)
            certificateEntity.setTimeOfApplication(dateUtil.getCurrentDateAndTime());

            //Ключ юзера, который войдет в сертификат
            PublicKey publicKeyUser = certificateUtil.
                    convertMultipartFileToPublicKey(certificateRequest.getMultipartFile());

            //наш приватный ключ (ключ УЦ) который хранится в базе данных
            ServerRootCertEntity serverRootCertEntity = serverRootCertEntityRepository.findByCertName("server_cert");

            PrivateKey privateKeyRootCACert = certificateUtil.convertByteArrToPrivateKey(
                    serverRootCertEntity.getCertPrivateKeyByteArray()
            );
            //TODO
            X509Certificate x509RootCACertificate =
                    certificateUtil.convertByteArrToCertificate(serverRootCertEntity.getCertByteArray());
            X500Name x500nameIssuer = new JcaX509CertificateHolder(x509RootCACertificate).getSubject();


            X509Certificate issuerCertificate = certificateUtil.generateCertificate(
                    publicKeyUser,
                    privateKeyRootCACert,
                    x500nameIssuer,
                    x509RootCACertificate,
                    certificateRequest.getDomainName(),
                    certificateRequest.getCity(),
                    certificateRequest.getOrganization(),
                    certificateRequest.getDepartment(),
                    certificateRequest.getRegion(),
                    certificateRequest.getCounty(),
                    certificateRequest.getEmail()
                    );

            User user = userRepositories.findByUsername(principal.getName());

            certificateEntity.setUsername(user.getUsername());
            certificateEntity.setFirstName(user.getFirstName());
            certificateEntity.setLastName(user.getLastName());

            certificateEntity.setCertificateByteArr(issuerCertificate.getEncoded());
            certificateEntity.setVerified(0);
            certificateEntity.setEmailCert(certificateRequest.getEmail());

            certificateEntityRepository.save(certificateEntity);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "redirect:/home";
    }

}
