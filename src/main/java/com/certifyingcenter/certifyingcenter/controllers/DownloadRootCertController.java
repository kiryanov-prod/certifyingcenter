package com.certifyingcenter.certifyingcenter.controllers;

import com.certifyingcenter.certifyingcenter.certificates.CertificateUtil;
import com.certifyingcenter.certifyingcenter.entryies.ServerRootCertEntity;
import com.certifyingcenter.certifyingcenter.repositories.ServerRootCertEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileWriter;
import java.security.cert.X509Certificate;

@Controller
public class DownloadRootCertController {
    private final ServerRootCertEntityRepository serverRootCertEntityRepository;
    private final CertificateUtil certificateUtil;

    @Autowired
    public DownloadRootCertController(ServerRootCertEntityRepository serverRootCertEntityRepository, CertificateUtil certificateUtil) {
        this.serverRootCertEntityRepository = serverRootCertEntityRepository;
        this.certificateUtil = certificateUtil;
    }


    @GetMapping("/download/root_cert")
    public ResponseEntity<Resource> downLoadRootCert() {
        try {
            ServerRootCertEntity serverRootCertEntity = serverRootCertEntityRepository.findByCertName("server_cert");

            byte[] certByteArr = serverRootCertEntity.getCertByteArray();
            X509Certificate x509Certificate = certificateUtil.convertByteArrToCertificate(certByteArr);
            String certContentString = certificateUtil.convertCertificateToString(x509Certificate);
            File file = new File("root-cert.crt");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(certContentString);
            fileWriter.close();

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=root-cert.crt");
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


        } catch (Exception ignore) {
        }
        return null;
    }

}
