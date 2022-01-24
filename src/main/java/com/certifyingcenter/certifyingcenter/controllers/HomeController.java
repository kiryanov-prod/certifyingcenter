package com.certifyingcenter.certifyingcenter.controllers;

import com.certifyingcenter.certifyingcenter.certificates.CertificateService;
import com.certifyingcenter.certifyingcenter.certificates.CertificateUtil;
import com.certifyingcenter.certifyingcenter.certificates.DateUtil;
import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.models.CertificateInformation;
import com.certifyingcenter.certifyingcenter.models.UserPageCertInfo;
import com.certifyingcenter.certifyingcenter.repositories.CertificateEntityRepository;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private final UserRepositories userRepositories;
    private final CertificateEntityRepository certificateEntityRepository;
    private final DateUtil dateUtil;
    private final CertificateService certificateService;
    private final CertificateUtil certificateUtil;
    @Autowired
    public HomeController(UserRepositories userRepositories, CertificateEntityRepository certificateEntityRepository, DateUtil dateUtil, CertificateService certificateService, CertificateUtil certificateUtil) {
        this.userRepositories = userRepositories;
        this.certificateEntityRepository = certificateEntityRepository;
        this.dateUtil = dateUtil;
        this.certificateService = certificateService;
        this.certificateUtil = certificateUtil;
    }


    @GetMapping("/home")
    public String getPageHome(Principal principal, Model model){
        if(principal!=null && principal.getName()!=null){
            //Login
            User user = userRepositories.findByUsername(principal.getName());
            String name  = user.getFirstName();
            model.addAttribute("first_name", name);

            List<CertificateEntity> certEntityList = certificateEntityRepository.findAllByUsername(user.getUsername());
            model.addAttribute("certList",convert(certEntityList));
        }else {
            model.addAttribute("first_name", null);
        }


        return "home_page";
    }


    @PostMapping("/home/cert/{cert_id}")
    @ResponseBody
    public CertificateInformation returnCertificateToUser(@PathVariable("cert_id") long certId, Principal principal){
        CertificateEntity certificateEntity = certificateEntityRepository.findByCertificateId(certId);
        if(principal.getName().equals(certificateEntity.getUsername())){
            return certificateService.getCertInfoById(certId);
        }
        return null;
    }

    @GetMapping("/home/cert/download/{cert_id}")
    public ResponseEntity<Resource> downloadCert(@PathVariable("cert_id") long certId, Principal principal){
        CertificateEntity certEntity = certificateEntityRepository.findByCertificateId(certId);
        if(principal.getName().equals(certEntity.getUsername())){
            try {
                byte[] certByteArr = certEntity.getCertificateByteArr();
                X509Certificate x509Certificate = certificateUtil.convertByteArrToCertificate(certByteArr);
                String certContentString = certificateUtil.convertCertificateToString(x509Certificate);
                File file = new File("issued-cert.crt");

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(certContentString);
                fileWriter.close();

                HttpHeaders header = new HttpHeaders();
                header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=issued-cert.crt");
                header.add("Cache-Control", "no-cache, no-store, must-revalidate");
                header.add("Pragma", "no-cache");
                header.add("Expires", "0");

                ByteArrayResource resource =
                        new ByteArrayResource(FileCopyUtils.copyToByteArray(file));
                return ResponseEntity.ok()
                        .headers(header)
                        .contentLength(file.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(resource);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private List<UserPageCertInfo> convert(List<CertificateEntity> certEntityList){
        List<UserPageCertInfo> userCertInfoList = new ArrayList<>();
        certEntityList.forEach(certEntity->{
            UserPageCertInfo userCertInfo = new UserPageCertInfo();
            userCertInfo.setCertId(certEntity.getCertificateId());

            String dateAtdTimeSubmitted = dateUtil.convertDateToString(certEntity.getTimeOfApplication());

            String date = dateAtdTimeSubmitted.split(" ")[0];
            String time = dateAtdTimeSubmitted.split(" ")[1];

            userCertInfo.setCertSubmitDate(date);
            userCertInfo.setCertSubmitTime(time);

            int verify = certEntity.getVerified();
            if(verify==-1){
                userCertInfo.setStatus("Отклонен");
            }else if(verify==0){
                userCertInfo.setStatus("На проверке");
            }
            else if(verify==1){
                userCertInfo.setStatus("Одобрен");
            }
            userCertInfo.setVerified(certEntity.getVerified());

            userCertInfoList.add(userCertInfo);

        });

        return userCertInfoList;
    }
}
