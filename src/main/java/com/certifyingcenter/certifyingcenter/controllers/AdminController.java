package com.certifyingcenter.certifyingcenter.controllers;

import com.certifyingcenter.certifyingcenter.certificates.CertificateService;
import com.certifyingcenter.certifyingcenter.certificates.CertificateUtil;
import com.certifyingcenter.certifyingcenter.certificates.DateUtil;
import com.certifyingcenter.certifyingcenter.certificates.ServerRootCertificateService;
import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;
import com.certifyingcenter.certifyingcenter.models.AjaxChangeCertVerify;
import com.certifyingcenter.certifyingcenter.models.CertificateInformation;
import com.certifyingcenter.certifyingcenter.models.BriefInformationAboutTheCertificate;
import com.certifyingcenter.certifyingcenter.repositories.CertificateEntityRepository;
import com.certifyingcenter.certifyingcenter.repositories.ServerRootCertEntityRepository;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final CertificateEntityRepository certificateEntityRepository;
    private final DateUtil dateUtil;
    private final CertificateService certificateService;
    private final ServerRootCertEntityRepository serverKeyEntityRepository;
    private final CertificateUtil certificateUtil;
    private final ServerRootCertificateService serverRootCertificateService;

    @Autowired
    public AdminController(CertificateEntityRepository certificateEntityRepository, DateUtil dateUtil, CertificateService certificateService, ServerRootCertEntityRepository serverKeyEntityRepository, CertificateUtil certificateUtil, ServerRootCertificateService serverRootCertificateService) {
        this.certificateEntityRepository = certificateEntityRepository;
        this.dateUtil = dateUtil;
        this.certificateService = certificateService;
        this.serverKeyEntityRepository = serverKeyEntityRepository;
        this.certificateUtil = certificateUtil;
        this.serverRootCertificateService = serverRootCertificateService;
    }

    @GetMapping("/admin/list_certificates")
    public String getAdminPageCertificates(Model model){

        //Список поданых заявок на сертификат
        List<CertificateEntity> listOfSubmittedApplications = certificateEntityRepository.findAllByVerified(0);
        //Список ранее одобненных заявок на сертификат
        List<CertificateEntity> listOfApprovedApplications = certificateEntityRepository.findAllByVerified(1);
        //Список ранее отклоненных заявок
        List<CertificateEntity> listOfRejectedApplications = certificateEntityRepository.findAllByVerified(-1);


        //краткая информация о сертификате
        List<BriefInformationAboutTheCertificate> submittedCertInfo =  convertCertToSub(listOfSubmittedApplications);
        List<BriefInformationAboutTheCertificate> approvedCertInfo =  convertCertToSub(listOfApprovedApplications);
        List<BriefInformationAboutTheCertificate> rejectedCertInfo =  convertCertToSub(listOfRejectedApplications);


        model.addAttribute("submittedCertInfoList", submittedCertInfo);
        model.addAttribute("approvedCertInfoList", approvedCertInfo);
        model.addAttribute("rejectedCertInfoList", rejectedCertInfo);



        return "admin/list_certificates_page";
    }


    //Метод, который принимает id сертификата
    //По этому id вытаскивает из БД
    //и возвращает его админу

    @PostMapping("/admin/certificate/{id}")
    @ResponseBody
    public CertificateInformation getSpecificCert(@PathVariable(name = "id") long id){
        return certificateService.getCertInfoById(id);
    }

    @PostMapping("/admin/certificate/change_verify")
    @ResponseBody
    public String change(@RequestBody AjaxChangeCertVerify ajaxChangeCertVerify){

        CertificateEntity cert = certificateEntityRepository.findByCertificateId(ajaxChangeCertVerify.getCertId());
        cert.setVerified(ajaxChangeCertVerify.getNewVerifyNumber());
        certificateEntityRepository.save(cert);
        return "{\"status\": \"ok\"}";
    }

    @GetMapping("/admin/generate_root_cert")
    public String generateAndSaveANewRootCertificate(){
        try {
            serverRootCertificateService.generateAndSaveANewRootCertificate();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
         return "redirect:/admin/list_certificates";
    }



    private  List<BriefInformationAboutTheCertificate> convertCertToSub(List<CertificateEntity> certList){
        List<BriefInformationAboutTheCertificate> coll = certList.stream().map(cert -> {
            BriefInformationAboutTheCertificate briefCert = new BriefInformationAboutTheCertificate();

            String dateAntTime = dateUtil.convertDateToString(cert.getTimeOfApplication());
            String date = dateAntTime.split(" ")[0];

            //19:51:33
            String time = (dateAntTime.split(" ")[1]).substring(0, 5);

            briefCert.setNameAndLatsName(cert.getFirstName()+" "+cert.getLastName());
            briefCert.setDate(date);
            briefCert.setTime(time);
            briefCert.setCertId(cert.getCertificateId());
            briefCert.setVerified(cert.getVerified());
            return briefCert;
        }).collect(Collectors.toList());

        return coll;
    }
}
